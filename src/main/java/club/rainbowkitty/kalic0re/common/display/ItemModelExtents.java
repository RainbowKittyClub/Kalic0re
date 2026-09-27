package club.rainbowkitty.kalic0re.common.display;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.math.Transformation;
import com.mojang.serialization.JsonOps;
import eu.pb4.polymer.common.api.PolymerCommonUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The box an item's model occupies, read server-side from the same files a client draws it from,
 * and whether that model is a flat sprite. Vanilla's files come from the client jar Polymer keeps
 * for building its resource pack; every other namespace from the loaded mods' own {@code assets/}.
 *
 * <p>It also reports which way a model's front faces, from its {@code fixed} display rotation: the
 * pose an item frame shows it in, which turns a model's front to face out of the frame. A plain
 * block's is 0, its front authored facing north; a chest's is 180, the chest being authored facing
 * south, which is why vanilla's {@code item/template_chest} turns it round for every context.
 *
 * <p>An item is resolved as drawn in one display context: a {@code select} on
 * {@code display_context} takes the case naming it, as a client does. Otherwise only the default
 * look is resolved: a {@code condition} takes its {@code on_false} branch, any other
 * {@code select} or {@code range_dispatch} its fallback or else its first entry. A model's own
 * display transform for that context is not applied. Each node's
 * {@code transformation} moves what lies under it, as a client applies it. A
 * {@code minecraft:special} model's geometry is code, not JSON, which a server does not have: one
 * that vanilla's item definitions use is measured at build time by a client datagen run
 * ({@code SpecialModelExtentsProvider}) and read back from {@link #SPECIAL_EXTENTS_PATH}. An
 * unmeasured one that places a block stands in with that block's shape. Any other is an entity
 * model built around its own origin, which its transformations place; it stands in with a
 * block-sized box centred on that origin. Not all of it is exact, and need not be: the box
 * positions an item on a surface, it does not size it.
 *
 * <p>Results are kept for the life of the server; assets cannot change without a restart. The
 * client jar is fetched in the background by {@link #prepare()}, since Polymer downloads it when it
 * is not cached, and nothing is kept until that fetch has settled.
 */
public final class ItemModelExtents {
    /** A full block, for anything that cannot be resolved. */
    public static final Extents FULL_BLOCK = new Extents(0, 0, 0, 16, 16, 16, false, 0);
    /** A flat 16×16 sprite, as {@code builtin/generated} extrudes it. */
    public static final Extents FLAT_SPRITE = new Extents(0, 0, 7.5f, 16, 16, 8.5f, true, 0);
    /**
     * Where the measured special models lie, from the root of a mod's jar: the consumer's datagen
     * writes the file and whichever mod carries it first is read. Each entry maps a
     * {@link #specialKey} to {@code [minX, minY, minZ, maxX, maxY, maxZ]}, in model units in the
     * renderer's own space, before any {@code transformation}.
     */
    public static final String SPECIAL_EXTENTS_PATH = "kalic0re/special_model_extents.json";

    // A block-sized box centred on the origin, for an entity model's geometry.
    private static final Extents AROUND_ORIGIN = new Extents(-8, -8, -8, 8, 8, 8, false, 0);
    private static final JsonObject EMPTY_MODEL =
            JsonParser.parseString("{\"type\":\"minecraft:empty\"}").getAsJsonObject();
    private static final Matrix4fc IDENTITY = new Matrix4f();

    // Parent chains and nested definitions deeper than this are treated as broken.
    private static final int MAX_DEPTH = 32;

    private static final List<Function<ItemStack, @Nullable Identifier>> MODEL_SOURCES =
            new CopyOnWriteArrayList<>();
    private static final Map<Key, Optional<Extents>> DEFINITIONS = new ConcurrentHashMap<>();
    private static final Map<Identifier, ModelInfo> MODELS = new ConcurrentHashMap<>();

    // Polymer's copy of the client jar once prepare() has fetched it, and whether that has ended.
    private static volatile @Nullable Path clientRoot;
    private static volatile boolean clientRootSettled;
    // The measured special models, read on first use.
    private static volatile @Nullable Map<String, Extents> measuredSpecials;

    private ItemModelExtents() {
    }

    /**
     * Fetches the client jar vanilla's models are read from, off the calling thread. Call as a
     * server starts; not during datagen, where Polymer would download it for nothing.
     */
    public static void prepare() {
        CompletableFuture.runAsync(() -> {
            try {
                clientRoot = PolymerCommonUtils.getClientJarRoot();
            } finally {
                clientRootSettled = true;
            }
        });
    }

    /**
     * Adds a source naming the item-model definition a stack is drawn with, for items whose own
     * {@code item_model} component is not what clients see, such as a Polymer item given its model
     * on the way out. Sources are asked in registration order; null passes to the next.
     */
    public static void registerModelSource(Function<ItemStack, @Nullable Identifier> source) {
        MODEL_SOURCES.add(source);
    }

    /**
     * The key a measured special model is filed under: the {@code model} object of a
     * {@code minecraft:special} node, every object's keys sorted, written compactly. The same
     * renderer reads the same wherever its definition came from.
     */
    public static String specialKey(JsonObject renderer) {
        return sorted(renderer).toString();
    }

    /**
     * The box a stack's model occupies in model units, 16 to a block, when drawn in a display
     * context; null for an empty stack or one whose model draws nothing there.
     */
    public static @Nullable Extents of(ItemStack stack, ItemDisplayContext context) {
        if (stack.isEmpty()) {
            return null;
        }
        Identifier model = null;
        for (Function<ItemStack, @Nullable Identifier> source : MODEL_SOURCES) {
            model = source.apply(stack);
            if (model != null) {
                break;
            }
        }
        if (model == null) {
            model = stack.get(DataComponents.ITEM_MODEL);
        }
        if (model == null) {
            model = BuiltInRegistries.ITEM.getKey(stack.getItem());
        }
        Key key = new Key(model, stack.getItem(), context);
        if (!clientRootSettled) {
            // Vanilla's models are not readable yet; resolve without them, and keep nothing.
            return resolveDefinition(key).orElse(null);
        }
        return DEFINITIONS.computeIfAbsent(key, ItemModelExtents::resolveDefinition).orElse(null);
    }

    // Reads and resolves an item model definition, items/<path>.json; a full block when malformed.
    private static Optional<Extents> resolveDefinition(Key key) {
        try {
            JsonObject file = readAsset(key.model(), "items/").orElse(null);
            if (file == null || !file.has("model")) {
                return Optional.of(FULL_BLOCK);
            }
            return Optional.ofNullable(resolveItemModel(file.get("model"), key.item(), 0, IDENTITY,
                    key.context()));
        } catch (RuntimeException e) {
            return Optional.of(FULL_BLOCK);
        }
    }

    // Resolves one node of an item model definition, under its ancestors' transformations; null
    // when it draws nothing.
    private static @Nullable Extents resolveItemModel(JsonElement node, Item item, int depth,
            Matrix4fc parent, ItemDisplayContext context) {
        if (depth > MAX_DEPTH || !node.isJsonObject()) {
            return FULL_BLOCK;
        }
        JsonObject model = node.getAsJsonObject();
        String type = Identifier.parse(model.get("type").getAsString()).getPath();
        Matrix4fc transform = withTransformation(parent, model);
        return switch (type) {
            case "model" -> transformed(
                    modelInfo(Identifier.parse(model.get("model").getAsString()), 0).extents(),
                    transform);
            case "composite" -> composite(model.getAsJsonArray("models"), item, depth, transform,
                    context);
            case "condition" -> resolveItemModel(model.get("on_false"), item, depth + 1, transform,
                    context);
            case "select" -> resolveItemModel(selectBranch(model, context), item, depth + 1,
                    transform, context);
            case "range_dispatch" -> resolveItemModel(model.has("fallback") ? model.get("fallback")
                    : firstEntry(model.getAsJsonArray("entries")), item, depth + 1, transform,
                    context);
            case "special" -> special(model.get("model"), item, transform)
                    .withFrontYaw(baseYaw(model));
            case "empty", "bundle/selected_item" -> null;
            default -> FULL_BLOCK;
        };
    }

    // The union of a composite's parts.
    private static @Nullable Extents composite(JsonArray models, Item item, int depth,
            Matrix4fc transform, ItemDisplayContext context) {
        Extents union = null;
        for (JsonElement part : models) {
            union = Extents.union(union,
                    resolveItemModel(part, item, depth + 1, transform, context));
        }
        return union;
    }

    // A select's branch: on display_context, the case naming the context, else the fallback, else
    // nothing, as a client picks it; on any other property, the default look.
    private static JsonElement selectBranch(JsonObject select, ItemDisplayContext context) {
        JsonElement fallback = select.get("fallback");
        if (!Identifier.parse(select.get("property").getAsString()).getPath()
                .equals("display_context")) {
            return fallback != null ? fallback : firstEntry(select.getAsJsonArray("cases"));
        }
        if (select.has("cases")) {
            for (JsonElement entry : select.getAsJsonArray("cases")) {
                JsonElement when = entry.getAsJsonObject().get("when");
                if (names(when, context.getSerializedName())) {
                    return entry.getAsJsonObject().get("model");
                }
            }
        }
        return fallback != null ? fallback : EMPTY_MODEL;
    }

    // Whether a select case's when, one value or a list of them, names this value.
    private static boolean names(JsonElement when, String value) {
        if (when.isJsonArray()) {
            for (JsonElement entry : when.getAsJsonArray()) {
                if (entry.getAsString().equals(value)) {
                    return true;
                }
            }
            return false;
        }
        return when.getAsString().equals(value);
    }

    // The parent's transformation followed by the node's own, when it has a readable one.
    private static Matrix4fc withTransformation(Matrix4fc parent, JsonObject model) {
        if (!model.has("transformation")) {
            return parent;
        }
        return Transformation.EXTENDED_CODEC.parse(JsonOps.INSTANCE, model.get("transformation"))
                .result()
                .<Matrix4fc>map(own -> parent.mul(own.getMatrix(), new Matrix4f()))
                .orElse(parent);
    }

    // A box moved by an item model's transformation, which works in blocks, not model units.
    private static Extents transformed(Extents box, Matrix4fc transform) {
        if ((transform.properties() & Matrix4fc.PROPERTY_IDENTITY) != 0) {
            return box;
        }
        Extents moved = null;
        for (int corner = 0; corner < 8; corner++) {
            Vector3f point = transform.transformPosition(
                    ((corner & 1) == 0 ? box.minX() : box.maxX()) / 16,
                    ((corner & 2) == 0 ? box.minY() : box.maxY()) / 16,
                    ((corner & 4) == 0 ? box.minZ() : box.maxZ()) / 16, new Vector3f()).mul(16);
            moved = Extents.union(moved, new Extents(point.x, point.y, point.z, point.x, point.y,
                    point.z, box.flat(), box.frontYaw()));
        }
        return moved;
    }

    // The model of a select case or range entry, or an empty model when there is none.
    private static JsonElement firstEntry(@Nullable JsonArray entries) {
        if (entries == null || entries.isEmpty() || !entries.get(0).isJsonObject()) {
            return EMPTY_MODEL;
        }
        return entries.get(0).getAsJsonObject().get("model");
    }

    // A special model's front, from its base model; 0 without one.
    private static float baseYaw(JsonObject special) {
        return special.has("base")
                ? modelInfo(Identifier.parse(special.get("base").getAsString()), 0).frontYaw()
                : 0;
    }

    // A special model: as measured, when datagen measured its renderer; otherwise its block's shape
    // when the item places one, which is where its transformations put the block's geometry;
    // otherwise a block-sized box round the renderer's origin, where they put it.
    private static Extents special(@Nullable JsonElement renderer, Item item,
            Matrix4fc transform) {
        Extents measured = renderer != null && renderer.isJsonObject()
                ? measuredSpecials().get(specialKey(renderer.getAsJsonObject())) : null;
        if (measured != null) {
            return transformed(measured, transform);
        }
        if (!(item instanceof BlockItem blockItem)) {
            return transformed(AROUND_ORIGIN, transform);
        }
        try {
            VoxelShape shape = blockItem.getBlock().defaultBlockState()
                    .getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
            if (shape.isEmpty()) {
                return FULL_BLOCK;
            }
            AABB bounds = shape.bounds();
            return new Extents((float) bounds.minX * 16, (float) bounds.minY * 16,
                    (float) bounds.minZ * 16, (float) bounds.maxX * 16, (float) bounds.maxY * 16,
                    (float) bounds.maxZ * 16, false, 0);
        } catch (RuntimeException e) {
            return FULL_BLOCK;
        }
    }

    // The measured special models, read once from the first mod carrying them; none if no mod
    // does or the file is malformed.
    private static Map<String, Extents> measuredSpecials() {
        Map<String, Extents> measured = measuredSpecials;
        if (measured == null) {
            measured = readMeasuredSpecials();
            measuredSpecials = measured;
        }
        return measured;
    }

    private static Map<String, Extents> readMeasuredSpecials() {
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            Optional<Path> path = mod.findPath(SPECIAL_EXTENTS_PATH);
            if (path.isEmpty() || !Files.isRegularFile(path.get())) {
                continue;
            }
            try {
                Map<String, Extents> measured = new HashMap<>();
                for (Map.Entry<String, JsonElement> entry
                        : parse(path.get()).orElseThrow().entrySet()) {
                    JsonArray box = entry.getValue().getAsJsonArray();
                    measured.put(entry.getKey(), new Extents(box.get(0).getAsFloat(),
                            box.get(1).getAsFloat(), box.get(2).getAsFloat(),
                            box.get(3).getAsFloat(), box.get(4).getAsFloat(),
                            box.get(5).getAsFloat(), false, 0));
                }
                return Map.copyOf(measured);
            } catch (RuntimeException e) {
                return Map.of();
            }
        }
        return Map.of();
    }

    // A copy of a JSON tree with every object's keys in order.
    private static JsonElement sorted(JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject sorted = new JsonObject();
            element.getAsJsonObject().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> sorted.add(entry.getKey(), sorted(entry.getValue())));
            return sorted;
        }
        if (element.isJsonArray()) {
            JsonArray sorted = new JsonArray();
            element.getAsJsonArray().forEach(child -> sorted.add(sorted(child)));
            return sorted;
        }
        return element;
    }

    // A block or item model's box and front, following its parents.
    private static ModelInfo modelInfo(Identifier id, int depth) {
        if (depth > MAX_DEPTH) {
            return ModelInfo.UNKNOWN;
        }
        if (id.getPath().equals("builtin/generated")) {
            return new ModelInfo(FLAT_SPRITE, null);
        }
        ModelInfo cached = MODELS.get(id);
        if (cached != null) {
            return cached;
        }
        ModelInfo resolved = readModel(id, depth);
        if (clientRootSettled) {
            MODELS.put(id, resolved);
        }
        return resolved;
    }

    // Reads models/<path>.json: its own elements and fixed rotation where it has them, its
    // parent's where it does not.
    private static ModelInfo readModel(Identifier id, int depth) {
        JsonObject model = readAsset(id, "models/").orElse(null);
        if (model == null) {
            return ModelInfo.UNKNOWN;
        }
        ModelInfo parent = model.has("parent")
                ? modelInfo(Identifier.parse(model.get("parent").getAsString()), depth + 1)
                : ModelInfo.UNKNOWN;
        Extents box = parent.box();
        if (model.has("elements")) {
            box = null;
            for (JsonElement element : model.getAsJsonArray("elements")) {
                box = Extents.union(box, elementExtents(element.getAsJsonObject()));
            }
        }
        Float yaw = fixedYaw(model);
        return new ModelInfo(box, yaw != null ? yaw : parent.explicitYaw());
    }

    // The y of a model's own display.fixed rotation, or null when it sets none.
    private static @Nullable Float fixedYaw(JsonObject model) {
        if (model.has("display") && model.getAsJsonObject("display").has("fixed")) {
            JsonObject fixed = model.getAsJsonObject("display").getAsJsonObject("fixed");
            if (fixed.has("rotation") && fixed.getAsJsonArray("rotation").size() == 3) {
                return fixed.getAsJsonArray("rotation").get(1).getAsFloat();
            }
        }
        return null;
    }

    // One element's box, with its rotation applied to its corners. Rescaling is left out.
    private static Extents elementExtents(JsonObject element) {
        Vector3f from = vector(element.getAsJsonArray("from"));
        Vector3f to = vector(element.getAsJsonArray("to"));
        Quaternionf rotation = null;
        Vector3f origin = new Vector3f(8, 8, 8);
        if (element.has("rotation")) {
            JsonObject spec = element.getAsJsonObject("rotation");
            if (spec.has("axis") && spec.has("angle")) {
                float angle = (float) Math.toRadians(spec.get("angle").getAsFloat());
                rotation = switch (spec.get("axis").getAsString()) {
                    case "x" -> new Quaternionf().rotationX(angle);
                    case "y" -> new Quaternionf().rotationY(angle);
                    default -> new Quaternionf().rotationZ(angle);
                };
                if (spec.has("origin")) {
                    origin = vector(spec.getAsJsonArray("origin"));
                }
            }
        }
        Extents box = null;
        for (int corner = 0; corner < 8; corner++) {
            Vector3f point = new Vector3f((corner & 1) == 0 ? from.x : to.x,
                    (corner & 2) == 0 ? from.y : to.y, (corner & 4) == 0 ? from.z : to.z);
            if (rotation != null) {
                point.sub(origin).rotate(rotation).add(origin);
            }
            box = Extents.union(box,
                    new Extents(point.x, point.y, point.z, point.x, point.y, point.z, false, 0));
        }
        return box;
    }

    // A three-number JSON array as a vector.
    private static Vector3f vector(JsonArray array) {
        return new Vector3f(array.get(0).getAsFloat(), array.get(1).getAsFloat(),
                array.get(2).getAsFloat());
    }

    // Reads assets/<namespace>/<folder><path>.json from the first loaded mod carrying it, else
    // from Polymer's copy of the client jar.
    private static Optional<JsonObject> readAsset(Identifier id, String folder) {
        String asset = "assets/" + id.getNamespace() + "/" + folder + id.getPath() + ".json";
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            Optional<Path> path = mod.findPath(asset);
            if (path.isPresent() && Files.isRegularFile(path.get())) {
                return parse(path.get());
            }
        }
        Path root = clientRoot;
        if (root != null) {
            Path path = root.resolve(asset);
            if (Files.isRegularFile(path)) {
                return parse(path);
            }
        }
        return Optional.empty();
    }

    // Parses a JSON object file; empty when unreadable or not an object.
    private static Optional<JsonObject> parse(Path path) {
        try (Reader reader = Files.newBufferedReader(path)) {
            JsonElement json = JsonParser.parseReader(reader);
            return json.isJsonObject() ? Optional.of(json.getAsJsonObject()) : Optional.empty();
        } catch (IOException | RuntimeException e) {
            return Optional.empty();
        }
    }

    /**
     * A model-space box, 16 units to a block.
     *
     * @param minX lowest x
     * @param minY lowest y
     * @param minZ lowest z
     * @param maxX highest x
     * @param maxY highest y
     * @param maxZ highest z
     * @param flat whether the model is a flat sprite, drawn facing +z
     * @param frontYaw the model's {@code fixed} display rotation about y, in degrees: the turn that
     *     brings its front to face north, as a plain block's does untouched
     */
    public record Extents(float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
            boolean flat, float frontYaw) {
        /** This box with another front. */
        public Extents withFrontYaw(float yaw) {
            return new Extents(minX, minY, minZ, maxX, maxY, maxZ, flat, yaw);
        }

        /**
         * The box holding both, flat only if both are and facing as {@code a} does; either alone
         * when the other is null.
         */
        public static @Nullable Extents union(@Nullable Extents a, @Nullable Extents b) {
            if (a == null) {
                return b;
            }
            if (b == null) {
                return a;
            }
            return new Extents(Math.min(a.minX, b.minX), Math.min(a.minY, b.minY),
                    Math.min(a.minZ, b.minZ), Math.max(a.maxX, b.maxX), Math.max(a.maxY, b.maxY),
                    Math.max(a.maxZ, b.maxZ), a.flat && b.flat, a.frontYaw);
        }
    }

    // A definition's cache key: special models resolve by the item as well as the model id, and
    // every model by the context it is drawn in.
    private record Key(Identifier model, Item item, ItemDisplayContext context) {
    }

    // What a model file resolves to: its box and front yaw, each null where neither it nor a parent
    // sets one.
    private record ModelInfo(@Nullable Extents box, @Nullable Float explicitYaw) {
        // Nothing known: an unreadable or missing model.
        private static final ModelInfo UNKNOWN = new ModelInfo(null, null);

        // The box, a full block when unknown, facing by the yaw found, 0 when none.
        private Extents extents() {
            return (box != null ? box : FULL_BLOCK).withFrontYaw(frontYaw());
        }

        // The yaw found, 0 when none.
        private float frontYaw() {
            return explicitYaw != null ? explicitYaw : 0;
        }
    }
}

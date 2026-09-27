package club.rainbowkitty.kalic0re.common.datagen;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.loader.api.FabricLoader;
import org.jspecify.annotations.Nullable;

import net.minecraft.client.ClientBootstrap;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.Identifier;

import club.rainbowkitty.kalic0re.common.display.ItemModelExtents;

/**
 * Measures every special item model vanilla's item definitions use, for {@link ItemModelExtents}
 * to read on a server. A special model's geometry is code, a renderer and the entity model it
 * draws, and only a client has either. This bakes each renderer from vanilla's own entity models
 * and collects the points it reports through {@code getExtents}, the same call a client sizes the
 * item by. Nothing is measured by hand.
 *
 * <p>Each box is in the renderer's own space, before any {@code transformation}, and is filed under
 * {@link ItemModelExtents#specialKey}. The file goes to
 * {@link ItemModelExtents#SPECIAL_EXTENTS_PATH} at the output's root, outside both packs. A
 * renderer that cannot be baked without real textures or skins is left out, and falls back to the
 * stand-ins {@code ItemModelExtents} describes.
 *
 * <p>Needs a client datagen run ({@code datagen_client=true}): the renderers and entity models are
 * client classes. Needs {@code common/display} shared alongside.
 */
@Environment(EnvType.CLIENT)
public final class SpecialModelExtentsProvider implements DataProvider {
    // Measurements are rounded to this many steps per model unit, dropping float noise.
    private static final float PRECISION = 10_000f;

    private final Path file;

    /**
     * @param output the output whose root the measurements are written under
     */
    public SpecialModelExtentsProvider(FabricPackOutput output) {
        this.file = output.getOutputFolder().resolve(ItemModelExtents.SPECIAL_EXTENTS_PATH);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        ClientBootstrap.bootstrap();
        SpecialModelRenderer.BakingContext context = bakingContext(EntityModelSet.vanilla());
        Map<String, JsonObject> renderers = new TreeMap<>();
        for (JsonObject definition : vanillaItemDefinitions()) {
            collectRenderers(definition, renderers);
        }
        JsonObject boxes = new JsonObject();
        renderers.forEach((key, renderer) -> {
            JsonArray box = measure(renderer, context);
            if (box != null) {
                boxes.add(key, box);
            }
        });
        return DataProvider.saveStable(cache, boxes, file);
    }

    @Override
    public String getName() {
        return "Special Item Model Extents";
    }

    // What baking needs: the entity models. No renderer reads sprites or skins until it draws.
    private static SpecialModelRenderer.BakingContext bakingContext(EntityModelSet models) {
        return new SpecialModelRenderer.BakingContext() {
            @Override
            public EntityModelSet entityModelSet() {
                return models;
            }

            @Override
            public SpriteGetter sprites() {
                return null;
            }

            @Override
            public PlayerSkinRenderCache playerSkinRenderCache() {
                return null;
            }
        };
    }

    // Every item definition in vanilla's assets, in path order.
    private static List<JsonObject> vanillaItemDefinitions() {
        Path items = FabricLoader.getInstance().getModContainer("minecraft").orElseThrow()
                .findPath("assets/minecraft/items").orElseThrow();
        try (Stream<Path> files = Files.walk(items)) {
            return files.filter(path -> path.toString().endsWith(".json")).sorted()
                    .map(SpecialModelExtentsProvider::read).toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static JsonObject read(Path path) {
        try (Reader reader = Files.newBufferedReader(path)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // Collects the renderer of every special node anywhere in a definition, whichever branch.
    private static void collectRenderers(JsonElement node, Map<String, JsonObject> renderers) {
        if (node.isJsonArray()) {
            node.getAsJsonArray().forEach(child -> collectRenderers(child, renderers));
            return;
        }
        if (!node.isJsonObject()) {
            return;
        }
        JsonObject object = node.getAsJsonObject();
        if (object.has("type") && object.get("type").isJsonPrimitive()
                && Identifier.parse(object.get("type").getAsString()).getPath().equals("special")
                && object.get("model") instanceof JsonObject renderer) {
            renderers.putIfAbsent(ItemModelExtents.specialKey(renderer), renderer);
        }
        object.entrySet().forEach(entry -> collectRenderers(entry.getValue(), renderers));
    }

    // The box a renderer draws in, in model units; null when it cannot be baked here.
    private static @Nullable JsonArray measure(JsonObject renderer,
            SpecialModelRenderer.BakingContext context) {
        try {
            SpecialModelRenderer<?> baked = SpecialModelRenderers.CODEC
                    .parse(JsonOps.INSTANCE, renderer).getOrThrow().bake(context);
            if (baked == null) {
                return null;
            }
            float[] min = {Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE};
            float[] max = {-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE};
            baked.getExtents(point -> {
                float[] coords = {point.x(), point.y(), point.z()};
                for (int axis = 0; axis < 3; axis++) {
                    min[axis] = Math.min(min[axis], coords[axis]);
                    max[axis] = Math.max(max[axis], coords[axis]);
                }
            });
            if (min[0] > max[0]) {
                return null;
            }
            JsonArray box = new JsonArray();
            for (float[] corner : new float[][] {min, max}) {
                for (float coord : corner) {
                    box.add(Math.round(coord * 16 * PRECISION) / PRECISION);
                }
            }
            return box;
        } catch (RuntimeException e) {
            return null;
        }
    }
}

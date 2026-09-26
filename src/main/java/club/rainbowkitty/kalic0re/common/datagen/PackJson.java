package club.rainbowkitty.kalic0re.common.datagen;

import java.util.Map;

import com.google.gson.JsonObject;

import net.minecraft.resources.Identifier;

/**
 * The small resource-pack files a server-side datagen run has to write as raw JSON, including the
 * pair a Polymer block needs so a client running the same mod does not see a missing model.
 *
 * <p>Datagen configured with {@code client = false} cannot touch vanilla's typed builders at all:
 * Fabric Loader enforces {@code @Environment} at class-load time, so naming
 * {@code MultiVariantGenerator} or {@code ItemModelUtils} fails with "Cannot load class ... in
 * environment type SERVER" however harmless the class itself looks. A mod that generates client
 * assets from a server-side run writes these shapes by hand instead.
 *
 * <p>Shared by relocation, not a mod dependency (see each consuming mod's {@code build.gradle}).
 */
public final class PackJson {
    private PackJson() {
    }

    /**
     * A model that inherits another and binds one texture to it.
     *
     * @param parent the model to take geometry from, e.g. {@code minecraft:item/generated}
     * @param textureKey the key that geometry samples, without the leading {@code #}
     * @param sprite the texture to bind to that key
     */
    public static JsonObject model(Identifier parent, String textureKey, Identifier sprite) {
        return model(parent, Map.of(textureKey, sprite));
    }

    /**
     * A model that inherits another and binds several textures to it, for a parent that samples
     * more than one key — {@code minecraft:block/cube}'s one key per face, say.
     *
     * @param parent the model to take geometry from, e.g. {@code minecraft:block/cube}
     * @param textures each key that geometry samples, without the leading {@code #}, mapped to the
     *     texture to bind to it
     */
    public static JsonObject model(Identifier parent, Map<String, Identifier> textures) {
        JsonObject bound = new JsonObject();
        textures.forEach((key, sprite) -> bound.addProperty(key, sprite.toString()));
        JsonObject root = new JsonObject();
        root.addProperty("parent", parent.toString());
        root.add("textures", bound);
        return root;
    }

    /**
     * The one-model item definition every {@code items/} file needs, whether the model behind it is
     * a flat icon or a display-entity part.
     *
     * @param model the model this item draws as
     */
    public static JsonObject itemDefinition(Identifier model) {
        JsonObject def = new JsonObject();
        def.addProperty("type", "minecraft:model");
        def.addProperty("model", model.toString());
        JsonObject root = new JsonObject();
        root.add("model", def);
        return root;
    }

    /**
     * A blockstate that maps every state to one model, which is all a block needs when its own
     * states never vary what is drawn.
     *
     * @param model the block model every state resolves to
     */
    public static JsonObject singleVariantBlockState(Identifier model) {
        JsonObject entry = new JsonObject();
        entry.addProperty("model", model.toString());
        JsonObject variants = new JsonObject();
        variants.add("", entry);
        JsonObject root = new JsonObject();
        root.add("variants", variants);
        return root;
    }

    /**
     * A model with a {@code particle} texture and no {@code elements}, so the block itself draws
     * nothing while break and landing particles still match its material.
     *
     * <p>With {@link #singleVariantBlockState} this is the file set a Polymer block wants whenever
     * its real block state can reach a client at all: one running the same mod resolves the real
     * state rather than the substitute sent to everyone else (see {@code PolymerClientHandshake}),
     * and without these two files it renders a missing-model cube behind whatever that mod's
     * block-entity renderer draws.
     *
     * @param particle the texture to take break and landing particle colours from
     */
    public static JsonObject particleOnlyModel(Identifier particle) {
        JsonObject textures = new JsonObject();
        textures.addProperty("particle", particle.toString());
        JsonObject root = new JsonObject();
        root.add("textures", textures);
        return root;
    }
}

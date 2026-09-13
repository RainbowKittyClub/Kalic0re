package club.rainbowkitty.kalic0re.common.datagen;

import java.util.Collection;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.resources.Identifier;

/**
 * An atlas-sources file that adds textures to one of vanilla's own atlases, so a model can name a
 * sprite vanilla stitches somewhere else — or nowhere at all — without copying the PNG.
 *
 * <p>Writing {@code assets/minecraft/atlases/blocks.json} <em>adds</em> to the block atlas rather
 * than replacing it: {@code SpriteSourceList.load} walks {@code ResourceManager#getResourceStack}
 * and calls {@code addAll} for every pack that has the file, so vanilla's own sources and a mod's
 * both survive. Vanilla pulls entity textures onto that atlas this way itself: its own
 * {@code blocks.json} holds four sources, of which {@code entity/bell/bell_body} and
 * {@code entity/enchantment/enchanting_table_book} are {@code minecraft:single}.
 *
 * <p>Copying the textures into the mod's own pack instead would work and is worse: it redistributes
 * vanilla art and freezes it, where a reference follows whatever resource pack sits on top.
 *
 * <p>Raw JSON because {@code SingleFile} and the rest of {@code SpriteSources} are
 * {@code @Environment(CLIENT)}, so a {@code client = false} datagen run cannot name them — see
 * {@link PackJson} for the same constraint on models.
 *
 * <p>Shared by relocation, not a mod dependency (see each consuming mod's {@code build.gradle}).
 */
public final class AtlasSources {
    /**
     * Vanilla's block atlas, which is the one a block or item model's sprites are resolved against.
     * Named deliberately rather than given a mod's own namespace: a private atlas would stitch
     * fine and no model could reach it.
     */
    public static final Identifier BLOCK_ATLAS = Identifier.withDefaultNamespace("blocks");

    private AtlasSources() {
    }

    /**
     * One {@code minecraft:single} source per texture, in iteration order.
     *
     * <p>Each source names no {@code sprite}, so the sprite takes the texture's own identifier
     * verbatim ({@code SingleFile#run} falls back to {@code resource}) and a model binds to the
     * same id it would use anywhere else — there is no second naming rule to keep in step.
     *
     * @param textures the textures to stitch, as texture ids without the {@code textures/} prefix
     *     or {@code .png} suffix, e.g. {@code minecraft:entity/shulker/shulker_red}
     */
    public static JsonObject singleFiles(Collection<Identifier> textures) {
        JsonArray sources = new JsonArray();
        for (Identifier texture : textures) {
            JsonObject single = new JsonObject();
            single.addProperty("type", "minecraft:single");
            single.addProperty("resource", texture.toString());
            sources.add(single);
        }
        JsonObject root = new JsonObject();
        root.add("sources", sources);
        return root;
    }
}

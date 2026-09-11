package club.rainbowkitty.rkcore.common.armor.datagen;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import club.rainbowkitty.rkcore.common.armor.ArmorPiece;
import club.rainbowkitty.rkcore.common.armor.ArmorSet;
import club.rainbowkitty.rkcore.common.datagen.PngAssets;

/**
 * Derives a set's textures from base art by tinting, for families of sets that differ only in
 * colour. Writes each piece's item icon and each target's layer image under the names the item
 * models and the equipment asset already expect.
 */
public final class ArmorTextureProvider implements DataProvider {
    private static final String EQUIPMENT_DIR = "textures/entity/equipment/";

    private final FabricPackOutput output;
    private final List<ArmorArt> art;

    /**
     * @param output the pack output to write into
     * @param art one entry per set whose textures are derived; sets absent from this list keep
     *     whatever textures their mod ships
     */
    public ArmorTextureProvider(FabricPackOutput output, List<ArmorArt> art) {
        this.output = output;
        this.art = List.copyOf(art);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        ClassLoader loader = ArmorTextureProvider.class.getClassLoader();
        List<CompletableFuture<?>> writes = new ArrayList<>();

        for (ArmorArt entry : art) {
            ArmorSet set = entry.set();
            PackOutput.PathProvider items =
                    output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "textures/item");
            for (ArmorPiece piece : set.pieces()) {
                String source = entry.itemArt().get(piece.type());
                if (source == null) {
                    continue;
                }
                writes.add(write(cache, items.file(set.itemId(piece), "png"),
                        PngAssets.read(loader, source), entry.tint()));
            }
            entry.layerArt().forEach((target, source) -> {
                PackOutput.PathProvider layers = output.createPathProvider(
                        PackOutput.Target.RESOURCE_PACK, EQUIPMENT_DIR + target.serializedName());
                writes.add(write(cache, layers.file(set.id(), "png"),
                        PngAssets.read(loader, source), entry.tint()));
            });
        }

        return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Armor Textures";
    }

    // Named for this provider so a stuck write is attributable in a thread dump.
    private static CompletableFuture<?> write(CachedOutput cache, java.nio.file.Path path,
            BufferedImage source, int tint) {
        return PngAssets.save(cache, path, PngAssets.tint(source, tint), "armorTextures");
    }
}

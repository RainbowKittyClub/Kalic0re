package club.rainbowkitty.rkcore.common.armor.datagen;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import net.minecraft.core.HolderLookup;

import club.rainbowkitty.rkcore.common.armor.ArmorPiece;
import club.rainbowkitty.rkcore.common.armor.ArmorSet;

/**
 * English names for every piece, title-cased from its registry path — {@code ranger_helmet}
 * becomes "Ranger Helmet". A set wanting anything else writes its own provider rather than
 * declaring names the framework would only pass through.
 */
public final class ArmorLanguageProvider extends FabricLanguageProvider {
    private final List<ArmorSet> sets;

    /**
     * @param output the pack output to write into
     * @param registries the registry future Fabric hands a language provider
     * @param sets every set the mod declares
     */
    public ArmorLanguageProvider(FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> registries, List<ArmorSet> sets) {
        super(output, registries);
        this.sets = List.copyOf(sets);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registries, TranslationBuilder builder) {
        for (ArmorSet set : sets) {
            for (ArmorPiece piece : set.pieces()) {
                builder.add("item." + set.namespace() + "." + piece.path(),
                        displayName(piece.path()));
            }
        }
    }

    // Underscores are word breaks in every registry path this framework generates.
    private static String displayName(String path) {
        return Arrays.stream(path.split("_"))
                .filter(word -> !word.isEmpty())
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
    }
}

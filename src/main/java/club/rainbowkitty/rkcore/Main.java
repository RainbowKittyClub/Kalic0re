package club.rainbowkitty.rkcore;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import club.rainbowkitty.rkcore.armorstands.ArmorStandLabels;
import club.rainbowkitty.rkcore.armorstands.ArmorStandRules;
import club.rainbowkitty.rkcore.books.BookText;
import club.rainbowkitty.rkcore.books.ShelfLabels;
import club.rainbowkitty.rkcore.common.label.HoverScanner;
import club.rainbowkitty.rkcore.frame.FrameLabels;

/** Mod entrypoint. */
public class Main implements ModInitializer {
    public static final String MOD_ID = "rkcore";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        HoverScanner.init();
        FrameLabels.register();
        ArmorStandLabels.register();
        ArmorStandRules.init();

        BookText.register();
        ShelfLabels.register();

        LOGGER.info("RKCore loaded!");
    }
}

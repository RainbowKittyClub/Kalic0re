package club.rainbowkitty.kalic0re;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import club.rainbowkitty.kalic0re.armorstands.ArmorStandLabels;
import club.rainbowkitty.kalic0re.armorstands.ArmorStandRules;
import club.rainbowkitty.kalic0re.books.BookText;
import club.rainbowkitty.kalic0re.books.ShelfLabels;
import club.rainbowkitty.kalic0re.common.label.HoverScanner;
import club.rainbowkitty.kalic0re.frame.FrameLabels;

/** Mod entrypoint. */
public class Main implements ModInitializer {
    public static final String MOD_ID = "kalic0re";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        HoverScanner.init();
        FrameLabels.register();
        ArmorStandLabels.register();
        ArmorStandRules.init();

        BookText.register();
        ShelfLabels.register();

        LOGGER.info("Kalic0re loaded!");
    }
}

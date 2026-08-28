package club.rainbowkitty.rkcore.books;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import club.rainbowkitty.rkcore.common.frame.FrameLabels;
import club.rainbowkitty.rkcore.common.label.HoverScanner;

/** Mod entrypoint. **/
public class Main implements ModInitializer {
    public static final String MOD_ID = "rkcore-books";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        HoverScanner.init();
        FrameLabels.register();

        BookText.register();
        ShelfLabels.register();

        LOGGER.info("RKCore Books loaded!");
    }
}

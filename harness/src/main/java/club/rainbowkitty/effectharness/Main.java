package club.rainbowkitty.effectharness;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import club.rainbowkitty.rkcore.common.effect.PlayerEffectDispatcher;

/** Scratch mod entrypoint: wires up rkcore's {@code common/effect} framework for real, in a jar. */
public class Main implements ModInitializer {
    public static final String MOD_ID = "effectharness";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        PlayerEffectDispatcher.register(TalariaBoots.EFFECT);
        PlayerEffectDispatcher.init();

        LOGGER.info("Effect harness loaded!");
    }
}

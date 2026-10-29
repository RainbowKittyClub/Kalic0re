package club.rainbowkitty.settingcheckstest;

import net.fabricmc.api.ModInitializer;
import org.jspecify.annotations.Nullable;

import net.minecraft.network.chat.Component;

import club.rainbowkitty.kalic0re.common.config.ModConfig;
import club.rainbowkitty.kalic0re.common.config.Setting;
import club.rainbowkitty.kalic0re.common.text.FallbackText;

/**
 * Settings whose checks look at who sets a value and for whom, and one whose default its own
 * check refuses, for the validator legs of rkcore-claims-b4.js. Never shipped: the value "who"
 * is refused with the check's whole context, so a leg can read what the check saw.
 */
public class SettingChecksTest implements ModInitializer {
    private static final String MOD_ID = "settingcheckstest";
    private static final String WHO = "who";
    private static final String NOBODY = "nobody";

    private static @Nullable FallbackText text;

    @Override
    public void onInitialize() {
        text = FallbackText.load(MOD_ID);
        ModConfig config = ModConfig.create(MOD_ID);
        // Refused when another player sets it; the console may.
        config.text("motto", "", 48).player().validator(SettingChecksTest::mottoRefusal);
        config.text("banner", "", 48).server().fallbackRule()
                .validator(SettingChecksTest::bannerRefusal);
        // A check of the value alone, refusing its own default: the start logs it once.
        config.range("odd", 2, 0, 9).server().fallbackRule()
                .validator(SettingChecksTest::oddRefusal);
        config.register();
    }

    // "who" refused with what the check saw; else refused when another player sets it.
    private static @Nullable Component mottoRefusal(String value, Setting.SetContext context) {
        if (value.equals(WHO)) {
            return who(context);
        }
        boolean someoneElse = context.setter().isPresent() && context.forPlayer().isPresent()
                && !context.setter().get().getUUID().equals(context.forPlayer().get());
        return someoneElse ? text().of("settingcheckstest.refused.others") : null;
    }

    // "who" refused with what the check saw.
    private static @Nullable Component bannerRefusal(String value, Setting.SetContext context) {
        return value.equals(WHO) ? who(context) : null;
    }

    // Even numbers refused.
    private static @Nullable Component oddRefusal(Integer value) {
        return value % 2 == 1 ? null : text().of("settingcheckstest.refused.odd");
    }

    // The refusal naming the setter and the player the value is for.
    private static Component who(Setting.SetContext context) {
        String setter = context.setter().map(player -> player.getScoreboardName()).orElse(NOBODY);
        String forPlayer = context.forPlayer().map(Object::toString).orElse(NOBODY);
        return text().of("settingcheckstest.refused.who", setter, forPlayer);
    }

    // The mod's English, loaded at initialization.
    private static FallbackText text() {
        FallbackText loaded = text;
        if (loaded == null) {
            throw new IllegalStateException(MOD_ID + " is not initialized");
        }
        return loaded;
    }
}

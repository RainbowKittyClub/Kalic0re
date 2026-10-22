package club.rainbowkitty.kalic0re.common.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlags;

import club.rainbowkitty.kalic0re.common.text.FallbackText;

// Builds a mod's declaration in the contract's v1 shape, labels carrying their English as
// fallback, since only the declaring mod's jar holds that English.
final class Declaration {
    private Declaration() {
    }

    // The declaration of config; a missing label or option key throws, naming the key.
    static JsonObject build(ModConfig config, FallbackText text) {
        String modId = config.modId();
        JsonObject root = new JsonObject();
        root.addProperty("v", 1);
        root.addProperty("mod", modId);
        JsonObject pages = new JsonObject();
        for (String page : config.pages().keySet()) {
            String titleKey = "config." + modId + ".page." + page;
            if (text.has(titleKey)) {
                pages.add(page, component(text, titleKey));
            }
        }
        if (!pages.isEmpty()) {
            root.add("pages", pages);
        }
        JsonObject tabs = new JsonObject();
        for (Setting<?> setting : config.settings()) {
            String tab = config.tabOf(setting);
            String titleKey = "config." + modId + ".tab." + tab;
            if (tab != null && !tabs.has(tab) && text.has(titleKey)) {
                tabs.add(tab, component(text, titleKey));
            }
        }
        if (!tabs.isEmpty()) {
            root.add("tabs", tabs);
        }
        JsonArray settings = new JsonArray();
        config.settings().forEach(setting -> settings.add(setting(config, setting, text)));
        root.add("settings", settings);
        return root;
    }

    // One setting's entry.
    private static <T> JsonObject setting(ModConfig config, Setting<T> setting,
            FallbackText text) {
        String modId = config.modId();
        JsonObject json = new JsonObject();
        json.addProperty("key", setting.key());
        json.addProperty("scope", setting.scope().serializedName());
        if (setting.pageDeclared()) {
            json.addProperty("page", setting.page());
        }
        String tab = config.tabOf(setting);
        if (tab != null) {
            json.addProperty("tab", tab);
        }
        if (!setting.inMenu()) {
            json.addProperty("inMenu", false);
        }
        SettingKind<T> kind = setting.kind();
        json.addProperty("kind", kind.name());
        json.add("default", kind.toJson(setting.defaultValue()));
        String labelKey = (setting.hasFallbackRule() ? "gamerule." : "config.") + modId + "."
                + setting.key();
        if (kind instanceof SettingKind.Range range) {
            json.addProperty("min", range.min());
            json.addProperty("max", range.max());
        } else if (kind instanceof SettingKind.Choice<?> choice) {
            json.add("options", options(modId, setting.key(), choice, text));
        } else if (kind instanceof SettingKind.Text textKind) {
            json.addProperty("maxLength", textKind.maxLength());
        }
        json.add("label", required(text, labelKey));
        if (text.has(labelKey + ".description")) {
            json.add("description", component(text, labelKey + ".description"));
        }
        if (setting.declaredPermission() != null || setting.declaredHidden() != null) {
            JsonObject permission = new JsonObject();
            permission.addProperty("fallback", setting.fallbackLevel().getSerializedName());
            permission.addProperty("whenDenied",
                    setting.hiddenWhenDeniedResolved() ? "hidden" : "readonly");
            json.add("permission", permission);
        }
        if (!setting.requiredFeatures().isEmpty()) {
            JsonArray features = new JsonArray();
            FeatureFlags.REGISTRY.toNames(setting.requiredFeatures()).stream()
                    .map(Identifier::toString).sorted().forEach(features::add);
            json.add("requiredFeatures", features);
        }
        return json;
    }

    // The option list of a choice, each labelled config.<mod>.<key>.<id>.
    private static JsonArray options(String modId, String key, SettingKind.Choice<?> choice,
            FallbackText text) {
        JsonArray options = new JsonArray();
        for (String id : choice.ids()) {
            JsonObject option = new JsonObject();
            option.addProperty("id", id);
            option.add("label", required(text, "config." + modId + "." + key + "." + id));
            options.add(option);
        }
        return options;
    }

    // The component for key, which the lang file must hold.
    private static JsonElement required(FallbackText text, String key) {
        if (!text.has(key)) {
            throw new IllegalStateException("No English for '" + key + "' in the mod's"
                    + " lang/en_us.json");
        }
        return component(text, key);
    }

    // The component for key, carrying its English as fallback, as JSON.
    private static JsonElement component(FallbackText text, String key) {
        return ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, text.of(key))
                .getOrThrow();
    }
}

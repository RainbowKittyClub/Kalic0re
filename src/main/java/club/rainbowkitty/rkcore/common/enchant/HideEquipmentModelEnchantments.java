package club.rainbowkitty.rkcore.common.enchant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Enchantment keys that suppress an equipped item's normal texture layer, each paired with the
 * {@link HiddenEquipmentStyle} to suppress it in. The vanilla choke point
 * ({@code EquipmentLayerRenderer#renderLayers}) draws the plain texture, the glint overlay and the
 * armor trim as separate calls told apart only by their {@code RenderType}, so a consumer's client
 * mixin needs nothing from a stack but the style to apply; this is that lookup, kept here so every
 * mod wanting a "hide the model" enchantment shares one registry.
 *
 * <p>The mixin itself cannot live here — a relocated {@code @Mixin} class is never registered in a
 * consumer's own mixins.json, so mixins are not shared code (see "Sharing code between mods" in the
 * workspace CLAUDE.md). {@link #register} runs from either side; the render decisions a consumer's
 * mixin delegates to live in {@code common.enchant.client.HiddenEquipmentRendering}.
 */
public final class HideEquipmentModelEnchantments {
    private static final Map<ResourceKey<Enchantment>, HiddenEquipmentStyle> STYLES =
            new ConcurrentHashMap<>();

    private HideEquipmentModelEnchantments() {
    }

    /** Marks an enchantment as hide-model, in the given style. Call once from mod init. */
    public static void register(ResourceKey<Enchantment> key, HiddenEquipmentStyle style) {
        STYLES.put(key, style);
    }

    /**
     * The style to hide this stack in, or null if it carries no registered hide-model enchantment.
     * A stack carrying two of them resolves to whichever its enchantment map iterates first, which
     * is not worth defining more tightly until something actually stacks two.
     */
    public static @Nullable HiddenEquipmentStyle styleFor(ItemStack stack) {
        if (STYLES.isEmpty() || stack.isEmpty()) {
            return null;
        }
        for (Holder<Enchantment> holder : stack.getEnchantments().keySet()) {
            HiddenEquipmentStyle style = holder.unwrapKey().map(STYLES::get).orElse(null);
            if (style != null) {
                return style;
            }
        }
        return null;
    }
}

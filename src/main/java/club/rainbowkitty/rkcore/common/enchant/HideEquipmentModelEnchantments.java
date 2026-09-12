package club.rainbowkitty.rkcore.common.enchant;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Enchantment keys that should suppress an equipped item's normal texture layer while its
 * enchantment glint still renders — a "hide the model, keep the shimmer" cosmetic. The vanilla
 * choke point for this ({@code EquipmentLayerRenderer#renderLayers}) already draws the plain
 * texture and the glint overlay as two separate calls distinguished only by which
 * {@code RenderType} they use, so the client mixin doing the actual skip only needs a yes/no per
 * item stack; this is that yes/no, kept here so it is shared instead of re-declared by every mod
 * that wants a "hide but glint" enchantment.
 *
 * <p>The mixin itself cannot live here: {@code common/mixin} is never shared code (see "Sharing
 * code between mods" in the workspace's CLAUDE.md), since a relocated {@code @Mixin} class is not
 * automatically registered in a consumer's own {@code <mod_id>.mixins.json}. Registration
 * ({@link #register}) can run from either side; the actual render skip is each consumer's own
 * client-side mixin, consulting {@link #appliesTo} against the stack it is about to draw.
 */
public final class HideEquipmentModelEnchantments {
    private static final Set<ResourceKey<Enchantment>> KEYS = ConcurrentHashMap.newKeySet();

    private HideEquipmentModelEnchantments() {
    }

    /** Marks an enchantment as hide-model-keep-foil. Call once from mod init. */
    public static void register(ResourceKey<Enchantment> key) {
        KEYS.add(key);
    }

    /** Whether the stack carries any registered hide-model-keep-foil enchantment. */
    public static boolean appliesTo(ItemStack stack) {
        if (KEYS.isEmpty() || stack.isEmpty()) {
            return false;
        }
        for (Holder<Enchantment> holder : stack.getEnchantments().keySet()) {
            if (holder.unwrapKey().map(KEYS::contains).orElse(false)) {
                return true;
            }
        }
        return false;
    }
}

package club.rainbowkitty.rkcore.common.effect;

import java.util.function.Predicate;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Whether a {@link PlayerEffect} is currently active on a player. {@link PlayerEffectDispatcher}
 * polls this once per tick per online player, so it must be cheap and depend on nothing but the
 * player's own current state.
 */
@FunctionalInterface
public interface PlayerEffectSource {
    /** Whether the effect is active on this player right now. */
    boolean isActive(ServerPlayer player);

    /** Active whenever a stack matching {@code matches} sits in any slot the group covers. */
    static PlayerEffectSource equipped(EquipmentSlotGroup slot, Predicate<ItemStack> matches) {
        return player -> {
            for (EquipmentSlot equipmentSlot : slot.slots()) {
                if (matches.test(player.getItemBySlot(equipmentSlot))) {
                    return true;
                }
            }
            return false;
        };
    }

    /** Active whenever the given item sits in any slot the group covers. */
    static PlayerEffectSource equipped(EquipmentSlotGroup slot, Item item) {
        return equipped(slot, stack -> stack.getItem() == item);
    }

    /** Active whenever a stack matching {@code matches} is held in either hand. */
    static PlayerEffectSource held(Predicate<ItemStack> matches) {
        return equipped(EquipmentSlotGroup.HAND, matches);
    }

    /** Active whenever the given item is held in either hand. */
    static PlayerEffectSource held(Item item) {
        return held(stack -> stack.getItem() == item);
    }
}

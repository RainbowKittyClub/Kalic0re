package club.rainbowkitty.effectharness;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;

import club.rainbowkitty.rkcore.common.effect.PlayerEffect;
import club.rainbowkitty.rkcore.common.effect.PlayerEffectAttributes;
import club.rainbowkitty.rkcore.common.effect.PlayerEffectDispatcher;
import club.rainbowkitty.rkcore.common.effect.PlayerEffectHooks;
import club.rainbowkitty.rkcore.common.effect.PlayerEffectSource;
import club.rainbowkitty.rkcore.common.item.ItemRegistration;

/**
 * Scratch proof for {@code common/effect}: a boot that boosts movement speed while worn, and only
 * while worn. Unlike a plain {@code ItemAttributeModifiers} component (which vanilla would apply
 * and remove on its own), this drives the boost through {@link PlayerEffectDispatcher}'s
 * activate/deactivate lifecycle, so it actually exercises the framework rather than vanilla
 * equipment handling.
 */
public final class TalariaBoots {
    private static final AttributeModifier.Operation OP =
            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
    private static final double SPEED_BONUS = 0.5;

    public static final Item ITEM = ItemRegistration.register(Main.MOD_ID, "talaria_boots",
            key -> new Item(new Item.Properties().setId(key).equippable(EquipmentSlot.FEET)));

    public static final PlayerEffect EFFECT = new PlayerEffect(Main.MOD_ID, "talaria_speed",
            PlayerEffectSource.equipped(EquipmentSlotGroup.FEET, ITEM), new Hooks());

    private TalariaBoots() {
    }

    private static final class Hooks implements PlayerEffectHooks {
        @Override
        public void onActivate(ServerPlayer player) {
            AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed == null) {
                return;
            }
            Identifier id = PlayerEffectAttributes.modifierId(EFFECT, Attributes.MOVEMENT_SPEED);
            speed.addOrUpdateTransientModifier(new AttributeModifier(id, SPEED_BONUS, OP));
        }

        @Override
        public void onDeactivate(ServerPlayer player) {
            AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed == null) {
                return;
            }
            Identifier id = PlayerEffectAttributes.modifierId(EFFECT, Attributes.MOVEMENT_SPEED);
            speed.removeModifier(id);
        }
    }
}

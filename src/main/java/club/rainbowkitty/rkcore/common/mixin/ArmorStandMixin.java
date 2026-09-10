package club.rainbowkitty.rkcore.common.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import club.rainbowkitty.rkcore.armorstands.ArmorStandRules;
import club.rainbowkitty.rkcore.armorstands.ArmorStandSlots;

// ShowArms needs defaulting on two spawn paths. Item placement only runs the constructor, but
// /summon and world load then run readAdditionalSaveData, which overwrites it again - so that path
// has to have the default swapped inside the read, where a tag that already has the key wins.
@Mixin(ArmorStand.class)
abstract class ArmorStandMixin {
    @Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;"
            + "Lnet/minecraft/world/level/Level;)V",
            at = @At("TAIL"))
    private void rkcore$showArmsByDefaultOnConstruct(EntityType<? extends ArmorStand> type,
            Level level, CallbackInfo ci) {
        ArmorStand self = (ArmorStand) (Object) this;
        if (self.level() instanceof ServerLevel serverLevel
                && serverLevel.getGameRules().get(ArmorStandRules.SHOW_ARMS_BY_DEFAULT)) {
            self.setShowArms(true);
        }
    }

    // Ordinal 2: readAdditionalSaveData calls getBooleanOr four times, for Invisible, Small,
    // ShowArms, NoBasePlate in that order.
    @ModifyArg(method = "readAdditionalSaveData",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/storage/ValueInput;"
                            + "getBooleanOr(Ljava/lang/String;Z)Z",
                    ordinal = 2),
            index = 1)
    private boolean rkcore$showArmsByDefaultOnLoad(boolean defaultValue) {
        ArmorStand self = (ArmorStand) (Object) this;
        return self.level() instanceof ServerLevel serverLevel
                && serverLevel.getGameRules().get(ArmorStandRules.SHOW_ARMS_BY_DEFAULT);
    }

    // Delegates to ArmorStandSlots so this and ArmorStandLabels always agree on which slot a point
    // resolves to - see that class for the band/arm-side thresholds and their derivation.
    @Inject(method = "getClickedSlot", at = @At("HEAD"), cancellable = true)
    private void rkcore$armSlotOverride(Vec3 location, CallbackInfoReturnable<EquipmentSlot> cir) {
        ArmorStand self = (ArmorStand) (Object) this;
        cir.setReturnValue(ArmorStandSlots.clickedSlot(self, location));
    }
}

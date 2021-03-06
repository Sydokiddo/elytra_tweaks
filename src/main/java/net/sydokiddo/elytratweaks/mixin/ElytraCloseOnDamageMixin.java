package net.sydokiddo.elytratweaks.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class ElytraCloseOnDamageMixin {
    @Inject(at = @At("RETURN"), method = "damage")
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable info) {
        LivingEntity player = (LivingEntity) (Object) this;
        if (player instanceof ServerPlayerEntity && player.isFallFlying() && !((ServerPlayerEntity) player).isCreative()) {
            ((ServerPlayerEntity) player).stopFallFlying();
        }
    }
}
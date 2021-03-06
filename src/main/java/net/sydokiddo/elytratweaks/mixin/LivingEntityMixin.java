package net.sydokiddo.elytratweaks.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow
    public abstract boolean damage(DamageSource source, float amount);

    private int elytrabounce$timer = 0;
    LivingEntity player = (LivingEntity) (Object) this;

    // Allows the player to bounce and continue gliding with Elytra

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Redirect(method = "travel(Lnet/minecraft/util/math/Vec3d;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setFlag(IZ)V"))
    public void travel(LivingEntity entity, int idx, boolean val) {
    }

    @Redirect(method = "tickFallFlying",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setFlag(IZ)V"))
    public void initAi(LivingEntity entity, int idx, boolean val) {
        if (entity.getVelocity().y == 0) {
            if (elytrabounce$timer > 1)
                ((EntityAccessor) entity).callSetFlag(7, val);
            elytrabounce$timer += 1;
        } else {
            elytrabounce$timer = 0;
        }

        // Allows the player to close their Elytra when pressing the Elytra close hotkey

        ItemStack stack = player.getEquippedStack(EquipmentSlot.CHEST);

        if (player instanceof ServerPlayerEntity && player.isFallFlying() && player.isSneaking()) {
            ((ServerPlayerEntity) player).stopFallFlying();
        }

        // Prevents the user from gliding when un-equipping Elytra

        if (player instanceof ServerPlayerEntity && !player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA)) {
            ((ServerPlayerEntity) player).stopFallFlying();
        }

        // Prevents the user from gliding when Elytra are broken

        if (player instanceof ServerPlayerEntity && player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA) && stack.getDamage() == 431) {
            ((ServerPlayerEntity) player).stopFallFlying();
        }
    }
}
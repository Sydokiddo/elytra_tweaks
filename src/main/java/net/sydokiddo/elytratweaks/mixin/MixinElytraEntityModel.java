package net.sydokiddo.elytratweaks.mixin;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.entity.LivingEntity;
import net.sydokiddo.elytratweaks.ElytraTweaks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ElytraEntityModel.class)
public abstract class MixinElytraEntityModel<T extends LivingEntity> extends AnimalModel<T> {

    @ModifyArg(method = "getTexturedModelData", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPartBuilder;cuboid(FFFFFFLnet/minecraft/client/model/Dilation;)Lnet/minecraft/client/model/ModelPartBuilder;"), index = 6)
    private static Dilation ETF_injected(Dilation dilation) {
        if (ElytraTweaks.elytraThicknessFix) {
            return new Dilation(1, 1, 0.2f);
        }
        return dilation;
    }
}
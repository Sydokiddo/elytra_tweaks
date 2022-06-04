package net.sydokiddo.elytratweaks.mixin;

import net.sydokiddo.elytratweaks.ElytraTweaks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Final @Shadow private MinecraftClient client;
    private float previousRollAngle = 0.0f;

    @Inject(at = @At("HEAD"), method = "renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V")
    public void renderWorld(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
        if (this.client.player != null && this.client.player.isFallFlying() && !(this.client.player.isTouchingWater() || this.client.player.isInLava())) {
            Vec3d facing = this.client.player.getRotationVecClient();
            Vec3d velocity = this.getPlayerInstantaneousVelocity(tickDelta);
            double horizontalFacing2 = facing.horizontalLengthSquared();
            double horizontalSpeed2 = velocity.horizontalLengthSquared();
            float rollAngle = 0.0f;
            if (horizontalFacing2 > 0.0D && horizontalSpeed2 > 0.0D) {
                double dot = (velocity.x * facing.x + velocity.z * facing.z) / Math.sqrt(horizontalFacing2 * horizontalSpeed2);
                if (dot >= 1.0) dot = 1.0;
                else if (dot <= -1.0) dot = -1.0;
                double direction = Math.signum(velocity.x * facing.z - velocity.z * facing.x);
                rollAngle = (float)(Math.atan(Math.sqrt(horizontalSpeed2) * Math.acos(dot) * ElytraTweaks.wingPower) * direction * 57.29577951308);
            }
            // Smoothens the camera roll angle
            rollAngle = (float)((1.0 - ElytraTweaks.rollSmoothing) * rollAngle + ElytraTweaks.rollSmoothing * this.previousRollAngle);
            this.previousRollAngle = rollAngle;

            matrix.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rollAngle));
        } else {
            this.previousRollAngle = 0.0f;
        }
    }

    public Vec3d getPlayerInstantaneousVelocity(float tickDelta) {

        assert this.client.player != null;
        Vec3d velocity = this.client.player.getVelocity();
        if (tickDelta < 0.01f)
            return velocity;

        double newvx = velocity.x;
        double newvy = velocity.y;
        double newvz = velocity.z;
        double gravity = 0.08;

        Vec3d facing = this.client.player.getRotationVector();
        float pitchRadians = this.client.player.getPitch() * 0.017453292f;
        double horizontalFacing2 = facing.horizontalLengthSquared();
        double horizontalFacing = Math.sqrt(horizontalFacing2);
        double horizontalSpeed = velocity.horizontalLength();

        newvy += gravity * (-1.0 + horizontalFacing2 * 0.75);

        if (horizontalFacing > 0.0) {
            if (velocity.y < 0.0) { // Player is falling
                double lift = newvy * -0.1 * horizontalFacing2;
                newvx += facing.x * lift / horizontalFacing;
                newvy += lift;
                newvz += facing.z * lift / horizontalFacing;
            }

            if (pitchRadians < 0.0f) { // Player is facing upwards
                double lift = horizontalSpeed * -(double)MathHelper.sin(pitchRadians) * 0.04;
                newvx += -facing.x * lift / horizontalFacing;
                newvy += lift * 3.2;
                newvz += -facing.z * lift / horizontalFacing;
            }

            newvx += (facing.x / horizontalFacing * horizontalSpeed - velocity.x) * 0.1;
            newvz += (facing.z / horizontalFacing * horizontalSpeed - velocity.z) * 0.1;
        }

        newvx *= 0.9900000095367432;
        newvy *= 0.9800000190734863;
        newvz *= 0.9900000095367432;

        Vec3d velocitynow = new Vec3d(MathHelper.lerp(tickDelta, velocity.x, newvx), MathHelper.lerp(tickDelta, velocity.y, newvy), MathHelper.lerp(tickDelta, velocity.z, newvz));

        if (ElytraTweaks.isRocketing) {
            velocitynow = velocitynow.add(facing.x * 0.1 + (facing.x * 1.5 - velocitynow.x) * 0.5, facing.y * 0.1 + (facing.y * 1.5 - velocitynow.y) * 0.5, facing.z * 0.1 + (facing.z * 1.5 - velocitynow.z) * 0.5);
        }

        return velocitynow;
    }
}
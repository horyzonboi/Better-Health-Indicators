package net.horyzon.mixin;


import net.horyzon.Networking.PlayerHealthPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class EntityHealthMixin {
    @Inject(method = "setHealth", at = @At("TAIL"))
    private void onSetHealth(float health, CallbackInfo ci) {

        try {
            System.out.println("MIXIN HIT");

            LivingEntity entity = (LivingEntity) (Object) this;

            System.out.println(
                    "SIDE=" +
                            (entity.level().isClientSide() ? "CLIENT" : "SERVER")
            );

            System.out.println("ENTITY CLASS: " + entity.getClass().getName());

            if (entity instanceof ServerPlayer player) {
                System.out.println("BEFORE BROADCAST");

                PlayerHealthPayload.broadcastHealth(
                        player,
                        (ServerLevel) player.level(),
                        health
                );

                System.out.println("AFTER BROADCAST");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
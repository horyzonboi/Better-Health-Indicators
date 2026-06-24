package net.horyzon.mixin;


import net.horyzon.Networking.GetPlayerHealthPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.logging.Level;
@Mixin(LivingEntity.class)
public class EntityHealthMixin {
    @Inject(method = "setHealth", at = @At("TAIL"))
    private void onSetHealth(float health, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof ServerPlayer player) {
            GetPlayerHealthPayload.HealthSync(player, (ServerLevel) player.level(), health);
        }
    }
}

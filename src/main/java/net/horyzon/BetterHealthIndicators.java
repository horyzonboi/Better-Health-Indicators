package net.horyzon;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.horyzon.Networking.PlayerHealthPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterHealthIndicators implements ModInitializer {
	public static final String MOD_ID = "horyzon_bthi";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		System.out.println("REGISTERING PAYLOAD SERVER");
		PayloadTypeRegistry.clientboundPlay().register(PlayerHealthPayload.TYPE, PlayerHealthPayload.CODEC);


		ServerEntityEvents.ENTITY_LOAD.register((  entity, level) -> {
			System.out.println("ENTITY LOAD: " + entity.getClass().getName());
			if (!(entity instanceof ServerPlayer joiningPlayer)) {
				return;
			}
			System.out.println("PLAYER LOADED: " + joiningPlayer.getName().getString());
			PlayerHealthPayload.broadcastHealth(joiningPlayer, (ServerLevel) level, joiningPlayer.getHealth());
			for (ServerPlayer other : level.getServer().getPlayerList().getPlayers()) {
				if (other == joiningPlayer) continue;
				ServerPlayNetworking.send(joiningPlayer, new PlayerHealthPayload(other.getUUID(), other.getHealth()));
			}
		});
	}
}

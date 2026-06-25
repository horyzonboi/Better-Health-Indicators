package net.horyzon.client;


import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.horyzon.BetterHealthIndicators;


import net.horyzon.Networking.PlayerHealthPayload;
import net.horyzon.client.Rendering.HeartRenderPipeline;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.UUID;


public class BetterHealthIndicatorsClient implements ClientModInitializer {
	static String basePath = "textures/gui/";
	public static HashMap<UUID, Float> playerHealth = new HashMap<>();


	public static Identifier HEARTS_DEFAULT = Identifier.fromNamespaceAndPath(BetterHealthIndicators.MOD_ID, basePath + "default/hearts_atlas.png");

	public static void createCustomBar(String string, Identifier location) {

	}

    @Override
	public void onInitializeClient() {
		System.out.println("CLIENT INITIALIZED");
		ClientTickEvents.END_CLIENT_TICK.register(_ -> {
			StoreInitialHealth.store();
		});
		ClientPlayNetworking.registerGlobalReceiver(PlayerHealthPayload.TYPE, ((payload, context) -> {
			ClientLevel level = context.client().level;
			if(level == null) {
				return;
			}
			System.out.println(
					"RECEIVED " +
							payload.uuid() +
							" health=" +
							payload.health()
			);
			playerHealth.put(payload.uuid(), payload.health());
		}));
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			playerHealth.clear();
		});
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		LevelRenderEvents.END_EXTRACTION.register(HeartRenderPipeline::extractHealth);
		LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN.register(HeartRenderPipeline::renderAndDrawHealth);
	}
}

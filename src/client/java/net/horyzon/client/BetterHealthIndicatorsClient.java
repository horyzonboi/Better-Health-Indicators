package net.horyzon.client;


import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;

import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.horyzon.BetterHealthIndicators;


import net.horyzon.Networking.GetPlayerHealthPayload;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;

import javax.xml.stream.Location;


public class BetterHealthIndicatorsClient implements ClientModInitializer {
	static String basePath = "textures/gui/";


	public static Identifier HEARTS_DEFAULT = Identifier.fromNamespaceAndPath(BetterHealthIndicators.MOD_ID, basePath + "default/hearts_atlas.png");

	public static void createCustomBar(String string, Identifier location) {

	}

    @Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(GetPlayerHealthPayload.TYPE, ((payload, context) -> {
			ClientLevel level = context.client().level;
			if(level == null) {
				return;
			}
			StoreAndPullHealth.playerHealth.put(payload.uuid(), payload.health());
			System.out.println("REC " + payload.health());
		}
		));
		ClientTickEvents.END_CLIENT_TICK.register(_ -> StoreAndPullHealth.getHealth());
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		LevelRenderEvents.END_EXTRACTION.register(RenderCustomTexturePipeline::extractHealth);
		LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN.register(RenderCustomTexturePipeline::renderAndDrawHealth);
	}

}

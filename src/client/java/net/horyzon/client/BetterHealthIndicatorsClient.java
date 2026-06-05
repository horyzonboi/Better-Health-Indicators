package net.horyzon.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.horyzon.BetterHealthIndicators;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import javax.swing.text.html.parser.Entity;

public class BetterHealthIndicatorsClient implements ClientModInitializer {

    @Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		LevelRenderEvents.END_EXTRACTION.register(CustomRenderPipeline::extractHealth);
		LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN.register(CustomRenderPipeline::renderAndDrawHealth);

		HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(BetterHealthIndicators.MOD_ID, "last_element"), HealthHud.HUD_LAYER);

		ClientTickEvents.END_CLIENT_TICK.register(client ->
				StoreAndPullHealth.getHealth());


	}

}

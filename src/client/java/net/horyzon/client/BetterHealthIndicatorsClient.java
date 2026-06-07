package net.horyzon.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.horyzon.BetterHealthIndicators;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import javax.swing.text.html.parser.Entity;

public class BetterHealthIndicatorsClient implements ClientModInitializer {
	public static Identifier HEART_TEXTURE = Identifier.fromNamespaceAndPath(BetterHealthIndicators.MOD_ID, "textures/gui/heart.png");


    @Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			StoreAndPullHealth.getHealth();
		});
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		LevelRenderEvents.END_EXTRACTION.register(RenderCustomTexturePipeline::extractHealth);
		LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN.register(RenderCustomTexturePipeline::renderAndDrawHealth);

		HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(BetterHealthIndicators.MOD_ID, "last_element"), HealthHud.HUD_LAYER);


	}

}

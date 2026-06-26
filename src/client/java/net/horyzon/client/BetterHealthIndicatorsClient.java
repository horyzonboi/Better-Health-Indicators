package net.horyzon.client;


import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.horyzon.BetterHealthIndicators;
import net.horyzon.client.KeyMapping.ModKeyMappings;
import net.horyzon.client.Rendering.HeartRenderPipeline;
import net.horyzon.client.Rendering.SettingScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class BetterHealthIndicatorsClient implements ClientModInitializer {
	static String basePath = "textures/gui/";


	public static Identifier HEARTS_DEFAULT = Identifier.fromNamespaceAndPath(BetterHealthIndicators.MOD_ID, basePath + "default/hearts_atlas.png");

	//Add ability for users to use custom hearts
	public static void createCustomBar(String string, Identifier location) {
	}
    @Override
	public void onInitializeClient() {
		ModKeyMappings.initialize();
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (ModKeyMappings.openSettingsScreen.consumeClick()) {
				if (client.player != null) {
					Minecraft.getInstance().setScreen(new SettingScreen(Component.empty()){
					});
				}
			}
			HealthStore.putHealth();
		});
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		LevelRenderEvents.END_EXTRACTION.register(HeartRenderPipeline::extractHealth);
		LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN.register(HeartRenderPipeline::renderAndDrawHealth);
	}

}

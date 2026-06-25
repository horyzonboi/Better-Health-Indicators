package net.horyzon.client;


import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.horyzon.BetterHealthIndicators;
import net.minecraft.resources.Identifier;

public class BetterHealthIndicatorsClient implements ClientModInitializer {
	static String basePath = "textures/gui/";


	public static Identifier HEARTS_DEFAULT = Identifier.fromNamespaceAndPath(BetterHealthIndicators.MOD_ID, basePath + "default/hearts_atlas.png");

	//Add ability for users to use custom hearts
	public static void createCustomBar(String string, Identifier location) {
	}
    @Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(_ -> HealthStore.putHealth());
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		LevelRenderEvents.END_EXTRACTION.register(HeartRenderPipeline::extractHealth);
		LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN.register(HeartRenderPipeline::renderAndDrawHealth);
	}

}

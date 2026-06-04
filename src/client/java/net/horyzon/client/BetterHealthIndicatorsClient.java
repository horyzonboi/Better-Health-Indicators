package net.horyzon.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.horyzon.BetterHealthIndicators;

import net.minecraft.resources.Identifier;

public class BetterHealthIndicatorsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {


		HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(BetterHealthIndicators.MOD_ID, "last_element"), HealthHud.HUD_LAYER);
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		ClientTickEvents.END_CLIENT_TICK.register(client ->
				StoreAndPullHealth.getHealth());
	}

}

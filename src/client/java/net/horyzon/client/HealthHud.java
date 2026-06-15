package net.horyzon.client;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.renderer.RenderPipelines;
import org.joml.Matrix3x2fStack;

import static net.horyzon.client.BetterHealthIndicatorsClient.HEARTS_DEFAULT;


public class HealthHud {
    private static float totalTickProgress =0;
    public static final HudElement HUD_LAYER =

            (graphics, deltaTracker) -> {

                Matrix3x2fStack matrices = graphics.pose();
                totalTickProgress += deltaTracker.getGameTimeDeltaPartialTick(true);
                matrices.pushMatrix();
                matrices.scale(2f);

                //cool effect
                float rotateAmount = (float) Math.sin(totalTickProgress / 20F);
                matrices.rotate(rotateAmount);

                matrices.translate(60f, 60f);

                graphics.blit(RenderPipelines.GUI_TEXTURED, HEARTS_DEFAULT, 50, 50, 0, 0, 16, 16, 16, 16);
                matrices.popMatrix();
            };
}
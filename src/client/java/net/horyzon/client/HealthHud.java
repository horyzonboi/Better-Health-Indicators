package net.horyzon.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.horyzon.BetterHealthIndicators;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Matrix3x2fStack;

public class HealthHud {
    private static float totalTickProgress =0;
    static Identifier HEART_TEXTURE = Identifier.fromNamespaceAndPath(BetterHealthIndicators.MOD_ID, "textures/gui/heart.png");
    public static final HudElement HUD_LAYER =

            (graphics, deltaTracker) -> {
                Identifier TEST_TEXTURE = Identifier.fromNamespaceAndPath(BetterHealthIndicators.MOD_ID, "textures/gui/test.png");
                Matrix3x2fStack matrices = graphics.pose();
                totalTickProgress += deltaTracker.getGameTimeDeltaPartialTick(true);
                matrices.pushMatrix();
                matrices.scale(2f);

                //cool effect
                float rotateAmount = (float) Math.sin(totalTickProgress / 20F);
                matrices.rotate(rotateAmount);

                matrices.translate(60f, 60f);

                graphics.blit(RenderPipelines.GUI_TEXTURED, HEART_TEXTURE, 50, 50, 0, 0, 16, 16, 16, 16);
                matrices.popMatrix();
            };
}
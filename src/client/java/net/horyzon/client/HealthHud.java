package net.horyzon.client;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.util.Mth;

import org.joml.Matrix3x2fStack;

public class HealthHud {
    private static float totalTickProgress =0;
    public static final HudElement HUD_LAYER =
            ((graphics, deltaTracker) -> {
                Matrix3x2fStack matrices = graphics.pose();
                totalTickProgress += deltaTracker.getGameTimeDeltaPartialTick(true);
                matrices.pushMatrix();




                matrices.translate(60f, 60f);

                graphics.fill(5, 20, 35, 40, 0xFFFFFFFF);


                matrices.popMatrix();
            });
}
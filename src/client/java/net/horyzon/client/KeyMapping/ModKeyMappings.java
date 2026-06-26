package net.horyzon.client.KeyMapping;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.horyzon.BetterHealthIndicators;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class ModKeyMappings {
    public static final KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(BetterHealthIndicators.MOD_ID, "general"));
    public static final KeyMapping openSettingsScreen = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(
                    "key.horyzon_bthi.open_settings_screen",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_I,
                    category
            ));
    public static void initialize() {

    }
}
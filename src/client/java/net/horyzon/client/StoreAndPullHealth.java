package net.horyzon.client;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.UUID;

public class StoreAndPullHealth {
    public static HashMap<UUID, Float> playerHealth = new  HashMap<>();
    //the method
    public static void getHealth() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null) {
            return;
        }
        for(Player player : minecraft.level.players()) {
            if (player instanceof LocalPlayer) {
                continue;
            }
            UUID pUUID = player.getUUID();
            float pHealth = player.getHealth();
            playerHealth.put(pUUID, pHealth);
        }
        System.out.println(playerHealth.size());
    }
}
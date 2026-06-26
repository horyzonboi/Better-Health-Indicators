package net.horyzon.client;


import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.UUID;

public class HealthStore {
    public static HashMap<UUID, Float> playerHealth = new  HashMap<>();
    public static boolean displayOwnHealth = true;
    //the method
    public static void putHealth() {
        Minecraft minecraft = Minecraft.getInstance();
        playerHealth.clear();
        if (minecraft.level == null) {
            return;
        }
        for(Player player : minecraft.level.players()) {
            //default


            if (player instanceof LocalPlayer) {
                if (!displayOwnHealth) {
                    continue;
                }
            }



            playerHealth.put(player.getUUID(), player.getHealth());
        }
    }
}
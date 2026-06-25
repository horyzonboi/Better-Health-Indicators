package net.horyzon.Networking;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.horyzon.BetterHealthIndicators;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.UUID;

public record PlayerHealthPayload(UUID uuid, float health) implements CustomPacketPayload {

    public static final Identifier PLAYER_HEALTH_PAYLOAD_ID = Identifier.fromNamespaceAndPath(BetterHealthIndicators.MOD_ID, "player_health");

    public static final CustomPacketPayload.Type<PlayerHealthPayload>  TYPE = new CustomPacketPayload.Type<>(PLAYER_HEALTH_PAYLOAD_ID);

    public static final StreamCodec<FriendlyByteBuf, PlayerHealthPayload> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, PlayerHealthPayload::uuid,
            ByteBufCodecs.FLOAT, PlayerHealthPayload::health,
            PlayerHealthPayload::new
    );
    public static void broadcastHealth(ServerPlayer target, ServerLevel serverLevel, Float health) {
        PlayerHealthPayload payload = new PlayerHealthPayload(target.getUUID(), health);
        for (ServerPlayer player : PlayerLookup.level(serverLevel)) {
            System.out.println(
                    "SENDING " +
                            target.getName().getString() +
                            " health=" +
                            health
            );
            ServerPlayNetworking.send(player, payload);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

package net.horyzon.Networking;

import io.netty.buffer.ByteBuf;
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

import java.util.UUID;
import java.util.stream.Stream;

public record GetPlayerHealthPayload(UUID uuid, float health) implements CustomPacketPayload {

    public static final Identifier PLAYER_HEALTH_PAYLOAD_ID = Identifier.fromNamespaceAndPath(BetterHealthIndicators.MOD_ID, "player_health");

    public static final CustomPacketPayload.Type<GetPlayerHealthPayload>  TYPE = new CustomPacketPayload.Type<>(PLAYER_HEALTH_PAYLOAD_ID);

    public static final StreamCodec<FriendlyByteBuf, GetPlayerHealthPayload> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, GetPlayerHealthPayload::uuid,
            ByteBufCodecs.FLOAT, GetPlayerHealthPayload::health,
            GetPlayerHealthPayload::new
    );
    public static void HealthSync(ServerPlayer target, ServerLevel serverLevel, Float health) {
        GetPlayerHealthPayload payload = new GetPlayerHealthPayload(target.getUUID(), health);
        for (ServerPlayer player : PlayerLookup.level(serverLevel)) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

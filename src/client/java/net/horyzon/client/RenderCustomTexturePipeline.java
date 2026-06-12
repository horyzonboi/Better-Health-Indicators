package net.horyzon.client;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.vertex.*;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.horyzon.BetterHealthIndicators;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.*;
import org.lwjgl.system.MemoryUtil;


import java.util.*;

import static net.horyzon.client.BetterHealthIndicatorsClient.HEART_TEXTURE;

public class RenderCustomTexturePipeline {


    //will add actual logic later
    public record HealthState(double x, double y, double z, float r, float g, float b, float a) {}
    public static final List<HealthState> healthStates = new ArrayList<>();
    private static BufferBuilder buffer;
    private static MappableRingBuffer vertexBuffer;
    private static final Vector4f COLOR_MODULATOR = new Vector4f(1f, 1f, 1f, 1f);
    private static final Vector3f MODEL_OFFSET = new Vector3f();
    private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();



    private static final RenderPipeline HEALTH_PIPELINE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(BetterHealthIndicators.MOD_ID, "pipeline/debug_filled_box_health_display"))
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .withCull(false)
            .build()
    );

    public static final ByteBufferBuilder ALLOCATOR = new ByteBufferBuilder(RenderType.SMALL_BUFFER_SIZE);

    protected static void extractHealth(LevelExtractionContext context) {
        healthStates.clear();
        float partialTick = context.deltaTracker().getGameTimeDeltaPartialTick(true);
        for (UUID uuid : StoreAndPullHealth.playerHealth.keySet()) {
            if (Minecraft.getInstance().level == null) {
                return;
            }
            Player player = Minecraft.getInstance().level.getPlayerByUUID(uuid);
            if (player == null) {
                return;
            }
            //absolutely essential shit
            double x = player.xOld + (player.getX() - player.xOld) * partialTick;
            double y = player.yOld + (player.getY() - player.yOld) * partialTick;
            double z = player.zOld + (player.getZ() - player.zOld) * partialTick;
            healthStates.add(new HealthState(x, y + player.getBbHeight() + 0.5, z, 0f, 0f, 0f, 0.5f));
        }

    }

    protected static void renderAndDrawHealth(LevelRenderContext context) {
        if (healthStates.isEmpty()) return;
        renderHealth(context);
        drawFilledThroughWalls(Minecraft.getInstance(), HEALTH_PIPELINE);
    }


    private static void renderHealth(LevelRenderContext context) {
        PoseStack matrices = context.poseStack();
        Vec3 camera = context.levelState().cameraRenderState.pos;

        //push
        if (buffer == null) {
            buffer = new BufferBuilder(ALLOCATOR, HEALTH_PIPELINE.getVertexFormatMode(), HEALTH_PIPELINE.getVertexFormat());
        }
        for (HealthState state : healthStates) {
            matrices.pushPose();
            matrices.translate(
                    //world origin -> players head
                    state.x - camera.x,
                    state.y - camera.y,
                    state.z - camera.z
            );

            matrices.mulPose(context.levelState().cameraRenderState.orientation);
            matrices.scale(1f, 1f, 1f);
            renderTexturedQuadAtOrigin(matrices.last().pose(), buffer);
            matrices.popPose();
        }

    }

    private static void renderTexturedQuadAtOrigin(Matrix4fc pose, BufferBuilder buffer) {
        float half = 1f / 2f;
        buffer.addVertex(pose, - half, - half, 0).setUv(0f, 1f).setColor(1f, 1f, 1f, 1f);
        buffer.addVertex(pose, + half, - half, 0).setUv(1f, 1f).setColor(1f, 1f, 1f, 1f);
        buffer.addVertex(pose, + half, + half, 0).setUv(1f, 0f).setColor(1f, 1f, 1f, 1f);
        buffer.addVertex(pose, - half, + half, 0).setUv(0f, 0f).setColor(1f, 1f, 1f, 1f);
    }

    private static void drawFilledThroughWalls(Minecraft client, @SuppressWarnings("SameParameterValue") RenderPipeline pipeline) {
        // Build the buffer
        MeshData builtBuffer = buffer.buildOrThrow();
        MeshData.DrawState drawParameters = builtBuffer.drawState();
        VertexFormat format = drawParameters.format();

        GpuBuffer vertices = upload(drawParameters, format, builtBuffer);

        draw(client, pipeline, builtBuffer, drawParameters, vertices, format);

        // Rotate the vertex buffer so we are less likely to use buffers that the GPU is using
        vertexBuffer.rotate();
        buffer = null;
    }

    private static GpuBuffer upload(MeshData.DrawState drawParameters, VertexFormat format, MeshData builtBuffer) {
        // Calculate the size needed for the vertex buffer
        int vertexBufferSize = drawParameters.vertexCount() * format.getVertexSize();

        // Initialize or resize the vertex buffer as needed
        if (vertexBuffer == null ||vertexBuffer.size() < vertexBufferSize) {
            if (vertexBuffer != null) {
                vertexBuffer.close();
            }

           vertexBuffer = new MappableRingBuffer(() -> BetterHealthIndicators.MOD_ID + " health render pipeline", GpuBuffer.USAGE_VERTEX | GpuBuffer.USAGE_MAP_WRITE, vertexBufferSize);
        }

        // Copy vertex data into the vertex buffer
        CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();

        try (GpuBuffer.MappedView mappedView = commandEncoder.mapBuffer(vertexBuffer.currentBuffer().slice(0, builtBuffer.vertexBuffer().remaining()), false, true)) {
            MemoryUtil.memCopy(builtBuffer.vertexBuffer(), mappedView.data());
        }

        return vertexBuffer.currentBuffer();
    }

    private static void draw(Minecraft client, RenderPipeline pipeline, MeshData builtBuffer, MeshData.DrawState drawParameters, GpuBuffer vertices, VertexFormat format) {
        GpuBuffer indices;
        VertexFormat.IndexType indexType;

        if (pipeline.getVertexFormatMode() == VertexFormat.Mode.QUADS) {
            builtBuffer.sortQuads(ALLOCATOR, RenderSystem.getProjectionType().vertexSorting());
            indices   = pipeline.getVertexFormat().uploadImmediateIndexBuffer(builtBuffer.indexBuffer());
            indexType = builtBuffer.drawState().indexType();
        } else {
            RenderSystem.AutoStorageIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(pipeline.getVertexFormatMode());
            indices   = shapeIndexBuffer.getBuffer(drawParameters.indexCount());
            indexType = shapeIndexBuffer.type();
        }

        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
                .writeTransform(RenderSystem.getModelViewMatrix(), COLOR_MODULATOR, MODEL_OFFSET, TEXTURE_MATRIX);

        var texture = client.getTextureManager().getTexture(HEART_TEXTURE);

        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(() -> BetterHealthIndicators.MOD_ID + " health render pipeline rendering",
                        client.getMainRenderTarget().getColorTextureView(), OptionalInt.empty(),
                        client.getMainRenderTarget().getDepthTextureView(), OptionalDouble.empty())) {

            renderPass.setPipeline(pipeline);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicTransforms);
            renderPass.bindTexture("Sampler0", texture.getTextureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
            renderPass.setVertexBuffer(0, vertices);
            renderPass.setIndexBuffer(indices, indexType);
            renderPass.drawIndexed(0 / format.getVertexSize(), 0, drawParameters.indexCount(), 1);
        }

        builtBuffer.close();
    }
    // #endregion custom_pipelines_drawing_phase

    // #region custom_pipelines_clean_up
    public static void close() {
        ALLOCATOR.close();

        if (vertexBuffer != null) {
            vertexBuffer.close();
            vertexBuffer = null;
        }
    }



}
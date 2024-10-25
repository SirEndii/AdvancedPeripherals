package de.srendi.advancedperipherals.client.smartglasses.objects.threedim;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.BlockObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.ThreeDimensionalObject;
import de.srendi.advancedperipherals.common.util.RegistryUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class BlockRenderer implements IThreeDObjectRenderer {

    @Override
    public void renderBatch(List<ThreeDimensionalObject> batch, RenderLevelStageEvent event, PoseStack poseStack, Vec3 view, BufferBuilder bufferBuilder) {
        poseStack.pushPose();

        for (ThreeDimensionalObject renderableObject : batch) {
            poseStack.pushPose();
            onPreRender(renderableObject);

            bufferBuilder.begin(RenderType.solid().mode(), DefaultVertexFormat.BLOCK);

            BlockObject block = (BlockObject) renderableObject;

            BlockPos blockPos = new BlockPos(renderableObject.getX(), renderableObject.getY(), renderableObject.getZ());

            poseStack.translate(-view.x + blockPos.getX(), -view.y + blockPos.getY(), -view.z + blockPos.getZ());
            float alpha = renderableObject.opacity;
            float red = (float) (renderableObject.color >> 16 & 255) / 255.0F;
            float green = (float) (renderableObject.color >> 8 & 255) / 255.0F;
            float blue = (float) (renderableObject.color & 255) / 255.0F;

            RenderSystem.setShader(GameRenderer::getBlockShader);
            RenderSystem.setShaderColor(red, green, blue, alpha);

            Block blockToRender = RegistryUtil.getRegistryEntry(block.block, ForgeRegistries.BLOCKS);

            if (blockToRender != null)
                Minecraft.getInstance().getBlockRenderer().renderBatched(blockToRender.defaultBlockState(), blockPos, event.getCamera().getEntity().level, poseStack, bufferBuilder, false, event.getCamera().getEntity().level.random);

            poseStack.popPose();
            BufferUploader.drawWithShader(bufferBuilder.end());
            onPostRender(renderableObject);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }


        poseStack.popPose();

    }
}

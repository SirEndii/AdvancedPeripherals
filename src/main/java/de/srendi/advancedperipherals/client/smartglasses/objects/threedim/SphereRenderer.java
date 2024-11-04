package de.srendi.advancedperipherals.client.smartglasses.objects.threedim;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.SphereObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.ThreeDimensionalObject;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;

import java.util.List;

public class SphereRenderer implements IThreeDObjectRenderer {

    @Override
    public void renderBatch(List<ThreeDimensionalObject> batch, RenderLevelStageEvent event, PoseStack poseStack, Vec3 view, BufferBuilder bufferBuilder) {
        poseStack.pushPose();

        for (ThreeDimensionalObject renderableObject : batch) {
            poseStack.pushPose();
            onPreRender(renderableObject);
            bufferBuilder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR_NORMAL);

            SphereObject sphere = (SphereObject) renderableObject;

            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            float alpha = renderableObject.opacity;
            float red = (float) (renderableObject.color >> 16 & 255) / 255.0F;
            float green = (float) (renderableObject.color >> 8 & 255) / 255.0F;
            float blue = (float) (renderableObject.color & 255) / 255.0F;

            poseStack.translate(-view.x + renderableObject.getX(), -view.y + renderableObject.getY(), -view.z + renderableObject.getZ());
            RenderUtil.drawSphere(poseStack, bufferBuilder, 1f, 0f, 0f, 0f, red, green, blue, alpha, sphere.sectors, sphere.stacks);
            BufferUploader.drawWithShader(bufferBuilder.end());
            onPostRender(renderableObject);

            poseStack.popPose();
        }


        poseStack.popPose();

    }
}

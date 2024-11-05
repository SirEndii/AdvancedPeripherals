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

        for (ThreeDimensionalObject obj : batch) {
            poseStack.pushPose();
            onPreRender(obj);
            bufferBuilder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR_NORMAL);

            SphereObject sphere = (SphereObject) obj;

            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            float alpha = sphere.opacity;
            float red = RenderUtil.getRed(sphere.color);
            float green = RenderUtil.getRed(sphere.color);
            float blue = RenderUtil.getRed(sphere.color);

            poseStack.translate(-view.x, -view.y, -view.z);
            RenderUtil.drawSphere(poseStack, bufferBuilder, sphere.radius, sphere.x, sphere.y, sphere.z, sphere.xRot, sphere.yRot, sphere.zRot, red, green, blue, alpha, sphere.sectors, sphere.stacks);
            BufferUploader.drawWithShader(bufferBuilder.end());
            onPostRender(obj);

            poseStack.popPose();
        }


        poseStack.popPose();

    }
}

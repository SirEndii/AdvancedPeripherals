package de.srendi.advancedperipherals.client.smartglasses.objects.twodim;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.RenderableObject;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.client.gui.overlay.ForgeGui;

import java.util.List;

public class RectangleRenderer implements ITwoDObjectRenderer {

    @Override
    public void renderBatch(List<RenderableObject> objects, ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        Matrix4f matrix = poseStack.last().pose();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        for (RenderableObject obj : objects) {
            float alpha = obj.opacity;
            float red = RenderUtil.getRed(obj.color);
            float green = RenderUtil.getGreen(obj.color);
            float blue = RenderUtil.getBlue(obj.color);

            bufferbuilder.vertex(matrix, obj.x, obj.maxY, 0f).color(red, green, blue, alpha).endVertex();
            bufferbuilder.vertex(matrix, obj.maxX, obj.maxY, 0f).color(red, green, blue, alpha).endVertex();
            bufferbuilder.vertex(matrix, obj.maxX, obj.y, 0f).color(red, green, blue, alpha).endVertex();
            bufferbuilder.vertex(matrix, obj.x, obj.y, 0f).color(red, green, blue, alpha).endVertex();
        }

        BufferUploader.drawWithShader(bufferbuilder.end());
    }
}

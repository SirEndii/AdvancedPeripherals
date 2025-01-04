package de.srendi.advancedperipherals.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import dan200.computercraft.api.client.TransformedModel;
import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.util.DataStorageUtil;
import de.srendi.advancedperipherals.lib.turtle.ClockwiseAnimatedTurtleUpgrade;
import net.minecraft.core.component.DataComponentPatch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MetaTurtleUpgradeModeller<T extends ClockwiseAnimatedTurtleUpgrade<?>> implements TurtleUpgradeModeller<T> {

    @NotNull
    @Override
    public TransformedModel getModel(T upgrade, @Nullable ITurtleAccess turtle, TurtleSide side, DataComponentPatch dataComponentPatch) {
        if (upgrade.getLeftModel() == null) {
            PoseStack stack = new PoseStack();
            stack.pushPose();
            stack.translate(0.0f, 0.5f, 0.5f);
            if (turtle != null) {
                int rotationStep = DataStorageUtil.RotationCharge.get(turtle, side);
                stack.mulPose(Axis.XN.rotationDegrees(-10f * rotationStep));
            }
            stack.translate(0.0f, -0.5f, -0.5f);
            stack.mulPose(Axis.YN.rotationDegrees(90));
            if (side == TurtleSide.LEFT) {
                stack.translate(0, 0, -0.6);
            } else {
                stack.translate(0, 0, -1.4);
            }
            return TransformedModel.of(upgrade.getCraftingItem(), new Transformation(stack.last().pose()));
        }
        return TransformedModel.of(side == TurtleSide.LEFT ? upgrade.getLeftModel() : upgrade.getRightModel());
    }

}

package de.srendi.advancedperipherals.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;

public class ScanUtils {
    public static void relativeTraverseBlocks(Level world, Vec3 center, double radius, BiConsumer<BlockState, Vec3> consumer) {
        traverseBlocks(world, center, radius, consumer, true);
    }

    public static void traverseBlocks(Level world, Vec3 center, double radius, BiConsumer<BlockState, Vec3> consumer) {
        traverseBlocks(world, center, radius, consumer, false);
    }

    public static void traverseBlocks(Level world, Vec3 center, double radius, BiConsumer<BlockState, Vec3> consumer, boolean relativePosition) {
        final double x = center.x, y = center.y, z = center.z;
        for (int oX = (int) (x - radius); oX <= (int) (x + radius); oX++) {
            for (int oY = (int) (y - radius); oY <= (int) (y + radius); oY++) {
                for (int oZ = (int) (z - radius); oZ <= (int) (z + radius); oZ++) {
                    BlockPos subPos = new BlockPos(oX, oY, oZ);
                    BlockState blockState = world.getBlockState(subPos);
                    if (!blockState.isAir()) {
                        if (relativePosition) {
                            consumer.accept(blockState, new Vec3(oX + 0.5 - center.x, oY + 0.5 - center.y, oZ + 0.5 - center.z));
                        } else {
                            consumer.accept(blockState, new Vec3(oX, oY, oZ));
                        }
                    }
                }
            }
        }
    }
}

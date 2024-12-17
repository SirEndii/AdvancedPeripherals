package de.srendi.advancedperipherals.common.blocks.blockentities;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.DistanceDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.BaseBlock;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.network.APNetworking;
import de.srendi.advancedperipherals.common.network.toclient.DistanceDetectorSyncPacket;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import de.srendi.advancedperipherals.common.util.HitResultUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DistanceDetectorEntity extends PeripheralBlockEntity<DistanceDetectorPeripheral> {

    private volatile double maxRange = APConfig.PERIPHERALS_CONFIG.distanceDetectorRange.get();
    private final AtomicInteger currentDistance = new AtomicInteger(Float.floatToRawIntBits(0));
    private final AtomicBoolean showLaser = new AtomicBoolean(true);
    private volatile boolean periodicallyCalculate = false;
    private volatile boolean ignoreTransparent = true;
    private volatile DistanceDetectorPeripheral.DetectionType detectionType = DistanceDetectorPeripheral.DetectionType.BOTH;

    public DistanceDetectorEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.DISTANCE_DETECTOR.get(), pos, state);
    }

    @NotNull
    @Override
    protected DistanceDetectorPeripheral createPeripheral() {
        return new DistanceDetectorPeripheral(this);
    }

    public void setShowLaser(boolean showLaser) {
        if (this.showLaser.getAndSet(showLaser) != showLaser) {
            APNetworking.sendToAll(new DistanceDetectorSyncPacket(getBlockPos(), getLevel().dimension(), this.getCurrentDistance(), showLaser));
        }
    }

    public void setCurrentDistance(float currentDistance) {
        int currentDistanceBits = Float.floatToRawIntBits(currentDistance);
        if (this.currentDistance.getAndSet(currentDistanceBits) != currentDistanceBits) {
            APNetworking.sendToAll(new DistanceDetectorSyncPacket(getBlockPos(), getLevel().dimension(), currentDistance, this.getLaserVisibility()));
        }
    }

    public void setShouldCalculatePeriodically(boolean periodicallyCalculate) {
        this.periodicallyCalculate = periodicallyCalculate;
    }

    public double getMaxDistance() {
        return this.maxRange;
    }

    public void setMaxRange(double maxRange) {
        this.maxRange = Math.min(Math.max(maxRange, 0), APConfig.PERIPHERALS_CONFIG.distanceDetectorRange.get());
    }

    public float getCurrentDistance() {
        return Float.intBitsToFloat(this.currentDistance.get());
    }

    public boolean getLaserVisibility() {
        return this.showLaser.get();
    }

    public boolean shouldCalculatePeriodically() {
        return this.periodicallyCalculate;
    }

    public boolean ignoreTransparent() {
        return this.ignoreTransparent;
    }

    public void setIgnoreTransparent(boolean ignoreTransparent) {
        this.ignoreTransparent = ignoreTransparent;
    }

    public DistanceDetectorPeripheral.DetectionType getDetectionType() {
        return this.detectionType;
    }

    public void setDetectionType(DistanceDetectorPeripheral.DetectionType detectionType) {
        this.detectionType = detectionType;
    }

    @Override
    public <T extends BlockEntity> void handleTick(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.getGameTime() % APConfig.PERIPHERALS_CONFIG.distanceDetectorUpdateRate.get() == 0 && this.shouldCalculatePeriodically()) {
            // We calculate the distance every 2 ticks, so we do not have to run the getDistance function of the peripheral
            // on the main thread which prevents the 1 tick yield time of the function.
            // The calculateDistance function is not thread safe, so we have to run it on the main thread.
            // It should be okay to run that function every 2 ticks, calculating it does not take too much time.
            this.calculateAndUpdateDistance();
        }
    }

    @Override
    public AABB getRenderBoundingBox() {
        final float currentDistance = this.getCurrentDistance();
        Direction direction = getBlockState().getValue(BaseBlock.ORIENTATION).front();
        return AABB.ofSize(Vec3.atCenterOf(getBlockPos()), direction.getStepX() * currentDistance + 1, direction.getStepY() * currentDistance + 1, direction.getStepZ() * currentDistance + 1)
                .move(direction.getStepX() * currentDistance / 2, direction.getStepY() * currentDistance / 2, direction.getStepZ() * currentDistance / 2);
    }

    public double calculateDistance() {
        final double maxRange = this.maxRange;
        Direction direction = getBlockState().getValue(BaseBlock.ORIENTATION).front();
        Vec3 center = Vec3.atCenterOf(getBlockPos());
        Vec3 from = center.add(direction.getStepX() * 0.501, direction.getStepY() * 0.501, direction.getStepZ() * 0.501);
        Vec3 to = from.add(direction.getStepX() * maxRange, direction.getStepY() * maxRange, direction.getStepZ() * maxRange);
        HitResult result = getResult(to, from);

        return calculateDistance(result, level, center, direction);
    }

    public double calculateAndUpdateDistance() {
        double distance = this.calculateDistance();
        this.setCurrentDistance((float) distance);
        return distance;
    }

    private HitResult getResult(Vec3 to, Vec3 from) {
        return switch (this.detectionType) {
            case ENTITIES -> HitResultUtil.getEntityHitResult(to, from, getLevel());
            case BLOCK -> HitResultUtil.getBlockHitResult(to, from, getLevel(), this.ignoreTransparent);
            default -> HitResultUtil.getHitResult(to, from, getLevel(), this.ignoreTransparent);
        };
    }

    private static float calculateDistance(HitResult result, Level level, Vec3 center, Direction direction) {
        float distance = 0;
        if (result.getType() != HitResult.Type.MISS) {
            if (result instanceof BlockHitResult blockHitResult) {
                BlockState resultBlock = level.getBlockState(blockHitResult.getBlockPos());
                distance = distManhattan(Vec3.atCenterOf(blockHitResult.getBlockPos()), center);

                distance = amendDistance(resultBlock, direction, distance);
            }
            if (result instanceof EntityHitResult entityHitResult) {
                distance = distManhattan(entityHitResult.getLocation(), center);
            }
        }
        return distance;
    }

    private static float amendDistance(BlockState resultBlock, Direction direction, float distance) {
        if (resultBlock.getBlock() instanceof SlabBlock && direction.getAxis() == Direction.Axis.Y) {
            SlabType type = resultBlock.getValue(SlabBlock.TYPE);
            if (type == SlabType.TOP && direction == Direction.UP)
                return distance + 0.5f;
            if (type == SlabType.BOTTOM && direction == Direction.DOWN)
                return distance - 0.5f;
        }
        return distance;
    }

    private static float distManhattan(Vec3 from, Vec3 to) {
        return Math.abs((float)(from.x - to.x)) + Math.abs((float)(from.y - to.y)) + Math.abs((float)(from.z - to.z));
    }
}

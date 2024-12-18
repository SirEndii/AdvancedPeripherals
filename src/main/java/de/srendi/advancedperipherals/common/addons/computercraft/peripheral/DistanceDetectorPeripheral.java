package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.DistanceDetectorEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;

public class DistanceDetectorPeripheral extends BasePeripheral<BlockEntityPeripheralOwner<DistanceDetectorEntity>> {

    public static final String PERIPHERAL_TYPE = "distance_detector";

    public DistanceDetectorPeripheral(DistanceDetectorEntity tileEntity) {
        super(PERIPHERAL_TYPE, new BlockEntityPeripheralOwner<>(tileEntity));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableDistanceDetector.get();
    }

    @LuaFunction
    public final void setLaserVisibility(boolean laser) {
        getPeripheralOwner().tileEntity.setShowLaser(laser);
    }

    @LuaFunction
    public final boolean getLaserVisibility() {
        return getPeripheralOwner().tileEntity.getLaserVisibility();
    }

    @LuaFunction
    public final void setIgnoreTransparency(boolean enable) {
        getPeripheralOwner().tileEntity.setIgnoreTransparent(enable);
    }

    @LuaFunction
    public final boolean ignoresTransparency() {
        return getPeripheralOwner().tileEntity.ignoreTransparent();
    }

    @LuaFunction
    public final void setDetectionMode(IArguments args) throws LuaException {
        Object mode = args.get(0);
        if (mode == null) {
            throw new LuaException("arg #1 must provide a mode name or an index between [0, 2]");
        }
        DetectionType detectionType;
        if (mode instanceof Number modeInd) {
            int index = Math.min(Math.max(modeInd.intValue(), 0), 2);
            detectionType = DetectionType.values()[index]
        } else if (mode instanceof String modeStr) {
            detectionType = switch (modeStr.toUpperCase()) {
                case "BLOCK" -> DetectionType.BLOCK;
                case "ENTITIES" -> DetectionType.ENTITIES;
                case "BOTH" -> DetectionType.BOTH;
                default -> throw new LuaException("Unknown detection mode '" + mode + "'");
            }
        } else {
            throw new LuaException("arg #1 must be a string or a number");
        }
        getPeripheralOwner().tileEntity.setDetectionType(detectionType);
    }

    @LuaFunction
    public final boolean detectsEntities() {
        DetectionType detectionType = getPeripheralOwner().tileEntity.getDetectionType();
        return detectionType == DetectionType.ENTITIES || detectionType == DetectionType.BOTH;
    }

    @LuaFunction
    public final boolean detectsBlocks() {
        DetectionType detectionType = getPeripheralOwner().tileEntity.getDetectionType();
        return detectionType == DetectionType.BLOCK || detectionType == DetectionType.BOTH;
    }

    @LuaFunction
    public final String getDetectionMode() {
        DetectionType detectionType = getPeripheralOwner().tileEntity.getDetectionType();
        return detectionType.toString();
    }

    @LuaFunction
    public final double getDistance() {
        return getPeripheralOwner().tileEntity.getCurrentDistance();
    }

    @LuaFunction(mainThread = true)
    public final double calculateDistance() {
        return getPeripheralOwner().tileEntity.calculateAndUpdateDistance();
    }

    @LuaFunction
    public final boolean shouldCalculatePeriodically() {
        return getPeripheralOwner().tileEntity.shouldCalculatePeriodically();
    }

    @LuaFunction
    public final void setCalculatePeriodically(boolean shouldRenderPeriodically) {
        getPeripheralOwner().tileEntity.setShouldCalculatePeriodically(shouldRenderPeriodically);
    }

    @LuaFunction
    public final void setMaxRange(double maxDistance) {
        getPeripheralOwner().tileEntity.setMaxRange((float) maxDistance);
    }

    @LuaFunction
    public final double getMaxRange() {
        return getPeripheralOwner().tileEntity.getMaxRange();
    }

    public enum DetectionType {
        BLOCK,
        ENTITIES,
        BOTH
    }

}

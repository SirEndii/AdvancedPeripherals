package de.srendi.advancedperipherals.common.util;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.InventoryManagerPeripheral;
import de.srendi.advancedperipherals.common.util.inventory.FluidUtil;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import org.joml.primitives.AABBic;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.properties.ShipInertiaData;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class LuaConverter {

    public static Map<String, Object> entityToLua(Entity entity, boolean detailed) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", entity.getId());
        data.put("uuid", entity.getStringUUID());
        EntityType<?> type = entity.getType();
        data.put("type", type.getDescriptionId());
        data.put("category", type.getCategory());
        data.put("canBurn", entity.fireImmune());
        data.put("canFreeze", entity.canFreeze());
        data.put("isGlowing", entity.isCurrentlyGlowing());
        data.put("isUnderWater", entity.isUnderWater());
        data.put("isInLava", entity.isInLava());
        data.put("isInWall", entity.isInWall());
        return data;
    }

    public static Map<String, Object> livingEntityToLua(LivingEntity entity, boolean detailed) {
        Map<String, Object> data = entityToLua(entity, detailed);
        data.put("baby", entity.isBaby());
        data.put("health", entity.getHealth());
        data.put("maxHealth", entity.getMaxHealth());
        data.put("lastDamageSource", entity.getLastDamageSource() == null ? null : entity.getLastDamageSource().toString());
        if (detailed) {
            Map<String, Object> effMap = new HashMap<>();
            entity.getActiveEffectsMap().forEach((key, value) -> {
                effMap.put(key.getDescriptionId(), effectToObject(value));
            });
            data.put("effects", effMap);
        }
        return data;
    }

    public static Map<String, Object> animalToLua(Animal animal, ItemStack itemInHand, boolean detailed) {
        Map<String, Object> data = livingEntityToLua(animal, detailed);
        data.put("baby", animal.isBaby());
        data.put("inLove", animal.isInLove());
        data.put("aggressive", animal.isAggressive());
        if (animal instanceof IForgeShearable shareable && !itemInHand.isEmpty()) {
            data.put("shareable", shareable.isShearable(itemInHand, animal.level, animal.blockPosition()));
        }
        return data;
    }

    public static Map<String, Object> playerToLua(Player player, boolean detailed) {
        Map<String, Object> data = livingEntityToLua(player, detailed);
        data.put("score", player.getScore());
        data.put("luck", player.getLuck());
        Inventory inv = player.getInventory();
        data.put("handSlot", inv.selected);
        if (detailed) {
            Map<Integer, Object> invMap = new HashMap<>();
            for (int slot = 0; slot < inv.getContainerSize(); slot++) {
                ItemStack item = inv.getItem(slot);
                if (!item.isEmpty()) {
                    invMap.put(slot, itemStackToObject(item));
                }
            }
            data.put("inventory", invMap);
        }
        return data;
    }

    public static Map<String, Object> completeEntityToLua(Entity entity) {
        return completeEntityToLua(entity, false);
    }

    public static Map<String, Object> completeEntityToLua(Entity entity, boolean detailed) {
        return completeEntityToLua(entity, ItemStack.EMPTY, detailed);
    }

    public static Map<String, Object> completeEntityToLua(Entity entity, ItemStack itemInHand) {
        return completeEntityToLua(entity, itemInHand, false);
    }

    public static Map<String, Object> completeEntityToLua(Entity entity, ItemStack itemInHand, boolean detailed) {
        if (entity instanceof Player player) return playerToLua(player, detailed);
        if (entity instanceof Animal animal) return animalToLua(animal, itemInHand, detailed);
        if (entity instanceof LivingEntity livingEntity) return livingEntityToLua(livingEntity, detailed);
        return entityToLua(entity, detailed);
    }

    public static Map<String, Object> completeEntityWithPositionToLua(Entity entity, BlockPos pos) {
        return completeEntityWithPositionToLua(entity, pos, false);
    }

    public static Map<String, Object> completeEntityWithPositionToLua(Entity entity, Vec3 pos) {
        return completeEntityWithPositionToLua(entity, pos, false);
    }

    public static Map<String, Object> completeEntityWithPositionToLua(Entity entity, BlockPos pos, boolean detailed) {
        return completeEntityWithPositionToLua(entity, ItemStack.EMPTY, pos, detailed);
    }

    public static Map<String, Object> completeEntityWithPositionToLua(Entity entity, Vec3 pos, boolean detailed) {
        return completeEntityWithPositionToLua(entity, ItemStack.EMPTY, pos, detailed);
    }

    public static Map<String, Object> completeEntityWithPositionToLua(Entity entity, ItemStack itemInHand, BlockPos pos, boolean detailed) {
        return completeEntityWithPositionToLua(entity, itemInHand, Vec3.atCenterOf(pos), detailed);
    }

    public static Map<String, Object> completeEntityWithPositionToLua(Entity entity, ItemStack itemInHand, Vec3 pos, boolean detailed) {
        Map<String, Object> data = completeEntityToLua(entity, itemInHand, detailed);
        data.put("x", entity.getX() - pos.x);
        data.put("y", entity.getY() - pos.y);
        data.put("z", entity.getZ() - pos.z);
        return data;
    }

    /**
     * Block states to a lua representable object
     *
     * @param blockStateValue block state see {@link net.minecraft.world.level.block.state.BlockState#getValue(Property)}
     * @return the state cast to a lua representable object
     */
    public static Object stateToObject(Comparable<?> blockStateValue) {
        if (blockStateValue == null) {
            return null;
        } else if (blockStateValue instanceof Boolean || blockStateValue instanceof Number || blockStateValue instanceof String) {
            // Just return the value since lua can represent them just fine
            return blockStateValue;
        } else if (blockStateValue instanceof StringRepresentable stringRepresentable) {
            return stringRepresentable.getSerializedName();
        } else {
            return null;
        }
    }

    @Nullable
    public static Object posToObject(BlockPos pos) {
        if (pos == null) {
            return null;
        }

        Map<String, Object> map = new HashMap<>(3);
        map.put("x", pos.getX());
        map.put("y", pos.getY());
        map.put("z", pos.getZ());
        return map;
    }

    @Nullable
    public static Map<String, Object> itemStackToObject(@NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        Map<String, Object> map = itemToObject(stack.getItem());
        CompoundTag nbt = stack.copy().getOrCreateTag();
        map.put("count", stack.getCount());
        map.put("displayName", stack.getDisplayName().getString());
        map.put("maxStackSize", stack.getMaxStackSize());
        map.put("nbt", NBTUtil.toLua(nbt));
        map.put("nbtHash", NBTUtil.getNBTHash(nbt));
        map.put("fingerprint", ItemUtil.getFingerprint(stack));
        return map;
    }

    @Nullable
    public static Map<String, Object> fluidStackToObject(@NotNull FluidStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        Map<String, Object> map = fluidToObject(stack.getFluid());
        CompoundTag nbt = stack.copy().getOrCreateTag();
        map.put("count", stack.getAmount());
        map.put("displayName", stack.getDisplayName().getString());
        map.put("nbt", NBTUtil.toLua(nbt));
        map.put("nbtHash", NBTUtil.getNBTHash(nbt));
        map.put("fingerprint", FluidUtil.getFingerprint(stack));
        return map;
    }

    public static Map<String, Object> itemStackToObject(@NotNull ItemStack itemStack, int amount) {
        ItemStack stack = itemStack.copy();
        stack.setCount(amount);
        return itemStackToObject(stack);
    }

    /**
     * Returns the stack but with a slot entry. Used to prevent zero indexed tables
     *
     * @param stack the item stack
     * @param slot  the slot of the item
     * @return a Map containing proper item stack details
     * @see InventoryManagerPeripheral#getItems()
     */
    @Nullable
    public static Map<String, Object> stackToObjectWithSlot(@NotNull ItemStack stack, int slot) {
        if (stack.isEmpty()) {
            return null;
        }
        Map<String, Object> map = itemStackToObject(stack);
        map.put("slot", slot + 1);
        return map;
    }

    public static Map<String, Object> itemToObject(@NotNull Item item) {
        Map<String, Object> map = new HashMap<>();
        map.put("tags", tagsToList(() -> item.builtInRegistryHolder().tags()));
        map.put("name", ItemUtil.getRegistryKey(item).toString());
        return map;
    }

    public static Map<String, Object> fluidToObject(@NotNull Fluid fluid) {
        Map<String, Object> map = new HashMap<>();
        FluidType fluidType = fluid.getFluidType();
        map.put("tags", tagsToList(() -> fluid.builtInRegistryHolder().tags()));
        map.put("name", FluidUtil.getRegistryKey(fluid).toString());
        map.put("density", fluidType.getDensity());
        map.put("lightLevel", fluidType.getLightLevel());
        map.put("temperature", fluidType.getTemperature());
        map.put("viscosity", fluidType.getViscosity());
        return map;
    }

    public static <T> List<String> tagsToList(@NotNull Supplier<Stream<TagKey<T>>> tags) {
        // We use new ArrayList here instead of Collections.emptyList to prevent an issue with
        // textutils.serialise. I just hope this does not lead to a memory leak
        if (tags.get().findAny().isEmpty()) return new ArrayList<>();
        return tags.get().map(LuaConverter::tagToString).toList();
    }

    public static <T> String tagToString(@NotNull TagKey<T> tag) {
        return tag.registry().location() + "/" + tag.location();
    }

    // BlockPos tricks
    public static BlockPos convertToBlockPos(Map<?, ?> table) throws LuaException {
        if (!table.containsKey("x") || !table.containsKey("y") || !table.containsKey("z")) {
            throw new LuaException("Table should contains key 'x', 'y' and 'z'");
        }
        if (!(table.get("x") instanceof Number x) || !(table.get("y") instanceof Number y) || !(table.get("z") instanceof Number z)) {
            throw new LuaException("Position should be numbers");
        }
        return new BlockPos(x.intValue(), y.intValue(), z.intValue());
    }

    public static BlockPos convertToBlockPos(BlockPos center, Map<?, ?> table) throws LuaException {
        BlockPos relative = convertToBlockPos(table);
        return new BlockPos(center.getX() + relative.getX(), center.getY() + relative.getY(), center.getZ() + relative.getZ());
    }

    public static Object effectToObject(MobEffectInstance effect) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", effect.getDescriptionId());
        map.put("duration", effect.getDuration());
        map.put("amplifier", effect.getAmplifier());
        return map;
    }

    public static Map<String, Object> shipToObject(ServerShip ship) {
        return shipToObject(ship, null);
    }

    public static Map<String, Object> shipToObject(ServerShip ship, Vec3 pos) {
        Map<String, Object> map = new HashMap<>();

        map.put("id", ship.getId());
        map.put("slug", ship.getSlug());

        ShipTransform tf = ship.getTransform();

        Vector3dc shipPos = tf.getShipPositionInShipCoordinates();
        if (pos != null) {
            Vector3dc worldPos = tf.getShipPositionInWorldCoordinates();
            map.put("x", worldPos.x() - pos.x);
            map.put("y", worldPos.y() - pos.y);
            map.put("z", worldPos.z() - pos.z);
        }
        Quaterniondc rot = tf.getShipToWorldRotation();
        final double rotX = rot.x(), rotY = rot.y(), rotZ = rot.z(), rotW = rot.w();
        map.put("rotate", Map.of("x", rotX, "y", rotY, "z", rotZ, "w", rotW));
        // Not really correct?
        // map.put("pitch", Math.toDegrees(Math.asin(-2 * (rotX * rotZ - rotW * rotY))));
        // map.put("roll", Math.toDegrees(Math.atan2(2 * (rotX * rotY + rotW * rotZ), rotW * rotW + rotX * rotX - rotY * rotY - rotZ * rotZ)));
        // map.put("yaw", Math.toDegrees(Math.atan2(2 * (rotY * rotZ + rotW * rotX), rotW * rotW - rotX * rotX - rotY * rotY + rotZ * rotZ)));

        AABBic box = ship.getShipAABB();
        if (box != null) {
            map.put("size", Map.of("x", box.maxX() - box.minX(), "y", box.maxY() - box.minY(), "z", box.maxZ() - box.minZ()));
            map.put("corner", Map.of("x", shipPos.x() - box.minX(), "y", shipPos.y() - box.minY(), "z", shipPos.z() - box.minZ()));
        }
        Vector3dc omega = ship.getOmega();
        map.put("omega", Map.of("x", omega.x(), "y", omega.y(), "z", omega.z()));
        Vector3dc velocity = ship.getVelocity();
        map.put("isStatic", ship.isStatic());
        map.put("velocity", Map.of("x", velocity.x(), "y", velocity.y(), "z", velocity.z()));

        ShipInertiaData data = ship.getInertiaData();
        map.put("mass", data.getMass());
        Vector3d com = tf.getShipToWorld().transformPosition(data.getCenterOfMassInShipSpace(), new Vector3d());
        if (pos != null) {
            map.put("centerOfMass", Map.of("x", com.x - pos.x, "y", com.y - pos.y, "z", com.z - pos.z));
        }
        return map;
    }

    public static Map<String, Object> shipToObjectOnShip(ServerShip ship, Vec3 pos) {
        Map<String, Object> map = shipToObject(ship);
        Vector3dc shipPos = ship.getTransform().getShipPositionInShipCoordinates();
        map.put("x", shipPos.x() - pos.x);
        map.put("y", shipPos.y() - pos.y);
        map.put("z", shipPos.z() - pos.z);
        Vector3dc com = ship.getInertiaData().getCenterOfMassInShipSpace();
        map.put("centerOfMass", Map.of("x", com.x() - pos.x, "y", com.y() - pos.y, "z", com.z() - pos.z));
        return map;
    }
}

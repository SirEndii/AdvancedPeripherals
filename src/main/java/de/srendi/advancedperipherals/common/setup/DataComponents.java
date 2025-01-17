package de.srendi.advancedperipherals.common.setup;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.UUID;
import java.util.function.UnaryOperator;

public class DataComponents {

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DataComponentPatch>> TURTLE_UPGRADE_STORED_DATA = registerDataComponent("stored_data");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DataComponentPatch>> ABILITY_COOLDOWN = registerDataComponent("cooldowns");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> OWNER = registerUUID("owner_id");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> CHUNKY_ID = registerUUID("chunky_id");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ROTATION_CHARGE_SETTING = registerInt("rotation_charge_setting");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FUEL_CONSUMPTION_RATE = registerInt("fuel_consumption_rate");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CompoundTag>> ENTITY_TRANSFER = registerNBT("entity_transfer");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> WORLD_DATA_MARK = registerString("world_data_mark");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CompoundTag>> POINT_DATA_MARK = registerNBT("point_data_mark");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CompoundTag>> CONSUMED_ENTITY_COMPOUND = registerNBT("consumed_entity_compound");

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> SIMPLE_FREE_OPERATION = registerLong("free_operation_cooldown");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> SINGLE_OPERATION = registerLong("single_operation");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> SPHERE_OPERATION = registerLong("sphere_operation");

    public static void register() {
    }

    private static <TYPE> DeferredHolder<DataComponentType<?>, DataComponentType<TYPE>> simple(String name, UnaryOperator<DataComponentType.Builder<TYPE>> operator) {
        return Registration.DATA_COMPONENT_TYPES.register(name, () -> operator.apply(DataComponentType.builder()).build());
    }

    private static DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> registerBoolean(String name) {
        return simple(name, builder -> builder.persistent(Codec.BOOL)
                .networkSynchronized(ByteBufCodecs.BOOL));
    }

    private static DeferredHolder<DataComponentType<?>, DataComponentType<String>> registerString(String name) {
        return simple(name, builder -> builder.persistent(Codec.STRING)
                .networkSynchronized(ByteBufCodecs.STRING_UTF8));
    }

    private static DeferredHolder<DataComponentType<?>, DataComponentType<DataComponentPatch>> registerDataComponent(String name) {
        return simple(name, builder -> builder.persistent(DataComponentPatch.CODEC)
                .networkSynchronized(DataComponentPatch.STREAM_CODEC));
    }

    private static DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> registerNonNegativeInt(String name) {
        return simple(name, builder -> builder.persistent(ExtraCodecs.POSITIVE_INT)
                .networkSynchronized(ByteBufCodecs.VAR_INT));
    }

    private static DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> registerInt(String name) {
        return simple(name, builder -> builder.persistent(Codec.INT)
                .networkSynchronized(ByteBufCodecs.VAR_INT));
    }

    private static DeferredHolder<DataComponentType<?>, DataComponentType<Long>> registerLong(String name) {
        return simple(name, builder -> builder.persistent(Codec.LONG)
                .networkSynchronized(ByteBufCodecs.VAR_LONG));
    }

    private static DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> registerUUID(String name) {
        return simple(name, builder -> builder.persistent(UUIDUtil.CODEC)
                .networkSynchronized(UUIDUtil.STREAM_CODEC));
    }

    private static DeferredHolder<DataComponentType<?>, DataComponentType<CompoundTag>> registerNBT(String name) {
        return simple(name, builder -> builder.persistent(CompoundTag.CODEC));
    }

    private static DeferredHolder<DataComponentType<?>, DataComponentType<Component>> registerComponent(String name) {
        return simple(name, builder -> builder.persistent(ComponentSerialization.FLAT_CODEC)
                .networkSynchronized(ComponentSerialization.STREAM_CODEC)
                .cacheEncoding());
    }

    private static <TYPE> DeferredHolder<DataComponentType<?>, DataComponentType<ResourceKey<TYPE>>> registerResourceKey(String name, ResourceKey<? extends Registry<TYPE>> registryKey) {
        return simple(name, builder -> builder.persistent(ResourceKey.codec(registryKey))
                .networkSynchronized(ResourceKey.streamCodec(registryKey)));
    }
}

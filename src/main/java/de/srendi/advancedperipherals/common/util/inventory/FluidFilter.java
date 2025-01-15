package de.srendi.advancedperipherals.common.util.inventory;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.core.apis.TableHelper;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.DataComponentUtil;
import de.srendi.advancedperipherals.common.util.NBTUtil;
import de.srendi.advancedperipherals.common.util.Pair;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Map;

//TODO tag
public class FluidFilter {

    private Fluid fluid = Fluids.EMPTY;
    private TagKey<Fluid> tag = null;
    private Tag componentsAsNbt = null;
    private PatchedDataComponentMap components;
    private int count = 1000;
    private String fingerprint = "";

    private FluidFilter() {
    }

    public static Pair<FluidFilter, String> parse(Map<?, ?> item) {
        FluidFilter fluidFilter = empty();
        // If the map is empty, return a filter without any filters
        if (item.isEmpty())
            return Pair.of(fluidFilter, null);
        if (item.containsKey("name")) {
            try {
                String name = TableHelper.getStringField(item, "name");
                if (name.startsWith("#")) {
                    fluidFilter.tag = TagKey.create(Registries.FLUID, ResourceLocation.parse(name.substring(1)));
                } else if ((fluidFilter.fluid = ItemUtil.getRegistryEntry(name, BuiltInRegistries.FLUID)) == null) {
                    return Pair.of(null, "FLUID_NOT_FOUND");
                }
            } catch (LuaException luaException) {
                return Pair.of(null, "NO_VALID_FLUID");
            }
        }
        if (item.containsKey("components")) {
            try {
                fluidFilter.componentsAsNbt = NBTUtil.fromText(TableHelper.getStringField(item, "components"));
            } catch (LuaException luaException) {
                try {
                    fluidFilter.componentsAsNbt = NBTUtil.fromText(TableHelper.getTableField(item, "components").toString());
                } catch (LuaException e) {
                    return Pair.of(null, "NO_VALID_COMPONENTS");
                }
            }
        }
        if (item.containsKey("fingerprint")) {
            try {
                fluidFilter.fingerprint = TableHelper.getStringField(item, "fingerprint");
            } catch (LuaException luaException) {
                return Pair.of(null, "NO_VALID_FINGERPRINT");
            }
        }
        if (item.containsKey("count")) {
            try {
                fluidFilter.count = TableHelper.getIntField(item, "count");
            } catch (LuaException luaException) {
                return Pair.of(null, "NO_VALID_COUNT");
            }
        }
        AdvancedPeripherals.debug("Parsed fluid filter: " + fluidFilter);

        return Pair.of(fluidFilter, null);
    }

    public static FluidFilter fromStack(FluidStack stack) {
        FluidFilter filter = empty();
        filter.fluid = stack.getFluid();
        filter.componentsAsNbt = DataComponentUtil.toNbt(stack.getComponentsPatch());
        filter.components = stack.getComponents();
        return filter;
    }

    public static FluidFilter empty() {
        return new FluidFilter();
    }

    public boolean isEmpty() {
        return fingerprint.isEmpty() && fluid == Fluids.EMPTY && tag == null && componentsAsNbt == null;
    }

    public FluidStack toFluidStack() {
        var result = new FluidStack(fluid, count);
        result.applyComponents(components);
        return result;
    }

    public FluidFilter setCount(int count) {
        this.count = count;
        return this;
    }

    public boolean test(FluidStack stack) {
        if (!fingerprint.isEmpty()) {
            String testFingerprint = FluidUtil.getFingerprint(stack);
            return fingerprint.equals(testFingerprint);
        }

        if (fluid != Fluids.EMPTY && !stack.getFluid().isSame(fluid)) {
            return false;
        }
        if (tag != null && !stack.getFluid().is(tag)) {
            return false;
        }
        if (componentsAsNbt != null && !DataComponentUtil.toNbt(stack.getComponentsPatch()).equals(componentsAsNbt)) {
            return false;
        }
        return true;
    }

    public int getCount() {
        return count;
    }

    public Fluid getFluid() {
        return fluid;
    }

    public Tag getComponentsAsNbt() {
        return componentsAsNbt;
    }

    @Override
    public String toString() {
        return "FluidFilter{" +
                "fluid=" + FluidUtil.getRegistryKey(fluid) +
                ", tag=" + tag +
                ", components=" + componentsAsNbt +
                ", count=" + count +
                ", fingerprint='" + fingerprint + '\'' +
                '}';
    }
}

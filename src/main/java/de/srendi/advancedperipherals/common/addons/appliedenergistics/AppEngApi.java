package de.srendi.advancedperipherals.common.addons.appliedenergistics;

import appeng.api.AECapabilities;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.CraftingJobStatus;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.networking.storage.IStorageService;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.AEKeyFilter;
import appeng.api.storage.IStorageProvider;
import appeng.api.storage.MEStorage;
import appeng.api.storage.cells.IBasicCellItem;
import appeng.blockentity.storage.DriveBlockEntity;
import appeng.me.cells.BasicCellHandler;
import appeng.me.cells.BasicCellInventory;
import appeng.parts.storagebus.StorageBusPart;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.APAddons;
import de.srendi.advancedperipherals.common.setup.BlockEntityTypes;
import de.srendi.advancedperipherals.common.util.DataComponentUtil;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.inventory.FluidFilter;
import de.srendi.advancedperipherals.common.util.inventory.ItemFilter;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppEngApi {

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                AECapabilities.IN_WORLD_GRID_NODE_HOST,
                BlockEntityTypes.ME_BRIDGE.get(),
                (blockEntity, side) -> blockEntity);
    }

    public static Pair<Long, AEItemKey> findAEStackFromStack(MEStorage monitor, @Nullable ICraftingService crafting, ItemStack item) {
        return findAEStackFromFilter(monitor, crafting, ItemFilter.fromStack(item));
    }

    public static Pair<Long, AEItemKey> findAEStackFromFilter(MEStorage monitor, @Nullable ICraftingService crafting, ItemFilter item) {
        for (Object2LongMap.Entry<AEKey> temp : monitor.getAvailableStacks()) {
            if (temp.getKey() instanceof AEItemKey key && item.test(key.toStack()))
                return Pair.of(temp.getLongValue(), key);
        }

        if (crafting == null)
            return Pair.of(0L, AEItemKey.of(ItemStack.EMPTY));

        for (var temp : crafting.getCraftables(param -> true)) {
            if (temp instanceof AEItemKey key && item.test(key.toStack()))
                return Pair.of(0L, key);
        }

        return Pair.of(0L, AEItemKey.of(ItemStack.EMPTY));
    }

    public static Pair<Long, AEFluidKey> findAEFluidFromStack(MEStorage monitor, @Nullable ICraftingService crafting, FluidStack item) {
        return findAEFluidFromFilter(monitor, crafting, FluidFilter.fromStack(item));
    }

    public static Pair<Long, AEFluidKey> findAEFluidFromFilter(MEStorage monitor, @Nullable ICraftingService crafting, FluidFilter item) {
        for (Object2LongMap.Entry<AEKey> temp : monitor.getAvailableStacks()) {
            if (temp.getKey() instanceof AEFluidKey key && item.test(key.toStack(1)))
                return Pair.of(temp.getLongValue(), key);
        }

        if (crafting == null)
            return null;

        for (var temp : crafting.getCraftables(param -> true)) {
            if (temp instanceof AEFluidKey key && item.test(key.toStack(1)))
                return Pair.of(0L, key);
        }

        return null;
    }

    public static List<Object> listStacks(MEStorage monitor, ICraftingService service) {
        List<Object> items = new ArrayList<>();
        KeyCounter keyCounter = monitor.getAvailableStacks();
        for (Object2LongMap.Entry<AEKey> aeKey : keyCounter) {
            if (aeKey.getKey() instanceof AEItemKey itemKey) {
                items.add(getObjectFromStack(Pair.of(aeKey.getLongValue(), itemKey), service));
            }
        }
        return items;
    }

    public static List<Object> listCraftableStacks(MEStorage monitor, ICraftingService service) {
        List<Object> items = new ArrayList<>();
        KeyCounter keyCounter = monitor.getAvailableStacks();
        Set<AEKey> craftables = service.getCraftables(AEKeyFilter.none());
        for (AEKey aeKey : craftables) {
            if (aeKey instanceof AEItemKey) {
                items.add(getObjectFromStack(Pair.of(keyCounter.get(aeKey), aeKey), service));
            }
        }
        return items;
    }

    public static List<Object> listFluids(MEStorage monitor, ICraftingService service) {
        List<Object> items = new ArrayList<>();
        for (Object2LongMap.Entry<AEKey> aeKey : monitor.getAvailableStacks()) {
            if (aeKey.getKey() instanceof AEFluidKey itemKey) {
                items.add(getObjectFromStack(Pair.of(aeKey.getLongValue(), itemKey), service));
            }
        }
        return items;
    }

    public static List<Object> listGases(MEStorage monitor, ICraftingService service, int flag) {
        List<Object> items = new ArrayList<>();
        for (Object2LongMap.Entry<AEKey> aeKey : monitor.getAvailableStacks()) {
            if (APAddons.appMekLoaded && aeKey.getKey() instanceof MekanismKey itemKey) {
                items.add(getObjectFromStack(Pair.of(aeKey.getLongValue(), itemKey), service));
            }
        }
        return items;
    }

    public static List<Object> listCraftableFluids(MEStorage monitor, ICraftingService service) {
        List<Object> items = new ArrayList<>();
        KeyCounter keyCounter = monitor.getAvailableStacks();
        Set<AEKey> craftables = service.getCraftables(AEKeyFilter.none());
        for (AEKey aeKey : craftables) {
            if (aeKey instanceof AEFluidKey) {
                items.add(getObjectFromStack(Pair.of(keyCounter.get(aeKey), aeKey), service));
            }
        }
        return items;
    }

    public static <T extends AEKey> Map<String, Object> getObjectFromStack(Pair<Long, T> stack, @Nullable ICraftingService service) {
        if (stack.getRight() == null)
            return Collections.emptyMap();
        if (stack.getRight() instanceof AEItemKey itemKey)
            return getObjectFromItemStack(Pair.of(stack.getLeft(), itemKey), service);
        if (stack.getRight() instanceof AEFluidKey fluidKey)
            return getObjectFromFluidStack(Pair.of(stack.getLeft(), fluidKey), service);
        if (APAddons.appMekLoaded && (stack.getRight() instanceof MekanismKey gasKey))
            return getObjectFromGasStack(Pair.of(stack.getLeft(), gasKey), service);

        AdvancedPeripherals.debug("Could not create table from unknown stack " + stack.getRight().getClass() + " - Report this to the maintainer of ap", Level.ERROR);
        return Collections.emptyMap();
    }

    private static Map<String, Object> getObjectFromItemStack(Pair<Long, AEItemKey> stack, @Nullable ICraftingService craftingService) {
        Map<String, Object> map = LuaConverter.stackToObject(stack.getRight().toStack());
        long count = stack.getLeft();
        // We re-set the amount since item stacks can only hold up to 2^31 for the count while ae2 stacks can hold up to 2^63
        map.put("count", count);
        map.put("isCraftable", craftingService != null && craftingService.isCraftable(stack.getRight()));

        return map;
    }

    private static Map<String, Object> getObjectFromFluidStack(Pair<Long, AEFluidKey> stack, @Nullable ICraftingService craftingService) {
        Map<String, Object> map = new HashMap<>();
        long count = stack.getLeft();
        map.put("name", stack.getRight().getFluid().builtInRegistryHolder().key().registry().toString());
        map.put("count", count);
        map.put("displayName", stack.getRight().getDisplayName().getString());
        map.put("tags", LuaConverter.tagsToList(() -> stack.getRight().getFluid().builtInRegistryHolder().tags()));
        map.put("isCraftable", craftingService != null && craftingService.isCraftable(stack.getRight()));

        return map;
    }

    private static Map<String, Object> getObjectFromGasStack(Pair<Long, MekanismKey> stack, @Nullable ICraftingService craftingService) {
        Map<String, Object> map = new HashMap<>();
        long count = stack.getLeft();
        map.put("name", stack.getRight().getStack().getTypeRegistryName().toString());
        map.put("count", count);
        map.put("displayName", stack.getRight().getDisplayName().getString());
        map.put("tags", LuaConverter.tagsToList(() -> stack.getRight().getStack().getTags()));

        return map;
    }

    public static Map<String, Object> getObjectFromCPU(ICraftingCPU cpu) {
        Map<String, Object> map = new HashMap<>();
        long storage = cpu.getAvailableStorage();
        int coProcessors = cpu.getCoProcessors();
        boolean isBusy = cpu.isBusy();
        map.put("storage", storage);
        map.put("coProcessors", coProcessors);
        map.put("isBusy", isBusy);
        map.put("craftingJob", cpu.getJobStatus() != null ? getObjectFromJob(cpu.getJobStatus()) : null);
        map.put("name", cpu.getName() != null ? cpu.getName().getString() : "Unnamed");
        map.put("selectionMode", cpu.getSelectionMode().toString());

        return map;
    }

    public static Map<String, Object> getObjectFromJob(CraftingJobStatus job) {
        Map<String, Object> map = new HashMap<>();
        map.put("storage", getObjectFromGenericStack(job.crafting()));
        map.put("elapsedTimeNanos", job.elapsedTimeNanos());
        map.put("totalItem", job.totalItems());
        map.put("progress", job.progress());

        return map;
    }

    public static Map<String, Object> getObjectFromGenericStack(GenericStack stack) {
        if (stack.what() == null)
            return Collections.emptyMap();
        if (stack.what() instanceof AEItemKey aeItemKey)
            return getObjectFromItemStack(Pair.of(stack.amount(), aeItemKey), null);
        if (stack.what() instanceof AEFluidKey aeFluidKey)
            return getObjectFromFluidStack(Pair.of(stack.amount(), aeFluidKey), null);
        return Collections.emptyMap();
    }

    public static MEStorage getMonitor(IGridNode node) {
        return node.getGrid().getService(IStorageService.class).getInventory();
    }

    public static boolean isItemCrafting(MEStorage monitor, ICraftingService grid, ItemFilter filter,
                                         @Nullable ICraftingCPU craftingCPU) {
        Pair<Long, AEItemKey> stack = AppEngApi.findAEStackFromFilter(monitor, grid, filter);

        // If the item stack does not exist, it cannot be crafted.
        if (stack == null)
            return false;

        // If the passed cpu is null, check all cpus
        if (craftingCPU == null) {
            // Loop through all crafting cpus and check if the item is being crafted.
            for (ICraftingCPU cpu : grid.getCpus()) {
                if (cpu.isBusy()) {
                    CraftingJobStatus jobStatus = cpu.getJobStatus();

                    // avoid null pointer exception
                    if (jobStatus == null)
                        continue;

                    if (jobStatus.crafting().what().equals(stack.getRight()))
                        return true;
                }
            }
        } else {
            if (craftingCPU.isBusy()) {
                CraftingJobStatus jobStatus = craftingCPU.getJobStatus();

                // avoid null pointer exception
                if (jobStatus == null)
                    return false;

                return jobStatus.crafting().what().equals(stack.getRight());
            }
        }

        return false;
    }

    public static boolean isFluidCrafting(MEStorage monitor, ICraftingService grid, FluidFilter filter,
                                          @Nullable ICraftingCPU craftingCPU) {
        Pair<Long, AEFluidKey> stack = AppEngApi.findAEFluidFromFilter(monitor, grid, filter);

        // If the fluid stack does not exist, it cannot be crafted.
        if (stack == null)
            return false;

        // If the passed cpu is null, check all cpus
        if (craftingCPU == null) {
            // Loop through all crafting cpus and check if the fluid is being crafted.
            for (ICraftingCPU cpu : grid.getCpus()) {
                if (cpu.isBusy()) {
                    CraftingJobStatus jobStatus = cpu.getJobStatus();

                    // avoid null pointer exception
                    if (jobStatus == null)
                        continue;

                    if (jobStatus.crafting().what().equals(stack.getRight()))
                        return true;
                }
            }
        } else {
            if (craftingCPU.isBusy()) {
                CraftingJobStatus jobStatus = craftingCPU.getJobStatus();

                // avoid null pointer exception
                if (jobStatus == null)
                    return false;

                return jobStatus.crafting().what().equals(stack.getRight());
            }
        }

        return false;
    }

    public static long getTotalItemStorage(IGridNode node) {
        long total = 0;

        // note: do not query DriveBlockEntity.class specifically here, because it will avoid subclasses, e.g. the ME Extended Drive from ExtendedAE
        Iterator<IGridNode> iterator = node.getGrid().getNodes().iterator();

        while (iterator.hasNext()) {
            if (!(iterator.next().getService(IStorageProvider.class) instanceof DriveBlockEntity entity))
                continue;

            InternalInventory inventory = entity.getInternalInventory();

            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);

                if (stack.isEmpty())
                    continue;

                if (stack.getItem() instanceof IBasicCellItem cell) {
                    if (cell.getKeyType().getClass().isAssignableFrom(AEKeyType.items().getClass())) {
                        total += cell.getBytes(null);
                    }
                }
            }
        }

        iterator = node.getGrid().getMachineNodes(StorageBusPart.class).iterator();

        while (iterator.hasNext()) {
            StorageBusPart bus = (StorageBusPart) iterator.next().getService(IStorageProvider.class);
            net.minecraft.world.level.Level level = bus.getLevel();
            BlockPos connectedInventoryPos = bus.getHost().getBlockEntity().getBlockPos().relative(bus.getSide());

            IItemHandler itemHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, connectedInventoryPos, bus.getSide());
            if (itemHandler != null) {
                for (int i = 0; i < itemHandler.getSlots(); i++) {
                    total += itemHandler.getSlotLimit(i);
                }
            }
        }

        return total;
    }

    public static long getTotalFluidStorage(IGridNode node) {
        long total = 0;

        Iterator<IGridNode> iterator = node.getGrid().getNodes().iterator();

        while (iterator.hasNext()) {
            if (!(iterator.next().getService(IStorageProvider.class) instanceof DriveBlockEntity entity))
                continue;

            InternalInventory inventory = entity.getInternalInventory();

            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);

                if (stack.isEmpty())
                    continue;

                if (stack.getItem() instanceof IBasicCellItem cell) {
                    if (cell.getKeyType().getClass().isAssignableFrom(AEKeyType.fluids().getClass())) {
                        total += cell.getBytes(null);
                    }
                }
            }
        }

        iterator = node.getGrid().getMachineNodes(StorageBusPart.class).iterator();

        while (iterator.hasNext()) {
            StorageBusPart bus = (StorageBusPart) iterator.next().getService(IStorageProvider.class);
            net.minecraft.world.level.Level level = bus.getLevel();
            BlockPos connectedInventoryPos = bus.getHost().getBlockEntity().getBlockPos().relative(bus.getSide());

            IFluidHandler fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, connectedInventoryPos, bus.getSide());
            if (fluidHandler != null) {
                for (int i = 0; i < fluidHandler.getTanks(); i++) {
                    total += fluidHandler.getTankCapacity(i);
                }
            }
        }

        return total;
    }

    public static long getUsedItemStorage(IGridNode node) {
        long used = 0;

        Iterator<IGridNode> iterator = node.getGrid().getNodes().iterator();

        while (iterator.hasNext()) {
            if (!(iterator.next().getService(IStorageProvider.class) instanceof DriveBlockEntity entity))
                continue;

            InternalInventory inventory = entity.getInternalInventory();

            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);

                if (stack.isEmpty())
                    continue;

                if (stack.getItem() instanceof IBasicCellItem cell) {
                    if (cell.getKeyType().getClass().isAssignableFrom(AEKeyType.items().getClass())) {

                        BasicCellInventory cellInventory = BasicCellHandler.INSTANCE.getCellInventory(stack, null);

                        if (cellInventory == null)
                            continue;

                        used += cellInventory.getUsedBytes();
                    }
                }
            }
        }

        iterator = node.getGrid().getMachineNodes(StorageBusPart.class).iterator();

        while (iterator.hasNext()) {
            StorageBusPart bus = (StorageBusPart) iterator.next().getService(IStorageProvider.class);
            KeyCounter keyCounter = bus.getInternalHandler().getAvailableStacks();

            for (Object2LongMap.Entry<AEKey> aeKey : keyCounter) {
                if (aeKey.getKey() instanceof AEItemKey) {
                    used += aeKey.getLongValue();
                }
            }
        }

        return used;
    }

    public static long getUsedFluidStorage(IGridNode node) {
        long used = 0;

        Iterator<IGridNode> iterator = node.getGrid().getNodes().iterator();

        while (iterator.hasNext()) {
            if (!(iterator.next().getService(IStorageProvider.class) instanceof DriveBlockEntity entity))
                continue;

            InternalInventory inventory = entity.getInternalInventory();

            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);

                if (stack.getItem() instanceof IBasicCellItem cell) {
                    if (cell.getKeyType().getClass().isAssignableFrom(AEKeyType.fluids().getClass())) {
                        BasicCellInventory cellInventory = BasicCellHandler.INSTANCE.getCellInventory(stack, null);

                        if (cellInventory == null)
                            continue;

                        used += cellInventory.getUsedBytes();
                    }
                }
            }
        }

        iterator = node.getGrid().getMachineNodes(StorageBusPart.class).iterator();

        while (iterator.hasNext()) {
            StorageBusPart bus = (StorageBusPart) iterator.next().getService(IStorageProvider.class);
            KeyCounter keyCounter = bus.getInternalHandler().getAvailableStacks();

            for (Object2LongMap.Entry<AEKey> aeKey : keyCounter) {
                if (aeKey.getKey() instanceof AEFluidKey) {
                    used += aeKey.getLongValue();
                }
            }
        }

        return used;
    }

    public static long getAvailableItemStorage(IGridNode node) {
        return getTotalItemStorage(node) - getUsedItemStorage(node);
    }

    public static long getAvailableFluidStorage(IGridNode node) {
        return getTotalFluidStorage(node) - getUsedFluidStorage(node);
    }

    public static List<Object> listCells(IGridNode node) {
        List<Object> items = new ArrayList<>();

        Iterator<IGridNode> iterator = node.getGrid().getNodes().iterator();

        if (!iterator.hasNext()) return items;
        while (iterator.hasNext()) {
            IStorageProvider entity = iterator.next().getService(IStorageProvider.class);
            if (!(entity instanceof DriveBlockEntity driveEntity))
                continue;

            InternalInventory inventory = driveEntity.getInternalInventory();

            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);

                if (stack.isEmpty())
                    continue;

                if (stack.getItem() instanceof IBasicCellItem cell) {
                    items.add(getObjectFromCell(cell, stack));
                }
            }
        }

        return items;
    }

    private static Map<String, Object> getObjectFromCell(IBasicCellItem cell, ItemStack stack) {
        Map<String, Object> map = new HashMap<>();

        map.put("item", ItemUtil.getRegistryKey(stack.getItem()).toString());

        String cellType = "";

        if (cell.getKeyType().getClass().isAssignableFrom(AEKeyType.items().getClass())) {
            cellType = "item";
        } else if (cell.getKeyType().getClass().isAssignableFrom(AEKeyType.fluids().getClass())) {
            cellType = "fluid";
        }

        map.put("cellType", cellType);
        map.put("bytesPerType", cell.getBytesPerType(null));
        map.put("totalBytes", cell.getBytes(null));

        return map;
    }

}

package de.srendi.advancedperipherals;

import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.common.setup.Items;
import de.srendi.advancedperipherals.common.setup.Registration;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public class APCreativeTab extends CreativeModeTab {

    public APCreativeTab() {
        super("advancedperipheralstab");
    }

    @Override
    public void fillItemList(NonNullList<ItemStack> items) {
        Registration.ITEMS.getEntries().stream().map(RegistryObject::get).forEach(item -> items.add(new ItemStack(item)));

        items.addAll(turtleUpgrade(Registry.ITEM.getKey(Items.CHUNK_CONTROLLER.get())));
        items.addAll(turtleUpgrade(Registry.ITEM.getKey(net.minecraft.world.item.Items.COMPASS)));

    }

    private static Collection<ItemStack> pocketUpgrade(ResourceLocation pocketId) {
        return Set.of(ItemUtil.makePocket(ItemUtil.POCKET_NORMAL, pocketId.toString()),
                ItemUtil.makePocket(ItemUtil.POCKET_ADVANCED, pocketId.toString()));
    }

    private static Collection<ItemStack> turtleUpgrade(ResourceLocation turtleId) {
        return Set.of(ItemUtil.makeTurtle(ItemUtil.TURTLE_NORMAL, turtleId.toString()),
                ItemUtil.makeTurtle(ItemUtil.TURTLE_ADVANCED, turtleId.toString()));
    }

    @Override
    @NotNull
    public ItemStack makeIcon() {
        return new ItemStack(Items.CHUNK_CONTROLLER.get());
    }
}

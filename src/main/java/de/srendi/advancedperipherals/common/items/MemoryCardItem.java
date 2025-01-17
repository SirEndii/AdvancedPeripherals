package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.items.base.BaseItem;
import de.srendi.advancedperipherals.common.util.EnumColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MemoryCardItem extends BaseItem {

    public MemoryCardItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableInventoryManager.get();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level levelIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, levelIn, tooltip, flagIn);
        CompoundTag data = stack.getOrCreateTag();
        if (data.contains("ownerId")) {
            tooltip.add(EnumColor.buildTextComponent(Component.translatable("item.advancedperipherals.tooltip.binding.bound_to", data.getString("ownerId"))));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if (!worldIn.isClientSide) {
            ItemStack stack = playerIn.getItemInHand(handIn);
            CompoundTag data = stack.getOrCreateTag();
            if (data.contains("ownerId")) {
                playerIn.displayClientMessage(EnumColor.buildTextComponent(Component.translatable("text.advancedperipherals.cleared_memorycard")), true);
                data.remove("ownerId");
            } else {
                playerIn.displayClientMessage(EnumColor.buildTextComponent(Component.translatable("text.advancedperipherals.bind_memorycard")), true);
                data.putUUID("ownerId", playerIn.getUUID());
            }
        }
        return super.use(worldIn, playerIn, handIn);
    }
}

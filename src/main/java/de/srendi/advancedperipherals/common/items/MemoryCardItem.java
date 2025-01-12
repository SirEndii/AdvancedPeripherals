package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.client.ClientUUIDCache;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.items.base.BaseItem;
import de.srendi.advancedperipherals.common.util.EnumColor;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static de.srendi.advancedperipherals.common.setup.DataComponents.OWNER;

public class MemoryCardItem extends BaseItem {

    public static final String OWNER_NBT_KEY = "ownerId";

    public MemoryCardItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableInventoryManager.get();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        Minecraft minecraft = Minecraft.getInstance();
        if (stack.has(OWNER)) {
            String username = ClientUUIDCache.getUsername(stack.get(OWNER), minecraft.player.getUUID());
            if (username == null)
                username = stack.get(OWNER).toString();
            tooltip.add(EnumColor.buildTextComponent(Component.translatable("item.advancedperipherals.tooltip.memory_card.bound", username)));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if (!worldIn.isClientSide) {
            ItemStack stack = playerIn.getItemInHand(handIn);
            if (stack.has(OWNER)) {
                playerIn.displayClientMessage(Component.translatable("text.advancedperipherals.removed_player"), true);
                stack.remove(OWNER);
            } else {
                playerIn.displayClientMessage(Component.translatable("text.advancedperipherals.added_player"), true);
                stack.set(OWNER, playerIn.getUUID());
            }
        }
        return super.use(worldIn, playerIn, handIn);
    }
}

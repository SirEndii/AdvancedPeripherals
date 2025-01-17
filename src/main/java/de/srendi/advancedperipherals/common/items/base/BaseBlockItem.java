package de.srendi.advancedperipherals.common.items.base;

import de.srendi.advancedperipherals.client.KeyBindings;
import de.srendi.advancedperipherals.common.util.EnumColor;
import de.srendi.advancedperipherals.common.util.KeybindUtil;
import de.srendi.advancedperipherals.common.util.TranslationUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class BaseBlockItem extends BlockItem {
    private Component description;

    public BaseBlockItem(Block blockIn, Properties properties) {
        super(blockIn, properties);
    }

    public BaseBlockItem(Block blockIn) {
        super(blockIn, new Properties());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        if (!KeybindUtil.isKeyPressed(KeyBindings.DESCRIPTION_KEYBINDING)) {
            tooltip.add(EnumColor.buildTextComponent(Component.translatable("item.advancedperipherals.tooltip.show_desc", KeyBindings.DESCRIPTION_KEYBINDING.getTranslatedKeyMessage())));
        } else {
            tooltip.add(EnumColor.buildTextComponent(getDescription()));
        }
        if (!isEnabled())
            tooltip.add(EnumColor.buildTextComponent(Component.translatable("item.advancedperipherals.tooltip.disabled")));
    }

    public @NotNull Component getDescription() {
        if (description == null) description = TranslationUtil.itemTooltip(getDescriptionId());
        return description;
    }

    public abstract boolean isEnabled();
}

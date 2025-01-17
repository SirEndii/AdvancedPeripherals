package de.srendi.advancedperipherals.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import dan200.computercraft.client.gui.ClientInputHandler;
import dan200.computercraft.client.gui.widgets.WidgetTerminal;
import dan200.computercraft.core.terminal.Terminal;
import dan200.computercraft.shared.computer.core.InputHandler;
import de.srendi.advancedperipherals.client.screens.base.BaseScreen;
import de.srendi.advancedperipherals.common.container.KeyboardContainer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

/**
 * A simple screen but without any rendering calls. Used to unlock the mouse so we can freely write stuff
 * <p>
 * We just create a terminal which is used to forward all the key presses and mouse clicks but we don't render it.
 */
public class KeyboardScreen extends BaseScreen<KeyboardContainer> {

    protected final InputHandler input;
    private final Terminal terminalData;

    private WidgetTerminal terminal;

    public KeyboardScreen(KeyboardContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        input = new ClientInputHandler(menu);
        terminalData = new Terminal(0, 0, false);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int x, int y, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        float scale = 2f;
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        // Make the text a bit smaller on small screens
        if (screenWidth <= 1080)
            scale = 1f;

        poseStack.scale(scale, scale, 1);
        Component text = Component.translatable("text.advancedperipherals.keyboard.close");
        float textX = (screenWidth / 2f - minecraft.font.width(text) * scale / 2f) / scale;
        minecraft.font.drawShadow(poseStack, text, textX, 1, 0xFFFFFF);
    }

    @Override
    protected void init() {
        passEvents = true;
        KeyMapping.releaseAll();

        super.init();
        minecraft.keyboardHandler.setSendRepeatsToGui(true);

        terminal = addWidget(new WidgetTerminal(terminalData, new ClientInputHandler(menu), 0, 0));
        terminal.visible = false;
        terminal.active = false;
        setFocused(terminal);
    }


    @Override
    protected void renderBg(@NotNull PoseStack matrixStack, float partialTicks, int x, int y) {
    }

    @Override
    public void renderBackground(@NotNull PoseStack pPoseStack) {
    }


    @Override
    public final void removed() {
        super.removed();
        minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        minecraft.player.getInventory().swapPaint(pDelta);
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    public void onClose() {
        // Don't allow closing using standard keys like E. Closing using ESCAPE is still possible due to the keyPressed method
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public final boolean keyPressed(int key, int scancode, int modifiers) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            super.onClose();
            return true;
        }
        // Forward the tab key to the terminal, rather than moving between controls.
        if (key == GLFW.GLFW_KEY_TAB && getFocused() != null && getFocused() == terminal) {
            return getFocused().keyPressed(key, scancode, modifiers);
        }

        return super.keyPressed(key, scancode, modifiers);
    }

    // We prevent jei by increasing the image size, even if we don't render it
    @Override
    public int getSizeX() {
        return 4096;
    }

    @Override
    public int getSizeY() {
        return 4096;
    }

    @Override
    public ResourceLocation getTexture() {
        return null;
    }
}

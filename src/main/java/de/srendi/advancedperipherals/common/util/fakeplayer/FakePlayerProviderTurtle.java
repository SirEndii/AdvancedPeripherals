package de.srendi.advancedperipherals.common.util.fakeplayer;

import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.shared.util.WorldUtil;
import de.srendi.advancedperipherals.common.addons.APAddons;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.Ship;

import java.util.WeakHashMap;
import java.util.function.Function;

public final class FakePlayerProviderTurtle {

    /*
    Highly inspired by https://github.com/SquidDev-CC/plethora/blob/minecraft-1.12/src/main/java/org/squiddev/plethora/integration/computercraft/FakePlayerProviderTurtle.java
    */
    private static final WeakHashMap<ITurtleAccess, APFakePlayer> registeredPlayers = new WeakHashMap<>();

    private FakePlayerProviderTurtle() {
    }

    public static APFakePlayer getPlayer(ITurtleAccess turtle, GameProfile profile) {
        return registeredPlayers.computeIfAbsent(turtle, iTurtleAccess -> new APFakePlayer((ServerLevel) turtle.getLevel(), null, profile));
    }

    public static void load(APFakePlayer player, ITurtleAccess turtle) {
        ServerLevel level = (ServerLevel) turtle.getLevel();
        player.setLevel(level);

        BlockPos pos = turtle.getPosition();
        player.setSourceBlock(pos);

        Vec3 direction = Vec3.atLowerCornerOf(turtle.getDirection().getNormal());
        Vec3 position = Vec3.atCenterOf(pos);
        if (APAddons.vs2Loaded) {
            Ship ship = APAddons.getVS2Ship(level, pos);
            if (ship != null) {
                Matrix4dc matrix = ship.getShipToWorld();
                Vector3d newPos = matrix.transformPosition(new Vector3d(position.x, position.y, position.z));
                Vector3d newDir = matrix.transformDirection(new Vector3d(direction.x, direction.y, direction.z));
                position = new Vec3(newPos.x, newPos.y, newPos.z);
                direction = new Vec3(newDir.x, newDir.y, newDir.z);
            }
        }
        player.lookAt(EntityAnchorArgument.Anchor.FEET, position.add(direction));
        player.moveTo(position.x, position.y, position.z, player.getYRot(), player.getXRot());

        // Player inventory
        Inventory playerInventory = player.getInventory();
        playerInventory.selected = 0;

        // Copy primary items into player inventory and empty the rest
        Container turtleInventory = turtle.getInventory();
        int size = turtleInventory.getContainerSize();
        int largerSize = playerInventory.getContainerSize();
        playerInventory.selected = turtle.getSelectedSlot();
        for (int i = 0; i < size; i++) {
            playerInventory.setItem(i, turtleInventory.getItem(i));
        }
        for (int i = size; i < largerSize; i++) {
            playerInventory.setItem(i, ItemStack.EMPTY);
        }

        // Add properties
        ItemStack activeStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!activeStack.isEmpty()) {
            player.getAttributes().addTransientAttributeModifiers(activeStack.getAttributeModifiers(EquipmentSlot.MAINHAND));
        }
    }

    public static void unload(APFakePlayer player, ITurtleAccess turtle) {
        Inventory playerInventory = player.getInventory();
        playerInventory.selected = 0;

        // Remove properties
        ItemStack activeStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!activeStack.isEmpty()) {
            player.getAttributes().removeAttributeModifiers(activeStack.getAttributeModifiers(EquipmentSlot.MAINHAND));
        }

        // Copy primary items into turtle inventory and then insert/drop the rest
        Container turtleInventory = turtle.getInventory();
        int size = turtleInventory.getContainerSize();
        int largerSize = playerInventory.getContainerSize();
        playerInventory.selected = turtle.getSelectedSlot();
        for (int i = 0; i < size; i++) {
            turtleInventory.setItem(i, playerInventory.getItem(i));
            playerInventory.setItem(i, ItemStack.EMPTY);
        }

        for (int i = size; i < largerSize; i++) {
            ItemStack remaining = playerInventory.getItem(i);
            if (!remaining.isEmpty()) {
                remaining = ItemHandlerHelper.insertItem(new InvWrapper(turtleInventory), remaining, false);
                if (!remaining.isEmpty()) {
                    BlockPos position = turtle.getPosition();
                    WorldUtil.dropItemStack(remaining, turtle.getLevel(), position, turtle.getDirection().getOpposite());
                }
            }

            playerInventory.setItem(i, ItemStack.EMPTY);
        }
    }

    public static <T> T withPlayer(ITurtleAccess turtle, Function<APFakePlayer, T> function) {
        APFakePlayer player = getPlayer(turtle, turtle.getOwningPlayer());
        load(player, turtle);
        T result = function.apply(player);
        unload(player, turtle);
        return result;
    }

}

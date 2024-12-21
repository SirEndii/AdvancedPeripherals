package de.srendi.advancedperipherals.common.data;

import de.srendi.advancedperipherals.common.setup.Registration;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class BlockLootTables extends BlockLootSubProvider {

    protected BlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        Registration.BLOCKS.getEntries().stream().map(DeferredHolder::get).forEach(registeredBlock -> {
            //Allow blocks to transfer their name to the dropped block when broken
            this.add(registeredBlock, this::createNameableBlockEntityTable);
        });
    }

    @NotNull
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return Registration.BLOCKS.getEntries().stream().map(DeferredHolder::get).map((block) -> (Block) block)::iterator;
    }
}

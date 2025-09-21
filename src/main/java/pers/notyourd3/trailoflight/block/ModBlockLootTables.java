package pers.notyourd3.trailoflight.block;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.HashSet;
import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {
    private final Set<Block> knownBlocks = new HashSet<>();
    protected ModBlockLootTables(Set<Item> explosionResistant, FeatureFlagSet enabledFeatures, HolderLookup.Provider registries) {
        super(explosionResistant, enabledFeatures, registries);
    }

    public ModBlockLootTables(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void add(Block block, LootTable.Builder builder) {
        super.add(block, builder);
        knownBlocks.add(block);
    }
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return knownBlocks;
    }
    @Override
    protected void generate() {
        dropSelf(ModBlocks.MIRROR.get());
        dropSelf(ModBlocks.REFLECTION_CHAMBER.get());
        dropSelf(ModBlocks.CHARGER.get());
        dropSelf(ModBlocks.LASER_GENERATOR.get());
        dropSelf(ModBlocks.MAGNIFIER.get());
    }
}
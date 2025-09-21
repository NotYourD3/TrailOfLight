package pers.notyourd3.trailoflight.block;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import pers.notyourd3.trailoflight.Trailoflight;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class ModBlockTags extends BlockTagsProvider {
    public ModBlockTags(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider, Trailoflight.MODID);
    }
    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                ModBlocks.CHARGER.get(),
                ModBlocks.LASER_GENERATOR.get(),
                ModBlocks.MIRROR.get(),
                ModBlocks.REFLECTION_CHAMBER.get(),
                ModBlocks.MAGNIFIER.get()
        );
    }
}

package pers.notyourd3.trailoflight.block;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import pers.notyourd3.trailoflight.block.custom.*;
import pers.notyourd3.trailoflight.item.ModItems;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

import static pers.notyourd3.trailoflight.Trailoflight.MODID;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

    public static final DeferredBlock<Block> MAGNIFIER = registerBlock("magnifier",
            () -> new MagnifierBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(MODID, "magnifier")))
                    .mapColor(MapColor.METAL)
                    .strength(5.0F)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()));
    public static final DeferredBlock<Block> MIRROR = registerBlock("mirror",
            () -> new MirrorBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(MODID, "mirror")))
                    .mapColor(MapColor.METAL)
                    .strength(5.0F)
                    .noOcclusion()));
    public static final DeferredBlock<Block> REFLECTION_CHAMBER = registerBlock("reflection_chamber",
            () -> new ReflectionChamberBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(MODID, "reflection_chamber")))
                    .mapColor(MapColor.METAL)
                    .strength(5.0F)
                    .noOcclusion()));
    public static final DeferredBlock<Block> LASER_GENERATOR = registerBlock("laser_generator",
            () -> new LaserGeneratorBlock(Block.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(MODID, "laser_generator")))
                    .mapColor(MapColor.METAL)
                    .strength(5.0F)
                    .noOcclusion()));
    public static final DeferredBlock<Block> CHARGER = registerBlock("charger",
            () -> new ChargerBlock(Block.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(MODID, "charger")))
                    .mapColor(MapColor.METAL)
                    .strength(5.0F)
                    .noOcclusion()));
    
    public static final DeferredBlock<Block> PRISM = registerBlock("prism",
            () -> new PrismBlock(Block.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(MODID, "prism")))
                    .mapColor(MapColor.METAL)
                    .strength(5.0F)
                    .noOcclusion()));
    public static final DeferredBlock<Block> LASER_SENSOR = registerBlock("laser_sensor",
            () -> new LaserSensorBlock(Block.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(MODID, "laser_detector")))
                    .mapColor(MapColor.METAL)
                    .strength(5.0F)
                    .noOcclusion()));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> DeferredItem<BlockItem> registerBlockItem(String name, DeferredBlock<T> block) {
        return ModItems.ITEMS.registerSimpleBlockItem(name, block);
    }


    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends T> supplier) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, supplier);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
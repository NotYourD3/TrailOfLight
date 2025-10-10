package pers.notyourd3.trailoflight.client;

import net.minecraft.client.color.item.Constant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import pers.notyourd3.trailoflight.Trailoflight;
import pers.notyourd3.trailoflight.block.ModBlocks;
import pers.notyourd3.trailoflight.block.custom.LaserSensorBlock;
import pers.notyourd3.trailoflight.block.custom.MirrorBlock;
import pers.notyourd3.trailoflight.item.ModDataComponents;
import pers.notyourd3.trailoflight.item.ModItems;

import java.util.Collections;
import java.util.List;

public class ModModels extends ModelProvider {
    public ModModels(PackOutput output) {
        super(output, Trailoflight.MODID);
    }



    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        registerBlockModels(blockModels);
        registerItemModels(itemModels);
    }
    private void registerBlockModels(BlockModelGenerators blockModels){
        registerSimpleBlockModel(blockModels, ModBlocks.CHARGER.get());
        registerSimpleBlockModel(blockModels, ModBlocks.LASER_EMITTER.get());
        registerSimpleBlockModel(blockModels, ModBlocks.MAGNIFIER.get());
        registerSimpleBlockModel(blockModels, ModBlocks.PRISM.get());
        registerSimpleBlockModel(blockModels, ModBlocks.REFLECTION_CHAMBER.get());
        registerSimpleBlockModel(blockModels, ModBlocks.LASER_ASSEMBLY_TABLE.get());

        Block mirrorBlock = ModBlocks.MIRROR.get();
        ResourceLocation mirrorModelId = ModelLocationUtils.getModelLocation(mirrorBlock);
        MultiVariant baseVariant = BlockModelGenerators.plainVariant(mirrorModelId);
        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(mirrorBlock)
                        .with(PropertyDispatch.initial(MirrorBlock.FACING)
                                .select(Direction.NORTH, baseVariant)
                                .select(Direction.EAST, baseVariant.with(BlockModelGenerators.Y_ROT_90))
                                .select(Direction.SOUTH, baseVariant.with(BlockModelGenerators.Y_ROT_180))
                                .select(Direction.WEST, baseVariant.with(BlockModelGenerators.Y_ROT_270))
                        )
        );

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.LASER_SENSOR.get())
                .with(PropertyDispatch.initial(LaserSensorBlock.POWERED)
                        .select(false,BlockModelGenerators.plainVariant(ResourceLocation.fromNamespaceAndPath(Trailoflight.MODID,"block/laser_sensor")))
                        .select(true,BlockModelGenerators.plainVariant(ResourceLocation.fromNamespaceAndPath(Trailoflight.MODID,"block/laser_sensor_on")))));

        registerSixWayOrientableBlock(ModBlocks.LASER_GENERATOR.get(),ResourceLocation.withDefaultNamespace("block/red_concrete")
                ,ResourceLocation.withDefaultNamespace("block/dispenser_front"),blockModels);
        registerSixWayOrientableBlock(ModBlocks.LAVA_GENERATOR.get(),ResourceLocation.withDefaultNamespace("block/orange_concrete")
                ,ResourceLocation.withDefaultNamespace("block/dispenser_front"),blockModels);
    }
    private void registerItemModels(ItemModelGenerators itemModels){
        registerSimpleItemModel(itemModels, ModItems.GLITTERING_INGOT.get());
        registerSimpleItemModel(itemModels, ModItems.LAMBENT_CRYSTAL_DUST.get());
        registerSimpleItemModel(itemModels, ModItems.SCREW_DRIVER.get());

        registerLensItemModel(itemModels, ModItems.LENS_EXTRACT.get());
        registerLensItemModel(itemModels, ModItems.LENS_SMELTING.get());
        registerLensItemModel(itemModels, ModItems.LENS_EXCAVATE.get());

        registerTintedItemModel(itemModels, ModItems.ENERGY_DRINK.get());
        registerTintedItemModel(itemModels, ModItems.LIGHT_SABER.get());
        registerTintedItemModel(itemModels, ModItems.LASER_POINTER.get());

        registerSimpleItemModel(itemModels, ModBlocks.MIRROR.asItem());
        registerSimpleItemModel(itemModels, ModBlocks.PRISM.asItem());
    }

    private void registerSimpleItemModel(ItemModelGenerators itemModels, Item item) {
        itemModels.itemModelOutput.accept(
                item,
                new BlockModelWrapper.Unbaked(
                        ModelLocationUtils.getModelLocation(item),
                        Collections.emptyList()

                ));
    }
    private void registerLensItemModel(ItemModelGenerators itemModels, Item item) {
        itemModels.itemModelOutput.accept(
                item,
                new BlockModelWrapper.Unbaked(
                        ResourceLocation.fromNamespaceAndPath(Trailoflight.MODID,"item/lens"),
                        Collections.emptyList()

                ));
    }

    private void registerTintedItemModel(ItemModelGenerators itemModels, Item item) {
        itemModels.itemModelOutput.accept(
                item,
                new BlockModelWrapper.Unbaked(
                        // Points to 'assets/examplemod/models/item/example_item.json'
                        ModelLocationUtils.getModelLocation(item),
                        // A list of tints to apply
                        List.of(
                                // For when tintindex: 0
                                new Constant(
                                        16777215
                                ),
                                // For when tintindex: 1
                                new ModDataComponents.BeamColorSource()
                        )
                )
        );
    }
    private void registerSimpleBlockModel(BlockModelGenerators blockModels, Block block) {
        blockModels.blockStateOutput.accept(
                BlockModelGenerators.createSimpleBlock(
                        block,
                        BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(block))
                )
        );
    }
    public void registerSixWayOrientableBlock(Block block, ResourceLocation sideTexture, ResourceLocation frontTexture,BlockModelGenerators blockModels) {
        TextureMapping horizontalMapping = new TextureMapping()
                .put(TextureSlot.FRONT,frontTexture)
                .put(TextureSlot.SIDE, sideTexture)
                .put(TextureSlot.TOP, sideTexture)
                .put(TextureSlot.BOTTOM, sideTexture);

        ResourceLocation horizontalModelId = ModelTemplates.CUBE_ORIENTABLE
                .create(block, horizontalMapping, blockModels.modelOutput);
        ResourceLocation verticalModelId = ModelTemplates.CUBE_ORIENTABLE_VERTICAL
                .create(block, horizontalMapping, blockModels.modelOutput);
        MultiVariant horizontalVariant = BlockModelGenerators.plainVariant(horizontalModelId);
        MultiVariant verticalVariant = BlockModelGenerators.plainVariant(verticalModelId);

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block)
                        .with(PropertyDispatch.initial(BlockStateProperties.FACING)
                                .select(Direction.UP, verticalVariant)
                                .select(Direction.DOWN, verticalVariant.with(BlockModelGenerators.X_ROT_180))

                                .select(Direction.NORTH, horizontalVariant)
                                .select(Direction.SOUTH, horizontalVariant.with(BlockModelGenerators.Y_ROT_180))
                                .select(Direction.EAST, horizontalVariant.with(BlockModelGenerators.Y_ROT_90))
                                .select(Direction.WEST, horizontalVariant.with(BlockModelGenerators.Y_ROT_270))
                        )
        );

    }
}

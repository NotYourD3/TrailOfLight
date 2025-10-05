package pers.notyourd3.trailoflight.block.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import pers.notyourd3.trailoflight.block.ModBlocks;
import pers.notyourd3.trailoflight.block.entity.custom.*;

import java.util.Arrays;
import java.util.Set;

import static pers.notyourd3.trailoflight.Trailoflight.MODID;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);

    @SafeVarargs
    private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<? extends T>> registerEntity(
            String name, BlockEntityType.BlockEntitySupplier<? extends T> supplier, DeferredHolder<Block, ?>... blocks
    ) {
        return BLOCK_ENTITIES.register(name, () ->
                new BlockEntityType<>(supplier,
                        Set.copyOf(Arrays.stream(blocks).map(DeferredHolder::get).toList())
                )
        );
    }
    
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<? extends MagnifierEntity>> MAGNIFIER =
            registerEntity("magnifier", MagnifierEntity::new, ModBlocks.MAGNIFIER);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<? extends MirrorEntity>> MIRROR =
            registerEntity("mirror", MirrorEntity::new, ModBlocks.MIRROR);
            
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<? extends ReflectionChamberEntity>> REFLECTION_CHAMBER =
            registerEntity("reflection_chamber", ReflectionChamberEntity::new, ModBlocks.REFLECTION_CHAMBER);
            
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<? extends LaserGeneratorEntity>> LASER_GENERATOR =
            registerEntity("laser_generator", LaserGeneratorEntity::new, ModBlocks.LASER_GENERATOR,ModBlocks.LAVA_GENERATOR);
            
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<? extends ChargerEntity>> CHARGER =
            registerEntity("charger", ChargerEntity::new, ModBlocks.CHARGER);
            
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<? extends PrismBlockEntity>> PRISM =
            registerEntity("prism", PrismBlockEntity::new, ModBlocks.PRISM);
            
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<? extends LaserEmitterEntity>> EMITTER =
            registerEntity("emitter", LaserEmitterEntity::new, ModBlocks.LASER_EMITTER);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<? extends LaserAssemblyTableEntity>> LASER_ASSEMBLY_TABLE =
            registerEntity("laser_assembly_table", LaserAssemblyTableEntity::new, ModBlocks.LASER_ASSEMBLY_TABLE);

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
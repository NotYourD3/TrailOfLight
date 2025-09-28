package pers.notyourd3.trailoflight;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;
import pers.notyourd3.trailoflight.block.ModBlockLootTables;
import pers.notyourd3.trailoflight.block.ModBlockTags;
import pers.notyourd3.trailoflight.block.ModBlocks;
import pers.notyourd3.trailoflight.block.entity.ModBlockEntities;
import pers.notyourd3.trailoflight.block.entity.custom.PrismBlockEntity;
import pers.notyourd3.trailoflight.block.entity.render.ChargerEntityRenderer;
import pers.notyourd3.trailoflight.block.entity.render.LaserAssemblyTableRenderer;
import pers.notyourd3.trailoflight.block.entity.render.MirrorEntityRenderer;
import pers.notyourd3.trailoflight.block.entity.render.PrismBlockEntityRenderer;
import pers.notyourd3.trailoflight.client.render.LaserRenderer;
import pers.notyourd3.trailoflight.feature.BeamManager;
import pers.notyourd3.trailoflight.item.ModCreativeTabs;
import pers.notyourd3.trailoflight.item.ModDataComponents;
import pers.notyourd3.trailoflight.item.ModItems;
import pers.notyourd3.trailoflight.network.PacketLaserFX;
import pers.notyourd3.trailoflight.recipe.ModRecipeSerializers;
import pers.notyourd3.trailoflight.recipe.ModRecipeTypes;
import pers.notyourd3.trailoflight.recipe.ModRecipes;

import java.util.List;
import java.util.Set;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
// The constructor for the mod class is the first code that is run when your mod is loaded.
// FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
@Mod(Trailoflight.MODID)
public class Trailoflight {
    public static final String MODID = "trailoflight";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Trailoflight(IEventBus modEventBus, ModContainer modContainer) {
        IEventBus forgeEventBus = NeoForge.EVENT_BUS;
        modEventBus.addListener(this::setupPackets);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::ClientSetup);
        modEventBus.addListener(this::registerEntityRenderers);
        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(this::registerColorHandlers);
        ModDataComponents.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModRecipeTypes.register(modEventBus);
        ModRecipeSerializers.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        forgeEventBus.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
        NeoForge.EVENT_BUS.register(BeamManager.INSTANCE);
    }

    private void ClientSetup(FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.register(LaserRenderer.INSTANCE);
    }


    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    public void setupPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MODID).versioned("1");
        registrar.playToClient(PacketLaserFX.TYPE, PacketLaserFX.STREAM_CODEC, (packet, context) -> packet.handle(packet, context));
    }

    public void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.MIRROR.get(), MirrorEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.CHARGER.get(), ChargerEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.PRISM.get(), PrismBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.LASER_ASSEMBLY_TABLE.get(), LaserAssemblyTableRenderer::new);
    }

    public void gatherData(GatherDataEvent.Client event) {
        event.createProvider(ModBlockTags::new);
        event.createProvider(ModRecipes.Runner::new);
        event.createProvider((output, provider) ->
                new LootTableProvider(output, Set.of(), List.of(new LootTableProvider.SubProviderEntry(ModBlockLootTables::new, LootContextParamSets.BLOCK)), provider));
    }

    public void registerColorHandlers(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(
                ResourceLocation.fromNamespaceAndPath(MODID, "beam_color"),
                ModDataComponents.BeamColorSource.MAP_CODEC
        );

    }


}
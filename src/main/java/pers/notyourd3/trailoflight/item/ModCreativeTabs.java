package pers.notyourd3.trailoflight.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import pers.notyourd3.trailoflight.Trailoflight;
import pers.notyourd3.trailoflight.block.ModBlocks;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Trailoflight.MODID);

    public static final Supplier<CreativeModeTab> TRAIL_OF_LIGHT_TAB = CREATIVE_MODE_TABS.register("trail_of_light_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.trailoflight"))
                    .icon(() -> ModItems.LIGHT_SABER.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.acceptAll(Stream.of(
                                ModItems.LIGHT_SABER,
                                ModItems.LAMBENT_CRYSTAL_DUST,
                                ModItems.GLITTERING_INGOT,
                                ModItems.SCREW_DRIVER,
                                ModItems.LASER_POINTER
                        ).map(sup -> sup.get().getDefaultInstance()).toList());
                        output.acceptAll(Stream.of(
                                ModBlocks.CHARGER,
                                ModBlocks.LASER_GENERATOR,
                                ModBlocks.REFLECTION_CHAMBER,
                                ModBlocks.MIRROR,
                                ModBlocks.MAGNIFIER
                        ).map(sup -> sup.get().asItem().getDefaultInstance()).toList());
                    })
                    .build()
    );
    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }

}

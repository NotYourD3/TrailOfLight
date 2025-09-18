package pers.notyourd3.trailoflight.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import pers.notyourd3.trailoflight.item.custom.ScrewDriverItem;

import static pers.notyourd3.trailoflight.Trailoflight.MODID;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static final DeferredItem<Item> LAMBENT_CRYSTAL_DUST = ITEMS.registerSimpleItem("lambent_powder");
    public static final DeferredItem<Item> GLITTERING_INGOT = ITEMS.registerSimpleItem("glittering_ingot");
    public static final DeferredItem<Item> SCREW_DRIVER = ITEMS.registerItem("screw_driver", 
            properties -> new ScrewDriverItem(properties.stacksTo(1).durability(250)));

    public static void register(IEventBus eventbus){
        ITEMS.register(eventbus);
    }
}


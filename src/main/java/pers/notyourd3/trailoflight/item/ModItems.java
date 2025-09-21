package pers.notyourd3.trailoflight.item;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import pers.notyourd3.trailoflight.item.custom.LaserPointerItem;
import pers.notyourd3.trailoflight.item.custom.LightSaberItem;
import pers.notyourd3.trailoflight.item.custom.ScrewDriverItem;

import static pers.notyourd3.trailoflight.Trailoflight.MODID;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static final DeferredItem<Item> LAMBENT_CRYSTAL_DUST = ITEMS.registerSimpleItem("lambent_powder");
    public static final DeferredItem<Item> GLITTERING_INGOT = ITEMS.registerSimpleItem("glittering_ingot");
    public static final DeferredItem<Item> SCREW_DRIVER = ITEMS.registerItem("screw_driver",
            properties -> new ScrewDriverItem(properties.stacksTo(1).durability(250)));
    public static final DeferredItem<Item> LASER_POINTER = ITEMS.registerItem("laser_pointer",
            properties -> new LaserPointerItem(properties.stacksTo(1)));
    public static final DeferredItem<Item> LIGHT_SABER = ITEMS.registerItem("light_saber",
            properties -> new LightSaberItem(properties
                    .stacksTo(1)
                    .attributes(ItemAttributeModifiers.builder()
                            .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 4, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                            .add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -2.4, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                            .build())));

    public static void register(IEventBus eventbus) {
        ITEMS.register(eventbus);
    }
}


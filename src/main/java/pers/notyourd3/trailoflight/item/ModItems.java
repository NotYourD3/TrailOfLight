package pers.notyourd3.trailoflight.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantable;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import pers.notyourd3.trailoflight.item.consume.EnergyDrinkConsumeEffect;
import pers.notyourd3.trailoflight.item.custom.*;

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
    public static final DeferredItem<Item> LENS_EXTRACT = ITEMS.registerItem("lens_extract", properties -> new LensExtractItem(properties.stacksTo(1)));
    public static final DeferredItem<Item> LENS_SMELTING = ITEMS.registerItem("lens_smelting",
            properties -> new LensSmeltingItem(properties.stacksTo(1)));
    public static final DeferredItem<Item> LENS_EXCAVATE = ITEMS.registerItem("lens_excavate",
            properties -> new LensExcavateItem(properties
                    .component(DataComponents.ENCHANTABLE,new Enchantable(10))
                    .stacksTo(1)));
    public static final DeferredItem<Item> ENERGY_DRINK = ITEMS.registerItem("energy_drink",
            properties -> new EnergyDrinkItem(properties.component(
                    DataComponents.CONSUMABLE,
                    Consumable.builder().
                            consumeSeconds(1f)
                            .animation(ItemUseAnimation.DRINK)
                            .sound(SoundEvents.GENERIC_DRINK)
                            .hasConsumeParticles(true)
                            .onConsume(
                                    new EnergyDrinkConsumeEffect(0)
                            )
                            .build()).stacksTo(1)));

    public static void register(IEventBus eventbus) {
        ITEMS.register(eventbus);
    }
}


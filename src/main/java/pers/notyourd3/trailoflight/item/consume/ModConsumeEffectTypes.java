package pers.notyourd3.trailoflight.item.consume;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import pers.notyourd3.trailoflight.Trailoflight;

import java.util.function.Supplier;

public class ModConsumeEffectTypes {
    public static final DeferredRegister<ConsumeEffect.Type<?>> CONSUME_EFFECT_TYPES = DeferredRegister.create(Registries.CONSUME_EFFECT_TYPE, Trailoflight.MODID);

    public static final Supplier<ConsumeEffect.Type<EnergyDrinkConsumeEffect>> ENERGY_DRINK =
            CONSUME_EFFECT_TYPES.register("energy_drink", () -> new ConsumeEffect.Type<>(
                    Codec.INT.fieldOf("dummy").xmap(EnergyDrinkConsumeEffect::new, EnergyDrinkConsumeEffect::dummy),
                    new StreamCodec<>() {
                        @Override
                        public EnergyDrinkConsumeEffect decode(RegistryFriendlyByteBuf buf) {
                            return new EnergyDrinkConsumeEffect(ByteBufCodecs.VAR_INT.decode(buf));
                        }

                        @Override
                        public void encode(RegistryFriendlyByteBuf buf, EnergyDrinkConsumeEffect effect) {
                            ByteBufCodecs.VAR_INT.encode(buf, effect.dummy());
                        }
                    }
            ));

    public static void register(IEventBus eventBus) {
        CONSUME_EFFECT_TYPES.register(eventBus);
    }


}
package pers.notyourd3.trailoflight.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;
import pers.notyourd3.trailoflight.Trailoflight;

import java.awt.*;
import java.util.function.Supplier;

import pers.notyourd3.trailoflight.feature.BeamCodec;

import static pers.notyourd3.trailoflight.feature.BeamCodec.COLOR_CODEC;
import static pers.notyourd3.trailoflight.feature.BeamCodec.COLOR_STREAM_CODEC;

public class ModDataComponents {
    public record BeamStorageInfoRecord(Color color,int alpha,int maxAlpha){}
    public record isActive(Boolean b){}
    public record BeamColorSource() implements ItemTintSource{
        public static final MapCodec<BeamColorSource> MAP_CODEC = MapCodec.unit(new BeamColorSource());
        @Override
        public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
            return ARGB.opaque(stack.getOrDefault(ModDataComponents.BEAM_STORAGE_INFO.get(), new BeamStorageInfoRecord(Color.WHITE,0,0)).color().getRGB());
        }

        @Override
        public MapCodec<? extends ItemTintSource> type() {
            return MAP_CODEC;
        }
    }

    public static final Codec<BeamStorageInfoRecord>BEAM_STORAGE_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    COLOR_CODEC.fieldOf("color").forGetter(BeamStorageInfoRecord::color),
                    Codec.INT.fieldOf("alpha").forGetter(BeamStorageInfoRecord::alpha),
                    Codec.INT.fieldOf("maxAlpha").forGetter(BeamStorageInfoRecord::maxAlpha)
            ).apply(instance, BeamStorageInfoRecord::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, BeamStorageInfoRecord> BEAM_STORAGE_STREAM_CODEC = StreamCodec.composite(
            COLOR_STREAM_CODEC, BeamStorageInfoRecord::color,
            ByteBufCodecs.INT, BeamStorageInfoRecord::alpha,
            ByteBufCodecs.INT, BeamStorageInfoRecord::maxAlpha,
            BeamStorageInfoRecord::new
    );
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Trailoflight.MODID);

    public static final Supplier<DataComponentType<BeamStorageInfoRecord>> BEAM_STORAGE_INFO = DATA_COMPONENTS.registerComponentType(
            "beam_storage_info",
            builder -> builder
                    .persistent(BEAM_STORAGE_CODEC)
                    .networkSynchronized(BEAM_STORAGE_STREAM_CODEC)
    );
    public static final Supplier<DataComponentType<isActive>> IS_ACTIVE = DATA_COMPONENTS.registerComponentType(
            "is_active",
            builder -> builder
                    .persistent(RecordCodecBuilder.create(instance -> instance.group(Codec.BOOL.fieldOf("isactive").forGetter(isActive::b)).apply(instance, isActive::new)))
                    .networkSynchronized(StreamCodec.composite(ByteBufCodecs.BOOL, isActive::b, isActive::new))
    );
    public static void register(IEventBus eventBus) {
        DATA_COMPONENTS.register(eventBus);
    }
}

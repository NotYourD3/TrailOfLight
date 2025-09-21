package pers.notyourd3.trailoflight.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

import java.awt.*;

public class BeamCodec {
    public static final Codec<Color> COLOR_CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.INT.fieldOf("red").forGetter(Color::getRed),
            Codec.INT.fieldOf("green").forGetter(Color::getGreen),
            Codec.INT.fieldOf("blue").forGetter(Color::getBlue),
            Codec.INT.fieldOf("alpha").forGetter(Color::getAlpha)
    ).apply(builder, Color::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Color> COLOR_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, Color::getRed,
            ByteBufCodecs.INT, Color::getGreen,
            ByteBufCodecs.INT, Color::getBlue,
            ByteBufCodecs.INT, Color::getAlpha,
            Color::new
    );

    public static final Codec<Beam> CODEC = COLOR_CODEC.xmap(
            color -> new Beam(Vec3.ZERO, Vec3.ZERO, null, color),
            beam -> beam.color
    );
}
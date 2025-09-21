package pers.notyourd3.trailoflight.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import pers.notyourd3.trailoflight.Trailoflight;
import pers.notyourd3.trailoflight.client.render.LaserRenderer;

import java.awt.*;

public class PacketLaserFX implements CustomPacketPayload, IPayloadHandler<PacketLaserFX> {
    public static final CustomPacketPayload.Type<PacketLaserFX> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Trailoflight.MODID, "laser_fx"));
    public static final StreamCodec<FriendlyByteBuf, PacketLaserFX> STREAM_CODEC = CustomPacketPayload.codec(PacketLaserFX::write, PacketLaserFX::new);
    private double x1, y1, z1, x2, y2, z2;
    private int r, g, b, a;

    public PacketLaserFX(Vec3 start, Vec3 end, Color color) {
        this.x1 = start.x;
        this.y1 = start.y;
        this.z1 = start.z;
        this.x2 = end.x;
        this.y2 = end.y;
        this.z2 = end.z;
        this.r = color.getRed();
        this.g = color.getGreen();
        this.b = color.getBlue();
        this.a = color.getAlpha();
    }

    public PacketLaserFX(FriendlyByteBuf buf) {
        this(new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()),
                new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()),
                new Color(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt()));
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeDouble(x1);
        buf.writeDouble(y1);
        buf.writeDouble(z1);
        buf.writeDouble(x2);
        buf.writeDouble(y2);
        buf.writeDouble(z2);
        buf.writeInt(r);
        buf.writeInt(g);
        buf.writeInt(b);
        buf.writeInt(a);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(PacketLaserFX message, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            LaserRenderer.INSTANCE.addLaser(new Vec3(message.x1, message.y1, message.z1),
                    new Vec3(message.x2, message.y2, message.z2),
                    new Color(message.r, message.g, message.b, message.a));
        }
    }
}

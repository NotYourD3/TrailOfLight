package pers.notyourd3.trailoflight.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import pers.notyourd3.trailoflight.feature.Beam;
import pers.notyourd3.trailoflight.feature.BeamManager;
import pers.notyourd3.trailoflight.feature.Matrix4;
import pers.notyourd3.trailoflight.block.entity.ModBlockEntities;

import java.util.stream.Stream;

public class MirrorEntity extends BlockEntity {
    public float rotX;
    public float rotY;
    public float rotXPowered;
    public float rotYPowered;
    public boolean isPowered;
    public MirrorEntity( BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.MIRROR.get(), pos, blockState);
    }

    public void onBeam(Beam beam){
        if (beam.life >= Beam.MAX_BOUNCES) return;
        
        float x, y;
        if (isPowered) {
            x = rotXPowered;
            y = rotYPowered;
        } else {
            x = rotX;
            y = rotY;
        }

        Matrix4 matrix = new Matrix4();
        matrix.rotate(Math.toRadians(y), new Vec3(0, 1, 0));
        matrix.rotate(Math.toRadians(x), new Vec3(1, 0, 0));
        Vec3 normal = matrix.apply(new Vec3(0, 0, -1));
        Vec3 incomingDir = beam.endLoc.subtract(beam.initLoc).normalize();
        if (incomingDir.dot(normal) > 0)
            return;

        Vec3 outgoingDir = incomingDir.subtract(normal.scale(incomingDir.dot(normal) * 2));
        BeamManager.INSTANCE.addBeam(beam.createSimilarBeam(outgoingDir).setAlpha((int) (beam.color.getAlpha()/1.05F)));

    }
    @Override
    public void loadAdditional(ValueInput input){
        super.loadAdditional(input);
        rotX = input.getFloatOr("rotX", 0);
        rotY = input.getFloatOr("rotY", 0);
        rotXPowered = input.getFloatOr("rotXPowered", 0);
        rotYPowered = input.getFloatOr("rotYPowered", 0);
        isPowered = input.getBooleanOr("isPowered", false);
    }
    @Override
    public void saveAdditional(ValueOutput output){
        super.saveAdditional( output);
        output.putFloat("rotX", rotX);
        output.putFloat("rotY", rotY);
        output.putFloat("rotXPowered", rotXPowered);
        output.putFloat("rotYPowered", rotYPowered);
        output.putBoolean("isPowered", isPowered);
    }
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries){
        return this.saveWithoutMetadata(registries);
    }
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public void onDataPacket(Connection connection,ValueInput input){
        super.onDataPacket(connection, input);
        loadAdditional(input);
    }
    public float getRotX(){
        return isPowered ? rotXPowered : rotX;
    }
    public float getRotY(){
        return isPowered ? rotYPowered : rotY;
    }

    public void setRotX(float rotX){
        if(isPowered){
            rotXPowered = rotX;
        }else {
            this.rotX = rotX;
        }
        this.setChanged();
        this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
    }
    public void setRotY(float rotY){
        if(isPowered){
            rotYPowered = rotY;
        }else {
            this.rotY = rotY;
        }
        this.setChanged();
        this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
    }
}
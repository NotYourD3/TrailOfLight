package pers.notyourd3.trailoflight.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.items.ItemStackHandler;
import pers.notyourd3.trailoflight.block.ModBlocks;
import pers.notyourd3.trailoflight.block.entity.ModBlockEntities;
import pers.notyourd3.trailoflight.feature.Beam;
import pers.notyourd3.trailoflight.feature.BeamManager;
import pers.notyourd3.trailoflight.item.custom.AbstractLensItem;

import java.awt.*;

public class LaserEmitterEntity extends BlockEntity {
    private final ItemStackHandler itemHandler = new ItemStackHandler(1);
    private float rotX = 0;
    private float rotY = 0;

    public LaserEmitterEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.EMITTER.get(), pos, blockState);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        itemHandler.deserialize(input);
        rotX = input.getFloatOr("rotX", 0);
        rotY = input.getFloatOr("rotY", 0);
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        itemHandler.serialize(output);
        output.putFloat("rotX", rotX);
        output.putFloat("rotY", rotY);
    }

    public ItemStackHandler getItemHandler() {
        return this.itemHandler;
    }

    public ItemStack getLens() {
        return this.itemHandler.getStackInSlot(0);
    }

    public void setLens(ItemStack stack) {
        this.itemHandler.setStackInSlot(0, stack);
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public void emitLaser(Color color) {
        if (level != null && !level.isClientSide) {
            // 获取方块的方向
            var direction = getBlockState().getValue(net.minecraft.world.level.block.DirectionalBlock.FACING);
            
            // 创建激光束
            Beam beam = new Beam(
                getBlockPos().getCenter(),
                direction.getUnitVec3(),
                level,
                color
            );
            
            // 如果有透镜，应用透镜效果
            ItemStack lens = getLens();
            if (!lens.isEmpty() && lens.getItem() instanceof AbstractLensItem lensItem) {
                lensItem.onSpawn(beam);
            } else {
                // 默认发射激光
                BeamManager.INSTANCE.addBeam(beam);
            }
        }
    }

    public float getRotX() {
        return rotX;
    }

    public float getRotY() {
        return rotY;
    }

    public void setRotX(float rotX) {
        this.rotX = rotX;
        setChanged();
    }

    public void setRotY(float rotY) {
        this.rotY = rotY;
        setChanged();
    }
}
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
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import pers.notyourd3.trailoflight.block.entity.ModBlockEntities;
import pers.notyourd3.trailoflight.feature.Beam;
import pers.notyourd3.trailoflight.feature.BeamManager;
import pers.notyourd3.trailoflight.item.custom.AbstractLensItem;

import java.awt.*;

public class LaserEmitterEntity extends BlockEntity {
    private final ItemStacksResourceHandler itemHandler = new ItemStacksResourceHandler(1);

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
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        itemHandler.serialize(output);
    }


    public ItemStack getLens() {
        return this.itemHandler.getResource(0).toStack();
    }

    public void setLens(ItemStack stack) {
        this.itemHandler.set(0, ItemResource.of(stack), stack.isEmpty() ? 0 : 1);
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public void emitLaser(Color color) {
        if (level != null && !level.isClientSide()) {
            var direction = getBlockState().getValue(net.minecraft.world.level.block.DirectionalBlock.FACING);

            Beam beam = new Beam(
                getBlockPos().getCenter(),
                direction.getUnitVec3(),
                level,
                color
            );

            ItemStack lens = getLens();
            if (!lens.isEmpty() && lens.getItem() instanceof AbstractLensItem lensItem) {
                lensItem.onSpawn(beam);
            } else {
                BeamManager.INSTANCE.addBeam(beam);
            }
        }
    }
}
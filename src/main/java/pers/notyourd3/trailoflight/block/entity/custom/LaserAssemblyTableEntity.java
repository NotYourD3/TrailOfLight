package pers.notyourd3.trailoflight.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.items.ItemStackHandler;
import pers.notyourd3.trailoflight.block.entity.ModBlockEntities;
import pers.notyourd3.trailoflight.feature.Beam;
import pers.notyourd3.trailoflight.recipe.custom.BeamRecipeInput;
import pers.notyourd3.trailoflight.recipe.ModRecipeTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LaserAssemblyTableEntity extends BlockEntity  {
    private final ItemStackHandler itemHandler = new ItemStackHandler(9); // 最多容纳9个物品
    private int alphaCache = 0;

    public LaserAssemblyTableEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.LASER_ASSEMBLY_TABLE.get(), pos, blockState);
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
        alphaCache = input.getIntOr("AlphaCache", 0);
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        itemHandler.serialize(output);
        output.putInt("AlphaCache", alphaCache);
    }

    public ItemStack addItem(ItemStack stack) {
        this.alphaCache =0;
        // 尝试将物品添加到第一个空槽位
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack slotStack = itemHandler.getStackInSlot(i);
            if (slotStack.isEmpty()) {
                itemHandler.setStackInSlot(i, stack.copyWithCount(1));
                stack.shrink(1);
                setChanged();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                return stack;
            }
        }
        // 如果没有空槽位，返回原始堆栈
        return stack;
    }

    public ItemStack extractItem() {
        this.alphaCache =0;
        // 从最后一个槽位开始提取物品（类似栈的逻辑）
        for (int i = itemHandler.getSlots() - 1; i >= 0; i--) {
            ItemStack slotStack = itemHandler.getStackInSlot(i);
            if (!slotStack.isEmpty()) {
                ItemStack extracted = slotStack.copy();
                itemHandler.setStackInSlot(i, ItemStack.EMPTY);
                setChanged();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                return extracted;
            }
        }
        return ItemStack.EMPTY;
    }

    public void onBeam(Beam beam) {
        // 收集所有非空物品用于配方
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                items.add(stack);
            }
        }

        if (!items.isEmpty()) {
            BeamRecipeInput input = new BeamRecipeInput(beam, items, alphaCache);
            Optional<net.minecraft.world.item.crafting.RecipeHolder<pers.notyourd3.trailoflight.recipe.custom.BeamRecipe>> optional = level.getServer().getRecipeManager().getRecipeFor(
                    ModRecipeTypes.BEAM_TYPE.get(), input, level
            );

            ItemStack result = optional.map(net.minecraft.world.item.crafting.RecipeHolder::value)
                    .map(e -> e.assemble(input, level.registryAccess()))
                    .orElse(ItemStack.EMPTY);

            if (!result.isEmpty()) {

                ItemEntity entity = new ItemEntity(level,
                        getBlockPos().getCenter().x, getBlockPos().getCenter().y + 1, getBlockPos().getCenter().z, result);
                level.addFreshEntity(entity);


                for (int i = 0; i < itemHandler.getSlots(); i++) {
                    itemHandler.setStackInSlot(i, ItemStack.EMPTY);
                }
                this.alphaCache =0;
                setChanged();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }

        alphaCache += beam.color.getAlpha();
        setChanged();
    }


    public ItemStack getItem(int slot) {
        return itemHandler.getStackInSlot(slot);
    }

    public int getContainerSize() {
        return itemHandler.getSlots();
    }
}
package pers.notyourd3.trailoflight.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import pers.notyourd3.trailoflight.block.entity.ModBlockEntities;
import pers.notyourd3.trailoflight.feature.Beam;
import pers.notyourd3.trailoflight.recipe.ModRecipeTypes;
import pers.notyourd3.trailoflight.recipe.custom.BeamRecipeInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
public class LaserAssemblyTableEntity extends BlockEntity  {

    private final ItemStacksResourceHandler itemHandler = new ItemStacksResourceHandler(9);
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
        for (int i = 0; i < itemHandler.size(); i++) {
            ItemStack slotStack = itemHandler.getResource(i).toStack();
            if (slotStack.isEmpty()) {
                itemHandler.set(i, ItemResource.of(stack),1);
                stack.shrink(1);
                setChanged();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                return stack;
            }
        }
        return stack;
    }

    public ItemStack extractItem() {
        this.alphaCache =0;
        for (int i = itemHandler.size() - 1; i >= 0; i--) {
            ItemStack slotStack = itemHandler.getResource(i).toStack();
            if (!slotStack.isEmpty()) {
                ItemStack extracted = slotStack.copy();
                itemHandler.set(i, ItemResource.EMPTY,0);
                setChanged();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                return extracted;
            }
        }
        return ItemStack.EMPTY;
    }

    public void onBeam(Beam beam) {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < itemHandler.size(); i++) {
            ItemStack stack = itemHandler.getResource(i).toStack();
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


                for (int i = 0; i < itemHandler.size(); i++) {
                    itemHandler.set(i, ItemResource.EMPTY,0);
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
        return itemHandler.getResource(slot).toStack();
    }

    public int getContainerSize() {
        return itemHandler.size();
    }
}
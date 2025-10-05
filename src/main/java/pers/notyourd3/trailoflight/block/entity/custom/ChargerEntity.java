package pers.notyourd3.trailoflight.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import pers.notyourd3.trailoflight.block.entity.ModBlockEntities;
import pers.notyourd3.trailoflight.feature.Beam;
import pers.notyourd3.trailoflight.item.custom.IChargableItem;
import pers.notyourd3.trailoflight.recipe.ModRecipeTypes;
import pers.notyourd3.trailoflight.recipe.custom.BeamRecipe;
import pers.notyourd3.trailoflight.recipe.custom.BeamRecipeInput;

import java.util.Collections;
import java.util.Optional;

public class ChargerEntity extends BlockEntity {
    private final ItemStacksResourceHandler itemHandler = new ItemStacksResourceHandler(1);
    private int alphaCache = 0;

    public ChargerEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CHARGER.get(), pos, blockState);
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
        alphaCache = input.getIntOr("alphaCache", 0);
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        itemHandler.serialize(output);
        output.putInt("alphaCache", alphaCache);
    }


    public ItemStack getStack() {
        return this.itemHandler.getResource(0).toStack();
    }

    public void setStack(ItemStack stack) {
        this.itemHandler.set(0, ItemResource.of(stack),stack.isEmpty() ? 0 : 1);
        alphaCache = 0;
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public void onBeam(Beam beam) {
        ItemStack stack = getStack();
        if (stack.getItem() instanceof IChargableItem) {
            IChargableItem.addBeam(stack, beam);
            this.setStack(stack);
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        } else {
            BeamRecipeInput input = new BeamRecipeInput(beam,
                    Collections.singletonList(getStack()), alphaCache);
            Optional<RecipeHolder<BeamRecipe>> optional = level.getServer().getRecipeManager().getRecipeFor(
                    ModRecipeTypes.BEAM_TYPE.get(), input, level
            );
            ItemStack result = optional.map(RecipeHolder::value)
                    .map(e -> e.assemble(input, level.registryAccess()))
                    .orElse(ItemStack.EMPTY);
            if (!result.isEmpty()) {
                ItemEntity entity = new ItemEntity(level,
                        getBlockPos().getCenter().x, getBlockPos().getCenter().y + 1, getBlockPos().getCenter().z, result);
                level.addFreshEntity(entity);
                setStack(getStack().consumeAndReturn(getStack().getCount() - 1, null));
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
        alphaCache += beam.color.getAlpha();
        setChanged();
    }
}

package com.beyondcosmos.culinaria.block;

import com.beyondcosmos.culinaria.block.entiy.CookingStationBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CookingStationBlock extends BlockWithEntity {

    public enum Mode implements StringIdentifiable {
        POT("pot"), PAN("pan");
        private final String name;
        Mode(String n) { this.name = n; }
        @Override public String asString() { return name; }
        @Override public String toString() { return name; }
    }

    public static final EnumProperty<Mode> MODE = EnumProperty.of("mode", Mode.class);

    public CookingStationBlock(Settings settings) {
        super(settings);
        this.setDefaultState(getStateManager().getDefaultState().with(MODE, Mode.POT));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    protected void appendProperties(StateManager.Builder<net.minecraft.block.Block, BlockState> builder) {
        builder.add(MODE);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof CookingStationBlockEntity station) {
            ItemScatterer.spawn(world, pos, station);
            world.updateListeners(pos, state, state, net.minecraft.block.Block.NOTIFY_ALL);
        }
        super.onStateReplaced(state, world, pos, moved);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        // Reservado para la acción “Cook” vía tecla F (C2S). Por ahora, no hace nada.
        return ActionResult.PASS;
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
                                         PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof CookingStationBlockEntity station)) return ActionResult.PASS;
        if (!player.getAbilities().allowModifyWorld) return ActionResult.FAIL;

        if (stack.isEmpty()) {
            boolean sneaking = player.isSneaking();

            if (sneaking) {
                ItemStack extracted = station.extractLast();
                if (!extracted.isEmpty()) {
                    if (!player.getInventory().insertStack(extracted)) player.dropItem(extracted, false);
                    station.markDirty();
                    world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
                    return ActionResult.SUCCESS;
                }
                return ActionResult.PASS;
            }

            // Toggle POT ↔ PAN
            CookingStationBlock.Mode current = state.get(CookingStationBlock.MODE);
            CookingStationBlock.Mode next = (current == CookingStationBlock.Mode.POT)
                    ? CookingStationBlock.Mode.PAN : CookingStationBlock.Mode.POT;
            world.setBlockState(pos, state.with(CookingStationBlock.MODE, next), Block.NOTIFY_ALL);
            player.sendMessage(Text.translatable("message.culinaria.mode_switched", next.asString()), true);
            return ActionResult.SUCCESS;
        }

        ItemStack remainder = station.insertItem(stack);
        if (remainder.getCount() != stack.getCount() || !ItemStack.areItemsAndComponentsEqual(remainder, stack)) {
            player.setStackInHand(hand, remainder);
            station.markDirty();
            world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CookingStationBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return null;
    }
}

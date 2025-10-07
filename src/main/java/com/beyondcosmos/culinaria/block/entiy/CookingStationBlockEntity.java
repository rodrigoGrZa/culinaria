package com.beyondcosmos.culinaria.block.entiy;

import com.beyondcosmos.culinaria.registry.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.PlayerEntity;

public class CookingStationBlockEntity extends BlockEntity implements Inventory {

    private static final int SLOT_COUNT = 5;
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(SLOT_COUNT, ItemStack.EMPTY);

    public CookingStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COOKING_STATION_BE, pos, state);
    }

    @Override
    protected void writeData(WriteView view) {
        Inventories.writeData(view, items, true);
    }

    @Override
    protected void readData(ReadView view) {
        for (int i = 0; i < items.size(); i++) items.set(i, ItemStack.EMPTY);
        Inventories.readData(view, items);
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack s : items) if (!s.isEmpty()) return false;
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack split = Inventories.splitStack(items, slot, amount);
        if (!split.isEmpty()) markDirty();
        return split;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack removed = Inventories.removeStack(items, slot);
        if (!removed.isEmpty()) markDirty();
        return removed;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > stack.getMaxCount()) stack.capCount(stack.getMaxCount());
        markDirty();
    }

    @Override
    public void clear() {
        for (int i = 0; i < items.size(); i++) items.set(i, ItemStack.EMPTY);
        markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world == null) return false;
        if (this.world.getBlockEntity(this.pos) != this) return false;
        return player.squaredDistanceTo(
                this.pos.getX() + 0.5,
                this.pos.getY() + 0.5,
                this.pos.getZ() + 0.5
        ) <= 64.0;
    }

    public ItemStack insertItem(ItemStack stack) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        ItemStack working = stack.copy();

        for (int i = 0; i < items.size(); i++) {
            ItemStack slot = items.get(i);
            if (slot.isEmpty()) continue;
            if (canStack(slot, working)) {
                int canMove = Math.min(working.getCount(), slot.getMaxCount() - slot.getCount());
                if (canMove > 0) {
                    slot.increment(canMove);
                    working.decrement(canMove);
                    if (working.isEmpty()) {
                        markDirty();
                        return ItemStack.EMPTY;
                    }
                }
            }
        }

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isEmpty()) {
                int move = Math.min(working.getCount(), working.getMaxCount());
                ItemStack placed = working.copy();
                placed.setCount(move);
                items.set(i, placed);
                working.decrement(move);
                if (working.isEmpty()) {
                    markDirty();
                    return ItemStack.EMPTY;
                }
            }
        }

        markDirty();
        return working;
    }

    public ItemStack extractLast() {
        for (int i = items.size() - 1; i >= 0; i--) {
            ItemStack slot = items.get(i);
            if (!slot.isEmpty()) {
                items.set(i, ItemStack.EMPTY);
                markDirty();
                return slot;
            }
        }
        return ItemStack.EMPTY;
    }

    private static boolean canStack(ItemStack a, ItemStack b) {
        if (a.isEmpty() || b.isEmpty()) return false;
        if (!a.isStackable() || !b.isStackable()) return false;
        if (!ItemStack.areItemsAndComponentsEqual(a, b)) return false;
        return a.getCount() < a.getMaxCount();
    }
}

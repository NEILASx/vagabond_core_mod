package com.vagabond.CrudeTable;

import com.vagabond.VagabondCore;
import com.vagabond.VagabondTypes;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.vagabond.CrudeTable.CrudeTableRecipeConverter.DYNAMIC_CRAFTING_LIST;

public class CrudeTableMenu extends AbstractContainerMenu {

    public static final int RESULT_SLOT = 0;
    private static final int MAT_SLOT_START = 1;
    private static final int MAT_SLOT_END = 6;     // 5 input slots total
    private static final int INV_SLOT_START = 6;
    private static final int INV_SLOT_END = 33;
    private static final int USE_ROW_SLOT_START = 33;
    private static final int USE_ROW_SLOT_END = 42;

    private final TransientCraftingContainer inputSlots = new TransientCraftingContainer(this, 3, 3);
    private final ResultContainer resultSlots = new ResultContainer();
    private final Player player;

    public CrudeTableMenu(int containerId, Inventory playerInventory) {
        super(VagabondTypes.CRAFTING_MAT_MENU.get(), containerId);
        this.player = playerInventory.player;

        this.addSlot(new CrudeTableResultSlot(this, this.resultSlots, RESULT_SLOT, 124, 35));

        int startX = 30;
        int startY = 17;

        for (int i = 0; i < 9; i++) {
            int col = i % 3;
            int row = i / 3;

            if (col % 2 == 0 && row % 2 == 0) {
                continue;
            }

            int pixelX = startX + (col * 18);
            int pixelY = startY + (row * 18);

            this.addSlot(new Slot(this.inputSlots, i, pixelX, pixelY));
        }

        for (int r = 0; r < 3; ++r) {
            for (int c = 0; c < 9; ++c) {
                int slotIdx = c + (r * 9) + 9;
                int posX = 8 + (c * 18);
                int posY = 84 + (r * 18);
                this.addSlot(new Slot(playerInventory, slotIdx, posX, posY));
            }
        }

        for (int h = 0; h < 9; ++h) {
            int posX = 8 + (h * 18);
            int posY = 142;
            this.addSlot(new Slot(playerInventory, h, posX, posY));
        }
    }

    private @Nullable CrudeTableRecipe getCurrentRecipe(CrudeTableInput input) {
        for (RecipeHolder<CrudeTableRecipe> holder : DYNAMIC_CRAFTING_LIST) {
            CrudeTableRecipe recipe = holder.value();

            if (recipe  .strictMatches(input, this.player.level())) {
                return recipe;
            }
            if (recipe instanceof CrudeTableShapelessRecipe) {
                if (recipe.matches(input, this.player.level())) {
                    return recipe;
                }
            }
        }
        return null;
    }

    @Override
    public void slotsChanged(@NotNull Container container) {
        super.slotsChanged(container);
        if (this.player.level().isClientSide) return;

        CrudeTableInput customInput = CrudeTableInput.fromContainer(this.inputSlots);

        ItemStack resultStack = ItemStack.EMPTY;

        CrudeTableRecipe recipe = getCurrentRecipe(customInput);

        if (recipe != null) {
            resultStack = recipe.assemble(customInput, this.player.level().registryAccess());
        }
        VagabondCore.LOGGER.info("detected recipe result: {}", resultStack);

        VagabondCore.LOGGER.info("updating slot");
        this.resultSlots.setItem(RESULT_SLOT, resultStack);

        this.resultSlots.setChanged();
        this.broadcastChanges();
//        if (!ItemStack.matches(this.resultSlots.getItem(RESULT_SLOT), resultStack)) {
//        }

    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        this.clearContainer(player, this.inputSlots);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemStack = slotStack.copy();

            if (index == RESULT_SLOT) {
                if (!this.moveItemStackTo(slotStack, INV_SLOT_START, USE_ROW_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onTake(player, slot.getItem());
            }
            else if (index >= MAT_SLOT_START && index < MAT_SLOT_END) {
                if (!this.moveItemStackTo(slotStack, INV_SLOT_START, USE_ROW_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (index >= INV_SLOT_START && index < USE_ROW_SLOT_END) {
                if (!this.moveItemStackTo(slotStack, MAT_SLOT_START, MAT_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemStack;
    }

    public static class CrudeTableResultSlot extends Slot {
        private final CrudeTableMenu menu;

        public CrudeTableResultSlot(CrudeTableMenu menu, Container container, int index, int x, int y) {
            super(container, index, x, y);
            this.menu = menu;
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return false;
        }

        @Override
        public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
            CrudeTableInput input = new CrudeTableInput(
                    menu.getSlot(1).getItem(), menu.getSlot(2).getItem(),
                    menu.getSlot(3).getItem(), menu.getSlot(4).getItem(),
                    menu.getSlot(5).getItem()
            );

            CrudeTableRecipe recipe = menu.getCurrentRecipe(input);
            if (recipe != null) {
                for (int i = 1; i <= 5; i++) {
                    menu.getSlot(i).getItem().shrink(1);
                }
            }

            menu.slotsChanged(menu.inputSlots);

            super.onTake(player, stack);
        }
    }
}
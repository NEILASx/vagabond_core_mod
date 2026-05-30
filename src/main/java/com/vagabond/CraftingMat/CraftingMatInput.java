package com.vagabond.CraftingMat;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

public record CraftingMatInput(
        ItemStack top,
        ItemStack left,
        ItemStack center,
        ItemStack right,
        ItemStack bottom
 ) implements RecipeInput {


    public static CraftingMatInput fromContainer(Container container) {
        return new CraftingMatInput(
                container.getItem(1),
                container.getItem(3),
                container.getItem(4),
                container.getItem(5),
                container.getItem(7)
        );
    }

    @Override
    public @NotNull ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> top;
            case 1 -> left;
            case 2 -> center;
            case 3 -> right;
            case 4 -> bottom;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return 5;
    }
}
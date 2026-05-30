package com.vagabond.CraftingMat;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

public class CraftingMatShapelessRecipe extends CraftingMatRecipe {

    public CraftingMatShapelessRecipe(Ingredient top, Ingredient left, Ingredient center, Ingredient right, Ingredient bottom, ItemStack output) {
        super(top, left, center, right, bottom, output);
    }

}


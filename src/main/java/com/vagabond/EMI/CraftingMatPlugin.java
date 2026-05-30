package com.vagabond.EMI;

import com.vagabond.CraftingMat.CraftingMatRecipe;
import com.vagabond.CraftingMat.CraftingMatRecipeConverter;
import com.vagabond.VagabondTypes;
import com.vagabond.VagabondCore;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EmiEntrypoint
public class CraftingMatPlugin implements EmiPlugin {
    public static final ResourceLocation MY_SPRITE_SHEET = ResourceLocation.fromNamespaceAndPath(VagabondCore.MODID, "textures/gui/container/crafting_mat.png");

    public static final EmiRecipeCategory MY_CATEGORY = new EmiRecipeCategory(
            ResourceLocation.fromNamespaceAndPath(VagabondCore.MODID, "crafting_mat"),
            EmiStack.of(VagabondCore.CRAFTING_MAT.get())
    );

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(MY_CATEGORY);
        registry.addWorkstation(MY_CATEGORY, EmiStack.of(VagabondCore.CRAFTING_MAT.get()));

        RecipeManager recipeManager = registry.getRecipeManager();

        for (RecipeHolder<?> holder : recipeManager.getRecipes()) {
            // if its a crafting mat recipe add it
            if (holder.value().getType() == VagabondTypes.MAT_TYPE.get()) {
                registry.addRecipe(new com.vagabond.EMI.CraftingMatRecipe(holder.id(), (CraftingMatRecipe) holder.value()));
            }
        }

        for (var holder : CraftingMatRecipeConverter.DYNAMIC_CRAFTING_LIST) {
            registry.addRecipe(new com.vagabond.EMI.CraftingMatRecipe(holder.id(), holder.value()));
        }
    }
}
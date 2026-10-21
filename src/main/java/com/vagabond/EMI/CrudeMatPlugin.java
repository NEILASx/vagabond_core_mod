package com.vagabond.EMI;

import com.vagabond.CrudeMat.CrudeMatRecipe;
import com.vagabond.CrudeMat.CrudeMatRecipeConverter;
import com.vagabond.VagabondTypes;
import com.vagabond.VagabondCore;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

@EmiEntrypoint
public class CrudeMatPlugin implements EmiPlugin {
    public static final ResourceLocation MY_SPRITE_SHEET = ResourceLocation.fromNamespaceAndPath(VagabondCore.MODID, "textures/gui/container/crafting_mat.png");

    public static final EmiRecipeCategory MY_CATEGORY = new EmiRecipeCategory(
            ResourceLocation.fromNamespaceAndPath(VagabondCore.MODID, "crude_table"),
            EmiStack.of(VagabondCore.CRUDE_MAT.get())
    );

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(MY_CATEGORY);
        registry.addWorkstation(MY_CATEGORY, EmiStack.of(VagabondCore.CRUDE_MAT.get()));

        RecipeManager recipeManager = registry.getRecipeManager();

        for (RecipeHolder<?> holder : recipeManager.getRecipes()) {
            // if its a crafting mat recipe add it
            if (holder.value().getType() == VagabondTypes.MAT_TYPE.get()) {
                registry.addRecipe(new com.vagabond.EMI.CrudeMatRecipe(holder.id(), (CrudeMatRecipe) holder.value()));
            }
        }

        for (var holder : CrudeMatRecipeConverter.DYNAMIC_CRAFTING_LIST) {
            registry.addRecipe(new com.vagabond.EMI.CrudeMatRecipe(holder.id(), holder.value()));
        }
    }
}
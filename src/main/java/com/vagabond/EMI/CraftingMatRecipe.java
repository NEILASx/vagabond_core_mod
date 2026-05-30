package com.vagabond.EMI;

import com.vagabond.CraftingMat.CraftingMatShapelessRecipe;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;

public class CraftingMatRecipe extends BasicEmiRecipe {
    private final boolean isShapeless;

    public CraftingMatRecipe(ResourceLocation id, com.vagabond.CraftingMat.CraftingMatRecipe recipe) {
        super(CraftingMatPlugin.MY_CATEGORY, id, 118, 54);
        this.isShapeless = (recipe instanceof CraftingMatShapelessRecipe);

        this.inputs.add(dev.emi.emi.api.stack.EmiIngredient.of(recipe.top));
        this.inputs.add(dev.emi.emi.api.stack.EmiIngredient.of(recipe.left));
        this.inputs.add(dev.emi.emi.api.stack.EmiIngredient.of(recipe.center));
        this.inputs.add(dev.emi.emi.api.stack.EmiIngredient.of(recipe.right));
        this.inputs.add(dev.emi.emi.api.stack.EmiIngredient.of(recipe.bottom));

        this.outputs.add(dev.emi.emi.api.stack.EmiStack.of(recipe.output));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 60, 18);
        if (isShapeless) {
            widgets.addTexture(EmiTexture.SHAPELESS, 97, 0);
        }

        int internalIdx = 0;
        for (int i = 0; i < 9; i++) {
            int col = i % 3;
            int row = i / 3;

            if ((col == 0 || col == 2) && (row == 0 || row == 2)) continue;

            if (internalIdx < this.inputs.size()) {
                widgets.addSlot(this.inputs.get(internalIdx), col * 18, row * 18);
                internalIdx++;
            }
        }

        widgets.addSlot(outputs.get(0), 92, 14).large(true).recipeContext(this);
    }
}
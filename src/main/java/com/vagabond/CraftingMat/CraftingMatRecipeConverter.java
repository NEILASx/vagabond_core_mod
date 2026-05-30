package com.vagabond.CraftingMat;

import com.vagabond.VagabondCore;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

record SlotPos(int x, int y) {}

class MatLayout {
    public static final Map<SlotPos, Integer> CROSS_MAP = Map.of(
            new SlotPos(1, 0), 0, // top
            new SlotPos(0, 1), 1, // left
            new SlotPos(1, 1), 2, // center
            new SlotPos(2, 1), 3, // right
            new SlotPos(1, 2),     4  // bottom
    );
}

@EventBusSubscriber(modid = VagabondCore.MODID)
public class CraftingMatRecipeConverter {

    public static final List<RecipeHolder<CraftingMatRecipe>> DYNAMIC_CRAFTING_LIST = new ArrayList<>();

    public static CraftingMatRecipe tryConvert(ShapedRecipe shaped, HolderLookup.Provider registries) {
        if (!shaped.canCraftInDimensions(3, 3)) return null;
        NonNullList<Ingredient> ingredients = shaped.getIngredients();
        int width = shaped.getWidth();
        int height = shaped.getHeight();

        Ingredient[][] rawGrid = new Ingredient[3][3];
        for (int i = 0; i < 9; i++) rawGrid[i / 3][i % 3] = Ingredient.EMPTY;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                rawGrid[y][x] = ingredients.get(y * width + x);
            }
        }

        // offsets the grid one at a time and checks if it still matches
        for (int yOffset = 0; yOffset <= (3 - height); yOffset++) {
            for (int xOffset = 0; xOffset <= (3 - width); xOffset++) {

                Ingredient[][] candidate = new Ingredient[3][3];
                for (int i = 0; i < 9; i++) candidate[i / 3][i % 3] = Ingredient.EMPTY;

                boolean fits = true;
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        Ingredient ing = rawGrid[y][x];
                        if (ing.isEmpty()) continue;

                        int targetX = x + xOffset;
                        int targetY = y + yOffset;

                        if (!MatLayout.CROSS_MAP.containsKey(new SlotPos(targetX, targetY))) {
                            fits = false;
                            break;
                        }
                        candidate[targetY][targetX] = ing;
                    }
                    if (!fits) break;
                }

                // if it fits add it
                if (fits) {
                    return new CraftingMatRecipe(
                            candidate[0][1], // top
                            candidate[1][0], // left
                            candidate[1][1], // center
                            candidate[1][2], // right
                            candidate[2][1], // bottom
                            shaped.getResultItem(registries)
                    );
                }
            }
        }

        return null; // could not fit into a cross pattern
    }

    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        VagabondCore.LOGGER.info("Converting all 'recipeManager' recipes to CraftingMat Recipes...");
        DYNAMIC_CRAFTING_LIST.clear();
        RecipeManager recipeManager = event.getServer().getRecipeManager();
        HolderLookup.Provider registries = event.getServer().registryAccess();
        final long startTime = System.currentTimeMillis();

        for (RecipeHolder<?> holder : recipeManager.getRecipes()) {
            CraftingMatRecipe matRecipe = null;
            NonNullList<Ingredient> ingredients = holder.value().getIngredients();

            if (holder.value() instanceof ShapedRecipe shapedRecipe) {
                matRecipe = tryConvert(shapedRecipe, registries);

            } else if (holder.value() instanceof ShapelessRecipe shapeless) {
                if (!ingredients.isEmpty() && ingredients.size() <= 5) {

                    List<Ingredient> padded = new ArrayList<>(ingredients);
                    while (padded.size() < 5) {
                        padded.add(Ingredient.EMPTY);
                    }

                    matRecipe = new CraftingMatShapelessRecipe(
                            padded.get(0),
                            padded.get(1),
                            padded.get(2),
                            padded.get(3),
                            padded.get(4),
                            shapeless.getResultItem(registries)
                    );
                }
            }
            if (matRecipe != null) {
                ResourceLocation newId = ResourceLocation.fromNamespaceAndPath(
                        holder.id().getNamespace(),
                        "/mat_dynamic_" + holder.id().getPath()
                );

                RecipeHolder<CraftingMatRecipe> wrappedHolder = new RecipeHolder<>(newId, matRecipe);
                DYNAMIC_CRAFTING_LIST.add(wrappedHolder);

//                VagabondCore.LOGGER.info("Successfully indexed plus recipe for: {}", holder.id());
            }
        }

        final long endTime = System.currentTimeMillis();


        VagabondCore.LOGGER.info("Total execution time: {} ms", (endTime - startTime));


    }
}
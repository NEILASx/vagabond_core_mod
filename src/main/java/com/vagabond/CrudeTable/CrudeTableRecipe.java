package com.vagabond.CrudeTable;

import com.vagabond.VagabondTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CrudeTableRecipe implements Recipe<CrudeTableInput> {
    public final Ingredient top;
    public final Ingredient left;
    public final Ingredient center;
    public final Ingredient right;
    public final Ingredient bottom;
    public final ItemStack output;

    public CrudeTableRecipe(Ingredient top, Ingredient left, Ingredient center, Ingredient right, Ingredient bottom, ItemStack output) {
        this.top = top;
        this.left = left;
        this.center = center;
        this.right = right;
        this.bottom = bottom;

        this.output = output;
    }

    public int getWidth() {
        int minX = 3, maxX = -1;

        if (!top.isEmpty())    { minX = Math.min(minX, 1); maxX = Math.max(maxX, 1); }
        if (!left.isEmpty())   { minX = Math.min(minX, 0); maxX = Math.max(maxX, 0); }
        if (!center.isEmpty()) { minX = Math.min(minX, 1); maxX = Math.max(maxX, 1); }
        if (!right.isEmpty())  { minX = Math.min(minX, 2); maxX = Math.max(maxX, 2); }
        if (!bottom.isEmpty()) { minX = Math.min(minX, 1); maxX = Math.max(maxX, 1); }

        return (maxX == -1) ? 0 : (maxX - minX) + 1;
    }

    public int getHeight() {
        int minY = 3, maxY = -1;

        if (!top.isEmpty())    { minY = Math.min(minY, 0); maxY = Math.max(maxY, 0); }
        if (!left.isEmpty())   { minY = Math.min(minY, 1); maxY = Math.max(maxY, 1); }
        if (!center.isEmpty()) { minY = Math.min(minY, 1); maxY = Math.max(maxY, 1); }
        if (!right.isEmpty())  { minY = Math.min(minY, 1); maxY = Math.max(maxY, 1); }
        if (!bottom.isEmpty()) { minY = Math.min(minY, 2); maxY = Math.max(maxY, 2); }

        return (maxY - minY) + 1;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= this.getWidth() && height >= this.getHeight();
    }

    // strict matching for shaped recipes
    public boolean strictMatches(CrudeTableInput other, @NotNull Level level) {
        List<ItemStack> inputs = new ArrayList<>(
                Arrays.asList(
                        other.top(),other.left(),other.center(),other.right(),other.bottom()
                )
        );
        inputs.removeIf(ItemStack::isEmpty);

        List<Ingredient> required = new ArrayList<>(Arrays.asList(top, left, center, right, bottom));
        required.removeIf(Ingredient::isEmpty);
        if (required.size() != inputs.size()) return false;

        int w = getWidth();
        int h = getHeight();

        // handles non fully shaped recipes
        if (w == 2 && h == 1) {
            return (this.left.test(other.left()) && this.center.test(other.center())) ||
                    (this.left.test(other.center()) && this.center.test(other.right()));
        }

        if (w == 1 && h == 2) {
            return (this.top.test(other.top()) && this.center.test(other.center())) ||
                    (this.top.test(other.center()) && this.center.test(other.bottom()));
        }
        // no need for a 1x1 as that should be handled via Shapeless

        return this.top.test(other.top())
                && this.left.test(other.left())
                && this.center.test(other.center())
                && this.right.test(other.right())
                && this.bottom.test(other.bottom());
    }

    // makes sure that we have a dynamic matching for shapeless
    @Override
    public boolean matches(CrudeTableInput input, @NotNull Level level) {
        List<ItemStack> inputs = new ArrayList<>(
                Arrays.asList(
                        input.top(),input.left(),input.center(),input.right(),input.bottom()
                )
        );
        inputs.removeIf(ItemStack::isEmpty);

        List<Ingredient> required = new ArrayList<>(Arrays.asList(top, left, center, right, bottom));
        required.removeIf(Ingredient::isEmpty);

        if (inputs.size() != required.size()) return false;

        List<ItemStack> remainingInputs = new ArrayList<>(inputs);
        for (Ingredient req : required) {
            boolean found = false;
            for (int i = 0; i < remainingInputs.size(); i++) {
                if (req.test(remainingInputs.get(i))) {
                    remainingInputs.remove(i);
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CrudeTableInput CrudeTableInputContainer, HolderLookup.@NotNull Provider registries) {
        return this.output.copy();
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registries) {
        return this.output;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(this.top);
        list.add(this.left);
        list.add(this.center);
        list.add(this.right);
        list.add(this.bottom);
        return list;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return VagabondTypes.MAT_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return VagabondTypes.MAT_TYPE.get();
    }
}


package com.vagabond;

import com.vagabond.CraftingMat.CraftingMatMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.vagabond.CraftingMat.CraftingMatSerializer;
import com.vagabond.CraftingMat.CraftingMatRecipe;

public class VagabondTypes {


    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, VagabondCore.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<CraftingMatMenu>> CRAFTING_MAT_MENU =
            MENUS.register("crafting_mat_menu", () -> IMenuTypeExtension.create((windowId, inv, data) -> new CraftingMatMenu(windowId, inv)));


    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, VagabondCore.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, CraftingMatSerializer> MAT_SERIALIZER =
            SERIALIZERS.register("crafting_mat", CraftingMatSerializer::new);

    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, VagabondCore.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<CraftingMatRecipe>> MAT_TYPE =
            TYPES.register("crafting_mat", () -> new RecipeType<>() {
                @Override
                public String toString() { return "crafting_mat"; }
            });

    public static void register(IEventBus bus) {
        MENUS.register(bus);
        SERIALIZERS.register(bus);
        TYPES.register(bus);
    }
}
package com.vagabond;

import com.vagabond.CrudeMat.CrudeMatMenu;
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
import com.vagabond.CrudeMat.CrudeMatSerializer;
import com.vagabond.CrudeMat.CrudeMatRecipe;

public class VagabondTypes {


    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, VagabondCore.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<CrudeMatMenu>> CRUDE_TABLE_MENU =
            MENUS.register("crude_mat_menu", () -> IMenuTypeExtension.create((windowId, inv, data) -> new CrudeMatMenu(windowId, inv)));


    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, VagabondCore.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, CrudeMatSerializer> MAT_SERIALIZER =
            SERIALIZERS.register("crude_mat", CrudeMatSerializer::new);

    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, VagabondCore.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<CrudeMatRecipe>> MAT_TYPE =
            TYPES.register("crude_mat", () -> new RecipeType<>() {
                @Override
                public String toString() { return "crude_mat"; }
            });

    public static void register(IEventBus bus) {
        MENUS.register(bus);
        SERIALIZERS.register(bus);
        TYPES.register(bus);
    }
}
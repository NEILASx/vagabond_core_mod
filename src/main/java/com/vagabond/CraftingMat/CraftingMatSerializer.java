package com.vagabond.CraftingMat;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

public class CraftingMatSerializer implements RecipeSerializer<CraftingMatRecipe> {

    public static final MapCodec<CraftingMatRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.optionalFieldOf("top", Ingredient.EMPTY).forGetter(r -> r.top),
            Ingredient.CODEC.optionalFieldOf("left", Ingredient.EMPTY).forGetter(r -> r.left),
            Ingredient.CODEC.optionalFieldOf("center", Ingredient.EMPTY).forGetter(r -> r.center),
            Ingredient.CODEC.optionalFieldOf("right", Ingredient.EMPTY).forGetter(r -> r.right),
            Ingredient.CODEC.optionalFieldOf("bottom", Ingredient.EMPTY).forGetter(r -> r.bottom),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.output)
    ).apply(inst, CraftingMatRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CraftingMatRecipe> STREAM_CODEC =
            StreamCodec.of(
                    (buf, recipe) -> {
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.top);
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.left);
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.center);
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.right);
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.bottom);
                        ItemStack.STREAM_CODEC.encode(buf, recipe.output);
                    },
                    buf -> new CraftingMatRecipe(
                            Ingredient.CONTENTS_STREAM_CODEC.decode(buf),
                            Ingredient.CONTENTS_STREAM_CODEC.decode(buf),
                            Ingredient.CONTENTS_STREAM_CODEC.decode(buf),
                            Ingredient.CONTENTS_STREAM_CODEC.decode(buf),
                            Ingredient.CONTENTS_STREAM_CODEC.decode(buf),
                            ItemStack.STREAM_CODEC.decode(buf)
                    )
            );

    @Override
    public @NotNull MapCodec<CraftingMatRecipe> codec() { return CODEC; }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, CraftingMatRecipe> streamCodec() { return STREAM_CODEC; }
}
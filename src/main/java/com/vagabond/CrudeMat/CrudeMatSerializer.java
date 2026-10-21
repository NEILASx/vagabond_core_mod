package com.vagabond.CrudeMat;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

public class CrudeMatSerializer implements RecipeSerializer<CrudeMatRecipe> {

    public static final MapCodec<CrudeMatRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.optionalFieldOf("top", Ingredient.EMPTY).forGetter(r -> r.top),
            Ingredient.CODEC.optionalFieldOf("left", Ingredient.EMPTY).forGetter(r -> r.left),
            Ingredient.CODEC.optionalFieldOf("center", Ingredient.EMPTY).forGetter(r -> r.center),
            Ingredient.CODEC.optionalFieldOf("right", Ingredient.EMPTY).forGetter(r -> r.right),
            Ingredient.CODEC.optionalFieldOf("bottom", Ingredient.EMPTY).forGetter(r -> r.bottom),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.output)
    ).apply(inst, CrudeMatRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CrudeMatRecipe> STREAM_CODEC =
            StreamCodec.of(
                    (buf, recipe) -> {
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.top);
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.left);
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.center);
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.right);
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.bottom);
                        ItemStack.STREAM_CODEC.encode(buf, recipe.output);
                    },
                    buf -> new CrudeMatRecipe(
                            Ingredient.CONTENTS_STREAM_CODEC.decode(buf),
                            Ingredient.CONTENTS_STREAM_CODEC.decode(buf),
                            Ingredient.CONTENTS_STREAM_CODEC.decode(buf),
                            Ingredient.CONTENTS_STREAM_CODEC.decode(buf),
                            Ingredient.CONTENTS_STREAM_CODEC.decode(buf),
                            ItemStack.STREAM_CODEC.decode(buf)
                    )
            );

    @Override
    public @NotNull MapCodec<CrudeMatRecipe> codec() { return CODEC; }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, CrudeMatRecipe> streamCodec() { return STREAM_CODEC; }
}
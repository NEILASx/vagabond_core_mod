package com.vagabond.mixin;

import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(AnvilMenu.class)
public class AnvilMenuMixin {

    /**
     * @author NEILASx
     * @reason Changing slot positions for custom UI
     */
    @Overwrite
    protected ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create()
                .withSlot(0, 27, 47, p_266635_ -> true)
                .withSlot(1, 76, 47, p_266634_ -> true)
                .withResultSlot(2, 134, 47)
                .withSlot(3, 22, 17, itemStack -> true)
                .build();
    }
}
package com.vagabond.anvil;

// this changes the anvil block to use the com.vagabond.anvil.AnvilMenu instead of default Minecraft's menu.

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilBlock.class)
public class AnvilBlockMixin {
    @Inject(method = "getMenuProvider", at = @At("HEAD"), cancellable = true)
    private void redirectMenuProvider(BlockState state, Level level, BlockPos pos, CallbackInfoReturnable<MenuProvider> cir) {
        Component containerTitle = Component.translatable("container.repair");

        cir.setReturnValue(new SimpleMenuProvider(
                (containerId, playerInventory, player) -> new com.vagabond.anvil.AnvilMenu(containerId, playerInventory, ContainerLevelAccess.create(level, pos)),
                containerTitle
        ));
    }
}
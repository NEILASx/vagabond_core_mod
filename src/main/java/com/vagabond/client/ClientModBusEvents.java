package com.vagabond.client;

import com.vagabond.CraftingMat.CraftingMatScreen;
import com.vagabond.VagabondCore;
import com.vagabond.VagabondTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = VagabondCore.MODID, value = Dist.CLIENT)
public class ClientModBusEvents {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(VagabondTypes.CRAFTING_MAT_MENU.get(), CraftingMatScreen::new);
    }
}
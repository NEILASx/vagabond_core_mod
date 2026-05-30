package com.vagabond;

import com.mojang.logging.LogUtils;
import com.vagabond.DataGeneration.RecipeProvider;
import com.vagabond.blocks.CraftingMat;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;

@Mod(VagabondCore.MODID)
public class VagabondCore {
    public static final String MODID = "vagabond_core";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredBlock<CraftingMat> CRAFTING_MAT = BLOCKS.register(
            "crafting_mat",
            () -> new CraftingMat(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL).noOcclusion())
    );

    public static final DeferredItem<BlockItem> CRAFTING_MAT_ITEM = ITEMS.registerSimpleBlockItem(
            "crafting_mat",
            CRAFTING_MAT
    );

    public static final DeferredItem<Item> COPPER_HAMMER = ITEMS.registerSimpleItem(
            "copper_hammer",
            new Item.Properties().stacksTo(1)
    );

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> VAGABOND_CREATIVE_TAB = CREATIVE_MODE_TABS.register("vagabond_core_tab", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.vagabond_core")).withTabsBefore(CreativeModeTabs.COMBAT).icon(() -> CRAFTING_MAT_ITEM.get().getDefaultInstance()).displayItems((parameters, output) -> {
        output.accept(CRAFTING_MAT_ITEM.get());
        output.accept(COPPER_HAMMER.get());
    }).build());

    public VagabondCore(IEventBus modEventBus, ModContainer modContainer) {
        // register everything that needs to be registered

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        VagabondTypes.register(modEventBus);

        LOGGER.info("Loaded Vagabond Core!");
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new RecipeProvider(output, lookupProvider));
    }
}

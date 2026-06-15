package com.pewpewcricket.cricketsperipherals.main;

import com.pewpewcricket.cricketsperipherals.block.BlockDataCoprocessor;
import com.pewpewcricket.cricketsperipherals.block.BlockHardDrive;
import com.pewpewcricket.cricketsperipherals.blockentity.BlockEntityDataCoprocessor;
import com.pewpewcricket.cricketsperipherals.blockentity.BlockEntityHardDrive;
import com.pewpewcricket.cricketsperipherals.util.RefStrings;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MainRegistry {
    // Create registries.
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(RefStrings.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, RefStrings.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RefStrings.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RefStrings.MODID);

    // Blocks
    public static final DeferredBlock<Block> BLOCK_HARD_DRIVE = BLOCKS.register("hard_drive", () -> new BlockHardDrive(BlockBehaviour.Properties.of()));
    public static final DeferredBlock<Block> BLOCK_DATA_COPROCESSOR = BLOCKS.register("data_coprocessor", () -> new BlockDataCoprocessor(BlockBehaviour.Properties.of()));

    // BlockEntities
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityHardDrive>> BLOCK_ENTITY_HARD_DRIVE =
            BLOCK_ENTITIES.register("hard_drive", () -> BlockEntityType.Builder.of(BlockEntityHardDrive::new, BLOCK_HARD_DRIVE.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityDataCoprocessor>> BLOCK_ENTITY_ADVANCED_CALCULATOR =
            BLOCK_ENTITIES.register("data_coprocessor", () -> BlockEntityType.Builder.of(BlockEntityDataCoprocessor::new, BLOCK_DATA_COPROCESSOR.get()).build(null));

    // Items
    public static final DeferredItem<BlockItem> ITEM_HARD_DRIVE =
            ITEMS.registerSimpleBlockItem(BLOCK_HARD_DRIVE);
    public static final DeferredItem<BlockItem> ITEM_DATA_COPROCESSOR =
            ITEMS.registerSimpleBlockItem(BLOCK_DATA_COPROCESSOR);

    // Creative tabs
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("cricketsperipherals_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.cricketsperipherals"))
            .icon(() -> ITEM_DATA_COPROCESSOR.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ITEM_HARD_DRIVE.get());
                output.accept(ITEM_DATA_COPROCESSOR.get());
            }).build());

    public static void register(IEventBus modEventBus) {
        // Register blocks & items.
        BLOCKS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}

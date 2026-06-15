package com.pewpewcricket.cricketsperipherals.main;

import com.pewpewcricket.cricketsperipherals.peripheral.PeripheralDataCoprocessor;
import com.pewpewcricket.cricketsperipherals.peripheral.PeripheralHardDrive;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import com.pewpewcricket.cricketsperipherals.util.RefStrings;

@Mod(RefStrings.MODID)
public class CricketsPeripherals {
    public static final Logger LOGGER = LogUtils.getLogger();

    public CricketsPeripherals(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for mod loading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerCapabilities);
        ModComponents.register(modEventBus);

        // Register blocks and items.
        MainRegistry.register(modEventBus);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                PeripheralCapability.get(),
                MainRegistry.BLOCK_ENTITY_HARD_DRIVE.get(),
                (be, side) -> new PeripheralHardDrive(be)
        );
        event.registerBlockEntity(
                PeripheralCapability.get(),
                MainRegistry.BLOCK_ENTITY_ADVANCED_CALCULATOR.get(),
                (be, side) -> new PeripheralDataCoprocessor(be)
        );
    }
}
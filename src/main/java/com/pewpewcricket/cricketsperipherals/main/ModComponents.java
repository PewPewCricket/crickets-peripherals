package com.pewpewcricket.cricketsperipherals.main;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModComponents {

    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(
                    Registries.DATA_COMPONENT_TYPE,
                    "betterperipherals"
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MOUNT_ID =
            COMPONENTS.registerComponentType(
                    "mount_id",
                    builder -> builder.persistent(Codec.INT)
            );

    public static void register(IEventBus bus) {
        COMPONENTS.register(bus);
    }
}
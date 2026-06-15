package com.pewpewcricket.cricketsperipherals.peripheral;

import com.pewpewcricket.cricketsperipherals.blockentity.BlockEntityHardDrive;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class PeripheralHardDrive implements IPeripheral {
    private final BlockEntityHardDrive be;

    public PeripheralHardDrive(BlockEntityHardDrive be) {
        this.be = be;
    }

    @Override
    public @NonNull String getType() {
        return "hard_drive";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other instanceof PeripheralHardDrive o && o.be == this.be;
    }

    @Override
    public void attach(@NonNull IComputerAccess computer) {
        be.attach(computer);
    }

    @Override
    public void detach(@NonNull IComputerAccess computer) {
        be.detach(computer);
    }

    @LuaFunction
    public String getMountPath(IComputerAccess computer) {
        return be.getMountPath(computer);
    }

    @LuaFunction
    public void mount(IComputerAccess computer) {
        be.attach(computer);
    }

    @LuaFunction
    public void unmount(IComputerAccess computer) {
        be.detach(computer);
    }
}

package com.pewpewcricket.cricketsperipherals.blockentity;

import com.google.errorprone.annotations.concurrent.GuardedBy;
import com.pewpewcricket.cricketsperipherals.block.BlockHardDrive;
import com.pewpewcricket.cricketsperipherals.main.Config;
import com.pewpewcricket.cricketsperipherals.main.MainRegistry;
import com.pewpewcricket.cricketsperipherals.main.ModComponents;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.filesystem.WritableMount;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

public class BlockEntityHardDrive extends BlockEntity {
    private static final String NBT_MOUNT_ID  = "MountId";
    private final Map<IComputerAccess, String> computers = new HashMap<>();

    @GuardedBy("this")
    public int mountId = -1;

    @GuardedBy("this")
    private WritableMount mount;

    public BlockEntityHardDrive(BlockPos pos, BlockState state) {
        super(MainRegistry.BLOCK_ENTITY_HARD_DRIVE.get(), pos, state);
    }

    public void attach(IComputerAccess computer) {
        synchronized (this) {
            if (computers.containsKey(computer)) return;
            String mountPath = null;

            for (int i = 0; mountPath == null; i++) {
                String name = i == 0 ? "hdd" : "hdd" + i;
                mountPath = computer.mountWritable(
                        name,
                        getMount()
                );
            }

            computers.put(computer, mountPath);
            updateState();
        }
    }

    public void detach(IComputerAccess computer) {
        synchronized (this) {
            String mountPath = computers.remove(computer);

            if (mountPath != null) {
                computer.unmount(mountPath);
            }

            computers.remove(computer);
            updateState();
        }
    }

    public WritableMount getMount() {
        if (mount == null) {
            if (mountId == -1)
                createMountDir();

            if (level instanceof ServerLevel serverLevel) {
                MinecraftServer server = serverLevel.getServer();
                mount = ComputerCraftAPI.createSaveDirMount(
                        server,
                        "betterperipherals/hdd/" + mountId,
                        Config.HDD_SIZE.get()
                );
            }
        }

        return mount;
    }

    public void deleteMounts() {
        for (var entry : computers.entrySet()) {
            entry.getKey().unmount(entry.getValue());
        }

        computers.clear();
    }

    public String getMountPath(IComputerAccess computer) {
        return computers.get(computer);
    }

    private void updateState() {
        if (isRemoved() || level == null) return;

        BlockHardDrive.HardDriveState newState = computers.isEmpty()
                ? BlockHardDrive.HardDriveState.OFF
                : BlockHardDrive.HardDriveState.IDLE;

        BlockState current = getBlockState();

        if (current.getValue(BlockHardDrive.STATE) != newState) {
            level.setBlock(
                    worldPosition,
                    current.setValue(BlockHardDrive.STATE, newState),
                    3
            );
        }
    }

    private void createMountDir() {
        if (!(level instanceof ServerLevel serverLevel)) return;

        MinecraftServer server = serverLevel.getServer();
        mountId = ComputerCraftAPI.createUniqueNumberedSaveDir(server, "betterperipherals/hdd");
        setChanged();
    }

    @Override
    public void saveAdditional(@NonNull CompoundTag tag, HolderLookup.@NonNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(NBT_MOUNT_ID, mountId);
    }

    @Override
    public void loadAdditional(@NonNull CompoundTag tag, HolderLookup.@NonNull Provider registries) {
        super.loadAdditional(tag, registries);
        mountId = tag.contains(NBT_MOUNT_ID) ? tag.getInt(NBT_MOUNT_ID) : -1;
    }

    @Override
    protected void applyImplicitComponents(@NonNull DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        mountId = componentInput.getOrDefault(ModComponents.MOUNT_ID.get(), mountId);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.@NonNull Builder components) {
        super.collectImplicitComponents(components);
        components.set(ModComponents.MOUNT_ID.get(), mountId);
    }
}
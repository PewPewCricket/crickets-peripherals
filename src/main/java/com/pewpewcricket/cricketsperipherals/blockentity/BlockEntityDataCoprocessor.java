package com.pewpewcricket.cricketsperipherals.blockentity;

import com.pewpewcricket.cricketsperipherals.block.BlockDataCoprocessor;
import com.pewpewcricket.cricketsperipherals.main.MainRegistry;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;

public class BlockEntityDataCoprocessor extends BlockEntity {
    private final ArrayList<IComputerAccess> computers = new ArrayList<>();

    public BlockEntityDataCoprocessor(BlockPos pos, BlockState state) {
        super(MainRegistry.BLOCK_ENTITY_ADVANCED_CALCULATOR.get(), pos, state);
    }

    public void attach(IComputerAccess computer) {
        computers.add(computer);
        updateState();
    }

    public void detach(IComputerAccess computer) {
        computers.remove(computer);
        updateState();
    }

    private void updateState() {
        if (isRemoved() || level == null) return;

        BlockDataCoprocessor.AdvancedCalculatorState newState = computers.isEmpty()
                ? BlockDataCoprocessor.AdvancedCalculatorState.OFF
                : BlockDataCoprocessor.AdvancedCalculatorState.ON;

        BlockState current = getBlockState();

        if (current.getValue(BlockDataCoprocessor.STATE) != newState) {
            level.setBlock(
                    worldPosition,
                    current.setValue(BlockDataCoprocessor.STATE, newState),
                    3
            );
        }
    }
}

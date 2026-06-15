package com.pewpewcricket.cricketsperipherals.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.pewpewcricket.cricketsperipherals.blockentity.BlockEntityHardDrive;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class BlockHardDrive extends Block implements EntityBlock {
    public static final EnumProperty<HardDriveState> STATE = EnumProperty.create("state", HardDriveState.class);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public BlockHardDrive(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(STATE, HardDriveState.OFF));
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STATE);
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new BlockEntityHardDrive(pos, state);
    }

    @Override
    public void onRemove(BlockState state, @NonNull Level level, @NonNull BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BlockEntityHardDrive drive) {
                drive.deleteMounts();
            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    public enum HardDriveState implements StringRepresentable {
        OFF,
        IDLE,
        ACTIVE;

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase();
        }
    }
}

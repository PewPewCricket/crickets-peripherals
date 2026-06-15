package com.pewpewcricket.cricketsperipherals.block;

import com.pewpewcricket.cricketsperipherals.blockentity.BlockEntityDataCoprocessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

public class BlockDataCoprocessor extends Block implements EntityBlock {
    public static final EnumProperty<BlockDataCoprocessor.AdvancedCalculatorState> STATE = EnumProperty.create(
            "state",
            BlockDataCoprocessor.AdvancedCalculatorState.class
    );
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public BlockDataCoprocessor(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(STATE, BlockDataCoprocessor.AdvancedCalculatorState.OFF));
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
        return new BlockEntityDataCoprocessor(pos, state);
    }

    public enum AdvancedCalculatorState implements StringRepresentable {
        OFF,
        ON;

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase();
        }
    }
}

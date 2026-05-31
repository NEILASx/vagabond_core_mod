package com.vagabond.blocks;

import com.vagabond.CraftingMat.CraftingMatMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class CraftingMat extends Block {
    private static final Component CONTAINER_TITLE = Component.literal("Crafting Mat");

    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty EAST = BooleanProperty.create("east");

    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0); // taken straight from CarpetBlock

    public CraftingMat(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(EAST, false));
    }

    @Override
    protected void onPlace(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean movedByPiston) {
        level.scheduleTick(pos, this, 1);
    }

    // TODO: THIS IS SO BAD FOR PERFORMANCE BUT IT UPDATES EVERY TICK 😭😭😭
    @Override
    protected void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.tick(state, level, pos, random);

        BlockState updatedState = calculateState(state, level, pos);
        level.setBlock(pos, updatedState, Block.UPDATE_CLIENTS | Block.UPDATE_INVISIBLE | Block.UPDATE_IMMEDIATE);
        level.scheduleTick(pos, this, 1);
    }
//   ISNT RELIABLE FOR CHECKING
//    @Override
//    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
//        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
//
//        BlockState updatedState = calculateState(state, level, pos);
//
//        if (updatedState != state) {
//            level.setBlock(pos, updatedState, 3);
//        }
//    }

    private BlockState calculateState(BlockState state, LevelAccessor level, BlockPos pos) {
        return state
                .setValue(NORTH, !level.isEmptyBlock(pos.north().below()))
                .setValue(SOUTH, !level.isEmptyBlock(pos.south().below()))
                .setValue(WEST, !level.isEmptyBlock(pos.west().below()))
                .setValue(EAST, !level.isEmptyBlock(pos.east().below()));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        return calculateState(this.defaultBlockState(), level, pos);
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction facing, @NotNull BlockState neighborState, @NotNull LevelAccessor level, @NotNull BlockPos currentPos, @NotNull BlockPos neighborPos) {
        BlockState updatedState = super.updateShape(state, facing, neighborState, level, currentPos, neighborPos);

        if (!updatedState.canSurvive(level, currentPos)) {
            return Blocks.AIR.defaultBlockState();
        }

        if (facing.getAxis().isHorizontal()) {
            boolean isEmpty = level.isEmptyBlock(neighborPos);
            return updatedState.setValue(getBooleanPropFromDir(facing), isEmpty);
        }

        return calculateState(this.defaultBlockState(), level, currentPos);
    }

    private BooleanProperty getBooleanPropFromDir(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
            default -> throw new IllegalArgumentException("Invalid direction");
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, WEST, EAST);
    }
    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            MenuProvider menuProvider = this.getMenuProvider(state, level, pos);
            if (menuProvider != null) {
                player.openMenu(menuProvider);
            }
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public MenuProvider getMenuProvider(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        return new SimpleMenuProvider(
                (containerId, playerInventory, p) -> new CraftingMatMenu(containerId, playerInventory),
                CONTAINER_TITLE
        );
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState p_152917_, @NotNull BlockGetter p_152918_, @NotNull BlockPos p_152919_, @NotNull CollisionContext p_152920_) {
        return SHAPE;
    }

    @Override
    protected boolean canSurvive(@NotNull BlockState p_152922_, LevelReader p_152923_, BlockPos p_152924_) {
        return !p_152923_.isEmptyBlock(p_152924_.below());
    }
}
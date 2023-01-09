package com.isaac.tutorialmod.blocks.custom;

import com.isaac.tutorialmod.blocks.entity.ModBlockEntityTypes;
import com.isaac.tutorialmod.entity.ModEntityTypes;
import com.isaac.tutorialmod.entity.custom.Toydog;
import com.isaac.tutorialmod.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;

public class ToydogFigure extends BaseEntityBlock implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public ToydogFigure() {
        super(Properties.of(Material.STONE).noOcclusion());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState){
        return ModBlockEntityTypes.TOYDOG_FIGURE.get().create(pPos, pState);
    }

    public static VoxelShape SHAPE =
            Block.box(4, 0, 4, 12, 16, 12);

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING,
                context.getHorizontalDirection().getClockWise().getClockWise());
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        ItemStack itemStack = player.getItemInHand(hand);
        Item item = itemStack.getItem();
        if (itemStack.is(ModItems.GOLD_HEART.get())) { //isaac this is a test
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
            Toydog toydog = ModEntityTypes.TOYDOG.get().create(level);
            toydog.moveTo(pos.getX(), pos.getY(), pos.getZ(), 0f, 0.0F); //isaac 0f yrot is a placeholder number for now because I dont know the direction the block is facing
            level.addFreshEntity(toydog);
            itemStack.shrink(1);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }
}

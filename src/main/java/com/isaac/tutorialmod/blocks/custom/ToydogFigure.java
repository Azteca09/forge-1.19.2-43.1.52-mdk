package com.isaac.tutorialmod.blocks.custom;

import com.isaac.tutorialmod.blocks.entity.ModBlockEntityTypes;
import com.isaac.tutorialmod.entity.ModEntityTypes;
import com.isaac.tutorialmod.entity.custom.Toydog;
import com.isaac.tutorialmod.item.ModItems;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;

import javax.annotation.Nullable;

import static java.lang.Math.PI;
import static java.lang.Math.random;

public class ToydogFigure extends BaseEntityBlock implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    //private static final Logger LOGGER = LogUtils.getLogger();
    public ToydogFigure() {
        super(Properties.of(Material.STONE).noOcclusion());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState){
        return ModBlockEntityTypes.TOYDOG_FIGURE.get().create(pPos, pState);
    }
    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {

        Direction direction = blockState.getValue(FACING);

        switch (direction) {
            case NORTH: {
                return Block.box(4, 0, 0, 12, 12, 8);
            }
            case SOUTH: {
                return Block.box(4, 0, 8, 12, 12, 16);
            }
            case WEST: {
                return Block.box(0, 0, 4, 8, 12, 12);
            }
            default:
                return Block.box(8, 0, 4, 16, 12, 12);
        }
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
    public InteractionResult use(BlockState state, Level level, BlockPos pPos, Player player, InteractionHand hand, BlockHitResult result) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.is(ModItems.GOLD_HEART.get())) {
            if (level.isRaining()) {
                if (level.canSeeSky(pPos)) {
                    LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(level);
                    lightningbolt.moveTo(Vec3.atBottomCenterOf(pPos.above()));
                    lightningbolt.setCause(player instanceof ServerPlayer ? (ServerPlayer)player : null);
                    level.addFreshEntity(lightningbolt);
                    if (!level.isClientSide) {
                        level.playSound((Player) null, pPos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0F, 0.5F + level.random.nextFloat() * 1.2F);
                    }
                    comeAlive(state, level, pPos, player);
                    itemStack.shrink(1);
                    return InteractionResult.CONSUME;
                }
            }
        }
        return InteractionResult.SUCCESS;
    }
    public void comeAlive(BlockState state, Level level, BlockPos pPos, Player player){
        float yrot = state.getValue(FACING).getClockWise().toYRot()-90F;
        //LOGGER.info(Float.toString(yrot));

        level.setBlock(pPos, Blocks.AIR.defaultBlockState(), 2);
        Toydog toydog = ModEntityTypes.TOYDOG.get().create(level);
        toydog.moveTo((double)pPos.getX()+0.5D, pPos.getY(), (double)pPos.getZ()+0.5D);

        toydog.setYBodyRot(yrot);
        toydog.setYHeadRot(yrot);

        level.addFreshEntity(toydog);
        toydog.tame(player);
        toydog.playAmbientSound();
        toydog.level.broadcastEntityEvent(toydog, (byte)7);
    }

    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pLevel.isThundering() && (long)pLevel.random.nextInt(200) <= pLevel.getGameTime() % 200L && pPos.getY() == pLevel.getHeight(Heightmap.Types.WORLD_SURFACE, pPos.getX(), pPos.getZ()) - 1) {
            ParticleUtils.spawnParticlesAlongAxis(pState.getValue(FACING).getAxis(), pLevel, pPos, 0.125D, ParticleTypes.ELECTRIC_SPARK, UniformInt.of(1, 2));
        }
    }
}

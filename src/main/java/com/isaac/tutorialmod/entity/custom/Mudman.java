package com.isaac.tutorialmod.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import net.minecraft.world.entity.item.FallingBlockEntity;

public class Mudman extends Monster implements IAnimatable {
    private AnimationFactory factory = new AnimationFactory(this);

    public Mudman(EntityType<? extends Monster> pEntityType, Level pLevel) {

        super(pEntityType, pLevel);
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.0f).build();
    }



    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(3, new MudmanPlacesDirt(this));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Creeper.class, true));
    }

    static class MudmanPlacesDirt extends Goal {
        private final Mudman mudman;
        private final Level level;
        private int timeUntilNextPlacement = 0;

        //public boolean canUse(){
            //return true;
        //}

        public MudmanPlacesDirt(Mudman mudman) {
            this.mudman = mudman;
            this.level = mudman.level;
        }
        public boolean canUse() {

            LivingEntity livingentity = mudman.getTarget();
            return livingentity != null && livingentity.isAlive() && this.mudman.canAttack(livingentity);
        }
        @Override
        public void tick() {
            // Choose a random position within a certain range of the mob
            BlockPos currentPos = this.mudman.blockPosition();
            //int range = 5; // Change this value to adjust the range
            //int x = currentPos.getX() + world.rand.nextInt(range * 2 + 1) - range;
            //int y = currentPos.getY() + world.rand.nextInt(range * 2 + 1) - range;
            //int z = currentPos.getZ() + world.rand.nextInt(range * 2 + 1) - range;
            BlockPos targetPos = currentPos; //new BlockPos(x, y, z);

            // Place a dirt block at the chosen position
            Level level = this.mudman.level;


            level.setBlock(targetPos, Blocks.DIRT.defaultBlockState(), 2);

            LivingEntity livingentity = mudman.getTarget();

            /*

            double d0 = this.mudman.distanceToSqr(livingentity);
            double d1 = livingentity.getX() - this.mudman.getX();
            double d2 = livingentity.getY(0.5D) - this.mudman.getY(0.5D);
            double d3 = livingentity.getZ() - this.mudman.getZ();
            double d4 = Math.sqrt(Math.sqrt(d0)) * 0.5D;

            SmallFireball smallfireball = new SmallFireball(this.mudman.level, this.mudman, this.mudman.getRandom().triangle(d1, 2.297D * d4), d2, this.mudman.getRandom().triangle(d3, 2.297D * d4));
            smallfireball.setPos(smallfireball.getX(), this.mudman.getY(0.5D) + 0.5D, smallfireball.getZ());


            this.mudman.level.addFreshEntity(smallfireball);

             */

            BlockState sandBlock = Blocks.SAND.defaultBlockState();

            //this.mudman.level, this.mudman.getX(), this.mudman.getY(), this.mudman.getZ(), Blocks.SAND.defaultBlockState());

            //FallingBlockEntity fallingBlock = new FallingBlockEntity(this.mudman.level, this.mudman.getX(), this.mudman.getY(), this.mudman.getZ(), Blocks.SAND.defaultBlockState());

            FallingBlockEntity fallingBlock = new FallingBlockEntity(this.mudman.level, 0, 0, 10, sandBlock);

            // set the FallingBlockEntity's motion to move it along an arch-shaped path
            //fallingBlock.moveTo(this.mudman.getX(), this.mudman.getY()+10, this.mudman.getZ());
            //fallingBlock.setVelocity(0, 0.1, 0);

            // add the FallingBlockEntity to the world
            this.mudman.level.addFreshEntity(fallingBlock);
            t
                    imeUntilNextPlacement = 20;
        }
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.mudman.walk", true));
            return PlayState.CONTINUE;
        }

        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.mudman.idle", true));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller",
                0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, 0.15F, 1.0F);
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.CAT_STRAY_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.DOLPHIN_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.DOLPHIN_DEATH;
    }

    protected float getSoundVolume() {
        return 0.2F;
    }



}
package com.isaac.tutorialmod.entity.custom;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.math.Vector3f;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.minecraftforge.fml.Logging;

public class Salamander extends TamableAnimal implements IAnimatable {

    public EntityDimensions getDimensions(Pose pPose) {
        return super.getDimensions(pPose).scale(0.50f);// * (float)this.getSize());
    }
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);
    public static final Ingredient TEMPTATION_ITEM = Ingredient.of(Items.SPIDER_EYE);

    public static final ImmutableList<? extends SensorType<? extends Sensor<? super Salamander>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_ADULT, SensorType.HURT_BY, SensorType.AXOLOTL_ATTACKABLES, SensorType.AXOLOTL_TEMPTATIONS);
    protected static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.BREED_TARGET, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.PLAY_DEAD_TICKS, MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, MemoryModuleType.HAS_HUNTING_COOLDOWN, MemoryModuleType.IS_PANICKING);
    public static final double PLAYER_FIRE_PROTECTION_DETECTION_RANGE = 20.0D;
    private static final int SALAMANDER_TOTAL_AIR_SUPPLY = 6000;
    private static final int REHYDRATE_AIR_SUPPLY = 1800;
    private static final int FIRE_PROTECTION_BUFF_MAX_DURATION = 2400;

    private boolean isMovingOnLand() {
        return this.onGround && this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D && !this.isInWaterOrBubble();
    }

    private boolean isMovingInWater() {
        return this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D && this.isInWaterOrBubble();
    }
    private final Map<String, Vector3f> modelRotationValues = Maps.newHashMap();
    private static final int FIRE_PROTECTION_BUFF_BASE_DURATION = 100;

    public Salamander(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.moveControl = new Salamander.SalamanderMoveControl(this);
        this.lookControl = new Salamander.SalamanderLookControl(this, 20);
        this.maxUpStep = 1.0F;
    }
    public Map<String, Vector3f> getModelRotationValues() {
        return this.modelRotationValues;
    }

    public float getWalkTargetValue(BlockPos pPos, LevelReader pLevel) {
        return 0.0F;
    }
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
    }
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
    }
    public void baseTick() {
        int i = this.getAirSupply();
        super.baseTick();
        if (!this.isNoAi()) {
            this.handleAirSupply(i);
        }
    }
    protected void handleAirSupply(int pAirSupply) {
        if (this.isAlive() && !this.isInWaterRainOrBubble()) {
            this.setAirSupply(pAirSupply - 1);
            if (this.getAirSupply() == -20) {
                this.setAirSupply(0);
                this.hurt(DamageSource.DRY_OUT, 2.0F);
            }
        } else {
            this.setAirSupply(this.getMaxAirSupply());
        }
    }
    public void rehydrate() {
        int i = this.getAirSupply() + 1800;
        this.setAirSupply(Math.min(i, this.getMaxAirSupply()));
    }
    public int getMaxAirSupply() {
        return 6000;
    }
    public boolean checkSpawnObstruction(LevelReader pLevel) {
        return pLevel.isUnobstructed(this);
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    public boolean isPushedByFluid() {
        return false;
    }

    public MobType getMobType() {
        return MobType.WATER;
    }
    public double getMeleeAttackRangeSqr(LivingEntity pEntity) {
        return 1.5D + (double)pEntity.getBbWidth() * 2.0D;
    }

    public boolean isFood(ItemStack pStack) {
        return pStack.is(ItemTags.AXOLOTL_TEMPT_ITEMS);
    }

    public boolean canBeLeashed(Player pPlayer) {
        return true;
    }
    protected void customServerAiStep() {
        this.level.getProfiler().push("salamanderBrain");
        this.getBrain().tick((ServerLevel)this.level, this);
        this.level.getProfiler().pop();
        this.level.getProfiler().push("salamanderActivityUpdate");
        SalamanderAi.updateActivity(this);
        this.level.getProfiler().pop();
        //if (!this.isNoAi()) {
            //Optional<Integer> optional = this.getBrain().getMemory(MemoryModuleType.PLAY_DEAD_TICKS);
            //this.setPlayingDead(optional.isPresent() && optional.get() > 0);
        //}
    }
    /*
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 14.0D).add(Attributes.MOVEMENT_SPEED, 1.0D).add(Attributes.ATTACK_DAMAGE, 2.0D);
    }
     */
    public static AttributeSupplier setAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 14.0D)
                .add(Attributes.ATTACK_DAMAGE, 2.0f)
                .add(Attributes.MOVEMENT_SPEED, 1.0f)
                .build();
    }
    protected PathNavigation createNavigation(Level pLevel) {
        return new AmphibiousPathNavigation(this, pLevel);
    }
    public boolean doHurtTarget(Entity pEntity) {
        boolean flag = pEntity.hurt(DamageSource.mobAttack(this), (float)((int)this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
        if (flag) {
            this.doEnchantDamageEffects(this, pEntity);
            this.playSound(SoundEvents.AXOLOTL_ATTACK, 1.0F, 1.0F);
        }

        return flag;
    }
    protected float getStandingEyeHeight(Pose pPose, EntityDimensions pDimensions) {
        return pDimensions.height * 0.655F;
    }
    public int getMaxHeadXRot() {
        return 1;
    }

    public int getMaxHeadYRot() {
        return 1;
    }
    public static void onStopAttacking(Salamander pSalamander, LivingEntity pTarget) {
        Level level = pSalamander.level;
        if (pTarget.isDeadOrDying()) {
            DamageSource damagesource = pTarget.getLastDamageSource();
            if (damagesource != null) {
                Entity entity = damagesource.getEntity();
                if (entity != null && entity.getType() == EntityType.PLAYER) {
                    Player player = (Player)entity;
                    List<Player> list = level.getEntitiesOfClass(Player.class, pSalamander.getBoundingBox().inflate(20.0D));
                    if (list.contains(player)) {
                        pSalamander.applySupportingEffects(player);
                    }
                }
            }
        }

    }
    public void applySupportingEffects(Player pPlayer) {
        MobEffectInstance mobeffectinstance = pPlayer.getEffect(MobEffects.FIRE_RESISTANCE);
        int i = mobeffectinstance != null ? mobeffectinstance.getDuration() : 0;
        if (i < 2400) {
            i = Math.min(2400, 100 + i);
            pPlayer.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, i, 0), this);
        }

        pPlayer.removeEffect(MobEffects.DIG_SLOWDOWN);
    }
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.AXOLOTL_HURT;
    }
    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.AXOLOTL_DEATH;
    }
    @Nullable
    protected SoundEvent getAmbientSound() {
        return this.isInWater() ? SoundEvents.AXOLOTL_IDLE_WATER : SoundEvents.AXOLOTL_IDLE_AIR;
    }
    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.AXOLOTL_SPLASH;
    }

    protected SoundEvent getSwimSound() {
        return SoundEvents.AXOLOTL_SWIM;
    }

    protected Brain.Provider<Salamander> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    protected Brain<?> makeBrain(Dynamic<?> pDynamic) {
        return SalamanderAi.makeBrain(this.brainProvider().makeBrain(pDynamic));
    }
    public Brain<Salamander> getBrain() {
        return (Brain<Salamander>)super.getBrain();
    }
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }
    public void travel(Vec3 pTravelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), pTravelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(pTravelVector);
        }
    }
    public static boolean checkSalamanderSpawnRules(EntityType<? extends LivingEntity> pSalamander, ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        return pLevel.getBlockState(pPos.below()).is(BlockTags.AXOLOTLS_SPAWNABLE_ON);
    }
    class SalamanderLookControl extends SmoothSwimmingLookControl {
        public SalamanderLookControl(Salamander pSalamander, int pMaxYRotFromCenter) {
            super(pSalamander, pMaxYRotFromCenter);
        }
    }
    static class SalamanderMoveControl extends SmoothSwimmingMoveControl {
        private final Salamander salamander;

        public SalamanderMoveControl(Salamander pSalamander) {
            super(pSalamander, 85, 10, 0.1F, 0.5F, false);
            this.salamander = pSalamander;
        }
    }
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        //this.entityData.define(WAGGING, false);
        //this.entityData.define(EXCITED, false);
        //.entityData.define(SITTING, false);
        //this.entityData.define(DATA_INTERESTED_ID, false);
    }


    /*public void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(7, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this)).setAlertOthers());
        this.targetSelector.addGoal(6, new NonTameRandomTargetGoal<>(this, Turtle.class, false, Turtle.BABY_ON_LAND_SELECTOR));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, AbstractSkeleton.class, false));
    }

     */
    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        //System.out.println(event.isMoving());
        if(this.isMovingOnLand()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.salamander.walk"));
            return PlayState.CONTINUE;
        }else{
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.salamander.idle"));
        }
        return PlayState.CONTINUE;
    }
    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller",
                0, this::predicate));
    }
    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        if (this.level.isClientSide) {
            boolean flag = this.isOwnedBy(player) || this.isTame() || itemstack.is(Items.SPIDER_EYE) && !this.isTame();
            return flag ? InteractionResult.CONSUME : InteractionResult.PASS;
        } else {
            if (this.isTame()) {
                if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
                    this.heal((float)itemstack.getFoodProperties(this).getNutrition());
                    if (!player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }

                    this.gameEvent(GameEvent.EAT, this);
                    return InteractionResult.SUCCESS;
                }
                if (!(item instanceof DyeItem)) {
                    InteractionResult interactionresult = super.mobInteract(player, hand);
                    if ((!interactionresult.consumesAction() || this.isBaby()) && this.isOwnedBy(player)) {
                        //this.setOrderedToSit(!this.isOrderedToSit());
                        //setSitting(!isSitting());
                        this.jumping = false;
                        this.navigation.stop();
                        this.setTarget((LivingEntity)null);
                        return InteractionResult.SUCCESS;
                    }

                    return interactionresult;
                }

            } else if (itemstack.is(Items.SPIDER_EYE)) {
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                if (this.random.nextInt(3) == 0 && !ForgeEventFactory.onAnimalTame(this, player)) {
                    this.tame(player);
                    this.navigation.stop();
                    this.setTarget((LivingEntity)null);
                    //setSitting(true);
                    this.level.broadcastEntityEvent(this, (byte)7);
                } else {
                    this.level.broadcastEntityEvent(this, (byte)6);
                }

                return InteractionResult.SUCCESS;
            }


            return super.mobInteract(player, hand);
        }
    }
}

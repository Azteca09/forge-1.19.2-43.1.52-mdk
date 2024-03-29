package com.isaac.tutorialmod.entity.custom;

//Template

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.scores.Team;
import net.minecraftforge.event.ForgeEventFactory;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.EnumSet;
import java.util.UUID;
import java.util.function.Predicate;

import com.isaac.tutorialmod.entity.ModEntityTypes;
import software.bernie.geckolib3.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class Toydog extends TamableAnimal implements IAnimatable {
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(Toydog.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> EXCITED = SynchedEntityData.defineId(Toydog.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> WAGGING = SynchedEntityData.defineId(Toydog.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_INTERESTED_ID = SynchedEntityData.defineId(Toydog.class, EntityDataSerializers.BOOLEAN);
    public Toydog(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }
    private float interestedAngle;
    private float interestedAngleO;
    private boolean isGlowing;
    private int glowDir;
    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.3f).build();
    }
    public EntityDimensions getDimensions(Pose pPose) {
        return super.getDimensions(pPose).scale(0.70f);
    }
    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (event.isMoving() && !this.isSitting()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.toydog.walk").addAnimation("animation.toydog.wagging"));
            return PlayState.CONTINUE;
        }
        if(this.isExcited() && !event.isMoving() && !this.isSitting()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.toydog.excited").addAnimation("animation.toydog.idle"));
            return PlayState.CONTINUE;
        }
        if (this.isSitting()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.toydog.sitting"));
            return PlayState.CONTINUE;
        }

        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.toydog.idle"));
        return PlayState.CONTINUE;
    }
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller",
                0, this::predicate));
    }
    public void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(7, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(1, new ToydogAIBeg(this, 8.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this)).setAlertOthers());
        this.targetSelector.addGoal(6, new NonTameRandomTargetGoal<>(this, Turtle.class, false, Turtle.BABY_ON_LAND_SELECTOR));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, AbstractSkeleton.class, false));
    }
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(WAGGING, false);
        this.entityData.define(EXCITED, false);
        this.entityData.define(SITTING, false);
        this.entityData.define(DATA_INTERESTED_ID, false);
    }

    public Toydog getBreedOffspring(ServerLevel level, AgeableMob ageableMob) {
        Toydog toydog = new Toydog(ModEntityTypes.TOYDOG.get(), level);
        UUID uuid = this.getOwnerUUID();
        if (uuid != null) {
            toydog.setOwnerUUID(uuid);
            toydog.setTame(true);
        }
        return toydog;
    }
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        if (this.level.isClientSide) {
            boolean flag = this.isOwnedBy(player) || this.isTame() || itemstack.is(Items.STICK) && !this.isTame();
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
                        setSitting(!isSitting());
                        this.jumping = false;
                        this.navigation.stop();
                        //this.setTarget((LivingEntity)null);
                        return InteractionResult.SUCCESS;
                    }

                    return interactionresult;
                }

            } else if (itemstack.is(Items.STICK)) {
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                if (this.random.nextInt(3) == 0 && !ForgeEventFactory.onAnimalTame(this, player)) {
                    this.tame(player);
                    this.navigation.stop();
                    //this.setTarget((LivingEntity)null);
                    setSitting(true);
                    this.level.broadcastEntityEvent(this, (byte)7);
                } else {
                    this.level.broadcastEntityEvent(this, (byte)6);
                }

                return InteractionResult.SUCCESS;
            }


            return super.mobInteract(player, hand);
        }
    }
    public void setTame(boolean pTamed) {
        super.setTame(pTamed);
        if (pTamed) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0D);
            this.setHealth(20.0F);
        } else {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(8.0D);
        }

        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4.0D);
    }
    @Override
    public void readAdditionalSaveData(CompoundTag tag){
        super.readAdditionalSaveData(tag);
        setSitting(tag.getBoolean("isSitting"));
    }
    @Override
    public void addAdditionalSaveData(CompoundTag tag){
        super.addAdditionalSaveData(tag);
        tag.putBoolean("isSitting", this.isSitting());
    }
    public boolean isExcited(){
        return this.entityData.get(EXCITED);
    }
    public void setExcited(boolean excited){
        this.entityData.set(EXCITED, excited);
    }
    public boolean isSitting(){
        return this.entityData.get(SITTING);
    }
    public void setSitting(boolean sitting){
        this.entityData.set(SITTING, sitting);
        this.setOrderedToSit(sitting);
    }
    public void setIsInterested(boolean interested) {
        this.entityData.set(DATA_INTERESTED_ID, interested);
    }
    public float getHeadRollAngle(float p_30449_) {
        return Mth.lerp(p_30449_, this.interestedAngleO, this.interestedAngle) * 0.15F * (float)Math.PI;
    }
    protected SoundEvent getAmbientSound() {
        if (this.random.nextInt(3) == 0) {
            return this.isTame() && this.getHealth() < 10.0F ? SoundEvents.WOLF_WHINE : SoundEvents.WOLF_PANT;
        } else {
            return SoundEvents.WOLF_AMBIENT;
        }
    }

    public float getTailAngle() {
        return this.isTame() ? (2f*32.5f/360f + (this.getMaxHealth() - this.getHealth()) * 0.04F) * (float)Math.PI : 2f*32.5f*(float)Math.PI/360f;

        //if (this.isAngry()) {  <--Isaac we dont have isAngry
            //return 1.5393804F;
            //return 1.5393804F;
        //} else {
            //return this.isTame() ? (0.55F - (this.getMaxHealth() - this.getHealth()) * 0.02F) * (float)Math.PI : ((float)Math.PI / 5F);
        //}
    }

    @Override
    public Team getTeam(){
        return super.getTeam();
    }
    public boolean wantsToAttack(LivingEntity pTarget, LivingEntity pOwner) {
        if (!(pTarget instanceof Creeper) && !(pTarget instanceof Ghast)) {
            if (pTarget instanceof Wolf) {
                Wolf wolf = (Wolf)pTarget;
                return !wolf.isTame() || wolf.getOwner() != pOwner;
            } else if (pTarget instanceof Player && pOwner instanceof Player && !((Player)pOwner).canHarmPlayer((Player)pTarget)) {
                return false;
            } else if (pTarget instanceof AbstractHorse && ((AbstractHorse)pTarget).isTamed()) {
                return false;
            } else {
                return !(pTarget instanceof TamableAnimal) || !((TamableAnimal)pTarget).isTame();
            }
        } else {
            return false;
        }
    }
    public boolean isGlowing() { return this.isGlowing; }
    private int maxGlowDuration(){ return 20; }
    /*public void thunderHit(ServerLevel pLevel, LightningBolt pLightning) {
        super.thunderHit(pLevel, pLightning);
        this.isGlowing = true;
        this.glowDir = 0;
    }

     */
    public void tick() {
        super.tick();
        if (this.isAlive()) {
            if(this.isGlowing){
                this.glowDir += 1;
                if (this.glowDir > this.maxGlowDuration()){
                    this.isGlowing = false;
                }
            }
            this.interestedAngleO = this.interestedAngle;
            if (this.isExcited()) {
                this.interestedAngle += (1.0F - this.interestedAngle) * 0.4F;
            } else {
                this.interestedAngle += (0.0F - this.interestedAngle) * 0.4F;
            }
        }
    }
    static class ToydogAIBeg extends Goal{

        //private static final TargetingConditions ENTITY_PREDICATE = TargetingConditions.m_148353_().m_26883_(32.0);
        protected final Toydog toydog;
        @Nullable
        private Player player;
        private final Level level;
        private final float lookDistance;
        private int lookTime;
        private final TargetingConditions begTargeting;

        public ToydogAIBeg(Toydog toydog, float lookdistance) {
            this.toydog = toydog;
            this.level = toydog.level;
            this.lookDistance = lookdistance;
            this.begTargeting = TargetingConditions.forNonCombat().range((double)lookdistance);
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        public boolean canUse() {
            this.player = this.level.getNearestPlayer(this.begTargeting, this.toydog);
            return this.player == null ? false : this.playerHoldingInteresting(this.player);
        }
e
        public boolean canContinueToUse() {
            if (!this.player.isAlive()) {
                return false;
            } else if (this.toydog.distanceToSqr(this.player) > (double)(this.lookDistance * this.lookDistance)) {
                return false;
            } else {
                return this.lookTime > 0 && this.playerHoldingInteresting(this.player);
            }
        }

        public void start() {
            this.toydog.setIsInterested(true);
            this.toydog.setExcited(true);
            this.lookTime = this.adjustedTickDelay(40 + this.toydog.getRandom().nextInt(40));
        }

        public void stop() {
            this.toydog.setIsInterested(false);
            this.toydog.setExcited(false); //CORRECT LATER
            this.player = null;
        }

        public void tick() {
            this.toydog.getLookControl().setLookAt(this.player.getX(), this.player.getEyeY(), this.player.getZ(), 10.0F, (float)this.toydog.getMaxHeadXRot());
            --this.lookTime;
        }
        private boolean playerHoldingInteresting(Player player) {
            for(InteractionHand interactionhand : InteractionHand.values()) {
                ItemStack itemstack = player.getItemInHand(interactionhand);
                if (this.toydog.isTame() && itemstack.is(Items.STICK)) {
                    return true;
                }

                if (this.toydog.isFood(itemstack)) {
                    return true;
                }
            }
            return false;
        }
    }
}

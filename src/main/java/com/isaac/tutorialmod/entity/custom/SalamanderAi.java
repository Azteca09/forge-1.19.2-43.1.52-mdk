package com.isaac.tutorialmod.entity.custom;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.isaac.tutorialmod.entity.ModEntityTypes;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class SalamanderAi {
    private static final UniformInt ADULT_FOLLOW_RANGE = UniformInt.of(5, 16);
    private static final float SPEED_MULTIPLIER_WHEN_MAKING_LOVE = 0.2F;
    private static final float SPEED_MULTIPLIER_ON_LAND = 1.0F;
    private static final float SPEED_MULTIPLIER_WHEN_IDLING_IN_WATER = 0.5F;
    private static final float SPEED_MULTIPLIER_WHEN_CHASING_IN_WATER = 0.6F;
    private static final float SPEED_MULTIPLIER_WHEN_FOLLOWING_ADULT_IN_WATER = 0.6F;
    protected static Brain<?> makeBrain(Brain<Salamander> pBrain) {
        initCoreActivity(pBrain);
        initIdleActivity(pBrain);
        initFightActivity(pBrain);
        pBrain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        pBrain.setDefaultActivity(Activity.IDLE);
        pBrain.useDefaultActivity();
        return pBrain;
    }
    private static void initFightActivity(Brain<Salamander> pBrain) {
        pBrain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 0,
                ImmutableList.of(
                        new StopAttackingIfTargetInvalid<>(Salamander::onStopAttacking),
                        new SetWalkTargetFromAttackTargetIfTargetOutOfReach(SalamanderAi::getSpeedModifierChasing),
                        new MeleeAttack(20),
                        new EraseMemoryIf<Salamander>(
                                BehaviorUtils::isBreeding,
                                MemoryModuleType.ATTACK_TARGET)),
                MemoryModuleType.ATTACK_TARGET);
    }

    private static void initCoreActivity(Brain<Salamander> pBrain) {
        pBrain.addActivity(Activity.CORE, 0,
                ImmutableList.of(
                        new LookAtTargetSink(45, 90),
                        new MoveToTargetSink(),
                        new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS)));
    }

    private static void initIdleActivity(Brain<Salamander> pBrain) {
        pBrain.addActivity(
                Activity.IDLE,
                ImmutableList.of(Pair.of(0,
                        new RunSometimes<>(new SetEntityLookTarget(EntityType.PLAYER, 6.0F),
                                UniformInt.of(30, 60))),
                        Pair.of(1, new AnimalMakeLove(ModEntityTypes.SALAMANDER.get(), 0.2F)),
                        Pair.of(2, new RunOne<>(ImmutableList.of(Pair.of(new FollowTemptation(SalamanderAi::getSpeedModifier), 1),
                                Pair.of(new BabyFollowAdult<>(ADULT_FOLLOW_RANGE, SalamanderAi::getSpeedModifierFollowingAdult), 1)))),
                        Pair.of(3, new StartAttacking<>(SalamanderAi::findNearestValidAttackTarget)),
                        Pair.of(3, new TryFindWater(6, 0.25F)),
                        Pair.of(4, new GateBehavior<>(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT),
                                ImmutableSet.of(),
                                GateBehavior.OrderPolicy.ORDERED,
                                GateBehavior.RunningPolicy.TRY_ALL,
                                ImmutableList.of(
                                        Pair.of(new RandomSwim(0.5F), 2),
                                        Pair.of(new RandomStroll(0.15F, true), 2), //updated to let salamander stroll from water
                                        Pair.of(new SetWalkTargetFromLookTarget(SalamanderAi::canSetWalkTargetFromLookTarget, SalamanderAi::getSpeedModifier, 3), 3),
                                        Pair.of(new RunIf<>(Entity::isInWaterOrBubble, new DoNothing(30, 60)), 5),
                                        Pair.of(new RunIf<>(Entity::isOnGround, new DoNothing(200, 400)), 5))))));
    }
    private static boolean canSetWalkTargetFromLookTarget(LivingEntity p_182381_) {
        Level level = p_182381_.level;
        Optional<PositionTracker> optional = p_182381_.getBrain().getMemory(MemoryModuleType.LOOK_TARGET);
        if (optional.isPresent()) {
            BlockPos blockpos = optional.get().currentBlockPosition();
            return level.isWaterAt(blockpos) == p_182381_.isInWaterOrBubble();
        } else {
            return false;
        }
    }
    public static void updateActivity(Salamander pSalamander) {
        Brain<Salamander> brain = pSalamander.getBrain();
        Activity activity = brain.getActiveNonCoreActivity().orElse((Activity)null);

        brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
        if (activity == Activity.FIGHT && brain.getActiveNonCoreActivity().orElse((Activity)null) != Activity.FIGHT) {
            brain.setMemoryWithExpiry(MemoryModuleType.HAS_HUNTING_COOLDOWN, true, 2400L);
        }


    }
    private static float getSpeedModifierChasing(LivingEntity p_149289_) {
        return p_149289_.isInWaterOrBubble() ? 0.6F : SPEED_MULTIPLIER_ON_LAND;//0.15F;
    }

    private static float getSpeedModifierFollowingAdult(LivingEntity p_149295_) {
        return p_149295_.isInWaterOrBubble() ? 0.6F : SPEED_MULTIPLIER_ON_LAND;//0.15F;
    }
    private static float getSpeedModifier(LivingEntity p_149301_) {
        return p_149301_.isInWaterOrBubble() ? 0.5F : SPEED_MULTIPLIER_ON_LAND;//0.15F;
    }

    private static Optional<? extends LivingEntity> findNearestValidAttackTarget(Salamander p_149299_) {
        return BehaviorUtils.isBreeding(p_149299_) ? Optional.empty() : p_149299_.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE);
    }

    public static Ingredient getTemptations() {
        return Ingredient.of(ItemTags.AXOLOTL_TEMPT_ITEMS);
    }
}

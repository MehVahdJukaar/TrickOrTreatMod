package net.mehvahdjukaar.hauntedharvest.ai;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.mehvahdjukaar.hauntedharvest.init.Configs;
import net.mehvahdjukaar.hauntedharvest.init.ModRegistry;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.entity.schedule.ScheduleBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AI {

    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> test(float speed) {
        return ImmutableList.of(
                //priority, task
                //move where you need to move  min duration max duration
                Pair.of(0, new MoveToTargetSink(80, 120)),
                //look at stuff
                getFullLookBehavior(),
                //play with kids
                Pair.of(5, new PlayTagWithOtherKids()),
                //choose and run one of these
                Pair.of(5, new RunOne<>(
                        //might be run one if those memory status match that predicate
                        ImmutableMap.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryStatus.VALUE_ABSENT),
                        //list of possible actions
                        ImmutableList.of(
                                Pair.of(InteractWith.of(EntityType.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, speed, 2), 2),
                                Pair.of(InteractWith.of(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, speed, 2), 1),
                                Pair.of(new VillageBoundRandomStroll(speed), 1),
                                Pair.of(new SetWalkTargetFromLookTarget(speed, 2), 1),
                                Pair.of(new JumpOnBed(speed), 2),
                                Pair.of(new DoNothing(20, 40), 2)))),
                //lowest priority, update schedule
                Pair.of(99, new UpdateActivityFromSchedule()));
    }

    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getRestPackage(VillagerProfession p_24593_, float p_24594_) {
        return ImmutableList.of(
                Pair.of(2, new SetWalkTargetFromBlockMemory(MemoryModuleType.HOME, p_24594_, 1, 150, 1200)),
                Pair.of(3, new ValidateNearbyPoi(PoiType.HOME, MemoryModuleType.HOME)),
                Pair.of(3, new SleepInBed()),
                //TODO: use this for random stroll
                Pair.of(5, new RunOne<>(
                        ImmutableMap.of(MemoryModuleType.HOME, MemoryStatus.VALUE_ABSENT),
                        ImmutableList.of(
                                Pair.of(new SetClosestHomeAsWalkTarget(p_24594_), 1),
                                Pair.of(new InsideBrownianWalk(p_24594_), 4),
                                Pair.of(new GoToClosestVillage(p_24594_, 4), 2),
                                Pair.of(new DoNothing(20, 40), 2)))),
                getMinimalLookBehavior(),
                Pair.of(99, new UpdateActivityFromSchedule()));
    }

    private static Pair<Integer, Behavior<LivingEntity>> getFullLookBehavior() {
        return Pair.of(5, new RunOne<>(
                ImmutableList.of(
                        Pair.of(new SetEntityLookTarget(EntityType.CAT, 8.0F), 8),
                        Pair.of(new SetEntityLookTarget(EntityType.VILLAGER, 8.0F), 2),
                        Pair.of(new SetEntityLookTarget(EntityType.PLAYER, 8.0F), 2),
                        Pair.of(new SetEntityLookTarget(MobCategory.CREATURE, 8.0F), 1),
                        Pair.of(new SetEntityLookTarget(MobCategory.WATER_CREATURE, 8.0F), 1),
                        Pair.of(new SetEntityLookTarget(MobCategory.UNDERGROUND_WATER_CREATURE, 8.0F), 1),
                        Pair.of(new SetEntityLookTarget(MobCategory.WATER_AMBIENT, 8.0F), 1),
                        Pair.of(new SetEntityLookTarget(MobCategory.MONSTER, 8.0F), 1),
                        Pair.of(new DoNothing(30, 60), 2))));
    }

    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getEatCandyPackage(float speed) {
        return ImmutableList.of(
                Pair.of(2, new RunOne<>(
                        ImmutableList.of(
                                Pair.of(new StrollAroundPoi(MemoryModuleType.MEETING_POINT, 0.4F, 40), 2),
                                Pair.of(new SocializeAtBell(), 2))
                )),
                Pair.of(10, new ShowTradesToPlayer(400, 1600)),
                Pair.of(10, new SetLookAndInteract(EntityType.PLAYER, 4)),
                Pair.of(2, new SetWalkTargetFromBlockMemory(MemoryModuleType.MEETING_POINT, speed, 6, 100, 200)),
                Pair.of(3, new GiveGiftToHero(100)), Pair.of(3, new ValidateNearbyPoi(PoiType.MEETING, MemoryModuleType.MEETING_POINT)),
                Pair.of(3, new GateBehavior<>(
                        ImmutableMap.of(),
                        ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET),
                        GateBehavior.OrderPolicy.ORDERED,
                        GateBehavior.RunningPolicy.RUN_ONE,
                        ImmutableList.of(Pair.of(new TradeWithVillager(), 1)))),
                getFullLookBehavior(),
                Pair.of(99, new UpdateActivityFromSchedule()));
    }

    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getEatCandyPackage2(float speed) {
        return ImmutableList.of(
                Pair.of(0, new RingBell()),
                Pair.of(0, new RunOne<>(
                        ImmutableList.of(
                                Pair.of(new SetWalkTargetFromBlockMemory(MemoryModuleType.MEETING_POINT, speed * 1.5F, 2, 150, 200), 6),
                                Pair.of(new VillageBoundRandomStroll(speed * 1.5F), 2)
                        ))),
                getMinimalLookBehavior(),
                Pair.of(99, new ResetRaidStatus()));

    }

    private static Pair<Integer, Behavior<LivingEntity>> getMinimalLookBehavior() {
        return Pair.of(5,
                new RunOne<>(ImmutableList.of(
                        Pair.of(new SetEntityLookTarget(EntityType.VILLAGER, 8.0F), 2),
                        Pair.of(new SetEntityLookTarget(EntityType.PLAYER, 8.0F), 2),
                        Pair.of(new DoNothing(30, 60), 8))));
    }

    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getHidePackage(VillagerProfession p_24611_, float p_24612_) {
        return ImmutableList.of(
                Pair.of(0, new SetHiddenState(15, 3)),
                Pair.of(1, new LocateHidingPlace(32, p_24612_ * 1.25F, 2)),
                getMinimalLookBehavior());
    }


    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getCorePackage(VillagerProfession p_24586_, float p_24587_) {
        return ImmutableList.of(
                Pair.of(0, new Swim(0.8F)),
                Pair.of(0, new InteractWithDoor()),
                Pair.of(0, new LookAtTargetSink(45, 90)),
                Pair.of(0, new VillagerPanicTrigger()),
                Pair.of(0, new WakeUp()),
                Pair.of(0, new ReactToBell()),
                Pair.of(0, new SetRaidStatus()),
                Pair.of(0, new ValidateNearbyPoi(p_24586_.getJobPoiType(), MemoryModuleType.JOB_SITE)),
                Pair.of(0, new ValidateNearbyPoi(p_24586_.getJobPoiType(), MemoryModuleType.POTENTIAL_JOB_SITE)),
                Pair.of(1, new MoveToTargetSink()),
                Pair.of(2, new PoiCompetitorScan(p_24586_)),
                Pair.of(3, new LookAndFollowTradingPlayerSink(p_24587_)),
                Pair.of(5, new GoToWantedItem(p_24587_, false, 4)),
                Pair.of(6, new AcquirePoi(p_24586_.getJobPoiType(), MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, true, Optional.empty())),
                Pair.of(7, new GoToPotentialJobSite(p_24587_)),
                Pair.of(8, new YieldJobSite(p_24587_)),
                Pair.of(10, new AcquirePoi(PoiType.HOME, MemoryModuleType.HOME, false, Optional.of((byte) 14))),
                Pair.of(10, new AcquirePoi(PoiType.MEETING, MemoryModuleType.MEETING_POINT, true, Optional.of((byte) 14))),
                Pair.of(10, new AssignProfessionFromJobSite()),
                Pair.of(10, new ResetProfession()));
    }

    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getIdlePackage(VillagerProfession p_24599_, float p_24600_) {
        return ImmutableList.of(
                Pair.of(2, new RunOne<>(
                        ImmutableList.of(
                                Pair.of(InteractWith.of(EntityType.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, p_24600_, 2), 2),
                                Pair.of(new InteractWith<>(EntityType.VILLAGER, 8, AgeableMob::canBreed, AgeableMob::canBreed, MemoryModuleType.BREED_TARGET, p_24600_, 2), 1),
                                Pair.of(InteractWith.of(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, p_24600_, 2), 1),
                                Pair.of(new VillageBoundRandomStroll(p_24600_), 1), Pair.of(new SetWalkTargetFromLookTarget(p_24600_, 2), 1),
                                Pair.of(new JumpOnBed(p_24600_), 1), Pair.of(new DoNothing(30, 60), 1)))),
                Pair.of(3, new GiveGiftToHero(100)),
                Pair.of(3, new SetLookAndInteract(EntityType.PLAYER, 4)),
                Pair.of(3, new ShowTradesToPlayer(400, 1600)),
                Pair.of(3, new GateBehavior<>(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET), GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.RUN_ONE, ImmutableList.of(Pair.of(new TradeWithVillager(), 1)))),
                Pair.of(3, new GateBehavior<>(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.BREED_TARGET), GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.RUN_ONE,
                        ImmutableList.of(Pair.of(new VillagerMakeLove(), 1)))),
                getFullLookBehavior(),
                Pair.of(99, new UpdateActivityFromSchedule()));
    }

    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getTrickOrTreatPackage(float speed) {

        return ImmutableList.of(
                //finds interaction and walk target
                Pair.of(1, new FindAdultThatHasCandy(26, speed * 1.15f)),
                //consumes walk target and makes a path
                Pair.of(1, new MoveToTargetSink(210, 320)),
                //starts when close enough to interaction target
                Pair.of(0, new AskCandy(2900, 3800)),
                Pair.of(3, new GoToAttackTargetIfFurtherThan(speed * 1.25f, 10)),
                Pair.of(2, new ThrowEggs(12)),

                Pair.of(7, new LightUpPumpkin(speed)),

                Pair.of(10, new RunOne<>(
                        ImmutableMap.of(
                                MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_ABSENT,
                                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT,
                                MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_ABSENT),
                        ImmutableList.of(
                                //Pair.of(new SetClosestHomeAsWalkTarget(speed), 7),
                                Pair.of(new InsideBrownianWalk(speed), 7),
                                Pair.of(new GoToClosestVillage(speed, 4), 10),
                                Pair.of(new StrollAroundPoi(MemoryModuleType.MEETING_POINT, 1, 50), 2),
                                Pair.of(new VillageBoundRandomStroll(speed, 25, 7), 1)
                        ))),


                //Pair.of(0, new SetLookAndInteract(EntityType.VILLAGER, 16)),
                //Pair.of(1, new LocateHidingPlace(32, speed, 2)),
                //getMinimalLookBehavior(),
                //Pair.of(20, new StrollAroundPoi(MemoryModuleType.MEETING_POINT, 1, 50)),
                //Pair.of(5, new VillageBoundRandomStroll(speed, 20, 7)),
                //Pair.of(10, new LocateHidingPlace(32, speed, 6)),

                Pair.of(99, new UpdateActivityFromSchedule()));
    }


    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getHalloweenPlayPackage(float speed) {
        var old = VillagerGoalPackages.getPlayPackage(speed);
        List<Pair<Integer, ? extends Behavior<? super Villager>>> mutable = new ArrayList<>(old);

        mutable.add(Pair.of(9, new EatCandy(100, 130)));
        mutable.add(Pair.of(10, new CarvePumpkin(speed)));

        return ImmutableList.copyOf(mutable);
    }


    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getHalloweenRestPackage(VillagerProfession profession, float speed) {
        var old = VillagerGoalPackages.getRestPackage(profession, speed);
        List<Pair<Integer, ? extends Behavior<? super Villager>>> mutable = new ArrayList<>(old);

        mutable.add(Pair.of(1, new GiveCandyToBabies()));

        return ImmutableList.copyOf(mutable);
    }


    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getHalloweenIdlePackage(VillagerProfession profession, float speed) {
        var old = VillagerGoalPackages.getIdlePackage(profession, speed);
        List<Pair<Integer, ? extends Behavior<? super Villager>>> mutable = new ArrayList<>(old);

        mutable.add(Pair.of(3, new PlacePumpkin(speed)));
        mutable.add(Pair.of(3, new RemovePumpkin(speed)));

        return ImmutableList.copyOf(mutable);
    }

    public static final Schedule INITIALIZED_BABY_VILLAGER_SCHEDULE;

    static {
        ScheduleBuilder builder = new ScheduleBuilder(ModRegistry.HALLOWEEN_VILLAGER_BABY_SCHEDULE.get())
                .changeActivityAt(10, Activity.IDLE)
                .changeActivityAt(3000, Activity.PLAY)
                .changeActivityAt(6000, Activity.IDLE)
                .changeActivityAt(10000, Activity.PLAY)
                .changeActivityAt(12000, Activity.REST);

        int s = Configs.START_TIME.get();
        int e = Configs.END_TIME.get();
        if (e == 0) e = 24000;
        //TODO: need to do here cause configs aren't loaded earlier
        if (s < e) {
            int startTime = Mth.clamp(s, 12010, Mth.clamp(e, 10020, 23980));
            int endTime = Mth.clamp(e, startTime + 10, 23991);
            builder.changeActivityAt(startTime, ModRegistry.TRICK_OR_TREAT.get());
            builder.changeActivityAt(endTime, Activity.REST);
        }
        INITIALIZED_BABY_VILLAGER_SCHEDULE = builder.build();

    }


}

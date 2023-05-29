package net.mehvahdjukaar.hauntedharvest.ai;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.hauntedharvest.reg.ModTags;
import net.mehvahdjukaar.moonlight.api.events.IVillagerBrainEvent;
import net.mehvahdjukaar.moonlight.api.events.MoonlightEventsHelper;
import net.mehvahdjukaar.moonlight.api.util.VillagerAIManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashSet;
import java.util.Set;

public class HalloweenVillagerAI {

    private static final Set<Item> BABY_VILLAGER_EATABLE = new HashSet<>();

    public static void setup() {

        MoonlightEventsHelper.addListener(HalloweenVillagerAI::onVillagerBrainInitialize, IVillagerBrainEvent.class);
        //memories
        VillagerAIManager.registerMemory(MemoryModuleType.ATTACK_TARGET);
        VillagerAIManager.registerMemory(ModRegistry.PUMPKIN_POS.get());
        VillagerAIManager.registerMemory(ModRegistry.NEAREST_PUMPKIN.get());
    }


    public static void onVillagerBrainInitialize(IVillagerBrainEvent event) {
        Villager villager = event.getVillager();
        //hopefully level isnt null
        if (HauntedHarvest.isHalloweenSeason(villager.level)) {

            event.addSensor(ModRegistry.PUMPKIN_POI_SENSOR.get());

            if (villager.isBaby()) {
                event.addOrReplaceActivity(ModRegistry.TRICK_OR_TREAT.get(), getTrickOrTreatPackage(0.5f));

                event.addTaskToActivity(Activity.PLAY, Pair.of(9, new EatCandy(100, 130)));
                event.addTaskToActivity(Activity.PLAY, Pair.of(10, new CarvePumpkin(0.5f)));

                event.scheduleActivity(ModRegistry.TRICK_OR_TREAT.get(), HauntedHarvest.getSeasonManager().getTrickOrTreatStart(),
                        HauntedHarvest.getSeasonManager().getTrickOrTreatEnd());

            } else {

                event.addTaskToActivity(Activity.REST, Pair.of(1, new GiveCandyToBabies()));
                event.addTaskToActivity(Activity.IDLE, Pair.of(3, new PlacePumpkin(0.5f)));
            }
        } else {
            if (!event.getVillager().isBaby()) {
                event.addTaskToActivity(Activity.IDLE, Pair.of(3, new RemovePumpkin(0.5f)));
            }
        }
    }


    private static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> getTrickOrTreatPackage(float speed) {

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
                                Pair.of(InsideBrownianWalk.create(speed), 7),
                                Pair.of(GoToClosestVillage.create(speed, 4), 10),
                                Pair.of(StrollAroundPoi.create(MemoryModuleType.MEETING_POINT, 1, 50), 2),
                                Pair.of(VillageBoundRandomStroll.create(speed, 25, 7), 1)
                        ))),


                //Pair.of(0, new SetLookAndInteract(EntityType.VILLAGER, 16)),
                //Pair.of(1, new LocateHidingPlace(32, speed, 2)),
                //getMinimalLookBehavior(),
                //Pair.of(20, new StrollAroundPoi(MemoryModuleType.MEETING_POINT, 1, 50)),
                //Pair.of(5, new VillageBoundRandomStroll(speed, 20, 7)),
                //Pair.of(10, new LocateHidingPlace(32, speed, 6)),

                Pair.of(99, UpdateActivityFromSchedule.create()));
    }


    public static boolean isTrickOrTreater(LivingEntity entity) {
        return entity.isBaby() && entity.getMainHandItem().is(Items.BUNDLE);
    }

    public static boolean isCandyOrApple(ItemStack stack) {
        return BABY_VILLAGER_EATABLE.contains(stack.getItem());
    }

    public static boolean hasCandyOrApple(SimpleContainer inventory) {
        return inventory.hasAnyOf(BABY_VILLAGER_EATABLE);
    }

    public static void refreshCandies() {
        BABY_VILLAGER_EATABLE.clear();
        Set<Item> temp = new HashSet<>();
        for (var p : BuiltInRegistries.ITEM.getTagOrEmpty(ModTags.SWEETS)) {
            temp.add(p.value());
        }
        temp.add(ModRegistry.GRIM_APPLE.get());
        temp.add(ModRegistry.ROTTEN_APPLE.get());
        for (Item i : temp) {
            if (i != Items.AIR) {
                BABY_VILLAGER_EATABLE.add(i);
            }
        }
    }
}

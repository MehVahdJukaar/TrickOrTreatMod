package net.mehvahdjukaar.hauntedharvest.reg;

import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.ai.PumpkinPoiSensor;
import net.mehvahdjukaar.hauntedharvest.blocks.*;
import net.mehvahdjukaar.hauntedharvest.configs.ModConfigs;
import net.mehvahdjukaar.hauntedharvest.entity.SplatteredEggEntity;
import net.mehvahdjukaar.hauntedharvest.items.GrimAppleItem;
import net.mehvahdjukaar.hauntedharvest.items.ModCarvedPumpkinItem;
import net.mehvahdjukaar.hauntedharvest.items.PaperBagItem;
import net.mehvahdjukaar.hauntedharvest.items.crafting.ModCarvedPumpkinRecipe;
import net.mehvahdjukaar.moonlight.api.platform.PlatformHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings({"unused"})
public class ModRegistry {

    public static void init() {
    }

    private static CreativeModeTab getTab(CreativeModeTab tab, String name) {
        return tab;
    }


    public static final Supplier<Activity> EAT_CANDY = regActivity("eat_candy");
    public static final Supplier<Activity> TRICK_OR_TREAT = regActivity("trick_or_treat");

    //do not use
    public static final Supplier<Schedule> HALLOWEEN_VILLAGER_BABY_SCHEDULE =
            RegHelper.registerSchedule(HauntedHarvest.res("villager_baby_halloween"), Schedule::new);


    public static final Supplier<MemoryModuleType<GlobalPos>> PUMPKIN_POS =
            RegHelper.registerMemoryModule(HauntedHarvest.res("pumpkin_pos"), () ->
                    new MemoryModuleType<>(Optional.of(GlobalPos.CODEC)));

    public static final Supplier<MemoryModuleType<GlobalPos>> NEAREST_PUMPKIN =
            RegHelper.registerMemoryModule(HauntedHarvest.res("nearest_pumpkin"), () ->
                    new MemoryModuleType<>(Optional.empty()));


    public static final Supplier<SensorType<PumpkinPoiSensor>> PUMPKIN_POI_SENSOR =
            RegHelper.registerSensor(HauntedHarvest.res("pumpkin_poi"), () ->
                    new SensorType<>(PumpkinPoiSensor::new));


    public static final Supplier<EntityType<SplatteredEggEntity>> SPLATTERED_EGG_ENTITY = RegHelper.registerEntityType(
            HauntedHarvest.res("splattered_egg"), () -> (
                    EntityType.Builder.<SplatteredEggEntity>of(SplatteredEggEntity::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE))
                    .build("splattered_egg"));


    public static final Supplier<SimpleParticleType> SPOOKED_PARTICLE = RegHelper.registerParticle(
            HauntedHarvest.res("spooked"));


    public static final Supplier<Item> ROTTEN_APPLE = RegHelper.registerItem(
            HauntedHarvest.res("rotten_apple"), () ->
                    new Item(new Item.Properties().tab(CreativeModeTab.TAB_FOOD).food(ModFood.ROTTEN_APPLE)));

    public static final Supplier<Item> DEATH_APPLE = RegHelper.registerItem(
            HauntedHarvest.res("grim_apple"), () ->
                    new GrimAppleItem(new Item.Properties().tab(CreativeModeTab.TAB_FOOD).rarity(Rarity.RARE).food(ModFood.DEATH_APPLE)));

    public static final CreativeModeTab MOD_TAB = !ModConfigs.MOD_TAB.get() ? null :
            PlatformHelper.createModTab(HauntedHarvest.res(HauntedHarvest.MOD_ID),
                    () -> DEATH_APPLE.get().getDefaultInstance(), false);


    public static final Supplier<RecipeSerializer<ModCarvedPumpkinRecipe>> CARVED_PUMPKIN_RECIPE = regRecipe(
            "carved_pumpkin", ModCarvedPumpkinRecipe::new);


    public static final Supplier<Block> CORN_BASE = regBlock("corn_base", () -> new CornBaseBlock(
            BlockBehaviour.Properties.copy(Blocks.WHEAT)
                    .randomTicks()
                    .offsetType(BlockBehaviour.OffsetType.NONE)
                    .instabreak()
                    .sound(SoundType.CROP))
    );

    public static final Supplier<Block> CORN_MIDDLE = regBlock("corn_middle", () -> new CornMiddleBlock(
            BlockBehaviour.Properties.copy(CORN_BASE.get()))
    );

    public static final Supplier<Block> CORN_TOP = regBlock("corn_top", () -> new CornTobBlock(
            BlockBehaviour.Properties.copy(CORN_BASE.get()))
    );

    public static final Supplier<Item> COB_ITEM = regItem("corn", () -> new Item(
            (new Item.Properties()).tab(CreativeModeTab.TAB_MISC)));

    public static final Supplier<Item> COOKED_COB = regItem("corn_on_the_cob", () -> new Item(
            (new Item.Properties()).tab(CreativeModeTab.TAB_FOOD).stacksTo(16).food(ModFood.CORN_ON_THE_COB)));

    public static final Supplier<Item> POP_CORN = regItem("popcorn", () -> new Item(
            (new Item.Properties()).tab(CreativeModeTab.TAB_FOOD).food(ModFood.POPCORN)));

    public static final Supplier<Item> CANDY_CORN = regItem("candy_corn", () -> new CandyCornItem(
            (new Item.Properties()).tab(CreativeModeTab.TAB_FOOD).food(ModFood.CANDY_CORN)));


    public static final Supplier<Block> PAPER_BAG = regBlock("paper_bag", () -> new PaperBagBlock(
            BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).strength(0.5f, 0.5f)));

    public static final Supplier<Item> PAPER_BAG_ITEM = regItem("paper_bag", () -> new PaperBagItem(
            PAPER_BAG.get(), (new Item.Properties()).tab(CreativeModeTab.TAB_MISC)));


    public static final Supplier<Block> CANDY_BAG = regBlock("candy_bag", () -> new CandyBagBlock(
            BlockBehaviour.Properties.copy(PAPER_BAG.get())));

    public static final Supplier<BlockEntityType<CandyBagTile>> CANDY_BAG_TILE = regTile(
            "candy_bag", () -> PlatformHelper.newBlockEntityType(
                    CandyBagTile::new, CANDY_BAG.get()));

    public static final Supplier<Item> CORN_SEEDS = regItem("kernels", () -> new ItemNameBlockItem(CORN_BASE.get(),
            (new Item.Properties()).tab(CreativeModeTab.TAB_MISC)));


    public static final Supplier<Block> MOD_CARVED_PUMPKIN = regBlock("carved_pumpkin",
            () -> new ModCarvedPumpkinBlock(BlockBehaviour.Properties.copy(Blocks.CARVED_PUMPKIN)));

    public static final Supplier<Item> MOD_CARVED_PUMPKIN_ITEM = regItem("carved_pumpkin",
            () -> new ModCarvedPumpkinItem(MOD_CARVED_PUMPKIN.get(), new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)));


    public static final Supplier<Block> MOD_JACK_O_LANTERN = regBlock("jack_o_lantern",
            () -> new ModCarvedPumpkinBlock(BlockBehaviour.Properties.copy(Blocks.CARVED_PUMPKIN)
                    .lightLevel(s -> 15)));

    public static final Supplier<Item> MOD_JACK_O_LANTERN_ITEM = regItem("jack_o_lantern",
            () -> new ModCarvedPumpkinItem(MOD_JACK_O_LANTERN.get(), new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)));


    public static final Supplier<BlockEntityType<ModCarvedPumpkinBlockTile>> MOD_CARVED_PUMPKIN_TILE =
            regTile("carved_pumpkin", () ->
                    PlatformHelper.newBlockEntityType(ModCarvedPumpkinBlockTile::new,
                            MOD_CARVED_PUMPKIN.get(), MOD_JACK_O_LANTERN.get()));



    public static <T extends Item> Supplier<T> regItem(String name, Supplier<T> sup) {
        return RegHelper.registerItem(HauntedHarvest.res(name), sup);
    }

    public static <T extends BlockEntityType<E>, E extends BlockEntity> Supplier<T> regTile(String name, Supplier<T> sup) {
        return RegHelper.registerBlockEntityType(HauntedHarvest.res(name), sup);
    }

    public static <T extends Block> Supplier<T> regBlock(String name, Supplier<T> sup) {
        return RegHelper.registerBlock(HauntedHarvest.res(name), sup);
    }

    public static <T extends Block> Supplier<T> regWithItem(String name, Supplier<T> blockFactory, CreativeModeTab tab) {
        return regWithItem(name, blockFactory, new Item.Properties().tab(getTab(tab, name)));
    }

    public static <T extends Block> Supplier<T> regWithItem(String name, Supplier<T> blockFactory, Item.Properties properties) {
        Supplier<T> block = regBlock(name, blockFactory);
        regBlockItem(name, block, properties);
        return block;
    }

    public static Supplier<BlockItem> regBlockItem(String name, Supplier<? extends Block> blockSup, Item.Properties properties) {
        return RegHelper.registerItem(HauntedHarvest.res(name), () -> new BlockItem(blockSup.get(), properties));
    }

    private static <T extends Recipe<?>> Supplier<RecipeSerializer<T>> regRecipe(String name, Function<ResourceLocation, T> factory) {
        return RegHelper.registerRecipeSerializer(HauntedHarvest.res(name), () -> new SimpleRecipeSerializer<>(factory));
    }

    private static Supplier<Activity> regActivity(String name) {
        return RegHelper.registerActivity(HauntedHarvest.res(name), () -> new Activity(name));
    }

}

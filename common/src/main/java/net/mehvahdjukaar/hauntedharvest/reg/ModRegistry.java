package net.mehvahdjukaar.hauntedharvest.reg;

import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.ai.PumpkinPoiSensor;
import net.mehvahdjukaar.hauntedharvest.blocks.*;
import net.mehvahdjukaar.hauntedharvest.configs.RegistryConfigs;
import net.mehvahdjukaar.hauntedharvest.entity.SplatteredEggEntity;
import net.mehvahdjukaar.hauntedharvest.items.GrimAppleItem;
import net.mehvahdjukaar.hauntedharvest.items.ModCarvedPumpkinItem;
import net.mehvahdjukaar.hauntedharvest.items.PaperBagItem;
import net.mehvahdjukaar.hauntedharvest.items.crafting.ModCarvedPumpkinRecipe;
import net.mehvahdjukaar.hauntedharvest.worldgen.FarmFieldFeature;
import net.mehvahdjukaar.hauntedharvest.worldgen.ProcessFarmProcessor;
import net.mehvahdjukaar.hauntedharvest.worldgen.SeedBasedFeaturePoolElement;
import net.mehvahdjukaar.moonlight.api.platform.PlatformHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
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
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings({"unused"})
public class ModRegistry {

    public static void init() {
    }

    @Contract
    @NotNull
    public static CreativeModeTab getTab(CreativeModeTab g, String regName) {
        if (RegistryConfigs.isEnabled(regName)) {
            return MOD_TAB == null ? g : MOD_TAB;
        }
        return null;
    }


    public static final Supplier<Activity> EAT_CANDY = regActivity("eat_candy");
    public static final Supplier<Activity> TRICK_OR_TREAT = regActivity("trick_or_treat");


    public static final Supplier<StructureProcessorType<ProcessFarmProcessor>> FARM_PROCESSOR =
            RegHelper.register(HauntedHarvest.res("process_farm"), () ->
                    () -> ProcessFarmProcessor.CODEC, Registry.STRUCTURE_PROCESSOR);

    public static final Supplier<StructurePoolElementType<SeedBasedFeaturePoolElement>> RANDOM_FEATURE_POOL =
            RegHelper.register(HauntedHarvest.res("random_feature_pool_element"), () ->
                    () -> SeedBasedFeaturePoolElement.CODEC, Registry.STRUCTURE_POOL_ELEMENT);

    public static final Supplier<Feature<FarmFieldFeature.Config>> FARM_FIELD_FEATURE =
            RegHelper.registerFeature(HauntedHarvest.res("farm_field"), () ->
                    new FarmFieldFeature(FarmFieldFeature.Config.CODEC));

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


    public static final String SPLATTERED_EGG_NAME = "splattered_egg";
    public static final Supplier<EntityType<SplatteredEggEntity>> SPLATTERED_EGG_ENTITY = RegHelper.registerEntityType(
            HauntedHarvest.res(SPLATTERED_EGG_NAME), SplatteredEggEntity::new, MobCategory.MISC,
            0.5F, 0.5F, 10, Integer.MAX_VALUE);


    public static final Supplier<SimpleParticleType> SPOOKED_PARTICLE = RegHelper.registerParticle(
            HauntedHarvest.res("spooked"));


    public static final String GRIM_APPLE_NAME = "grim_apple";
    public static final Supplier<Item> DEATH_APPLE = regItem(GRIM_APPLE_NAME, () ->
            new GrimAppleItem(new Item.Properties()
                    .rarity(Rarity.RARE).food(ModFood.DEATH_APPLE)
                    .tab(getTab(CreativeModeTab.TAB_FOOD, GRIM_APPLE_NAME))));

    public static final String ROTTEN_APPLE_NAME = "rotten_apple";
    public static final Supplier<Item> ROTTEN_APPLE = regItem(ROTTEN_APPLE_NAME, () ->
            new Item(new Item.Properties()
                    .food(ModFood.ROTTEN_APPLE)
                    .tab(getTab(CreativeModeTab.TAB_FOOD, GRIM_APPLE_NAME))));

    public static final CreativeModeTab MOD_TAB = !RegistryConfigs.CREATIVE_TAB.get() ? null :
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

    public static final Supplier<Block> CORN_TOP = regBlock("corn_top", () -> new CornTopBlock(
            BlockBehaviour.Properties.copy(CORN_BASE.get()))
    );

    public static final String CORN_NAME = "corn";
    public static final Supplier<Item> COB_ITEM = regItem(CORN_NAME, () -> new Item(
            new Item.Properties()
                    .tab(getTab(CreativeModeTab.TAB_MISC, CORN_NAME))));

    public static final Supplier<Item> COOKED_COB = regItem("corn_on_the_cob", () -> new Item(
            new Item.Properties()
                    .stacksTo(16).food(ModFood.CORN_ON_THE_COB)
                    .tab(getTab(CreativeModeTab.TAB_FOOD, CORN_NAME))));

    public static final Supplier<Item> CORN_SEEDS = regItem("kernels", () -> new ItemNameBlockItem(CORN_BASE.get(),
            new Item.Properties()
                    .tab(getTab(CreativeModeTab.TAB_MISC, CORN_NAME))));


    public static final String POPCORN_NAME = "popcorn";
    public static final Supplier<Item> POP_CORN = regItem(POPCORN_NAME, () -> new Item(
            new Item.Properties()
                    .food(ModFood.POPCORN)
                    .tab(getTab(CreativeModeTab.TAB_FOOD, POPCORN_NAME))));

    public static final String CANDY_CORN_NAME = "candy_corn";
    public static final Supplier<Item> CANDY_CORN = regItem(CANDY_CORN_NAME, () -> new CandyCornItem(
            new Item.Properties()
                    .food(ModFood.CANDY_CORN)
                    .tab(getTab(CreativeModeTab.TAB_FOOD, CANDY_CORN_NAME))));


    public static final Supplier<Block> PAPER_BAG = regBlock("paper_bag", () -> new PaperBagBlock(
            BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).strength(0.5f, 0.5f)));

    public static final String PAPER_BAG_NAME = "paper_bag";
    public static final Supplier<Item> PAPER_BAG_ITEM = regItem(PAPER_BAG_NAME, () -> new PaperBagItem(
            PAPER_BAG.get(), new Item.Properties()
            .tab(getTab(CreativeModeTab.TAB_MISC, PAPER_BAG_NAME))));


    public static final Supplier<Block> CANDY_BAG = regBlock("candy_bag", () -> new CandyBagBlock(
            BlockBehaviour.Properties.copy(PAPER_BAG.get())));

    public static final Supplier<BlockEntityType<CandyBagTile>> CANDY_BAG_TILE = regTile(
            "candy_bag", () -> PlatformHelper.newBlockEntityType(
                    CandyBagTile::new, CANDY_BAG.get()));


    public static final Supplier<Block> MOD_CARVED_PUMPKIN = regBlock("carved_pumpkin",
            () -> new ModCarvedPumpkinBlock(BlockBehaviour.Properties.copy(Blocks.CARVED_PUMPKIN)));

    public static final String CARVED_PUMPKIN_NAME = "carved_pumpkin";
    public static final Supplier<Item> MOD_CARVED_PUMPKIN_ITEM = regItem(CARVED_PUMPKIN_NAME,
            () -> new ModCarvedPumpkinItem(MOD_CARVED_PUMPKIN.get(), new Item.Properties()
                    .tab(getTab(CreativeModeTab.TAB_DECORATIONS, CARVED_PUMPKIN_NAME))));


    public static final Supplier<Block> MOD_JACK_O_LANTERN = regBlock("jack_o_lantern",
            () -> new ModCarvedPumpkinBlock(BlockBehaviour.Properties.copy(Blocks.CARVED_PUMPKIN)
                    .lightLevel(s -> 15)));

    public static final Supplier<Item> MOD_JACK_O_LANTERN_ITEM = regItem("jack_o_lantern",
            () -> new ModCarvedPumpkinItem(MOD_JACK_O_LANTERN.get(), new Item.Properties()
                    .tab(getTab(CreativeModeTab.TAB_DECORATIONS, CARVED_PUMPKIN_NAME))));


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

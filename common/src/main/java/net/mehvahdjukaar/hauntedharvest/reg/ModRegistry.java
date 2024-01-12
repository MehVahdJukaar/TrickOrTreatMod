package net.mehvahdjukaar.hauntedharvest.reg;

import net.mehvahdjukaar.hauntedharvest.ai.PumpkinPoiSensor;
import net.mehvahdjukaar.hauntedharvest.blocks.*;
import net.mehvahdjukaar.hauntedharvest.configs.CommonConfigs;
import net.mehvahdjukaar.hauntedharvest.entity.SplatteredEggEntity;
import net.mehvahdjukaar.hauntedharvest.items.GrimAppleItem;
import net.mehvahdjukaar.hauntedharvest.items.ModCarvedPumpkinItem;
import net.mehvahdjukaar.hauntedharvest.items.PaperBagItem;
import net.mehvahdjukaar.hauntedharvest.items.crafting.ModCarvedPumpkinRecipe;
import net.mehvahdjukaar.hauntedharvest.worldgen.AbandonedFarmStructure;
import net.mehvahdjukaar.hauntedharvest.worldgen.FarmFieldFeature;
import net.mehvahdjukaar.hauntedharvest.worldgen.ProcessFarmProcessor;
import net.mehvahdjukaar.hauntedharvest.worldgen.SeedBasedFeaturePoolElement;
import net.mehvahdjukaar.moonlight.api.misc.ModItemListing;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.material.PushReaction;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static net.mehvahdjukaar.hauntedharvest.HauntedHarvest.res;

@SuppressWarnings({"unused"})
public class ModRegistry {

    public static void init() {
        RegHelper.registerWanderingTraderTrades(0, ModRegistry::addTrades);
    }

    private static void addTrades(List<VillagerTrades.ItemListing> itemListings) {
        if (CommonConfigs.CORN_ENABLED.get()) {
            itemListings.add(new ModItemListing(2, ModRegistry.KERNELS.get().getDefaultInstance(), 12, 1));
        }
    }

    public static final Supplier<Activity> EAT_CANDY = RegHelper.registerActivity(res("eat_candy"));
    public static final Supplier<Activity> TRICK_OR_TREAT = RegHelper.registerActivity(res("trick_or_treat"));

    //worldgen

    public static final Supplier<StructureType<AbandonedFarmStructure>> FARM = RegHelper.registerStructure(
            res("abandoned_farm"), AbandonedFarmStructure.Type::new);


    public static final Supplier<StructureProcessorType<ProcessFarmProcessor>> FARM_PROCESSOR =
            RegHelper.register(res("process_farm"), () ->
                    () -> ProcessFarmProcessor.CODEC, Registries.STRUCTURE_PROCESSOR);

    public static final Supplier<StructurePoolElementType<SeedBasedFeaturePoolElement>> RANDOM_FEATURE_POOL =
            RegHelper.register(res("random_feature_pool_element"), () ->
                    () -> SeedBasedFeaturePoolElement.CODEC, Registries.STRUCTURE_POOL_ELEMENT);

    public static final Supplier<Feature<FarmFieldFeature.Config>> FARM_FIELD_FEATURE =
            RegHelper.registerFeature(res("farm_field"), () ->
                    new FarmFieldFeature(FarmFieldFeature.Config.CODEC));

    //ai

    //do not use
    public static final Supplier<Schedule> HALLOWEEN_VILLAGER_BABY_SCHEDULE =
            RegHelper.registerSchedule(res("villager_baby_halloween"), Schedule::new);

    public static final Supplier<MemoryModuleType<GlobalPos>> PUMPKIN_POS =
            RegHelper.registerMemoryModule(res("pumpkin_pos"), () ->
                    new MemoryModuleType<>(Optional.of(GlobalPos.CODEC)));

    public static final Supplier<MemoryModuleType<GlobalPos>> NEAREST_PUMPKIN =
            RegHelper.registerMemoryModule(res("nearest_pumpkin"), () ->
                    new MemoryModuleType<>(Optional.empty()));


    public static final Supplier<SensorType<PumpkinPoiSensor>> PUMPKIN_POI_SENSOR =
            RegHelper.registerSensor(res("pumpkin_poi"), () ->
                    new SensorType<>(PumpkinPoiSensor::new));

    //recipes

    public static final Supplier<RecipeSerializer<ModCarvedPumpkinRecipe>> CARVED_PUMPKIN_RECIPE = RegHelper.registerSpecialRecipe(
            res("carved_pumpkin"), ModCarvedPumpkinRecipe::new);

    //particles

    public static final Supplier<SimpleParticleType> SPOOKED_PARTICLE = RegHelper.registerParticle(
            res("spooked"));

    //items

    public static final String SPLATTERED_EGG_NAME = "splattered_egg";
    public static final Supplier<EntityType<SplatteredEggEntity>> SPLATTERED_EGG_ENTITY = RegHelper.registerEntityType(
            res(SPLATTERED_EGG_NAME), SplatteredEggEntity::new, MobCategory.MISC,
            0.5F, 0.5F, 10, Integer.MAX_VALUE);

    public static final String GRIM_APPLE_NAME = "grim_apple";
    public static final Supplier<Item> GRIM_APPLE = regItem(GRIM_APPLE_NAME, () ->
            new GrimAppleItem(new Item.Properties()
                    .rarity(Rarity.RARE).food(ModFoods.DEATH_APPLE)));

    public static final String ROTTEN_APPLE_NAME = "rotten_apple";
    public static final Supplier<Item> ROTTEN_APPLE = regItem(ROTTEN_APPLE_NAME, () ->
            new Item(new Item.Properties()
                    .food(ModFoods.ROTTEN_APPLE)));


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

    //pot
    public static final Supplier<Block> CORN_POT = regBlock("potted_corn", () -> PlatHelper.newFlowerPot(
            () -> (FlowerPotBlock) Blocks.FLOWER_POT, CORN_BASE, BlockBehaviour.Properties.copy(Blocks.FLOWER_POT)));

    public static final String CORN_NAME = "corn";
    public static final Supplier<Item> COB_ITEM = regItem(CORN_NAME, () -> new Item(
            new Item.Properties()));

    public static final Supplier<Item> COOKED_COB = regItem("corn_on_the_cob", () -> new Item(
            new Item.Properties()
                    .stacksTo(16).food(ModFoods.CORN_ON_THE_COB)));

    public static final Supplier<Item> KERNELS = regItem("kernels", () -> new ItemNameBlockItem(CORN_BASE.get(),
            new Item.Properties()));

    public static final String POPCORN_NAME = "popcorn";
    public static final Supplier<Item> POP_CORN = regItem(POPCORN_NAME, () -> new Item(
            new Item.Properties()
                    .food(ModFoods.POPCORN)));

    public static final String CANDY_CORN_NAME = "candy_corn";
    public static final Supplier<Item> CANDY_CORN = regItem(CANDY_CORN_NAME, () -> new CandyCornItem(
            new Item.Properties()
                    .food(ModFoods.CANDY_CORN)));


    public static final Supplier<Block> PAPER_BAG = regBlock("paper_bag", () -> new PaperBagBlock(
            BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).strength(0.5f, 0.5f)));

    public static final String PAPER_BAG_NAME = "paper_bag";
    public static final Supplier<Item> PAPER_BAG_ITEM = regItem(PAPER_BAG_NAME, () -> new PaperBagItem(
            PAPER_BAG.get(), new Item.Properties()));


    public static final Supplier<Block> CANDY_BAG = regBlock("candy_bag", () -> new CandyBagBlock(
            BlockBehaviour.Properties.copy(PAPER_BAG.get())
                    .pushReaction(PushReaction.DESTROY)));

    public static final Supplier<BlockEntityType<CandyBagTile>> CANDY_BAG_TILE = regTile(
            "candy_bag", () -> PlatHelper.newBlockEntityType(
                    CandyBagTile::new, CANDY_BAG.get()));


    public static final String CARVED_PUMPKIN_NAME = "carved_pumpkin";
    public static final Supplier<ModCarvedPumpkinBlock> CARVED_PUMPKIN = regPumpkin(CARVED_PUMPKIN_NAME,
            () -> new ModCarvedPumpkinBlock(BlockBehaviour.Properties.copy(Blocks.CARVED_PUMPKIN), PumpkinType.NORMAL));


    public static final Supplier<ModCarvedPumpkinBlock> JACK_O_LANTERN = regPumpkin("jack_o_lantern",
            () -> new ModCarvedPumpkinBlock(BlockBehaviour.Properties.copy(Blocks.CARVED_PUMPKIN)
                    .lightLevel(s -> 15), PumpkinType.JACK));


    public static final Supplier<BlockEntityType<ModCarvedPumpkinBlockTile>> MOD_CARVED_PUMPKIN_TILE =
            regTile("carved_pumpkin", () ->
                    PlatHelper.newBlockEntityType(ModCarvedPumpkinBlockTile::new,
                            PumpkinType.getTypes().stream().map(PumpkinType::getPumpkin).toArray(Block[]::new)));

    public static Supplier<ModCarvedPumpkinBlock> regPumpkin(String name, Supplier<ModCarvedPumpkinBlock> supplier) {
        var block = regBlock(name, supplier);
        var item = regItem(name, () -> new ModCarvedPumpkinItem(block.get(), new Item.Properties()));
        return block;
    }

    public static <T extends Item> Supplier<T> regItem(String name, Supplier<T> sup) {
        return RegHelper.registerItem(res(name), sup);
    }

    public static <T extends BlockEntityType<E>, E extends BlockEntity> Supplier<T> regTile(String name, Supplier<T> sup) {
        return RegHelper.registerBlockEntityType(res(name), sup);
    }

    public static <T extends Block> Supplier<T> regBlock(String name, Supplier<T> sup) {
        return RegHelper.registerBlock(res(name), sup);
    }

    public static <T extends Block> Supplier<T> regWithItem(String name, Supplier<T> blockFactory, Item.Properties properties) {
        Supplier<T> block = regBlock(name, blockFactory);
        regBlockItem(name, block, properties);
        return block;
    }

    public static Supplier<BlockItem> regBlockItem(String name, Supplier<? extends Block> blockSup, Item.Properties properties) {
        return RegHelper.registerItem(res(name), () -> new BlockItem(blockSup.get(), properties));
    }

}

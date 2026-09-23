package net.mehvahdjukaar.hauntedharvest.reg;

import net.mehvahdjukaar.hauntedharvest.ai.PumpkinPoiSensor;
import net.mehvahdjukaar.hauntedharvest.blocks.*;
import net.mehvahdjukaar.hauntedharvest.entity.SplatteredEggEntity;
import net.mehvahdjukaar.hauntedharvest.items.GrimAppleItem;
import net.mehvahdjukaar.hauntedharvest.items.ModCarvedPumpkinItem;
import net.mehvahdjukaar.hauntedharvest.items.PaperBagItem;
import net.mehvahdjukaar.hauntedharvest.items.components.PumpkinCarvingData;
import net.mehvahdjukaar.hauntedharvest.items.crafting.ModCarvedPumpkinRecipe;
import net.mehvahdjukaar.hauntedharvest.worldgen.AbandonedFarmStructure;
import net.mehvahdjukaar.hauntedharvest.worldgen.FarmFieldFeature;
import net.mehvahdjukaar.hauntedharvest.worldgen.ProcessFarmProcessor;
import net.mehvahdjukaar.hauntedharvest.worldgen.RandomFeaturePoolElement;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.mehvahdjukaar.hauntedharvest.HauntedHarvest.res;

@SuppressWarnings({"unused"})
public class ModRegistry {

    public static void init() {
        RegHelper.addExtraBEBlockStatesRegistration(event -> {
            for (var t : PumpkinType.REGISTRY) {
                event.addBlocks(MOD_CARVED_PUMPKIN_TILE.get(), t.getPumpkin());
            }
        });
    }


    public static final Supplier<Activity> EAT_CANDY = RegHelper.registerActivity(res("eat_candy"));
    public static final Supplier<Activity> TRICK_OR_TREAT = RegHelper.registerActivity(res("trick_or_treat"));

    //worldgen

    public static final Supplier<StructureType<AbandonedFarmStructure>> FARM = RegHelper.registerStructure(
            res("abandoned_farm"), AbandonedFarmStructure.Type::new);


    public static final Supplier<StructureProcessorType<ProcessFarmProcessor>> FARM_PROCESSOR =
            RegHelper.registerStructureProcessor(res("process_farm"), ProcessFarmProcessor.CODEC);

    public static final Supplier<StructurePoolElementType<RandomFeaturePoolElement>> RANDOM_FEATURE_POOL =
            RegHelper.registerStructurePoolElement(res("random_feature_pool_element"), RandomFeaturePoolElement.CODEC);

    public static final Supplier<Feature<FarmFieldFeature.Config>> FARM_FIELD_FEATURE =
            RegHelper.registerFeature(res("farm_field"), () ->
                    new FarmFieldFeature(FarmFieldFeature.Config.CODEC));

    //ai

    public static final Supplier<MemoryModuleType<GlobalPos>> PUMPKIN_POS =
            RegHelper.registerMemoryModule(res("pumpkin_pos"), GlobalPos.CODEC);

    public static final Supplier<MemoryModuleType<GlobalPos>> NEAREST_PUMPKIN =
            RegHelper.registerMemoryModule(res("nearest_pumpkin"), () ->
                    new MemoryModuleType<GlobalPos>(Optional.empty()));


    public static final Supplier<SensorType<PumpkinPoiSensor>> PUMPKIN_POI_SENSOR =
            RegHelper.registerSensor(res("pumpkin_poi"), () ->
                    new SensorType<>(PumpkinPoiSensor::new));

    //recipes

    public static final Supplier<RecipeSerializer<ModCarvedPumpkinRecipe>> CARVED_PUMPKIN_RECIPE = RegHelper.registerSpecialRecipe(
            res("carved_pumpkin"), ModCarvedPumpkinRecipe::new);

    //particles

    public static final Supplier<SimpleParticleType> SPOOKED_PARTICLE = RegHelper.registerParticle(
            res("spooked"));

    //data comp
    public static final Supplier<DataComponentType<PumpkinCarvingData>> PUMPKIN_CARVING = RegHelper.registerDataComponent(
            res("pumpkin_carving"), () -> DataComponentType.<PumpkinCarvingData>builder()
                    .networkSynchronized(PumpkinCarvingData.STREAM_CODEC)
                    .persistent(PumpkinCarvingData.CODEC)
                    .cacheEncoding()
                    .build());

    //items
    public static final String SPLATTERED_EGG_NAME = "splattered_egg";
    public static final Supplier<EntityType<SplatteredEggEntity>> SPLATTERED_EGG_ENTITY = RegHelper.registerEntityType(
            res(SPLATTERED_EGG_NAME), EntityType.Builder.<SplatteredEggEntity>of(SplatteredEggEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE));

    public static final String GRIM_APPLE_NAME = "grim_apple";
    public static final Supplier<Item> GRIM_APPLE = regItem(GRIM_APPLE_NAME, GrimAppleItem::new,
            new Item.Properties().rarity(Rarity.RARE).food(ModFoods.DEATH_APPLE, ModFoods.DEATH_APPLE_CONSUMABLE));

    public static final String ROTTEN_APPLE_NAME = "rotten_apple";
    public static final Supplier<Item> ROTTEN_APPLE = regItem(ROTTEN_APPLE_NAME, Item::new,
            new Item.Properties().food(ModFoods.ROTTEN_APPLE, ModFoods.ROTTEN_APPLE_CONSUMABLE));


    public static final Supplier<Block> CORN_BASE = regBlock("corn_base", CornBaseBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT)
                    .mapColor(MapColor.PLANT)
                    .randomTicks()
                    .offsetType(BlockBehaviour.OffsetType.NONE)
                    .instabreak()
                    .sound(SoundType.CROP));

    public static final Supplier<Block> CORN_MIDDLE = regBlock("corn_middle", CornMiddleBlock::new,
            () -> BlockBehaviour.Properties.ofFullCopy(CORN_BASE.get()));

    public static final Supplier<Block> CORN_TOP = regBlock("corn_top", CornTopBlock::new,
            () -> BlockBehaviour.Properties.ofFullCopy(CORN_BASE.get()));

    //pot
    public static final Supplier<Block> CORN_POT = regBlock("potted_corn",
            p -> PlatHelper.newFlowerPot(() -> (FlowerPotBlock) Blocks.FLOWER_POT, CORN_BASE, p),
            BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT));

    public static final String CORN_NAME = "corn";
    public static final Supplier<Item> COB_ITEM = regItem(CORN_NAME, Item::new, new Item.Properties());

    public static final Supplier<Item> COOKED_COB = regItem("corn_on_the_cob", Item::new,
            new Item.Properties().stacksTo(16).food(ModFoods.CORN_ON_THE_COB));

    public static final Supplier<Item> KERNELS = regItem("kernels", p -> new BlockItem(CORN_BASE.get(), p),
            new Item.Properties().useItemDescriptionPrefix());

    public static final String POPCORN_NAME = "popcorn";
    public static final Supplier<Item> POP_CORN = regItem(POPCORN_NAME, Item::new,
            new Item.Properties().food(ModFoods.POPCORN, ModFoods.POPCORN_CONSUMABLE));

    public static final String CANDY_CORN_NAME = "candy_corn";
    public static final Supplier<Item> CANDY_CORN = regItem(CANDY_CORN_NAME, CandyCornItem::new,
            new Item.Properties().food(ModFoods.CANDY_CORN, ModFoods.CANDY_CORN_CONSUMABLE));


    public static final Supplier<Block> PAPER_BAG = regBlock("paper_bag", PaperBagBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL).strength(0.5f, 0.5f));

    public static final String PAPER_BAG_NAME = "paper_bag";
    public static final Supplier<Item> PAPER_BAG_ITEM = regItem(PAPER_BAG_NAME,
            p -> new PaperBagItem(PAPER_BAG.get(), p), new Item.Properties().useBlockDescriptionPrefix()
                    .component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.HEAD)
                            .setCameraOverlay(res("misc/paper_bag_overlay")).build()));


    public static final Supplier<Block> CANDY_BAG = regBlock("candy_bag", CandyBagBlock::new,
            () -> BlockBehaviour.Properties.ofFullCopy(PAPER_BAG.get())
                    .pushReaction(PushReaction.DESTROY));

    public static final Supplier<BlockEntityType<CandyBagTile>> CANDY_BAG_TILE = RegHelper.registerBlockEntityType(
            res("candy_bag"), CandyBagTile::new, CANDY_BAG);


    public static final String CARVED_PUMPKIN_NAME = "carved_pumpkin";
    public static final Supplier<ModCarvedPumpkinBlock> CARVED_PUMPKIN = regPumpkin(CARVED_PUMPKIN_NAME,
            p -> new ModCarvedPumpkinBlock(p, PumpkinType.NORMAL),
            BlockBehaviour.Properties.ofFullCopy(Blocks.CARVED_PUMPKIN));


    public static final Supplier<ModCarvedPumpkinBlock> JACK_O_LANTERN = regPumpkin("jack_o_lantern",
            p -> new ModCarvedPumpkinBlock(p, PumpkinType.JACK),
            BlockBehaviour.Properties.ofFullCopy(Blocks.CARVED_PUMPKIN).lightLevel(s -> 15));


    public static final Supplier<BlockEntityType<ModCarvedPumpkinBlockTile>> MOD_CARVED_PUMPKIN_TILE =
            RegHelper.registerBlockEntityType(res("carved_pumpkin"), ModCarvedPumpkinBlockTile::new, CARVED_PUMPKIN);

    public static Supplier<ModCarvedPumpkinBlock> regPumpkin(String name,
                                                             Function<BlockBehaviour.Properties, ModCarvedPumpkinBlock> factory,
                                                             BlockBehaviour.Properties properties) {
        var block = regBlock(name, factory, properties);
        RegHelper.registerItem(res(name), p -> {
            var type = block.get().getType(block.get().defaultBlockState());
            p.useBlockDescriptionPrefix().component(PUMPKIN_CARVING.get(), PumpkinCarvingData.empty(type));
            if (type.is(PumpkinType.NORMAL.getKey())) {
                p.equippable(EquipmentSlot.HEAD);
            }
            return new ModCarvedPumpkinItem(block.get(), p);
        });
        return block;
    }

    public static <T extends Item> Supplier<T> regItem(String name, Function<Item.Properties, T> factory,
                                                       Item.Properties properties) {
        return RegHelper.registerItem(res(name), factory, properties);
    }


    public static <T extends Block> Supplier<T> regBlock(String name, Function<BlockBehaviour.Properties, T> factory,
                                                         BlockBehaviour.Properties properties) {
        return RegHelper.registerBlock(res(name), factory, properties);
    }

    public static <T extends Block> Supplier<T> regBlock(String name, Function<BlockBehaviour.Properties, T> factory,
                                                         Supplier<BlockBehaviour.Properties> properties) {
        return RegHelper.registerBlock(res(name), factory, properties);
    }

    public static <T extends Block> Supplier<T> regWithItem(String name, Function<BlockBehaviour.Properties, T> factory,
                                                            BlockBehaviour.Properties properties, Item.Properties itemProperties) {
        return RegHelper.registerBlockWithItem(res(name), factory, properties, itemProperties);
    }

    public static Supplier<BlockItem> regBlockItem(String name, Supplier<? extends Block> blockSup, Item.Properties properties) {
        return RegHelper.registerBlockItem(res(name), blockSup, properties);
    }

}

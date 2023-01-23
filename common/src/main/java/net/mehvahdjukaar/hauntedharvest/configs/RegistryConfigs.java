package net.mehvahdjukaar.hauntedharvest.configs;

import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigBuilder;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigSpec;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

//loaded before registry
public class RegistryConfigs {

    public static void init() {
    }

    public static final ConfigSpec SPEC;


    private static final Map<String, Supplier<Boolean>> CONFIGS_BY_NAME = new HashMap<>();

    public static final Supplier<Boolean> CORN_ENABLED;
    public static final Supplier<Boolean> GRIM_APPLE;
    public static final Supplier<Boolean> PAPER_BAG;
    public static final Supplier<Boolean> POPCORN_ENABLED;
    public static final Supplier<Boolean> SPLATTERED_EGG_ENABLED;
    public static final Supplier<Boolean> CARVED_PUMPKINS_ENABLED;
    public static final Supplier<Boolean> CANDY_CORN_ENABLED;


    public static final Supplier<Boolean> CREATIVE_TAB;
    public static final Supplier<Boolean> CUSTOM_CONFIGURED_SCREEN;

    static {
        ConfigBuilder builder = ConfigBuilder.create(HauntedHarvest.res("registry"), ConfigType.COMMON);


        builder.comment("Here are configs that need reloading to take effect");

        builder.push("general");
        CREATIVE_TAB = builder.comment("Enable Creative Tab").define("creative_tab", false);

        CUSTOM_CONFIGURED_SCREEN = builder.comment("Enables custom Configured config screen")
                .define("custom_configured_screen", true);
        builder.pop();


        builder.push("features");
        CORN_ENABLED = regConfig(builder, ModRegistry.CORN_NAME, true);
        GRIM_APPLE = regConfig(builder, ModRegistry.GRIM_APPLE_NAME, true);
        PAPER_BAG = regConfig(builder, ModRegistry.PAPER_BAG_NAME, true);
        POPCORN_ENABLED = regConfig(builder, ModRegistry.POPCORN_NAME, true);
        CARVED_PUMPKINS_ENABLED = regConfig(builder, ModRegistry.CARVED_PUMPKIN_NAME, true);
        SPLATTERED_EGG_ENABLED = regConfig(builder, ModRegistry.SPLATTERED_EGG_NAME, true);
        CANDY_CORN_ENABLED = regConfig(builder, ModRegistry.CANDY_CORN_NAME, true);

        SPEC = builder.build();
        //load early
        SPEC.loadFromFile();
    }

    private static Supplier<Boolean> regConfig(ConfigBuilder builder, String name, Boolean value) {
        var config = builder.define(name, value);
        CONFIGS_BY_NAME.put(name, config);
        return config;
    }

    public static boolean isEnabled(String key) {
        return CONFIGS_BY_NAME.getOrDefault(key, () -> true).get();
    }

}
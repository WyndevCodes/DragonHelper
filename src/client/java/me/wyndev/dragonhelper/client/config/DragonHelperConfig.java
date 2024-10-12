package me.wyndev.dragonhelper.client.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import me.wyndev.dragonhelper.client.DragonHelperClient;
import me.wyndev.dragonhelper.client.Utils;
import me.wyndev.dragonhelper.client.data.JsonDataProvider;
import me.wyndev.dragonhelper.client.feature.Feature;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DragonHelperConfig extends JsonDataProvider {

    private static DragonHelperConfig instance;

    public static void register() {
        instance = new DragonHelperConfig();
    }

    public static DragonHelperConfig get() {
        return instance;
    }

    @Override
    protected String getFileName() {
        return "config";
    }

    @Override
    protected @Nullable Map<String, Object> getDefaultData() {
        HashMap<String, Object> dataMap = new HashMap<>(17);

        dataMap.put("messages.hideOtherPlayerDrops", true);
        dataMap.put("messages.hideSummoningEyePlace", false);
        dataMap.put("messages.hideLootnumText", false);
        dataMap.put("messages.hidePvpProtectionText", true);

        dataMap.put("autosell.normalAutoSell", true);
        dataMap.put("autosell.autoSellEyes", false);
        dataMap.put("autosell.specialAutoSell", false);
        dataMap.put("autosell.autoSellInterval", 20);

        dataMap.put("notifications.notifyForSuperior", true);
        dataMap.put("notifications.notifyForInfernal", true);
        dataMap.put("notifications.notifyForEndstoneProtector", true);

        dataMap.put("ui.zealotKillCounter", true);
        dataMap.put("ui.dragonKillCounter", true);
        dataMap.put("ui.endstoneProtectorKillCounter", true);
        dataMap.put("ui.endstoneProtectorTimer", true);
        dataMap.put("ui.rngMeterTracker", true);
        dataMap.put("ui.zombieSlayerLevelTracker", true);

        return dataMap;
    }

    @Nullable
    public static Screen createConfig(Screen parent) {
        String server = Utils.getClientServer();
        DragonHelperClient.LOGGER.info("{}Config was opened while the client was connected to the server: {}", DragonHelperClient.LOGGER_PREFIX, server);
        if (server == null) return null;

        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Text.translatable("text.config.dragonhelper.title"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory category = builder.getOrCreateCategory(Text.of("empty")); //only 1 category so this does not show

        //create messages category
        if (isCategoryEnabled(server, Feature.FeatureCategory.MESSAGES)) {
            SubCategoryBuilder subBuilder = createSubCategory(entryBuilder, "messages", true);

            //add elements to category
            addBooleanConfigEntry(server, subBuilder, entryBuilder, Feature.DRAGON_DROP_CONTAINS_TEXT, "messages",
                    "hideOtherPlayerDrops", true);
            addBooleanConfigEntry(server, subBuilder, entryBuilder, Feature.EYE_PLACE_CONTAINS_TEXT, "messages",
                    "hideSummoningEyePlace", false);
            addBooleanConfigEntry(server, subBuilder, entryBuilder, Feature.LOOTNUM_CONTAINS_TEXT, "messages",
                    "hideLootnumText", false);
            addBooleanConfigEntry(server, subBuilder, entryBuilder, Feature.PVP_PROTECTION_MESSAGE_CONTAINS, "messages",
                    "hidePvpProtectionText", true);

            category.addEntry(subBuilder.build());
        }

        //create auto sell category
        if (isCategoryEnabled(server, Feature.FeatureCategory.AUTOSELL)) {
            SubCategoryBuilder subBuilder = createSubCategory(entryBuilder, "autosell", true);

            //add elements to category
            addBooleanConfigEntry(server, subBuilder, entryBuilder, Feature.AUTO_SELL_NORMAL, "autosell",
                    "normalAutoSell", true);
            addBooleanConfigEntry(server, subBuilder, entryBuilder, Feature.AUTO_SELL_EYES, "autosell",
                    "autoSellEyes", false);
            addBooleanConfigEntry(server, subBuilder, entryBuilder, Feature.AUTO_SELL_SPECIAL, "autosell",
                    "specialAutoSell", false);
            addIntSliderConfigEntry(server, subBuilder, entryBuilder, Feature.AUTO_SELL_NORMAL, "autosell",
                    "autoSellInterval", 20, 10, 60);

            category.addEntry(subBuilder.build());
        }

        //create notification category
        if (isCategoryEnabled(server, Feature.FeatureCategory.NOTIFICATIONS)) {
            SubCategoryBuilder subBuilder = createSubCategory(entryBuilder, "notifications", true);

            //add elements to category
            addBooleanConfigEntry(server, subBuilder, entryBuilder, Feature.DRAGON_SPAWN_CONTAINS_TEXT, "notifications",
                    "notifyForSuperior", true);
            addBooleanConfigEntry(server, subBuilder, entryBuilder, Feature.HAS_INFERNAL_DRAGONS, "notifications",
                    "notifyForInfernal", true);
            addBooleanConfigEntry(server, subBuilder, entryBuilder, Feature.PROTECTOR_NAME_CONTAINS_TEXT, "notifications",
                    "notifyForEndstoneProtector", true);

        }

        //create ui category
        if (isCategoryEnabled(server, Feature.FeatureCategory.UI)) {
            SubCategoryBuilder subBuilder = createSubCategory(entryBuilder, "ui", true);

            //add elements to category
            addBooleanConfigEntry(server, subBuilder, entryBuilder, Feature.KILL_TRACKING_COMMAND, "ui",
                    "zealotKillCounter", true);
            addBooleanConfigEntry(server, subBuilder, entryBuilder, Feature.KILL_TRACKING_COMMAND, "ui",
                    "dragonKillCounter", true);
            addBooleanConfigEntry(server, subBuilder, entryBuilder, Feature.KILL_TRACKING_COMMAND, "ui",
                    "endstoneProtectorKillCounter", true);
            addBooleanConfigEntry(server, subBuilder, entryBuilder, Feature.PROTECTOR_TIMER, "ui",
                    "endstoneProtectorTimer", true);
            addBooleanConfigEntry(server, subBuilder, entryBuilder, Feature.RNG_METER_TRACKING_COMMAND, "ui",
                    "rngMeterTracker", true);
            addBooleanConfigEntry(server, subBuilder, entryBuilder, Feature.SLAYER_EXP_TRACKING_COMMAND, "ui",
                    "zombieSlayerLevelTracker", true);

            category.addEntry(subBuilder.build());
        }

        builder.setSavingRunnable(() -> instance.saveData());

        return builder.build();
    }

    private static boolean isCategoryEnabled(String server, Feature.FeatureCategory category) {
        return Arrays.stream(category.getFeaturesInCategory()).anyMatch(feature -> ServerConfig.instance.serverHasFeature(server, feature));
    }

    private static SubCategoryBuilder createSubCategory(ConfigEntryBuilder entryBuilder, String categoryName, boolean hasTooltip) {
        SubCategoryBuilder b = entryBuilder.startSubCategory(Text.translatable("text.config.dragonhelper.category." + categoryName));
        if (hasTooltip) b.setTooltip(Text.translatable("text.config.dragonhelper.category." + categoryName + ".tooltip"));
        return b;
    }

    private static void addBooleanConfigEntry(String server, SubCategoryBuilder sub, ConfigEntryBuilder entryBuilder, Feature feature,
                                           String langCategory, String name, boolean defaultValue) {
        if (ServerConfig.instance.serverHasFeature(server, feature)) {
            String id = langCategory + "." + name;
            sub.add(entryBuilder.startBooleanToggle(
                                    Text.translatable("text.config.dragonhelper.option." + id),
                                    instance.getBoolean(id, defaultValue)
                            )
                            .setDefaultValue(defaultValue).setSaveConsumer(b -> instance.dataMap.put(id, b))
                            .build()
            );
        }
    }

    private static void addIntSliderConfigEntry(String server, SubCategoryBuilder sub, ConfigEntryBuilder entryBuilder, Feature feature,
                                              String langCategory, String name, int defaultValue, int low, int high) {
        if (ServerConfig.instance.serverHasFeature(server, feature)) {
            String id = langCategory + "." + name;
            sub.add(entryBuilder.startIntSlider(
                                    Text.translatable("text.config.dragonhelper.option." + id),
                                    instance.getInt(id, defaultValue),
                                    low,
                                    high
                            )
                            .setDefaultValue(defaultValue).setSaveConsumer(i -> instance.dataMap.put(id, i))
                            .build()
            );
        }
    }

}

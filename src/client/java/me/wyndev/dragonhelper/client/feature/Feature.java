package me.wyndev.dragonhelper.client.feature;

public enum Feature {

    AUTO_SELL_NORMAL(String.class),
    AUTO_SELL_SPECIAL(String.class),
    AUTO_SELL_EYES(String.class),
    KILL_TRACKING_COMMAND(String.class),
    KILL_CONTAINS_TEXT(String.class),
    BESTIARY_LOADING_TEXT(String.class),
    EXPERIMENTS_NPC_NAME(String.class),
    RENEW_EXPERIMENTS_COMMAND(String.class),
    RNG_METER_TRACKING_COMMAND(String.class),
    RNG_METER_UPDATE_STARTS_TEXT(String.class),
    RNG_METER_LOAD_CONTAINS_TEXT(String.class),
    SLAYER_EXP_TRACKING_COMMAND(String.class),
    SLAYER_EXP_UPDATE_CONTAINS_TEXT(String.class),
    SLAYER_EXP_LOAD_CONTAINS_TEXT(String.class),
    PROTECTOR_NAME_CONTAINS_TEXT(String.class),
    PROTECTOR_HAS_SPAWN_MESSAGE(Boolean.class),
    PROTECTOR_TIMER(Double.class),
    HAS_INFERNAL_DRAGONS(Boolean.class),
    DRAGON_DROP_CONTAINS_TEXT(String.class),
    EYE_PLACE_CONTAINS_TEXT(String.class),
    LOOTNUM_CONTAINS_TEXT(String.class),
    PVP_PROTECTION_MESSAGE_CONTAINS(String.class),
    DRAGON_SPAWN_CONTAINS_TEXT(String.class),

    ;

    private final Class<?> clazz;

    Feature(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getType() {
        return clazz;
    }

    public static <T> T getValue(Class<T> featureClass, Object obj) {
        return featureClass.cast(obj);
    }

    /**
     * A category for features to help the config check if the server
     * the user is connected to should show a specific configuration
     * category.
     */
    public enum FeatureCategory {
        MESSAGES(DRAGON_DROP_CONTAINS_TEXT, EYE_PLACE_CONTAINS_TEXT, LOOTNUM_CONTAINS_TEXT, PVP_PROTECTION_MESSAGE_CONTAINS),
        AUTOSELL(AUTO_SELL_EYES, AUTO_SELL_NORMAL, AUTO_SELL_SPECIAL),
        NOTIFICATIONS(DRAGON_SPAWN_CONTAINS_TEXT, HAS_INFERNAL_DRAGONS, PROTECTOR_NAME_CONTAINS_TEXT),
        UI(KILL_TRACKING_COMMAND, RNG_METER_TRACKING_COMMAND, SLAYER_EXP_TRACKING_COMMAND, PROTECTOR_NAME_CONTAINS_TEXT, PROTECTOR_TIMER);

        private final Feature[] features;

        FeatureCategory(Feature... features) {
            this.features = features;
        }

        public Feature[] getFeaturesInCategory() {
            return features;
        }
    }

}

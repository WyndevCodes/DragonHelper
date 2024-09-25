package me.wyndev.dragonhelper.client.feature;

public enum Feature {

    AUTO_SELL_NORMAL(String.class),
    AUTO_SELL_SPECIAL(String.class),
    AUTO_SELL_EYES(String.class),
    KILL_TRACKING_COMMAND(String.class),
    KILL_CONTAINS_TEXT(String.class),
    EXPERIMENTS_NPC_NAME(String.class),
    RENEW_EXPERIMENTS_COMMAND(String.class),
    RNG_METER_TRACKING_COMMAND(String.class),
    RNG_METER_UPDATE_STARTS_TEXT(String.class),
    RNG_METER_LOAD_CONTAINS_TEXT(String.class),
    SLAYER_EXP_TRACKING_COMMAND(String.class),
    SLAYER_EXP_UPDATE_CONTAINS_TEXT(String.class),
    SLAYER_EXP_LOAD_CONTAINS_TEXT(String.class),
    PROTECTOR_NAME_CONTAINS_TEXT(String.class),
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

}

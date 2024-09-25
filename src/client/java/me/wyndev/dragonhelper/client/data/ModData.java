package me.wyndev.dragonhelper.client.data;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Saved data for this mod.
 */
public class ModData extends JsonDataProvider {
    private static ModData instance;

    public static void init() {
        instance = new ModData();
    }

    @Override
    protected String getFileName() {
        return "mod-data.json";
    }

    @Override
    protected @Nullable Map<String, Object> getDefaultData() {
        return null;
    }

    public static ModData getInstance() {
        return instance;
    }

}
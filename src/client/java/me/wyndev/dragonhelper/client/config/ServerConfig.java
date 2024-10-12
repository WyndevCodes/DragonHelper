package me.wyndev.dragonhelper.client.config;

import me.wyndev.dragonhelper.client.Utils;
import me.wyndev.dragonhelper.client.data.JsonDataProvider;
import me.wyndev.dragonhelper.client.feature.Feature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * YAML config for server-based feature data.
 */
public class ServerConfig extends JsonDataProvider {

    private Map<String, Map<Feature, Object>> serverFeatureData;

    public static ServerConfig instance;

    public static void init() {
        instance = new ServerConfig();
    }

    @Override
    protected String getFileName() {
        return "servers.json";
    }

    @Override
    protected @Nullable Map<String, Object> getDefaultData() {
        Map<String, Object> dragnetData = new HashMap<>();
        dragnetData.put("auto-sell-normal", "sell");
        dragnetData.put("auto-sell-special", "specialsell");
        dragnetData.put("auto-sell-eyes", "eyes");
        dragnetData.put("kill-tracking-command", "debugkill");
        dragnetData.put("kill-contains-text", "you killed");
        dragnetData.put("experiments-npc-name", "CIT-e98c26597f30");
        dragnetData.put("renew-experiments-command", "renewexperiments");
        dragnetData.put("rng-meter-tracking-command", "debugmeter");
        dragnetData.put("rng-meter-update-starts-text", "rng meter -");
        dragnetData.put("rng-meter-load-contains-text", "rng meter experience");
        dragnetData.put("slayer-exp-tracking-command", "debugslayerxp");
        dragnetData.put("slayer-exp-update-contains-text", "> total xp");
        dragnetData.put("slayer-exp-load-contains-text", "slayer xp experience");
        dragnetData.put("protector-name-contains-text", "protector");
        dragnetData.put("has-infernal-dragons", true);
        dragnetData.put("dragon-drop-contains-text", " has obtained ");
        dragnetData.put("eye-place-contains-text", "placed a summoning eye");
        dragnetData.put("lootnum-contains-text", "lootnum");
        dragnetData.put("pvp-protection-message-contains", "you can't pvp here");
        dragnetData.put("dragon-spawn-contains-text", "has spawned");

        Map<String, Object> dragfightsData = new HashMap<>();
        dragfightsData.put("auto-sell-normal", "sellall");
        dragfightsData.put("kill-tracking-command", "debugkill");
        dragnetData.put("dragon-drop-contains-text", " has obtained ");
        dragfightsData.put("eye-place-contains-text", "placed a summoning eye");

        return Map.of(
                "dragnet", dragnetData,
                "dragfights", dragfightsData
        );
    }

    @Override
    public void loadData() {
        super.loadData();

        serverFeatureData = convertToFeatureData(this.dataMap);
    }

    @Nullable
    public Object getServerFeatureValue(@Nullable String server, @NotNull Feature feature) {
        if (server == null || !serverFeatureData.containsKey(server.toLowerCase())) return null;
        Map<Feature, Object> featureMap = serverFeatureData.get(server.toLowerCase());
        return featureMap.containsKey(feature) ? Feature.getValue(feature.getType(), featureMap.get(feature)) : null;
    }

    public boolean currentServerHasFeature(@NotNull Feature feature) {
        return serverHasFeature(Utils.getClientServer(), feature);
    }

    public boolean serverHasFeature(@Nullable String server, @NotNull Feature feature) {
        if (server == null || !serverFeatureData.containsKey(server.toLowerCase())) return false;
        return serverFeatureData.get(server.toLowerCase()).containsKey(feature);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Map<Feature, Object>> convertToFeatureData(Map<String, Object> serverData) {
        Map<String, Map<Feature, Object>> converted = new HashMap<>();

        for (String server : serverData.keySet()) {
            if (!(serverData.get(server) instanceof Map)) continue;
            Map<String, Object> data = ((Map<String, Object>)serverData.get(server.toLowerCase()));

            Map<Feature, Object> features = new HashMap<>();

            for (String feature : data.keySet()) {
                try {
                    Feature modFeature = Feature.valueOf(feature.toUpperCase().replaceAll("-", "_"));
                    features.put(modFeature, data.get(feature));
                } catch (IllegalArgumentException ignored) {}
            }

            converted.put(server, features);
        }

        return converted;
    }

}

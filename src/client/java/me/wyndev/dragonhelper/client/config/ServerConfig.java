package me.wyndev.dragonhelper.client.config;

import com.ibm.icu.impl.Pair;
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
        Map<String, Object> dragnetData = new HashMap<>(21);
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
        dragnetData.put("protector-timer", 60.0);

        Map<String, Object> dragfightsData = new HashMap<>(11);
        dragfightsData.put("auto-sell-normal", "sellall");
        dragfightsData.put("kill-tracking-command", "debugkill");
        dragfightsData.put("kill-contains-text", "you killed");
        dragfightsData.put("bestiary-loading-text", "bestiary kill count");
        dragfightsData.put("dragon-drop-contains-text", " has obtained ");
        dragfightsData.put("eye-place-contains-text", "placed a summoning eye");
        dragfightsData.put("dragon-spawn-contains-text", "has spawned");
        dragfightsData.put("pvp-protection-message-contains", "you can't pvp here");
        dragfightsData.put("protector-name-contains-text", "protector");
        dragfightsData.put("protector-spawn-chat-message", "an endstone protector has risen");
        dragfightsData.put("protector-timer", 300.0);

        return Map.of(
                "dragnet", dragnetData,
                "dragfights", dragfightsData
        );
    }

    @Override
    public void loadData() {
        super.loadData();

        Pair<Map<String, Map<Feature, Object>>, Boolean> p = convertToFeatureData(this.dataMap);
        serverFeatureData = p.first;
        if (p.second) saveData();
    }

    public Map<String, Map<Feature, Object>> getServerFeatureData() {
        return serverFeatureData;
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
    private Pair<Map<String, Map<Feature, Object>>, Boolean> convertToFeatureData(Map<String, Object> serverData) {
        Map<String, Map<Feature, Object>> converted = new HashMap<>();
        Map<String, Object> defaultData = null;
        if (getDefaultData() != null) defaultData = new HashMap<>(getDefaultData());

        boolean addedDefaults = false;

        for (String server : serverData.keySet()) {
            if (!(serverData.get(server) instanceof Map)) continue;
            Map<String, Object> data = ((Map<String, Object>)serverData.get(server.toLowerCase()));

            Map<Feature, Object> features = new HashMap<>();

            for (String feature : data.keySet()) {
                try {
                    Feature modFeature = Feature.valueOf(feature.toUpperCase().replaceAll("-", "_"));
                    features.put(modFeature, data.get(feature));
                    if (defaultData != null && defaultData.get(server) instanceof Map<?,?> map) map.remove(feature);
                } catch (IllegalArgumentException ignored) {}
            }

            //default key checks
            if (defaultData != null && defaultData.get(server) instanceof Map<?,?> map && super.dataMap.get(server) instanceof Map<?,?> serverMap) {
                if (!map.isEmpty()) {
                    for (String uncheckedFeature : ((Map<String, Object>) map).keySet()) {
                        //default key check
                        Feature modFeature = Feature.valueOf(uncheckedFeature.toUpperCase().replaceAll("-", "_"));
                        if (map.containsKey(uncheckedFeature)) {
                            var val = ((Map<String, Object>) map).get(uncheckedFeature);
                            features.put(modFeature, val);
                            ((Map<String, Object>)serverMap).put(uncheckedFeature, val);
                        }
                    }
                    addedDefaults = true;
                }
            }

            converted.put(server, features);
        }

        return Pair.of(converted, addedDefaults);
    }

}

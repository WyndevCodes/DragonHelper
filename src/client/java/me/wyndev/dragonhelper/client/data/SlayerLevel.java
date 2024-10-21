package me.wyndev.dragonhelper.client.data;

import java.util.Map;
import java.util.TreeMap;

public class SlayerLevel {

    private static final TreeMap<Integer, String[]> dragnetSlayerLevels = new TreeMap<>(Map.of(
            5, new String[]{"Zombie Slayer 1"},
            15, new String[]{"Zombie Slayer 2"},
            200, new String[]{"Zombie Slayer 3"},
            1_000, new String[]{"Zombie Slayer 4"},
            5_000, new String[]{"Zombie Slayer 5"},
            20_000, new String[]{"Zombie Slayer 6"},
            100_000, new String[]{"Zombie Slayer 7"},
            400_000, new String[]{"Zombie Slayer 8"},
            1_000_000, new String[]{"Zombie Slayer 9"}
    ));

    public static TreeMap<Integer, String[]> getSlayerLevelsForServer(String server) {
        if (server != null && server.equalsIgnoreCase("dragnet")) {
            return dragnetSlayerLevels;
        }
        return new TreeMap<>(Map.of(
                0, new String[]{"Zombie Slayer 1"},
                20, new String[]{"Zombie Slayer 2"},
                100, new String[]{"Zombie Slayer 3"},
                500, new String[]{"Zombie Slayer 4"},
                2_500, new String[]{"Zombie Slayer 5"},
                12_500, new String[]{"Zombie Slayer 6"},
                62_500, new String[]{"Zombie Slayer 7"},
                62_500 * 5, new String[]{"Zombie Slayer 8"},
                62_500 * 25, new String[]{"Zombie Slayer 9"}
        ));
    }

}

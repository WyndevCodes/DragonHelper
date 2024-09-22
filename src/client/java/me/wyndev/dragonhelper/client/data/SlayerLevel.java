package me.wyndev.dragonhelper.client.data;

import java.util.Map;
import java.util.TreeMap;

public class SlayerLevel {

    public static TreeMap<Integer, String[]> slayerLevels = new TreeMap<>(Map.of(
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

}

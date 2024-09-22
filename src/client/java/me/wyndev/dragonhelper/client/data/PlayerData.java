package me.wyndev.dragonhelper.client.data;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

import java.util.HashMap;
import java.util.Map;

public class PlayerData {

    private final Map<String, Integer> mobKills = new HashMap<>(Map.of(
            "dragon-kills", 0,
            "zealot-kills", 0,
            "endstone-protector-kills", 0,
            "crypt-ghoul-kills", 0,
            "zombie-slayer-kills", 0
    ));
    private int rngMeterExp = 0;
    private final Map<String, Integer> slayerExperience = new HashMap<>(Map.of(
            "zombie-slayer-exp", 0
    ));

    public PlayerData() {
        loadData();

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> saveData());
    }

    private void loadData() {
        mobKills.replaceAll((k, v) -> ModData.getInstance().getInt(k));
        rngMeterExp = ModData.getInstance().getInt("rng-meter-exp");
        slayerExperience.replaceAll((k, v) -> ModData.getInstance().getInt(k));
    }

    private void saveData() {
        mobKills.forEach((k, v) -> ModData.getInstance().setInt(k, v));
        ModData.getInstance().setInt("rng-meter-exp", rngMeterExp);
        slayerExperience.forEach((k, v) -> ModData.getInstance().setInt(k, v));
    }

    public int getDragonKills() {
        return mobKills.getOrDefault("dragon-kills", 0);
    }

    public void setDragonKills(int dragonKills) {
        mobKills.put("dragon-kills", dragonKills);
    }

    public int getZealotKills() {
        return mobKills.getOrDefault("zealot-kills", 0);
    }

    public void setZealotKills(int zealotKills) {
        mobKills.put("zealot-kills", zealotKills);
    }

    public int getEndstoneProtectorKills() {
        return mobKills.getOrDefault("endstone-protector-kills", 0);
    }

    public void setEndstoneProtectorKills(int endstoneProtectorKills) {
        mobKills.put("endstone-protector-kills", endstoneProtectorKills);
    }

    public int getCryptGhoulKills() {
        return mobKills.getOrDefault("crypt-ghoul-kills", 0);
    }

    public void setCryptGhoulKills(int cryptGhoulKills) {
        mobKills.put("crypt-ghoul-kills", cryptGhoulKills);
    }

    public int getZombieSlayerBossKills() {
        return mobKills.getOrDefault("zombie-slayer-kills", 0);
    }

    public void setZombieSlayerBossKills(int zombieSlayerBossKills) {
        mobKills.put("zombie-slayer-kills", zombieSlayerBossKills);
    }

    public int getRngMeterExp() {
        return rngMeterExp;
    }

    public void setRngMeterExp(int rngMeterExp) {
        this.rngMeterExp = rngMeterExp;
    }

    public int getZombieSlayerExp() {
        return slayerExperience.getOrDefault("zombie-slayer-exp", 0);
    }

    public void setZombieSlayerExp(int zombieSlayerExp) {
        slayerExperience.put("zombie-slayer-exp", zombieSlayerExp);
    }
}

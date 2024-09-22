package me.wyndev.dragonhelper.client;

import me.wyndev.dragonhelper.client.config.DragonHelperConfig;
import me.wyndev.dragonhelper.client.data.ModData;
import me.wyndev.dragonhelper.client.data.PlayerData;
import me.wyndev.dragonhelper.client.data.ServerDataTracker;
import me.wyndev.dragonhelper.client.feature.*;
import me.wyndev.dragonhelper.client.hud.DragonHelperScreen;
import net.fabricmc.api.ClientModInitializer;

public class DragonHelperClient implements ClientModInitializer {

    public static String NAMESPACE = "dragonhelper";

    private static PlayerData playerData;
    private static ServerDataTracker serverDataTracker;

    @Override
    public void onInitializeClient() {

        DragonHelperConfig.register();
        ModData.init();

        serverDataTracker = new ServerDataTracker();
        playerData = new PlayerData();

        MessageFeature.register();
        AutoSellFeature.register();
        KillTrackingFeature.register();
        RNGMeterFeature.register();
        SlayerTrackingFeature.register();
        RenewExperimentsFeature.register();

        DragonHelperScreen.register();
    }

    public static PlayerData getPlayerData() {
        return playerData;
    }

    public static ServerDataTracker getServerDataTracker() {
        return serverDataTracker;
    }
}

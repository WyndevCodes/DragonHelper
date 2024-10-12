package me.wyndev.dragonhelper.client;

import me.wyndev.dragonhelper.client.config.DragonHelperConfig;
import me.wyndev.dragonhelper.client.config.ServerConfig;
import me.wyndev.dragonhelper.client.data.ModData;
import me.wyndev.dragonhelper.client.data.PlayerData;
import me.wyndev.dragonhelper.client.data.ServerDataTracker;
import me.wyndev.dragonhelper.client.feature.*;
import me.wyndev.dragonhelper.client.hud.DragonHelperScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;

public class DragonHelperClient implements ClientModInitializer {

    public static String NAMESPACE = "dragonhelper";
    public static final String CONFIG_DIRECTORY = "config" + File.separator + "dragonhelper";
    public static Logger LOGGER = LoggerFactory.getLogger("DragonHelper");
    public static String LOGGER_PREFIX = "[DragonHelper] ";

    private static PlayerData playerData;
    private static ServerDataTracker serverDataTracker;

    @Override
    public void onInitializeClient() {

        LOGGER.info("{}Registering configs...", LOGGER_PREFIX);
        //create mod directory if it does not exist
        File dir = new File(CONFIG_DIRECTORY);
        if (!dir.exists()) dir.mkdirs();

        DragonHelperConfig.register();
        ModData.init();
        ServerConfig.init();

        serverDataTracker = new ServerDataTracker();
        playerData = new PlayerData();

        LOGGER.info("{}Initializing mod features...", LOGGER_PREFIX);
        MessageFeature.register();
        AutoSellFeature.register();
        KillTrackingFeature.register();
        RNGMeterFeature.register();
        SlayerTrackingFeature.register();
        RenewExperimentsFeature.register();

        DragonHelperScreen.register();

        //create dhc reload command
        LOGGER.info("{}Setting up dragonhelper command...", LOGGER_PREFIX);
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("dragonhelper")
                    .then(ClientCommandManager.literal("reload")
                    .executes(context -> {
                        //reload server config
                        ServerConfig.instance.loadData();
                        context.getSource().sendFeedback(Text.of("Reloaded the mod's servers.json file!").copy().withColor(0x00FF00));

                        return 1;
                    })));
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            String server = Utils.getClientServer(handler);
            if (client.player != null && server != null) client.player.sendMessage(Text.of("You have joined " + server + "! Your " + server +
                    " settings will apply here since this is a valid dragon server!").copy().withColor(Color.GREEN.getRGB()));
        });

        LOGGER.info("{}Mod initialization complete!", LOGGER_PREFIX);
    }

    public static PlayerData getPlayerData() {
        return playerData;
    }

    public static ServerDataTracker getServerDataTracker() {
        return serverDataTracker;
    }
}

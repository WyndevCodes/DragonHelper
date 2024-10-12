package me.wyndev.dragonhelper.client.feature;

import me.wyndev.dragonhelper.client.Utils;
import me.wyndev.dragonhelper.client.config.DragonHelperConfig;
import me.wyndev.dragonhelper.client.config.ServerConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayNetworkHandler;

import java.util.Random;

public class AutoSellFeature {

    private static int tick = 1;
    private static final int additionalSellOffset = 40; //40 ticks later
    private static final Random random = new Random();
    private static int randomOffset = 0;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            if (client.player == null) return;

            //check auto-sell intervals
            int autoSellInterval = DragonHelperConfig.get().getInt("autosell.autoSellInterval", 20) * 20;
            if ((++tick % autoSellInterval != 0) &&
                    (tick % autoSellInterval) != additionalSellOffset + randomOffset &&
                    (tick % autoSellInterval) != (additionalSellOffset * 2) + randomOffset) return;

            //auto-sell on pickup
            ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
            String server = Utils.getClientServer(networkHandler);
            if (server == null) return;

            String normalCommand = (String) ServerConfig.instance.getServerFeatureValue(server, Feature.AUTO_SELL_NORMAL);
            String specialCommand = (String) ServerConfig.instance.getServerFeatureValue(server, Feature.AUTO_SELL_SPECIAL);
            String eyeCommand = (String) ServerConfig.instance.getServerFeatureValue(server, Feature.AUTO_SELL_EYES);

            //regular sell command (sell regular items)
            if (normalCommand != null && DragonHelperConfig.get().getBoolean("autosell.normalAutoSell", true) &&
                    (tick % autoSellInterval) == 0) {
                networkHandler.sendCommand(normalCommand);
                randomOffset = random.nextInt(autoSellInterval / 4);
            }

            //advanced sell command (sell non-regular items)
            if (specialCommand != null && DragonHelperConfig.get().getBoolean("autosell.specialAutoSell", false) &&
                    (tick % autoSellInterval) == additionalSellOffset + randomOffset) {
                networkHandler.sendCommand(specialCommand);
            }

            //eye sell command (sell summoning eyes)
            if (eyeCommand != null && DragonHelperConfig.get().getBoolean("autosell.autoSellEyes", false) &&
                    (tick % autoSellInterval) == (additionalSellOffset * 2) + randomOffset) {
                networkHandler.sendCommand(eyeCommand);
            }
        });
    }

}

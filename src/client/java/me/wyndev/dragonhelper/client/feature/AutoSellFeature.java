package me.wyndev.dragonhelper.client.feature;

import me.wyndev.dragonhelper.client.config.DragonHelperConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;

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
            if ((++tick % (DragonHelperConfig.get().autosell.autoSellInterval * 20)) != 0 &&
                    (tick % (DragonHelperConfig.get().autosell.autoSellInterval * 20)) != additionalSellOffset + randomOffset &&
                    (tick % (DragonHelperConfig.get().autosell.autoSellInterval * 20)) != (additionalSellOffset * 2) + randomOffset) return;

            //auto-sell on pickup
            ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
            if (networkHandler == null) return;
            ServerInfo serverInfo = networkHandler.getServerInfo();
            if (serverInfo != null && !serverInfo.name.equalsIgnoreCase("dragnet")) return; //don't run these commands on other servers

            //regular sell command (sell regular items)
            if (DragonHelperConfig.get().autosell.normalAutoSell && (tick % (DragonHelperConfig.get().autosell.autoSellInterval * 20)) == 0) {
                networkHandler.sendCommand("sell");
                randomOffset = random.nextInt(DragonHelperConfig.get().autosell.autoSellInterval / 4);
            }

            //advanced sell command (sell non-regular items)
            if (DragonHelperConfig.get().autosell.specialAutoSell && (tick % (DragonHelperConfig.get().autosell.autoSellInterval * 20)) == additionalSellOffset + randomOffset) {
                networkHandler.sendCommand("specialsell");
            }

            //eye sell command (sell summoning eyes)
            if (DragonHelperConfig.get().autosell.autoSellEyes && (tick % (DragonHelperConfig.get().autosell.autoSellInterval * 20)) == (additionalSellOffset * 2) + randomOffset) {
                networkHandler.sendCommand("eyes");
            }
        });
    }

}

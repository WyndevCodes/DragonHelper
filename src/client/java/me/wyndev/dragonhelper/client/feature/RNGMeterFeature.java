package me.wyndev.dragonhelper.client.feature;

import me.wyndev.dragonhelper.client.DragonHelperClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class RNGMeterFeature {

    private static final String RNG_METER_TRACKING_COMMAND = "debugmeter";
    private static State state = State.NONE;
    private static int currentTick = 0; //delayed loading

    public static void register() {
        ClientReceiveMessageEvents.ALLOW_GAME.register((text, isInActionBar) -> {
            String s = text.getString().toLowerCase();
            int rngMeterExp = Integer.parseInt(s.replaceAll("[^0-9.]", ""));

            if (state == State.WAIT) {
                //rng meter exp loading
                if (!s.contains("rng meter experience")) return true;

                //save data
                try {
                    DragonHelperClient.getPlayerData().setRngMeterExp(rngMeterExp);
                    state = State.SUCCESS;
                } catch (NumberFormatException ignored) {
                }

                return false;
            } else if (state == State.SUCCESS) {
                //exp tracking from boss kills
                if (!s.startsWith("rng meter -")) return true;

                //update rng meter exp
                try {
                    DragonHelperClient.getPlayerData().setRngMeterExp(rngMeterExp);
                } catch (NumberFormatException ignored) {}
            }

            return true;
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> state = State.NONE);
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> state = State.NONE);

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (++currentTick < 20) return;
            if (client.getNetworkHandler() != null && state == State.NONE) {
                if (DragonHelperClient.getPlayerData().getRngMeterExp() == 0) {
                    state = State.WAIT;
                    client.getNetworkHandler().sendCommand(RNG_METER_TRACKING_COMMAND);
                } else {
                    state = State.SUCCESS;
                }
            }
        });
    }

    private enum State {
        NONE,
        WAIT,
        SUCCESS
    }

}

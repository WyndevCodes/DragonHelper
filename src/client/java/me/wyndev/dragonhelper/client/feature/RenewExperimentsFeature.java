package me.wyndev.dragonhelper.client.feature;

import me.wyndev.dragonhelper.client.Utils;
import me.wyndev.dragonhelper.client.config.ServerConfig;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class RenewExperimentsFeature {

    private static boolean triedToRenew = false;

    public static void register() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (hand == Hand.OFF_HAND) return ActionResult.PASS;

            ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();

            String server = Utils.getClientServer(networkHandler);
            if (server == null) return ActionResult.PASS;
            String npc = (String) ServerConfig.instance.getServerFeatureValue(server, Feature.EXPERIMENTS_NPC_NAME);
            if (npc == null) return ActionResult.PASS;

            if (entity.getDisplayName() != null && entity.getDisplayName().getString().equalsIgnoreCase(npc)) {
                //clicked on experiments NPC

                String command = (String) ServerConfig.instance.getServerFeatureValue(server, Feature.RENEW_EXPERIMENTS_COMMAND);
                if (command == null) return ActionResult.PASS;

                triedToRenew = true;
                networkHandler.sendCommand(command);
            }

            return ActionResult.PASS;
        });

        ClientReceiveMessageEvents.ALLOW_GAME.register((text, isInActionBar) -> {
            if (!triedToRenew) return true;
            triedToRenew = false;
            if (text.getString().contains("try again later!")) {
                if (MinecraftClient.getInstance().player != null) {
                    MinecraftClient.getInstance().player.sendMessage(Text.of("You are out of experiments!").copy().withColor(0xFF0000));
                }
                return false;
            }
            return true;
        });
    }

}

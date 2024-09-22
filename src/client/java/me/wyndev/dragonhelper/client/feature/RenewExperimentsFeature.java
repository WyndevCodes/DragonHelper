package me.wyndev.dragonhelper.client.feature;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class RenewExperimentsFeature {

    private static final String NPC_DISPLAY_NAME = "CIT-e98c26597f30";
    private static boolean triedToRenew = false;

    public static void register() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (hand == Hand.OFF_HAND) return ActionResult.PASS;

            if (entity.getDisplayName() != null && entity.getDisplayName().getString().equalsIgnoreCase(NPC_DISPLAY_NAME)) {
                //clicked on experiments NPC
                ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
                if (networkHandler == null) return ActionResult.PASS;

                triedToRenew = true;
                networkHandler.sendCommand("renewexperiments");
            }

            return ActionResult.PASS;
        });

        ClientReceiveMessageEvents.ALLOW_GAME.register((text, isInActionBar) -> {
            if (!triedToRenew) return true;
            triedToRenew = false;
            if (text.getString().equalsIgnoreCase("try again later!")) {
                if (MinecraftClient.getInstance().player != null) {
                    MinecraftClient.getInstance().player.sendMessage(Text.of("You are out of experiments!").copy().withColor(0xFF0000));
                }
                return false;
            }
            return true;
        });
    }

}

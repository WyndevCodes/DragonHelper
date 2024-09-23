package me.wyndev.dragonhelper.client.feature;

import me.wyndev.dragonhelper.client.DragonHelperClient;
import me.wyndev.dragonhelper.client.Utils;
import me.wyndev.dragonhelper.client.config.DragonHelperConfig;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class MessageFeature {

    public static void register() {
        ClientReceiveMessageEvents.ALLOW_GAME.register((text, isInActionBar) -> {
            if (isInActionBar) return true;

            String rawText = text.getString();
            if (rawText == null) return true;
            String rawLowerText = rawText.toLowerCase();

            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null || player.getDisplayName() == null) return true;
            String displayName = player.getDisplayName().getString();

            if (!Utils.isOnDragnet()) return true;

            //check for message
            //dragon drop message
            if (rawLowerText.contains(" has obtained ")) {
                if (!rawText.contains(displayName)) {
                    if (DragonHelperConfig.get().messages.hideOtherPlayerDrops) return false; //hide other user drop messages
                } else {
                    //does contain name, add 1 to dragons killed
                    DragonHelperClient.getPlayerData().setDragonKills(DragonHelperClient.getPlayerData().getDragonKills() + 1);
                }
            }

            //eye placement message
            if (rawLowerText.contains("placed a summoning eye") && DragonHelperConfig.get().messages.hideSummoningEyePlace) {
                return false; //hide summoning eye placement messages
            }

            //loot number message
            if (rawLowerText.contains("lootnum") && DragonHelperConfig.get().messages.hideLootnumText) {
                return false; //hide loot messages
            }

            //pvp message
            if (rawLowerText.contains("you can't pvp here") && DragonHelperConfig.get().messages.hidePvpProtectionText) {
                return false; //hide pvp protection message
            }

            //dragon notifier
            if (rawLowerText.contains("dragon") && rawLowerText.contains("has spawned")) {
                //infernal and superior notification
                if (rawLowerText.contains("superior") && DragonHelperConfig.get().notifications.notifyForSuperior) {
                    Utils.sendTitleToClient(Text.of("Superior Dragon Spawn!").copy().withColor(0xFF0000));
                    player.playSound(SoundEvents.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
                } else if (rawLowerText.contains("infernal") && DragonHelperConfig.get().notifications.notifyForInfernal) {
                    Utils.sendTitleToClient(Text.of("Infernal Dragon Spawn!").copy().withColor(0xFC7703));
                    player.playSound(SoundEvents.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
                }
            }

            return true;
        });
    }

    public static void sendEndstoneGolemNotificationToClient() {
        if (DragonHelperConfig.get().notifications.notifyForEndstoneProtector) {
            Utils.sendTitleToClient(Text.of("Endstone Protector Spawn!").copy().withColor(0xA903FC));
            if (MinecraftClient.getInstance().player != null) MinecraftClient.getInstance().player.playSound(SoundEvents.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
        }
    }

}

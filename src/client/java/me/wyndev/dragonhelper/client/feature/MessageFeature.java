package me.wyndev.dragonhelper.client.feature;

import me.wyndev.dragonhelper.client.DragonHelperClient;
import me.wyndev.dragonhelper.client.Utils;
import me.wyndev.dragonhelper.client.config.DragonHelperConfig;
import me.wyndev.dragonhelper.client.config.ServerConfig;
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

            String server = Utils.getClientServer();
            if (server == null) return true;
            String dragonDropText = (String) ServerConfig.instance.getServerFeatureValue(server, Feature.DRAGON_DROP_CONTAINS_TEXT);
            String eyePlaceText = (String) ServerConfig.instance.getServerFeatureValue(server, Feature.EYE_PLACE_CONTAINS_TEXT);
            String lootNumText = (String) ServerConfig.instance.getServerFeatureValue(server, Feature.LOOTNUM_CONTAINS_TEXT);
            String pvpProtectionText = (String) ServerConfig.instance.getServerFeatureValue(server, Feature.PVP_PROTECTION_MESSAGE_CONTAINS);
            String dragonSpawnText = (String) ServerConfig.instance.getServerFeatureValue(server, Feature.DRAGON_SPAWN_CONTAINS_TEXT);
            Object hasInfernal = ServerConfig.instance.getServerFeatureValue(server, Feature.HAS_INFERNAL_DRAGONS); //MUST CAST TO OBJ B/C BOOL CANNOT BE NULL

            //check for message
            //dragon drop message
            if (dragonDropText != null && rawLowerText.contains(dragonDropText)) {
                if (!rawText.contains(displayName)) {
                    if (DragonHelperConfig.get().messages.hideOtherPlayerDrops) return false; //hide other user drop messages
                } else {
                    //does contain name, add 1 to dragons killed
                    DragonHelperClient.getPlayerData().setDragonKills(DragonHelperClient.getPlayerData().getDragonKills() + 1);
                }
            }

            //eye placement message
            if (eyePlaceText != null && rawLowerText.contains(eyePlaceText) && DragonHelperConfig.get().messages.hideSummoningEyePlace) {
                return false; //hide summoning eye placement messages
            }

            //loot number message
            if (lootNumText != null && rawLowerText.contains(lootNumText) && DragonHelperConfig.get().messages.hideLootnumText) {
                return false; //hide loot messages
            }

            //pvp message
            if (pvpProtectionText != null && rawLowerText.contains(pvpProtectionText) && DragonHelperConfig.get().messages.hidePvpProtectionText) {
                return false; //hide pvp protection message
            }

            //dragon notifier
            if (rawLowerText.contains("dragon") && dragonSpawnText != null && rawLowerText.contains(dragonSpawnText)) {
                //infernal and superior notification
                if (rawLowerText.contains("superior") && DragonHelperConfig.get().notifications.notifyForSuperior) {
                    Utils.sendTitleToClient(Text.of("Superior Dragon Spawn!").copy().withColor(0xFF0000));
                    player.playSound(SoundEvents.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
                } else if (hasInfernal != null && (Boolean)hasInfernal && rawLowerText.contains("infernal") && DragonHelperConfig.get().notifications.notifyForInfernal) {
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

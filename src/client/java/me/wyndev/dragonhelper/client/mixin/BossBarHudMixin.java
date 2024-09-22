package me.wyndev.dragonhelper.client.mixin;

import me.wyndev.dragonhelper.client.DragonHelperClient;
import me.wyndev.dragonhelper.client.feature.MessageFeature;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BossBarHud.class)
public class BossBarHudMixin {

    @ModifyVariable(method = "render", at = @At(value = "STORE"), ordinal = 0)
    public ClientBossBar onRender(ClientBossBar bar) {
        //return if the endstone protector has been recently spawned
        if (System.currentTimeMillis() - DragonHelperClient.getServerDataTracker().getLastSpawnedEndstoneProtectorTime() < 50_000) return bar;

        //set the new last spawn time
        DragonHelperClient.getServerDataTracker().setLastSpawnedEndstoneProtectorTime(System.currentTimeMillis());

        //send the spawn notification to the client
        if (bar.getName().getString().toLowerCase().contains("protector")) {
            MessageFeature.sendEndstoneGolemNotificationToClient();
        }

        return bar;
    }

}

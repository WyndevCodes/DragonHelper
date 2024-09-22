package me.wyndev.dragonhelper.client.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

@Config(name = "dragonhelper")
public class DragonHelperConfig implements ConfigData {

    public static void register() {
        AutoConfig.register(DragonHelperConfig.class, GsonConfigSerializer::new);
    }

    public static DragonHelperConfig get() {
        return AutoConfig.getConfigHolder(DragonHelperConfig.class).getConfig();
    }

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Gui.Tooltip
    public MessagesConfig messages = new MessagesConfig();

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Gui.Tooltip
    public AutoSellConfig autosell = new AutoSellConfig();

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Gui.Tooltip
    public NotificationConfig notifications = new NotificationConfig();

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Gui.Tooltip
    public UIConfig ui = new UIConfig();

    public static class MessagesConfig {
        @ConfigEntry.Gui.Excluded
        public String label = "Server Message Settings";

        public boolean hideOtherPlayerDrops = true;
        public boolean hideSummoningEyePlace = false;
        public boolean hideLootnumText = false;
        public boolean hidePvpProtectionText = true;
    }

    public static class AutoSellConfig {
        @ConfigEntry.Gui.Excluded
        public String label = "Auto Sell Settings";

        public boolean normalAutoSell = true;
        public boolean specialAutoSell = false;
        public boolean autoSellEyes = false;
        @ConfigEntry.BoundedDiscrete(min = 10, max = 60)
        public int autoSellInterval = 20;
    }

    public static class NotificationConfig {
        @ConfigEntry.Gui.Excluded
        public String label = "Notification Settings";

        public boolean notifyForSuperior = true;
        public boolean notifyForInfernal = true;
        public boolean notifyForEndstoneProtector = true;
    }

    public static class UIConfig {
        @ConfigEntry.Gui.Excluded
        public String label = "UI Settings";

        public boolean zealotKillCounter = true;
        public boolean dragonKillCounter = true;
        public boolean endstoneProtectorKillCounter = true;
        public boolean endstoneProtectorTimer = true;
        public boolean rngMeterTracker = true;
        public boolean zombieSlayerLevelTracker = true;
    }

}

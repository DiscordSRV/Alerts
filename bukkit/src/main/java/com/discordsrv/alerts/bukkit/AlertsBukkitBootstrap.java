package com.discordsrv.alerts.bukkit;

import com.discordsrv.alerts.logger.JavaUtilLogger;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused") // Used by Bukkit
public class AlertsBukkitBootstrap extends JavaPlugin {

    private final DiscordSRVAlertsBukkit alerts;

    public AlertsBukkitBootstrap() {
        this.alerts = new DiscordSRVAlertsBukkit(
                getDataFolder().toPath(),
                new JavaUtilLogger(getLogger()),
                this
        );
    }

    @Override
    public void onEnable() {
        alerts.enable();
    }
}

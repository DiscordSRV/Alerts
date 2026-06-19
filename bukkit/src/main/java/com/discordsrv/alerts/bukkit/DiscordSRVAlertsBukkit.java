package com.discordsrv.alerts.bukkit;

import com.discordsrv.alerts.DiscordSRVAlerts;
import com.discordsrv.alerts.bukkit.command.BukkitCommand;
import com.discordsrv.alerts.bukkit.eventhook.BukkitEventHook;
import com.discordsrv.alerts.logger.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;

public class DiscordSRVAlertsBukkit extends DiscordSRVAlerts {

    private final JavaPlugin bootstrap;

    public DiscordSRVAlertsBukkit(Path dataDirectory, Logger logger, JavaPlugin bootstrap) {
        super(dataDirectory, logger);
        this.bootstrap = bootstrap;
    }

    @Override
    public void enable() {
        eventHookRegistry().register(
                Event.class,
                (alertHandler, eventClass) -> new BukkitEventHook(alertHandler, eventClass, this)
        );
        staticContexts().put("server", Bukkit.getServer());

        PluginCommand pluginCommand = bootstrap().getCommand("discordsrvalerts");
        if (pluginCommand != null) {
            BukkitCommand bukkitCommand = new BukkitCommand(this);
            pluginCommand.setExecutor(bukkitCommand);
            pluginCommand.setTabCompleter(bukkitCommand);
        }

        super.enable();
    }

    @Override
    public Class<?>[] getCommandEventClasses() {
        return new Class[] {
                PlayerCommandPreprocessEvent.class,
                ServerCommandEvent.class
        };
    }

    private Command getCommand(String alias) {
        PluginCommand pluginCommand = bootstrap.getServer().getPluginCommand(alias);
        if (pluginCommand != null) {
            return pluginCommand;
        }

        return bootstrap.getServer().getCommandMap().getCommand(alias);
    }

    @Override
    public boolean isSameCommand(String executedCommand, String configuredCommand) {
        Command executedBukkitCommand = getCommand(executedCommand);
        Command configuredBukkitCommand = getCommand(configuredCommand);
        if (executedBukkitCommand == configuredBukkitCommand) {
            return true;
        }

        return super.isSameCommand(executedCommand, configuredCommand);
    }

    public JavaPlugin bootstrap() {
        return bootstrap;
    }

}

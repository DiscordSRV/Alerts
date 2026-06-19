/*
 * This file is part of DiscordSRV-Alerts, licensed under the GPLv3 License
 * Copyright (c) 2026 Henri "Vankka" Schubin and DiscordSRV-Alerts contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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

import java.net.URL;
import java.nio.file.Path;

public class DiscordSRVAlertsBukkit extends DiscordSRVAlerts {

    private final AlertsBukkitBootstrap bootstrap;

    public DiscordSRVAlertsBukkit(Path dataDirectory, Logger logger, AlertsBukkitBootstrap bootstrap) {
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

    @Override
    public URL[] getExampleAlertResources() {
        return new URL[] {
            bootstrap.classLoader().getResource("alert-examples/example.yaml"),
            bootstrap.classLoader().getResource("alert-examples/v1compat.yaml")
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

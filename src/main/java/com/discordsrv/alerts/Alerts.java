/*
 * Alerts: A bukkit plugin to send customizable alerts to Discord driven by events and commands
 * Copyright (C) 2021 Alerts contributors
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

package com.discordsrv.alerts;

import com.discordsrv.alerts.hook.DiscordSRVHook;
import com.discordsrv.alerts.listener.AlertListener;
import com.discordsrv.alerts.provider.AvatarProvider;
import com.discordsrv.alerts.provider.PlayerProvider;
import com.discordsrv.alerts.provider.TimeProvider;
import github.scarsz.configuralize.DynamicConfig;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.commons.lang3.exception.ExceptionUtils;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public final class Alerts extends JavaPlugin {

    private DynamicConfig config;

    private AlertListener listener;

    private AvatarProvider avatarProvider;
    private PlayerProvider playerProvider;
    private TimeProvider timeProvider;

    private DiscordSRVHook dsrvHook;

    @Override
    public void onEnable() {
        config = new DynamicConfig();
        config.addSource(Alerts.class, "config", new File(getDataFolder(), "config.yml"));

        try {
            config.saveAllDefaults();
        } catch (IOException e) {
            error("Failed to save default config files", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        try {
            config.loadAll();
        } catch (Exception e) {
            error("Failed to load config", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        listener = new AlertListener(this);
        listener.reloadAlerts();

        this.avatarProvider = new AvatarProvider(this);
        this.playerProvider = new PlayerProvider(this);
        this.timeProvider = new TimeProvider(this);

        if (getServer().getPluginManager().getPlugin("DiscordSRV") != null) {
            dsrvHook = new DiscordSRVHook(this);
        }
    }

    @Override
    public void onDisable() {
        if (listener != null) {
            listener.unregister();
        }
    }

    public DynamicConfig config() {
        return config;
    }

    public AlertListener getListener() {
        return listener;
    }

    public AvatarProvider getAvatarProvider() {
        return avatarProvider;
    }

    public PlayerProvider getPlayerProvider() {
        return playerProvider;
    }

    public TimeProvider getTimeProvider() {
        return timeProvider;
    }

    public Optional<DiscordSRVHook> getDSRVHook() {
        return Optional.ofNullable(dsrvHook);
    }

    public void info(String message) {
        getLogger().info(message);
    }

    public void error(String message) {
        getLogger().severe(message);
    }

    public void error(Throwable throwable) {
        getLogger().severe(ExceptionUtils.getMessage(throwable));
        for (String stackFrame : ExceptionUtils.getStackFrames(throwable)) {
            getLogger().severe(stackFrame);
        }
    }

    public void error(String message, Throwable throwable) {
        error(message);
        error(throwable);
    }

    public void debug(String message) {
        if (DiscordSRV.config().getIntElse("DebugLevel", 0) > 0) {
            getLogger().info("[DEBUG] " + message);
        }
    }
}

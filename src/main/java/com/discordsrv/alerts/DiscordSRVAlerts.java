/*
 * DiscordSRVAlerts: A DiscordSRV addon to provide customizable alerts based on events and commands
 * Copyright (C) 2021 DiscordSRVAlerts contributors
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

import com.discordsrv.alerts.listener.AlertListener;
import github.scarsz.configuralize.DynamicConfig;
import github.scarsz.configuralize.Language;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.ConfigReloadedEvent;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.dependencies.commons.lang3.exception.ExceptionUtils;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class DiscordSRVAlerts extends JavaPlugin {

    private DynamicConfig config;
    private AlertListener listener;

    @Override
    public void onEnable() {
        config = new DynamicConfig();
        config.addSource(DiscordSRVAlerts.class, "config", new File(getDataFolder(), "config.yml"));

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

        Language dsrvLanguage = DiscordSRV.config().getLanguage();
        if (config.isLanguageAvailable(dsrvLanguage)) {
            config.setLanguage(dsrvLanguage);
        }

        DiscordSRV.api.subscribe(this);
    }

    @Subscribe
    public void onDiscordSRVReady(DiscordReadyEvent event) {
        listener = new AlertListener(this);
        DiscordUtil.getJda().addEventListener(listener);
    }

    @Subscribe
    public void onDiscordSRVReload(ConfigReloadedEvent event) {
        if (listener != null) {
            listener.reloadAlerts();
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

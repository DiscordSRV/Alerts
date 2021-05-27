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

package com.discordsrv.alerts.listener;

import com.discordsrv.alerts.Alerts;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.GenericEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.EventListener;
import github.scarsz.discordsrv.util.DiscordUtil;

import javax.annotation.Nonnull;

public class DiscordSRVListener implements EventListener {

    private final Alerts plugin;

    public DiscordSRVListener(Alerts plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onDiscordSRVReady(DiscordReadyEvent event) {
        DiscordUtil.getJda().addEventListener(this);
    }

    @Override
    public void onEvent(@Nonnull GenericEvent genericEvent) {
        AlertListener listener = plugin.getListener();
        if (listener != null) {
            listener.runAlertsForEvent(genericEvent);
        }
    }
}

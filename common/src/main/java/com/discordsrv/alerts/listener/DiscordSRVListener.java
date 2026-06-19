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

package com.discordsrv.alerts.listener;

import com.discordsrv.alerts.DiscordSRVAlerts;
import com.discordsrv.api.eventbus.Subscribe;
import com.discordsrv.dependencies.net.dv8tion.jda.api.JDA;
import com.discordsrv.dependencies.net.dv8tion.jda.api.events.session.ReadyEvent;

public class DiscordSRVListener {

    private final DiscordSRVAlerts alerts;

    public DiscordSRVListener(DiscordSRVAlerts alerts) {
        this.alerts = alerts;
    }

    @Subscribe
    public void onReady(ReadyEvent event) {
        JDA jda = event.getJDA();
        alerts.staticContexts().put("jda", jda);
    }
}

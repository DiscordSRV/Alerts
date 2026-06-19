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

package com.discordsrv.alerts.eventhook;

import com.discordsrv.alerts.alert.AlertReceiver;
import com.discordsrv.api.DiscordSRV;
import com.discordsrv.api.eventbus.EventListener;
import com.discordsrv.dependencies.net.dv8tion.jda.api.events.GenericEvent;

import java.util.Map;

public class JDAEventHook extends EventHook<GenericEvent> {

    private final DiscordSRV discordSRV;
    private EventListener eventListener;

    public JDAEventHook(DiscordSRV discordSRV, AlertReceiver alertReceiver, Class<? extends GenericEvent> eventClass) {
        super(alertReceiver, eventClass);
        this.discordSRV = discordSRV;
    }

    @Override
    public void register() {
        eventListener = discordSRV.eventBus().subscribe(eventClass, this::receiveEvent);
    }

    @Override
    public void unregister() {
        if (eventListener == null) {
            return;
        }

        discordSRV.eventBus().unsubscribe(eventListener);
        eventListener = null;
    }

    @Override
    public boolean isCancelled(GenericEvent event) {
        return false;
    }

    @Override
    public void collectContext(GenericEvent event, Map<String, Object> context) {}
}

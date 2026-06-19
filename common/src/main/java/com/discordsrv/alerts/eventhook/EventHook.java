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
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class EventHook<E> {

    protected final AlertReceiver alertReceiver;
    protected final Class<? extends E> eventClass;

    public EventHook(AlertReceiver alertReceiver, @NotNull Class<? extends E> eventClass) {
        this.alertReceiver = alertReceiver;
        this.eventClass = eventClass;
    }

    public abstract void register();
    public abstract void unregister();

    public abstract boolean isCancelled(E event);

    public abstract void collectContext(E event, Map<String, Object> context);

    protected final void receiveEvent(E event) {
        Map<String, Object> context = new LinkedHashMap<>();
        collectContext(event, context);

        boolean cancelled = isCancelled(event);
        alertReceiver.receiveEvent(event, context, cancelled);
    }

}

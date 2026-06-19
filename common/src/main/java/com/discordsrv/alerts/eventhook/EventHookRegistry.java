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
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class EventHookRegistry {

    private final Map<Class<?>, BiFunction<AlertReceiver, Class<?>, EventHook<?>>> eventHookConstructors = new LinkedHashMap<>();

    @SuppressWarnings("unchecked")
    public <E> void register(Class<E> baseEventClass, BiFunction<AlertReceiver, Class<E>, EventHook<E>> constructor) {
        eventHookConstructors.put(baseEventClass, (BiFunction<AlertReceiver, Class<?>, EventHook<?>>) (Object) constructor);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <E> EventHook<E> create(AlertReceiver alertReceiver, Class<E> eventClass) {
        for (Map.Entry<Class<?>, BiFunction<AlertReceiver, Class<?>, EventHook<?>>> entry : eventHookConstructors.entrySet()) {
            if (!entry.getKey().isAssignableFrom(eventClass)) {
                continue;
            }

            return (EventHook<E>) entry.getValue().apply(alertReceiver, eventClass);
        }
        return null;
    }
}

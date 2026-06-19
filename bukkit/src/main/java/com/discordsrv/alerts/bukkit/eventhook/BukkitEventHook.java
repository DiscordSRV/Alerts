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

package com.discordsrv.alerts.bukkit.eventhook;

import com.discordsrv.alerts.alert.AlertReceiver;
import com.discordsrv.alerts.bukkit.DiscordSRVAlertsBukkit;
import com.discordsrv.alerts.eventhook.EventHook;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.RegisteredListener;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Map;

public class BukkitEventHook extends EventHook<Event> implements Listener, EventExecutor {

    private final DiscordSRVAlertsBukkit plugin;
    private final HandlerList handlerList;

    public BukkitEventHook(AlertReceiver alertReceiver, Class<? extends Event> eventClass, DiscordSRVAlertsBukkit plugin) {
        super(alertReceiver, eventClass);
        this.plugin = plugin;
        this.handlerList = getHandlerList(eventClass);
    }

    private static HandlerList getHandlerList(@NotNull Class<?> eventClass) {
        Class<?> checkClass = eventClass;
        while (checkClass != null) {
            try {
                Method method = checkClass.getMethod("getHandlerList");

                return (HandlerList) method.invoke(null);
            } catch (ReflectiveOperationException ignored) {}

            checkClass = checkClass.getSuperclass();
        }
        throw new IllegalStateException(eventClass.getName() + " does not have a working getHandlerList method");
    }

    @Override
    public void register() {
        handlerList.register(new RegisteredListener(
                this,
                this,
                EventPriority.MONITOR,
                plugin.bootstrap(),
                false
        ));
    }

    @Override
    public void unregister() {
        handlerList.unregister(this);
    }

    @Override
    public boolean isCancelled(Event event) {
        return event instanceof Cancellable && ((Cancellable) event).isCancelled();
    }

    @Override
    public void collectContext(Event event, Map<String, Object> context) {
        Player player = null;
        if (event instanceof PlayerEvent) {
            player = ((PlayerEvent) event).getPlayer();
        } else {
            // v1 backwards compatability

            try {
                Method getPlayerMethod = event.getClass().getMethod("getPlayer");
                if (getPlayerMethod.getReturnType().equals(Player.class)) {
                    player = (Player) getPlayerMethod.invoke(event);
                }
            } catch (ReflectiveOperationException ignored) {}
        }
        if (player != null) {
            context.put("player", player);
        }

        String command = null;
        CommandSender commandSender = null;
        if (event instanceof PlayerCommandPreprocessEvent commandEvent) {
            command = commandEvent.getMessage().substring(1);
            commandSender = commandEvent.getPlayer();
        } else if (event instanceof ServerCommandEvent commandEvent) {
            command = commandEvent.getCommand();
            commandSender = commandEvent.getSender();
        }

        if (command != null) {
            context.put("command", command);
            context.put("sender", commandSender);
        }
    }

    @Override
    public void execute(@NotNull Listener listener, @NotNull Event event) {
        if (!event.getClass().isAssignableFrom(eventClass)) {
            // HandlerLists may be inherited (and shared between multiple events)
            return;
        }
        receiveEvent(event);
    }

    // For debugging if something logs this "Listener" via toString
    @Override
    public String toString() {
        return getClass().getName() + "(" + eventClass.getName() + ")";
    }
}

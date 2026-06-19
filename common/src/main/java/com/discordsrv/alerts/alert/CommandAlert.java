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

package com.discordsrv.alerts.alert;

import java.util.regex.Pattern;

public class CommandAlert {

    private final String mainCommand;
    private final Pattern parameterMatcher;
    private final AlertHandler alertHandler;

    public CommandAlert(String configuredCommand, AlertHandler alertHandler) {
        String[] parts = configuredCommand.split(" ", 2);

        this.mainCommand = parts[0];
        this.parameterMatcher = parts.length == 1
                                ? Pattern.compile("(.*)")
                                : Pattern.compile(Pattern.quote(parts[1].trim()) + "(?: (.+))?");
        this.alertHandler = alertHandler;
    }

    public String mainCommand() {
        return mainCommand;
    }

    public Pattern parameterMatcher() {
        return parameterMatcher;
    }

    public AlertHandler alertHandler() {
        return alertHandler;
    }
}

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

package com.discordsrv.alerts.util;

import github.scarsz.discordsrv.DiscordSRV;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class MessageUtil {

    /**
     * Pattern for capturing both ampersand and the legacy section sign color codes.
     */
    public static final Pattern STRIP_PATTERN = Pattern.compile("(?<!<@)[&§](?i)[0-9a-fklmnorx]");

    /**
     * Pattern for capturing section sign color codes.
     */
    public static final Pattern STRIP_SECTION_ONLY_PATTERN = Pattern.compile("(?<!<@)§(?i)[0-9a-fklmnorx]");

    /**
     * Strips the given String of legacy Minecraft coloring (both & and §).
     *
     * @param text the given String to strip colors and formatting from
     * @return the given String with coloring and formatting stripped
     * @see #stripLegacy(String)
     */
    public static String strip(String text) {
        return stripLegacy(text);
    }

    /**
     * Strip the given String of legacy Minecraft coloring (both & and §). Useful for sending things to Discord.
     *
     * @param text the given String to strip colors from
     * @return the given String with coloring stripped
     * @see #STRIP_PATTERN
     * @see #stripLegacySectionOnly(String)
     */
    public static String stripLegacy(String text) {
        if (StringUtils.isBlank(text)) {
            DiscordSRV.debug("Tried stripping blank message");
            return "";
        }

        return STRIP_PATTERN.matcher(text).replaceAll("");
    }

    /**
     * Strip the given String of legacy Minecraft coloring (§ only). Useful for sending things to Discord.
     *
     * @param text the given String to strip colors from
     * @return the given String with coloring stripped
     * @see #STRIP_SECTION_ONLY_PATTERN
     */
    public static String stripLegacySectionOnly(String text) {
        return STRIP_SECTION_ONLY_PATTERN.matcher(text).replaceAll("");
    }
}

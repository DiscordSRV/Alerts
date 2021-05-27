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

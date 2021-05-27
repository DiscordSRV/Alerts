package com.discordsrv.alerts.util;

public class DiscordUtil {

    /**
     * Return the given String with Markdown escaped. Useful for sending things to Discord.
     * @param text String to escape markdown in
     * @return String with markdown escaped
     */
    public static String escapeMarkdown(String text) {
        return text == null ? "" : text.replace("_", "\\_").replace("*", "\\*").replace("~", "\\~").replace("|", "\\|").replace(">", "\\>").replace("`", "\\`");
    }
}

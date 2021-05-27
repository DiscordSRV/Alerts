package com.discordsrv.alerts.provider;

import com.discordsrv.alerts.Alerts;
import com.discordsrv.alerts.util.NMSUtil;
import github.scarsz.discordsrv.util.PlaceholderUtil;
import github.scarsz.discordsrv.util.PlayerUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

public class AvatarProvider {

    private final Alerts plugin;
    private boolean offlineUuidAvatarUrlNagged = false;

    public AvatarProvider(Alerts plugin) {
        this.plugin = plugin;
    }

    public String getAvatarUrl(String username, UUID uuid) {
        String avatarUrl = constructAvatarUrl(username, uuid, "");
        avatarUrl = PlaceholderUtil.replacePlaceholders(avatarUrl);
        return avatarUrl;
    }
    private String getAvatarUrl(OfflinePlayer player) {
        if (player.isOnline()) {
            return getAvatarUrl(player.getPlayer());
        } else {
            String avatarUrl = constructAvatarUrl(player.getName(), player.getUniqueId(), "");
            avatarUrl = PlaceholderUtil.replacePlaceholdersToDiscord(avatarUrl, player);
            return avatarUrl;
        }
    }
    public String getAvatarUrl(Player player) {
        String avatarUrl = constructAvatarUrl(player.getName(), player.getUniqueId(), NMSUtil.getTexture(player));
        avatarUrl = PlaceholderUtil.replacePlaceholdersToDiscord(avatarUrl, player);
        return avatarUrl;
    }
    private String constructAvatarUrl(String username, UUID uuid, String texture) {
        boolean offline = uuid == null || PlayerUtil.uuidIsOffline(uuid);
        OfflinePlayer player = null;
        if (StringUtils.isNotBlank(username) && offline) {
            // resolve username to player/uuid
            //TODO resolve name to online uuid when offline player is present
            // (can't do it by calling Bukkit.getOfflinePlayer(username).getUniqueId() because bukkit just returns the offline-mode CraftPlayer)
            player = Bukkit.getOfflinePlayer(username);
            uuid = player.getUniqueId();
            offline = PlayerUtil.uuidIsOffline(uuid);
        }
        if (StringUtils.isBlank(username) && uuid != null) {
            // resolve uuid to player/username
            player = Bukkit.getOfflinePlayer(uuid);
            username = player.getName();
        }
        if (StringUtils.isBlank(texture) && player != null && player.isOnline()) {
            // grab texture placeholder from player if online
            texture = NMSUtil.getTexture(player.getPlayer());
        }

        String configAvatarUrl = plugin.config().getString("AvatarUrl");
        String avatarUrl = configAvatarUrl;
        String defaultUrl = "https://crafatar.com/avatars/{uuid-nodashes}.png?size={size}&overlay#{texture}";
        String offlineUrl = "https://cravatar.eu/helmavatar/{username}/{size}.png#{texture}";

        if (StringUtils.isBlank(avatarUrl)) {
            avatarUrl = !offline ? defaultUrl : offlineUrl;
        }

        if (offline && !avatarUrl.contains("{username}")) {
            boolean defaultValue = avatarUrl.equals(defaultUrl);
            if (defaultValue) {
                // Using default value while in offline mode -> use offline url
                avatarUrl = offlineUrl;
            }

            if (!offlineUuidAvatarUrlNagged) {
                plugin.error("Your AvatarUrl does not contain the {username} placeholder even though this server is using offline UUIDs.");
                plugin.error(offlineUrl + " will be used because the default value does not support offline mode servers");
                plugin.error("You should set your AvatarUrl to " + offlineUrl + " (or another url that supports usernames) "
                        + (defaultValue ? "to get rid of this error" : " to get avatars to work."));
                offlineUuidAvatarUrlNagged = true;
            }
        }

        if (username.startsWith("*")) {
            // geyser adds * to beginning of it's usernames
            username = username.substring(1);
        }
        try {
            username = URLEncoder.encode(username, "utf8");
        } catch (UnsupportedEncodingException ignored) {}

        avatarUrl = avatarUrl
                .replace("{texture}", texture != null ? texture : "")
                .replace("{username}", username)
                .replace("{uuid}", uuid != null ? uuid.toString() : "")
                .replace("{uuid-nodashes}", uuid.toString().replace("-", ""))
                .replace("{size}", "128");

        plugin.debug("Constructed avatar url: " + avatarUrl + " from " + configAvatarUrl);
        plugin.debug("Avatar url is for " + (offline ? "**offline** " : "") + "uuid: " + uuid + ". The texture is: " + texture);

        return avatarUrl;
    }
}

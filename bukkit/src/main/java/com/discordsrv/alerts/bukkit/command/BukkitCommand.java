package com.discordsrv.alerts.bukkit.command;

import com.discordsrv.alerts.bukkit.DiscordSRVAlertsBukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class BukkitCommand implements TabExecutor {

    private final DiscordSRVAlertsBukkit alerts;

    public BukkitCommand(DiscordSRVAlertsBukkit alerts) {
        this.alerts = alerts;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String @NonNull [] args
    ) {
        List<String> arguments = Arrays.asList(args);
        if (arguments.isEmpty() || arguments.size() == 1 && arguments.get(0).equalsIgnoreCase("version")) {
            sender.sendMessage("Running DiscordSRV-Alerts " + alerts.bootstrap().getDescription().getVersion());
            return true;
        }
        if (arguments.size() == 1 && arguments.get(0).equalsIgnoreCase("reload")) {
            alerts.reload();
            sender.sendMessage("Reload successful");
            return true;
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String @NotNull [] args
    ) {
        if (args.length <= 1) {
            String argument = args.length == 1 ? args[0].toLowerCase() : "";
            return Stream.of("version", "reload")
                    .filter(argument::startsWith)
                    .toList();
        }
        return Collections.emptyList();
    }
}

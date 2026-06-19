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

package com.discordsrv.alerts.alert;

import com.discordsrv.alerts.DiscordSRVAlerts;

import java.util.*;
import java.util.regex.Matcher;

public class CommandAlertAggregator implements AlertReceiver {

    private final DiscordSRVAlerts alerts;
    private final List<CommandAlert> commandAlerts;

    public CommandAlertAggregator(DiscordSRVAlerts alerts) {
        this.alerts = alerts;
        this.commandAlerts = new ArrayList<>();
    }

    public List<CommandAlert> commandAlerts() {
        return commandAlerts;
    }

    @Override
    public void receiveEvent(Object event, Map<String, Object> context, boolean cancelled) {
        Object commandContext = context.get("command");
        if (!(commandContext instanceof String command)) {
            return;
        }

        List<String> arguments = new ArrayList<>(Arrays.asList(command.split(" ")));
        String executedMainCommand = arguments.remove(0);
        String allArguments = String.join(" ", arguments);

        for (CommandAlert commandAlert : commandAlerts) {
            AlertHandler alertHandler = commandAlert.alertHandler();
            if (cancelled && alertHandler.ignoringCancelled()) {
                continue;
            }

            String configuredMainCommand = commandAlert.mainCommand();
            if (!alerts.isSameCommand(executedMainCommand, configuredMainCommand)) {
                continue;
            }

            Matcher matcher = commandAlert.parameterMatcher().matcher(allArguments);
            if (!matcher.matches()) {
                continue;
            }

            String allOverflowParameters = matcher.group(1);
            if (allOverflowParameters == null) {
                allOverflowParameters = "";
            }

            List<String> overflowArguments = new ArrayList<>(Arrays.asList(allOverflowParameters.split(" ")));

            Map<String, Object> newContext = new HashMap<>(context);
            newContext.put("alias", executedMainCommand);
            newContext.put("arguments", arguments);
            newContext.put("allArguments", allArguments);
            newContext.put("overflowArguments", overflowArguments);
            newContext.put("allOverflowArguments", allOverflowParameters);

            // v1 backwards compatability
            newContext.put("allArgs", allArguments);
            newContext.put("args", arguments);

            alertHandler.receiveEvent(event, newContext, cancelled);
        }
    }
}

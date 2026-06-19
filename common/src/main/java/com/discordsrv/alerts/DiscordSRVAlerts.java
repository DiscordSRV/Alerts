package com.discordsrv.alerts;

import com.discordsrv.alerts.alert.AlertHandler;
import com.discordsrv.alerts.alert.CommandAlert;
import com.discordsrv.alerts.alert.CommandAlertAggregator;
import com.discordsrv.alerts.config.alert.AlertConfig;
import com.discordsrv.alerts.config.AlertConfigManager;
import com.discordsrv.alerts.config.alertfile.AlertFileConfig;
import com.discordsrv.alerts.eventhook.DiscordSRVEventHook;
import com.discordsrv.alerts.eventhook.EventHook;
import com.discordsrv.alerts.eventhook.EventHookRegistry;
import com.discordsrv.alerts.eventhook.JDAEventHook;
import com.discordsrv.alerts.listener.DiscordSRVListener;
import com.discordsrv.alerts.logger.Logger;
import com.discordsrv.api.DiscordSRV;
import com.discordsrv.api.events.Event;
import com.discordsrv.dependencies.net.dv8tion.jda.api.events.GenericEvent;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

public abstract class DiscordSRVAlerts {

    private final EventHookRegistry eventHookRegistry = new EventHookRegistry();
    private final AlertConfigManager configManager = new AlertConfigManager();
    private final CommandAlertAggregator commandAlertAggregator = new CommandAlertAggregator(this);
    private final Map<String, Object> staticContexts = new LinkedHashMap<>();
    private final SpelExpressionParser spelExpressionParser = new SpelExpressionParser();

    private final Path dataDirectory;
    private final Logger logger;

    private final List<EventHook<?>> registeredHooks = new ArrayList<>();

    public DiscordSRVAlerts(Path dataDirectory, Logger logger) {
        this.dataDirectory = dataDirectory;
        this.logger = logger;
    }

    public void reload() {
        List<AlertFileConfig> fileConfigs;
        try {
            fileConfigs = configManager.loadConfigs(dataDirectory);
        } catch (Exception e) {
            logger.error("Failed to reload configuration", e);
            return;
        }

        for (EventHook<?> registeredHook : registeredHooks) {
            registeredHook.unregister();
        }

        for (Class<?> eventClass : getCommandEventClasses()) {
            EventHook<?> eventHook = eventHookRegistry().create(commandAlertAggregator, eventClass);
            if (eventHook == null) {
                logger.warning("Command event class " + eventClass.getName() + " is not a compatible event type");
                continue;
            }

            registerHook(eventHook);
        }

        commandAlertAggregator.commandAlerts().clear();

        for (AlertFileConfig fileConfig : fileConfigs) {
            for (AlertConfig config : fileConfig.alerts) {
                AlertHandler alertHandler = new AlertHandler(this, config, fileConfig);

                for (String trigger : config.triggers) {
                    if (trigger.startsWith("/")) {
                        CommandAlert commandAlert = new CommandAlert(trigger.substring(1), alertHandler);
                        commandAlertAggregator.commandAlerts().add(commandAlert);
                        continue;
                    }

                    Class<?> eventClass;
                    try {
                        eventClass = Class.forName(trigger);
                    } catch (ClassNotFoundException ignored) {
                        logger.error("Alert class " + trigger + " not found");
                        continue;
                    }

                    EventHook<?> eventHook = eventHookRegistry().create(alertHandler, eventClass);
                    if (eventHook == null) {
                        logger.error("Alert class " + eventClass.getName() + " is not a compatible event type");
                        continue;
                    }

                    registerHook(eventHook);
                }
            }
        }
    }

    private void registerHook(EventHook<?> eventHook) {
        eventHook.register();
        registeredHooks.add(eventHook);
    }

    public void enable() {
        try {
            Class.forName("com.discordsrv.api.DiscordSRV");
            DiscordSRV discordSRV = DiscordSRV.get();

            eventHookRegistry.register(
                    Event.class,
                    (alertHandler, eventClass) -> new DiscordSRVEventHook(discordSRV, alertHandler, eventClass)
            );
            eventHookRegistry.register(
                    GenericEvent.class,
                    (alertHandler, eventClass) -> new JDAEventHook(discordSRV, alertHandler, eventClass)
            );

            staticContexts.put("discordsrv", discordSRV);
            staticContexts.put("jda", discordSRV.jda());

            discordSRV.eventBus().subscribe(new DiscordSRVListener(this));
        } catch (ClassNotFoundException ignored) {}

        reload();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isSameCommand(String executedCommand, String configuredCommand) {
        return executedCommand.equalsIgnoreCase(configuredCommand);
    }

    public void runAsync(Runnable task) {
        ForkJoinPool.commonPool().execute(task);
    }

    public abstract Class<?>[] getCommandEventClasses();

    public EventHookRegistry eventHookRegistry() {
        return eventHookRegistry;
    }

    public AlertConfigManager configManager() {
        return configManager;
    }

    public Map<String, Object> staticContexts() {
        return staticContexts;
    }

    public SpelExpressionParser spelExpressionParser() {
        return spelExpressionParser;
    }

    public Path dataDirectory() {
        return dataDirectory;
    }

    public Logger logger() {
        return logger;
    }

}

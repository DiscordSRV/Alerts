package com.discordsrv.alerts.eventhook;

import com.discordsrv.alerts.alert.AlertReceiver;
import com.discordsrv.api.DiscordSRV;
import com.discordsrv.api.eventbus.EventListener;
import com.discordsrv.dependencies.net.dv8tion.jda.api.events.GenericEvent;

import java.util.Map;

public class JDAEventHook extends EventHook<GenericEvent> {

    private final DiscordSRV discordSRV;
    private EventListener eventListener;

    public JDAEventHook(DiscordSRV discordSRV, AlertReceiver alertReceiver, Class<? extends GenericEvent> eventClass) {
        super(alertReceiver, eventClass);
        this.discordSRV = discordSRV;
    }

    @Override
    public void register() {
        eventListener = discordSRV.eventBus().subscribe(eventClass, this::receiveEvent);
    }

    @Override
    public void unregister() {
        if (eventListener == null) {
            return;
        }

        discordSRV.eventBus().unsubscribe(eventListener);
        eventListener = null;
    }

    @Override
    public boolean isCancelled(GenericEvent event) {
        return false;
    }

    @Override
    public void collectContext(GenericEvent event, Map<String, Object> context) {}
}

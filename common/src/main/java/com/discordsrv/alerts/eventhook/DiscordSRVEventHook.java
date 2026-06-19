package com.discordsrv.alerts.eventhook;

import com.discordsrv.alerts.alert.AlertReceiver;
import com.discordsrv.api.DiscordSRV;
import com.discordsrv.api.eventbus.EventListener;
import com.discordsrv.api.events.Cancellable;
import com.discordsrv.api.events.Event;

import java.util.Map;

public class DiscordSRVEventHook extends EventHook<Event> {

    private final DiscordSRV discordSRV;
    private EventListener eventListener;

    public DiscordSRVEventHook(DiscordSRV discordSRV, AlertReceiver alertReceiver, Class<? extends Event> eventClass) {
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
    public boolean isCancelled(Event event) {
        return event instanceof Cancellable && ((Cancellable) event).isCancelled();
    }

    @Override
    public void collectContext(Event event, Map<String, Object> context) {

    }
}

package com.discordsrv.alerts.eventhook;

import com.discordsrv.alerts.alert.AlertReceiver;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class EventHook<E> {

    protected final AlertReceiver alertReceiver;
    protected final Class<? extends E> eventClass;

    public EventHook(AlertReceiver alertReceiver, @NotNull Class<? extends E> eventClass) {
        this.alertReceiver = alertReceiver;
        this.eventClass = eventClass;
    }

    public abstract void register();
    public abstract void unregister();

    public abstract boolean isCancelled(E event);

    public abstract void collectContext(E event, Map<String, Object> context);

    protected final void receiveEvent(E event) {
        Map<String, Object> context = new LinkedHashMap<>();
        collectContext(event, context);

        boolean cancelled = isCancelled(event);
        alertReceiver.receiveEvent(event, context, cancelled);
    }

}

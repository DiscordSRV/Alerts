package com.discordsrv.alerts.eventhook;

import com.discordsrv.alerts.alert.AlertReceiver;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class EventHookRegistry {

    private final Map<Class<?>, BiFunction<AlertReceiver, Class<?>, EventHook<?>>> eventHookConstructors = new LinkedHashMap<>();

    @SuppressWarnings("unchecked")
    public <E> void register(Class<E> baseEventClass, BiFunction<AlertReceiver, Class<E>, EventHook<E>> constructor) {
        eventHookConstructors.put(baseEventClass, (BiFunction<AlertReceiver, Class<?>, EventHook<?>>) (Object) constructor);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <E> EventHook<E> create(AlertReceiver alertReceiver, Class<E> eventClass) {
        for (Map.Entry<Class<?>, BiFunction<AlertReceiver, Class<?>, EventHook<?>>> entry : eventHookConstructors.entrySet()) {
            if (!entry.getKey().isAssignableFrom(eventClass)) {
                continue;
            }

            return (EventHook<E>) entry.getValue().apply(alertReceiver, eventClass);
        }
        return null;
    }
}

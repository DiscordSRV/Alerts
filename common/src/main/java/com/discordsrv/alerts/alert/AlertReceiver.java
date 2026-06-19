package com.discordsrv.alerts.alert;

import java.util.Map;

public interface AlertReceiver {

    void receiveEvent(Object event, Map<String, Object> context, boolean cancelled);
}

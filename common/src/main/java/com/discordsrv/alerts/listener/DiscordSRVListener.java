package com.discordsrv.alerts.listener;

import com.discordsrv.alerts.DiscordSRVAlerts;
import com.discordsrv.api.eventbus.Subscribe;
import com.discordsrv.dependencies.net.dv8tion.jda.api.JDA;
import com.discordsrv.dependencies.net.dv8tion.jda.api.events.session.ReadyEvent;

public class DiscordSRVListener {

    private final DiscordSRVAlerts alerts;

    public DiscordSRVListener(DiscordSRVAlerts alerts) {
        this.alerts = alerts;
    }

    @Subscribe
    public void onReady(ReadyEvent event) {
        JDA jda = event.getJDA();
        alerts.staticContexts().put("jda", jda);
    }
}

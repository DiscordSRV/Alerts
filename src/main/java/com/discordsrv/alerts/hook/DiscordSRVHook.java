package com.discordsrv.alerts.hook;

import com.discordsrv.alerts.Alerts;
import com.discordsrv.alerts.listener.AlertListener;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Emote;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.events.GenericEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.EventListener;
import github.scarsz.discordsrv.util.DiscordUtil;

import javax.annotation.Nonnull;
import java.util.List;

public class DiscordSRVHook implements EventListener {

    private final Alerts plugin;

    public DiscordSRVHook(Alerts plugin) {
        this.plugin = plugin;
        DiscordSRV.api.subscribe(this);
    }

    @Subscribe
    public void onDiscordSRVReady(DiscordReadyEvent event) {
        DiscordUtil.getJda().addEventListener(this);
    }

    @Override
    public void onEvent(@Nonnull GenericEvent genericEvent) {
        AlertListener listener = plugin.getListener();
        if (listener != null) {
            listener.runAlertsForEvent(genericEvent);
        }
    }

    public DiscordSRV getDiscordSRV() {
        return DiscordSRV.getPlugin();
    }

    public JDA getJDA() {
        return DiscordUtil.getJda();
    }

    public String translateEmotes(String messageToTranslate) {
        return translateEmotes(messageToTranslate, getJDA().getEmotes());
    }
    public String translateEmotes(String messageToTranslate, Guild guild) {
        return translateEmotes(messageToTranslate, guild.getEmotes());
    }
    public String translateEmotes(String messageToTranslate, List<Emote> emotes) {
        for (Emote emote : emotes)
            messageToTranslate = messageToTranslate.replace(":" + emote.getName() + ":", emote.getAsMention());
        return messageToTranslate;
    }
}

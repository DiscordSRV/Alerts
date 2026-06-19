package com.discordsrv.alerts.config.alert;

import com.discordsrv.api.discord.entity.message.SendableDiscordMessage;

import java.util.List;

public class AlertConfig {

    public List<Long> channelIds;
    public List<String> channelNames; // TODO: implement?

    public List<String> triggers;
    public Boolean async;
    public Boolean ignoreCancelled;

    public List<String> conditions;
    public List<String> contextExpressions;
    public SendableDiscordMessage.Builder format;

}

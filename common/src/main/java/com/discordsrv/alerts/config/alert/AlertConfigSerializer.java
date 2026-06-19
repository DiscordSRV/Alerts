package com.discordsrv.alerts.config.alert;

import com.discordsrv.api.discord.entity.message.SendableDiscordMessage;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Collections;

import static com.discordsrv.configurate.serializer.SerializerUtil.resolveNode;

public class AlertConfigSerializer implements TypeSerializer<AlertConfig> {

    @Override
    public AlertConfig deserialize(@NonNull Type type, @NonNull ConfigurationNode node) throws SerializationException {
        AlertConfig config = new AlertConfig();

        ConfigurationNode channelsNode = resolveNode(node, "channels", "Channel");
        try {
            config.channelIds = channelsNode.getList(Long.class);
        } catch (SerializationException ignored) {
            try {
                config.channelNames = channelsNode.getList(String.class);
            } catch (SerializationException ignore) {
                String channelName = channelsNode.getString();
                if (channelName == null) {
                    throw new SerializationException("Invalid alert, no channel provided");
                }
                config.channelNames = Collections.singletonList(channelName);
            }
        }

        ConfigurationNode triggersNode = resolveNode(node, "triggers", "Trigger");
        try {
            config.triggers = triggersNode.getList(String.class);
        } catch (SerializationException ignored) {
            String trigger = triggersNode.getString();
            if (trigger == null) {
                throw new SerializationException("Invalid alert, no trigger provided");
            }
            config.triggers = Collections.singletonList(trigger);
        }

        ConfigurationNode asyncNode = resolveNode(node, "async", "Async");
        if (!asyncNode.virtual()) {
            config.async = asyncNode.getBoolean();
        }

        ConfigurationNode ignoreCancelledNode = resolveNode(node, "ignored-cancelled", "IgnoreCancelled");
        if (!ignoreCancelledNode.virtual()) {
            config.ignoreCancelled = ignoreCancelledNode.getBoolean();
        }

        config.conditions = resolveNode(node, "conditions", "Conditions")
                .getList(String.class, Collections.emptyList());

        config.contextExpressions = resolveNode(node, "context")
                .getList(String.class, Collections.emptyList());

        config.format = node.get(SendableDiscordMessage.Builder.class);

        return config;
    }

    @Override
    public void serialize(@NonNull Type type, @Nullable AlertConfig obj, @NonNull ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(null);
            return;
        }

        if (obj.channelIds != null) {
            node.node("channels").set(obj.channelIds);
        } else if (obj.channelNames != null) {
            node.node("channels").set(obj.channelNames);
        } else {
            throw new SerializationException("Invalid alert, no channels");
        }

        node.node("triggers").set(obj.triggers);
        if (obj.async != null) {
            node.node("async").set(obj.async);
        }
        if (obj.ignoreCancelled != null) {
            node.node("ignore-cancelled").set(obj.ignoreCancelled);
        }

        node.node("conditions").set(obj.conditions);
        node.node("context").set(obj.contextExpressions);
        node.set(SendableDiscordMessage.Builder.class, obj.format);
    }
}

/*
 * This file is part of DiscordSRV-Alerts, licensed under the GPLv3 License
 * Copyright (c) 2026 Henri "Vankka" Schubin and DiscordSRV-Alerts contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.discordsrv.alerts.config.alertfile;

import com.discordsrv.alerts.config.alert.AlertConfig;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.discordsrv.configurate.serializer.SerializerUtil.resolveNode;

public class AlertFileConfigSerializer implements TypeSerializer<AlertFileConfig> {

    @Override
    public AlertFileConfig deserialize(@NonNull Type type, @NonNull ConfigurationNode node) throws SerializationException {
        AlertFileConfig config = new AlertFileConfig();

        Map<String, String> shorthands = new LinkedHashMap<>();
        for (Map.Entry<Object, ? extends ConfigurationNode> shorthand : node.node("shorthands").childrenMap().entrySet()) {
            String key = (String) shorthand.getKey();
            String expression = shorthand.getValue().getString();
            shorthands.put(key, expression);
        }

        config.shorthands = shorthands;
        config.alerts = resolveNode(node, "alerts", "Alerts").getList(AlertConfig.class);
        return config;
    }

    @Override
    public void serialize(@NonNull Type type, @Nullable AlertFileConfig obj, @NonNull ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(null);
            return;
        }

        ConfigurationNode shorthandsNode = node.node("shorthands");
        for (Map.Entry<String, String> entry : obj.shorthands.entrySet()) {
            shorthandsNode.node(entry.getKey()).set(entry.getValue());
        }

        node.node("alerts").setList(AlertConfig.class, obj.alerts);
    }
}

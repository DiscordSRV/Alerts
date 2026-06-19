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

package com.discordsrv.alerts.config;

import com.discordsrv.alerts.config.alert.AlertConfig;
import com.discordsrv.alerts.config.alert.AlertConfigSerializer;
import com.discordsrv.alerts.config.alertfile.AlertFileConfig;
import com.discordsrv.alerts.config.alertfile.AlertFileConfigSerializer;
import com.discordsrv.configurate.DiscordSRVConfigurate;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.discordsrv.configurate.serializer.SerializerUtil.resolveNode;

public class AlertConfigManager {

    public List<AlertFileConfig> loadConfigs(Path dataDirectory) throws IOException {
        if (!Files.exists(dataDirectory)) {
            Files.createDirectories(dataDirectory);
        }

        ConfigurationOptions configurationOptions = ConfigurationOptions
                .defaults()
                .serializers(builder -> builder
                        .registerAll(DiscordSRVConfigurate.SERIALIZERS)
                        .register(AlertConfig.class, new AlertConfigSerializer())
                        .register(AlertFileConfig.class, new AlertFileConfigSerializer())
                );

        List<AlertFileConfig> alertFiles = new ArrayList<>();
        try (Stream<Path> files = Files.list(dataDirectory)) {
            for (Path path : files.toList()) {
                YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                        .indent(4)
                        .defaultOptions(configurationOptions)
                        .path(path)
                        .build();

                CommentedConfigurationNode node = loader.load();
                alertFiles.add(node.get(AlertFileConfig.class));
            }
        }

        return alertFiles;
    }
}

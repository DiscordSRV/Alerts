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

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class AlertConfigManager {

    public List<AlertFileConfig> loadAlertConfigs(Path alertDataDirectory, URL[] exampleResources) throws IOException {
        if (!Files.exists(alertDataDirectory)) {
            Files.createDirectories(alertDataDirectory);

            for (URL exampleResource : exampleResources) {
                String fileName = exampleResource.getFile();
                fileName = fileName.substring(fileName.lastIndexOf(File.separatorChar) + 1);
                Path filePath = alertDataDirectory.resolve(fileName);

                try (BufferedInputStream inputStream = new BufferedInputStream(exampleResource.openStream())) {
                    try (BufferedOutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(filePath))) {
                        byte[] buffer = new byte[512];
                        int i;
                        while ((i = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, i);
                        }
                    }
                }
            }
        }

        ConfigurationOptions configurationOptions = ConfigurationOptions
                .defaults()
                .serializers(builder -> builder
                        .registerAll(DiscordSRVConfigurate.SERIALIZERS)
                        .register(AlertConfig.class, new AlertConfigSerializer())
                        .register(AlertFileConfig.class, new AlertFileConfigSerializer())
                );

        List<AlertFileConfig> alertFiles = new ArrayList<>();
        try (Stream<Path> files = Files.list(alertDataDirectory)) {
            for (Path path : files.toList()) {
                String fileName = path.getFileName().toString();
                if (!fileName.endsWith(".yaml") && !fileName.endsWith(".yml"))  {
                    continue;
                }

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

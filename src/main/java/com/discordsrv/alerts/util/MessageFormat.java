/*-
 * LICENSE
 * DiscordSRV
 * -------------
 * Copyright (C) 2016 - 2021 Austin "Scarsz" Shapiro
 * -------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * END
 */

package com.discordsrv.alerts.util;

import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.Color;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class MessageFormat {

    // Regular message
    private String content;

    // Embed contents
    private String authorName;
    private String authorUrl;
    private String authorImageUrl;
    private String thumbnailUrl;
    private String title;
    private String titleUrl;
    private String description;
    private String imageUrl;
    private String footerText;
    private String footerIconUrl;
    private Instant timestamp;
    private Color color;
    private List<Field> fields;

    // Webhook capabilities
    private boolean useWebhooks;
    private String webhookAvatarUrl;
    private String webhookName;

    public boolean isAnyContent() {
        return content != null || authorName != null || authorUrl != null || authorImageUrl != null
                || thumbnailUrl != null || title != null || titleUrl != null || description != null
                || imageUrl != null || fields != null || footerText != null;
    }

    public github.scarsz.discordsrv.objects.MessageFormat toDSRV() {
        return new github.scarsz.discordsrv.objects.MessageFormat(
                content,
                authorName,
                authorUrl,
                authorImageUrl,
                thumbnailUrl,
                title,
                titleUrl,
                description,
                imageUrl,
                footerText,
                footerIconUrl,
                timestamp,
                color,
                fields.stream().map(field -> new MessageEmbed.Field(field.getTitle(), field.getValue(), field.isInline(), false)).collect(Collectors.toList()),
                useWebhooks,
                webhookAvatarUrl,
                webhookName
        );
    }

    public static class Field {

        private final String title;
        private final String value;
        private final boolean inline;

        public Field(String title, String value, boolean inline) {
            this.title = title;
            this.value = value;
            this.inline = inline;
        }

        public String getTitle() {
            return title;
        }

        public String getValue() {
            return value;
        }

        public boolean isInline() {
            return inline;
        }
    }

}

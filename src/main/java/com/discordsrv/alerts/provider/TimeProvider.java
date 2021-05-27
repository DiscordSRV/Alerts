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

package com.discordsrv.alerts.provider;

import com.discordsrv.alerts.Alerts;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeProvider {

    private final Date date = new Date();
    private final SimpleDateFormat timestampFormat;
    private final SimpleDateFormat dateFormat;
    private final TimeZone zone;

    public TimeProvider(Alerts plugin) {
        timestampFormat = new SimpleDateFormat(plugin.config().getOptionalString("TimestampFormat").orElse("EEE, d. MMM yyyy HH:mm:ss z"));
        dateFormat = new SimpleDateFormat(plugin.config().getOptionalString("DateFormat").orElse("yyyy-MM-dd"));

        String timezone = plugin.config().getOptionalString("Timezone").orElse("default");
        zone = timezone.equalsIgnoreCase("default") ? TimeZone.getDefault() : TimeZone.getTimeZone(timezone);
        timestampFormat.setTimeZone(zone);
        dateFormat.setTimeZone(zone);
    }

    public String format(String format) {
        return format(new SimpleDateFormat(format));
    }
    public String format(SimpleDateFormat format) {
        date.setTime(System.currentTimeMillis());
        return format.format(date);
    }

    public String date() {
        return format(dateFormat);
    }
    public String timeStamp() {
        return format(timestampFormat);
    }

}

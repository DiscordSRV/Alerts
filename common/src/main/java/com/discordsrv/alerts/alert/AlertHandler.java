package com.discordsrv.alerts.alert;

import com.discordsrv.alerts.DiscordSRVAlerts;
import com.discordsrv.alerts.config.alert.AlertConfig;
import com.discordsrv.alerts.config.alertfile.AlertFileConfig;
import com.discordsrv.api.DiscordSRV;
import com.discordsrv.api.discord.entity.channel.DiscordChannel;
import com.discordsrv.api.discord.entity.channel.DiscordGuildMessageChannel;
import com.discordsrv.api.discord.entity.message.SendableDiscordMessage;
import com.discordsrv.api.placeholder.format.FormattedText;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeLocator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class AlertHandler implements AlertReceiver {

    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("(\\$?)\\{((?:(?<=\\\\)[{}]|[^{}])+)}");

    // Prevents uncontrolled input from a previous replacement
    private static final String ESCAPE_CHARACTER = "\uE0DA";
    private static final Map<String, String> ESCAPE_CHARACTERS = createEscapeCharacterMap();
    private static Map<String, String> createEscapeCharacterMap() {
        Map<String, String> escapes = new LinkedHashMap<>();
        escapes.put("$", ESCAPE_CHARACTER + "&#36;");
        escapes.put("%", ESCAPE_CHARACTER + "&#37;");
        return Collections.unmodifiableMap(escapes);
    }

    private final DiscordSRVAlerts alerts;
    private final AlertConfig config;
    private final Map<String, Expression> shorthandExpressions;
    private final List<Expression> conditions;
    private final List<Expression> contextExpressions;
    private final Map<String, Expression> expressionCache = new ConcurrentHashMap<>();

    public AlertHandler(DiscordSRVAlerts alerts, AlertConfig config, AlertFileConfig fileConfig) {
        this.alerts = alerts;
        this.config = config;

        this.shorthandExpressions = new HashMap<>(fileConfig.shorthands.size());
        for (Map.Entry<String, String> shorthand : fileConfig.shorthands.entrySet()) {
            this.shorthandExpressions.put(
                    shorthand.getKey(),
                    alerts.spelExpressionParser().parseExpression(shorthand.getValue())
            );
        }

        this.conditions = new ArrayList<>(config.conditions.size());
        for (String condition : config.conditions) {
            this.conditions.add(alerts.spelExpressionParser().parseExpression(condition));
        }

        this.contextExpressions = new ArrayList<>(config.contextExpressions.size());
        for (String contextExpression : config.contextExpressions) {
            this.contextExpressions.add(alerts.spelExpressionParser().parseExpression(contextExpression));
        }
    }

    public boolean ignoringCancelled() {
        return config.ignoreCancelled == null || config.ignoreCancelled;
    }

    @Override
    public void receiveEvent(Object event, Map<String, Object> context, boolean cancelled) {
        if (ignoringCancelled() && cancelled) {
            return;
        }

        if (config.async == null || config.async) {
            alerts.runAsync(() -> processAlert(event, context));
            return;
        }

        processAlert(event, context);
    }

    private void processAlert(Object event, Map<String, Object> context) {
        context.put("event", event);
        context.putAll(alerts.staticContexts());

        DiscordSRV discordSRV = DiscordSRV.get();

        for (Long channelId : config.channelIds) {
            DiscordChannel channel = discordSRV.discordAPI().getChannelById(channelId);
            if (channel == null) {
                alerts.logger().error("Channel " + Long.toUnsignedString(channelId) + " not found to send alert to");
                continue;
            }
            if (!(channel instanceof DiscordGuildMessageChannel messageChannel)) {
                alerts.logger().error(Long.toUnsignedString(channelId) + " is not a valid Discord server's message channel");
                continue;
            }

            context.put("channel", messageChannel);

            StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
            evaluationContext.setRootObject(event);
            evaluationContext.setVariables(context);
            evaluationContext.setTypeLocator(new StandardTypeLocator(getClass().getClassLoader()));

            for (Expression condition : conditions) {
                Boolean result = condition.getValue(evaluationContext, Boolean.class);
                if (!Boolean.TRUE.equals(result)) {
                    // unmet condition
                    return;
                }
            }

            List<Object> additionalContexts = new ArrayList<>(contextExpressions.size());
            for (Expression contextExpression : contextExpressions) {
                additionalContexts.add(contextExpression.getValue(evaluationContext));
            }

            SendableDiscordMessage.Formatter messageFormatter = config.format
                    .clone()
                    .toFormatter()
                    .addReplacement(EXPRESSION_PATTERN, matcher -> {
                        String dollarSign = matcher.group(1);
                        boolean isShorthand = dollarSign == null || dollarSign.isEmpty();

                        String insideBrackets = matcher.group(2)
                                .replace("\\{", "{")
                                .replace("\\}", "}");

                        Expression expression;
                        if (isShorthand) {
                            expression = shorthandExpressions.get(insideBrackets);
                            if (expression == null) {
                                return matcher.group();
                            }
                        } else {
                            expression = expressionCache.computeIfAbsent(
                                insideBrackets,
                                __ -> alerts.spelExpressionParser().parseExpression(insideBrackets)
                            );
                        }

                        Object value = expression.getValue(evaluationContext);
                        return escape(discordSRV, value);
                    })
                    .addContext(context.values())
                    .addContext(additionalContexts);

            // Unescape after all replacements
            for (Map.Entry<String, String> entry : ESCAPE_CHARACTERS.entrySet()) {
                messageFormatter = messageFormatter.addReplacement(entry.getValue(), entry.getKey());
            }

            SendableDiscordMessage message = messageFormatter.build();
            messageChannel.sendMessage(message).whenComplete((__, t) -> {
                if (t != null) {
                    alerts.logger().error("Failed to send alert to channel", t);
                }
            });
        }
    }

    private CharSequence escape(DiscordSRV discordSRV, Object result) {
        CharSequence plain = discordSRV.placeholderService().convertReplacementToCharSequence(result);
        boolean isFormattedText = plain instanceof FormattedText;

        String string = plain.toString();
        for (Map.Entry<String, String> entry : ESCAPE_CHARACTERS.entrySet()) {
            string = string.replace(entry.getKey(), entry.getValue());
        }
        return isFormattedText ? FormattedText.of(string) : string;
    }
}

package com.discordsrv.alerts.logger;

import java.io.PrintWriter;
import java.io.StringWriter;

public class JavaUtilLogger implements Logger {

    private final java.util.logging.Logger logger;

    public JavaUtilLogger(java.util.logging.Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void warning(String message) {
        logger.warning(message);
    }

    @Override
    public void error(String message) {
        logger.severe(message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        StringBuilder builder = new StringBuilder(message);
        builder.append("\n");

        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        builder.append(stringWriter);

        logger.severe(builder.toString());
    }
}

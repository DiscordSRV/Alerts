package com.discordsrv.alerts.logger;

public interface Logger {

    void info(String message);
    void warning(String message);
    void error(String message);
    void error(String message, Throwable throwable);
}

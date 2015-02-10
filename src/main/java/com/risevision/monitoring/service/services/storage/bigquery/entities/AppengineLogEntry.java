package com.risevision.monitoring.service.services.storage.bigquery.entities;

/**
 * Created by rodrigopavezi on 2/4/15.
 */
public class AppengineLogEntry extends LogEntry {

    private String logMessage;

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }
}

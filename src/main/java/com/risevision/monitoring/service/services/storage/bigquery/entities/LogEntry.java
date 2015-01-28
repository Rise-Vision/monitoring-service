package com.risevision.monitoring.service.services.storage.bigquery.entities;

import java.util.Date;

/**
 * Created by rodrigopavezi on 1/21/15.
 */
public class LogEntry {

    private String ip;
    private String host;
    private String resource;
    private String logMessage;
    private Date time;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LogEntry logEntry = (LogEntry) o;

        if (host != null ? !host.equals(logEntry.host) : logEntry.host != null) return false;
        if (ip != null ? !ip.equals(logEntry.ip) : logEntry.ip != null) return false;
        if (logMessage != null ? !logMessage.equals(logEntry.logMessage) : logEntry.logMessage != null) return false;
        if (resource != null ? !resource.equals(logEntry.resource) : logEntry.resource != null) return false;
        if (time != null ? time.compareTo(logEntry.time) != 0 : logEntry.time != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ip != null ? ip.hashCode() : 0;
        result = 31 * result + (host != null ? host.hashCode() : 0);
        result = 31 * result + (resource != null ? resource.hashCode() : 0);
        result = 31 * result + (logMessage != null ? logMessage.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        return result;
    }
}

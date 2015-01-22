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

}

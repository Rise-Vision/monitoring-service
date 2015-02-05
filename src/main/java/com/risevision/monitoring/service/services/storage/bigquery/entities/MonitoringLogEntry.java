package com.risevision.monitoring.service.services.storage.bigquery.entities;

/**
 * Created by rodrigopavezi on 2/4/15.
 */
public class MonitoringLogEntry extends LogEntry {

    private String clientId;
    private String api;
    private String userId;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

package com.risevision.monitoring.service.api.resources;

import java.util.Date;

public class AppActivity extends Resource{

    private String clientId;
    private String api;
    private Date firstCall;
    private Date lastCall;
    private float avgCallsPerDay;

    public AppActivity(String clientId, String api, Date firstCall, Date lastCall, float averageCallsPerDay) {
        this.clientId = clientId;
        this.api = api;
        this.firstCall = firstCall;
        this.lastCall = lastCall;
        this.avgCallsPerDay = averageCallsPerDay;
    }

    public AppActivity(String clientId, String api) {
        this.clientId = clientId;
        this.api = api;
    }

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

    public Date getFirstCall() {
        return firstCall;
    }

    public void setFirstCall(Date firstCall) {
        this.firstCall = firstCall;
    }

    public Date getLastCall() {
        return lastCall;
    }

    public void setLastCall(Date lastCall) {
        this.lastCall = lastCall;
    }

    public float getAvgCallsPerDay() {
        return avgCallsPerDay;
    }

    public void setAvgCallsPerDay(float avgCallsPerDay) {
        this.avgCallsPerDay = avgCallsPerDay;
    }
}

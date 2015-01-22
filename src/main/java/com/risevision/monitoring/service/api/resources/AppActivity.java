package com.risevision.monitoring.service.api.resources;

import java.util.Date;

public class AppActivity extends Resource{

    private String api;
    private String clientId;
    private Date firstCall;
    private Date lastCall;
    private long avgCallsPerDay;

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
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

    public long getAvgCallsPerDay() {
        return avgCallsPerDay;
    }

    public void setAvgCallsPerDay(long avgCallsPerDay) {
        this.avgCallsPerDay = avgCallsPerDay;
    }
}

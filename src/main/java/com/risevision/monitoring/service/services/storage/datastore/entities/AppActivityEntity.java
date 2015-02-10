package com.risevision.monitoring.service.services.storage.datastore.entities;

import com.googlecode.objectify.annotation.Subclass;

import java.util.Date;

/**
 * Created by rodrigopavezi on 1/23/15.
 */
@Subclass(index = true)
public class AppActivityEntity extends MonitoringDatastoreEntity {

    private String clientId;
    private String api;
    private Date firstCall;
    private Date lastCall;
    private double avgCallsPerDay;

    public AppActivityEntity() {

    }

    public AppActivityEntity(String id) {
        super(id);
    }

    public AppActivityEntity(String clientId, String api) {
        super(api + "-" + clientId);
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

    public double getAvgCallsPerDay() {
        return avgCallsPerDay;
    }

    public void setAvgCallsPerDay(double avgCallsPerDay) {
        this.avgCallsPerDay = avgCallsPerDay;
    }
}

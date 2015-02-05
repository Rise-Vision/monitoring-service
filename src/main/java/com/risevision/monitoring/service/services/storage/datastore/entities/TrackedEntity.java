package com.risevision.monitoring.service.services.storage.datastore.entities;

import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Subclass;

import java.util.Date;

/**
 * Created by rodrigopavezi on 2/4/15.
 */
@Subclass(index = true)
public class TrackedEntity extends MonitoringDatastoreEntity {

    private Date creationDate;
    private Date changedDate;
    private String changedBy;
    private String createdBy;

    @OnSave
    public void setStatusParameters() {
        Date now = new Date();
        if (creationDate == null) {
            creationDate = now;
        }
        changedDate = now;
        if (createdBy == null || createdBy.isEmpty()) {
            createdBy = changedBy;
        }
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getChangedDate() {
        return changedDate;
    }

    public void setChangedDate(Date changedDate) {
        this.changedDate = changedDate;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}

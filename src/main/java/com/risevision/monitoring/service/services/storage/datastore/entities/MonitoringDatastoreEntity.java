package com.risevision.monitoring.service.services.storage.datastore.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.UUID;

/**
 * Created by rodrigopavezi on 12/10/14.
 */
@Entity
public class MonitoringDatastoreEntity {

    @Id
    private String id;

    public MonitoringDatastoreEntity() {
        this.id = UUID.randomUUID().toString();
    }

    public MonitoringDatastoreEntity(String id) {
        this.id = id;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}

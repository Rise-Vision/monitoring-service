package com.risevision.monitoring.service.services.storage.datastore;

import static com.risevision.monitoring.service.services.storage.datastore.OfyService.ofy;

/**
 * Created by rodrigopavezi on 12/9/14.
 */

public class DatastoreService {

    private static DatastoreService instance;

    private DatastoreService() {
    }

    public static DatastoreService getInstance() {
        try {
            if (instance == null) {
                instance = new DatastoreService();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    public void put(Object entity) {
        ofy().save().entity(entity).now();
    }

    public Object get(Object entity) {
        return ofy().load().entity(entity).now();
    }
}

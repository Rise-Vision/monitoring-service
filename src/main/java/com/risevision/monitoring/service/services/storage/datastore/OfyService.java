package com.risevision.monitoring.service.services.storage.datastore;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import com.risevision.monitoring.service.services.storage.datastore.entities.AppActivityEntity;
import com.risevision.monitoring.service.services.storage.datastore.entities.DatastoreEntity;

public class OfyService {
    static {
        factory().register(DatastoreEntity.class);
        factory().register(AppActivityEntity.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }

    public static Closeable begin() {
        return ObjectifyService.begin();
    }
}
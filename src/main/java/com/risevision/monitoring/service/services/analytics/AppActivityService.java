package com.risevision.monitoring.service.services.analytics;

import com.google.appengine.api.users.User;
import com.risevision.monitoring.service.services.storage.datastore.entities.AppActivityEntity;

/**
 * Created by rodrigopavezi on 1/21/15.
 */
public interface AppActivityService {

    public AppActivityEntity getActivity(String clientId, String api, User user);
}

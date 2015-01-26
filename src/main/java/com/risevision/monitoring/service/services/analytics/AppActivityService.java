package com.risevision.monitoring.service.services.analytics;

import com.risevision.monitoring.service.services.storage.datastore.entities.AppActivityEntity;

import javax.xml.bind.ValidationException;
import java.io.IOException;

/**
 * Created by rodrigopavezi on 1/21/15.
 */
public interface AppActivityService {

    public AppActivityEntity getActivity(String clientId, String api) throws ValidationException, IOException, InterruptedException;
}

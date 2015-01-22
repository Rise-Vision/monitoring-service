package com.risevision.monitoring.service.services.analytics;

import com.risevision.monitoring.service.api.resources.AppActivity;

/**
 * Created by rodrigopavezi on 1/21/15.
 */
public interface AppActivityService {

    public AppActivity getActivity(String clientId, String api);
}

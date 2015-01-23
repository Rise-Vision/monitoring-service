package com.risevision.monitoring.service.services.analytics;

import com.risevision.monitoring.service.api.resources.AppActivity;

import javax.xml.bind.ValidationException;

/**
 * Created by rodrigopavezi on 1/21/15.
 */
public interface AppActivityService {

    public AppActivity getActivityFromTheLastNumberOfDays(String clientId, String api, int numberOfDays) throws ValidationException;
}

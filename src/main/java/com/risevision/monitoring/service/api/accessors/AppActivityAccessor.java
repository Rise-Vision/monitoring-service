package com.risevision.monitoring.service.api.accessors;

import com.google.api.server.spi.ServiceException;
import com.google.appengine.api.users.User;
import com.risevision.monitoring.service.api.resources.AppActivity;
import com.risevision.monitoring.service.services.analytics.AppActivityService;
import com.risevision.monitoring.service.services.analytics.AppActivityServiceImpl;
import com.risevision.monitoring.service.services.gcs.AuthorizationService;
import com.risevision.monitoring.service.services.gcs.AuthorizationServiceImpl;
import com.risevision.monitoring.service.services.storage.datastore.entities.AppActivityEntity;

import javax.xml.bind.ValidationException;

/**
 * Created by rodrigopavezi on 1/20/15.
 */
public class AppActivityAccessor {


    private AppActivityService appActivityService;
    private AuthorizationService authorizationService;

    public AppActivityAccessor() {
        this.appActivityService = new AppActivityServiceImpl();
        this.authorizationService = new AuthorizationServiceImpl();
    }

    public AppActivityAccessor(AppActivityService appActivityService, AuthorizationService authorizationService) {
        this.appActivityService = appActivityService;
        this.authorizationService = authorizationService;
    }

    public AppActivity get(String clientId, String api, User user) throws ValidationException, ServiceException {

        if (clientId == null || clientId.isEmpty()) {
            throw new ValidationException("Client Id cannot be null or empty.");
        }

        if (api == null || api.isEmpty()) {
            throw new ValidationException("Api cannot be null or empty.");
        }

        authorizationService.verifyAuthorization(user, clientId);

        AppActivity appActivity;

        AppActivityEntity appActivityEntity = appActivityService.getActivity(clientId, api);

        if (appActivityEntity != null) {
            appActivity = new AppActivity(clientId, api, appActivityEntity.getFirstCall(), appActivityEntity.getLastCall(), appActivityEntity.getAvgCallsPerDay());
        } else {
            appActivity = new AppActivity(clientId, api);
        }

        return appActivity;
    }

}

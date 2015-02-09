package com.risevision.monitoring.service.services.gcs;

import com.google.api.server.spi.ServiceException;
import com.google.appengine.api.users.User;

/**
 * Created by rodrigopavezi on 2/9/15.
 */
public interface AuthorizationService {

    public void verifyAuthorization(User user, String clientId) throws ServiceException;
}

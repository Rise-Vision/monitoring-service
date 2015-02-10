package com.risevision.monitoring.service.services.gcs;

import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.users.User;

/**
 * Created by rodrigopavezi on 2/9/15.
 */
public class AuthorizationServiceImpl implements AuthorizationService {

    private UserCompanyVerifier userCompanyVerifier;
    private AppLookup appLookup;

    public AuthorizationServiceImpl() {
        userCompanyVerifier = new UserCompanyVerifier();
        appLookup = new AppLookup();
    }

    public AuthorizationServiceImpl(UserCompanyVerifier userCompanyVerifier, AppLookup appLookup) {
        this.userCompanyVerifier = userCompanyVerifier;
        this.appLookup = appLookup;
    }

    @Override
    public void verifyAuthorization(User user, String clientId) throws ServiceException {

        ThirdPartyApp thirdPartyApp = appLookup.getApp(clientId);

        if (thirdPartyApp != null) {
            userCompanyVerifier.verifyUserCompany(thirdPartyApp.getCompanyId(), user.getEmail());
        } else {
            throw new NotFoundException("App not found for client id: " + clientId);
        }

    }
}

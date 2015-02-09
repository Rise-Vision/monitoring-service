package com.risevision.monitoring.service.api.accessors;

import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.appengine.api.users.User;
import com.risevision.monitoring.service.api.resources.AppActivity;
import com.risevision.monitoring.service.services.analytics.AppActivityService;
import com.risevision.monitoring.service.services.gcs.AuthorizationService;
import com.risevision.monitoring.service.services.storage.datastore.entities.AppActivityEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.xml.bind.ValidationException;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

/**
 * Created by rodrigopavezi on 1/21/15.
 */
public class AppActivityAccessorTest {

    @Mock
    private AppActivityService appActivityService;
    @Mock
    private AppActivityEntity appActivityEntity;
    @Mock
    private AuthorizationService authorizationService;

    private User user;

    private AppActivityAccessor appActivityAccessor;
    private String api;
    private String clientId;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        appActivityAccessor = new AppActivityAccessor(appActivityService, authorizationService);
        api = "CoreAPIv1";
        clientId = "xxxxxxxxxxx";
    }

    @Test
    public void testGetAppActivityForAnAPIClientIdAndUser() throws ServiceException, ValidationException, IOException, InterruptedException {
        given(appActivityService.getActivity(clientId, api)).willReturn(appActivityEntity);

        AppActivity response = appActivityAccessor.get(clientId, api, user);

        verify(appActivityService).getActivity(clientId, api);

        assertThat(clientId, is(response.getClientId()));
        assertThat(api, is(response.getApi()));
        assertThat(appActivityEntity.getFirstCall(), is(response.getFirstCall()));
        assertThat(appActivityEntity.getLastCall(), is(response.getLastCall()));
        assertThat(appActivityEntity.getAvgCallsPerDay(), is(response.getAvgCallsPerDay()));
    }

    @Test(expected = ServiceException.class)
    public void testGetAppActivityForAnALIClientIdAndUserThrowsExceptionWhenUserIsNotAuthorized() throws ServiceException, ValidationException, IOException, InterruptedException {

        willThrow(new ForbiddenException("User is not allowed to access company")).given(authorizationService).verifyAuthorization(user, clientId);

        given(appActivityService.getActivity(clientId, api)).willReturn(appActivityEntity);

        AppActivity response = appActivityAccessor.get(clientId, api, user);
    }
}

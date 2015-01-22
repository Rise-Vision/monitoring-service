package com.risevision.monitoring.service;

import com.google.api.server.spi.ServiceException;
import com.risevision.monitoring.service.api.accessories.AppActivityAccessor;
import com.risevision.monitoring.service.api.resources.AppActivity;
import com.risevision.monitoring.service.services.analytics.AppActivityService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.xml.bind.ValidationException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by rodrigopavezi on 1/21/15.
 */
public class AppActivityAccessorTest {

    @Mock
    private AppActivityService appActivityService;
    @Mock
    private AppActivity appActivity;

    private AppActivityAccessor appActivityAccessor;
    private String api;
    private String clientId;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        appActivityAccessor = new AppActivityAccessor(appActivityService);
        api = "CoreAPIv1";
        clientId = "xxxxxxxxxxx";
    }

    @Test
    public void testGetAppActivityForAnAPIAndAClientId() throws ServiceException, ValidationException {
        given(appActivityService.getActivity(clientId, api)).willReturn(appActivity);

        AppActivity response = appActivityAccessor.get(clientId, api);

        verify(appActivityService).getActivity(clientId, api);

        assertThat(appActivity, is(response));
    }
}

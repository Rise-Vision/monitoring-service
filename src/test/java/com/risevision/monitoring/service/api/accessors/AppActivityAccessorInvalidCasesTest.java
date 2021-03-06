package com.risevision.monitoring.service.api.accessors;

import com.google.api.server.spi.ServiceException;
import com.google.appengine.api.users.User;
import com.risevision.monitoring.service.services.analytics.AppActivityService;
import com.risevision.monitoring.service.services.gcs.AuthorizationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.xml.bind.ValidationException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by rodrigopavezi on 1/21/15.
 */
@RunWith(Parameterized.class)
public class AppActivityAccessorInvalidCasesTest {

    @Parameter
    public String api;
    @Parameter(value = 1)
    public String clientId;
    @Mock
    private AppActivityService appActivityService;
    @Mock
    private AuthorizationService authorizationService;

    private User user;


    private AppActivityAccessor appActivityAccessor;


    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{

                {null, "CoreAPIv1"}, // null client Id
                {"", "CoreAPIv1"}, // empty client Id
                {"xxxxxx", null}, // null api
                {"xxxxxx", ""}, // empty api
                {null, null}, // null client Id and api
                {"", ""}, // empty client Id and api
                {null, ""}, // null client Id and empty api
                {"", null}, // empty client Id and null api
        });
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        appActivityAccessor = new AppActivityAccessor(appActivityService, authorizationService);

    }

    @Test(expected = ValidationException.class)
    public void test() throws ServiceException, ValidationException {

        appActivityAccessor.get(clientId, api, user);

    }

}

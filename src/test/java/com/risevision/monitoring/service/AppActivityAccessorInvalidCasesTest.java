package com.risevision.monitoring.service;

import com.google.api.server.spi.ServiceException;
import com.risevision.monitoring.service.api.accessories.AppActivityAccessor;
import com.risevision.monitoring.service.api.resources.AppActivity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import javax.xml.bind.ValidationException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by rodrigopavezi on 1/21/15.
 */
@RunWith(Parameterized.class)
public class AppActivityAccessorInvalidCasesTest {

    private AppActivityAccessor appActivityAccessor;

    @Parameter
    public String api;
    @Parameter(value = 1)
    public String clientId;

    @Before
    public void setup(){
        appActivityAccessor = new AppActivityAccessor();
    }

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> scenariosToBeTested = Arrays.asList(new Object[][]{

                {null, "CoreAPIv1"}, // null client Id
                {"", "CoreAPIv1"}, // empty client Id
                {"xxxxxx", null}, // null api
                {"xxxxxx", ""}, // empty api
                {null, null}, // null client Id and api
                {"", ""}, // empty client Id and api
                {null, ""}, // null client Id and empty api
                {"", null}, // empty client Id and null api
        });

        return scenariosToBeTested;
    }


    @Test(expected = ValidationException.class)
    public void test() throws ServiceException, ValidationException {

        AppActivity response = appActivityAccessor.get(clientId, api);

    }

}

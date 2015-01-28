package com.risevision.monitoring.service.api;

import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.BadRequestException;
import com.google.appengine.api.users.User;
import com.risevision.monitoring.service.api.accessors.AppActivityAccessor;
import com.risevision.monitoring.service.api.resources.Resource;

import javax.xml.bind.ValidationException;

@Api(
        name = "monitoring",
        description = "Monitoring API for accessing monitoring entities.",
        title = "Monitoring API",
        version = "v0",
        namespace = @ApiNamespace(ownerDomain = "risevision.com",
                ownerName = "risevision",
                packagePath = "com/risevision/monitoring"))

public class MonitoringAPIv0 {


    @ApiMethod(
            name = "activity.get",
            path = "activity",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public Resource getActivity(@Named("clientId") String clientId, @Named("api") String api,
                                User user) throws ServiceException, ValidationException {

        AppActivityAccessor appActivityAccessor = new AppActivityAccessor();
        Resource resource = null;
        try {
            resource = appActivityAccessor.get(clientId, api);

        } catch (ValidationException exception) {
            throw new BadRequestException(exception);
        }

        return resource;
    }
}

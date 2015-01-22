package com.risevision.monitoring.service.api;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.risevision.monitoring.service.api.resources.AppActivity;
import com.risevision.monitoring.service.api.resources.Resource;

@Api(
        name = "monitoring",
        description = "Monitoring API for accessing monitoring entities.",
        title = "Monitoring API",
        version = "v0",
        namespace = @ApiNamespace(ownerDomain = "risevision.com",
                ownerName = "risevision",
                packagePath="com/risevision/monitoring"),
        useDatastoreForAdditionalConfig = AnnotationBoolean.TRUE)

public class MonitoringAPIv0 {


    @ApiMethod(
            name = "app.activity.get",
            path = "app.activity",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public Resource getActivity(){

        return null;
    }
}

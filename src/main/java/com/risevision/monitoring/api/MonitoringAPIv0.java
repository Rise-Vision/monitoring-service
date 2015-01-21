package com.risevision.monitoring.api;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.risevision.monitoring.api.response.APIResponse;
import com.risevision.monitoring.api.wrappers.ActivityWrapper;

@Api(
        name = "monitoring",
        description = "Monitoring API for accessing monitoring entity.",
        title = "Monitoring API",
        version = "v0",
        namespace = @ApiNamespace(ownerDomain = "risevision.com",
                ownerName = "risevision",
                packagePath="com/risevision/monitoring"),
        useDatastoreForAdditionalConfig = AnnotationBoolean.TRUE)

public class MonitoringAPIv0 {


    @ApiMethod(
            name = "activity.get",
            path = "activity",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public APIResponse<ActivityWrapper> getActivity(){

        return null;
    }
}

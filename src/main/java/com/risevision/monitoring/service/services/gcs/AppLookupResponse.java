package com.risevision.monitoring.service.services.gcs;

import com.google.api.client.util.Key;

/**
 * Created by rodrigopavezi on 2/9/15.
 */
public class AppLookupResponse {

    @Key("item")
    private ThirdPartyApp thirdPartyApp;

    public ThirdPartyApp getThirdPartyApp() {
        return thirdPartyApp;
    }
}

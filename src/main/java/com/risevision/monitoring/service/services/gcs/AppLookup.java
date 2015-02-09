package com.risevision.monitoring.service.services.gcs;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.response.InternalServerErrorException;
import com.risevision.monitoring.service.util.Options;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.logging.Logger;

public class AppLookup {
    private static final String HTTP_CHARSET = "UTF-8";
    private static final Logger log = Logger.getAnonymousLogger();
    private Options options;

    public AppLookup() {
        options = Options.getInstance();
    }

    public ThirdPartyApp getApp(String clientId)
            throws ServiceException {

        log.info("Get app for client Id " + clientId);

        try {
            GenericUrl url = new GenericUrl
                    (options.getAPP_LOOKUP_URL()
                            .replace("CLIENT_ID", URLEncoder.encode(clientId, HTTP_CHARSET)));

            HttpResponse response = ServiceAccountAPIRequestor.makeRequest
                    (ServiceAccountAPIRequestor.SERVICE_ACCOUNT.CORE, "GET", url, null);

            AppLookupResponse appLookupResponse = response.parseAs(AppLookupResponse.class);

            return appLookupResponse.getThirdPartyApp();

        } catch (IOException e) {
            e.printStackTrace();
            throw new InternalServerErrorException("VerifyUserCompany Internal Error");
        }
    }
}

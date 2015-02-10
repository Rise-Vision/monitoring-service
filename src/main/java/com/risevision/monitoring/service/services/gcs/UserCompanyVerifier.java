package com.risevision.monitoring.service.services.gcs;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.InternalServerErrorException;
import com.risevision.monitoring.service.util.Options;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.logging.Logger;

public class UserCompanyVerifier {
    private static final String HTTP_CHARSET = "UTF-8";
    private static final Logger log = Logger.getAnonymousLogger();
    private Options options;

    public UserCompanyVerifier() {
        options = Options.getInstance();
    }

    public void verifyUserCompany(String companyId, String email)
            throws ServiceException {

        log.info("Verifying company access for user " + email);

        try {
            GenericUrl url = new GenericUrl
                    (options.getUSER_VERIFICATION_URL()
                            .replace("COMPANYID", URLEncoder.encode(companyId, HTTP_CHARSET))
                            .replace("EMAIL", URLEncoder.encode(email, HTTP_CHARSET)));

            HttpResponse response = ServiceAccountAPIRequestor.makeRequest
                    (ServiceAccountAPIRequestor.SERVICE_ACCOUNT.CORE, "POST", url, null);

            if (!response.parseAsString().contains("\"allowedAccess\": true")) {
                throw new ForbiddenException("User is not allowed to access company");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new InternalServerErrorException("VerifyUserCompany Internal Error");
        }
    }
}

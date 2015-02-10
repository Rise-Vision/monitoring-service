package com.risevision.monitoring.service.services.gcs;

import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.risevision.monitoring.service.util.Options;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class P12CredentialBuilder {
    private static final Logger log = Logger.getAnonymousLogger();
    private static final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    private static final UrlFetchTransport
            urlTransport = UrlFetchTransport.getDefaultInstance();

    private GoogleCredential credential;
    private GoogleCredential.Builder builder;
    private Options options;

    public P12CredentialBuilder() {
        builder = new GoogleCredential.Builder()
                .setTransport(urlTransport)
                .setJsonFactory(jsonFactory);
        options = Options.getInstance();
    }

    public GoogleCredential getCredentialFromP12File
            (String p12path, String id) {
        builder.setServiceAccountId(id)
                .setServiceAccountScopes
                        (Arrays.asList(options.getEMAIL_SCOPE(), options.getSTORAGE_SCOPE(), options.getBQ_SCOPE()));

        log.info("Credential file: " + p12path + " Id: " + id);

        try {
            File p12File = new File(p12path);
            credential = builder.setServiceAccountPrivateKeyFromP12File(p12File).build();
            credential.refreshToken();
        } catch (Exception e) {
            log.log(Level.WARNING, "Error building credential", e);
            e.printStackTrace();
        }

        return credential;
    }
}

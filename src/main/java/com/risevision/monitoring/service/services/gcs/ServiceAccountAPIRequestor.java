package com.risevision.monitoring.service.services.gcs;

import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.*;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.risevision.monitoring.service.util.Options;

import java.io.IOException;

public class ServiceAccountAPIRequestor {
    public static HttpResponse makeRequest
            (SERVICE_ACCOUNT serviceAccount, String method, GenericUrl url, HttpContent content)
            throws IOException {
        if (serviceAccount.getCredential().getExpirationTimeMilliseconds() < 60000) {
            serviceAccount.getCredential().refreshToken();
        }

        return serviceAccount.requestFactory.buildRequest(method, url, content).execute();
    }

    public enum SERVICE_ACCOUNT {
        CORE(Options.getInstance().getRVCORE_ID(), Options.getInstance().getRVCORE_P12_PATH());

        private String id;
        private String p12_path;
        private JsonObjectParser parser;
        private GoogleCredential credential;
        private HttpRequestFactory requestFactory;
        private HttpRequestInitializer requestInitializer;
        private Options options;

        SERVICE_ACCOUNT(String id, String p12_path) {
            this.id = id;
            this.p12_path = p12_path;

            this.credential = new P12CredentialBuilder().getCredentialFromP12File
                    (p12_path, id);

            this.parser = new JsonObjectParser(new GsonFactory());

            this.requestInitializer = new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                    request.setInterceptor(credential);
                    request.setParser(parser);
                }
            };

            this.requestFactory =
                    new UrlFetchTransport().createRequestFactory(this.requestInitializer);
        }

        public GoogleCredential getCredential() {
            return credential;
        }
    }
}


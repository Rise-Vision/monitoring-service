package com.risevision.monitoring.service.services.oauth;

import java.io.IOException;

public interface GoogleOAuthClientService {

    public TokenInfo lookupTokenInfo(String token) throws IOException;

}
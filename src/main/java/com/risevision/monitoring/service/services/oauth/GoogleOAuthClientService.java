package com.risevision.monitoring.service.services.oauth;

public interface GoogleOAuthClientService {

    public TokenInfo lookupTokenInfo(String token);

}
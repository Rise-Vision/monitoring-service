package com.risevision.monitoring.service.services.oauth;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by rodrigopavezi on 1/14/15.
 */
public class GoogleOAuthClientServiceImpl implements GoogleOAuthClientService {

    private static final Logger logger = Logger.getLogger(GoogleOAuthClientService.class.getName());
    private final TokenInfoService tokenInfoService;

    public GoogleOAuthClientServiceImpl(TokenInfoService tokenInfoService) {
        this.tokenInfoService = tokenInfoService;
    }

    public TokenInfo lookupTokenInfo(String token) throws IOException {

        TokenInfo tokenInfo = null;

        if (token != null && !token.isEmpty()) {
            tokenInfo = tokenInfoService.getTokenInfo(token);
        } else {
            logger.info("There isn't Bearer token on the Authorization header");
        }

        return tokenInfo;

    }
}

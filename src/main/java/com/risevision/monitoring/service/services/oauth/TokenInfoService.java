package com.risevision.monitoring.service.services.oauth;

import java.io.IOException;

/**
 * Created by rodrigopavezi on 1/13/15.
 */
interface TokenInfoService {

    public TokenInfo getTokenInfo(String token) throws IOException;
}

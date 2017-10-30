package com.risevision.monitoring.service.services.oauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.risevision.common.security.customauth.TokenManager;

public class CustomAuthClientServiceTest {

    private CustomAuthClientService customAuthClientService;

    @Before
    public void setup() {
        customAuthClientService = new CustomAuthClientServiceImpl();
    }

    @Test
    public void testLookUpTokenInfoValidToken() throws IOException {
    	  String testEmail = "test@mail.org";
        String token = TokenManager.getInstance().generateToken(testEmail, 10000);

        TokenInfo tokenInfo = customAuthClientService.lookupTokenInfo(token);

        assertNotNull(tokenInfo);
        assertEquals(tokenInfo.getEmail(), testEmail);
    }

    @Test
    public void testLookUpTokenInfoNotValid() throws IOException {
        TokenInfo tokenInfo = customAuthClientService.lookupTokenInfo("a.b.c");

        assertNull(tokenInfo);
    }
}

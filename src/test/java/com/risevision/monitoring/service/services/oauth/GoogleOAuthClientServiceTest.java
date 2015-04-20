package com.risevision.monitoring.service.services.oauth;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by rodrigopavezi on 1/13/15.
 */
public class GoogleOAuthClientServiceTest {

    private GoogleOAuthClientService googleOAuthClientService;

    @Mock
    private TokenInfoService tokenInfoService;

    @Mock
    private TokenInfo tokenInfo;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        googleOAuthClientService = new GoogleOAuthClientServiceImpl(tokenInfoService);
    }

    @Test
    public void testLookUpTokenInfo() throws IOException {
        String token = "xxxxxxxxxx";

        given(tokenInfoService.getTokenInfo(token)).willReturn(tokenInfo);

        TokenInfo actualTokenInfo = googleOAuthClientService.lookupTokenInfo(token);

        verify(tokenInfoService).getTokenInfo(token);

        assertThat(actualTokenInfo, is(tokenInfo));
    }

    @Test
    public void testLookUpTokenInfoReturnsNullIfTokenInfoCannotBeRetrieved() throws IOException {
        String token = "xxxxxxxxxx";

        given(tokenInfoService.getTokenInfo(token)).willReturn(null);

        TokenInfo actualTokenInfo = googleOAuthClientService.lookupTokenInfo(token);

        verify(tokenInfoService).getTokenInfo(token);

        assertThat(actualTokenInfo, is(nullValue()));
    }


    @Test
    public void testLookUpTokenInfoReturnsNullIfTokenIsEmpty() throws IOException {

        String token = "";

        TokenInfo actualTokenInfo = googleOAuthClientService.lookupTokenInfo(token);

        verify(tokenInfoService, never()).getTokenInfo(token);

        assertThat(actualTokenInfo, is(nullValue()));
    }

    @Test
    public void testLookUpTokenInfoReturnsNullIfTokenNull() throws IOException {

        String token = null;

        TokenInfo actualTokenInfo = googleOAuthClientService.lookupTokenInfo(token);

        verify(tokenInfoService, never()).getTokenInfo(token);

        assertThat(actualTokenInfo, is(nullValue()));
    }
}

package com.risevision.monitoring.service.queue.tasks;

import com.google.api.services.bigquery.model.TableRow;
import com.risevision.monitoring.service.services.oauth.GoogleOAuthClientService;
import com.risevision.monitoring.service.services.oauth.TokenInfo;
import com.risevision.monitoring.service.services.storage.bigquery.BigQueryService;
import com.risevision.monitoring.service.util.Options;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by rodrigopavezi on 2/5/15.
 */
public class MonitoringLogTaskTest {

    @Mock
    private BigQueryService bigQueryService;
    @Mock
    private Options options;
    @Mock
    private GoogleOAuthClientService googleOAuthClientService;
    @Mock
    private TokenInfo tokenInfo;

    private TableRow row;

    private MonitoringLogTask monitoringLogTask;

    private String ip;
    private String host;
    private String resource;
    private String bearerToken;
    private String clientId;
    private String api;
    private String userId;
    private String time;
    private String projectId;
    private String datasetId;
    private String tableId;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        row = new TableRow();
        monitoringLogTask = new MonitoringLogTask(bigQueryService, options, googleOAuthClientService, row);

        ip = "1.1.1.1";
        host = "test.com";
        resource = "/_ah/spi/test";
        bearerToken = "ttttttttttt";
        clientId = "xxxxxxx";
        api = "CoreAPIv1";
        userId = "example@gmail.com";
        time = String.valueOf(System.currentTimeMillis() / 1000L);

        projectId = "rvacore-test";
        datasetId = "monitoringLogs";
        tableId = "apiRequests";
    }

    @Test
    public void testMonitoringTestExecutesAndCallsBigQueryStreamInsertWithClientIdAndUserId() throws Exception {

        given(tokenInfo.getIssued_to()).willReturn(clientId);
        given(tokenInfo.getEmail()).willReturn(userId);
        given(googleOAuthClientService.lookupTokenInfo(bearerToken)).willReturn(tokenInfo);

        given(options.getPROJECT_ID()).willReturn(projectId);
        given(options.getDATASET_ID()).willReturn(datasetId);
        given(options.getMONITORING_LOG_TABLE_ID()).willReturn(tableId);

        monitoringLogTask.execute(ip, host, resource, bearerToken, api, time);

        verify(googleOAuthClientService).lookupTokenInfo(bearerToken);
        verify(tokenInfo, atLeastOnce()).getIssued_to();
        verify(tokenInfo, atMost(3)).getEmail();

        verify(bigQueryService).streamInsert(eq(projectId), eq(datasetId), eq(tableId), eq(row), anyString());

        assertThat(row.get(MonitoringLogTaskParameters.CLIENT_ID), CoreMatchers.<Object>is(clientId));
        assertThat(row.get(MonitoringLogTaskParameters.USER_ID), CoreMatchers.<Object>is(userId));
    }

    @Test
    public void testMonitoringTestExecutesAndCallsStreamInsertWithTokenExpiredBecauseTokenInfoIsNull() throws IOException {

        clientId = "N/A (token expired)";
        userId = "N/A (token expired)";

        given(googleOAuthClientService.lookupTokenInfo(bearerToken)).willReturn(null);

        monitoringLogTask.execute(ip, host, resource, bearerToken, api, time);

        verify(googleOAuthClientService, atMost(3)).lookupTokenInfo(bearerToken);

        assertThat(row.get(MonitoringLogTaskParameters.CLIENT_ID), CoreMatchers.<Object>is(clientId));
        assertThat(row.get(MonitoringLogTaskParameters.USER_ID), CoreMatchers.<Object>is(userId));
    }

    @Test
    public void testMonitoringTestExecutesAndCallsStreamInsertWithTokenExpiredBecauseClientIdIsNull() throws IOException {

        clientId = "N/A (token expired)";

        given(tokenInfo.getIssued_to()).willReturn(null);
        given(tokenInfo.getEmail()).willReturn(userId);
        given(googleOAuthClientService.lookupTokenInfo(bearerToken)).willReturn(tokenInfo);

        monitoringLogTask.execute(ip, host, resource, bearerToken, api, time);

        verify(googleOAuthClientService, atMost(1)).lookupTokenInfo(bearerToken);
        verify(tokenInfo, atMost(2)).getIssued_to();
        verify(tokenInfo, atMost(3)).getEmail();

        assertThat(row.get(MonitoringLogTaskParameters.CLIENT_ID), CoreMatchers.<Object>is(clientId));
        assertThat(row.get(MonitoringLogTaskParameters.USER_ID), CoreMatchers.<Object>is(userId));
    }

    @Test
    public void testMonitoringTestExecutesAndCallsStreamInsertWithTokenExpiredBecauseClientIdIsEmpty() throws IOException {

        clientId = "N/A (token expired)";

        given(tokenInfo.getIssued_to()).willReturn("");
        given(tokenInfo.getEmail()).willReturn(userId);
        given(googleOAuthClientService.lookupTokenInfo(bearerToken)).willReturn(tokenInfo);

        monitoringLogTask.execute(ip, host, resource, bearerToken, api, time);

        verify(googleOAuthClientService, atMost(1)).lookupTokenInfo(bearerToken);
        verify(tokenInfo, atMost(2)).getIssued_to();
        verify(tokenInfo, atMost(3)).getEmail();

        assertThat(row.get(MonitoringLogTaskParameters.CLIENT_ID), CoreMatchers.<Object>is(clientId));
        assertThat(row.get(MonitoringLogTaskParameters.USER_ID), CoreMatchers.<Object>is(userId));
    }

    @Test
    public void testMonitoringTestExecutesAndCallsStreamInsertWithTokenExpiredBecauseUserIdIsNull() throws IOException {

        userId = "N/A (token expired)";

        given(tokenInfo.getIssued_to()).willReturn(clientId);
        given(tokenInfo.getEmail()).willReturn(null);
        given(googleOAuthClientService.lookupTokenInfo(bearerToken)).willReturn(tokenInfo);

        monitoringLogTask.execute(ip, host, resource, bearerToken, api, time);

        verify(googleOAuthClientService, atMost(1)).lookupTokenInfo(bearerToken);
        verify(tokenInfo, atMost(3)).getIssued_to();
        verify(tokenInfo, atMost(2)).getEmail();

        assertThat(row.get(MonitoringLogTaskParameters.CLIENT_ID), CoreMatchers.<Object>is(clientId));
        assertThat(row.get(MonitoringLogTaskParameters.USER_ID), CoreMatchers.<Object>is(userId));
    }

    @Test
    public void testMonitoringTestExecutesAndCallsStreamInsertWithTokenExpiredBecauseUserIdIsEmpty() throws IOException {

        userId = "N/A (token expired)";

        given(tokenInfo.getIssued_to()).willReturn(clientId);
        given(tokenInfo.getEmail()).willReturn("");
        given(googleOAuthClientService.lookupTokenInfo(bearerToken)).willReturn(tokenInfo);

        monitoringLogTask.execute(ip, host, resource, bearerToken, api, time);

        verify(googleOAuthClientService, atMost(1)).lookupTokenInfo(bearerToken);
        verify(tokenInfo, atMost(3)).getIssued_to();
        verify(tokenInfo, atMost(2)).getEmail();

        assertThat(row.get(MonitoringLogTaskParameters.CLIENT_ID), CoreMatchers.<Object>is(clientId));
        assertThat(row.get(MonitoringLogTaskParameters.USER_ID), CoreMatchers.<Object>is(userId));
    }

    @Test
    public void testMonitoringTestExecutesAndAttemptTwiceWhenThereIsANetworkIssue() throws IOException {

        clientId = "N/A (token expired)";
        userId = "N/A (token expired)";

        given(googleOAuthClientService.lookupTokenInfo(bearerToken)).willThrow(new IOException());

        monitoringLogTask.execute(ip, host, resource, bearerToken, api, time);

        verify(googleOAuthClientService, atMost(2)).lookupTokenInfo(bearerToken);
        verify(tokenInfo, never()).getIssued_to();
        verify(tokenInfo, never()).getEmail();

        assertThat(row.get(MonitoringLogTaskParameters.CLIENT_ID), CoreMatchers.<Object>is(clientId));
        assertThat(row.get(MonitoringLogTaskParameters.USER_ID), CoreMatchers.<Object>is(userId));
    }
}


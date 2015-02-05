package com.risevision.monitoring.service.services.queue.tasks;

import com.google.api.services.bigquery.model.TableRow;
import com.risevision.monitoring.service.queue.tasks.MonitoringLogTask;
import com.risevision.monitoring.service.services.storage.bigquery.BigQueryService;
import com.risevision.monitoring.service.util.Options;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Created by rodrigopavezi on 2/5/15.
 */
public class MonitoringLogTaskTest {

    @Mock
    private BigQueryService bigQueryService;
    @Mock
    private Options options;

    private TableRow row;

    private MonitoringLogTask monitoringLogTask;

    private String ip;
    private String host;
    private String resource;
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
        monitoringLogTask = new MonitoringLogTask(bigQueryService, options, row);

        ip = "1.1.1.1";
        host = "test.com";
        resource = "/_ah/spi/test";
        clientId = "xxxxxxx";
        api = "CoreAPIv1";
        userId = "example@gmail.com";
        time = String.valueOf(System.currentTimeMillis() / 1000L);

        projectId = "rvacore-test";
        datasetId = "monitoringLogs";
        tableId = "apiRequests";
    }

    @Test
    public void testMonitoringTestExecutesAndCallsBigQueryStreamInsert() throws IOException {

        given(options.getPROJECT_ID()).willReturn(projectId);
        given(options.getDATASET_ID()).willReturn(datasetId);
        given(options.getMONITORING_LOG_TABLE_ID()).willReturn(tableId);

        monitoringLogTask.execute(ip, host, resource, clientId, api, userId, time);

        verify(bigQueryService).streamInsert(eq(projectId), eq(datasetId), eq(tableId), eq(row), anyString());
    }
}

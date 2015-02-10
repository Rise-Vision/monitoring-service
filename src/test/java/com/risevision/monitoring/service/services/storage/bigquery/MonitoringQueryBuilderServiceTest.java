package com.risevision.monitoring.service.services.storage.bigquery;

import com.risevision.monitoring.service.util.Options;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Created by rodrigopavezi on 1/27/15.
 */
public class MonitoringQueryBuilderServiceTest {

    @Mock
    private BigQueryService bigQueryService;
    @Mock
    private Options options;

    private QueryBuilderService monitoringQueryBuilderService;
    private String projectId;
    private String datasetId;

    private String clientId;
    private String api;
    private String conditionalWithAPIAndClientId;
    private String orderBy;
    private String tableId;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        projectId = "projectId";
        datasetId = "datasetId";
        tableId = "requests";

        monitoringQueryBuilderService = new MonitoringQueryBuilderService(options);

        clientId = "xxxxxxxxxxx";
        api = "CoreAPIv1";
        conditionalWithAPIAndClientId = "clientId=" + clientId + " AND api=" + api;
        orderBy = "time ASC";

        given(options.getDATASET_ID()).willReturn(datasetId);
        given(options.getMONITORING_LOG_TABLE_ID()).willReturn(tableId);
    }

    private String getExpectedQuery(String conditionalWithAPIAndClientId, String orderBy, String datasetId, String tableId) {

        return "SELECT " +
                "ip, " +
                "host, " +
                "resource, " +
                "clientId, " +
                "api, " +
                "userId, " +
                "time " +
                "FROM " +
                "[" + datasetId + "." + tableId + "] " +
                "WHERE " +
                conditionalWithAPIAndClientId +
                " ORDER BY " +
                orderBy;

    }

    @Test
    public void testBuildQuery() throws IOException {

        String expectedQuery = getExpectedQuery(conditionalWithAPIAndClientId, orderBy, datasetId, tableId);

        String actualQuery = monitoringQueryBuilderService.buildQuery(conditionalWithAPIAndClientId, orderBy);

        assertThat(actualQuery, is(expectedQuery));
    }
}

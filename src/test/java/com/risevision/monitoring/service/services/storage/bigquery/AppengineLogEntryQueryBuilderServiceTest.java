package com.risevision.monitoring.service.services.storage.bigquery;

import com.google.api.services.bigquery.model.TableList;
import com.google.api.services.bigquery.model.TableReference;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Created by rodrigopavezi on 1/27/15.
 */
public class AppengineLogEntryQueryBuilderServiceTest {

    @Mock
    private BigQueryService bigQueryService;

    private QueryBuilderService logEntryQueryBuilderService;
    private String projectId;
    private String datasetId;

    private List<TableList.Tables> tables;
    private List<String> tableIds;
    private String clientId;
    private String api;
    private String conditionalWithAPIAndClientId;
    private String orderBy;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        projectId = "projectId";
        datasetId = "datasetId";

        logEntryQueryBuilderService = new AppengineLogEntryQueryBuilderService(bigQueryService, projectId, datasetId);

        tables = new LinkedList<>();
        tableIds = new LinkedList<>();


        for (int i = 0; i < 10; i++) {
            String tableId = "table" + i + "";
            tableIds.add(tableId);

            TableReference tableReference = new TableReference();
            tableReference.setTableId(tableId);

            TableList.Tables table = new TableList.Tables();
            table.setTableReference(tableReference);
            tables.add(table);

        }

        clientId = "xxxxxxxxxxx";
        api = "CoreAPIv1";
        conditionalWithAPIAndClientId = "protoPayload.line.logMessage like 'com.risevision.monitoring.filter.MonitoringFilter doFilter: Monitoring: data={\"api\":\"" + api + "\",\"clientId\":\"" + clientId + "\"%'";
        orderBy = "protoPayload.line.time ASC";
    }

    private String getExpectedQuery(String conditionalWithAPIAndClientId, String orderBy) {

        String ids = "";
        for (String id : tableIds) {
            ids += "[" + datasetId + "." + id + "],";
        }

        return "SELECT protoPayload.ip, protoPayload.host, protoPayload.resource, protoPayload.line.logMessage, protoPayload.line.time FROM " +
                ids +
                " WHERE " +
                conditionalWithAPIAndClientId +
                " ORDER BY " +
                orderBy;
    }

    @Test
    public void testBuildQuery() throws IOException {

        String expectedQuery = getExpectedQuery(conditionalWithAPIAndClientId, orderBy);

        given(bigQueryService.listTables(projectId, datasetId)).willReturn(tables);

        String actualQuery = logEntryQueryBuilderService.buildQuery(conditionalWithAPIAndClientId, orderBy);

        assertThat(actualQuery, is(expectedQuery));
    }

    @Test
    public void testBuildQueryReturnNullIfTableListCannotBeRetrieved() throws IOException {

        String expectedQuery = null;

        given(bigQueryService.listTables(projectId, datasetId)).willReturn(null);

        String actualQuery = logEntryQueryBuilderService.buildQuery(conditionalWithAPIAndClientId, orderBy);

        assertThat(actualQuery, is(expectedQuery));
    }

    @Test
    public void testBuildQueryReturnNullIfTableListIsEmpty() throws IOException {

        String expectedQuery = null;

        given(bigQueryService.listTables(projectId, datasetId)).willReturn(new LinkedList<TableList.Tables>());

        String actualQuery = logEntryQueryBuilderService.buildQuery(conditionalWithAPIAndClientId, orderBy);

        assertThat(actualQuery, is(expectedQuery));
    }

    @Test
    public void testBuildQueryReturnNullIfExceptionIsThrown() throws IOException {

        String expectedQuery = null;

        given(bigQueryService.listTables(projectId, datasetId)).willThrow(new IOException());

        String actualQuery = logEntryQueryBuilderService.buildQuery(conditionalWithAPIAndClientId, orderBy);

        assertThat(actualQuery, is(expectedQuery));
    }

}

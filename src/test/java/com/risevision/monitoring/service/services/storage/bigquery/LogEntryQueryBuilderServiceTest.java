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
public class LogEntryQueryBuilderServiceTest {

    @Mock
    private BigQueryService bigQueryService;

    private QueryBuilderService logEntryQueryBuilderService;
    private String projectId;
    private String datasetId;

    private List<TableList.Tables> tables;
    private List<String> tableIds;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        projectId = "projectId";
        datasetId = "datasetId";

        logEntryQueryBuilderService = new LogEntryQueryBuilderService(bigQueryService, projectId, datasetId);

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
    }

    private String getExpectedQuery(String conditional, String orderBy) {

        String ids = "";
        for (String id : tableIds) {
            ids += "[" + datasetId + "." + id + "],";
        }

        return "SELECT protoPayload.ip, protoPayload.host, protoPayload.resource, protoPayload.line.logMessage, protoPayload.line.time FROM " +
                ids +
                " WHERE " +
                conditional +
                " ORDER BY " +
                orderBy;
    }

    @Test
    public void testBuildQuery() throws IOException {

        String clientId = "xxxxxxxxxxx";
        String api = "CoreAPIv1";
        String conditional = "protoPayload.line.logMessage like 'com.risevision.monitor.MonitoringFilter doFilter: Monitoring: data={\"api\":\"" + api + "\",\"clientId\":\"" + clientId + "\"}%'";
        String orderBy = "protoPayload.line.time ASC";

        String expectedQuery = getExpectedQuery(conditional, orderBy);


        given(bigQueryService.listTables(projectId, datasetId)).willReturn(tables);

        String actualQuery = logEntryQueryBuilderService.buildQuery(conditional, orderBy);

        assertThat(actualQuery, is(expectedQuery));
    }

    @Test
    public void testBuildQueryReturnNullIfTableListCannotBeRetrieved() throws IOException {

        String clientId = "xxxxxxxxxxx";
        String api = "CoreAPIv1";
        String conditional = "protoPayload.line.logMessage like 'com.risevision.monitor.MonitoringFilter doFilter: Monitoring: data={\"api\":\"" + api + "\",\"clientId\":\"" + clientId + "\"}%'";
        String orderBy = "protoPayload.line.time ASC";

        String expectedQuery = null;


        given(bigQueryService.listTables(projectId, datasetId)).willReturn(null);

        String actualQuery = logEntryQueryBuilderService.buildQuery(conditional, orderBy);

        assertThat(actualQuery, is(expectedQuery));
    }

    @Test
    public void testBuildQueryReturnNullIfTableListIsEmpty() throws IOException {

        String clientId = "xxxxxxxxxxx";
        String api = "CoreAPIv1";
        String conditional = "protoPayload.line.logMessage like 'com.risevision.monitor.MonitoringFilter doFilter: Monitoring: data={\"api\":\"" + api + "\",\"clientId\":\"" + clientId + "\"}%'";
        String orderBy = "protoPayload.line.time ASC";

        String expectedQuery = null;


        given(bigQueryService.listTables(projectId, datasetId)).willReturn(new LinkedList<TableList.Tables>());

        String actualQuery = logEntryQueryBuilderService.buildQuery(conditional, orderBy);

        assertThat(actualQuery, is(expectedQuery));
    }

    @Test
    public void testBuildQueryReturnNullIfExceptionIsThrown() throws IOException {

        String clientId = "xxxxxxxxxxx";
        String api = "CoreAPIv1";
        String conditional = "protoPayload.line.logMessage like 'com.risevision.monitor.MonitoringFilter doFilter: Monitoring: data={\"api\":\"" + api + "\",\"clientId\":\"" + clientId + "\"}%'";
        String orderBy = "protoPayload.line.time ASC";

        String expectedQuery = null;


        given(bigQueryService.listTables(projectId, datasetId)).willThrow(new IOException());

        String actualQuery = logEntryQueryBuilderService.buildQuery(conditional, orderBy);

        assertThat(actualQuery, is(expectedQuery));
    }

}

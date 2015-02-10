package com.risevision.monitoring.service.services.storage.bigquery;

import com.google.api.services.bigquery.model.Job;
import com.google.api.services.bigquery.model.JobReference;
import com.google.api.services.bigquery.model.TableCell;
import com.google.api.services.bigquery.model.TableRow;
import com.risevision.monitoring.service.services.storage.bigquery.entities.AppengineLogEntry;
import com.risevision.monitoring.service.services.storage.bigquery.entities.LogEntry;
import com.risevision.monitoring.service.util.Options;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.SimpleTimeZone;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by rodrigopavezi on 1/27/15.
 */
public class BigQueryLogEntryServiceTest {

    private final String PROJECT_ID = "rvacore-test";
    List<LogEntry> logEntries;
    @Mock
    private BigQueryService bigQueryService;
    @Mock
    private QueryBuilderService queryBuilderService;
    @Mock
    private Options options;

    private JobReference jobReference;
    private Job job;
    private List<TableRow> rows;
    private LogEntryService bigQueryLogEntryService;
    private String clientId;
    private String api;
    private Date date;
    private String conditionalWithAPIAndClientId;
    private String conditionalWithAPIClientIdAndDate;
    private String orderBy;

    @Before
    public void setup() throws ParseException {
        MockitoAnnotations.initMocks(this);
        bigQueryLogEntryService = new BiqQueryAppengineLogEntryService(bigQueryService, queryBuilderService, options);

        jobReference = new JobReference();
        job = new Job();

        rows = new LinkedList<>();
        logEntries = new LinkedList<>();

        for (int i = 0; i < 10; i++) {

            LogEntry logEntry = getLogEntry();
            logEntries.add(logEntry);

            TableRow row = new TableRow();
            row.setF(getCells((AppengineLogEntry) logEntry));
            rows.add(row);
        }


        clientId = "xxxxxxxxxxx";
        api = "CoreAPIv1";
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
        date = sdf.parse(sdf.format(new Date()));

        conditionalWithAPIAndClientId = "protoPayload.line.logMessage like 'com.risevision.monitoring.filter.MonitoringFilter doFilter: Monitoring: data={\"api\":\"" + api + "\",\"clientId\":\"" + clientId + "\"%'";
        conditionalWithAPIClientIdAndDate = "protoPayload.line.logMessage like 'com.risevision.monitoring.filter.MonitoringFilter doFilter: Monitoring: data={\"api\":\"" + api + "\",\"clientId\":\"" + clientId + "\"%' AND protoPayload.line.time > '" + date.getTime() / 1000 + "'";
        orderBy = "protoPayload.line.time ASC";

        given(options.getPROJECT_ID()).willReturn(PROJECT_ID);
    }


    private LogEntry getLogEntry() {
        AppengineLogEntry logEntry = new AppengineLogEntry();
        logEntry.setIp("1.1.1.1");
        logEntry.setHost("test.com");
        logEntry.setResource("/test/test");
        Double timestamp = Double.parseDouble(String.valueOf((double) (new Date().getTime() / 1000)));
        logEntry.setTime(new Date(timestamp.longValue() * 1000));
        logEntry.setLogMessage("message");

        return logEntry;
    }


    private List<TableCell> getCells(AppengineLogEntry logEntry) {

        List<TableCell> cells = new LinkedList<>();

        TableCell cell1 = new TableCell();
        cell1.setV(logEntry.getIp());
        cells.add(cell1);

        TableCell cell2 = new TableCell();
        cell2.setV(logEntry.getHost());
        cells.add(cell2);

        TableCell cell3 = new TableCell();
        cell3.setV(logEntry.getResource());
        cells.add(cell3);

        TableCell cell4 = new TableCell();
        cell4.setV(logEntry.getLogMessage());
        cells.add(cell4);

        TableCell cell5 = new TableCell();
        cell5.setV(String.valueOf((double) (logEntry.getTime().getTime() / 1000)));
        cells.add(cell5);


        return cells;
    }

    private String getExpectedQuery(String conditional, String orderBy) {

        return "SELECT protoPayload.ip, protoPayload.host, protoPayload.resource, protoPayload.line.logMessage, protoPayload.line.time FROM " +
                "[table]" +
                " WHERE " +
                conditional +
                " ORDER BY " +
                orderBy;
    }

    @Test
    public void testGetLogEntriesOrderedByDate() throws IOException, InterruptedException {


        String expectedQuery = getExpectedQuery(conditionalWithAPIAndClientId, orderBy);

        given(queryBuilderService.buildQuery(conditionalWithAPIAndClientId, orderBy)).willReturn(expectedQuery);
        given(bigQueryService.startQuery(expectedQuery, PROJECT_ID)).willReturn(jobReference);
        given(bigQueryService.checkQueryResults(PROJECT_ID, jobReference)).willReturn(job);
        given(bigQueryService.getQueryResults(PROJECT_ID, job)).willReturn(rows);

        List<LogEntry> actualLogEntries = bigQueryLogEntryService.getLogEntriesOrderedByDate(clientId, api);

        verify(queryBuilderService).buildQuery(conditionalWithAPIAndClientId, orderBy);
        verify(bigQueryService).startQuery(expectedQuery, PROJECT_ID);
        verify(bigQueryService).checkQueryResults(PROJECT_ID, jobReference);
        verify(bigQueryService).getQueryResults(PROJECT_ID, job);

        assertThat(actualLogEntries, is(this.logEntries));
    }


    @Test
    public void testGetLogEntriesAfterDateOrderedByDate() throws IOException, InterruptedException {

        String expectedQuery = getExpectedQuery(conditionalWithAPIClientIdAndDate, orderBy);
        given(queryBuilderService.buildQuery(conditionalWithAPIClientIdAndDate, orderBy)).willReturn(expectedQuery);
        given(bigQueryService.startQuery(expectedQuery, PROJECT_ID)).willReturn(jobReference);
        given(bigQueryService.checkQueryResults(PROJECT_ID, jobReference)).willReturn(job);
        given(bigQueryService.getQueryResults(PROJECT_ID, job)).willReturn(rows);

        List<LogEntry> actualLogEntries = bigQueryLogEntryService.getLogEntriesAfterDateOrderedByDate(clientId, api, date);

        verify(queryBuilderService).buildQuery(conditionalWithAPIClientIdAndDate, orderBy);
        verify(bigQueryService).startQuery(expectedQuery, PROJECT_ID);
        verify(bigQueryService).checkQueryResults(PROJECT_ID, jobReference);
        verify(bigQueryService).getQueryResults(PROJECT_ID, job);

        assertThat(actualLogEntries, is(this.logEntries));
    }

    @Test
    public void testGetLogEntriesAfterDateOrderedByDateReturnsNullIfQueryIsNull() throws IOException, InterruptedException {

        String expectedQuery = null;
        given(queryBuilderService.buildQuery(conditionalWithAPIClientIdAndDate, orderBy)).willReturn(expectedQuery);
        given(bigQueryService.startQuery(expectedQuery, PROJECT_ID)).willReturn(jobReference);
        given(bigQueryService.checkQueryResults(PROJECT_ID, jobReference)).willReturn(job);
        given(bigQueryService.getQueryResults(PROJECT_ID, job)).willReturn(rows);

        List<LogEntry> actualLogEntries = bigQueryLogEntryService.getLogEntriesAfterDateOrderedByDate(clientId, api, date);

        verify(queryBuilderService).buildQuery(conditionalWithAPIClientIdAndDate, orderBy);
        verify(bigQueryService, never()).startQuery(expectedQuery, PROJECT_ID);
        verify(bigQueryService, never()).checkQueryResults(PROJECT_ID, jobReference);
        verify(bigQueryService, never()).getQueryResults(PROJECT_ID, job);

        assertThat(actualLogEntries, is(nullValue()));
    }

    @Test
    public void testGetLogEntriesOrderedByDateReturnsNullIfQueryIsNull() throws IOException, InterruptedException {

        String expectedQuery = null;
        given(queryBuilderService.buildQuery(conditionalWithAPIAndClientId, orderBy)).willReturn(expectedQuery);
        given(bigQueryService.startQuery(expectedQuery, PROJECT_ID)).willReturn(jobReference);
        given(bigQueryService.checkQueryResults(PROJECT_ID, jobReference)).willReturn(job);
        given(bigQueryService.getQueryResults(PROJECT_ID, job)).willReturn(rows);

        List<LogEntry> actualLogEntries = bigQueryLogEntryService.getLogEntriesOrderedByDate(clientId, api);

        verify(queryBuilderService).buildQuery(conditionalWithAPIAndClientId, orderBy);
        verify(bigQueryService, never()).startQuery(expectedQuery, PROJECT_ID);
        verify(bigQueryService, never()).checkQueryResults(PROJECT_ID, jobReference);
        verify(bigQueryService, never()).getQueryResults(PROJECT_ID, job);

        assertThat(actualLogEntries, is(nullValue()));
    }

    @Test
    public void testGetLogEntriesAfterDateOrderedByDateReturnsNullIfQueryIsEmpty() throws IOException, InterruptedException {

        String expectedQuery = "";
        given(queryBuilderService.buildQuery(conditionalWithAPIClientIdAndDate, orderBy)).willReturn(expectedQuery);
        given(bigQueryService.startQuery(expectedQuery, PROJECT_ID)).willReturn(jobReference);
        given(bigQueryService.checkQueryResults(PROJECT_ID, jobReference)).willReturn(job);
        given(bigQueryService.getQueryResults(PROJECT_ID, job)).willReturn(rows);

        List<LogEntry> actualLogEntries = bigQueryLogEntryService.getLogEntriesAfterDateOrderedByDate(clientId, api, date);

        verify(queryBuilderService).buildQuery(conditionalWithAPIClientIdAndDate, orderBy);
        verify(bigQueryService, never()).startQuery(expectedQuery, PROJECT_ID);
        verify(bigQueryService, never()).checkQueryResults(PROJECT_ID, jobReference);
        verify(bigQueryService, never()).getQueryResults(PROJECT_ID, job);

        assertThat(actualLogEntries, is(nullValue()));
    }

    @Test
    public void testGetLogEntriesOrderedByDateReturnsNullIfQueryIsEmpty() throws IOException, InterruptedException {

        String expectedQuery = "";
        given(queryBuilderService.buildQuery(conditionalWithAPIAndClientId, orderBy)).willReturn(expectedQuery);
        given(bigQueryService.startQuery(expectedQuery, PROJECT_ID)).willReturn(jobReference);
        given(bigQueryService.checkQueryResults(PROJECT_ID, jobReference)).willReturn(job);
        given(bigQueryService.getQueryResults(PROJECT_ID, job)).willReturn(rows);

        List<LogEntry> actualLogEntries = bigQueryLogEntryService.getLogEntriesOrderedByDate(clientId, api);

        verify(queryBuilderService).buildQuery(conditionalWithAPIAndClientId, orderBy);
        verify(bigQueryService, never()).startQuery(expectedQuery, PROJECT_ID);
        verify(bigQueryService, never()).checkQueryResults(PROJECT_ID, jobReference);
        verify(bigQueryService, never()).getQueryResults(PROJECT_ID, job);

        assertThat(actualLogEntries, is(nullValue()));
    }

    @Test
    public void testGetLogEntriesOrderedByDateReturnsNullIfJobReferenceIsNull() throws IOException, InterruptedException {

        String expectedQuery = getExpectedQuery(conditionalWithAPIAndClientId, orderBy);
        given(queryBuilderService.buildQuery(conditionalWithAPIAndClientId, orderBy)).willReturn(expectedQuery);
        given(bigQueryService.startQuery(expectedQuery, PROJECT_ID)).willReturn(null);
        given(bigQueryService.checkQueryResults(PROJECT_ID, jobReference)).willReturn(job);
        given(bigQueryService.getQueryResults(PROJECT_ID, job)).willReturn(rows);

        List<LogEntry> actualLogEntries = bigQueryLogEntryService.getLogEntriesOrderedByDate(clientId, api);

        verify(queryBuilderService).buildQuery(conditionalWithAPIAndClientId, orderBy);
        verify(bigQueryService).startQuery(expectedQuery, PROJECT_ID);
        verify(bigQueryService, never()).checkQueryResults(PROJECT_ID, jobReference);
        verify(bigQueryService, never()).getQueryResults(PROJECT_ID, job);

        assertThat(actualLogEntries, is(nullValue()));
    }

    @Test
    public void testGetLogEntriesOrderedByDateReturnsNullIfStartQueryThrowsAnException() throws IOException, InterruptedException {

        String expectedQuery = getExpectedQuery(conditionalWithAPIAndClientId, orderBy);
        given(queryBuilderService.buildQuery(conditionalWithAPIAndClientId, orderBy)).willReturn(expectedQuery);
        given(bigQueryService.startQuery(expectedQuery, PROJECT_ID)).willThrow(new IOException());
        given(bigQueryService.checkQueryResults(PROJECT_ID, jobReference)).willReturn(job);
        given(bigQueryService.getQueryResults(PROJECT_ID, job)).willReturn(rows);

        List<LogEntry> actualLogEntries = bigQueryLogEntryService.getLogEntriesOrderedByDate(clientId, api);

        verify(queryBuilderService).buildQuery(conditionalWithAPIAndClientId, orderBy);
        verify(bigQueryService).startQuery(expectedQuery, PROJECT_ID);
        verify(bigQueryService, never()).checkQueryResults(PROJECT_ID, jobReference);
        verify(bigQueryService, never()).getQueryResults(PROJECT_ID, job);

        assertThat(actualLogEntries, is(nullValue()));
    }

    @Test
    public void testGetLogEntriesOrderedByDateReturnsNullIfCheckQueryResultsThrowsAnIOException() throws IOException, InterruptedException {

        String expectedQuery = getExpectedQuery(conditionalWithAPIAndClientId, orderBy);
        given(queryBuilderService.buildQuery(conditionalWithAPIAndClientId, orderBy)).willReturn(expectedQuery);
        given(bigQueryService.startQuery(expectedQuery, PROJECT_ID)).willReturn(jobReference);
        given(bigQueryService.checkQueryResults(PROJECT_ID, jobReference)).willThrow(new IOException());
        given(bigQueryService.getQueryResults(PROJECT_ID, job)).willReturn(rows);

        List<LogEntry> actualLogEntries = bigQueryLogEntryService.getLogEntriesOrderedByDate(clientId, api);

        verify(queryBuilderService).buildQuery(conditionalWithAPIAndClientId, orderBy);
        verify(bigQueryService).startQuery(expectedQuery, PROJECT_ID);
        verify(bigQueryService).checkQueryResults(PROJECT_ID, jobReference);
        verify(bigQueryService, never()).getQueryResults(PROJECT_ID, job);

        assertThat(actualLogEntries, is(nullValue()));
    }

    @Test
    public void testGetLogEntriesOrderedByDateReturnsNullIfGetQueryResultsThrowsAnIOException() throws IOException, InterruptedException {

        String expectedQuery = getExpectedQuery(conditionalWithAPIAndClientId, orderBy);
        given(queryBuilderService.buildQuery(conditionalWithAPIAndClientId, orderBy)).willReturn(expectedQuery);
        given(bigQueryService.startQuery(expectedQuery, PROJECT_ID)).willReturn(jobReference);
        given(bigQueryService.checkQueryResults(PROJECT_ID, jobReference)).willReturn(job);
        given(bigQueryService.getQueryResults(PROJECT_ID, job)).willThrow(new IOException());

        List<LogEntry> actualLogEntries = bigQueryLogEntryService.getLogEntriesOrderedByDate(clientId, api);

        verify(queryBuilderService).buildQuery(conditionalWithAPIAndClientId, orderBy);
        verify(bigQueryService).startQuery(expectedQuery, PROJECT_ID);
        verify(bigQueryService).checkQueryResults(PROJECT_ID, jobReference);
        verify(bigQueryService).getQueryResults(PROJECT_ID, job);

        assertThat(actualLogEntries, is(nullValue()));
    }

    @Test
    public void testGetLogEntriesOrderedByDateReturnsNullIfJobIsNull() throws IOException, InterruptedException {

        String expectedQuery = getExpectedQuery(conditionalWithAPIAndClientId, orderBy);
        given(queryBuilderService.buildQuery(conditionalWithAPIAndClientId, orderBy)).willReturn(expectedQuery);
        given(bigQueryService.startQuery(expectedQuery, PROJECT_ID)).willReturn(jobReference);
        given(bigQueryService.checkQueryResults(PROJECT_ID, jobReference)).willReturn(null);
        given(bigQueryService.getQueryResults(PROJECT_ID, job)).willReturn(rows);

        List<LogEntry> actualLogEntries = bigQueryLogEntryService.getLogEntriesOrderedByDate(clientId, api);

        verify(queryBuilderService).buildQuery(conditionalWithAPIAndClientId, orderBy);
        verify(bigQueryService).startQuery(expectedQuery, PROJECT_ID);
        verify(bigQueryService).checkQueryResults(PROJECT_ID, jobReference);
        verify(bigQueryService, never()).getQueryResults(PROJECT_ID, job);

        assertThat(actualLogEntries, is(nullValue()));
    }

    @Test
    public void testGetLogEntriesOrderedByDateReturnsNullIfRowsIsNull() throws IOException, InterruptedException {

        String expectedQuery = getExpectedQuery(conditionalWithAPIAndClientId, orderBy);
        given(queryBuilderService.buildQuery(conditionalWithAPIAndClientId, orderBy)).willReturn(expectedQuery);
        given(bigQueryService.startQuery(expectedQuery, PROJECT_ID)).willReturn(jobReference);
        given(bigQueryService.checkQueryResults(PROJECT_ID, jobReference)).willReturn(job);
        given(bigQueryService.getQueryResults(PROJECT_ID, job)).willReturn(null);

        List<LogEntry> actualLogEntries = bigQueryLogEntryService.getLogEntriesOrderedByDate(clientId, api);

        verify(queryBuilderService).buildQuery(conditionalWithAPIAndClientId, orderBy);
        verify(bigQueryService).startQuery(expectedQuery, PROJECT_ID);
        verify(bigQueryService).checkQueryResults(PROJECT_ID, jobReference);
        verify(bigQueryService).getQueryResults(PROJECT_ID, job);

        assertThat(actualLogEntries, is(nullValue()));
    }

}

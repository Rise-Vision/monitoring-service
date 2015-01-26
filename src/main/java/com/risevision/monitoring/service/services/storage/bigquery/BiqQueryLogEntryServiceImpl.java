package com.risevision.monitoring.service.services.storage.bigquery;

import com.google.api.services.bigquery.model.Job;
import com.google.api.services.bigquery.model.JobReference;
import com.google.api.services.bigquery.model.TableList;
import com.google.api.services.bigquery.model.TableRow;
import com.risevision.monitoring.service.services.storage.bigquery.entities.LogEntry;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by rodrigopavezi on 1/22/15.
 */
public class BiqQueryLogEntryServiceImpl implements LogEntryService {

    private final String PROJECT_ID = "rvaserver2";
    private final String DATASET_ID = "appengine_logs";

    private BigQueryService bigQueryService;

    public BiqQueryLogEntryServiceImpl() {
        bigQueryService = new BigQueryServiceImpl();
    }


    @Override
    public List<LogEntry> getLogEntriesOrderedByDate(String clientId, String api) throws IOException, InterruptedException {

        String conditional = "protoPayload.line.logMessage like '%Monitoring: data={\"api\":\"" + api + "\", \"clientId\":\"" + clientId + "\"}%'";
        String orderBy = "ORDER BY protoPayload.line.time ASC";

        String query = getQuery(conditional, orderBy);

        return getLogEntriesForQuery(query);
    }

    @Override
    public List<LogEntry> getLogEntriesAfterDateOrdedByDate(String clientId, String api, Date lastCall) throws IOException, InterruptedException {

        String conditional = "protoPayload.line.logMessage like '%Monitoring: data={\"api\":\"" + api + "\", \"clientId\":\"" + clientId + "\"}%' AND" +
                "protoPayload.line.time >= " + lastCall.getTime();
        String orderBy = "ORDER BY protoPayload.line.time ASC";

        String query = getQuery(conditional, orderBy);
        return getLogEntriesForQuery(query);
    }

    private List<LogEntry> getLogEntriesForQuery(String query) throws IOException, InterruptedException {

        JobReference jobReference = bigQueryService.startQuery(query, PROJECT_ID);

        Job completedJob = bigQueryService.checkQueryResults(PROJECT_ID, jobReference);

        List<TableRow> resultRows = bigQueryService.getQueryResults(PROJECT_ID, completedJob);

        List<LogEntry> logEntries = new LinkedList<>();
        for (TableRow row : resultRows) {
            LogEntry logEntry = new LogEntry();
            logEntry.setIp((String) row.getF().get(0).getV());
            logEntry.setHost((String) row.getF().get(1).getV());
            logEntry.setResource((String) row.getF().get(2).getV());
            logEntry.setLogMessage((String) row.getF().get(3).getV());
            logEntry.setTime((Date) row.getF().get(4).getV());
            logEntries.add(logEntry);
        }

        return logEntries;
    }

    private String getQuery(String conditional, String orderBy) throws IOException {

        List<TableList.Tables> tables = bigQueryService.listTables(PROJECT_ID, DATASET_ID);
        String tableIdsForTheQuery = "";
        for (TableList.Tables table : tables) {
            tableIdsForTheQuery += "[" + table.getTableReference().getTableId() + "],";
        }

        String query = "SELECT " +
                "protoPayload.ip, " +
                "protoPayload.host, " +
                "protoPayload.resource, " +
                "protoPayload.line.logMessage, " +
                "protoPayload.line.time FROM " +
                tableIdsForTheQuery +
                "WHERE " +
                conditional +
                orderBy;

        return query;

    }
}

package com.risevision.monitoring.service.services.storage.bigquery;

import com.google.api.services.bigquery.model.Job;
import com.google.api.services.bigquery.model.JobReference;
import com.google.api.services.bigquery.model.TableRow;
import com.risevision.monitoring.service.services.storage.bigquery.entities.AppengineLogEntry;
import com.risevision.monitoring.service.services.storage.bigquery.entities.LogEntry;
import com.risevision.monitoring.service.util.Options;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rodrigopavezi on 1/22/15.
 */
public class BiqQueryAppengineLogEntryService implements LogEntryService {

    private final Logger logger = Logger.getLogger(BiqQueryAppengineLogEntryService.class.getName());

    private BigQueryService bigQueryService;
    private QueryBuilderService logEntryQueryBuilderService;
    private Options options;

    public BiqQueryAppengineLogEntryService() {
        bigQueryService = new BigQueryServiceImpl();
        options = Options.getInstance();
        logEntryQueryBuilderService = new AppengineLogEntryQueryBuilderService(bigQueryService, options.getPROJECT_ID(), options.getDATASET_ID());
    }

    public BiqQueryAppengineLogEntryService(BigQueryService bigQueryService, QueryBuilderService logEntryQueryBuilderService, Options options) {
        this.bigQueryService = bigQueryService;
        this.logEntryQueryBuilderService = logEntryQueryBuilderService;
        this.options = options;
    }


    @Override
    public List<LogEntry> getLogEntriesOrderedByDate(String clientId, String api) {

        List<LogEntry> logEntries = null;

        String conditional = "protoPayload.line.logMessage like 'com.risevision.monitoring.filter.MonitoringFilter doFilter: Monitoring: data={\"api\":\"" + api + "\",\"clientId\":\"" + clientId + "\"%'";
        String orderBy = "protoPayload.line.time ASC";

        String query = logEntryQueryBuilderService.buildQuery(conditional, orderBy);

        if (query != null && !query.isEmpty()) {
            logEntries = getLogEntriesForQuery(query);
        } else {
            logger.warning("LogEntry Query is null or empty");
        }

        return logEntries;
    }

    @Override
    public List<LogEntry> getLogEntriesAfterDateOrderedByDate(String clientId, String api, Date lastCall) {
        List<LogEntry> logEntries = null;

        String conditional = "protoPayload.line.logMessage like 'com.risevision.monitoring.filter.MonitoringFilter doFilter: Monitoring: data={\"api\":\"" + api + "\",\"clientId\":\"" + clientId + "\"%' AND " +
                "protoPayload.line.time > '" + (lastCall.getTime() / 1000) + "'";
        String orderBy = "protoPayload.line.time ASC";

        String query = logEntryQueryBuilderService.buildQuery(conditional, orderBy);

        if (query != null && !query.isEmpty()) {
            logEntries = getLogEntriesForQuery(query);
        } else {
            logger.warning("LogEntry Query is null or empty");
        }

        return logEntries;
    }

    private List<LogEntry> getLogEntriesForQuery(String query) {
        List<LogEntry> logEntries = null;

        try {
            JobReference jobReference = bigQueryService.startQuery(query, options.getPROJECT_ID());

            Job completedJob = bigQueryService.checkQueryResults(options.getPROJECT_ID(), jobReference);

            List<TableRow> resultRows = bigQueryService.getQueryResults(options.getPROJECT_ID(), completedJob);

            if (resultRows != null && resultRows.size() > 0) {
                logEntries = new LinkedList<>();
                for (TableRow row : resultRows) {
                    AppengineLogEntry logEntry = new AppengineLogEntry();
                    logEntry.setIp((String) row.getF().get(0).getV());
                    logEntry.setHost((String) row.getF().get(1).getV());
                    logEntry.setResource((String) row.getF().get(2).getV());
                    logEntry.setLogMessage((String) row.getF().get(3).getV());

                    Double timestamp = Double.parseDouble((String) row.getF().get(4).getV());
                    // Multiply by 1000, since java is expecting milliseconds
                    logEntry.setTime(new Date(timestamp.longValue() * 1000));
                    logEntries.add(logEntry);
                }
            } else {
                logger.log(Level.INFO, "LogEntries could not be retrieve from Big Query. \n Cause: Query Result are null or empty");
            }
        } catch (Exception exception) {
            logger.log(Level.WARNING, "LogEntries could not be retrieve from Big Query.", exception);
        }

        return logEntries;
    }
}

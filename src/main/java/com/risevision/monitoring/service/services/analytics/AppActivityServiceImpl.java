package com.risevision.monitoring.service.services.analytics;

import com.risevision.monitoring.service.services.date.DateService;
import com.risevision.monitoring.service.services.date.DateServiceImpl;
import com.risevision.monitoring.service.services.storage.bigquery.BigQueryMonitoringLogEntryService;
import com.risevision.monitoring.service.services.storage.bigquery.LogEntryService;
import com.risevision.monitoring.service.services.storage.bigquery.entities.LogEntry;
import com.risevision.monitoring.service.services.storage.datastore.DatastoreService;
import com.risevision.monitoring.service.services.storage.datastore.entities.AppActivityEntity;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by rodrigopavezi on 1/21/15.
 */
public class AppActivityServiceImpl implements AppActivityService {

    private final Logger logger = Logger.getLogger(AppActivityServiceImpl.class.getName());


    // Hardcoded the number of days for the average calculation.
    private final int NUMBER_OF_DAYS = 7;

    private DatastoreService datastoreService;
    private LogEntryService logEntryService;
    private DateService dateService;

    public AppActivityServiceImpl() {
        logEntryService = new BigQueryMonitoringLogEntryService();
        datastoreService = datastoreService.getInstance();
        dateService = new DateServiceImpl();
    }

    public AppActivityServiceImpl(LogEntryService logEntryService, DatastoreService datastoreService, DateService dateService) {
        this.logEntryService = logEntryService;
        this.datastoreService = datastoreService;
        this.dateService = dateService;
    }


    @Override
    public AppActivityEntity getActivity(String clientId, String api) {

        Date daysAgoDate = dateService.getDaysAgoDate(NUMBER_OF_DAYS);

        AppActivityEntity appActivityEntity = (AppActivityEntity) datastoreService.get(new AppActivityEntity(clientId, api));
        List<LogEntry> logEntries;

        if (appActivityEntity == null) {
            logEntries = logEntryService.getLogEntriesOrderedByDate(clientId, api);

            if (logEntries != null) {
                appActivityEntity = new AppActivityEntity(clientId, api);
                Date firstCall = logEntries.get(0).getTime();
                appActivityEntity.setFirstCall(firstCall);

                getLastCallAndAverageCallsFromLogEntries(logEntries, appActivityEntity, daysAgoDate);
                datastoreService.put(appActivityEntity);
            }

        } else {
            // TODO,  We might consider to add a logic here to wait certain time after last call before querying BQ again. Something like 10 to 30 minutes.
            Date lastCall = appActivityEntity.getLastCall();
            if (lastCall != null && lastCall.after(daysAgoDate)) {
                logEntries = logEntryService.getLogEntriesAfterDateOrderedByDate(clientId, api, daysAgoDate);
                if (logEntries != null) {
                    appActivityEntity.setLastCall(logEntries.get(logEntries.size() - 1).getTime());
                    appActivityEntity.setAvgCallsPerDay((double) logEntries.size() / (double) NUMBER_OF_DAYS);

                    datastoreService.put(appActivityEntity);
                }
            } else {
                logEntries = logEntryService.getLogEntriesAfterDateOrderedByDate(clientId, api, appActivityEntity.getLastCall());

                if (logEntries != null) {
                    getLastCallAndAverageCallsFromLogEntries(logEntries, appActivityEntity, daysAgoDate);
                } else {
                    appActivityEntity.setAvgCallsPerDay(0.0);

                }

                datastoreService.put(appActivityEntity);
            }
        }

        return appActivityEntity;
    }

    private void getLastCallAndAverageCallsFromLogEntries(List<LogEntry> logEntries, AppActivityEntity appActivityEntity, Date daysAgoDate) {

        appActivityEntity.setLastCall(logEntries.get(logEntries.size() - 1).getTime());

        int numberOfLogs = 0;
        for (LogEntry logEntry : logEntries) {
            if (logEntry.getTime().after(daysAgoDate) || logEntries.equals(daysAgoDate)) {
                numberOfLogs++;
            }
        }
        appActivityEntity.setAvgCallsPerDay((double) numberOfLogs / (double) NUMBER_OF_DAYS);
    }

}

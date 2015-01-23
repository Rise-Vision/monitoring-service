package com.risevision.monitoring.service.services.analytics;

import com.risevision.monitoring.service.api.resources.AppActivity;
import com.risevision.monitoring.service.services.storage.bigquery.BigQueryService;
import com.risevision.monitoring.service.services.storage.bigquery.BiqQueryServiceImpl;
import com.risevision.monitoring.service.services.storage.bigquery.entities.LogEntry;

import javax.xml.bind.ValidationException;
import java.util.Date;
import java.util.List;

/**
 * Created by rodrigopavezi on 1/21/15.
 */
public class AppActivityServiceImpl implements AppActivityService {

    private BigQueryService bigQueryService;

    public AppActivityServiceImpl(){
        bigQueryService = new BiqQueryServiceImpl();
    }

    public AppActivityServiceImpl(BigQueryService bigQueryService) {
        this.bigQueryService = bigQueryService;
    }

    @Override
    public AppActivity getActivityFromTheLastNumberOfDays(String clientId, String api, int numberOfDays) throws ValidationException {

        AppActivity appActivity = new AppActivity(clientId, api);

        if(numberOfDays <= 0){
            throw new ValidationException("Number of days must be greater than zero.");
        }

        List<LogEntry> logEntries = bigQueryService.getLogEntriesFromTheLastNumberOfDays(clientId,api,numberOfDays);


        if(!logEntries.isEmpty()) {

            Date firstCall = new Date();
            Date lastCall = new Date(0L);

            for (LogEntry logEntry : logEntries) {
                Date time = logEntry.getTime();

                if (time.after(lastCall)) {
                    lastCall = time;
                }

                if (time.before(firstCall)) {
                    firstCall = time;
                }
            }

            float averageCallsPerDay = logEntries.size()/numberOfDays;

            appActivity.setFirstCall(firstCall);
            appActivity.setLastCall(lastCall);
            appActivity.setAvgCallsPerDay(averageCallsPerDay);
        }


        return appActivity;
    }
}

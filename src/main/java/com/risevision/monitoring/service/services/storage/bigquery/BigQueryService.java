package com.risevision.monitoring.service.services.storage.bigquery;

import com.risevision.monitoring.service.services.storage.bigquery.entities.LogEntry;

import java.util.Date;
import java.util.List;

/**
 * Created by rodrigopavezi on 1/21/15.
 */
public interface BigQueryService {

    public List<LogEntry> getLogEntriesFromTheLastNumberOfDays(String clientId, String api, int numberOfDays);

    public List<LogEntry> getLogEntriesOrderedByDate(String clientId, String api);

    public List<LogEntry> getLogEntriesAfterDateOrdedByDate(String clientId, String api, Date lastCall);
}

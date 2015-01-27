package com.risevision.monitoring.service.services.storage.bigquery;

import com.risevision.monitoring.service.services.storage.bigquery.entities.LogEntry;

import java.util.Date;
import java.util.List;

/**
 * Created by rodrigopavezi on 1/21/15.
 */
public interface LogEntryService {

    public List<LogEntry> getLogEntriesOrderedByDate(String clientId, String api);

    public List<LogEntry> getLogEntriesAfterDateOrderedByDate(String clientId, String api, Date lastCall);
}

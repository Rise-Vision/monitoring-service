package com.risevision.monitoring.service.services.storage.bigquery;

import com.risevision.monitoring.service.services.storage.bigquery.entities.LogEntry;

import java.util.List;

/**
 * Created by rodrigopavezi on 1/22/15.
 */
public class BiqQueryServiceImpl implements BigQueryService {
    @Override
    public List<LogEntry> getLogEntriesFromTheLastNumberOfDays(String clientId, String api, int numberOfDays) {
        return null;
    }
}

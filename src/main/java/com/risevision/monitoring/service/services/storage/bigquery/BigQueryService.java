package com.risevision.monitoring.service.services.storage.bigquery;

import com.risevision.monitoring.service.services.storage.bigquery.entities.LogEntry;

/**
 * Created by rodrigopavezi on 1/21/15.
 */
public interface BigQueryService {

    public LogEntry getLogEntry();

}

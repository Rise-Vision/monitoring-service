package com.risevision.monitoring.service.queue.tasks;

import com.google.api.services.bigquery.model.TableRow;
import com.risevision.monitoring.service.services.storage.bigquery.BigQueryService;
import com.risevision.monitoring.service.services.storage.bigquery.BigQueryServiceImpl;
import com.risevision.monitoring.service.util.Options;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by rodrigopavezi on 2/4/15.
 */
public class MonitoringLogTask {

    private BigQueryService bigQueryService;
    private Options options;
    private TableRow row;

    public MonitoringLogTask() {
        bigQueryService = new BigQueryServiceImpl();
        options = Options.getInstance();
        row = new TableRow();
    }

    public MonitoringLogTask(BigQueryService bigQueryService, Options options, TableRow row) {
        this.bigQueryService = bigQueryService;
        this.options = options;
        this.row = row;
    }

    public void execute(String ip, String host, String resource, String clientId, String api, String userId, String time) throws IOException {

        row.set(MonitoringLogTaskParameters.IP, ip);
        row.set(MonitoringLogTaskParameters.HOST, host);
        row.set(MonitoringLogTaskParameters.RESOURCE, resource);
        row.set(MonitoringLogTaskParameters.CLIENT_ID, clientId);
        row.set(MonitoringLogTaskParameters.API, api);
        row.set(MonitoringLogTaskParameters.USER_ID, userId);
        row.set(MonitoringLogTaskParameters.TIME, time);

        String insertId = UUID.randomUUID().toString();

        bigQueryService.streamInsert(options.getPROJECT_ID(), options.getDATASET_ID(), options.getMONITORING_LOG_TABLE_ID(), row, insertId);
    }
}

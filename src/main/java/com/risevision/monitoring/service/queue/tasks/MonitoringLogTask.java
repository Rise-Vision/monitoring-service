package com.risevision.monitoring.service.queue.tasks;

import com.google.api.services.bigquery.model.TableRow;
import com.risevision.monitoring.service.services.oauth.GoogleOAuthClientService;
import com.risevision.monitoring.service.services.oauth.GoogleOAuthClientServiceImpl;
import com.risevision.monitoring.service.services.oauth.TokenInfo;
import com.risevision.monitoring.service.services.oauth.TokenInfoServiceImpl;
import com.risevision.monitoring.service.services.storage.bigquery.BigQueryService;
import com.risevision.monitoring.service.services.storage.bigquery.BigQueryServiceImpl;
import com.risevision.monitoring.service.util.Options;

import java.util.UUID;

/**
 * Created by rodrigopavezi on 2/4/15.
 */
public class MonitoringLogTask {

    private BigQueryService bigQueryService;
    private Options options;
    private GoogleOAuthClientService googleOAuthClientService;
    private TableRow row;

    public MonitoringLogTask() {
        bigQueryService = new BigQueryServiceImpl();
        options = Options.getInstance();
        googleOAuthClientService = new GoogleOAuthClientServiceImpl(new TokenInfoServiceImpl());
        row = new TableRow();
    }

    public MonitoringLogTask(BigQueryService bigQueryService, Options options, GoogleOAuthClientService googleOAuthClientService, TableRow row) {
        this.bigQueryService = bigQueryService;
        this.options = options;
        this.googleOAuthClientService = googleOAuthClientService;
        this.row = row;
    }

    public void execute(String ip, String host, String resource, String bearerToken, String api, String time) throws Exception {

        TokenInfo tokenInfo = googleOAuthClientService.lookupTokenInfo(bearerToken);

        if (tokenInfo != null) {
            if (tokenInfo.getIssued_to() != null && !tokenInfo.getIssued_to().isEmpty()) {

                String clientId = tokenInfo.getIssued_to();
                String userId = tokenInfo.getEmail();

                row.set(MonitoringLogTaskParameters.IP, ip);
                row.set(MonitoringLogTaskParameters.HOST, host);
                row.set(MonitoringLogTaskParameters.RESOURCE, resource);
                row.set(MonitoringLogTaskParameters.CLIENT_ID, clientId);
                row.set(MonitoringLogTaskParameters.API, api);
                row.set(MonitoringLogTaskParameters.USER_ID, userId);
                row.set(MonitoringLogTaskParameters.TIME, time);

                String insertId = UUID.randomUUID().toString();

                bigQueryService.streamInsert(options.getPROJECT_ID(), options.getDATASET_ID(), options.getMONITORING_LOG_TABLE_ID(), row, insertId);
            } else {
                throw new Exception("Client Id is null or empty");
            }
        } else {
            throw new Exception("Token info is null");
        }
    }
}

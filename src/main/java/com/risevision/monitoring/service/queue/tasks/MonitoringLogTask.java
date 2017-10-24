package com.risevision.monitoring.service.queue.tasks;

import com.google.api.services.bigquery.model.TableRow;
import com.risevision.monitoring.service.services.oauth.CustomAuthClientService;
import com.risevision.monitoring.service.services.oauth.CustomAuthClientServiceImpl;
import com.risevision.monitoring.service.services.oauth.GoogleOAuthClientService;
import com.risevision.monitoring.service.services.oauth.GoogleOAuthClientServiceImpl;
import com.risevision.monitoring.service.services.oauth.TokenInfo;
import com.risevision.monitoring.service.services.oauth.TokenInfoServiceImpl;
import com.risevision.monitoring.service.services.storage.bigquery.BigQueryService;
import com.risevision.monitoring.service.services.storage.bigquery.BigQueryServiceImpl;
import com.risevision.monitoring.service.util.Options;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by rodrigopavezi on 2/4/15.
 */
public class MonitoringLogTask {

    private final Logger logger = Logger.getLogger(MonitoringLogTask.class.getName());

    private BigQueryService bigQueryService;
    private Options options;
    private GoogleOAuthClientService googleOAuthClientService;
    private CustomAuthClientService customAuthClientService;
    private TableRow row;

    public MonitoringLogTask() {
        bigQueryService = new BigQueryServiceImpl();
        options = Options.getInstance();
        googleOAuthClientService = new GoogleOAuthClientServiceImpl(new TokenInfoServiceImpl());
        customAuthClientService = new CustomAuthClientServiceImpl();
        row = new TableRow();
    }

    public MonitoringLogTask(BigQueryService bigQueryService, Options options, GoogleOAuthClientService googleOAuthClientService, CustomAuthClientService customAuthClientService, TableRow row) {
        this.bigQueryService = bigQueryService;
        this.options = options;
        this.googleOAuthClientService = googleOAuthClientService;
        this.customAuthClientService = customAuthClientService;
        this.row = row;
    }

    public void execute(String ip, String host, String resource, String bearerToken, String api, String time) throws IOException {

        TokenInfo tokenInfo = null;
        String clientId = "N/A (token expired)";
        String userId = "N/A (token expired)";

        if (customAuthClientService.isCustomAuthToken(bearerToken)) {
          tokenInfo = customAuthClientService.lookupTokenInfo(bearerToken);
        }
        else {
          // Attempts to get the token info twice before saving a entity without the client id and user email info.
          try {
              tokenInfo = googleOAuthClientService.lookupTokenInfo(bearerToken);
          } catch (IOException e) {
              try {
                  tokenInfo = googleOAuthClientService.lookupTokenInfo(bearerToken);
              } catch (IOException e1) {
                  logger.warning("Cannot retrieve Token Info: " + e1.getMessage());
              }
          }
        }

        if (tokenInfo != null) {
            if (tokenInfo.getIssued_to() != null && !tokenInfo.getIssued_to().isEmpty()) {

                clientId = tokenInfo.getIssued_to();

            } else {
                logger.warning("Client Id is null or empty");
            }

            if (tokenInfo.getEmail() != null && !tokenInfo.getEmail().isEmpty()) {

                userId = tokenInfo.getEmail();

            } else {
                logger.warning("User Id is null or empty");
            }
        } else {
            logger.warning("Token info is null");

        }

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

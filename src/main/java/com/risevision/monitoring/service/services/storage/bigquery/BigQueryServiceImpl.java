package com.risevision.monitoring.service.services.storage.bigquery;

import com.google.api.client.googleapis.extensions.appengine.auth.oauth2.AppIdentityCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.Bigquery.Jobs.Insert;
import com.google.api.services.bigquery.BigqueryScopes;
import com.google.api.services.bigquery.model.*;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.ApiProxy.Environment;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class BigQueryServiceImpl implements BigQueryService {


    private final Logger log = Logger.getAnonymousLogger();
    private final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private final JsonFactory JSON_FACTORY = new GsonFactory();
    private final List<String> BIGQUERY_SCOPE = Arrays.asList(BigqueryScopes.BIGQUERY);
    private Bigquery bigquery;

    public BigQueryServiceImpl() {
        bigquery = getBigquery();
    }

    @Override
    public JobReference startQuery(String query, String projectId) throws IOException {

        log.info("Starting Query: " + query);

        Job job = new Job();
        JobConfiguration config = new JobConfiguration();
        JobConfigurationQuery queryConfig = new JobConfigurationQuery();
        config.setQuery(queryConfig);

        job.setConfiguration(config);

        queryConfig.setQuery(query);

        Insert insert = bigquery.jobs().insert(projectId, job);
        JobReference jobRef = insert.execute().getJobReference();

        log.info("Job ID of Query Job is::" + jobRef.getJobId());

        return jobRef;
    }

    @Override
    public List<TableList.Tables> listTables(String projectId, String datasetId)
            throws IOException {

        Bigquery.Tables.List TableRequest = bigquery.tables().list(projectId, datasetId);
        TableList tableList = TableRequest.execute();

        if (tableList.getTables() != null) {
            return tableList.getTables();
        }

        return null;
    }

    @Override
    public Job checkQueryResults(String projectId, JobReference job) throws IOException, InterruptedException {
//		 // Variables to keep track of total query time
        long startTime = System.currentTimeMillis();
        long elapsedTime;

        while (true) {
            Job pollJob = bigquery.jobs().get(projectId, job.getJobId()).execute();
            elapsedTime = System.currentTimeMillis() - startTime;
            log.log(Level.INFO, "Job status {1} {2}: {3}", new Object[]{elapsedTime, job.getJobId(), pollJob.getStatus().getState()});
            if (pollJob.getStatus().getState().equals("DONE")) {
                return pollJob;
            }
            // Pause execution for one second before polling job status again, to
            // reduce unnecessary calls to the BigQUery API and lower overall
            // application bandwidth.
            Thread.sleep(1000);
        }
    }

    @Override
    public List<TableRow> getQueryResults(String projectId, Job completedJob) throws IOException {
        GetQueryResultsResponse queryResult = bigquery.jobs()
                .getQueryResults(
                        projectId, completedJob
                                .getJobReference()
                                .getJobId()
                ).execute();
        return queryResult.getRows();
    }

    private Bigquery getBigquery() {
        if (bigquery == null) {
            Environment env = ApiProxy.getCurrentEnvironment();
            String appId = env.getAppId();
            AppIdentityCredential credential = new AppIdentityCredential(BIGQUERY_SCOPE);

            bigquery = new Bigquery.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(appId).build();
        }

        return bigquery;
    }
}

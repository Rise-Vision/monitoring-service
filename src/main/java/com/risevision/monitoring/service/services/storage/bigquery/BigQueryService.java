package com.risevision.monitoring.service.services.storage.bigquery;

import com.google.api.services.bigquery.model.Job;
import com.google.api.services.bigquery.model.JobReference;
import com.google.api.services.bigquery.model.TableList;
import com.google.api.services.bigquery.model.TableRow;

import java.io.IOException;
import java.util.List;

/**
 * Created by rodrigopavezi on 1/26/15.
 */
public interface BigQueryService {

    public JobReference startQuery(String query, String projectId) throws IOException;

    public List<TableList.Tables> listTables(String projectId, String datasetId) throws IOException;

    public Job checkQueryResults(String projectId, JobReference job) throws IOException, InterruptedException;

    public List<TableRow> getQueryResults(String projectId, Job completedJob) throws IOException;
}

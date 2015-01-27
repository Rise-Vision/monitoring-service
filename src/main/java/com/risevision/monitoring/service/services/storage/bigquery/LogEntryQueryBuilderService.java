package com.risevision.monitoring.service.services.storage.bigquery;

import com.google.api.services.bigquery.model.TableList;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by rodrigopavezi on 1/27/15.
 */
public class LogEntryQueryBuilderService implements QueryBuilderService {


    private final Logger logger = Logger.getLogger(LogEntryQueryBuilderService.class.getName());

    private BigQueryService bigQueryService;
    private String projectId;
    private String datasetId;

    public LogEntryQueryBuilderService(BigQueryService bigQueryService, String projectId, String datasetId) {
        this.bigQueryService = bigQueryService;
        this.projectId = projectId;
        this.datasetId = datasetId;
    }

    @Override
    public String buildQuery(String conditional, String orderBy) {
        String query = null;

        try {
            List<TableList.Tables> tables = bigQueryService.listTables(projectId, datasetId);

            if (tables != null && tables.size() > 0) {
                String tableIdsForTheQuery = "";
                for (TableList.Tables table : tables) {
                    tableIdsForTheQuery += "[" + table.getTableReference().getTableId() + "],";
                }

                query = "SELECT " +
                        "protoPayload.ip, " +
                        "protoPayload.host, " +
                        "protoPayload.resource, " +
                        "protoPayload.line.logMessage, " +
                        "protoPayload.line.time FROM " +
                        tableIdsForTheQuery +
                        " WHERE " +
                        conditional +
                        " ORDER BY " +
                        orderBy;
            }
        } catch (IOException exception) {
            logger.warning("Could not get tables for dataset: " + datasetId + "\n Cause: " + exception.getMessage());
        }

        return query;
    }
}

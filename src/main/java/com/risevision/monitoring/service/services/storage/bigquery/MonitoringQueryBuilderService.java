package com.risevision.monitoring.service.services.storage.bigquery;

/**
 * Created by rodrigopavezi on 2/4/15.
 */
public class MonitoringQueryBuilderService implements QueryBuilderService {

    @Override
    public String buildQuery(String conditional, String orderBy) {

        String query = "SELECT " +
                "ip, " +
                "host, " +
                "resource, " +
                "clientId, " +
                "api, " +
                "userId, " +
                "time " +
                "FROM " +
                "monitoring_logs " +
                "WHERE " +
                conditional +
                " ORDER BY " +
                orderBy;

        return query;
    }
}

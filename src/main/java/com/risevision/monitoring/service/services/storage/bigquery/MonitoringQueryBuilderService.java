package com.risevision.monitoring.service.services.storage.bigquery;

import com.risevision.monitoring.service.util.Options;

/**
 * Created by rodrigopavezi on 2/4/15.
 */
public class MonitoringQueryBuilderService implements QueryBuilderService {

    private Options options;

    public MonitoringQueryBuilderService(Options options) {
        this.options = options;
    }

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
                "[" + options.getDATASET_ID() + "." + options.getMONITORING_LOG_TABLE_ID() + "] " +
                "WHERE " +
                conditional +
                " ORDER BY " +
                orderBy;

        return query;
    }
}

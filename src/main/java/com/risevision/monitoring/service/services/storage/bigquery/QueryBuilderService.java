package com.risevision.monitoring.service.services.storage.bigquery;

/**
 * Created by rodrigopavezi on 1/27/15.
 */
public interface QueryBuilderService {

    public String buildQuery(String conditional, String orderBy);

}

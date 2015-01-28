package com.risevision.monitoring.service.util;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created by rodrigopavezi on 1/28/15.
 */
public class Options {

    private static Options instance;
    private final String PROJECT_ID;
    private final String DATASET_ID;

    public Options() {
        Config conf = ConfigFactory.load();
        this.PROJECT_ID = conf.getString("monitoring-service.bigquery.productId");
        this.DATASET_ID = conf.getString("monitoring-service.bigquery.datasetId");
    }

    public static Options getInstance() {
        try {
            if (instance == null) {
                instance = new Options();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    public String getPROJECT_ID() {
        return PROJECT_ID;
    }

    public String getDATASET_ID() {
        return DATASET_ID;
    }
}

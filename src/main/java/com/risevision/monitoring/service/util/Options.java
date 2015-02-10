package com.risevision.monitoring.service.util;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created by rodrigopavezi on 1/28/15.
 */
public class Options {

    private static Options instance;
    public final String STORAGE_SCOPE =
            "https://www.googleapis.com/auth/devstorage.full_control";
    public final String EMAIL_SCOPE =
            "https://www.googleapis.com/auth/userinfo.email";
    public final String BQ_SCOPE =
            "https://www.googleapis.com/auth/bigquery";
    private final String PROJECT_ID;
    private final String DATASET_ID;
    private final String MONITORING_LOG_TABLE_ID;
    private final String USER_VERIFICATION_URL;
    private final String APP_LOOKUP_URL;
    private final String RVCORE_ID;
    private final String RVCORE_P12_PATH;

    public Options() {
        Config conf = ConfigFactory.load();
        this.PROJECT_ID = conf.getString("monitoring-service.bigquery.productId");
        this.DATASET_ID = conf.getString("monitoring-service.bigquery.datasetId");
        this.MONITORING_LOG_TABLE_ID = conf.getString("monitoring-service.bigquery.monitoringLogTableId");
        this.USER_VERIFICATION_URL = conf.getString("monitoring-service.gcs.userVerificationURL");
        this.APP_LOOKUP_URL = conf.getString("monitoring-service.gcs.appLookupURL");
        this.RVCORE_ID = conf.getString("monitoring-service.gcs.rvcoreId");
        this.RVCORE_P12_PATH = conf.getString("monitoring-service.gcs.rvcoreP12Path");
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

    public String getMONITORING_LOG_TABLE_ID() {
        return MONITORING_LOG_TABLE_ID;
    }

    public String getUSER_VERIFICATION_URL() {
        return USER_VERIFICATION_URL;
    }

    public String getAPP_LOOKUP_URL() {
        return APP_LOOKUP_URL;
    }

    public String getRVCORE_ID() {
        return RVCORE_ID;
    }

    public String getRVCORE_P12_PATH() {
        return RVCORE_P12_PATH;
    }

    public String getSTORAGE_SCOPE() {
        return STORAGE_SCOPE;
    }

    public String getEMAIL_SCOPE() {
        return EMAIL_SCOPE;
    }

    public String getBQ_SCOPE() {
        return BQ_SCOPE;
    }
}

package com.risevision.monitoring.service.services.gcs;

import com.google.api.client.util.Key;

import java.util.Date;

/**
 * Created by rodrigopavezi on 2/9/15.
 */
public class ThirdPartyApp {

    @Key
    private String id;
    @Key
    private String companyId;
    @Key
    private String name;
    @Key
    private String clientId;
    @Key
    private String url;
    @Key
    private boolean isInternal;
    @Key
    private Date creationDate;
    @Key
    private String changedBy;
    @Key
    private Date changeDate;

    public String getId() {
        return id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getName() {
        return name;
    }

    public String getClientId() {
        return clientId;
    }

    public String getUrl() {
        return url;
    }

    public boolean isInternal() {
        return isInternal;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public Date getChangeDate() {
        return changeDate;
    }
}

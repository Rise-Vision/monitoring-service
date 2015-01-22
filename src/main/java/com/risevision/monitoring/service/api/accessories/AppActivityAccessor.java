package com.risevision.monitoring.service.api.accessories;

import com.google.api.server.spi.ServiceException;
import com.risevision.monitoring.service.api.resources.AppActivity;
import com.risevision.monitoring.service.services.analytics.AppActivityService;
import com.risevision.monitoring.service.services.analytics.AppActivityServiceImpl;

import javax.xml.bind.ValidationException;

/**
 * Created by rodrigopavezi on 1/20/15.
 */
public class AppActivityAccessor {

    private AppActivityService appActivityService;

    public AppActivityAccessor(){
        this.appActivityService = new AppActivityServiceImpl();
    }

    public AppActivityAccessor(AppActivityService appActivityService){
        this.appActivityService = appActivityService;
    }

    public AppActivity get(String clientId, String api) throws ServiceException, ValidationException {

        if(clientId == null || clientId.isEmpty()){
            throw new ValidationException("Client Id cannot be null or empty.");
        }

        if(api == null || api.isEmpty()){
            throw new ValidationException("Api cannot be null or empty.");
        }

        return appActivityService.getActivity(clientId, api);
    }
}

package com.risevision.monitoring.service.services.date;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by rodrigopavezi on 1/23/15.
 */
public class DateServiceImpl implements DateService {
    @Override
    public Date getDaysAgoDate(int numberOfDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -numberOfDays);
        return calendar.getTime();
    }
}

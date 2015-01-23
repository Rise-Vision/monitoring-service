package com.risevision.monitoring.service.services.analytics;

import com.risevision.monitoring.service.api.resources.AppActivity;
import com.risevision.monitoring.service.services.storage.bigquery.BigQueryService;
import com.risevision.monitoring.service.services.storage.bigquery.entities.LogEntry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.xml.bind.ValidationException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by rodrigopavezi on 1/22/15.
 */
public class AppActivityServiceTest {

    @Mock
    private BigQueryService bigQueryServiceMock;

    @Mock
    private LogEntry logEntryMock;

    private List<LogEntry> logEntryList;
    private String clientId;
    private String api;
    private int numberOfDays;
    private List<LogEntry> mockedLogEntries;
    private Date firstCall;
    private Date lastCall;
    private boolean verifyCalls;

    private AppActivityService appActivityService;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        appActivityService = new AppActivityServiceImpl(bigQueryServiceMock);
        logEntryList = new LinkedList<>();
        clientId = "xxxxxxxx";
        api = "CoreAPIv1";
        numberOfDays = 7;
        verifyCalls = true;



        given(bigQueryServiceMock.getLogEntriesFromTheLastNumberOfDays(clientId, api, numberOfDays)).willReturn(logEntryList);

        Calendar calendar = Calendar.getInstance();
        firstCall = getLogEntryTime(calendar, 100);
        lastCall = getLogEntryTime(calendar, 0);

        List<Date> times = new LinkedList<>();
        mockedLogEntries = new LinkedList<>();
        for(int i = 0; i <= 100; i++){
            LogEntry logEntry = mock(LogEntry.class);
            Date time = getLogEntryTime(calendar,i);

            times.add(time);
            mockedLogEntries.add(logEntry);

            logEntryList.add(logEntry);
            given(logEntry.getTime()).willReturn(time);
        }
    }

    private Date getLogEntryTime(Calendar calendar, int difference){
        Calendar calendarPassedDate = Calendar.getInstance();
        calendarPassedDate.setTime(calendar.getTime());
        calendarPassedDate.add(Calendar.DAY_OF_MONTH, -(difference));
        return calendarPassedDate.getTime();
    }

    @After
    public void teardown(){
        if(verifyCalls) {
            verify(bigQueryServiceMock).getLogEntriesFromTheLastNumberOfDays(clientId, api, numberOfDays);


            // verify if getTime was called for each log entry retrieved from BQ.
            for (int i = 0; i < logEntryList.size(); i++) {
                verify(mockedLogEntries.get(i)).getTime();
            }
        }
    }

    @Test
    public void testGetAppActivityForAClientIdAndApp() throws ValidationException {

        AppActivity appActivity = appActivityService.getActivityFromTheLastNumberOfDays(clientId, api, numberOfDays);

        assertThat("AppActivity is not null", appActivity, is(notNullValue()));
        assertThat("Client Id is equal the client id passed to the query", appActivity.getClientId(), is(clientId));
        assertThat("Api is equal the api passed to the query",appActivity.getApi(), is(api));
    }

    @Test
    public void testAppActivityFirstCallAndLastCall() throws ValidationException {
        AppActivity appActivity = appActivityService.getActivityFromTheLastNumberOfDays(clientId, api, numberOfDays);

        assertThat("Fist Call is the earliest call", appActivity.getFirstCall(), is(firstCall));
        assertThat("Last Call is the latest call", appActivity.getLastCall(), is(lastCall));
    }


    @Test
    public void testAppActivityFirstCallAndLastCallIsNullIfThereIsNoLogEntries() throws ValidationException {

        logEntryList =  new LinkedList<>();
        given(bigQueryServiceMock.getLogEntriesFromTheLastNumberOfDays(clientId, api, numberOfDays)).willReturn(logEntryList);

        AppActivity appActivity = appActivityService.getActivityFromTheLastNumberOfDays(clientId, api, numberOfDays);

        assertThat("Fist Call is null", appActivity.getFirstCall(), is(nullValue()));
        assertThat("Last Call is null", appActivity.getLastCall(), is(nullValue()));
    }


    @Test
    public void testAppActivityAvgCallsPerDayForTheLastSevenDays() throws ValidationException {
        float expectedAverageCallsPerDay = 1f;
        logEntryList =  logEntryList.subList(0,numberOfDays);
        given(bigQueryServiceMock.getLogEntriesFromTheLastNumberOfDays(clientId, api, numberOfDays)).willReturn(logEntryList);


        AppActivity appActivity = appActivityService.getActivityFromTheLastNumberOfDays(clientId, api, numberOfDays);

        assertThat("AppActivity contains the right Average Calls Per Day for Last 7 days", appActivity.getAvgCallsPerDay(), is(expectedAverageCallsPerDay));
    }

    @Test
    public void testAppActivityAvgCallsPerDayForTheLastSevenDaysIsNullWhenThereIsNORequestInThePeriod() throws ValidationException {
        float expectedAverageCallsPerDay = 0;
        logEntryList =  new LinkedList<>();
        given(bigQueryServiceMock.getLogEntriesFromTheLastNumberOfDays(clientId, api, numberOfDays)).willReturn(logEntryList);


        AppActivity appActivity = appActivityService.getActivityFromTheLastNumberOfDays(clientId, api, numberOfDays);

        assertThat("AppActivity contains the zero Average Calls Per Day for Last 7 days", appActivity.getAvgCallsPerDay(), is(expectedAverageCallsPerDay));
    }

    @Test(expected = ValidationException.class)
    public void testValidationExceptionIsThrownIfNumberOfDaysIsLessAndEqualZero() throws ValidationException {
        numberOfDays = 0;
        verifyCalls = false;
        AppActivity appActivity = appActivityService.getActivityFromTheLastNumberOfDays(clientId, api, numberOfDays);

    }

}

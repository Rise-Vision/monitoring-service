package com.risevision.monitoring.service.services.analytics;

import com.google.appengine.api.users.User;
import com.risevision.monitoring.service.services.date.DateService;
import com.risevision.monitoring.service.services.storage.bigquery.LogEntryService;
import com.risevision.monitoring.service.services.storage.bigquery.entities.LogEntry;
import com.risevision.monitoring.service.services.storage.datastore.DatastoreService;
import com.risevision.monitoring.service.services.storage.datastore.entities.AppActivityEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Created by rodrigopavezi on 1/22/15.
 */
public class AppActivityServiceTest {

    @Mock
    private LogEntryService logEntryServiceMock;
    @Mock
    private DatastoreService datastoreServiceMock;
    @Mock
    private DateService dateService;

    private User user;

    @Mock
    private LogEntry logEntryMock;
    @Spy
    private AppActivityEntity appActivityEntitySpy;

    private String clientId;
    private String api;
    private int numberOfDays;

    private AppActivityService appActivityService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        appActivityService = new AppActivityServiceImpl(logEntryServiceMock, datastoreServiceMock, dateService);
        clientId = "xxxxxxxx";
        api = "CoreAPIv1";
        numberOfDays = 7;
        user = new User("example@gmail.com", "authDomain");
    }

    private Date getLogEntryTime(Calendar calendar, int difference) {
        Calendar calendarPassedDate = Calendar.getInstance();
        calendarPassedDate.setTime(calendar.getTime());
        calendarPassedDate.add(Calendar.DAY_OF_MONTH, -(difference));
        return calendarPassedDate.getTime();
    }

    private List<LogEntry> getMockedLogEntries(Calendar calendar, int daysAgo) {
        List<LogEntry> logEntryList = new LinkedList<>();

        for (int i = daysAgo - 1; i >= 0; i--) {
            LogEntry logEntry = mock(LogEntry.class);
            Date time = getLogEntryTime(calendar, i);

            logEntryList.add(logEntry);
            given(logEntry.getTime()).willReturn(time);
        }

        return logEntryList;
    }

    @Test
    public void testGetAppActivityFromDatastoreWithNoLogEntriesAfterLastCallAndNotFromTheLastSevenDays() throws ValidationException, IOException, InterruptedException {
        float lastAvgCall = 2.5f;
        float expectedAvgCall = 0.0f;

        appActivityEntitySpy.setClientId(clientId);
        appActivityEntitySpy.setApi(api);
        appActivityEntitySpy.setAvgCallsPerDay(lastAvgCall);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -numberOfDays);
        given(dateService.getDaysAgoDate(numberOfDays)).willReturn(calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, -10);
        appActivityEntitySpy.setLastCall(calendar.getTime());

        given(datastoreServiceMock.get(notNull(AppActivityEntity.class))).willReturn(appActivityEntitySpy);
        given(logEntryServiceMock.getLogEntriesAfterDateOrderedByDate(clientId, api, calendar.getTime())).willReturn(null);

        AppActivityEntity actualAppActivityEntity = appActivityService.getActivity(clientId, api, user);

        verify(datastoreServiceMock).get(notNull(AppActivityEntity.class));
        verify(logEntryServiceMock).getLogEntriesAfterDateOrderedByDate(clientId, api, calendar.getTime());
        verify(datastoreServiceMock).put(actualAppActivityEntity);
        verify(logEntryServiceMock, never()).getLogEntriesOrderedByDate(clientId, api);

        assertThat("AppActivityEntity is not null", actualAppActivityEntity, is(notNullValue()));
        assertThat("Client Id is equal the client id passed to the query", actualAppActivityEntity.getClientId(), is(clientId));
        assertThat("Api is equal the api passed to the query", actualAppActivityEntity.getApi(), is(api));
        assertThat("Average calls per day for the last 7 days is Zero", actualAppActivityEntity.getAvgCallsPerDay(), is(expectedAvgCall));
    }

    @Test
    public void testGetAppActivityFromDatastoreWithLogEntriesAfterLastCallButNotFromTheLastSevenDays() throws ValidationException, IOException, InterruptedException {
        float lastAvgCall = 2.5f;
        float expectedAvgCall = 0.0f;

        appActivityEntitySpy.setClientId(clientId);
        appActivityEntitySpy.setApi(api);
        appActivityEntitySpy.setAvgCallsPerDay(lastAvgCall);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -numberOfDays);
        given(dateService.getDaysAgoDate(numberOfDays)).willReturn(calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, -10);
        appActivityEntitySpy.setLastCall(calendar.getTime());

        List<LogEntry> logEntries = getMockedLogEntries(calendar, 20);

        given(datastoreServiceMock.get(notNull(AppActivityEntity.class))).willReturn(appActivityEntitySpy);
        given(logEntryServiceMock.getLogEntriesAfterDateOrderedByDate(clientId, api, appActivityEntitySpy.getLastCall())).willReturn(logEntries);

        AppActivityEntity actualAppActivityEntity = appActivityService.getActivity(clientId, api, user);

        verify(datastoreServiceMock).get(notNull(AppActivityEntity.class));
        verify(logEntryServiceMock).getLogEntriesAfterDateOrderedByDate(clientId, api, calendar.getTime());
        verify(datastoreServiceMock).put(actualAppActivityEntity);
        verify(logEntryServiceMock, never()).getLogEntriesOrderedByDate(clientId, api);

        assertThat("Average calls per day for the last 7 days is Zero", actualAppActivityEntity.getAvgCallsPerDay(), is(expectedAvgCall));
    }

    @Test
    public void testGetAppActivityFromDatastoreWithLogEntriesAfterLastCallAndFromTheLastSevenDays() throws ValidationException, IOException, InterruptedException {
        float lastAvgCall = 2.5f;
        float expectedAvgCall = 1.0f;

        appActivityEntitySpy.setClientId(clientId);
        appActivityEntitySpy.setApi(api);
        appActivityEntitySpy.setAvgCallsPerDay(lastAvgCall);

        Calendar calendar = Calendar.getInstance();
        List<LogEntry> logEntries = getMockedLogEntries(calendar, 50);

        calendar.add(Calendar.DAY_OF_MONTH, -numberOfDays);
        given(dateService.getDaysAgoDate(numberOfDays)).willReturn(calendar.getTime());

        calendar.add(Calendar.DAY_OF_MONTH, -10);
        appActivityEntitySpy.setLastCall(calendar.getTime());

        given(datastoreServiceMock.get(notNull(AppActivityEntity.class))).willReturn(appActivityEntitySpy);
        given(logEntryServiceMock.getLogEntriesAfterDateOrderedByDate(clientId, api, calendar.getTime())).willReturn(logEntries);

        AppActivityEntity actualAppActivityEntity = appActivityService.getActivity(clientId, api, user);

        verify(datastoreServiceMock).get(notNull(AppActivityEntity.class));
        verify(logEntryServiceMock).getLogEntriesAfterDateOrderedByDate(clientId, api, calendar.getTime());
        verify(datastoreServiceMock).put(actualAppActivityEntity);
        verify(logEntryServiceMock, never()).getLogEntriesOrderedByDate(clientId, api);

        assertThat("Average calls per day for the last 7 days is Zero", actualAppActivityEntity.getAvgCallsPerDay(), is(expectedAvgCall));
    }


    @Test
    public void testGetAppActivityFromDatastoreWithLogEntriesAfterLastCallAndFromTheLastSevenDaysCurrentLastCallIsAfterDaysAgoDate() throws ValidationException, IOException, InterruptedException {
        float lastAvgCall = 2.5f;
        float expectedAvgCall = 1.0f;

        appActivityEntitySpy.setClientId(clientId);
        appActivityEntitySpy.setApi(api);
        appActivityEntitySpy.setAvgCallsPerDay(lastAvgCall);

        Calendar calendar = Calendar.getInstance();
        List<LogEntry> logEntries = getMockedLogEntries(calendar, 7);

        calendar.add(Calendar.DAY_OF_MONTH, -numberOfDays);
        Date daysAgoDate = calendar.getTime();
        given(dateService.getDaysAgoDate(numberOfDays)).willReturn(daysAgoDate);

        calendar.add(Calendar.DAY_OF_MONTH, +3);
        appActivityEntitySpy.setLastCall(calendar.getTime());

        given(datastoreServiceMock.get(notNull(AppActivityEntity.class))).willReturn(appActivityEntitySpy);
        given(logEntryServiceMock.getLogEntriesAfterDateOrderedByDate(clientId, api, daysAgoDate)).willReturn(logEntries);

        AppActivityEntity actualAppActivityEntity = appActivityService.getActivity(clientId, api, user);

        verify(datastoreServiceMock).get(notNull(AppActivityEntity.class));
        verify(logEntryServiceMock).getLogEntriesAfterDateOrderedByDate(clientId, api, daysAgoDate);
        verify(datastoreServiceMock).put(actualAppActivityEntity);
        verify(logEntryServiceMock, never()).getLogEntriesOrderedByDate(clientId, api);

        assertThat("Average calls per day for the last 7 days is Zero", actualAppActivityEntity.getAvgCallsPerDay(), is(expectedAvgCall));
    }


    @Test
    public void testGetAppActivityFromBigQueryWithLastSevenDaysLogEntriesAsThereIsNoRecordOnDatastore() throws ValidationException, IOException, InterruptedException {
        float expectedAvgCall = 1.0f;

        Calendar calendar = Calendar.getInstance();
        List<LogEntry> logEntries = getMockedLogEntries(calendar, 100);

        calendar.add(Calendar.DAY_OF_MONTH, -numberOfDays);
        Date daysAgoDate = calendar.getTime();
        given(dateService.getDaysAgoDate(numberOfDays)).willReturn(daysAgoDate);

        given(datastoreServiceMock.get(notNull(AppActivityEntity.class))).willReturn(null);
        given(logEntryServiceMock.getLogEntriesOrderedByDate(clientId, api)).willReturn(logEntries);

        AppActivityEntity actualAppActivityEntity = appActivityService.getActivity(clientId, api, user);

        verify(datastoreServiceMock).get(notNull(AppActivityEntity.class));
        verify(logEntryServiceMock).getLogEntriesOrderedByDate(clientId, api);
        verify(datastoreServiceMock).put(actualAppActivityEntity);
        verify(logEntryServiceMock, never()).getLogEntriesAfterDateOrderedByDate(eq(clientId), eq(api), notNull(Date.class));

        assertThat("Average calls per day for the last 7 days is Zero", actualAppActivityEntity.getFirstCall(), is(logEntries.get(0).getTime()));
        assertThat("Average calls per day for the last 7 days is Zero", actualAppActivityEntity.getAvgCallsPerDay(), is(expectedAvgCall));
    }

    @Test
    public void testGetAppActivityFromBigQueryWithNoLastSevenDaysLogEntriesAsThereIsNoRecordOnDatastore() throws ValidationException, IOException, InterruptedException {
        float expectedAvgCall = 0.0f;

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_MONTH, -numberOfDays);
        Date daysAgoDate = calendar.getTime();
        given(dateService.getDaysAgoDate(numberOfDays)).willReturn(daysAgoDate);
        List<LogEntry> logEntries = getMockedLogEntries(calendar, 100);

        given(datastoreServiceMock.get(notNull(AppActivityEntity.class))).willReturn(null);
        given(logEntryServiceMock.getLogEntriesOrderedByDate(clientId, api)).willReturn(logEntries);

        AppActivityEntity actualAppActivityEntity = appActivityService.getActivity(clientId, api, user);

        verify(datastoreServiceMock).get(notNull(AppActivityEntity.class));
        verify(logEntryServiceMock).getLogEntriesOrderedByDate(clientId, api);
        verify(datastoreServiceMock).put(actualAppActivityEntity);
        verify(logEntryServiceMock, never()).getLogEntriesAfterDateOrderedByDate(eq(clientId), eq(api), notNull(Date.class));

        assertThat("Average calls per day for the last 7 days is Zero", actualAppActivityEntity.getFirstCall(), is(logEntries.get(0).getTime()));
        assertThat("Average calls per day for the last 7 days is Zero", actualAppActivityEntity.getAvgCallsPerDay(), is(expectedAvgCall));
    }

    @Test
    public void testGetAppActivityWithNoLogsOnDatastoreAndBigQuery() throws ValidationException, IOException, InterruptedException {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -numberOfDays);
        Date daysAgoDate = calendar.getTime();
        given(dateService.getDaysAgoDate(numberOfDays)).willReturn(daysAgoDate);

        given(datastoreServiceMock.get(notNull(AppActivityEntity.class))).willReturn(null);
        given(logEntryServiceMock.getLogEntriesOrderedByDate(clientId, api)).willReturn(null);

        AppActivityEntity actualAppActivityEntity = appActivityService.getActivity(clientId, api, user);

        verify(datastoreServiceMock).get(notNull(AppActivityEntity.class));
        verify(logEntryServiceMock).getLogEntriesOrderedByDate(clientId, api);
        verify(datastoreServiceMock, never()).put(actualAppActivityEntity);
        verify(logEntryServiceMock, never()).getLogEntriesAfterDateOrderedByDate(eq(clientId), eq(api), notNull(Date.class));

        assertThat("AppActivityEntity is not null", actualAppActivityEntity, is(nullValue()));

    }
}

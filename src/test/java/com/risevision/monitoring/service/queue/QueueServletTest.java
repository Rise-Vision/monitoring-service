package com.risevision.monitoring.service.queue;

import com.risevision.monitoring.service.queue.tasks.MonitoringLogTask;
import com.risevision.monitoring.service.queue.tasks.MonitoringLogTaskParameters;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

/**
 * Created by rodrigopavezi on 2/5/15.
 */
public class QueueServletTest {

    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private MonitoringLogTask monitoringLogTask;

    private QueueServlet queueServlet;

    private String ip;
    private String host;
    private String resource;
    private String bearerToken;
    private String api;
    private String time;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        queueServlet = new QueueServlet(monitoringLogTask);

        ip = "1.1.1.1";
        host = "test.com";
        resource = "/_ah/spi/test";
        bearerToken = "xxxxxxx";
        api = "CoreAPIv1";
        time = String.valueOf(System.currentTimeMillis() / 1000L);

        given(httpServletRequest.getParameter(QueueParameters.TASK)).willReturn(QueueParameters.MONITORING_LOG_TASK);
        given(httpServletRequest.getParameter(MonitoringLogTaskParameters.IP)).willReturn(ip);
        given(httpServletRequest.getParameter(MonitoringLogTaskParameters.HOST)).willReturn(host);
        given(httpServletRequest.getParameter(MonitoringLogTaskParameters.RESOURCE)).willReturn(resource);
        given(httpServletRequest.getParameter(MonitoringLogTaskParameters.BEARER_TOKEN)).willReturn(bearerToken);
        given(httpServletRequest.getParameter(MonitoringLogTaskParameters.API)).willReturn(api);
        given(httpServletRequest.getParameter(MonitoringLogTaskParameters.TIME)).willReturn(time);
    }

    @Test
    public void testMonitoringLogTask() throws Exception {


        queueServlet.doGet(httpServletRequest, httpServletResponse);

        verify(httpServletRequest).getParameter(QueueParameters.TASK);
        verify(httpServletRequest).getParameter(MonitoringLogTaskParameters.IP);
        verify(httpServletRequest).getParameter(MonitoringLogTaskParameters.HOST);
        verify(httpServletRequest).getParameter(MonitoringLogTaskParameters.RESOURCE);
        verify(httpServletRequest).getParameter(MonitoringLogTaskParameters.BEARER_TOKEN);
        verify(httpServletRequest).getParameter(MonitoringLogTaskParameters.API);
        verify(httpServletRequest).getParameter(MonitoringLogTaskParameters.TIME);

        verify(monitoringLogTask).execute(ip, host, resource, bearerToken, api, time);

    }

    @Test
    public void testResponseWithInternalServerErrorWhenExceptionHappens() throws Exception {

        willThrow(new IOException()).given(monitoringLogTask).execute(ip, host, resource, bearerToken, api, time);

        queueServlet.doGet(httpServletRequest, httpServletResponse);

        verify(httpServletResponse).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

}

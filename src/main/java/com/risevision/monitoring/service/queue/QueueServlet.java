package com.risevision.monitoring.service.queue;

import com.risevision.monitoring.service.queue.tasks.MonitoringLogTask;
import com.risevision.monitoring.service.queue.tasks.MonitoringLogTaskParameters;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

/**
 * Created by rodrigopavezi on 2/4/15.
 */
public class QueueServlet extends HttpServlet {

    private final Logger logger = Logger.getLogger(QueueServlet.class.getName());

    private MonitoringLogTask monitoringLogTask;

    public QueueServlet() {
        monitoringLogTask = new MonitoringLogTask();
    }

    public QueueServlet(MonitoringLogTask monitoringLogTask) {
        this.monitoringLogTask = monitoringLogTask;
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {


        String task = req.getParameter(QueueParameters.TASK);

        if (task == null || task.isEmpty()) {
            logger.severe("Task is not supplied, exiting.");
            return;
        }

        logger.info("task: " + task);

        try {

            if (task.equals(QueueParameters.MONITORING_LOG_TASK)) {

                String ip = req.getParameter(MonitoringLogTaskParameters.IP);
                String host = req.getParameter(MonitoringLogTaskParameters.HOST);
                String resource = req.getParameter(MonitoringLogTaskParameters.RESOURCE);
                String bearerToken = req.getParameter(MonitoringLogTaskParameters.BEARER_TOKEN);
                String api = req.getParameter(MonitoringLogTaskParameters.API);
                String time = req.getParameter(MonitoringLogTaskParameters.TIME);

                monitoringLogTask.execute(ip, host, resource, bearerToken, api, time);

            }
        } catch (Exception e) {
            logger.severe("Error: " + e.toString());

            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.warning(sw.toString());

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

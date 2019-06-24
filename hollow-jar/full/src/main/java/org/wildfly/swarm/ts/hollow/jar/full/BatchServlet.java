package org.wildfly.swarm.ts.hollow.jar.full;

import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

@WebServlet("/batch")
public class BatchServlet extends HttpServlet {
    @Inject
    private Result result;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String operation = req.getParameter("operation");

        if (operation == null || "".equals(operation)) {
            return;
        }

        if (operation.equals("start")) {
            result.clear();

            JobOperator jobOperator = BatchRuntime.getJobOperator();
            Properties jobProperties = new Properties();
            jobProperties.setProperty("chunk.start", "0");
            jobProperties.setProperty("chunk.end", "10");

            long id = jobOperator.start("simple", jobProperties);

            resp.getWriter().print(id);
        } else if (operation.equals("results")) {
            result.getItems().forEach(resp.getWriter()::println);
        }
    }
}

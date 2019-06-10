package org.wildfly.swarm.ts.microprofile.metrics.v20;

import javax.inject.Inject;
import javax.json.Json;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/summary")
public class MetricsSummaryServlet extends HttpServlet {
    @Inject
    private MetricsSummary metricsSummary;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Content-Type", "application/json");

        String of = req.getParameter("of");
        switch (of) {
            case "all-registries":
                Json.createWriter(resp.getWriter()).write(metricsSummary.summarizeAllRegistries());
                break;
            case "app-registry":
                Json.createWriter(resp.getWriter()).write(metricsSummary.summarizeAppRegistry());
                break;
        }
    }
}

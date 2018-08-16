package org.wildfly.swarm.ts.microprofile.metrics.v11;

import javax.inject.Inject;
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
        String of = req.getParameter("of");
        switch (of) {
            case "all-registries":
                resp.getWriter().print(metricsSummary.summarizeAllRegistries().toString());
                break;
            case "app-registry":
                resp.getWriter().print(metricsSummary.summarizeAppRegistry().toString());
                break;
        }
    }
}

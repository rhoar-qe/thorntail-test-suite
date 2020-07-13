package org.wildfly.swarm.ts.microprofile.fault.tolerance.v21.timeout;

import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.wildfly.swarm.ts.microprofile.fault.tolerance.v21.MyConnection;

@WebServlet("/timeout")
public class TimeoutServlet extends HttpServlet {
    @Inject
    private TimeoutService service;

    @Inject
    private TimeoutContext context;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        boolean fail = Boolean.parseBoolean(req.getParameter("fail"));
        String operation = req.getParameter("operation");
        String context = req.getParameter("context");
        this.context.setValue(context);
        try {
            if ("async".equals(operation)) {
                CompletionStage<MyConnection> completionStage = service.timeoutAsync(fail);
                resp.getWriter().print(completionStage != null ?
                                               completionStage.toCompletableFuture().get().getData() :
                                               "completion stage is null");
            } else {
                resp.getWriter().print(service.timeout(fail));
            }

        } catch (Throwable t) {
            throw new ServletException("in TimeoutServlet.doGet Throwable: " + t);
        }
    }
}

package org.wildfly.swarm.ts.microprofile.fault.tolerance.v21.retry;

import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.wildfly.swarm.ts.microprofile.fault.tolerance.v21.MyConnection;

@WebServlet("/async")
public class RetryServlet extends HttpServlet {
    @Inject
    private RetryService service;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        String operation = req.getParameter("operation");

        CompletionStage<MyConnection> completionStage = null;
        try {
            switch (operation) {
                case "throwable-retryont":
                    completionStage = service.throwable_retryOnT();
                    break;
                case "myexception-retryont":
                    completionStage = service.myException_retryOnT();
                    break;
                case "throwable-abortont":
                    completionStage = service.throwable_abortOnT();
                    break;
                case "myexception-abortont":
                    completionStage = service.myException_abortOnT();
                    break;
                case "throwable-retryonmye-abortont":
                    completionStage = service.throwable_retryOnMyE_abortOnT();
                    break;
                case "throwable-retryont-abortonmye":
                    completionStage = service.throwable_retryOnT_abortOnMyE();
                    break;
                case "myexception-retryonmye-abortont":
                    completionStage = service.myException_retryOnMyE_abortOnT();
                    break;
                case "myexception-retryont-abortonmye":
                    completionStage = service.myException_retryOnT_abortOnMyE();
                    break;
            }
            resp.getWriter().print(completionStage != null ?
                                           completionStage.toCompletableFuture().get().getData() :
                                           "completion stage is null");
        } catch (Throwable t) {
            throw new ServletException(t);
        }
    }
}

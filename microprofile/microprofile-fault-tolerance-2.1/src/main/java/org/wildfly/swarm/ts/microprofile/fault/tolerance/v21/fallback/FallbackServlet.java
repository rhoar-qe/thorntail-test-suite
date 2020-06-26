package org.wildfly.swarm.ts.microprofile.fault.tolerance.v21.fallback;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.wildfly.swarm.ts.microprofile.fault.tolerance.v21.MyConnection;

@WebServlet("/fallback")
public class FallbackServlet extends HttpServlet {
    @Inject
    private FallbackService service;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        String operation = req.getParameter("operation");
        boolean fail = Boolean.parseBoolean(req.getParameter("fail"));

        CompletionStage<MyConnection> completionStage = null;
        try {
            switch (operation) {
                case "exception":
                    completionStage = service.exception(fail);
                    break;
                case "myexception":
                    completionStage = service.myException(fail);
                    break;
                case "exception-skiponmye":
                    completionStage = service.exception_skipOnMyE(fail);
                    break;
                case "exception-applyone-skiponmye":
                    completionStage = service.exception_applyOnE_skipOnMyE(fail);
                    break;
                case "myexception-applyone-skiponmye":
                    completionStage = service.myException_applyOnE_skipOnMyE(fail);
                    break;
                case "exception-applyonmye-skipone":
                    completionStage = service.exception_applyOnMyE_skipOnE(fail);
                    break;
                case "myexception-applyonmye-skipone":
                    completionStage = service.myException_applyOnMyE_skipOnE(fail);
                    break;
            }
            resp.getWriter().print(completionStage != null ?
                                           completionStage.toCompletableFuture().get().getData() :
                                           "completion stage is null");
        } catch (ExecutionException e) {
            throw new ServletException("in FallbackServlet.doGet ExecutionException: " + e);
        } catch (Throwable t) {
            throw new ServletException("in FallbackServlet.doGet Throwable" + t);
        }
    }
}

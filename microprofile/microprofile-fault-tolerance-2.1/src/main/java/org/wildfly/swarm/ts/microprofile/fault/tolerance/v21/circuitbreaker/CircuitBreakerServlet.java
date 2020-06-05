package org.wildfly.swarm.ts.microprofile.fault.tolerance.v21.circuitbreaker;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.wildfly.swarm.ts.microprofile.fault.tolerance.v21.MyConnection;

@WebServlet("/circuitbreaker")
public class CircuitBreakerServlet extends HttpServlet {
    @Inject
    private CircuitBreakerService service;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        String operation = req.getParameter("operation");
        boolean fail = Boolean.parseBoolean(req.getParameter("fail"));

        CompletionStage<MyConnection> completionStage = null;
        try {
            switch (operation) {
                case "exception-failone":
                    completionStage = service.exception_failOnE(fail);
                    break;
                case "exception-failonmye":
                    completionStage = service.exception_failOnMyE(fail);
                    break;
                case "myexception-failone":
                    completionStage = service.myException_failOnE(fail);
                    break;
                case "myexception-failonmye":
                    completionStage = service.myException_failOnMyE(fail);
                    break;
                case "myexception-nocb":
                    completionStage = service.myException_noCB(fail);
                    break;
                case "myexception-blankcb":
                    completionStage = service.myException_blankCB(fail);
                    break;
                case "exception-failone-skiponmye":
                    completionStage = service.exception_failOnE_skipOnMyE(fail);
                    break;
                case "myexception-failone-skiponmye":
                    completionStage = service.myException_failOnE_skipOnMyE(fail);
                    break;
                case "exception-skiponmye":
                    completionStage = service.exception_skipOnMyE(fail);
                    break;
                case "myexception-skiponmye":
                    completionStage = service.myException_skipOnMyE(fail);
                    break;
                case "exception-failonmye-skipone":
                    completionStage = service.exception_failOnMyE_skipOnE(fail);
                    break;
                case "myexception-failonmye-skipone":
                    completionStage = service.myException_failOnMyE_skipOnE(fail);
                    break;
            }
            resp.getWriter().print(completionStage != null ?
                                           completionStage.toCompletableFuture().get().getData() :
                                           "completion stage is null");
        } catch (ExecutionException e) {
            throw new ServletException("in CircuitBreakerServlet.doGet ExecutionException: " + e);
        } catch (Throwable t) {
            throw new ServletException("in CircuitBreakerServlet.doGet Throwable" + t);
        }
    }
}

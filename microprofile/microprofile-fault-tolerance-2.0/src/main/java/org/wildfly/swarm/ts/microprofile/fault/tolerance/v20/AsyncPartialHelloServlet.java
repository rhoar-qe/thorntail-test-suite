package org.wildfly.swarm.ts.microprofile.fault.tolerance.v20;

import java.io.IOException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/partial")
public class AsyncPartialHelloServlet extends HttpServlet {
    @Inject
    private AsyncHelloService asyncHello;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String operation = req.getParameter("operation");
        String counterParam = req.getParameter("counter");
        int counter = counterParam != null ? Integer.parseInt(counterParam) : -1;

        CompletionStage<MyConnection> completionStage = null;
        try {
            switch (operation) {
                case "bulkhead5q5-timeout":
                    completionStage = asyncHello.bulkhead5q5Timeout(counter);
                    break;
                case "bulkhead15q5-timeout":
                    completionStage = asyncHello.bulkhead15q5Timeout(counter);
                    break;
                case "bulkhead5q5-timeout-retry":
                    completionStage = asyncHello.bulkhead5q5TimeoutRetry(counter);
                    break;
                case "bulkhead15q5-timeout-retry":
                    completionStage = asyncHello.bulkhead15q5TimeoutRetry(counter);
                    break;
                case "retry-circuitbreaker":
                    completionStage = asyncHello.retryCircuitBreaker(counter);
                    break;
                case "retry-circuitbreaker-timeout":
                    completionStage = asyncHello.retryCircuitBreakerTimeout(counter);
                    break;

            }
            resp.getWriter().print(completionStage != null ?
                                           completionStage.toCompletableFuture().get().getData() :
                                           "completion stage is null");
        } catch (InterruptedException | ExecutionException e) {
            throw new ServletException(e);
        }
    }
}

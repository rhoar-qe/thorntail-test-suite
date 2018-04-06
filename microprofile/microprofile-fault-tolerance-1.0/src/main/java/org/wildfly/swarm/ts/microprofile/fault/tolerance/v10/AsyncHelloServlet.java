package org.wildfly.swarm.ts.microprofile.fault.tolerance.v10;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@WebServlet("/async")
public class AsyncHelloServlet extends HttpServlet {
    @Inject
    private AsyncHelloService asyncHello;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String operation = req.getParameter("operation");
        boolean fail = Boolean.parseBoolean(req.getParameter("fail"));

        try {
            switch (operation) {
                case "timeout":
                    resp.getWriter().print(asyncHello.timeout(fail).get());
                    break;
                case "retry":
                    resp.getWriter().print(asyncHello.retry(fail).get());
                    break;
                case "circuit-breaker":
                    resp.getWriter().print(asyncHello.circuitBreaker(fail).get());
                    break;
                case "bulkhead":
                    resp.getWriter().print(asyncHello.bulkhead(fail).get());
                    break;
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new ServletException(e);
        }
    }
}

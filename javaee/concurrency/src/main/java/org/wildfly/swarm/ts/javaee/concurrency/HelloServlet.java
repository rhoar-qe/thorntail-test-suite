package org.wildfly.swarm.ts.javaee.concurrency;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@WebServlet("/")
public class HelloServlet extends HttpServlet {
    @Resource
    ManagedExecutorService executor;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String result = executor.submit(new ComputeHello()).get();
            resp.getWriter().print(result);
        } catch (InterruptedException | ExecutionException e) {
            throw new ServletException(e);
        }
    }
}

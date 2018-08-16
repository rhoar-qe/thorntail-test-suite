package org.wildfly.swarm.ts.microprofile.metrics.v11;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/hello2")
public class HelloServlet2 extends HttpServlet {
    @Inject
    private HelloService2 hello;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try {
            resp.getWriter().print(hello.hello());
        } catch (InterruptedException e) {
            throw new ServletException(e);
        }
    }
}

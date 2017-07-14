package org.wildfly.swarm.ts.wildfly.logging;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@WebServlet("/")
public class HelloServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(HelloServlet.class.getSimpleName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Servlet reached!");
        log.warning("Servlet reached!");
        resp.getWriter().print("Hello");
    }
}

package org.wildfly.swarm.ts.hollow.jar.web;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class HelloServlet extends HttpServlet {
    private String initParam;

    @Override
    public void init(ServletConfig servletConfig) {
        initParam = servletConfig.getInitParameter("foo");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        PrintWriter writer = resp.getWriter();
        writer.println("Servlet says: Hello, " + name + "!");
        writer.print("Init param:" + initParam);
    }
}

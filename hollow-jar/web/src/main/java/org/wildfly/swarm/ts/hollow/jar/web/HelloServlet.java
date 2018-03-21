package org.wildfly.swarm.ts.hollow.jar.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloServlet extends HttpServlet {

   
    private String initParam;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
       initParam = servletConfig.getInitParameter("foo");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        PrintWriter writer = resp.getWriter();
        writer.print("Servlet says: Hello, " + name + "!\n");
        writer.print("Init param:" + initParam);
        writer.flush();
    }

}

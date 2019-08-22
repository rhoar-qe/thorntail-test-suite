package org.wildfly.swarm.ts.javaee8.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/MappingServlet", "/Path/*", "*.txt", "/", ""})
public class MappingServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpServletMapping httpServletMapping = req.getHttpServletMapping();
        PrintWriter writer = resp.getWriter();
        writer.write("Mapping match: " + httpServletMapping.getMappingMatch() + "\n");
        writer.write("Servlet name: " + httpServletMapping.getServletName() + "\n");
        writer.write("Match value: " + httpServletMapping.getMatchValue() + "\n");
        writer.write("Pattern: " + httpServletMapping.getPattern() + "\n");
    }
}


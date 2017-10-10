package org.wildfly.swarm.ts.netflix.ribbon.secured;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.wildfly.swarm.topology.Topology;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/protected")
public class ProtectedAreaServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String response = "";
        try {
            Topology topology = Topology.lookup();
            Map<String, List<Topology.Entry>> entries = topology.asMap();
            for (String key : entries.keySet()) {               
                response = response + key;
            }
        } catch (Exception e) {
            throw new ServletException();
        }
        resp.getWriter().print(response);
    }
}

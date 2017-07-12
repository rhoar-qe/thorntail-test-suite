package org.wildfly.swarm.ts.datasources;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/")
public class HelloServlet extends HttpServlet {
    @Resource
    private DataSource ds;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println("DataSource present: " + (ds != null));
        try (Connection conn = ds.getConnection()) {
            resp.getWriter().println("Connection obtained: " + (conn != null));
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}

package org.wildfly.swarm.ts.hollow.jar.full;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/clear-result")
public class ClearResultServlet extends HttpServlet {
    @Inject
    private Result result;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        result.clear();
        resp.getWriter().print("OK");
    }
}

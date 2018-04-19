package org.wildfly.swarm.ts.hollow.jar.full;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/clear-process-result")
public class ClearProcessResultServlet extends HttpServlet {
    @Inject
    private ProcessResult processResult;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        processResult.clear();
        resp.getWriter().print("OK");
    }
}

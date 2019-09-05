package org.wildfly.swarm.ts.javaee8.jsonp11;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/jsonp/collector")
public class CollectorServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonController controller = new JsonController();
        String result = controller.jsonCollector();
        resp.setContentType("text/plain");
        resp.getWriter().print(result);
    }
}

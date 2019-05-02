package org.wildfly.swarm.ts.javaee.jsonb;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/")
public class HelloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Result result = new Result();
        result.hello = "world";

        resp.setContentType("application/json");
        Jsonb jsonb = JsonbBuilder.create();
        jsonb.toJson(result, resp.getWriter());
    }
}

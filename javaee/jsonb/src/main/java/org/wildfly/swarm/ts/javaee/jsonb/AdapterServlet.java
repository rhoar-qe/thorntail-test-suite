package org.wildfly.swarm.ts.javaee.jsonb;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/adapt")
public class AdapterServlet extends HttpServlet {
    private static final Jsonb jsonb = JsonbBuilder.create(
            new JsonbConfig().withAdapters(new ResultAdapter())
    );

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String adaptCase = req.getParameter("case");
        if (adaptCase.equals("to")) {
            adaptToJson(resp);
        } else if (adaptCase.equals("from")) {
            adaptFromJson(resp);
        } else {
            resp.getWriter().println("Invalid 'case' query parameter: '" + adaptCase + "'. Valid are 'to' and 'from'.");
        }
    }

    private void adaptToJson(HttpServletResponse resp) throws IOException {
        Result result = new Result();
        result.hello = "world";

        resp.setContentType("application/json");
        jsonb.toJson(result, resp.getWriter());
    }

    private void adaptFromJson(HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.getWriter().print(jsonb.fromJson("{\"hello\":\"world\"}", Result.class).hello);
    }
}

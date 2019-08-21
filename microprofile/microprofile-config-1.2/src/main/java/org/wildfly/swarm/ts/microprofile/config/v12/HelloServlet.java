package org.wildfly.swarm.ts.microprofile.config.v12;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebServlet("/")
public class HelloServlet extends HttpServlet {
    @Inject
    private Config config;

    @Inject
    @ConfigProperty(name = "arrayProperty")
    private String string;

    @Inject
    @ConfigProperty(name = "arrayProperty")
    private String[] array;

    @Inject
    @ConfigProperty(name = "arrayProperty")
    private List<String> list;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        switch (req.getParameter("mode")) {
            case "injected-string":
                resp.getWriter().print(string);
                break;
            case "injected-array":
                resp.getWriter().print(String.join("; ", array));
                break;
            case "injected-list":
                resp.getWriter().print(String.join("; ", list));
                break;
            case "lookup-string":
                String string = config.getValue("arrayProperty", String.class);
                resp.getWriter().print(string);
                break;
            case "lookup-array":
                String[] array = config.getValue("arrayProperty", String[].class);
                resp.getWriter().print(String.join("; ", array));
                break;
        }
    }
}

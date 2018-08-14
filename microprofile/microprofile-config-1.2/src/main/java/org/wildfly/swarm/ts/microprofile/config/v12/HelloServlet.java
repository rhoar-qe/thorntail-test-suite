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
    @ConfigProperty(name="arrayProperty")
    private List<String> arrayPropertyList;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String[] arrayProperty = config.getValue("arrayProperty", String[].class);
        resp.getWriter().println("arrayproperty programatic: " + Arrays.toString(arrayProperty));
        resp.getWriter().println("arrayproperty injected list: " + String.join(",",arrayPropertyList));
    }
}

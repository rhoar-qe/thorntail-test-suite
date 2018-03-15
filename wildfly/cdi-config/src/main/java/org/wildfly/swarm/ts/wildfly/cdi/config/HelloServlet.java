package org.wildfly.swarm.ts.wildfly.cdi.config;

import org.wildfly.swarm.spi.api.config.ConfigKey;
import org.wildfly.swarm.spi.api.config.ConfigView;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/")
public class HelloServlet extends HttpServlet {
    @Inject
    @ConfigurationValue("app.config")
    private String appConfig;

    @Inject
    private ConfigView configView;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.getWriter().println("Value of app.config: " + appConfig);
        resp.getWriter().println("ConfigView contains app.config: " + configView.hasKeyOrSubkeys(ConfigKey.parse("app.config")));
    }
}

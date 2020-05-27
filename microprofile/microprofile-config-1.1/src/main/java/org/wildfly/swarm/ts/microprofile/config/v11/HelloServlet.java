package org.wildfly.swarm.ts.microprofile.config.v11;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/")
public class HelloServlet extends HttpServlet {
    @Inject
    @ConfigProperty(name = "app.timeout")
    private long appTimeout;

    @Inject
    @ConfigProperty(name = "fileProperty")
    private String propFromCustomScanDir;

    @Inject
    @ConfigProperty(name = "yamlProperty")
    private String yamlProperty;

    @Inject
    @ConfigProperty(name = "yamlOrderedProperty")
    private String yamlOrderedProperty;

    @Inject
    @ConfigProperty(name = "missing.property", defaultValue = "it's present anyway")
    private String missingProperty;

    @Inject
    private Config config;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.getWriter().println("Value of app.timeout: " + appTimeout);
        resp.getWriter().println("Value of missing.property: " + missingProperty);
        resp.getWriter().println("Config contains app.timeout: " + config.getOptionalValue("app.timeout", String.class).isPresent());
        resp.getWriter().println("Config contains missing.property: " + config.getOptionalValue("missing.property", String.class).isPresent());
        resp.getWriter().println("Custom scan dir: " + propFromCustomScanDir);
        resp.getWriter().println("YAML Property: " + yamlProperty);
        resp.getWriter().println("YAML Ordered property: " + yamlOrderedProperty);
    }
}

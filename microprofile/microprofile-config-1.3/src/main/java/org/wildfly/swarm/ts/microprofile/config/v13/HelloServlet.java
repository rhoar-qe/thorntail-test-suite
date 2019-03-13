package org.wildfly.swarm.ts.microprofile.config.v13;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

@WebServlet("/")
public class HelloServlet extends HttpServlet {

    @Inject
    private Config config;

    @Inject
    @ConfigProperty(name = "my.notexisting.string.property")
    private Optional<String> notExisting;

    @Inject
    @ConfigProperty(name = "my.existing.string.property")
    private Optional<String> existing;

    @Inject
    @ConfigProperty(name = "MY_ENVIRONMENT_VARIABLE")
    private String myProperty;

    @Inject
    @ConfigProperty(name = "MY.ENVIRONMENT.VARIABLE")
    private String myPropertyDot;

    @Inject
    @ConfigProperty(name = "my.environment.variable")
    private String myPropertyDotLowercase;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Check mapping rule: from config property name to environment variable
        // https://github.com/eclipse/microprofile-config/issues/264
        final String key = "MY_ENVIRONMENT_VARIABLE";
        final String keyDots = "MY.ENVIRONMENT.VARIABLE";
        final String keyDotsLowercase = "my.environment.variable";
        resp.getWriter().println(key + ": " + config.getValue(key, String.class));
        resp.getWriter().println(keyDots + ": " + config.getValue(keyDots, String.class));
        resp.getWriter().println(keyDotsLowercase + ": " + config.getValue(keyDotsLowercase, String.class));
        resp.getWriter().println(key + ": " + myProperty);
        resp.getWriter().println(keyDots + ": " + myPropertyDot);
        resp.getWriter().println(keyDotsLowercase + ": " + myPropertyDotLowercase);

        // Check URI will be converted
        // https://github.com/eclipse/microprofile-config/issues/322
        URI uri = config.getValue("tck.config.test.javaconfig.converter.urlvalue", URI.class);
        resp.getWriter().println("URI: " + uri);

        // Testing injecting an Optional<String> that has no config value
        // https://github.com/eclipse/microprofile-config/issues/336
        resp.getWriter().println("Optional notExisting: " + notExisting.isPresent());
        resp.getWriter().println("Optional existing: " + existing.isPresent());
    }
}

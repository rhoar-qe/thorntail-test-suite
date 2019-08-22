package org.wildfly.swarm.ts.microprofile.config.v13;

import org.eclipse.microprofile.config.Config;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.NoSuchElementException;

@WebServlet("/empty-properties")
public class EmptyPropertiesServlet extends HttpServlet {
    private static final String EMPTY_PROPERTY = "my.empty.system.property";

    private static final String PROP_FILE_EMPTY_PROPERTY = "my.empty.property.in.config.file";

    @Inject
    private Config config;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.setProperty(EMPTY_PROPERTY, "");

        String fromSystemProperty = getPossiblyEmptyConfigProperty(EMPTY_PROPERTY);
        resp.getWriter().println("Empty system property: '" + fromSystemProperty + "'");

        String fromConfigFile = getPossiblyEmptyConfigProperty(PROP_FILE_EMPTY_PROPERTY);
        resp.getWriter().println("Empty property in config file: '" + fromConfigFile + "'");
    }

    private String getPossiblyEmptyConfigProperty(String propertyName) {
        try {
            return config.getValue(propertyName, String.class);
        } catch (NoSuchElementException e) {
            return e.getMessage();
        }
    }
}

package org.wildfly.swarm.ts.microprofile.config.v14;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@WebServlet("/")
public class HelloServlet extends HttpServlet {

    @Inject
    private Config config;

    @Inject
    @ConfigProperty(name = "my.byte.property")
    private byte byteProperty;

    @Inject
    @ConfigProperty(name = "my.byte.property")
    private Byte byteObjProperty;

    @Inject
    @ConfigProperty(name = "my.short.property")
    private short shortProperty;

    @Inject
    @ConfigProperty(name = "my.short.property")
    private Short shortObjProperty;

    @Inject
    @ConfigProperty(name = "my.char.property")
    private char charProperty;

    @Inject
    @ConfigProperty(name = "my.char.property")
    private Character charObjProperty;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.getWriter().println("my.byte.property: " + byteProperty);
        resp.getWriter().println("my.byte.property: " + byteObjProperty);
        resp.getWriter().println("my.short.property: " + shortProperty);
        resp.getWriter().println("my.short.property: " + shortObjProperty);
        resp.getWriter().println("my.char.property: " + charProperty);
        resp.getWriter().println("my.char.property: " + charObjProperty);
    }
}

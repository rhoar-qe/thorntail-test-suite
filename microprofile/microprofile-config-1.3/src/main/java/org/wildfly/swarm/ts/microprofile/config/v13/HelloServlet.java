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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Inject
    @ConfigProperty(name = "my.boolean.property")
    private boolean booleanProperty;

    @Inject
    @ConfigProperty(name = "my.boolean.property")
    private Boolean booleanObjProperty;

    @Inject
    @ConfigProperty(name = "my.int.property")
    private int intProperty;

    @Inject
    @ConfigProperty(name = "my.int.property")
    private Integer integerProperty;

    @Inject
    @ConfigProperty(name = "my.long.property")
    private long longProperty;

    @Inject
    @ConfigProperty(name = "my.long.property")
    private Long longObjProperty;

    @Inject
    @ConfigProperty(name = "my.float.property")
    private float floatProperty;

    @Inject
    @ConfigProperty(name = "my.float.property")
    private Float floatObjProperty;

    @Inject
    @ConfigProperty(name = "my.double.property")
    private double doubleProperty;

    @Inject
    @ConfigProperty(name = "my.double.property")
    private Double doubleObjProperty;

    @Inject
    @ConfigProperty(name = "my.stringWrapper.property")
    private StringWrapper stringWrapper;

    @Inject
    @ConfigProperty(name = "my.animals.array.property")
    private String[] myAnimalsArray;

    @Inject
    @ConfigProperty(name = "my.animals.array.property")
    private List<String> myAnimalsList;

    @Inject
    @ConfigProperty(name = "my.animals.array.property")
    private Set<String> myAnimalsSet;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Check mapping rule: from config property name to environment variable
        // https://github.com/eclipse/microprofile-config/issues/264
        String key = "MY_ENVIRONMENT_VARIABLE";
        String keyDots = "MY.ENVIRONMENT.VARIABLE";
        String keyDotsLowercase = "my.environment.variable";
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

        resp.getWriter().println("my.boolean.property: " + booleanObjProperty);
        resp.getWriter().println("my.boolean.property: " + booleanProperty);
        resp.getWriter().println("my.int.property: " + integerProperty);
        resp.getWriter().println("my.int.property: " + intProperty);
        resp.getWriter().println("my.long.property: " + longObjProperty);
        resp.getWriter().println("my.long.property: " + longProperty);
        resp.getWriter().println("my.float.property: " + floatObjProperty);
        resp.getWriter().println("my.float.property: " + floatProperty);
        resp.getWriter().println("my.double.property: " + doubleObjProperty);
        resp.getWriter().println("my.double.property: " + doubleProperty);

        // custom converter on custom class
        resp.getWriter().println("my.stringWrapper.property: " + stringWrapper.getProperty());
        resp.getWriter().println("my.stringWrapper.property: "
                + config.getValue("my.stringWrapper.property", StringWrapper.class).getProperty());

        String[] animals = config.getValue("my.animals.array.property", String[].class);
        resp.getWriter().println("my.animals.array.property.array: " + (animals.length == 2
                ? animals[0] + ", " + animals[1]
                : "unexpected length: " + animals.length));
        resp.getWriter().println("my.animals.array.property.array: " + (myAnimalsArray.length == 2
                ? myAnimalsArray[0] + ", " + myAnimalsArray[1]
                : "unexpected length: " + myAnimalsArray.length));
        resp.getWriter().println("my.animals.array.property.list: " + myAnimalsList);
        // can't rely on Set iteration order
        resp.getWriter().println("my.animals.array.property.set: ["
                + myAnimalsSet.stream().sorted().collect(Collectors.joining(", ")) + "]");
    }
}

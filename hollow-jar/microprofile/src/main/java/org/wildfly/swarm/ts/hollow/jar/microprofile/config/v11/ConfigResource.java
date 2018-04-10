package org.wildfly.swarm.ts.hollow.jar.microprofile.config.v11;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/config")
public class ConfigResource {
    @Inject
    @ConfigProperty(name = "app.timeout")
    private long appTimeout;

    @Inject
    @ConfigProperty(name = "missing.property", defaultValue = "it's present anyway")
    private String missingProperty;

    @Inject
    @ConfigProperty(name = "prop.from.config.source")
    private String propFromConfigSource;

    @Inject
    @ConfigProperty(name = "prop.from.config.source.with.ordinal")
    private String propFromConfigSourceWithOrdinal;

    @Inject
    private Config config;

    @GET
    public String get() {
        return "Value of app.timeout: " + appTimeout + "\n"
                + "Value of missing.property: " + missingProperty + "\n"
                + "Config contains app.timeout: " + config.getOptionalValue("app.timeout", String.class).isPresent() + "\n"
                + "Config contains missing.property: " + config.getOptionalValue("missing.property", String.class).isPresent() + "\n"
                + "Custom config source prop.from.config.source: " + propFromConfigSource + "\n"
                + "Custom config source prop.from.config.source.with.ordinal: " + propFromConfigSourceWithOrdinal + "\n";
    }
}

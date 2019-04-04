package org.wildfly.swarm.ts.microprofile.jwt.v11;

import java.util.Set;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/issuer")
@DenyAll
public class Config11Resource {
    @Inject
    private JsonWebToken jwt;

    @Inject
    @Claim(standard = Claims.iss)
    private String issuer;

    @Inject
    @Claim(standard = Claims.groups)
    private Set<String> groups;

    @Inject
    private Config config;

    @Inject
    @ConfigProperty(name = "mp.jwt.verify.issuer")
    private String configPropertyIssuer;

    @GET
    @Path("/")
    @RolesAllowed("*")
    public String helloIssuer(@Context SecurityContext security, @QueryParam("source") String source) {
        String configIssuer;
        if (source.equals("property")) {
            configIssuer = configPropertyIssuer;
        } else if (source.equals("value")) {
            configIssuer = config.getValue("mp.jwt.verify.issuer", String.class);
        } else {
            configIssuer = "unknown \"" + source + "\"";
        }
        return "Hello, " + jwt.getName() + ", your token was issued by " + configIssuer + ", you are in groups " + groups
                + " and you are" + (security.isUserInRole("superuser") ? "" : " NOT") + " in role superuser";
    }
}

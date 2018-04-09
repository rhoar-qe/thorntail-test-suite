package org.wildfly.swarm.ts.microprofile.jwt.v10;

import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.util.Set;

@Path("/secured")
@DenyAll
public class SecuredResource {
    @Inject
    private JsonWebToken jwt;

    @Inject
    @Claim(standard = Claims.iss)
    private String issuer;

    @Inject
    @Claim(standard = Claims.groups)
    private Set<String> groups;

    @GET
    @Path("/everyone")
    @RolesAllowed("*")
    public String hello(@Context SecurityContext security) {
        return "Hello, " + jwt.getName() + ", your token was issued by " + issuer + ", you are in groups " + groups
                + " and you are" + (security.isUserInRole("superuser") ? "" : " NOT") + " in role superuser";
    }

    @GET
    @Path("/admin")
    @RolesAllowed("admin")
    public String admin() {
        return "Restricted area! Admin access granted!";
    }

    @GET
    @Path("/noone")
    public String noone() {
        return "How did you get here?";
    }
}

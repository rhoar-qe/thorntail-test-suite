package org.wildfly.swarm.ts.javaee.ejb.remote.security;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

@Remote
@Stateless
public class SecuredRemoteEjbImpl implements SecuredRemoteEjb {
    @Resource
    private SessionContext ctx;

    @Override
    @PermitAll
    public String publicMethod() {
        return "public secured method, user is " + ctx.getCallerPrincipal() + ", admin: " + ctx.isCallerInRole("admin");
    }

    @Override
    @RolesAllowed("admin")
    public String privateMethod() {
        return "private secured method";
    }
}

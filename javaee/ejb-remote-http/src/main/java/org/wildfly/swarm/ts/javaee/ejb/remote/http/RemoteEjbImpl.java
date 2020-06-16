package org.wildfly.swarm.ts.javaee.ejb.remote.http;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

@Remote
@Stateless
public class RemoteEjbImpl implements RemoteEjb {
    @Resource
    private SessionContext ctx;

    @Override
    @PermitAll // without forcing authz, authn won't happen?
    public String hello() {
        return "hello " + ctx.getCallerPrincipal();
    }
}

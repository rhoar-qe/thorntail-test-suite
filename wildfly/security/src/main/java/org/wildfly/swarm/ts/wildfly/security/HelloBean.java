package org.wildfly.swarm.ts.wildfly.security;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Stateless
public class HelloBean {
    @RolesAllowed("AdminRole")
    public String hello() {
        return "Hello, World!";
    }
}

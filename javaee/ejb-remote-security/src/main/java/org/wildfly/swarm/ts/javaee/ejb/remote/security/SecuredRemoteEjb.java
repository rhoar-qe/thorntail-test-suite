package org.wildfly.swarm.ts.javaee.ejb.remote.security;

public interface SecuredRemoteEjb {
    String publicMethod();

    String privateMethod();
}

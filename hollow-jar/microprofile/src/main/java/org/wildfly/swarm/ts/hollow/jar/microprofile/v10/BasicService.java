package org.wildfly.swarm.ts.hollow.jar.microprofile.v10;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BasicService {
    public String hello() {
        return "Hello, World!";
    }
}

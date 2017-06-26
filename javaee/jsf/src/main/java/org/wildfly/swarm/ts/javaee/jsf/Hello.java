package org.wildfly.swarm.ts.javaee.jsf;

import javax.enterprise.inject.Model;

@Model
public class Hello {
    public String hello() {
        return "Hello from JSF";
    }
}


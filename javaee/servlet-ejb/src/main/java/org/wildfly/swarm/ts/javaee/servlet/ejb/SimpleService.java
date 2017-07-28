package org.wildfly.swarm.ts.javaee.servlet.ejb;

import javax.ejb.Singleton;

@Singleton
public class SimpleService {
    public String yay() {
        return "Yay! ";
    }
}

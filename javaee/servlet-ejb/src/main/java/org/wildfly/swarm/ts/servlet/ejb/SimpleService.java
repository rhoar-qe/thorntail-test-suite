package org.wildfly.swarm.ts.servlet.ejb;

import javax.ejb.Singleton;

@Singleton
public class SimpleService {
    public String yay() {
        return "Yay! ";
    }
}

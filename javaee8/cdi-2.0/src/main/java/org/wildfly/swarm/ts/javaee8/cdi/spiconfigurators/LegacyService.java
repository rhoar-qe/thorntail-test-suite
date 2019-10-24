package org.wildfly.swarm.ts.javaee8.cdi.spiconfigurators;

public class LegacyService {
    private HelloGetter getter;

    public String sayHello() {
        return getter.getHello();
    }
}

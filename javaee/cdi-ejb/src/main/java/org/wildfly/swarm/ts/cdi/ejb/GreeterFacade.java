package org.wildfly.swarm.ts.cdi.ejb;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GreeterFacade {
    @EJB
    private GreeterBean greeterBean;

    public String hello(String name) {
        return "Said: " + greeterBean.hello(name);
    }
}

package org.wildfly.swarm.ts.hollow.jar.web.ejb;

import javax.ejb.Stateless;

@Stateless
public class EjbLocal implements EjbLocalInterface {
    public String method() {
        return "EJB method";
    }
}

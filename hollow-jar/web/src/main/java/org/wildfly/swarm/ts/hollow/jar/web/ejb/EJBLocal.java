package org.wildfly.swarm.ts.hollow.jar.web.ejb;

import javax.ejb.Stateless;

@Stateless
public class EJBLocal implements EJBLocalInterface{

    public String method() {
        return "EJBMethod";
    }
}

package org.wildfly.swarm.ts.hollow.jar.web.ejb;

import javax.ejb.Local;

@Local
public interface EJBLocalInterface {

    public String method();
}

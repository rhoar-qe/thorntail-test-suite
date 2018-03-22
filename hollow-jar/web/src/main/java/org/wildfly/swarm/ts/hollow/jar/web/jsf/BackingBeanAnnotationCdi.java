package org.wildfly.swarm.ts.hollow.jar.web.jsf;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named
@RequestScoped
public class BackingBeanAnnotationCdi {
    public String getMessage() {
        return "Hello from BackingBeanAnnotationCdi";
    }
}

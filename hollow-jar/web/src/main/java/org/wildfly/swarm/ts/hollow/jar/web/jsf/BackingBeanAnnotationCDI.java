package org.wildfly.swarm.ts.hollow.jar.web.jsf;

import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

@Named
@RequestScoped
public class BackingBeanAnnotationCDI {

    public String getMessage() {
        return "Hello from BackingBeanAnnotationCDI";
    }
}

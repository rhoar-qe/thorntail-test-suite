package org.wildfly.swarm.ts.hollow.jar.web.jsf;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean
@RequestScoped
public class BackingBeanAnnotation {
    public String getMessage() {
        return "Hello from BackingBeanAnnotation";
    }
}

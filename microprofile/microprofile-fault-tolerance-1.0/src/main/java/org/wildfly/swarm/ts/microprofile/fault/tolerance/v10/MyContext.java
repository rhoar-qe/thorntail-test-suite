package org.wildfly.swarm.ts.microprofile.fault.tolerance.v10;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class MyContext {
    private String value = "default";

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        this.value = value;
    }
}

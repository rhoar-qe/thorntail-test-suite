package org.wildfly.swarm.ts.microprofile.fault.tolerance.v21.timeout;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class TimeoutContext {
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

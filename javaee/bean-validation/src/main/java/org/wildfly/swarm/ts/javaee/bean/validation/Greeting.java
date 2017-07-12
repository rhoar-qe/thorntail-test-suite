package org.wildfly.swarm.ts.javaee.bean.validation;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Greeting {
    @NotNull
    @Size(min = 1, max = 25)
    private String value;

    public Greeting(String value) {
        this.value = value;
    }
}

package org.wildfly.swarm.ts.microprofile.config.v13;

public class StringWrapper {
    private String property;

    private StringWrapper(String property) {
        this.property = property;
    }

    public static StringWrapper of(String property) {
        return new StringWrapper(property);
    }

    public String getProperty() {
        return property;
    }
}

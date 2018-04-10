package org.wildfly.swarm.ts.hollow.jar.microprofile.config.v11;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.HashMap;
import java.util.Map;

public class TestConfigSource implements ConfigSource {
    private static final String NAME = TestConfigSource.class.getSimpleName();

    private Map<String, String> props;

    public TestConfigSource() {
        props = new HashMap<>();
        props.put("prop.from.config.source", NAME);
        props.put("prop.from.config.source.with.ordinal", NAME);
    }

    @Override
    public Map<String, String> getProperties() {
        return props;
    }

    @Override
    public String getValue(String propertyName) {
        return props.get(propertyName);
    }

    @Override
    public String getName() {
        return NAME;
    }
}

package org.wildfly.swarm.ts.hollow.jar.microprofile.config.v11;

public class TestConfigSourceWithOrdinal extends TestConfigSource {
    private static final String NAME = TestConfigSourceWithOrdinal.class.getSimpleName();

    public TestConfigSourceWithOrdinal() {
        super();
        getProperties().put("prop.from.config.source.with.ordinal", NAME);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int getOrdinal() {
        return 99;
    }
}

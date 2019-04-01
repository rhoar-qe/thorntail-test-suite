package org.wildfly.swarm.ts.microprofile.config.v13;

import javax.annotation.Priority;

import org.eclipse.microprofile.config.spi.Converter;

@Priority(500)
public class StringWrapperConverter implements Converter<StringWrapper> {
    public static final String CONVERTED = "Converted";

    @Override
    public StringWrapper convert(String value) {
        return StringWrapper.of(CONVERTED + " " + value);
    }
}
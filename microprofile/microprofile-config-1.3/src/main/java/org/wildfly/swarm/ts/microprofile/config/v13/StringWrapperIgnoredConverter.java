package org.wildfly.swarm.ts.microprofile.config.v13;

import javax.annotation.Priority;

import org.eclipse.microprofile.config.spi.Converter;

@Priority(499) // priority lower than StringWrapperConverter
public class StringWrapperIgnoredConverter implements Converter<StringWrapper> {
    public static final String CONVERTED = "This should not occur, check priority!";

    @Override
    public StringWrapper convert(String value) {
        return StringWrapper.of(CONVERTED + " " + value);
    }
}
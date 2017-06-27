package org.wildfly.swarm.ts.common;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Only makes sense in integration tests where the Swarm uberjar is started by the Swarm Maven plugin.
 * Does <b>not</b> work in Arquillian tests!
 */
public final class LogAssert extends AbstractAssert<LogAssert, Log> {
    private List<String> lines;

    public static LogAssert assertThat(Log log) {
        return new LogAssert(log);
    }

    public LogAssert(Log log) {
        super(log, LogAssert.class);
    }

    private List<String> lines() throws IOException {
        if (lines == null) {
            lines = Files.readAllLines(actual.path);
        }

        return lines;
    }

    public LogAssert hasLine(String line) throws IOException {
        isNotNull();
        Assertions.assertThat(actual.path).isRegularFile();
        Assertions.assertThat(lines()).contains(line);
        return this;
    }
}

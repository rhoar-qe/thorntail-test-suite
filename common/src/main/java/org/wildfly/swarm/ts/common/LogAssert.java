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

    /** Asserts that the log contains at least one line that contains given {@code string}. */
    public LogAssert hasLine(String string) throws IOException {
        isNotNull();
        Assertions.assertThat(actual.path).isRegularFile();
        Assertions.assertThat(lines())
                .as("log %s must contain at least one line that contains '%s'", actual, string)
                .anySatisfy(line -> Assertions.assertThat(line).contains(string));
        return this;
    }
}

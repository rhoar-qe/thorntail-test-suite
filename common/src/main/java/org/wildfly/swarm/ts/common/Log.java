package org.wildfly.swarm.ts.common;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Only makes sense in integration tests where the Swarm uberjar is started by the Swarm Maven plugin.
 * Does <b>not</b> work in Arquillian tests!
 */
public final class Log {
    public static final Log STDOUT = new Log("target/stdout.log");
    public static final Log STDERR = new Log("target/stderr.log");

    public static Log custom(String path) {
        return new Log(path);
    }

    final Path path;

    private Log(String path) {
        this.path = Paths.get(path);
    }
}

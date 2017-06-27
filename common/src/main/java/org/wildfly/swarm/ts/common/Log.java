package org.wildfly.swarm.ts.common;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Only makes sense in integration tests where the Swarm uberjar is started by the Swarm Maven plugin.
 * Does <b>not</b> work in Arquillian tests!
 */
public enum Log {
    STDOUT("target/stdout.log"),
    STDERR("target/stderr.log"),;

    final Path path;

    Log(String path) {
        this.path = Paths.get(path);
    }
}

package org.wildfly.swarm.ts.common.docker;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class DockerContainer {
    private final String containerId;
    private final ExecutorService outputPrinter;

    DockerContainer(String containerId, ExecutorService outputPrinter) {
        this.containerId = containerId;
        this.outputPrinter = outputPrinter;
    }

    public void stop() throws IOException, InterruptedException {
        new ProcessBuilder()
                .command("docker", "stop", containerId)
                .start()
                .waitFor(10, TimeUnit.SECONDS);

        outputPrinter.shutdown();
        outputPrinter.awaitTermination(10, TimeUnit.SECONDS);

        new ProcessBuilder()
                .command("docker", "rm", containerId)
                .start()
                .waitFor(10, TimeUnit.SECONDS);
    }
}

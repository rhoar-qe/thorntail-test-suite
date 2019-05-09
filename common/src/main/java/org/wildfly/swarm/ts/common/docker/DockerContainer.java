package org.wildfly.swarm.ts.common.docker;

import org.fusesource.jansi.Ansi;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class DockerContainer {
    private final String name;
    private final String containerId;
    private final ExecutorService outputPrinter;

    DockerContainer(String name, String containerId, ExecutorService outputPrinter) {
        this.name = name;
        this.containerId = containerId;
        this.outputPrinter = outputPrinter;
    }

    public void stop() throws IOException, InterruptedException {
        System.out.println(Ansi.ansi().reset().a("Stopping container ").fgCyan().a(name).reset()
                .a(" with ID ").fgYellow().a(containerId).reset());

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

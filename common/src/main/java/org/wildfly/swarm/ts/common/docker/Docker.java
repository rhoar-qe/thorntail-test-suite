package org.wildfly.swarm.ts.common.docker;

import org.fusesource.jansi.Ansi;
import org.junit.rules.ExternalResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Intended to be used as a JUnit {@link org.junit.ClassRule ClassRule}.
 */
public class Docker extends ExternalResource {
    private final String uuid;
    private final String name;
    private final String image;
    private final List<String> dockerArguments = new ArrayList<>();
    private final List<String> commandArguments = new ArrayList<>();
    private String awaitedLogLine;
    private long waitTimeoutInMillis = 600_000; // 10 minutes

    private final ExecutorService outputPrinter = Executors.newSingleThreadExecutor();

    public Docker(String name, String image) {
        this.uuid = UUID.randomUUID().toString();
        this.name = name;
        this.image = image;
    }

    public Docker waitForLogLine(String logWait) {
        this.awaitedLogLine = logWait;
        return this;
    }

    public Docker waitTimeout(long timeoutInMillis) {
        this.waitTimeoutInMillis = timeoutInMillis;
        return this;
    }

    public Docker port(String port) {
        dockerArguments.add("-p");
        dockerArguments.add(port);
        return this;
    }

    public Docker envVar(String key, String value) {
        dockerArguments.add("-e");
        dockerArguments.add(key + "=" + value);
        return this;
    }

    public Docker tmpfsMount(String directoryInContainer) {
        dockerArguments.add("--tmpfs");
        dockerArguments.add(directoryInContainer);
        return this;
    }

    public Docker bindMount(String directoryOnHost, String directoryInContainer) {
        dockerArguments.add("-v");
        dockerArguments.add(directoryOnHost + ":" + directoryInContainer);
        return this;
    }

    public Docker cmdArg(String commandArgument) {
        commandArguments.add(commandArgument);
        return this;
    }

    private void start() throws IOException, TimeoutException, InterruptedException {
        List<String> cmd = new ArrayList<>();

        cmd.add("docker");
        cmd.add("run");
        cmd.add("--name");
        cmd.add(uuid);

        cmd.addAll(dockerArguments);

        cmd.add(image);

        cmd.addAll(commandArguments);

        System.out.println(Ansi.ansi().reset().a("Starting container ").fgCyan().a(name).reset()
                .a(" with ID ").fgYellow().a(uuid).reset());

        Process dockerRunProcess = new ProcessBuilder()
                .redirectErrorStream(true)
                .command(cmd)
                .start();

        CountDownLatch latch = new CountDownLatch(1);

        outputPrinter.execute(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(dockerRunProcess.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(Ansi.ansi().fgCyan().a(name).reset().a("> ").a(line));

                    if (awaitedLogLine != null && line.contains(awaitedLogLine)) {
                        latch.countDown();
                    }
                }
            } catch (IOException ignored) {
            }
        });

        if (awaitedLogLine != null) {
            if (latch.await(waitTimeoutInMillis, TimeUnit.MILLISECONDS)) {
                return;
            }

            try {
                stop();
            } catch (Exception ignored) {
                // ignored because we're about to throw another exception
            }

            throw new TimeoutException("Container '" + name + " (" + uuid + ")' didn't print '"
                    + awaitedLogLine + "' in " + waitTimeoutInMillis + " ms");
        }
    }

    private void stop() throws IOException, InterruptedException {
        System.out.println(Ansi.ansi().reset().a("Stopping container ").fgCyan().a(name).reset()
                .a(" with ID ").fgYellow().a(uuid).reset());

        new ProcessBuilder()
                .command("docker", "stop", uuid)
                .start()
                .waitFor(10, TimeUnit.SECONDS);

        outputPrinter.shutdown();
        outputPrinter.awaitTermination(10, TimeUnit.SECONDS);

        new ProcessBuilder()
                .command("docker", "rm", uuid)
                .start()
                .waitFor(10, TimeUnit.SECONDS);
    }

    // ---

    @Override
    protected void before() throws Throwable {
        start();
    }

    @Override
    protected void after() {
        try {
            stop();
        } catch (IOException | InterruptedException e) {
            System.out.println(Ansi.ansi().reset().a("Failed stopping container ").fgCyan().a(name).reset()
                    .a(" with ID ").fgYellow().a(uuid).reset());
        }
    }
}

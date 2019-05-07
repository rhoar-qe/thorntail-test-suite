package org.wildfly.swarm.ts.common.docker;

import org.fusesource.jansi.Ansi;

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

public class Docker {
    private final String name;
    private final String image;
    private final List<String> ports = new ArrayList<>();
    private final Map<String, String> environmentVariables = new HashMap<>();
    private final List<String> commandArguments = new ArrayList<>();
    private String awaitedLogLine;
    private long waitTimeoutInMillis = 600_000; // 10 minutes

    public Docker(String name, String image) {
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
        this.ports.add(port);
        return this;
    }

    public Docker envVar(String key, String value) {
        environmentVariables.put(key, value);
        return this;
    }

    public Docker cmdArg(String commandArgument) {
        commandArguments.add(commandArgument);
        return this;
    }

    public DockerContainer start() throws Exception {
        List<String> cmd = new ArrayList<>();

        String uuid = UUID.randomUUID().toString();

        cmd.add("docker");
        cmd.add("run");
        cmd.add("--name");
        cmd.add(uuid);

        for (String port : ports) {
            cmd.add("-p");
            cmd.add(port);
        }

        for (Map.Entry<String, String> envVar : environmentVariables.entrySet()) {
            cmd.add("-e");
            cmd.add(envVar.getKey() + "=" + envVar.getValue());
        }

        cmd.add(image);

        cmd.addAll(commandArguments);

        System.out.println(Ansi.ansi().reset().a("Starting container ").fgCyan().a(name).reset()
                .a(" with ID ").fgYellow().a(uuid).reset());

        Process dockerRunProcess = new ProcessBuilder()
                .redirectErrorStream(true)
                .command(cmd)
                .start();

        CountDownLatch latch = new CountDownLatch(1);

        ExecutorService outputPrinter = Executors.newSingleThreadExecutor();
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

        DockerContainer result = new DockerContainer(name, uuid, outputPrinter);

        if (awaitedLogLine != null) {
            if (latch.await(waitTimeoutInMillis, TimeUnit.MILLISECONDS)) {
                return result;
            }

            try {
                result.stop();
            } catch (Exception ignored) {
                // ignored because we're about to throw another exception
            }

            throw new TimeoutException("Container '" + name + " (" + uuid + ")' didn't print '"
                    + awaitedLogLine + "' in " + waitTimeoutInMillis + " ms");
        } else {
            return result;
        }
    }
}

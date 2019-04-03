package org.wildfly.swarm.ts.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Simple container runner using "docker" command
 * 
 * @author juagonza
 *
 */
public class DockerRunner {

    private static Logger LOGGER = null;
    private String containerId;
    private String containerImage;
    private List<String> ports = new ArrayList<>();
    private Map<String, String> environmentVariables = new HashMap<>();
    private String logWait;
    private long timeoutMillis = 60000;

    public DockerRunner(String containerImage, String containerId) {
        LOGGER = Logger.getLogger(DockerRunner.class.getName());
        LOGGER.setUseParentHandlers(false);

        CustomDockerRunnerFormatter formatter = new CustomDockerRunnerFormatter();
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(formatter);

        LOGGER.addHandler(handler);
        this.containerImage = containerImage;
        this.containerId = containerId;
    }

    public DockerRunner logWait(String logWait) {
        this.logWait = logWait;
        return this;
    }

    public DockerRunner envVar(String key, String value) {
        environmentVariables.put(key, value);
        return this;
    }

    public DockerRunner port(String port) {
        this.ports.add(port);
        return this;
    }

    public DockerRunner timeoutMillis(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
        return this;
    }

    public void start() throws Exception {

        ProcessBuilder dockerStartProcessBuilder = new ProcessBuilder();

        List<String> parameters = new ArrayList<>();

        parameters.add("docker");
        parameters.add("run");

        for (String port : ports) {
            parameters.add("-p");
            parameters.add(port);
        }

        for (Entry<String, String> envVar : environmentVariables.entrySet()) {
            parameters.add("-e");
            parameters.add("\"" + envVar.getKey() + "=" + envVar.getValue() + "\"");
        }

        parameters.add("--name");
        parameters.add(containerId);

        parameters.add(containerImage);

        dockerStartProcessBuilder.redirectErrorStream(true);
        dockerStartProcessBuilder.command(parameters);

        Process process = dockerStartProcessBuilder.start();

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    LOGGER.info(line);

                    if (logWait != null && line.contains(logWait)) {
                        break;
                    }
                }
                return;
            } catch (IOException e) {
                return;
            }
        });

        executor.shutdown();

        if (!executor.awaitTermination(timeoutMillis, TimeUnit.MILLISECONDS)) {
            stop();
        }
    }

    public void stop() throws IOException, InterruptedException {

        ProcessBuilder stopProcessBuilder = new ProcessBuilder();
        stopProcessBuilder.command("docker", "stop", containerId);
        stopProcessBuilder.start().waitFor(10, TimeUnit.SECONDS);

        ProcessBuilder rmProcessBuilder = new ProcessBuilder();
        rmProcessBuilder.command("docker", "rm", containerId);
        rmProcessBuilder.start().waitFor(10, TimeUnit.SECONDS);
    }

    private class CustomDockerRunnerFormatter extends SimpleFormatter {

        @Override
        public synchronized String format(LogRecord logRecord) {
            StringBuilder builder = new StringBuilder(1000);
            builder.append(containerId + "> ");
            builder.append(logRecord.getMessage());
            builder.append("\n");
            return builder.toString();

        }

    }

}

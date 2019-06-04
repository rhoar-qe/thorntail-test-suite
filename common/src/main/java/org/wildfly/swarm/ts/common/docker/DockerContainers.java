package org.wildfly.swarm.ts.common.docker;

public class DockerContainers {
    private DockerContainers() {} // avoid instantiation

    public static Docker amq7() {
        return new Docker("amq7", "registry.access.redhat.com/amq-broker-7/amq-broker-72-openshift:1.3")
                .waitForLogLine("AMQ241004: Artemis Console available")
                .port("5672:5672") // AMQP
                .port("61616:61616") // Artemis Core, OpenWire, etc.
                .envVar("AMQ_USER", "amq")
                .envVar("AMQ_PASSWORD", "amq")
                .envVar("AMQ_QUEUES", "my-queue");
    }
}

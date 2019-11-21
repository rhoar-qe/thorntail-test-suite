package org.wildfly.swarm.ts.common.docker;

public class DockerContainers {
    private DockerContainers() {} // avoid instantiation

    public static Docker amq7() {
        return new Docker("amq7", "registry.access.redhat.com/amq-broker-7/amq-broker-72-openshift:1.3")
                .waitForLogLine("AMQ241004: Artemis Console available")
                .port("5672:5672") // AMQP
                .port("61616:61616") // Artemis Core, OpenWire, etc.
                .port("8161:8161") // web console
                .tmpfsMount("/tmp/amq-data")
                .envVar("AMQ_DATA_DIR", "/tmp/amq-data")
                .envVar("AMQ_USER", "amq")
                .envVar("AMQ_PASSWORD", "amq")
                .envVar("AMQ_QUEUES", "my-queue");
    }

    public static Docker jaeger() {
        return new Docker("jaeger", "jaegertracing/all-in-one:1.11")
                .waitForLogLine("\"Health Check state change\",\"status\":\"ready\"")
                // https://www.jaegertracing.io/docs/1.11/getting-started/
                .port("5775:5775/udp")
                .port("6831:6831/udp")
                .port("6832:6832/udp")
                .port("5778:5778")
                .port("16686:16686")
                .port("14250:14250")
                .port("14267:14267")
                .port("14268:14268")
                .port("9411:9411")
                .envVar("COLLECTOR_ZIPKIN_HTTP_PORT", "9411")
                .cmdArg("--reporter.grpc.host-port=localhost:14250");
    }

    public static Docker keycloak() {
        return new Docker("keycloak", "jboss/keycloak:" + System.getProperty("keycloak.version"))
                .waitForLogLine("WFLYSRV0025: Keycloak")
                .port("8180:8080")
                .envVar("KEYCLOAK_USER", "admin")
                .envVar("KEYCLOAK_PASSWORD", "admin");
    }
}

package org.wildfly.swarm.ts.microprofile.fault.tolerance.v10;

import org.apache.http.client.fluent.Request;
import org.awaitility.Awaitility;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.undertow.WARArchive;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@RunWith(Arquillian.class)
public class MicroProfileFaultTolerance10YamlTest {
    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(WARArchive.class)
                .addPackage(HelloService.class.getPackage())
                .addAsResource("META-INF/beans.xml")
                .addAsResource("project-defaults-async.yml", "project-defaults.yml");
    }

    @Before
    public void setup() {
        Awaitility.setDefaultPollInterval(10, TimeUnit.MILLISECONDS);
    }

    @Test
    @RunAsClient
    public void circuitBreaker() throws IOException {
        // hystrix.command.default.circuitBreaker.requestVolumeThreshold
        int initialRequestsCount = 20;

        for (int i = 0; i < initialRequestsCount; i++) {
            String response = Request.Get("http://localhost:8080/?operation=circuit-breaker&context=foobar").execute().returnContent().asString();
            assertThat(response).isEqualTo("Hello from @CircuitBreaker method, context = foobar");
        }

        // @CircuitBreaker.delay
        long circuitBreakerDelayMillis = 5000;
        long maxWaitTime = circuitBreakerDelayMillis + 1000;

        await().atMost(maxWaitTime, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            String response = Request.Get("http://localhost:8080/?operation=circuit-breaker&context=foobar" + "&fail=true").execute().returnContent().asString();
            assertThat(response).isEqualTo("Fallback Hello, context = foobar");
        });

        await().atMost(maxWaitTime, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            String response = Request.Get("http://localhost:8080/?operation=circuit-breaker&context=foobar").execute().returnContent().asString();
            assertThat(response).isEqualTo("Hello from @CircuitBreaker method, context = foobar");
        });
    }
}

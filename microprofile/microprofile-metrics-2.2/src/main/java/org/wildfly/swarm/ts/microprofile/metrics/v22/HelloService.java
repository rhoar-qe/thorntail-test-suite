package org.wildfly.swarm.ts.microprofile.metrics.v22;

import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.ConcurrentGauge;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Random;

@ApplicationScoped
public class HelloService {
    @Inject
    private MetricRegistry metrics;

    private Counter counter;

    @PostConstruct
    private void setupMetrics() {
        counter = metrics.counter(Metadata.builder()
                .withName("hello-count")
                .withType(MetricType.COUNTER)
                .withDisplayName("Hello Count")
                .withDescription("Number of hello invocations")
                .reusable(true)
                .build());
    }

    public String hello()  {
        counter.inc();
        return "Hello from programmatically counted method";
    }
}

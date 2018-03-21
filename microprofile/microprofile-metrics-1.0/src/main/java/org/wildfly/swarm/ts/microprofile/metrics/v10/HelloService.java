package org.wildfly.swarm.ts.microprofile.metrics.v10;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.enterprise.context.ApplicationScoped;
import java.util.Random;

@ApplicationScoped
public class HelloService {
    @Counted(
            monotonic = true,
            name = "hello-count",
            absolute = true,
            displayName = "Hello Count",
            description = "Number of hello invocations"
    )
    @Timed(
            unit = MetricUnits.MILLISECONDS,
            name = "hello-time",
            absolute = true,
            displayName = "Hello Time",
            description = "Time of hello invocations"
    )
    @Metered(
            name = "hello-freq",
            absolute = true,
            displayName = "Hello Freq",
            description = "Frequency of hello invocations"
    )
    public String hello() throws InterruptedException {
        Thread.sleep(new Random().nextInt(100) + 1);
        return "Hello from counted and timed and metered method";
    }
}

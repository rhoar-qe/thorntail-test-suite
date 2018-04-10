package org.wildfly.swarm.ts.hollow.jar.microprofile.health.check.v10;

import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

@Health
public class MyHealthCheck implements HealthCheck {
    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("elvis-lives").up().withData("it's", "true").build();
    }
}

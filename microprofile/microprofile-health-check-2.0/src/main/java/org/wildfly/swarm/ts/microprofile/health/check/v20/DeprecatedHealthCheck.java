package org.wildfly.swarm.ts.microprofile.health.check.v20;

import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

@Health
@SuppressWarnings("deprecation") // @Health is deprecated, but should work in v20
public class DeprecatedHealthCheck implements HealthCheck {
    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("deprecated-health").up().withData("key", "value").build();
    }
}

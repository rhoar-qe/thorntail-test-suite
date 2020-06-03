package org.wildfly.swarm.ts.microprofile.health.check.v22;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
public class SimplifiedHealthCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        Map<String, Object> data = new HashMap<>();
        data.put("key","value");
        return new HealthCheckResponse("simplified", HealthCheckResponse.State.UP, Optional.of(data));
    }
}

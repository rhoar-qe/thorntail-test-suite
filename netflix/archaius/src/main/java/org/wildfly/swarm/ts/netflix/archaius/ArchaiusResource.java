package org.wildfly.swarm.ts.netflix.archaius;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import com.netflix.hystrix.strategy.properties.HystrixProperty;

@Path("/")
public class ArchaiusResource {

    @GET
    public Response get() {
        HystrixPropertiesStrategy strategy = HystrixPlugins.getInstance().getPropertiesStrategy();

        HystrixCommandProperties defaultProps = strategy.getCommandProperties(HystrixCommandKey.Factory.asKey("default"), HystrixCommandProperties.Setter());
        HystrixProperty<Integer> response = defaultProps.circuitBreakerErrorThresholdPercentage();
        return Response.ok().entity(response.get()).build();
    }
}

package org.wildfly.swarm.ts.microprofile.fault.tolerance.v20;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;
import org.wildfly.swarm.ts.microprofile.fault.tolerance.v20.priority.AfterInterceptor;
import org.wildfly.swarm.ts.microprofile.fault.tolerance.v20.priority.BeforeInterceptor;
import org.wildfly.swarm.ts.microprofile.fault.tolerance.v20.priority.InterceptorsContext;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class PriorityOrderTest {
    @Inject
    private InterceptorsContext context;

    /**
     * Tests priority order according to annotation:
     * BeforeInterceptor with priority 4000 is before AfterInterceptor with priority 6000
     */
    @Test
    @RunAsClient
    public void basicPriority() throws IOException {
        String response = Request.Get("http://localhost:8080/priority?operation=retry&fail=false").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from method: [" + PriorityServlet.class.getSimpleName() +
                                               ", " + BeforeInterceptor.class.getSimpleName() +
                                               ", " + AfterInterceptor.class.getSimpleName() +
                                               ", Inside method]");
    }

    /**
     * Tests @Retry with priority loaded from microprofile-config.properties:
     * mp.fault.tolerance.interceptor.priority=5000
     * Expected order contains second AfterInterceptor (priority 6000 > 5000) but only one BeforeInterceptor
     */
    @Test
    @RunAsClient
    public void priorityWithRetry() throws IOException {
        String response = Request.Get("http://localhost:8080/priority?operation=retry&fail=true").execute().returnContent().asString();
        assertThat(response).isEqualTo("Fallback Hello: [" + PriorityServlet.class.getSimpleName() +
                                               ", " + BeforeInterceptor.class.getSimpleName() +
                                               ", " + AfterInterceptor.class.getSimpleName() +
                                               ", Inside method, " +
                                               AfterInterceptor.class.getSimpleName() +
                                               ", Inside method, processFallback]");
    }

    @After
    public void clearQueue() {
        if (context != null) {
            context.getOrderQueue().clear();
        }
    }
}
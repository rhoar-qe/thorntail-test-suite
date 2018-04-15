package org.wildfly.swarm.ts.javaee.ejb.passivation;

import org.apache.http.client.fluent.Request;
import org.junit.Test;
import org.wildfly.swarm.ts.common.Log;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.wildfly.swarm.ts.common.LogAssert.assertThat;

public class EjbPassivationIT {
    @Test
    public void test() throws IOException {
        for (int i = 0; i < 100; i++) {
            String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
            assertThat(response).isEqualTo("Hello from stateful EJB " + i);
        }

        for (int i = 0; i < 100; i++) {
            assertThat(Log.STDOUT).hasLine("Constructed HelloBean " + i);
        }

        // can't determine _precisely_ how many beans will be passivated -- it should be cca 95,
        // so 90 should be a good lower bound
        for (int i = 0; i < 90; i++) {
            assertThat(Log.STDOUT).hasLine("Passivating HelloBean " + i);
        }
    }
}

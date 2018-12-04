package org.wildfly.swarm.ts.wildfly.logging;

import org.apache.http.client.fluent.Request;
import org.junit.Test;
import org.wildfly.swarm.ts.common.Log;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.wildfly.swarm.ts.common.LogAssert.assertThat;

public class LoggingIT {
    @Test
    public void test() throws IOException {
        String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello");
        assertThat(Log.STDOUT).hasLine("CUSTOM LOG FORMAT INFO [stdout] Servlet reached!");
        assertThat(Log.STDOUT).hasLine("CUSTOM LOG FORMAT WARNING [HelloServlet] Servlet reached!");
        assertThat(Log.custom("target/foobar.log")).hasLine("CUSTOM LOG FORMAT INFO [stdout] Servlet reached!");
        assertThat(Log.custom("target/foobar.log")).hasLine("CUSTOM LOG FORMAT WARNING [HelloServlet] Servlet reached!");
        assertThat(Log.custom("target/access_log.log")).hasLine("CUSTOM ACCESS LOG FORMAT GET / - 200");
    }
}

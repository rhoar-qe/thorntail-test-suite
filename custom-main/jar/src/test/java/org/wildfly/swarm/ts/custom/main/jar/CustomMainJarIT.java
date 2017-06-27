package org.wildfly.swarm.ts.custom.main.jar;

import org.apache.http.client.fluent.Request;
import org.junit.Test;
import org.wildfly.swarm.ts.common.Log;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.wildfly.swarm.ts.common.LogAssert.assertThat;

public class CustomMainJarIT {
    @Test
    public void test() throws IOException {
        assertThat(Log.STDOUT).hasLine("Starting custom main in JAR");

        String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from servlet with custom main in JAR");
    }
}

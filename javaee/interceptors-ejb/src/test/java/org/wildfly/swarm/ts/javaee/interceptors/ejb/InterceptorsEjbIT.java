package org.wildfly.swarm.ts.javaee.interceptors.ejb;

import org.apache.http.client.fluent.Request;
import org.junit.Test;
import org.wildfly.swarm.ts.common.Log;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.wildfly.swarm.ts.common.LogAssert.assertThat;

public class InterceptorsEjbIT {
    @Test
    public void test() throws IOException {
        String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello, World!");
        assertThat(Log.STDOUT).hasLine("HelloService initialized");
        assertThat(Log.STDOUT).hasLine("HelloService.hello invoked");
    }
}

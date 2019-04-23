package org.wildfly.swarm.ts.hollow.jar.custom;

import org.apache.http.client.fluent.Request;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomHollowJarIT {
    @Test
    public void serviceResponds() throws IOException {
        String response = Request.Get("http://localhost:8080/rest/hello?name=World").execute().returnContent().asString();
        assertThat(response).isEqualTo("JAX-RS says: Hello, World!");
    }

    @Test
    public void warHasEmptyWebInfLib() {
        WebArchive war = ShrinkWrap.createFromZipFile(WebArchive.class, new File(System.getProperty("app.war.path")));
        Node webInfLib = war.get("WEB-INF/lib");
        assertThat(webInfLib.getAsset()).isNull();
        assertThat(webInfLib.getChildren()).isEmpty();
    }
}

package org.wildfly.swarm.ts.javaee.jaxrs.jsonp;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class JaxrsJsonpTest {
    @Test
    @RunAsClient
    public void test() throws IOException {
        String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        JsonStructure json = Json.createReader(new StringReader(response)).read();
        assertThat(json).isInstanceOf(JsonObject.class);
        assertThat(json.getValueType()).isEqualTo(JsonValue.ValueType.OBJECT);
        JsonObject jsonObj = (JsonObject) json;
        assertThat(jsonObj).containsKey("hello");
        assertThat(jsonObj.get("hello").getValueType()).isEqualTo(JsonValue.ValueType.STRING);
        assertThat(jsonObj.getString("hello")).isEqualTo("world");
    }
}

package org.wildfly.swarm.ts.microprofile.openapi.v11;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;
import org.yaml.snakeyaml.Yaml;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class MicroProfileOpenApi11Test {
    @Test
    @RunAsClient
    @InSequence(1)
    public void hitEndpoint() throws IOException {
        String response = Request.Patch("http://localhost:8080/rest/patch").execute().returnContent().asString();
        assertThat(response).isEqualTo("Patch OK");
    }

    @Test
    @RunAsClient
    @InSequence(2)
    @SuppressWarnings("unchecked")
    public void openapiDocument() throws IOException {
        Response response = Request.Get("http://localhost:8080/openapi").execute();
        String content = response.returnContent().asString();
        Yaml yaml = new Yaml();
        Object yamlObject = yaml.load(content);
        Map<String, Object> yamlMap = (Map<String, Object>) yamlObject;
        Map<String, Object> paths = (Map<String, Object>) yamlMap.get("paths");
        assertThat(paths).isNotEmpty();
        Map<String, Object> patchPath = (Map<String, Object>) paths.get("/rest/patch");
        assertThat(patchPath).isNotEmpty();
        // 1.1 the addition of the JAXRS 2.1 PATCH method
        Map<String, Object> patchMethod = (Map<String, Object>) patchPath.get("patch");
        assertThat(patchMethod).isNotEmpty();
        assertThat(patchMethod.keySet()).hasSize(7);
        // 1.1 @Content now supports a singular example field
        Map<String, Object> responses = (Map<String, Object>) patchMethod.get("responses");
        assertThat(responses).isNotEmpty();
        Map<String, Object> defaultResponse = (Map<String, Object>) responses.get("default");
        assertThat(defaultResponse).isNotEmpty();
        Map<String, Object> contentAnnotation = (Map<String, Object>) defaultResponse.get("content");
        assertThat(contentAnnotation).isNotEmpty();
        Map<String, Object> mediaType = (Map<String, Object>) contentAnnotation.get("application/json");
        assertThat(mediaType).isNotEmpty();
        assertThat(mediaType.get("example")).isEqualTo("content-example");
        // 1.1 @Extension now has a parseValue field for complex values
        assertThat(patchMethod.get("x-string-property")).isEqualTo("string-value");
        assertThat(patchMethod.get("x-boolean-property")).isEqualTo(true);
        assertThat(patchMethod.get("x-number-property")).isEqualTo(42);

        Map<String, Object> objectProperty = (Map<String, Object>) patchMethod.get("x-object-property");
        assertThat(objectProperty.get("property-1")).isEqualTo("value-1");
        assertThat(objectProperty.get("property-2")).isEqualTo("value-2");
        assertThat(((Map<String, Object>) objectProperty.get("property-3")).get("prop-3-1")).isEqualTo(42);
        assertThat(((Map<String, Object>) objectProperty.get("property-3")).get("prop-3-2")).isEqualTo(true);

        assertThat(((List<String>) patchMethod.get("x-string-array-property"))).containsAll(Arrays.asList("one", "two", "three"));

        List<Object> objectArrayProperty = (List<Object>) patchMethod.get("x-object-array-property");
        assertThat(objectArrayProperty).hasSize(2);
        assertThat(((Map<String, String>) objectArrayProperty.get(0)).get("name")).isEqualTo("item-1");
        assertThat(((Map<String, String>) objectArrayProperty.get(1)).get("name")).isEqualTo("item-2");
    }
}

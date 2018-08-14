package org.wildfly.swarm.ts.microprofile.openapi.v10;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.jaxrs.JAXRSArchive;
import org.yaml.snakeyaml.Yaml;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RunWith(Arquillian.class)
public class MicroProfileAnnotationsOpenApi10Test {

    @Deployment(testable = false)
    public static Archive<?> createDeployment() throws Exception {
        JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class, "restannotated.war");
        deployment.addClass(RestApplication.class);
        deployment.addClass(ResponsePOJO.class);
        deployment.addClass(RestAnnotatedResource.class);
        deployment.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        return deployment;
    }

    @Test
    @RunAsClient
    public void testEndpoint() throws IOException {
        String response = Request.Get("http://localhost:8080/rest/user1").
                addHeader("Accept", "application/json").execute().returnContent().asString();
        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        assertThat(json.has("attribute1")).isTrue();
        assertThat(json.get("attribute1").getAsString()).isEqualTo("user1");
    }

    @SuppressWarnings("unchecked")
    @Test
    @RunAsClient
    public void applicationMetricsAreRegisteredAtDeploymentTime() throws IOException {
        Response response = Request.Get("http://localhost:8080/openapi").execute();
        String content = response.returnContent().asString();
        Object yamlObject = new Yaml().load(content);
        Map<String, Object> yamlMap = (Map<String, Object>) yamlObject;
        Map<String, Object> paths = (Map<String, Object>) yamlMap.get("paths");
        assertThat(paths).isNotEmpty();
        Map<String, Object> restPath = (Map<String, Object>) paths.get("/rest/{pathParam}");
        assertThat(restPath).isNotEmpty();

        Map<String, Object> getMethod = (Map<String, Object>) restPath.get("get");
        assertThat(getMethod).isNotEmpty();

        assertThat(getMethod.get("responses")).isNotNull();

        Map<String, Object> responses = (Map<String, Object>) getMethod.get("responses");

        assertThat(responses.get("default")).isNotNull();

        Map<String, Object> defaultResponse = (Map<String, Object>) responses.get("default");
        assertThat(defaultResponse.get("content")).isNotNull();

        Map<String, Object> defaultContent = (Map<String, Object>) defaultResponse.get("content");
        assertThat(defaultContent.get("application/json")).isNotNull();

        Map<String, Object> jsonContent = (Map<String, Object>) defaultContent.get("application/json");
        assertThat(jsonContent.get("schema")).isNotNull();
        assertThat(yamlMap.get("components")).isNotNull();

        Map<String, Object> components = (Map<String, Object>) yamlMap.get("components");
        assertThat(components.get("schemas")).isNotNull();

        Map<String, Object> schemas = (Map<String, Object>) components.get("schemas");
        assertThat(schemas.get("ResponsePOJO")).isNotNull();

        Map<String, Object> responsePOJO = (Map<String, Object>) schemas.get("ResponsePOJO");
        assertThat(responsePOJO.get("properties")).isNotNull();

        Map<String, Object> properties = (Map<String, Object>) responsePOJO.get("properties");
        assertThat(properties.get("attribute1")).isNotNull();
        assertThat(properties.get("attribute2")).isNotNull();
    }
}
package org.wildfly.swarm.ts.microprofile.openapi.v10;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.jaxrs.JAXRSArchive;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class MicroProfileSimpleOpenApi10Test {

    @Deployment(testable = false)
    public static Archive<?> createDeployment() throws Exception {
        JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class, "restsimple.war");
        deployment.addClass(RestApplication.class);
        deployment.addClass(RestSimpleResource.class);
        deployment.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        return deployment;
    }

    @SuppressWarnings("unchecked")
    @Test
    @RunAsClient
    public void applicationMetricsAreRegisteredAtDeploymentTime() throws IOException {
        Response response = Request.Get("http://localhost:8080/openapi").execute();
        String content = response.returnContent().asString();
        Yaml yaml = new Yaml();
        Object yamlObject = yaml.load(content);
        Map<String, Object> yamlMap = (Map<String, Object>) yamlObject;
        Map<String, Object> paths = (Map<String, Object>) yamlMap.get("paths");
        assertThat(paths).isNotEmpty();
        Map<String, Object> restPath = (Map<String, Object>) paths.get("/rest/{pathParam}");
        assertThat(restPath).isNotEmpty();

        Map<String, Object> getMethod = (Map<String, Object>) restPath.get("get");
        assertThat(getMethod).isNotEmpty();

        List<Object> parameters = (List<Object>) getMethod.get("parameters");
        assertThat(parameters.size()).isEqualTo(2);

        Map<String, Object> pathParam = (Map<String, Object>) parameters.get(0);
        assertThat(pathParam).isNotEmpty();
        assertThat(pathParam.get("name")).isEqualTo("pathParam");
        assertThat(pathParam.get("in")).isEqualTo("path");

        Map<String, Object> queryParam = (Map<String, Object>) parameters.get(1);
        assertThat(queryParam).isNotEmpty();
        assertThat(queryParam.get("name")).isEqualTo("queryParam");
        assertThat(queryParam.get("in")).isEqualTo("query");
    }
}

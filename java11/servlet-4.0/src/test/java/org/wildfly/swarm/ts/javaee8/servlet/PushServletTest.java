package org.wildfly.swarm.ts.javaee8.servlet;

import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.ssl.SSLContexts;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import static java.net.http.HttpClient.Version.HTTP_2;
import static org.assertj.core.api.Assertions.assertThat;


@RunWith(Arquillian.class)
public class PushServletTest {
    private static final String EXPECTED_SUCCESS = "<html><head><title>Servlet 4.0</title>" +
            "<link rel=\"stylesheet\" href=\"css/style.css\"/>" +
            "</head><body><p>secure</p><p>pushing succeeded</p></body></html>";

    private static final String EXPECTED_FAILURE = "<html><head><title>Servlet 4.0</title>" +
            "<link rel=\"stylesheet\" href=\"css/style.css\"/>" +
            "</head><body><p>secure</p><p>pushing failed</p></body></html>";

    // can't use `@DefaultDeployment` because content of `src/main/webapp` is never copied
    // to `target/[test-]classes`, and that's what @DefaultDeployment puts into the archive
    //
    // also the `MappingServlet` must not be present in the deployment, otherwise the application
    // wouldn't be able to serve `css/style.css`
    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClass(PushServlet.class)
                .addAsWebResource(new File("src/main/webapp/css/style.css"), "css/style.css")
                .addAsResource(new ClassLoaderAsset("project-defaults.yml"), "project-defaults.yml");
    }

    @Test
    @RunAsClient
    public void serverPushSucceeds() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException, ExecutionException, InterruptedException {
        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(TrustAllStrategy.INSTANCE)
                .build();
        HttpClient client = HttpClient.newBuilder()
                .version(HTTP_2)
                .sslContext(sslContext)
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://localhost:8443/PushServlet"))
                .build();


        ConcurrentMap<HttpRequest, CompletableFuture<HttpResponse<String>>> pushes = new ConcurrentHashMap<>();

        HttpResponse<String> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString(),
                HttpResponse.PushPromiseHandler.of(ignored -> HttpResponse.BodyHandlers.ofString(), pushes)).get();
        assertThat(response.body()).isEqualTo(EXPECTED_SUCCESS);

        assertThat(pushes).hasSize(1);
        Map.Entry<HttpRequest, CompletableFuture<HttpResponse<String>>> push = pushes.entrySet().iterator().next();
        assertThat(push.getKey().uri()).hasPath("/css/style.css");
        assertThat(push.getValue().get().body()).contains("background-color: red");
    }

    @Test
    @RunAsClient
    public void serverPushFails() throws Exception {
        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(TrustAllStrategy.INSTANCE)
                .build();
        HttpClient client = HttpClient.newBuilder()
                .version(HTTP_2)
                .sslContext(sslContext)
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://localhost:8443/PushServlet?forceFail=true"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        assertThat(body).isEqualTo(EXPECTED_FAILURE);
    }
}

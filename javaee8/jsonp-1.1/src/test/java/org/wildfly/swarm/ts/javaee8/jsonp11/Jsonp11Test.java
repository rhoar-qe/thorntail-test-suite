package org.wildfly.swarm.ts.javaee8.jsonp11;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class Jsonp11Test {
    private static final String URL = "http://localhost:8080/jsonp";

    // JSON Pointer

    @Test
    @RunAsClient
    public void jsonPointer_containsValue() throws IOException {
        String response = Request.Get(URL + "/pointer?operation=contains-value&value=name").execute().returnContent().asString();
        assertThat(response).isEqualTo("true");
        response = Request.Get(URL + "/pointer?operation=contains-value&value=NONSENSE").execute().returnContent().asString();
        assertThat(response).isEqualTo("false");
    }

    @Test
    @RunAsClient
    public void jsonPointer_getValue() throws IOException {
        String response = Request.Get(URL + "/pointer?operation=get-value&value=name").execute().returnContent().asString();
        assertThat(response).isEqualTo("\"Joe Black\"");
        response = Request.Get(URL + "/pointer?operation=get-value&value=NONSENSE").execute().returnContent().asString();
        assertThat(response).isEqualTo("Non-existing name/value pair in the object for key NONSENSE");
    }

    @Test
    @RunAsClient
    public void jsonPointer_add() throws IOException {
        String response = Request.Get(URL + "/pointer?operation=add&path=children&value=none").execute().returnContent().asString();
        assertThat(response).isEqualTo("{\"name\":\"Joe Black\",\"age\":35,\"children\":\"none\"}");
    }

    @Test
    @RunAsClient
    public void jsonPointer_replace() throws IOException {
        String response = Request.Get(URL + "/pointer?operation=replace&path=name&value=Alice").execute().returnContent().asString();
        assertThat(response).isEqualTo("{\"name\":\"Alice\",\"age\":35}");
        response = Request.Get(URL + "/pointer?operation=replace&path=NONSENSE&value=Alice").execute().returnContent().asString();
        assertThat(response).isEqualTo("Non-existing name/value pair in the object for key NONSENSE");
    }

    @Test
    @RunAsClient
    public void jsonPointer_remove() throws IOException {
        String response = Request.Get(URL + "/pointer?operation=remove&path=name").execute().returnContent().asString();
        assertThat(response).isEqualTo("{\"age\":35}");
        response = Request.Get(URL + "/pointer?operation=remove&path=NONSENSE").execute().returnContent().asString();
        assertThat(response).isEqualTo("Non-existing name/value pair in the object for key NONSENSE");
    }

    // JSON Patch

    @Test
    @RunAsClient
    public void jsonPatch_add() throws IOException {
        String response = Request.Get(URL + "/patch?operation=add&path=children&value=42").execute().returnContent().asString();
        assertThat(response).isEqualTo("{\"name\":\"Joe Black\",\"age\":35,\"children\":\"42\"}");
    }

    @Test
    @RunAsClient
    public void jsonPatch_remove() throws IOException {
        String response = Request.Get(URL + "/patch?operation=remove&path=name").execute().returnContent().asString();
        assertThat(response).isEqualTo("{\"age\":35}");
        response = Request.Get(URL + "/patch?operation=remove&path=NONSENSE").execute().returnContent().asString();
        assertThat(response).isEqualTo("Non-existing name/value pair in the object for key NONSENSE");
    }

    @Test
    @RunAsClient
    public void jsonPatch_move() throws IOException {
        String response = Request.Get(URL + "/patch?operation=move&path=name&value=age").execute().returnContent().asString();
        assertThat(response).isEqualTo("{\"name\":35}");
        response = Request.Get(URL + "/patch?operation=move&path=name&value=NONSENSE").execute().returnContent().asString();
        assertThat(response).isEqualTo("The '/NONSENSE' path of the patch operation 'move' does not exist in target object");
    }

    @Test
    @RunAsClient
    public void jsonPatch_copy() throws IOException {
        String response = Request.Get(URL + "/patch?operation=copy&path=name&value=age").execute().returnContent().asString();
        assertThat(response).isEqualTo("{\"name\":35,\"age\":35}");
        response = Request.Get(URL + "/patch?operation=copy&path=name&value=NONSENSE").execute().returnContent().asString();
        assertThat(response).isEqualTo("Non-existing name/value pair in the object for key NONSENSE");
    }

    @Test
    @RunAsClient
    public void jsonPatch_replace() throws IOException {
        String response = Request.Get(URL + "/patch?operation=replace&path=name&value=Eva").execute().returnContent().asString();
        assertThat(response).isEqualTo("{\"name\":\"Eva\",\"age\":35}");
        response = Request.Get(URL + "/patch?operation=replace&path=NONSENSE&value=Eva").execute().returnContent().asString();
        assertThat(response).isEqualTo("Non-existing name/value pair in the object for key NONSENSE");
    }

    // JSON Merge

    @Test
    @RunAsClient
    public void jsonMerge_minimalPatch() throws IOException {
        String response = Request.Get(URL + "/merge?feature=merge-patch-minimal").execute().returnContent().asString();
        assertThat(response).isEqualTo("{\"name\":\"Alice\",\"age\":35,\"enabled\":true}");
    }

    @Test
    @RunAsClient
    public void jsonMerge_normalPatch() throws IOException {
        String response = Request.Get(URL + "/merge?feature=merge-patch-normal").execute().returnContent().asString();
        assertThat(response).isEqualTo("{\"name\":\"Alice\",\"age\":35,\"enabled\":true}");
    }

    @Test
    @RunAsClient
    public void jsonMerge_diff() throws IOException {
        String response = Request.Get(URL + "/merge?feature=merge-diff").execute().returnContent().asString();
        assertThat(response).isEqualTo("{\"name\":\"Alice\",\"enabled\":true}");
    }

    // JSON Collector

    @Test
    @RunAsClient
    public void jsonCollector() throws IOException {
        String response = Request.Get(URL + "/collector").execute().returnContent().asString();
        assertThat(response).isEqualTo("[{\"key1\":\"value1\"},{\"key2\":\"value2\"},{\"key3\":\"value3\"},{\"key4\":\"value4\"},{\"key5\":\"value5\"}]");
    }
}

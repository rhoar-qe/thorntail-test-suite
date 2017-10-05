package org.wildfly.swarm.ts.netflix.ribbon.secured;

import java.nio.charset.StandardCharsets;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.wildfly.swarm.netflix.ribbon.secured.client.SecuredRibbonResourceFactory;

import com.netflix.ribbon.http.HttpRequestTemplate;
import com.netflix.ribbon.http.HttpRequestTemplate.Builder;
import com.netflix.ribbon.http.HttpResourceGroup;

import io.netty.buffer.ByteBuf;

@Path("/")
public class RibbonSecuredClientResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/test")
    public Response test(TokenDTO tokenDTO) {
        HttpResourceGroup group = SecuredRibbonResourceFactory.INSTANCE.createHttpResourceGroup("ts-netflix-ribbon-secured");

        Builder<ByteBuf> builder = group.newTemplateBuilder("template")
                .withMethod("GET")
                .withUriTemplate("/protected")
                .withHeader("Authorization", "Bearer " + tokenDTO.getToken())
                .withFallbackProvider(new RibbonTestFallbackHandler());

        HttpRequestTemplate<ByteBuf> template = builder.build();

        ByteBuf response = template.requestBuilder().build().execute();
        return Response.ok().entity(response.toString(StandardCharsets.UTF_8)).build();
    }
}

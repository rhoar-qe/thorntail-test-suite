package org.wildfly.swarm.ts.microprofile.rest.client.v13.mediatype;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class ResourceMediaType {
    public static final String GREETINGS_TEXT = "Greetings!";

    public static final String GREETINGS_JSON = "{\"data\": \"Greetings!\"}";

    public static final String GREETINGS_XML = "<data>Greetings!</data>";

    // @Produces

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/greetings-text")
    public String greetingsText() {
        return GREETINGS_TEXT;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/greetings-json")
    public String greetingsJson() {
        return GREETINGS_JSON;
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/greetings-xml")
    public String greetingsXml() {
        return GREETINGS_XML;
    }

    // @Consumes

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/consume-text")
    public String consumeText(String content) {
        return content;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/consume-json")
    public String consumeJson(String content) {
        return content;
    }
}

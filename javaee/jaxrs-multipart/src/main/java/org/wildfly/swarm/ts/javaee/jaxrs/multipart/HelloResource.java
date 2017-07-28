package org.wildfly.swarm.ts.javaee.jaxrs.multipart;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class HelloResource {
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String formRequest(@MultipartForm MyForm form) {
        return "OK: " + form.text + " " + form.binary;
    }

    @GET
    @Produces(MediaType.MULTIPART_FORM_DATA)
    @MultipartForm
    public MyForm formResponse() {
        MyForm result = new MyForm();
        result.text = "Hello, World!";
        result.binary = "MULTIPART!";
        return result;
    }
}

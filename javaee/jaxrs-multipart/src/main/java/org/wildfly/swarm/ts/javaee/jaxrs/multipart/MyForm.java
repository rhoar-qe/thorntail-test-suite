package org.wildfly.swarm.ts.javaee.jaxrs.multipart;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

public class MyForm {
    @FormParam("text")
    @PartType(MediaType.TEXT_PLAIN)
    String text;

    @FormParam("binary")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    String binary;
}

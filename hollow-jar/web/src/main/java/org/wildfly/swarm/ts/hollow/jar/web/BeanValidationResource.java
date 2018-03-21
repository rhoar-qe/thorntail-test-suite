package org.wildfly.swarm.ts.hollow.jar.web;

import javax.validation.constraints.Size;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/beanvalidation")
public class BeanValidationResource {

    @GET
    public String validateParamAndReturnValue(@QueryParam("param") @Size(min = 10) String param) {
        return param + " " + param;
    }
}

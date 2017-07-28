package org.wildfly.swarm.ts.javaee.jaxrs.bean.validation;

import javax.validation.constraints.Size;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/")
public class HelloResource {
    @GET
    @Size(min = 10)
    public String validateParamAndReturnValue(@QueryParam("param") @Size(max = 10) String param) {
        return param + " " + param;
    }
}

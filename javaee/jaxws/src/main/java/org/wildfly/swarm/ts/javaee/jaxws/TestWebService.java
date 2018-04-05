package org.wildfly.swarm.ts.javaee.jaxws;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public class TestWebService {
    @WebMethod
    public String webMethod(String text) {
        return "Hello " + text;
    }
}

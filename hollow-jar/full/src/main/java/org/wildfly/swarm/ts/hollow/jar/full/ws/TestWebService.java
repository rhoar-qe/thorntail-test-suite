package org.wildfly.swarm.ts.hollow.jar.full.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public class TestWebService {
    @WebMethod
    public String webMethod(String text) {
        return "Hello " + text;
    }
}

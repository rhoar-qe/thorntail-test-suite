package org.wildfly.swarm.ts.microprofile.openapi.v10;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
public class MyResponse implements Serializable {
    private String attribute1;

    private String attribute2;

    public MyResponse(String attribute1, String attribute2) {
        this.attribute1 = attribute1;
        this.attribute2 = attribute2;
    }

    public String getAttribute1() {
        return attribute1;
    }

    public String getAttribute2() {
        return attribute2;
    }
}

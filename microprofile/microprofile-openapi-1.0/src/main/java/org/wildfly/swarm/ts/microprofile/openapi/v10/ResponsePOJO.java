package org.wildfly.swarm.ts.microprofile.openapi.v10;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ResponsePOJO implements Serializable{

    private String attribute1;
    
    private String attribute2;

    public ResponsePOJO(String attribute1, String attribute2) {
        super();
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

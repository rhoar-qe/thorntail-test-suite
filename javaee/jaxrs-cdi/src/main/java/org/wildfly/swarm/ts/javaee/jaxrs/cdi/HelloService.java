package org.wildfly.swarm.ts.javaee.jaxrs.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@ApplicationScoped
public class HelloService {
    public String hello() {
        return "Hello, World!";
    }
}

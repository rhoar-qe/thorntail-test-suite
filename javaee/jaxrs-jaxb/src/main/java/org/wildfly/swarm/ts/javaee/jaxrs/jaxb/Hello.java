package org.wildfly.swarm.ts.javaee.jaxrs.jaxb;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "hello-element")
public class Hello {
    @XmlAttribute(name = "value-attribute")
    private String value1;

    @XmlElement(name = "value-element")
    private String value2;

    // only for JAXB
    public Hello() {
    }

    public Hello(String value) {
        this.value1 = value;
        this.value2 = value;
    }
}

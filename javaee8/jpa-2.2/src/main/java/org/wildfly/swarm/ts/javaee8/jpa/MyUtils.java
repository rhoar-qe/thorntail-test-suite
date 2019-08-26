package org.wildfly.swarm.ts.javaee8.jpa;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MyUtils {
    public String myAttributeToString(MyAttribute attribute) {
        if (attribute == null) return null;
        return attribute.getFirst() + ":" + attribute.getLast();
    }

    public MyAttribute stringToMyAttribute(String str) {
        String[] split = str.split(":");
        return new MyAttribute(split[0], split[1]);
    }
}

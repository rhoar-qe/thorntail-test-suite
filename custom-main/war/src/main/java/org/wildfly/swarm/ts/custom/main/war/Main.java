package org.wildfly.swarm.ts.custom.main.war;

import org.wildfly.swarm.Swarm;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting custom main in WAR");

        new Swarm().start().deploy();
    }
}

package org.wildfly.swarm.ts.custom.main.jar;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.undertow.WARArchive;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting custom main in JAR");

        Swarm swarm = new Swarm();
        WARArchive war = ShrinkWrap.create(WARArchive.class)
                .addClass(HelloServlet.class);
        swarm.start().deploy(war);
    }
}

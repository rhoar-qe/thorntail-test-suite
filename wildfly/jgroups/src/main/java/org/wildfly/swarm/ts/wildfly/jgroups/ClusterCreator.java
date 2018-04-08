package org.wildfly.swarm.ts.wildfly.jgroups;

import org.jgroups.JChannel;

import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup
public class ClusterCreator {
    @Resource(lookup = "java:jboss/jgroups/channel/swarm-jgroups")
    private JChannel channel;
}

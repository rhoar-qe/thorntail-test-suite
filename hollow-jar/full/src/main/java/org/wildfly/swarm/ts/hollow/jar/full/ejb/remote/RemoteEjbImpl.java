package org.wildfly.swarm.ts.hollow.jar.full.ejb.remote;

import javax.ejb.Remote;
import javax.ejb.Stateless;

@Remote
@Stateless
public class RemoteEjbImpl implements RemoteEjb {
    @Override
    public String method() {
        return "remote ejb method";
    }
}

package org.wildfly.swarm.ts.hollow.jar.microprofile;

import org.eclipse.microprofile.auth.LoginConfig;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/")
@LoginConfig(authMethod = "MP-JWT", realmName = "test-realm")
public class RestApplication extends Application {
}

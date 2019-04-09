package org.wildfly.swarm.ts.microprofile.jwt.v11;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.eclipse.microprofile.auth.LoginConfig;

@LoginConfig(authMethod = "MP-JWT", realmName = "test-realm")
@ApplicationPath("/")
public class RestApplication extends Application {
}

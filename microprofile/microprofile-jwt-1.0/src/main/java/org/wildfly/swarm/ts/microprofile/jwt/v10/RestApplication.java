package org.wildfly.swarm.ts.microprofile.jwt.v10;

import org.eclipse.microprofile.auth.LoginConfig;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@LoginConfig(authMethod = "MP-JWT", realmName = "test-realm")
@ApplicationPath("/")
public class RestApplication extends Application {
}

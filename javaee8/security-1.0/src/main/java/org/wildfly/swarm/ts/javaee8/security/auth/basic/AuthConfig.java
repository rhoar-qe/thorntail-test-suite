package org.wildfly.swarm.ts.javaee8.security.auth.basic;

import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.authentication.mechanism.http.BasicAuthenticationMechanismDefinition;

@BasicAuthenticationMechanismDefinition(realmName = "default")
@ApplicationScoped
public class AuthConfig {
}

package org.wildfly.swarm.ts.javaee8.security.auth.form;

import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.authentication.mechanism.http.CustomFormAuthenticationMechanismDefinition;
import javax.security.enterprise.authentication.mechanism.http.LoginToContinue;

@CustomFormAuthenticationMechanismDefinition(
        loginToContinue = @LoginToContinue(loginPage = "/login.html", useForwardToLogin = false)
)
@ApplicationScoped
public class AuthConfig {
}

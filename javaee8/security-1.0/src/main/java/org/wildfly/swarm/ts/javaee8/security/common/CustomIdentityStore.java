package org.wildfly.swarm.ts.javaee8.security.common;

import com.google.common.collect.ImmutableMap;

import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class CustomIdentityStore implements IdentityStore {
    private Map<String, String> emailsToPasswords = ImmutableMap.<String, String>builder()
            .put("admin@example.com", "adminPassword")
            .put("user@example.com", "userPassword")
            .build();

    private Map<String, Set<String>> emailsToRoles = ImmutableMap.<String, Set<String>>builder()
            .put("admin@example.com", Collections.singleton("ADMIN"))
            .put("user@example.com", Collections.singleton("USER"))
            .build();

    public CredentialValidationResult validate(UsernamePasswordCredential credential) {
        String email = credential.getCaller();
        String password = credential.getPasswordAsString();
        if (emailsToPasswords.containsKey(email) && emailsToPasswords.get(email).equals(password)) {
            return new CredentialValidationResult(email.substring(0, email.indexOf('@')),
                    emailsToRoles.get(email));
        }
        return CredentialValidationResult.NOT_VALIDATED_RESULT;
    }
}

package org.wildfly.swarm.ts.javaee8.bean.validation.services;

import org.wildfly.swarm.ts.javaee8.bean.validation.entities.CustomEmailEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class CustomEmailService {
    private List<CustomEmailEntity> emails = new ArrayList<>();

    public List<CustomEmailEntity> getEmails() {
        return emails;
    }

    @Valid
    public CustomEmailEntity create(
            @Email(regexp = "^(.+)@(.+)\\..{1,3}$", message = "invalid email format") String customEmail
    ) {
        CustomEmailEntity customEmailEntity = new CustomEmailEntity(customEmail);
        emails.add(customEmailEntity);
        return customEmailEntity;
    }
}

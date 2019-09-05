package org.wildfly.swarm.ts.javaee8.bean.validation.services;

import org.wildfly.swarm.ts.javaee8.bean.validation.entities.EmailEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class EmailService {
    private List<EmailEntity> emails = new ArrayList<>();

    public List<EmailEntity> getEmails() {
        return emails;
    }

    @Valid
    public EmailEntity create(@Email(message = "invalid email format") String email) {
        EmailEntity emailEntity = new EmailEntity(email);
        emails.add(emailEntity);
        return emailEntity;
    }
}

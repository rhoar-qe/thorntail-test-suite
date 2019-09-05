package org.wildfly.swarm.ts.javaee8.bean.validation.entities;

import javax.validation.constraints.Email;

public class EmailEntity {
    @Email(message = "invalid email format")
    private String email;

    public EmailEntity(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}

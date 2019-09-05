package org.wildfly.swarm.ts.javaee8.bean.validation.entities;

import javax.validation.constraints.Email;

public class CustomEmailEntity {
    @Email(regexp = "^(.+)@(.+)\\..{1,3}$", message = "invalid email format")
    private String customEmail;

    public CustomEmailEntity(String customEmail) {
        this.customEmail = customEmail;
    }

    public String getCustomEmail() {
        return customEmail;
    }
}

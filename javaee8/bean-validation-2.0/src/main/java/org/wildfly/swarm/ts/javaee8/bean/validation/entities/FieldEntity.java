package org.wildfly.swarm.ts.javaee8.bean.validation.entities;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

public class FieldEntity {
    @NotEmpty(message = "cannot be empty")
    private String notEmpty;

    @NotBlank(message = "cannot be blank")
    private String notBlank;

    public String getNotEmpty() {
        return notEmpty;
    }

    public String getNotBlank() {
        return notBlank;
    }

    public FieldEntity(String notEmpty, String notBlank) {
        this.notEmpty = notEmpty;
        this.notBlank = notBlank;
    }
}

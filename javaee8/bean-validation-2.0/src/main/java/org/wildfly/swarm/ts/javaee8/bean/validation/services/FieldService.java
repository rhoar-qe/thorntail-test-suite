package org.wildfly.swarm.ts.javaee8.bean.validation.services;

import org.wildfly.swarm.ts.javaee8.bean.validation.entities.FieldEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class FieldService {
    private List<FieldEntity> fields = new ArrayList<>();

    public List<FieldEntity> getFields() {
        return fields;
    }

    @Valid
    public FieldEntity create(@NotEmpty(message = "cannot be empty") String notEmpty,
                              @NotBlank(message = "cannot be blank") String notBlank
    ) {
        FieldEntity fieldEntity = new FieldEntity(notEmpty, notBlank);
        fields.add(fieldEntity);
        return fieldEntity;
    }
}

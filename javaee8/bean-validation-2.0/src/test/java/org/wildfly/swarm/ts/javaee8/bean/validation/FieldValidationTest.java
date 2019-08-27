package org.wildfly.swarm.ts.javaee8.bean.validation;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;
import org.wildfly.swarm.ts.javaee8.bean.validation.entities.FieldEntity;
import org.wildfly.swarm.ts.javaee8.bean.validation.services.FieldService;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(Arquillian.class)
@DefaultDeployment(type = DefaultDeployment.Type.JAR)
public class FieldValidationTest {
    @Inject
    private FieldService service;

    @Test
    @InSequence(1)
    public void valid() {
        FieldEntity fieldEntity = service.create("word", "word");
        assertThat(service.getFields().size()).isEqualTo(1);
    }

    @Test
    @InSequence(2)
    public void invalidEmpty() {
        try {
            service.create("", "word");
            fail();
        } catch (ConstraintViolationException ex) {
            assertThat(ex.getMessage()).contains("cannot be empty");
        }
    }

    @Test
    @InSequence(3)
    public void invalidBlank() {
        try {
            service.create("word", "        ");
            fail();
        } catch (ConstraintViolationException ex) {
            assertThat(ex.getMessage()).contains("cannot be blank");
        }
    }
}

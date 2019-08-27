package org.wildfly.swarm.ts.javaee8.bean.validation;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;
import org.wildfly.swarm.ts.javaee8.bean.validation.entities.CustomEmailEntity;
import org.wildfly.swarm.ts.javaee8.bean.validation.services.CustomEmailService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(Arquillian.class)
@DefaultDeployment(type = DefaultDeployment.Type.JAR)
public class CustomEmailValidationTest {
    @Inject
    private CustomEmailService service;

    @Test
    @InSequence(1)
    public void empty() {
        assertThat(service.getEmails()).isEmpty();
    }

    @Test
    @InSequence(2)
    public void valid() {
        checkValid("foobar@example.com");
        assertThat(service.getEmails().size()).isEqualTo(1);
    }

    @Test
    @InSequence(3)
    public void invalid() {
        checkInvalid("foobar@example.toolong");
        checkInvalid("foobar@nodot");
    }

    private void checkValid(String email) {
        CustomEmailEntity customEmailEntity = service.create(email);
        assertThat(customEmailEntity.getCustomEmail()).isEqualTo(email);
    }

    private void checkInvalid(String email) {
        try {
            service.create(email);
            fail();
        } catch (ConstraintViolationException ex) {
            assertThat(ex.getMessage()).contains("invalid email format");
        }
    }
}

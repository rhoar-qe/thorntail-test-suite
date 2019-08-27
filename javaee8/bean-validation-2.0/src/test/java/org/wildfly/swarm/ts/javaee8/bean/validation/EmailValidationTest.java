package org.wildfly.swarm.ts.javaee8.bean.validation;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;
import org.wildfly.swarm.ts.javaee8.bean.validation.entities.EmailEntity;
import org.wildfly.swarm.ts.javaee8.bean.validation.services.EmailService;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(Arquillian.class)
@DefaultDeployment(type = DefaultDeployment.Type.JAR)
public class EmailValidationTest {
    private static final List<String> VALID_EMAILS = Arrays.asList(
            "email@example.com",
            "firstname.lastname@example.com",
            "email@subdomain.example.com",
            "firstname+lastname@example.com",
            "email@198.51.100.0",
            "email@[198.51.100.0]",
            "\"email\"@example.com",
            "1234567890@example.com",
            "email@example-one.com",
            "_______@example.com",
            "email@example.name",
            "email@example.co.jp",
            "firstname-lastname@example.com"
    );

    private static final List<String> INVALID_EMAILS = Arrays.asList(
            "plainaddress",
            "#@%^%#$@#$@#.com",
            "@example.com ",
            "Joe Smith <email@example.com>",
            "email.example.com",
            "email@domain@example.com",
            ".email@example.com",
            "email.@example.com",
            "email..email@example.com ",
            "email@example.com (Joe Smith)",
            "email@-example.com",
            "email@example..com"
    );

    @Inject
    private EmailService service;

    @Test
    @InSequence(1)
    public void empty() {
        assertThat(service.getEmails()).isEmpty();
    }

    @Test
    @InSequence(2)
    public void valid() {
        VALID_EMAILS.forEach(this::checkValid);
        assertThat(service.getEmails().size()).isEqualTo(VALID_EMAILS.size());
    }

    @Test
    @InSequence(3)
    public void invalid() {
        INVALID_EMAILS.forEach(this::checkInvalid);
    }

    private void checkValid(String email) {
        EmailEntity emailEntity = service.create(email);
        assertThat(emailEntity.getEmail()).isEqualTo(email);
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

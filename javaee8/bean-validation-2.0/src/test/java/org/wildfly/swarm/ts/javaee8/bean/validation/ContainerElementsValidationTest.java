package org.wildfly.swarm.ts.javaee8.bean.validation;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;
import org.wildfly.swarm.ts.javaee8.bean.validation.entities.EmailEntity;
import org.wildfly.swarm.ts.javaee8.bean.validation.services.ContainerService;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(Arquillian.class)
@DefaultDeployment(type = DefaultDeployment.Type.JAR)
public class ContainerElementsValidationTest {
    @Inject
    private ContainerService service;

    @Test
    @InSequence(1)
    public void valid() {
        service.add(1, new EmailEntity("bla@example.com"));
        assertThat(service.getMap().size()).isEqualTo(1);
    }

    @Test
    @InSequence(2)
    public void invalid() {
        try {
            service.add(3, new EmailEntity("word"));
        } catch (ConstraintViolationException ex) {
            assertThat(ex.getMessage()).contains("email");
        }
    }

    @Test
    @InSequence(3)
    public void validNested() {
        List<EmailEntity> emails = new ArrayList<>();
        emails.add(new EmailEntity("foobar@example.com"));
        service.add(1, emails);
        assertThat(service.getNestedMap().size()).isEqualTo(1);
    }

    @Test
    @InSequence(4)
    public void invalidNested() {
        List<EmailEntity> emails = new ArrayList<>();
        emails.add(new EmailEntity("foobar@"));
        try {
            service.add(1, emails);
            fail();
        } catch (ConstraintViolationException ex) {
            assertThat(ex.getMessage()).contains("email");
        }
        assertThat(service.getNestedMap().size()).isEqualTo(1);
    }
}

package org.wildfly.swarm.ts.javaee8.bean.validation;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;
import org.wildfly.swarm.ts.javaee8.bean.validation.services.OptionalService;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(Arquillian.class)
@DefaultDeployment(type = DefaultDeployment.Type.JAR)
public class OptionalValidationTest {
    @Inject
    private OptionalService service;

    @Test
    @InSequence(1)
    public void empty() {
        assertThat(service.getOptionalNumber()).isEqualTo(Optional.empty());
    }

    @Test
    @InSequence(2)
    public void valid() {
        service.setOptionalNumber(Optional.of(5));
        assertThat(service.getOptionalNumber()).isPresent().hasValue(5);
    }

    @Test
    @InSequence(3)
    public void invalid() {
        try {
            service.setOptionalNumber(Optional.of(30));
            fail();
        } catch (ConstraintViolationException ex) {
            assertThat(ex.getMessage()).contains("too big");
        }
    }

    @Test
    @InSequence(4)
    public void emptyAgain() {
        service.setOptionalNumber(Optional.empty());
        assertThat(service.getOptionalNumber()).isEqualTo(Optional.empty());
    }
}

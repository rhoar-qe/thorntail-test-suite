package org.wildfly.swarm.ts.javaee8.bean.validation;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;
import org.wildfly.swarm.ts.javaee8.bean.validation.entities.NumberEntity;
import org.wildfly.swarm.ts.javaee8.bean.validation.services.NumberService;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(Arquillian.class)
@DefaultDeployment(type = DefaultDeployment.Type.JAR)
public class NumberValidationTest {
    @Inject
    private NumberService service;

    @Test
    @InSequence(1)
    public void isEmpty() {
        assertThat(service.getNumbers()).isEmpty();
    }

    @Test
    @InSequence(2)
    public void valid() {
        NumberEntity numberEntity = service.create(1, -1, 0, 0);
        assertThat(numberEntity.getPositive()).isEqualTo(1);
        assertThat(numberEntity.getNegative()).isEqualTo(-1);
        assertThat(numberEntity.getPositiveOrZero()).isEqualTo(0);
        assertThat(numberEntity.getNegativeOrZero()).isEqualTo(0);
        assertThat(service.getNumbers().size()).isEqualTo(1);
    }

    @Test
    @InSequence(3)
    public void invalidPositive() {
        try {
            service.create(0, -1, 0, 0);
            fail();
        } catch (ConstraintViolationException ex) {
            assertThat(ex.getMessage()).contains("must be positive");
        }
    }

    @Test
    @InSequence(4)
    public void invalidNegative() {
        try {
            service.create(1, 0, 0, 0);
            fail();
        } catch (ConstraintViolationException ex) {
            assertThat(ex.getMessage()).contains("must be negative");
        }
    }

    @Test
    @InSequence(5)
    public void invalidPositiveOrZero() {
        try {
            service.create(1, -1, -1, 0);
            fail();
        } catch (ConstraintViolationException ex) {
            assertThat(ex.getMessage()).contains("must be positive or zero");
        }
    }

    @Test
    @InSequence(6)
    public void invalidNegativeOrZero() {
        try {
            service.create(1, -1, 0, 1);
            fail();
        } catch (ConstraintViolationException ex) {
            assertThat(ex.getMessage()).contains("must be negative or zero");
        }
    }
}

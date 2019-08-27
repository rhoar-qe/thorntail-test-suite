package org.wildfly.swarm.ts.javaee8.bean.validation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;
import org.wildfly.swarm.ts.javaee8.bean.validation.entities.DateEntity;
import org.wildfly.swarm.ts.javaee8.bean.validation.services.DateService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(Arquillian.class)
@DefaultDeployment(type = DefaultDeployment.Type.JAR)
public class DateValidationTest {
    private static final LocalDate PAST = LocalDate.now().minus(10, ChronoUnit.DAYS);

    private static final LocalDate PRESENT = LocalDate.now();

    private static final LocalDate FUTURE = LocalDate.now().plus(10, ChronoUnit.DAYS);

    @Inject
    private DateService service;

    @Test
    @InSequence(1)
    public void testEmpty() {
        assertThat(service.getDates()).isEmpty();
    }

    @Test
    @InSequence(2)
    public void valid() {
        DateEntity dateEntity = service.create(PAST, FUTURE, PRESENT, PRESENT);
        assertThat(dateEntity.getPastDate()).isEqualTo(PAST);
        assertThat(dateEntity.getFutureDate()).isEqualTo(FUTURE);
        assertThat(dateEntity.getPastOrPresentDate()).isEqualTo(PRESENT);
        assertThat(dateEntity.getFutureOrPresentDate()).isEqualTo(PRESENT);
        assertThat(service.getDates().size()).isEqualTo(1);
    }

    @Test
    @InSequence(3)
    public void invalidPast() {
        try {
            service.create(PRESENT, FUTURE, PRESENT, PRESENT);
            fail();
        } catch (ConstraintViolationException ex) {
            assertThat(ex.getMessage()).contains("must be past");
        }
    }

    @Test
    @InSequence(4)
    public void invalidFuture() {
        try {
            service.create(PAST, PRESENT, PRESENT, PRESENT);
            fail();
        } catch (ConstraintViolationException ex) {
            assertThat(ex.getMessage()).contains("must be future");
        }
    }

    @Test
    @InSequence(5)
    public void invalidPastOrPresent() {
        try {
            service.create(PAST, FUTURE, FUTURE, PRESENT);
            fail();
        } catch (ConstraintViolationException ex) {
            assertThat(ex.getMessage()).contains("must be past or present");
        }
    }

    @Test
    @InSequence(6)
    public void invalidFutureOrPresent() {
        try {
            service.create(PAST, FUTURE, PRESENT, PAST);
            fail();
        } catch (ConstraintViolationException ex) {
            assertThat(ex.getMessage()).contains("must be future or present");
        }
    }
}

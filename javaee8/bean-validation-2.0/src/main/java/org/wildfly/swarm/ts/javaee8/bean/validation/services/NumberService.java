package org.wildfly.swarm.ts.javaee8.bean.validation.services;

import org.wildfly.swarm.ts.javaee8.bean.validation.entities.NumberEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.Valid;
import javax.validation.constraints.Negative;
import javax.validation.constraints.NegativeOrZero;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class NumberService {
    private List<NumberEntity> numbers = new ArrayList<>();

    public List<NumberEntity> getNumbers() {
        return numbers;
    }

    @Valid
    public NumberEntity create(@Positive(message = "must be positive") int positive,
                               @Negative(message = "must be negative") int negative,
                               @PositiveOrZero(message = "must be positive or zero") int positiveOrZero,
                               @NegativeOrZero(message = "must be negative or zero") int negativeOrZero
    ) {
        NumberEntity numberEntity = new NumberEntity(positive, negative, positiveOrZero, negativeOrZero);
        numbers.add(numberEntity);
        return numberEntity;
    }
}

package org.wildfly.swarm.ts.javaee8.bean.validation.entities;

import javax.validation.constraints.Negative;
import javax.validation.constraints.NegativeOrZero;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

public class NumberEntity {
    @Positive(message = "must be positive")
    private int positive;

    @Negative(message = "must be negative")
    private int negative;

    @PositiveOrZero(message = "must be positive or zero")
    private int positiveOrZero;

    @NegativeOrZero(message = "must be negative or zero")
    private int negativeOrZero;

    public NumberEntity(int positive,
                        int negative,
                        int positiveOrZero,
                        int negativeOrZero) {
        this.positive = positive;
        this.negative = negative;
        this.positiveOrZero = positiveOrZero;
        this.negativeOrZero = negativeOrZero;
    }

    public int getPositive() {
        return positive;
    }

    public int getNegative() {
        return negative;
    }

    public int getPositiveOrZero() {
        return positiveOrZero;
    }

    public int getNegativeOrZero() {
        return negativeOrZero;
    }
}

package org.wildfly.swarm.ts.wildfly.hibernate.validator;

import org.hibernate.validator.constraints.CreditCardNumber;

import javax.validation.constraints.NotNull;

public class CreditCard {
    @NotNull
    @CreditCardNumber
    private String creditCardNumber;

    public CreditCard(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }
}

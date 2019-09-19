package org.wildfly.swarm.ts.javaee8.jsf.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, PasswordHolder> {
    @Override
    public void initialize(Password constraintAnnotation) {
    }

    @Override
    public boolean isValid(PasswordHolder value, ConstraintValidatorContext context) {
        return value.getPassword1().equals(value.getPassword2());
    }
}

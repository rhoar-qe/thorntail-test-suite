package org.wildfly.swarm.ts.javaee8.jsf.validation;

import javax.validation.Constraint;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = PasswordValidator.class)
@Target(TYPE)
@Retention(RUNTIME)
public @interface Password {
    String message() default "password fields must match";

    Class[] groups() default {};

    Class[] payload() default {};
}

package org.wildfly.swarm.ts.javaee.cdi.bean.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.lang.annotation.*;

@NotNull(message = "ISBN must be set")
@Pattern(regexp = "^\\d{9}[\\d|X]$", message = "ISBN must be valid") // the regexp here is very simplistic
@Constraint(validatedBy = {})
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Isbn {
    String message() default "{org.wildfly.swarm.ts.cdi.bean.validation.Isbn.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

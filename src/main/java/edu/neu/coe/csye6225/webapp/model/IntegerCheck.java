package edu.neu.coe.csye6225.webapp.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IntegerValidator.class)
public @interface IntegerCheck {
    String message() default "Field must be an integer";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

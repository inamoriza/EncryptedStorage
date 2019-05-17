package com.crypt.storage.annotation;

import com.crypt.storage.validator.UniqueUserValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueUserValidator.class)
@Documented
public @interface UniqueUser {
    String message() default "Username is already registered";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

package com.crypt.storage.annotation;

import com.crypt.storage.validator.ComplexPasswordValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ComplexPasswordValidator.class)
@Documented
public @interface ComplexPassword {
    String message() default "Password has to be at least 8 characters long " +
            "and must contain: Uppercase, Lowercase, Numbers and Non-alpha numeric characters";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

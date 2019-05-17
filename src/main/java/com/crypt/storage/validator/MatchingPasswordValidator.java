package com.crypt.storage.validator;

import com.crypt.storage.model.User;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MatchingPasswordValidator implements ConstraintValidator<ValidPassword, Object> {
    @Override
    public void initialize(ValidPassword constraintAnnotation) {
    }
    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        User user = (User) object;
        return user.getPassword().equals(user.getMatchingPassword());
    }
}

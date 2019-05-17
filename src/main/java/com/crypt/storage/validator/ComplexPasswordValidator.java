package com.crypt.storage.validator;

import com.crypt.storage.annotation.ComplexPassword;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComplexPasswordValidator implements ConstraintValidator<ComplexPassword, String> {
    private Pattern pattern;
    private Matcher matcher;
    private static final String PASSWORD_PATTERN = "^(?:(?=.*[a-z])(?:(?=.*[A-Z])(?=.*[\\d\\W])|(?=.*\\W)(?=.*\\d))|(?=.*\\W)(?=.*[A-Z])(?=.*\\d)).{8,}$";

    @Override
    public void initialize(ComplexPassword constraintAnnotation) {}

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) { return (validatePassword(password)); }
    private boolean validatePassword(String password) {
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }
}

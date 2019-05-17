package com.crypt.storage.validator;

import com.crypt.storage.DAO.UserManagment;
import com.crypt.storage.annotation.UniqueEmail;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    private BufferedReader read;

    @Override
    public void initialize(UniqueEmail constraintAnnotation) {}

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {return !(exists(email)); }

    private boolean exists(String email) {
        try {
            read = new BufferedReader(new FileReader(UserManagment.listURL));
            String next = read.readLine();
            while (next != null) {
                if (next.split(":")[2].equals(email)) {
                    read.close();
                    return true;
                }
                next = read.readLine();
            }
            read.close();
        } catch (IOException ex) {
            System.out.println("Error checking unique email.");
        }
        return false;
    }
}

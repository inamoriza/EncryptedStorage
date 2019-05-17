package com.crypt.storage.validator;

import com.crypt.storage.DAO.UserManagment;
import com.crypt.storage.annotation.UniqueUser;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class UniqueUserValidator implements ConstraintValidator<UniqueUser, String> {

    private BufferedReader read;

    @Override
    public void initialize(UniqueUser constraintAnnotation) {}

    @Override
    public boolean isValid(String user, ConstraintValidatorContext context) {return !(exists(user)); }

    private boolean exists(String user) {
        try {
            read = new BufferedReader(new FileReader(UserManagment.listURL));
            String next = read.readLine();
            while (next != null) {
                if (next.split(":")[0].equals(user)) {
                    read.close();
                    return true;
                }
                next = read.readLine();
            }
            read.close();
        } catch (IOException ex) {
            System.out.println("Error checking unique username.");
        }
        return false;
    }
}

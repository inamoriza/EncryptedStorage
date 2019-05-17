package com.crypt.storage.model;


import com.crypt.storage.annotation.*;

import javax.validation.constraints.NotNull;

@MatchingPassword
public class User {
    @NotNull
    @UniqueUser
    private String username;

    @NotNull
    @ComplexPassword
    private String password;

    @NotNull
    private String matchingPassword;

    @NotNull
    @ValidEmail
    @UniqueEmail
    private String email;

    public User(){
        super();
    }

    public User(String username, String password, String matchingPassword, String email) {
        this.username = username;
        this.password = password;
        this.matchingPassword = matchingPassword;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMatchingPassword() { return matchingPassword; }

    public void setMatchingPassword(String matchingPassword) { this.matchingPassword = matchingPassword; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }
}

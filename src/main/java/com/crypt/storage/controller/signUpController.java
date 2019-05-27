package com.crypt.storage.controller;

import com.crypt.storage.DAO.FileManagment;
import com.crypt.storage.DAO.UserManagment;
import com.crypt.storage.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.crypto.SecretKey;
import javax.validation.Valid;

@Controller
public class signUpController  {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserManagment userManagment = new UserManagment();
    private FileManagment fileManagment = new FileManagment();

    @RequestMapping("/signUp")
    public String signUp(Model model) {
        model.addAttribute("user", new User());
        return "/signUp";
    }

    @RequestMapping(value="/signUp", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("user") @Valid User user,
                                   BindingResult bindingResult, Errors errors) {
        if (bindingResult.hasErrors()){
            System.out.println("Binding Error");
            return "/signUp";
        }

        fileManagment.generateSecretKey(user.getPassword(),user.getUsername());
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        if (!(userManagment.createUserDir(user.getUsername()) &&
                userManagment.createUser(user.getUsername(), user.getPassword(), user.getEmail()))) {
            return "redirect:/error";
        }
        return "redirect:/";
    }
}

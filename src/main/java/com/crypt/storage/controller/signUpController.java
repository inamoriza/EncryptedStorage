package com.crypt.storage.controller;

import com.crypt.storage.DAO.UserManagment;
import com.crypt.storage.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

@Controller
public class signUpController  {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserManagment userManagment = new UserManagment();


    @RequestMapping("/signUp")
    public String signUp(Model model) {
        model.addAttribute("user", new User());
        return "signUp";
    }

    @RequestMapping(value="/signUp", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("user") User user,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            bindingResult.getAllErrors();
            System.out.println("ERROR");
            return "/signUp";
        }

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        try {
            userManagment.saveUser(user.getUsername(),user.getPassword());
        } catch (IOException e) {
            e.printStackTrace();
        }


        return "redirect:/";
    }
}

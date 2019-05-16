package com.crypt.storage.controller;

import com.crypt.storage.DAO.UserManagment;
import com.crypt.storage.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class loginController {

    @Autowired
    private PasswordEncoder passwordEncoder;
    private UserManagment userManagment = new UserManagment();

    @RequestMapping("/")
    public String index(Model model){
        model.addAttribute("user", new User());
        return "/index";
    }
    @RequestMapping(value="/", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("user") User user,
                                   BindingResult bindingResult, HttpSession session) {
        if(bindingResult.hasErrors()){
            return "/";
        }

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        userManagment.checkPassword(user.getUsername(), user.getPassword());

        session.setAttribute("user", user);
        return "/files/list";
    }
}
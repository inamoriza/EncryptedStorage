package com.crypt.storage.controller;

import com.crypt.storage.DAO.FileManagment;
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

import javax.crypto.SecretKey;
import javax.servlet.http.HttpSession;

@Controller
public class loginController {

    @Autowired
    private PasswordEncoder passwordEncoder;
    private UserManagment userManagment = new UserManagment();
    private FileManagment fileManagment = new FileManagment();

    @RequestMapping("/")
    public String index(Model model){
        model.addAttribute("user", new User());
        model.addAttribute("loginFailed", false);
        return "/index";
    }

    @RequestMapping(value="/", method = RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("user") User user, Model model,
                                   BindingResult bindingResult, HttpSession session) {
        if(bindingResult.hasErrors()){
            userManagment.invalidateUser(user);
            return "redirect:/error";
        }
        if (!passwordEncoder.matches(user.getPassword(), userManagment.getPassword(user.getUsername()))) {
            System.out.println("Login for user "+user.getUsername()+" failed.");
            model.addAttribute("loginFailed", true);
            userManagment.invalidateUser(user);
            return "/index";
        }
        System.out.println("Login for user "+user.getUsername()+" was successful.");
        SecretKey secret = fileManagment.getSecretKey(user.getUsername(),user.getPassword());
        System.out.println("Key login: "+ secret);
        session.setAttribute("secret", secret);
        session.setAttribute("user", userManagment.invalidatePassword(user));
        userManagment.invalidateUser(user);
        return "redirect:/files/list";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}

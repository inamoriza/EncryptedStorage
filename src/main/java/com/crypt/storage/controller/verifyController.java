package com.crypt.storage.controller;

import com.crypt.storage.DAO.FileManagment;
import com.crypt.storage.DAO.UserManagment;
import com.crypt.storage.model.User;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.io.*;

@Controller
public class verifyController {

    @Autowired
    private PasswordEncoder passwordEncoder;
    private UserManagment userManagment = new UserManagment();
    private FileManagment fileManagment = new FileManagment();

    @RequestMapping("/verify")
    public String verify(Model model) {
        model.addAttribute("user", new User());
        return "/verify";
    }

    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    public String verifyAndChange(@ModelAttribute("user") User user, HttpSession session) {
        if (user.getPassword().equals(user.getMatchingPassword())) {
            User sUser = (User) session.getAttribute("tmpUser");
            String passcode = sUser.getUsername() + "_passcode";
            try {
                BufferedReader br = new BufferedReader(new FileReader(UserManagment.userDB + passcode));
                passcode = br.readLine();
                br.close();
                if (user.getUsername().equals(passcode)) {
                    boolean res = userManagment.changePassword(sUser.getUsername(), passwordEncoder.encode(user.getPassword()));
                    if (res) {
                        fileManagment.generateSecretKey(user.getPassword(),sUser.getUsername());
                        System.out.println("Password Changed");
                    } else {
                        System.out.println("Password did not change");
                    }
                    userManagment.invalidateUser(sUser);
                    session.removeAttribute("tmpUser");
                    return "redirect:/";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        session.removeAttribute("tmpUser");
        return "/verify";
    }
}

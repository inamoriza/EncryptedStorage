package com.crypt.storage.controller;

import com.crypt.storage.DAO.UserManagment;
import com.crypt.storage.model.RandomString;
import com.crypt.storage.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

@Controller
public class changePasswordController {

    @Autowired
    private PasswordEncoder passwordEncoder;
    private UserManagment userManagment = new UserManagment();

    @RequestMapping("/changePassword")
    public String changePassword(Model model) {
        model.addAttribute("user", new User());
        return "/changePassword";
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public String submit(@ModelAttribute("user") User user, HttpSession session) {
        if (userManagment.getEmail(user.getUsername()).equals("")) {
            System.out.println("Invalid Account");
            return "/changePassword";
        }
        session.setAttribute("tmpUser", user);
        RandomString rs = new RandomString(6);
        String random = rs.nextString();
        try {
            PrintWriter passcode = new PrintWriter(UserManagment.userDB + user.getUsername() + "_passcode");
            passcode.write(random);
            passcode.close();
            this.sendMail(random, userManagment.getEmail(user.getUsername()));
        } catch (IOException ex) {
            System.out.println("Verification Error");
            ex.printStackTrace();
        }
        return  "redirect:/verify";
    }

    private void sendMail(String random, String email) {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "smtp.mailtrap.io");
        prop.put("mail.smtp.port", "2525");
        prop.put("mail.smtp.ssl.trust", "smtp.mailtrap.io");
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("f2b8258c6e60c2", "41f962b61fe8fc");
            }
        });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("EncryptedStorage@gmail.com"));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Change Password");

            String msg = "Verification Code: "+random;

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(msg, "text/html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);

            Transport.send(message);
        } catch (Exception ex) {
            System.out.println("Error sending email");
            ex.printStackTrace();
        }

    }
}

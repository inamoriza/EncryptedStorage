package com.crypt.storage.controller;

import com.crypt.storage.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/files")
public class listController {

    @RequestMapping("/list")
    public String list(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        return "/files/list";
    }

    @RequestMapping(value="/list", method = RequestMethod.POST)
    public String addFile(@RequestParam("file") MultipartFile file, Model model, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            String name = user.getUsername();
            System.out.println("Cifrando archivo del usuario "+user.getUsername());
            byte[] bytes = file.getBytes();
            System.out.println("BYTES: "+ bytes.length);
            String pathname= "C:/esdb/users/"+name+"/"+file.getOriginalFilename();
            System.out.println("PATHNAME: "+pathname);
            Path path = Paths.get(pathname);
            Files.write(path, bytes);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "redirect:/files/list";
    }




}

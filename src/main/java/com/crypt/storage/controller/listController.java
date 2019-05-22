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

@Controller
@RequestMapping("/files")
public class listController {

    @RequestMapping("/list")
    public String list(Model model) {
        return "/files/list";
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public String submit(@RequestParam("file") MultipartFile file, Model modelMap, HttpSession session) throws IOException {

        User usuario = (User) session.getAttribute("user");
        String name = usuario.getUsername();
        byte[] bytes = file.getBytes();

        return "/files/list";
    }


}

package com.crypt.storage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class listController {

    @RequestMapping("/files/list")
    public String list(Model model) {
        return "/files/list";
    }

}

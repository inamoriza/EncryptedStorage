package com.crypt.storage.controller;

import com.crypt.storage.DAO.FileManagment;
import com.crypt.storage.DAO.UserManagment;
import com.crypt.storage.model.User;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpSession;
import java.io.FileOutputStream;
import java.io.IOException;

@Controller
@RequestMapping("/files")
public class listController {

    private FileManagment fileManagment = new FileManagment();
    private UserManagment userManagment = new UserManagment();

    @RequestMapping("/list")
    public String list(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        session.setAttribute("fileList", userManagment.listUserDir(user.getUsername()));
        return "/files/list";
    }

    @RequestMapping(value="/list", method = RequestMethod.POST)
    public String addFile(@RequestParam("file") MultipartFile file, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            byte[] cf = fileManagment.encryptFile((SecretKeySpec) session.getAttribute("secret"), file.getBytes());
            FileOutputStream fs = new FileOutputStream(UserManagment.userDatabase
                    + user.getUsername() + "/" + FilenameUtils.removeExtension(file.getOriginalFilename()));
            fs.write(cf);
            fs.close();

            SecretKey key = (SecretKey) session.getAttribute("secret");
            System.out.println("key: "+ key);
            SecretKeySpec spec = new SecretKeySpec(key.getEncoded(), "AES");
            byte [] decryptedFile = fileManagment.decryptFile(spec,cf);

            fs = new FileOutputStream(UserManagment.userDatabase
                    + user.getUsername() + "/" +"desencriptado-"+ file.getOriginalFilename());
            fs.write(decryptedFile);
            fs.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "redirect:/files/list";
    }
}

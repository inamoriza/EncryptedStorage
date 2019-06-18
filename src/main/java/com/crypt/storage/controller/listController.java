package com.crypt.storage.controller;

import com.crypt.storage.DAO.FileManagment;
import com.crypt.storage.DAO.UserManagment;
import com.crypt.storage.model.User;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;

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
            FileOutputStream fs = new FileOutputStream(UserManagment.userDB
                    + user.getUsername() + "/" + file.getOriginalFilename());
            fs.write(cf);
            fs.close();

        } catch (IOException ex) {
            System.out.println("Input/Output error");
            ex.printStackTrace();
        }
        return "redirect:/files/list";
    }

    @RequestMapping(value="/list", params = "df", method = RequestMethod.GET)
    public void downloadFile(@RequestParam(value = "df") String filename, HttpSession session,
                             HttpServletResponse response) {
        response.setContentType("application/force-download");
        response.setHeader("Content-disposition", "attachment; filename="+filename);
        User user = (User) session.getAttribute("user");
        try {
            InputStream is = new FileInputStream(UserManagment.userDB + user.getUsername() + "/" + filename);
            byte [] decryptedFile = fileManagment.decryptFile((SecretKeySpec) session.getAttribute("secret"),
                    IOUtils.toByteArray(is));
            is.close();
            is = new ByteArrayInputStream(decryptedFile);
            org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
            is.close();
            response.flushBuffer();
            response.getOutputStream().close();

        } catch (IOException ex) {
            System.out.println("Input/Output error");
            ex.printStackTrace();
        }
    }

    @RequestMapping(value="/list", params = "rf", method = RequestMethod.GET)
    public String removeFile(@RequestParam(value = "rf") String filename, HttpSession session) {
        User user = (User) session.getAttribute("user");
        filename = UserManagment.userDB + user.getUsername() + "/" + filename;
        System.out.println(filename);
        File file = new File(filename);
        if (file.delete()) {
            System.out.println("Deleted " + filename);
        } else {
            System.out.println("Error while deleting " + filename);
        }
        return "redirect:/files/list";
    }
}

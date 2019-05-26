package com.crypt.storage.DAO;

import com.crypt.storage.model.User;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Date;

//Class for user CRUD operations.
public class UserManagment {
    public static final String listURL = "C:/esdb/userList.txt";
    public static final String userDatabase = "C:/esdb/users/";
    private BufferedWriter writer;
    private BufferedReader read;

    public UserManagment() {
        try {
            File list = new File(listURL);
            File root = new File(userDatabase);
            if (!list.exists()) { list.createNewFile(); }
            if (!root.exists()) { root.mkdir(); }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean createUser(String username, String hash, String email) {
        try {
            String user = username + ":" + hash + ":" + email;
            writer = new BufferedWriter(new FileWriter(listURL, true));
            writer.write(user); writer.newLine(); writer.close();
            System.out.println("Created " + username + " user account.");
            return true;
        } catch (IOException ex) {
            System.out.println("Error creating " + username + " user account.");
        }
        return false;
    }

    public boolean createUserDir(String username) {
        File dir = new File(userDatabase + username);
        try {
            if (!dir.exists()) {
                System.out.println("Creating " + username + " personal directory...");
                return dir.mkdir();
            } else {
                System.out.println(username + " personal directory already exists.");
            }
        } catch (SecurityException se) {
            System.out.println("Error creating " + username + " personal directory.");
        }
        return false;
    }

    public String getPassword(String username) {
        String pass="";
        try {
            read = new BufferedReader(new FileReader(listURL));
            String next = read.readLine();
            while (next != null) {
                if (next.split(":")[0].equals(username)) {
                    pass = next.split(":")[1];
                }
                next = read.readLine();
            }
            read.close();
        } catch (IOException ex) {
            System.out.println("Error checking password.");
        }
        return pass;
    }

    public ArrayList<String[]> listUserDir(String username) {
        ArrayList<String[]> list = new ArrayList<>();
        File folder = new File(UserManagment.userDatabase + username);
        File[] dir = folder.listFiles();
        String[] aux;

        try {
            for (File file : dir) {
                aux = new String[3];
                aux[0] = file.getName();
                aux[1] = (file.length() < 1000000 ) ? file.length()/1000 + " KB" : file.length()/1000000 + " MB";
                aux[2] = LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()),
                        ZoneId.systemDefault()).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM,
                        FormatStyle.SHORT));

                list.add(aux);
            }

        } catch (NullPointerException ex) {
            System.out.println("Error reading user directory.");
            ex.printStackTrace();
        }
        return list;
    }

    public User invalidatePassword(User user) {
        user.setPassword("");
        return user;
    }
}
package com.crypt.storage.DAO;

import com.crypt.storage.model.User;

import java.io.*;

//Class for user CRUD operations.
public class UserManagment {
    public static final String listURL = "C:/esdb/userList.txt";
    private final String userDatabase = "C:/esdb/users/";
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

    public User invalidatePassword(User user) {
        user.setPassword("");
        return user;
    }
}
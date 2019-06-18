package com.crypt.storage.DAO;

import com.crypt.storage.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;

//Class for user CRUD operations.
public class UserManagment {
    public static final String listURL = "C:/esdb/userList.txt";
    private static final String tmplistURL = "C:/esdb/tmpUserList.txt";
    public static final String userDB = "C:/esdb/users/";

    public UserManagment() {
        try {
            File list = new File(listURL);
            File root = new File(userDB);
            if (!list.exists()) { list.createNewFile(); }
            if (!root.exists()) { root.mkdir(); }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean createUser(String username, String hash, String email) {
        try {
            String user = username + ":" + hash + ":" + email;
            BufferedWriter writer = new BufferedWriter(new FileWriter(listURL, true));
            writer.write(user); writer.newLine(); writer.close();
            System.out.println("Created " + username + " user account.");
            return true;

        } catch (IOException ex) {
            System.out.println("Error creating " + username + " user account.");
        }
        return false;
    }

    public boolean createUserDir(String username) {
        File dir = new File(userDB + username);
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
            BufferedReader read = new BufferedReader(new FileReader(listURL));
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

    public boolean changePassword(String username, String password) {
        String email = this.getEmail(username);
        try {
            File inputFile = new File(listURL);
            File tempFile = new File(tmplistURL);
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            String currentLine;

            while((currentLine = reader.readLine()) != null) {
                String user = currentLine.split(":")[0];
                if(user.equals(username)) continue;
                writer.write(currentLine + System.getProperty("line.separator"));
            }
            writer.close();
            reader.close();
            boolean deleted = inputFile.delete();
            if (!deleted) {
                System.out.println("Error Deleting File");
            }
            boolean successful = tempFile.renameTo(inputFile);
            if (!successful) {
                System.out.println("Error Renaming File");
            }

        } catch (IOException ex) {
            System.out.println("Error Changing Password");
            ex.printStackTrace();
        }
        return this.createUser(username, password, email);
    }

    public String getEmail(String username) {
        String email="";
        try {
            BufferedReader read = new BufferedReader(new FileReader(listURL));
            String next = read.readLine();
            while (next != null) {
                if (next.split(":")[0].equals(username)) {
                    email = next.split(":")[2];
                }
                next = read.readLine();
            }
            read.close();

        } catch (IOException ex) {
            System.out.println("Error checking email.");
        }
        return email;
    }

    public ArrayList<String[]> listUserDir(String username) {
        ArrayList<String[]> list = new ArrayList<>();
        File folder = new File(UserManagment.userDB + username);
        File[] dir = folder.listFiles();
        try {
            for (File file : dir) {
                String[] aux = new String[3];
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
        user.setMatchingPassword("");
        return user;
    }

    public void invalidateUser(User user) {
        user.setUsername("");
        invalidatePassword(user);
        user.setEmail("");
    }
}
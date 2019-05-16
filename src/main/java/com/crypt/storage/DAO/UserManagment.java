package com.crypt.storage.DAO;

import java.io.*;

public class UserManagment {

    private final String listURL = "C:/esdb/userList.txt";
    private final String userDatabase = "C:/esdb/users/";
    private BufferedWriter writer;
    private BufferedReader read;

    public UserManagment() {
        try {
            File list = new File(listURL);
            File root = new File(userDatabase);
            if (!list.exists()) {
                list.createNewFile();
            }
            if (!root.exists()) {
                root.mkdir();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void saveUser(String username, String hash) {
        try {
            String user = username + ":" + hash;
            writer = new BufferedWriter(new FileWriter(listURL, true));
            writer.write(user);
            writer.newLine();
            writer.close();
            System.out.println("Created " + username + " user account.");
        } catch (IOException ex) {
            System.out.println("Error creating " + username + " user account.");
        }
    }

    public boolean checkPassword(String username, String hash) {
        String user = username + ":" + hash;
        try {
            read = new BufferedReader(new FileReader(listURL));
            String next = read.readLine();
            while (next != null) {
                if (next.equals(user)) {
                    read.close();
                    return true;
                }
                next = read.readLine();
            }
            read.close();
        } catch (IOException ex) {
            System.out.println("Error checking password.");
        }
        return false;
    }

    public boolean existingUser(String username) {
        try {
            read = new BufferedReader(new FileReader(listURL));
            String next = read.readLine();
            while (next != null) {
                if (next.split(":")[0].equals(username)) {
                    read.close();
                    return true;
                }
                next = read.readLine();
            }
            read.close();
        } catch (IOException ex) {
            System.out.println("Error checking user.");
        }
        return false;
    }

    public boolean userMkdir(String username) {
        File dir = new File(userDatabase + username);
        boolean result = false;

        try {
            if (!dir.exists()) {
                System.out.println("Creating " + username + " personal directory...");
                dir.mkdir();
                System.out.println("Created " + username + " personal directory.");
                result = true;
            } else {
                System.out.println(username + " personal directory already exists.");
            }
        } catch (SecurityException se) {
            System.out.println("Error creating " + username + " personal directory.");
        }
        return result;
    }
}
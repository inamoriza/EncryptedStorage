package com.crypt.storage.DAO;

import java.io.*;

public class UserManagment {

    private final String listURL = "C:/esdb/userList.txt";
    private BufferedWriter writer;
    private BufferedReader read;

    public UserManagment() {
        super();
    }

    public void saveUser(String username, String hash) throws IOException {
        String user = username + ":" + hash;
        writer = new BufferedWriter(new FileWriter(listURL, true));
        writer.write(user);
        writer.newLine();
        writer.close();
    }

    public boolean checkUser(String username, String hash) throws IOException {
        String user = username + ":" + hash;
        read = new BufferedReader(new FileReader(listURL));

        String next = read.readLine();
        while (next != null) {
            if (next.equals(user))
                return true;
            next = read.readLine();
        }
        read.close();
        return false;
    }
}

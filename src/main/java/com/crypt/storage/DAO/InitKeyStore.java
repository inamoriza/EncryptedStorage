package com.crypt.storage.DAO;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class InitKeyStore {


    private static final String KS_PASS = "Jd_&Aw*,`hZ8BG]Q";

    public static void main(String[] args) throws KeyStoreException {

        FileManagment fm = new FileManagment();
        KeyStore keyStore = KeyStore.getInstance("JCEKS");
        char [] password = KS_PASS.toCharArray();

        try(FileOutputStream keyStoreOutput = new FileOutputStream("src/main/resources/database/keystore.ks")){
            keyStore.load(null, password);
            keyStore.store(keyStoreOutput, password);
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
            e.printStackTrace();
        }
        System.out.println("KeyStore initialized successfully");
    }
}

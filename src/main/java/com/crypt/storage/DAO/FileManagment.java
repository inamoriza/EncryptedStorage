package com.crypt.storage.DAO;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class FileManagment {

    private static final String ALGORITHM = "AES";
    private static final byte[] salt = new byte[8];

    public String encrypt(String password, File file) throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecureRandom srandom = new SecureRandom();
        srandom.nextBytes(salt);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec skey = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);

        byte[] iv = new byte[128/8];
        srandom.nextBytes(iv);
        IvParameterSpec ivspec = new IvParameterSpec(iv);

        Cipher ci=null;

        try {
            ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
            ci.init(Cipher.ENCRYPT_MODE, skey, ivspec);
        } catch (NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            System.out.println("Bad encrypt");
            e.printStackTrace();
        }

        byte[] bytes = new byte[(int) file.length()];

        try {
            final byte[] resultBytes = ci.doFinal(bytes);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        //return Base64.getMimeEncoder().encodeToString(resultBytes);

        return "";
    }

}

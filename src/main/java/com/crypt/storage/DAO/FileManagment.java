package com.crypt.storage.DAO;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;

public class FileManagment {

    private static final String SK_FACTORY_INSTANCE = "PBKDF2WithHmacSHA256";
    private static final String CIPHER_INSTANCE = "AES/CBC/PKCS5Padding";
    private static final String ALGORITHM = "AES";
    private static final int ITERATIONS = 65536;
    private static final int SIZE = 256;

    public SecretKeySpec getSecretKey(String password) {
        byte[] salt = new byte[8];
        SecureRandom sRandom = new SecureRandom();
        sRandom.nextBytes(salt);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(SK_FACTORY_INSTANCE);
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, SIZE);
            SecretKey k = factory.generateSecret(spec);
            return new SecretKeySpec(k.getEncoded(), ALGORITHM);
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Factory specified algorithm is not valid.");
            ex.printStackTrace();
        } catch (InvalidKeySpecException ex) {
            System.out.println("Secret Key specification is not valid.");
            ex.printStackTrace();
        }
        return null;
    }

    public ArrayList<byte[]> encryptFile(SecretKeySpec secret, byte[] file) {
        ArrayList<byte[]> res = new ArrayList<>();
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE);
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            AlgorithmParameters params = cipher.getParameters();
            byte[] initVector = params.getParameterSpec(IvParameterSpec.class).getIV();
            byte[] cipherText = cipher.doFinal(file);
            res.add(initVector);
            res.add(cipherText);
            return res;

        } catch (NoSuchPaddingException ex) {
            System.out.println("Specified cipher instance padding is not valid.");
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Specified algorithm is not valid.");
            ex.printStackTrace();
        } catch (InvalidKeyException ex) {
            System.out.println("Specified key is not valid.");
            ex.printStackTrace();
        } catch (InvalidParameterSpecException ex) {
            System.out.println("Specified parameter spec is not valid.");
            ex.printStackTrace();
        } catch (IllegalBlockSizeException ex) {
            System.out.println("Specified block size is not valid.");
            ex.printStackTrace();
        } catch (BadPaddingException ex) {
            System.out.println("Specified padding is not valid.");
            ex.printStackTrace();
        }
        return res;
    }

    public byte[] decryptFile(SecretKeySpec secret, byte[] initVector, byte[] cipherText) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE);
            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(initVector));

            CipherInputStream cipherInput = new CipherInputStream(new ByteArrayInputStream(initVector), cipher);
            ByteArrayOutputStream cipherOutput = new ByteArrayOutputStream();

            int len;
            byte[] buffer = new byte[4096];
            while ((len = cipherInput.read(buffer, 0, buffer.length)) != -1) {
                cipherOutput.write(buffer, 0, len);
            }
            cipherOutput.flush();
            return cipherOutput.toByteArray();

        } catch (IOException ex) {
            System.out.println("IO error.");
            ex.printStackTrace();
        } catch (NoSuchPaddingException ex) {
            System.out.println("Specified cipher instance padding is not valid.");
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Specified algorithm is not valid.");
            ex.printStackTrace();
        } catch (InvalidKeyException ex) {
            System.out.println("Specified key is not valid.");
            ex.printStackTrace();
        } catch (InvalidAlgorithmParameterException ex) {
            System.out.println("Specified algorithm parameter is not valid.");
            ex.printStackTrace();
        }
        return null;
    }
}

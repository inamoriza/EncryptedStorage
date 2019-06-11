package com.crypt.storage.DAO;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class FileManagment {

    //Cipher Params
    private static final String SK_FACTORY_INSTANCE = "PBKDF2WithHmacSHA256";
    private static final String CIPHER_INSTANCE = "AES/CBC/PKCS5Padding";
    private static final String ALGORITHM = "AES";
    private static final int ITERATIONS = 65536;
    private static final int SIZE = 256;

    public void generateSecretKey(String password,String username) {
        byte[] salt = new byte[8];
        SecureRandom sRandom = new SecureRandom();
        sRandom.nextBytes(salt);
        String alias = InitKeyStore.PREFIX + username;
        char [] pass = password.toCharArray();
        try {
            //Create secret key
            SecretKeyFactory factory = SecretKeyFactory.getInstance(SK_FACTORY_INSTANCE);
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, SIZE);
            SecretKey k = factory.generateSecret(spec);
            SecretKey key = new SecretKeySpec(k.getEncoded(), ALGORITHM);

            //Save secret key
            KeyStore ks = loadKeyStore();
            KeyStore.SecretKeyEntry entry = new KeyStore.SecretKeyEntry(key);
            ks.setEntry(alias, entry, new KeyStore.PasswordProtection(pass));

            //Save KeyStore
            saveKeyStore(ks);

        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Factory specified algorithm is not valid.");
            ex.printStackTrace();
        } catch (InvalidKeySpecException ex) {
            System.out.println("Secret Key specification is not valid.");
            ex.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    private KeyStore loadKeyStore() throws GeneralSecurityException {
        char [] password = InitKeyStore.KS_PASS.toCharArray();
        KeyStore keyStore = KeyStore.getInstance("JCEKS");
        try {
            InputStream in = new FileInputStream("src/main/resources/database/keystore.ks");
            keyStore.load(in, password);
            return keyStore;

        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private void saveKeyStore(KeyStore ks){
        char [] password = InitKeyStore.KS_PASS.toCharArray();
        try(FileOutputStream keyStoreOutput = new FileOutputStream("src/main/resources/database/keystore.ks")){
            ks.store(keyStoreOutput, password);
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
            e.printStackTrace();
        }
    }

    public SecretKey getSecretKey(String username, String password){
        String alias = InitKeyStore.PREFIX + username;
        char [] pass = password.toCharArray();
        try {
            KeyStore ks = loadKeyStore();
            KeyStore.ProtectionParameter entryPassword = new KeyStore.PasswordProtection(pass);
            KeyStore.SecretKeyEntry key = (KeyStore.SecretKeyEntry) ks.getEntry(alias, entryPassword);
            return key.getSecretKey();

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] encryptFile(SecretKeySpec secret, byte[] file) {
        byte[] res = new byte[0];
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE);
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            byte[] initVector = cipher.getIV();
            byte[] cipherText = cipher.doFinal(file);
            res = new byte[initVector.length + cipherText.length];
            System.arraycopy(initVector, 0, res, 0, initVector.length);
            System.arraycopy(cipherText, 0, res, initVector.length, cipherText.length);

        } catch (NoSuchPaddingException ex) {
            System.out.println("Specified cipher instance padding is not valid.");
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Specified algorithm is not valid.");
            ex.printStackTrace();
        } catch (InvalidKeyException ex) {
            System.out.println("Specified key is not valid.");
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

    public byte[] decryptFile(SecretKeySpec secret, byte[] cipherText) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE);
            byte [] iv = new byte[cipher.getBlockSize()];
            byte [] file = new byte[cipherText.length - iv.length];
            System.arraycopy(cipherText, 0, iv, 0, iv.length);
            System.arraycopy(cipherText, iv.length, file, 0, file.length);
            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
            return cipher.doFinal(file);

        } catch (NoSuchPaddingException ex) {
            System.out.println("Specified cipher instance padding is not valid.");
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Specified algorithm is not valid.");
            ex.printStackTrace();
        } catch (InvalidKeyException ex) {
            System.out.println("Specified key is not valid.");
            ex.printStackTrace();
        } catch (InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }
}

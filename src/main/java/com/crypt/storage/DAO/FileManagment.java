package com.crypt.storage.DAO;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;

public class FileManagment {

    private static final String SK_FACTORY_INSTANCE = "PBKDF2WithHmacSHA256";
    private static final String CIPHER_INSTANCE = "AES/CBC/PKCS5Padding";
    private static final String ALGORITHM = "AES";
    private static final String KS_PASS = "Jd_&Aw*,`hZ8BG]Q";
    private static final int ITERATIONS = 65536;
    private static final int SIZE = 256;
    private static final String PREFIX = "esdb-key-user:";


    public void generateSecretKey(String password,String username) {
        byte[] salt = new byte[8];
        SecureRandom sRandom = new SecureRandom();
        sRandom.nextBytes(salt);
        String alias = PREFIX+username;
        char [] pass = password.toCharArray();
        char [] ks_password = KS_PASS.toCharArray();

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

        char [] password = KS_PASS.toCharArray();
        KeyStore keyStore = KeyStore.getInstance("JCEKS");
        InputStream in = null;

        try {
            in = new FileInputStream("src/main/resources/database/keystore.ks");

            System.out.println("KeyStore ANTES: "+keyStore);
            keyStore.load(in, password);

            return keyStore;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private void saveKeyStore(KeyStore ks){
        char [] password = KS_PASS.toCharArray();
        try(FileOutputStream keyStoreOutput = new FileOutputStream("src/main/resources/database/keystore.ks")){
            ks.store(keyStoreOutput, password);
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
            e.printStackTrace();
        }

    }

    public SecretKey getSecretKey(String username, String password){
        String alias = PREFIX+username;
        char [] pass = password.toCharArray();

        KeyStore ks = null;
        try {
            ks = (KeyStore) loadKeyStore();
            System.out.println("KeyStore: "+ks);
            KeyStore.ProtectionParameter entryPassword =
                    new KeyStore.PasswordProtection(pass);
            KeyStore.SecretKeyEntry key = (KeyStore.SecretKeyEntry) ks.getEntry(alias, entryPassword);
            return key.getSecretKey();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
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

    public byte[] decryptFile(SecretKeySpec secret, byte[] cipherText) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE);
            cipher.init(Cipher.DECRYPT_MODE, secret, cipher.getParameters());
            System.out.println("Decrypting...");
            byte [] decrypted = cipher.doFinal(cipherText);
            /*CipherInputStream cipherInput = new CipherInputStream(new ByteArrayInputStream(initVector), cipher);
            ByteArrayOutputStream cipherOutput = new ByteArrayOutputStream();

            int len;
            byte[] buffer = new byte[4096];
            while ((len = cipherInput.read(buffer, 0, buffer.length)) != -1) {
                cipherOutput.write(buffer, 0, len);
            }
            cipherOutput.flush(); */
            return decrypted;

        } catch (NoSuchPaddingException ex) {
            System.out.println("Specified cipher instance padding is not valid.");
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Specified algorithm is not valid.");
            ex.printStackTrace();
        } catch (InvalidKeyException ex) {
            System.out.println("Specified key is not valid.");
            ex.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }
}

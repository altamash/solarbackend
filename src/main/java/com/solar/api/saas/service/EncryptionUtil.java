package com.solar.api.saas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class EncryptionUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(EncryptionUtil.class);
    public static void main(String[] args) {
        /* path to project root folder for saving public and private keys */
        String path = System.getProperty("user.dir");

        // Generates New public and private keys
        KeyPair keyPair = generateKeys();

        /* save public and private keys to files at the given location */
        saveKeysToFiles(path, keyPair.getPublic(), keyPair.getPrivate());

        // Algorithm to convert keys
        String algorithm = "RSA";

        // Read keys from files
        PublicKey publicKey = loadPublicKey(path, algorithm);
        PrivateKey privateKey = loadPrivateKey(path, algorithm);

        // Test
        String plainText = "This is a  secret message";
        /* Encrypting plain text to ciphertext */
        String encryptedText = encrypt(publicKey, plainText);
        System.out.println("Encrypted text = " + encryptedText);

        /* Decrypting encrypted text back to plain text */
        String decryptedText = decrypt(privateKey, encryptedText);
        System.out.println("Decrypted text = " + decryptedText);
    }

    public static String encrypt(PublicKey publicKey, String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            System.out.println("No data to encrypt!");
            return plainText;
        }
        Cipher cipher = null;
        String encryptedString = "";
        try {
            // Creating a Cipher object
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

            // Initializing a Cipher object with public key
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            // Encrypting the plain text string
            byte[] encryptedText = cipher.doFinal(plainText.getBytes());

            // Encoding the encrypted text to Base64
            encryptedString = Base64.getEncoder().encodeToString(encryptedText);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println("Exception caught while encrypting : " + ex);
        }

        return encryptedString;
    }

    public static String decrypt(PrivateKey privateKey, String cipherText) {
        if (cipherText == null || cipherText.isEmpty()) {
            System.out.println("No data to decrypt!");
            return cipherText;
        }
        String decryptedString = "";
        Cipher cipher = null;
        try {
            // Creating a Cipher object
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

            // Initializing a Cipher object with private key
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            // Decoding from Base64
            byte[] encryptedText = Base64.getDecoder().decode(cipherText.getBytes());

            // Decrypting to plain text
            decryptedString = new String(cipher.doFinal(encryptedText));

        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println("Exception caught while decrypting : " + ex);
        }
        return decryptedString;
    }

    public static KeyPair generateKeys() {
        KeyPair keyPair = null;
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");

            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");

            // Initializing KeyPairGenerator
            keyGen.initialize(2048, random);

            // Generate keys
            keyPair = keyGen.generateKeyPair();

        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return keyPair;
    }

    public static void saveKeysToFiles(String path, PublicKey publicKey, PrivateKey privateKey) {

        FileOutputStream fos = null;

        try {
            File file = new File(path + "/public.key");
            if (file.createNewFile()) {
                fos = new FileOutputStream(file);
                X509EncodedKeySpec x509EncodedKeySpec =
                        new X509EncodedKeySpec(publicKey.getEncoded());
                fos.write(x509EncodedKeySpec.getEncoded());
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }

        // Private Key
        try {
            File file = new File(path + "/private.key");
            if (file.createNewFile()) {
                fos = new FileOutputStream(file);
                PKCS8EncodedKeySpec pkcs8EncodedKeySpec =
                        new PKCS8EncodedKeySpec(privateKey.getEncoded());
                fos.write(pkcs8EncodedKeySpec.getEncoded());
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }

    }

    public static PublicKey loadPublicKey(String path, String algorithm) {

        FileInputStream fis = null;
        PublicKey publicKey = null;
        try {
            File file = new File(path + "/public.key");
            fis = new FileInputStream(file);
            byte[] encodedPublicKey = new byte[(int) file.length()];
            fis.read(encodedPublicKey);

            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(encodedPublicKey);
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        return publicKey;
    }

    public static PrivateKey loadPrivateKey(String path, String algorithm) {

        FileInputStream fis = null;
        PrivateKey privateKey = null;

        try {
            File file = new File(path + "/private.key");
            fis = new FileInputStream(file);
            byte[] encodedPrivateKey = new byte[(int) file.length()];
            fis.read(encodedPrivateKey);

            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            privateKey = keyFactory.generatePrivate(privateKeySpec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        return privateKey;
    }

}
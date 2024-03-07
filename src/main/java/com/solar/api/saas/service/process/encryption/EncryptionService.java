package com.solar.api.saas.service.process.encryption;

import com.microsoft.azure.storage.StorageException;
import com.solar.api.saas.model.attribute.SystemAttribute;
import com.solar.api.saas.service.StorageService;
import com.solar.api.saas.service.systemAttribute.ESystemAttribute;
import com.solar.api.saas.service.systemAttribute.SystemAttributeService;
import com.solar.api.saas.service.tenantDetails.TenantDetailsImpl;
import com.solar.api.tenant.service.userDetails.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class EncryptionService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final String ENCRYPTION_ALGORITHM = "AES";
    private final String BLOCK_OPERATION_MODE = "CBC";
    private final String PADDING_TYPE = "PKCS5Padding";
    private final String ENCRYPTION_MODE = ENCRYPTION_ALGORITHM + "/" + BLOCK_OPERATION_MODE + "/" + PADDING_TYPE;
    private final String SPLITTER = "\\.";
    private final int AES_IV_SIZE = 16;
    private final int DEFAULT_SALT_SIZE = 32;
    private final int DEFAULT_ITERATIONS = 128;
    private final int DEFAULT_AES_KEY_SIZE = 128;
    private final int INDEX_SALT = 0;
    private final int INDEX_IV = 1;
    private final int INDEX_ENCRYPTED_DATA = 2;

    @Value("${app.profile}")
    private String appProfile;
    @Autowired
    private SystemAttributeService systemAttributeService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private KeyUtils keyUtils;
    @Autowired
    private PEMUtils pemUtils;

    public void init() {

        // Generate secret key
        SecretKey secretKey = keyUtils.generateSymmetricKey("AES", 256);

        // Generates New public and private keys
        KeyPair keyPair = keyUtils.generateAsymmetricKeys("RSA", 2048);

        // Save secret key
        saveOrUpdate(ESystemAttribute.SECRET_KEY, secretKey.getEncoded());

        // Save public key
        X509EncodedKeySpec x509EncodedKeySpec =
                new X509EncodedKeySpec(keyPair.getPublic().getEncoded());
        saveOrUpdate(ESystemAttribute.PUBLIC_KEY, x509EncodedKeySpec.getEncoded());

        // Save private key
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec =
                new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded());
        saveOrUpdate(ESystemAttribute.PRIVATE_KEY, pkcs8EncodedKeySpec.getEncoded());
    }

    private void saveOrUpdate(ESystemAttribute eSystemAttribute, byte[] bytes) {
        SystemAttribute keyAttribute =
                systemAttributeService.findByAttributeKey(eSystemAttribute.getAttributeKey());
        if (keyAttribute == null) {
            systemAttributeService.save(SystemAttribute.builder()
                    .attributeKey(eSystemAttribute.getAttributeKey())
                    .attributeValueLob(bytes)
                    .attributeDescription(eSystemAttribute.getAttributeDescription())
                    .build());
        } else {
            keyAttribute.setAttributeValueLob(bytes);
            keyAttribute.setAttributeDescription(eSystemAttribute.getAttributeDescription());
            systemAttributeService.update(keyAttribute);
        }
        if (eSystemAttribute == ESystemAttribute.PRIVATE_KEY) {
            try {
                /*int pkLength = keyAttribute.getAttributeValueLob().length;
                byte[] ppk2 = new byte[pkLength - 10];
                System.arraycopy(keyAttribute.getAttributeValueLob(), 10, ppk2, 0, pkLength - 10);*/
                storageService.uploadByteArray(getPPK2().getBytes(), appProfile, "saas/keyvalue", "ppk2");
            } catch (URISyntaxException e) {
                LOGGER.error(e.getMessage(), e);
            } catch (StorageException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public String encrypt(String plainText, String algorithm, Key key) {
        if (plainText == null || plainText.isEmpty()) {
            System.out.println("No data to encrypt!");
            return plainText;
        }
        Cipher cipher = null;
        String encryptedString = "";
        try {
            // Creating a Cipher object
//            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher = Cipher.getInstance(algorithm);

            // Initializing a Cipher object with public key
//            cipher.init(Cipher.ENCRYPT_MODE, loadPublicKey("RSA"));
            cipher.init(Cipher.ENCRYPT_MODE, key);

            // Encrypting the plain text string
            byte[] encryptedText = cipher.doFinal(plainText.getBytes("UTF-8"));
//            new String(Hex.encodeHex(encryptedText))
            // Encoding the encrypted text to Base64
            encryptedString = Base64.getEncoder().encodeToString(encryptedText);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException ex) {
            System.out.println("Exception caught while encrypting : " + ex);
        }

        return encryptedString;
    }

    // https://stackify.dev/639389-aes-encryption-using-java-and-decryption-using-javascript
    private String encryptAES(String password, String data) {
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[AES_IV_SIZE];
        random.nextBytes(iv);
        byte[] salt = new byte[DEFAULT_SALT_SIZE];
        random.nextBytes(salt);
        byte[] encrypted = new byte[0];
        try {
            byte[] key = pbkdf2(password, salt);
            Cipher cipher = Cipher.getInstance(ENCRYPTION_MODE);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, ENCRYPTION_ALGORITHM), new IvParameterSpec(iv));
            encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
            System.out.println("Exception caught while encrypting : " + ex);
        }
        return toBase64(salt) + "," + toBase64(iv) + "," + toBase64(encrypted);
    }

    /**
     * Generates PBKDF2 hash for the configured password using the provided salt.
     *
     * @param salt The salt to use.
     * @return The password hash as byte array
     */
    private byte[] pbkdf2(String password, byte[] salt) throws NoSuchAlgorithmException {
        KeySpec keySpec =
                new PBEKeySpec(password.toCharArray(), salt, DEFAULT_ITERATIONS, DEFAULT_AES_KEY_SIZE);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey secretKey = null;
        try {
            secretKey = keyFactory.generateSecret(keySpec);
        } catch (InvalidKeySpecException ex) {
            System.out.println("InvalidKeySpecException : " + ex);
        }
        return secretKey.getEncoded();
    }

    private String toBase64(byte[] data) {
        // use NO_WRAP because https://code.google.com/p/android/issues/detail?id=159799
//        return Base64.encodeToString(data, Base64.get.NO_WRAP);
        return Base64.getEncoder().encodeToString(data);
    }

    public String decrypt(String cipherText, String algorithm, Key key) {
        if (cipherText == null || cipherText.isEmpty()) {
            System.out.println("No data to decrypt!");
            return cipherText;
        }
        String decryptedString = "";
        Cipher cipher = null;
        try {
            // Creating a Cipher objec62
//            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher = Cipher.getInstance(algorithm);

            // Initializing a Cipher object with private key
//            cipher.init(Cipher.DECRYPT_MODE, loadPrivateKey("RSA"));
            cipher.init(Cipher.DECRYPT_MODE, key);

            // Decoding from Base64
            byte[] encryptedText = Base64.getDecoder().decode(cipherText.getBytes());

            // Decrypting to plain text
            decryptedString = new String(cipher.doFinal(encryptedText));

        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println("Exception caught while decrypting: " + ex);
        }
        return decryptedString;
    }

    public String getPPK2() {
        String privateKeyPEMString = pemUtils.getPEMString("RSA", PrivateKey.class);
        return Base64.getEncoder().encodeToString(privateKeyPEMString.substring(10).getBytes());
    }

    /**
     * <pre>
     * Encrypt plain text using AES
     * Encrypt the key using RSA public key
     * Refer README.md
     * </pre>
     */
    public List<String> encryptWithRSAWorkflow(String plainText) {
        UserDetails details = (UserDetailsImpl) (SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        String password = null;
        if (details instanceof UserDetailsImpl) {
            password = ((UserDetailsImpl) (SecurityContextHolder.getContext().getAuthentication()).getPrincipal()).getPassword();
        } else if (details instanceof TenantDetailsImpl) {
            password = ((TenantDetailsImpl) (SecurityContextHolder.getContext().getAuthentication()).getPrincipal()).getPassword();
        }
        List<String> strings = new ArrayList<>();
        SecretKey secretKey = keyUtils.loadSecretKey("AES");
//        strings.add(encrypt(plainText, "AES/CBC/PKCS5Padding", secretKey));
        String encrypted = encryptAES(password, plainText);
        String[] encryptedParts = encrypted.split(",");
        strings.add(encryptedParts[2]);
        String key = toBase64(password.getBytes()) + "," + encryptedParts[0] + "," + encryptedParts[1];
//        strings.add(encrypt(Base64.getEncoder().encodeToString(secretKey.getEncoded()), "RSA/ECB/PKCS1Padding", keyUtils.loadPublicKey("RSA")));
        strings.add(encrypt(Base64.getEncoder().encodeToString(key.getBytes()), "RSA/ECB/PKCS1Padding", keyUtils.loadPublicKey("RSA")));
//        strings.add(Base64.getEncoder().encodeToString(secretKey.getEncoded()));
        return strings;
    }

    /**
     * <pre>
     * On the client side, decrypt symmetric key using RSA private key
     * Decrypt the cipher text using decrypted symmetric key
     * Refer README.md
     * </pre>
     */
    public String decryptWithRSAWorkflow(List<String> cipherTextStrings) {
        byte[] bytes = Base64.getDecoder().decode(decrypt(cipherTextStrings.get(1), "RSA/ECB/PKCS1Padding", keyUtils.loadPrivateKey("RSA")));
        SecretKeySpec secretKey = new SecretKeySpec(bytes, 0, bytes.length, "AES");
        return decrypt(cipherTextStrings.get(0), "AES", secretKey);
    }
}
package com.solar.api.saas.service.process.encryption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import java.security.InvalidKeyException;
import java.security.Key;
import java.util.Base64;

//@Convert
public class StringAttributeConverter implements AttributeConverter<String, String> {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static final String AES = "AES";
    private static final byte[] encryptionKey = "fM6ZI1JBA5lIoAiIG0ihqA5v4NXHJmyY".getBytes();
    private final Key key;

    private final Cipher cipher;

    public StringAttributeConverter() throws Exception {
        key = new SecretKeySpec(encryptionKey, AES);
        cipher = Cipher.getInstance(AES);
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return attribute == null ? null : Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            LOGGER.error(e.getMessage(), e);
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            return dbData == null ? null : new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            LOGGER.error(e.getMessage(), e);
            throw new IllegalArgumentException(e);
        }
    }
}

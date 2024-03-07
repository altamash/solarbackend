##RSA Specific Encryption/Decryption Workflow
1. Generate a symmetric key
2. Encrypt the data with the symmetric key
3. Encrypt the symmetric key with rsa
4. Send the encrypted key and the data
5. Decrypt the encrypted symmetric key with rsa
6. Decrypt the data with the symmetric key

####Example

1. Generate Symmetric Key (AES with 128 bits)

        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(128); // The AES key size in number of bits
        SecretKey secKey = generator.generateKey();

2. Encrypt plain text using AES

        String plainText = "Please encrypt me urgently..."
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, secKey);
        byte[] byteCipherText = aesCipher.doFinal(plainText.getBytes());

3. Encrypt the key using RSA public key

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair keyPair = kpg.generateKeyPair();
    
        PublicKey puKey = keyPair.getPublic();
        PrivateKey prKey = keyPair.getPrivate();
    
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.PUBLIC_KEY, puKey);
        byte[] encryptedKey = cipher.doFinal(secKey.getEncoded()/*Seceret Key From Step 1*/);

4. Send encrypted data (byteCipherText) + encrypted AES Key (encryptedKey)

5. On the client side, decrypt symmetric key using RSA private key

        cipher.init(Cipher.PRIVATE_KEY, prKey);
        byte[] decryptedKey = cipher.doFinal(encryptedKey);

6. Decrypt the cipher text using decrypted symmetric key

        //Convert bytes to AES SecertKey
        SecretKey originalKey = new SecretKeySpec(decryptedKey , 0, decryptedKey .length, "AES");
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, originalKey);
        byte[] bytePlainText = aesCipher.doFinal(byteCipherText);
        String plainText = new String(bytePlainText);


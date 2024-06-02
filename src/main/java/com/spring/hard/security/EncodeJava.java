package com.spring.hard.security;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;

public class EncodeJava {

    public static String getPrivateKey(){
        java.security.KeyPair keyPair = generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    public static String getPublicKey(){
        java.security.KeyPair keyPair = generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public static java.security.KeyPair generateKeyPair(){
        KeyPairGenerator keyPairGenerator=null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return keyPairGenerator.generateKeyPair();
    }

    public static String encrypt(String message, String  publicKeyString){
        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString.getBytes());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            PublicKey publicKey =  keyFactory.generatePublic(publicKeySpec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(message.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
        }
        return null;
    }

    public static String decrypt(String encryptedMessage, String privateKeyString){
        try {
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString.getBytes());
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey= keyFactory.generatePrivate(privateKeySpec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedMessage)), StandardCharsets.UTF_8);
        } catch (Exception e) {
        }
        return null;
    }

}
package com.org.worker.aspect;

import com.org.worker.exception.EncryptionException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class SimpleEncryptJavaAop {
    private static final String PATH = "secret/key";
    private static final String AES = "AES";

    public static String encrypt(String value) {
        try {
            return encrypt(value, new SecretKeySpec(Files.readAllBytes(Paths.get(PATH)), AES));
        } catch (IOException e) {
            throw new EncryptionException("Did not find any key, please generate new one", e);
        }
    }

    private static String encrypt(String value, SecretKeySpec secretKeySpec)  {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(AES);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new EncryptionException("Could not find cipher", e);
        }
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        } catch (InvalidKeyException e) {
            throw new EncryptionException("Could not init cipher", e);
        }
        try {
            return Base64.getEncoder().encodeToString(cipher.doFinal(value.getBytes()));
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new EncryptionException("Could not encrypt value", e);
        }
    }

    public static String decrypt(String value) {
        try {
            return decrypt(value, new SecretKeySpec(Files.readAllBytes(Paths.get(PATH)), AES));
        } catch (IOException e) {
            throw new EncryptionException("Did not find key, please generate one", e);
        }
    }

    private static String decrypt(String value, SecretKeySpec secretKeySpec)  {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new EncryptionException("Could not find cipher", e);
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        } catch (InvalidKeyException e) {
            throw new EncryptionException("Could not decrypt value", e);
        }
        try {
            return new String(cipher.doFinal(Base64.getDecoder().decode(value.getBytes())));
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new EncryptionException("Could not decrypt value", e);
        }
    }
}

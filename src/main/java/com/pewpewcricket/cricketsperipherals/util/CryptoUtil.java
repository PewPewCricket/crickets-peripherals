package com.pewpewcricket.cricketsperipherals.util;

import net.minecraft.world.entity.animal.Panda;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.ByteBuffer;
import java.security.*;

public class CryptoUtil {
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int AES_KEY_SIZE = 256;
    private static final int IV_LENGTH_BYTES = 12;
    private static final int TAG_LENGTH_BIT = 128;

    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(AES_KEY_SIZE);
        return keyGenerator.generateKey();
    }

    public static byte[] encrypt(byte[] text, SecretKey key) throws GeneralSecurityException {
        byte[] iv = new byte[IV_LENGTH_BYTES];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);

        byte[] cipherText = cipher.doFinal(text);

        // Prepend the IV to the ciphertext so it's available for decryption
        return ByteBuffer.allocate(iv.length + cipherText.length).put(iv).put(cipherText).array();
    }

    public static byte[] decrypt(byte[] encryptedText, SecretKey key) throws GeneralSecurityException {
        // Split the IV and ciphertext
        ByteBuffer byteBuffer = ByteBuffer.wrap(encryptedText);
        byte[] iv = new byte[IV_LENGTH_BYTES];
        byteBuffer.get(iv);
        byte[] cipherText = new byte[byteBuffer.remaining()];
        byteBuffer.get(cipherText);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

        return cipher.doFinal(cipherText);
    }
}

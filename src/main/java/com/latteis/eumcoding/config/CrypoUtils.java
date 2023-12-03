package com.latteis.eumcoding.config;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;

//암호화 및 복호화(카카오 accessToken저장할떄 암호화하기위해 만듦)
@Component
public class CrypoUtils {

    @Value("${secretKey}")
    private String tempKey;
    private static String KEY;

    @PostConstruct
    private void init() {
        CrypoUtils.KEY = tempKey;
    }


    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int IV_SIZE = 16; // AES 블록 크기와 동일

    public static String encrypt(String value) throws Exception {
        SecretKey key = new SecretKeySpec(KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv); // 무작위 IV 생성
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
        byte[] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
        String encodedIV = Base64.getEncoder().encodeToString(iv);
        String encodedCipherText = Base64.getEncoder().encodeToString(encryptedByteValue);

        return encodedIV + ":" + encodedCipherText;
    }

    public static String decrypt(String encryptedValue) throws Exception {
        SecretKey key = new SecretKeySpec(KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);

        String[] parts = encryptedValue.split(":");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(Base64.getDecoder().decode(parts[0]));

        cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
        byte[] decryptedByteValue = cipher.doFinal(Base64.getDecoder().decode(parts[1]));
        return new String(decryptedByteValue, "utf-8");
    }
}

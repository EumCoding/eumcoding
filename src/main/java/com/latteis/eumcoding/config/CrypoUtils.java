package com.latteis.eumcoding.config;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
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

    private static final String ALGORITHM = "AES";
    //private static final byte[] KEY = "비밀키직접입력, application에다 빼놓음".getBytes();  //AES는 128, 192, 256 비트의 키 길이를 지원


    public static String encrypt(String value) throws Exception{
        SecretKey key = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM,"BC");
        cipher.init(Cipher.ENCRYPT_MODE,key);

        byte[] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
        return Base64.getEncoder().encodeToString(encryptedByteValue);
    }

    public static String decrypt(String value) throws Exception{
        SecretKey key = new SecretKeySpec(KEY.getBytes(),ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM,"BC");
        cipher.init(Cipher.DECRYPT_MODE,key);

        byte[] decryptedValue64 = Base64.getDecoder().decode(value);
        byte[] decryptedByteValue = cipher.doFinal(decryptedValue64);
        return new String(decryptedByteValue, "utf-8");
    }

}

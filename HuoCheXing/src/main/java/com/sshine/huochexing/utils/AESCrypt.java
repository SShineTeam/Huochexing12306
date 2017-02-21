package com.sshine.huochexing.utils;

import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

public class AESCrypt {
    private Cipher cipher;
    private SecretKeySpec key;
    private AlgorithmParameterSpec spec;
    public AESCrypt(){
        // hash password with SHA-256 and crop the output to 128-bit for key
        MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			digest.update(getSeedStr().getBytes("UTF-8"));
			byte[] keyBytes = new byte[32];
			System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);
			cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
			key = new SecretKeySpec(keyBytes, "AES");
			spec = getIV();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    public String getSeedStr(){
    	String str1 = getSeedChar();
    	str1 = str1.substring(0, 5) + "9.KM"+str1.substring(5);
    	return str1;
    }
    native String getSeedChar();
    public AlgorithmParameterSpec getIV() {
        byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, };
        IvParameterSpec ivParameterSpec;
        ivParameterSpec = new IvParameterSpec(iv);
        return ivParameterSpec;
    }
    public String encrypt(String plainText) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
        String encryptedText = new String(Base64.encode(encrypted,
                Base64.DEFAULT), "UTF-8");
        return encryptedText;
    }
    public String decrypt(String cryptedText) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] bytes = Base64.decode(cryptedText, Base64.DEFAULT);
        byte[] decrypted = cipher.doFinal(bytes);
        String decryptedText = new String(decrypted, "UTF-8");
        return decryptedText;
    }
    static {
        System.loadLibrary("secret_helper");
    }
}
package com.sshine.huochexing.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Encryptor {
	//MD5加密
	public static String getMD5(String val) throws Exception {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		String val1 = (new AESCrypt()).getSeedStr()+val;
		md5.update(val1.getBytes());
		byte[] m = md5.digest();// 加密
		return getString(m);
	}

	private static String getString(byte[] b) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			sb.append(b[i]);
		}
		return sb.toString();
	}
}

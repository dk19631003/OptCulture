package org.mq.loyality.utils;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class EncryptDecryptLtyMembshpPwd {

	private static final char[] PASSWORD = "enfldsgbnlsngdlksdsgm".toCharArray();
	private static final byte[] SALT = {
		(byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
		(byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
	};
	public static String encryptMemPassword(String property) throws Exception {
		/*SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
		SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
		Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
		return base64Encode(pbeCipher.doFinal(property.getBytes("UTF-8")));*/
		String encryptedPwd = BCrypt.hashpw(property, BCrypt.gensalt());   
	 	return encryptedPwd;
	}
	public static String encryptCookie(String property) throws Exception {
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
		SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
		Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
		return base64Encode(pbeCipher.doFinal(property.getBytes("UTF-8")));
	}

	private static String base64Encode(byte[] bytes)  throws Exception {
		return new String(Base64.encodeBase64(bytes));
	}
	public static String decryptCookie(String property) throws Exception {
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
		SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
		Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
		return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
	}
	/*public static String decrypt(String property) throws Exception {
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
		SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
		Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
		return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
	}*/

	private static byte[] base64Decode(String property) throws Exception {
		return Base64.decodeBase64(property.getBytes());
	}
	
}

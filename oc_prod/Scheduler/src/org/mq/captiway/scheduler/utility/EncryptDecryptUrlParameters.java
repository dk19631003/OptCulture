package org.mq.captiway.scheduler.utility;

import java.net.URLEncoder;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class EncryptDecryptUrlParameters {

	private static final String ALGO = "AES";
	private static final byte[] keyValue = new byte[] { 'T', 'h', 'e', 'B',
		'e', 's', 't', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y' };

	public static  String encrypt(String Data) throws Exception {
		try {
			Key key = generateKey();
			Cipher c = Cipher.getInstance(ALGO);
			c.init(Cipher.ENCRYPT_MODE, key);
			byte[] encVal = c.doFinal(Data.getBytes());
			byte[] base64Encoded = Base64.encodeBase64(encVal);
			String base64String = new String(base64Encoded);
			String urlEncodedData = URLEncoder.encode(base64String,"UTF-8");
			return urlEncodedData;
		} catch (Exception e) {
			throw new Exception("Exception while encrypting::" , e);
		}
	}

	public static String decrypt(String encryptedData) throws Exception {
		try {
			Key key = generateKey();
			Cipher c = Cipher.getInstance(ALGO);
			c.init(Cipher.DECRYPT_MODE, key);
			//        String urlDecodedData = URLDecoder.decode(encryptedData,"UTF-8");
			byte[] decordedValue = Base64.decodeBase64(encryptedData.getBytes()); 
			byte[] decValue = c.doFinal(decordedValue);
			String decryptedValue = new String(decValue);
			return decryptedValue;
		} catch (Exception e) {
			throw new Exception("Exception while decrypting::" , e);
		}
	}

	private static Key generateKey() throws Exception {
		Key key = new SecretKeySpec(keyValue, ALGO);
		return key;
	}

}


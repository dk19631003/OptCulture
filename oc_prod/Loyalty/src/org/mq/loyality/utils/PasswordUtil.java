package org.mq.loyality.utils;


import org.springframework.security.authentication.encoding.ShaPasswordEncoder;



/**
 * Password Util Class - Contains the encode and compare PWD APIs
 * @author Swapna Ayyalaraju
 * 
 *
 */
public class PasswordUtil {

	ShaPasswordEncoder encoder;

	public ShaPasswordEncoder getEncoder() {
		return encoder;
	}

	public void setEncoder(ShaPasswordEncoder encoder) {
		this.encoder = encoder;
	}

	/**
	 * Encodes the given password
	 * @param pwd
	 * @return
	 */
	public String encodePassword(String pwd) {

		String encpwd = this.encoder.encodePassword(pwd, null);
		return encpwd;
	}

	/**
	 * Compares given 2 passwords,returns to true if equal
	 * @param dbPassword
	 * @param uiPassword
	 * @return
	 */
	public boolean comparePassword(String dbPassword, String uiPassword) {

		if (this.encoder.isPasswordValid(dbPassword, uiPassword, null)) {
			return true;
		} else
			return false;
	}

}

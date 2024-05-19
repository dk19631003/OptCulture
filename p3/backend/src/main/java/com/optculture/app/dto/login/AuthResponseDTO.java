package com.optculture.app.dto.login;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponseDTO {
	
	private String token;
	private List<Map<String, String>> userAbilities;
	private UserData userData;
	private boolean pwdMatch;
	private Long expirationTime;
	public AuthResponseDTO(String token,boolean passwordMatch) {
		super();
		this.token = token;
		this.pwdMatch=passwordMatch;
	}

	/*public AuthResponseDTO(String token) {
		super();
		this.token = token;
	}

	public String getToken() {
		return token;
	}*/

}

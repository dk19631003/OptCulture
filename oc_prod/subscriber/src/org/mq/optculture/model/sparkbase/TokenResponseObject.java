package org.mq.optculture.model.sparkbase;

import org.mq.optculture.model.BaseResponseObject;

public class TokenResponseObject  extends BaseResponseObject{
	
	private TokenResponse TokenResponse;
	
	public TokenResponseObject() {
	}

	public TokenResponse getTokenResponse() {
		return TokenResponse;
	}

	public void setTokenResponse(TokenResponse tokenResponse) {
		TokenResponse = tokenResponse;
	}

}

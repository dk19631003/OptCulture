package org.mq.optculture.model.sparkbase;

public class TokenResponse {
private Body body;
private Head head;

public TokenResponse() {
}

public TokenResponse(Head hEAD) {
	head = hEAD;
}

public TokenResponse(Body bODY, Head hEAD) {
	body = bODY;
	head = hEAD;
}
public Body getBody() {
	return body;
}
public void setBody(Body bODY) {
	body = bODY;
}
public Head getHead() {
	return head;
}
public void setHead(Head hEAD) {
	head = hEAD;
}

}

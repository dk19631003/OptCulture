package org.mq.optculture.model.beeContentApi;

public class PdfResponse {

	private String statusCode;
	private Body body;

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
}

/*
{"statusCode":200,"body":{"page_size":"Full","page_orientation":"portrait",
"content_type":"application/pdf",
"url":"https://pro-bee-beepro-pdf.s3.amazonaws.com/public/pdf/A3clJLncJB.pdf","filename":"A3clJLncJB.pdf"}}
*/
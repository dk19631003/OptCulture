package org.mq.optculture.model.mobileapp;

import org.mq.optculture.model.faq.Status;

public class FaqResponse {
	private String engContent;
	private String spanishContent;
	private Status status;
	private String engTermsContent;
	private String spanishTermsContent;
	
	public String getSpanishTermsContent() {
		return spanishTermsContent;
	}

	public void setSpanishTermsContent(String spanishTermsContent) {
		this.spanishTermsContent = spanishTermsContent;
	}

	public String getEngTermsContent() {
		return engTermsContent;
	}

	public void setEngTermsContent(String engTermsContent) {
		this.engTermsContent = engTermsContent;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getEngContent() {
		return engContent;
	}

	public void setEngContent(String engContent) {
		this.engContent = engContent;
	}

	public String getSpanishContent() {
		return spanishContent;
	}

	public void setSpanishContent(String spanishContent) {
		this.spanishContent = spanishContent;
	}
	
}
package org.mq.optculture.model.importcontact;

public class Suppress {

	private String emailAddress;
	private Email email;
	private String phonenumber;
	private Phone phone;

	public Suppress()
	{
		
	}
	public Suppress(Email email, Phone phone) {
		this.email = email;
		this.phone = phone;

	}

	public Email getEmail() {
		return email;
	}

	public void setEmail(Email email) {
		this.email = email;
	}

	public Phone getPhone() {
		return phone;
	}

	public void setPhone(Phone phone) {
		this.phone = phone;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}

}

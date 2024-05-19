package org.mq.optculture.model.updatecontacts;

public class Suppress {

	private Email email;
	private Phone phone;

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

}

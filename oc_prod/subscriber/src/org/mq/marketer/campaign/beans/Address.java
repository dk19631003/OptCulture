package org.mq.marketer.campaign.beans;

public class Address implements java.io.Serializable{
	
	private String addressOne;
    private String addressTwo;
    private String city;
    private String state;
    private String country;
    private String pin;
    private String phone;
    private String dinamicAddrstr;
    
    
    public String getDinamicAddrstr() {
		return dinamicAddrstr;
	}

	public void setDinamicAddrstr(String dinamicAddrstr) {
		this.dinamicAddrstr = dinamicAddrstr;
	}

	public Address(){
    }
    
    public Address(String dinamicAddrstr) {
    	
    	this.dinamicAddrstr = dinamicAddrstr;
    	
    }
    
	public Address(String addressOne, String addressTwo, String city,
			String state, String country, String pin, String phone) {
		
		this.addressOne = addressOne;
		this.addressTwo = addressTwo;
		this.city = city;
		this.state = state;
		this.country = country;
		this.pin = pin;
		this.phone = phone;
	}

	public String getAddressOne() {
		return addressOne;
	}
	public void setAddressOne(String addressOne) {
		this.addressOne = addressOne;
	}
	public String getAddressTwo() {
		return addressTwo;
	}
	public void setAddressTwo(String addressTwo) {
		this.addressTwo = addressTwo;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public String toString() {
		return addressOne+"::"+addressTwo+"::"+city+"::"+state+"::"+country+"::"+pin+"::"+phone;
	}
}

package org.mq.optculture.model.couponcodes;
import javax.xml.bind.annotation.XmlElement;
public class Customer {
	
	private String gender;
	private String first_name;
	private String last_name;
	private String email;
	private String phone;
	private String address;
	private String zip;
	private String city;
	private String birthday;
	private String custom_field_1;
	private String custom_field_2;
	private String custom_field_3;
	private String custom_field_4;
	private String custom_field_5;
	private String custom_field_6;
	private String custom_field_7;
	private String socialid;
	private String question_1;
	public String getGender() {
		return gender;
	}
	@XmlElement(name ="gender")
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getFirst_name() {
		return first_name;
	}
	@XmlElement(name ="first_name")
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	public String getLast_name() {
		return last_name;
	}
	@XmlElement(name ="last_name")
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	public String getEmail() {
		return email;
	}
	@XmlElement(name ="email")
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	@XmlElement(name ="phone")
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAddress() {
		return address;
	}
	@XmlElement(name ="address")
	public void setAddress(String address) {
		this.address = address;
	}
	public String getZip() {
		return zip;
	}
	@XmlElement(name ="zip")
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getCity() {
		return city;
	}
	@XmlElement(name ="city")
	public void setCity(String city) {
		this.city = city;
	}
	public String getBirthday() {
		return birthday;
	}
	@XmlElement(name ="birthday")
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getCustom_field_1() {
		return custom_field_1;
	}
	@XmlElement(name ="custom_field_1")
	public void setCustom_field_1(String custom_field_1) {
		this.custom_field_1 = custom_field_1;
	}
	public String getCustom_field_2() {
		return custom_field_2;
	}
	@XmlElement(name ="custom_field_2")
	public void setCustom_field_2(String custom_field_2) {
		this.custom_field_2 = custom_field_2;
	}
	public String getCustom_field_3() {
		return custom_field_3;
	}
	@XmlElement(name ="custom_field_3")
	public void setCustom_field_3(String custom_field_3) {
		this.custom_field_3 = custom_field_3;
	}
	public String getCustom_field_4() {
		return custom_field_4;
	}
	@XmlElement(name ="custom_field_4")
	public void setCustom_field_4(String custom_field_4) {
		this.custom_field_4 = custom_field_4;
	}
	public String getCustom_field_5() {
		return custom_field_5;
	}
	@XmlElement(name ="custom_field_5")
	public void setCustom_field_5(String custom_field_5) {
		this.custom_field_5 = custom_field_5;
	}
	public String getCustom_field_6() {
		return custom_field_6;
	}
	@XmlElement(name ="custom_field_6")
	public void setCustom_field_6(String custom_field_6) {
		this.custom_field_6 = custom_field_6;
	}
	public String getCustom_field_7() {
		return custom_field_7;
	}
	@XmlElement(name ="custom_field_7")
	public void setCustom_field_7(String custom_field_7) {
		this.custom_field_7 = custom_field_7;
	}
	public String getSocialid() {
		return socialid;
	}
	@XmlElement(name ="socialid")
	public void setSocialid(String socialid) {
		this.socialid = socialid;
	}
	public String getQuestion_1() {
		return question_1;
	}
	@XmlElement(name ="question_1")
	public void setQuestion_1(String question_1) {
		this.question_1 = question_1;
	}
	

}

package org.mq.captiway.sparkbase.sparkbaseAdminWsdl;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * A customer which represents information about the customer and not an account. However a customer can be linked to one or more accounts.
 * 
 * <p>Java class for Customer complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Customer">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="customerId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="programId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="firstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="middleName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="suffix" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="prefix" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="address1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="address2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="city" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="state" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="postal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="country" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="timeZone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mailPref" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="phone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mobilePhone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="altPhone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phonePref" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="altEmail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="emailPref" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="birthday" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="anniversary" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gender" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom4" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom5" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom6" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom7" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom8" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom9" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom10" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="facebookUserId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="facebookOauthToken" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="paycloudUserId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pcShareEmail" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pcShareAddress" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pcSharePhone" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pcNewOfferOptOverride" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="twitterOauthToken" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="twitterOauthSecre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Customer", propOrder = {

})
public class Customer {

    protected String customerId;
    protected String programId;
    protected String firstName;
    protected String middleName;
    protected String lastName;
    protected String suffix;
    protected String prefix;
    protected String address1;
    protected String address2;
    protected String city;
    protected String state;
    protected String postal;
    protected String country;
    protected String timeZone;
    protected Boolean mailPref;
    protected String phone;
    protected String mobilePhone;
    protected String altPhone;
    protected Boolean phonePref;
    protected String email;
    protected String altEmail;
    protected Boolean emailPref;
    protected String birthday;
    protected String anniversary;
    protected String gender;
    protected String userName;
    protected String password;
    protected String custom1;
    protected String custom2;
    protected String custom3;
    protected String custom4;
    protected String custom5;
    protected String custom6;
    protected String custom7;
    protected String custom8;
    protected String custom9;
    protected String custom10;
    protected String facebookUserId;
    protected String facebookOauthToken;
    protected String paycloudUserId;
    protected Boolean pcShareEmail;
    protected Boolean pcShareAddress;
    protected Boolean pcSharePhone;
    protected Boolean pcNewOfferOptOverride;
    protected String twitterOauthToken;
    protected String twitterOauthSecre;

    /**
     * Gets the value of the customerId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Sets the value of the customerId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerId(String value) {
        this.customerId = value;
    }

    /**
     * Gets the value of the programId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProgramId() {
        return programId;
    }

    /**
     * Sets the value of the programId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProgramId(String value) {
        this.programId = value;
    }

    /**
     * Gets the value of the firstName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the value of the firstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstName(String value) {
        this.firstName = value;
    }

    /**
     * Gets the value of the middleName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the value of the middleName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMiddleName(String value) {
        this.middleName = value;
    }

    /**
     * Gets the value of the lastName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the value of the lastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastName(String value) {
        this.lastName = value;
    }

    /**
     * Gets the value of the suffix property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * Sets the value of the suffix property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSuffix(String value) {
        this.suffix = value;
    }

    /**
     * Gets the value of the prefix property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sets the value of the prefix property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrefix(String value) {
        this.prefix = value;
    }

    /**
     * Gets the value of the address1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddress1() {
        return address1;
    }

    /**
     * Sets the value of the address1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddress1(String value) {
        this.address1 = value;
    }

    /**
     * Gets the value of the address2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddress2() {
        return address2;
    }

    /**
     * Sets the value of the address2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddress2(String value) {
        this.address2 = value;
    }

    /**
     * Gets the value of the city property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the value of the city property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCity(String value) {
        this.city = value;
    }

    /**
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setState(String value) {
        this.state = value;
    }

    /**
     * Gets the value of the postal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostal() {
        return postal;
    }

    /**
     * Sets the value of the postal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostal(String value) {
        this.postal = value;
    }

    /**
     * Gets the value of the country property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the value of the country property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountry(String value) {
        this.country = value;
    }

    /**
     * Gets the value of the timeZone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimeZone() {
        return timeZone;
    }

    /**
     * Sets the value of the timeZone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimeZone(String value) {
        this.timeZone = value;
    }

    /**
     * Gets the value of the mailPref property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMailPref() {
        return mailPref;
    }

    /**
     * Sets the value of the mailPref property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMailPref(Boolean value) {
        this.mailPref = value;
    }

    /**
     * Gets the value of the phone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the value of the phone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhone(String value) {
        this.phone = value;
    }

    /**
     * Gets the value of the mobilePhone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMobilePhone() {
        return mobilePhone;
    }

    /**
     * Sets the value of the mobilePhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMobilePhone(String value) {
        this.mobilePhone = value;
    }

    /**
     * Gets the value of the altPhone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAltPhone() {
        return altPhone;
    }

    /**
     * Sets the value of the altPhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAltPhone(String value) {
        this.altPhone = value;
    }

    /**
     * Gets the value of the phonePref property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPhonePref() {
        return phonePref;
    }

    /**
     * Sets the value of the phonePref property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPhonePref(Boolean value) {
        this.phonePref = value;
    }

    /**
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Gets the value of the altEmail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAltEmail() {
        return altEmail;
    }

    /**
     * Sets the value of the altEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAltEmail(String value) {
        this.altEmail = value;
    }

    /**
     * Gets the value of the emailPref property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEmailPref() {
        return emailPref;
    }

    /**
     * Sets the value of the emailPref property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEmailPref(Boolean value) {
        this.emailPref = value;
    }

    /**
     * Gets the value of the birthday property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBirthday() {
        return birthday;
    }

    /**
     * Sets the value of the birthday property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBirthday(String value) {
        this.birthday = value;
    }

    /**
     * Gets the value of the anniversary property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnniversary() {
        return anniversary;
    }

    /**
     * Sets the value of the anniversary property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnniversary(String value) {
        this.anniversary = value;
    }

    /**
     * Gets the value of the gender property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGender() {
        return gender;
    }

    /**
     * Sets the value of the gender property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGender(String value) {
        this.gender = value;
    }

    /**
     * Gets the value of the userName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the value of the userName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserName(String value) {
        this.userName = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Gets the value of the custom1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustom1() {
        return custom1;
    }

    /**
     * Sets the value of the custom1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustom1(String value) {
        this.custom1 = value;
    }

    /**
     * Gets the value of the custom2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustom2() {
        return custom2;
    }

    /**
     * Sets the value of the custom2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustom2(String value) {
        this.custom2 = value;
    }

    /**
     * Gets the value of the custom3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustom3() {
        return custom3;
    }

    /**
     * Sets the value of the custom3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustom3(String value) {
        this.custom3 = value;
    }

    /**
     * Gets the value of the custom4 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustom4() {
        return custom4;
    }

    /**
     * Sets the value of the custom4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustom4(String value) {
        this.custom4 = value;
    }

    /**
     * Gets the value of the custom5 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustom5() {
        return custom5;
    }

    /**
     * Sets the value of the custom5 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustom5(String value) {
        this.custom5 = value;
    }

    /**
     * Gets the value of the custom6 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustom6() {
        return custom6;
    }

    /**
     * Sets the value of the custom6 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustom6(String value) {
        this.custom6 = value;
    }

    /**
     * Gets the value of the custom7 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustom7() {
        return custom7;
    }

    /**
     * Sets the value of the custom7 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustom7(String value) {
        this.custom7 = value;
    }

    /**
     * Gets the value of the custom8 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustom8() {
        return custom8;
    }

    /**
     * Sets the value of the custom8 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustom8(String value) {
        this.custom8 = value;
    }

    /**
     * Gets the value of the custom9 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustom9() {
        return custom9;
    }

    /**
     * Sets the value of the custom9 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustom9(String value) {
        this.custom9 = value;
    }

    /**
     * Gets the value of the custom10 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustom10() {
        return custom10;
    }

    /**
     * Sets the value of the custom10 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustom10(String value) {
        this.custom10 = value;
    }

    /**
     * Gets the value of the facebookUserId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFacebookUserId() {
        return facebookUserId;
    }

    /**
     * Sets the value of the facebookUserId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFacebookUserId(String value) {
        this.facebookUserId = value;
    }

    /**
     * Gets the value of the facebookOauthToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFacebookOauthToken() {
        return facebookOauthToken;
    }

    /**
     * Sets the value of the facebookOauthToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFacebookOauthToken(String value) {
        this.facebookOauthToken = value;
    }

    /**
     * Gets the value of the paycloudUserId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaycloudUserId() {
        return paycloudUserId;
    }

    /**
     * Sets the value of the paycloudUserId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaycloudUserId(String value) {
        this.paycloudUserId = value;
    }

    /**
     * Gets the value of the pcShareEmail property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPcShareEmail() {
        return pcShareEmail;
    }

    /**
     * Sets the value of the pcShareEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPcShareEmail(Boolean value) {
        this.pcShareEmail = value;
    }

    /**
     * Gets the value of the pcShareAddress property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPcShareAddress() {
        return pcShareAddress;
    }

    /**
     * Sets the value of the pcShareAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPcShareAddress(Boolean value) {
        this.pcShareAddress = value;
    }

    /**
     * Gets the value of the pcSharePhone property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPcSharePhone() {
        return pcSharePhone;
    }

    /**
     * Sets the value of the pcSharePhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPcSharePhone(Boolean value) {
        this.pcSharePhone = value;
    }

    /**
     * Gets the value of the pcNewOfferOptOverride property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPcNewOfferOptOverride() {
        return pcNewOfferOptOverride;
    }

    /**
     * Sets the value of the pcNewOfferOptOverride property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPcNewOfferOptOverride(Boolean value) {
        this.pcNewOfferOptOverride = value;
    }

    /**
     * Gets the value of the twitterOauthToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTwitterOauthToken() {
        return twitterOauthToken;
    }

    /**
     * Sets the value of the twitterOauthToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTwitterOauthToken(String value) {
        this.twitterOauthToken = value;
    }

    /**
     * Gets the value of the twitterOauthSecre property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTwitterOauthSecre() {
        return twitterOauthSecre;
    }

    /**
     * Sets the value of the twitterOauthSecre property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTwitterOauthSecre(String value) {
        this.twitterOauthSecre = value;
    }

}


package org.mq.marketer.sparkbase.sparkbaseAdminWsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * A location which represents a physical or virtual store or merchant.
 * 
 * <p>Java class for Location complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Location">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="locationId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idPos" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="crossRefId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="crossRefName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="virtual" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="address1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="address2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="city" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="state" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="postal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="country" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="timeZone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fax" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="website" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contactEmployeeId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="currency" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="issuerIdNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cardIdPercentRandom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom1Label" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom2Label" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom3Label" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom4" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom4Label" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom5" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom5Label" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="multipurseGroupId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="allExport" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="smsBroadcast" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="emailBroadcast" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="perSmsFee" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="franchiseId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="integrationAuth" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="integrationPassword" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="accountingId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="bankRoutingId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="bankAccountId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="autoBillingFrequency" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hotNote" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="paycloudOptIn" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="paycloudProgramId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="audioReader" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="qrcReader" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="twitterUserUame" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="twitterOauthToken" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="twitterOauthSecret" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Location", propOrder = {

})
public class Location {

    protected String locationId;
    protected String idPos;
    protected String crossRefId;
    protected String crossRefName;
    protected String name;
    protected String type;
    protected String status;
    protected Boolean virtual;
    protected String address1;
    protected String address2;
    protected String city;
    protected String state;
    protected String postal;
    protected String country;
    protected String timeZone;
    protected String phone;
    protected String fax;
    protected String email;
    protected String website;
    protected String contactEmployeeId;
    protected String currency;
    protected String issuerIdNumber;
    protected String cardIdPercentRandom;
    protected String custom1;
    protected String custom1Label;
    protected String custom2;
    protected String custom2Label;
    protected String custom3;
    protected String custom3Label;
    protected String custom4;
    protected String custom4Label;
    protected String custom5;
    protected String custom5Label;
    protected String multipurseGroupId;
    protected Boolean allExport;
    protected Boolean smsBroadcast;
    protected Boolean emailBroadcast;
    protected String perSmsFee;
    protected String franchiseId;
    protected String integrationAuth;
    protected String integrationPassword;
    protected String accountingId;
    protected String bankRoutingId;
    protected String bankAccountId;
    protected String autoBillingFrequency;
    protected String hotNote;
    protected Boolean paycloudOptIn;
    protected String paycloudProgramId;
    protected Boolean audioReader;
    protected Boolean qrcReader;
    protected String twitterUserUame;
    protected String twitterOauthToken;
    protected String twitterOauthSecret;

    /**
     * Gets the value of the locationId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocationId() {
        return locationId;
    }

    /**
     * Sets the value of the locationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocationId(String value) {
        this.locationId = value;
    }

    /**
     * Gets the value of the idPos property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdPos() {
        return idPos;
    }

    /**
     * Sets the value of the idPos property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdPos(String value) {
        this.idPos = value;
    }

    /**
     * Gets the value of the crossRefId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCrossRefId() {
        return crossRefId;
    }

    /**
     * Sets the value of the crossRefId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCrossRefId(String value) {
        this.crossRefId = value;
    }

    /**
     * Gets the value of the crossRefName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCrossRefName() {
        return crossRefName;
    }

    /**
     * Sets the value of the crossRefName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCrossRefName(String value) {
        this.crossRefName = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the virtual property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isVirtual() {
        return virtual;
    }

    /**
     * Sets the value of the virtual property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setVirtual(Boolean value) {
        this.virtual = value;
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
     * Gets the value of the fax property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFax() {
        return fax;
    }

    /**
     * Sets the value of the fax property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFax(String value) {
        this.fax = value;
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
     * Gets the value of the website property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWebsite() {
        return website;
    }

    /**
     * Sets the value of the website property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWebsite(String value) {
        this.website = value;
    }

    /**
     * Gets the value of the contactEmployeeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactEmployeeId() {
        return contactEmployeeId;
    }

    /**
     * Sets the value of the contactEmployeeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactEmployeeId(String value) {
        this.contactEmployeeId = value;
    }

    /**
     * Gets the value of the currency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the value of the currency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrency(String value) {
        this.currency = value;
    }

    /**
     * Gets the value of the issuerIdNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIssuerIdNumber() {
        return issuerIdNumber;
    }

    /**
     * Sets the value of the issuerIdNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIssuerIdNumber(String value) {
        this.issuerIdNumber = value;
    }

    /**
     * Gets the value of the cardIdPercentRandom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardIdPercentRandom() {
        return cardIdPercentRandom;
    }

    /**
     * Sets the value of the cardIdPercentRandom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardIdPercentRandom(String value) {
        this.cardIdPercentRandom = value;
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
     * Gets the value of the custom1Label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustom1Label() {
        return custom1Label;
    }

    /**
     * Sets the value of the custom1Label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustom1Label(String value) {
        this.custom1Label = value;
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
     * Gets the value of the custom2Label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustom2Label() {
        return custom2Label;
    }

    /**
     * Sets the value of the custom2Label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustom2Label(String value) {
        this.custom2Label = value;
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
     * Gets the value of the custom3Label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustom3Label() {
        return custom3Label;
    }

    /**
     * Sets the value of the custom3Label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustom3Label(String value) {
        this.custom3Label = value;
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
     * Gets the value of the custom4Label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustom4Label() {
        return custom4Label;
    }

    /**
     * Sets the value of the custom4Label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustom4Label(String value) {
        this.custom4Label = value;
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
     * Gets the value of the custom5Label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustom5Label() {
        return custom5Label;
    }

    /**
     * Sets the value of the custom5Label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustom5Label(String value) {
        this.custom5Label = value;
    }

    /**
     * Gets the value of the multipurseGroupId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMultipurseGroupId() {
        return multipurseGroupId;
    }

    /**
     * Sets the value of the multipurseGroupId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMultipurseGroupId(String value) {
        this.multipurseGroupId = value;
    }

    /**
     * Gets the value of the allExport property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAllExport() {
        return allExport;
    }

    /**
     * Sets the value of the allExport property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAllExport(Boolean value) {
        this.allExport = value;
    }

    /**
     * Gets the value of the smsBroadcast property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSmsBroadcast() {
        return smsBroadcast;
    }

    /**
     * Sets the value of the smsBroadcast property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSmsBroadcast(Boolean value) {
        this.smsBroadcast = value;
    }

    /**
     * Gets the value of the emailBroadcast property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEmailBroadcast() {
        return emailBroadcast;
    }

    /**
     * Sets the value of the emailBroadcast property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEmailBroadcast(Boolean value) {
        this.emailBroadcast = value;
    }

    /**
     * Gets the value of the perSmsFee property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPerSmsFee() {
        return perSmsFee;
    }

    /**
     * Sets the value of the perSmsFee property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPerSmsFee(String value) {
        this.perSmsFee = value;
    }

    /**
     * Gets the value of the franchiseId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFranchiseId() {
        return franchiseId;
    }

    /**
     * Sets the value of the franchiseId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFranchiseId(String value) {
        this.franchiseId = value;
    }

    /**
     * Gets the value of the integrationAuth property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIntegrationAuth() {
        return integrationAuth;
    }

    /**
     * Sets the value of the integrationAuth property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIntegrationAuth(String value) {
        this.integrationAuth = value;
    }

    /**
     * Gets the value of the integrationPassword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIntegrationPassword() {
        return integrationPassword;
    }

    /**
     * Sets the value of the integrationPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIntegrationPassword(String value) {
        this.integrationPassword = value;
    }

    /**
     * Gets the value of the accountingId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountingId() {
        return accountingId;
    }

    /**
     * Sets the value of the accountingId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccountingId(String value) {
        this.accountingId = value;
    }

    /**
     * Gets the value of the bankRoutingId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBankRoutingId() {
        return bankRoutingId;
    }

    /**
     * Sets the value of the bankRoutingId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBankRoutingId(String value) {
        this.bankRoutingId = value;
    }

    /**
     * Gets the value of the bankAccountId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBankAccountId() {
        return bankAccountId;
    }

    /**
     * Sets the value of the bankAccountId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBankAccountId(String value) {
        this.bankAccountId = value;
    }

    /**
     * Gets the value of the autoBillingFrequency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAutoBillingFrequency() {
        return autoBillingFrequency;
    }

    /**
     * Sets the value of the autoBillingFrequency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAutoBillingFrequency(String value) {
        this.autoBillingFrequency = value;
    }

    /**
     * Gets the value of the hotNote property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHotNote() {
        return hotNote;
    }

    /**
     * Sets the value of the hotNote property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHotNote(String value) {
        this.hotNote = value;
    }

    /**
     * Gets the value of the paycloudOptIn property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPaycloudOptIn() {
        return paycloudOptIn;
    }

    /**
     * Sets the value of the paycloudOptIn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPaycloudOptIn(Boolean value) {
        this.paycloudOptIn = value;
    }

    /**
     * Gets the value of the paycloudProgramId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaycloudProgramId() {
        return paycloudProgramId;
    }

    /**
     * Sets the value of the paycloudProgramId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaycloudProgramId(String value) {
        this.paycloudProgramId = value;
    }

    /**
     * Gets the value of the audioReader property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAudioReader() {
        return audioReader;
    }

    /**
     * Sets the value of the audioReader property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAudioReader(Boolean value) {
        this.audioReader = value;
    }

    /**
     * Gets the value of the qrcReader property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isQrcReader() {
        return qrcReader;
    }

    /**
     * Sets the value of the qrcReader property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setQrcReader(Boolean value) {
        this.qrcReader = value;
    }

    /**
     * Gets the value of the twitterUserUame property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTwitterUserUame() {
        return twitterUserUame;
    }

    /**
     * Sets the value of the twitterUserUame property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTwitterUserUame(String value) {
        this.twitterUserUame = value;
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
     * Gets the value of the twitterOauthSecret property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTwitterOauthSecret() {
        return twitterOauthSecret;
    }

    /**
     * Sets the value of the twitterOauthSecret property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTwitterOauthSecret(String value) {
        this.twitterOauthSecret = value;
    }

}

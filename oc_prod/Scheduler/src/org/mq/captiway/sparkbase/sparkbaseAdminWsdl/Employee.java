package org.mq.captiway.sparkbase.sparkbaseAdminWsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * An employee which represents a person who can operate a pos system.
 * 
 * <p>Java class for Employee complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Employee">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="employeeId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idPos" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="firstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="middleName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="locationId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="passwordPos" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="administrator" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="accountView" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="accountEdit" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="cardSetView" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="cardSetEdit" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="customerView" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="customerEdit" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="employeeView" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="employeeEdit" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="locationView" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="locationEdit" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="smsBroadcast" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="emailBroadcast" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="transView" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="accountHistory" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="adjustment" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="enrollment" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="giftIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="giftRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="inquiry" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="loyaltyIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="loyaltyRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="multipleIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="promoIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="promoRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="renewal" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="return" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="tip" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="transfer" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="void" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="employeeReport" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="terminalReport" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Employee", propOrder = {

})
public class Employee {

    protected String employeeId;
    protected String idPos;
    protected String firstName;
    protected String middleName;
    protected String lastName;
    protected String locationId;
    protected String status;
    protected String password;
    protected String passwordPos;
    protected String email;
    protected String phone;
    protected Boolean administrator;
    protected Boolean accountView;
    protected Boolean accountEdit;
    protected Boolean cardSetView;
    protected Boolean cardSetEdit;
    protected Boolean customerView;
    protected Boolean customerEdit;
    protected Boolean employeeView;
    protected Boolean employeeEdit;
    protected Boolean locationView;
    protected Boolean locationEdit;
    protected Boolean smsBroadcast;
    protected String emailBroadcast;
    protected Boolean transView;
    protected Boolean accountHistory;
    protected Boolean adjustment;
    protected Boolean enrollment;
    protected Boolean giftIssuance;
    protected Boolean giftRedemption;
    protected Boolean inquiry;
    protected Boolean loyaltyIssuance;
    protected Boolean loyaltyRedemption;
    protected Boolean multipleIssuance;
    protected Boolean promoIssuance;
    protected Boolean promoRedemption;
    protected Boolean renewal;
    @XmlElement(name = "return")
    protected Boolean _return;
    protected Boolean tip;
    protected Boolean transfer;
    @XmlElement(name = "void")
    protected Boolean _void;
    protected Boolean employeeReport;
    protected Boolean terminalReport;

    /**
     * Gets the value of the employeeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmployeeId() {
        return employeeId;
    }

    /**
     * Sets the value of the employeeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmployeeId(String value) {
        this.employeeId = value;
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
     * Gets the value of the passwordPos property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPasswordPos() {
        return passwordPos;
    }

    /**
     * Sets the value of the passwordPos property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPasswordPos(String value) {
        this.passwordPos = value;
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
     * Gets the value of the administrator property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAdministrator() {
        return administrator;
    }

    /**
     * Sets the value of the administrator property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAdministrator(Boolean value) {
        this.administrator = value;
    }

    /**
     * Gets the value of the accountView property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAccountView() {
        return accountView;
    }

    /**
     * Sets the value of the accountView property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAccountView(Boolean value) {
        this.accountView = value;
    }

    /**
     * Gets the value of the accountEdit property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAccountEdit() {
        return accountEdit;
    }

    /**
     * Sets the value of the accountEdit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAccountEdit(Boolean value) {
        this.accountEdit = value;
    }

    /**
     * Gets the value of the cardSetView property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCardSetView() {
        return cardSetView;
    }

    /**
     * Sets the value of the cardSetView property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCardSetView(Boolean value) {
        this.cardSetView = value;
    }

    /**
     * Gets the value of the cardSetEdit property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCardSetEdit() {
        return cardSetEdit;
    }

    /**
     * Sets the value of the cardSetEdit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCardSetEdit(Boolean value) {
        this.cardSetEdit = value;
    }

    /**
     * Gets the value of the customerView property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCustomerView() {
        return customerView;
    }

    /**
     * Sets the value of the customerView property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCustomerView(Boolean value) {
        this.customerView = value;
    }

    /**
     * Gets the value of the customerEdit property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCustomerEdit() {
        return customerEdit;
    }

    /**
     * Sets the value of the customerEdit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCustomerEdit(Boolean value) {
        this.customerEdit = value;
    }

    /**
     * Gets the value of the employeeView property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEmployeeView() {
        return employeeView;
    }

    /**
     * Sets the value of the employeeView property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEmployeeView(Boolean value) {
        this.employeeView = value;
    }

    /**
     * Gets the value of the employeeEdit property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEmployeeEdit() {
        return employeeEdit;
    }

    /**
     * Sets the value of the employeeEdit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEmployeeEdit(Boolean value) {
        this.employeeEdit = value;
    }

    /**
     * Gets the value of the locationView property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isLocationView() {
        return locationView;
    }

    /**
     * Sets the value of the locationView property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLocationView(Boolean value) {
        this.locationView = value;
    }

    /**
     * Gets the value of the locationEdit property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isLocationEdit() {
        return locationEdit;
    }

    /**
     * Sets the value of the locationEdit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLocationEdit(Boolean value) {
        this.locationEdit = value;
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
     *     {@link String }
     *     
     */
    public String getEmailBroadcast() {
        return emailBroadcast;
    }

    /**
     * Sets the value of the emailBroadcast property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmailBroadcast(String value) {
        this.emailBroadcast = value;
    }

    /**
     * Gets the value of the transView property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTransView() {
        return transView;
    }

    /**
     * Sets the value of the transView property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTransView(Boolean value) {
        this.transView = value;
    }

    /**
     * Gets the value of the accountHistory property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAccountHistory() {
        return accountHistory;
    }

    /**
     * Sets the value of the accountHistory property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAccountHistory(Boolean value) {
        this.accountHistory = value;
    }

    /**
     * Gets the value of the adjustment property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAdjustment() {
        return adjustment;
    }

    /**
     * Sets the value of the adjustment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAdjustment(Boolean value) {
        this.adjustment = value;
    }

    /**
     * Gets the value of the enrollment property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEnrollment() {
        return enrollment;
    }

    /**
     * Sets the value of the enrollment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEnrollment(Boolean value) {
        this.enrollment = value;
    }

    /**
     * Gets the value of the giftIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isGiftIssuance() {
        return giftIssuance;
    }

    /**
     * Sets the value of the giftIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setGiftIssuance(Boolean value) {
        this.giftIssuance = value;
    }

    /**
     * Gets the value of the giftRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isGiftRedemption() {
        return giftRedemption;
    }

    /**
     * Sets the value of the giftRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setGiftRedemption(Boolean value) {
        this.giftRedemption = value;
    }

    /**
     * Gets the value of the inquiry property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isInquiry() {
        return inquiry;
    }

    /**
     * Sets the value of the inquiry property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setInquiry(Boolean value) {
        this.inquiry = value;
    }

    /**
     * Gets the value of the loyaltyIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isLoyaltyIssuance() {
        return loyaltyIssuance;
    }

    /**
     * Sets the value of the loyaltyIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLoyaltyIssuance(Boolean value) {
        this.loyaltyIssuance = value;
    }

    /**
     * Gets the value of the loyaltyRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isLoyaltyRedemption() {
        return loyaltyRedemption;
    }

    /**
     * Sets the value of the loyaltyRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLoyaltyRedemption(Boolean value) {
        this.loyaltyRedemption = value;
    }

    /**
     * Gets the value of the multipleIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMultipleIssuance() {
        return multipleIssuance;
    }

    /**
     * Sets the value of the multipleIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMultipleIssuance(Boolean value) {
        this.multipleIssuance = value;
    }

    /**
     * Gets the value of the promoIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPromoIssuance() {
        return promoIssuance;
    }

    /**
     * Sets the value of the promoIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPromoIssuance(Boolean value) {
        this.promoIssuance = value;
    }

    /**
     * Gets the value of the promoRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPromoRedemption() {
        return promoRedemption;
    }

    /**
     * Sets the value of the promoRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPromoRedemption(Boolean value) {
        this.promoRedemption = value;
    }

    /**
     * Gets the value of the renewal property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRenewal() {
        return renewal;
    }

    /**
     * Sets the value of the renewal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRenewal(Boolean value) {
        this.renewal = value;
    }

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReturn(Boolean value) {
        this._return = value;
    }

    /**
     * Gets the value of the tip property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTip() {
        return tip;
    }

    /**
     * Sets the value of the tip property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTip(Boolean value) {
        this.tip = value;
    }

    /**
     * Gets the value of the transfer property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTransfer() {
        return transfer;
    }

    /**
     * Sets the value of the transfer property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTransfer(Boolean value) {
        this.transfer = value;
    }

    /**
     * Gets the value of the void property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isVoid() {
        return _void;
    }

    /**
     * Sets the value of the void property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setVoid(Boolean value) {
        this._void = value;
    }

    /**
     * Gets the value of the employeeReport property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEmployeeReport() {
        return employeeReport;
    }

    /**
     * Sets the value of the employeeReport property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEmployeeReport(Boolean value) {
        this.employeeReport = value;
    }

    /**
     * Gets the value of the terminalReport property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTerminalReport() {
        return terminalReport;
    }

    /**
     * Sets the value of the terminalReport property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTerminalReport(Boolean value) {
        this.terminalReport = value;
    }

}

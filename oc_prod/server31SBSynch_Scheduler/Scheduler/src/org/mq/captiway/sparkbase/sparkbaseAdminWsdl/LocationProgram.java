package org.mq.captiway.sparkbase.sparkbaseAdminWsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A location program association which represents a program that a location belongs to and what permissions that program has with in that location.
 * 
 * <p>Java class for LocationProgram complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LocationProgram">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="locationId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="programId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="locationName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="programName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="accountEdit" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="accountView" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="cardSetEdit" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="cardSetView" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="customerEdit" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="customerView" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
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
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LocationProgram", propOrder = {

})
public class LocationProgram {

    protected String locationId;
    protected String programId;
    protected String locationName;
    protected String programName;
    protected Boolean accountEdit;
    protected Boolean accountView;
    protected Boolean cardSetEdit;
    protected Boolean cardSetView;
    protected Boolean customerEdit;
    protected Boolean customerView;
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
     * Gets the value of the locationName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocationName() {
        return locationName;
    }

    /**
     * Sets the value of the locationName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocationName(String value) {
        this.locationName = value;
    }

    /**
     * Gets the value of the programName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProgramName() {
        return programName;
    }

    /**
     * Sets the value of the programName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProgramName(String value) {
        this.programName = value;
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

}

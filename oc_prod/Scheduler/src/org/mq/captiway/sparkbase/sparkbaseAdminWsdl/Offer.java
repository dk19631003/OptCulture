package org.mq.captiway.sparkbase.sparkbaseAdminWsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * An offer used to give some reward to the customer.
 * 
 * <p>Java class for Offer complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Offer">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="offerId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="programId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="target" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="percentage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="valueTypeId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="triggerAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="triggerValueTypeId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="promoCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="minimumTransactionAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="maximumTransactionAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="minimumRewardAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="maximumRewardAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="maximumLifetimeAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="daysAfter" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="effectiveDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="expirationDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="startTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="endTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="scheduleMonday" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="scheduleTuesday" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="scheduleWednesday" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="scheduleThursday" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="scheduleFriday" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="scheduleSaturday" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="scheduleSunday" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Offer", propOrder = {

})
public class Offer {

    protected String offerId;
    protected String programId;
    protected String name;
    protected String description;
    protected String type;
    protected String target;
    protected String percentage;
    protected String amount;
    protected String valueTypeId;
    protected String triggerAmount;
    protected String triggerValueTypeId;
    protected String promoCode;
    protected String minimumTransactionAmount;
    protected String maximumTransactionAmount;
    protected String minimumRewardAmount;
    protected String maximumRewardAmount;
    protected String maximumLifetimeAmount;
    protected String daysAfter;
    protected String effectiveDate;
    protected String expirationDate;
    protected String startTime;
    protected String endTime;
    protected Boolean scheduleMonday;
    protected Boolean scheduleTuesday;
    protected Boolean scheduleWednesday;
    protected Boolean scheduleThursday;
    protected Boolean scheduleFriday;
    protected Boolean scheduleSaturday;
    protected Boolean scheduleSunday;

    /**
     * Gets the value of the offerId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOfferId() {
        return offerId;
    }

    /**
     * Sets the value of the offerId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOfferId(String value) {
        this.offerId = value;
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
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
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
     * Gets the value of the target property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTarget() {
        return target;
    }

    /**
     * Sets the value of the target property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTarget(String value) {
        this.target = value;
    }

    /**
     * Gets the value of the percentage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPercentage() {
        return percentage;
    }

    /**
     * Sets the value of the percentage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPercentage(String value) {
        this.percentage = value;
    }

    /**
     * Gets the value of the amount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAmount(String value) {
        this.amount = value;
    }

    /**
     * Gets the value of the valueTypeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValueTypeId() {
        return valueTypeId;
    }

    /**
     * Sets the value of the valueTypeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValueTypeId(String value) {
        this.valueTypeId = value;
    }

    /**
     * Gets the value of the triggerAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTriggerAmount() {
        return triggerAmount;
    }

    /**
     * Sets the value of the triggerAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTriggerAmount(String value) {
        this.triggerAmount = value;
    }

    /**
     * Gets the value of the triggerValueTypeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTriggerValueTypeId() {
        return triggerValueTypeId;
    }

    /**
     * Sets the value of the triggerValueTypeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTriggerValueTypeId(String value) {
        this.triggerValueTypeId = value;
    }

    /**
     * Gets the value of the promoCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPromoCode() {
        return promoCode;
    }

    /**
     * Sets the value of the promoCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPromoCode(String value) {
        this.promoCode = value;
    }

    /**
     * Gets the value of the minimumTransactionAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMinimumTransactionAmount() {
        return minimumTransactionAmount;
    }

    /**
     * Sets the value of the minimumTransactionAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMinimumTransactionAmount(String value) {
        this.minimumTransactionAmount = value;
    }

    /**
     * Gets the value of the maximumTransactionAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaximumTransactionAmount() {
        return maximumTransactionAmount;
    }

    /**
     * Sets the value of the maximumTransactionAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaximumTransactionAmount(String value) {
        this.maximumTransactionAmount = value;
    }

    /**
     * Gets the value of the minimumRewardAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMinimumRewardAmount() {
        return minimumRewardAmount;
    }

    /**
     * Sets the value of the minimumRewardAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMinimumRewardAmount(String value) {
        this.minimumRewardAmount = value;
    }

    /**
     * Gets the value of the maximumRewardAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaximumRewardAmount() {
        return maximumRewardAmount;
    }

    /**
     * Sets the value of the maximumRewardAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaximumRewardAmount(String value) {
        this.maximumRewardAmount = value;
    }

    /**
     * Gets the value of the maximumLifetimeAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaximumLifetimeAmount() {
        return maximumLifetimeAmount;
    }

    /**
     * Sets the value of the maximumLifetimeAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaximumLifetimeAmount(String value) {
        this.maximumLifetimeAmount = value;
    }

    /**
     * Gets the value of the daysAfter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDaysAfter() {
        return daysAfter;
    }

    /**
     * Sets the value of the daysAfter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDaysAfter(String value) {
        this.daysAfter = value;
    }

    /**
     * Gets the value of the effectiveDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEffectiveDate() {
        return effectiveDate;
    }

    /**
     * Sets the value of the effectiveDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEffectiveDate(String value) {
        this.effectiveDate = value;
    }

    /**
     * Gets the value of the expirationDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpirationDate() {
        return expirationDate;
    }

    /**
     * Sets the value of the expirationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpirationDate(String value) {
        this.expirationDate = value;
    }

    /**
     * Gets the value of the startTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Sets the value of the startTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStartTime(String value) {
        this.startTime = value;
    }

    /**
     * Gets the value of the endTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * Sets the value of the endTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndTime(String value) {
        this.endTime = value;
    }

    /**
     * Gets the value of the scheduleMonday property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isScheduleMonday() {
        return scheduleMonday;
    }

    /**
     * Sets the value of the scheduleMonday property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setScheduleMonday(Boolean value) {
        this.scheduleMonday = value;
    }

    /**
     * Gets the value of the scheduleTuesday property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isScheduleTuesday() {
        return scheduleTuesday;
    }

    /**
     * Sets the value of the scheduleTuesday property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setScheduleTuesday(Boolean value) {
        this.scheduleTuesday = value;
    }

    /**
     * Gets the value of the scheduleWednesday property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isScheduleWednesday() {
        return scheduleWednesday;
    }

    /**
     * Sets the value of the scheduleWednesday property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setScheduleWednesday(Boolean value) {
        this.scheduleWednesday = value;
    }

    /**
     * Gets the value of the scheduleThursday property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isScheduleThursday() {
        return scheduleThursday;
    }

    /**
     * Sets the value of the scheduleThursday property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setScheduleThursday(Boolean value) {
        this.scheduleThursday = value;
    }

    /**
     * Gets the value of the scheduleFriday property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isScheduleFriday() {
        return scheduleFriday;
    }

    /**
     * Sets the value of the scheduleFriday property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setScheduleFriday(Boolean value) {
        this.scheduleFriday = value;
    }

    /**
     * Gets the value of the scheduleSaturday property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isScheduleSaturday() {
        return scheduleSaturday;
    }

    /**
     * Sets the value of the scheduleSaturday property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setScheduleSaturday(Boolean value) {
        this.scheduleSaturday = value;
    }

    /**
     * Gets the value of the scheduleSunday property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isScheduleSunday() {
        return scheduleSunday;
    }

    /**
     * Sets the value of the scheduleSunday property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setScheduleSunday(Boolean value) {
        this.scheduleSunday = value;
    }

}

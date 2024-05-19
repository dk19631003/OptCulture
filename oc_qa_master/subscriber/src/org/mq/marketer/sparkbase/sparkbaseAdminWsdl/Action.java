
package org.mq.marketer.sparkbase.sparkbaseAdminWsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * An action which represents an event that is triggered when certain requirements are ment.
 * 
 * <p>Java class for Action complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Action">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="actionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="programId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="groupId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="beforeProcessing" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="replaceIssueRedeem" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="priority" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="skipActionsAfter" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="expirationIntervals" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="expirationIntervalType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="autoRecurRelAfter" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="autoRecurRelUnits" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="conversionBalIncrements" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="conversionRate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rejectErrorType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rejectErrorData" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="triggerRangeBegin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="triggerRangeEnd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="triggerScheduleId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="triggerScheduleRelativeTo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="triggerAfterActionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="triggerCardSetId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="triggerAccountHistory" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="triggerAdjustment" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="triggerEnrollment" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="triggerGiftIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="triggerGiftRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="triggerInquiry" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="triggerLoyaltyIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="triggerLoyaltyRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="triggerMultipleIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="triggerPromoIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="triggerPromoRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="triggerRenewal" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="triggerReturn" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="triggerTip" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="triggerTransfer" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="triggerVoid" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="triggerMaxPerTrans" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="triggerValField" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="triggerValMin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="triggerValMax" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="triggerValTypeId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="triggerRegCompletion" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="triggerRegistered" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="autoAdd" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="addAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="addPercent" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="addPercentOf" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="addPercentOfBalanceValueTypeId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="addPercentRound" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="addPercentAbsoluteVal" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="addValueTypeId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="addAsReturn" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="autoSubtract" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="subtractAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subtractPercent" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subtractPercentOf" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subtractPercentRound" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subtractValueTypeId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subtractNSFAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="subtractReturnBal" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="notifyTemplateReceipt40" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="notifyTemplateReceipt24" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="notifyExternalMedium" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="notifyExternalTemplate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="notify3rdPartyEmail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="notifyEmailSender" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Action", propOrder = {

})
public class Action {

    protected String actionId;
    protected String programId;
    protected String groupId;
    protected String name;
    protected String type;
    protected String status;
    protected Boolean beforeProcessing;
    protected Boolean replaceIssueRedeem;
    protected String priority;
    protected Boolean skipActionsAfter;
    protected String expirationIntervals;
    protected String expirationIntervalType;
    protected String autoRecurRelAfter;
    protected String autoRecurRelUnits;
    protected String conversionBalIncrements;
    protected String conversionRate;
    protected String rejectErrorType;
    protected String rejectErrorData;
    protected String triggerRangeBegin;
    protected String triggerRangeEnd;
    protected String triggerScheduleId;
    protected String triggerScheduleRelativeTo;
    protected String triggerAfterActionId;
    protected String triggerCardSetId;
    protected Boolean triggerAccountHistory;
    protected Boolean triggerAdjustment;
    protected Boolean triggerEnrollment;
    protected Boolean triggerGiftIssuance;
    protected Boolean triggerGiftRedemption;
    protected Boolean triggerInquiry;
    protected Boolean triggerLoyaltyIssuance;
    protected Boolean triggerLoyaltyRedemption;
    protected Boolean triggerMultipleIssuance;
    protected Boolean triggerPromoIssuance;
    protected Boolean triggerPromoRedemption;
    protected Boolean triggerRenewal;
    protected Boolean triggerReturn;
    protected Boolean triggerTip;
    protected Boolean triggerTransfer;
    protected Boolean triggerVoid;
    protected String triggerMaxPerTrans;
    protected String triggerValField;
    protected String triggerValMin;
    protected String triggerValMax;
    protected String triggerValTypeId;
    protected Boolean triggerRegCompletion;
    protected Boolean triggerRegistered;
    protected Boolean autoAdd;
    protected String addAmount;
    protected String addPercent;
    protected String addPercentOf;
    protected String addPercentOfBalanceValueTypeId;
    protected String addPercentRound;
    protected Boolean addPercentAbsoluteVal;
    protected String addValueTypeId;
    protected Boolean addAsReturn;
    protected Boolean autoSubtract;
    protected String subtractAmount;
    protected String subtractPercent;
    protected String subtractPercentOf;
    protected String subtractPercentRound;
    protected String subtractValueTypeId;
    protected Boolean subtractNSFAllowed;
    protected Boolean subtractReturnBal;
    protected String notifyTemplateReceipt40;
    protected String notifyTemplateReceipt24;
    protected String notifyExternalMedium;
    protected String notifyExternalTemplate;
    @XmlElement(name = "notify3rdPartyEmail")
    protected String notify3RdPartyEmail;
    protected String notifyEmailSender;

    /**
     * Gets the value of the actionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActionId() {
        return actionId;
    }

    /**
     * Sets the value of the actionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActionId(String value) {
        this.actionId = value;
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
     * Gets the value of the groupId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Sets the value of the groupId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroupId(String value) {
        this.groupId = value;
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
     * Gets the value of the beforeProcessing property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBeforeProcessing() {
        return beforeProcessing;
    }

    /**
     * Sets the value of the beforeProcessing property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBeforeProcessing(Boolean value) {
        this.beforeProcessing = value;
    }

    /**
     * Gets the value of the replaceIssueRedeem property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReplaceIssueRedeem() {
        return replaceIssueRedeem;
    }

    /**
     * Sets the value of the replaceIssueRedeem property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReplaceIssueRedeem(Boolean value) {
        this.replaceIssueRedeem = value;
    }

    /**
     * Gets the value of the priority property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Sets the value of the priority property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPriority(String value) {
        this.priority = value;
    }

    /**
     * Gets the value of the skipActionsAfter property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSkipActionsAfter() {
        return skipActionsAfter;
    }

    /**
     * Sets the value of the skipActionsAfter property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSkipActionsAfter(Boolean value) {
        this.skipActionsAfter = value;
    }

    /**
     * Gets the value of the expirationIntervals property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpirationIntervals() {
        return expirationIntervals;
    }

    /**
     * Sets the value of the expirationIntervals property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpirationIntervals(String value) {
        this.expirationIntervals = value;
    }

    /**
     * Gets the value of the expirationIntervalType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpirationIntervalType() {
        return expirationIntervalType;
    }

    /**
     * Sets the value of the expirationIntervalType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpirationIntervalType(String value) {
        this.expirationIntervalType = value;
    }

    /**
     * Gets the value of the autoRecurRelAfter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAutoRecurRelAfter() {
        return autoRecurRelAfter;
    }

    /**
     * Sets the value of the autoRecurRelAfter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAutoRecurRelAfter(String value) {
        this.autoRecurRelAfter = value;
    }

    /**
     * Gets the value of the autoRecurRelUnits property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAutoRecurRelUnits() {
        return autoRecurRelUnits;
    }

    /**
     * Sets the value of the autoRecurRelUnits property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAutoRecurRelUnits(String value) {
        this.autoRecurRelUnits = value;
    }

    /**
     * Gets the value of the conversionBalIncrements property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConversionBalIncrements() {
        return conversionBalIncrements;
    }

    /**
     * Sets the value of the conversionBalIncrements property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConversionBalIncrements(String value) {
        this.conversionBalIncrements = value;
    }

    /**
     * Gets the value of the conversionRate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConversionRate() {
        return conversionRate;
    }

    /**
     * Sets the value of the conversionRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConversionRate(String value) {
        this.conversionRate = value;
    }

    /**
     * Gets the value of the rejectErrorType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRejectErrorType() {
        return rejectErrorType;
    }

    /**
     * Sets the value of the rejectErrorType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRejectErrorType(String value) {
        this.rejectErrorType = value;
    }

    /**
     * Gets the value of the rejectErrorData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRejectErrorData() {
        return rejectErrorData;
    }

    /**
     * Sets the value of the rejectErrorData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRejectErrorData(String value) {
        this.rejectErrorData = value;
    }

    /**
     * Gets the value of the triggerRangeBegin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTriggerRangeBegin() {
        return triggerRangeBegin;
    }

    /**
     * Sets the value of the triggerRangeBegin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTriggerRangeBegin(String value) {
        this.triggerRangeBegin = value;
    }

    /**
     * Gets the value of the triggerRangeEnd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTriggerRangeEnd() {
        return triggerRangeEnd;
    }

    /**
     * Sets the value of the triggerRangeEnd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTriggerRangeEnd(String value) {
        this.triggerRangeEnd = value;
    }

    /**
     * Gets the value of the triggerScheduleId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTriggerScheduleId() {
        return triggerScheduleId;
    }

    /**
     * Sets the value of the triggerScheduleId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTriggerScheduleId(String value) {
        this.triggerScheduleId = value;
    }

    /**
     * Gets the value of the triggerScheduleRelativeTo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTriggerScheduleRelativeTo() {
        return triggerScheduleRelativeTo;
    }

    /**
     * Sets the value of the triggerScheduleRelativeTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTriggerScheduleRelativeTo(String value) {
        this.triggerScheduleRelativeTo = value;
    }

    /**
     * Gets the value of the triggerAfterActionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTriggerAfterActionId() {
        return triggerAfterActionId;
    }

    /**
     * Sets the value of the triggerAfterActionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTriggerAfterActionId(String value) {
        this.triggerAfterActionId = value;
    }

    /**
     * Gets the value of the triggerCardSetId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTriggerCardSetId() {
        return triggerCardSetId;
    }

    /**
     * Sets the value of the triggerCardSetId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTriggerCardSetId(String value) {
        this.triggerCardSetId = value;
    }

    /**
     * Gets the value of the triggerAccountHistory property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTriggerAccountHistory() {
        return triggerAccountHistory;
    }

    /**
     * Sets the value of the triggerAccountHistory property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTriggerAccountHistory(Boolean value) {
        this.triggerAccountHistory = value;
    }

    /**
     * Gets the value of the triggerAdjustment property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTriggerAdjustment() {
        return triggerAdjustment;
    }

    /**
     * Sets the value of the triggerAdjustment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTriggerAdjustment(Boolean value) {
        this.triggerAdjustment = value;
    }

    /**
     * Gets the value of the triggerEnrollment property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTriggerEnrollment() {
        return triggerEnrollment;
    }

    /**
     * Sets the value of the triggerEnrollment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTriggerEnrollment(Boolean value) {
        this.triggerEnrollment = value;
    }

    /**
     * Gets the value of the triggerGiftIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTriggerGiftIssuance() {
        return triggerGiftIssuance;
    }

    /**
     * Sets the value of the triggerGiftIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTriggerGiftIssuance(Boolean value) {
        this.triggerGiftIssuance = value;
    }

    /**
     * Gets the value of the triggerGiftRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTriggerGiftRedemption() {
        return triggerGiftRedemption;
    }

    /**
     * Sets the value of the triggerGiftRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTriggerGiftRedemption(Boolean value) {
        this.triggerGiftRedemption = value;
    }

    /**
     * Gets the value of the triggerInquiry property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTriggerInquiry() {
        return triggerInquiry;
    }

    /**
     * Sets the value of the triggerInquiry property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTriggerInquiry(Boolean value) {
        this.triggerInquiry = value;
    }

    /**
     * Gets the value of the triggerLoyaltyIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTriggerLoyaltyIssuance() {
        return triggerLoyaltyIssuance;
    }

    /**
     * Sets the value of the triggerLoyaltyIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTriggerLoyaltyIssuance(Boolean value) {
        this.triggerLoyaltyIssuance = value;
    }

    /**
     * Gets the value of the triggerLoyaltyRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTriggerLoyaltyRedemption() {
        return triggerLoyaltyRedemption;
    }

    /**
     * Sets the value of the triggerLoyaltyRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTriggerLoyaltyRedemption(Boolean value) {
        this.triggerLoyaltyRedemption = value;
    }

    /**
     * Gets the value of the triggerMultipleIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTriggerMultipleIssuance() {
        return triggerMultipleIssuance;
    }

    /**
     * Sets the value of the triggerMultipleIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTriggerMultipleIssuance(Boolean value) {
        this.triggerMultipleIssuance = value;
    }

    /**
     * Gets the value of the triggerPromoIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTriggerPromoIssuance() {
        return triggerPromoIssuance;
    }

    /**
     * Sets the value of the triggerPromoIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTriggerPromoIssuance(Boolean value) {
        this.triggerPromoIssuance = value;
    }

    /**
     * Gets the value of the triggerPromoRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTriggerPromoRedemption() {
        return triggerPromoRedemption;
    }

    /**
     * Sets the value of the triggerPromoRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTriggerPromoRedemption(Boolean value) {
        this.triggerPromoRedemption = value;
    }

    /**
     * Gets the value of the triggerRenewal property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTriggerRenewal() {
        return triggerRenewal;
    }

    /**
     * Sets the value of the triggerRenewal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTriggerRenewal(Boolean value) {
        this.triggerRenewal = value;
    }

    /**
     * Gets the value of the triggerReturn property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTriggerReturn() {
        return triggerReturn;
    }

    /**
     * Sets the value of the triggerReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTriggerReturn(Boolean value) {
        this.triggerReturn = value;
    }

    /**
     * Gets the value of the triggerTip property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTriggerTip() {
        return triggerTip;
    }

    /**
     * Sets the value of the triggerTip property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTriggerTip(Boolean value) {
        this.triggerTip = value;
    }

    /**
     * Gets the value of the triggerTransfer property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTriggerTransfer() {
        return triggerTransfer;
    }

    /**
     * Sets the value of the triggerTransfer property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTriggerTransfer(Boolean value) {
        this.triggerTransfer = value;
    }

    /**
     * Gets the value of the triggerVoid property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTriggerVoid() {
        return triggerVoid;
    }

    /**
     * Sets the value of the triggerVoid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTriggerVoid(Boolean value) {
        this.triggerVoid = value;
    }

    /**
     * Gets the value of the triggerMaxPerTrans property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTriggerMaxPerTrans() {
        return triggerMaxPerTrans;
    }

    /**
     * Sets the value of the triggerMaxPerTrans property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTriggerMaxPerTrans(String value) {
        this.triggerMaxPerTrans = value;
    }

    /**
     * Gets the value of the triggerValField property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTriggerValField() {
        return triggerValField;
    }

    /**
     * Sets the value of the triggerValField property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTriggerValField(String value) {
        this.triggerValField = value;
    }

    /**
     * Gets the value of the triggerValMin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTriggerValMin() {
        return triggerValMin;
    }

    /**
     * Sets the value of the triggerValMin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTriggerValMin(String value) {
        this.triggerValMin = value;
    }

    /**
     * Gets the value of the triggerValMax property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTriggerValMax() {
        return triggerValMax;
    }

    /**
     * Sets the value of the triggerValMax property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTriggerValMax(String value) {
        this.triggerValMax = value;
    }

    /**
     * Gets the value of the triggerValTypeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTriggerValTypeId() {
        return triggerValTypeId;
    }

    /**
     * Sets the value of the triggerValTypeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTriggerValTypeId(String value) {
        this.triggerValTypeId = value;
    }

    /**
     * Gets the value of the triggerRegCompletion property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTriggerRegCompletion() {
        return triggerRegCompletion;
    }

    /**
     * Sets the value of the triggerRegCompletion property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTriggerRegCompletion(Boolean value) {
        this.triggerRegCompletion = value;
    }

    /**
     * Gets the value of the triggerRegistered property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTriggerRegistered() {
        return triggerRegistered;
    }

    /**
     * Sets the value of the triggerRegistered property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTriggerRegistered(Boolean value) {
        this.triggerRegistered = value;
    }

    /**
     * Gets the value of the autoAdd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAutoAdd() {
        return autoAdd;
    }

    /**
     * Sets the value of the autoAdd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAutoAdd(Boolean value) {
        this.autoAdd = value;
    }

    /**
     * Gets the value of the addAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddAmount() {
        return addAmount;
    }

    /**
     * Sets the value of the addAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddAmount(String value) {
        this.addAmount = value;
    }

    /**
     * Gets the value of the addPercent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddPercent() {
        return addPercent;
    }

    /**
     * Sets the value of the addPercent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddPercent(String value) {
        this.addPercent = value;
    }

    /**
     * Gets the value of the addPercentOf property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddPercentOf() {
        return addPercentOf;
    }

    /**
     * Sets the value of the addPercentOf property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddPercentOf(String value) {
        this.addPercentOf = value;
    }

    /**
     * Gets the value of the addPercentOfBalanceValueTypeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddPercentOfBalanceValueTypeId() {
        return addPercentOfBalanceValueTypeId;
    }

    /**
     * Sets the value of the addPercentOfBalanceValueTypeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddPercentOfBalanceValueTypeId(String value) {
        this.addPercentOfBalanceValueTypeId = value;
    }

    /**
     * Gets the value of the addPercentRound property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddPercentRound() {
        return addPercentRound;
    }

    /**
     * Sets the value of the addPercentRound property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddPercentRound(String value) {
        this.addPercentRound = value;
    }

    /**
     * Gets the value of the addPercentAbsoluteVal property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAddPercentAbsoluteVal() {
        return addPercentAbsoluteVal;
    }

    /**
     * Sets the value of the addPercentAbsoluteVal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAddPercentAbsoluteVal(Boolean value) {
        this.addPercentAbsoluteVal = value;
    }

    /**
     * Gets the value of the addValueTypeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddValueTypeId() {
        return addValueTypeId;
    }

    /**
     * Sets the value of the addValueTypeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddValueTypeId(String value) {
        this.addValueTypeId = value;
    }

    /**
     * Gets the value of the addAsReturn property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAddAsReturn() {
        return addAsReturn;
    }

    /**
     * Sets the value of the addAsReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAddAsReturn(Boolean value) {
        this.addAsReturn = value;
    }

    /**
     * Gets the value of the autoSubtract property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAutoSubtract() {
        return autoSubtract;
    }

    /**
     * Sets the value of the autoSubtract property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAutoSubtract(Boolean value) {
        this.autoSubtract = value;
    }

    /**
     * Gets the value of the subtractAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubtractAmount() {
        return subtractAmount;
    }

    /**
     * Sets the value of the subtractAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubtractAmount(String value) {
        this.subtractAmount = value;
    }

    /**
     * Gets the value of the subtractPercent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubtractPercent() {
        return subtractPercent;
    }

    /**
     * Sets the value of the subtractPercent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubtractPercent(String value) {
        this.subtractPercent = value;
    }

    /**
     * Gets the value of the subtractPercentOf property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubtractPercentOf() {
        return subtractPercentOf;
    }

    /**
     * Sets the value of the subtractPercentOf property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubtractPercentOf(String value) {
        this.subtractPercentOf = value;
    }

    /**
     * Gets the value of the subtractPercentRound property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubtractPercentRound() {
        return subtractPercentRound;
    }

    /**
     * Sets the value of the subtractPercentRound property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubtractPercentRound(String value) {
        this.subtractPercentRound = value;
    }

    /**
     * Gets the value of the subtractValueTypeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubtractValueTypeId() {
        return subtractValueTypeId;
    }

    /**
     * Sets the value of the subtractValueTypeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubtractValueTypeId(String value) {
        this.subtractValueTypeId = value;
    }

    /**
     * Gets the value of the subtractNSFAllowed property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSubtractNSFAllowed() {
        return subtractNSFAllowed;
    }

    /**
     * Sets the value of the subtractNSFAllowed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSubtractNSFAllowed(Boolean value) {
        this.subtractNSFAllowed = value;
    }

    /**
     * Gets the value of the subtractReturnBal property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSubtractReturnBal() {
        return subtractReturnBal;
    }

    /**
     * Sets the value of the subtractReturnBal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSubtractReturnBal(Boolean value) {
        this.subtractReturnBal = value;
    }

    /**
     * Gets the value of the notifyTemplateReceipt40 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotifyTemplateReceipt40() {
        return notifyTemplateReceipt40;
    }

    /**
     * Sets the value of the notifyTemplateReceipt40 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotifyTemplateReceipt40(String value) {
        this.notifyTemplateReceipt40 = value;
    }

    /**
     * Gets the value of the notifyTemplateReceipt24 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotifyTemplateReceipt24() {
        return notifyTemplateReceipt24;
    }

    /**
     * Sets the value of the notifyTemplateReceipt24 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotifyTemplateReceipt24(String value) {
        this.notifyTemplateReceipt24 = value;
    }

    /**
     * Gets the value of the notifyExternalMedium property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotifyExternalMedium() {
        return notifyExternalMedium;
    }

    /**
     * Sets the value of the notifyExternalMedium property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotifyExternalMedium(String value) {
        this.notifyExternalMedium = value;
    }

    /**
     * Gets the value of the notifyExternalTemplate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotifyExternalTemplate() {
        return notifyExternalTemplate;
    }

    /**
     * Sets the value of the notifyExternalTemplate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotifyExternalTemplate(String value) {
        this.notifyExternalTemplate = value;
    }

    /**
     * Gets the value of the notify3RdPartyEmail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotify3RdPartyEmail() {
        return notify3RdPartyEmail;
    }

    /**
     * Sets the value of the notify3RdPartyEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotify3RdPartyEmail(String value) {
        this.notify3RdPartyEmail = value;
    }

    /**
     * Gets the value of the notifyEmailSender property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotifyEmailSender() {
        return notifyEmailSender;
    }

    /**
     * Sets the value of the notifyEmailSender property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotifyEmailSender(String value) {
        this.notifyEmailSender = value;
    }

}

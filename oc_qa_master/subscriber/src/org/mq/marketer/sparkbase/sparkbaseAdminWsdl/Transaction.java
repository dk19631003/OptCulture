
package org.mq.marketer.sparkbase.sparkbaseAdminWsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * A transaction which represents a completed transaction sent from a pos system.
 * 
 * <p>Java class for Transaction complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Transaction">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="transactionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="locationId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="locationName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="approvalCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cardId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="accountEntry" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="accountId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lifeId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="terminalId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="employeeId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="batch" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="batchReference" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="amountEntered" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="amountRemaining" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="amountValTypeId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tipEntered" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="newAccountStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="newCardStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="processedTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="commType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="relatedTransactionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="note" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="voided" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="destCardId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="destAccountEntry" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="destAccountId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="destLifeId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="destNewAccountStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="destNewCardStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="balances" type="{urn:SparkbaseAdminV45Wsdl}ArrayOfTransactionBalance" minOccurs="0"/>
 *         &lt;element name="actions" type="{urn:SparkbaseAdminV45Wsdl}ArrayOfTransactionAction" minOccurs="0"/>
 *         &lt;element name="answers" type="{urn:SparkbaseAdminV45Wsdl}ArrayOfTransactionAnswer" minOccurs="0"/>
 *         &lt;element name="promos" type="{urn:SparkbaseAdminV45Wsdl}ArrayOfTransactionPromo" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Transaction", propOrder = {

})
public class Transaction {

    protected String transactionId;
    protected String locationId;
    protected String locationName;
    protected String approvalCode;
    protected String cardId;
    protected String accountEntry;
    protected String accountId;
    protected String lifeId;
    protected String terminalId;
    protected String employeeId;
    protected String type;
    protected String batch;
    protected String batchReference;
    protected String amountEntered;
    protected String amountRemaining;
    protected String amountValTypeId;
    protected String tipEntered;
    protected String newAccountStatus;
    protected String newCardStatus;
    protected String processedTime;
    protected String commType;
    protected String relatedTransactionId;
    protected String note;
    protected Boolean voided;
    protected String destCardId;
    protected String destAccountEntry;
    protected String destAccountId;
    protected String destLifeId;
    protected String destNewAccountStatus;
    protected String destNewCardStatus;
    protected ArrayOfTransactionBalance balances;
    protected ArrayOfTransactionAction actions;
    protected ArrayOfTransactionAnswer answers;
    protected ArrayOfTransactionPromo promos;

    /**
     * Gets the value of the transactionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Sets the value of the transactionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionId(String value) {
        this.transactionId = value;
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
     * Gets the value of the approvalCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApprovalCode() {
        return approvalCode;
    }

    /**
     * Sets the value of the approvalCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApprovalCode(String value) {
        this.approvalCode = value;
    }

    /**
     * Gets the value of the cardId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardId() {
        return cardId;
    }

    /**
     * Sets the value of the cardId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardId(String value) {
        this.cardId = value;
    }

    /**
     * Gets the value of the accountEntry property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountEntry() {
        return accountEntry;
    }

    /**
     * Sets the value of the accountEntry property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccountEntry(String value) {
        this.accountEntry = value;
    }

    /**
     * Gets the value of the accountId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountId() {
        return accountId;
    }

    /**
     * Sets the value of the accountId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccountId(String value) {
        this.accountId = value;
    }

    /**
     * Gets the value of the lifeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLifeId() {
        return lifeId;
    }

    /**
     * Sets the value of the lifeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLifeId(String value) {
        this.lifeId = value;
    }

    /**
     * Gets the value of the terminalId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTerminalId() {
        return terminalId;
    }

    /**
     * Sets the value of the terminalId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTerminalId(String value) {
        this.terminalId = value;
    }

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
     * Gets the value of the batch property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBatch() {
        return batch;
    }

    /**
     * Sets the value of the batch property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBatch(String value) {
        this.batch = value;
    }

    /**
     * Gets the value of the batchReference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBatchReference() {
        return batchReference;
    }

    /**
     * Sets the value of the batchReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBatchReference(String value) {
        this.batchReference = value;
    }

    /**
     * Gets the value of the amountEntered property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAmountEntered() {
        return amountEntered;
    }

    /**
     * Sets the value of the amountEntered property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAmountEntered(String value) {
        this.amountEntered = value;
    }

    /**
     * Gets the value of the amountRemaining property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAmountRemaining() {
        return amountRemaining;
    }

    /**
     * Sets the value of the amountRemaining property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAmountRemaining(String value) {
        this.amountRemaining = value;
    }

    /**
     * Gets the value of the amountValTypeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAmountValTypeId() {
        return amountValTypeId;
    }

    /**
     * Sets the value of the amountValTypeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAmountValTypeId(String value) {
        this.amountValTypeId = value;
    }

    /**
     * Gets the value of the tipEntered property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipEntered() {
        return tipEntered;
    }

    /**
     * Sets the value of the tipEntered property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipEntered(String value) {
        this.tipEntered = value;
    }

    /**
     * Gets the value of the newAccountStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewAccountStatus() {
        return newAccountStatus;
    }

    /**
     * Sets the value of the newAccountStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewAccountStatus(String value) {
        this.newAccountStatus = value;
    }

    /**
     * Gets the value of the newCardStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewCardStatus() {
        return newCardStatus;
    }

    /**
     * Sets the value of the newCardStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewCardStatus(String value) {
        this.newCardStatus = value;
    }

    /**
     * Gets the value of the processedTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcessedTime() {
        return processedTime;
    }

    /**
     * Sets the value of the processedTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcessedTime(String value) {
        this.processedTime = value;
    }

    /**
     * Gets the value of the commType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommType() {
        return commType;
    }

    /**
     * Sets the value of the commType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommType(String value) {
        this.commType = value;
    }

    /**
     * Gets the value of the relatedTransactionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRelatedTransactionId() {
        return relatedTransactionId;
    }

    /**
     * Sets the value of the relatedTransactionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRelatedTransactionId(String value) {
        this.relatedTransactionId = value;
    }

    /**
     * Gets the value of the note property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets the value of the note property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNote(String value) {
        this.note = value;
    }

    /**
     * Gets the value of the voided property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isVoided() {
        return voided;
    }

    /**
     * Sets the value of the voided property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setVoided(Boolean value) {
        this.voided = value;
    }

    /**
     * Gets the value of the destCardId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestCardId() {
        return destCardId;
    }

    /**
     * Sets the value of the destCardId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestCardId(String value) {
        this.destCardId = value;
    }

    /**
     * Gets the value of the destAccountEntry property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestAccountEntry() {
        return destAccountEntry;
    }

    /**
     * Sets the value of the destAccountEntry property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestAccountEntry(String value) {
        this.destAccountEntry = value;
    }

    /**
     * Gets the value of the destAccountId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestAccountId() {
        return destAccountId;
    }

    /**
     * Sets the value of the destAccountId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestAccountId(String value) {
        this.destAccountId = value;
    }

    /**
     * Gets the value of the destLifeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestLifeId() {
        return destLifeId;
    }

    /**
     * Sets the value of the destLifeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestLifeId(String value) {
        this.destLifeId = value;
    }

    /**
     * Gets the value of the destNewAccountStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestNewAccountStatus() {
        return destNewAccountStatus;
    }

    /**
     * Sets the value of the destNewAccountStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestNewAccountStatus(String value) {
        this.destNewAccountStatus = value;
    }

    /**
     * Gets the value of the destNewCardStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestNewCardStatus() {
        return destNewCardStatus;
    }

    /**
     * Sets the value of the destNewCardStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestNewCardStatus(String value) {
        this.destNewCardStatus = value;
    }

    /**
     * Gets the value of the balances property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfTransactionBalance }
     *     
     */
    public ArrayOfTransactionBalance getBalances() {
        return balances;
    }

    /**
     * Sets the value of the balances property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfTransactionBalance }
     *     
     */
    public void setBalances(ArrayOfTransactionBalance value) {
        this.balances = value;
    }

    /**
     * Gets the value of the actions property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfTransactionAction }
     *     
     */
    public ArrayOfTransactionAction getActions() {
        return actions;
    }

    /**
     * Sets the value of the actions property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfTransactionAction }
     *     
     */
    public void setActions(ArrayOfTransactionAction value) {
        this.actions = value;
    }

    /**
     * Gets the value of the answers property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfTransactionAnswer }
     *     
     */
    public ArrayOfTransactionAnswer getAnswers() {
        return answers;
    }

    /**
     * Sets the value of the answers property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfTransactionAnswer }
     *     
     */
    public void setAnswers(ArrayOfTransactionAnswer value) {
        this.answers = value;
    }

    /**
     * Gets the value of the promos property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfTransactionPromo }
     *     
     */
    public ArrayOfTransactionPromo getPromos() {
        return promos;
    }

    /**
     * Sets the value of the promos property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfTransactionPromo }
     *     
     */
    public void setPromos(ArrayOfTransactionPromo value) {
        this.promos = value;
    }

}

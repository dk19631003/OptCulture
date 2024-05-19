
package org.mq.marketer.sparkbase.sparkbaseTransactionWsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A request to perform a transfer transaction. A transfer transaction can be used to transfer value from one account to an other. If no amount is given, then all the value will be transfered.
 * 
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="standardHeader" type="{urn:SparkbaseTransactionWsdl}RequestStandardHeaderComponent" minOccurs="0"/>
 *         &lt;element name="account" type="{urn:SparkbaseTransactionWsdl}AccountComponent" minOccurs="0"/>
 *         &lt;element name="activating" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="transfer" type="{urn:SparkbaseTransactionWsdl}TransferComponent" minOccurs="0"/>
 *         &lt;element name="questionsAndAnswers" type="{urn:SparkbaseTransactionWsdl}ArrayOfQuestionAndAnswer" minOccurs="0"/>
 *         &lt;element name="customerInfo" type="{urn:SparkbaseTransactionWsdl}CustomerInfoComponent" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "Transfer")
public class Transfer {

    protected RequestStandardHeaderComponent standardHeader;
    protected AccountComponent account;
    protected String activating;
    protected TransferComponent transfer;
    protected ArrayOfQuestionAndAnswer questionsAndAnswers;
    protected CustomerInfoComponent customerInfo;

    /**
     * Gets the value of the standardHeader property.
     * 
     * @return
     *     possible object is
     *     {@link RequestStandardHeaderComponent }
     *     
     */
    public RequestStandardHeaderComponent getStandardHeader() {
        return standardHeader;
    }

    /**
     * Sets the value of the standardHeader property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestStandardHeaderComponent }
     *     
     */
    public void setStandardHeader(RequestStandardHeaderComponent value) {
        this.standardHeader = value;
    }

    /**
     * Gets the value of the account property.
     * 
     * @return
     *     possible object is
     *     {@link AccountComponent }
     *     
     */
    public AccountComponent getAccount() {
        return account;
    }

    /**
     * Sets the value of the account property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountComponent }
     *     
     */
    public void setAccount(AccountComponent value) {
        this.account = value;
    }

    /**
     * Gets the value of the activating property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActivating() {
        return activating;
    }

    /**
     * Sets the value of the activating property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActivating(String value) {
        this.activating = value;
    }

    /**
     * Gets the value of the transfer property.
     * 
     * @return
     *     possible object is
     *     {@link TransferComponent }
     *     
     */
    public TransferComponent getTransfer() {
        return transfer;
    }

    /**
     * Sets the value of the transfer property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransferComponent }
     *     
     */
    public void setTransfer(TransferComponent value) {
        this.transfer = value;
    }

    /**
     * Gets the value of the questionsAndAnswers property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfQuestionAndAnswer }
     *     
     */
    public ArrayOfQuestionAndAnswer getQuestionsAndAnswers() {
        return questionsAndAnswers;
    }

    /**
     * Sets the value of the questionsAndAnswers property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfQuestionAndAnswer }
     *     
     */
    public void setQuestionsAndAnswers(ArrayOfQuestionAndAnswer value) {
        this.questionsAndAnswers = value;
    }

    /**
     * Gets the value of the customerInfo property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerInfoComponent }
     *     
     */
    public CustomerInfoComponent getCustomerInfo() {
        return customerInfo;
    }

    /**
     * Sets the value of the customerInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerInfoComponent }
     *     
     */
    public void setCustomerInfo(CustomerInfoComponent value) {
        this.customerInfo = value;
    }

}

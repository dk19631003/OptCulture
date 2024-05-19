
package org.mq.captiway.sparkbase.transactionWsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
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
 *         &lt;element name="amount" type="{urn:SparkbaseTransactionWsdl}AmountComponent" minOccurs="0"/>
 *         &lt;element name="promotionCodes" type="{urn:SparkbaseTransactionWsdl}ArrayOfPromotionCode" minOccurs="0"/>
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
@XmlRootElement(name = "PromotionRedemption")
public class PromotionRedemption {

    protected RequestStandardHeaderComponent standardHeader;
    protected AccountComponent account;
    protected String activating;
    protected AmountComponent amount;
    protected ArrayOfPromotionCode promotionCodes;
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
     * Gets the value of the amount property.
     * 
     * @return
     *     possible object is
     *     {@link AmountComponent }
     *     
     */
    public AmountComponent getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountComponent }
     *     
     */
    public void setAmount(AmountComponent value) {
        this.amount = value;
    }

    /**
     * Gets the value of the promotionCodes property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfPromotionCode }
     *     
     */
    public ArrayOfPromotionCode getPromotionCodes() {
        return promotionCodes;
    }

    /**
     * Sets the value of the promotionCodes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfPromotionCode }
     *     
     */
    public void setPromotionCodes(ArrayOfPromotionCode value) {
        this.promotionCodes = value;
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

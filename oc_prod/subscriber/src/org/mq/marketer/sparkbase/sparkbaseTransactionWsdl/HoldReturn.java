
package org.mq.marketer.sparkbase.sparkbaseTransactionWsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A request to perform a hold return transaction. A hold return transaction can be used to remove currency value in to a hold state from an account and place it back in to the regular state.
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
 *         &lt;element name="amount" type="{urn:SparkbaseTransactionWsdl}AmountComponent" minOccurs="0"/>
 *         &lt;element name="transactionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlRootElement(name = "HoldReturn")
public class HoldReturn {

    protected RequestStandardHeaderComponent standardHeader;
    protected AccountComponent account;
    protected AmountComponent amount;
    protected String transactionId;

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

}

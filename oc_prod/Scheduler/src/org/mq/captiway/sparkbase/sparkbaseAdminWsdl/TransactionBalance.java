
package org.mq.captiway.sparkbase.sparkbaseAdminWsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * A transaction balance which represents a change to a balances that was a result of the transaction.
 * 
 * <p>Java class for TransactionBalance complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TransactionBalance">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="transactionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="balanceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="difference" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionBalance", propOrder = {

})
public class TransactionBalance {

    protected String transactionId;
    protected String balanceId;
    protected String difference;

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
     * Gets the value of the balanceId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBalanceId() {
        return balanceId;
    }

    /**
     * Sets the value of the balanceId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBalanceId(String value) {
        this.balanceId = value;
    }

    /**
     * Gets the value of the difference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDifference() {
        return difference;
    }

    /**
     * Sets the value of the difference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDifference(String value) {
        this.difference = value;
    }

}

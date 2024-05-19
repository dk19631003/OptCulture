
package org.mq.marketer.sparkbase.sparkbaseTransactionWsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TransferComponent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TransferComponent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="destAccountId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="destPin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="destEntryType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="closeReason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="valueCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="enteredAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransferComponent", propOrder = {

})
public class TransferComponent {

    protected String destAccountId;
    protected String destPin;
    protected String destEntryType;
    protected String closeReason;
    protected String valueCode;
    protected String enteredAmount;

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
     * Gets the value of the destPin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestPin() {
        return destPin;
    }

    /**
     * Sets the value of the destPin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestPin(String value) {
        this.destPin = value;
    }

    /**
     * Gets the value of the destEntryType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestEntryType() {
        return destEntryType;
    }

    /**
     * Sets the value of the destEntryType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestEntryType(String value) {
        this.destEntryType = value;
    }

    /**
     * Gets the value of the closeReason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCloseReason() {
        return closeReason;
    }

    /**
     * Sets the value of the closeReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCloseReason(String value) {
        this.closeReason = value;
    }

    /**
     * Gets the value of the valueCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValueCode() {
        return valueCode;
    }

    /**
     * Sets the value of the valueCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValueCode(String value) {
        this.valueCode = value;
    }

    /**
     * Gets the value of the enteredAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnteredAmount() {
        return enteredAmount;
    }

    /**
     * Sets the value of the enteredAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnteredAmount(String value) {
        this.enteredAmount = value;
    }

}

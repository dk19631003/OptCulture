
package org.mq.captiway.sparkbase.transactionWsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AmountComponent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AmountComponent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="valueCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="enteredAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nsfAllowed" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AmountComponent", propOrder = {

})
public class AmountComponent {

    protected String valueCode;
    protected String enteredAmount;
    protected String nsfAllowed;

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

    /**
     * Gets the value of the nsfAllowed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNsfAllowed() {
        return nsfAllowed;
    }

    /**
     * Sets the value of the nsfAllowed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNsfAllowed(String value) {
        this.nsfAllowed = value;
    }

}

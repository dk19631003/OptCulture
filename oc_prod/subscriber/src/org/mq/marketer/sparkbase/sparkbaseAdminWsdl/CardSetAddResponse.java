
package org.mq.marketer.sparkbase.sparkbaseAdminWsdl;

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
 *         &lt;element name="errors" type="{urn:SparkbaseAdminV45Wsdl}ArrayOfError" minOccurs="0"/>
 *         &lt;element name="cardSet" type="{urn:SparkbaseAdminV45Wsdl}CardSet" minOccurs="0"/>
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
@XmlRootElement(name = "CardSetAddResponse")
public class CardSetAddResponse {

    protected ArrayOfError errors;
    protected CardSet cardSet;

    /**
     * Gets the value of the errors property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfError }
     *     
     */
    public ArrayOfError getErrors() {
        return errors;
    }

    /**
     * Sets the value of the errors property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfError }
     *     
     */
    public void setErrors(ArrayOfError value) {
        this.errors = value;
    }

    /**
     * Gets the value of the cardSet property.
     * 
     * @return
     *     possible object is
     *     {@link CardSet }
     *     
     */
    public CardSet getCardSet() {
        return cardSet;
    }

    /**
     * Sets the value of the cardSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link CardSet }
     *     
     */
    public void setCardSet(CardSet value) {
        this.cardSet = value;
    }

}

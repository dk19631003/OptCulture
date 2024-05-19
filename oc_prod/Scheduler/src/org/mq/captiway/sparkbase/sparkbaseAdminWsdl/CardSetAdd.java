package org.mq.captiway.sparkbase.sparkbaseAdminWsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="header" type="{urn:SparkbaseAdminV45Wsdl}RequestHeader"/>
 *         &lt;element name="cardSet" type="{urn:SparkbaseAdminV45Wsdl}CardSet"/>
 *         &lt;element name="cardQuantity" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
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
@XmlRootElement(name = "CardSetAdd")
public class CardSetAdd {

    @XmlElement(required = true)
    protected RequestHeader header;
    @XmlElement(required = true)
    protected CardSet cardSet;
    protected Long cardQuantity;

    /**
     * Gets the value of the header property.
     * 
     * @return
     *     possible object is
     *     {@link RequestHeader }
     *     
     */
    public RequestHeader getHeader() {
        return header;
    }

    /**
     * Sets the value of the header property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestHeader }
     *     
     */
    public void setHeader(RequestHeader value) {
        this.header = value;
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

    /**
     * Gets the value of the cardQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCardQuantity() {
        return cardQuantity;
    }

    /**
     * Sets the value of the cardQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCardQuantity(Long value) {
        this.cardQuantity = value;
    }

}

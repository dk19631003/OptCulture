
package org.mq.captiway.sparkbase.sparkbaseTransactionWsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A request to perform a multiple issuance transaction. A multiple issuance transaction can be used to add value to multiple account.
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
 *         &lt;element name="multipleIssuance" type="{urn:SparkbaseTransactionWsdl}MultipleIssuanceComponent" minOccurs="0"/>
 *         &lt;element name="amount" type="{urn:SparkbaseTransactionWsdl}AmountComponent" minOccurs="0"/>
 *         &lt;element name="activating" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="promotionCodes" type="{urn:SparkbaseTransactionWsdl}ArrayOfPromotionCode" minOccurs="0"/>
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
@XmlRootElement(name = "MultipleIssuance")
public class MultipleIssuance {

    protected RequestStandardHeaderComponent standardHeader;
    protected MultipleIssuanceComponent multipleIssuance;
    protected AmountComponent amount;
    protected String activating;
    protected ArrayOfPromotionCode promotionCodes;

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
     * Gets the value of the multipleIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link MultipleIssuanceComponent }
     *     
     */
    public MultipleIssuanceComponent getMultipleIssuance() {
        return multipleIssuance;
    }

    /**
     * Sets the value of the multipleIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link MultipleIssuanceComponent }
     *     
     */
    public void setMultipleIssuance(MultipleIssuanceComponent value) {
        this.multipleIssuance = value;
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

}

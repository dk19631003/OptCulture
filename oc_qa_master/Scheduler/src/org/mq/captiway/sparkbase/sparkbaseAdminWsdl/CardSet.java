package org.mq.captiway.sparkbase.sparkbaseAdminWsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * A card set which represents a set of cards that are created together.
 * 
 * <p>Java class for CardSet complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CardSet">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="cardSetId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="programId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="added" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cardIdOrder" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="productId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cardDesignId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="giftIssuanceAmt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="loyaltyIssuanceAmt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="crossRefId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CardSet", propOrder = {

})
public class CardSet {

    protected String cardSetId;
    protected String programId;
    protected String name;
    protected String added;
    protected String cardIdOrder;
    protected String productId;
    protected String cardDesignId;
    protected String status;
    protected String giftIssuanceAmt;
    protected String loyaltyIssuanceAmt;
    protected String crossRefId;

    /**
     * Gets the value of the cardSetId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardSetId() {
        return cardSetId;
    }

    /**
     * Sets the value of the cardSetId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardSetId(String value) {
        this.cardSetId = value;
    }

    /**
     * Gets the value of the programId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProgramId() {
        return programId;
    }

    /**
     * Sets the value of the programId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProgramId(String value) {
        this.programId = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the added property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdded() {
        return added;
    }

    /**
     * Sets the value of the added property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdded(String value) {
        this.added = value;
    }

    /**
     * Gets the value of the cardIdOrder property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardIdOrder() {
        return cardIdOrder;
    }

    /**
     * Sets the value of the cardIdOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardIdOrder(String value) {
        this.cardIdOrder = value;
    }

    /**
     * Gets the value of the productId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductId() {
        return productId;
    }

    /**
     * Sets the value of the productId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductId(String value) {
        this.productId = value;
    }

    /**
     * Gets the value of the cardDesignId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardDesignId() {
        return cardDesignId;
    }

    /**
     * Sets the value of the cardDesignId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardDesignId(String value) {
        this.cardDesignId = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the giftIssuanceAmt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGiftIssuanceAmt() {
        return giftIssuanceAmt;
    }

    /**
     * Sets the value of the giftIssuanceAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGiftIssuanceAmt(String value) {
        this.giftIssuanceAmt = value;
    }

    /**
     * Gets the value of the loyaltyIssuanceAmt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLoyaltyIssuanceAmt() {
        return loyaltyIssuanceAmt;
    }

    /**
     * Sets the value of the loyaltyIssuanceAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLoyaltyIssuanceAmt(String value) {
        this.loyaltyIssuanceAmt = value;
    }

    /**
     * Gets the value of the crossRefId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCrossRefId() {
        return crossRefId;
    }

    /**
     * Sets the value of the crossRefId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCrossRefId(String value) {
        this.crossRefId = value;
    }

}

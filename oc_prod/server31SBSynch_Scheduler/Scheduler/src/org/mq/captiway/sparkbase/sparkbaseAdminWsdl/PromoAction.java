package org.mq.captiway.sparkbase.sparkbaseAdminWsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * A promo action which represents an action that is triggered by a promotion.
 * 
 * <p>Java class for PromoAction complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PromoAction">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="promoId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="actionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="promoName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="actionName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PromoAction", propOrder = {

})
public class PromoAction {

    protected String promoId;
    protected String actionId;
    protected String promoName;
    protected String actionName;

    /**
     * Gets the value of the promoId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPromoId() {
        return promoId;
    }

    /**
     * Sets the value of the promoId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPromoId(String value) {
        this.promoId = value;
    }

    /**
     * Gets the value of the actionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActionId() {
        return actionId;
    }

    /**
     * Sets the value of the actionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActionId(String value) {
        this.actionId = value;
    }

    /**
     * Gets the value of the promoName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPromoName() {
        return promoName;
    }

    /**
     * Sets the value of the promoName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPromoName(String value) {
        this.promoName = value;
    }

    /**
     * Gets the value of the actionName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActionName() {
        return actionName;
    }

    /**
     * Sets the value of the actionName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActionName(String value) {
        this.actionName = value;
    }

}

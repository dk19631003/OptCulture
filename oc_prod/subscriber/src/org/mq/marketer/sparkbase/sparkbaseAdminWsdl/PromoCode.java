
package org.mq.marketer.sparkbase.sparkbaseAdminWsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * A promo codes which represents a customizable additional information that can be prompted.
 * 
 * <p>Java class for PromoCode complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PromoCode">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="promoCodeId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="promoId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="programId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="promoName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="groupId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="begin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="end" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PromoCode", propOrder = {

})
public class PromoCode {

    protected String promoCodeId;
    protected String promoId;
    protected String programId;
    protected String promoName;
    protected String groupId;
    protected String code;
    protected String begin;
    protected String end;

    /**
     * Gets the value of the promoCodeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPromoCodeId() {
        return promoCodeId;
    }

    /**
     * Sets the value of the promoCodeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPromoCodeId(String value) {
        this.promoCodeId = value;
    }

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
     * Gets the value of the groupId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Sets the value of the groupId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroupId(String value) {
        this.groupId = value;
    }

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCode(String value) {
        this.code = value;
    }

    /**
     * Gets the value of the begin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBegin() {
        return begin;
    }

    /**
     * Sets the value of the begin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBegin(String value) {
        this.begin = value;
    }

    /**
     * Gets the value of the end property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnd() {
        return end;
    }

    /**
     * Sets the value of the end property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnd(String value) {
        this.end = value;
    }

}

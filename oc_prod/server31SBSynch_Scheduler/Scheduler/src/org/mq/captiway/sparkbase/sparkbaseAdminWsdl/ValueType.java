
package org.mq.captiway.sparkbase.sparkbaseAdminWsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * A value type which represents a currency, points or custom type of value.
 * 
 * <p>Java class for ValueType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ValueType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="valueTypeId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="programId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="groupId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="decimalPlaces" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="currency" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="holdable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="balanceMax" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="returnBalanceMax" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="issuanceRounding" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="issuanceMin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="issuanceMax" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="returnIssuanceMin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="returnIssuanceMax" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="redemptionMin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="redemptionMax" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ValueType", propOrder = {

})
public class ValueType {

    protected String valueTypeId;
    protected String programId;
    protected String groupId;
    protected String type;
    protected String decimalPlaces;
    protected String currency;
    protected Boolean holdable;
    protected String code;
    protected String balanceMax;
    protected String returnBalanceMax;
    protected String issuanceRounding;
    protected String issuanceMin;
    protected String issuanceMax;
    protected String returnIssuanceMin;
    protected String returnIssuanceMax;
    protected String redemptionMin;
    protected String redemptionMax;

    /**
     * Gets the value of the valueTypeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValueTypeId() {
        return valueTypeId;
    }

    /**
     * Sets the value of the valueTypeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValueTypeId(String value) {
        this.valueTypeId = value;
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
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the decimalPlaces property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDecimalPlaces() {
        return decimalPlaces;
    }

    /**
     * Sets the value of the decimalPlaces property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDecimalPlaces(String value) {
        this.decimalPlaces = value;
    }

    /**
     * Gets the value of the currency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the value of the currency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrency(String value) {
        this.currency = value;
    }

    /**
     * Gets the value of the holdable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isHoldable() {
        return holdable;
    }

    /**
     * Sets the value of the holdable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setHoldable(Boolean value) {
        this.holdable = value;
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
     * Gets the value of the balanceMax property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBalanceMax() {
        return balanceMax;
    }

    /**
     * Sets the value of the balanceMax property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBalanceMax(String value) {
        this.balanceMax = value;
    }

    /**
     * Gets the value of the returnBalanceMax property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReturnBalanceMax() {
        return returnBalanceMax;
    }

    /**
     * Sets the value of the returnBalanceMax property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReturnBalanceMax(String value) {
        this.returnBalanceMax = value;
    }

    /**
     * Gets the value of the issuanceRounding property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIssuanceRounding() {
        return issuanceRounding;
    }

    /**
     * Sets the value of the issuanceRounding property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIssuanceRounding(String value) {
        this.issuanceRounding = value;
    }

    /**
     * Gets the value of the issuanceMin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIssuanceMin() {
        return issuanceMin;
    }

    /**
     * Sets the value of the issuanceMin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIssuanceMin(String value) {
        this.issuanceMin = value;
    }

    /**
     * Gets the value of the issuanceMax property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIssuanceMax() {
        return issuanceMax;
    }

    /**
     * Sets the value of the issuanceMax property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIssuanceMax(String value) {
        this.issuanceMax = value;
    }

    /**
     * Gets the value of the returnIssuanceMin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReturnIssuanceMin() {
        return returnIssuanceMin;
    }

    /**
     * Sets the value of the returnIssuanceMin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReturnIssuanceMin(String value) {
        this.returnIssuanceMin = value;
    }

    /**
     * Gets the value of the returnIssuanceMax property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReturnIssuanceMax() {
        return returnIssuanceMax;
    }

    /**
     * Sets the value of the returnIssuanceMax property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReturnIssuanceMax(String value) {
        this.returnIssuanceMax = value;
    }

    /**
     * Gets the value of the redemptionMin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRedemptionMin() {
        return redemptionMin;
    }

    /**
     * Sets the value of the redemptionMin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRedemptionMin(String value) {
        this.redemptionMin = value;
    }

    /**
     * Gets the value of the redemptionMax property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRedemptionMax() {
        return redemptionMax;
    }

    /**
     * Sets the value of the redemptionMax property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRedemptionMax(String value) {
        this.redemptionMax = value;
    }

}


package org.mq.marketer.sparkbase.sparkbaseAdminWsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * A terminal which represents a physical or vitural device that sends transactions to the system.
 * 
 * <p>Java class for Terminal complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Terminal">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="terminalId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="locationId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idPos" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="terminalTypeId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="passwordWeb" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="primaryCommType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="secondaryCommType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="manufacturersId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="storedValSettingsId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="storedValTermSoftId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="webReqCardPinNotEmp" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Terminal", propOrder = {

})
public class Terminal {

    protected String terminalId;
    protected String locationId;
    protected String idPos;
    protected String status;
    protected String terminalTypeId;
    protected String passwordWeb;
    protected String primaryCommType;
    protected String secondaryCommType;
    protected String manufacturersId;
    protected String storedValSettingsId;
    protected String storedValTermSoftId;
    protected Boolean webReqCardPinNotEmp;

    /**
     * Gets the value of the terminalId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTerminalId() {
        return terminalId;
    }

    /**
     * Sets the value of the terminalId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTerminalId(String value) {
        this.terminalId = value;
    }

    /**
     * Gets the value of the locationId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocationId() {
        return locationId;
    }

    /**
     * Sets the value of the locationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocationId(String value) {
        this.locationId = value;
    }

    /**
     * Gets the value of the idPos property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdPos() {
        return idPos;
    }

    /**
     * Sets the value of the idPos property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdPos(String value) {
        this.idPos = value;
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
     * Gets the value of the terminalTypeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTerminalTypeId() {
        return terminalTypeId;
    }

    /**
     * Sets the value of the terminalTypeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTerminalTypeId(String value) {
        this.terminalTypeId = value;
    }

    /**
     * Gets the value of the passwordWeb property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPasswordWeb() {
        return passwordWeb;
    }

    /**
     * Sets the value of the passwordWeb property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPasswordWeb(String value) {
        this.passwordWeb = value;
    }

    /**
     * Gets the value of the primaryCommType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrimaryCommType() {
        return primaryCommType;
    }

    /**
     * Sets the value of the primaryCommType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrimaryCommType(String value) {
        this.primaryCommType = value;
    }

    /**
     * Gets the value of the secondaryCommType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSecondaryCommType() {
        return secondaryCommType;
    }

    /**
     * Sets the value of the secondaryCommType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSecondaryCommType(String value) {
        this.secondaryCommType = value;
    }

    /**
     * Gets the value of the manufacturersId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getManufacturersId() {
        return manufacturersId;
    }

    /**
     * Sets the value of the manufacturersId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setManufacturersId(String value) {
        this.manufacturersId = value;
    }

    /**
     * Gets the value of the storedValSettingsId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStoredValSettingsId() {
        return storedValSettingsId;
    }

    /**
     * Sets the value of the storedValSettingsId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStoredValSettingsId(String value) {
        this.storedValSettingsId = value;
    }

    /**
     * Gets the value of the storedValTermSoftId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStoredValTermSoftId() {
        return storedValTermSoftId;
    }

    /**
     * Sets the value of the storedValTermSoftId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStoredValTermSoftId(String value) {
        this.storedValTermSoftId = value;
    }

    /**
     * Gets the value of the webReqCardPinNotEmp property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isWebReqCardPinNotEmp() {
        return webReqCardPinNotEmp;
    }

    /**
     * Sets the value of the webReqCardPinNotEmp property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setWebReqCardPinNotEmp(Boolean value) {
        this.webReqCardPinNotEmp = value;
    }

}

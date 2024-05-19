
package org.mq.captiway.sparkbase.sparkbaseAdminWsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * A terminal type which represents the type of terminal which is used to determine terminal attributes.
 * 
 * <p>Java class for TerminalType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TerminalType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="terminalTypeId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="commType1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="commType2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="printerCharWidth" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="downloadServerType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="integrated" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TerminalType", propOrder = {

})
public class TerminalType {

    protected String terminalTypeId;
    protected String name;
    protected String commType1;
    protected String commType2;
    protected String printerCharWidth;
    protected String downloadServerType;
    protected Boolean integrated;

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
     * Gets the value of the commType1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommType1() {
        return commType1;
    }

    /**
     * Sets the value of the commType1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommType1(String value) {
        this.commType1 = value;
    }

    /**
     * Gets the value of the commType2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommType2() {
        return commType2;
    }

    /**
     * Sets the value of the commType2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommType2(String value) {
        this.commType2 = value;
    }

    /**
     * Gets the value of the printerCharWidth property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrinterCharWidth() {
        return printerCharWidth;
    }

    /**
     * Sets the value of the printerCharWidth property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrinterCharWidth(String value) {
        this.printerCharWidth = value;
    }

    /**
     * Gets the value of the downloadServerType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDownloadServerType() {
        return downloadServerType;
    }

    /**
     * Sets the value of the downloadServerType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDownloadServerType(String value) {
        this.downloadServerType = value;
    }

    /**
     * Gets the value of the integrated property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIntegrated() {
        return integrated;
    }

    /**
     * Sets the value of the integrated property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIntegrated(Boolean value) {
        this.integrated = value;
    }

}

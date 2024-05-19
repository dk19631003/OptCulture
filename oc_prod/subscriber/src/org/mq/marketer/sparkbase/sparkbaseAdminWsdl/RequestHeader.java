
package org.mq.marketer.sparkbase.sparkbaseAdminWsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A header that must be sent with every request. It contains information used to authenicate the client and user.
 * 
 * <p>Java class for RequestHeader complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestHeader">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="client" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="integrationAuth" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="integrationPass" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="initiatorType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="initiatorId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="initiatorPass" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="initiatorIP" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="initiatorHostname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestHeader", propOrder = {

})
public class RequestHeader {

    @XmlElement(required = true)
    protected String client;
    @XmlElement(required = true)
    protected String integrationAuth;
    @XmlElement(required = true)
    protected String integrationPass;
    @XmlElement(required = true)
    protected String initiatorType;
    @XmlElement(required = true)
    protected String initiatorId;
    @XmlElement(required = true)
    protected String initiatorPass;
    @XmlElement(required = true)
    protected String initiatorIP;
    protected String initiatorHostname;

    /**
     * Gets the value of the client property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClient() {
        return client;
    }

    /**
     * Sets the value of the client property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClient(String value) {
        this.client = value;
    }

    /**
     * Gets the value of the integrationAuth property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIntegrationAuth() {
        return integrationAuth;
    }

    /**
     * Sets the value of the integrationAuth property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIntegrationAuth(String value) {
        this.integrationAuth = value;
    }

    /**
     * Gets the value of the integrationPass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIntegrationPass() {
        return integrationPass;
    }

    /**
     * Sets the value of the integrationPass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIntegrationPass(String value) {
        this.integrationPass = value;
    }

    /**
     * Gets the value of the initiatorType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInitiatorType() {
        return initiatorType;
    }

    /**
     * Sets the value of the initiatorType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInitiatorType(String value) {
        this.initiatorType = value;
    }

    /**
     * Gets the value of the initiatorId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInitiatorId() {
        return initiatorId;
    }

    /**
     * Sets the value of the initiatorId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInitiatorId(String value) {
        this.initiatorId = value;
    }

    /**
     * Gets the value of the initiatorPass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInitiatorPass() {
        return initiatorPass;
    }

    /**
     * Sets the value of the initiatorPass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInitiatorPass(String value) {
        this.initiatorPass = value;
    }

    /**
     * Gets the value of the initiatorIP property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInitiatorIP() {
        return initiatorIP;
    }

    /**
     * Sets the value of the initiatorIP property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInitiatorIP(String value) {
        this.initiatorIP = value;
    }

    /**
     * Gets the value of the initiatorHostname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInitiatorHostname() {
        return initiatorHostname;
    }

    /**
     * Sets the value of the initiatorHostname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInitiatorHostname(String value) {
        this.initiatorHostname = value;
    }

}


package org.mq.captiway.sparkbase.sparkbaseTransactionWsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ErrorMessageComponent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ErrorMessageComponent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="rejectionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="errorCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="briefMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="inDepthMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ErrorMessageComponent", propOrder = {

})
public class ErrorMessageComponent {

    protected String rejectionId;
    protected String errorCode;
    protected String briefMessage;
    protected String inDepthMessage;

    /**
     * Gets the value of the rejectionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRejectionId() {
        return rejectionId;
    }

    /**
     * Sets the value of the rejectionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRejectionId(String value) {
        this.rejectionId = value;
    }

    /**
     * Gets the value of the errorCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the value of the errorCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorCode(String value) {
        this.errorCode = value;
    }

    /**
     * Gets the value of the briefMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBriefMessage() {
        return briefMessage;
    }

    /**
     * Sets the value of the briefMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBriefMessage(String value) {
        this.briefMessage = value;
    }

    /**
     * Gets the value of the inDepthMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInDepthMessage() {
        return inDepthMessage;
    }

    /**
     * Sets the value of the inDepthMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInDepthMessage(String value) {
        this.inDepthMessage = value;
    }

}

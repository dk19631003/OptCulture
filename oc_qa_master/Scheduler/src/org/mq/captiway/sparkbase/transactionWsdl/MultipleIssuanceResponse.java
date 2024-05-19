
package org.mq.captiway.sparkbase.transactionWsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="standardHeader" type="{urn:SparkbaseTransactionWsdl}ResponseStandardHeaderComponent" minOccurs="0"/>
 *         &lt;element name="identification" type="{urn:SparkbaseTransactionWsdl}IdentificationComponent" minOccurs="0"/>
 *         &lt;element name="errorMessage" type="{urn:SparkbaseTransactionWsdl}ErrorMessageComponent" minOccurs="0"/>
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
@XmlRootElement(name = "MultipleIssuanceResponse")
public class MultipleIssuanceResponse {

    protected ResponseStandardHeaderComponent standardHeader;
    protected IdentificationComponent identification;
    protected ErrorMessageComponent errorMessage;

    /**
     * Gets the value of the standardHeader property.
     * 
     * @return
     *     possible object is
     *     {@link ResponseStandardHeaderComponent }
     *     
     */
    public ResponseStandardHeaderComponent getStandardHeader() {
        return standardHeader;
    }

    /**
     * Sets the value of the standardHeader property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseStandardHeaderComponent }
     *     
     */
    public void setStandardHeader(ResponseStandardHeaderComponent value) {
        this.standardHeader = value;
    }

    /**
     * Gets the value of the identification property.
     * 
     * @return
     *     possible object is
     *     {@link IdentificationComponent }
     *     
     */
    public IdentificationComponent getIdentification() {
        return identification;
    }

    /**
     * Sets the value of the identification property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentificationComponent }
     *     
     */
    public void setIdentification(IdentificationComponent value) {
        this.identification = value;
    }

    /**
     * Gets the value of the errorMessage property.
     * 
     * @return
     *     possible object is
     *     {@link ErrorMessageComponent }
     *     
     */
    public ErrorMessageComponent getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the value of the errorMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link ErrorMessageComponent }
     *     
     */
    public void setErrorMessage(ErrorMessageComponent value) {
        this.errorMessage = value;
    }

}

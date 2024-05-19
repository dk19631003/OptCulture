
package org.mq.marketer.sparkbase.sparkbaseTransactionWsdl;

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
 *         &lt;element name="printableData" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlRootElement(name = "EmployeeReportResponse")
public class EmployeeReportResponse {

    protected ResponseStandardHeaderComponent standardHeader;
    protected String printableData;
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
     * Gets the value of the printableData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrintableData() {
        return printableData;
    }

    /**
     * Sets the value of the printableData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrintableData(String value) {
        this.printableData = value;
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

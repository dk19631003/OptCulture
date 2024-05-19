
package org.mq.captiway.sparkbase.sparkbaseTransactionWsdl;

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
 *         &lt;element name="expirationDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="balances" type="{urn:SparkbaseTransactionWsdl}ArrayOfBalance" minOccurs="0"/>
 *         &lt;element name="customerInfo" type="{urn:SparkbaseTransactionWsdl}CustomerInfoComponent" minOccurs="0"/>
 *         &lt;element name="hostMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="printCodes" type="{urn:SparkbaseTransactionWsdl}ArrayOfPrintCode" minOccurs="0"/>
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
@XmlRootElement(name = "TransferResponse")
public class TransferResponse {

    protected ResponseStandardHeaderComponent standardHeader;
    protected IdentificationComponent identification;
    protected String expirationDate;
    protected ArrayOfBalance balances;
    protected CustomerInfoComponent customerInfo;
    protected String hostMessage;
    protected ArrayOfPrintCode printCodes;
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
     * Gets the value of the expirationDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpirationDate() {
        return expirationDate;
    }

    /**
     * Sets the value of the expirationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpirationDate(String value) {
        this.expirationDate = value;
    }

    /**
     * Gets the value of the balances property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfBalance }
     *     
     */
    public ArrayOfBalance getBalances() {
        return balances;
    }

    /**
     * Sets the value of the balances property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfBalance }
     *     
     */
    public void setBalances(ArrayOfBalance value) {
        this.balances = value;
    }

    /**
     * Gets the value of the customerInfo property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerInfoComponent }
     *     
     */
    public CustomerInfoComponent getCustomerInfo() {
        return customerInfo;
    }

    /**
     * Sets the value of the customerInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerInfoComponent }
     *     
     */
    public void setCustomerInfo(CustomerInfoComponent value) {
        this.customerInfo = value;
    }

    /**
     * Gets the value of the hostMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHostMessage() {
        return hostMessage;
    }

    /**
     * Sets the value of the hostMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHostMessage(String value) {
        this.hostMessage = value;
    }

    /**
     * Gets the value of the printCodes property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfPrintCode }
     *     
     */
    public ArrayOfPrintCode getPrintCodes() {
        return printCodes;
    }

    /**
     * Sets the value of the printCodes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfPrintCode }
     *     
     */
    public void setPrintCodes(ArrayOfPrintCode value) {
        this.printCodes = value;
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

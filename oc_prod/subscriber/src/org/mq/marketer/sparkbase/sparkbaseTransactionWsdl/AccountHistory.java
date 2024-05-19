
package org.mq.marketer.sparkbase.sparkbaseTransactionWsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A request to perform an account history transaction. Account history will return a list of transactions for a given account.
 * 
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="standardHeader" type="{urn:SparkbaseTransactionWsdl}RequestStandardHeaderComponent" minOccurs="0"/>
 *         &lt;element name="account" type="{urn:SparkbaseTransactionWsdl}AccountComponent" minOccurs="0"/>
 *         &lt;element name="report" type="{urn:SparkbaseTransactionWsdl}ReportComponent" minOccurs="0"/>
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
@XmlRootElement(name = "AccountHistory")
public class AccountHistory {

    protected RequestStandardHeaderComponent standardHeader;
    protected AccountComponent account;
    protected ReportComponent report;

    /**
     * Gets the value of the standardHeader property.
     * 
     * @return
     *     possible object is
     *     {@link RequestStandardHeaderComponent }
     *     
     */
    public RequestStandardHeaderComponent getStandardHeader() {
        return standardHeader;
    }

    /**
     * Sets the value of the standardHeader property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestStandardHeaderComponent }
     *     
     */
    public void setStandardHeader(RequestStandardHeaderComponent value) {
        this.standardHeader = value;
    }

    /**
     * Gets the value of the account property.
     * 
     * @return
     *     possible object is
     *     {@link AccountComponent }
     *     
     */
    public AccountComponent getAccount() {
        return account;
    }

    /**
     * Sets the value of the account property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountComponent }
     *     
     */
    public void setAccount(AccountComponent value) {
        this.account = value;
    }

    /**
     * Gets the value of the report property.
     * 
     * @return
     *     possible object is
     *     {@link ReportComponent }
     *     
     */
    public ReportComponent getReport() {
        return report;
    }

    /**
     * Sets the value of the report property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReportComponent }
     *     
     */
    public void setReport(ReportComponent value) {
        this.report = value;
    }

}

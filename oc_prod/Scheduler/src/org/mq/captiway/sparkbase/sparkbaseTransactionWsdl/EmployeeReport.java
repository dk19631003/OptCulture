
package org.mq.captiway.sparkbase.sparkbaseTransactionWsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A request to perform an employee report transaction. An employee report transaction  will return a list of transactions initiated by a given employee.
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
 *         &lt;element name="reportOnEmployeeId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlRootElement(name = "EmployeeReport")
public class EmployeeReport {

    protected RequestStandardHeaderComponent standardHeader;
    protected String reportOnEmployeeId;
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
     * Gets the value of the reportOnEmployeeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReportOnEmployeeId() {
        return reportOnEmployeeId;
    }

    /**
     * Sets the value of the reportOnEmployeeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReportOnEmployeeId(String value) {
        this.reportOnEmployeeId = value;
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

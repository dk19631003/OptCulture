
package org.mq.marketer.sparkbase.sparkbaseAdminWsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="header" type="{urn:SparkbaseAdminV45Wsdl}RequestHeader"/>
 *         &lt;element name="locationProgram" type="{urn:SparkbaseAdminV45Wsdl}LocationProgram"/>
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
@XmlRootElement(name = "LocationProgramRemove")
public class LocationProgramRemove {

    @XmlElement(required = true)
    protected RequestHeader header;
    @XmlElement(required = true)
    protected LocationProgram locationProgram;

    /**
     * Gets the value of the header property.
     * 
     * @return
     *     possible object is
     *     {@link RequestHeader }
     *     
     */
    public RequestHeader getHeader() {
        return header;
    }

    /**
     * Sets the value of the header property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestHeader }
     *     
     */
    public void setHeader(RequestHeader value) {
        this.header = value;
    }

    /**
     * Gets the value of the locationProgram property.
     * 
     * @return
     *     possible object is
     *     {@link LocationProgram }
     *     
     */
    public LocationProgram getLocationProgram() {
        return locationProgram;
    }

    /**
     * Sets the value of the locationProgram property.
     * 
     * @param value
     *     allowed object is
     *     {@link LocationProgram }
     *     
     */
    public void setLocationProgram(LocationProgram value) {
        this.locationProgram = value;
    }

}

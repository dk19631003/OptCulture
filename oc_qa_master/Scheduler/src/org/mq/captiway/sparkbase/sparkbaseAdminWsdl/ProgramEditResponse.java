package org.mq.captiway.sparkbase.sparkbaseAdminWsdl;

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
 *         &lt;element name="errors" type="{urn:SparkbaseAdminV45Wsdl}ArrayOfError" minOccurs="0"/>
 *         &lt;element name="program" type="{urn:SparkbaseAdminV45Wsdl}Program" minOccurs="0"/>
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
@XmlRootElement(name = "ProgramEditResponse")
public class ProgramEditResponse {

    protected ArrayOfError errors;
    protected Program program;

    /**
     * Gets the value of the errors property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfError }
     *     
     */
    public ArrayOfError getErrors() {
        return errors;
    }

    /**
     * Sets the value of the errors property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfError }
     *     
     */
    public void setErrors(ArrayOfError value) {
        this.errors = value;
    }

    /**
     * Gets the value of the program property.
     * 
     * @return
     *     possible object is
     *     {@link Program }
     *     
     */
    public Program getProgram() {
        return program;
    }

    /**
     * Sets the value of the program property.
     * 
     * @param value
     *     allowed object is
     *     {@link Program }
     *     
     */
    public void setProgram(Program value) {
        this.program = value;
    }

}

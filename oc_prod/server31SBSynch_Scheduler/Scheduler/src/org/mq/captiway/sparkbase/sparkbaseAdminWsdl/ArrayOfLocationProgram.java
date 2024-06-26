package org.mq.captiway.sparkbase.sparkbaseAdminWsdl; 

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A list of location program associations which may be returned by the location programs view response.
 * 
 * <p>Java class for ArrayOfLocationProgram complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfLocationProgram">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="locationPrograms" type="{urn:SparkbaseAdminV45Wsdl}LocationProgram" maxOccurs="1000" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfLocationProgram", propOrder = {
    "locationPrograms"
})
public class ArrayOfLocationProgram {

    @XmlElement(nillable = true)
    protected List<LocationProgram> locationPrograms;

    /**
     * Gets the value of the locationPrograms property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the locationPrograms property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLocationPrograms().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LocationProgram }
     * 
     * 
     */
    public List<LocationProgram> getLocationPrograms() {
        if (locationPrograms == null) {
            locationPrograms = new ArrayList<LocationProgram>();
        }
        return this.locationPrograms;
    }

}

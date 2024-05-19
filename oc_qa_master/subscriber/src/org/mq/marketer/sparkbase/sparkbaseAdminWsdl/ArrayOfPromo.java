
package org.mq.marketer.sparkbase.sparkbaseAdminWsdl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A list of promos which may be returned by the promos view response.
 * 
 * <p>Java class for ArrayOfPromo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfPromo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="promos" type="{urn:SparkbaseAdminV45Wsdl}Promo" maxOccurs="1000" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfPromo", propOrder = {
    "promos"
})
public class ArrayOfPromo {

    @XmlElement(nillable = true)
    protected List<Promo> promos;

    /**
     * Gets the value of the promos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the promos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPromos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Promo }
     * 
     * 
     */
    public List<Promo> getPromos() {
        if (promos == null) {
            promos = new ArrayList<Promo>();
        }
        return this.promos;
    }

}

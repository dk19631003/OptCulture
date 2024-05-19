
package org.mq.captiway.sparkbase.sparkbaseAdminWsdl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A list of balances which may be returned by the balances view response.
 * 
 * <p>Java class for ArrayOfBalance complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfBalance">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="balances" type="{urn:SparkbaseAdminV45Wsdl}Balance" maxOccurs="1000" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfBalance", propOrder = {
    "balances"
})
public class ArrayOfBalance {

    @XmlElement(nillable = true)
    protected List<Balance> balances;

    /**
     * Gets the value of the balances property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the balances property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBalances().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Balance }
     * 
     * 
     */
    public List<Balance> getBalances() {
        if (balances == null) {
            balances = new ArrayList<Balance>();
        }
        return this.balances;
    }

}

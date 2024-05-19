
package org.mq.marketer.sparkbase.sparkbaseAdminWsdl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A transaction promos which represents an action that fired as a result of the transaction.
 * 
 * <p>Java class for ArrayOfTransactionPromo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfTransactionPromo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="transactionPromos" type="{urn:SparkbaseAdminV45Wsdl}TransactionPromo" maxOccurs="1000" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfTransactionPromo", propOrder = {
    "transactionPromos"
})
public class ArrayOfTransactionPromo {

    @XmlElement(nillable = true)
    protected List<TransactionPromo> transactionPromos;

    /**
     * Gets the value of the transactionPromos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the transactionPromos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransactionPromos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransactionPromo }
     * 
     * 
     */
    public List<TransactionPromo> getTransactionPromos() {
        if (transactionPromos == null) {
            transactionPromos = new ArrayList<TransactionPromo>();
        }
        return this.transactionPromos;
    }

}

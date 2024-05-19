package org.mq.captiway.sparkbase.sparkbaseAdminWsdl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A transaction balances which represents an action that fired as a result of the transaction.
 * 
 * <p>Java class for ArrayOfTransactionBalance complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfTransactionBalance">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="transactionBalances" type="{urn:SparkbaseAdminV45Wsdl}TransactionBalance" maxOccurs="1000" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfTransactionBalance", propOrder = {
    "transactionBalances"
})
public class ArrayOfTransactionBalance {

    @XmlElement(nillable = true)
    protected List<TransactionBalance> transactionBalances;

    /**
     * Gets the value of the transactionBalances property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the transactionBalances property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransactionBalances().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransactionBalance }
     * 
     * 
     */
    public List<TransactionBalance> getTransactionBalances() {
        if (transactionBalances == null) {
            transactionBalances = new ArrayList<TransactionBalance>();
        }
        return this.transactionBalances;
    }

}

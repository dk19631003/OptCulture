
package org.mq.captiway.sparkbase.transactionWsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MultipleIssuanceComponent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MultipleIssuanceComponent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="specifiedBy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="accounts" type="{urn:SparkbaseTransactionWsdl}ArrayOfMultipleIssuanceAccount" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MultipleIssuanceComponent", propOrder = {

})
public class MultipleIssuanceComponent {

    protected String specifiedBy;
    protected ArrayOfMultipleIssuanceAccount accounts;

    /**
     * Gets the value of the specifiedBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecifiedBy() {
        return specifiedBy;
    }

    /**
     * Sets the value of the specifiedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecifiedBy(String value) {
        this.specifiedBy = value;
    }

    /**
     * Gets the value of the accounts property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfMultipleIssuanceAccount }
     *     
     */
    public ArrayOfMultipleIssuanceAccount getAccounts() {
        return accounts;
    }

    /**
     * Sets the value of the accounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfMultipleIssuanceAccount }
     *     
     */
    public void setAccounts(ArrayOfMultipleIssuanceAccount value) {
        this.accounts = value;
    }

}

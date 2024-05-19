
package org.mq.marketer.sparkbase.sparkbaseAdminWsdl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A list of wallet offers which may be returned by the wallet offers view response.
 * 
 * <p>Java class for ArrayOfWalletOffer complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfWalletOffer">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="walletOffers" type="{urn:SparkbaseAdminV45Wsdl}WalletOffer" maxOccurs="1000" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfWalletOffer", propOrder = {
    "walletOffers"
})
public class ArrayOfWalletOffer {

    @XmlElement(nillable = true)
    protected List<WalletOffer> walletOffers;

    /**
     * Gets the value of the walletOffers property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the walletOffers property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWalletOffers().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WalletOffer }
     * 
     * 
     */
    public List<WalletOffer> getWalletOffers() {
        if (walletOffers == null) {
            walletOffers = new ArrayList<WalletOffer>();
        }
        return this.walletOffers;
    }

}

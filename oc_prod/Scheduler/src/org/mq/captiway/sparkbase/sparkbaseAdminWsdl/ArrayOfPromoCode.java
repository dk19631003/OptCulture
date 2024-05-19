package org.mq.captiway.sparkbase.sparkbaseAdminWsdl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A list of promo codes which may be returned by the promo codes view response.
 * 
 * <p>Java class for ArrayOfPromoCode complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfPromoCode">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="promoCodes" type="{urn:SparkbaseAdminV45Wsdl}PromoCode" maxOccurs="1000" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfPromoCode", propOrder = {
    "promoCodes"
})
public class ArrayOfPromoCode {

    @XmlElement(nillable = true)
    protected List<PromoCode> promoCodes;

    /**
     * Gets the value of the promoCodes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the promoCodes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPromoCodes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PromoCode }
     * 
     * 
     */
    public List<PromoCode> getPromoCodes() {
        if (promoCodes == null) {
            promoCodes = new ArrayList<PromoCode>();
        }
        return this.promoCodes;
    }

}

package org.mq.captiway.sparkbase.sparkbaseAdminWsdl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A list of promo actions which may be returned by the promo actions view response.
 * 
 * <p>Java class for ArrayOfPromoAction complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfPromoAction">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="promoActions" type="{urn:SparkbaseAdminV45Wsdl}PromoAction" maxOccurs="1000" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfPromoAction", propOrder = {
    "promoActions"
})
public class ArrayOfPromoAction {

    @XmlElement(nillable = true)
    protected List<PromoAction> promoActions;

    /**
     * Gets the value of the promoActions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the promoActions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPromoActions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PromoAction }
     * 
     * 
     */
    public List<PromoAction> getPromoActions() {
        if (promoActions == null) {
            promoActions = new ArrayList<PromoAction>();
        }
        return this.promoActions;
    }

}

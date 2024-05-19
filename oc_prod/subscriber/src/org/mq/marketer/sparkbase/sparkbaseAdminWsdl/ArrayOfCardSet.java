
package org.mq.marketer.sparkbase.sparkbaseAdminWsdl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A list of cards sets which may be returned by the card sets view.
 * 
 * <p>Java class for ArrayOfCardSet complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfCardSet">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cardSets" type="{urn:SparkbaseAdminV45Wsdl}CardSet" maxOccurs="1000" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfCardSet", propOrder = {
    "cardSets"
})
public class ArrayOfCardSet {

    @XmlElement(nillable = true)
    protected List<CardSet> cardSets;

    /**
     * Gets the value of the cardSets property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cardSets property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCardSets().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CardSet }
     * 
     * 
     */
    public List<CardSet> getCardSets() {
        if (cardSets == null) {
            cardSets = new ArrayList<CardSet>();
        }
        return this.cardSets;
    }

}

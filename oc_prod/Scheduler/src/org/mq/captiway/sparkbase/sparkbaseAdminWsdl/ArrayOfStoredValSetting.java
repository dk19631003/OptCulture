package org.mq.captiway.sparkbase.sparkbaseAdminWsdl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A list of stored value settings which may be returned by the stored value settings view response.
 * 
 * <p>Java class for ArrayOfStoredValSetting complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfStoredValSetting">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="settings" type="{urn:SparkbaseAdminV45Wsdl}StoredValSetting" maxOccurs="1000" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfStoredValSetting", propOrder = {
    "settings"
})
public class ArrayOfStoredValSetting {

    @XmlElement(nillable = true)
    protected List<StoredValSetting> settings;

    /**
     * Gets the value of the settings property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the settings property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSettings().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StoredValSetting }
     * 
     * 
     */
    public List<StoredValSetting> getSettings() {
        if (settings == null) {
            settings = new ArrayList<StoredValSetting>();
        }
        return this.settings;
    }

}

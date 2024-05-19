
package org.mq.marketer.sparkbase.sparkbaseTransactionWsdl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfQuestionAndAnswer complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfQuestionAndAnswer">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="questionAndAnswer" type="{urn:SparkbaseTransactionWsdl}QuestionAndAnswer" maxOccurs="5" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfQuestionAndAnswer", propOrder = {
    "questionAndAnswer"
})
public class ArrayOfQuestionAndAnswer {

    @XmlElement(nillable = true)
    protected List<QuestionAndAnswer> questionAndAnswer;

    /**
     * Gets the value of the questionAndAnswer property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the questionAndAnswer property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQuestionAndAnswer().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QuestionAndAnswer }
     * 
     * 
     */
    public List<QuestionAndAnswer> getQuestionAndAnswer() {
        if (questionAndAnswer == null) {
            questionAndAnswer = new ArrayList<QuestionAndAnswer>();
        }
        return this.questionAndAnswer;
    }

}

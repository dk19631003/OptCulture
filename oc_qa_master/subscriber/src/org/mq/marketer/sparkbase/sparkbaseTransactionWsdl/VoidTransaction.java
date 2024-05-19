
package org.mq.marketer.sparkbase.sparkbaseTransactionWsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A request to perform a void transaction. A void transaction will void a previous transaction.
 * 
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="standardHeader" type="{urn:SparkbaseTransactionWsdl}RequestStandardHeaderComponent" minOccurs="0"/>
 *         &lt;element name="account" type="{urn:SparkbaseTransactionWsdl}AccountComponent" minOccurs="0"/>
 *         &lt;element name="search" type="{urn:SparkbaseTransactionWsdl}SearchComponent" minOccurs="0"/>
 *         &lt;element name="questionsAndAnswers" type="{urn:SparkbaseTransactionWsdl}ArrayOfQuestionAndAnswer" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "VoidTransaction")
public class VoidTransaction {

    protected RequestStandardHeaderComponent standardHeader;
    protected AccountComponent account;
    protected SearchComponent search;
    protected ArrayOfQuestionAndAnswer questionsAndAnswers;

    /**
     * Gets the value of the standardHeader property.
     * 
     * @return
     *     possible object is
     *     {@link RequestStandardHeaderComponent }
     *     
     */
    public RequestStandardHeaderComponent getStandardHeader() {
        return standardHeader;
    }

    /**
     * Sets the value of the standardHeader property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestStandardHeaderComponent }
     *     
     */
    public void setStandardHeader(RequestStandardHeaderComponent value) {
        this.standardHeader = value;
    }

    /**
     * Gets the value of the account property.
     * 
     * @return
     *     possible object is
     *     {@link AccountComponent }
     *     
     */
    public AccountComponent getAccount() {
        return account;
    }

    /**
     * Sets the value of the account property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountComponent }
     *     
     */
    public void setAccount(AccountComponent value) {
        this.account = value;
    }

    /**
     * Gets the value of the search property.
     * 
     * @return
     *     possible object is
     *     {@link SearchComponent }
     *     
     */
    public SearchComponent getSearch() {
        return search;
    }

    /**
     * Sets the value of the search property.
     * 
     * @param value
     *     allowed object is
     *     {@link SearchComponent }
     *     
     */
    public void setSearch(SearchComponent value) {
        this.search = value;
    }

    /**
     * Gets the value of the questionsAndAnswers property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfQuestionAndAnswer }
     *     
     */
    public ArrayOfQuestionAndAnswer getQuestionsAndAnswers() {
        return questionsAndAnswers;
    }

    /**
     * Sets the value of the questionsAndAnswers property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfQuestionAndAnswer }
     *     
     */
    public void setQuestionsAndAnswers(ArrayOfQuestionAndAnswer value) {
        this.questionsAndAnswers = value;
    }

}

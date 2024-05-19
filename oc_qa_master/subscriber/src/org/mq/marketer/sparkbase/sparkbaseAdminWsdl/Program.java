
package org.mq.marketer.sparkbase.sparkbaseAdminWsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * A program which represents a set of rules for processing transactions.
 * 
 * <p>Java class for Program complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Program">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="programId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="primaryLocationId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reqEmpIdAccountHistory" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqEmpIdAdjustment" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqEmpIdEnrollment" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqEmpIdGiftIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqEmpIdGiftRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqEmpIdInquiry" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqEmpIdLoyaltyIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqEmpIdLoyaltyRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqEmpIdMultipleIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqEmpIdPromoIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqEmpIdPromoRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqEmpIdRenewal" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqEmpIdReturn" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqEmpIdTip" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqEmpIdTransfer" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqEmpIdVoid" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqEmpPassAccountHistory" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqEmpPassAdjustment" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqEmpPassEnrollment" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqEmpPassGiftIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqEmpPassGiftRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqEmpPassInquiry" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqEmpPassLoyaltyIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqEmpPassLoyaltyRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqEmpPassMultipleIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqEmpPassPromoIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqEmpPassPromoRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqEmpPassRenewal" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqEmpPassReturn" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqEmpPassTip" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqEmpPassTransfer" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqEmpPassVoid" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqCardPinAccountHistory" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqCardPinAdjustment" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqCardPinEnrollment" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqCardPinGiftIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqCardPinGiftRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqCardPinInquiry" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqCardPinLoyaltyIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqCardPinLoyaltyRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqCardPinMultipleIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqCardPinPromoIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqCardPinPromoRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqCardPinRenewal" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqCardPinReturn" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqCardPinTip" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqCardPinTransfer" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqCardPinVoid" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="acptPhAccountHistory" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="acptPhAdjustment" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="acptPhEnrollment" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="acptPhGiftIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="acptPhGiftRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="acptPhInquiry" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="acptPhLoyaltyIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="acptPhLoyaltyRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="acptPhPromoIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="acptPhPromoRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="acptPhRenewal" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="acptPhReturn" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="acptPhTip" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="acptPhVoid" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="extExpAccountHistory" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="extExpAdjustment" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="extExpEnrollment" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="extExpGiftIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="extExpGiftRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="extExpInquiry" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="extExpLoyaltyIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="extExpLoyaltyRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="extExpMultipleIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="extExpPromoIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="extExpPromoRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="extExpRenewal" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="extExpReturn" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="extExpTip" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="extExpTransfer" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="extExpVoid" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqAccPinAccountHistory" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqAccPinAdjustment" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqAccPinEnrollment" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqAccPinGiftIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqAccPinGiftRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqAccPinInquiry" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqAccPinLoyaltyIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqAccPinLoyaltyRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqAccPinMultipleIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqAccPinPromoIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqAccPinPromoRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqAccPinRenewal" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqAccPinReturn" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqAccPinTip" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqAccPinTransfer" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="reqAccPinVoid" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="autoImportAcctViaPhone" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="nsfMode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="giftReissuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="returnReissuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="acctExpType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="acctExpSoftIntervals" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="acctExpIntervals" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="acctExpIntervalType" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="acctExpFixedDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="acctExpFixedRecurring" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="issActivatesByDefault" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="renewable" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="renewExpiredAccounts" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="autoRenewOnIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="autoRecycleCards" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="custom1Label" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom2Label" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom3Label" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom4Label" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom5Label" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom6Label" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom7Label" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom8Label" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom9Label" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custom10Label" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="autobillAcctholderFunds" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="autobillOrgFunds" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="orgBankAccountId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orgBankRoutingId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="acctholderBankAccountId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="acctholderBankRoutingId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="paycloudOptIn" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Program", propOrder = {

})
public class Program {

    protected String programId;
    protected String primaryLocationId;
    protected String name;
    protected String type;
    protected String status;
    protected Boolean reqEmpIdAccountHistory;
    protected Boolean reqEmpIdAdjustment;
    protected Boolean reqEmpIdEnrollment;
    protected Boolean reqEmpIdGiftIssuance;
    protected Boolean reqEmpIdGiftRedemption;
    protected Boolean reqEmpIdInquiry;
    protected Boolean reqEmpIdLoyaltyIssuance;
    protected Boolean reqEmpIdLoyaltyRedemption;
    protected Boolean reqEmpIdMultipleIssuance;
    protected Boolean reqEmpIdPromoIssuance;
    protected Boolean reqEmpIdPromoRedemption;
    protected Boolean reqEmpIdRenewal;
    protected Boolean reqEmpIdReturn;
    protected Boolean reqEmpIdTip;
    protected Boolean reqEmpIdTransfer;
    protected Boolean reqEmpIdVoid;
    protected Boolean reqEmpPassAccountHistory;
    protected Boolean reqEmpPassAdjustment;
    protected Boolean reqEmpPassEnrollment;
    protected Boolean reqEmpPassGiftIssuance;
    protected Boolean reqEmpPassGiftRedemption;
    protected Boolean reqEmpPassInquiry;
    protected Boolean reqEmpPassLoyaltyIssuance;
    protected Boolean reqEmpPassLoyaltyRedemption;
    protected Boolean reqEmpPassMultipleIssuance;
    protected Boolean reqEmpPassPromoIssuance;
    protected Boolean reqEmpPassPromoRedemption;
    protected Boolean reqEmpPassRenewal;
    protected Boolean reqEmpPassReturn;
    protected Boolean reqEmpPassTip;
    protected Boolean reqEmpPassTransfer;
    protected Boolean reqEmpPassVoid;
    protected Boolean reqCardPinAccountHistory;
    protected Boolean reqCardPinAdjustment;
    protected Boolean reqCardPinEnrollment;
    protected Boolean reqCardPinGiftIssuance;
    protected Boolean reqCardPinGiftRedemption;
    protected Boolean reqCardPinInquiry;
    protected Boolean reqCardPinLoyaltyIssuance;
    protected Boolean reqCardPinLoyaltyRedemption;
    protected Boolean reqCardPinMultipleIssuance;
    protected Boolean reqCardPinPromoIssuance;
    protected Boolean reqCardPinPromoRedemption;
    protected Boolean reqCardPinRenewal;
    protected Boolean reqCardPinReturn;
    protected Boolean reqCardPinTip;
    protected Boolean reqCardPinTransfer;
    protected Boolean reqCardPinVoid;
    protected Boolean acptPhAccountHistory;
    protected Boolean acptPhAdjustment;
    protected Boolean acptPhEnrollment;
    protected Boolean acptPhGiftIssuance;
    protected Boolean acptPhGiftRedemption;
    protected Boolean acptPhInquiry;
    protected Boolean acptPhLoyaltyIssuance;
    protected Boolean acptPhLoyaltyRedemption;
    protected Boolean acptPhPromoIssuance;
    protected Boolean acptPhPromoRedemption;
    protected Boolean acptPhRenewal;
    protected Boolean acptPhReturn;
    protected Boolean acptPhTip;
    protected Boolean acptPhVoid;
    protected Boolean extExpAccountHistory;
    protected Boolean extExpAdjustment;
    protected Boolean extExpEnrollment;
    protected Boolean extExpGiftIssuance;
    protected Boolean extExpGiftRedemption;
    protected Boolean extExpInquiry;
    protected Boolean extExpLoyaltyIssuance;
    protected Boolean extExpLoyaltyRedemption;
    protected Boolean extExpMultipleIssuance;
    protected Boolean extExpPromoIssuance;
    protected Boolean extExpPromoRedemption;
    protected Boolean extExpRenewal;
    protected Boolean extExpReturn;
    protected Boolean extExpTip;
    protected Boolean extExpTransfer;
    protected Boolean extExpVoid;
    protected Boolean reqAccPinAccountHistory;
    protected Boolean reqAccPinAdjustment;
    protected Boolean reqAccPinEnrollment;
    protected Boolean reqAccPinGiftIssuance;
    protected Boolean reqAccPinGiftRedemption;
    protected Boolean reqAccPinInquiry;
    protected Boolean reqAccPinLoyaltyIssuance;
    protected Boolean reqAccPinLoyaltyRedemption;
    protected Boolean reqAccPinMultipleIssuance;
    protected Boolean reqAccPinPromoIssuance;
    protected Boolean reqAccPinPromoRedemption;
    protected Boolean reqAccPinRenewal;
    protected Boolean reqAccPinReturn;
    protected Boolean reqAccPinTip;
    protected Boolean reqAccPinTransfer;
    protected Boolean reqAccPinVoid;
    protected Boolean autoImportAcctViaPhone;
    protected String nsfMode;
    protected Boolean giftReissuance;
    protected Boolean returnReissuance;
    protected String acctExpType;
    protected Boolean acctExpSoftIntervals;
    protected Boolean acctExpIntervals;
    protected Boolean acctExpIntervalType;
    protected String acctExpFixedDate;
    protected Boolean acctExpFixedRecurring;
    protected Boolean issActivatesByDefault;
    protected String renewable;
    protected Boolean renewExpiredAccounts;
    protected Boolean autoRenewOnIssuance;
    protected Boolean autoRecycleCards;
    protected String custom1Label;
    protected String custom2Label;
    protected String custom3Label;
    protected String custom4Label;
    protected String custom5Label;
    protected String custom6Label;
    protected String custom7Label;
    protected String custom8Label;
    protected String custom9Label;
    protected String custom10Label;
    protected Boolean autobillAcctholderFunds;
    protected Boolean autobillOrgFunds;
    protected String orgBankAccountId;
    protected String orgBankRoutingId;
    protected String acctholderBankAccountId;
    protected String acctholderBankRoutingId;
    protected Boolean paycloudOptIn;

    /**
     * Gets the value of the programId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProgramId() {
        return programId;
    }

    /**
     * Sets the value of the programId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProgramId(String value) {
        this.programId = value;
    }

    /**
     * Gets the value of the primaryLocationId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrimaryLocationId() {
        return primaryLocationId;
    }

    /**
     * Sets the value of the primaryLocationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrimaryLocationId(String value) {
        this.primaryLocationId = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the reqEmpIdAccountHistory property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpIdAccountHistory() {
        return reqEmpIdAccountHistory;
    }

    /**
     * Sets the value of the reqEmpIdAccountHistory property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpIdAccountHistory(Boolean value) {
        this.reqEmpIdAccountHistory = value;
    }

    /**
     * Gets the value of the reqEmpIdAdjustment property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpIdAdjustment() {
        return reqEmpIdAdjustment;
    }

    /**
     * Sets the value of the reqEmpIdAdjustment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpIdAdjustment(Boolean value) {
        this.reqEmpIdAdjustment = value;
    }

    /**
     * Gets the value of the reqEmpIdEnrollment property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpIdEnrollment() {
        return reqEmpIdEnrollment;
    }

    /**
     * Sets the value of the reqEmpIdEnrollment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpIdEnrollment(Boolean value) {
        this.reqEmpIdEnrollment = value;
    }

    /**
     * Gets the value of the reqEmpIdGiftIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpIdGiftIssuance() {
        return reqEmpIdGiftIssuance;
    }

    /**
     * Sets the value of the reqEmpIdGiftIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpIdGiftIssuance(Boolean value) {
        this.reqEmpIdGiftIssuance = value;
    }

    /**
     * Gets the value of the reqEmpIdGiftRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpIdGiftRedemption() {
        return reqEmpIdGiftRedemption;
    }

    /**
     * Sets the value of the reqEmpIdGiftRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpIdGiftRedemption(Boolean value) {
        this.reqEmpIdGiftRedemption = value;
    }

    /**
     * Gets the value of the reqEmpIdInquiry property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpIdInquiry() {
        return reqEmpIdInquiry;
    }

    /**
     * Sets the value of the reqEmpIdInquiry property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpIdInquiry(Boolean value) {
        this.reqEmpIdInquiry = value;
    }

    /**
     * Gets the value of the reqEmpIdLoyaltyIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpIdLoyaltyIssuance() {
        return reqEmpIdLoyaltyIssuance;
    }

    /**
     * Sets the value of the reqEmpIdLoyaltyIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpIdLoyaltyIssuance(Boolean value) {
        this.reqEmpIdLoyaltyIssuance = value;
    }

    /**
     * Gets the value of the reqEmpIdLoyaltyRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpIdLoyaltyRedemption() {
        return reqEmpIdLoyaltyRedemption;
    }

    /**
     * Sets the value of the reqEmpIdLoyaltyRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpIdLoyaltyRedemption(Boolean value) {
        this.reqEmpIdLoyaltyRedemption = value;
    }

    /**
     * Gets the value of the reqEmpIdMultipleIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpIdMultipleIssuance() {
        return reqEmpIdMultipleIssuance;
    }

    /**
     * Sets the value of the reqEmpIdMultipleIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpIdMultipleIssuance(Boolean value) {
        this.reqEmpIdMultipleIssuance = value;
    }

    /**
     * Gets the value of the reqEmpIdPromoIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpIdPromoIssuance() {
        return reqEmpIdPromoIssuance;
    }

    /**
     * Sets the value of the reqEmpIdPromoIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpIdPromoIssuance(Boolean value) {
        this.reqEmpIdPromoIssuance = value;
    }

    /**
     * Gets the value of the reqEmpIdPromoRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpIdPromoRedemption() {
        return reqEmpIdPromoRedemption;
    }

    /**
     * Sets the value of the reqEmpIdPromoRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpIdPromoRedemption(Boolean value) {
        this.reqEmpIdPromoRedemption = value;
    }

    /**
     * Gets the value of the reqEmpIdRenewal property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpIdRenewal() {
        return reqEmpIdRenewal;
    }

    /**
     * Sets the value of the reqEmpIdRenewal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpIdRenewal(Boolean value) {
        this.reqEmpIdRenewal = value;
    }

    /**
     * Gets the value of the reqEmpIdReturn property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpIdReturn() {
        return reqEmpIdReturn;
    }

    /**
     * Sets the value of the reqEmpIdReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpIdReturn(Boolean value) {
        this.reqEmpIdReturn = value;
    }

    /**
     * Gets the value of the reqEmpIdTip property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpIdTip() {
        return reqEmpIdTip;
    }

    /**
     * Sets the value of the reqEmpIdTip property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpIdTip(Boolean value) {
        this.reqEmpIdTip = value;
    }

    /**
     * Gets the value of the reqEmpIdTransfer property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpIdTransfer() {
        return reqEmpIdTransfer;
    }

    /**
     * Sets the value of the reqEmpIdTransfer property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpIdTransfer(Boolean value) {
        this.reqEmpIdTransfer = value;
    }

    /**
     * Gets the value of the reqEmpIdVoid property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpIdVoid() {
        return reqEmpIdVoid;
    }

    /**
     * Sets the value of the reqEmpIdVoid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpIdVoid(Boolean value) {
        this.reqEmpIdVoid = value;
    }

    /**
     * Gets the value of the reqEmpPassAccountHistory property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpPassAccountHistory() {
        return reqEmpPassAccountHistory;
    }

    /**
     * Sets the value of the reqEmpPassAccountHistory property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpPassAccountHistory(Boolean value) {
        this.reqEmpPassAccountHistory = value;
    }

    /**
     * Gets the value of the reqEmpPassAdjustment property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpPassAdjustment() {
        return reqEmpPassAdjustment;
    }

    /**
     * Sets the value of the reqEmpPassAdjustment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpPassAdjustment(Boolean value) {
        this.reqEmpPassAdjustment = value;
    }

    /**
     * Gets the value of the reqEmpPassEnrollment property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpPassEnrollment() {
        return reqEmpPassEnrollment;
    }

    /**
     * Sets the value of the reqEmpPassEnrollment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpPassEnrollment(Boolean value) {
        this.reqEmpPassEnrollment = value;
    }

    /**
     * Gets the value of the reqEmpPassGiftIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpPassGiftIssuance() {
        return reqEmpPassGiftIssuance;
    }

    /**
     * Sets the value of the reqEmpPassGiftIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpPassGiftIssuance(Boolean value) {
        this.reqEmpPassGiftIssuance = value;
    }

    /**
     * Gets the value of the reqEmpPassGiftRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpPassGiftRedemption() {
        return reqEmpPassGiftRedemption;
    }

    /**
     * Sets the value of the reqEmpPassGiftRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpPassGiftRedemption(Boolean value) {
        this.reqEmpPassGiftRedemption = value;
    }

    /**
     * Gets the value of the reqEmpPassInquiry property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpPassInquiry() {
        return reqEmpPassInquiry;
    }

    /**
     * Sets the value of the reqEmpPassInquiry property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpPassInquiry(Boolean value) {
        this.reqEmpPassInquiry = value;
    }

    /**
     * Gets the value of the reqEmpPassLoyaltyIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpPassLoyaltyIssuance() {
        return reqEmpPassLoyaltyIssuance;
    }

    /**
     * Sets the value of the reqEmpPassLoyaltyIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpPassLoyaltyIssuance(Boolean value) {
        this.reqEmpPassLoyaltyIssuance = value;
    }

    /**
     * Gets the value of the reqEmpPassLoyaltyRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpPassLoyaltyRedemption() {
        return reqEmpPassLoyaltyRedemption;
    }

    /**
     * Sets the value of the reqEmpPassLoyaltyRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpPassLoyaltyRedemption(Boolean value) {
        this.reqEmpPassLoyaltyRedemption = value;
    }

    /**
     * Gets the value of the reqEmpPassMultipleIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpPassMultipleIssuance() {
        return reqEmpPassMultipleIssuance;
    }

    /**
     * Sets the value of the reqEmpPassMultipleIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpPassMultipleIssuance(Boolean value) {
        this.reqEmpPassMultipleIssuance = value;
    }

    /**
     * Gets the value of the reqEmpPassPromoIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpPassPromoIssuance() {
        return reqEmpPassPromoIssuance;
    }

    /**
     * Sets the value of the reqEmpPassPromoIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpPassPromoIssuance(Boolean value) {
        this.reqEmpPassPromoIssuance = value;
    }

    /**
     * Gets the value of the reqEmpPassPromoRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpPassPromoRedemption() {
        return reqEmpPassPromoRedemption;
    }

    /**
     * Sets the value of the reqEmpPassPromoRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpPassPromoRedemption(Boolean value) {
        this.reqEmpPassPromoRedemption = value;
    }

    /**
     * Gets the value of the reqEmpPassRenewal property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpPassRenewal() {
        return reqEmpPassRenewal;
    }

    /**
     * Sets the value of the reqEmpPassRenewal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpPassRenewal(Boolean value) {
        this.reqEmpPassRenewal = value;
    }

    /**
     * Gets the value of the reqEmpPassReturn property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpPassReturn() {
        return reqEmpPassReturn;
    }

    /**
     * Sets the value of the reqEmpPassReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpPassReturn(Boolean value) {
        this.reqEmpPassReturn = value;
    }

    /**
     * Gets the value of the reqEmpPassTip property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpPassTip() {
        return reqEmpPassTip;
    }

    /**
     * Sets the value of the reqEmpPassTip property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpPassTip(Boolean value) {
        this.reqEmpPassTip = value;
    }

    /**
     * Gets the value of the reqEmpPassTransfer property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpPassTransfer() {
        return reqEmpPassTransfer;
    }

    /**
     * Sets the value of the reqEmpPassTransfer property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpPassTransfer(Boolean value) {
        this.reqEmpPassTransfer = value;
    }

    /**
     * Gets the value of the reqEmpPassVoid property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqEmpPassVoid() {
        return reqEmpPassVoid;
    }

    /**
     * Sets the value of the reqEmpPassVoid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqEmpPassVoid(Boolean value) {
        this.reqEmpPassVoid = value;
    }

    /**
     * Gets the value of the reqCardPinAccountHistory property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqCardPinAccountHistory() {
        return reqCardPinAccountHistory;
    }

    /**
     * Sets the value of the reqCardPinAccountHistory property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqCardPinAccountHistory(Boolean value) {
        this.reqCardPinAccountHistory = value;
    }

    /**
     * Gets the value of the reqCardPinAdjustment property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqCardPinAdjustment() {
        return reqCardPinAdjustment;
    }

    /**
     * Sets the value of the reqCardPinAdjustment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqCardPinAdjustment(Boolean value) {
        this.reqCardPinAdjustment = value;
    }

    /**
     * Gets the value of the reqCardPinEnrollment property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqCardPinEnrollment() {
        return reqCardPinEnrollment;
    }

    /**
     * Sets the value of the reqCardPinEnrollment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqCardPinEnrollment(Boolean value) {
        this.reqCardPinEnrollment = value;
    }

    /**
     * Gets the value of the reqCardPinGiftIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqCardPinGiftIssuance() {
        return reqCardPinGiftIssuance;
    }

    /**
     * Sets the value of the reqCardPinGiftIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqCardPinGiftIssuance(Boolean value) {
        this.reqCardPinGiftIssuance = value;
    }

    /**
     * Gets the value of the reqCardPinGiftRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqCardPinGiftRedemption() {
        return reqCardPinGiftRedemption;
    }

    /**
     * Sets the value of the reqCardPinGiftRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqCardPinGiftRedemption(Boolean value) {
        this.reqCardPinGiftRedemption = value;
    }

    /**
     * Gets the value of the reqCardPinInquiry property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqCardPinInquiry() {
        return reqCardPinInquiry;
    }

    /**
     * Sets the value of the reqCardPinInquiry property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqCardPinInquiry(Boolean value) {
        this.reqCardPinInquiry = value;
    }

    /**
     * Gets the value of the reqCardPinLoyaltyIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqCardPinLoyaltyIssuance() {
        return reqCardPinLoyaltyIssuance;
    }

    /**
     * Sets the value of the reqCardPinLoyaltyIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqCardPinLoyaltyIssuance(Boolean value) {
        this.reqCardPinLoyaltyIssuance = value;
    }

    /**
     * Gets the value of the reqCardPinLoyaltyRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqCardPinLoyaltyRedemption() {
        return reqCardPinLoyaltyRedemption;
    }

    /**
     * Sets the value of the reqCardPinLoyaltyRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqCardPinLoyaltyRedemption(Boolean value) {
        this.reqCardPinLoyaltyRedemption = value;
    }

    /**
     * Gets the value of the reqCardPinMultipleIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqCardPinMultipleIssuance() {
        return reqCardPinMultipleIssuance;
    }

    /**
     * Sets the value of the reqCardPinMultipleIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqCardPinMultipleIssuance(Boolean value) {
        this.reqCardPinMultipleIssuance = value;
    }

    /**
     * Gets the value of the reqCardPinPromoIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqCardPinPromoIssuance() {
        return reqCardPinPromoIssuance;
    }

    /**
     * Sets the value of the reqCardPinPromoIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqCardPinPromoIssuance(Boolean value) {
        this.reqCardPinPromoIssuance = value;
    }

    /**
     * Gets the value of the reqCardPinPromoRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqCardPinPromoRedemption() {
        return reqCardPinPromoRedemption;
    }

    /**
     * Sets the value of the reqCardPinPromoRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqCardPinPromoRedemption(Boolean value) {
        this.reqCardPinPromoRedemption = value;
    }

    /**
     * Gets the value of the reqCardPinRenewal property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqCardPinRenewal() {
        return reqCardPinRenewal;
    }

    /**
     * Sets the value of the reqCardPinRenewal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqCardPinRenewal(Boolean value) {
        this.reqCardPinRenewal = value;
    }

    /**
     * Gets the value of the reqCardPinReturn property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqCardPinReturn() {
        return reqCardPinReturn;
    }

    /**
     * Sets the value of the reqCardPinReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqCardPinReturn(Boolean value) {
        this.reqCardPinReturn = value;
    }

    /**
     * Gets the value of the reqCardPinTip property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqCardPinTip() {
        return reqCardPinTip;
    }

    /**
     * Sets the value of the reqCardPinTip property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqCardPinTip(Boolean value) {
        this.reqCardPinTip = value;
    }

    /**
     * Gets the value of the reqCardPinTransfer property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqCardPinTransfer() {
        return reqCardPinTransfer;
    }

    /**
     * Sets the value of the reqCardPinTransfer property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqCardPinTransfer(Boolean value) {
        this.reqCardPinTransfer = value;
    }

    /**
     * Gets the value of the reqCardPinVoid property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqCardPinVoid() {
        return reqCardPinVoid;
    }

    /**
     * Sets the value of the reqCardPinVoid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqCardPinVoid(Boolean value) {
        this.reqCardPinVoid = value;
    }

    /**
     * Gets the value of the acptPhAccountHistory property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAcptPhAccountHistory() {
        return acptPhAccountHistory;
    }

    /**
     * Sets the value of the acptPhAccountHistory property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAcptPhAccountHistory(Boolean value) {
        this.acptPhAccountHistory = value;
    }

    /**
     * Gets the value of the acptPhAdjustment property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAcptPhAdjustment() {
        return acptPhAdjustment;
    }

    /**
     * Sets the value of the acptPhAdjustment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAcptPhAdjustment(Boolean value) {
        this.acptPhAdjustment = value;
    }

    /**
     * Gets the value of the acptPhEnrollment property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAcptPhEnrollment() {
        return acptPhEnrollment;
    }

    /**
     * Sets the value of the acptPhEnrollment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAcptPhEnrollment(Boolean value) {
        this.acptPhEnrollment = value;
    }

    /**
     * Gets the value of the acptPhGiftIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAcptPhGiftIssuance() {
        return acptPhGiftIssuance;
    }

    /**
     * Sets the value of the acptPhGiftIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAcptPhGiftIssuance(Boolean value) {
        this.acptPhGiftIssuance = value;
    }

    /**
     * Gets the value of the acptPhGiftRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAcptPhGiftRedemption() {
        return acptPhGiftRedemption;
    }

    /**
     * Sets the value of the acptPhGiftRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAcptPhGiftRedemption(Boolean value) {
        this.acptPhGiftRedemption = value;
    }

    /**
     * Gets the value of the acptPhInquiry property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAcptPhInquiry() {
        return acptPhInquiry;
    }

    /**
     * Sets the value of the acptPhInquiry property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAcptPhInquiry(Boolean value) {
        this.acptPhInquiry = value;
    }

    /**
     * Gets the value of the acptPhLoyaltyIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAcptPhLoyaltyIssuance() {
        return acptPhLoyaltyIssuance;
    }

    /**
     * Sets the value of the acptPhLoyaltyIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAcptPhLoyaltyIssuance(Boolean value) {
        this.acptPhLoyaltyIssuance = value;
    }

    /**
     * Gets the value of the acptPhLoyaltyRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAcptPhLoyaltyRedemption() {
        return acptPhLoyaltyRedemption;
    }

    /**
     * Sets the value of the acptPhLoyaltyRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAcptPhLoyaltyRedemption(Boolean value) {
        this.acptPhLoyaltyRedemption = value;
    }

    /**
     * Gets the value of the acptPhPromoIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAcptPhPromoIssuance() {
        return acptPhPromoIssuance;
    }

    /**
     * Sets the value of the acptPhPromoIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAcptPhPromoIssuance(Boolean value) {
        this.acptPhPromoIssuance = value;
    }

    /**
     * Gets the value of the acptPhPromoRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAcptPhPromoRedemption() {
        return acptPhPromoRedemption;
    }

    /**
     * Sets the value of the acptPhPromoRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAcptPhPromoRedemption(Boolean value) {
        this.acptPhPromoRedemption = value;
    }

    /**
     * Gets the value of the acptPhRenewal property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAcptPhRenewal() {
        return acptPhRenewal;
    }

    /**
     * Sets the value of the acptPhRenewal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAcptPhRenewal(Boolean value) {
        this.acptPhRenewal = value;
    }

    /**
     * Gets the value of the acptPhReturn property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAcptPhReturn() {
        return acptPhReturn;
    }

    /**
     * Sets the value of the acptPhReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAcptPhReturn(Boolean value) {
        this.acptPhReturn = value;
    }

    /**
     * Gets the value of the acptPhTip property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAcptPhTip() {
        return acptPhTip;
    }

    /**
     * Sets the value of the acptPhTip property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAcptPhTip(Boolean value) {
        this.acptPhTip = value;
    }

    /**
     * Gets the value of the acptPhVoid property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAcptPhVoid() {
        return acptPhVoid;
    }

    /**
     * Sets the value of the acptPhVoid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAcptPhVoid(Boolean value) {
        this.acptPhVoid = value;
    }

    /**
     * Gets the value of the extExpAccountHistory property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExtExpAccountHistory() {
        return extExpAccountHistory;
    }

    /**
     * Sets the value of the extExpAccountHistory property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExtExpAccountHistory(Boolean value) {
        this.extExpAccountHistory = value;
    }

    /**
     * Gets the value of the extExpAdjustment property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExtExpAdjustment() {
        return extExpAdjustment;
    }

    /**
     * Sets the value of the extExpAdjustment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExtExpAdjustment(Boolean value) {
        this.extExpAdjustment = value;
    }

    /**
     * Gets the value of the extExpEnrollment property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExtExpEnrollment() {
        return extExpEnrollment;
    }

    /**
     * Sets the value of the extExpEnrollment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExtExpEnrollment(Boolean value) {
        this.extExpEnrollment = value;
    }

    /**
     * Gets the value of the extExpGiftIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExtExpGiftIssuance() {
        return extExpGiftIssuance;
    }

    /**
     * Sets the value of the extExpGiftIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExtExpGiftIssuance(Boolean value) {
        this.extExpGiftIssuance = value;
    }

    /**
     * Gets the value of the extExpGiftRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExtExpGiftRedemption() {
        return extExpGiftRedemption;
    }

    /**
     * Sets the value of the extExpGiftRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExtExpGiftRedemption(Boolean value) {
        this.extExpGiftRedemption = value;
    }

    /**
     * Gets the value of the extExpInquiry property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExtExpInquiry() {
        return extExpInquiry;
    }

    /**
     * Sets the value of the extExpInquiry property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExtExpInquiry(Boolean value) {
        this.extExpInquiry = value;
    }

    /**
     * Gets the value of the extExpLoyaltyIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExtExpLoyaltyIssuance() {
        return extExpLoyaltyIssuance;
    }

    /**
     * Sets the value of the extExpLoyaltyIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExtExpLoyaltyIssuance(Boolean value) {
        this.extExpLoyaltyIssuance = value;
    }

    /**
     * Gets the value of the extExpLoyaltyRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExtExpLoyaltyRedemption() {
        return extExpLoyaltyRedemption;
    }

    /**
     * Sets the value of the extExpLoyaltyRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExtExpLoyaltyRedemption(Boolean value) {
        this.extExpLoyaltyRedemption = value;
    }

    /**
     * Gets the value of the extExpMultipleIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExtExpMultipleIssuance() {
        return extExpMultipleIssuance;
    }

    /**
     * Sets the value of the extExpMultipleIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExtExpMultipleIssuance(Boolean value) {
        this.extExpMultipleIssuance = value;
    }

    /**
     * Gets the value of the extExpPromoIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExtExpPromoIssuance() {
        return extExpPromoIssuance;
    }

    /**
     * Sets the value of the extExpPromoIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExtExpPromoIssuance(Boolean value) {
        this.extExpPromoIssuance = value;
    }

    /**
     * Gets the value of the extExpPromoRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExtExpPromoRedemption() {
        return extExpPromoRedemption;
    }

    /**
     * Sets the value of the extExpPromoRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExtExpPromoRedemption(Boolean value) {
        this.extExpPromoRedemption = value;
    }

    /**
     * Gets the value of the extExpRenewal property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExtExpRenewal() {
        return extExpRenewal;
    }

    /**
     * Sets the value of the extExpRenewal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExtExpRenewal(Boolean value) {
        this.extExpRenewal = value;
    }

    /**
     * Gets the value of the extExpReturn property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExtExpReturn() {
        return extExpReturn;
    }

    /**
     * Sets the value of the extExpReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExtExpReturn(Boolean value) {
        this.extExpReturn = value;
    }

    /**
     * Gets the value of the extExpTip property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExtExpTip() {
        return extExpTip;
    }

    /**
     * Sets the value of the extExpTip property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExtExpTip(Boolean value) {
        this.extExpTip = value;
    }

    /**
     * Gets the value of the extExpTransfer property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExtExpTransfer() {
        return extExpTransfer;
    }

    /**
     * Sets the value of the extExpTransfer property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExtExpTransfer(Boolean value) {
        this.extExpTransfer = value;
    }

    /**
     * Gets the value of the extExpVoid property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExtExpVoid() {
        return extExpVoid;
    }

    /**
     * Sets the value of the extExpVoid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExtExpVoid(Boolean value) {
        this.extExpVoid = value;
    }

    /**
     * Gets the value of the reqAccPinAccountHistory property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqAccPinAccountHistory() {
        return reqAccPinAccountHistory;
    }

    /**
     * Sets the value of the reqAccPinAccountHistory property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqAccPinAccountHistory(Boolean value) {
        this.reqAccPinAccountHistory = value;
    }

    /**
     * Gets the value of the reqAccPinAdjustment property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqAccPinAdjustment() {
        return reqAccPinAdjustment;
    }

    /**
     * Sets the value of the reqAccPinAdjustment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqAccPinAdjustment(Boolean value) {
        this.reqAccPinAdjustment = value;
    }

    /**
     * Gets the value of the reqAccPinEnrollment property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqAccPinEnrollment() {
        return reqAccPinEnrollment;
    }

    /**
     * Sets the value of the reqAccPinEnrollment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqAccPinEnrollment(Boolean value) {
        this.reqAccPinEnrollment = value;
    }

    /**
     * Gets the value of the reqAccPinGiftIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqAccPinGiftIssuance() {
        return reqAccPinGiftIssuance;
    }

    /**
     * Sets the value of the reqAccPinGiftIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqAccPinGiftIssuance(Boolean value) {
        this.reqAccPinGiftIssuance = value;
    }

    /**
     * Gets the value of the reqAccPinGiftRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqAccPinGiftRedemption() {
        return reqAccPinGiftRedemption;
    }

    /**
     * Sets the value of the reqAccPinGiftRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqAccPinGiftRedemption(Boolean value) {
        this.reqAccPinGiftRedemption = value;
    }

    /**
     * Gets the value of the reqAccPinInquiry property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqAccPinInquiry() {
        return reqAccPinInquiry;
    }

    /**
     * Sets the value of the reqAccPinInquiry property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqAccPinInquiry(Boolean value) {
        this.reqAccPinInquiry = value;
    }

    /**
     * Gets the value of the reqAccPinLoyaltyIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqAccPinLoyaltyIssuance() {
        return reqAccPinLoyaltyIssuance;
    }

    /**
     * Sets the value of the reqAccPinLoyaltyIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqAccPinLoyaltyIssuance(Boolean value) {
        this.reqAccPinLoyaltyIssuance = value;
    }

    /**
     * Gets the value of the reqAccPinLoyaltyRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqAccPinLoyaltyRedemption() {
        return reqAccPinLoyaltyRedemption;
    }

    /**
     * Sets the value of the reqAccPinLoyaltyRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqAccPinLoyaltyRedemption(Boolean value) {
        this.reqAccPinLoyaltyRedemption = value;
    }

    /**
     * Gets the value of the reqAccPinMultipleIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqAccPinMultipleIssuance() {
        return reqAccPinMultipleIssuance;
    }

    /**
     * Sets the value of the reqAccPinMultipleIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqAccPinMultipleIssuance(Boolean value) {
        this.reqAccPinMultipleIssuance = value;
    }

    /**
     * Gets the value of the reqAccPinPromoIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqAccPinPromoIssuance() {
        return reqAccPinPromoIssuance;
    }

    /**
     * Sets the value of the reqAccPinPromoIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqAccPinPromoIssuance(Boolean value) {
        this.reqAccPinPromoIssuance = value;
    }

    /**
     * Gets the value of the reqAccPinPromoRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqAccPinPromoRedemption() {
        return reqAccPinPromoRedemption;
    }

    /**
     * Sets the value of the reqAccPinPromoRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqAccPinPromoRedemption(Boolean value) {
        this.reqAccPinPromoRedemption = value;
    }

    /**
     * Gets the value of the reqAccPinRenewal property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqAccPinRenewal() {
        return reqAccPinRenewal;
    }

    /**
     * Sets the value of the reqAccPinRenewal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqAccPinRenewal(Boolean value) {
        this.reqAccPinRenewal = value;
    }

    /**
     * Gets the value of the reqAccPinReturn property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqAccPinReturn() {
        return reqAccPinReturn;
    }

    /**
     * Sets the value of the reqAccPinReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqAccPinReturn(Boolean value) {
        this.reqAccPinReturn = value;
    }

    /**
     * Gets the value of the reqAccPinTip property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqAccPinTip() {
        return reqAccPinTip;
    }

    /**
     * Sets the value of the reqAccPinTip property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqAccPinTip(Boolean value) {
        this.reqAccPinTip = value;
    }

    /**
     * Gets the value of the reqAccPinTransfer property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqAccPinTransfer() {
        return reqAccPinTransfer;
    }

    /**
     * Sets the value of the reqAccPinTransfer property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqAccPinTransfer(Boolean value) {
        this.reqAccPinTransfer = value;
    }

    /**
     * Gets the value of the reqAccPinVoid property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReqAccPinVoid() {
        return reqAccPinVoid;
    }

    /**
     * Sets the value of the reqAccPinVoid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReqAccPinVoid(Boolean value) {
        this.reqAccPinVoid = value;
    }

    /**
     * Gets the value of the autoImportAcctViaPhone property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAutoImportAcctViaPhone() {
        return autoImportAcctViaPhone;
    }

    /**
     * Sets the value of the autoImportAcctViaPhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAutoImportAcctViaPhone(Boolean value) {
        this.autoImportAcctViaPhone = value;
    }

    /**
     * Gets the value of the nsfMode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNsfMode() {
        return nsfMode;
    }

    /**
     * Sets the value of the nsfMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNsfMode(String value) {
        this.nsfMode = value;
    }

    /**
     * Gets the value of the giftReissuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isGiftReissuance() {
        return giftReissuance;
    }

    /**
     * Sets the value of the giftReissuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setGiftReissuance(Boolean value) {
        this.giftReissuance = value;
    }

    /**
     * Gets the value of the returnReissuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReturnReissuance() {
        return returnReissuance;
    }

    /**
     * Sets the value of the returnReissuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReturnReissuance(Boolean value) {
        this.returnReissuance = value;
    }

    /**
     * Gets the value of the acctExpType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAcctExpType() {
        return acctExpType;
    }

    /**
     * Sets the value of the acctExpType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAcctExpType(String value) {
        this.acctExpType = value;
    }

    /**
     * Gets the value of the acctExpSoftIntervals property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAcctExpSoftIntervals() {
        return acctExpSoftIntervals;
    }

    /**
     * Sets the value of the acctExpSoftIntervals property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAcctExpSoftIntervals(Boolean value) {
        this.acctExpSoftIntervals = value;
    }

    /**
     * Gets the value of the acctExpIntervals property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAcctExpIntervals() {
        return acctExpIntervals;
    }

    /**
     * Sets the value of the acctExpIntervals property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAcctExpIntervals(Boolean value) {
        this.acctExpIntervals = value;
    }

    /**
     * Gets the value of the acctExpIntervalType property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAcctExpIntervalType() {
        return acctExpIntervalType;
    }

    /**
     * Sets the value of the acctExpIntervalType property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAcctExpIntervalType(Boolean value) {
        this.acctExpIntervalType = value;
    }

    /**
     * Gets the value of the acctExpFixedDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAcctExpFixedDate() {
        return acctExpFixedDate;
    }

    /**
     * Sets the value of the acctExpFixedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAcctExpFixedDate(String value) {
        this.acctExpFixedDate = value;
    }

    /**
     * Gets the value of the acctExpFixedRecurring property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAcctExpFixedRecurring() {
        return acctExpFixedRecurring;
    }

    /**
     * Sets the value of the acctExpFixedRecurring property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAcctExpFixedRecurring(Boolean value) {
        this.acctExpFixedRecurring = value;
    }

    /**
     * Gets the value of the issActivatesByDefault property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIssActivatesByDefault() {
        return issActivatesByDefault;
    }

    /**
     * Sets the value of the issActivatesByDefault property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIssActivatesByDefault(Boolean value) {
        this.issActivatesByDefault = value;
    }

    /**
     * Gets the value of the renewable property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRenewable() {
        return renewable;
    }

    /**
     * Sets the value of the renewable property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRenewable(String value) {
        this.renewable = value;
    }

    /**
     * Gets the value of the renewExpiredAccounts property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRenewExpiredAccounts() {
        return renewExpiredAccounts;
    }

    /**
     * Sets the value of the renewExpiredAccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRenewExpiredAccounts(Boolean value) {
        this.renewExpiredAccounts = value;
    }

    /**
     * Gets the value of the autoRenewOnIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAutoRenewOnIssuance() {
        return autoRenewOnIssuance;
    }

    /**
     * Sets the value of the autoRenewOnIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAutoRenewOnIssuance(Boolean value) {
        this.autoRenewOnIssuance = value;
    }

    /**
     * Gets the value of the autoRecycleCards property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAutoRecycleCards() {
        return autoRecycleCards;
    }

    /**
     * Sets the value of the autoRecycleCards property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAutoRecycleCards(Boolean value) {
        this.autoRecycleCards = value;
    }

    /**
     * Gets the value of the custom1Label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustom1Label() {
        return custom1Label;
    }

    /**
     * Sets the value of the custom1Label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustom1Label(String value) {
        this.custom1Label = value;
    }

    /**
     * Gets the value of the custom2Label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustom2Label() {
        return custom2Label;
    }

    /**
     * Sets the value of the custom2Label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustom2Label(String value) {
        this.custom2Label = value;
    }

    /**
     * Gets the value of the custom3Label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustom3Label() {
        return custom3Label;
    }

    /**
     * Sets the value of the custom3Label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustom3Label(String value) {
        this.custom3Label = value;
    }

    /**
     * Gets the value of the custom4Label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustom4Label() {
        return custom4Label;
    }

    /**
     * Sets the value of the custom4Label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustom4Label(String value) {
        this.custom4Label = value;
    }

    /**
     * Gets the value of the custom5Label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustom5Label() {
        return custom5Label;
    }

    /**
     * Sets the value of the custom5Label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustom5Label(String value) {
        this.custom5Label = value;
    }

    /**
     * Gets the value of the custom6Label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustom6Label() {
        return custom6Label;
    }

    /**
     * Sets the value of the custom6Label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustom6Label(String value) {
        this.custom6Label = value;
    }

    /**
     * Gets the value of the custom7Label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustom7Label() {
        return custom7Label;
    }

    /**
     * Sets the value of the custom7Label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustom7Label(String value) {
        this.custom7Label = value;
    }

    /**
     * Gets the value of the custom8Label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustom8Label() {
        return custom8Label;
    }

    /**
     * Sets the value of the custom8Label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustom8Label(String value) {
        this.custom8Label = value;
    }

    /**
     * Gets the value of the custom9Label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustom9Label() {
        return custom9Label;
    }

    /**
     * Sets the value of the custom9Label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustom9Label(String value) {
        this.custom9Label = value;
    }

    /**
     * Gets the value of the custom10Label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustom10Label() {
        return custom10Label;
    }

    /**
     * Sets the value of the custom10Label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustom10Label(String value) {
        this.custom10Label = value;
    }

    /**
     * Gets the value of the autobillAcctholderFunds property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAutobillAcctholderFunds() {
        return autobillAcctholderFunds;
    }

    /**
     * Sets the value of the autobillAcctholderFunds property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAutobillAcctholderFunds(Boolean value) {
        this.autobillAcctholderFunds = value;
    }

    /**
     * Gets the value of the autobillOrgFunds property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAutobillOrgFunds() {
        return autobillOrgFunds;
    }

    /**
     * Sets the value of the autobillOrgFunds property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAutobillOrgFunds(Boolean value) {
        this.autobillOrgFunds = value;
    }

    /**
     * Gets the value of the orgBankAccountId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgBankAccountId() {
        return orgBankAccountId;
    }

    /**
     * Sets the value of the orgBankAccountId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgBankAccountId(String value) {
        this.orgBankAccountId = value;
    }

    /**
     * Gets the value of the orgBankRoutingId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgBankRoutingId() {
        return orgBankRoutingId;
    }

    /**
     * Sets the value of the orgBankRoutingId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgBankRoutingId(String value) {
        this.orgBankRoutingId = value;
    }

    /**
     * Gets the value of the acctholderBankAccountId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAcctholderBankAccountId() {
        return acctholderBankAccountId;
    }

    /**
     * Sets the value of the acctholderBankAccountId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAcctholderBankAccountId(String value) {
        this.acctholderBankAccountId = value;
    }

    /**
     * Gets the value of the acctholderBankRoutingId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAcctholderBankRoutingId() {
        return acctholderBankRoutingId;
    }

    /**
     * Sets the value of the acctholderBankRoutingId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAcctholderBankRoutingId(String value) {
        this.acctholderBankRoutingId = value;
    }

    /**
     * Gets the value of the paycloudOptIn property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPaycloudOptIn() {
        return paycloudOptIn;
    }

    /**
     * Sets the value of the paycloudOptIn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPaycloudOptIn(Boolean value) {
        this.paycloudOptIn = value;
    }

}


package org.mq.marketer.sparkbase.sparkbaseAdminWsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * A stored value setting which represents a set of rules for terminal behavior.
 * 
 * <p>Java class for StoredValSetting complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StoredValSetting">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="storedValSettingId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="locationId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cardCharsVisible" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ipCheckRequired" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="managerPassword" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nsfMode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="supportInfo1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="supportInfo2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ipEnabled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="dialEnabled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="secondReceiptPrompt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="encodingStyle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="loyaltyDecimalPrompt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="receiptHeader1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="receiptHeader2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="receiptHeader3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="receiptHeader4" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="receiptFooter1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="receiptFooter2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="receiptFooter3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="receiptFooter4" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="accountHistoryLabel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="employeeReportLabel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="enrollmentLabel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="giftIssuanceLabel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="giftRedemptionLabel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="holdLabel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="holdRedemptionLabel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="holdReturnLabel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="inquiryLabel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="loyaltyIssuanceLabel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="loyaltyRedemptionLabel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="multipleIssuanceLabel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="promoIssuanceLabel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="promoRedemptionLabel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="renewalLabel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="returnLabel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="terminalReportLabel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tipLabel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="transferLabel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="voidLabel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ttAccountHistory" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ttAdjustment" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ttEnrollment" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ttGiftIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ttGiftRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ttInquiry" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ttLoyaltyIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ttLoyaltyRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ttMultipleIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ttPromoIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ttPromoRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ttRenewal" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ttReturn" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ttTip" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ttTransfer" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ttVoid" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ttQuickTransaction" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ttEmployeeReport" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ttTerminalReport" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="peiAccountHistory" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="peiAdjustment" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="peiEnrollment" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="peiGiftIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="peiGiftRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="peiInquiry" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="peiLoyaltyIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="peiLoyaltyRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="peiMultipleIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="peiPromoIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="peiPromoRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="peiRenewal" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="peiReturn" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="peiTip" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="peiTransfer" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="peiVoid" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="peiQuickTransaction" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="peiEmployeeReport" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="peiTerminalReport" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pepAccountHistory" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pepAdjustment" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pepEnrollment" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pepGiftIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pepGiftRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pepInquiry" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pepLoyaltyIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pepLoyaltyRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pepMultipleIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pepPromoIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pepPromoRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pepRenewal" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pepReturn" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pepTip" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pepTransfer" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pepVoid" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pepQuickTransaction" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pepEmployeeReport" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pepTerminalReport" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pcpAccountHistory" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pcpAdjustment" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pcpEnrollment" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pcpGiftIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pcpGiftRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pcpInquiry" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pcpLoyaltyIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pcpLoyaltyRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pcpMultipleIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pcpPromoIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pcpPromoRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pcpRenewal" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pcpReturn" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pcpTip" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pcpTransfer" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pcpVoid" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="paAccountHistory" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="paAdjustment" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="paEnrollment" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="paGiftIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="paGiftRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="paInquiry" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="paLoyaltyIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="paLoyaltyRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="paMultipleIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="paPromoIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="paPromoRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="paRenewal" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="paReturn" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="paTip" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="paTransfer" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="paVoid" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pmpAccountHistory" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pmpAdjustment" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pmpEnrollment" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pmpGiftIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pmpGiftRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pmpInquiry" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pmpLoyaltyIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pmpLoyaltyRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pmpMultipleIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pmpPromoIssuance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pmpPromoRedemption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pmpRenewal" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pmpReturn" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pmpTip" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pmpTransfer" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pmpVoid" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pmpQuickTransaction" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pmpEmployeeReport" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pmpTerminalReport" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="epType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="epFirst" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="epMiddle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="epLast" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="epAddr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="epCity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="epState" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="epPostal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="epCountry" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="epMailPref" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="epPhone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="epIsMobile" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="epPhonePref" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="epEmail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="epEmailPref" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="epBirthday" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="epAnniversary" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="epGender" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nepType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nepFirst" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nepMiddle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nepLast" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nepAddr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nepCity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nepState" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nepPostal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nepCountry" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nepMailPref" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nepPhone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nepIsMobile" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nepPhonePref" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nepEmail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nepEmailPref" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nepBirthday" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nepAnniversary" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nepGender" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="promptPromoCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StoredValSetting", propOrder = {

})
public class StoredValSetting {

    protected String storedValSettingId;
    protected String name;
    protected String title;
    protected String locationId;
    protected String cardCharsVisible;
    protected Boolean ipCheckRequired;
    protected String managerPassword;
    protected String nsfMode;
    protected String supportInfo1;
    protected String supportInfo2;
    protected Boolean ipEnabled;
    protected Boolean dialEnabled;
    protected String secondReceiptPrompt;
    protected String encodingStyle;
    protected String loyaltyDecimalPrompt;
    protected String receiptHeader1;
    protected String receiptHeader2;
    protected String receiptHeader3;
    protected String receiptHeader4;
    protected String receiptFooter1;
    protected String receiptFooter2;
    protected String receiptFooter3;
    protected String receiptFooter4;
    protected String accountHistoryLabel;
    protected String employeeReportLabel;
    protected String enrollmentLabel;
    protected String giftIssuanceLabel;
    protected String giftRedemptionLabel;
    protected String holdLabel;
    protected String holdRedemptionLabel;
    protected String holdReturnLabel;
    protected String inquiryLabel;
    protected String loyaltyIssuanceLabel;
    protected String loyaltyRedemptionLabel;
    protected String multipleIssuanceLabel;
    protected String promoIssuanceLabel;
    protected String promoRedemptionLabel;
    protected String renewalLabel;
    protected String returnLabel;
    protected String terminalReportLabel;
    protected String tipLabel;
    protected String transferLabel;
    protected String voidLabel;
    protected Boolean ttAccountHistory;
    protected Boolean ttAdjustment;
    protected Boolean ttEnrollment;
    protected Boolean ttGiftIssuance;
    protected Boolean ttGiftRedemption;
    protected Boolean ttInquiry;
    protected Boolean ttLoyaltyIssuance;
    protected Boolean ttLoyaltyRedemption;
    protected Boolean ttMultipleIssuance;
    protected Boolean ttPromoIssuance;
    protected Boolean ttPromoRedemption;
    protected Boolean ttRenewal;
    protected Boolean ttReturn;
    protected Boolean ttTip;
    protected Boolean ttTransfer;
    protected Boolean ttVoid;
    protected Boolean ttQuickTransaction;
    protected Boolean ttEmployeeReport;
    protected Boolean ttTerminalReport;
    protected Boolean peiAccountHistory;
    protected Boolean peiAdjustment;
    protected Boolean peiEnrollment;
    protected Boolean peiGiftIssuance;
    protected Boolean peiGiftRedemption;
    protected Boolean peiInquiry;
    protected Boolean peiLoyaltyIssuance;
    protected Boolean peiLoyaltyRedemption;
    protected Boolean peiMultipleIssuance;
    protected Boolean peiPromoIssuance;
    protected Boolean peiPromoRedemption;
    protected Boolean peiRenewal;
    protected Boolean peiReturn;
    protected Boolean peiTip;
    protected Boolean peiTransfer;
    protected Boolean peiVoid;
    protected Boolean peiQuickTransaction;
    protected Boolean peiEmployeeReport;
    protected Boolean peiTerminalReport;
    protected Boolean pepAccountHistory;
    protected Boolean pepAdjustment;
    protected Boolean pepEnrollment;
    protected Boolean pepGiftIssuance;
    protected Boolean pepGiftRedemption;
    protected Boolean pepInquiry;
    protected Boolean pepLoyaltyIssuance;
    protected Boolean pepLoyaltyRedemption;
    protected Boolean pepMultipleIssuance;
    protected Boolean pepPromoIssuance;
    protected Boolean pepPromoRedemption;
    protected Boolean pepRenewal;
    protected Boolean pepReturn;
    protected Boolean pepTip;
    protected Boolean pepTransfer;
    protected Boolean pepVoid;
    protected Boolean pepQuickTransaction;
    protected Boolean pepEmployeeReport;
    protected Boolean pepTerminalReport;
    protected Boolean pcpAccountHistory;
    protected Boolean pcpAdjustment;
    protected Boolean pcpEnrollment;
    protected Boolean pcpGiftIssuance;
    protected Boolean pcpGiftRedemption;
    protected Boolean pcpInquiry;
    protected Boolean pcpLoyaltyIssuance;
    protected Boolean pcpLoyaltyRedemption;
    protected Boolean pcpMultipleIssuance;
    protected Boolean pcpPromoIssuance;
    protected Boolean pcpPromoRedemption;
    protected Boolean pcpRenewal;
    protected Boolean pcpReturn;
    protected Boolean pcpTip;
    protected Boolean pcpTransfer;
    protected Boolean pcpVoid;
    protected Boolean paAccountHistory;
    protected Boolean paAdjustment;
    protected Boolean paEnrollment;
    protected Boolean paGiftIssuance;
    protected Boolean paGiftRedemption;
    protected Boolean paInquiry;
    protected Boolean paLoyaltyIssuance;
    protected Boolean paLoyaltyRedemption;
    protected Boolean paMultipleIssuance;
    protected Boolean paPromoIssuance;
    protected Boolean paPromoRedemption;
    protected Boolean paRenewal;
    protected Boolean paReturn;
    protected Boolean paTip;
    protected Boolean paTransfer;
    protected Boolean paVoid;
    protected Boolean pmpAccountHistory;
    protected Boolean pmpAdjustment;
    protected Boolean pmpEnrollment;
    protected Boolean pmpGiftIssuance;
    protected Boolean pmpGiftRedemption;
    protected Boolean pmpInquiry;
    protected Boolean pmpLoyaltyIssuance;
    protected Boolean pmpLoyaltyRedemption;
    protected Boolean pmpMultipleIssuance;
    protected Boolean pmpPromoIssuance;
    protected Boolean pmpPromoRedemption;
    protected Boolean pmpRenewal;
    protected Boolean pmpReturn;
    protected Boolean pmpTip;
    protected Boolean pmpTransfer;
    protected Boolean pmpVoid;
    protected Boolean pmpQuickTransaction;
    protected Boolean pmpEmployeeReport;
    protected Boolean pmpTerminalReport;
    protected String epType;
    protected String epFirst;
    protected String epMiddle;
    protected String epLast;
    protected String epAddr;
    protected String epCity;
    protected String epState;
    protected String epPostal;
    protected String epCountry;
    protected String epMailPref;
    protected String epPhone;
    protected String epIsMobile;
    protected String epPhonePref;
    protected String epEmail;
    protected String epEmailPref;
    protected String epBirthday;
    protected String epAnniversary;
    protected String epGender;
    protected String nepType;
    protected String nepFirst;
    protected String nepMiddle;
    protected String nepLast;
    protected String nepAddr;
    protected String nepCity;
    protected String nepState;
    protected String nepPostal;
    protected String nepCountry;
    protected String nepMailPref;
    protected String nepPhone;
    protected String nepIsMobile;
    protected String nepPhonePref;
    protected String nepEmail;
    protected String nepEmailPref;
    protected String nepBirthday;
    protected String nepAnniversary;
    protected String nepGender;
    protected String promptPromoCode;

    /**
     * Gets the value of the storedValSettingId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStoredValSettingId() {
        return storedValSettingId;
    }

    /**
     * Sets the value of the storedValSettingId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStoredValSettingId(String value) {
        this.storedValSettingId = value;
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
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the locationId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocationId() {
        return locationId;
    }

    /**
     * Sets the value of the locationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocationId(String value) {
        this.locationId = value;
    }

    /**
     * Gets the value of the cardCharsVisible property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardCharsVisible() {
        return cardCharsVisible;
    }

    /**
     * Sets the value of the cardCharsVisible property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardCharsVisible(String value) {
        this.cardCharsVisible = value;
    }

    /**
     * Gets the value of the ipCheckRequired property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIpCheckRequired() {
        return ipCheckRequired;
    }

    /**
     * Sets the value of the ipCheckRequired property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIpCheckRequired(Boolean value) {
        this.ipCheckRequired = value;
    }

    /**
     * Gets the value of the managerPassword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getManagerPassword() {
        return managerPassword;
    }

    /**
     * Sets the value of the managerPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setManagerPassword(String value) {
        this.managerPassword = value;
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
     * Gets the value of the supportInfo1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSupportInfo1() {
        return supportInfo1;
    }

    /**
     * Sets the value of the supportInfo1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSupportInfo1(String value) {
        this.supportInfo1 = value;
    }

    /**
     * Gets the value of the supportInfo2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSupportInfo2() {
        return supportInfo2;
    }

    /**
     * Sets the value of the supportInfo2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSupportInfo2(String value) {
        this.supportInfo2 = value;
    }

    /**
     * Gets the value of the ipEnabled property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIpEnabled() {
        return ipEnabled;
    }

    /**
     * Sets the value of the ipEnabled property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIpEnabled(Boolean value) {
        this.ipEnabled = value;
    }

    /**
     * Gets the value of the dialEnabled property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDialEnabled() {
        return dialEnabled;
    }

    /**
     * Sets the value of the dialEnabled property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDialEnabled(Boolean value) {
        this.dialEnabled = value;
    }

    /**
     * Gets the value of the secondReceiptPrompt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSecondReceiptPrompt() {
        return secondReceiptPrompt;
    }

    /**
     * Sets the value of the secondReceiptPrompt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSecondReceiptPrompt(String value) {
        this.secondReceiptPrompt = value;
    }

    /**
     * Gets the value of the encodingStyle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEncodingStyle() {
        return encodingStyle;
    }

    /**
     * Sets the value of the encodingStyle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEncodingStyle(String value) {
        this.encodingStyle = value;
    }

    /**
     * Gets the value of the loyaltyDecimalPrompt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLoyaltyDecimalPrompt() {
        return loyaltyDecimalPrompt;
    }

    /**
     * Sets the value of the loyaltyDecimalPrompt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLoyaltyDecimalPrompt(String value) {
        this.loyaltyDecimalPrompt = value;
    }

    /**
     * Gets the value of the receiptHeader1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReceiptHeader1() {
        return receiptHeader1;
    }

    /**
     * Sets the value of the receiptHeader1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReceiptHeader1(String value) {
        this.receiptHeader1 = value;
    }

    /**
     * Gets the value of the receiptHeader2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReceiptHeader2() {
        return receiptHeader2;
    }

    /**
     * Sets the value of the receiptHeader2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReceiptHeader2(String value) {
        this.receiptHeader2 = value;
    }

    /**
     * Gets the value of the receiptHeader3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReceiptHeader3() {
        return receiptHeader3;
    }

    /**
     * Sets the value of the receiptHeader3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReceiptHeader3(String value) {
        this.receiptHeader3 = value;
    }

    /**
     * Gets the value of the receiptHeader4 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReceiptHeader4() {
        return receiptHeader4;
    }

    /**
     * Sets the value of the receiptHeader4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReceiptHeader4(String value) {
        this.receiptHeader4 = value;
    }

    /**
     * Gets the value of the receiptFooter1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReceiptFooter1() {
        return receiptFooter1;
    }

    /**
     * Sets the value of the receiptFooter1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReceiptFooter1(String value) {
        this.receiptFooter1 = value;
    }

    /**
     * Gets the value of the receiptFooter2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReceiptFooter2() {
        return receiptFooter2;
    }

    /**
     * Sets the value of the receiptFooter2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReceiptFooter2(String value) {
        this.receiptFooter2 = value;
    }

    /**
     * Gets the value of the receiptFooter3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReceiptFooter3() {
        return receiptFooter3;
    }

    /**
     * Sets the value of the receiptFooter3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReceiptFooter3(String value) {
        this.receiptFooter3 = value;
    }

    /**
     * Gets the value of the receiptFooter4 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReceiptFooter4() {
        return receiptFooter4;
    }

    /**
     * Sets the value of the receiptFooter4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReceiptFooter4(String value) {
        this.receiptFooter4 = value;
    }

    /**
     * Gets the value of the accountHistoryLabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountHistoryLabel() {
        return accountHistoryLabel;
    }

    /**
     * Sets the value of the accountHistoryLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccountHistoryLabel(String value) {
        this.accountHistoryLabel = value;
    }

    /**
     * Gets the value of the employeeReportLabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmployeeReportLabel() {
        return employeeReportLabel;
    }

    /**
     * Sets the value of the employeeReportLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmployeeReportLabel(String value) {
        this.employeeReportLabel = value;
    }

    /**
     * Gets the value of the enrollmentLabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnrollmentLabel() {
        return enrollmentLabel;
    }

    /**
     * Sets the value of the enrollmentLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnrollmentLabel(String value) {
        this.enrollmentLabel = value;
    }

    /**
     * Gets the value of the giftIssuanceLabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGiftIssuanceLabel() {
        return giftIssuanceLabel;
    }

    /**
     * Sets the value of the giftIssuanceLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGiftIssuanceLabel(String value) {
        this.giftIssuanceLabel = value;
    }

    /**
     * Gets the value of the giftRedemptionLabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGiftRedemptionLabel() {
        return giftRedemptionLabel;
    }

    /**
     * Sets the value of the giftRedemptionLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGiftRedemptionLabel(String value) {
        this.giftRedemptionLabel = value;
    }

    /**
     * Gets the value of the holdLabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHoldLabel() {
        return holdLabel;
    }

    /**
     * Sets the value of the holdLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHoldLabel(String value) {
        this.holdLabel = value;
    }

    /**
     * Gets the value of the holdRedemptionLabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHoldRedemptionLabel() {
        return holdRedemptionLabel;
    }

    /**
     * Sets the value of the holdRedemptionLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHoldRedemptionLabel(String value) {
        this.holdRedemptionLabel = value;
    }

    /**
     * Gets the value of the holdReturnLabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHoldReturnLabel() {
        return holdReturnLabel;
    }

    /**
     * Sets the value of the holdReturnLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHoldReturnLabel(String value) {
        this.holdReturnLabel = value;
    }

    /**
     * Gets the value of the inquiryLabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInquiryLabel() {
        return inquiryLabel;
    }

    /**
     * Sets the value of the inquiryLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInquiryLabel(String value) {
        this.inquiryLabel = value;
    }

    /**
     * Gets the value of the loyaltyIssuanceLabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLoyaltyIssuanceLabel() {
        return loyaltyIssuanceLabel;
    }

    /**
     * Sets the value of the loyaltyIssuanceLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLoyaltyIssuanceLabel(String value) {
        this.loyaltyIssuanceLabel = value;
    }

    /**
     * Gets the value of the loyaltyRedemptionLabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLoyaltyRedemptionLabel() {
        return loyaltyRedemptionLabel;
    }

    /**
     * Sets the value of the loyaltyRedemptionLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLoyaltyRedemptionLabel(String value) {
        this.loyaltyRedemptionLabel = value;
    }

    /**
     * Gets the value of the multipleIssuanceLabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMultipleIssuanceLabel() {
        return multipleIssuanceLabel;
    }

    /**
     * Sets the value of the multipleIssuanceLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMultipleIssuanceLabel(String value) {
        this.multipleIssuanceLabel = value;
    }

    /**
     * Gets the value of the promoIssuanceLabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPromoIssuanceLabel() {
        return promoIssuanceLabel;
    }

    /**
     * Sets the value of the promoIssuanceLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPromoIssuanceLabel(String value) {
        this.promoIssuanceLabel = value;
    }

    /**
     * Gets the value of the promoRedemptionLabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPromoRedemptionLabel() {
        return promoRedemptionLabel;
    }

    /**
     * Sets the value of the promoRedemptionLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPromoRedemptionLabel(String value) {
        this.promoRedemptionLabel = value;
    }

    /**
     * Gets the value of the renewalLabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRenewalLabel() {
        return renewalLabel;
    }

    /**
     * Sets the value of the renewalLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRenewalLabel(String value) {
        this.renewalLabel = value;
    }

    /**
     * Gets the value of the returnLabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReturnLabel() {
        return returnLabel;
    }

    /**
     * Sets the value of the returnLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReturnLabel(String value) {
        this.returnLabel = value;
    }

    /**
     * Gets the value of the terminalReportLabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTerminalReportLabel() {
        return terminalReportLabel;
    }

    /**
     * Sets the value of the terminalReportLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTerminalReportLabel(String value) {
        this.terminalReportLabel = value;
    }

    /**
     * Gets the value of the tipLabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipLabel() {
        return tipLabel;
    }

    /**
     * Sets the value of the tipLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipLabel(String value) {
        this.tipLabel = value;
    }

    /**
     * Gets the value of the transferLabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransferLabel() {
        return transferLabel;
    }

    /**
     * Sets the value of the transferLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransferLabel(String value) {
        this.transferLabel = value;
    }

    /**
     * Gets the value of the voidLabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVoidLabel() {
        return voidLabel;
    }

    /**
     * Sets the value of the voidLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVoidLabel(String value) {
        this.voidLabel = value;
    }

    /**
     * Gets the value of the ttAccountHistory property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTtAccountHistory() {
        return ttAccountHistory;
    }

    /**
     * Sets the value of the ttAccountHistory property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTtAccountHistory(Boolean value) {
        this.ttAccountHistory = value;
    }

    /**
     * Gets the value of the ttAdjustment property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTtAdjustment() {
        return ttAdjustment;
    }

    /**
     * Sets the value of the ttAdjustment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTtAdjustment(Boolean value) {
        this.ttAdjustment = value;
    }

    /**
     * Gets the value of the ttEnrollment property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTtEnrollment() {
        return ttEnrollment;
    }

    /**
     * Sets the value of the ttEnrollment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTtEnrollment(Boolean value) {
        this.ttEnrollment = value;
    }

    /**
     * Gets the value of the ttGiftIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTtGiftIssuance() {
        return ttGiftIssuance;
    }

    /**
     * Sets the value of the ttGiftIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTtGiftIssuance(Boolean value) {
        this.ttGiftIssuance = value;
    }

    /**
     * Gets the value of the ttGiftRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTtGiftRedemption() {
        return ttGiftRedemption;
    }

    /**
     * Sets the value of the ttGiftRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTtGiftRedemption(Boolean value) {
        this.ttGiftRedemption = value;
    }

    /**
     * Gets the value of the ttInquiry property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTtInquiry() {
        return ttInquiry;
    }

    /**
     * Sets the value of the ttInquiry property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTtInquiry(Boolean value) {
        this.ttInquiry = value;
    }

    /**
     * Gets the value of the ttLoyaltyIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTtLoyaltyIssuance() {
        return ttLoyaltyIssuance;
    }

    /**
     * Sets the value of the ttLoyaltyIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTtLoyaltyIssuance(Boolean value) {
        this.ttLoyaltyIssuance = value;
    }

    /**
     * Gets the value of the ttLoyaltyRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTtLoyaltyRedemption() {
        return ttLoyaltyRedemption;
    }

    /**
     * Sets the value of the ttLoyaltyRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTtLoyaltyRedemption(Boolean value) {
        this.ttLoyaltyRedemption = value;
    }

    /**
     * Gets the value of the ttMultipleIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTtMultipleIssuance() {
        return ttMultipleIssuance;
    }

    /**
     * Sets the value of the ttMultipleIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTtMultipleIssuance(Boolean value) {
        this.ttMultipleIssuance = value;
    }

    /**
     * Gets the value of the ttPromoIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTtPromoIssuance() {
        return ttPromoIssuance;
    }

    /**
     * Sets the value of the ttPromoIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTtPromoIssuance(Boolean value) {
        this.ttPromoIssuance = value;
    }

    /**
     * Gets the value of the ttPromoRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTtPromoRedemption() {
        return ttPromoRedemption;
    }

    /**
     * Sets the value of the ttPromoRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTtPromoRedemption(Boolean value) {
        this.ttPromoRedemption = value;
    }

    /**
     * Gets the value of the ttRenewal property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTtRenewal() {
        return ttRenewal;
    }

    /**
     * Sets the value of the ttRenewal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTtRenewal(Boolean value) {
        this.ttRenewal = value;
    }

    /**
     * Gets the value of the ttReturn property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTtReturn() {
        return ttReturn;
    }

    /**
     * Sets the value of the ttReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTtReturn(Boolean value) {
        this.ttReturn = value;
    }

    /**
     * Gets the value of the ttTip property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTtTip() {
        return ttTip;
    }

    /**
     * Sets the value of the ttTip property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTtTip(Boolean value) {
        this.ttTip = value;
    }

    /**
     * Gets the value of the ttTransfer property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTtTransfer() {
        return ttTransfer;
    }

    /**
     * Sets the value of the ttTransfer property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTtTransfer(Boolean value) {
        this.ttTransfer = value;
    }

    /**
     * Gets the value of the ttVoid property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTtVoid() {
        return ttVoid;
    }

    /**
     * Sets the value of the ttVoid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTtVoid(Boolean value) {
        this.ttVoid = value;
    }

    /**
     * Gets the value of the ttQuickTransaction property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTtQuickTransaction() {
        return ttQuickTransaction;
    }

    /**
     * Sets the value of the ttQuickTransaction property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTtQuickTransaction(Boolean value) {
        this.ttQuickTransaction = value;
    }

    /**
     * Gets the value of the ttEmployeeReport property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTtEmployeeReport() {
        return ttEmployeeReport;
    }

    /**
     * Sets the value of the ttEmployeeReport property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTtEmployeeReport(Boolean value) {
        this.ttEmployeeReport = value;
    }

    /**
     * Gets the value of the ttTerminalReport property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTtTerminalReport() {
        return ttTerminalReport;
    }

    /**
     * Sets the value of the ttTerminalReport property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTtTerminalReport(Boolean value) {
        this.ttTerminalReport = value;
    }

    /**
     * Gets the value of the peiAccountHistory property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPeiAccountHistory() {
        return peiAccountHistory;
    }

    /**
     * Sets the value of the peiAccountHistory property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPeiAccountHistory(Boolean value) {
        this.peiAccountHistory = value;
    }

    /**
     * Gets the value of the peiAdjustment property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPeiAdjustment() {
        return peiAdjustment;
    }

    /**
     * Sets the value of the peiAdjustment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPeiAdjustment(Boolean value) {
        this.peiAdjustment = value;
    }

    /**
     * Gets the value of the peiEnrollment property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPeiEnrollment() {
        return peiEnrollment;
    }

    /**
     * Sets the value of the peiEnrollment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPeiEnrollment(Boolean value) {
        this.peiEnrollment = value;
    }

    /**
     * Gets the value of the peiGiftIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPeiGiftIssuance() {
        return peiGiftIssuance;
    }

    /**
     * Sets the value of the peiGiftIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPeiGiftIssuance(Boolean value) {
        this.peiGiftIssuance = value;
    }

    /**
     * Gets the value of the peiGiftRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPeiGiftRedemption() {
        return peiGiftRedemption;
    }

    /**
     * Sets the value of the peiGiftRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPeiGiftRedemption(Boolean value) {
        this.peiGiftRedemption = value;
    }

    /**
     * Gets the value of the peiInquiry property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPeiInquiry() {
        return peiInquiry;
    }

    /**
     * Sets the value of the peiInquiry property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPeiInquiry(Boolean value) {
        this.peiInquiry = value;
    }

    /**
     * Gets the value of the peiLoyaltyIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPeiLoyaltyIssuance() {
        return peiLoyaltyIssuance;
    }

    /**
     * Sets the value of the peiLoyaltyIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPeiLoyaltyIssuance(Boolean value) {
        this.peiLoyaltyIssuance = value;
    }

    /**
     * Gets the value of the peiLoyaltyRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPeiLoyaltyRedemption() {
        return peiLoyaltyRedemption;
    }

    /**
     * Sets the value of the peiLoyaltyRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPeiLoyaltyRedemption(Boolean value) {
        this.peiLoyaltyRedemption = value;
    }

    /**
     * Gets the value of the peiMultipleIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPeiMultipleIssuance() {
        return peiMultipleIssuance;
    }

    /**
     * Sets the value of the peiMultipleIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPeiMultipleIssuance(Boolean value) {
        this.peiMultipleIssuance = value;
    }

    /**
     * Gets the value of the peiPromoIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPeiPromoIssuance() {
        return peiPromoIssuance;
    }

    /**
     * Sets the value of the peiPromoIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPeiPromoIssuance(Boolean value) {
        this.peiPromoIssuance = value;
    }

    /**
     * Gets the value of the peiPromoRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPeiPromoRedemption() {
        return peiPromoRedemption;
    }

    /**
     * Sets the value of the peiPromoRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPeiPromoRedemption(Boolean value) {
        this.peiPromoRedemption = value;
    }

    /**
     * Gets the value of the peiRenewal property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPeiRenewal() {
        return peiRenewal;
    }

    /**
     * Sets the value of the peiRenewal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPeiRenewal(Boolean value) {
        this.peiRenewal = value;
    }

    /**
     * Gets the value of the peiReturn property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPeiReturn() {
        return peiReturn;
    }

    /**
     * Sets the value of the peiReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPeiReturn(Boolean value) {
        this.peiReturn = value;
    }

    /**
     * Gets the value of the peiTip property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPeiTip() {
        return peiTip;
    }

    /**
     * Sets the value of the peiTip property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPeiTip(Boolean value) {
        this.peiTip = value;
    }

    /**
     * Gets the value of the peiTransfer property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPeiTransfer() {
        return peiTransfer;
    }

    /**
     * Sets the value of the peiTransfer property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPeiTransfer(Boolean value) {
        this.peiTransfer = value;
    }

    /**
     * Gets the value of the peiVoid property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPeiVoid() {
        return peiVoid;
    }

    /**
     * Sets the value of the peiVoid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPeiVoid(Boolean value) {
        this.peiVoid = value;
    }

    /**
     * Gets the value of the peiQuickTransaction property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPeiQuickTransaction() {
        return peiQuickTransaction;
    }

    /**
     * Sets the value of the peiQuickTransaction property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPeiQuickTransaction(Boolean value) {
        this.peiQuickTransaction = value;
    }

    /**
     * Gets the value of the peiEmployeeReport property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPeiEmployeeReport() {
        return peiEmployeeReport;
    }

    /**
     * Sets the value of the peiEmployeeReport property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPeiEmployeeReport(Boolean value) {
        this.peiEmployeeReport = value;
    }

    /**
     * Gets the value of the peiTerminalReport property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPeiTerminalReport() {
        return peiTerminalReport;
    }

    /**
     * Sets the value of the peiTerminalReport property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPeiTerminalReport(Boolean value) {
        this.peiTerminalReport = value;
    }

    /**
     * Gets the value of the pepAccountHistory property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPepAccountHistory() {
        return pepAccountHistory;
    }

    /**
     * Sets the value of the pepAccountHistory property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPepAccountHistory(Boolean value) {
        this.pepAccountHistory = value;
    }

    /**
     * Gets the value of the pepAdjustment property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPepAdjustment() {
        return pepAdjustment;
    }

    /**
     * Sets the value of the pepAdjustment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPepAdjustment(Boolean value) {
        this.pepAdjustment = value;
    }

    /**
     * Gets the value of the pepEnrollment property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPepEnrollment() {
        return pepEnrollment;
    }

    /**
     * Sets the value of the pepEnrollment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPepEnrollment(Boolean value) {
        this.pepEnrollment = value;
    }

    /**
     * Gets the value of the pepGiftIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPepGiftIssuance() {
        return pepGiftIssuance;
    }

    /**
     * Sets the value of the pepGiftIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPepGiftIssuance(Boolean value) {
        this.pepGiftIssuance = value;
    }

    /**
     * Gets the value of the pepGiftRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPepGiftRedemption() {
        return pepGiftRedemption;
    }

    /**
     * Sets the value of the pepGiftRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPepGiftRedemption(Boolean value) {
        this.pepGiftRedemption = value;
    }

    /**
     * Gets the value of the pepInquiry property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPepInquiry() {
        return pepInquiry;
    }

    /**
     * Sets the value of the pepInquiry property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPepInquiry(Boolean value) {
        this.pepInquiry = value;
    }

    /**
     * Gets the value of the pepLoyaltyIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPepLoyaltyIssuance() {
        return pepLoyaltyIssuance;
    }

    /**
     * Sets the value of the pepLoyaltyIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPepLoyaltyIssuance(Boolean value) {
        this.pepLoyaltyIssuance = value;
    }

    /**
     * Gets the value of the pepLoyaltyRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPepLoyaltyRedemption() {
        return pepLoyaltyRedemption;
    }

    /**
     * Sets the value of the pepLoyaltyRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPepLoyaltyRedemption(Boolean value) {
        this.pepLoyaltyRedemption = value;
    }

    /**
     * Gets the value of the pepMultipleIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPepMultipleIssuance() {
        return pepMultipleIssuance;
    }

    /**
     * Sets the value of the pepMultipleIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPepMultipleIssuance(Boolean value) {
        this.pepMultipleIssuance = value;
    }

    /**
     * Gets the value of the pepPromoIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPepPromoIssuance() {
        return pepPromoIssuance;
    }

    /**
     * Sets the value of the pepPromoIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPepPromoIssuance(Boolean value) {
        this.pepPromoIssuance = value;
    }

    /**
     * Gets the value of the pepPromoRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPepPromoRedemption() {
        return pepPromoRedemption;
    }

    /**
     * Sets the value of the pepPromoRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPepPromoRedemption(Boolean value) {
        this.pepPromoRedemption = value;
    }

    /**
     * Gets the value of the pepRenewal property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPepRenewal() {
        return pepRenewal;
    }

    /**
     * Sets the value of the pepRenewal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPepRenewal(Boolean value) {
        this.pepRenewal = value;
    }

    /**
     * Gets the value of the pepReturn property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPepReturn() {
        return pepReturn;
    }

    /**
     * Sets the value of the pepReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPepReturn(Boolean value) {
        this.pepReturn = value;
    }

    /**
     * Gets the value of the pepTip property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPepTip() {
        return pepTip;
    }

    /**
     * Sets the value of the pepTip property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPepTip(Boolean value) {
        this.pepTip = value;
    }

    /**
     * Gets the value of the pepTransfer property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPepTransfer() {
        return pepTransfer;
    }

    /**
     * Sets the value of the pepTransfer property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPepTransfer(Boolean value) {
        this.pepTransfer = value;
    }

    /**
     * Gets the value of the pepVoid property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPepVoid() {
        return pepVoid;
    }

    /**
     * Sets the value of the pepVoid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPepVoid(Boolean value) {
        this.pepVoid = value;
    }

    /**
     * Gets the value of the pepQuickTransaction property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPepQuickTransaction() {
        return pepQuickTransaction;
    }

    /**
     * Sets the value of the pepQuickTransaction property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPepQuickTransaction(Boolean value) {
        this.pepQuickTransaction = value;
    }

    /**
     * Gets the value of the pepEmployeeReport property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPepEmployeeReport() {
        return pepEmployeeReport;
    }

    /**
     * Sets the value of the pepEmployeeReport property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPepEmployeeReport(Boolean value) {
        this.pepEmployeeReport = value;
    }

    /**
     * Gets the value of the pepTerminalReport property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPepTerminalReport() {
        return pepTerminalReport;
    }

    /**
     * Sets the value of the pepTerminalReport property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPepTerminalReport(Boolean value) {
        this.pepTerminalReport = value;
    }

    /**
     * Gets the value of the pcpAccountHistory property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPcpAccountHistory() {
        return pcpAccountHistory;
    }

    /**
     * Sets the value of the pcpAccountHistory property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPcpAccountHistory(Boolean value) {
        this.pcpAccountHistory = value;
    }

    /**
     * Gets the value of the pcpAdjustment property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPcpAdjustment() {
        return pcpAdjustment;
    }

    /**
     * Sets the value of the pcpAdjustment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPcpAdjustment(Boolean value) {
        this.pcpAdjustment = value;
    }

    /**
     * Gets the value of the pcpEnrollment property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPcpEnrollment() {
        return pcpEnrollment;
    }

    /**
     * Sets the value of the pcpEnrollment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPcpEnrollment(Boolean value) {
        this.pcpEnrollment = value;
    }

    /**
     * Gets the value of the pcpGiftIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPcpGiftIssuance() {
        return pcpGiftIssuance;
    }

    /**
     * Sets the value of the pcpGiftIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPcpGiftIssuance(Boolean value) {
        this.pcpGiftIssuance = value;
    }

    /**
     * Gets the value of the pcpGiftRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPcpGiftRedemption() {
        return pcpGiftRedemption;
    }

    /**
     * Sets the value of the pcpGiftRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPcpGiftRedemption(Boolean value) {
        this.pcpGiftRedemption = value;
    }

    /**
     * Gets the value of the pcpInquiry property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPcpInquiry() {
        return pcpInquiry;
    }

    /**
     * Sets the value of the pcpInquiry property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPcpInquiry(Boolean value) {
        this.pcpInquiry = value;
    }

    /**
     * Gets the value of the pcpLoyaltyIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPcpLoyaltyIssuance() {
        return pcpLoyaltyIssuance;
    }

    /**
     * Sets the value of the pcpLoyaltyIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPcpLoyaltyIssuance(Boolean value) {
        this.pcpLoyaltyIssuance = value;
    }

    /**
     * Gets the value of the pcpLoyaltyRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPcpLoyaltyRedemption() {
        return pcpLoyaltyRedemption;
    }

    /**
     * Sets the value of the pcpLoyaltyRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPcpLoyaltyRedemption(Boolean value) {
        this.pcpLoyaltyRedemption = value;
    }

    /**
     * Gets the value of the pcpMultipleIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPcpMultipleIssuance() {
        return pcpMultipleIssuance;
    }

    /**
     * Sets the value of the pcpMultipleIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPcpMultipleIssuance(Boolean value) {
        this.pcpMultipleIssuance = value;
    }

    /**
     * Gets the value of the pcpPromoIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPcpPromoIssuance() {
        return pcpPromoIssuance;
    }

    /**
     * Sets the value of the pcpPromoIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPcpPromoIssuance(Boolean value) {
        this.pcpPromoIssuance = value;
    }

    /**
     * Gets the value of the pcpPromoRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPcpPromoRedemption() {
        return pcpPromoRedemption;
    }

    /**
     * Sets the value of the pcpPromoRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPcpPromoRedemption(Boolean value) {
        this.pcpPromoRedemption = value;
    }

    /**
     * Gets the value of the pcpRenewal property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPcpRenewal() {
        return pcpRenewal;
    }

    /**
     * Sets the value of the pcpRenewal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPcpRenewal(Boolean value) {
        this.pcpRenewal = value;
    }

    /**
     * Gets the value of the pcpReturn property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPcpReturn() {
        return pcpReturn;
    }

    /**
     * Sets the value of the pcpReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPcpReturn(Boolean value) {
        this.pcpReturn = value;
    }

    /**
     * Gets the value of the pcpTip property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPcpTip() {
        return pcpTip;
    }

    /**
     * Sets the value of the pcpTip property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPcpTip(Boolean value) {
        this.pcpTip = value;
    }

    /**
     * Gets the value of the pcpTransfer property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPcpTransfer() {
        return pcpTransfer;
    }

    /**
     * Sets the value of the pcpTransfer property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPcpTransfer(Boolean value) {
        this.pcpTransfer = value;
    }

    /**
     * Gets the value of the pcpVoid property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPcpVoid() {
        return pcpVoid;
    }

    /**
     * Sets the value of the pcpVoid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPcpVoid(Boolean value) {
        this.pcpVoid = value;
    }

    /**
     * Gets the value of the paAccountHistory property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPaAccountHistory() {
        return paAccountHistory;
    }

    /**
     * Sets the value of the paAccountHistory property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPaAccountHistory(Boolean value) {
        this.paAccountHistory = value;
    }

    /**
     * Gets the value of the paAdjustment property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPaAdjustment() {
        return paAdjustment;
    }

    /**
     * Sets the value of the paAdjustment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPaAdjustment(Boolean value) {
        this.paAdjustment = value;
    }

    /**
     * Gets the value of the paEnrollment property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPaEnrollment() {
        return paEnrollment;
    }

    /**
     * Sets the value of the paEnrollment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPaEnrollment(Boolean value) {
        this.paEnrollment = value;
    }

    /**
     * Gets the value of the paGiftIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPaGiftIssuance() {
        return paGiftIssuance;
    }

    /**
     * Sets the value of the paGiftIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPaGiftIssuance(Boolean value) {
        this.paGiftIssuance = value;
    }

    /**
     * Gets the value of the paGiftRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPaGiftRedemption() {
        return paGiftRedemption;
    }

    /**
     * Sets the value of the paGiftRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPaGiftRedemption(Boolean value) {
        this.paGiftRedemption = value;
    }

    /**
     * Gets the value of the paInquiry property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPaInquiry() {
        return paInquiry;
    }

    /**
     * Sets the value of the paInquiry property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPaInquiry(Boolean value) {
        this.paInquiry = value;
    }

    /**
     * Gets the value of the paLoyaltyIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPaLoyaltyIssuance() {
        return paLoyaltyIssuance;
    }

    /**
     * Sets the value of the paLoyaltyIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPaLoyaltyIssuance(Boolean value) {
        this.paLoyaltyIssuance = value;
    }

    /**
     * Gets the value of the paLoyaltyRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPaLoyaltyRedemption() {
        return paLoyaltyRedemption;
    }

    /**
     * Sets the value of the paLoyaltyRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPaLoyaltyRedemption(Boolean value) {
        this.paLoyaltyRedemption = value;
    }

    /**
     * Gets the value of the paMultipleIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPaMultipleIssuance() {
        return paMultipleIssuance;
    }

    /**
     * Sets the value of the paMultipleIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPaMultipleIssuance(Boolean value) {
        this.paMultipleIssuance = value;
    }

    /**
     * Gets the value of the paPromoIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPaPromoIssuance() {
        return paPromoIssuance;
    }

    /**
     * Sets the value of the paPromoIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPaPromoIssuance(Boolean value) {
        this.paPromoIssuance = value;
    }

    /**
     * Gets the value of the paPromoRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPaPromoRedemption() {
        return paPromoRedemption;
    }

    /**
     * Sets the value of the paPromoRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPaPromoRedemption(Boolean value) {
        this.paPromoRedemption = value;
    }

    /**
     * Gets the value of the paRenewal property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPaRenewal() {
        return paRenewal;
    }

    /**
     * Sets the value of the paRenewal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPaRenewal(Boolean value) {
        this.paRenewal = value;
    }

    /**
     * Gets the value of the paReturn property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPaReturn() {
        return paReturn;
    }

    /**
     * Sets the value of the paReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPaReturn(Boolean value) {
        this.paReturn = value;
    }

    /**
     * Gets the value of the paTip property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPaTip() {
        return paTip;
    }

    /**
     * Sets the value of the paTip property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPaTip(Boolean value) {
        this.paTip = value;
    }

    /**
     * Gets the value of the paTransfer property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPaTransfer() {
        return paTransfer;
    }

    /**
     * Sets the value of the paTransfer property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPaTransfer(Boolean value) {
        this.paTransfer = value;
    }

    /**
     * Gets the value of the paVoid property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPaVoid() {
        return paVoid;
    }

    /**
     * Sets the value of the paVoid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPaVoid(Boolean value) {
        this.paVoid = value;
    }

    /**
     * Gets the value of the pmpAccountHistory property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPmpAccountHistory() {
        return pmpAccountHistory;
    }

    /**
     * Sets the value of the pmpAccountHistory property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPmpAccountHistory(Boolean value) {
        this.pmpAccountHistory = value;
    }

    /**
     * Gets the value of the pmpAdjustment property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPmpAdjustment() {
        return pmpAdjustment;
    }

    /**
     * Sets the value of the pmpAdjustment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPmpAdjustment(Boolean value) {
        this.pmpAdjustment = value;
    }

    /**
     * Gets the value of the pmpEnrollment property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPmpEnrollment() {
        return pmpEnrollment;
    }

    /**
     * Sets the value of the pmpEnrollment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPmpEnrollment(Boolean value) {
        this.pmpEnrollment = value;
    }

    /**
     * Gets the value of the pmpGiftIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPmpGiftIssuance() {
        return pmpGiftIssuance;
    }

    /**
     * Sets the value of the pmpGiftIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPmpGiftIssuance(Boolean value) {
        this.pmpGiftIssuance = value;
    }

    /**
     * Gets the value of the pmpGiftRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPmpGiftRedemption() {
        return pmpGiftRedemption;
    }

    /**
     * Sets the value of the pmpGiftRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPmpGiftRedemption(Boolean value) {
        this.pmpGiftRedemption = value;
    }

    /**
     * Gets the value of the pmpInquiry property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPmpInquiry() {
        return pmpInquiry;
    }

    /**
     * Sets the value of the pmpInquiry property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPmpInquiry(Boolean value) {
        this.pmpInquiry = value;
    }

    /**
     * Gets the value of the pmpLoyaltyIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPmpLoyaltyIssuance() {
        return pmpLoyaltyIssuance;
    }

    /**
     * Sets the value of the pmpLoyaltyIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPmpLoyaltyIssuance(Boolean value) {
        this.pmpLoyaltyIssuance = value;
    }

    /**
     * Gets the value of the pmpLoyaltyRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPmpLoyaltyRedemption() {
        return pmpLoyaltyRedemption;
    }

    /**
     * Sets the value of the pmpLoyaltyRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPmpLoyaltyRedemption(Boolean value) {
        this.pmpLoyaltyRedemption = value;
    }

    /**
     * Gets the value of the pmpMultipleIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPmpMultipleIssuance() {
        return pmpMultipleIssuance;
    }

    /**
     * Sets the value of the pmpMultipleIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPmpMultipleIssuance(Boolean value) {
        this.pmpMultipleIssuance = value;
    }

    /**
     * Gets the value of the pmpPromoIssuance property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPmpPromoIssuance() {
        return pmpPromoIssuance;
    }

    /**
     * Sets the value of the pmpPromoIssuance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPmpPromoIssuance(Boolean value) {
        this.pmpPromoIssuance = value;
    }

    /**
     * Gets the value of the pmpPromoRedemption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPmpPromoRedemption() {
        return pmpPromoRedemption;
    }

    /**
     * Sets the value of the pmpPromoRedemption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPmpPromoRedemption(Boolean value) {
        this.pmpPromoRedemption = value;
    }

    /**
     * Gets the value of the pmpRenewal property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPmpRenewal() {
        return pmpRenewal;
    }

    /**
     * Sets the value of the pmpRenewal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPmpRenewal(Boolean value) {
        this.pmpRenewal = value;
    }

    /**
     * Gets the value of the pmpReturn property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPmpReturn() {
        return pmpReturn;
    }

    /**
     * Sets the value of the pmpReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPmpReturn(Boolean value) {
        this.pmpReturn = value;
    }

    /**
     * Gets the value of the pmpTip property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPmpTip() {
        return pmpTip;
    }

    /**
     * Sets the value of the pmpTip property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPmpTip(Boolean value) {
        this.pmpTip = value;
    }

    /**
     * Gets the value of the pmpTransfer property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPmpTransfer() {
        return pmpTransfer;
    }

    /**
     * Sets the value of the pmpTransfer property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPmpTransfer(Boolean value) {
        this.pmpTransfer = value;
    }

    /**
     * Gets the value of the pmpVoid property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPmpVoid() {
        return pmpVoid;
    }

    /**
     * Sets the value of the pmpVoid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPmpVoid(Boolean value) {
        this.pmpVoid = value;
    }

    /**
     * Gets the value of the pmpQuickTransaction property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPmpQuickTransaction() {
        return pmpQuickTransaction;
    }

    /**
     * Sets the value of the pmpQuickTransaction property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPmpQuickTransaction(Boolean value) {
        this.pmpQuickTransaction = value;
    }

    /**
     * Gets the value of the pmpEmployeeReport property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPmpEmployeeReport() {
        return pmpEmployeeReport;
    }

    /**
     * Sets the value of the pmpEmployeeReport property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPmpEmployeeReport(Boolean value) {
        this.pmpEmployeeReport = value;
    }

    /**
     * Gets the value of the pmpTerminalReport property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPmpTerminalReport() {
        return pmpTerminalReport;
    }

    /**
     * Sets the value of the pmpTerminalReport property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPmpTerminalReport(Boolean value) {
        this.pmpTerminalReport = value;
    }

    /**
     * Gets the value of the epType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEpType() {
        return epType;
    }

    /**
     * Sets the value of the epType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEpType(String value) {
        this.epType = value;
    }

    /**
     * Gets the value of the epFirst property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEpFirst() {
        return epFirst;
    }

    /**
     * Sets the value of the epFirst property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEpFirst(String value) {
        this.epFirst = value;
    }

    /**
     * Gets the value of the epMiddle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEpMiddle() {
        return epMiddle;
    }

    /**
     * Sets the value of the epMiddle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEpMiddle(String value) {
        this.epMiddle = value;
    }

    /**
     * Gets the value of the epLast property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEpLast() {
        return epLast;
    }

    /**
     * Sets the value of the epLast property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEpLast(String value) {
        this.epLast = value;
    }

    /**
     * Gets the value of the epAddr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEpAddr() {
        return epAddr;
    }

    /**
     * Sets the value of the epAddr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEpAddr(String value) {
        this.epAddr = value;
    }

    /**
     * Gets the value of the epCity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEpCity() {
        return epCity;
    }

    /**
     * Sets the value of the epCity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEpCity(String value) {
        this.epCity = value;
    }

    /**
     * Gets the value of the epState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEpState() {
        return epState;
    }

    /**
     * Sets the value of the epState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEpState(String value) {
        this.epState = value;
    }

    /**
     * Gets the value of the epPostal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEpPostal() {
        return epPostal;
    }

    /**
     * Sets the value of the epPostal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEpPostal(String value) {
        this.epPostal = value;
    }

    /**
     * Gets the value of the epCountry property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEpCountry() {
        return epCountry;
    }

    /**
     * Sets the value of the epCountry property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEpCountry(String value) {
        this.epCountry = value;
    }

    /**
     * Gets the value of the epMailPref property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEpMailPref() {
        return epMailPref;
    }

    /**
     * Sets the value of the epMailPref property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEpMailPref(String value) {
        this.epMailPref = value;
    }

    /**
     * Gets the value of the epPhone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEpPhone() {
        return epPhone;
    }

    /**
     * Sets the value of the epPhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEpPhone(String value) {
        this.epPhone = value;
    }

    /**
     * Gets the value of the epIsMobile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEpIsMobile() {
        return epIsMobile;
    }

    /**
     * Sets the value of the epIsMobile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEpIsMobile(String value) {
        this.epIsMobile = value;
    }

    /**
     * Gets the value of the epPhonePref property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEpPhonePref() {
        return epPhonePref;
    }

    /**
     * Sets the value of the epPhonePref property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEpPhonePref(String value) {
        this.epPhonePref = value;
    }

    /**
     * Gets the value of the epEmail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEpEmail() {
        return epEmail;
    }

    /**
     * Sets the value of the epEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEpEmail(String value) {
        this.epEmail = value;
    }

    /**
     * Gets the value of the epEmailPref property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEpEmailPref() {
        return epEmailPref;
    }

    /**
     * Sets the value of the epEmailPref property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEpEmailPref(String value) {
        this.epEmailPref = value;
    }

    /**
     * Gets the value of the epBirthday property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEpBirthday() {
        return epBirthday;
    }

    /**
     * Sets the value of the epBirthday property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEpBirthday(String value) {
        this.epBirthday = value;
    }

    /**
     * Gets the value of the epAnniversary property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEpAnniversary() {
        return epAnniversary;
    }

    /**
     * Sets the value of the epAnniversary property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEpAnniversary(String value) {
        this.epAnniversary = value;
    }

    /**
     * Gets the value of the epGender property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEpGender() {
        return epGender;
    }

    /**
     * Sets the value of the epGender property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEpGender(String value) {
        this.epGender = value;
    }

    /**
     * Gets the value of the nepType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNepType() {
        return nepType;
    }

    /**
     * Sets the value of the nepType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNepType(String value) {
        this.nepType = value;
    }

    /**
     * Gets the value of the nepFirst property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNepFirst() {
        return nepFirst;
    }

    /**
     * Sets the value of the nepFirst property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNepFirst(String value) {
        this.nepFirst = value;
    }

    /**
     * Gets the value of the nepMiddle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNepMiddle() {
        return nepMiddle;
    }

    /**
     * Sets the value of the nepMiddle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNepMiddle(String value) {
        this.nepMiddle = value;
    }

    /**
     * Gets the value of the nepLast property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNepLast() {
        return nepLast;
    }

    /**
     * Sets the value of the nepLast property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNepLast(String value) {
        this.nepLast = value;
    }

    /**
     * Gets the value of the nepAddr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNepAddr() {
        return nepAddr;
    }

    /**
     * Sets the value of the nepAddr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNepAddr(String value) {
        this.nepAddr = value;
    }

    /**
     * Gets the value of the nepCity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNepCity() {
        return nepCity;
    }

    /**
     * Sets the value of the nepCity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNepCity(String value) {
        this.nepCity = value;
    }

    /**
     * Gets the value of the nepState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNepState() {
        return nepState;
    }

    /**
     * Sets the value of the nepState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNepState(String value) {
        this.nepState = value;
    }

    /**
     * Gets the value of the nepPostal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNepPostal() {
        return nepPostal;
    }

    /**
     * Sets the value of the nepPostal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNepPostal(String value) {
        this.nepPostal = value;
    }

    /**
     * Gets the value of the nepCountry property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNepCountry() {
        return nepCountry;
    }

    /**
     * Sets the value of the nepCountry property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNepCountry(String value) {
        this.nepCountry = value;
    }

    /**
     * Gets the value of the nepMailPref property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNepMailPref() {
        return nepMailPref;
    }

    /**
     * Sets the value of the nepMailPref property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNepMailPref(String value) {
        this.nepMailPref = value;
    }

    /**
     * Gets the value of the nepPhone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNepPhone() {
        return nepPhone;
    }

    /**
     * Sets the value of the nepPhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNepPhone(String value) {
        this.nepPhone = value;
    }

    /**
     * Gets the value of the nepIsMobile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNepIsMobile() {
        return nepIsMobile;
    }

    /**
     * Sets the value of the nepIsMobile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNepIsMobile(String value) {
        this.nepIsMobile = value;
    }

    /**
     * Gets the value of the nepPhonePref property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNepPhonePref() {
        return nepPhonePref;
    }

    /**
     * Sets the value of the nepPhonePref property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNepPhonePref(String value) {
        this.nepPhonePref = value;
    }

    /**
     * Gets the value of the nepEmail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNepEmail() {
        return nepEmail;
    }

    /**
     * Sets the value of the nepEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNepEmail(String value) {
        this.nepEmail = value;
    }

    /**
     * Gets the value of the nepEmailPref property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNepEmailPref() {
        return nepEmailPref;
    }

    /**
     * Sets the value of the nepEmailPref property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNepEmailPref(String value) {
        this.nepEmailPref = value;
    }

    /**
     * Gets the value of the nepBirthday property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNepBirthday() {
        return nepBirthday;
    }

    /**
     * Sets the value of the nepBirthday property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNepBirthday(String value) {
        this.nepBirthday = value;
    }

    /**
     * Gets the value of the nepAnniversary property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNepAnniversary() {
        return nepAnniversary;
    }

    /**
     * Sets the value of the nepAnniversary property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNepAnniversary(String value) {
        this.nepAnniversary = value;
    }

    /**
     * Gets the value of the nepGender property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNepGender() {
        return nepGender;
    }

    /**
     * Sets the value of the nepGender property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNepGender(String value) {
        this.nepGender = value;
    }

    /**
     * Gets the value of the promptPromoCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPromptPromoCode() {
        return promptPromoCode;
    }

    /**
     * Sets the value of the promptPromoCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPromptPromoCode(String value) {
        this.promptPromoCode = value;
    }

}

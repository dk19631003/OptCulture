package org.mq.optculture.controller.loyalty;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.AutoSMS;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.LoyaltyAutoComm;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.loyalty.LoyaltyProgramService;
import org.mq.optculture.utils.OCConstants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Div;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Image;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

public class LoyaltyAutoCommunicationController extends GenericForwardComposer {
	private Listbox selRegAutoEmailsLbId,selTierAutoEmailsLbId,selBonusAutoEmailsLbId,selRewardAutoEmailsLbId,selMemExpiryAutoEmailsLbId,
	selGftAmtAutoEmailsLbId,selGftCrdAutoEmailsLbId,gftIssAutoEmailsLbId,selRegAutoSmsLbId,gftIssAutoSmsLbId,selTierAutoSmsLbId,selBonusAutoSmsLbId,
	selRewardAutoSmsLbId,selGftAmtAutoSmsLbId,selMemExpiryAutoSmsLbId,selGftCrdAutoSmsLbId,selAdjustmentEmailLbId,selAdjustmentAutoSmsLbId,
	selIssuanceEmailLbId,selIssuanceAutoSmsLbId,selRedemptionEmailLbId,selRedemptionAutoSmsLbId, selOtpmessageAutoSmsLbId,selOtpmessageEmailLbId,selRedemptionOtpEmailLbId,selRedemptionOtpAutoSmsLbId;
	private Window previewWin;
	private Iframe previewWin$html;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Users user;
	private Image previewRegEmailImgId,editRegEmailImgId,addRegEmailImgId,previewTierEmailImgId,editTierEmailImgId,addTierEmailImgId,previewThresholdEmailImgId,editThresholdEmailImgId,addThresholdEmailImgId,
	previewRewExpEmailImgId, editRewExpEmailImgId,addRewExpEmailImgId,previewMemEmailImgId,editMemEmailImgId,addMemEmailImgId,
	previewGftAmtExpEmailImgId,editGftAmtExpEmailImgId,addGftAmtExpEmailImgId,previewGftCrdExpEmailImgId,editGftCrdExpEmailImgId,addGftCrdExpEmailImgId,
	previewGftIssEmailImgId,editGftIssEmailImgId,addGftIssEmailImgId,previewRegSmsImgId,editRegSmsImgId,addRegSmsImgId,previewGftIssSmsImgId,addGftIssSmsImgId,editGftIssSmsImgId,
	previewTierSmsImgId,addTierSmsImgId,editTierSmsImgId,previewThresholdSmsImgId,editThresholdSmsImgId,addThresholdSmsImgId,editRewExpSmsImgId,addRewExpSmsImgId,previewRewExpSmsImgId,
	previewMemSmsImgId,editMemSmsImgId,addMemSmsImgId,previewGftCrdExpSmsImgId,editGftCrdExpSmsImgId,addGftCrdExpSmsImgId,editGftAmtExpSmsImgId,addGftAmtExpSmsImgId,previewGftAmtExpSmsImgId;
	private A regEmailPreviewBtnId,regEmailEditMsgBtnId,regEmailAddBtnId,tierEmailPreviewBtnId,tierEmailEditMsgBtnId,tierEmailAddBtnId,bonusEmailPreviewBtnId,bonusEmailEditMsgBtnId
	,bonusEmailAddBtnId,rewardEmailPreviewBtnId,rewardEmailEditMsgBtnId,rewardEmailAddMsgBtnId,memEmailPreviewBtnId,memEmailEditMsgBtnId,
	memEmailAddMsgBtnId,gftAmtEmailPreviewBtnId,gftAmtEmailEditMsgBtnId,gftAmtEmailAddMsgBtnId,gftCrdEmailPreviewBtnId,gftCrdEmailEditMsgBtnId,gftCrdEmailAddMsgBtnId
	,gftIssEmailEditMsgBtnId,gftIssEmailPreviewBtnId,gftIssEmailAddBtnId,regSmsPreviewBtnId,regSmsEditMsgBtnId,regSmsAddBtnId,
	gftIssSmsPreviewBtnId,gftIssSmsEditMsgBtnId,gftIssSmsAddBtnId,tierSmsEditMsgBtnId,tierSmsAddBtnId,tierSmsPreviewBtnId,bonusSmsEditMsgBtnId,
	bonusSmsAddBtnId,bonusSmsPreviewBtnId,rewardSmsPreviewBtnId,rewardSmsEditMsgBtnId,rewardSmsAddMsgBtnId,memSmsPreviewBtnId,memSmsEditMsgBtnId,memSmsAddMsgBtnId,
	gftCrdSmsPreviewBtnId,gftCrdSmsAddMsgBtnId,gftCrdSmsEditMsgBtnId,gftAmtSmsPreviewBtnId,gftAmtSmsEditMsgBtnId,gftAmtSmsAddMsgBtnId;
	
	private Div giftIssDivId, giftAmtDivId, giftExpDivId;
	private  MyTemplatesDao myTemplatesDao;
	
	private Image previewAdjustmentSmsImgId,previewAdjustmentEmailImgId,editAdjustmentExpSmsImgId,editAdjustmentEmailImgId,addAdjustmentSmsImgId,addAdjustmentEmailImgId;
	private A adjustmentSmsPreviewBtnId,adjustmentSmsEditMsgBtnId,adjustmentSmsAddMsgBtnId,adjustmentPreviewBtnId,AdjustmentEmailEditMsgBtnId,adjustmentEmailAddMsgBtnId;
	
	private Image previewIssuanceSmsImgId,previewIssuanceEmailImgId,editIssuanceSmsImgId,editIssuanceEmailImgId,addIssuanceSmsImgId,addIssuanceEmailImgId;
	private A issuanceSmsPreviewBtnId,issuanceSmsEditMsgBtnId,issuanceSmsAddMsgBtnId,issuancePreviewBtnId,issuanceEmailEditMsgBtnId,issuanceEmailAddMsgBtnId;
	
	private Image previewRedemptionSmsImgId,previewRedemptionEmailImgId,editRedemptionSmsImgId,editRedemptionEmailImgId,addRedemptionSmsImgId,addRedemptionEmailImgId;
	private A redemptionSmsPreviewBtnId,redemptionSmsEditMsgBtnId,redemptionSmsAddMsgBtnId,redemptionPreviewBtnId,redemptionEmailEditMsgBtnId,redemptionEmailAddMsgBtnId;
	
	private Image previewOtpmessageSmsImgId,editOtpmessageSmsImgId,addOtpmessageSmsImgId,previewOtpmessageEmailImgId,editOtpmessageEmailImgId,addOtpmessageEmailImgId;
	private A OtpmessagePreviewBtnId,OtpmessageEmailEditMsgBtnId,OtpmessageEmailAddMsgBtnId,OtpmessageSmsPreviewBtnId,OtpmessageSmsEditMsgBtnId,OtpmessageSmsAddMsgBtnId;
		private String privateDomains;
	
	private Image previewRedemptionOtpSmsImgId,editRedemptionOtpSmsImgId,addRedemptionOtpSmsImgId;
	private A RedemptionOtpSmsPreviewBtnId,RedemptionOtpSmsEditMsgBtnId,RedemptionOtpSmsAddMsgBtnId;
		
	private Image previewRedemptionOtpEmailImgId,editRedemptionOtpEmailImgId,addRedemptionOtpEmailImgId;
	private A   RedemptionOtpPreviewBtnId,RedemptionOtpEmailEditMsgBtnId,RedemptionOtpEmailAddMsgBtnId;    
	
	
	
	
	public LoyaltyAutoCommunicationController() {

		user = GetUser.getUserObj();
		session = Sessions.getCurrent();
		Utility.ltyBreadCrumbFrom(5, OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype()));
		myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Create Loyalty Program (Step 5 of 6)","",style,true);
		Long prgmId=(Long) session.getAttribute("programId");

		//get templates for auto emails
		getLoyaltyTemplateList(selRegAutoEmailsLbId,"loyaltyOptin");
		getLoyaltyTemplateList(selTierAutoEmailsLbId,"tierUpgradation");
		getLoyaltyTemplateList(selBonusAutoEmailsLbId,"earnedBonus");
		getLoyaltyTemplateList(selRewardAutoEmailsLbId,"earnedRewardExpiration");
		getLoyaltyTemplateList(selMemExpiryAutoEmailsLbId,"membershipExpiration");
		getLoyaltyTemplateList(selGftAmtAutoEmailsLbId,"giftAmountExpiration");
		getLoyaltyTemplateList(selGftCrdAutoEmailsLbId,"giftCardExpiration");
		getLoyaltyTemplateList(gftIssAutoEmailsLbId,"giftCardIssuance");
		getLoyaltyTemplateList(selAdjustmentEmailLbId,"loyaltyAdjustment");
		getLoyaltyTemplateList(selIssuanceEmailLbId,"loyaltyIssuance");
		getLoyaltyTemplateList(selRedemptionEmailLbId,"loyaltyRedemption");
		//otp
		getLoyaltyTemplateList(selOtpmessageEmailLbId,"OTPMESSAGE");
		
		getLoyaltyTemplateList(selRedemptionOtpEmailLbId,"redemptionOtp");
		
		//get templates for auto sms 
		getLoyaltySmsTemplateList(selRegAutoSmsLbId,"loyaltyRegistration");
		getLoyaltySmsTemplateList(gftIssAutoSmsLbId,"giftCardIssuance");
		getLoyaltySmsTemplateList(selTierAutoSmsLbId,"tierUpgradation");
		getLoyaltySmsTemplateList(selBonusAutoSmsLbId,"earningBonus");
		getLoyaltySmsTemplateList(selRewardAutoSmsLbId,"earnedRewardExpiration");
		getLoyaltySmsTemplateList(selGftAmtAutoSmsLbId,"giftAmountExpiration");
		getLoyaltySmsTemplateList(selMemExpiryAutoSmsLbId,"membershipExpiration");
		getLoyaltySmsTemplateList(selGftCrdAutoSmsLbId,"giftCardExpiration");
		getLoyaltySmsTemplateList(selAdjustmentAutoSmsLbId, "loyaltyAdjustment");
		getLoyaltySmsTemplateList(selIssuanceAutoSmsLbId, "loyaltyIssuance");
		getLoyaltySmsTemplateList(selRedemptionAutoSmsLbId, "loyaltyRedemption");
		//otp
		getLoyaltySmsTemplateList(selOtpmessageAutoSmsLbId, "OTPMESSAGE");
		
		getLoyaltySmsTemplateList(selRedemptionOtpAutoSmsLbId, "redemptionOtp");

		//edit mode
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		LoyaltyAutoComm loyaltyAutoComm=ltyPrgmSevice.getAutoCommunicationObj(prgmId);
		if(loyaltyAutoComm != null) {
			defaultSettings(loyaltyAutoComm);
			onSelect$selRegAutoEmailsLbId();
			onSelect$gftIssAutoEmailsLbId();
			onSelect$selTierAutoEmailsLbId();
			onSelect$selBonusAutoEmailsLbId();
			onSelect$selRewardAutoEmailsLbId();
			onSelect$selMemExpiryAutoEmailsLbId();
			onSelect$selGftAmtAutoEmailsLbId();
			onSelect$selGftCrdAutoEmailsLbId();
			onSelect$selAdjustmentEmailLbId();
			onSelect$selIssuanceEmailLbId();
			onSelect$selRedemptionEmailLbId();
			onSelect$selOtpmessageEmailLbId();
			onSelect$selRedemptionOtpEmailLbId();
			
			
			onSelect$selRegAutoSmsLbId();;
			onSelect$gftIssAutoSmsLbId();
			onSelect$selTierAutoSmsLbId();
			onSelect$selBonusAutoSmsLbId();
			onSelect$selRewardAutoSmsLbId();
			onSelect$selMemExpiryAutoSmsLbId();
			onSelect$selGftAmtAutoSmsLbId();
			onSelect$selGftCrdAutoSmsLbId();
			onSelect$selAdjustmentAutoSmsLbId();
			onSelect$selIssuanceAutoSmsLbId();
			onSelect$selRedemptionAutoSmsLbId();
			onSelect$selOtpmessageAutoSmsLbId();
			onSelect$selRedemptionOtpAutoSmsLbId();
			
		}
		
		LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
		if(OCConstants.LOYALTY_SERVICE_TYPE_OC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype()) && OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE.equalsIgnoreCase(prgmObj.getMembershipType())){
			giftIssDivId.setVisible(false);
			giftAmtDivId.setVisible(false);
			giftExpDivId.setVisible(false);
		}
		else if(OCConstants.LOYALTY_SERVICE_TYPE_OC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype()) && OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD.equalsIgnoreCase(prgmObj.getMembershipType()) ){
			giftIssDivId.setVisible(true);
			giftAmtDivId.setVisible(true);
			giftExpDivId.setVisible(true);
		}
	}//doAfterCompose()

	private void defaultSettings(LoyaltyAutoComm loyaltyAutoComm) {

		//auto-emails
		setRegistrationValues(loyaltyAutoComm);
		setGiftIssuanceValues(loyaltyAutoComm);
		setTierUpgrdValues(loyaltyAutoComm);
		setBonusValues(loyaltyAutoComm);
		setRewardExpValues(loyaltyAutoComm);
		setMembershipExpValues(loyaltyAutoComm);
		setGiftAmountExpValues(loyaltyAutoComm);
		setGiftCardExpValues(loyaltyAutoComm);
		setAdjustmentAutoEmailValues(loyaltyAutoComm);
		setIssuanceAutoEmailValues(loyaltyAutoComm);
		setRedemptionAutoEmailValues(loyaltyAutoComm);
		setOtpMessageAutoEmailValues(loyaltyAutoComm);
		setRedemptionOtpAutoEmailValues(loyaltyAutoComm);
		
		//auto-SMS
		setSMSRegistrationValues(loyaltyAutoComm);
		setSMSGiftIssuanceValues(loyaltyAutoComm);
		setSMSTierUpgrdValues(loyaltyAutoComm);
		setSMSBonusValues(loyaltyAutoComm);
		setSMSRewardExpValues(loyaltyAutoComm);
		setSMSMembershipExpValues(loyaltyAutoComm);
		setSMSGiftAmountExpValues(loyaltyAutoComm);
		setSMSGiftCardExpValues(loyaltyAutoComm);
		setAdjustmentAutoSmsValues(loyaltyAutoComm);
		setIssuanceAutoSmsValues(loyaltyAutoComm);
		setRedemptionAutoSmsValues(loyaltyAutoComm);
		setOtpMessageAutoSmsValues(loyaltyAutoComm);
	
		setRedemptionOtpAutoSmsValues(loyaltyAutoComm);
	}//defaultSettings()

	/**
	 * setting the gift-card expiry SMS template 
	 * @param loyaltyAutoComm
	 */
	private void setSMSGiftCardExpValues(LoyaltyAutoComm loyaltyAutoComm) {
		logger.debug("---Entered setSMSGiftCardExpValues---");
		List<Listitem> gftCrdExpSmsList = selGftCrdAutoSmsLbId.getItems();
		for(Listitem li : gftCrdExpSmsList ) {
			Long getGiftMembrshpExpirySmsTmpltId=li.getValue();
			if(getGiftMembrshpExpirySmsTmpltId != null) {
				if(getGiftMembrshpExpirySmsTmpltId.equals(loyaltyAutoComm.getGiftMembrshpExpirySmsTmpltId())) {
					selGftCrdAutoSmsLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}
		}
		logger.debug("---Exit setSMSGiftCardExpValues---");
	}//setSMSGiftCardExpValues()

	/**
	 * setting the gift amount SMS template 
	 * @param loyaltyAutoComm
	 */
	private void setSMSGiftAmountExpValues(LoyaltyAutoComm loyaltyAutoComm) {
		logger.debug("---Entered setSMSGiftAmountExpValues---");
		List<Listitem> gftAmtExpSmsList = selGftAmtAutoSmsLbId.getItems();
		for(Listitem li : gftAmtExpSmsList ) {
			Long getGiftAmtExpirySmsTmpltId=li.getValue();
			if(getGiftAmtExpirySmsTmpltId != null) {
				if(getGiftAmtExpirySmsTmpltId.equals(loyaltyAutoComm.getGiftAmtExpirySmsTmpltId())) {
					selGftAmtAutoSmsLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}
		}
		logger.debug("---Exit setSMSGiftAmountExpValues---");
	}//setSMSGiftAmountExpValues()

	/**
	 * setting the membership expiry SMS template 
	 * @param loyaltyAutoComm
	 */
	private void setSMSMembershipExpValues(LoyaltyAutoComm loyaltyAutoComm) {
		logger.debug("---Entered setSMSMembershipExpValues---");
		List<Listitem> memExpSmsList = selMemExpiryAutoSmsLbId.getItems();
		for(Listitem li : memExpSmsList ) {
			Long mbrshipExpirySmsTmpltId=li.getValue();
			if(mbrshipExpirySmsTmpltId != null) {
				if(mbrshipExpirySmsTmpltId.equals(loyaltyAutoComm.getMbrshipExpirySmsTmpltId())) {
					selMemExpiryAutoSmsLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}
		}
		logger.debug("---Exit setSMSMembershipExpValues---");
	}//setSMSMembershipExpValues()

	/**
	 * setting the reward expiry SMS template 
	 * @param loyaltyAutoComm
	 */
	private void setSMSRewardExpValues(LoyaltyAutoComm loyaltyAutoComm) {
		logger.debug("---Entered setSMSRewardExpValues---");
		List<Listitem> earnedRewardExpSmsList = selRewardAutoSmsLbId.getItems();
		for(Listitem li : earnedRewardExpSmsList ) {
			Long rewardExpirySmsTmpltId = li.getValue();
			if(rewardExpirySmsTmpltId != null) {
				if(rewardExpirySmsTmpltId.equals(loyaltyAutoComm.getRewardExpirySmsTmpltId())) {
					selRewardAutoSmsLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}
		}
		logger.debug("---Exit setSMSRewardExpValues---");
	}//setSMSRewardExpValues()

	/**
	 * setting the earning bonus SMS template 
	 * @param loyaltyAutoComm
	 */
	private void setSMSBonusValues(LoyaltyAutoComm loyaltyAutoComm) {
		logger.debug("---Entered setSMSBonusValues---");
		List<Listitem> thresholdBonusSmsList = selBonusAutoSmsLbId.getItems();

		for(Listitem li : thresholdBonusSmsList ) {
			Long threshBonusSmsTmpltId=li.getValue();
			if(threshBonusSmsTmpltId != null) {
				if(threshBonusSmsTmpltId.equals(loyaltyAutoComm.getThreshBonusSmsTmpltId())) {
					selBonusAutoSmsLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}
		}
		logger.debug("---Exit setSMSBonusValues---");
	}//setSMSBonusValues()

	/**
	 * setting the tier upgrade SMS template 
	 * @param loyaltyAutoComm
	 */
	private void setSMSTierUpgrdValues(LoyaltyAutoComm loyaltyAutoComm) {
		logger.debug("---Entered setSMSTierUpgrdValues---");
		List<Listitem> tierUpgradeSmsList = selTierAutoSmsLbId.getItems();
		for(Listitem li : tierUpgradeSmsList ) {
			Long tierUpgdSmsTmpltId=li.getValue();
			if(tierUpgdSmsTmpltId != null) {
				if(tierUpgdSmsTmpltId.equals(loyaltyAutoComm.getTierUpgdSmsTmpltId())) {
					selTierAutoSmsLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}
		}
		logger.debug("---Exit setSMSTierUpgrdValues---");
	}//setSMSTierUpgrdValues()

	/**
	 * setting the gift issuance expiry SMS template 
	 * @param loyaltyAutoComm
	 */
	private void setSMSGiftIssuanceValues(LoyaltyAutoComm loyaltyAutoComm) {
		logger.debug("---Entered setSMSGiftIssuanceValues---");
		List<Listitem> gftCrdIssuanceSmsList = gftIssAutoSmsLbId.getItems();
		for(Listitem li : gftCrdIssuanceSmsList ) {
			Long gifCardIssuanceSmsTmpltId=li.getValue();
			if(gifCardIssuanceSmsTmpltId != null) {
				if(gifCardIssuanceSmsTmpltId.equals(loyaltyAutoComm.getGiftCardIssuanceSmsTmpltId())) {
					gftIssAutoSmsLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}
		}
		logger.debug("---Exit setSMSGiftIssuanceValues---");
	}//setSMSGiftIssuanceValues()

	/**
	 * setting the loyalty enrollment expiry SMS template 
	 * @param loyaltyAutoComm
	 */
	private void setSMSRegistrationValues(LoyaltyAutoComm loyaltyAutoComm) {
		logger.debug("---Entered setSMSRegistrationValues---");
		List<Listitem> loyaltyRegSmsTempList = selRegAutoSmsLbId.getItems();
		for(Listitem li : loyaltyRegSmsTempList ) {
			Long regSmsTmpltId=li.getValue();
			if(regSmsTmpltId != null) {
				if(regSmsTmpltId.equals(loyaltyAutoComm.getRegSmsTmpltId())) {
					selRegAutoSmsLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}
		}
		logger.debug("---Exit setSMSRegistrationValues---");
	}//setSMSRegistrationValues()

	/**
	 * setting the gift-card expiry email template 
	 * @param loyaltyAutoComm
	 */
	private void setGiftCardExpValues(LoyaltyAutoComm loyaltyAutoComm) {
		logger.debug("---Entered setGiftCardExpValues---");
		List<Listitem> gftCrdExpiryList = selGftCrdAutoEmailsLbId.getItems();
		getDefaultValueForOldRecords(loyaltyAutoComm.getGiftMembrshpExpiryEmailTmpltId(),selGftCrdAutoEmailsLbId);
		for(Listitem li : gftCrdExpiryList ) {
			Long giftMembrshpExpiryEmailTmpltId=li.getValue();
			if(giftMembrshpExpiryEmailTmpltId != null) {
				if(giftMembrshpExpiryEmailTmpltId.equals(loyaltyAutoComm.getGiftMembrshpExpiryEmailTmpltId())) {
					selGftCrdAutoEmailsLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}
		}
		logger.debug("---Exit setGiftCardExpValues---");
	}//setGiftCardExpValues()


	/**
	 * setting the gift amount expiry email template 
	 * @param loyaltyAutoComm
	 */
	private void setGiftAmountExpValues(LoyaltyAutoComm loyaltyAutoComm) {
		logger.debug("---Entered setGiftAmountExpValues---");
		List<Listitem> gftAmtExpiryList = selGftAmtAutoEmailsLbId.getItems();
		getDefaultValueForOldRecords(loyaltyAutoComm.getGiftAmtExpiryEmailTmpltId(),selGftAmtAutoEmailsLbId);
		for(Listitem li :gftAmtExpiryList ) {
			Long giftAmtExpiryEmailTmpltId=li.getValue();
			if(giftAmtExpiryEmailTmpltId != null) {
				if(giftAmtExpiryEmailTmpltId.equals(loyaltyAutoComm.getGiftAmtExpiryEmailTmpltId())) {
					selGftAmtAutoEmailsLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}
		}
		logger.debug("---Exit setGiftAmountExpValues---");
	}//setGiftAmountExpValues()

	/**
	 * setting the membership expiry email template 
	 * @param loyaltyAutoComm
	 */
	private void setMembershipExpValues(LoyaltyAutoComm loyaltyAutoComm) {
		logger.debug("---Entered setMembershipExpValues---");
		List<Listitem> memExpiryList = selMemExpiryAutoEmailsLbId.getItems();
		getDefaultValueForOldRecords(loyaltyAutoComm.getMbrshipExpiryEmailTmpltId(),selMemExpiryAutoEmailsLbId);
		for(Listitem li :memExpiryList ) {
			Long mbrshipExpiryEmailTmpltId=li.getValue();
			if(mbrshipExpiryEmailTmpltId != null) {
				if(mbrshipExpiryEmailTmpltId.equals(loyaltyAutoComm.getMbrshipExpiryEmailTmpltId())) {
					selMemExpiryAutoEmailsLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}
		}
		logger.debug("---Exit setMembershipExpValues---");
	}//setMembershipExpValues()

	/**
	 * setting the reward expiry email template 
	 * @param loyaltyAutoComm
	 */
	private void setRewardExpValues(LoyaltyAutoComm loyaltyAutoComm) {
		logger.debug("---Entered setRewardExpValues---");
		List<Listitem> earnedRewardExpList = selRewardAutoEmailsLbId.getItems();
		getDefaultValueForOldRecords(loyaltyAutoComm.getRewardExpiryEmailTmpltId(),selRewardAutoEmailsLbId);
		for(Listitem li :earnedRewardExpList ) {
			Long rewardExpiryEmailTmpltId=li.getValue();
			if(rewardExpiryEmailTmpltId != null) {
				if(rewardExpiryEmailTmpltId.equals(loyaltyAutoComm.getRewardExpiryEmailTmpltId())) {
					selRewardAutoEmailsLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}			
		}
		logger.debug("---Exit setRewardExpValues---");
	}//setRewardExpValues()

	/**
	 * setting the earned bonus email template 
	 * @param loyaltyAutoComm
	 */
	private void setBonusValues(LoyaltyAutoComm loyaltyAutoComm) {
		logger.debug("---Entered setBonusValues---");
		List<Listitem> thresholdBonusList = selBonusAutoEmailsLbId.getItems();
		getDefaultValueForOldRecords(loyaltyAutoComm.getThreshBonusEmailTmpltId(),selBonusAutoEmailsLbId);
		for(Listitem li :thresholdBonusList ) {
			Long threshBonusEmailTmpltId=li.getValue();
			if(threshBonusEmailTmpltId != null) {
				if(threshBonusEmailTmpltId.equals(loyaltyAutoComm.getThreshBonusEmailTmpltId())) {
					selBonusAutoEmailsLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}
		}
		logger.debug("---Exit setBonusValues---");
	}//setBonusValues()

	/**
	 * setting the gift issuance email template 
	 * @param loyaltyAutoComm
	 */
	private void setGiftIssuanceValues(LoyaltyAutoComm loyaltyAutoComm) {
		logger.debug("---Entered setGiftIssuanceValues---");
		List<Listitem> gftCrdIssuanceList = gftIssAutoEmailsLbId.getItems();
		getDefaultValueForOldRecords(loyaltyAutoComm.getGiftCardIssuanceEmailTmpltId(),gftIssAutoEmailsLbId);
		for(Listitem li : gftCrdIssuanceList ) {
			Long gifCardIssuanceEmailTmpltId=li.getValue();
			if(gifCardIssuanceEmailTmpltId != null) {
				if(gifCardIssuanceEmailTmpltId.equals(loyaltyAutoComm.getGiftCardIssuanceEmailTmpltId())) {
					gftIssAutoEmailsLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}
		}
		logger.debug("---Exit setGiftIssuanceValues---");
	}//setGiftIssuanceValues()

	/**
	 * setting the tier upgrade email template 
	 * @param loyaltyAutoComm
	 */
	private void setTierUpgrdValues(LoyaltyAutoComm loyaltyAutoComm) {
		logger.debug("---Entered setTierUpgrdValues---");
		List<Listitem> tierUpgradeList = selTierAutoEmailsLbId.getItems();
		getDefaultValueForOldRecords(loyaltyAutoComm.getTierUpgdEmailTmpltId(),selTierAutoEmailsLbId);
		for(Listitem li :tierUpgradeList ) {
			Long tierUpgdEmailTmpltId=li.getValue();
			if(tierUpgdEmailTmpltId != null) {
				if(tierUpgdEmailTmpltId.equals(loyaltyAutoComm.getTierUpgdEmailTmpltId())) {
					selTierAutoEmailsLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}
		}
		logger.debug("---Exit setTierUpgrdValues---");
	}//setTierUpgrdValues()

	/**
	 * setting the loyalty enrollment email template 
	 * @param loyaltyAutoComm
	 */
	private void setRegistrationValues(LoyaltyAutoComm loyaltyAutoComm) {
		logger.debug("---Entered setRegistrationValues---");
		List<Listitem> regAutoMailList = selRegAutoEmailsLbId.getItems();
		getDefaultValueForOldRecords(loyaltyAutoComm.getRegEmailTmpltId(),selRegAutoEmailsLbId);
		for(Listitem li :regAutoMailList ) {
			Long regEmailTmpltId=li.getValue();
			logger.info("regEmailTmpltId-"+regEmailTmpltId);
			if(regEmailTmpltId != null) {
				if(regEmailTmpltId.equals(loyaltyAutoComm.getRegEmailTmpltId())) {
					selRegAutoEmailsLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}
		}
		logger.debug("---Exit setRegistrationValues---");
	}//setRegistrationValues()
	
	private void setAdjustmentAutoEmailValues(LoyaltyAutoComm loyaltyAutoComm){

		logger.debug("---Entered setAdjustmentAutoEmailValues---");
		List<Listitem> adjAutoMailList = selAdjustmentEmailLbId.getItems();
		getDefaultValueForAdjOldRecords(loyaltyAutoComm.getAdjustmentAutoEmailTmplId(),selAdjustmentEmailLbId);
		for(Listitem li :adjAutoMailList ) {
			Long adjEmailTmpltId=li.getValue();
			logger.info("adjEmailTmpltId-"+adjEmailTmpltId);
			if(adjEmailTmpltId != null) {
				if(adjEmailTmpltId.equals(loyaltyAutoComm.getAdjustmentAutoEmailTmplId())) {
					selAdjustmentEmailLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}
		}
		logger.debug("---Exit setAdjustmentAutoEmailValues---");
	
		
		
		
	}
	private void setAdjustmentAutoSmsValues(LoyaltyAutoComm loyaltyAutoComm){

		logger.debug("---Entered setAdjustmentAutoSmsValues---");
		List<Listitem> adjAutoSmsList = selAdjustmentAutoSmsLbId.getItems();
		for(Listitem li :adjAutoSmsList ) {
			Long adjSmsTmpltId=li.getValue();
			if(adjSmsTmpltId != null) {
				if(adjSmsTmpltId.equals(loyaltyAutoComm.getAdjustmentAutoSmsTmplId())) {
					selAdjustmentAutoSmsLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}
		}
		logger.debug("---Exit setAdjustmentAutoSmsValues---");
	}
	
	private void setIssuanceAutoEmailValues(LoyaltyAutoComm loyaltyAutoComm){

		logger.debug("---Entered setIssuanceAutoEmailValues---");
		List<Listitem> issuanceAutoMailList = selIssuanceEmailLbId.getItems();
		getDefaultValueForOldRecords(loyaltyAutoComm.getIssuanceAutoEmailTmplId(),selIssuanceEmailLbId);
		for(Listitem li :issuanceAutoMailList ) {
			Long issuanceEmailTmpltId=li.getValue();
			logger.info("issuanceEmailTmpltId-"+issuanceEmailTmpltId);
			if(issuanceEmailTmpltId != null) {
				if(issuanceEmailTmpltId.equals(loyaltyAutoComm.getIssuanceAutoEmailTmplId())) {
					selIssuanceEmailLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}
		}
		logger.debug("---Exit setIssuanceAutoEmailValues---");
	
		
		
		
	}
	
	private void setIssuanceAutoSmsValues(LoyaltyAutoComm loyaltyAutoComm){

		logger.debug("---Entered setIssuanceAutoSmsValues---");
		List<Listitem> issuanceAutoSmsList = selIssuanceAutoSmsLbId.getItems();
		for(Listitem li :issuanceAutoSmsList ) {
			Long issuanceSmsTmpltId=li.getValue();
			if(issuanceSmsTmpltId != null) {
				if(issuanceSmsTmpltId.equals(loyaltyAutoComm.getIssuanceAutoSmsTmplId())) {
					selIssuanceAutoSmsLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}
		}
		logger.debug("---Exit setAdjustmentAutoSmsValues---");
	}
	
	private void setRedemptionAutoEmailValues(LoyaltyAutoComm loyaltyAutoComm){

		logger.debug("---Entered setRedemptionAutoEmailValues---");
		List<Listitem> redemptionAutoMailList = selRedemptionEmailLbId.getItems();
		getDefaultValueForOldRecords(loyaltyAutoComm.getRedemptionAutoEmailTmplId(),selRedemptionEmailLbId);
		for(Listitem li :redemptionAutoMailList ) {
			Long redemptionEmailTmpltId=li.getValue();
			logger.info("redeptionEmailTmpltId-"+redemptionEmailTmpltId);
			if(redemptionEmailTmpltId != null) {
				if(redemptionEmailTmpltId.equals(loyaltyAutoComm.getRedemptionAutoEmailTmplId())) {
					selRedemptionEmailLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}
		}
		logger.debug("---Exit setRedemptionAutoEmailValues---");
	
		
		
		
	}
	
	private void setRedemptionAutoSmsValues(LoyaltyAutoComm loyaltyAutoComm){

		logger.debug("---Entered setRedemptionAutoSmsValues---");
		List<Listitem> redemptionAutoSmsList = selRedemptionAutoSmsLbId.getItems();
		for(Listitem li :redemptionAutoSmsList ) {
			Long redemptionSmsTmpltId=li.getValue();
			if(redemptionSmsTmpltId != null) {
				if(redemptionSmsTmpltId.equals(loyaltyAutoComm.getRedemptionAutoSmsTmplId())) {
					selRedemptionAutoSmsLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}
		}
		logger.debug("---Exit setRedemptionAutoSmsValues---");
	}
	
	//otp
	private void setOtpMessageAutoSmsValues(LoyaltyAutoComm loyaltyAutoComm){

		logger.debug("---Entered setOtpMessageAutoSmsValues---");
		List<Listitem> otpmessageAutoSmsList = selOtpmessageAutoSmsLbId.getItems();
		for(Listitem li : otpmessageAutoSmsList) {
			Long otpmessageSmsTmpltId=li.getValue();
			if(otpmessageSmsTmpltId != null) {
				if(otpmessageSmsTmpltId.equals(loyaltyAutoComm.getOtpMessageAutoSmsTmpltId())) {
					selOtpmessageAutoSmsLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}
		}
		logger.debug("---Exit setOtpMessageAutoSmsValues---");
	}
	
	
	private void setRedemptionOtpAutoSmsValues(LoyaltyAutoComm loyaltyAutoComm){

		logger.debug("---Entered setOtpMessageAutoSmsValues---");
		List<Listitem> RedemptionOtpAutoSmsList = selRedemptionOtpAutoSmsLbId.getItems();
		for(Listitem li : RedemptionOtpAutoSmsList) {
			Long RedemptionOtpSmsTmpltId=li.getValue();
			if(RedemptionOtpSmsTmpltId != null) {
				if(RedemptionOtpSmsTmpltId.equals(loyaltyAutoComm.getRedemptionOtpAutoSmsTmpltId())) {
					selRedemptionOtpAutoSmsLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}
		}
		logger.debug("---Exit setRedemptionOtpAutoSmsValues---");
	}
	
	
	
	
	
	
	private void setOtpMessageAutoEmailValues(LoyaltyAutoComm loyaltyAutoComm){

		logger.debug("---Entered setOtpMessageAutoEmailValues---");
		List<Listitem> OtpMessageAutoMailList = selOtpmessageEmailLbId.getItems();
		getDefaultValueForOldRecords(loyaltyAutoComm.getOtpMessageAutoEmailTmplId(),selOtpmessageEmailLbId);
		for(Listitem li :OtpMessageAutoMailList ) {
			Long otpmessageEmailTmpltId=li.getValue();
			logger.info("otpmessageEmailTmpltId-"+otpmessageEmailTmpltId);
			if(otpmessageEmailTmpltId != null) {
				if(otpmessageEmailTmpltId.equals(loyaltyAutoComm.getOtpMessageAutoEmailTmplId())) {
					selOtpmessageEmailLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}
		}
		logger.debug("---Exit setOtpMessageAutoEmailValues---");
	
		
		
		
	}
	
	
	
	
	private void setRedemptionOtpAutoEmailValues(LoyaltyAutoComm loyaltyAutoComm){

		logger.debug("---Entered setRedemptionOtpAutoEmailValues---");
		
		List<Listitem> RedemptionOtpAutoMailList = selRedemptionOtpEmailLbId.getItems();
		getDefaultValueForOldRecords(loyaltyAutoComm.getRedemptionOtpAutoEmailTmplId(),selRedemptionOtpEmailLbId);
		for(Listitem li :RedemptionOtpAutoMailList ) {
			Long redemptionotpEmailTmpltId=li.getValue();
			logger.info("redemptionotpEmailTmpltId-"+redemptionotpEmailTmpltId);
			if(redemptionotpEmailTmpltId != null) {
				if(redemptionotpEmailTmpltId.equals(loyaltyAutoComm.getRedemptionOtpAutoEmailTmplId())) {
					selRedemptionOtpEmailLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}
		}
		logger.debug("---Exit setRedemptionOtpAutoEmailValues---");
	
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void getDefaultValueForAdjOldRecords(Long tmplatetId,Listbox selAdjustmentEmailLbId){
		if(tmplatetId!= null && tmplatetId.equals(-1L)) {
			Listitem item = new Listitem("Default Message", new Long(-1));
			item.setParent(selAdjustmentEmailLbId);
		}
		
		
	}

	private void getDefaultValueForOldRecords(Long tmplatetId, Listbox selRegAutoEmailsLabelbId) {
		if(tmplatetId!= null && tmplatetId.equals(-1L)) {
			Listitem item = new Listitem("Default Message", new Long(-1));
			item.setParent(selRegAutoEmailsLabelbId);
		}
	}
	
	
	//onselect methods for adjustment auto emails and auto sms
	//previewAdjustmentSmsImgId, adjustmentSmsPreviewBtnId,editAdjustmentExpSmsImgId,adjustmentSmsEditMsgBtnId,addAdjustmentSmsImgId,adjustmentSmsAddMsgBtnId
	//previewAdjustmentEmailImgId,adjustmentPreviewBtnId,editAdjustmentEmailImgId,AdjustmentEmailEditMsgBtnId,addAdjustmentEmailImgId,adjustmentEmailAddMsgBtnId

	
	public void onSelect$selAdjustmentAutoSmsLbId(){


		if(selAdjustmentAutoSmsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto SMS")) {
			previewAdjustmentSmsImgId.setVisible(false);
			adjustmentSmsPreviewBtnId.setVisible(false);
			editAdjustmentExpSmsImgId.setVisible(false);
			adjustmentSmsEditMsgBtnId.setVisible(false);
			addAdjustmentSmsImgId.setVisible(true);
			adjustmentSmsAddMsgBtnId.setVisible(true);
		}else{
			previewAdjustmentSmsImgId.setVisible(true);
			adjustmentSmsPreviewBtnId.setVisible(true);
			editAdjustmentExpSmsImgId.setVisible(true);
			adjustmentSmsEditMsgBtnId.setVisible(true);
			addAdjustmentSmsImgId.setVisible(true);
			adjustmentSmsAddMsgBtnId.setVisible(true);
		}

	
		
	}
	
	public void onSelect$selAdjustmentEmailLbId(){

		Boolean flag = false;
		if(selAdjustmentEmailLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto Email")) {
			previewAdjustmentEmailImgId.setVisible(false);
			adjustmentPreviewBtnId.setVisible(false);
			editAdjustmentEmailImgId.setVisible(false);
			AdjustmentEmailEditMsgBtnId.setVisible(false);
			addAdjustmentEmailImgId.setVisible(true);
			adjustmentEmailAddMsgBtnId.setVisible(true);
			flag = true;
		}else{
			previewAdjustmentEmailImgId.setVisible(true);
			adjustmentPreviewBtnId.setVisible(true);
			editAdjustmentEmailImgId.setVisible(true);
			AdjustmentEmailEditMsgBtnId.setVisible(true);
			addAdjustmentEmailImgId.setVisible(true);
			adjustmentEmailAddMsgBtnId.setVisible(true);
		}
		if(selAdjustmentEmailLbId.getSelectedItem().getLabel().equalsIgnoreCase("Default Message")) {
			AdjustmentEmailEditMsgBtnId.setVisible(false);
			editAdjustmentEmailImgId.setVisible(false);
		}else if(!flag) {
			AdjustmentEmailEditMsgBtnId.setVisible(true);
			editAdjustmentEmailImgId.setVisible(true);
		}

	
		
	}
	
	public void onSelect$selIssuanceAutoSmsLbId(){


		if(selIssuanceAutoSmsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto SMS")) {
			previewIssuanceSmsImgId.setVisible(false);
			issuanceSmsPreviewBtnId.setVisible(false);
			editIssuanceSmsImgId.setVisible(false);
			issuanceSmsEditMsgBtnId.setVisible(false);
			addIssuanceSmsImgId.setVisible(true);
			issuanceSmsAddMsgBtnId.setVisible(true);
		}else{
			previewIssuanceSmsImgId.setVisible(true);
			issuanceSmsPreviewBtnId.setVisible(true);
			editIssuanceSmsImgId.setVisible(true);
			issuanceSmsEditMsgBtnId.setVisible(true);
			addIssuanceSmsImgId.setVisible(true);
			issuanceSmsAddMsgBtnId.setVisible(true);
		}

	
		
	}
	
	public void onSelect$selIssuanceEmailLbId(){

		Boolean flag = false;
		if(selIssuanceEmailLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto Email")) {
			previewIssuanceEmailImgId.setVisible(false);
			issuancePreviewBtnId.setVisible(false);
			editIssuanceEmailImgId.setVisible(false);
			issuanceEmailEditMsgBtnId.setVisible(false);
			addIssuanceEmailImgId.setVisible(true);
			issuanceEmailAddMsgBtnId.setVisible(true);
			flag = true;
		}else{
			previewIssuanceEmailImgId.setVisible(true);
			issuancePreviewBtnId.setVisible(true);
			editIssuanceEmailImgId.setVisible(true);
			issuanceEmailEditMsgBtnId.setVisible(true);
			addIssuanceEmailImgId.setVisible(true);
			issuanceEmailAddMsgBtnId.setVisible(true);
		}
		if(selAdjustmentEmailLbId.getSelectedItem().getLabel().equalsIgnoreCase("Default Message")) {
			issuanceEmailEditMsgBtnId.setVisible(false);
			editIssuanceEmailImgId.setVisible(false);
		}else if(!flag) {
			issuanceEmailEditMsgBtnId.setVisible(true);
			editIssuanceEmailImgId.setVisible(true);
		}

	
		
	}
	
	public void onSelect$selRedemptionAutoSmsLbId(){


		if(selRedemptionAutoSmsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto SMS")) {
			previewRedemptionSmsImgId.setVisible(false);
			redemptionSmsPreviewBtnId.setVisible(false);
			editRedemptionSmsImgId.setVisible(false);
			redemptionSmsEditMsgBtnId.setVisible(false);
			addRedemptionSmsImgId.setVisible(true);
			redemptionSmsAddMsgBtnId.setVisible(true);
		}else{
			previewRedemptionSmsImgId.setVisible(true);
			redemptionSmsPreviewBtnId.setVisible(true);
			editRedemptionSmsImgId.setVisible(true);
			redemptionSmsEditMsgBtnId.setVisible(true);
			addRedemptionSmsImgId.setVisible(true);
			redemptionSmsAddMsgBtnId.setVisible(true);
		}

	
		
	}
	
	//otp
	public void onSelect$selOtpmessageAutoSmsLbId(){


		if(selOtpmessageAutoSmsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto SMS")) {
			previewOtpmessageSmsImgId.setVisible(false);
			OtpmessageSmsPreviewBtnId.setVisible(false);
			editOtpmessageSmsImgId.setVisible(false);
			OtpmessageSmsEditMsgBtnId.setVisible(false);
			addOtpmessageSmsImgId.setVisible(true);
			OtpmessageSmsAddMsgBtnId.setVisible(true);
		}else{
			previewOtpmessageSmsImgId.setVisible(true);
			OtpmessageSmsPreviewBtnId.setVisible(true);
			editOtpmessageSmsImgId.setVisible(true);
			OtpmessageSmsEditMsgBtnId.setVisible(true);
			addOtpmessageSmsImgId.setVisible(true);
			OtpmessageSmsAddMsgBtnId.setVisible(true);
		}

	
		
	}
	
	
	
	public void onSelect$selRedemptionOtpAutoSmsLbId (){


		if(selRedemptionOtpAutoSmsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto SMS")) {
			
			previewRedemptionOtpSmsImgId.setVisible(false);
			RedemptionOtpSmsPreviewBtnId.setVisible(false);
			editRedemptionOtpSmsImgId.setVisible(false);
			RedemptionOtpSmsEditMsgBtnId.setVisible(false);
			addRedemptionOtpSmsImgId.setVisible(true);
			RedemptionOtpSmsAddMsgBtnId.setVisible(true);
		
			
		
		}else{
			
			previewRedemptionOtpSmsImgId.setVisible(true);
			RedemptionOtpSmsPreviewBtnId.setVisible(true);
			editRedemptionOtpSmsImgId.setVisible(true);
			RedemptionOtpSmsEditMsgBtnId.setVisible(true);
			addRedemptionOtpSmsImgId.setVisible(true);
			RedemptionOtpSmsAddMsgBtnId.setVisible(true);
		
		}

	
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void onSelect$selOtpmessageEmailLbId(){

		Boolean flag = false;
		if(selOtpmessageEmailLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto Email")) {
			previewOtpmessageEmailImgId.setVisible(false);
			OtpmessagePreviewBtnId.setVisible(false);
			editOtpmessageEmailImgId.setVisible(false);
			OtpmessageEmailEditMsgBtnId.setVisible(false);
			addOtpmessageEmailImgId.setVisible(true);
			OtpmessageEmailAddMsgBtnId.setVisible(true);
			flag = true;
		}else{
			previewOtpmessageEmailImgId.setVisible(true);
			OtpmessagePreviewBtnId.setVisible(true);
			editOtpmessageEmailImgId.setVisible(true);
			OtpmessageEmailEditMsgBtnId.setVisible(true);
			addOtpmessageEmailImgId.setVisible(true);
			OtpmessageEmailAddMsgBtnId.setVisible(true);
			
			
		}
		if(selOtpmessageEmailLbId.getSelectedItem().getLabel().equalsIgnoreCase("Default Message")) {
			OtpmessageEmailEditMsgBtnId.setVisible(false);
			editOtpmessageEmailImgId.setVisible(false);
		}else if(!flag) {
			OtpmessageEmailEditMsgBtnId.setVisible(true);
			editOtpmessageEmailImgId.setVisible(true);
		}

	
		
	}
	
	
	
	
	
	
	public void onSelect$selRedemptionOtpEmailLbId(){

		Boolean flag = false;
		if(selRedemptionOtpEmailLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto Email")) {
			
			previewRedemptionOtpEmailImgId.setVisible(false);
			RedemptionOtpPreviewBtnId.setVisible(false);
			editRedemptionOtpEmailImgId.setVisible(false);
			RedemptionOtpEmailEditMsgBtnId.setVisible(false);
			addRedemptionOtpEmailImgId.setVisible(true);
			RedemptionOtpEmailAddMsgBtnId.setVisible(true);
			flag = true;
		
		}else{
			previewRedemptionOtpEmailImgId.setVisible(true);
			RedemptionOtpPreviewBtnId.setVisible(true);
			editRedemptionOtpEmailImgId.setVisible(true);
			RedemptionOtpEmailEditMsgBtnId.setVisible(true);
			addRedemptionOtpEmailImgId.setVisible(true);
			RedemptionOtpEmailAddMsgBtnId.setVisible(true);
			
			
		}
		if(selRedemptionOtpEmailLbId.getSelectedItem().getLabel().equalsIgnoreCase("Default Message")) {
		
			RedemptionOtpEmailEditMsgBtnId.setVisible(false);
			editRedemptionOtpEmailImgId.setVisible(false);
		}else if(!flag) {
		
			RedemptionOtpEmailEditMsgBtnId.setVisible(true);
			editRedemptionOtpEmailImgId.setVisible(true);
		}

	
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void onSelect$selRedemptionEmailLbId(){

		Boolean flag = false;
		if(selRedemptionEmailLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto Email")) {
			previewRedemptionEmailImgId.setVisible(false);
			redemptionPreviewBtnId.setVisible(false);
			editRedemptionEmailImgId.setVisible(false);
			redemptionEmailEditMsgBtnId.setVisible(false);
			addRedemptionEmailImgId.setVisible(true);
			redemptionEmailAddMsgBtnId.setVisible(true);
			flag = true;
		}else{
			previewRedemptionEmailImgId.setVisible(true);
			redemptionPreviewBtnId.setVisible(true);
			editRedemptionEmailImgId.setVisible(true);
			redemptionEmailEditMsgBtnId.setVisible(true);
			addRedemptionEmailImgId.setVisible(true);
			redemptionEmailAddMsgBtnId.setVisible(true);
		}
		if(selRedemptionEmailLbId.getSelectedItem().getLabel().equalsIgnoreCase("Default Message")) {
			redemptionEmailEditMsgBtnId.setVisible(false);
			editRedemptionEmailImgId.setVisible(false);
		}else if(!flag) {
			redemptionEmailEditMsgBtnId.setVisible(true);
			editRedemptionEmailImgId.setVisible(true);
		}

	
		
	}
	

	public void onSelect$selGftCrdAutoSmsLbId() {

		if(selGftCrdAutoSmsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto SMS")) {
			previewGftCrdExpSmsImgId.setVisible(false);
			gftCrdSmsPreviewBtnId.setVisible(false);
			editGftCrdExpSmsImgId.setVisible(false);
			gftCrdSmsEditMsgBtnId.setVisible(false);
			addGftCrdExpSmsImgId.setVisible(true);
			gftCrdSmsAddMsgBtnId.setVisible(true);
		}else{
			previewGftCrdExpSmsImgId.setVisible(true);
			gftCrdSmsPreviewBtnId.setVisible(true);
			editGftCrdExpSmsImgId.setVisible(true);
			gftCrdSmsEditMsgBtnId.setVisible(true);
			addGftCrdExpSmsImgId.setVisible(true);
			gftCrdSmsAddMsgBtnId.setVisible(true);
		}

	}//onSelect$selGftCrdAutoSmsLbId()

	public void onSelect$selGftAmtAutoSmsLbId() {

		if(selGftAmtAutoSmsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto SMS")) {
			previewGftAmtExpSmsImgId.setVisible(false);
			gftAmtSmsPreviewBtnId.setVisible(false);
			editGftAmtExpSmsImgId.setVisible(false);
			gftAmtSmsEditMsgBtnId.setVisible(false);
			addGftAmtExpSmsImgId.setVisible(true);
			gftAmtSmsAddMsgBtnId.setVisible(true);
		}else{
			previewGftAmtExpSmsImgId.setVisible(true);
			gftAmtSmsPreviewBtnId.setVisible(true);
			editGftAmtExpSmsImgId.setVisible(true);
			gftAmtSmsEditMsgBtnId.setVisible(true);
			addGftAmtExpSmsImgId.setVisible(true);
			gftAmtSmsAddMsgBtnId.setVisible(true);
		}

	}//onSelect$selGftAmtAutoSmsLbId()

	public void onSelect$selMemExpiryAutoSmsLbId() {

		if(selMemExpiryAutoSmsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto SMS")) {
			previewMemSmsImgId.setVisible(false);
			memSmsPreviewBtnId.setVisible(false);
			editMemSmsImgId.setVisible(false);
			memSmsEditMsgBtnId.setVisible(false);
			addMemSmsImgId.setVisible(true);
			memSmsAddMsgBtnId.setVisible(true);
		}else{
			previewMemSmsImgId.setVisible(true);
			memSmsPreviewBtnId.setVisible(true);
			editMemSmsImgId.setVisible(true);
			memSmsEditMsgBtnId.setVisible(true);
			addMemSmsImgId.setVisible(true);
			memSmsAddMsgBtnId.setVisible(true);
		}

	}//onSelect$selMemExpiryAutoSmsLbId()

	public void onSelect$selRewardAutoSmsLbId() {

		if(selRewardAutoSmsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto SMS")) {
			previewRewExpSmsImgId.setVisible(false);
			rewardSmsPreviewBtnId.setVisible(false);
			editRewExpSmsImgId.setVisible(false);
			rewardSmsEditMsgBtnId.setVisible(false);
			addRewExpSmsImgId.setVisible(true);
			rewardSmsAddMsgBtnId.setVisible(true);
		}else{
			previewRewExpSmsImgId.setVisible(true);
			rewardSmsPreviewBtnId.setVisible(true);
			editRewExpSmsImgId.setVisible(true);
			rewardSmsEditMsgBtnId.setVisible(true);
			addRewExpSmsImgId.setVisible(true);
			rewardSmsAddMsgBtnId.setVisible(true);
		}

	}//onSelect$selRewardAutoSmsLbId()

	public void onSelect$selBonusAutoSmsLbId() {

		if(selBonusAutoSmsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto SMS")) {
			previewThresholdSmsImgId.setVisible(false);
			bonusSmsPreviewBtnId.setVisible(false);
			editThresholdSmsImgId.setVisible(false);
			bonusSmsEditMsgBtnId.setVisible(false);
			addThresholdSmsImgId.setVisible(true);
			bonusSmsAddBtnId.setVisible(true);

		}else{
			addThresholdSmsImgId.setVisible(true);
			bonusSmsAddBtnId.setVisible(true);
			previewThresholdSmsImgId.setVisible(true);
			bonusSmsPreviewBtnId.setVisible(true);
			editThresholdSmsImgId.setVisible(true);
			bonusSmsEditMsgBtnId.setVisible(true);
		}

	}//onSelect$selBonusAutoSmsLbId()

	public void onSelect$selTierAutoSmsLbId() {

		if(selTierAutoSmsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto SMS")) {
			previewTierSmsImgId.setVisible(false);
			tierSmsPreviewBtnId.setVisible(false);
			editTierSmsImgId.setVisible(false);
			tierSmsEditMsgBtnId.setVisible(false);
			addTierSmsImgId.setVisible(true);
			tierSmsAddBtnId.setVisible(true);

		}else{
			addTierSmsImgId.setVisible(true);
			tierSmsAddBtnId.setVisible(true);
			previewTierSmsImgId.setVisible(true);
			tierSmsPreviewBtnId.setVisible(true);
			editTierSmsImgId.setVisible(true);
			tierSmsEditMsgBtnId.setVisible(true);
		}

	}//onSelect$selTierAutoSmsLbId()

	public void onSelect$gftIssAutoSmsLbId() {

		if(gftIssAutoSmsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto SMS")) {
			previewGftIssSmsImgId.setVisible(false);
			gftIssSmsPreviewBtnId.setVisible(false);
			editGftIssSmsImgId.setVisible(false);
			gftIssSmsEditMsgBtnId.setVisible(false);
			addGftIssSmsImgId.setVisible(true);
			gftIssSmsAddBtnId.setVisible(true);

		}else{
			addGftIssSmsImgId.setVisible(true);
			gftIssSmsAddBtnId.setVisible(true);
			previewGftIssSmsImgId.setVisible(true);
			gftIssSmsPreviewBtnId.setVisible(true);
			editGftIssSmsImgId.setVisible(true);
			gftIssSmsEditMsgBtnId.setVisible(true);
		}

	}//onSelect$gftIssAutoSmsLbId()

	public void onSelect$selRegAutoSmsLbId() {

		if(selRegAutoSmsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto SMS")) {
			previewRegSmsImgId.setVisible(false);
			regSmsPreviewBtnId.setVisible(false);
			editRegSmsImgId.setVisible(false);
			regSmsEditMsgBtnId.setVisible(false);
			addRegSmsImgId.setVisible(true);
			regSmsAddBtnId.setVisible(true);

		}else{
			addRegSmsImgId.setVisible(true);
			regSmsAddBtnId.setVisible(true);
			previewRegSmsImgId.setVisible(true);
			regSmsPreviewBtnId.setVisible(true);
			editRegSmsImgId.setVisible(true);
			regSmsEditMsgBtnId.setVisible(true);
		}

	}//onSelect$selRegAutoSmsLbId()

	public void onSelect$selRegAutoEmailsLbId() {
		Boolean flag = false;
		if(selRegAutoEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto Email")) {
			previewRegEmailImgId.setVisible(false);
			regEmailPreviewBtnId.setVisible(false);
			editRegEmailImgId.setVisible(false);
			regEmailEditMsgBtnId.setVisible(false);
			addRegEmailImgId.setVisible(true);
			regEmailAddBtnId.setVisible(true);
			flag = true;
		}else{
			addRegEmailImgId.setVisible(true);
			regEmailAddBtnId.setVisible(true);
			previewRegEmailImgId.setVisible(true);
			regEmailPreviewBtnId.setVisible(true);
			editRegEmailImgId.setVisible(true);
			regEmailEditMsgBtnId.setVisible(true);
		}
		
		if(selRegAutoEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Default Message")) {
			editRegEmailImgId.setVisible(false);
			regEmailEditMsgBtnId.setVisible(false);
		}else if(!flag){
			editRegEmailImgId.setVisible(true);
			regEmailEditMsgBtnId.setVisible(true);
		}

	}//onSelect$selRegAutoEmailsLbId()

	public void onSelect$selTierAutoEmailsLbId() {
		Boolean flag = false;
		if(selTierAutoEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto Email")) {
			previewTierEmailImgId.setVisible(false);
			tierEmailPreviewBtnId.setVisible(false);
			editTierEmailImgId.setVisible(false);
			tierEmailEditMsgBtnId.setVisible(false);
			addTierEmailImgId.setVisible(true);
			tierEmailAddBtnId.setVisible(true);
			flag = true;
		}else{
			previewTierEmailImgId.setVisible(true);
			tierEmailPreviewBtnId.setVisible(true);
			editTierEmailImgId.setVisible(true);
			tierEmailEditMsgBtnId.setVisible(true);
			addTierEmailImgId.setVisible(true);
			tierEmailAddBtnId.setVisible(true);
		}
		if(selTierAutoEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Default Message")) {
			editTierEmailImgId.setVisible(false);
			tierEmailEditMsgBtnId.setVisible(false);
		}else if(!flag) {
			editTierEmailImgId.setVisible(true);
			tierEmailEditMsgBtnId.setVisible(true);
		}
		
	}//onSelect$selTierAutoEmailsLbId()

	public void onSelect$selBonusAutoEmailsLbId() {
		Boolean flag = false;
		if(selBonusAutoEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto Email")) {
			previewThresholdEmailImgId.setVisible(false);
			bonusEmailPreviewBtnId.setVisible(false);
			editThresholdEmailImgId.setVisible(false);
			bonusEmailEditMsgBtnId.setVisible(false);
			addThresholdEmailImgId.setVisible(true);
			bonusEmailAddBtnId.setVisible(true);
			flag = true;
		}else{
			previewThresholdEmailImgId.setVisible(true);
			bonusEmailPreviewBtnId.setVisible(true);
			editThresholdEmailImgId.setVisible(true);
			bonusEmailEditMsgBtnId.setVisible(true);
			addThresholdEmailImgId.setVisible(true);
			bonusEmailAddBtnId.setVisible(true);
		}
		if(selBonusAutoEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Default Message")) {
			editThresholdEmailImgId.setVisible(false);
			bonusEmailEditMsgBtnId.setVisible(false);
		}else if(!flag) {
			editThresholdEmailImgId.setVisible(true);
			bonusEmailEditMsgBtnId.setVisible(true);
		}

	}//onSelect$selBonusAutoEmailsLbId()

	public void onSelect$selRewardAutoEmailsLbId() {
		Boolean flag = false;
		if(selRewardAutoEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto Email")) {
			previewRewExpEmailImgId.setVisible(false);
			rewardEmailPreviewBtnId.setVisible(false);
			editRewExpEmailImgId.setVisible(false);
			rewardEmailEditMsgBtnId.setVisible(false);
			addRewExpEmailImgId.setVisible(true);
			rewardEmailAddMsgBtnId.setVisible(true);
			flag = true;
		}else{
			previewRewExpEmailImgId.setVisible(true);
			rewardEmailPreviewBtnId.setVisible(true);
			editRewExpEmailImgId.setVisible(true);
			rewardEmailEditMsgBtnId.setVisible(true);
			addRewExpEmailImgId.setVisible(true);
			rewardEmailAddMsgBtnId.setVisible(true);
		}
		if(selRewardAutoEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Default Message")) {
			editRewExpEmailImgId.setVisible(false);
			rewardEmailEditMsgBtnId.setVisible(false);
		}else if(!flag) {
			editRewExpEmailImgId.setVisible(true);
			rewardEmailEditMsgBtnId.setVisible(true);
		}

	}//onSelect$selRewardAutoEmailsLbId()

	public void onSelect$selMemExpiryAutoEmailsLbId() {
		Boolean flag = false;
		if(selMemExpiryAutoEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto Email")) {
			previewMemEmailImgId.setVisible(false);
			memEmailPreviewBtnId.setVisible(false);
			editMemEmailImgId.setVisible(false);
			memEmailEditMsgBtnId.setVisible(false);
			addMemEmailImgId.setVisible(true);
			memEmailAddMsgBtnId.setVisible(true);
			flag = true;
		}else{
			previewMemEmailImgId.setVisible(true);
			memEmailPreviewBtnId.setVisible(true);
			editMemEmailImgId.setVisible(true);
			memEmailEditMsgBtnId.setVisible(true);
			addMemEmailImgId.setVisible(true);
			memEmailAddMsgBtnId.setVisible(true);
		}
		if(selMemExpiryAutoEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Default Message")) {
			memEmailEditMsgBtnId.setVisible(false);
			editMemEmailImgId.setVisible(false);
		}else if(!flag) {
			memEmailEditMsgBtnId.setVisible(true);
			editMemEmailImgId.setVisible(true);
		}

	}//onSelect$selMemExpiryAutoEmailsLbId()

	public void onSelect$selGftAmtAutoEmailsLbId() {
		Boolean flag = false;
		if(selGftAmtAutoEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto Email")) {
			previewGftAmtExpEmailImgId.setVisible(false);
			gftAmtEmailPreviewBtnId.setVisible(false);
			editGftAmtExpEmailImgId.setVisible(false);
			gftAmtEmailEditMsgBtnId.setVisible(false);
			addGftAmtExpEmailImgId.setVisible(true);
			gftAmtEmailAddMsgBtnId.setVisible(true);
			flag = true;
		}else{
			previewGftAmtExpEmailImgId.setVisible(true);
			gftAmtEmailPreviewBtnId.setVisible(true);
			editGftAmtExpEmailImgId.setVisible(true);
			gftAmtEmailEditMsgBtnId.setVisible(true);
			addGftAmtExpEmailImgId.setVisible(true);
			gftAmtEmailAddMsgBtnId.setVisible(true);
		}
		if(selGftAmtAutoEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Default Message")) {
			gftAmtEmailEditMsgBtnId.setVisible(false);
			editGftAmtExpEmailImgId.setVisible(false);
		}else if(!flag) {
			editGftAmtExpEmailImgId.setVisible(true);
			gftAmtEmailEditMsgBtnId.setVisible(true);
		}

	}//onSelect$selGftAmtAutoEmailsLbId()

	public void onSelect$selGftCrdAutoEmailsLbId() {
		Boolean flag = false;
		if(selGftCrdAutoEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto Email")) {
			previewGftCrdExpEmailImgId.setVisible(false);
			gftCrdEmailPreviewBtnId.setVisible(false);
			editGftCrdExpEmailImgId.setVisible(false);
			gftCrdEmailEditMsgBtnId.setVisible(false);
			addGftCrdExpEmailImgId.setVisible(true);
			gftCrdEmailAddMsgBtnId.setVisible(true);
			flag = true;
		}else{
			previewGftCrdExpEmailImgId.setVisible(true);
			gftCrdEmailPreviewBtnId.setVisible(true);
			editGftCrdExpEmailImgId.setVisible(true);
			gftCrdEmailEditMsgBtnId.setVisible(true);
			addGftCrdExpEmailImgId.setVisible(true);
			gftCrdEmailAddMsgBtnId.setVisible(true);
		}
		if(selGftCrdAutoEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Default Message")) {
			gftCrdEmailEditMsgBtnId.setVisible(false);
			editGftCrdExpEmailImgId.setVisible(false);
		}else if(!flag) {
			gftCrdEmailEditMsgBtnId.setVisible(true);
			editGftCrdExpEmailImgId.setVisible(true);
		}

	}//onSelect$selGftCrdAutoEmailsLbId()


	public void onSelect$gftIssAutoEmailsLbId() {
		Boolean flag = false;
		if(gftIssAutoEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto Email")) {
			previewGftIssEmailImgId.setVisible(false);
			gftIssEmailPreviewBtnId.setVisible(false);
			editGftIssEmailImgId.setVisible(false);
			gftIssEmailEditMsgBtnId.setVisible(false);
			addGftIssEmailImgId.setVisible(true);
			gftIssEmailAddBtnId.setVisible(true);
			flag = true;
		}else{
			previewGftIssEmailImgId.setVisible(true);
			gftIssEmailPreviewBtnId.setVisible(true);
			editGftIssEmailImgId.setVisible(true);
			gftIssEmailEditMsgBtnId.setVisible(true);
			addGftIssEmailImgId.setVisible(true);
			gftIssEmailAddBtnId.setVisible(true);
		}
		if(gftIssAutoEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Default Message")) {
			editGftIssEmailImgId.setVisible(false);
			gftIssEmailEditMsgBtnId.setVisible(false);
		}else if(!flag) {
			editGftIssEmailImgId.setVisible(true);
			gftIssEmailEditMsgBtnId.setVisible(true);
		}

	}//onSelect$gftIssAutoEmailsLbId()

	/**
	 * Populates the email templates for all types
	 * @param emailLbid
	 * @param type
	 */
	public void getLoyaltyTemplateList(Listbox emailLbid, String type) {
		logger.debug("---Entered getLoyaltyTemplateList---");
		List<CustomTemplates> templatelist = null;
		try {
			LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
			templatelist = ltyPrgmSevice.getTemplateList(user.getUserId(),type);
			Components.removeAllChildren(emailLbid);
			Listitem item = null;
			item = new Listitem("Select Auto Email", null);
			item.setParent(emailLbid);
			/*item = new Listitem("Default Message", new Long(-1));
			item.setParent(emailLbid);*/

			if(templatelist != null && templatelist.size() >0) {

				for (CustomTemplates customTemplates : templatelist) {

					item = new Listitem(customTemplates.getTemplateName(), customTemplates.getTemplateId());
					item.setParent(emailLbid);
				}
			}
			if(emailLbid.getItemCount() > 0 ) emailLbid.setSelectedIndex(0);

		} catch(Exception e) {
			logger.error(" - ** Exception to get the Loyalty template List - " + e + " **");
		}
		logger.debug("---Exit getLoyaltyTemplateList---");
	}//getLoyaltyTemplateList()

	/**
	 * Previews the email template auto-emails
	 * @param lbId
	 * @param defaultMsgType
	 */
	private void previewEmailTemplate(Listbox lbId,String defaultMsgType) {
		logger.debug("---Entered previewEmailTemplate---");
		if(lbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto Email")) {
			MessageUtil.setMessage("Please select atleast one template", "color:red", "TOP");
		}
		else {
			String templateContent = "";
			LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
			CustomTemplates customTemplates = ltyPrgmSevice.getCustomTemplate((Long) lbId.getSelectedItem().getValue());
			if(customTemplates == null) {
				templateContent = 	PropertyUtil.getPropertyValueFromDB(defaultMsgType);
			}else {
				if(customTemplates.getHtmlText() != null && !customTemplates.getHtmlText().isEmpty()) {
					templateContent = customTemplates.getHtmlText();
				}else if(customTemplates.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_BEE) && customTemplates.getMyTemplateId()!= null) {
					 MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(customTemplates.getMyTemplateId());
					 if(myTemplates!=null) {
						 templateContent = myTemplates.getContent();
					 }else {
						 MessageUtil.setMessage("No template was found configured in this auto-email message. Please edit the message to add a template to it.", "color:red", "TOP");
						 return;
					 }
				}
			}
			logger.info("templateContent 8----"+templateContent);
			Utility.showPreview(previewWin$html, user.getUserName(), templateContent);
			previewWin.setVisible(true);
		}
		logger.debug("---Exit previewEmailTemplate---");
	}//previewEmailTemplate()

	/**
	 * Edits the email template auto-emails
	 * @param lbId
	 * @param defaultMsgType
	 */
	private void editEmailTemplate(Listbox lbId,String tempType) {
		logger.debug("---Entered editEmailTemplate---");
		if(lbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto Email")) {
			MessageUtil.setMessage("Please select atleast one template", "color:red", "TOP");
		}
		else {
			logger.info("in edit mode--------------------");
			logger.info("custom template value "+lbId.getSelectedItem().getValue());
			LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
			CustomTemplates customTemplates = ltyPrgmSevice.getCustomTemplate((Long) lbId.getSelectedItem().getValue());
			session.setAttribute("editCustomTemplate", customTemplates);
			session.setAttribute("Mode", "edit");
			session.setAttribute("typeOfEmail",tempType);
			session.setAttribute("fromAddNewBtn","loyalty/loyaltyAutoCommunication");
			Redirect.goTo(PageListEnum.CONTACT_MANAGE_AUTO_EMAILS_BEE);
		}
		logger.debug("---Exit editEmailTemplate---");
	}//editEmailTemplate()

	/**
	 * Adds the email template auto-emails
	 * @param lbId
	 * @param tempType
	 */
	private void addEmailTemplate(Listbox lbId,String tempType) {

		logger.info("--Entered addEmailTemplate----");
		logger.info("custom template value "+lbId.getSelectedItem().getValue());
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		CustomTemplates customTemplates = ltyPrgmSevice.getCustomTemplate((Long) lbId.getSelectedItem().getValue());
		session.setAttribute("editCustomTemplate", customTemplates);
		session.setAttribute("Mode", "add");
		session.setAttribute("typeOfEmail",tempType);
		session.setAttribute("fromAddNewBtn","loyalty/loyaltyAutoCommunication");
		Redirect.goTo(PageListEnum.CONTACT_MANAGE_AUTO_EMAILS_BEE);
		logger.debug("---Exit addEmailTemplate---");
	}//addEmailTemplate()
	
	
	
	
	
	public void onClick$RedemptionOtpSmsPreviewBtnId() {
		logger.debug("---Entered onClick$RedemptionOtpSmsPreviewBtnId---");
		
		previewSMSTemplate(selRedemptionOtpAutoSmsLbId,OCConstants.REDEMPTIONOTP);
		
		logger.debug("---Exit onClick$RedemptionOtpSmsPreviewBtnId---");
	}


	public void onClick$RedemptionOtpSmsEditMsgBtnId() {
		
		logger.debug("---Entered onClick$RedemptionOtpSmsEditMsgBtnId--");
		
		editSMSTemplate(selRedemptionOtpAutoSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_REDEMPTION_OTP);
		
		logger.debug("---Exit onClick$RedemptionOtpSmsEditMsgBtnId---");
	}

	
	public void onClick$RedemptionOtpSmsAddMsgBtnId() {
		
		logger.debug("---Entered onClick$RedemptionOtpSmsAddMsgBtnId---");
		
		addSMSTemplate(selRedemptionOtpAutoSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_REDEMPTION_OTP);
		
		logger.debug("---Exit onClick$RedemptionOtpSmsAddMsgBtnId---");
	}
	
	
	
	
	
	
	
	
	
	// preview , edit and add the otp message templates
	
	public void onClick$OtpmessageSmsPreviewBtnId() {
		logger.debug("---Entered onClick$OtpmessageSmsPreviewBtnId---");
		previewSMSTemplate(selOtpmessageAutoSmsLbId,OCConstants.OTPMESSAGES);
		logger.debug("---Exit onClick$OtpmessageSmsPreviewBtnId---");
	}


	public void onClick$OtpmessageSmsEditMsgBtnId() {
		logger.debug("---Entered onClick$OtpmessageSmsEditMsgBtnId--");
		editSMSTemplate(selOtpmessageAutoSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_OTP_MESSAGE);
		logger.debug("---Exit onClick$OtpmessageSmsEditMsgBtnId---");
	}

	
	public void onClick$OtpmessageSmsAddMsgBtnId() {
		logger.debug("---Entered onClick$OtpmessageSmsAddMsgBtnId---");
		addSMSTemplate(selOtpmessageAutoSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_OTP_MESSAGE);
		logger.debug("---Exit onClick$redemptionSmsAddMsgBtnId---");
	}
	
	
	
	public void onClick$OtpmessagePreviewBtnId() {
		logger.debug("---Entered onClick$OtpmessagePreviewBtnId---");
		previewEmailTemplate(selOtpmessageEmailLbId,OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_OTPMESSAGE);
		logger.debug("---Exit onClick$redemptionPreviewBtnId---");
	}
	public void onClick$OtpmessageEmailEditMsgBtnId() {
		logger.debug("---Entered onClick$OtpmessageEmailEditMsgBtnId---");
		editEmailTemplate(selOtpmessageEmailLbId,Constants.CUSTOM_TEMPLATE_TYPE_OTP_MESSAGE);
		logger.debug("---Exit onClick$OtpmessageEmailEditMsgBtnId---");
	}

	public void onClick$OtpmessageEmailAddMsgBtnId() {
		logger.debug("---Entered onClick$OtpmessageEmailAddMsgBtnId---");
		addEmailTemplate(selOtpmessageEmailLbId,Constants.CUSTOM_TEMPLATE_TYPE_OTP_MESSAGE);
		logger.debug("---Exit onClick$OtpmessageEmailAddMsgBtnId---");
	}
	
	
	
	
	public void onClick$RedemptionOtpPreviewBtnId() {
		logger.debug("---Entered RedemptionOtpPreviewBtnId---");
		
		previewEmailTemplate(selRedemptionOtpEmailLbId,OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_REDEMPTIONOTP);
		
		logger.debug("---Exit RedemptionOtpPreviewBtnId---");
	}
	public void onClick$RedemptionOtpEmailEditMsgBtnId() {
		logger.debug("---Entered onClick$RedemptionOtpEmailEditMsgBtnId---");
		
		editEmailTemplate(selRedemptionOtpEmailLbId,Constants.CUSTOM_TEMPLATE_TYPE_REDEMPTION_OTP);
		
		logger.debug("---Exit onClick$RedemptionOtpEmailEditMsgBtnId()---");
	}

	public void onClick$RedemptionOtpEmailAddMsgBtnId() {
		logger.debug("---Entered onClick$RedemptionOtpEmailAddMsgBtnId---");
		
		addEmailTemplate(selRedemptionOtpEmailLbId,Constants.CUSTOM_TEMPLATE_TYPE_REDEMPTION_OTP);
	
		logger.debug("---Exit onClick$RedemptionOtpEmailAddMsgBtnId---");
	}
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// preview , edit and add the loyalty redemption templates
	
			public void onClick$redemptionPreviewBtnId() {
				logger.debug("---Entered onClick$redemptionPreviewBtnId---");
				previewEmailTemplate(selRedemptionEmailLbId,OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_REDEMPTION);
				logger.debug("---Exit onClick$redemptionPreviewBtnId---");
			}
			public void onClick$redemptionEmailEditMsgBtnId() {
				logger.debug("---Entered onClick$redemptionEmailEditMsgBtnId---");
				editEmailTemplate(selRedemptionEmailLbId,Constants.CUSTOM_TEMPLATE_TYPE_LOYALTY_REDEMPTION);
				logger.debug("---Exit onClick$redemptionEmailEditMsgBtnId---");
			}//onClick$redemptionEmailEditMsgBtnId

			public void onClick$redemptionEmailAddMsgBtnId() {
				logger.debug("---Entered onClick$redemptionEmailAddBtnId---");
				addEmailTemplate(selRedemptionEmailLbId,Constants.CUSTOM_TEMPLATE_TYPE_LOYALTY_REDEMPTION);
				logger.debug("---Exit onClick$redemptionEmailAddBtnId---");
			}//onClick$redemptionEmailEditMsgBtnId
			
			//redemptionSmsPreviewBtnId,redemptionSmsEditMsgBtnId,redemptionSmsAddMsgBtnId
			
			public void onClick$redemptionSmsPreviewBtnId() {
				logger.debug("---Entered onClick$redemptionSmsPreviewBtnId---");
				previewSMSTemplate(selRedemptionAutoSmsLbId,OCConstants.LOYALTYREDEMPTION);
				logger.debug("---Exit onClick$redemptionSmsPreviewBtnId---");
			}//onClick$redemptionSmsPreviewBtnId

			/**
			 * Edits the template of redemption
			 */
			public void onClick$redemptionSmsEditMsgBtnId() {
				logger.debug("---Entered onClick$redemptionSmsEditMsgBtnId--");
				editSMSTemplate(selRedemptionAutoSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_LOYALTY_REDEMPTION);
				logger.debug("---Exit onClick$redemptionSmsEditMsgBtnId---");
			}//onClick$redemptionSmsEditMsgBtnId

			/**
			 * To add a new template for redemption
			 */
			public void onClick$redemptionSmsAddMsgBtnId() {
				logger.debug("---Entered onClick$redemptionSmsAddMsgBtnId---");
				addSMSTemplate(selRedemptionAutoSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_LOYALTY_REDEMPTION);
				logger.debug("---Exit onClick$redemptionSmsAddMsgBtnId---");
			}//onClick$issuanceSmsAddBtnId
	
	// preview , edit and add the loyalty issuance templates
	
		public void onClick$issuancePreviewBtnId() {
			logger.debug("---Entered onClick$issuancePreviewBtnId---");
			previewEmailTemplate(selIssuanceEmailLbId,OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_ISSUANCE);
			logger.debug("---Exit onClick$issuancePreviewBtnId---");
		}
		public void onClick$issuanceEmailEditMsgBtnId() {
			logger.debug("---Entered onClick$issuanceEmailEditMsgBtnId---");
			editEmailTemplate(selIssuanceEmailLbId,Constants.CUSTOM_TEMPLATE_TYPE_LOYALTY_ISSUANCE);
			logger.debug("---Exit onClick$issuanceEmailEditMsgBtnId---");
		}//onClick$issuanceEmailEditMsgBtnId

		public void onClick$issuanceEmailAddMsgBtnId() {
			logger.debug("---Entered onClick$issuanceEmailAddBtnId---");
			addEmailTemplate(selIssuanceEmailLbId,Constants.CUSTOM_TEMPLATE_TYPE_LOYALTY_ISSUANCE);
			logger.debug("---Exit onClick$issuanceEmailAddBtnId---");
		}//onClick$issuanceEmailEditMsgBtnId
		
		//issuanceSmsPreviewBtnId,issuanceSmsEditMsgBtnId,issuanceSmsAddMsgBtnId
		
		public void onClick$issuanceSmsPreviewBtnId() {
			logger.debug("---Entered onClick$issuanceSmsPreviewBtnId---");
			previewSMSTemplate(selIssuanceAutoSmsLbId,OCConstants.LOYALTYISSUANCE);
			logger.debug("---Exit onClick$issuanceSmsPreviewBtnId---");
		}//onClick$regSmsPreviewBtnId

		/**
		 * Edits the template of issuance
		 */
		public void onClick$issuanceSmsEditMsgBtnId() {
			logger.debug("---Entered onClick$issuanceSmsEditMsgBtnId--");
			editSMSTemplate(selIssuanceAutoSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_LOYALTY_ISSUANCE);
			logger.debug("---Exit onClick$issuanceSmsEditMsgBtnId---");
		}//onClick$regSmsEditMsgBtnId

		/**
		 * To add a new template for issuance
		 */
		public void onClick$issuanceSmsAddMsgBtnId() {
			logger.debug("---Entered onClick$issuanceSmsAddMsgBtnId---");
			addSMSTemplate(selIssuanceAutoSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_LOYALTY_ISSUANCE);
			logger.debug("---Exit onClick$issuanceSmsAddMsgBtnId---");
		}//onClick$issuanceSmsAddBtnId
		
		
	
	// preview , edit and add the loyalty adjustment templates
	
	public void onClick$adjustmentPreviewBtnId() {
		logger.debug("---Entered onClick$adjustmentPreviewBtnId---");
		previewEmailTemplate(selAdjustmentEmailLbId,OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_ADJUSTMENT);
		logger.debug("---Exit onClick$adjustmentPreviewBtnId---");
	}
	public void onClick$AdjustmentEmailEditMsgBtnId() {
		logger.debug("---Entered onClick$gftIssEmailEditMsgBtnId---");
		editEmailTemplate(selAdjustmentEmailLbId,Constants.CUSTOM_TEMPLATE_TYPE_LOYALTY_ADJUSTMENT);
		logger.debug("---Exit onClick$gftIssEmailEditMsgBtnId---");
	}//onClick$AdjustmentEmailEditMsgBtnId

	public void onClick$adjustmentEmailAddMsgBtnId() {
		logger.debug("---Entered onClick$gftIssEmailAddBtnId---");
		addEmailTemplate(selAdjustmentEmailLbId,Constants.CUSTOM_TEMPLATE_TYPE_LOYALTY_ADJUSTMENT);
		logger.debug("---Exit onClick$gftIssEmailAddBtnId---");
	}//onClick$AdjustmentEmailEditMsgBtnId

	
	//adjustmentSmsPreviewBtnId,adjustmentSmsEditMsgBtnId,adjustmentSmsAddMsgBtnId
	
	public void onClick$adjustmentSmsPreviewBtnId() {
		logger.debug("---Entered onClick$adjustmentSmsPreviewBtnId---");
		previewSMSTemplate(selAdjustmentAutoSmsLbId,OCConstants.LOYALTYADJUSTMENT);
		logger.debug("---Exit onClick$adjustmentSmsPreviewBtnId---");
	}//onClick$regSmsPreviewBtnId

	/**
	 * Edits the template of enrollment
	 */
	public void onClick$adjustmentSmsEditMsgBtnId() {
		logger.debug("---Entered onClick$adjustmentSmsEditMsgBtnId--");
		editSMSTemplate(selAdjustmentAutoSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_LOYALTY_ADJUSTMENT);
		logger.debug("---Exit onClick$adjustmentSmsEditMsgBtnId---");
	}//onClick$regSmsEditMsgBtnId

	/**
	 * To add a new template for enrollment
	 */
	public void onClick$adjustmentSmsAddMsgBtnId() {
		logger.debug("---Entered onClick$adjustmentSmsAddMsgBtnId---");
		addSMSTemplate(selAdjustmentAutoSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_LOYALTY_ADJUSTMENT);
		logger.debug("---Exit onClick$adjustmentSmsAddMsgBtnId---");
	}//onClick$regSmsAddBtnId

	
	
	
	
	
	/**
	 * Preview the template of gift issuance 
	 */
	public void onClick$gftIssEmailPreviewBtnId() {
		logger.debug("---Entered onClick$gftIssEmailPreviewBtnId---");
		previewEmailTemplate(gftIssAutoEmailsLbId,OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_GIFTISSUE);
		logger.debug("---Exit onClick$gftIssEmailPreviewBtnId---");
	}//onClick$gftIssEmailPreviewBtnId()

	/**
	 * Edits the template of gift issuance 
	 */
	public void onClick$gftIssEmailEditMsgBtnId() {
		logger.debug("---Entered onClick$gftIssEmailEditMsgBtnId---");
		editEmailTemplate(gftIssAutoEmailsLbId,Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_ISSUANCE);
		logger.debug("---Exit onClick$gftIssEmailEditMsgBtnId---");
	}//onClick$gftIssEmailEditMsgBtnId()

	/**
	 * To add a new template for gift issuance 
	 */
	public void onClick$gftIssEmailAddBtnId() {
		logger.debug("---Entered onClick$gftIssEmailAddBtnId---");
		addEmailTemplate(gftIssAutoEmailsLbId,Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_ISSUANCE);
		logger.debug("---Exit onClick$gftIssEmailAddBtnId---");
	}//onClick$gftIssEmailAddBtnId()

	/**
	 * Preview the template of gift-card expiry
	 */
	public void onClick$gftCrdEmailPreviewBtnId() {
		logger.debug("---Entered onClick$gftCrdEmailPreviewBtnId---");
		previewEmailTemplate(selGftCrdAutoEmailsLbId,OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_GIFTMEMBSHIPEXPIRY);
		logger.debug("---Exit onClick$gftCrdEmailPreviewBtnId---");
	}//onClick$gftCrdEmailPreviewBtnId()

	/**
	 * Edits the template of gift-card expiry
	 */
	public void onClick$gftCrdEmailEditMsgBtnId() {
		logger.debug("---Entered onClick$gftCrdEmailEditMsgBtnId---");
		editEmailTemplate(selGftCrdAutoEmailsLbId,Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION);
		logger.debug("---Exit onClick$gftCrdEmailEditMsgBtnId---");
	}//onClick$gftCrdEmailEditMsgBtnId()

	/**
	 * To add a new template for gift-card expiry
	 */
	public void onClick$gftCrdEmailAddMsgBtnId() {
		logger.debug("---Entered onClick$gftCrdEmailAddMsgBtnId---");
		addEmailTemplate(selGftCrdAutoEmailsLbId,Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION);
		logger.debug("---Exit onClick$gftCrdEmailAddMsgBtnId---");
	}//onClick$gftCrdEmailAddMsgBtnId()

	/**
	 * Preview the template of gift amount expiry
	 */
	public void onClick$gftAmtEmailPreviewBtnId() {
		logger.debug("---Entered onClick$gftAmtEmailPreviewBtnId---");
		previewEmailTemplate(selGftAmtAutoEmailsLbId,OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_GIFTAMTEXPIRY);
		logger.debug("---Exit onClick$gftAmtEmailPreviewBtnId---");
	}//onClick$gftAmtEmailPreviewBtnId()

	/**
	 * Edits the template of gift amount expiry
	 */
	public void onClick$gftAmtEmailEditMsgBtnId() {
		logger.debug("---Entered onClick$gftAmtEmailEditMsgBtnId---");
		editEmailTemplate(selGftAmtAutoEmailsLbId,Constants.CUSTOM_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION);
		logger.debug("---Exit onClick$gftAmtEmailEditMsgBtnId---");
	}//onClick$gftAmtEmailEditMsgBtnId()

	/**
	 * To add a new template for gift amount expiry
	 */
	public void onClick$gftAmtEmailAddMsgBtnId() {
		logger.debug("---Entered onClick$gftAmtEmailAddMsgBtnId---");
		addEmailTemplate(selGftAmtAutoEmailsLbId,Constants.CUSTOM_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION);
		logger.debug("---Exit onClick$gftAmtEmailAddMsgBtnId---");
	}//onClick$gftAmtEmailAddMsgBtnId()

	/**
	 * Preview the template of membership expiry
	 */
	public void onClick$memEmailPreviewBtnId() {
		logger.debug("---Entered onClick$memEmailPreviewBtnId---");
		previewEmailTemplate(selMemExpiryAutoEmailsLbId,OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_LOYALTYMEMBSHIPEXPIRY);
		logger.debug("---Exit onClick$memEmailPreviewBtnId---");
	}//onClick$memEmailPreviewBtnId()

	/**
	 * Edits the template of membership expiry
	 */
	public void onClick$memEmailEditMsgBtnId() {
		logger.debug("---Entered onClick$memEmailEditMsgBtnId---");
		editEmailTemplate(selMemExpiryAutoEmailsLbId,Constants.CUSTOM_TEMPLATE_TYPE_MEMBERSHIP_EXPIRATION);
		logger.debug("---Exit onClick$memEmailEditMsgBtnId---");
	}//onClick$memEmailEditMsgBtnId()

	/**
	 * To add a new template for membership expiry
	 */
	public void onClick$memEmailAddMsgBtnId() {
		logger.debug("---Entered onClick$memEmailAddMsgBtnId---");
		addEmailTemplate(selMemExpiryAutoEmailsLbId,Constants.CUSTOM_TEMPLATE_TYPE_MEMBERSHIP_EXPIRATION);
		logger.debug("---Exit onClick$memEmailAddMsgBtnId---");
	}//onClick$memEmailAddMsgBtnId()

	/**
	 * Preview the template of reward expiry
	 */
	public void onClick$rewardEmailPreviewBtnId() {
		logger.debug("---Entered onClick$rewardEmailPreviewBtnId---");
		previewEmailTemplate(selRewardAutoEmailsLbId,OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_REWARDAMTEXPIRY);
		logger.debug("---Exit onClick$rewardEmailPreviewBtnId---");
	}//onClick$rewardEmailPreviewBtnId()

	/**
	 * Edits the template of reward expiry
	 */
	public void onClick$rewardEmailEditMsgBtnId() {
		logger.debug("---Entered onClick$rewardEmailEditMsgBtnId---");
		editEmailTemplate(selRewardAutoEmailsLbId,Constants.CUSTOM_TEMPLATE_TYPE_EARNED_REWARD_EXPIRATION);
		logger.debug("---Exit onClick$rewardEmailEditMsgBtnId---");
	}//onClick$rewardEmailEditMsgBtnId()

	/**
	 * To add a new template for reward expiry
	 */
	public void onClick$rewardEmailAddMsgBtnId() {
		logger.debug("---Entered onClick$rewardEmailAddMsgBtnId---");
		addEmailTemplate(selRewardAutoEmailsLbId,Constants.CUSTOM_TEMPLATE_TYPE_EARNED_REWARD_EXPIRATION);
		logger.debug("---Exit onClick$rewardEmailAddMsgBtnId---");
	}//onClick$rewardEmailAddMsgBtnId()

	/**
	 * Preview the template of earning bonus
	 */
	public void onClick$bonusEmailPreviewBtnId() {
		logger.debug("---Entered onClick$bonusEmailPreviewBtnId---");
		previewEmailTemplate(selBonusAutoEmailsLbId,OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_BONUS);
		logger.debug("---Exit onClick$bonusEmailPreviewBtnId---");
	}//onClick$bonusEmailPreviewBtnId()

	/**
	 * Edits the template of earning bonus
	 */
	public void onClick$bonusEmailEditMsgBtnId() {
		logger.debug("---Entered onClick$bonusEmailEditMsgBtnId---");
		editEmailTemplate(selBonusAutoEmailsLbId,Constants.CUSTOM_TEMPLATE_TYPE_EARNED_BONUS);
		logger.debug("---Exit onClick$bonusEmailEditMsgBtnId---");
	}//onClick$bonusEmailEditMsgBtnId()

	/**
	 * To add a new template for earning bonus
	 */
	public void onClick$bonusEmailAddBtnId() {
		logger.debug("---Entered onClick$bonusEmailAddBtnId---");
		addEmailTemplate(selBonusAutoEmailsLbId,Constants.CUSTOM_TEMPLATE_TYPE_EARNED_BONUS);
		logger.debug("---Exit onClick$bonusEmailAddBtnId---");
	}//onClick$bonusEmailAddBtnId()

	/**
	 * Preview the template of tier upgrade
	 */
	public void onClick$tierEmailPreviewBtnId() {
		logger.debug("---Entered onClick$tierEmailPreviewBtnId---");
		previewEmailTemplate(selTierAutoEmailsLbId,OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_TIERUPGRADE);
		logger.debug("---Exit onClick$tierEmailPreviewBtnId---");
	}//onClick$tierEmailPreviewBtnId()

	/**
	 * Edits the template of tier upgrade
	 */
	public void onClick$tierEmailEditMsgBtnId() {
		logger.debug("---Entered onClick$tierEmailEditMsgBtnId---");
		editEmailTemplate(selTierAutoEmailsLbId,Constants.CUSTOM_TEMPLATE_TYPE_TIER_UPGRADATION);
		logger.debug("---Exit onClick$tierEmailEditMsgBtnId---");
	}//onClick$tierEmailEditMsgBtnId()

	/**
	 * To add a new template for tier upgrade
	 */
	public void onClick$tierEmailAddBtnId() {
		logger.debug("---Entered onClick$tierEmailAddBtnId---");
		addEmailTemplate(selTierAutoEmailsLbId,Constants.CUSTOM_TEMPLATE_TYPE_TIER_UPGRADATION);
		logger.debug("---Exit onClick$tierEmailAddBtnId---");
	}//onClick$tierEmailAddBtnId()

	/**
	 * Preview the template of enrollment
	 */
	public void onClick$regEmailPreviewBtnId() {
		logger.debug("---Entered onClick$regEmailPreviewBtnId---");
		previewEmailTemplate(selRegAutoEmailsLbId,OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_REGISTRATION);
		logger.debug("---Exit onClick$regEmailPreviewBtnId---");
	}//onClick$regEmailPreviewBtnId()

	/**
	 * Edits the template of enrollment
	 */
	public void onClick$regEmailEditMsgBtnId() {
		logger.debug("---Entered onClick$regEmailEditMsgBtnId---");
		editEmailTemplate(selRegAutoEmailsLbId,Constants.CUSTOM_TEMPLATE_TYPE_LOYALTYOPTIN);
		logger.debug("---Exit onClick$regEmailEditMsgBtnId---");
	}//onClick$regEmailEditMsgBtnId()

	/**
	 * To add a new template for enrollment
	 */
	public void onClick$regEmailAddBtnId() {
		logger.debug("---Entered onClick$regEmailAddBtnId---");
		addEmailTemplate(selRegAutoEmailsLbId,Constants.CUSTOM_TEMPLATE_TYPE_LOYALTYOPTIN);
		logger.debug("---Exit onClick$regEmailAddBtnId---");
	}//onClick$regEmailAddBtnId()

	/**
	 * Populates the templates of SMS for all types
	 * @param smsLbId
	 * @param type
	 */
	private void getLoyaltySmsTemplateList(Listbox smsLbId, String type) {
		logger.debug("---Entered getLoyaltySmsTemplateList---");
		List<AutoSMS> smsTemplateList = null;
		try {
			LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
			smsTemplateList = ltyPrgmSevice.getSmsTemplateList(user.getUserId(),type);
			Components.removeAllChildren(smsLbId);
			Listitem item = null;
			item = new Listitem("Select Auto SMS", null);
			item.setParent(smsLbId);
			item = new Listitem("Default Message", new Long(-1));
			item.setParent(smsLbId);

			if(smsTemplateList != null && smsTemplateList.size() >0) {
				for (AutoSMS autoSMS : smsTemplateList) {
					item = new Listitem(autoSMS.getTemplateName(), autoSMS.getAutoSmsId());
					item.setParent(smsLbId);
				}
			}
			if(smsLbId.getItemCount() > 0 ) smsLbId.setSelectedIndex(0);

		} catch(Exception e) {
			logger.error(" - ** Exception to get the Loyalty template List - " + e + " **");
		}
		logger.debug("---Exit getLoyaltySmsTemplateList----");
	}//getLoyaltySmsTemplateList()

	/**
	 * Previews the selected template
	 * @param lbId
	 * @param tempType
	 */
	public void previewSMSTemplate(Listbox lbId,String defaultMsgType) {
		logger.info("---Entered previewSMSTemplate-----");
		if(lbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto SMS")) {
			MessageUtil.setMessage("Please select atleast one template", "color:red", "TOP");
		}
		else {
			String templateContent = "";
			LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
			AutoSMS autoSMS = ltyPrgmSevice.getAutoSmsTemplateById((Long) lbId.getSelectedItem().getValue());
			if(autoSMS == null) {
				templateContent = 	PropertyUtil.getPropertyValueFromDB(defaultMsgType);
			}else {
				templateContent = autoSMS.getMessageContent();
			}
			logger.info("templateContent 1----"+templateContent);
			logger.info("previewWin$html----"+previewWin$html);

			Utility.showPreview(previewWin$html, user.getUserName(), templateContent);
			previewWin.setVisible(true);
		}
		logger.debug("--Exit previewSMSTemplate---");
	}//previewSMSTemplate()

	/**
	 * Edits the template in auto-SMS
	 * @param lbId
	 * @param tempType
	 */
	public void editSMSTemplate(Listbox lbId,String tempType) {
		logger.info("---Entered editSMSTemplate-----");
		if(lbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto SMS")) {
			MessageUtil.setMessage("Please select atleast one template", "color:red", "TOP");
		}
		else {
			logger.info("in edit mode--------------------");
			logger.info("Sms template value "+lbId.getSelectedItem().getValue());
			LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
			AutoSMS autoSMS = ltyPrgmSevice.getAutoSmsTemplateById((Long) lbId.getSelectedItem().getValue());
			session.setAttribute("editSmsTemplate", autoSMS);
			session.setAttribute("SmsMode", "edit");
			session.setAttribute("typeOfSms",tempType);
			session.setAttribute("fromAddNewBtn","loyalty/loyaltyAutoCommunication");
			Redirect.goTo(PageListEnum.CONTACT_MANAGE_AUTO_SMS);
		}
		logger.debug("--Exit editSMSTemplate---");
	}//editSMSTemplate()

	/**
	 * Adds the email template in auto-SMS
	 * @param lbId
	 * @param tempType
	 */
	public void addSMSTemplate(Listbox lbId,String tempType) {
		logger.info("---Entered addSMSTemplate-----");
		logger.info("Sms template value "+lbId.getSelectedItem().getValue());
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		AutoSMS autoSMS = ltyPrgmSevice.getAutoSmsTemplateById((Long) lbId.getSelectedItem().getValue());
		session.setAttribute("editSmsTemplate", autoSMS);
		session.setAttribute("SmsMode", "add");
		session.setAttribute("typeOfSms",tempType);
		session.setAttribute("fromAddNewBtn","loyalty/loyaltyAutoCommunication");
		Redirect.goTo(PageListEnum.CONTACT_MANAGE_AUTO_SMS);
		logger.debug("--Exit addSMSTemplate---");
	}//addSMSTemplate()

	/**
	 * Preview the template of enrollment
	 */
	public void onClick$regSmsPreviewBtnId() {
		logger.debug("---Entered onClick$regSmsPreviewBtnId---");
		previewSMSTemplate(selRegAutoSmsLbId,OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_REGISTRATION);
		logger.debug("---Exit onClick$regSmsPreviewBtnId---");
	}//onClick$regSmsPreviewBtnId

	/**
	 * Edits the template of enrollment
	 */
	public void onClick$regSmsEditMsgBtnId() {
		logger.debug("---Entered onClick$regSmsEditMsgBtnId---");
		editSMSTemplate(selRegAutoSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_LOYALTY_REGISTRATION);
		logger.debug("---Exit onClick$regSmsEditMsgBtnId---");
	}//onClick$regSmsEditMsgBtnId

	/**
	 * To add a new template for enrollment
	 */
	public void onClick$regSmsAddBtnId() {
		logger.debug("---Entered onClick$regSmsAddBtnId---");
		addSMSTemplate(selRegAutoSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_LOYALTY_REGISTRATION);
		logger.debug("---Exit onClick$regSmsAddBtnId---");
	}//onClick$regSmsAddBtnId


	/**
	 * Preview the template of gift issuance
	 */
	public void onClick$gftIssSmsPreviewBtnId() {
		logger.debug("---Entered onClick$gftIssSmsPreviewBtnId---");
		previewSMSTemplate(gftIssAutoSmsLbId,OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_GIFTISSUE);
		logger.debug("---Exit onClick$gftIssSmsPreviewBtnId---");
	}//onClick$gftIssSmsPreviewBtnId

	/**
	 * Edits the template of gift issuance
	 */
	public void onClick$gftIssSmsEditMsgBtnId() {
		logger.debug("---Entered onClick$gftIssSmsEditMsgBtnId---");
		editSMSTemplate(gftIssAutoSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_GIFT_CARD_ISSUANCE);
		logger.debug("---Exit onClick$gftIssSmsEditMsgBtnId---");
	}//onClick$gftIssSmsEditMsgBtnId

	/**
	 * To add a new template for gift issuance
	 */
	public void onClick$gftIssSmsAddBtnId() {
		logger.debug("---Entered onClick$gftIssSmsAddBtnId---");
		addSMSTemplate(gftIssAutoSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_GIFT_CARD_ISSUANCE);
		logger.debug("---Exit onClick$gftIssSmsAddBtnId---");
	}//onClick$gftIssSmsAddBtnId


	/**
	 * Preview the template of tier upgrade
	 */
	public void onClick$tierSmsPreviewBtnId() {
		logger.debug("---Entered onClick$tierSmsPreviewBtnId---");
		previewSMSTemplate(selTierAutoSmsLbId,OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_TIERUPGRADE);
		logger.debug("---Exit onClick$tierSmsPreviewBtnId---");
	}//onClick$tierSmsPreviewBtnId()

	/**
	 * Edits the template of tier upgrade
	 */
	public void onClick$tierSmsEditMsgBtnId() {
		logger.debug("---Entered onClick$tierSmsEditMsgBtnId---");
		editSMSTemplate(selTierAutoSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_TIER_UPGRADATION);
		logger.debug("---Exit onClick$tierSmsEditMsgBtnId---");
	}//onClick$tierSmsEditMsgBtnId()

	/**
	 * To add a new template for tier upgrade
	 */
	public void onClick$tierSmsAddBtnId() {
		logger.debug("---Entered onClick$tierSmsAddBtnId---");
		addSMSTemplate(selTierAutoSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_TIER_UPGRADATION);
		logger.debug("---Exit onClick$tierSmsAddBtnId---");
	}//onClick$tierSmsAddBtnId()

	/**
	 * Preview the template of earning bonus
	 */
	public void onClick$bonusSmsPreviewBtnId() {
		logger.debug("---Entered onClick$bonusSmsPreviewBtnId---");
		previewSMSTemplate(selBonusAutoSmsLbId,OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_BONUS);
		logger.debug("---Exit onClick$bonusSmsPreviewBtnId---");
	}//onClick$bonusSmsPreviewBtnId()

	/**
	 * Edits the template of earning bonus
	 */
	public void onClick$bonusSmsEditMsgBtnId() {
		logger.debug("---Entered onClick$bonusSmsEditMsgBtnId---");
		editSMSTemplate(selBonusAutoSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_EARNED_BONUS);
		logger.debug("---Exit onClick$bonusSmsEditMsgBtnId---");
	}//onClick$bonusSmsEditMsgBtnId()

	/**
	 * To add a new template for earning bonus
	 */
	public void onClick$bonusSmsAddBtnId() {
		logger.debug("---Entered onClick$bonusSmsAddBtnId---");
		addSMSTemplate(selBonusAutoSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_EARNED_BONUS);
		logger.debug("---Exit onClick$bonusSmsAddBtnId---");
	}//onClick$bonusSmsAddBtnId()

	/**
	 * Preview the template of reward expiry
	 */
	public void onClick$rewardSmsPreviewBtnId() {
		logger.debug("---Entered onClick$rewardSmsPreviewBtnId---");
		previewSMSTemplate(selRewardAutoSmsLbId,OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_REWARDAMTEXPIRY);
		logger.debug("---Exit onClick$rewardSmsPreviewBtnId---");
	}//onClick$rewardSmsPreviewBtnId()
	
	/**
	 * Edits the template of reward expiry
	 */
	public void onClick$rewardSmsEditMsgBtnId() {
		logger.debug("---Entered onClick$rewardSmsEditMsgBtnId---");
		editSMSTemplate(selRewardAutoSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_EARNED_REWARD_EXPIRATION);
		logger.debug("---Exit onClick$rewardSmsEditMsgBtnId---");
	}//onClick$rewardSmsEditMsgBtnId()
	
	/**
	 * To add a new template for reward expiry
	 */
	public void onClick$rewardSmsAddMsgBtnId() {
		logger.debug("---Entered onClick$rewardSmsAddMsgBtnId---");
		addSMSTemplate(selRewardAutoSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_EARNED_REWARD_EXPIRATION);
		logger.debug("---Exit onClick$rewardSmsAddMsgBtnId---");
	}//onClick$rewardSmsAddMsgBtnId()

	/**
	 * Preview the template of membership expiry
	 */
	public void onClick$memSmsPreviewBtnId() {
		logger.debug("---Entered onClick$memSmsPreviewBtnId---");
		previewSMSTemplate(selMemExpiryAutoSmsLbId,OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_LOYALTYMEMBSHIPEXPIRY);
		logger.debug("---Exit onClick$memSmsPreviewBtnId---");
	}//onClick$memSmsPreviewBtnId()

	/**
	 * Edits the template of membership expiry
	 */
	public void onClick$memSmsEditMsgBtnId() {
		logger.debug("---Entered onClick$memSmsEditMsgBtnId---");
		editSMSTemplate(selMemExpiryAutoSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_MEMBERSHIP_EXPIRATION);
		logger.debug("---Exit onClick$memSmsEditMsgBtnId---");
	}//onClick$memSmsEditMsgBtnId()

	/**
	 * To add a new template for membership expiry
	 */
	public void onClick$memSmsAddMsgBtnId() {
		logger.debug("---Entered onClick$memSmsAddMsgBtnId---");
		addSMSTemplate(selMemExpiryAutoSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_MEMBERSHIP_EXPIRATION);
		logger.debug("---Exit onClick$memSmsAddMsgBtnId---");
	}//onClick$memSmsAddMsgBtnId()

	/**
	 * Previews the template of gift amount expiry
	 */
	public void onClick$gftAmtSmsPreviewBtnId() {
		logger.debug("--Entered onClick$gftAmtSmsPreviewBtnId---");
		previewSMSTemplate(selGftAmtAutoSmsLbId,OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_GIFTAMTEXPIRY);
		logger.debug("--Exit onClick$gftAmtSmsPreviewBtnId---");
	}//onClick$gftAmtSmsPreviewBtnId()
	
	/**
	 * Edits the template of gift amount expiry
	 */
	public void onClick$gftAmtSmsEditMsgBtnId() {
		logger.debug("--Entered onClick$gftAmtSmsEditMsgBtnId---");
		editSMSTemplate(selGftAmtAutoSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION);
		logger.debug("--Exit onClick$gftAmtSmsEditMsgBtnId---");
	}//onClick$gftAmtSmsEditMsgBtnId() 
	
	/**
	 * To add a new template for gift amount expiry
	 */
	public void onClick$gftAmtSmsAddMsgBtnId() {
		logger.debug("--Entered onClick$gftAmtSmsAddMsgBtnId---");
		addSMSTemplate(selGftAmtAutoSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION);
		logger.debug("--Exit onClick$gftAmtSmsAddMsgBtnId---");
	}//onClick$gftAmtSmsAddMsgBtnId()

	/**
	 * Previews the template of gift-card expiry
	 */
	public void onClick$gftCrdSmsPreviewBtnId() {
		logger.debug("--Entered onClick$gftCrdSmsPreviewBtnId---");
		previewSMSTemplate(selGftCrdAutoSmsLbId,OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_GIFTMEMBSHIPEXPIRY);
		logger.debug("--Exit onClick$gftCrdSmsPreviewBtnId---");
	}//onClick$gftCrdSmsPreviewBtnId()

	/**
	 * Edits the template of gift-card expiry
	 */
	public void onClick$gftCrdSmsEditMsgBtnId() {
		logger.debug("--Entered onClick$gftCrdSmsEditMsgBtnId---");
		editSMSTemplate(selGftCrdAutoSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION);
		logger.debug("--Exit onClick$gftCrdSmsEditMsgBtnId---");
	}//onClick$gftCrdSmsEditMsgBtnId()

	/**
	 * To add a new template for gift-card expiry
	 */
	public void onClick$gftCrdSmsAddMsgBtnId() {
		logger.debug("--Entered onClick$gftCrdSmsAddMsgBtnId---");
		addSMSTemplate(selGftCrdAutoSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION);
		logger.debug("--Exit onClick$gftCrdSmsAddMsgBtnId---");
	}//onClick$gftCrdSmsAddMsgBtnId()

	/**
	 * Saves the selected auto communication templates
	 */
	public void onClick$saveBtnId() {
		logger.debug("---Entered onClick$saveBtnId---");
		Long  prgmId=(Long) session.getAttribute("programId");
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		LoyaltyAutoComm loyaltyAutoComm=ltyPrgmSevice.getAutoCommunicationObj(prgmId);
		//auto-emails
		Long regEmailTmpltId = selRegAutoEmailsLbId.getSelectedItem().getValue();
		Long tierUpgdEmailTmpltId = selTierAutoEmailsLbId.getSelectedItem().getValue();
		Long threshBonusEmailTmpltId = selBonusAutoEmailsLbId.getSelectedItem().getValue();
		Long rewardExpiryEmailTmpltId = selRewardAutoEmailsLbId.getSelectedItem().getValue();
		Long mbrshipExpiryEmailTmpltId = selMemExpiryAutoEmailsLbId.getSelectedItem().getValue();
		Long giftAmtExpiryEmailTmpltId = selGftAmtAutoEmailsLbId.getSelectedItem().getValue();
		Long giftMembrshpExpiryEmailTmpltId = selGftCrdAutoEmailsLbId.getSelectedItem().getValue();
		Long giftCardIssuanceEmailTmpltId = gftIssAutoEmailsLbId.getSelectedItem().getValue();
		Long adjustmentAutoEmailTmplId = selAdjustmentEmailLbId.getSelectedItem().getValue();
		Long issuanceAutoEmailTmplId = selIssuanceEmailLbId.getSelectedItem().getValue();
		Long redemptionAutoEmailTmplId = selRedemptionEmailLbId.getSelectedItem().getValue();
		Long otpMessageAutoEmailTmplId = selOtpmessageEmailLbId.getSelectedItem().getValue();
		logger.info("Otp email template name :"+ selOtpmessageEmailLbId.getSelectedItem().getValue() +" : "+selOtpmessageEmailLbId.getSelectedItem().getLabel());
		
		Long redemptionOtpAutoEmailTmplId=selRedemptionOtpEmailLbId.getSelectedItem().getValue();
		
		//auto-sms
		Long regSmsTmpltId = selRegAutoSmsLbId.getSelectedItem().getValue();
		Long tierUpgdSmsTmpltId = selTierAutoSmsLbId.getSelectedItem().getValue();
		Long threshBonusSmsTmpltId = selBonusAutoSmsLbId.getSelectedItem().getValue();
		Long rewardExpirySmsTmpltId = selRewardAutoSmsLbId.getSelectedItem().getValue();
		Long mbrshipExpirySmsTmpltId = selMemExpiryAutoSmsLbId.getSelectedItem().getValue();
		Long giftAmtExpirySmsTmpltId = selGftAmtAutoSmsLbId.getSelectedItem().getValue();
		Long giftMembrshpExpirySmsTmpltId = selGftCrdAutoSmsLbId.getSelectedItem().getValue();
		Long giftCardIssuanceSmsTmpltId = gftIssAutoSmsLbId.getSelectedItem().getValue();
		Long adjustmentAutoSmsTmplId = selAdjustmentAutoSmsLbId.getSelectedItem().getValue();
		Long issuanceAutoSmsTmplId = selIssuanceAutoSmsLbId.getSelectedItem().getValue();
		Long redemptionAutoSmsTmplId = selRedemptionAutoSmsLbId.getSelectedItem().getValue();
		Long otpMessageAutoSmsTmpltId = selOtpmessageAutoSmsLbId.getSelectedItem().getValue();
		
		Long redemptionOtpAutoSmsTmpltId=selRedemptionOtpAutoSmsLbId.getSelectedItem().getValue();
		//Long EreceiptmessageAutoSmsTmpltId=selEreceiptmessageAutoSmsLbId.getSelectedItem().getValue();

		
		
		logger.info("Otp sms template name :"+ selOtpmessageAutoSmsLbId.getSelectedItem().getValue() +" : "+selOtpmessageAutoSmsLbId.getSelectedItem().getLabel());
		
		ltyPrgmSevice.saveAutoCommunication(prgmId,regEmailTmpltId,tierUpgdEmailTmpltId,threshBonusEmailTmpltId,rewardExpiryEmailTmpltId,mbrshipExpiryEmailTmpltId,user.getUserId(),
				loyaltyAutoComm,giftAmtExpiryEmailTmpltId,giftMembrshpExpiryEmailTmpltId,giftCardIssuanceEmailTmpltId,
				regSmsTmpltId,tierUpgdSmsTmpltId,threshBonusSmsTmpltId,rewardExpirySmsTmpltId,mbrshipExpirySmsTmpltId,giftAmtExpirySmsTmpltId,
				giftMembrshpExpirySmsTmpltId,giftCardIssuanceSmsTmpltId,adjustmentAutoEmailTmplId,adjustmentAutoSmsTmplId,
				issuanceAutoEmailTmplId,issuanceAutoSmsTmplId,redemptionAutoEmailTmplId,redemptionAutoSmsTmplId,
				otpMessageAutoEmailTmplId,otpMessageAutoSmsTmpltId,redemptionOtpAutoEmailTmplId,redemptionOtpAutoSmsTmpltId); // order
		MessageUtil.setMessage("Auto communications saved successfully.", "color:blue", "TOP");
		resetFields();
		Redirect.goTo(PageListEnum.LOYALTY_PROGRAM_OVERVIEW);
		logger.debug("---Exit onClick$saveBtnId---");
	}//onClick$saveBtnId()

	/**
	 * Resets the selected auto communication data
	 */
	private void resetFields() {
		logger.debug("---Entered resetFields----");
		//auto-emails
		selRegAutoEmailsLbId.setSelectedIndex(0);
		selTierAutoEmailsLbId.setSelectedIndex(0);
		selBonusAutoEmailsLbId.setSelectedIndex(0);
		selRewardAutoEmailsLbId.setSelectedIndex(0);
		selMemExpiryAutoEmailsLbId.setSelectedIndex(0);
		selGftAmtAutoEmailsLbId.setSelectedIndex(0);
		selGftCrdAutoEmailsLbId.setSelectedIndex(0);
		gftIssAutoEmailsLbId.setSelectedIndex(0);
		//auto-sms 
		selRegAutoSmsLbId.setSelectedIndex(0);
		selTierAutoSmsLbId.setSelectedIndex(0);
		selBonusAutoSmsLbId.setSelectedIndex(0);
		selRewardAutoSmsLbId.setSelectedIndex(0);
		selMemExpiryAutoSmsLbId.setSelectedIndex(0);
		selGftAmtAutoSmsLbId.setSelectedIndex(0);
		selGftCrdAutoSmsLbId.setSelectedIndex(0);
		gftIssAutoSmsLbId.setSelectedIndex(0);
		logger.debug("---Exit resetFields----");
	}//resetFields()

	/**
	 * Redirects to previous step
	 */
	public void onClick$previousBtnId() {
		logger.debug("---Entered onClick$previousBtnId---");
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj((Long) session.getAttribute("programId"));
		if(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equalsIgnoreCase(user.getloyaltyServicetype())){
			if(prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE) || prgmObj.getProgramType().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_TYPE_DYNAMIC)){
				Redirect.goTo(PageListEnum.LOYALTY_RULES);
			}
			else{
			Redirect.goTo(PageListEnum.LOYALTY_ADD_CARDS);
			}
		}else{
			Redirect.goTo(PageListEnum.LOYALTY_ADDITIONAL_SETTINGS);
		}
		logger.debug("---Exit onClick$previousBtnId---");
	}//onClick$previousBtnId()

	/**
	 * Redirects to next step
	 */
	public void onClick$nextBtnId() {
		logger.debug("---Entered onClick$nextBtnId---");
		Redirect.goTo(PageListEnum.LOYALTY_PROGRAM_OVERVIEW);
		logger.debug("---Entered onClick$nextBtnId--");
	}//onClick$nextBtnId()
}

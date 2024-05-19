package org.mq.optculture.controller.loyalty;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.AutoSMS;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.LoyaltyAutoComm;
import org.mq.marketer.campaign.beans.LoyaltyCardSet;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramExclusion;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltyThresholdBonus;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.business.loyalty.LoyaltyProgramService;
import org.mq.optculture.data.dao.JdbcResultsetHandler;
import org.mq.optculture.data.dao.LoyaltyThresholdBonusDao;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

public class LoyaltyPrgmOverviewController extends GenericForwardComposer {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Long userId;
	
	private Users user;
	private Label prgmNameLblId, prgmDescLblId, prgmTypeLblId, enableTierLblId, enableDefaultLblId,  defaultPrgmId, enableUniqueMblNoLblId,enableUniqueEmailLblId, excludedProdTypeLblId,
	selectedFieldsLblId, enableOTPAuthLblId, rewardExpLblId, membshpExpLblId, resetValFlagLblId,regEmailLblId,tierEmailLblId,
	thresholdEmailLblId,rewExpEmailLblId,memExpEmailLblId,gftAmtExpEmailLblId,gftCardExpEmailLblId,gftAmtExpLblId,gftCrdExpLblId,gftCrdIssEmailLblId,
	selectedIssuePromosLblId,selectedRedeemPromosLblId,regSMSLblId,gftCrdIssSMSLblId,tierSMSLblId,tresholdSMSLblId,rewExpSMSLblId,memExpSMSLblId,gftAmtExpSMSLblId,gftCardExpSMSLblId, allselectedRedeemLblId;
	private Listbox thresholdBonusWinId$selRewardAutoEmailsLbId,thresholdBonusWinId$selRewardAutoSmsLbId,thresholdBonusWinId$giftAmtExpTypeLbId,rewardTierLbId, membshpTierLbId, winId$genTypeLbId,winId$statusLbId,
	thresholdBonusWinId$bonusThresholdTypeLbId,thresholdBonusWinId$bonusTypeLbId,thresholdBonusWinId$levelTypeLbId,excProductLbId,
	exportWinId$statusLbId, winId$selTierLbId, thresholdBonusWinId$levelValueLbId,thresholdBonusWinId$selBonusAutoEmailsLbId,thresholdBonusWinId$selBonusAutoSmsLbId;
	private Rows cardSetRowsId, tierRowsId, levelRowsId;
	private Div thresholdBonusWinId$expiryAutoCommDiv,thresholdBonusWinId$rewardTierDivId,thresholdBonusWinId$limitDivId,rewardTierDivId, rewardExpDivId, membshpTierDivId, membshpExpDivId,noExcludedStoresDivId, giftAmtDivId, giftValidityDivId, giftAmtExpDivId, giftIssDivId, giftMemExpDivId,
	excludedStoresDivId, ltyPrgmTypeDivId, excludedDatesDivId, noExcludedDatesDivId,noExcludedRedemDatesDivId,excludedRedemDatesDivId,excLableDivId,excludeDivId,resetLevelDivId, otpDivId,disallowIssuRemdDivId, noRedemExcludedStoresDivId, redemExcludedStoresDivId;
	private Window winId,thresholdBonusWinId;
	private Textbox thresholdBonusWinId$bonusThresholdValueTbId,thresholdBonusWinId$bonusValueTbId,thresholdBonusWinId$levelValueTbId,winId$cardSetNameTbId,
	winId$quantityNameTbId,thresholdBonusWinId$thresholdLimitID,thresholdBonusWinId$giftAmtExpValueTbId;
	private Button thresholdBonusWinId$bonusaddBtnId,draftBtnId,activeBtnId,suspendBtnId,thresholdBonusWinId$bonusCancelBtnId;
	private Image previewRegEmailImgId,editRegEmailImgId,previewTierEmailImgId,editTierEmailImgId,previewThresholdEmailImgId,editThresholdEmailImgId,previewRewExpEmailImgId,
	editRewExpEmailImgId,previewMemExpEmailImgId,editMemExpEmailImgId,previewGftAmtExpEmailImgId,editGftAmtExpEmailImgId,
	previewGftcrdExpEmailImgId,editGftcrdExpEmailImgId,previewGftCrdIssEmailImgId,editGftCrdIssEmailImgId,previewRegSMSImgId,editRegSMSImgId,
	previewGftCrdIssSMSImgId,editGftCrdIssSMSImgId,previewTierSMSImgId,editTierSMSImgId,previewThresholdSMSImgId,editThresholdSMSImgId,
	previewRewExpSMSImgId,editRewExpSMSImgId,previewMemExpSMSImgId,editMemExpSMSImgId,previewGftAmtExpSMSImgId,editGftAmtExpSMSImgId,previewGftcrdExpSMSImgId,editGftcrdExpSMSImgId;
	private Window previewWin,exportWinId;
	private Groupbox cardSetGbId, validitySettingsGbId, exclusionsSettingsGbId;
	private Radiogroup winId$ltyCardTypeRadioGrId, winId$tierAssignmentRadioGrId;
	private Radio winId$autoAssignRadioId, winId$linkTierRadioId;
	private Iframe previewWin$html;
	private Columns tierColsId;
	private boolean isEdit=false;
	TimeZone clientTimeZone ;
	private  String  userCurrencySymbol = "$ "; 
	private LoyaltyThresholdBonusDao loyaltyThresholdBonusDao;
	private String desc_Asc="desc"; 
	Long prgmId=null;
	private static int availableTiersCount = 0;
	
	private Label loyaltyAdjEmailLblId,loyaltyAdjSmsLblId;
	private Label loyaltyIssuanceEmailLblId,loyaltyIssuanceSmsLblId;
	private Label loyaltyRedemptionEmailLblId,loyaltyRedemptionSmsLblId;
	private Label otpMessageEmailLblId,otpMessageSmsLblId;
	private Label redemptionOtpEmailLblId,redemptionOtpSmsLblId;
	private Image previewLoyaltyRedemptionEmailImgId,editLoyaltyRedemptionEmailImgId,previewLoyaltyRedemptionSMSImgId,editLoyaltyRedemptionSMSImgId,
	previewLoyaltyIssuanceEmailImgId,editLoyaltyIssuanceEmailImgId,previewLoyaltyIssuanceSMSImgId,editLoyaltyIssuanceSMSImgId,
	previewLoyaltyAdjEmailImgId,editLoyaltyAdjEmailImgId,previewLoyaltyAdjSMSImgId,editLoyaltyAdjSMSImgId,
	thresholdBonusWinId$previewThresholdSmsImgIdForBonus,thresholdBonusWinId$previewThresholdEmailImgIdForBonus,
	thresholdBonusWinId$editThresholdEmailImgIdForBonus,thresholdBonusWinId$editThresholdSmsImgIdForBonus,
	thresholdBonusWinId$addThresholdEmailImgId,thresholdBonusWinId$addThresholdSmsImgId,
	thresholdBonusWinId$previewRewExpSmsImgId,thresholdBonusWinId$editRewExpSmsImgId,thresholdBonusWinId$addRewExpSmsImgId,
	thresholdBonusWinId$previewRewExpEmailImgId,thresholdBonusWinId$editRewExpEmailImgId,thresholdBonusWinId$addRewExpEmailImgId,editOtpMessageEmailImgId,previewOtpMessageEmailImgId,previewOtpMessageSMSImgId,editOtpMessageSMSImgId,
	previewRedemptionOtpEmailImgId,editRedemptionOtpEmailImgId,previewRedemptionOtpSMSImgId,editRedemptionOtpSMSImgId;
	
	
	private A thresholdBonusWinId$bonusEmailPreviewBtnId,thresholdBonusWinId$bonusSmsPreviewBtnId,bonusEmailEditMsgBtnId,
	thresholdBonusWinId$bonusSmsEditMsgBtnId,thresholdBonusWinId$bonusEmailAddBtnId,thresholdBonusWinId$bonusSmsAddBtnId,
	thresholdBonusWinId$bonusEmailEditMsgBtnId,thresholdBonusWinId$rewardSmsPreviewBtnId,thresholdBonusWinId$rewardSmsEditMsgBtnId,
	thresholdBonusWinId$rewardSmsAddMsgBtnId,thresholdBonusWinId$rewardEmailPreviewBtnId,thresholdBonusWinId$rewardEmailEditMsgBtnId
	,thresholdBonusWinId$rewardEmailAddMsgBtnId;
	//loyaltyAdjEmailLblId,previewLoyaltyAdjEmailImgId,editLoyaltyAdjEmailImgId
	//loyaltyAdjSmsLblId,previewLoyaltyAdjSMSImgId,editLoyaltyAdjSMSImgId

	private  MyTemplatesDao myTemplatesDao;
	private Checkbox thresholdBonusWinId$earnedRwrdExpiryCbId;


	public LoyaltyPrgmOverviewController() {
		user=GetUser.getUserObj();
		userId = user.getUserId();
		session = Sessions.getCurrent();
		myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
		Utility.ltyBreadCrumbFrom(6, OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype()));
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Create Loyalty Program (Step 6 of 6)","",style,true);
		clientTimeZone =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
		prgmId = (Long) session.getAttribute("programId");
		
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);

		if(prgmObj.getTierEnableFlag() == OCConstants.FLAG_YES) {
			setTiersList(prgmObj.getNoOfTiers());
		}
		winId$autoAssignRadioId.setDisabled(false);
		winId$linkTierRadioId.setDisabled(false);
		
		if(prgmObj.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_DRAFT)) {
			activeBtnId.setVisible(true);
			suspendBtnId.setVisible(false);
			draftBtnId.setVisible(true);
		}
		if(prgmObj.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE)) {
			suspendBtnId.setVisible(true);
			activeBtnId.setVisible(false);
			draftBtnId.setVisible(false);
		}
		if(prgmObj.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_SUSPENDED)) {
			suspendBtnId.setVisible(false);
			activeBtnId.setVisible(true);
			draftBtnId.setVisible(false);
		}

		loadBasicSettings(prgmObj);
		loadTransactionalSettings(prgmObj);

		if(prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE) ) {
			cardSetGbId.setVisible(false);
			giftAmtDivId.setVisible(false);
			giftValidityDivId.setVisible(false);
			giftIssDivId.setVisible(false);
			giftAmtExpDivId.setVisible(false);
			giftMemExpDivId.setVisible(false);
		}else if(prgmObj.getProgramType().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_TYPE_DYNAMIC) ) {
			cardSetGbId.setVisible(false);
		}else if(prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD) ) {
			loadAddedCardSets(prgmId);
		}
		if(OCConstants.LOYALTY_SERVICE_TYPE_OC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype())){
			giftAmtDivId.setVisible(true);
			giftValidityDivId.setVisible(true);
			giftIssDivId.setVisible(true);
			giftAmtExpDivId.setVisible(true);
			giftMemExpDivId.setVisible(true);
			disallowIssuRemdDivId.setVisible(true);
			resetLevelDivId.setVisible(true);
		}
		else{
			giftAmtDivId.setVisible(false);
			giftValidityDivId.setVisible(false);
			giftIssDivId.setVisible(false);
			giftAmtExpDivId.setVisible(false);
			giftMemExpDivId.setVisible(false);	
		}
		loadPrivlegeTiers(prgmId);
		loadThresholdBonus(prgmId);
		loadValiditySettings(prgmObj);
		loadExclusions(prgmObj);
		loadAutoCommunication(prgmObj,prgmId);
		disallowIssuRemdDivId.setVisible(true);
		if(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype())){
		 //validitySettingsGbId.setVisible(false);
		 exclusionsSettingsGbId.setVisible(false);
		 rewardExpDivId.setVisible(true);
		 membshpExpDivId.setVisible(true);
		 resetLevelDivId.setVisible(false);
		 disallowIssuRemdDivId.setVisible(false);
		}
		if(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype())&& GetUser.getUserObj().isEnableLoyaltyExtraction()){
			disallowIssuRemdDivId.setVisible(false);
			exclusionsSettingsGbId.setVisible(true);
		}
		//otpDivId.setVisible(true);
	}

	private void setTiersList(int noOfTiers) {
		for(int i=1 ; i <= noOfTiers ; i++){
			Listitem li = new Listitem("Tier "+i , i);
			li.setParent(winId$selTierLbId);
		}
		availableTiersCount = noOfTiers;

		Listitem listItemWin = new Listitem(OCConstants.THRESHOLD_TYPE_TIER);
		listItemWin.setValue(OCConstants.THRESHOLD_TYPE_TIER);
		listItemWin.setParent(thresholdBonusWinId$levelTypeLbId);
	}
	
	private void loadAutoCommunication(LoyaltyProgram prgmObj,Long prgmId) {
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		LoyaltyAutoComm loyaltyAutoComm=ltyPrgmSevice.getAutoCommunicationObj(prgmId);
		defaultSettings(loyaltyAutoComm);
	}
	public void onCheck$earnedRwrdExpiryCbId$thresholdBonusWinId() {

		if(thresholdBonusWinId$earnedRwrdExpiryCbId.isChecked()) {
			thresholdBonusWinId$rewardTierDivId.setVisible(true);
			thresholdBonusWinId$expiryAutoCommDiv.setVisible(true);
			thresholdBonusWinId$giftAmtExpTypeLbId.setSelectedIndex(0);
			thresholdBonusWinId$selRewardAutoEmailsLbId.setSelectedIndex(0);
			thresholdBonusWinId$selRewardAutoSmsLbId.setSelectedIndex(0);

		}
		else {
			thresholdBonusWinId$rewardTierDivId.setVisible(false);
			thresholdBonusWinId$expiryAutoCommDiv.setVisible(false);
		}
	}
	private void defaultSettings(LoyaltyAutoComm loyaltyAutoComm) {
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		if(loyaltyAutoComm != null) {
			Long regEmailId = loyaltyAutoComm.getRegEmailTmpltId();
			if(regEmailId != null && regEmailId != -1) {
				CustomTemplates customTemplates=ltyPrgmSevice.getCustomTemplate(regEmailId);
				regEmailLblId.setValue("Auto-Email Name : "+(customTemplates!=null?customTemplates.getTemplateName():" Not Configured"));
				previewRegEmailImgId.setVisible(true);
				editRegEmailImgId.setVisible(true);
				editRegEmailImgId.setAttribute("regEdit", customTemplates);
			}else if(regEmailId == null){
				regEmailLblId.setValue("Auto-Email Name : Not Configured");
				previewRegEmailImgId.setVisible(false);
				editRegEmailImgId.setVisible(false);
			}else {
				regEmailLblId.setValue("Auto-Email Name : Default Message");
				previewRegEmailImgId.setVisible(true);
				editRegEmailImgId.setVisible(true);
				editRegEmailImgId.setAttribute("regEdit", null);
			}

			Long tierEmailId = loyaltyAutoComm.getTierUpgdEmailTmpltId();
			if(tierEmailId != null && tierEmailId != -1) {
				CustomTemplates customTemplates=ltyPrgmSevice.getCustomTemplate(tierEmailId);
				tierEmailLblId.setValue("Auto-Email Name : "+(customTemplates!=null?customTemplates.getTemplateName():" Not Configured"));
				previewTierEmailImgId.setVisible(true);
				editTierEmailImgId.setVisible(true);
				editTierEmailImgId.setAttribute("tierEdit", customTemplates);
			}else if(tierEmailId==null){
				tierEmailLblId.setValue("Auto-Email Name : Not Configured");
				previewTierEmailImgId.setVisible(false);
				editTierEmailImgId.setVisible(false);
			}else {
				tierEmailLblId.setValue("Auto-Email Name : Default Message");
				previewTierEmailImgId.setVisible(true);
				editTierEmailImgId.setVisible(true);
				editTierEmailImgId.setAttribute("tierEdit", null);
			}

			Long bonusEmailId = loyaltyAutoComm.getThreshBonusEmailTmpltId();
			if(bonusEmailId != null && bonusEmailId != -1) {
				CustomTemplates customTemplates=ltyPrgmSevice.getCustomTemplate(bonusEmailId);
				thresholdEmailLblId.setValue("Auto-Email Name : "+(customTemplates!=null?customTemplates.getTemplateName():" Not Configured"));
				previewThresholdEmailImgId.setVisible(true);
				editThresholdEmailImgId.setVisible(true);
				editThresholdEmailImgId.setAttribute("thresholdBonusEdit", customTemplates);
			}else if(bonusEmailId==null){
				thresholdEmailLblId.setValue("Auto-Email Name : Not Configured");
				previewThresholdEmailImgId.setVisible(false);
				editThresholdEmailImgId.setVisible(false);
			}else {
				thresholdEmailLblId.setValue("Auto-Email Name : Default Message");
				previewThresholdEmailImgId.setVisible(true);
				editThresholdEmailImgId.setVisible(true);
				editThresholdEmailImgId.setAttribute("thresholdBonusEdit", null);
			}

			Long rewardExpEmailId = loyaltyAutoComm.getRewardExpiryEmailTmpltId();
			if(rewardExpEmailId != null && rewardExpEmailId != -1 ) {
				CustomTemplates customTemplates=ltyPrgmSevice.getCustomTemplate(rewardExpEmailId);
				rewExpEmailLblId.setValue("Auto-Email Name : "+(customTemplates!=null?customTemplates.getTemplateName():" Not Configured"));
				previewRewExpEmailImgId.setVisible(true);
				editRewExpEmailImgId.setVisible(true);
				editRewExpEmailImgId.setAttribute("rewExpEdit", customTemplates);
			}else if(rewardExpEmailId == null){
				rewExpEmailLblId.setValue("Auto-Email Name : Not Configured");
				previewRewExpEmailImgId.setVisible(false);
				editRewExpEmailImgId.setVisible(false);
			}else {
				rewExpEmailLblId.setValue("Auto-Email Name :  Default Message");
				previewRewExpEmailImgId.setVisible(true);
				editRewExpEmailImgId.setVisible(true);
				editRewExpEmailImgId.setAttribute("rewExpEdit", null);
			}

			Long memExpEmailId = loyaltyAutoComm.getMbrshipExpiryEmailTmpltId();
			if(memExpEmailId != null && memExpEmailId != -1 ) {
				CustomTemplates customTemplates=ltyPrgmSevice.getCustomTemplate(memExpEmailId);
				memExpEmailLblId.setValue("Auto-Email Name : "+(customTemplates!=null?customTemplates.getTemplateName():" Not Configured"));
				previewMemExpEmailImgId.setVisible(true);
				editMemExpEmailImgId.setVisible(true);
				editMemExpEmailImgId.setAttribute("memEdit", customTemplates);
			}else if(memExpEmailId == null){
				memExpEmailLblId.setValue("Auto-Email Name : Not Configured");
				previewMemExpEmailImgId.setVisible(false);
				editMemExpEmailImgId.setVisible(false);
			}else {
				memExpEmailLblId.setValue("Auto-Email Name : Default Message");
				previewMemExpEmailImgId.setVisible(true);
				editMemExpEmailImgId.setVisible(true);
				editMemExpEmailImgId.setAttribute("memEdit", null);
			}

			Long gftAmtExpEmailId = loyaltyAutoComm.getGiftAmtExpiryEmailTmpltId();
			if(gftAmtExpEmailId != null && gftAmtExpEmailId != -1 ) {
				CustomTemplates customTemplates=ltyPrgmSevice.getCustomTemplate(gftAmtExpEmailId);
				gftAmtExpEmailLblId.setValue("Auto-Email Name : "+(customTemplates!=null?customTemplates.getTemplateName():" Not Configured"));
				previewGftAmtExpEmailImgId.setVisible(true);
				editGftAmtExpEmailImgId.setVisible(true);
				editGftAmtExpEmailImgId.setAttribute("gftAmtEdit", customTemplates);
			}else if(gftAmtExpEmailId == null){
				gftAmtExpEmailLblId.setValue("Auto-Email Name : Not Configured");
				previewGftAmtExpEmailImgId.setVisible(false);
				editGftAmtExpEmailImgId.setVisible(false);
			}else {
				gftAmtExpEmailLblId.setValue("Auto-Email Name : Default Message");
				previewGftAmtExpEmailImgId.setVisible(true);
				editGftAmtExpEmailImgId.setVisible(true);
				editGftAmtExpEmailImgId.setAttribute("gftAmtEdit", null);
			}

			Long gftCrdExpEmailId = loyaltyAutoComm.getGiftMembrshpExpiryEmailTmpltId();
			if(gftCrdExpEmailId != null && gftCrdExpEmailId != -1 ) {
				CustomTemplates customTemplates=ltyPrgmSevice.getCustomTemplate(gftCrdExpEmailId);
				gftCardExpEmailLblId.setValue("Auto-Email Name : "+(customTemplates!=null?customTemplates.getTemplateName():" Not Configured"));
				previewGftcrdExpEmailImgId.setVisible(true);
				editGftcrdExpEmailImgId.setVisible(true);
				editGftcrdExpEmailImgId.setAttribute("gftCrdEdit", customTemplates);
			}else if(gftCrdExpEmailId == null){

				gftCardExpEmailLblId.setValue("Auto-Email Name : Not Configured");
				previewGftcrdExpEmailImgId.setVisible(false);
				editGftcrdExpEmailImgId.setVisible(false);
			}else {
				gftCardExpEmailLblId.setValue("Auto-Email Name : Default Message");
				previewGftcrdExpEmailImgId.setVisible(true);
				editGftcrdExpEmailImgId.setVisible(true);
				editGftcrdExpEmailImgId.setAttribute("gftCrdEdit", null);
			}


			Long gftCrdIssEmailId = loyaltyAutoComm.getGiftCardIssuanceEmailTmpltId();
			if(gftCrdIssEmailId != null && gftCrdIssEmailId != -1 ) {
				CustomTemplates customTemplates=ltyPrgmSevice.getCustomTemplate(gftCrdIssEmailId);
				gftCrdIssEmailLblId.setValue("Auto-Email Name : "+(customTemplates!=null?customTemplates.getTemplateName():" Not Configured"));
				previewGftCrdIssEmailImgId.setVisible(true);
				editGftCrdIssEmailImgId.setVisible(true);
				editGftCrdIssEmailImgId.setAttribute("gftCrdIssEdit", customTemplates);
			}else if(gftCrdIssEmailId == null){
				gftCrdIssEmailLblId.setValue("Auto-Email Name : Not Configured");
				previewGftCrdIssEmailImgId.setVisible(false);
				editGftCrdIssEmailImgId.setVisible(false);
			}else {
				gftCrdIssEmailLblId.setValue("Auto-Email Name : Default Message");
				previewGftCrdIssEmailImgId.setVisible(true);
				editGftCrdIssEmailImgId.setVisible(true);
				editGftCrdIssEmailImgId.setAttribute("gftCrdIssEdit", null);
			}
//loyaltyAdjEmailLblId,previewLoyaltyAdjEmailImgId,editLoyaltyAdjEmailImgId
			Long loyaltyAdjEmailTmpltId = loyaltyAutoComm.getAdjustmentAutoEmailTmplId();
			if(loyaltyAdjEmailTmpltId != null && loyaltyAdjEmailTmpltId != -1) {
				CustomTemplates customTemplates=ltyPrgmSevice.getCustomTemplate(loyaltyAdjEmailTmpltId);
				loyaltyAdjEmailLblId.setValue("Auto-Email Name : "+(customTemplates!=null?customTemplates.getTemplateName():" Not Configured"));
				previewLoyaltyAdjEmailImgId.setVisible(true);
				editLoyaltyAdjEmailImgId.setVisible(true);
				editLoyaltyAdjEmailImgId.setAttribute("loyaltyAdjEmailEdit", customTemplates);
			}else if(loyaltyAdjEmailTmpltId == null){
				loyaltyAdjEmailLblId.setValue("Auto-Email Name : Not Configured");
				previewLoyaltyAdjEmailImgId.setVisible(false);
				editLoyaltyAdjEmailImgId.setVisible(false);
			}else {
				loyaltyAdjEmailLblId.setValue("Auto-Email Name : Default Message");
				previewLoyaltyAdjEmailImgId.setVisible(true);
				editLoyaltyAdjEmailImgId.setVisible(true);
				editLoyaltyAdjEmailImgId.setAttribute("loyaltyAdjEmailEdit", null);
			}
			
			
			
			//Loyalty Issuance
			Long loyaltyIssuanceEmailTmpltId = loyaltyAutoComm.getIssuanceAutoEmailTmplId();
			if(loyaltyIssuanceEmailTmpltId != null && loyaltyIssuanceEmailTmpltId != -1) {
				CustomTemplates customTemplates=ltyPrgmSevice.getCustomTemplate(loyaltyIssuanceEmailTmpltId);
				loyaltyIssuanceEmailLblId.setValue("Auto-Email Name : "+(customTemplates!=null?customTemplates.getTemplateName():" Not Configured"));
				previewLoyaltyIssuanceEmailImgId.setVisible(true);
				editLoyaltyIssuanceEmailImgId.setVisible(true);
				editLoyaltyIssuanceEmailImgId.setAttribute("loyaltyIssuanceEmailEdit", customTemplates);
			}else if(loyaltyIssuanceEmailTmpltId == null){
				loyaltyIssuanceEmailLblId.setValue("Auto-Email Name : Not Configured");
				previewLoyaltyIssuanceEmailImgId.setVisible(false);
				editLoyaltyIssuanceEmailImgId.setVisible(false);
			}else {
				loyaltyIssuanceEmailLblId.setValue("Auto-Email Name : Default Message");
				previewLoyaltyIssuanceEmailImgId.setVisible(true);
				editLoyaltyIssuanceEmailImgId.setVisible(true);
				editLoyaltyIssuanceEmailImgId.setAttribute("loyaltyIssuanceEmailEdit", null);
			}
			
			//Loyalty redemption
			Long loyaltyRedemptionEmailTmpltId = loyaltyAutoComm.getRedemptionAutoEmailTmplId();
			if(loyaltyRedemptionEmailTmpltId != null && loyaltyRedemptionEmailTmpltId != -1) {
				CustomTemplates customTemplates=ltyPrgmSevice.getCustomTemplate(loyaltyRedemptionEmailTmpltId);
				loyaltyRedemptionEmailLblId.setValue("Auto-Email Name : "+(customTemplates!=null?customTemplates.getTemplateName():" Not Configured"));
				previewLoyaltyRedemptionEmailImgId.setVisible(true);
				editLoyaltyRedemptionEmailImgId.setVisible(true);
				editLoyaltyRedemptionEmailImgId.setAttribute("loyaltyRedemptionEmailEdit", customTemplates);
			}else if(loyaltyRedemptionEmailTmpltId == null){
				loyaltyRedemptionEmailLblId.setValue("Auto-Email Name : Not Configured");
				previewLoyaltyRedemptionEmailImgId.setVisible(false);
				editLoyaltyRedemptionEmailImgId.setVisible(false);
			}else {
				loyaltyRedemptionEmailLblId.setValue("Auto-Email Name : Default Message");
				previewLoyaltyRedemptionEmailImgId.setVisible(true);
				editLoyaltyRedemptionEmailImgId.setVisible(true);
				editLoyaltyRedemptionEmailImgId.setAttribute("loyaltyRedemptionEmailEdit", null);
			}
			
			//OTP Message
			Long otpMessageEmailTmpltId = loyaltyAutoComm.getOtpMessageAutoEmailTmplId();
			logger.info("value for email is"+otpMessageEmailTmpltId);
			if(otpMessageEmailTmpltId != null && otpMessageEmailTmpltId!= -1) {
				CustomTemplates customTemplates=ltyPrgmSevice.getCustomTemplate(otpMessageEmailTmpltId);
				otpMessageEmailLblId.setValue("Auto-Email Name : "+(customTemplates!=null?customTemplates.getTemplateName():" Not Configured"));
				previewOtpMessageEmailImgId.setVisible(true);
				editOtpMessageEmailImgId.setVisible(true);
				editOtpMessageEmailImgId.setAttribute("otpMessageEmailEdit", customTemplates);
			}else if(otpMessageEmailTmpltId == null){
				otpMessageEmailLblId.setValue("Auto-Email Name : Not Configured");
				previewOtpMessageEmailImgId.setVisible(false);
				editOtpMessageEmailImgId.setVisible(false);
			}else {
				otpMessageEmailLblId.setValue("Auto-Email Name : Default Message");
				previewOtpMessageEmailImgId.setVisible(true);
				editOtpMessageEmailImgId.setVisible(true);
				editOtpMessageEmailImgId.setAttribute("otpMessageEmailEdit", null);
			}
			
			
			
			Long redemptionOtpEmailTmpltId = loyaltyAutoComm.getRedemptionOtpAutoEmailTmplId();
			logger.info("value for email is"+redemptionOtpEmailTmpltId);
			if(redemptionOtpEmailTmpltId != null && redemptionOtpEmailTmpltId!= -1) {
				CustomTemplates customTemplates=ltyPrgmSevice.getCustomTemplate(redemptionOtpEmailTmpltId);
				
				redemptionOtpEmailLblId.setValue("Auto-Email Name : "+(customTemplates!=null?customTemplates.getTemplateName():" Not Configured"));
			
				previewRedemptionOtpEmailImgId.setVisible(true);
				editRedemptionOtpEmailImgId.setVisible(true);
				editRedemptionOtpEmailImgId.setAttribute("redemptionOtpEmailEdit", customTemplates);
			
			}else if(redemptionOtpEmailTmpltId == null){
				redemptionOtpEmailLblId.setValue("Auto-Email Name : Not Configured");
				previewRedemptionOtpEmailImgId.setVisible(false);
				editRedemptionOtpEmailImgId.setVisible(false);
			}else {
				redemptionOtpEmailLblId.setValue("Auto-Email Name : Default Message");
				previewRedemptionOtpEmailImgId.setVisible(true);
				editRedemptionOtpEmailImgId.setVisible(true);
				editRedemptionOtpEmailImgId.setAttribute("redemptionOtpEmailEdit", null);
			}
			
			
			
			
			//auto sms 

			Long regSmsTmpltId = loyaltyAutoComm.getRegSmsTmpltId();
			if(regSmsTmpltId != null && regSmsTmpltId != -1 ) {
				AutoSMS autoSMS = ltyPrgmSevice.getAutoSmsTemplateById(regSmsTmpltId);
				regSMSLblId.setValue("Auto-SMS Name : "+(autoSMS!=null?autoSMS.getTemplateName():" Not Configured"));
				previewRegSMSImgId.setVisible(true);
				editRegSMSImgId.setVisible(true);
				editRegSMSImgId.setAttribute("regSmsEdit", autoSMS);
			}else if(regSmsTmpltId == null){
				regSMSLblId.setValue("Auto-SMS Name : Not Configured");
				previewRegSMSImgId.setVisible(false);
				editRegSMSImgId.setVisible(false);
			}else {
				regSMSLblId.setValue("Auto-SMS Name : Default Message");
				previewRegSMSImgId.setVisible(true);
				editRegSMSImgId.setVisible(true);
				editRegSMSImgId.setAttribute("regSmsEdit", null);
			}

			Long giftCardIssuanceSmsTmpltId = loyaltyAutoComm.getGiftCardIssuanceSmsTmpltId();
			if(giftCardIssuanceSmsTmpltId != null && giftCardIssuanceSmsTmpltId != -1 ) {
				AutoSMS autoSMS = ltyPrgmSevice.getAutoSmsTemplateById(giftCardIssuanceSmsTmpltId);
				gftCrdIssSMSLblId.setValue("Auto-SMS Name : "+(autoSMS!=null?autoSMS.getTemplateName():" Not Configured"));
				previewGftCrdIssSMSImgId.setVisible(true);
				editGftCrdIssSMSImgId.setVisible(true);
				editGftCrdIssSMSImgId.setAttribute("gftIssSmsEdit", autoSMS);
			}else if(giftCardIssuanceSmsTmpltId == null){
				gftCrdIssSMSLblId.setValue("Auto-SMS Name : Not Configured");
				previewGftCrdIssSMSImgId.setVisible(false);
				editGftCrdIssSMSImgId.setVisible(false);
			}else {
				gftCrdIssSMSLblId.setValue("Auto-SMS Name : Default Message");
				previewGftCrdIssSMSImgId.setVisible(true);
				editGftCrdIssSMSImgId.setVisible(true);
				editGftCrdIssSMSImgId.setAttribute("gftIssSmsEdit", null);
			}


			Long tierUpgdSmsTmpltId = loyaltyAutoComm.getTierUpgdSmsTmpltId();
			if(tierUpgdSmsTmpltId != null && tierUpgdSmsTmpltId != -1 ) {
				AutoSMS autoSMS = ltyPrgmSevice.getAutoSmsTemplateById(tierUpgdSmsTmpltId);
				tierSMSLblId.setValue("Auto-SMS Name : "+(autoSMS!=null?autoSMS.getTemplateName():" Not Configured"));
				previewTierSMSImgId.setVisible(true);
				editTierSMSImgId.setVisible(true);
				editTierSMSImgId.setAttribute("tierUpgSmsEdit", autoSMS);
			}else if(tierUpgdSmsTmpltId == null){
				tierSMSLblId.setValue("Auto-SMS Name : Not Configured");
				previewTierSMSImgId.setVisible(false);
				editTierSMSImgId.setVisible(false);
			}else {
				tierSMSLblId.setValue("Auto-SMS Name : Default Message");
				previewTierSMSImgId.setVisible(true);
				editTierSMSImgId.setVisible(true);
				editTierSMSImgId.setAttribute("tierUpgSmsEdit", null);
			}

			Long threshBonusSmsTmpltId = loyaltyAutoComm.getThreshBonusSmsTmpltId();
			if(threshBonusSmsTmpltId != null && threshBonusSmsTmpltId != -1 ) {
				AutoSMS autoSMS = ltyPrgmSevice.getAutoSmsTemplateById(threshBonusSmsTmpltId);
				tresholdSMSLblId.setValue("Auto-SMS Name : "+(autoSMS!=null?autoSMS.getTemplateName():" Not Configured"));
				previewThresholdSMSImgId.setVisible(true);
				editThresholdSMSImgId.setVisible(true);
				editThresholdSMSImgId.setAttribute("bonusSmsEdit", autoSMS);
			}else if(threshBonusSmsTmpltId == null){
				tresholdSMSLblId.setValue("Auto-SMS Name : Not Configured");
				previewThresholdSMSImgId.setVisible(false);
				editThresholdSMSImgId.setVisible(false);
			}else {
				tresholdSMSLblId.setValue("Auto-SMS Name : Default Message");
				previewThresholdSMSImgId.setVisible(true);
				editThresholdSMSImgId.setVisible(true);
				editThresholdSMSImgId.setAttribute("bonusSmsEdit", null);
			}


			Long rewardExpirySmsTmpltId = loyaltyAutoComm.getRewardExpirySmsTmpltId();
			if(rewardExpirySmsTmpltId != null && rewardExpirySmsTmpltId != -1 ) {
				AutoSMS autoSMS = ltyPrgmSevice.getAutoSmsTemplateById(rewardExpirySmsTmpltId);
				rewExpSMSLblId.setValue("Auto-SMS Name : "+(autoSMS!=null?autoSMS.getTemplateName():" Not Configured"));
				previewRewExpSMSImgId.setVisible(true);
				editRewExpSMSImgId.setVisible(true);
				editRewExpSMSImgId.setAttribute("rewardExpSmsEdit", autoSMS);
			}else if(rewardExpirySmsTmpltId == null){
				rewExpSMSLblId.setValue("Auto-SMS Name : Not Configured");
				previewRewExpSMSImgId.setVisible(false);
				editRewExpSMSImgId.setVisible(false);
			}else {
				rewExpSMSLblId.setValue("Auto-SMS Name : Default Message");
				previewRewExpSMSImgId.setVisible(true);
				editRewExpSMSImgId.setVisible(true);
				editRewExpSMSImgId.setAttribute("rewardExpSmsEdit", null);
			}


			Long mbrshipExpirySmsTmpltId = loyaltyAutoComm.getMbrshipExpirySmsTmpltId();
			if(mbrshipExpirySmsTmpltId != null && mbrshipExpirySmsTmpltId != -1 ) {
				AutoSMS autoSMS = ltyPrgmSevice.getAutoSmsTemplateById(mbrshipExpirySmsTmpltId);
				memExpSMSLblId.setValue("Auto-SMS Name : "+(autoSMS!=null?autoSMS.getTemplateName():" Not Configured"));
				previewMemExpSMSImgId.setVisible(true);
				editMemExpSMSImgId.setVisible(true);
				editMemExpSMSImgId.setAttribute("memExpSmsEdit", autoSMS);
			}else if(mbrshipExpirySmsTmpltId == null){
				memExpSMSLblId.setValue("Auto-SMS Name : Not Configured");
				previewMemExpSMSImgId.setVisible(false);
				editMemExpSMSImgId.setVisible(false);
			}else {
				memExpSMSLblId.setValue("Auto-SMS Name : Default Message");
				previewMemExpSMSImgId.setVisible(true);
				editMemExpSMSImgId.setVisible(true);
				editMemExpSMSImgId.setAttribute("memExpSmsEdit", null);
			}


			Long giftAmtExpirySmsTmpltId = loyaltyAutoComm.getGiftAmtExpirySmsTmpltId();
			if(giftAmtExpirySmsTmpltId != null && giftAmtExpirySmsTmpltId != -1 ) {
				AutoSMS autoSMS = ltyPrgmSevice.getAutoSmsTemplateById(giftAmtExpirySmsTmpltId);
				gftAmtExpSMSLblId.setValue("Auto-SMS Name : "+(autoSMS!=null?autoSMS.getTemplateName():" Not Configured"));
				previewGftAmtExpSMSImgId.setVisible(true);
				editGftAmtExpSMSImgId.setVisible(true);
				editGftAmtExpSMSImgId.setAttribute("gftAmtExpSmsEdit", autoSMS);
			}else if(giftAmtExpirySmsTmpltId == null){
				gftAmtExpSMSLblId.setValue("Auto-SMS Name : Not Configured");
				previewGftAmtExpSMSImgId.setVisible(false);
				editGftAmtExpSMSImgId.setVisible(false);
			}else {
				gftAmtExpSMSLblId.setValue("Auto-SMS Name : Default Message");
				previewGftAmtExpSMSImgId.setVisible(true);
				editGftAmtExpSMSImgId.setVisible(true);
				editGftAmtExpSMSImgId.setAttribute("gftAmtExpSmsEdit", null);
			}


			Long giftMembrshpExpirySmsTmpltId = loyaltyAutoComm.getGiftMembrshpExpirySmsTmpltId();
			if(giftMembrshpExpirySmsTmpltId != null && giftMembrshpExpirySmsTmpltId != -1 ) {
				AutoSMS autoSMS = ltyPrgmSevice.getAutoSmsTemplateById(giftMembrshpExpirySmsTmpltId);
				gftCardExpSMSLblId.setValue("Auto-SMS Name : "+(autoSMS!=null?autoSMS.getTemplateName():" Not Configured"));
				previewGftcrdExpSMSImgId.setVisible(true);
				editGftcrdExpSMSImgId.setVisible(true);
				editGftcrdExpSMSImgId.setAttribute("gftCardExpSmsEdit", autoSMS);
			}else if(giftMembrshpExpirySmsTmpltId == null){
				gftCardExpSMSLblId.setValue("Auto-SMS Name : Not Configured");
				previewGftcrdExpSMSImgId.setVisible(false);
				editGftcrdExpSMSImgId.setVisible(false);
			}else {
				gftCardExpSMSLblId.setValue("Auto-SMS Name : Default Message");
				previewGftcrdExpSMSImgId.setVisible(true);
				editGftcrdExpSMSImgId.setVisible(true);
				editGftcrdExpSMSImgId.setAttribute("gftCardExpSmsEdit", null);
			}
			
			//loyaltyAdjSmsLblId,previewLoyaltyAdjSMSImgId,editLoyaltyAdjSMSImgId
			Long loyaltyAdjustmentSmsTmpltId = loyaltyAutoComm.getAdjustmentAutoSmsTmplId();
			if(loyaltyAdjustmentSmsTmpltId != null && loyaltyAdjustmentSmsTmpltId != -1 ) {
				AutoSMS autoSMS = ltyPrgmSevice.getAutoSmsTemplateById(loyaltyAdjustmentSmsTmpltId);
				loyaltyAdjSmsLblId.setValue("Auto-SMS Name : "+(autoSMS!=null?autoSMS.getTemplateName():" Not Configured"));
				previewLoyaltyAdjSMSImgId.setVisible(true);
				editLoyaltyAdjSMSImgId.setVisible(true);
				editLoyaltyAdjSMSImgId.setAttribute("loyaltyAdjSmsEdit", autoSMS);
			}else if(loyaltyAdjustmentSmsTmpltId == null){
				loyaltyAdjSmsLblId.setValue("Auto-SMS Name : Not Configured");
				previewLoyaltyAdjSMSImgId.setVisible(false);
				editLoyaltyAdjSMSImgId.setVisible(false);
			}else {
				loyaltyAdjSmsLblId.setValue("Auto-SMS Name : Default Message");
				previewLoyaltyAdjSMSImgId.setVisible(true);
				editLoyaltyAdjSMSImgId.setVisible(true);
				editLoyaltyAdjSMSImgId.setAttribute("loyaltyAdjSmsEdit", null);
			}
			// Loyalty Issuance SMS
			Long loyaltyIssuanceSmsTmpltId = loyaltyAutoComm.getIssuanceAutoSmsTmplId();
			if(loyaltyIssuanceSmsTmpltId != null && loyaltyIssuanceSmsTmpltId != -1 ) {
				AutoSMS autoSMS = ltyPrgmSevice.getAutoSmsTemplateById(loyaltyIssuanceSmsTmpltId);
				loyaltyIssuanceSmsLblId.setValue("Auto-SMS Name : "+(autoSMS!=null?autoSMS.getTemplateName():" Not Configured"));
				previewLoyaltyIssuanceSMSImgId.setVisible(true);
				editLoyaltyIssuanceSMSImgId.setVisible(true);
				editLoyaltyIssuanceSMSImgId.setAttribute("loyaltyIssuanceSmsEdit", autoSMS);
			}else if(loyaltyIssuanceSmsTmpltId == null){
				loyaltyIssuanceSmsLblId.setValue("Auto-SMS Name : Not Configured");
				previewLoyaltyIssuanceSMSImgId.setVisible(false);
				editLoyaltyIssuanceSMSImgId.setVisible(false);
			}else {
				loyaltyIssuanceSmsLblId.setValue("Auto-SMS Name : Default Message");
				previewLoyaltyIssuanceSMSImgId.setVisible(true);
				editLoyaltyIssuanceSMSImgId.setVisible(true);
				editLoyaltyIssuanceSMSImgId.setAttribute("loyaltyIssuanceSmsEdit", null);
			}
			
			Long loyaltyRedemptionSmsTmpltId = loyaltyAutoComm.getRedemptionAutoSmsTmplId();
			if(loyaltyRedemptionSmsTmpltId != null && loyaltyRedemptionSmsTmpltId != -1 ) {
				AutoSMS autoSMS = ltyPrgmSevice.getAutoSmsTemplateById(loyaltyRedemptionSmsTmpltId);
				loyaltyRedemptionSmsLblId.setValue("Auto-SMS Name : "+(autoSMS!=null?autoSMS.getTemplateName():" Not Configured"));
				previewLoyaltyRedemptionSMSImgId.setVisible(true);
				editLoyaltyRedemptionSMSImgId.setVisible(true);
				editLoyaltyRedemptionSMSImgId.setAttribute("loyaltyRedemptionSmsEdit", autoSMS);
			}else if(loyaltyRedemptionSmsTmpltId == null){
				loyaltyRedemptionSmsLblId.setValue("Auto-SMS Name : Not Configured");
				previewLoyaltyRedemptionSMSImgId.setVisible(false);
				editLoyaltyRedemptionSMSImgId.setVisible(false);
			}else {
				loyaltyRedemptionSmsLblId.setValue("Auto-SMS Name : Default Message");
				previewLoyaltyRedemptionSMSImgId.setVisible(true);
				editLoyaltyRedemptionSMSImgId.setVisible(true);
				editLoyaltyRedemptionSMSImgId.setAttribute("loyaltyRedemptionSmsEdit", null);
			}

			Long otpMessageSmsTmpltId = loyaltyAutoComm.getOtpMessageAutoSmsTmpltId();
			logger.info("value is "+otpMessageSmsTmpltId);
			if(otpMessageSmsTmpltId != null && otpMessageSmsTmpltId != -1 ) {
				AutoSMS autoSMS = ltyPrgmSevice.getAutoSmsTemplateById(otpMessageSmsTmpltId);
				otpMessageSmsLblId.setValue("Auto-SMS Name : "+(autoSMS!=null?autoSMS.getTemplateName():" Not Configured"));
				previewOtpMessageSMSImgId.setVisible(true);
				editOtpMessageSMSImgId.setVisible(true);
				editOtpMessageSMSImgId.setAttribute("otpMessageSmsEdit", autoSMS);
			}else if(otpMessageSmsTmpltId == null){
				otpMessageSmsLblId.setValue("Auto-SMS Name : Not Configured");
				previewOtpMessageSMSImgId.setVisible(false);
				editOtpMessageSMSImgId.setVisible(false);
			}else {
				otpMessageSmsLblId.setValue("Auto-SMS Name : Default Message");
				previewOtpMessageSMSImgId.setVisible(true);
				editOtpMessageSMSImgId.setVisible(true);
				editOtpMessageSMSImgId.setAttribute("otpMessageSmsEdit", null);
			}
		
		
			Long redemptionOtpSmsTmpltId = loyaltyAutoComm.getRedemptionOtpAutoSmsTmpltId();
			logger.info("value is "+redemptionOtpSmsTmpltId);
			if(redemptionOtpSmsTmpltId != null && redemptionOtpSmsTmpltId!= -1 ) {
				AutoSMS autoSMS = ltyPrgmSevice.getAutoSmsTemplateById(redemptionOtpSmsTmpltId);
			
				redemptionOtpSmsLblId.setValue("Auto-SMS Name : "+(autoSMS!=null?autoSMS.getTemplateName():" Not Configured"));
				previewRedemptionOtpSMSImgId.setVisible(true);
				editRedemptionOtpSMSImgId.setVisible(true);
				editRedemptionOtpSMSImgId.setAttribute("redemptionOtpSmsEdit", autoSMS);
			}else if(redemptionOtpSmsTmpltId == null){
				
				redemptionOtpSmsLblId.setValue("Auto-SMS Name : Not Configured");
				previewRedemptionOtpSMSImgId.setVisible(false);
				editRedemptionOtpSMSImgId.setVisible(false);
			}else {
				redemptionOtpSmsLblId.setValue("Auto-SMS Name : Default Message");
				previewRedemptionOtpSMSImgId.setVisible(true);
				editRedemptionOtpSMSImgId.setVisible(true);
				editRedemptionOtpSMSImgId.setAttribute("redemptionOtpSmsEdit", null);
			}
		
		
		
		
		
		
		
		
		
		
		}else {
			regEmailLblId.setValue("Auto-Email Name : Not Configured");
			previewRegEmailImgId.setVisible(false);
			editRegEmailImgId.setVisible(false);
			tierEmailLblId.setValue("Auto-Email Name : Not Configured");
			previewTierEmailImgId.setVisible(false);
			editTierEmailImgId.setVisible(false);
			thresholdEmailLblId.setValue("Auto-Email Name : Not Configured");
			previewThresholdEmailImgId.setVisible(false);
			editThresholdEmailImgId.setVisible(false);
			rewExpEmailLblId.setValue("Auto-Email Name : Not Configured");
			previewRewExpEmailImgId.setVisible(false);
			editRewExpEmailImgId.setVisible(false);
			memExpEmailLblId.setValue("Auto-Email Name : Not Configured");
			previewMemExpEmailImgId.setVisible(false);
			editMemExpEmailImgId.setVisible(false);
			gftAmtExpEmailLblId.setValue("Auto-Email Name : Not Configured");
			previewGftAmtExpEmailImgId.setVisible(false);
			editGftAmtExpEmailImgId.setVisible(false);
			gftCardExpEmailLblId.setValue("Auto-Email Name : Not Configured");
			previewGftcrdExpEmailImgId.setVisible(false);
			editGftcrdExpEmailImgId.setVisible(false);
			gftCrdIssEmailLblId.setValue("Auto-Email Name : Not Configured");
			previewGftCrdIssEmailImgId.setVisible(false);
			editGftCrdIssEmailImgId.setVisible(false);
			loyaltyAdjEmailLblId.setValue("Auto-Email Name : Not Configured");
			previewLoyaltyAdjEmailImgId.setVisible(false);
			editLoyaltyAdjEmailImgId.setVisible(false);
			loyaltyIssuanceEmailLblId.setValue("Auto-Email Name : Not Configured");
			previewLoyaltyIssuanceEmailImgId.setVisible(false);
			editLoyaltyIssuanceEmailImgId.setVisible(false);
			loyaltyRedemptionEmailLblId.setValue("Auto-Email Name : Not Configured");
			previewLoyaltyRedemptionEmailImgId.setVisible(false);
			editLoyaltyRedemptionEmailImgId.setVisible(false);
			otpMessageEmailLblId.setValue("Auto-Email Name : Not Configured");
			previewOtpMessageEmailImgId.setVisible(false);
			editOtpMessageEmailImgId.setVisible(false);
			
			redemptionOtpEmailLblId.setValue("Auto-Email Name : Not Configured");
			previewRedemptionOtpEmailImgId.setVisible(false);
			editRedemptionOtpEmailImgId.setVisible(false);
			
			

			//Auto-SMS
			regSMSLblId.setValue("Auto-SMS Name : Not Configured");
			previewRegSMSImgId.setVisible(false);
			editRegSMSImgId.setVisible(false);
			gftCrdIssSMSLblId.setValue("Auto-SMS Name : Not Configured");
			previewGftCrdIssSMSImgId.setVisible(false);
			editGftCrdIssSMSImgId.setVisible(false);
			tierSMSLblId.setValue("Auto-SMS Name : Not Configured");
			previewTierSMSImgId.setVisible(false);
			editTierSMSImgId.setVisible(false);
			tresholdSMSLblId.setValue("Auto-SMS Name : Not Configured");
			previewThresholdSMSImgId.setVisible(false);
			editThresholdSMSImgId.setVisible(false);
			rewExpSMSLblId.setValue("Auto-SMS Name : Not Configured");
			previewRewExpSMSImgId.setVisible(false);
			editRewExpSMSImgId.setVisible(false);
			memExpSMSLblId.setValue("Auto-SMS Name : Not Configured");
			previewMemExpSMSImgId.setVisible(false);
			editMemExpSMSImgId.setVisible(false);
			gftAmtExpSMSLblId.setValue("Auto-SMS Name : Not Configured");
			previewGftAmtExpSMSImgId.setVisible(false);
			editGftAmtExpSMSImgId.setVisible(false);
			gftCardExpSMSLblId.setValue("Auto-SMS Name : Not Configured");
			previewGftcrdExpSMSImgId.setVisible(false);
			editGftcrdExpSMSImgId.setVisible(false);
			loyaltyAdjSmsLblId.setValue("Auto-SMS Name : Not Configured");
			previewLoyaltyAdjSMSImgId.setVisible(false);
			editLoyaltyAdjSMSImgId.setVisible(false);
			loyaltyIssuanceSmsLblId.setValue("Auto-SMS Name : Not Configured");
			previewLoyaltyIssuanceSMSImgId.setVisible(false);
			editLoyaltyIssuanceSMSImgId.setVisible(false);
			loyaltyRedemptionSmsLblId.setValue("Auto-SMS Name : Not Configured");
			previewLoyaltyRedemptionSMSImgId.setVisible(false);
			editLoyaltyRedemptionSMSImgId.setVisible(false);
			otpMessageSmsLblId.setValue("Auto-SMS Name : Not Configured");
			previewOtpMessageSMSImgId.setVisible(false);
			editOtpMessageSMSImgId.setVisible(false);
		
			redemptionOtpSmsLblId.setValue("Auto-SMS Name : Not Configured");
			previewRedemptionOtpSMSImgId.setVisible(false);
			editRedemptionOtpSMSImgId.setVisible(false);
		
		
		
		
		}
	}

	public void onClick$editAutoCommAnchId() {
		Redirect.goTo(PageListEnum.LOYALTY_AUTO_COMMUNICATION);
	}


	private void previewEmailTemplate(Image imgId, String editType, String defaultMsgType) {
		String templateContent = "";

		CustomTemplates customTemplates = (CustomTemplates) imgId.getAttribute(editType);
		if(customTemplates == null) {
			templateContent = 	PropertyUtil.getPropertyValueFromDB(defaultMsgType);
		}else {
			if(customTemplates.getHtmlText() != null && !customTemplates.getHtmlText().isEmpty()) {
				templateContent = customTemplates.getHtmlText();
			}else if(customTemplates.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_BEE) && customTemplates.getMyTemplateId()!= null) {
				 MyTemplatesDao myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
				 MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(customTemplates.getMyTemplateId());
				 if(myTemplates!=null) {
					 templateContent = myTemplates.getContent();
				 }else {
					 MessageUtil.setMessage("No template was found configured in this auto-email message. Please edit the message to add a template to it.", "color:red", "TOP");
					 return;
				 }
			}
		}
		Utility.showPreview(previewWin$html, user.getUserName(), templateContent);
		previewWin.setVisible(true);

	}

	private void editEmailTemplate(Image imgId, String editType,String tempType) {

		logger.info("in edit mode--------------------");
		logger.info("custom template value "+imgId.getAttribute(editType));
		session.setAttribute("editCustomTemplate", imgId.getAttribute(editType));
		session.setAttribute("Mode", "edit");
		session.setAttribute("typeOfEmail",tempType);
		session.setAttribute("fromAddNewBtn","loyalty/loyaltyAutoCommunication");
		Redirect.goTo(PageListEnum.CONTACT_MANAGE_AUTO_EMAILS_BEE);
	}


	public void onClick$previewRegEmailImgId() {

		previewEmailTemplate(editRegEmailImgId,"regEdit",OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_REGISTRATION);

	}

	public void onClick$editRegEmailImgId() {

		editEmailTemplate(editRegEmailImgId,"regEdit",Constants.CUSTOM_TEMPLATE_TYPE_LOYALTYOPTIN);

	}


	public void onClick$previewTierEmailImgId() {

		previewEmailTemplate(editTierEmailImgId,"tierEdit",OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_TIERUPGRADE);
	}

	public void onClick$editTierEmailImgId() {
		editEmailTemplate(editTierEmailImgId,"tierEdit",Constants.CUSTOM_TEMPLATE_TYPE_TIER_UPGRADATION);
	}

	public void onClick$previewThresholdEmailImgId() {
		previewEmailTemplate(editThresholdEmailImgId,"thresholdBonusEdit",OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_BONUS);
	}

	public void onClick$editThresholdEmailImgId() {
		editEmailTemplate(editThresholdEmailImgId,"thresholdBonusEdit",Constants.CUSTOM_TEMPLATE_TYPE_EARNED_BONUS);
	}

	public void onClick$previewRewExpEmailImgId() {
		previewEmailTemplate(editRewExpEmailImgId,"rewExpEdit",OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_REWARDAMTEXPIRY);

	}

	public void onClick$editRewExpEmailImgId() {

		editEmailTemplate(editRewExpEmailImgId,"rewExpEdit",Constants.CUSTOM_TEMPLATE_TYPE_EARNED_REWARD_EXPIRATION);
	}

	public void onClick$previewMemExpEmailImgId() {
		previewEmailTemplate(editMemExpEmailImgId,"memEdit",OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_LOYALTYMEMBSHIPEXPIRY);
	}

	public void onClick$editMemExpEmailImgId() {
		editEmailTemplate(editMemExpEmailImgId,"memEdit",Constants.CUSTOM_TEMPLATE_TYPE_MEMBERSHIP_EXPIRATION);
	}

	public void onClick$previewGftAmtExpEmailImgId() {
		previewEmailTemplate(editGftAmtExpEmailImgId,"gftAmtEdit",OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_GIFTAMTEXPIRY);
	}

	public void onClick$editGftAmtExpEmailImgId() {
		editEmailTemplate(editGftAmtExpEmailImgId,"gftAmtEdit",Constants.CUSTOM_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION);
	}

	public void onClick$previewGftcrdExpEmailImgId() {
		previewEmailTemplate(editGftcrdExpEmailImgId,"gftCrdEdit",OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_GIFTMEMBSHIPEXPIRY);
	}

	public void onClick$editGftcrdExpEmailImgId() {
		editEmailTemplate(editGftcrdExpEmailImgId,"gftCrdEdit",Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION);
	}
	

	public void onClick$previewGftCrdIssEmailImgId() {
		previewEmailTemplate(editGftCrdIssEmailImgId,"gftCrdIssEdit",OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_GIFTISSUE);
	}

	public void onClick$editGftCrdIssEmailImgId() {
		editEmailTemplate(editGftCrdIssEmailImgId,"gftCrdIssEdit",Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_ISSUANCE);
	}
	
	public void onClick$previewLoyaltyAdjEmailImgId() {
		previewEmailTemplate(editLoyaltyAdjEmailImgId,"loyaltyAdjEmailEdit",OCConstants.LOYALTYADJUSTMENT);
	}

	public void onClick$editLoyaltyAdjEmailImgId() {
		editEmailTemplate(editLoyaltyAdjEmailImgId,"loyaltyAdjEmailEdit",Constants.CUSTOM_TEMPLATE_TYPE_LOYALTY_ADJUSTMENT);
	}
	
	public void onClick$previewLoyaltyIssuanceEmailImgId() {
		previewEmailTemplate(editLoyaltyIssuanceEmailImgId,"loyaltyIssuanceEmailEdit",OCConstants.LOYALTYISSUANCE);
	}

	public void onClick$editLoyaltyIssuanceEmailImgId() {
		editEmailTemplate(editLoyaltyIssuanceEmailImgId,"loyaltyIssuanceEmailEdit",Constants.CUSTOM_TEMPLATE_TYPE_LOYALTY_ISSUANCE);
	}
	public void onClick$previewLoyaltyRedemptionEmailImgId() {
		previewEmailTemplate(editLoyaltyRedemptionEmailImgId,"loyaltyRedemptionEmailEdit",OCConstants.LOYALTYREDEMPTION);
	}

	public void onClick$editLoyaltyRedemptionEmailImgId() {
		editEmailTemplate(editLoyaltyRedemptionEmailImgId,"loyaltyRedemptionEmailEdit",Constants.CUSTOM_TEMPLATE_TYPE_LOYALTY_REDEMPTION);
	}

	public void onClick$previewOtpMessageEmailImgId() {
		previewEmailTemplate(editOtpMessageEmailImgId,"otpMessageEmailEdit",OCConstants.OTPMESSAGES);
	}

	public void onClick$editOtpMessageEmailImgId() {
		editEmailTemplate(editOtpMessageEmailImgId,"otpMessageEmailEdit",Constants.CUSTOM_TEMPLATE_TYPE_OTP_MESSAGE);
	}


	public void onClick$previewRedemptionOtpEmailImgId() {
		
		previewEmailTemplate(editRedemptionOtpEmailImgId,"redemptionOtpEmailEdit",OCConstants.REDEMPTIONOTP);
	}

	public void onClick$editRedemptionOtpEmailImgId() {
		
		editEmailTemplate(editRedemptionOtpEmailImgId,"redemptionOtpEmailEdit",Constants.CUSTOM_TEMPLATE_TYPE_REDEMPTION_OTP);
	}
	
	
	
	
	
	
	
	
	//Auto-SMS

	public void onClick$previewRegSMSImgId() {

		priviewSmsTemplate(editRegSMSImgId,"regSmsEdit",OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_REGISTRATION);
	}

	public void onClick$editRegSMSImgId() {

		editSmsTemplate(editRegSMSImgId,"regSmsEdit",OCConstants.AUTO_SMS_TEMPLATE_TYPE_LOYALTY_REGISTRATION);

	}	

	public void onClick$previewGftCrdIssSMSImgId() {

		priviewSmsTemplate(editGftCrdIssSMSImgId,"gftIssSmsEdit",OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_GIFTISSUE);
	}

	public void onClick$editGftCrdIssSMSImgId() {

		editSmsTemplate(editGftCrdIssSMSImgId,"gftIssSmsEdit",OCConstants.AUTO_SMS_TEMPLATE_TYPE_GIFT_CARD_ISSUANCE);

	}


	public void onClick$previewTierSMSImgId() {

		priviewSmsTemplate(editTierSMSImgId,"tierUpgSmsEdit",OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_TIERUPGRADE);
	}

	public void onClick$editTierSMSImgId() {

		editSmsTemplate(editTierSMSImgId,"tierUpgSmsEdit",OCConstants.AUTO_SMS_TEMPLATE_TYPE_TIER_UPGRADATION);
	}

	public void onClick$previewThresholdSMSImgId() {

		priviewSmsTemplate(editThresholdSMSImgId,"bonusSmsEdit",OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_BONUS);

	}

	public void onClick$editThresholdSMSImgId() {

		editSmsTemplate(editThresholdSMSImgId,"bonusSmsEdit",OCConstants.AUTO_SMS_TEMPLATE_TYPE_EARNED_BONUS);

	}

	public void onClick$previewRewExpSMSImgId() {

		priviewSmsTemplate(editRewExpSMSImgId,"rewardExpSmsEdit",OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_REWARDAMTEXPIRY);

	}

	public void onClick$editRewExpSMSImgId() {

		editSmsTemplate(editRewExpSMSImgId,"rewardExpSmsEdit",OCConstants.AUTO_SMS_TEMPLATE_TYPE_EARNED_REWARD_EXPIRATION);
	}

	public void onClick$previewMemExpSMSImgId() {

		priviewSmsTemplate(editMemExpSMSImgId,"memExpSmsEdit",OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_LOYALTYMEMBSHIPEXPIRY);

	}

	public void onClick$editMemExpSMSImgId() {

		editSmsTemplate(editMemExpSMSImgId,"memExpSmsEdit",OCConstants.AUTO_SMS_TEMPLATE_TYPE_MEMBERSHIP_EXPIRATION);
	}


	public void onClick$previewGftAmtExpSMSImgId() {

		priviewSmsTemplate(editGftAmtExpSMSImgId,"gftAmtExpSmsEdit",OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_GIFTAMTEXPIRY);

	}

	public void onClick$editGftAmtExpSMSImgId() {

		editSmsTemplate(editGftAmtExpSMSImgId,"gftAmtExpSmsEdit",OCConstants.AUTO_SMS_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION);
	}

	public void onClick$previewGftcrdExpSMSImgId() {

		priviewSmsTemplate(editGftcrdExpSMSImgId,"gftCardExpSmsEdit",OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_GIFTMEMBSHIPEXPIRY);

	}

	public void onClick$editGftcrdExpSMSImgId() {

		editSmsTemplate(editGftcrdExpSMSImgId,"gftCardExpSmsEdit",OCConstants.AUTO_SMS_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION);

	}
	
	//loyaltyAdjEmailLblId,previewLoyaltyAdjEmailImgId,editLoyaltyAdjEmailImgId
		//loyaltyAdjSmsLblId,previewLoyaltyAdjSMSImgId,editLoyaltyAdjSMSImgId
	
	
	public void onClick$previewLoyaltyAdjSMSImgId() {

		priviewSmsTemplate(editLoyaltyAdjSMSImgId,"loyaltyAdjSmsEdit",OCConstants.LOYALTYADJUSTMENT);

	}

	public void onClick$editLoyaltyAdjSMSImgId() {

		editSmsTemplate(editLoyaltyAdjSMSImgId,"loyaltyAdjSmsEdit",Constants.CUSTOM_TEMPLATE_TYPE_LOYALTY_ADJUSTMENT);

	}
	public void onClick$previewLoyaltyIssuanceSMSImgId() {

		priviewSmsTemplate(editLoyaltyIssuanceSMSImgId,"loyaltyIssuanceSmsEdit",OCConstants.LOYALTYISSUANCE);

	}

	public void onClick$editLoyaltyIssuanceSMSImgId() {

		editSmsTemplate(editLoyaltyIssuanceSMSImgId,"loyaltyIssuanceSmsEdit",Constants.CUSTOM_TEMPLATE_TYPE_LOYALTY_ISSUANCE);

	}
	public void onClick$previewLoyaltyRedemptionSMSImgId() {

		priviewSmsTemplate(editLoyaltyRedemptionSMSImgId,"loyaltyRedemptionSmsEdit",OCConstants.LOYALTYREDEMPTION);

	}

	public void onClick$editLoyaltyRedemptionSMSImgId() {

		editSmsTemplate(editLoyaltyRedemptionSMSImgId,"loyaltyRedemptionSmsEdit",Constants.CUSTOM_TEMPLATE_TYPE_LOYALTY_REDEMPTION);

	}
	public void onClick$previewOtpMessageSMSImgId() {

		priviewSmsTemplate(editOtpMessageSMSImgId,"otpMessageSmsEdit",OCConstants.OTPMESSAGES);

	}
	public void onClick$editOtpMessageSMSImgId() {

		editSmsTemplate(editOtpMessageSMSImgId,"otpMessageSmsEdit",Constants.CUSTOM_TEMPLATE_TYPE_OTP_MESSAGE);

	}
	
	
	public void onClick$previewRedemptionOtpSMSImgId() {

		priviewSmsTemplate(editRedemptionOtpSMSImgId,"redemptionOtpSmsEdit",OCConstants.REDEMPTIONOTP);

	}
	public void onClick$editRedemptionOtpSMSImgId() {

		editSmsTemplate(editRedemptionOtpSMSImgId,"redemptionOtpSmsEdit",Constants.CUSTOM_TEMPLATE_TYPE_REDEMPTION_OTP);

	}
	
	
	
	
	
	
	
	//previewOtpMessageSMSImgId.setVisible(true);
	//editOtpMessageSMSImgId.setVisible(true);
	//editOtpMessageSMSImgId.


	private void editSmsTemplate(Image imgId, String editType,String tempType) {
		AutoSMS autoSMS = (AutoSMS)imgId.getAttribute(editType);
		session.setAttribute("editSmsTemplate", autoSMS);
		session.setAttribute("SmsMode", "edit");
		session.setAttribute("typeOfSms",tempType);
		session.setAttribute("fromAddNewBtn","loyalty/loyaltyAutoCommunication");
		Redirect.goTo(PageListEnum.CONTACT_MANAGE_AUTO_SMS);

	}

	private void priviewSmsTemplate(Image imgId, String editType,String defaultMsgType) {

		String templateContent = "";

		AutoSMS autoSMS = (AutoSMS)imgId.getAttribute(editType);
		if(autoSMS == null) {
			templateContent = 	PropertyUtil.getPropertyValueFromDB(defaultMsgType);
		}else {
			templateContent = autoSMS.getMessageContent();
		}
		logger.info("templateContent 1----"+templateContent);

		Utility.showPreview(previewWin$html, user.getUserName(), templateContent);
		previewWin.setVisible(true);

	}

	private void loadBasicSettings(LoyaltyProgram prgmObj) {
		prgmNameLblId.setValue(prgmObj.getProgramName());
		prgmDescLblId.setValue(prgmObj.getDescription() == null ? "No description provided" : prgmObj.getDescription());
		if(prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
			prgmTypeLblId.setValue("Mobile-based");

		}
		else if(prgmObj.getProgramType().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_TYPE_DYNAMIC)) {
			prgmTypeLblId.setValue("Custom-based");
			ltyPrgmTypeDivId.setVisible(true);
			enableUniqueMblNoLblId.setValue(prgmObj.getUniqueMobileFlag() == OCConstants.FLAG_YES ? "Yes" : "No");
			enableUniqueEmailLblId.setValue(prgmObj.getUniqueEmailFlag() == OCConstants.FLAG_YES ? "Yes" : "No");
			enableDefaultLblId.setVisible(false);
			defaultPrgmId.setVisible(false);
		}else{
			prgmTypeLblId.setValue("Card-based");
			ltyPrgmTypeDivId.setVisible(true);
			enableUniqueMblNoLblId.setValue(prgmObj.getUniqueMobileFlag() == OCConstants.FLAG_YES ? "Yes" : "No");
			enableUniqueEmailLblId.setValue(prgmObj.getUniqueEmailFlag() == OCConstants.FLAG_YES ? "Yes" : "No");
			enableDefaultLblId.setVisible(true);
			defaultPrgmId.setVisible(true);
			enableDefaultLblId.setValue(prgmObj.getDefaultFlag() == OCConstants.FLAG_YES ? "Yes" : "No");
		}
		if(prgmObj.getTierEnableFlag() == OCConstants.FLAG_YES) {
			enableTierLblId.setValue("Yes" + " (" + prgmObj.getNoOfTiers() + " Tiers)");
		}
		else {
			enableTierLblId.setValue("No");
		}
		//		prgmStatusLblId.setValue(prgmObj.getStatus());
	}

	private void loadTransactionalSettings(LoyaltyProgram prgmObj) {
		String regReqStr = prgmObj.getRegRequisites();
		String selectedFielsStr = "";

		if(regReqStr != null && !regReqStr.isEmpty()) {
			String[] regReqArr = regReqStr.split(Constants.ADDR_COL_DELIMETER);
			for (String regReq : regReqArr) {
				if(regReq.equalsIgnoreCase(OCConstants.LOYALTY_REG_REQUISITE_FIRSTNAME)) {
					regReq = "First Name";
				}
				else if(regReq.equalsIgnoreCase(OCConstants.LOYALTY_REG_REQUISITE_LASTNAME)){
					regReq = "Last Name";
				}
				else if(regReq.equalsIgnoreCase(OCConstants.LOYALTY_REG_REQUISITE_EMAILID)){
					regReq = "Email Address";
				}
				else if(regReq.equalsIgnoreCase(OCConstants.LOYALTY_REG_REQUISITE_MOBILEPHONE)){
					regReq = "Mobile Number";
				}
				else if(regReq.equalsIgnoreCase(OCConstants.LOYALTY_REG_REQUISITE_ADDRESSONE)){
					regReq = "Street";
				}
				else if(regReq.equalsIgnoreCase(OCConstants.LOYALTY_REG_REQUISITE_CITY)){
					regReq = "City";
				}
				else if(regReq.equalsIgnoreCase(OCConstants.LOYALTY_REG_REQUISITE_STATE)){
					regReq = "State";
				}
				else if(regReq.equalsIgnoreCase(OCConstants.LOYALTY_REG_REQUISITE_ZIP)){
					regReq = "Postal Code";
				}
				else if(regReq.equalsIgnoreCase(OCConstants.LOYALTY_REG_REQUISITE_COUNTRY)){
					regReq = "Country";
				}
				else if(regReq.equalsIgnoreCase(OCConstants.LOYALTY_REG_REQUISITE_BIRTHDAY)){
					regReq = "Birthday";
				}
				else if(regReq.equalsIgnoreCase(OCConstants.LOYALTY_REG_REQUISITE_ANNIVERSARY)){
					regReq = "Anniversary";
				}
				else if(regReq.equalsIgnoreCase(OCConstants.LOYALTY_REG_REQUISITE_GENDER)){
					regReq = "Gender";
				}


				if(selectedFielsStr.length() == 0) {
					selectedFielsStr = regReq;
				}
				else {
					selectedFielsStr = selectedFielsStr + ", " + regReq; 
				}
			}
		}
		else {
			selectedFielsStr = "--";
		}

		selectedFieldsLblId.setValue(selectedFielsStr);

		/*if(prgmObj.getRedemptionOTPFlag() == OCConstants.FLAG_YES){
			String otpSettingStr = Constants.STRING_NILL;
			if(prgmObj.getOtpLimitAmt() != null && prgmObj.getOtpLimitAmt() > 0){
				otpSettingStr += "Yes, for amount greater than "+Utility.getAmountInUSFormat(prgmObj.getOtpLimitAmt());
			}
			else{
				otpSettingStr += "Yes (no redemption amount limit)";
			}
			enableOTPAuthLblId.setValue(otpSettingStr);
		}
		else if( prgmObj.getRedemptionOTPFlag() == OCConstants.FLAG_NO){
			enableOTPAuthLblId.setValue("No");
		}*/
	}
	
	/*public void onSelect$levelValueLbId$thresholdBonusWinId(){
		if(thresholdBonusWinId$levelValueLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Increment")){
			thresholdBonusWinId$limitDivId.setVisible(true);
		}else thresholdBonusWinId$limitDivId.setVisible(false);
	
	}*/
	public void onSelect$levelTypeLbId$thresholdBonusWinId(){
		Long thresholdId= null;
		LoyaltyThresholdBonus bonus = null;


		if((thresholdBonusWinId$levelValueLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Increment"))
				&&(thresholdBonusWinId$levelTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("LPV"))){
			thresholdBonusWinId$limitDivId.setVisible(true);
			if(isEdit) {
				thresholdId = (Long) session.getAttribute("thresholdId");
			}
			try {
				loyaltyThresholdBonusDao=(LoyaltyThresholdBonusDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
				if(thresholdId != null) {
					bonus	= loyaltyThresholdBonusDao.getThresholdById(thresholdId);
					if(bonus.getThresholdLimit()!=null){
						thresholdBonusWinId$thresholdLimitID.setValue(bonus.getThresholdLimit().longValue()+"");
					}
				}	
			
			} catch (Exception e) {
				logger.error("Exception ::", e);
			}
		}else thresholdBonusWinId$limitDivId.setVisible(false);
	}
	public void onSelect$levelValueLbId$thresholdBonusWinId(){
		Long thresholdId= null;
		LoyaltyThresholdBonus bonus = null;


		if((thresholdBonusWinId$levelValueLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Increment"))
				&&(thresholdBonusWinId$levelTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("LPV"))){
			thresholdBonusWinId$limitDivId.setVisible(true);
			if(isEdit) {
				thresholdId = (Long) session.getAttribute("thresholdId");
			}
			try {
				loyaltyThresholdBonusDao=(LoyaltyThresholdBonusDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
				if(thresholdId != null) {
					bonus	= loyaltyThresholdBonusDao.getThresholdById(thresholdId);
					if(bonus.getThresholdLimit()!=null){
						thresholdBonusWinId$thresholdLimitID.setValue(bonus.getThresholdLimit().longValue()+"");
					}
				}	
			
			} catch (Exception e) {
				logger.error("Exception ::", e);
			}
		}else thresholdBonusWinId$limitDivId.setVisible(false);
	}

	private void loadAddedCardSets(Long prgmId) {
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		List<LoyaltyCardSet> list = ltyPrgmSevice.getCardsetList(prgmId);
		if(list != null ) {
			redrawCardSetList(list);
		}
	}

	private void redrawCardSetList(List<LoyaltyCardSet> cardSetList) {
		Components.removeAllChildren(cardSetRowsId);
		for (LoyaltyCardSet loyaltyCardSet : cardSetList) {
			Row row = new Row();

			row.appendChild(new Label(loyaltyCardSet.getCardSetName()));
			row.appendChild(new Label(loyaltyCardSet.getQuantity().toString()));
			row.appendChild(new Label(loyaltyCardSet.getCardSetType().toString()));
			row.appendChild(new Label(loyaltyCardSet.getGenerationType()));
			
			if(loyaltyCardSet.getStatus().equalsIgnoreCase("Processing")){
				Hbox hbox = new Hbox();
				Label lb = new Label(loyaltyCardSet.getStatus());
				lb.setStyle("margin-right:5px;");
				hbox.appendChild(lb);
				A a = new A("Refresh");
				a.addEventListener("onClick", this);
				a.setAttribute("type","refresh");
				hbox.appendChild(a);
				row.appendChild(hbox);
			}else {
				row.appendChild(new Label(loyaltyCardSet.getStatus()));
			}
			
			if(loyaltyCardSet.getLinkedTierLevel() > 0){
				row.appendChild(new Label("Tier "+loyaltyCardSet.getLinkedTierLevel()));
			}
			else{
				row.appendChild(new Label("Automatic"));
			}
			
			Hbox hbox = new Hbox();

			String src = "";
			String toolTipTxtStr = "";
			String type = "";
			if(loyaltyCardSet.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_CARDSET_STATUS_ACTIVE)) {
				src = "/img/pause_icn.png";
				toolTipTxtStr  = "Suspend";
				type = "Suspend";
			}
			else if(loyaltyCardSet.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_CARDSET_STATUS_SUSPENDED)) {
				src = "/img/play_icn.png";
				toolTipTxtStr = "Activate";
				type = "Activate";
			}

			Image editImg = new Image(src);
			editImg.setTooltiptext(toolTipTxtStr);
			editImg.setStyle("cursor:pointer;margin-right:5px;");
			editImg.addEventListener("onClick", this);
			editImg.setAttribute("type", type);
			editImg.setParent(hbox);

			Image exportImg = new Image("/images/loyalty/export.png");
			exportImg.setTooltiptext("Export");
			exportImg.setStyle("cursor:pointer;margin-right:5px;");
			exportImg.addEventListener("onClick", this);
			exportImg.setAttribute("type", "export");
			exportImg.setParent(hbox);

			hbox.setParent(row);

			row.setValue(loyaltyCardSet);
			row.setParent(cardSetRowsId);
		}

	}

	private void loadPrivlegeTiers(Long prgmId) {
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		List<LoyaltyProgramTier> tierList = ltyPrgmSevice.getTierList(prgmId);
		redrawTierList(tierList);
	}

	private void redrawTierList(List<LoyaltyProgramTier> tierList) {
		Long prgmId=(Long) session.getAttribute("programId");
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
		String currSymbol = Utility.countryCurrencyMap.get(user.getCountryType());
		if(prgmObj.getRewardType()!=null && prgmObj.getRewardType().equalsIgnoreCase(OCConstants.REWARD_TYPE_PERK)) {
			
			resetPerkTiersGridCols(prgmObj);
			Components.removeAllChildren(tierRowsId);
			int noOfTiers=prgmObj.getNoOfTiers();
			enableOTPAuthLblId.setValue("No");
			
			for(int i=1;i<=noOfTiers;i++){
				String tier="Tier "+i;
				boolean isExists=false;
				if (tierList != null) {
					for (LoyaltyProgramTier loyaltyProgramTier : tierList) {
						if (loyaltyProgramTier.getTierType().equalsIgnoreCase(tier)) {
							
							Row row = new Row();
							row.setParent(tierRowsId);

							if(prgmObj.getTierEnableFlag() == OCConstants.FLAG_YES ) {
								row.appendChild(new Label(loyaltyProgramTier.getTierType()));
								row.appendChild(new Label(loyaltyProgramTier.getTierName()));
							}
							
							String quota = "Earn "+loyaltyProgramTier.getEarnValue().intValue()+" "+loyaltyProgramTier.getEarnType()+" on purchase.";
		          			
							row.appendChild(new Label(quota));
							
							String perkUsageLimit = "Max "+loyaltyProgramTier.getPerkLimitValue()+" "+loyaltyProgramTier.getEarnType() +" per "+loyaltyProgramTier.getPerkLimitExpType();
	                        
							row.appendChild(new Label(perkUsageLimit));
							
							String quotaAndExp = loyaltyProgramTier.getEarnValue().intValue()+" "+loyaltyProgramTier.getEarnType() +" Valid for "+
				                    loyaltyProgramTier.getRewardExpiryDateType();
	                       
							row.appendChild(new Label(quotaAndExp));
							
							String conversionRule = ltyPrgmSevice
									.getConversionRule(loyaltyProgramTier);
		                   if(!conversionRule.isEmpty() && conversionRule.contains("$")){
		                	   
								if(currSymbol != null && !currSymbol.isEmpty()) userCurrencySymbol = currSymbol + " ";
								    
								conversionRule = conversionRule.contains("$")? conversionRule.replace("$", userCurrencySymbol):conversionRule; 
							}
							row.appendChild(new Label(conversionRule));
							
							String maxRedemption = ltyPrgmSevice.getRedeemRule(loyaltyProgramTier);
	                        if(!maxRedemption.isEmpty() && maxRedemption.contains("$")){
								
								if(currSymbol != null && !currSymbol.isEmpty()) userCurrencySymbol = currSymbol + " ";
								    
								maxRedemption = maxRedemption.contains("$")? maxRedemption.replace("$", userCurrencySymbol):maxRedemption; 
							}
							
							row.appendChild(new Label(maxRedemption));
							
							String otpSettingStr = "No";
							if(loyaltyProgramTier.getRedemptionOTPFlag() == OCConstants.FLAG_YES){
								if(loyaltyProgramTier.getOtpLimitAmt() != null && loyaltyProgramTier.getOtpLimitAmt() > 0){
									otpSettingStr = "Yes, for amount greater than "+Utility.getAmountInUSFormat(loyaltyProgramTier.getOtpLimitAmt());
								}
								else{
									otpSettingStr = "Yes (no redemption amount limit)";
								}
							}
							row.appendChild(new Label(otpSettingStr));

							Hbox hbox = new Hbox();
							Image editImg = new Image("/img/email_edit.gif");
							editImg.setTooltiptext("Edit");
							editImg.setStyle("cursor:pointer;margin-right:5px;");
							editImg.addEventListener("onClick", this);
							editImg.setAttribute("type", "tierEdit");
							editImg.setParent(hbox);

							Image delImg = new Image("/img/action_delete.gif");
							delImg.setTooltiptext("Delete");
							delImg.setStyle("cursor:pointer;");
							delImg.addEventListener("onClick", this);
							delImg.setAttribute("type", "tierDelete");
							delImg.setParent(hbox);

							row.setValue(loyaltyProgramTier);
							hbox.setParent(row);
							isExists = true;
						}
					}
				}
				if(!isExists) {
					Row row = new Row();
					row.setParent(tierRowsId);
					if(prgmObj.getTierEnableFlag() == OCConstants.FLAG_YES ) {
						row.appendChild(new Label(tier));
						row.appendChild(new Label("--"));
					}
					row.appendChild(new Label("--"));
					row.appendChild(new Label("--"));
					row.appendChild(new Label("--"));
					row.appendChild(new Label("--"));
					row.appendChild(new Label("--"));
					row.appendChild(new Label("--"));
					row.appendChild(new Label());
				}
			}
		}else if(prgmObj.getRewardType()==null || prgmObj.getRewardType().equalsIgnoreCase(OCConstants.REWARD_TYPE_LOYALTY)){
			
			resetTiersGridCols(prgmObj);
			Components.removeAllChildren(tierRowsId);
			int noOfTiers=prgmObj.getNoOfTiers();

			for(int i=1;i<=noOfTiers;i++){
				String tier="Tier "+i;
				boolean isExists=false;

				if(tierList != null) {
					for(LoyaltyProgramTier loyaltyProgramTier:tierList) {
						if(loyaltyProgramTier.getTierType().equalsIgnoreCase(tier)) {

							Row row = new Row();
							row.setParent(tierRowsId);

							if(prgmObj.getTierEnableFlag() == OCConstants.FLAG_YES ) {
								row.appendChild(new Label(loyaltyProgramTier.getTierType()));
								row.appendChild(new Label(loyaltyProgramTier.getTierName()));
							}
							String earn=ltyPrgmSevice.getEarn(loyaltyProgramTier);
	                           if(!earn.isEmpty() && earn.contains("$")){
								
								if(currSymbol != null && !currSymbol.isEmpty()) userCurrencySymbol = currSymbol + " ";
								    
								   earn = earn.contains("$")? earn.replace("$", userCurrencySymbol):earn; 
							}
							
							
							row.appendChild(new Label(earn));
							String conversionRule=ltyPrgmSevice.getRule(loyaltyProgramTier);
							
							 if(!conversionRule.isEmpty() && conversionRule.contains("$")){
									
									if(currSymbol != null && !currSymbol.isEmpty()) userCurrencySymbol = currSymbol + " ";
									    
									conversionRule = conversionRule.contains("$")? conversionRule.replace("$", userCurrencySymbol):conversionRule; 
								}
							
							
							row.appendChild(new Label(conversionRule));
							String activationTime=ltyPrgmSevice.getActivationTime(loyaltyProgramTier);
							row.appendChild(new Label(activationTime));

							if(loyaltyProgramTier.getEarnType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS) && !conversionRule.isEmpty() && loyaltyProgramTier.getConversionType() != null ) {

								row.appendChild(new Label(loyaltyProgramTier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO) ? "Auto-Conversion" : "On-Demand"));
							}
							else {
								row.appendChild(new Label(""));
							}

							/*if(loyaltyProgramTier.getConversionType() != null) {
								row.appendChild(new Label(loyaltyProgramTier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO) ? "Auto Conversion" : "On Demand"));
							}
							else {
								row.appendChild(new Label(""));
							}*/
							if(prgmObj.getTierEnableFlag() == OCConstants.FLAG_YES ) {
								String tierUpgradeRule=ltyPrgmSevice.getUpgradeRule(loyaltyProgramTier);
								
								 if(!tierUpgradeRule.isEmpty() && tierUpgradeRule.contains("$")){
										
										if(currSymbol != null && !currSymbol.isEmpty()) userCurrencySymbol = currSymbol + " ";
												    
										tierUpgradeRule = tierUpgradeRule.contains("$")? tierUpgradeRule.replace("$", userCurrencySymbol):tierUpgradeRule; 
									}
								
								row.appendChild(new Label(tierUpgradeRule));
							}
							
							//APP-3666
							String rewardExpStr = "No";
							if(loyaltyProgramTier.getRewardExpiryDateType()!=null && loyaltyProgramTier.getRewardExpiryDateValue()!=null) {
								rewardExpStr = "After "+loyaltyProgramTier.getRewardExpiryDateValue()+" Month(s) of earning, at the end of the month";
							}
							row.appendChild(new Label(rewardExpStr));
							
							String otpSettingStr = "No";
							if(loyaltyProgramTier.getRedemptionOTPFlag() == OCConstants.FLAG_YES){
								if(loyaltyProgramTier.getOtpLimitAmt() != null && loyaltyProgramTier.getOtpLimitAmt() > 0){
									otpSettingStr = "Yes, for amount greater than "+Utility.getAmountInUSFormat(loyaltyProgramTier.getOtpLimitAmt());
								}
								else{
									otpSettingStr = "Yes (no redemption amount limit)";
								}
							}
							row.appendChild(new Label(otpSettingStr));

							Hbox hbox=new Hbox();
							Image editImg = new Image("/img/email_edit.gif");
							editImg.setTooltiptext("Edit");
							editImg.setStyle("cursor:pointer;margin-right:5px;");
							editImg.addEventListener("onClick", this);
							editImg.setAttribute("type", "tierEdit");
							editImg.setParent(hbox);

							Image delImg = new Image("/img/action_delete.gif");
							delImg.setTooltiptext("Delete");
							delImg.setStyle("cursor:pointer;");
							delImg.addEventListener("onClick", this);
							delImg.setAttribute("type", "tierDelete");
							delImg.setParent(hbox);

							row.setValue(loyaltyProgramTier);
							hbox.setParent(row);


							isExists=true;
						}
						//APP-3666
						/*if(loyaltyProgramTier.getRedemptionOTPFlag() == OCConstants.FLAG_YES){
							String otpSettingStr = Constants.STRING_NILL;
							if(loyaltyProgramTier.getOtpLimitAmt() != null && loyaltyProgramTier.getOtpLimitAmt() > 0){
								otpSettingStr += "Yes, for amount greater than "+Utility.getAmountInUSFormat(loyaltyProgramTier.getOtpLimitAmt());
							}
							else{
								otpSettingStr += "Yes (no redemption amount limit)";
							}
							enableOTPAuthLblId.setValue(otpSettingStr);
						}
						else if( loyaltyProgramTier.getRedemptionOTPFlag() == OCConstants.FLAG_NO){
							enableOTPAuthLblId.setValue("No");
						}*/
					}
				}
				if(!isExists) {
					Row row = new Row();
					row.setParent(tierRowsId);
					if(prgmObj.getTierEnableFlag() == OCConstants.FLAG_YES ) {
						row.appendChild(new Label(tier));
						row.appendChild(new Label("--"));
					}
					row.appendChild(new Label("--"));
					row.appendChild(new Label("--"));
					row.appendChild(new Label("--"));
					row.appendChild(new Label("--"));
					row.appendChild(new Label("--"));
					row.appendChild(new Label("--"));
					if(prgmObj.getTierEnableFlag() == OCConstants.FLAG_YES ) {
						row.appendChild(new Label("--"));
					}
					row.appendChild(new Label());
				}
			}
		} 
		
	}

	private void resetTiersGridCols(LoyaltyProgram prgmObj) {
		Components.removeAllChildren(tierColsId);

		if(prgmObj.getTierEnableFlag() == OCConstants.FLAG_YES ) {
			Column tierType = new Column("Privilege Tier");
			tierType.setParent(tierColsId);
			Column tierName = new Column("Privilege Name");
			tierName.setParent(tierColsId);
		}
		Column earn = new Column("Earn Rule");
		earn.setParent(tierColsId);
		Column redeem = new Column("Redeem Rule");
		redeem.setParent(tierColsId);
		Column activation = new Column("Activation Period");
		activation.setParent(tierColsId);
		Column ptsConversion = new Column("Pts. Conversion Decision");
		ptsConversion.setParent(tierColsId);
		if(prgmObj.getTierEnableFlag() == OCConstants.FLAG_YES ) {
			Column upgrade = new Column("Tier Upgrade Rule");
			upgrade.setParent(tierColsId);
		}
		Column rewardExp = new Column("Reward Expiry");
		rewardExp.setParent(tierColsId);
		Column otp = new Column("OTP for Redemption");
		otp.setParent(tierColsId);
		Column actions = new Column("Actions");
		actions.setParent(tierColsId);
	}
	
	private void resetPerkTiersGridCols(LoyaltyProgram prgmObj) {
		Components.removeAllChildren(tierColsId);

		if(prgmObj.getTierEnableFlag() == OCConstants.FLAG_YES ) {
				Column tierType = new Column("Privilege Tier");
				tierType.setParent(tierColsId);
				Column tierName = new Column("Privilege Name");
				tierName.setParent(tierColsId);
			}
			Column earn = new Column("Perk Issuance (Quota)");
			earn.setParent(tierColsId);
			Column perkLimit = new Column("Perk Usage Limit");
			perkLimit.setParent(tierColsId);
			Column expiry = new Column("Quota & Expiry");
			expiry.setParent(tierColsId);
			Column conversion = new Column("Conversion");
			conversion.setParent(tierColsId);
			Column maxRedemption = new Column("Max Redemption");
			maxRedemption.setParent(tierColsId);
			Column otp = new Column("OTP for Redemption");
			otp.setParent(tierColsId);
			Column actions = new Column("Actions");
			actions.setParent(tierColsId);
		}

	private void loadThresholdBonus(Long prgmId) {
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		List<LoyaltyThresholdBonus> bonusList = ltyPrgmSevice.getBonusList(prgmId);
		if(bonusList!=null) {
			redrawBonusList(bonusList);
		}
	}

	private void redrawBonusList(List<LoyaltyThresholdBonus> bonusList) {
		Components.removeAllChildren(levelRowsId);
		for(LoyaltyThresholdBonus loyaltyThresholdBonus:bonusList) {
			LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
			Row row = new Row();
			row.setParent(levelRowsId);
			if(loyaltyThresholdBonus.getRegistrationFlag()==OCConstants.FLAG_YES) {
				row.appendChild(new Label("On Enrollment"));
			}
			else {
				String earnedPoints=ltyPrgmSevice.getEarnedPoints(loyaltyThresholdBonus);
				
				if(!earnedPoints.isEmpty() && earnedPoints.contains("$")){
					
					String currSymbol = Utility.countryCurrencyMap.get(user.getCountryType());
					if(currSymbol != null && !currSymbol.isEmpty()) userCurrencySymbol = currSymbol + " ";
					    
					earnedPoints = earnedPoints.contains("$")? earnedPoints.replace("$", userCurrencySymbol):earnedPoints; 
				}
				row.appendChild(new Label(earnedPoints));
			}

			String bonus=ltyPrgmSevice.getBonus(loyaltyThresholdBonus);
			if(!bonus.isEmpty() && bonus.contains("$")){
				
				String currSymbol = Utility.countryCurrencyMap.get(user.getCountryType());
				if(currSymbol != null && !currSymbol.isEmpty()) userCurrencySymbol = currSymbol + " ";
				    
				bonus = bonus.contains("$")? bonus.replace("$", userCurrencySymbol):bonus; 
				row.appendChild(new Label(bonus));
				row.appendChild(new Label("N/A"));

			}
			else if(!bonus.isEmpty() && bonus.contains("Points")) {
				
				
				//Double bonusConvertedAmount = getConversionVal(loyaltyThresholdBonus.getExtraBonusValue().intValue(),null);
				row.appendChild(new Label("N/A"));
				
				row.appendChild(new Label(bonus));

			}
			String expirationRule = "--";
			if(loyaltyThresholdBonus.getRegistrationFlag()==OCConstants.FLAG_YES) {
				expirationRule = "--";
			}else{
				String expiryType=loyaltyThresholdBonus.getBonusExpiryDateType();
				Long expiryValue=loyaltyThresholdBonus.getBonusExpiryDateValue();
				if(expiryValue!=null && expiryValue>12) {
					int years = Integer.parseInt(expiryValue.toString())/12;
					int remMonths = Integer.parseInt(expiryValue.toString()) % 12;
					expirationRule= "After " + years+ " Year(s) and " + remMonths + " Month(s) of earning, at the end of the month";
				}
				else if(expiryType!=null && expiryValue!=null && !expiryType.isEmpty()){
					expirationRule= "After " + expiryValue+ " " + expiryType + "(s) of earning, at the end of the month";
				}
			}
			
			row.appendChild(new Label(expirationRule));
			

			Hbox hbox = new Hbox();
			if(loyaltyThresholdBonus.getRegistrationFlag()!=OCConstants.FLAG_YES) {
				Image editImg = new Image("/img/email_edit.gif");
				editImg.setTooltiptext("Edit");
				editImg.setStyle("cursor:pointer;margin-right:10px;margin-top:5px;margin-bottom:5px;");
				editImg.addEventListener("onClick", this);
				editImg.setAttribute("type", "thresholdEdit");
				editImg.setParent(hbox);
				
				Image delImg = new Image("/img/action_delete.gif");
				delImg.setTooltiptext("Delete");
				delImg.setStyle("cursor:pointer;margin-top:5px;margin-bottom:5px;margin-right:10px;");
				delImg.addEventListener("onClick", this);
				delImg.setAttribute("type", "thresholdDelete");
				delImg.setParent(hbox);
				

				if(loyaltyThresholdBonus.getEmailTempId()!=null){
							
							Image prev = new Image("/images/Preview_icn.png");
							prev.setStyle("cursor:pointer;margin-top:5px;margin-bottom:5px;");
							prev.setParent(hbox);
							
							A prevEmail = new A();
							prevEmail.setLabel("Preview Email");
							prevEmail.setStyle("color:#2886B9;text-decoration: underline;margin-left:5px;margin-top:5px;margin-bottom:5px;margin-right:10px;");
							prevEmail.setWidth("100px");
							prevEmail.addEventListener("onClick",this);
							prevEmail.setAttribute("type", "previewEmail");
							prevEmail.setAttribute("templateId", loyaltyThresholdBonus.getEmailTempId());
							prevEmail.setParent(hbox);
					
				}
			

				if(loyaltyThresholdBonus.getSmsTempId()!=null){
					
					Image prev = new Image("/images/Preview_icn.png");
					prev.setStyle("cursor:pointer;margin-top:5px;margin-bottom:5px;");
					prev.setParent(hbox);

					
					A prevSms = new A();
					prevSms.setLabel("Preview SMS");
					prevSms.setStyle("color:#2886B9;text-decoration: underline;margin-bottom:5px;padding-top:5px;margin-left:5px;");
					prevSms.setWidth("100px");
					prevSms.addEventListener("onClick", this);
					prevSms.setAttribute("type", "previewSms");
					prevSms.setAttribute("templateId", loyaltyThresholdBonus.getSmsTempId());
					prevSms.setParent(hbox);
				}
				
				/*List<CustomTemplates> templatelist = null;
				List<AutoSMS> templatelist1 = null;

				templatelist = ltyPrgmSevice.getTemplateList(GetUser.getUserObj().getUserId(),"earnedBonus");
				String name;
				if(loyaltyThresholdBonus.getEmailTempId()!=null){
					for (CustomTemplates customTemplates : templatelist) {
						
						//logger.info("lty bonus id==="+loyaltyThresholdBonus.getEmailTempId()+"custom temp id=="+customTemplates.getTemplateId());
						
						if(loyaltyThresholdBonus.getEmailTempId().equals(customTemplates.getTemplateId())){
							
							name=customTemplates.getTemplateName();
							Button email=new Button();
							email.setLabel(name);
							email.setWidth("150px");
							//email.setStyle("cursor:pointer;margin-right:5px;margin-left:5px;min-width:100px;max-width:200px !important;line-break: strict;");	
							email.setStyle("cursor:pointer;margin-right:5px;margin-left:5px;");	
							email.setTooltiptext(name);
							email.setParent(hbox);
							
						}
					}
					
				}
				templatelist1 = ltyPrgmSevice.getSmsTemplateList(userId,"earningBonus");
				String nameSms;
				if(loyaltyThresholdBonus.getSmsTempId()!=null){
					
					if(loyaltyThresholdBonus.getSmsTempId()==-1){
						Button sms=new Button();
						sms.setLabel("Default Message");
						sms.setStyle("cursor:pointer;margin-right:5px;margin-left:5px;");	
						sms.setWidth("150px");
						sms.setTooltiptext("Default Message");
						sms.setParent(hbox);
						
					}else{
						
						for (AutoSMS customTemplates : templatelist1) {
							
							if(loyaltyThresholdBonus.getSmsTempId().equals(customTemplates.getAutoSmsId())){
								
								nameSms=customTemplates.getTemplateName();
								Button sms=new Button();
								sms.setLabel(nameSms);
								sms.setStyle("cursor:pointer;margin-right:5px;margin-left:5px;");	
								sms.setWidth("150px");
								sms.setTooltiptext(nameSms);
								sms.setParent(hbox);
								
							}
						}
					}
				}*/
				
				
				row.setValue(loyaltyThresholdBonus);
				hbox.setParent(row);

			}else{
				
				Image delImg = new Image("/img/action_delete.gif");
				delImg.setTooltiptext("Delete");
				delImg.setStyle("cursor:pointer;margin-top:5px;margin-bottom:5px;");
				delImg.addEventListener("onClick", this);
				delImg.setAttribute("type", "thresholdDelete");
				delImg.setParent(hbox);
				
				row.setValue(loyaltyThresholdBonus);
				hbox.setParent(row);
			}
		}		
	}

	private void loadValiditySettings(LoyaltyProgram prgmObj) {
		if(prgmObj.getRewardExpiryFlag() == OCConstants.FLAG_YES) {
			setRewardExpiryTier(prgmObj.getProgramId());
			rewardExpDivId.setVisible(false);
			rewardTierDivId.setVisible(true);
		}
		else {
			rewardExpLblId.setValue("No");
			rewardExpDivId.setVisible(true);
			rewardTierDivId.setVisible(false);
		}
		if(prgmObj.getMembershipExpiryFlag() == OCConstants.FLAG_YES) {
			setMemExpiryTier(prgmObj.getProgramId());
			membshpExpDivId.setVisible(false);
			membshpTierDivId.setVisible(true);
		}else {
			membshpExpLblId.setValue("No");
			membshpExpDivId.setVisible(true);
			membshpTierDivId.setVisible(false);
		}

		if(prgmObj.getTierEnableFlag() == OCConstants.FLAG_YES) {
			resetLevelDivId.setVisible(true);
			if(prgmObj.getMbrshipExpiryOnLevelUpgdFlag() == OCConstants.FLAG_YES) {
				resetValFlagLblId.setValue("Yes");
			}else {
				resetValFlagLblId.setValue("No");
			}
		}
		else {
			resetLevelDivId.setVisible(false);
		}

		if(prgmObj.getGiftAmountExpiryFlag() == OCConstants.FLAG_YES) {

			gftAmtExpLblId.setValue("after " + prgmObj.getGiftAmountExpiryDateValue() + " " + prgmObj.getGiftAmountExpiryDateType()+"(s)");
		}
		else {
			gftAmtExpLblId.setValue("No");
		}

		if(prgmObj.getGiftMembrshpExpiryFlag() == OCConstants.FLAG_YES) {
			gftCrdExpLblId.setValue("after " + prgmObj.getGiftMembrshpExpiryDateValue() + " " + prgmObj.getGiftMembrshpExpiryDateType()+"(s)");
		}
		else {
			gftCrdExpLblId.setValue("No");
		}
	}

	private void setRewardExpiryTier(Long prgmId) {
		Components.removeAllChildren(rewardTierLbId);
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
		int noOfTiers=prgmObj.getNoOfTiers();
		List<LoyaltyProgramTier> tierList=ltyPrgmSevice.getTierList(prgmId);
		Listitem li=null;
		for(int i=1;i<=noOfTiers;i++){
			String tier="Tier "+i;
			boolean isExists=false;
			if(tierList!=null) {
				for(LoyaltyProgramTier loyaltyProgramTier:tierList) {
					if(loyaltyProgramTier.getTierType().equalsIgnoreCase(tier)) {
						li = new Listitem();
						li.appendChild(new Listcell(loyaltyProgramTier.getTierType()));
						li.appendChild(new Listcell(loyaltyProgramTier.getTierName()));
						if(loyaltyProgramTier.getRewardExpiryDateValue()!=null && loyaltyProgramTier.getRewardExpiryDateType()!=null ) {
							if(loyaltyProgramTier.getRewardExpiryDateValue()>12) {
								int years = Integer.parseInt(loyaltyProgramTier.getRewardExpiryDateValue().toString())/12;
								int remMonths = Integer.parseInt(loyaltyProgramTier.getRewardExpiryDateValue().toString()) % 12;
								li.appendChild(new Listcell("after " + years + " Year(s) and " + remMonths +" Month(s)"));
							} else
							li.appendChild(new Listcell("after " + loyaltyProgramTier.getRewardExpiryDateValue() + " " + loyaltyProgramTier.getRewardExpiryDateType()+"(s)"));
						}else {
							li.appendChild(new Listcell("--"));
						}
						li.setParent(rewardTierLbId);
						li.setValue(loyaltyProgramTier);
						isExists=true;
					}
				}
			}

			if(!isExists) {
				li = new Listitem();
				li.appendChild(new Listcell(tier));
				li.appendChild(new Listcell("--"));
				li.appendChild(new Listcell("--"));
				li.setParent(rewardTierLbId);
			}
		}
	}

	private void setMemExpiryTier(Long prgmId) {
		Components.removeAllChildren(membshpTierLbId);
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
		int noOfTiers=prgmObj.getNoOfTiers();
		List<LoyaltyProgramTier> tierList=ltyPrgmSevice.getTierList(prgmId);
		Listitem li=null;
		for(int i=1;i<=noOfTiers;i++){
			String tier="Tier "+i;
			boolean isExists=false;
			if(tierList!=null) {
				for(LoyaltyProgramTier loyaltyProgramTier:tierList) {
					if(loyaltyProgramTier.getTierType().equalsIgnoreCase(tier)) {

						li = new Listitem();
						li.appendChild(new Listcell(loyaltyProgramTier.getTierType()));
						li.appendChild(new Listcell(loyaltyProgramTier.getTierName()));
						if(loyaltyProgramTier.getMembershipExpiryDateValue()!=null && loyaltyProgramTier.getMembershipExpiryDateType()!=null) {
							li.appendChild(new Listcell("after " + loyaltyProgramTier.getMembershipExpiryDateValue() + " " + loyaltyProgramTier.getMembershipExpiryDateType()+"(s)"));
						}else {
							li.appendChild(new Listcell("--"));
						}

						li.setParent(membshpTierLbId);
						li.setValue(loyaltyProgramTier);
						isExists=true;
					}
				}
			}
			if(!isExists) {
				li = new Listitem();
				li.appendChild(new Listcell(tier));
				li.appendChild(new Listcell("--"));
				li.appendChild(new Listcell("--"));
				li.setParent(membshpTierLbId);
			}
		}
	}

	private void loadExclusions(LoyaltyProgram prgmObj) {
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		LoyaltyProgramExclusion exclusionObj = ltyPrgmSevice.getExclusionObj(prgmObj.getProgramId());
		Long orgId=ltyPrgmSevice.getOrgId(userId) ;
		List<Coupons> couponList=ltyPrgmSevice.getCouponList(orgId);
		String selectedFielsStr = "";
		if(exclusionObj != null && exclusionObj.getIssuanceWithPromoFlag() == OCConstants.FLAG_YES) {
			String issueStr = exclusionObj.getIssuancePromoIdStr();
			if(issueStr != null && !issueStr.trim().isEmpty()) {
				if(issueStr.equalsIgnoreCase(OCConstants.LOYALTY_PROMO_EXCLUSION_ALL)) {
					selectedFielsStr = "Applicable for all promotions";
				}
				else {
					String[] issueStrArr = issueStr.split(Constants.ADDR_COL_DELIMETER);
					if(couponList!=null) {
						for (String issue : issueStrArr) {
							for(Coupons coupons:couponList) {
								if(issue.equalsIgnoreCase(coupons.getCouponId().toString())) {
									if(selectedFielsStr.length() == 0) {
										selectedFielsStr = coupons.getCouponName();
									}
									else {
										selectedFielsStr = selectedFielsStr + ", " + coupons.getCouponName(); 
									}
									break;
								}
							}
						}
					}
				}
			}
			else {
				selectedFielsStr = "No";
			}
		}
		else {
			selectedFielsStr = "No";
		}
		selectedIssuePromosLblId.setValue(selectedFielsStr);

		selectedFielsStr = "";
		if(exclusionObj != null && exclusionObj.getRedemptionWithPromoFlag() == OCConstants.FLAG_YES) {
			String redeemStr = exclusionObj.getRedemptionPromoIdStr();
			if(redeemStr != null && !redeemStr.trim().isEmpty()) {
				if(redeemStr.equalsIgnoreCase(OCConstants.LOYALTY_PROMO_EXCLUSION_ALL)) {
					selectedFielsStr = "Applicable for all promotions";
				}
				else {
					String[] redeemStrArr = redeemStr.split(Constants.ADDR_COL_DELIMETER);
					if(couponList!=null) {
						for (String redeem : redeemStrArr) {
							for(Coupons coupons:couponList) {
								if(redeem.equalsIgnoreCase(coupons.getCouponId().toString())) {
									if(selectedFielsStr.length() == 0) {
										selectedFielsStr = coupons.getCouponName();
									}
									else {
										selectedFielsStr = selectedFielsStr + ", " + coupons.getCouponName(); 
									}
									break;
								}
							}
						}
					}
				}

			}
			else {
				selectedFielsStr = "No";
			}

		}
		else {
			selectedFielsStr = "No";
		}

		selectedRedeemPromosLblId.setValue(selectedFielsStr);
		
		String redemStoreStr = exclusionObj.getSelectedStoreStr();
		if(exclusionObj.getAllStrChk()!=null && exclusionObj.getAllStrChk()==true) {
			allselectedRedeemLblId.setValue("Applicable for all stores");
		}
		else if(redemStoreStr != null && !redemStoreStr.trim().isEmpty()) {
				String[] storeStrArr = redemStoreStr.split(Constants.ADDR_COL_DELIMETER);
				redemExcludedStoresDivId.setVisible(true);
				for (String store : storeStrArr) {
					Label lb = new Label(store);
					lb.setParent(redemExcludedStoresDivId);
					Separator tempSep = new Separator();
					tempSep.setHeight("0px");
					tempSep.setParent(redemExcludedStoresDivId);
				}
		}
		else {
			allselectedRedeemLblId.setValue("No");
		}
		
		/*String redemStoreStr = exclusionObj == null ? null : exclusionObj.getSelectedStoreStr();
		if(redemStoreStr != null && redemStoreStr.length() != 0) {
			String[] storeStrArr = redemStoreStr.split(Constants.ADDR_COL_DELIMETER);
			redemExcludedStoresDivId.setVisible(true);
			for (String store : storeStrArr) {
				Label lb = new Label(store);
				lb.setParent(redemExcludedStoresDivId);
				Separator tempSep = new Separator();
				tempSep.setHeight("0px");
				tempSep.setParent(redemExcludedStoresDivId);
			}
		}
		else {
			noRedemExcludedStoresDivId.setVisible(true);
		}*/

		String storeStr = exclusionObj == null ? null : exclusionObj.getStoreNumberStr();
		if(storeStr != null && storeStr.length() != 0) {
			String[] storeStrArr = storeStr.split(Constants.ADDR_COL_DELIMETER);
			excludedStoresDivId.setVisible(true);
			for (String store : storeStrArr) {
				Label lb = new Label(store);
				lb.setParent(excludedStoresDivId);
				Separator tempSep = new Separator();
				tempSep.setHeight("0px");
				tempSep.setParent(excludedStoresDivId);
			}
		}
		else {
			noExcludedStoresDivId.setVisible(true);
		}

		if (exclusionObj != null) {
			String itemStr = exclusionObj.getItemCatStr();
			String dcsStr = exclusionObj.getDcsStr();
			String vendorStr = exclusionObj.getVendorStr();
			String classStr = exclusionObj.getClassStr();
			String subClassStr = exclusionObj.getSubClassStr();
			String skuNumStr = exclusionObj.getSkuNumStr();
			String deptCodeStr = exclusionObj.getDeptCodeStr();

			if((itemStr != null && !itemStr.isEmpty()) || (dcsStr != null && !dcsStr.isEmpty()) || (vendorStr != null && !vendorStr.isEmpty())
					|| (classStr != null && !classStr.isEmpty()) || (subClassStr != null && !subClassStr.isEmpty()) || (skuNumStr != null && !skuNumStr.isEmpty()) 
					|| (deptCodeStr != null && !deptCodeStr.isEmpty())) {

				if (itemStr != null && !itemStr.isEmpty()) {
					excludeDivId.setVisible(true);
					excLableDivId.setVisible(false);
					itemStr = itemStr.replace(Constants.ADDR_COL_DELIMETER,
							Constants.DELIMETER_COMMA);
					Listitem li = new Listitem();
					Listcell lc = new Listcell();
					lc.setLabel("Item Category");
					lc.setParent(li);
					lc = new Listcell();
					lc.setLabel(itemStr);
					lc.setTooltiptext(itemStr);
					lc.setParent(li);
					li.setParent(excProductLbId);
				} if (dcsStr != null && !dcsStr.isEmpty()) {
					excludeDivId.setVisible(true);
					excLableDivId.setVisible(false);
					dcsStr.replace(Constants.ADDR_COL_DELIMETER,
							Constants.DELIMETER_COMMA);
					Listitem li = new Listitem();
					Listcell lc = new Listcell();
					lc.setLabel("DCS");
					lc.setParent(li);
					lc = new Listcell();
					lc.setLabel(dcsStr);
					lc.setTooltiptext(dcsStr);
					lc.setParent(li);
					li.setParent(excProductLbId);
				} if (vendorStr != null && !vendorStr.isEmpty()) {
					excludeDivId.setVisible(true);
					excLableDivId.setVisible(false);
					vendorStr.replace(Constants.ADDR_COL_DELIMETER,
							Constants.DELIMETER_COMMA);
					Listitem li = new Listitem();
					Listcell lc = new Listcell();
					lc.setLabel("Vendor Code");
					lc.setParent(li);
					lc = new Listcell();
					lc.setLabel(vendorStr);
					lc.setTooltiptext(vendorStr);
					lc.setParent(li);
					li.setParent(excProductLbId);
				} if (classStr != null && !classStr.isEmpty()) {
					excludeDivId.setVisible(true);
					excLableDivId.setVisible(false);
					classStr.replace(Constants.ADDR_COL_DELIMETER,
							Constants.DELIMETER_COMMA);
					Listitem li = new Listitem();
					Listcell lc = new Listcell();
					lc.setLabel("Class");
					lc.setParent(li);
					lc = new Listcell();
					lc.setLabel(classStr);
					lc.setTooltiptext(classStr);
					lc.setParent(li);
					li.setParent(excProductLbId);
				} if (subClassStr != null && !subClassStr.isEmpty()) {
					excludeDivId.setVisible(true);
					excLableDivId.setVisible(false);
					subClassStr.replace(Constants.ADDR_COL_DELIMETER,
							Constants.DELIMETER_COMMA);
					Listitem li = new Listitem();
					Listcell lc = new Listcell();
					lc.setLabel("Subclass");
					lc.setParent(li);
					lc = new Listcell();
					lc.setLabel(subClassStr);
					lc.setTooltiptext(subClassStr);
					lc.setParent(li);
					li.setParent(excProductLbId);
				} if (skuNumStr != null && !skuNumStr.isEmpty()) {
					excludeDivId.setVisible(true);
					excLableDivId.setVisible(false);
					skuNumStr.replace(Constants.ADDR_COL_DELIMETER,
							Constants.DELIMETER_COMMA);
					Listitem li = new Listitem();
					Listcell lc = new Listcell();
					lc.setLabel("SKU Number");
					lc.setParent(li);
					lc = new Listcell();
					lc.setLabel(skuNumStr);
					lc.setTooltiptext(skuNumStr);
					lc.setParent(li);
					li.setParent(excProductLbId);
				} if (deptCodeStr != null && !deptCodeStr.isEmpty()) {
					excludeDivId.setVisible(true);
					excLableDivId.setVisible(false);
					deptCodeStr.replace(Constants.ADDR_COL_DELIMETER,
							Constants.DELIMETER_COMMA);
					Listitem li = new Listitem();
					Listcell lc = new Listcell();
					lc.setLabel("Department Code");
					lc.setParent(li);
					lc = new Listcell();
					lc.setLabel(deptCodeStr);
					lc.setTooltiptext(deptCodeStr);
					lc.setParent(li);
					li.setParent(excProductLbId);
				} 

			}
			else {
				excludeDivId.setVisible(false);
				excLableDivId.setVisible(true);
				excludedProdTypeLblId.setValue("No");
			}
		}
		else {
			excludeDivId.setVisible(false);
			excLableDivId.setVisible(true);
			excludedProdTypeLblId.setValue("No");
		}
		String dateStr = exclusionObj == null ? null : exclusionObj.getDateStr();
		if(dateStr != null && dateStr.length() != 0) {
			String[] prodStrArr = dateStr.split(Constants.ADDR_COL_DELIMETER);
			excludedDatesDivId.setVisible(true);
			for (String prod : prodStrArr) {
				Label lb = new Label(prod);
				lb.setParent(excludedDatesDivId);
				Separator tempSep = new Separator();
				tempSep.setHeight("0px");
				tempSep.setParent(excludedDatesDivId);
			}
		}
		else {
			noExcludedDatesDivId.setVisible(true);
		}
		
		String redemdateStr = exclusionObj == null ? null : exclusionObj.getExclRedemDateStr();
		if(redemdateStr != null && redemdateStr.length() != 0) {
			String[] prodStrArr = redemdateStr.split(Constants.ADDR_COL_DELIMETER);
			excludedRedemDatesDivId.setVisible(true);
			for (String prod : prodStrArr) {
				Label lb = new Label(prod);
				lb.setParent(excludedRedemDatesDivId);
				Separator tempSep = new Separator();
				tempSep.setHeight("0px");
				tempSep.setParent(excludedRedemDatesDivId);
			}
		}
		else {
			noExcludedRedemDatesDivId.setVisible(true);
		}
		
	}

	private void setAvailablePromos(Listbox exclusionLbId) {
		Components.removeAllChildren(exclusionLbId);
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		Long orgId=ltyPrgmSevice.getOrgId(userId) ;
		List<Coupons> couponList=ltyPrgmSevice.getCouponList(orgId);
		Listitem li=null;
		if(couponList!=null) {
			for(Coupons coupons:couponList) {
				li = new Listitem(coupons.getCouponName(),coupons.getCouponId());
				li.setDisabled(true);
				li.setParent(exclusionLbId);
			}
		}

	}

	@Override
	public void onEvent(Event event) throws Exception {
		super.onEvent(event);

		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		Long prgmId = (Long) session.getAttribute("programId");
		if(event.getTarget() instanceof Image){
			Image img = (Image)event.getTarget();
			Row tempRow = ((Row)((Hbox)img.getParent()).getParent());

			String evtType = (String)img.getAttribute("type");

			if("Suspend".equalsIgnoreCase(evtType)) {
				LoyaltyCardSet  loyaltyCardSet = (LoyaltyCardSet)tempRow.getValue();
				loyaltyCardSet.setStatus(OCConstants.LOYALTY_CARDSET_STATUS_SUSPENDED);
				ltyPrgmSevice.saveCardSetObj(loyaltyCardSet);
				List<LoyaltyCardSet> list = ltyPrgmSevice.getCardsetList(prgmId);
				redrawCardSetList(list);
			}
			else if("Activate".equalsIgnoreCase(evtType)) {
				LoyaltyCardSet  loyaltyCardSet = (LoyaltyCardSet)tempRow.getValue();
				loyaltyCardSet.setStatus(OCConstants.LOYALTY_CARDSET_STATUS_ACTIVE);
				ltyPrgmSevice.saveCardSetObj(loyaltyCardSet);
				List<LoyaltyCardSet> list = ltyPrgmSevice.getCardsetList(prgmId);
				redrawCardSetList(list);
			}
			else if ("export".equalsIgnoreCase(evtType)) {
				LoyaltyCardSet  loyaltyCardSet = (LoyaltyCardSet)tempRow.getValue();
				export(loyaltyCardSet);

			}

			else if("tierEdit".equalsIgnoreCase((String) img.getAttribute("type"))) {
				LoyaltyProgramTier  loyaltyProgramTier = (LoyaltyProgramTier)tempRow.getValue();
				LoyaltyProgram progmObj = ltyPrgmSevice.getProgmObj(prgmId);
				session.setAttribute("loyaltyProgramTier", loyaltyProgramTier);
				session.setAttribute("editedTierType", loyaltyProgramTier.getTierType().toString());
				if(progmObj.getRewardType()!=null && progmObj.getRewardType().equalsIgnoreCase(OCConstants.REWARD_TYPE_PERK))
					Redirect.goTo(PageListEnum.LOYALTY_PERK_RULES);
				else 
				    Redirect.goTo(PageListEnum.LOYALTY_RULES);
			}
			else if("tierDelete".equalsIgnoreCase((String) img.getAttribute("type"))) {
				LoyaltyProgramTier  loyaltyProgramTier = (LoyaltyProgramTier)tempRow.getValue();
				Long programId = (Long) session.getAttribute("programId");
				LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(programId);
				int tierLevel = Integer.valueOf(loyaltyProgramTier.getTierType().substring(5)).intValue();
				List<LoyaltyCardSet> cardSetList = ltyPrgmSevice.findCardSetByTierLevel(tierLevel, programId);
				if(cardSetList != null && cardSetList.size() > 0){
					MessageUtil.setMessage("This tier cannot be deleted as it is linked to one or more card-sets."
							+ " However the settings of the tier can be edited.", "color:red", "TOP");
					return;
				}
				
				int confirm;
				int custCount = ltyPrgmSevice.getCustomersCountByTierId(userId,loyaltyProgramTier.getTierId());
				if(!prgmObj.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_DRAFT) && custCount > 0) {
					confirm = Messagebox.show("There are "+ custCount+ " customers in this tier. Do you want to continue deleting the tier?", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm == Messagebox.OK) {

						List<ContactsLoyalty> contList = ltyPrgmSevice.getCustomersByTierId(userId,loyaltyProgramTier.getTierId());
						List<LoyaltyProgramTier> tierList = ltyPrgmSevice.getTierList(programId);
						int tierVal = Integer.parseInt(loyaltyProgramTier.getTierType().replace("Tier ", ""));
						LoyaltyProgramTier tier = null;

						for(int i = tierVal-1 ; i > 0 ; i--){
							boolean isExist = false;
							String tierType = "Tier "+i;
							for (LoyaltyProgramTier tierObj : tierList) {
								if(tierObj.getTierType().equalsIgnoreCase(tierType)) {
									tier = tierObj;
									isExist = true;
									break;
								}
							}
							if(isExist) break;
						}
						List <ContactsLoyalty> newList = new ArrayList<ContactsLoyalty>();
						for (ContactsLoyalty contObj : contList) {
							if(tier != null) {
								contObj.setProgramTierId(tier.getTierId());
							}
							else {
								contObj.setProgramTierId(null);
							}
							newList.add(contObj);
						}

						ltyPrgmSevice.updateContactTierIds(newList);
						StringBuffer logDetails = ltyPrgmSevice.logTierChanges(programId, userId, null, false,false);
						ltyPrgmSevice.deleteTier(loyaltyProgramTier);
						ltyPrgmSevice.logTierChanges(programId, userId, logDetails, true,false);
						MessageUtil.setMessage("Tier deleted successfully.","color:blue","TOP");
						tierList = ltyPrgmSevice.getTierList(programId);
						redrawTierList(tierList);
						loadValiditySettings(prgmObj);

					}
					else {
						return;
					}
				}
				else {
					confirm = Messagebox.show("Are you sure you want to delete tier : "+loyaltyProgramTier.getTierName()+"?", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm == Messagebox.OK) {
						StringBuffer logDetails = ltyPrgmSevice.logTierChanges(programId, userId, null, false,false);
						ltyPrgmSevice.deleteTier(loyaltyProgramTier);
						ltyPrgmSevice.logTierChanges(programId, userId, logDetails, true,false);
						MessageUtil.setMessage("Tier deleted successfully.","color:blue","TOP");
						List<LoyaltyProgramTier> tierList = ltyPrgmSevice.getTierList(programId);
						redrawTierList(tierList);
						loadValiditySettings(prgmObj);
					}
					else {
						return;
					}

				}


				String draftStatus = "";
				draftStatus = prgmObj.getDraftStatus();
				String[] draftList = draftStatus.split(Constants.ADDR_COL_DELIMETER);
				if(prgmObj.getTierEnableFlag() == OCConstants.FLAG_YES) {
					List<LoyaltyProgramTier> list = ltyPrgmSevice.getTierList(programId);
					int count = list == null ? 0 : list.size();
					if(count == prgmObj.getNoOfTiers()) {
						draftList[1] = OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE;
					}
					else {
						draftList[1] = OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE;
					}

				}
				else {
					if(prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
						draftList[1] = OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE;
					}
					else {
						draftList[1] = OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE;
					}
				}
				draftStatus = "";
				for (String eachStr : draftList) {

					if (draftStatus.isEmpty()) {
						draftStatus = eachStr;
					} else {
						draftStatus += Constants.ADDR_COL_DELIMETER + eachStr;
					}
				}
				prgmObj.setDraftStatus(draftStatus);
				ltyPrgmSevice.savePrgmObj(prgmObj);
			}
			else if("thresholdEdit".equalsIgnoreCase((String) img.getAttribute("type"))) {
				LoyaltyThresholdBonus loyaltyThresholdBonus=(LoyaltyThresholdBonus)tempRow.getValue();
				String type="edit";
				thresholdBonusWinId.setVisible(true);
				thresholdBonusWinId.setPosition("center");
				thresholdBonusWinId.doHighlighted();
				editSettings();
				thresholdSettings(loyaltyThresholdBonus,type);
			}
			else if("thresholdDelete".equalsIgnoreCase((String) img.getAttribute("type"))) {
				Long programId = (Long) session.getAttribute("programId");
				LoyaltyThresholdBonus loyaltyThresholdBonus=(LoyaltyThresholdBonus)tempRow.getValue();
				int confirm = Messagebox.show("Are you sure you want to delete the selected threshold?", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				if(confirm == Messagebox.OK) {
					StringBuffer logDetails = null;
					/*if(loyaltyThresholdBonus.getRegistrationFlag() == OCConstants.FLAG_YES){
						logDetails = ltyPrgmSevice.logRegBonusChanges(programId, userId, null, false);
					}
					else if(loyaltyThresholdBonus.getRegistrationFlag() == OCConstants.FLAG_NO){
						logDetails = ltyPrgmSevice.logThresholdBonusChanges(programId, userId, null, false);
					}*/
					logDetails = ltyPrgmSevice.logBonusChanges(programId, userId, null, false);
					ltyPrgmSevice.deleteThreshold(loyaltyThresholdBonus);
					ltyPrgmSevice.logBonusChanges(programId, userId, logDetails, true);
					/*if(loyaltyThresholdBonus.getRegistrationFlag() == OCConstants.FLAG_YES){
						ltyPrgmSevice.logRegBonusChanges(programId, userId, logDetails, true);
					}
					else if(loyaltyThresholdBonus.getRegistrationFlag() == OCConstants.FLAG_NO){
						ltyPrgmSevice.logThresholdBonusChanges(programId, userId, logDetails, true);
					}*/
					
					MessageUtil.setMessage("Threshold deleted successfully.","color:blue","TOP");
					levelRowsId.removeChild(tempRow);
				}
				return;

			}

		}else if(event.getTarget() instanceof A) {
			A a = (A)event.getTarget();
			String evtType = (String)a.getAttribute("type");
			Long templateId = (Long) a.getAttribute("templateId");
			
			if("Refresh".equalsIgnoreCase(evtType)) {
				List<LoyaltyCardSet> list = ltyPrgmSevice.getCardsetList(prgmId);
				redrawCardSetList(list);
			}else if("previewEmail".equalsIgnoreCase(evtType)){
				
				previewEmailTemplateInActions(templateId,OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_BONUS);

			}else if("previewSms".equalsIgnoreCase(evtType)){
				
				previewSMSTemplateInActions(templateId,OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_BONUS);
			}
		}
	}
	private void previewEmailTemplateInActions(Long emailTemplateId,String defaultMsgType) {
		
		String templateContent = "";
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		CustomTemplates customTemplates = ltyPrgmSevice.getCustomTemplate(emailTemplateId);
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
		Utility.showPreview(previewWin$html, GetUser.getUserObj().getUserName(), templateContent);
		previewWin.setVisible(true);
	
		}//previewEmailTemplate()
		public void previewSMSTemplateInActions(Long smsTemplateId,String defaultMsgType) {
				logger.info("---Entered previewSMSTemplateInActions-----");
				String templateContent = "";
				LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
				AutoSMS autoSMS = ltyPrgmSevice.getAutoSmsTemplateById(smsTemplateId);
				if(autoSMS == null) {
					templateContent = 	PropertyUtil.getPropertyValueFromDB(defaultMsgType);
				}else {
					templateContent = autoSMS.getMessageContent();
				}
				logger.info("templateContent 1----"+templateContent);
				Utility.showPreview(previewWin$html, GetUser.getUserObj().getUserName(), templateContent);
				previewWin.setVisible(true);
				logger.debug("--Exit previewSMSTemplateInActions---");
		}//previewSMSTemplate()


	private void export(LoyaltyCardSet loyaltyCardSet) {
		exportWinId.setAttribute("loyaltyCardSet", loyaltyCardSet);
		exportWinId.setVisible(true);
		exportWinId.setPosition("center");
		exportWinId.doHighlighted();
	}

	private void editSettings() {
		DecimalFormat f = new DecimalFormat("#0.00");
		Long prgmId=(Long) session.getAttribute("programId");
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		List<LoyaltyThresholdBonus> bonusList = ltyPrgmSevice.getBonusList(prgmId);
		if(bonusList!=null) {
			for(LoyaltyThresholdBonus loyaltyThresholdBonus:bonusList) {
				if(loyaltyThresholdBonus.getRegistrationFlag()=='Y') {
					List<Listitem> bonusTypeList=thresholdBonusWinId$bonusThresholdTypeLbId.getItems();
					for(Listitem listitem : bonusTypeList) {
						if(loyaltyThresholdBonus.getExtraBonusType().equalsIgnoreCase(listitem.getValue().toString())) {
							thresholdBonusWinId$bonusThresholdTypeLbId.setSelectedItem(listitem);
							break;
						}
					}

					if(thresholdBonusWinId$bonusThresholdTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)) {
						thresholdBonusWinId$bonusThresholdValueTbId.setValue(loyaltyThresholdBonus.getExtraBonusValue() == null ? "": loyaltyThresholdBonus.getExtraBonusValue().intValue()+"");
					}
					else {
						//thresholdBonusWinId$bonusThresholdValueTbId.setValue(loyaltyThresholdBonus.getExtraBonusValue() == null ? "": f.format(loyaltyThresholdBonus.getExtraBonusValue()));
						thresholdBonusWinId$bonusThresholdValueTbId.setValue(loyaltyThresholdBonus.getExtraBonusValue() == null ? "": loyaltyThresholdBonus.getExtraBonusValue().toString());
					}
					thresholdBonusWinId$bonusThresholdTypeLbId.setDisabled(true);
					thresholdBonusWinId$bonusThresholdValueTbId.setDisabled(true);
					thresholdBonusWinId$bonusaddBtnId.setLabel("Modify");
					thresholdBonusWinId$bonusCancelBtnId.setVisible(false);
				}else {
					thresholdBonusWinId$bonusValueTbId.setValue("");
					thresholdBonusWinId$levelValueTbId.setValue("");
					thresholdBonusWinId$thresholdLimitID.setValue("");
					thresholdBonusWinId$bonusTypeLbId.setSelectedIndex(0);
					thresholdBonusWinId$levelTypeLbId.setSelectedIndex(0);
				}
			}

		}
		thresholdBonusWinId$bonusValueTbId.setValue("");
		thresholdBonusWinId$levelValueTbId.setValue("");
		thresholdBonusWinId$thresholdLimitID.setValue("");
		thresholdBonusWinId$bonusTypeLbId.setSelectedIndex(0);
		thresholdBonusWinId$levelTypeLbId.setSelectedIndex(0);
		/*thresholdBonusWinId$giftAmtExpTypeLbId.setSelectedIndex(0);
		thresholdBonusWinId$giftAmtExpValueLbId.setSelectedIndex(0);
		thresholdBonusWinId$earnedRwrdExpiryCbId.setChecked(false);
		thresholdBonusWinId$rewardTierDivId.setVisible(false);*/
	}

	private void thresholdSettings(LoyaltyThresholdBonus loyaltyThresholdBonus,
			String type) {
		DecimalFormat f = new DecimalFormat("#0.00");
		isEdit=true;
		session.setAttribute("thresholdId", loyaltyThresholdBonus.getThresholdBonusId());
		if(loyaltyThresholdBonus!=null && type!=null) {
			List<Listitem> thresholdList=thresholdBonusWinId$bonusTypeLbId.getItems();
			for(Listitem li : thresholdList) {
				if(loyaltyThresholdBonus.getExtraBonusType().equalsIgnoreCase(li.getValue().toString())) {
					thresholdBonusWinId$bonusTypeLbId.setSelectedItem(li);
					break;
				}
			}
			List<Listitem> earnedList=thresholdBonusWinId$levelTypeLbId.getItems();
			for(Listitem li : earnedList) {
				if(loyaltyThresholdBonus.getEarnedLevelType().equalsIgnoreCase(li.getValue().toString())) {
					thresholdBonusWinId$levelTypeLbId.setSelectedItem(li);
					break;
				}
			}
			if(loyaltyThresholdBonus.isRecurring()) {
				//Listitem item=thresholdBonusWinId$levelValueLbId.getItemAtIndex(1);	
				thresholdBonusWinId$levelValueLbId.setSelectedIndex(1);
				if((thresholdBonusWinId$levelValueLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Increment"))
						&&(thresholdBonusWinId$levelTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("LPV"))){
					thresholdBonusWinId$limitDivId.setVisible(true);
					if(loyaltyThresholdBonus.getThresholdLimit()!=null){
						thresholdBonusWinId$thresholdLimitID.setValue(loyaltyThresholdBonus.getThresholdLimit().longValue()+"");	
					}
				}
				
			}else if(!loyaltyThresholdBonus.isRecurring()){
				thresholdBonusWinId$levelValueLbId.setSelectedIndex(0);
				thresholdBonusWinId$limitDivId.setVisible(false);
				
			}

			if(thresholdBonusWinId$bonusTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)) {
				thresholdBonusWinId$bonusValueTbId.setValue(loyaltyThresholdBonus.getExtraBonusValue().intValue()+"");
			}else {

				thresholdBonusWinId$bonusValueTbId.setValue(f.format(loyaltyThresholdBonus.getExtraBonusValue()));
			}

			if(thresholdBonusWinId$levelTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)
					|| thresholdBonusWinId$levelTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.THRESHOLD_TYPE_TIER)) {
				thresholdBonusWinId$levelValueTbId.setValue(loyaltyThresholdBonus.getEarnedLevelValue().intValue()+"");

			}else {
				thresholdBonusWinId$levelValueTbId.setValue(f.format(loyaltyThresholdBonus.getEarnedLevelValue()));
			}
			if(thresholdBonusWinId$levelValueLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Increment")){
				if(loyaltyThresholdBonus.getThresholdLimit()!=null){
				thresholdBonusWinId$thresholdLimitID.setValue(loyaltyThresholdBonus.getThresholdLimit().intValue()+"");	
				}else{
					thresholdBonusWinId$thresholdLimitID.setValue("");
				}
			}
		}
		getLoyaltyTemplateList(thresholdBonusWinId$selBonusAutoEmailsLbId, "earnedBonus");
		getLoyaltySmsTemplateList(thresholdBonusWinId$selBonusAutoSmsLbId,"earningBonus");
		getLoyaltyTemplateList(thresholdBonusWinId$selRewardAutoEmailsLbId,"earnedRewardExpiration");
		getLoyaltySmsTemplateList(thresholdBonusWinId$selRewardAutoSmsLbId,"earnedRewardExpiration");
		if(loyaltyThresholdBonus.getEmailTempId()!=null){
			
			setBonusValuesInEdit(loyaltyThresholdBonus);
		}
		if(loyaltyThresholdBonus.getSmsTempId()!=null){
			
			setSMSBonusValuesInEdit(loyaltyThresholdBonus);
		}
		if(loyaltyThresholdBonus.getEmailExpiryTempId()!=null) setRewardExpValues(loyaltyThresholdBonus);
		if(loyaltyThresholdBonus.getSmsExpiryTempId()!=null)	setSMSRewardExpValues(loyaltyThresholdBonus);
		
		
		onSelect$selBonusAutoEmailsLbId$thresholdBonusWinId();
		onSelect$selBonusAutoSmsLbId$thresholdBonusWinId();
		onSelect$selRewardAutoSmsLbId$thresholdBonusWinId();
		onSelect$selRewardAutoEmailsLbId$thresholdBonusWinId();
		
		if(loyaltyThresholdBonus.getBonusExpiryDateType()!=null && loyaltyThresholdBonus.getBonusExpiryDateValue()!=null) {
			thresholdBonusWinId$earnedRwrdExpiryCbId.setChecked(true);
			thresholdBonusWinId$rewardTierDivId.setVisible(true);
			thresholdBonusWinId$expiryAutoCommDiv.setVisible(true);
			thresholdBonusWinId$giftAmtExpTypeLbId.setSelectedIndex(0);
			if(thresholdBonusWinId$earnedRwrdExpiryCbId.isChecked()) {
				thresholdBonusWinId$giftAmtExpValueTbId.setValue(loyaltyThresholdBonus.getBonusExpiryDateValue().toString());
			}else {
				thresholdBonusWinId$giftAmtExpValueTbId.setValue("");
			}
		}else{
			thresholdBonusWinId$earnedRwrdExpiryCbId.setChecked(false);
			thresholdBonusWinId$rewardTierDivId.setVisible(false);
			thresholdBonusWinId$expiryAutoCommDiv.setVisible(false);
		}
		
		
		
	}

	
	private void getLoyaltyTemplateList(Listbox emailLbid, String type) {

		logger.debug("---Entered getLoyaltyTemplateList---");
		List<CustomTemplates> templatelist = null;
		try {
			LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
			templatelist = ltyPrgmSevice.getTemplateList(GetUser.getUserObj().getUserId(),type);
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
	
		
	}
	
	/**
	 * setting the earned bonus email template 
	 * @param loyaltyAutoComm
	 */
	private void setBonusValuesInEdit(LoyaltyThresholdBonus loyaltyThresholdBonus) {
		logger.debug("---Entered setBonusValues---");
		List<Listitem> thresholdBonusList = thresholdBonusWinId$selBonusAutoEmailsLbId.getItems();
		getDefaultValueForOldRecords(loyaltyThresholdBonus.getEmailTempId(),thresholdBonusWinId$selBonusAutoEmailsLbId);
		for(Listitem li :thresholdBonusList ) {
			Long threshBonusEmailTmpltId=li.getValue();
			if(threshBonusEmailTmpltId != null) {
				if(threshBonusEmailTmpltId.equals(loyaltyThresholdBonus.getEmailTempId())) {
					thresholdBonusWinId$selBonusAutoEmailsLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}
		}
		logger.debug("---Exit setBonusValues---");
	}//setBonusValues()
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
			smsTemplateList = ltyPrgmSevice.getSmsTemplateList(userId,type);
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

	
	private void getDefaultValueForOldRecords(Long tmplatetId, Listbox selRegAutoEmailsLabelbId) {
		if(tmplatetId!= null && tmplatetId.equals(-1L)) {
			Listitem item = new Listitem("Default Message", new Long(-1));
			item.setParent(selRegAutoEmailsLabelbId);
		}
	}
	
	/**
	 * setting the earning bonus SMS template 
	 * @param loyaltyAutoComm
	 */
	private void setSMSBonusValuesInEdit(LoyaltyThresholdBonus loyaltyThresholdBonus) {
		logger.debug("---Entered setSMSBonusValues---");
		List<Listitem> thresholdBonusSmsList = thresholdBonusWinId$selBonusAutoSmsLbId.getItems();

		for(Listitem li : thresholdBonusSmsList ) {
			Long threshBonusSmsTmpltId=li.getValue();
			if(threshBonusSmsTmpltId != null) {
				if(threshBonusSmsTmpltId.equals(loyaltyThresholdBonus.getSmsTempId())) {
					thresholdBonusWinId$selBonusAutoSmsLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}
		}
		logger.debug("---Exit setSMSBonusValues---");
	}//setSMSBonusValues()
	
	public void onSelect$selBonusAutoEmailsLbId$thresholdBonusWinId() {
		Boolean flag = false;
		if(thresholdBonusWinId$selBonusAutoEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto Email")) {
			thresholdBonusWinId$previewThresholdEmailImgIdForBonus.setVisible(false);
			thresholdBonusWinId$bonusEmailPreviewBtnId.setVisible(false);
			thresholdBonusWinId$editThresholdEmailImgIdForBonus.setVisible(false);
			thresholdBonusWinId$bonusEmailEditMsgBtnId.setVisible(false);
			thresholdBonusWinId$addThresholdEmailImgId.setVisible(true);
			thresholdBonusWinId$bonusEmailAddBtnId.setVisible(true);
			flag = true;
		}else{
			thresholdBonusWinId$previewThresholdEmailImgIdForBonus.setVisible(true);
			thresholdBonusWinId$bonusEmailPreviewBtnId.setVisible(true);
			thresholdBonusWinId$editThresholdEmailImgIdForBonus.setVisible(true);
			thresholdBonusWinId$bonusEmailEditMsgBtnId.setVisible(true);
			thresholdBonusWinId$addThresholdEmailImgId.setVisible(true);
			thresholdBonusWinId$bonusEmailAddBtnId.setVisible(true);
		}
		if(thresholdBonusWinId$selBonusAutoEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Default Message")) {
			thresholdBonusWinId$editThresholdEmailImgIdForBonus.setVisible(false);
			thresholdBonusWinId$bonusEmailEditMsgBtnId.setVisible(false);
		}else if(!flag) {
			thresholdBonusWinId$editThresholdEmailImgIdForBonus.setVisible(true);
			thresholdBonusWinId$bonusEmailEditMsgBtnId.setVisible(true);
		}

	}//onSelect$selBonusAutoEmailsLbId()
	
	public void onSelect$selBonusAutoSmsLbId$thresholdBonusWinId() {

		if(thresholdBonusWinId$selBonusAutoSmsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto SMS")) {
			thresholdBonusWinId$previewThresholdSmsImgIdForBonus.setVisible(false);
			thresholdBonusWinId$bonusSmsPreviewBtnId.setVisible(false);
			thresholdBonusWinId$editThresholdSmsImgIdForBonus.setVisible(false);
			thresholdBonusWinId$bonusSmsEditMsgBtnId.setVisible(false);
			thresholdBonusWinId$addThresholdSmsImgId.setVisible(true);
			thresholdBonusWinId$bonusSmsAddBtnId.setVisible(true);

		}else{
			thresholdBonusWinId$addThresholdSmsImgId.setVisible(true);
			thresholdBonusWinId$bonusSmsAddBtnId.setVisible(true);
			thresholdBonusWinId$previewThresholdSmsImgIdForBonus.setVisible(true);
			thresholdBonusWinId$bonusSmsPreviewBtnId.setVisible(true);
			thresholdBonusWinId$editThresholdSmsImgIdForBonus.setVisible(true);
			thresholdBonusWinId$bonusSmsEditMsgBtnId.setVisible(true);
		}

	}//onSelect$selBonusAutoSmsLbId()
	
	/**
	 * Preview the template of earning bonus
	 */
	public void onClick$bonusSmsPreviewBtnId$thresholdBonusWinId() {
		logger.debug("---Entered onClick$thresholdBonusWinId---");
		previewSMSTemplate(thresholdBonusWinId$selBonusAutoSmsLbId,OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_BONUS);
		logger.debug("---Exit onClick$thresholdBonusWinId$bonusSmsPreviewBtnId---");
	}//onClick$bonusSmsPreviewBtnId()

	/**
	 * Edits the template of earning bonus
	 */
	public void onClick$bonusSmsEditMsgBtnId$thresholdBonusWinId() {
		logger.debug("---Entered onClick$thresholdBonusWinId$bonusSmsEditMsgBtnId---");
		editSMSTemplate(thresholdBonusWinId$selBonusAutoSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_EARNED_BONUS);
		logger.debug("---Exit onClick$thresholdBonusWinId$bonusSmsEditMsgBtnId---");
	}//onClick$bonusSmsEditMsgBtnId()

	/**
	 * To add a new template for earning bonus
	 */
	public void onClick$bonusSmsAddBtnId$thresholdBonusWinId() {
		logger.debug("---Entered onClick$thresholdBonusWinId$bonusSmsAddBtnId---");
		addSMSTemplate(thresholdBonusWinId$selBonusAutoSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_EARNED_BONUS);
		logger.debug("---Exit onClick$thresholdBonusWinId$bonusSmsAddBtnId---");
	}//onClick$bonusSmsAddBtnId()
	
	/**
	 * Preview the template of earning bonus
	 */
	public void onClick$bonusEmailPreviewBtnId$thresholdBonusWinId() {
		logger.debug("---Entered onClick$thresholdBonusWinId$bonusEmailPreviewBtnId---");
		previewEmailTemplate(thresholdBonusWinId$selBonusAutoEmailsLbId,OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_BONUS);
		logger.debug("---Exit onClick$thresholdBonusWinId$bonusEmailPreviewBtnId---");
	}//onClick$bonusEmailPreviewBtnId()

	/**
	 * Edits the template of earning bonus
	 */
	public void onClick$bonusEmailEditMsgBtnId$thresholdBonusWinId() {
		logger.debug("---Entered onClick$thresholdBonusWinId$bonusEmailEditMsgBtnId---");
		editEmailTemplate(thresholdBonusWinId$selBonusAutoEmailsLbId,Constants.CUSTOM_TEMPLATE_TYPE_EARNED_BONUS);
		logger.debug("---Exit onClick$thresholdBonusWinId$bonusEmailEditMsgBtnId---");
	}//onClick$bonusEmailEditMsgBtnId()

	/**
	 * To add a new template for earning bonus
	 */
	public void onClick$bonusEmailAddBtnId$thresholdBonusWinId() {
		logger.debug("---Entered onClick$thresholdBonusWinId$bonusEmailAddBtnId---");
		addEmailTemplate(thresholdBonusWinId$selBonusAutoEmailsLbId,Constants.CUSTOM_TEMPLATE_TYPE_EARNED_BONUS);
		logger.debug("---Exit onClick$thresholdBonusWinId$bonusEmailAddBtnId---");
	}//onClick$bonusEmailAddBtnId()

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
			Utility.showPreview(previewWin$html, GetUser.getUserObj().getUserName(), templateContent);
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
			Utility.showPreview(previewWin$html, GetUser.getUserObj().getUserName(), templateContent);
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
	
	
	public void onClick$addCardSetAnchId() {
		winId.setVisible(true);
		winId.setPosition("center");
		winId.doHighlighted();
	}
	
	public void onCheck$ltyCardTypeRadioGrId$winId() {
		if(winId$ltyCardTypeRadioGrId.getSelectedItem().getValue().toString().equalsIgnoreCase("Physical")) {
			winId$autoAssignRadioId.setSelected(true);
			winId$linkTierRadioId.setSelected(false);
			winId$autoAssignRadioId.setDisabled(false);
			winId$linkTierRadioId.setDisabled(false);
			winId$selTierLbId.setDisabled(true);
			winId$selTierLbId.setSelectedIndex(0);
			onCheck$tierAssignmentRadioGrId$winId();
		}
		else if(winId$ltyCardTypeRadioGrId.getSelectedItem().getValue().toString().equalsIgnoreCase("Virtual")) {
			winId$autoAssignRadioId.setSelected(true);
			winId$linkTierRadioId.setSelected(false);
			winId$autoAssignRadioId.setDisabled(true);
			winId$linkTierRadioId.setDisabled(true);
			winId$selTierLbId.setDisabled(true);
			winId$selTierLbId.setSelectedIndex(0);
		}
	}//onCheck$ltyCardTypeRadioGrId$winId()
	
	public void onCheck$tierAssignmentRadioGrId$winId() {
		if(winId$tierAssignmentRadioGrId.getSelectedItem().getValue().toString().equalsIgnoreCase("auto")) {
			winId$selTierLbId.setDisabled(true);
			winId$selTierLbId.setSelectedIndex(0);
		}
		else if(winId$tierAssignmentRadioGrId.getSelectedItem().getValue().toString().equalsIgnoreCase("link")) {
			Long prgmId = (Long) session.getAttribute("programId");
			LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
			LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
			if(prgmObj.getTierEnableFlag() == OCConstants.FLAG_NO) {
				MessageUtil.setMessage("Card-set cannot be linked to a particular tier level as the program is a non-tiered program.", "color:red", "TOP");
				winId$autoAssignRadioId.setSelected(true);
				winId$linkTierRadioId.setSelected(false);
				return;
			}
			winId$selTierLbId.setDisabled(false);
			winId$selTierLbId.setSelectedIndex(0);
		}
	}//onCheck$tierAssignmentRadioGrId$winId()

	public void onClick$addBtnId$winId(){
		Long prgmId = (Long) session.getAttribute("programId");
		String cardSetName = winId$cardSetNameTbId.getValue().trim();
		if(cardSetName == null || cardSetName.trim().length() == 0) {
			MessageUtil.setMessage("Card-set name cannot be empty.", "color:red", "TOP");
			return;
		}else if(!Utility.validateBy(Constants.LTY_NAME_PATTERN,cardSetName)) {
			MessageUtil.setMessage("Enter valid card-set name.","color:red");
			return;
		}
		if(winId$cardSetNameTbId.getValue().trim().length() > 60) {
			MessageUtil.setMessage("Card-set name exceeds the maximum characters limit.", "color:red", "TOP");
			return;
		}
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		List<LoyaltyCardSet> cardList=ltyPrgmSevice.getCardsetList(prgmId);
		if(cardList != null) {
			for(LoyaltyCardSet loyaltyCardSet:cardList) {
				if(loyaltyCardSet.getCardSetName().equalsIgnoreCase(winId$cardSetNameTbId.getValue().trim())) {
					MessageUtil.setMessage("Card-set name already exists.", "color:red", "TOP");
					return;
				}
			}
		}

		if(winId$quantityNameTbId.getValue().trim().length() > 0) {
			if(!checkIfNum(winId$quantityNameTbId.getValue().trim())) {
				MessageUtil.setMessage("Please provide number value for quantity.", "red");
				return;
			}
		}
		if(winId$quantityNameTbId.getValue() == null || winId$quantityNameTbId.getValue().trim().length() == 0 || Long.parseLong(winId$quantityNameTbId.getValue().trim()) <= 0){
			MessageUtil.setMessage("Quantity cannot be empty or zero.", "color:red", "TOP");
			return;
		}
		if(winId$quantityNameTbId.getValue().trim().length() > 60) {
			MessageUtil.setMessage("Quantity value exceeds the maximum characters limit.", "color:red", "TOP");
			return;
		}
		if(LoyaltyProgramHelper.anotherCardGeneration(prgmId)){
			MessageUtil.setMessage("Card generation is already underway by organisation user. Please try again after some time.", "color:blue", "TOP");
			return;
		}

		String quantity = winId$quantityNameTbId.getValue().trim();
		String cardsetType = "";
		int linkedTierLevel = 0;
		if(winId$ltyCardTypeRadioGrId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_CARDSET_TYPE_PHYSICAL)) {
			cardsetType = OCConstants.LOYALTY_CARDSET_TYPE_PHYSICAL;
			linkedTierLevel = Integer.parseInt(winId$selTierLbId.getSelectedItem().getValue().toString());
		}
		else if(winId$ltyCardTypeRadioGrId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_CARDSET_TYPE_VIRTUAL)) {
			cardsetType = OCConstants.LOYALTY_CARDSET_TYPE_VIRTUAL;
		}
		Listbox genTypeLbId=winId$genTypeLbId;
		String genTyp = genTypeLbId.getSelectedItem().getValue().toString();
		Listbox statusLbId=winId$statusLbId;
		String status = statusLbId.getSelectedItem().getValue().toString();
		char migrationFlag = OCConstants.FLAG_NO;
		LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
		String draftStatus = "";
		if (prgmObj.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_DRAFT)) {
			draftStatus = prgmObj.getDraftStatus();
			String[] draftList = draftStatus.split(Constants.ADDR_COL_DELIMETER);
			draftList[2] = OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE;
			draftStatus = "";
			for (String eachStr : draftList) {

				if (draftStatus.isEmpty()) {
					draftStatus = eachStr;
				} else {
					draftStatus += Constants.ADDR_COL_DELIMETER + eachStr;
				}
			}

			prgmObj.setDraftStatus(draftStatus);
			ltyPrgmSevice.savePrgmObj(prgmObj);

		}
		ltyPrgmSevice.onAddCardSet(cardSetName,quantity,cardsetType,genTyp,status,migrationFlag,prgmId,userId,linkedTierLevel,OCConstants.LOYALTY_CARD_GENERATION_TYPE_SYSTEM);
		MessageUtil.setMessage("Card-set added successfully.", "color:blue", "TOP");
		List<LoyaltyCardSet> list = ltyPrgmSevice.getCardsetList(prgmId);
		redrawCardSetList(list);
		winId$cardSetNameTbId.setValue("");
		winId$quantityNameTbId.setValue("");
		winId$ltyCardTypeRadioGrId.setSelectedIndex(0);
		genTypeLbId.setSelectedIndex(0);
		statusLbId.setSelectedIndex(0);
		onCheck$ltyCardTypeRadioGrId$winId();
		winId.setVisible(false);

	}


	private boolean checkIfNum(String in) {
		try {
			Long.parseLong(in);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}


	public boolean checkIfNumber(String in) {
		try {
			Double.parseDouble(in);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}// checkIfNumber

	public void onClick$cancelBtnId$winId() {
		winId$cardSetNameTbId.setValue("");
		winId$quantityNameTbId.setValue("");
		winId$genTypeLbId.setSelectedIndex(0);
		winId$ltyCardTypeRadioGrId.setSelectedIndex(0);
		winId$statusLbId.setSelectedIndex(0);
		winId.setVisible(false);
	}

	public void onClick$exportBtnId$exportWinId() {

		LoyaltyCardSet loyaltyCardSet = (LoyaltyCardSet) exportWinId.getAttribute("loyaltyCardSet");
		String cardIdStr=loyaltyCardSet.getCardSetId().toString();
		Listitem li=exportWinId$statusLbId.getSelectedItem();
		String status=li.getValue().toString();
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		long size = ltyPrgmSevice.getCardsCount(cardIdStr,status,prgmId); 
		logger.info("prgmId in LoyaltyPrgmOverviewController is  ::"+prgmId);

		logger.info("total size is  ::"+size);
		if(size == 0l){
			MessageUtil.setMessage("No Cards exists.","color:red","TOP");
			return;
		}

		JdbcResultsetHandler jdbcResultSetHandler = null;
		try {

			String userName = GetUser.getUserName();
			String usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
			String exportDir = usersParentDirectory + "/" + userName + "/Export/Cards" ;
			File downloadDir = new File(exportDir);

			StringBuffer sb = null;
			if(downloadDir.exists()){
				try {
					FileUtils.deleteDirectory(downloadDir);
					logger.debug(downloadDir.getName() + " is deleted");
				} catch (Exception e) {
					logger.error("Exception ::" , e);
					logger.debug(downloadDir.getName() + " is not deleted");
				}
			}
			if(!downloadDir.exists()){
				downloadDir.mkdirs();
			}

			String filePath = exportDir +File.separator+  "LoyaltyCards_" +	MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_YEARTOSEC, clientTimeZone);

			filePath = filePath + ".csv";
			logger.debug("Download File path : " + filePath);
			File file = new File(filePath);
			BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
			bw.write("\"Card Number\",\"Card Pin\",\"Status\",\r\n");

			jdbcResultSetHandler = new JdbcResultsetHandler();

			String sqlqry = "";

			if(status==null || status.trim().equalsIgnoreCase("All")) {
				sqlqry = "select card_number,card_pin,status from loyalty_cards where card_set_id in ("+cardIdStr+") AND program_id="+prgmId+" ";
			}else if(status != null && status.trim().equalsIgnoreCase("Registered")) {
				sqlqry = "select card_number,card_pin,status from loyalty_cards where card_set_id in ("+cardIdStr+") and registered_flag = '"+OCConstants.FLAG_YES+"' AND program_id="+prgmId+" ";
			}
			else{
				sqlqry = "select card_number,card_pin,status from loyalty_cards where card_set_id in ("+cardIdStr+") and status ='"+status.trim()+"' AND program_id="+prgmId+" ";
			}
			logger.info("sqlqry ==== "+sqlqry);
			jdbcResultSetHandler.executeStmt(sqlqry);

			List<String> recordList = jdbcResultSetHandler.getRecords();
			if(recordList == null) {
				logger.debug("Error getting from jdbcResulSetHandler  ...");
			}
			logger.info("record list is "+recordList.size());
			int countingCount = recordList.size();

			while(countingCount <= size ){
				sb = new StringBuffer();

				for (String eachRecord : recordList) {

					logger.info("eachRecord"+eachRecord);
					String[] strArr = eachRecord.split(";");
					sb.append("\"");sb.append(strArr[0].replace("card_number=", ""));sb.append("\",");
					sb.append("\"");sb.append(strArr[1].replace("card_pin=", ""));sb.append("\",");
					sb.append("\"");sb.append(strArr[2].replace("status=", ""));sb.append("\"");
					sb.append("\r\n");

				}

				bw.write(sb.toString());
				sb = null;
				//System.gc();
				recordList = jdbcResultSetHandler.getRecords();
				if(recordList == null) break;
				countingCount += recordList != null ?recordList.size() :0;
				logger.info("countingCount"+countingCount);
			}

			bw.flush();
			bw.close();
			Filedownload.save(file, "text/plain");

		} catch (Exception e) {
			logger.error("Error  :: ",e);
		}finally{
			if(jdbcResultSetHandler != null)jdbcResultSetHandler.destroy();
		}
		exportWinId$statusLbId.setSelectedIndex(0);
		exportWinId.setVisible(false);
	}

	public void onClick$cancelBtnId$exportWinId() {
		exportWinId$statusLbId.setSelectedIndex(0);
		exportWinId.setVisible(false);
	}


	public void onClick$editTierAnchId(){
		Long prgmId=(Long) session.getAttribute("programId");
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		LoyaltyProgram progmObj = ltyPrgmSevice.getProgmObj(prgmId);
		List<LoyaltyProgramTier> tierList = ltyPrgmSevice.getTierList(prgmId); 
		if(tierList != null){
			if(tierList.size()== progmObj.getNoOfTiers()){
				MessageUtil.setMessage("All the tiers are defined, please change the no. of tiers in step-1 to add more tiers.", "color:blue", "Top");
				return;
			}
		}
		session.removeAttribute("loyaltyProgramTier");
		if(progmObj.getRewardType()!=null && progmObj.getRewardType().equalsIgnoreCase(OCConstants.REWARD_TYPE_PERK))
			Redirect.goTo(PageListEnum.LOYALTY_PERK_RULES);
		else 
		    Redirect.goTo(PageListEnum.LOYALTY_RULES);
	}


	public void onClick$addThresholdAnchId() {
		DecimalFormat f = new DecimalFormat("#0.00");
		thresholdBonusWinId.setVisible(true);
		thresholdBonusWinId.setPosition("center");
		thresholdBonusWinId.doHighlighted();
		isEdit=false;
		Long prgmId=(Long) session.getAttribute("programId");
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		List<LoyaltyThresholdBonus> bonusList = ltyPrgmSevice.getBonusList(prgmId);
		if(bonusList!=null) {
			for(LoyaltyThresholdBonus loyaltyThresholdBonus:bonusList) {
				if(loyaltyThresholdBonus.getRegistrationFlag()==OCConstants.FLAG_YES) {
					List<Listitem> bonusTypeList=thresholdBonusWinId$bonusThresholdTypeLbId.getItems();
					for(Listitem listitem : bonusTypeList) {
						if(loyaltyThresholdBonus.getExtraBonusType().equalsIgnoreCase(listitem.getValue().toString())) {
							thresholdBonusWinId$bonusThresholdTypeLbId.setSelectedItem(listitem);
							break;
						}
					}

					if(thresholdBonusWinId$bonusThresholdTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)) {
						thresholdBonusWinId$bonusThresholdValueTbId.setValue(loyaltyThresholdBonus.getExtraBonusValue() == null ?"" :loyaltyThresholdBonus.getExtraBonusValue().intValue()+"");

					}
					else {
						//thresholdBonusWinId$bonusThresholdValueTbId.setValue(loyaltyThresholdBonus.getExtraBonusValue() == null ?"" :f.format(loyaltyThresholdBonus.getExtraBonusValue()));
						thresholdBonusWinId$bonusThresholdValueTbId.setValue(loyaltyThresholdBonus.getExtraBonusValue() == null ?"" : loyaltyThresholdBonus.getExtraBonusValue().toString());
					}
					thresholdBonusWinId$bonusThresholdTypeLbId.setDisabled(true);
					thresholdBonusWinId$bonusThresholdValueTbId.setDisabled(true);
					thresholdBonusWinId$bonusaddBtnId.setLabel("Modify");
					thresholdBonusWinId$bonusCancelBtnId.setVisible(false);
				}else {
					thresholdBonusWinId$bonusThresholdTypeLbId.setSelectedIndex(0);
					thresholdBonusWinId$bonusThresholdValueTbId.setValue("");;
					thresholdBonusWinId$bonusThresholdTypeLbId.setDisabled(false);
					thresholdBonusWinId$bonusThresholdValueTbId.setDisabled(false);
					thresholdBonusWinId$bonusaddBtnId.setLabel("Add");
					thresholdBonusWinId$bonusValueTbId.setValue("");
					thresholdBonusWinId$levelValueTbId.setValue("");
					thresholdBonusWinId$bonusTypeLbId.setSelectedIndex(0);
					thresholdBonusWinId$levelTypeLbId.setSelectedIndex(0);
					if(thresholdBonusWinId$earnedRwrdExpiryCbId.isChecked()){
						
						thresholdBonusWinId$giftAmtExpTypeLbId.setSelectedIndex(0);
						thresholdBonusWinId$giftAmtExpValueTbId.setValue("");
						thresholdBonusWinId$earnedRwrdExpiryCbId.setChecked(false);
						thresholdBonusWinId$rewardTierDivId.setVisible(false);
						thresholdBonusWinId$expiryAutoCommDiv.setVisible(false);
					}
				}
			}
		}
		else {
			thresholdBonusWinId$bonusThresholdTypeLbId.setSelectedIndex(0);
			thresholdBonusWinId$bonusThresholdValueTbId.setValue("");;
			thresholdBonusWinId$bonusThresholdTypeLbId.setDisabled(false);
			thresholdBonusWinId$bonusThresholdValueTbId.setDisabled(false);
			thresholdBonusWinId$bonusaddBtnId.setLabel("Add");
			thresholdBonusWinId$bonusValueTbId.setValue("");
			thresholdBonusWinId$levelValueTbId.setValue("");
			thresholdBonusWinId$bonusTypeLbId.setSelectedIndex(0);
			thresholdBonusWinId$levelTypeLbId.setSelectedIndex(0);
			thresholdBonusWinId$expiryAutoCommDiv.setVisible(false);
		}
		
		getLoyaltyTemplateList(thresholdBonusWinId$selBonusAutoEmailsLbId, "earnedBonus");
		getLoyaltySmsTemplateList(thresholdBonusWinId$selBonusAutoSmsLbId,"earningBonus");
		getLoyaltyTemplateList(thresholdBonusWinId$selRewardAutoEmailsLbId,"earnedRewardExpiration");
		getLoyaltySmsTemplateList(thresholdBonusWinId$selRewardAutoSmsLbId,"earnedRewardExpiration");
		
		onSelect$selBonusAutoEmailsLbId$thresholdBonusWinId();
		onSelect$selBonusAutoSmsLbId$thresholdBonusWinId();
		onSelect$selRewardAutoSmsLbId$thresholdBonusWinId();
		onSelect$selRewardAutoEmailsLbId$thresholdBonusWinId();
		
		
	}


	public void onClick$bonusCancelBtnId$thresholdBonusWinId() {
		
		DecimalFormat f = new DecimalFormat("#0.00");
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		Long programId = (Long)session.getAttribute("programId");
		LoyaltyThresholdBonus  loyaltyThresholdBonus = ltyPrgmSevice.getThresholdObj(programId);
		if(loyaltyThresholdBonus != null) {
			List<Listitem> bonusTypeList = thresholdBonusWinId$bonusThresholdTypeLbId.getItems();
			for(Listitem listitem : bonusTypeList) {
				if(loyaltyThresholdBonus.getExtraBonusType().equalsIgnoreCase(listitem.getValue().toString())) {
					thresholdBonusWinId$bonusThresholdTypeLbId.setSelectedItem(listitem);
					break;
				}
			}
			if(thresholdBonusWinId$bonusThresholdTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)) {
				thresholdBonusWinId$bonusThresholdValueTbId.setValue(loyaltyThresholdBonus.getExtraBonusValue().intValue()+"");
			}else {
				//thresholdBonusWinId$bonusThresholdValueTbId.setValue(f.format(loyaltyThresholdBonus.getExtraBonusValue()));
				thresholdBonusWinId$bonusThresholdValueTbId.setValue(loyaltyThresholdBonus.getExtraBonusValue().toString());
			}
		}
		
		thresholdBonusWinId$bonusThresholdTypeLbId.setDisabled(true);
		thresholdBonusWinId$bonusThresholdValueTbId.setDisabled(true);
		thresholdBonusWinId$bonusaddBtnId.setLabel("Modify");
		thresholdBonusWinId$bonusCancelBtnId.setVisible(false);
	}


	public void onClick$bonusaddBtnId$thresholdBonusWinId(){
		if(thresholdBonusWinId$bonusThresholdValueTbId.isDisabled()) {
			thresholdBonusWinId$bonusThresholdTypeLbId.setDisabled(false);
			thresholdBonusWinId$bonusThresholdValueTbId.setDisabled(false);
			thresholdBonusWinId$bonusaddBtnId.setLabel("Save");
			thresholdBonusWinId$bonusCancelBtnId.setVisible(true);
		}else {

			if(thresholdBonusWinId$bonusThresholdValueTbId.getValue().trim().isEmpty() || thresholdBonusWinId$bonusThresholdValueTbId.getValue()==null ) {
				MessageUtil.setMessage("Bonus on enrollment value cannot be empty.", "red");
				return ;
			}

			if(thresholdBonusWinId$bonusThresholdValueTbId.getValue().trim().length() > 60) {
				MessageUtil.setMessage("Bonus on enrollment value exceeds the maximum characters limit.", "red");
				return ;
			}


			if(thresholdBonusWinId$bonusThresholdValueTbId.getValue().trim().length() > 0) {

				if(thresholdBonusWinId$bonusThresholdTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)){
					if(!checkIfNum(thresholdBonusWinId$bonusThresholdValueTbId.getValue().trim())) {
						MessageUtil.setMessage("Please provide number value for bonus on enrollment.", "red");
						thresholdBonusWinId$bonusThresholdValueTbId.setFocus(true);
						return ;
					}
					
					if(Integer.parseInt(thresholdBonusWinId$bonusThresholdValueTbId.getValue()) <= 0) {
						MessageUtil.setMessage("Please provide valid value for bonus on enrollment.", "red");
						thresholdBonusWinId$bonusThresholdValueTbId.setFocus(true);
						return;
					}
				}
				else {
					if(!checkIfNumber(thresholdBonusWinId$bonusThresholdValueTbId.getValue().trim())) {
						MessageUtil.setMessage("Please provide number value for bonus on enrollment.", "red");
						thresholdBonusWinId$bonusThresholdValueTbId.setFocus(true);
						return ;
					}
					if(Double.parseDouble(thresholdBonusWinId$bonusThresholdValueTbId.getValue()) <= 0) {
						MessageUtil.setMessage("Please provide valid value for bonus on enrollment.", "red");
						thresholdBonusWinId$bonusThresholdValueTbId.setFocus(true);
						return;
					}
				}

			}
			Long thresholdId=null;
			char registrationFlag=OCConstants.FLAG_YES;
			Double extraBonusValue=null;
			Long programId=(Long) session.getAttribute("programId");
			LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
			LoyaltyThresholdBonus  loyaltyThresholdBonus=ltyPrgmSevice.getThresholdObj(programId);
			if(loyaltyThresholdBonus!=null) {
				thresholdId=loyaltyThresholdBonus.getThresholdBonusId();
			}
			String extraBonusType=thresholdBonusWinId$bonusThresholdTypeLbId.getSelectedItem().getValue().toString();
			if(thresholdBonusWinId$bonusThresholdValueTbId.getValue()!=null && !thresholdBonusWinId$bonusThresholdValueTbId.getValue().trim().isEmpty()) {
				String amtRegExp = "^[0-9]*(\\.[0-9]{1,2})?$";
				Pattern pattern = Pattern.compile(amtRegExp);  
				Matcher m = pattern.matcher(thresholdBonusWinId$bonusThresholdValueTbId.getValue().trim());
				String match = "";
				while (m.find()) {
					match += m.group();
				}
				try {
					String value  = ""+Double.parseDouble(match);
				} catch (NumberFormatException e) {
					MessageUtil.setMessage("Currency value should not exceed two decimal points.", "Color:red", "Top");
					thresholdBonusWinId$bonusThresholdValueTbId.setFocus(true);
					return;
				}	
				//extraBonusValue=Double.parseDouble(thresholdBonusWinId$bonusThresholdValueTbId.getValue());
				extraBonusValue=Double.parseDouble(Utility.truncateUptoTwoDecimal(Double.parseDouble(thresholdBonusWinId$bonusThresholdValueTbId.getValue())));
			}
			ltyPrgmSevice.saveBonus(programId, extraBonusType, extraBonusValue, null, null,thresholdId,registrationFlag,userId);
			List<LoyaltyThresholdBonus> bonusList = ltyPrgmSevice.getBonusList(programId);
			redrawBonusList(bonusList);
			thresholdBonusWinId$bonusThresholdTypeLbId.setDisabled(true);
			thresholdBonusWinId$bonusThresholdValueTbId.setDisabled(true);
			thresholdBonusWinId$bonusaddBtnId.setLabel("Modify");
			thresholdBonusWinId$bonusCancelBtnId.setVisible(false);
			thresholdBonusWinId.setVisible(false);
		}
	}


	public void onClick$addBtnId$thresholdBonusWinId() {
		Long thresholdId= null;
		Double extraBonusValue=null;
		Double earnedLevelValue=null;
		boolean isRecurring=false;
		char registrationFlag=OCConstants.FLAG_NO;

		if(thresholdBonusWinId$bonusValueTbId.getValue()==null || thresholdBonusWinId$bonusValueTbId.getValue().trim().isEmpty()
				|| thresholdBonusWinId$levelValueTbId.getValue()==null || thresholdBonusWinId$levelValueTbId.getValue().trim().isEmpty() ) {
			MessageUtil.setMessage("Bonus on threshold value cannot be empty.", "red");
			return;
		}

		if(thresholdBonusWinId$bonusValueTbId.getValue().trim().length() > 60 || thresholdBonusWinId$levelValueTbId.getValue().trim().length() > 60 ) {
			MessageUtil.setMessage("Bonus on threshold value exceeds the maximum characters.", "red");
			return;
		}

		if(thresholdBonusWinId$bonusValueTbId.getValue().trim().length() > 0 || thresholdBonusWinId$levelValueTbId.getValue().trim().length() > 0) {

			if(thresholdBonusWinId$bonusTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)) {

				if(!checkIfNum(thresholdBonusWinId$bonusValueTbId.getValue().trim())) {
					MessageUtil.setMessage("Please provide number value for bonus on threshold.", "red");
					thresholdBonusWinId$bonusValueTbId.setFocus(true);
					return ;
				}
				if( Integer.parseInt(thresholdBonusWinId$bonusValueTbId.getValue()) <= 0 ) {
					MessageUtil.setMessage("Please provide valid value for bonus on threshold.", "red");
					return;
				}
			}else {
				if(!checkIfNumber(thresholdBonusWinId$bonusValueTbId.getValue().trim())) {
					MessageUtil.setMessage("Please provide number value for bonus on threshold.", "red");
					thresholdBonusWinId$bonusValueTbId.setFocus(true);
					return ;
				}
				
				if(Double.parseDouble(thresholdBonusWinId$bonusValueTbId.getValue()) <= 0) {
					MessageUtil.setMessage("Please provide valid value for bonus on threshold.", "red");
					return;
				}
			}
			if(thresholdBonusWinId$levelTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)) {

				if(!checkIfNum(thresholdBonusWinId$levelValueTbId.getValue().trim())) {
					MessageUtil.setMessage("Please provide number value for bonus on threshold.", "red");
					thresholdBonusWinId$levelValueTbId.setFocus(true);
					return ;
				}
				
				if(Integer.parseInt(thresholdBonusWinId$levelValueTbId.getValue()) <= 0) {
					MessageUtil.setMessage("Please provide valid value for bonus on threshold.", "red");
					return;
				}
			}else if(thresholdBonusWinId$levelTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.THRESHOLD_TYPE_TIER)) {

				if(!checkIfNum(thresholdBonusWinId$levelValueTbId.getValue().trim())) {
					MessageUtil.setMessage("Please provide number value for bonus on threshold.", "red");
					thresholdBonusWinId$levelValueTbId.setFocus(true);
					return ;
				}
				if( Integer.parseInt(thresholdBonusWinId$levelValueTbId.getValue()) <= 0 ) {
					MessageUtil.setMessage("Please provide number value for bonus on threshold.", "red");
					return;
				}
				if( Integer.parseInt(thresholdBonusWinId$levelValueTbId.getValue()) <= 1) {
					MessageUtil.setMessage("Please Enter Tiers Greater than 1.", "red");
					return;
				}
				if( Integer.parseInt(thresholdBonusWinId$levelValueTbId.getValue()) > availableTiersCount) {
					MessageUtil.setMessage("Please provide available Tier Number.", "red");
					return;
				}
			}else {
				if(!checkIfNumber(thresholdBonusWinId$levelValueTbId.getValue().trim())) {
					MessageUtil.setMessage("Please provide number value for bonus on threshold.", "red");
					thresholdBonusWinId$levelValueTbId.setFocus(true);
					return ;
				}
				
				if(Double.parseDouble(thresholdBonusWinId$levelValueTbId.getValue()) <= 0) {
					MessageUtil.setMessage("Please provide valid value for bonus on threshold.", "red");
					return;
				}
			}

		}
		if(thresholdBonusWinId$levelTypeLbId.getSelectedItem().getValue().equals("LPV")&&(thresholdBonusWinId$thresholdLimitID.getValue()!=null && !thresholdBonusWinId$thresholdLimitID.getValue().trim().isEmpty())){

			if(!checkIfNumber(thresholdBonusWinId$thresholdLimitID.getValue().trim())) {
				MessageUtil.setMessage("Please provide number value for limit on threshold.", "red");
				thresholdBonusWinId$thresholdLimitID.setFocus(true);
				return ;
			}
			if(Integer.parseInt(thresholdBonusWinId$thresholdLimitID.getValue()) <= 0) {
				MessageUtil.setMessage("Please provide valid value for bonus on threshold.", "red");
				return;
			}
			if(Double.parseDouble(thresholdBonusWinId$thresholdLimitID.getValue()) <= (Double.parseDouble(thresholdBonusWinId$levelValueTbId.getValue()))) {
				MessageUtil.setMessage("Threshold limit cannot be less than threshold value.", "red");
				return;
			}
		}
		
		if(isEdit) {
			thresholdId = (Long) session.getAttribute("thresholdId");
		}
		Long programId=(Long) session.getAttribute("programId");
		if(thresholdBonusWinId$levelTypeLbId.getSelectedItem().getValue().equals("LPV")){
			String earnType="LPV";
			Double earnlevel=Double.parseDouble(thresholdBonusWinId$levelValueTbId.getValue());
		//	LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		//	List<LoyaltyThresholdBonus> bonusList = ltyPrgmSevice.getBonusList(programId);
			try {
				loyaltyThresholdBonusDao = (LoyaltyThresholdBonusDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
			List<LoyaltyThresholdBonus> bonusList = loyaltyThresholdBonusDao.getBonusListByEarnType(programId, earnType);
			if(bonusList!=null){
			for (LoyaltyThresholdBonus loyaltyThresholdBonus : bonusList) {
				if(loyaltyThresholdBonus.getThresholdBonusId().equals(thresholdId)) continue;
				if(loyaltyThresholdBonus.getEarnedLevelValue()!=null && loyaltyThresholdBonus.getEarnedLevelValue().equals(earnlevel)){
					MessageUtil.setMessage("Threshold already exists.Please provide a different one.", "red");
					return;
				}
					
			}
			}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::", e);
			}
			
		}
		
		String extraBonusType=thresholdBonusWinId$bonusTypeLbId.getSelectedItem().getValue().toString();
		if(thresholdBonusWinId$bonusValueTbId.getValue()!=null && !thresholdBonusWinId$bonusValueTbId.getValue().trim().isEmpty()) {
			String amtRegExp = "^[0-9]*(\\.[0-9]{1,2})?$";
			Pattern pattern = Pattern.compile(amtRegExp);  
			Matcher m = pattern.matcher(thresholdBonusWinId$bonusValueTbId.getValue().trim());
			String match = "";
			while (m.find()) {
				match += m.group();
			}
			try {
				String value  = ""+Double.parseDouble(match);
			} catch (NumberFormatException e) {
				MessageUtil.setMessage("Currency value should not exceed two decimal points.", "Color:red", "Top");
				thresholdBonusWinId$bonusValueTbId.setFocus(true);
				return;
			}	
			//extraBonusValue=Double.parseDouble(thresholdBonusWinId$bonusValueTbId.getValue());
			extraBonusValue=Double.parseDouble(Utility.truncateUptoTwoDecimal(Double.parseDouble(thresholdBonusWinId$bonusValueTbId.getValue())));
		}
		String earnedLevelType=thresholdBonusWinId$levelTypeLbId.getSelectedItem().getValue().toString();
		if(thresholdBonusWinId$levelValueTbId.getValue()!=null && !thresholdBonusWinId$levelValueTbId.getValue().trim().isEmpty()) {
			String amtRegExp = "^[0-9]*(\\.[0-9]{1,2})?$";
			Pattern pattern = Pattern.compile(amtRegExp);  
			Matcher m = pattern.matcher(thresholdBonusWinId$levelValueTbId.getValue().trim());
			String match = "";
			while (m.find()) {
				match += m.group();
			}
			try {
				String value  = ""+Double.parseDouble(match);
			} catch (NumberFormatException e) {
				MessageUtil.setMessage("Currency value should not exceed two decimal points.", "Color:red", "Top");
				thresholdBonusWinId$levelValueTbId.setFocus(true);
				return;
			}	
			//earnedLevelValue=Double.parseDouble(thresholdBonusWinId$levelValueTbId.getValue());
			earnedLevelValue=Double.parseDouble(Utility.truncateUptoTwoDecimal(Double.parseDouble(thresholdBonusWinId$levelValueTbId.getValue())));
			String recurring = thresholdBonusWinId$levelValueLbId.getSelectedItem().getValue();
			logger.info(recurring);
	
			
			if(recurring.equalsIgnoreCase("Increment")){
				isRecurring = true;
			}else{
				isRecurring = false;
			}
		}
		
		
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		Double thresholdLimitIDValue= null;

		if(thresholdBonusWinId$thresholdLimitID.getValue()!=null && !thresholdBonusWinId$thresholdLimitID.getValue().isEmpty())
		{
			 thresholdLimitIDValue=Double.parseDouble(thresholdBonusWinId$thresholdLimitID.getValue());
		}
		
		Long threshBonusEmailTmpltId = thresholdBonusWinId$selBonusAutoEmailsLbId.getSelectedItem().getValue();
		Long threshBonusSmsTmpltId = thresholdBonusWinId$selBonusAutoSmsLbId.getSelectedItem().getValue();
		
		String bonusExpiryDateType=null;
		Long bonusExpiryDateValue=null;
		Long rewardExpirySmsTmpltId =null;
		Long rewardExpiryEmailTmpltId =null;
		
		if(thresholdBonusWinId$earnedRwrdExpiryCbId.isChecked()){
			bonusExpiryDateType=thresholdBonusWinId$giftAmtExpTypeLbId.getSelectedItem().getValue().toString();
			rewardExpirySmsTmpltId = thresholdBonusWinId$selRewardAutoSmsLbId.getSelectedItem().getValue();
			rewardExpiryEmailTmpltId = thresholdBonusWinId$selRewardAutoEmailsLbId.getSelectedItem().getValue();
			if(thresholdBonusWinId$giftAmtExpValueTbId.getValue()!=null && !thresholdBonusWinId$giftAmtExpValueTbId.getValue().isEmpty()) {
				try {
					bonusExpiryDateValue=Long.parseLong(thresholdBonusWinId$giftAmtExpValueTbId.getValue());
					if (bonusExpiryDateValue < 1) {
						MessageUtil.setMessage("Please provide valid input for Reward Expiry Month(s).", "color:red", "TOP");
						return;
					}
				}catch (Exception e) {
					MessageUtil.setMessage("Please provide valid input for Reward Expiry Month(s).", "color:red", "TOP");
					thresholdBonusWinId$giftAmtExpValueTbId.setFocus(true);
					return;
				}
			} else {
				MessageUtil.setMessage("please provide Reward Expiry Month(s)", "Color:red", "Top");
				thresholdBonusWinId$giftAmtExpValueTbId.setFocus(true);
				return;
			}
			
		}
		
		
		ltyPrgmSevice.saveBonus(programId,extraBonusType,extraBonusValue,earnedLevelType,earnedLevelValue,thresholdId,
				registrationFlag,userId,isRecurring,thresholdLimitIDValue,threshBonusEmailTmpltId,threshBonusSmsTmpltId,
				bonusExpiryDateType,bonusExpiryDateValue,rewardExpiryEmailTmpltId,rewardExpirySmsTmpltId);
		MessageUtil.setMessage("Threshold  saved successfully.", "color:blue", "TOP");
		List<LoyaltyThresholdBonus> bonusList = ltyPrgmSevice.getBonusList(programId);
		redrawBonusList(bonusList);
		thresholdBonusWinId$bonusValueTbId.setValue("");
		thresholdBonusWinId$levelValueTbId.setValue("");
		thresholdBonusWinId$thresholdLimitID.setValue("");
		thresholdBonusWinId$bonusTypeLbId.setSelectedIndex(0);
		thresholdBonusWinId$levelTypeLbId.setSelectedIndex(0);
		if(thresholdBonusWinId$earnedRwrdExpiryCbId.isChecked()){
			
			thresholdBonusWinId$giftAmtExpTypeLbId.setSelectedIndex(0);
			thresholdBonusWinId$giftAmtExpValueTbId.setValue("");
			thresholdBonusWinId$earnedRwrdExpiryCbId.setChecked(false);
			thresholdBonusWinId$rewardTierDivId.setVisible(false);
		}
		thresholdBonusWinId$selRewardAutoSmsLbId.setSelectedIndex(0);
		thresholdBonusWinId$selRewardAutoEmailsLbId.setSelectedIndex(0);
		
		thresholdBonusWinId.setVisible(false);
	}

	private Div basicSettingsDivId;
	private Toolbarbutton basicSettingsTbBtnId;
	public void onClick$basicSettingsTbBtnId() {

		basicSettingsDivId.setVisible(!basicSettingsDivId.isVisible());
		String image = basicSettingsDivId.isVisible() ? "/img/icons/icon_minus.png" : "/img/icons/icon_plus.png";
		basicSettingsTbBtnId.setImage(image);
	}

	private Div transactionalDivId;
	private Toolbarbutton trnxnlSettingsTbBtnId;
	public void onClick$trnxnlSettingsTbBtnId() {

		transactionalDivId.setVisible(!transactionalDivId.isVisible());
		String image = transactionalDivId.isVisible() ? "/img/icons/icon_minus.png" : "/img/icons/icon_plus.png";
		trnxnlSettingsTbBtnId.setImage(image);
	}

	private Div addCardSetDivId;
	private Toolbarbutton cardSetTbBtnId;
	public void onClick$cardSetTbBtnId() {

		addCardSetDivId.setVisible(!addCardSetDivId.isVisible());
		String image = addCardSetDivId.isVisible() ? "/img/icons/icon_minus.png" : "/img/icons/icon_plus.png";
		cardSetTbBtnId.setImage(image);
	}

	private Div previlegeTierDivId;
	private Toolbarbutton previlegeTierTbBtnId;
	public void onClick$previlegeTierTbBtnId() {

		previlegeTierDivId.setVisible(!previlegeTierDivId.isVisible());
		String image = previlegeTierDivId.isVisible() ? "/img/icons/icon_minus.png" : "/img/icons/icon_plus.png";
		previlegeTierTbBtnId.setImage(image);
	}

	private Div thresholdBonusDivId;
	private Toolbarbutton thresholdBonusTbBtnId;
	public void onClick$thresholdBonusTbBtnId() {

		thresholdBonusDivId.setVisible(!thresholdBonusDivId.isVisible());
		String image = thresholdBonusDivId.isVisible() ? "/img/icons/icon_minus.png" : "/img/icons/icon_plus.png";
		thresholdBonusTbBtnId.setImage(image);
	}

	private Div validitySetDivId;
	private Toolbarbutton validityTbBtnId;
	public void onClick$validityTbBtnId() {

		validitySetDivId.setVisible(!validitySetDivId.isVisible());
		String image = validitySetDivId.isVisible() ? "/img/icons/icon_minus.png" : "/img/icons/icon_plus.png";
		validityTbBtnId.setImage(image);
	}

	private Div exclusionDivId;
	private Toolbarbutton exclusionsTbBtnId;
	public void onClick$exclusionsTbBtnId() {

		exclusionDivId.setVisible(!exclusionDivId.isVisible());
		String image = exclusionDivId.isVisible() ? "/img/icons/icon_minus.png" : "/img/icons/icon_plus.png";
		exclusionsTbBtnId.setImage(image);
	}

	private Div autoCommDivId;
	private Toolbarbutton autoCommTbBtnId;
	public void onClick$autoCommTbBtnId() {

		autoCommDivId.setVisible(!autoCommDivId.isVisible());
		String image = autoCommDivId.isVisible() ? "/img/icons/icon_minus.png" : "/img/icons/icon_plus.png";
		autoCommTbBtnId.setImage(image);
	}

	public void onClick$editBasicSettingsAnchId() {
		Redirect.goTo(PageListEnum.LOYALTY_CREATE_PROGRAM);
	}

	public void onClick$editTrnxnlAnchId() {
		Redirect.goTo(PageListEnum.LOYALTY_CREATE_PROGRAM);
	}

	public void onClick$editValidityAnchId() {
		Redirect.goTo(PageListEnum.LOYALTY_ADDITIONAL_SETTINGS);
	}

	public void onClick$editExclusionsAnchId() {
		Redirect.goTo(PageListEnum.LOYALTY_ADDITIONAL_SETTINGS);
	}

	public void onClick$activeBtnId() {
		
		try{
			if( Messagebox.show("Do you want to Activate the program.","Confirm",
					 Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION) == Messagebox.OK ){
				
				Long prgmId = (Long) session.getAttribute("programId");
				LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
				LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
				String draftStatus = prgmObj.getDraftStatus();
				String[] draftList = draftStatus.split(Constants.ADDR_COL_DELIMETER);
				String step1 = draftList[0];
				String step2 = draftList[1];
				String step3 = draftList[2];
				if(step1.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE) && 
						step2.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE) &&
						step3.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE)) {
					prgmObj.setStatus(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE);
					ltyPrgmSevice.savePrgmObj(prgmObj);
					Redirect.goTo(PageListEnum.LOYALTY_PROGRAMS_LIST);
				}
				else {
					String errMsg = "The program cannot be activated, ";
					if(step2.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE) && 
							step3.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE)) {
						errMsg += "as no card-sets were found and not all tiers have been configured for this program.";
					}
					else if(step2.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE) && 
							step3.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE)) {
						errMsg += "as all tiers have not been configured for this program";
					}
					else if(step2.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE) && 
							step3.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE)){
						errMsg += "as no card-sets were found for this program.";
					}
					MessageUtil.setMessage(errMsg, "color:red", "TOP");
					//			status = OCConstants.LOYALTY_PROGRAM_STATUS_DRAFT;
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
		
		/*Long prgmId = (Long) session.getAttribute("programId");
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
		String draftStatus = prgmObj.getDraftStatus();
		String[] draftList = draftStatus.split(Constants.ADDR_COL_DELIMETER);
		String step1 = draftList[0];
		String step2 = draftList[1];
		String step3 = draftList[2];
		if(step1.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE) && 
				step2.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE) &&
				step3.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE)) {
			prgmObj.setStatus(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE);
			ltyPrgmSevice.savePrgmObj(prgmObj);
			Redirect.goTo(PageListEnum.LOYALTY_PROGRAMS_LIST);
		}
		else {
			String errMsg = "The program cannot be activated, ";
			if(step2.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE) && 
					step3.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE)) {
				errMsg += "as no card-sets were found and not all tiers have been configured for this program.";
			}
			else if(step2.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE) && 
					step3.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE)) {
				errMsg += "as all tiers have not been configured for this program";
			}
			else if(step2.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE) && 
					step3.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE)){
				errMsg += "as no card-sets were found for this program.";
			}
			MessageUtil.setMessage(errMsg, "color:red", "TOP");
			//			status = OCConstants.LOYALTY_PROGRAM_STATUS_DRAFT;
		}*/
	}

	public void onClick$suspendBtnId() {
		
		try{
			if( Messagebox.show("Do you want to Suspend the program.","Confirm",
					 Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION) == Messagebox.OK ){
				
				Long prgmId = (Long) session.getAttribute("programId");
				LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
				LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
				if(prgmObj.getDefaultFlag() == OCConstants.FLAG_YES) {
					MessageUtil.setMessage("You are suspending a default program, enrollments without card number cannot be done in future.", "color:blue", "TOP");
				}
				prgmObj.setStatus(OCConstants.LOYALTY_PROGRAM_STATUS_SUSPENDED);
				ltyPrgmSevice.savePrgmObj(prgmObj);
				Redirect.goTo(PageListEnum.LOYALTY_PROGRAMS_LIST);
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
		
		/*Long prgmId = (Long) session.getAttribute("programId");
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
		if(prgmObj.getDefaultFlag() == OCConstants.FLAG_YES) {
			MessageUtil.setMessage("You are suspending a default program, enrollments without card number cannot be done in future.", "color:blue", "TOP");
		}
		prgmObj.setStatus(OCConstants.LOYALTY_PROGRAM_STATUS_SUSPENDED);
		ltyPrgmSevice.savePrgmObj(prgmObj);
		Redirect.goTo(PageListEnum.LOYALTY_PROGRAMS_LIST);*/
	}

	public void onClick$draftBtnId() {
		Long prgmId = (Long) session.getAttribute("programId");
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
		prgmObj.setStatus(OCConstants.LOYALTY_PROGRAM_STATUS_DRAFT);
		ltyPrgmSevice.savePrgmObj(prgmObj);
		Redirect.goTo(PageListEnum.LOYALTY_PROGRAMS_LIST);
	}


	public void onClick$exitBtnId() {
		Long prgmId = (Long) session.getAttribute("programId");
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
		if(prgmObj.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE)) {
			String draftStatus = prgmObj.getDraftStatus();
			String[] draftList = draftStatus.split(Constants.ADDR_COL_DELIMETER);
			String step2 = draftList[1];
			if(step2.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE)) {
				MessageUtil.setMessage("Looks like the program is missing some mandatory settings " +
						"without which it may not function as expected." +
						" Please review the program's settings on Step 6.", "color:blue", "TOP");
			}
		}
		Redirect.goTo(PageListEnum.LOYALTY_PROGRAMS_LIST);
	}
	/**
	 * setting the reward expiry email template 
	 * @param loyaltyAutoComm
	 */
	private void setRewardExpValues(LoyaltyThresholdBonus bonus) {
		logger.debug("---Entered setRewardExpValues---");
		List<Listitem> earnedRewardExpList = thresholdBonusWinId$selRewardAutoEmailsLbId.getItems();
		getDefaultValueForOldRecords(bonus.getEmailExpiryTempId(),thresholdBonusWinId$selRewardAutoEmailsLbId);
		for(Listitem li :earnedRewardExpList ) {
			Long rewardExpiryEmailTmpltId=li.getValue();
			if(rewardExpiryEmailTmpltId != null) {
				if(rewardExpiryEmailTmpltId.equals(bonus.getEmailExpiryTempId())) {
					thresholdBonusWinId$selRewardAutoEmailsLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}			
		}
		logger.debug("---Exit setRewardExpValues---");
	}//setRewardExpValues()
	public void onSelect$selRewardAutoEmailsLbId$thresholdBonusWinId() {

		Boolean flag = false;
		if(thresholdBonusWinId$selRewardAutoEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto Email")) {
			thresholdBonusWinId$previewRewExpEmailImgId.setVisible(false);
			thresholdBonusWinId$rewardEmailPreviewBtnId.setVisible(false);
			thresholdBonusWinId$editRewExpEmailImgId.setVisible(false);
			thresholdBonusWinId$rewardEmailEditMsgBtnId.setVisible(false);
			thresholdBonusWinId$addRewExpEmailImgId.setVisible(true);
			thresholdBonusWinId$rewardEmailAddMsgBtnId.setVisible(true);
			flag = true;
		}else{
			thresholdBonusWinId$previewRewExpEmailImgId.setVisible(true);
			thresholdBonusWinId$rewardEmailPreviewBtnId.setVisible(true);
			thresholdBonusWinId$editRewExpEmailImgId.setVisible(true);
			thresholdBonusWinId$rewardEmailEditMsgBtnId.setVisible(true);
			thresholdBonusWinId$addRewExpEmailImgId.setVisible(true);
			thresholdBonusWinId$rewardEmailAddMsgBtnId.setVisible(true);
		}
		if(thresholdBonusWinId$selRewardAutoEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Default Message")) {
			thresholdBonusWinId$editRewExpEmailImgId.setVisible(false);
			thresholdBonusWinId$rewardEmailEditMsgBtnId.setVisible(false);
		}else if(!flag) {
			thresholdBonusWinId$editRewExpEmailImgId.setVisible(true);
			thresholdBonusWinId$rewardEmailEditMsgBtnId.setVisible(true);
		}

	}//onSelect$selBonusAutoSmsLbId()
	/**
	 * setting the reward expiry SMS template 
	 * @param loyaltyAutoComm
	 */
	private void setSMSRewardExpValues(LoyaltyThresholdBonus bonus) {
		logger.debug("---Entered setSMSRewardExpValues---");
		List<Listitem> earnedRewardExpSmsList = thresholdBonusWinId$selRewardAutoSmsLbId.getItems();
		for(Listitem li : earnedRewardExpSmsList ) {
			Long rewardExpirySmsTmpltId = li.getValue();
			if(rewardExpirySmsTmpltId != null) {
				if(rewardExpirySmsTmpltId.equals(bonus.getSmsExpiryTempId())) {
					thresholdBonusWinId$selRewardAutoSmsLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}
		}
		logger.debug("---Exit setSMSRewardExpValues---");
	}//setSMSRewardExpValues()

	public void onSelect$selRewardAutoSmsLbId$thresholdBonusWinId() {

		if(thresholdBonusWinId$selRewardAutoSmsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto SMS")) {
			thresholdBonusWinId$previewRewExpSmsImgId.setVisible(false);
			thresholdBonusWinId$rewardSmsPreviewBtnId.setVisible(false);
			thresholdBonusWinId$editRewExpSmsImgId.setVisible(false);
			thresholdBonusWinId$rewardSmsEditMsgBtnId.setVisible(false);
			thresholdBonusWinId$addRewExpSmsImgId.setVisible(true);
			thresholdBonusWinId$rewardSmsAddMsgBtnId.setVisible(true);
		}else{
			thresholdBonusWinId$previewRewExpSmsImgId.setVisible(true);
			thresholdBonusWinId$rewardSmsPreviewBtnId.setVisible(true);
			thresholdBonusWinId$editRewExpSmsImgId.setVisible(true);
			thresholdBonusWinId$rewardSmsEditMsgBtnId.setVisible(true);
			thresholdBonusWinId$addRewExpSmsImgId.setVisible(true);
			thresholdBonusWinId$rewardSmsAddMsgBtnId.setVisible(true);
		}

	}//onSelect$selBonusAutoEmailsLbId()
	/**
	 * Preview the template of reward expiry
	 */
	public void onClick$rewardSmsPreviewBtnId$thresholdBonusWinId() {
		logger.debug("---Entered onClick$thresholdBonusWinId$rewardSmsPreviewBtnId---");
		previewSMSTemplate(thresholdBonusWinId$selRewardAutoSmsLbId,OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_REWARDAMTEXPIRY);
		logger.debug("---Exit onClick$thresholdBonusWinId$rewardSmsPreviewBtnId---");
	}//onClick$rewardSmsPreviewBtnId()
	
	/**
	 * Edits the template of reward expiry
	 */
	public void onClick$rewardSmsEditMsgBtnId$thresholdBonusWinId() {
		logger.debug("---Entered onClick$thresholdBonusWinId$rewardSmsEditMsgBtnId---");
		editSMSTemplate(thresholdBonusWinId$selRewardAutoSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_EARNED_REWARD_EXPIRATION);
		logger.debug("---Exit onClick$thresholdBonusWinId$rewardSmsEditMsgBtnId---");
	}//onClick$rewardSmsEditMsgBtnId()
	
	/**
	 * To add a new template for reward expiry
	 */
	public void onClick$rewardSmsAddMsgBtnId$thresholdBonusWinId() {
		logger.debug("---Entered onClick$thresholdBonusWinId$rewardSmsAddMsgBtnId---");
		addSMSTemplate(thresholdBonusWinId$selRewardAutoSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_EARNED_REWARD_EXPIRATION);
		logger.debug("---Exit onClick$thresholdBonusWinId$rewardSmsAddMsgBtnId---");
	}//onClick$rewardSmsAddMsgBtnId()
	/**
	 * Preview the template of reward expiry
	 */
	public void onClick$rewardEmailPreviewBtnId$thresholdBonusWinId() {
		logger.debug("---Entered onClick$thresholdBonusWinId$rewardEmailPreviewBtnId---");
		previewEmailTemplate(thresholdBonusWinId$selRewardAutoEmailsLbId,OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_REWARDAMTEXPIRY);
		logger.debug("---Exit onClick$thresholdBonusWinId$rewardEmailPreviewBtnId---");
	}//onClick$rewardEmailPreviewBtnId()

	/**
	 * Edits the template of reward expiry
	 */
	public void onClick$rewardEmailEditMsgBtnId$thresholdBonusWinId() {
		logger.debug("---Entered onClick$thresholdBonusWinId$rewardEmailEditMsgBtnId---");
		editEmailTemplate(thresholdBonusWinId$selRewardAutoEmailsLbId,Constants.CUSTOM_TEMPLATE_TYPE_EARNED_REWARD_EXPIRATION);
		logger.debug("---Exit onClick$thresholdBonusWinId$rewardEmailEditMsgBtnId---");
	}//onClick$rewardEmailEditMsgBtnId()

	/**
	 * To add a new template for reward expiry
	 */
	public void onClick$rewardEmailAddMsgBtnId$thresholdBonusWinId() {
		logger.debug("---Entered onClick$thresholdBonusWinId$rewardEmailAddMsgBtnId---");
		addEmailTemplate(thresholdBonusWinId$selRewardAutoEmailsLbId,Constants.CUSTOM_TEMPLATE_TYPE_EARNED_REWARD_EXPIRATION);
		logger.debug("---Exit onClick$thresholdBonusWinId$rewardEmailAddMsgBtnId---");
	}//onClick$rewardEmailAddMsgBtnId()

	  public void desc2ascasc2desc(){
	    	if(desc_Asc=="desc")
				desc_Asc="asc";
			else
				desc_Asc="desc";
	    }
		
	public void onClick$sortByBonusAmount(){
		desc2ascasc2desc();
		Long prgmId=(Long) session.getAttribute("programId");
		List<LoyaltyThresholdBonus> bonusListAmount = new LoyaltyProgramService().getBonusListByOrderAndEarnType(prgmId, desc_Asc,OCConstants.Bonus_Earn_Type_Amount);
		List<LoyaltyThresholdBonus> bonusListPoints = new LoyaltyProgramService().getBonusListByEarnType(prgmId,OCConstants.Bonus_Earn_Type_Points);
		Components.removeAllChildren(levelRowsId);
		List<LoyaltyThresholdBonus> bonusList = new ArrayList<LoyaltyThresholdBonus>();
		if(bonusListAmount!=null && !bonusListAmount.isEmpty())	bonusList.addAll(bonusListAmount);
		if(bonusListPoints!=null && !bonusListPoints.isEmpty()) bonusList.addAll(bonusListPoints);
		redrawBonusList(bonusList);
	}
	public void onClick$sortByBonusPoints(){
		desc2ascasc2desc();
		Long prgmId=(Long) session.getAttribute("programId");
		List<LoyaltyThresholdBonus> bonusListAmount = new LoyaltyProgramService().getBonusListByOrderAndEarnType(prgmId, desc_Asc,OCConstants.Bonus_Earn_Type_Points);
		List<LoyaltyThresholdBonus> bonusListPoints = new LoyaltyProgramService().getBonusListByEarnType(prgmId,OCConstants.Bonus_Earn_Type_Amount);
		Components.removeAllChildren(levelRowsId);
		List<LoyaltyThresholdBonus> bonusList = new ArrayList<LoyaltyThresholdBonus>();
		if(bonusListAmount!=null && !bonusListAmount.isEmpty())	bonusList.addAll(bonusListAmount);
		if(bonusListPoints!=null && !bonusListPoints.isEmpty()) bonusList.addAll(bonusListPoints);
		redrawBonusList(bonusList);
	}

	
}

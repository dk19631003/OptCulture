package org.mq.optculture.controller.loyalty;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.AutoSMS;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.LoyaltyAutoComm;
import org.mq.marketer.campaign.beans.LoyaltyCardSet;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltyThresholdBonus;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.OrganizationStores;
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
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltyThresholdBonusDao;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class LoyaltyRulesController extends GenericForwardComposer {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Long userId;
	private Listbox thresholdBonusWinId$giftAmtExpTypeLbId,giftAmtExpTypeLbId,ptsActiveValueLbId, earnTypeLbId, selTierLbId/*,ruleTypeLbId*/,bonusTypeLbId,levelTypeLbId,earnValueTypeLbId,bonusThresholdTypeLbId,
	thresholdBonusWinId$bonusTypeLbId,thresholdBonusWinId$levelTypeLbId/*,cumValueLbId*/,levelValueLbId, thresholdBonusWinId$levelValueLbId,selBonusAutoEmailsLbId,selBonusAutoSmsLbId,roundLbId;
	//cumTypeLbId;
	private Div thresholdBonusWinId$expiryAutoCommDiv,expiryAutoCommDiv,thresholdBonusWinId$rewardTierDivId,rewardTierDivId,enrollmentVisibilityDiv,thresholdVisibilityDiv1,thresholdVisibilityDiv,amountDivId,percentDivId,activateAfterDivId/*,cummulativeDivId,upgradeDivId,upgradeCumDivId*/,tierTypeDivId,tierUpgradeDivId,pointsConversionDivId, issuanceAmountDivId, valueDivId,otpEnabledDivId;
	private Textbox privilegeNameTbId, valueTbId,amountSpentTbId,coversionfromTbId,conversionToTbId/*,crossAtValueTbId*/,bonusValueTbId,levelValueTbId,bonusThresholdValueTbId,
	thresholdBonusWinId$bonusValueTbId,thresholdBonusWinId$levelValueTbId, issuanceValueTbId,giftAmtExpValueTbId,thresholdBonusWinId$giftAmtExpValueTbId,minReceiptAmtTbId,minBalanceTbId,redemptionPercentageTbId,redemptionValueTbId,expAftrTbId,crossOverBounsTbId,otpAmtLimitTbId;
	private Radio  autoConvertRadioId,onDemandRadioId,enrollmentRadioId,thresholdRadioId,onAllStore,selectedStores;
	private Button savBtnId,cancelBtnId,bonusaddBtnId,bonusCancelBtnId;
	private Window thresholdBonusWinId;
	private Rows tierRowsId,levelRowsId,mulTierRuleRowsId;
	private Checkbox enableDateChkId,earnedRwrdExpiryCbId,thresholdBonusWinId$earnedRwrdExpiryCbId, enableChkIdForIssuance,enableRedeemedAmountChkId,enableOTPAuthChkId,enablePartialReturnsAuthChkId;
	private Columns tierColsId;
	private boolean isEdit=false;
	private  String  userCurrencySymbol = "$ "; 
	private Label currencySymbolId , forCurrId,onPurchaseRadioLblId, issuanceForCurrId;
	private Users currUser = GetUser.getUserObj();
	private LoyaltyThresholdBonusDao loyaltyThresholdBonusDao;
	private Window previewWin;
	private Groupbox prevliegeDefGrpId,tierUpgradeGrpId;
	private Datebox expOnDateBxId;
	private Combobox storeNumbCmboBxId;
	Radiogroup onPurchaseRadioGrId;
	private String desc_Asc="desc"; 
	private Iframe previewWin$html;
	private Image thresholdBonusWinId$addThresholdSmsImgId,thresholdBonusWinId$editThresholdSmsImgId,
		thresholdBonusWinId$previewThresholdSmsImgId,addThresholdEmailImgId,previewThresholdEmailImgId,
		editThresholdEmailImgId,previewThresholdSmsImgId,editThresholdSmsImgId,addThresholdSmsImgId,
		thresholdBonusWinId$previewThresholdEmailImgId,thresholdBonusWinId$editThresholdEmailImgId,
		thresholdBonusWinId$addThresholdEmailImgId,previewRewExpEmailImgId,editRewExpEmailImgId,addRewExpEmailImgId
		,thresholdBonusWinId$previewRewExpSmsImgId,thresholdBonusWinId$editRewExpSmsImgId,thresholdBonusWinId$addRewExpSmsImgId,
		thresholdBonusWinId$previewRewExpEmailImgId,thresholdBonusWinId$editRewExpEmailImgId,thresholdBonusWinId$addRewExpEmailImgId
		,previewRewExpSmsImgId,editRewExpSmsImgId,addRewExpSmsImgId;
	private A thresholdBonusWinId$bonusSmsAddBtnId,thresholdBonusWinId$bonusSmsEditMsgBtnId,thresholdBonusWinId$bonusSmsPreviewBtnId,
		bonusEmailPreviewBtnId,bonusEmailEditMsgBtnId,bonusEmailAddBtnId,bonusSmsPreviewBtnId,bonusSmsEditMsgBtnId,bonusSmsAddBtnId
		,thresholdBonusWinId$bonusEmailPreviewBtnId,thresholdBonusWinId$bonusEmailEditMsgBtnId,thresholdBonusWinId$bonusEmailAddBtnId,
		rewardEmailPreviewBtnId,rewardEmailEditMsgBtnId,rewardEmailAddMsgBtnId,
		thresholdBonusWinId$rewardSmsPreviewBtnId,thresholdBonusWinId$rewardSmsEditMsgBtnId,thresholdBonusWinId$rewardSmsAddMsgBtnId
		,thresholdBonusWinId$rewardEmailPreviewBtnId,thresholdBonusWinId$rewardEmailEditMsgBtnId,thresholdBonusWinId$rewardEmailAddMsgBtnId,rewardSmsPreviewBtnId,rewardSmsEditMsgBtnId,rewardSmsAddMsgBtnId;
	private Listbox thresholdBonusWinId$selBonusAutoEmailsLbId,selectedStoreLbId,thresholdBonusWinId$selBonusAutoSmsLbId,selRewardAutoEmailsLbId,selRewardAutoSmsLbId,
		thresholdBonusWinId$selRewardAutoEmailsLbId,thresholdBonusWinId$selRewardAutoSmsLbId;
	private  MyTemplatesDao myTemplatesDao;
        private LoyaltyProgramTier tier;
	private Image addStoreImgId;
	private static Boolean tierUpdateFirstRow=true;
	private static Boolean replaceRow=false;
	private static Boolean selectedTierIsValid = true;
	private static int availableTiersCount = 0;

	public LoyaltyRulesController() {
		userId = GetUser.getUserObj().getUserId();
		session = Sessions.getCurrent();
		myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");

		
		Utility.ltyBreadCrumbFrom(2, OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype()));
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		DecimalFormat f = new DecimalFormat("#0.00");
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Create Loyalty Program (Step 2 of 6)","",style,true);
		logger.info("in do after compose");
		Long prgmId=(Long) session.getAttribute("programId");
		List<LoyaltyProgramTier> tierList = ltyPrgmSevice.getTierList(prgmId);

		LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
		if(prgmObj.getTierEnableFlag() == OCConstants.FLAG_YES ){
			//tierTypeDivId.setVisible(true);
			//tierUpgradeDivId.setVisible(true);
			prevliegeDefGrpId.setVisible(true);
			tierUpgradeGrpId.setVisible(true);
			
		}else {
			//tierTypeDivId.setVisible(false);
			//tierUpgradeDivId.setVisible(false);
			prevliegeDefGrpId.setVisible(false);
			tierUpgradeGrpId.setVisible(false);
		}
		/*Date date = null;
		Calendar.getInstance();
		expOnDateBxId.setValue(date);*/
		
		//program edit case when atleast one tier is defined 
		if(tierList != null) {
			for(LoyaltyProgramTier loyaltyProgramTier :tierList ) {
				List<Listitem> list = earnTypeLbId.getItems();
				for(Listitem li:list) {
					if(loyaltyProgramTier.getEarnType().equalsIgnoreCase(li.getValue().toString())) {
						earnTypeLbId.setSelectedItem(li);
						earnTypeLbId.setDisabled(true);
					}
				}
			}

			boolean isExists = false;
			LoyaltyProgramTier tierObj = null;
			for(LoyaltyProgramTier loyaltyProgramTier :tierList) {
				if(loyaltyProgramTier.getTierUpgdConstraintValue() != null || loyaltyProgramTier.getMultipleTierUpgrdRules() != null){
					isExists = true;
					tierObj = loyaltyProgramTier;
					break;
				}
			}
			/*if(isExists) {
				List<Listitem> upgradeList = ruleTypeLbId.getItems();
				for(Listitem li:upgradeList) {
					if(tierObj.getTierUpgdConstraint().equalsIgnoreCase(li.getValue().toString())) {
						ruleTypeLbId.setSelectedItem(li);
						ruleTypeLbId.setDisabled(true);
					}
				}
			}
			else{
				ruleTypeLbId.setSelectedIndex(0);
				ruleTypeLbId.setDisabled(false);
			}*/


		}

		//tier edit from step-6
		prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
		logger.info("prgmObj"+prgmObj);
		LoyaltyProgramTier loyaltyProgramTier=(LoyaltyProgramTier)session.getAttribute("loyaltyProgramTier");
		if(loyaltyProgramTier!=null) {
			savBtnId.setLabel("Save");
			cancelBtnId.setVisible(true);
			selectedTierIsValid = true;
			tierSettings(loyaltyProgramTier, "tierEdit");
		}
		else {
			Listitem li=null;
			if(prgmObj != null) {
				if(prgmObj.getTierEnableFlag() == OCConstants.FLAG_YES ) {
					int noOfTiers=prgmObj.getNoOfTiers();
					logger.info("noOfTiers"+noOfTiers);
					for(int i=1;i<=noOfTiers;i++) {
						String tier="Tier "+i;
						boolean isExists=false;
						if(tierList != null) {
							for(LoyaltyProgramTier ltyprogrmtier:tierList) {
								if(ltyprogrmtier.getTierType().equalsIgnoreCase(tier)) {
									isExists=true;
								}
							}
						}
						if(!isExists) {
							li=new Listitem(tier);
							li.setValue(tier);
							li.setParent(selTierLbId);
							selTierLbId.setSelectedIndex(0);
						}
					}
					availableTiersCount = noOfTiers;
					
					Listitem listItem = new Listitem(OCConstants.THRESHOLD_TYPE_TIER);
					listItem.setValue(OCConstants.THRESHOLD_TYPE_TIER);
					listItem.setParent(levelTypeLbId);
					
					Listitem listItemWin = new Listitem(OCConstants.THRESHOLD_TYPE_TIER);
					listItemWin.setValue(OCConstants.THRESHOLD_TYPE_TIER);
					listItemWin.setParent(thresholdBonusWinId$levelTypeLbId);
				}
				else {
					logger.info("in else");
					li=new Listitem("Tier 1");
					li.setValue("Tier 1");
					li.setParent(selTierLbId);
					selTierLbId.setSelectedItem(li);
					selTierLbId.setDisabled(true);
//					ruleTypeLbId.setDisabled(true);
//					crossAtValueTbId.setValue("");
//					crossAtValueTbId.setDisabled(true);
				}
			}
			tierUpdateFirstRow = true;
			mulTierRuleRowsId.setAttribute("ListBoxSelection", OCConstants.LOYALTY_LIFETIME_POINTS); //default on start-up
			selectedTierIsValid = true;
			addTierRule();
		}

		redrawTierList(tierList);

		//setting bonus list
		List<LoyaltyThresholdBonus> bonusList = ltyPrgmSevice.getBonusList(prgmId);
		if(bonusList!=null) {
			for(LoyaltyThresholdBonus loyaltyThresholdBonus:bonusList) {
				if(loyaltyThresholdBonus.getRegistrationFlag()==OCConstants.FLAG_YES) {
					List<Listitem> bonusTypeList=bonusThresholdTypeLbId.getItems();
					for(Listitem listitem : bonusTypeList) {
						if(loyaltyThresholdBonus.getExtraBonusType().equalsIgnoreCase(listitem.getValue().toString())) {
							bonusThresholdTypeLbId.setSelectedItem(listitem);
							break;
						}
					}
					bonusThresholdTypeLbId.setDisabled(true);
					if(bonusThresholdTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)) {
						bonusThresholdValueTbId.setValue(loyaltyThresholdBonus.getExtraBonusValue().intValue()+"");
					}else {
						//bonusThresholdValueTbId.setValue(f.format(loyaltyThresholdBonus.getExtraBonusValue()));
						bonusThresholdValueTbId.setValue(loyaltyThresholdBonus.getExtraBonusValue().toString());
					}
					bonusThresholdValueTbId.setDisabled(true);
					bonusaddBtnId.setLabel("Modify");
					bonusCancelBtnId.setVisible(false);
				}
			}
			redrawBonusList(bonusList);

		}

		if(earnTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT)) {
			coversionfromTbId.setDisabled(true);
			conversionToTbId.setDisabled(true) ;
			autoConvertRadioId.setDisabled(true);
			onDemandRadioId.setDisabled(true);
		}
		else {
			coversionfromTbId.setDisabled(false);
			conversionToTbId.setDisabled(false) ;
			autoConvertRadioId.setDisabled(false);
			onDemandRadioId.setDisabled(false);
		}

		/*if(ruleTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE)) {
			upgradeCumDivId.setVisible(true);
			upgradeCumDivId.setStyle("margin-top:10px;margin-right:10px;margin-left:5px;");
			cummulativeDivId.setVisible(true);
			upgradeDivId.setVisible(false);
		}else {
			upgradeCumDivId.setVisible(false);
			cummulativeDivId.setVisible(false);
			upgradeDivId.setVisible(true);
			upgradeDivId.setStyle("margin-top:10px;margin-right:10px;margin-left:5px;");
		}*/
		
		if(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype())){
			autoConvertRadioId.setSelected(true);
			autoConvertRadioId.setDisabled(false);
			onDemandRadioId.setDisabled(false);
			pointsConversionDivId.setVisible(true);
		}
		
		//adding currency symbol
		 String currSymbol = Utility.countryCurrencyMap.get(GetUser.getUserObj().getCountryType());
		   if(currSymbol != null && !currSymbol.isEmpty()) userCurrencySymbol = currSymbol + " ";
		   
		   
		   currencySymbolId.setValue("points earned is equivalent to  " + userCurrencySymbol + " ");
		   forCurrId.setValue("for every  " + userCurrencySymbol + " ");
		   
		    getLoyaltyTemplateList(selBonusAutoEmailsLbId, "earnedBonus");
		    getLoyaltyTemplateList(selRewardAutoEmailsLbId,"earnedRewardExpiration");
		    
			getLoyaltySmsTemplateList(selBonusAutoSmsLbId,"earningBonus");
			getLoyaltySmsTemplateList(selRewardAutoSmsLbId,"earnedRewardExpiration");

	
			if(enrollmentRadioId.isSelected()){
				enrollmentVisibilityDiv.setVisible(true);
				thresholdVisibilityDiv.setVisible(false);
				thresholdVisibilityDiv1.setVisible(false);
			}else if (thresholdRadioId.isSelected()){
				enrollmentVisibilityDiv.setVisible(false);
				thresholdVisibilityDiv.setVisible(true);
				thresholdVisibilityDiv1.setVisible(true);
			}
			setDefaultStores();
			onCheck$enableDateChkId();

	}//doAfterCompose()
	public void onCheck$earnedRwrdExpiryCbId() {

		if(earnedRwrdExpiryCbId.isChecked()) {
			rewardTierDivId.setVisible(true);
			expiryAutoCommDiv.setVisible(true);
		}
		else {
			rewardTierDivId.setVisible(false);
			expiryAutoCommDiv.setVisible(false);
		}
	}//onCheck$earnedRwrdExpiryCbId()
	
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
	}//onCheck$earnedRwrdExpiryCbId()


	private void setDateValues(Listbox valuelb, String selectedItem) {

		if(selectedItem.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_YEAR)) {
			Listitem li=null;
			for(int i=1;i<=10;i++) {
				li=new Listitem(i+"");
				li.setValue(i);
				li.setParent(valuelb);
				valuelb.setSelectedIndex(0);
			}
		}else if(selectedItem.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_MONTH)){
			Listitem li=null;
			for(int i=1;i<=12;i++) {
				li=new Listitem(i+"");
				li.setValue(i);
				li.setParent(valuelb);
				valuelb.setSelectedIndex(0);
			}
		}

	}//setDateValues()


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
	
	public void onSelect$selBonusAutoEmailsLbId$thresholdBonusWinId() {
		Boolean flag = false;
		if(thresholdBonusWinId$selBonusAutoEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto Email")) {
			thresholdBonusWinId$previewThresholdEmailImgId.setVisible(false);
			thresholdBonusWinId$bonusEmailPreviewBtnId.setVisible(false);
			thresholdBonusWinId$editThresholdEmailImgId.setVisible(false);
			thresholdBonusWinId$bonusEmailEditMsgBtnId.setVisible(false);
			thresholdBonusWinId$addThresholdEmailImgId.setVisible(true);
			thresholdBonusWinId$bonusEmailAddBtnId.setVisible(true);
			flag = true;
		}else{
			thresholdBonusWinId$previewThresholdEmailImgId.setVisible(true);
			thresholdBonusWinId$bonusEmailPreviewBtnId.setVisible(true);
			thresholdBonusWinId$editThresholdEmailImgId.setVisible(true);
			thresholdBonusWinId$bonusEmailEditMsgBtnId.setVisible(true);
			thresholdBonusWinId$addThresholdEmailImgId.setVisible(true);
			thresholdBonusWinId$bonusEmailAddBtnId.setVisible(true);
		}
		/*if(selBonusAutoEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Default Message")) {
			editThresholdEmailImgId.setVisible(false);
			bonusEmailEditMsgBtnId.setVisible(false);
		}else if(!flag) {
			editThresholdEmailImgId.setVisible(true);
			bonusEmailEditMsgBtnId.setVisible(true);
		}*/

	}//onSelect$selBonusAutoEmailsLbId()
	
	public void onSelect$selBonusAutoSmsLbId$thresholdBonusWinId() {

		if(thresholdBonusWinId$selBonusAutoSmsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto SMS")) {
			thresholdBonusWinId$previewThresholdSmsImgId.setVisible(false);
			thresholdBonusWinId$bonusSmsPreviewBtnId.setVisible(false);
			thresholdBonusWinId$editThresholdSmsImgId.setVisible(false);
			thresholdBonusWinId$bonusSmsEditMsgBtnId.setVisible(false);
			thresholdBonusWinId$addThresholdSmsImgId.setVisible(true);
			thresholdBonusWinId$bonusSmsAddBtnId.setVisible(true);

		}else{
			thresholdBonusWinId$addThresholdSmsImgId.setVisible(true);
			thresholdBonusWinId$bonusSmsAddBtnId.setVisible(true);
			thresholdBonusWinId$previewThresholdSmsImgId.setVisible(true);
			thresholdBonusWinId$bonusSmsPreviewBtnId.setVisible(true);
			thresholdBonusWinId$editThresholdSmsImgId.setVisible(true);
			thresholdBonusWinId$bonusSmsEditMsgBtnId.setVisible(true);
		}

	}//onSelect$selBonusAutoSmsLbId()
	
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
		/*if(thresholdBonusWinId$selRewardAutoEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase("Default Message")) {
			thresholdBonusWinId$editRewExpEmailImgId.setVisible(false);
			thresholdBonusWinId$rewardEmailEditMsgBtnId.setVisible(false);
		}else if(!flag) {
			thresholdBonusWinId$editRewExpEmailImgId.setVisible(true);
			thresholdBonusWinId$rewardEmailEditMsgBtnId.setVisible(true);
		}*/

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
	
	public void onCheck$enrollmentRadioId(){
		enrollmentVisibilityDiv.setVisible(true);
		thresholdVisibilityDiv.setVisible(false);
		thresholdVisibilityDiv1.setVisible(false);
	}

	public void onCheck$thresholdRadioId(){
		enrollmentVisibilityDiv.setVisible(false);
		thresholdVisibilityDiv.setVisible(true);
		thresholdVisibilityDiv1.setVisible(true);
		//thresholdVisibilityDiv.setWidth("80%");
	}
	
	public void onClick$bonusCancelBtnId() {
		
		DecimalFormat f = new DecimalFormat("#0.00");
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		Long programId = (Long)session.getAttribute("programId");
		LoyaltyThresholdBonus  loyaltyThresholdBonus = ltyPrgmSevice.getThresholdObj(programId);
		if(loyaltyThresholdBonus != null) {
			List<Listitem> bonusTypeList = bonusThresholdTypeLbId.getItems();
			for(Listitem listitem : bonusTypeList) {
				if(loyaltyThresholdBonus.getExtraBonusType().equalsIgnoreCase(listitem.getValue().toString())) {
					bonusThresholdTypeLbId.setSelectedItem(listitem);
					break;
				}
			}
			if(bonusThresholdTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)) {
				bonusThresholdValueTbId.setValue(loyaltyThresholdBonus.getExtraBonusValue().intValue()+"");
			}else {
				//bonusThresholdValueTbId.setValue(f.format(loyaltyThresholdBonus.getExtraBonusValue()));
				bonusThresholdValueTbId.setValue(loyaltyThresholdBonus.getExtraBonusValue().toString());
			}
		}
		bonusThresholdTypeLbId.setDisabled(true);
		bonusThresholdValueTbId.setDisabled(true);
		bonusaddBtnId.setLabel("Modify");
		bonusCancelBtnId.setVisible(false);
	}//onClick$bonusCancelBtnId()

	public void onClick$bonusaddBtnId(){
		DecimalFormat f = new DecimalFormat("#0.00");
		if(bonusThresholdValueTbId.isDisabled()) {
			bonusThresholdTypeLbId.setDisabled(false);
			bonusThresholdValueTbId.setDisabled(false);
			bonusaddBtnId.setLabel("Save");
			bonusCancelBtnId.setVisible(true);
		}else {

			if(bonusThresholdValueTbId.getValue().trim().isEmpty() || bonusThresholdValueTbId.getValue()==null ) {
				MessageUtil.setMessage("Bonus on enrollment value cannot be empty.", "red");
				bonusThresholdValueTbId.setFocus(true);
				return ;
			}
		
			if(bonusThresholdValueTbId.getValue().trim().length() > 60) {
				MessageUtil.setMessage("Bonus on enrollment value exceeds the maximum characters limit.", "red");
				bonusThresholdValueTbId.setFocus(true);
				return ;
			}


			if(bonusThresholdValueTbId.getValue().trim().length() > 0) {

				if(bonusThresholdTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)){
					if(!checkIfLong(bonusThresholdValueTbId.getValue().trim())) {
						MessageUtil.setMessage("Please provide number value for bonus on enrollment.", "red");
						bonusThresholdValueTbId.setFocus(true);
						return ;
					}
					
					if(Integer.parseInt(bonusThresholdValueTbId.getValue()) <= 0) {
						MessageUtil.setMessage("Please provide valid value for bonus on enrollment.", "red");
						bonusThresholdValueTbId.setFocus(true);
						return;
					}
				}
				else {
					if(!checkIfDouble(bonusThresholdValueTbId.getValue().trim())) {
						MessageUtil.setMessage("Please provide number value for bonus on enrollment.", "red");
						bonusThresholdValueTbId.setFocus(true);
						return ;
					}
					
					if(Double.parseDouble(bonusThresholdValueTbId.getValue()) <= 0) {
						MessageUtil.setMessage("Please provide valid value for bonus on enrollment.", "red");
						bonusThresholdValueTbId.setFocus(true);
						return;
					}
				}

			}
			Long thresholdId=null;
			Double extraBonusValue=null;
			char registrationFlag=OCConstants.FLAG_YES;
			LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
			Long programId=(Long) session.getAttribute("programId");
			LoyaltyThresholdBonus  loyaltyThresholdBonus=ltyPrgmSevice.getThresholdObj(programId);
			if(loyaltyThresholdBonus!=null) {
				thresholdId=loyaltyThresholdBonus.getThresholdBonusId();
			}

			String extraBonusType=bonusThresholdTypeLbId.getSelectedItem().getValue().toString();
			if(bonusThresholdValueTbId.getValue()!=null && !bonusThresholdValueTbId.getValue().trim().isEmpty()) {
				String amtRegExp = "^[0-9]*(\\.[0-9]{1,2})?$";
				Pattern pattern = Pattern.compile(amtRegExp);  
				Matcher m = pattern.matcher(bonusThresholdValueTbId.getValue().trim());
				String match = "";
				while (m.find()) {
					match += m.group();
				}
				try {
					String value  = ""+Double.parseDouble(match);
				} catch (NumberFormatException e) {
					MessageUtil.setMessage("Currency value should not exceed two decimal points.", "Color:red", "Top");
					bonusThresholdValueTbId.setFocus(true);
					return;
				}
				//extraBonusValue=Double.parseDouble(bonusThresholdValueTbId.getValue());
				extraBonusValue=Double.parseDouble(Utility.truncateUptoTwoDecimal(Double.parseDouble(bonusThresholdValueTbId.getValue())));
			
			}
			//ltyPrgmSevice.saveBonus(programId, extraBonusType, extraBonusValue, null, null,thresholdId,registrationFlag,userId);
			ltyPrgmSevice.saveBonus(programId, extraBonusType, extraBonusValue, null, null,thresholdId,registrationFlag,userId);
			List<LoyaltyThresholdBonus> bonusList = ltyPrgmSevice.getBonusList(programId);
			redrawBonusList(bonusList);
			if(bonusThresholdTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Amount")){
				//bonusThresholdValueTbId.setValue(f.format(Double.parseDouble(bonusThresholdValueTbId.getValue())));
				bonusThresholdValueTbId.setValue(Utility.truncateUptoTwoDecimal(Double.parseDouble(bonusThresholdValueTbId.getValue())));
			}
			bonusThresholdTypeLbId.setDisabled(true);
			bonusThresholdValueTbId.setDisabled(true);
			bonusaddBtnId.setLabel("Modify");
			bonusCancelBtnId.setVisible(false);
		}
	}//onClick$bonusaddBtnId()

	public void onSelect$selTierLbId() {
		Long prgmId=(Long) session.getAttribute("programId");
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
		if(!selTierLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Select Tier")) {
			int tierVal = Integer.parseInt(selTierLbId.getSelectedItem().getValue().toString().replace("Tier ", ""));
			if(tierVal==prgmObj.getNoOfTiers()) {
//				crossAtValueTbId.setValue("");
//				crossAtValueTbId.setDisabled(true);
				selectedTierIsValid = false;
			//	cumTypeLbId.setDisabled(true);
			//	cumValueLbId.setDisabled(true);
			}else {
//				crossAtValueTbId.setDisabled(false);
				selectedTierIsValid = true;
			//	cumTypeLbId.setDisabled(false);
			//	cumValueLbId.setDisabled(false);
			}
			List rowList = mulTierRuleRowsId.getChildren();
			for (int i = rowList.size() - 1; i >= 0; i--) {
				Row row = (Row) rowList.get(i);
				mulTierRuleRowsId.removeChild(row);
			}
			tierUpdateFirstRow = true;
			mulTierRuleRowsId.setAttribute("ListBoxSelection", OCConstants.LOYALTY_LIFETIME_POINTS); //default on start-up
			mulTierRuleRowsId.setAttribute("TierEdit", false);
			tierUpgradeDropDownCount=2;
			addTierRule();
		}
	}//onSelect$selTierLbId()
	/*public void onSelect$levelTypeLbId(){
		if((levelValueLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Increment"))
				&&(levelTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("LPV"))){
			limitDivId.setVisible(false);
		}else limitDivId.setVisible(false);
	}*/
	/*public void onSelect$levelTypeLbId$thresholdBonusWinId(){
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
					}else{
						thresholdBonusWinId$thresholdLimitID.setValue("");
					}
				}	
			
			} catch (Exception e) {
				logger.error("Exception ::", e);
			}
		}else thresholdBonusWinId$limitDivId.setVisible(false);
	}*/
/*	public void onSelect$levelValueLbId$thresholdBonusWinId(){
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
					}else{
						thresholdBonusWinId$thresholdLimitID.setValue("");
					}
				}	
			
			} catch (Exception e) {
				logger.error("Exception ::", e);
			}
		}else thresholdBonusWinId$limitDivId.setVisible(false);
	}*/
	
	/*public void onSelect$levelTypeLbId$thresholdBonusWinId(){
		if((thresholdBonusWinId$levelValueLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Increment"))
				&&(thresholdBonusWinId$levelTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("LPV"))){
			thresholdBonusWinId$limitDivId.setVisible(true);
			if(thresholdLimitID.getValue()!=null){
				thresholdBonusWinId$thresholdLimitID.setValue(thresholdLimitID.getValue());
			}
		}else thresholdBonusWinId$limitDivId.setVisible(false);
	}*/

	/*public void onSelect$ruleTypeLbId() {
		if(ruleTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE)) {
			upgradeCumDivId.setVisible(true);
			upgradeCumDivId.setStyle("margin-top:10px;margin-right:10px;margin-left:5px;");
			cummulativeDivId.setVisible(true);
			upgradeDivId.setVisible(false);
		}else {
			upgradeDivId.setVisible(true);
			upgradeDivId.setStyle("margin-top:10px;margin-right:10px;margin-left:5px;");
			cummulativeDivId.setVisible(false);
			upgradeCumDivId.setVisible(false);
		}
	}*///onSelect$ruleTypeLbId()

	public void onSelect$earnValueTypeLbId() {
		if(earnValueTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_PERCENTAGE)){
			amountDivId.setVisible(false);
			percentDivId.setVisible(true);
		}
		else {
			amountDivId.setVisible(true);
			percentDivId.setVisible(false);
		}
	}//onSelect$earnValueTypeLbId()

	public void onSelect$earnTypeLbId() {

		if(earnTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT)) {
			coversionfromTbId.setDisabled(true);
			conversionToTbId.setDisabled(true) ;
			autoConvertRadioId.setDisabled(true);
			onDemandRadioId.setDisabled(true);
		}
		else {
			coversionfromTbId.setDisabled(false);
			conversionToTbId.setDisabled(false) ;
			autoConvertRadioId.setDisabled(false);
			onDemandRadioId.setDisabled(false);
			if(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype())){
				autoConvertRadioId.setSelected(true);
				autoConvertRadioId.setDisabled(false);
				onDemandRadioId.setDisabled(false);
				pointsConversionDivId.setVisible(true);
			}
		}
	}//onSelect$earnTypeLbId()
	
	public void onCheck$enableOTPAuthChkId() {
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		Long tierId=(Long) session.getAttribute("tierId");
		if(enableOTPAuthChkId.isChecked()) {
			otpEnabledDivId.setVisible(true);
			if(tierId != null){
				DecimalFormat f = new DecimalFormat("#0.00");
				LoyaltyProgramTier tierObj = ltyPrgmSevice.getTierObj(tierId);
				otpAmtLimitTbId.setValue(tierObj.getOtpLimitAmt() == null ? "" : f.format(tierObj.getOtpLimitAmt()));
				
			}
			else {
				otpAmtLimitTbId.setValue("");
			}
		}
		else {
			otpEnabledDivId.setVisible(false);
			otpAmtLimitTbId.setValue("");
		}
	}//onCheck$enableTierChkId()

	public void onClick$savBtnId() {

		Long tierId=null;
		Double earnValue=null;
		//String issuanceValueType;
		Double maxcap=null;
		boolean issuanceEnable=false;
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		Long programId=(Long) session.getAttribute("programId");
		LoyaltyProgram prgmObj=ltyPrgmSevice.getProgmObj(programId);
		List<LoyaltyProgramTier> tierList = ltyPrgmSevice.getTierList(programId);
		if(!isEdit) {
			if(prgmObj.getTierEnableFlag() == OCConstants.FLAG_NO ) {
				if(tierList!=null) {
					MessageUtil.setMessage("Tier already saved, please edit the existing tier.", "color:red", "TOP");	
					return;
				}
			}
		}
		
		if(enableChkIdForIssuance.isChecked()) {
			issuanceEnable=true;
		}

		if(!validateFields()) {
			return;
		}
		
		if(validatePercentageRedemption())//PercentageRedemptionValidation
			return;

		String tierType=selTierLbId.getSelectedItem().getValue().toString();
		String tierName="";
		 if(prgmObj.getTierEnableFlag() == OCConstants.FLAG_NO) {
			   tierName = "Tier 1";
			  }else {
			   tierName=privilegeNameTbId.getValue().trim();
			  }
		String earnType=earnTypeLbId.getSelectedItem().getValue().toString();

		String earnValueType=earnValueTypeLbId.getSelectedItem().getValue().toString();
		if(valueTbId.getValue()!=null && !valueTbId.getValue().trim().isEmpty()) {
			String amtRegExp = "^[0-9]*(\\.[0-9]{1,2})?$";
			Pattern pattern = Pattern.compile(amtRegExp);  
			Matcher m = pattern.matcher(valueTbId.getValue().trim());
			String match = "";
			while (m.find()) {
				match += m.group();
			}
			try {
				String value  = ""+Double.parseDouble(match);
			} catch (NumberFormatException e) {
				MessageUtil.setMessage("Currency value should not exceed two decimal points.", "Color:red", "Top");
				valueTbId.setFocus(true);
				return;
			}				
			//earnValue=Double.parseDouble(valueTbId.getValue());
			earnValue=Double.parseDouble(Utility.truncateUptoTwoDecimal(Double.parseDouble(valueTbId.getValue())));
		}
		if(issuanceValueTbId.getValue()!=null && !issuanceValueTbId.getValue().trim().isEmpty()) {
			String issamtRegExp = "^[0-9]*(\\.[0-9]{1,2})?$";
			Pattern pattern = Pattern.compile(issamtRegExp);  
			Matcher m = pattern.matcher(issuanceValueTbId.getValue().trim());
			String match = "";
			while (m.find()) {
				match += m.group();
			}
			try {
				String value  = ""+Double.parseDouble(match);
			} catch (NumberFormatException e) {
				MessageUtil.setMessage("value should not exceed two decimal points.", "Color:red", "Top");
				issuanceValueTbId.setFocus(true);
				return;
			}				
			//earnValue=Double.parseDouble(valueTbId.getValue());
			maxcap=Double.parseDouble(Utility.truncateUptoTwoDecimal(Double.parseDouble(issuanceValueTbId.getValue())));
		} 
		
		Double earnOnSpentAmount=null,convertFromPoints=null,convertToAmount=null,tierUpgdConstraintValue=null;
		String conversionType="";
		if(earnValueTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_VALUE)) {
			if(amountSpentTbId.getValue()!=null && !amountSpentTbId.getValue().trim().isEmpty()) {
			
				String amtRegExp = "^[0-9]*(\\.[0-9]{1,2})?$";
				Pattern pattern = Pattern.compile(amtRegExp);  
				Matcher m = pattern.matcher(amountSpentTbId.getValue().trim());
				String match = "";
				while (m.find()) {
					match += m.group();
				}
				try {
					String value  = ""+Double.parseDouble(match);
				} catch (NumberFormatException e) {
					MessageUtil.setMessage("Currency value should not exceed two decimal points.", "Color:red", "Top");
					amountSpentTbId.setFocus(true);
					return;
				}				
			
				//earnOnSpentAmount=Double.parseDouble(amountSpentTbId.getValue());
				earnOnSpentAmount=Double.parseDouble(Utility.truncateUptoTwoDecimal(Double.parseDouble(amountSpentTbId.getValue())));
			}
		}

		boolean dateEnable =  enableDateChkId.isChecked();
		boolean activateAfterDisableAllStore =  false;
		String disallowActivateAfterStores = Constants.STRING_NILL;

		Long ptsActiveDateValue=null;
		String ptsActiveDateType =OCConstants.LOYALTY_TYPE_DAY;
		if(enableDateChkId.isChecked()) {
			if(ptsActiveValueLbId.getSelectedItem().getValue().toString()!=null && !ptsActiveValueLbId.getSelectedItem().getValue().toString().isEmpty()) {
				ptsActiveDateValue=Long.parseLong(ptsActiveValueLbId.getSelectedItem().getValue().toString());
			}
			//APP-3284
			activateAfterDisableAllStore = onAllStore.isChecked();	
			if(!activateAfterDisableAllStore) {
				List<Listitem> listItem = selectedStoreLbId.getItems();
				int i=0;
				StringBuffer strBuff =new StringBuffer();
				for(Listitem li:listItem) {
					if(i==0) {
						strBuff.append(li.getLabel());
					}
					else {
						strBuff.append(OCConstants.FORM_MAPPING_SPLIT_DELIMETER+li.getLabel());
					}
				i++;
				}
				disallowActivateAfterStores = strBuff.toString();
			}
		}

		if(earnTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)) {
			if(coversionfromTbId.getValue()!=null && !coversionfromTbId.getValue().trim().isEmpty()) {
				convertFromPoints=Double.parseDouble(coversionfromTbId.getValue().trim());
			}
			if(conversionToTbId.getValue()!=null && !conversionToTbId.getValue().trim().isEmpty()) {
				
				String amtRegExp = "^[0-9]*(\\.[0-9]{1,2})?$";
				Pattern pattern = Pattern.compile(amtRegExp);  
				Matcher m = pattern.matcher(conversionToTbId.getValue().trim());
				String match = "";
				while (m.find()) {
					match += m.group();
				}
				try {
					String value  = ""+Double.parseDouble(match);
				} catch (NumberFormatException e) {
					MessageUtil.setMessage("Currency value should not exceed two decimal points.", "Color:red", "Top");
					conversionToTbId.setFocus(true);
					return;
				}	
				//convertToAmount=Double.parseDouble(conversionToTbId.getValue().trim());
				convertToAmount=Double.parseDouble(Utility.truncateUptoTwoDecimal(Double.parseDouble(conversionToTbId.getValue().trim())));
			}

			if(coversionfromTbId.getValue()!=null && !coversionfromTbId.getValue().trim().isEmpty()) {
				if(autoConvertRadioId.isChecked()) {
					conversionType=OCConstants.LOYALTY_CONVERSION_TYPE_AUTO;
				}
				else {
					conversionType=OCConstants.LOYALTY_CONVERSION_TYPE_ONDEMAND;
				}
			}
		}
		String tierUpgdConstraint=null;
		/*String tierUpgdConstraint=ruleTypeLbId.getSelectedItem().getValue().toString();
		if(crossAtValueTbId.getValue()!=null && !crossAtValueTbId.getValue().trim().isEmpty()) {
			String amtRegExp = "^[0-9]*(\\.[0-9]{1,2})?$";
			Pattern pattern = Pattern.compile(amtRegExp);  
			Matcher m = pattern.matcher(crossAtValueTbId.getValue().trim());
			String match = "";
			while (m.find()) {
				match += m.group();
			}
			try {
				String value  = ""+Double.parseDouble(match);
			} catch (NumberFormatException e) {
				MessageUtil.setMessage("Currency value should not exceed two decimal points.", "Color:red", "Top");
				crossAtValueTbId.setFocus(true);
				return;
			}	
			//tierUpgdConstraintValue=Double.parseDouble(crossAtValueTbId.getValue());
			tierUpgdConstraintValue=Double.parseDouble(Utility.truncateUptoTwoDecimal(crossAtValueTbId.getValue().trim()));
			//tierUpgdConstraintValue=Double.parseDouble(Utility.truncateUptoTwoDecimal(Double.parseDouble(crossAtValueTbId.getValue().trim())));
		}*/
		Long tierUpgradeCumulativeValue=null;
		//String tierUpgradeCumulativeType= null;
		/*if(tierUpgdConstraint.equalsIgnoreCase(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE)) {
			tierUpgradeCumulativeValue=Long.parseLong(cumValueLbId.getSelectedItem().getValue().toString());
			//tierUpgradeCumulativeType = cumTypeLbId.getSelectedItem().getValue().toString();

		}*/
		
		Double minReceiptValue = null;
		Double minBalanceValue = null;
		if(minReceiptAmtTbId.getValue()!=null && !minReceiptAmtTbId.getValue().isEmpty()) {
			try {
				minReceiptValue = Double.parseDouble(minReceiptAmtTbId.getValue().toString());
			}catch (Exception e) {
				MessageUtil.setMessage("Please provide valid input for Min Receipt Amount.", "color:red", "TOP");
				minReceiptAmtTbId.setFocus(true);
				return;
			}
			
		}
		if(minBalanceTbId.getValue()!=null && !minBalanceTbId.getValue().isEmpty()) {
			try {
				minBalanceValue = Double.parseDouble(minBalanceTbId.getValue().toString());
			}catch (Exception e) {
				MessageUtil.setMessage("Please provide valid input for Min Balance required to redeem.", "color:red", "TOP");
				minBalanceTbId.setFocus(true);
				return;
			}
			
		}
		
		double redemptionPercentageLimit = redemptionPercentageTbId.getValue() != null && !redemptionPercentageTbId.getValue().trim().isEmpty() 
				? Double.parseDouble(Utility.truncateUptoTwoDecimal(redemptionPercentageTbId.getValue().trim())) : 0.0;	
		double redemptionValueLimit = redemptionValueTbId.getValue() != null && !redemptionValueTbId.getValue().trim().isEmpty() 
				? Double.parseDouble(Utility.truncateUptoTwoDecimal(redemptionValueTbId.getValue().trim())) : 0.0;
		
		
		Long expAftrMonths = null;
		String expType =null;
		Double crossOverBonus = null;
		boolean rewardExpFlag=false;
		if(expAftrTbId.getValue()!=null && !expAftrTbId.getValue().toString().isEmpty())
		{
			try {
			expType = "Month";
			expAftrMonths = Long.parseLong(expAftrTbId.getValue().toString());
			rewardExpFlag=true;
			} catch(Exception e) {
				MessageUtil.setMessage("Please provide valid input for expiry.", "color:red", "TOP");
				expAftrTbId.setFocus(true);
				return;
			}
			if(expAftrMonths <= 0) {
				MessageUtil.setMessage("Please provide valid input for expiry.", "color:red", "TOP");
				expAftrTbId.setFocus(true);
				return;
			}
		}
		if(crossOverBounsTbId.getValue()!=null && !crossOverBounsTbId.getValue().toString().isEmpty())
		{
			try {
			crossOverBonus = Double.parseDouble(crossOverBounsTbId.getValue().toString());
			} catch(Exception e) {
				MessageUtil.setMessage("Please provide valid input for crossover bonus.", "color:red", "TOP");
				crossOverBounsTbId.setFocus(true);
				return;
			}
		}
		
		boolean partialReversalEnable = enablePartialReturnsAuthChkId.isChecked();
		boolean includeRedeemedAmount = enableRedeemedAmountChkId.isChecked();
		boolean otpEnable = enableOTPAuthChkId.isChecked();
		Double otpLimit = null;
		if(otpEnable) {
			otpEnable = isOptedForSMS();
			if(!otpEnable){
				enableOTPAuthChkId.setChecked(false);
				MessageUtil.setMessage("You have not opted for SMS, sending OTP for authentication cannot be enabled.", "color:red", "TOP");
				otpEnabledDivId.setVisible(false);
				otpAmtLimitTbId.setValue("");
				enableOTPAuthChkId.setChecked(false);
				return;
			}
			if(!otpAmtLimitTbId.getValue().trim().isEmpty()){
				otpLimit = Double.valueOf(otpAmtLimitTbId.getValue());
			}
		}
				
		if(isEdit) {
			//tierId=(Long) session.getAttribute("tierId");
			
			//String selectedTier=(String) session.getAttribute("tierType");
			String editedTier=(String) session.getAttribute("editedTierType");
			if(editedTier.equalsIgnoreCase(tierType)){
				tierId=(Long) session.getAttribute("tierId");
			}
		}

		/*if(!ruleTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE)) {
			// to check the ascending order of the tier upgrade constraint value
			if(crossAtValueTbId.getValue()!=null && !crossAtValueTbId.getValue().trim().isEmpty()) {
				if(tierList != null) {
					int tierVal = Integer.parseInt(selTierLbId.getSelectedItem().getValue().toString().replace("Tier ", ""));
					for(LoyaltyProgramTier loyaltyProgramTier : tierList) {
						int existTierVal = Integer.parseInt(loyaltyProgramTier.getTierType().replace("Tier ", ""));
						if(tierVal > existTierVal) {
							Double val = Double.parseDouble(crossAtValueTbId.getValue().trim());
							Double existVal = (Double) (loyaltyProgramTier.getTierUpgdConstraintValue()== null ? 0:loyaltyProgramTier.getTierUpgdConstraintValue());
							if(existVal !=0 ) {
								if(existVal >= val) {
									MessageUtil.setMessage("Cross-over to next tier value should be greater than the previous tier.", "color:red", "TOP");
									return;
								}
							}
						}else if(tierVal < existTierVal) {
							Double val = Double.parseDouble(crossAtValueTbId.getValue());
							Double existVal = (Double) (loyaltyProgramTier.getTierUpgdConstraintValue()== null ? 0:loyaltyProgramTier.getTierUpgdConstraintValue());
							if(existVal !=0 ) {
								if(existVal <= val) {
									MessageUtil.setMessage("Cross-over to next tier value should be less than the next tier.", "color:red", "TOP");
									return;
								}
							}
						}
					}
				}
			}
		}*/
		String mulTierRules = getMultipleTierRules(prgmObj); // Multile tier update rules
		
		String roundingType=null;
		if(roundLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("down")){
			roundingType="Down";
		}else if(roundLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Up")){
			roundingType="Up";
		}else if(roundLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Near")){
			roundingType="Near";
		}
		/*String ruleType=null;
		if(discountedLBId.getSelectedItem().getValue().toString().equalsIgnoreCase("all")){
			ruleType="One";
		}else{
			ruleType="Split";
		}*/
	
		//validation to check and give prompt to user when existing tier type being changed and it has linked card-sets or  active memberships
		if(tierId != null){
			LoyaltyProgramTier tierObj = ltyPrgmSevice.getTierObj(tierId);
			if(!tierType.equalsIgnoreCase(tierObj.getTierType())){

				int tierLevel = Integer.valueOf(tierObj.getTierType().substring(5)).intValue();
				List<LoyaltyCardSet> cardSetList = ltyPrgmSevice.findCardSetByTierLevel(tierLevel, programId);
				int cardSetCount = 0;
				if(cardSetList != null && cardSetList.size() > 0){
					cardSetCount = cardSetList.size();
				}

				int confirm;
				int custCount = ltyPrgmSevice.getCustomersCountByTierId(userId,tierId);
				if(cardSetCount > 0 && custCount > 0) {
					confirm = Messagebox.show("This tier has currently "+cardSetCount+" card-set(s) linked to it and "+custCount+" customer(s) belonging to it."
							+"\nFuture enrollments and the existing memberships will belong to the new tier."
							+ "\nWould you like to continue?", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm == Messagebox.OK){
						List<LoyaltyCardSet> newList = new ArrayList<LoyaltyCardSet>();
						for (LoyaltyCardSet cardSetObj : cardSetList) {
							cardSetObj.setLinkedTierLevel(Integer.valueOf(tierType.substring(5)).intValue());
							newList.add(cardSetObj);
						}
						ltyPrgmSevice.updateCardSets(newList);
					}
					else return;
				}
				else if(cardSetCount > 0 && custCount == 0){
					confirm = Messagebox.show("This tier has currently "+cardSetCount+" card-set(s) linked to it."
							+"\nFuture enrollments will belong to the new tier."
							+ "\nWould you like to continue?", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm == Messagebox.OK){
						List<LoyaltyCardSet> newList = new ArrayList<LoyaltyCardSet>();
						for (LoyaltyCardSet cardSetObj : cardSetList) {
							cardSetObj.setLinkedTierLevel(Integer.valueOf(tierType.substring(5)).intValue());
							newList.add(cardSetObj);
						}
						ltyPrgmSevice.updateCardSets(newList);
					}
					else return;
				}
				else if(cardSetCount == 0 && custCount > 0){
					confirm = Messagebox.show("This tier has currently "+custCount+" customer(s) belonging to it."
							+"\nAll the existing memberships will belong to the new tier."
							+ "\nWould you like to continue?", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm != Messagebox.OK){
						return;
					}
//					else return;
				}
			}
			tierUpgdConstraint = tierObj.getTierUpgdConstraint();
			tierUpgdConstraintValue = tierObj.getTierUpgdConstraintValue();
			tierUpgradeCumulativeValue = tierObj.getTierUpgradeCumulativeValue();
		}

		ltyPrgmSevice.saveTier(programId,tierType,tierName,earnType,earnValueType,maxcap,issuanceEnable, earnValue,earnOnSpentAmount,ptsActiveDateType,ptsActiveDateValue,
				convertFromPoints,convertToAmount,conversionType,tierUpgdConstraint,tierUpgdConstraintValue,tierId,userId,dateEnable,
				tierUpgradeCumulativeValue,roundingType,activateAfterDisableAllStore,disallowActivateAfterStores,minReceiptValue,minBalanceValue,redemptionPercentageLimit,redemptionValueLimit,expType,expAftrMonths,crossOverBonus,includeRedeemedAmount,otpEnable,otpLimit,partialReversalEnable,mulTierRules);
		MessageUtil.setMessage("Tier saved successfully.", "color:blue", "TOP");

		//for setting draft status
		prgmObj = ltyPrgmSevice.getProgmObj(programId);
		String draftStatus = "";
		draftStatus = prgmObj.getDraftStatus();
		String[] draftList = draftStatus.split(Constants.ADDR_COL_DELIMETER);
		if(prgmObj.getTierEnableFlag() == OCConstants.FLAG_YES) {
			List<LoyaltyProgramTier> list = ltyPrgmSevice.getTierList(programId);
			int count = list == null ? 0 : list.size();
			if(count==prgmObj.getNoOfTiers()) {
				boolean incomplete = false;  
				for (LoyaltyProgramTier tierObj : list) {
					int val = Integer.parseInt(tierObj.getTierType().replace("Tier ", ""));
					if(val != prgmObj.getNoOfTiers()) {
						if(tierObj.getTierUpgdConstraintValue() == null && tierObj.getMultipleTierUpgrdRules() == null) {
							incomplete = true;
							break;
						}
					}

				}
				if(!incomplete) {
					draftList[1] = OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE;
				}
				else {
					draftList[1] = OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE;	
				}
			}
			else {
				draftList[1] = OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE;
			}

		}
		else {
			draftList[1] = OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE;
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
		
		if(prgmObj.getRewardExpiryFlag()!='Y') {
			if(rewardExpFlag) prgmObj.setRewardExpiryFlag('Y');
		}
		
		ltyPrgmSevice.savePrgmObj(prgmObj);

		session.removeAttribute("tierId");
		isEdit=false;
		resetFields();
		tierList = ltyPrgmSevice.getTierList(programId);
		redrawTierList(tierList);
		savBtnId.setLabel("Add");
		cancelBtnId.setVisible(false);
		onPurchaseRadioLblId.setStyle("visibility:hidden;");
		onAllStore.setStyle("visibility:hidden;");
		selectedStores.setStyle("visibility:hidden;");
		storeNumbCmboBxId.setVisible(false);
		addStoreImgId.setVisible(false);
		selectedStoreLbId.setVisible(false);
		
		List rowList = mulTierRuleRowsId.getChildren();
		for (int i = rowList.size() - 1; i >= 0; i--) {
			Row row = (Row) rowList.get(i);
			mulTierRuleRowsId.removeChild(row);
		}
		tierUpdateFirstRow=true;
		replaceRow=false;
		mulTierRuleRowsId.setAttribute("ListBoxSelection", OCConstants.LOYALTY_LIFETIME_POINTS);
		mulTierRuleRowsId.setAttribute("TierEdit", false);
		addTierRule();
	}//onClick$savBtnId()

	private boolean validateFields() {

		if(selTierLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Select Tier")) {
			MessageUtil.setMessage("Please select the tier.", "red");
			return false;
		}
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		Long programId=(Long) session.getAttribute("programId");
		LoyaltyProgram prgmObj=ltyPrgmSevice.getProgmObj(programId);
		if(prgmObj.getTierEnableFlag() == OCConstants.FLAG_YES) {
			if(privilegeNameTbId.getValue().trim().isEmpty() || privilegeNameTbId.getValue()==null) {
				MessageUtil.setMessage("Please provide the  privilege name.", "red");
				privilegeNameTbId.setFocus(true);
				return false;
			}else if(!Utility.validateBy(Constants.LTY_NAME_PATTERN,privilegeNameTbId.getValue())) {
				MessageUtil.setMessage("Enter valid privilege name.","color:red");
				return false;
			}
		}
		
		if(privilegeNameTbId.getValue().trim().length() > 60) {
			MessageUtil.setMessage("Privilege name exceeds the maximum characters limit.", "red");
			privilegeNameTbId.setFocus(true);
			return false;
		}

		if(valueTbId.getValue().trim().isEmpty() || valueTbId.getValue()==null ) {
			MessageUtil.setMessage("Earn rule cannot be empty.", "red");
			valueTbId.setFocus(true);
			return false;
		}

		if(valueTbId.getValue().trim().length() > 60) {
			MessageUtil.setMessage("Earn rule exceeds the maximum characters limit.", "red");
			valueTbId.setFocus(true);
			return false;
		}

		if(valueTbId.getValue().trim().contains("-")) {
			MessageUtil.setMessage("Please enter valid value for earn rule.", "red");
			valueTbId.setFocus(true);
			return false;
		}
		
		if(earnTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)) {
			if(!checkIfLong(valueTbId.getValue().trim())) {
				MessageUtil.setMessage("Please provide number value for earn rule.", "red");
				valueTbId.setFocus(true);
				return false;
			}
		}else if(earnTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT)) {

			//if(earnValueTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_VALUE)) {
				if(!checkIfDouble(valueTbId.getValue().trim())) {
					MessageUtil.setMessage("Please provide number value for earn rule.", "red");
					valueTbId.setFocus(true);
					return false;
				}
			/*}else if(earnValueTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_PERCENTAGE)){
				if(!checkIfLong(valueTbId.getValue().trim())) {
					MessageUtil.setMessage("Please provide number value for earn rule.", "red");
					valueTbId.setFocus(true);
					return false;
				}
			}*/

		}

		if(earnValueTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_VALUE)) {
			if(amountSpentTbId.getValue().trim().isEmpty() || amountSpentTbId.getValue()==null) {
				MessageUtil.setMessage("Earn rule cannot be empty.", "red");
				amountSpentTbId.setFocus(true);
				return false;
			}
			if(amountSpentTbId.getValue().trim().length() > 60) {
				MessageUtil.setMessage("Earn rule exceeds the maximum characters limit.", "red");
				amountSpentTbId.setFocus(true);
				return false;
			}

			if(amountSpentTbId.getValue().trim().contains("-")) {
				MessageUtil.setMessage("Please enter valid value for earn rule.", "red");
				return false;
			}

			if(!checkIfDouble(amountSpentTbId.getValue().trim())) {
				MessageUtil.setMessage("Please provide number value for earn rule.", "red");
				amountSpentTbId.setFocus(true);
				return false;
			}
		}
		if(enableChkIdForIssuance.isChecked()) {
			if(issuanceValueTbId.getValue().trim().isEmpty() || issuanceValueTbId.getValue()==null) {
				MessageUtil.setMessage("Issuance limit cannot be empty.", "red");
				issuanceValueTbId.setFocus(true);
				return false;
			}

			if(issuanceValueTbId.getValue().trim().contains("-")) {
				MessageUtil.setMessage("Please enter valid value for Issuance limit.", "red");
				return false;
			}

			if(!checkIfDouble(issuanceValueTbId.getValue().trim())) {
				MessageUtil.setMessage("Please provide number value for Issuance limit.", "red");
				issuanceValueTbId.setFocus(true);
				return false;
			}
		} 
		
		/*if(expAftrTbId.getValue()==null || expAftrTbId.getValue().trim().isEmpty()) {
			MessageUtil.setMessage("Please provide expiry value in months.", "red");
			expAftrTbId.setFocus(true);
			return false;
		}*/

		if(earnTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)) {

			if((conversionToTbId.getValue().trim().isEmpty() && coversionfromTbId.getValue().trim().length() > 0) 
					||(conversionToTbId.getValue().trim().length() > 0 && coversionfromTbId.getValue().trim().isEmpty()) ){
				MessageUtil.setMessage("Conversion rule cannot be empty.", "red");
				coversionfromTbId.setFocus(true);
				return false;
			}

			if(coversionfromTbId.getValue().trim().contains("-")) {
				MessageUtil.setMessage("Please enter valid value for conversion rule.", "red");
				coversionfromTbId.setFocus(true);
				return false;
			}

			if(conversionToTbId.getValue().trim().contains("-")) {
				MessageUtil.setMessage("Please enter valid value for conversion rule.", "red");
				conversionToTbId.setFocus(true);
				return false;
			}

			if (conversionToTbId.getValue().trim().length() > 0) {
				if (!checkIfDouble(conversionToTbId.getValue().trim())) {
					MessageUtil.setMessage(
							"Please provide number value for conversion rule.",	"red");
					conversionToTbId.setFocus(true);
					return false;
				}
			}
			if (conversionToTbId.getValue().trim().length() > 60) {
				MessageUtil.setMessage("Conversion rule exceeds the maximum characters limit.","red");
				conversionToTbId.setFocus(true);
				return false;
			}

			if(coversionfromTbId.getValue().trim().length() > 0) {
				if (!checkIfLong(coversionfromTbId.getValue().trim())) {
					MessageUtil.setMessage("Please provide number value for conversion rule.","red");
					coversionfromTbId.setFocus(true);
					return false;
				}
			}

			if (coversionfromTbId.getValue().trim().length() > 60) {
				MessageUtil	.setMessage("Conversion rule exceeds the maximum characters limit.","red");
				coversionfromTbId.setFocus(true);
				return false;
			}


		}

		/*if(!crossAtValueTbId.isDisabled() && crossAtValueTbId.getValue().trim().isEmpty()){
			MessageUtil.setMessage("Upgrade rule cannot be empty.", "red");
			crossAtValueTbId.setFocus(true);
			return false;
		}

		if(crossAtValueTbId.getValue().trim().contains("-")) {
			MessageUtil.setMessage("Please enter valid value for upgrade rule.", "red");
			crossAtValueTbId.setFocus(true);
			return false;
		}*/


		/*if(crossAtValueTbId.getValue().trim().length() > 0) {
			if(ruleTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_LIFETIME_POINTS)) {
				if(!checkIfLong(crossAtValueTbId.getValue().trim())) {
					MessageUtil.setMessage("Please provide number value for upgrade rule.", "red");
					crossAtValueTbId.setFocus(true);
					return false;
				}
			}
			else {
				if(!checkIfDouble(crossAtValueTbId.getValue().trim())) {
					MessageUtil.setMessage("Please provide number value for upgrade rule.", "red");
					crossAtValueTbId.setFocus(true);
					return false;
				}
			}
		}*/

		/*if(crossAtValueTbId.getValue().trim().length() > 60) {
			MessageUtil.setMessage("Upgrade rule exceeds the maximum characters limit.", "red");
			crossAtValueTbId.setFocus(true);
			return false;
		}*/
		
		String tiername = privilegeNameTbId.getText();
		/*if((earnTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT)) && (ruleTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_LIFETIME_POINTS)) && (!crossAtValueTbId.isDisabled())){
			MessageUtil.setMessage("You've currently set-up ["+tiername+"] to issue loyalty currency on every purchase whereas the tier upgrade happens on accumulating loyalty points. Please note that customers may never upgrade to higher tier with this set-up.","");	
		    return true;
		}*/
		
		if(enableOTPAuthChkId.isChecked() && !otpAmtLimitTbId.getValue().trim().isEmpty()){
			if(!checkIfDouble(otpAmtLimitTbId.getValue().trim())){
				MessageUtil.setMessage("Please provide valid OTP limit.", "color:red", "TOP");
				return false;
			}
			if(Double.parseDouble(otpAmtLimitTbId.getValue().trim()) < 0){
				MessageUtil.setMessage("Please provide valid OTP limit.", "color:red", "TOP");
				return false;
			}
		}
		
		List rowList = mulTierRuleRowsId.getChildren();
		if (prgmObj.getTierEnableFlag() == OCConstants.FLAG_YES) {

			Map<String, Integer> usedMulTierRules = new HashMap<String, Integer>();
			for (Object object : rowList) {
				Row row = (Row) object;
				List rowFields = row.getChildren();
				Listbox listbox = (Listbox) rowFields.get(0);
				Listitem listitem = listbox.getSelectedItem();
				String tierType = (String) listitem.getValue();
				Textbox crossAtValueTextbox;

				if(usedMulTierRules.containsKey(tierType)) {
					usedMulTierRules.put(tierType, usedMulTierRules.get(tierType)+1);
				} else {
					usedMulTierRules.put(tierType, 1);
				}
				
				if (tierType.equalsIgnoreCase(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE)
						|| tierType.equalsIgnoreCase(OCConstants.LOYALTY_CUMULATIVE_VISITS)) {
					Div div = (Div) rowFields.get(1);
					List list = div.getChildren();
					crossAtValueTextbox = (Textbox) list.get(1);
				} else if (tierType.equalsIgnoreCase(OCConstants.LOYALTY_CUMULATIVE_POINTS)) {
					Div div1 = (Div) rowFields.get(1);
					List list1 = div1.getChildren();
					crossAtValueTextbox = (Textbox) list1.get(1);
					if (!crossAtValueTextbox.isDisabled() && crossAtValueTextbox.getValue().trim().isEmpty()) {
						MessageUtil.setMessage("Upgrade rule cannot be empty.", "red");
						crossAtValueTextbox.setFocus(true);
						return false;
					}

					Textbox withInMonth = (Textbox) list1.get(3);
					if (!withInMonth.isDisabled() && withInMonth.getValue().trim().isEmpty()) {
						MessageUtil.setMessage("Month textbox cannot be empty.", "red");
						withInMonth.setFocus(true);
						return false;
					}
					
					Div div2 = (Div) rowFields.get(2);
					List list2 = div2.getChildren();
					Textbox crossAtValueTextbox2 = (Textbox) list2.get(1);
					Textbox withInMonth2 = (Textbox) list2.get(3);
					
					if ((!crossAtValueTextbox2.getValue().trim().isEmpty() && withInMonth2.getValue().trim().isEmpty())
							|| (crossAtValueTextbox2.getValue().trim().isEmpty() && !withInMonth2.getValue().trim().isEmpty())) {
						if (!(crossAtValueTextbox2.getValue().trim().length() > 0)) {
							MessageUtil.setMessage("Upgrade rule cannot be empty.", "red");
							crossAtValueTextbox2.setFocus(true);
							return false;
						} else if (!(withInMonth2.getValue().trim().length() > 0)) {
							MessageUtil.setMessage("Month textbox cannot be empty.", "red");
							withInMonth2.setFocus(true);
							return false;
						}
					} else if(!crossAtValueTextbox2.getValue().trim().isEmpty() && !withInMonth2.getValue().trim().isEmpty()) {
						try {
							Integer value = Integer.parseInt(crossAtValueTextbox2.getValue().trim());
							if (value <= 0) {
								MessageUtil.setMessage("Upgrade rule cannot be Zero / Negative.", "red");
								crossAtValueTextbox2.setFocus(true);
								return false;
							}
						} catch (NumberFormatException e) {
							MessageUtil.setMessage("Please enter valid value for upgrade rule.", "red");
							crossAtValueTextbox2.setFocus(true);
							return false;
						}

						if (!checkIfLong(withInMonth2.getValue().trim().trim()) || withInMonth2.getValue().trim().contains("-")) {
							MessageUtil.setMessage("Please enter valid month for upgrade rule.", "red");
							withInMonth2.setFocus(true);
							return false;
						}
					}
				} else {
					Div div = (Div) rowFields.get(2);
					List list = div.getChildren();
					crossAtValueTextbox = (Textbox) list.get(0);
				}

				if (!crossAtValueTextbox.isDisabled() && crossAtValueTextbox.getValue().trim().isEmpty()) {
					MessageUtil.setMessage("Upgrade rule cannot be empty.", "red");
					crossAtValueTextbox.setFocus(true);
					return false;
				}

//				if (crossAtValueTextbox.getValue().trim().contains("-")
//						|| crossAtValueTextbox.getValue().trim().contains(" ")) {
//					MessageUtil.setMessage("Please enter valid value for upgrade rule.", "red");
//					crossAtValueTextbox.setFocus(true);
//					return false;
//				}

				if (crossAtValueTextbox.getValue().trim().length() > 0) {
					if (tierType.equalsIgnoreCase(OCConstants.LOYALTY_LIFETIME_POINTS)
							|| tierType.equalsIgnoreCase(OCConstants.LOYALTY_CUMULATIVE_VISITS)) {
						try {
							Integer value = Integer.parseInt(crossAtValueTextbox.getValue().trim());
							if (value <= 0) {
								MessageUtil.setMessage("Upgrade rule cannot be Zero / Negative.", "red");
								crossAtValueTextbox.setFocus(true);
								return false;
							}
						} catch (NumberFormatException e) {
							MessageUtil.setMessage("Please enter valid value for upgrade rule.", "red");
							crossAtValueTextbox.setFocus(true);
							return false;

						}
					} else {
						try {
							Double value = Double.parseDouble(crossAtValueTextbox.getValue().trim());
							if (value <= 0) {
								MessageUtil.setMessage("Upgrade rule cannot be Zero / Negative.", "red");
								crossAtValueTextbox.setFocus(true);
								return false;
							}
						} catch (NumberFormatException e) {
							MessageUtil.setMessage("Please enter valid value for upgrade rule.", "red");
							crossAtValueTextbox.setFocus(true);
							return false;
						} 
						String val = crossAtValueTextbox.getValue().trim();
						if (val.contains("f") || val.contains("F") || val.contains("d") || val.contains("D")) {
							MessageUtil.setMessage("Please enter valid value for upgrade rule.", "red");
							crossAtValueTextbox.setFocus(true);
							return false;
						}
					}
				}

				if (crossAtValueTextbox.getValue().trim().length() > 60) {
					MessageUtil.setMessage("Upgrade rule exceeds the maximum characters limit.", "red");
					crossAtValueTextbox.setFocus(true);
					return false;
				}

				if (tierType.equalsIgnoreCase(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE)
						|| tierType.equalsIgnoreCase(OCConstants.LOYALTY_CUMULATIVE_VISITS)) {
					Div div = (Div) rowFields.get(2);
					List list = div.getChildren();
					Textbox withInMonth = (Textbox) list.get(1);

					if (!withInMonth.isDisabled() && withInMonth.getValue().trim().isEmpty()) {
						MessageUtil.setMessage("Month textbox cannot be empty.", "red");
						withInMonth.setFocus(true);
						return false;
					}

					if ((withInMonth.getValue().trim().length() > 0 && !checkIfLong(withInMonth.getValue().trim()))
							|| withInMonth.getValue().trim().contains("-")) {
						MessageUtil.setMessage("Please enter valid month for upgrade rule.", "red");
						withInMonth.setFocus(true);
						return false;
					}
				}
			}
			for(Map.Entry<String, Integer> map : usedMulTierRules.entrySet()) {
				if (map.getValue()>1) {
					MessageUtil.setMessage("Same Tier rule can't use more than once.", "red");
					return false;
				}
			}
		}

		return true;

	}//validateFields()
	
	public boolean isOptedForSMS() {
		Users currentUser = GetUser.getUserObj();

		if(!currentUser.isEnableSMS()) {
			return false;
		}
		return true;
	}//isOptedForSMS()
	
	public boolean validatePercentageRedemption() {//Percentage Redemption

		//changes 2.5.5.0
		String redemptionPercentageVal=redemptionPercentageTbId.getValue().toString();
		String redemptionFlatVal=redemptionValueTbId.getValue().toString();
		 
		
		if(!redemptionPercentageVal.isEmpty()) { //For SBtoOC changes 2.5.5.0
				if(checkUpTwoDecimalPlaces(redemptionPercentageVal)){
				
					MessageUtil.setMessage("Limit Redemption Amount should be a valid number with upto two decimal places.", "color:red", "TOP");
					return true;
				}
				if(!checkIfDouble(redemptionPercentageVal)) {
					MessageUtil.setMessage("Entered value should be a decimal value.", "color:red;margin-right:10px", "TOP");
					return true;
				}
				if(Double.parseDouble(redemptionPercentageVal)>100 || Double.parseDouble(redemptionPercentageVal)<=0) {
					MessageUtil.setMessage("Entered value cannot be negative, 0 or exceed 100.", "color:red;margin-right:10px", "TOP");
					return true;
				}
		}		
		if(!redemptionFlatVal.isEmpty()) {//For SBtoOC changes 2.5.5.0
			
				
				if(!checkIfDouble(redemptionFlatVal)) {
					MessageUtil.setMessage("Entered value should be a decimal value.", "color:red;margin-right:10px", "TOP");
					return true;
				}
				if(Double.parseDouble(redemptionFlatVal)<=0) {
					MessageUtil.setMessage("Entered value cannot be negative or 0.", "color:red;margin-right:10px", "TOP");
					return true;
				}
				if(checkUpTwoDecimalPlaces(redemptionFlatVal)) {
					MessageUtil.setMessage("Billed Amount, or flat value should be a valid number with upto two decimal places.", "color:red", "TOP");
					return true;
				}
		}
		return false;
	}
	
	public boolean checkUpTwoDecimalPlaces(String value) {//APP--1169,1170
		logger.info("Value is"+value);
		if(value!=null && !value.isEmpty()&& value.contains(".")) {
		logger.info("Entered check");
		String[] splitter = value.toString().split("\\.");
		int decimalLength = splitter.length==1?0:splitter[1].length();  
		if( decimalLength ==0) {
			return true;
		}
		if (decimalLength > 2) {	
			return true;
			}
		}
		return false;
	}// checkUpTwoDecimalPlaces()

	private boolean checkIfLong(String in) {
		try {
			Long.parseLong(in);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}//checkIfNum()

	public boolean checkIfDouble(String in) {
		try {
			Double.parseDouble(in);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}// checkIfNumber()


	public void onClick$cancelBtnId() {
		resetFields();
		cancelBtnId.setVisible(false);
		savBtnId.setLabel("Add");
		session.removeAttribute("loyaltyProgramTier");
		session.removeAttribute("tierId");
		List rowList = mulTierRuleRowsId.getChildren();
		for (int i = rowList.size() - 1; i >= 0; i--) {
			Row row = (Row) rowList.get(i);
			mulTierRuleRowsId.removeChild(row);
		}
		tierUpdateFirstRow=true;
		replaceRow=false;
		mulTierRuleRowsId.setAttribute("ListBoxSelection", OCConstants.LOYALTY_LIFETIME_POINTS);
		mulTierRuleRowsId.setAttribute("TierEdit", false);
		addTierRule();
	}//onClick$cancelBtnId()

	private void resetFields() {
		Components.removeAllChildren(selTierLbId);
		Long prgmId=(Long) session.getAttribute("programId");
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		List<LoyaltyProgramTier> tierList = ltyPrgmSevice.getTierList(prgmId);
		LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);

		logger.info("prgmObj"+prgmObj);
		Listitem li=null;
		li=new Listitem("Select Tier");
		li.setValue("Select Tier");
		li.setParent(selTierLbId);
		selTierLbId.setSelectedItem(li);
		if(prgmObj != null) {
			if(prgmObj.getTierEnableFlag() == OCConstants.FLAG_YES ) {
				int noOfTiers=prgmObj.getNoOfTiers();
				logger.info("noOfTiers"+noOfTiers);
				for(int i=1;i<=noOfTiers;i++) {
					String tier="Tier "+i;
					boolean isExists=false;
					if(tierList != null) {
						for(LoyaltyProgramTier ltyprogrmtier:tierList) {
							if(ltyprogrmtier.getTierType().equalsIgnoreCase(tier)) {
								isExists=true;
							}
						}
					}
					if(!isExists) {
						li=new Listitem(tier);
						li.setValue(tier);
						li.setParent(selTierLbId);
						selTierLbId.setSelectedIndex(0);
					}
				}
			}
			else {

				if(tierList!=null) {
					selTierLbId.setSelectedItem(li);
					selTierLbId.setDisabled(true);
				}
				else {
					logger.info("in else");
					li=new Listitem("Tier 1");
					li.setValue("Tier 1");
					li.setParent(selTierLbId);
					selTierLbId.setSelectedItem(li);
					selTierLbId.setDisabled(true);
					//ruleTypeLbId.setDisabled(true);
					//crossAtValueTbId.setValue("");
					//crossAtValueTbId.setDisabled(true);

				}
			}
		}
		privilegeNameTbId.setValue("");
		if(tierList == null) {
			earnTypeLbId.setDisabled(false);
		}else {
			earnTypeLbId.setDisabled(true);
		}
		earnValueTypeLbId.setSelectedIndex(0);
		if(earnValueTypeLbId.getSelectedItem().getValue().equals(OCConstants.LOYALTY_TYPE_VALUE)) {
			amountDivId.setVisible(true);
			percentDivId.setVisible(false);
		}
		valueTbId.setValue("");
		amountSpentTbId.setValue("");
		issuanceValueTbId.setValue("");
		minReceiptAmtTbId.setValue("");
		minBalanceTbId.setValue("");
		redemptionPercentageTbId.setValue("");
		redemptionValueTbId.setValue("");
		crossOverBounsTbId.setValue("");
		expAftrTbId.setValue("");
		enableDateChkId.setChecked(false);
		otpEnabledDivId.setVisible(false);
		otpAmtLimitTbId.setValue("");
		enableOTPAuthChkId.setChecked(false);
		enablePartialReturnsAuthChkId.setChecked(true);
		enableRedeemedAmountChkId.setChecked(false);
		onPurchaseRadioLblId.setStyle("visibility:hidden;");
		onAllStore.setStyle("visibility:hidden;");
		selectedStores.setStyle("visibility:hidden;");
		storeNumbCmboBxId.setVisible(false);
		addStoreImgId.setVisible(false);
		selectedStoreLbId.setVisible(false);
		try {
		enableChkIdForIssuance.setChecked(false);
		if(!enableChkIdForIssuance.isChecked()) {
			valueDivId.setVisible(false);
			issuanceAmountDivId.setVisible(false);
			 } } catch(Exception e) {}
		activateAfterDivId.setVisible(false);
		ptsActiveValueLbId.setSelectedIndex(0);
		coversionfromTbId.setValue("");
		conversionToTbId.setValue("");
		if(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype())){
			autoConvertRadioId.setSelected(true);
			pointsConversionDivId.setVisible(true);
		}else{
			onDemandRadioId.setSelected(true);
		}

		/*if(prgmObj != null) {
			if(prgmObj.getTierEnableFlag() == OCConstants.FLAG_YES ) {
				if(tierList == null) {
					ruleTypeLbId.setDisabled(false);
				}
				else {

					boolean isExists = false;
					LoyaltyProgramTier tierObj = null;
					for(LoyaltyProgramTier loyaltyTier :tierList) {
						if(loyaltyTier.getTierUpgdConstraintValue() != null){
							isExists = true;
							tierObj = loyaltyTier;
							break;
						}
					}
					if(isExists) {
						List<Listitem> upgradeList = ruleTypeLbId.getItems();
						for(Listitem listitem:upgradeList) {
							if(tierObj.getTierUpgdConstraint().equalsIgnoreCase(listitem.getValue().toString())) {
								ruleTypeLbId.setSelectedItem(listitem);
								ruleTypeLbId.setDisabled(true);
							}
						}
					}
					else{
						ruleTypeLbId.setSelectedIndex(0);
						ruleTypeLbId.setDisabled(false);
					}

				}
				crossAtValueTbId.setValue("");
				crossAtValueTbId.setDisabled(false);
				//cumTypeLbId.setSelectedIndex(0);
				//cumTypeLbId.setDisabled(false);
//				cumValueLbId.setSelectedIndex(0);
//				cumValueLbId.setDisabled(false);
			}
		}*/
		/*if(ruleTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE)) {
			upgradeDivId.setVisible(false);
			cummulativeDivId.setVisible(true);
			upgradeCumDivId.setVisible(true);
			upgradeCumDivId.setStyle("margin-top:10px;margin-right:10px;margin-left:5px;");
		}else {
			upgradeDivId.setVisible(true);
			upgradeDivId.setStyle("margin-top:10px;margin-right:10px;margin-left:5px;");
			cummulativeDivId.setVisible(false);
			upgradeCumDivId.setVisible(false);
		}*/
		roundLbId.setSelectedIndex(0);
	}//resetFields()

	public void onClick$addBtnId() {
		isEdit=false;
		Long thresholdId= null;
		Double extraBonusValue=null;
		Double earnedLevelValue=null;
		boolean isRecurring = false;
		char registrationFlag=OCConstants.FLAG_NO;
		if(bonusValueTbId.getValue()==null || bonusValueTbId.getValue().trim().isEmpty() 
				|| levelValueTbId.getValue()==null || levelValueTbId.getValue().trim().isEmpty() ) {
			MessageUtil.setMessage("Bonus on threshold value cannot be empty.", "red");
			return;
		}

	

		if(bonusValueTbId.getValue().trim().length() > 60 || levelValueTbId.getValue().trim().length() > 60 ) {
			MessageUtil.setMessage("Bonus on threshold value exceeds the maximum characters.", "red");
			return;
		}


		if(bonusValueTbId.getValue().trim().length() > 0 || levelValueTbId.getValue().trim().length() > 0) {

			if(bonusTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)) {

				if(!checkIfLong(bonusValueTbId.getValue().trim())) {
					MessageUtil.setMessage("Please provide number value for bonus on threshold.", "red");
					bonusValueTbId.setFocus(true);
					return ;
				}
				if( Integer.parseInt(bonusValueTbId.getValue()) <= 0 ) {
					MessageUtil.setMessage("Please provide valid value for bonus on threshold.", "red");
					return;
				}
			}else {
				if(!checkIfDouble(bonusValueTbId.getValue().trim())) {
					MessageUtil.setMessage("Please provide number value for bonus on threshold.", "red");
					bonusValueTbId.setFocus(true);
					return ;
				}
				if(Double.parseDouble(bonusValueTbId.getValue()) <= 0) {
					MessageUtil.setMessage("Please provide valid value for bonus on threshold.", "red");
					return;
				}
			}
			if(levelTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)) {

				if(!checkIfLong(levelValueTbId.getValue().trim())) {
					MessageUtil.setMessage("Please provide number value for bonus on threshold.", "red");
					levelValueTbId.setFocus(true);
					return ;
				}
				if( Integer.parseInt(levelValueTbId.getValue()) <= 0 ) {
					MessageUtil.setMessage("Please provide valid value for bonus on threshold.", "red");
					return;
				}
			}else if(levelTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.THRESHOLD_TYPE_TIER)) {

				if(!checkIfLong(levelValueTbId.getValue().trim())) {
					MessageUtil.setMessage("Please provide number value for bonus on threshold.", "red");
					levelValueTbId.setFocus(true);
					return ;
				}
				if( Integer.parseInt(levelValueTbId.getValue()) <= 0 ) {
					MessageUtil.setMessage("Please provide number value for bonus on threshold.", "red");
					return;
				}
				if( Integer.parseInt(levelValueTbId.getValue()) <= 1) {
					MessageUtil.setMessage("Please Enter Tiers Greater than 1.", "red");
					return;
				}
				if( Integer.parseInt(levelValueTbId.getValue()) > availableTiersCount) {
					MessageUtil.setMessage("Please provide available Tier Number.", "red");
					return;
				}
			}else {
				if(!checkIfDouble(levelValueTbId.getValue().trim())) {
					MessageUtil.setMessage("Please provide number value for bonus on threshold.", "red");
					levelValueTbId.setFocus(true);
					return ;
				}
				if(Double.parseDouble(levelValueTbId.getValue()) <= 0) {
					MessageUtil.setMessage("Please provide valid value for bonus on threshold.", "red");
					return;
				}
			}

		}
		/*if(levelTypeLbId.getSelectedItem().getValue().equals("LPV")&&(thresholdLimitID.getValue()!=null && !thresholdLimitID.getValue().isEmpty())){

			if(!checkIfLong(thresholdLimitID.getValue().trim())) {
				MessageUtil.setMessage("Please provide number value for limit on threshold.", "red");
				thresholdLimitID.setFocus(true);
				return ;
			}
			if(Integer.parseInt(thresholdLimitID.getValue()) <= 0) {
				MessageUtil.setMessage("Please provide valid value for bonus on threshold.", "red");
				return;
			}
			if(Double.parseDouble(thresholdLimitID.getValue()) <= (Double.parseDouble(levelValueTbId.getValue()))) {
				MessageUtil.setMessage("Threshold limit cannot be less than threshold value.", "red");
				return;
			}
		}*/
		if(isEdit) {
			thresholdId = (Long) session.getAttribute("thresholdId");
		}
		Long programId=(Long) session.getAttribute("programId");
		
		if(levelTypeLbId.getSelectedItem().getValue().equals("LPV")){
			String earnType="LPV";
			Double earnlevel=Double.parseDouble(levelValueTbId.getValue());
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

		String extraBonusType=bonusTypeLbId.getSelectedItem().getValue().toString();
		if(bonusValueTbId.getValue()!=null && !bonusValueTbId.getValue().trim().isEmpty()) {
			String amtRegExp = "^[0-9]*(\\.[0-9]{1,2})?$";
			Pattern pattern = Pattern.compile(amtRegExp);  
			Matcher m = pattern.matcher(bonusValueTbId.getValue().trim());
			String match = "";
			while (m.find()) {
				match += m.group();
			}
			try {
				String value  = ""+Double.parseDouble(match);
			} catch (NumberFormatException e) {
				MessageUtil.setMessage("Currency value should not exceed two decimal points.", "Color:red", "Top");
				bonusValueTbId.setFocus(true);
				return;
			}
			//extraBonusValue=Double.parseDouble(bonusValueTbId.getValue());
			extraBonusValue=Double.parseDouble(Utility.truncateUptoTwoDecimal(Double.parseDouble(bonusValueTbId.getValue())));
		}
		String earnedLevelType=levelTypeLbId.getSelectedItem().getValue().toString();
		if(levelValueTbId.getValue()!=null && !levelValueTbId.getValue().trim().isEmpty()) {
			String amtRegExp = "^[0-9]*(\\.[0-9]{1,2})?$";
			Pattern pattern = Pattern.compile(amtRegExp);  
			Matcher m = pattern.matcher(levelValueTbId.getValue().trim());
			String match = "";
			while (m.find()) {
				match += m.group();
			}
			try {
				String value  = ""+Double.parseDouble(match);
			} catch (NumberFormatException e) {
				MessageUtil.setMessage("Currency value should not exceed two decimal points.", "Color:red", "Top");
				levelValueTbId.setFocus(true);
				return;
			}
			//earnedLevelValue=Double.parseDouble(levelValueTbId.getValue());
			earnedLevelValue=Double.parseDouble(Utility.truncateUptoTwoDecimal(Double.parseDouble(levelValueTbId.getValue())));
			logger.info("label "+levelValueLbId.getSelectedItem().getLabel());
			String recurring = levelValueLbId.getSelectedItem().getValue();
			logger.info(recurring);
	
			
			if(recurring.equalsIgnoreCase("Increment")){
				isRecurring = true;
			}else
				isRecurring = false;
			
			
		}
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		LoyaltyAutoComm loyaltyAutoComm=ltyPrgmSevice.getAutoCommunicationObj(programId);
		Long threshBonusEmailTmpltId = selBonusAutoEmailsLbId.getSelectedItem().getValue();
		Long threshBonusSmsTmpltId = selBonusAutoSmsLbId.getSelectedItem().getValue();	
		Double thresholdLimitIDValue=null;
		/*if(thresholdLimitID.getValue()!=null && !thresholdLimitID.getValue().isEmpty())
		{
			 thresholdLimitIDValue=Double.parseDouble(thresholdLimitID.getValue());
		}*/
		String bonusExpiryDateType=null;
		Long bonusExpiryDateValue=null;
		Long rewardExpirySmsTmpltId = null;
		Long rewardExpiryEmailTmpltId =null;
		if(earnedRwrdExpiryCbId.isChecked()){
			
			bonusExpiryDateType=giftAmtExpTypeLbId.getSelectedItem().getValue().toString();
			rewardExpirySmsTmpltId = selRewardAutoSmsLbId.getSelectedItem().getValue();
			rewardExpiryEmailTmpltId = selRewardAutoEmailsLbId.getSelectedItem().getValue();
			if(giftAmtExpValueTbId.getValue()!=null && !giftAmtExpValueTbId.getValue().isEmpty()) {
				try {
					bonusExpiryDateValue=Long.parseLong(giftAmtExpValueTbId.getValue());
					if (bonusExpiryDateValue < 1) {
						MessageUtil.setMessage("Please provide valid input for Reward Expiry Month(s).", "color:red", "TOP");
						return;
					}
				}catch (Exception e) {
					MessageUtil.setMessage("Please provide valid input for Reward Expiry Month(s).", "color:red", "TOP");
					giftAmtExpValueTbId.setFocus(true);
					return;
				}
				
			} else {
				MessageUtil.setMessage("please provide Reward Expiry Month(s)", "Color:red", "Top");
				giftAmtExpValueTbId.setFocus(true);
				return;
			}
		}

	
		
		ltyPrgmSevice.saveBonus(programId,extraBonusType,extraBonusValue,earnedLevelType,earnedLevelValue,thresholdId,registrationFlag,userId,isRecurring,thresholdLimitIDValue,threshBonusEmailTmpltId,threshBonusSmsTmpltId,bonusExpiryDateType,bonusExpiryDateValue,rewardExpiryEmailTmpltId,rewardExpirySmsTmpltId);
		MessageUtil.setMessage("Threshold  saved successfully.", "color:blue", "TOP");
		List<LoyaltyThresholdBonus> bonusList = ltyPrgmSevice.getBonusList(programId);
		redrawBonusList(bonusList);
		bonusValueTbId.setValue("");
		levelValueTbId.setValue("");
		//thresholdLimitID.setValue("");
	//	limitDivId.setVisible(false);
		bonusTypeLbId.setSelectedIndex(0);
		levelTypeLbId.setSelectedIndex(0);
		levelValueLbId.setSelectedIndex(0);
		selBonusAutoEmailsLbId.setSelectedIndex(0);
		selBonusAutoSmsLbId.setSelectedIndex(0);
		giftAmtExpTypeLbId.setSelectedIndex(0);
		//giftAmtExpValueLbId.setSelectedIndex(0);
		giftAmtExpValueTbId.setValue("");
		earnedRwrdExpiryCbId.setChecked(false);
		rewardTierDivId.setVisible(false);
		expiryAutoCommDiv.setVisible(false);
		
	}//onClick$addBtnId()

	private void redrawBonusList(List<LoyaltyThresholdBonus> bonusList) {
		Components.removeAllChildren(levelRowsId);
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		for(LoyaltyThresholdBonus loyaltyThresholdBonus:bonusList) {
			

			Row row = new Row();
			row.setParent(levelRowsId);
			if(loyaltyThresholdBonus.getRegistrationFlag()==OCConstants.FLAG_YES) {
				row.appendChild(new Label("On Enrollment"));
			}
			else {
				String earnedPoints=ltyPrgmSevice.getEarnedPoints(loyaltyThresholdBonus);
				
				if(!earnedPoints.isEmpty() && earnedPoints.contains("$")){
					
					String currSymbol = Utility.countryCurrencyMap.get(currUser.getCountryType());
					if(currSymbol != null && !currSymbol.isEmpty()) userCurrencySymbol = currSymbol + " ";
					    
					earnedPoints = earnedPoints.contains("$")? earnedPoints.replace("$", userCurrencySymbol):earnedPoints; 
				}
				
				
				row.appendChild(new Label(earnedPoints));
			}
			String bonus=ltyPrgmSevice.getBonus(loyaltyThresholdBonus);
			if(!bonus.isEmpty() && bonus.contains("$")){
				
				String currSymbol = Utility.countryCurrencyMap.get(currUser.getCountryType());
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
			
			
			logger.info("threshold id===="+loyaltyThresholdBonus.getThresholdBonusId());
			logger.info("email id======"+loyaltyThresholdBonus.getEmailTempId());
			logger.info("sms id ====="+loyaltyThresholdBonus.getSmsTempId());
			
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
				delImg.setStyle("cursor:pointer;margin-right:10px;margin-top:5px;margin-bottom:5px;");
				delImg.addEventListener("onClick", this);
				delImg.setAttribute("type", "thresholdDelete");
				delImg.setParent(hbox);
				
				
				
				
		
				if(loyaltyThresholdBonus.getEmailTempId()!=null){
							
							Image prev = new Image("/images/Preview_icn.png");
							prev.setStyle("cursor:pointer;margin-top:5px;margin-bottom:5px;");
							prev.setParent(hbox);
							
							A prevEmail = new A();
							prevEmail.setLabel("Preview Email");
							prevEmail.setStyle("color:#2886B9;text-decoration: underline;margin-left:5px;margin-top:10px;margin-bottom:5px;margin-right:10px;");
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
					prevSms.setStyle("color:#2886B9;text-decoration: underline;margin-left:5px;margin-bottom:5px;margin-top:10px;");
					prevSms.setWidth("100px");
					prevSms.addEventListener("onClick", this);
					prevSms.setAttribute("type", "previewSms");
					prevSms.setAttribute("templateId", loyaltyThresholdBonus.getSmsTempId());
					prevSms.setParent(hbox);
				}
				
				row.setValue(loyaltyThresholdBonus);
				hbox.setParent(row);


				
				
				/*Listbox autoEmailLb= new Listbox();
				autoEmailLb.setMold("select");
				autoEmailLb.setStyle("cursor:pointer;margin-right:5px;margin-left:5px;");	
				getLoyaltyTemplateList(autoEmailLb, "earnedBonus");
				autoEmailLb.setParent(hbox);
				if(loyaltyAutoComm != null) {
					
					setBonusValuesInEdit(loyaltyAutoComm,autoEmailLb,loyaltyThresholdBonus);
				}
				autoEmailLb.setDisabled(true);
				
				Listbox autoSmsLb= new Listbox();
				autoSmsLb.setMold("select");
				autoSmsLb.setStyle("cursor:pointer;margin-left:5px;");
				getLoyaltySmsTemplateList(autoSmsLb,"earningBonus");
				autoSmsLb.setParent(hbox);
				
				
				if(loyaltyAutoComm != null) {
					
					setSMSBonusValuesInEdit(loyaltyAutoComm,autoSmsLb,loyaltyThresholdBonus);
				}
				autoSmsLb.setDisabled(true);
			*/	
				
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
	}//redrawBonusList()

	private void redrawTierList(List<LoyaltyProgramTier> tierList) {
		Long prgmId=(Long) session.getAttribute("programId");
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
		resetTiersGridCols(prgmObj);
		Components.removeAllChildren(tierRowsId);
		int noOfTiers=prgmObj.getNoOfTiers();
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
						String earn = ltyPrgmSevice.getEarn(loyaltyProgramTier);
						
						if(!earn.isEmpty() && earn.contains("$")){
							
							String currSymbol = Utility.countryCurrencyMap.get(currUser.getCountryType());
							if(currSymbol != null && !currSymbol.isEmpty()) userCurrencySymbol = currSymbol + " ";
							    
							   earn = earn.contains("$")? earn.replace("$", userCurrencySymbol):earn; 
						}
						
						
						row.appendChild(new Label(earn));
						
						String conversionRule = ltyPrgmSevice
								.getRule(loyaltyProgramTier);
	                   if(!conversionRule.isEmpty() && conversionRule.contains("$")){
							
							String currSymbol = Utility.countryCurrencyMap.get(currUser.getCountryType());
							if(currSymbol != null && !currSymbol.isEmpty()) userCurrencySymbol = currSymbol + " ";
							    
							conversionRule = conversionRule.contains("$")? conversionRule.replace("$", userCurrencySymbol):conversionRule; 
						}
						
						
						
						row.appendChild(new Label(conversionRule));
						String activationTime = ltyPrgmSevice
								.getActivationTime(loyaltyProgramTier);
						row.appendChild(new Label(activationTime));

						if(loyaltyProgramTier.getEarnType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)&& !conversionRule.isEmpty() && loyaltyProgramTier.getConversionType() != null ) {
							row.appendChild(new Label(loyaltyProgramTier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO) ? "Auto-Conversion" : "On-Demand"));
						}
						else {
							row.appendChild(new Label(""));
						}
						
						if(prgmObj.getTierEnableFlag() == OCConstants.FLAG_YES ) {
							String tierUpgradeRule = ltyPrgmSevice
									.getUpgradeRule(loyaltyProgramTier);
							
							
					 if(!tierUpgradeRule.isEmpty() && tierUpgradeRule.contains("$")){
									
							String currSymbol = Utility.countryCurrencyMap.get(currUser.getCountryType());
							if(currSymbol != null && !currSymbol.isEmpty()) userCurrencySymbol = currSymbol + " ";
									    
							tierUpgradeRule = tierUpgradeRule.contains("$")? tierUpgradeRule.replace("$", userCurrencySymbol):tierUpgradeRule; 
						}
							
							row.appendChild(new Label(tierUpgradeRule));
						}
						
						//APP-3666
						String rewardExpStr = "No";
						if(loyaltyProgramTier.getRewardExpiryDateType()!=null && loyaltyProgramTier.getRewardExpiryDateValue()!=null) {
							rewardExpStr = "After "+loyaltyProgramTier.getRewardExpiryDateValue()+" Month(s) of earning, at the end of the month.";
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
				if(prgmObj.getTierEnableFlag() == OCConstants.FLAG_YES ) {
					row.appendChild(new Label("--"));
				}
				row.appendChild(new Label());
			}
		}	
	}//redrawTierList()

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
		ptsConversion.setWidth("180px");
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

	public void onClick$previousBtnId() {
		Long prgmId = (Long) Sessions.getCurrent().getAttribute("programId");
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
		if(prgmObj.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE)) {
			String draftStatus = prgmObj.getDraftStatus();
			String[] draftList = draftStatus.split(Constants.ADDR_COL_DELIMETER);
			String step2 = draftList[1];
			if(step2.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE)) {
				MessageUtil.setMessage("Looks like the program is missing some mandatory settings \n" +
						"without which it may not function as expected.\n" +
						" Please review the program's settings on Step 6.", "color:blue", "TOP");
			}
		}
		Redirect.goTo(PageListEnum.LOYALTY_CREATE_PROGRAM);
		/*if(prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE) ) {
			Redirect.goTo(PageListEnum.LOYALTY_CREATE_PROGRAM);
		}
		else {
			Redirect.goTo(PageListEnum.LOYALTY_ADD_CARDS);
		}*/
	}//onClick$previousBtnId()

	public void onClick$nextBtnId() {
		Long prgmId = (Long) session.getAttribute("programId");
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
		if(prgmObj.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE)) {
			String draftStatus = prgmObj.getDraftStatus();
			String[] draftList = draftStatus.split(Constants.ADDR_COL_DELIMETER);
			String step2 = draftList[1];
			if(step2.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE)) {
				MessageUtil.setMessage("Looks like the program is missing some mandatory settings \n " +
						"without which it may not function as expected.\n" +
						" Please review the program's settings on Step 6.", "color:blue", "TOP");
			}
		}
		if(prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE) ) {
			/*if(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype())){
				Redirect.goTo(PageListEnum.LOYALTY_AUTO_COMMUNICATION);
			}else{
				Redirect.goTo(PageListEnum.LOYALTY_ADDITIONAL_SETTINGS);
			}*/
			Redirect.goTo(PageListEnum.LOYALTY_ADDITIONAL_SETTINGS);
		}else if (prgmObj.getProgramType().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_TYPE_DYNAMIC)){
			if(ltyPrgmSevice.getCardSetsByCardGenerationType(OCConstants.LOYALTY_CARD_GENERATION_TYPE_DYNAMIC, prgmId)==null){
			ltyPrgmSevice.onAddCardSet("Dynamic cardset",null, OCConstants.LOYALTY_CARDSET_TYPE_VIRTUAL, null,OCConstants.LOYALTY_CARDSET_STATUS_ACTIVE, OCConstants.FLAG_NO, prgmId, userId, 0, OCConstants.LOYALTY_CARD_GENERATION_TYPE_DYNAMIC);
			}
			/*if(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype())){
				Redirect.goTo(PageListEnum.LOYALTY_AUTO_COMMUNICATION);
			}else{
				Redirect.goTo(PageListEnum.LOYALTY_ADDITIONAL_SETTINGS);
			}*/
			Redirect.goTo(PageListEnum.LOYALTY_ADDITIONAL_SETTINGS);
		}else {
			Redirect.goTo(PageListEnum.LOYALTY_ADD_CARDS);
		}
	}//onClick$nextBtnId()

	Integer tierUpgradeDropDownCount = 2;
	Integer noOfTierRulesAvailable = 6;
	public void onEvent(Event event) throws Exception {

		super.onEvent(event);
		
		if (event.getTarget() instanceof Listbox) {
			Listbox listBox = (Listbox) event.getTarget();
			Listitem listItem = listBox.getSelectedItem();
			String tierType = (String) listItem.getValue();

			if (tierType.equals(OCConstants.LOYALTY_LIFETIME_POINTS)
					|| tierType.equals(OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE)
					|| tierType.equals(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE)
					|| tierType.equals(OCConstants.LOYALTY_SINGLE_PURCHASE_VALUE)
					|| tierType.equals(OCConstants.LOYALTY_CUMULATIVE_POINTS)
					|| tierType.equals(OCConstants.LOYALTY_CUMULATIVE_VISITS)) {
				Row row = (Row) listBox.getParent();
				if (row.getIndex() == 0) {
					tierUpdateFirstRow = true;
				}
				replaceRow = true;
				mulTierRuleRowsId.setAttribute("TierEdit", false);
				mulTierRuleRowsId.setAttribute("ROW", row);
				mulTierRuleRowsId.setAttribute("ListBoxSelection", tierType);
				addTierRule();
			}
			return;
		}
		
		if(event.getTarget() instanceof Image) {

			Image tempImg = (Image)event.getTarget();

			if("DELETE_STORE".equalsIgnoreCase((String) tempImg.getAttribute("EXCLUSION_TYPE"))) {
				logger.info("delete store here ");
				Listitem storeItem = (Listitem)tempImg.getParent().getParent();
				selectedStoreLbId.removeChild(storeItem);

			}

		}
		
		if(event.getTarget() instanceof Image){
			Image img = (Image)event.getTarget();
			//APP-3284
			if("DELETE_STORE".equalsIgnoreCase((String) img.getAttribute("EXCLUSION_TYPE"))) {
				logger.info("delete store here ");
				Listitem storeItem = (Listitem)img.getParent().getParent();
				selectedStoreLbId.removeChild(storeItem);

			}
			else {
				if (((String) img.getAttribute("TYPE")) != null) {

					if (img.getAttribute("TYPE").equals("ADD_TIER_RULE")) {
						tierUpdateFirstRow = false;
						mulTierRuleRowsId.setAttribute("ListBoxSelection", OCConstants.LOYALTY_LIFETIME_POINTS);
						mulTierRuleRowsId.setAttribute("TierEdit", false);
						
						if(tierUpgradeDropDownCount <= noOfTierRulesAvailable) {
							tierUpgradeDropDownCount++;
							addTierRule();
						} else {
							MessageUtil.setMessage("Only 6 types of Tier upgradation rules can be added for single Tier.", "Color:blue", "Top");
						}
						return;
					}

					else if (img.getAttribute("TYPE").equals("DELETE_TIER_RULE")) {
						Row temRow = (Row) img.getParent().getParent();
						mulTierRuleRowsId.removeChild(temRow);
						tierUpgradeDropDownCount--;
						return;
					}
				}
				
			Row tempRow = ((Row)((Hbox)img.getParent()).getParent());

			LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();

			String evtType = (String)img.getAttribute("type");

			if("tierEdit".equalsIgnoreCase(evtType)) {
				LoyaltyProgramTier  loyaltyProgramTier = (LoyaltyProgramTier)tempRow.getValue();
				String type="edit";
				savBtnId.setLabel("Save");
				cancelBtnId.setVisible(true);
				tierSettings(loyaltyProgramTier,type);
				privilegeNameTbId.setFocus(true);
				session.setAttribute("editedTierType", loyaltyProgramTier.getTierType().toString());
			}
			else if("tierDelete".equalsIgnoreCase(evtType)) {
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
					confirm = Messagebox.show("There are "+ custCount+ " customers in this tier.\n Do you want to continue deleting the tier?", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
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
						resetFields();
						tierList = ltyPrgmSevice.getTierList(programId);
						redrawTierList(tierList);

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
						resetFields();
						List<LoyaltyProgramTier> tierList = ltyPrgmSevice.getTierList(programId);
						redrawTierList(tierList);
					}
					else {
						return;
					}

				}
				
				//set draft status
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

				session.removeAttribute("tierId");
				isEdit=false;

			}
			else if("thresholdDelete".equalsIgnoreCase(evtType)) {
				LoyaltyThresholdBonus loyaltyThresholdBonus=(LoyaltyThresholdBonus)tempRow.getValue();
				Long programId = (Long) session.getAttribute("programId");
				try {

					int confirm = Messagebox.show("Are you sure you want to delete the selected threshold?", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm == Messagebox.OK) {

						try {
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
							if(loyaltyThresholdBonus.getRegistrationFlag() == OCConstants.FLAG_YES) {
								bonusThresholdTypeLbId.setSelectedIndex(0);
								bonusThresholdValueTbId.setValue("");
								bonusThresholdTypeLbId.setDisabled(false);
								bonusThresholdValueTbId.setDisabled(false);
								bonusaddBtnId.setLabel("Add");
							}    
							levelRowsId.removeChild(tempRow);
							isEdit=false;
							session.removeAttribute("thresholdId");
						} catch (Exception e) {
							logger.error("Exception ::", e);
						}
					}
				}
				catch (Exception e) {
					logger.error("Exception ::", e);
				}
				return;

			}
			else if("thresholdEdit".equalsIgnoreCase(evtType)) {
				LoyaltyThresholdBonus loyaltyThresholdBonus=(LoyaltyThresholdBonus)tempRow.getValue();
				String type="edit";
				thresholdBonusWinId.setVisible(true);
				thresholdBonusWinId.setPosition("center");
				thresholdBonusWinId.doHighlighted();
				thresholdSettings(loyaltyThresholdBonus,type);
			}
		}}else if(event.getTarget() instanceof A){
			A link = (A)event.getTarget();
			String evtType = (String)link.getAttribute("type");
			Long templateId = (Long) link.getAttribute("templateId");
			if("previewEmail".equalsIgnoreCase(evtType)){
				
				previewEmailTemplateInActions(templateId,OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_BONUS);

			}else if("previewSms".equalsIgnoreCase(evtType)){
				
				previewSMSTemplateInActions(templateId,OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_BONUS);
			}
			
		}
	}//onEvent()
	
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
		logger.info("---Entered previewSMSTemplate-----");
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
		
		logger.debug("--Exit previewSMSTemplate---");
	}//previewSMSTemplate()

	private void thresholdSettings(LoyaltyThresholdBonus loyaltyThresholdBonus,String type) {
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
				/*if((thresholdBonusWinId$levelValueLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Increment"))
						&&(thresholdBonusWinId$levelTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("LPV"))){
					thresholdBonusWinId$limitDivId.setVisible(true);
					if(loyaltyThresholdBonus.getThresholdLimit()!=null){
						thresholdBonusWinId$thresholdLimitID.setValue(loyaltyThresholdBonus.getThresholdLimit().longValue()+"");	
					}else{
						thresholdBonusWinId$thresholdLimitID.setValue("");
					}
				}*/
				
			}else if(!loyaltyThresholdBonus.isRecurring()){
					thresholdBonusWinId$levelValueLbId.setSelectedIndex(0);
					//thresholdBonusWinId$limitDivId.setVisible(false);
					
				}

			
			/*if(loyaltyThresholdBonus.isRecurring()){
				limitDivId.setVisible(true);
				if(loyaltyThresholdBonus.getThresholdLimit()!=null){
					thresholdBonusWinId$thresholdLimitID.setValue(loyaltyThresholdBonus.getThresholdLimit().intValue()+"");	
					}
				
			}
*/
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
				/*if(loyaltyThresholdBonus.getThresholdLimit()!=null){
				thresholdBonusWinId$thresholdLimitID.setValue(loyaltyThresholdBonus.getThresholdLimit().intValue()+"");	
				}else{
					thresholdBonusWinId$thresholdLimitID.setValue("");
				}*/
			}
			
			
			getLoyaltyTemplateList(thresholdBonusWinId$selBonusAutoEmailsLbId, "earnedBonus");
			getLoyaltySmsTemplateList(thresholdBonusWinId$selBonusAutoSmsLbId,"earningBonus");
			
		    getLoyaltyTemplateList(thresholdBonusWinId$selRewardAutoEmailsLbId,"earnedRewardExpiration");
			getLoyaltySmsTemplateList(thresholdBonusWinId$selRewardAutoSmsLbId,"earnedRewardExpiration");
			
			if(loyaltyThresholdBonus.getEmailTempId()!=null) setBonusValuesInEdit(loyaltyThresholdBonus);
			if(loyaltyThresholdBonus.getSmsTempId()!=null)	setSMSBonusValuesInEdit(loyaltyThresholdBonus);
			if(loyaltyThresholdBonus.getEmailExpiryTempId()!=null) setRewardExpValues(loyaltyThresholdBonus);
			if(loyaltyThresholdBonus.getSmsExpiryTempId()!=null)	setSMSRewardExpValues(loyaltyThresholdBonus);
				
			onSelect$selBonusAutoEmailsLbId$thresholdBonusWinId();
			onSelect$selBonusAutoSmsLbId$thresholdBonusWinId();
			onSelect$selRewardAutoSmsLbId$thresholdBonusWinId();
			onSelect$selRewardAutoEmailsLbId$thresholdBonusWinId();
						
			//onSelect$giftAmtExpTypeLbId$thresholdBonusWinId();
			logger.info("bonus id==="+loyaltyThresholdBonus.getThresholdBonusId()+"=="+loyaltyThresholdBonus.getBonusExpiryDateType()+" and "+loyaltyThresholdBonus.getBonusExpiryDateValue());
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
	}//thresholdSettings()

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
			MessageUtil.setMessage("Bonus on threshold value rule exceeds the maximum characters.", "red");
			return;
		}


		if(thresholdBonusWinId$bonusValueTbId.getValue().trim().length() > 0 || thresholdBonusWinId$levelValueTbId.getValue().trim().length() > 0) {

			if(thresholdBonusWinId$bonusTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)) {

				if(!checkIfLong(thresholdBonusWinId$bonusValueTbId.getValue().trim())) {
					MessageUtil.setMessage("Please provide number value for bonus on threshold.", "red");
					thresholdBonusWinId$bonusValueTbId.setFocus(true);
					return ;
				}
				if( Integer.parseInt(thresholdBonusWinId$bonusValueTbId.getValue()) <= 0 ) {
					MessageUtil.setMessage("Please provide valid value for bonus on threshold.", "red");
					return;
				}
			}else {
				if(!checkIfDouble(thresholdBonusWinId$bonusValueTbId.getValue().trim())) {
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

				if(!checkIfLong(thresholdBonusWinId$levelValueTbId.getValue().trim())) {
					MessageUtil.setMessage("Please provide number value for bonus on threshold.", "red");
					thresholdBonusWinId$levelValueTbId.setFocus(true);
					return ;
				}
				if(Integer.parseInt(thresholdBonusWinId$levelValueTbId.getValue()) <= 0) {
					MessageUtil.setMessage("Please provide valid value for bonus on threshold.", "red");
					return;
				}
			}else if(thresholdBonusWinId$levelTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.THRESHOLD_TYPE_TIER)) {

				if(!checkIfLong(thresholdBonusWinId$levelValueTbId.getValue().trim())) {
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
				if(!checkIfDouble(thresholdBonusWinId$levelValueTbId.getValue().trim())) {
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
		/*if(thresholdBonusWinId$levelTypeLbId.getSelectedItem().getValue().equals("LPV")&&(thresholdBonusWinId$thresholdLimitID.getValue()!=null && !thresholdBonusWinId$thresholdLimitID.getValue().trim().isEmpty())){

			if(!checkIfLong(thresholdBonusWinId$thresholdLimitID.getValue().trim())) {
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
		}*/

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
					logger.info("Threshold id of current bonus"+loyaltyThresholdBonus.getThresholdBonusId());
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
			}else
				isRecurring = false;
			
		}
		

	
		
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();

		Long threshBonusEmailTmpltId = thresholdBonusWinId$selBonusAutoEmailsLbId.getSelectedItem().getValue();
		Long threshBonusSmsTmpltId = thresholdBonusWinId$selBonusAutoSmsLbId.getSelectedItem().getValue();	
		
		Double thresholdLimitIDValue= null;
		/*if(thresholdBonusWinId$thresholdLimitID.getValue()!=null && !thresholdBonusWinId$thresholdLimitID.getValue().trim().isEmpty())
		{
			thresholdLimitIDValue=Double.parseDouble(thresholdBonusWinId$thresholdLimitID.getValue().trim());
		}*/
		
		String bonusExpiryDateType=null;
		Long bonusExpiryDateValue=null;
		Long rewardExpirySmsTmpltId = null;
		Long rewardExpiryEmailTmpltId = null;
		
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
		
		ltyPrgmSevice.saveBonus(programId,extraBonusType,extraBonusValue,earnedLevelType,earnedLevelValue,thresholdId
				,registrationFlag,userId,isRecurring,thresholdLimitIDValue,threshBonusEmailTmpltId,threshBonusSmsTmpltId,
				bonusExpiryDateType,bonusExpiryDateValue,rewardExpiryEmailTmpltId,rewardExpirySmsTmpltId);
		isEdit=false;
		session.removeAttribute("thresholdId");
		MessageUtil.setMessage("Threshold  saved successfully.", "color:blue", "TOP");
		List<LoyaltyThresholdBonus> bonusList = ltyPrgmSevice.getBonusList(programId);
		redrawBonusList(bonusList);
		thresholdBonusWinId$bonusValueTbId.setValue("");
		thresholdBonusWinId$levelValueTbId.setValue("");
		//thresholdBonusWinId$thresholdLimitID.setValue("");
		thresholdBonusWinId$bonusTypeLbId.setSelectedIndex(0);
		thresholdBonusWinId$levelTypeLbId.setSelectedIndex(0);
		if(thresholdBonusWinId$earnedRwrdExpiryCbId.isChecked()){
			thresholdBonusWinId$selRewardAutoSmsLbId.setSelectedIndex(0);
			thresholdBonusWinId$selRewardAutoEmailsLbId.setSelectedIndex(0);
			thresholdBonusWinId$giftAmtExpTypeLbId.setSelectedIndex(0);
			thresholdBonusWinId$giftAmtExpValueTbId.setValue("");
			
		}

		thresholdBonusWinId.setVisible(false);
		
		
	}//onClick$addBtnId$thresholdBonusWinId()
	
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


	public void onCheck$enableDateChkId() {
		Radio selRadio = onPurchaseRadioGrId.getSelectedItem()!=null?onPurchaseRadioGrId.getSelectedItem():null;
		
		if(enableDateChkId.isChecked()) {
			activateAfterDivId.setVisible(true);
			onPurchaseRadioLblId.setStyle("visibility:visible;");
			onAllStore.setStyle("visibility:visible;");
			selectedStores.setStyle("visibility:visible;");
			if (onAllStore.isChecked()) {
			storeNumbCmboBxId.setVisible(false);
			addStoreImgId.setVisible(false);
			selectedStoreLbId.setVisible(false);
		
		} 
			else if(selectedStores.isChecked()) {
			storeNumbCmboBxId.setVisible(true);
			addStoreImgId.setVisible(true);
			selectedStoreLbId.setVisible(true);
		}
		}
		else {
			activateAfterDivId.setVisible(false);
			onPurchaseRadioLblId.setStyle("visibility:hidden;");
			onAllStore.setStyle("visibility:hidden;");
			selectedStores.setStyle("visibility:hidden;");
			storeNumbCmboBxId.setVisible(false);
			addStoreImgId.setVisible(false);
			selectedStoreLbId.setVisible(false);
		

		}
	}

	//onCheck$enableDateChkId()
public void onCheck$enableChkIdForIssuance() {
		if(enableChkIdForIssuance.isChecked()) {
			valueDivId.setVisible(true);
			issuanceAmountDivId.setVisible(true);
		}else {
			valueDivId.setVisible(false);
			issuanceAmountDivId.setVisible(false);
		}
	}//onCheck$enableChkIdForIssuance()

	public void onCheck$selectedStores(){
	//	enableDateChkId.isChecked();
		onCheck$enableDateChkId();
		onAllStore.setStyle("visibility:visible;");
		selectedStores.setStyle("visibility:visible;");
		storeNumbCmboBxId.setVisible(true);
		addStoreImgId.setVisible(true);
		selectedStoreLbId.setVisible(true);
		
	}
	public void onCheck$onAllStore(){

		onCheck$enableDateChkId();
		
	}
	long count=0;
	private void tierSettings(LoyaltyProgramTier loyaltyProgramTier,String type) {
		DecimalFormat f = new DecimalFormat("#0.00");
		Components.removeAllChildren(selTierLbId);
		Long prgmId=(Long) session.getAttribute("programId");
		logger.info("prgmId====>"+prgmId);
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
		List<LoyaltyProgramTier> tiersList = ltyPrgmSevice.getTierList(prgmId);
		Listitem tempItem=null;
		tempItem= new Listitem("Select Tier");
		tempItem.setValue("Select Tier");
		tempItem.setParent(selTierLbId);
		selTierLbId.setSelectedItem(tempItem);
		if(prgmObj!=null) {
			if(prgmObj.getTierEnableFlag() == OCConstants.FLAG_YES ) {


				int noOfTiers=prgmObj.getNoOfTiers();
				logger.info("noOfTiers"+noOfTiers);
				for(int i=1;i<=noOfTiers;i++) {
					String tier="Tier "+i;
					boolean isExists=false;
					if(tiersList != null) {
						for(LoyaltyProgramTier ltyprogrmtier:tiersList) {
							if(ltyprogrmtier.getTierType().equalsIgnoreCase(tier) && !loyaltyProgramTier.getTierType().equalsIgnoreCase(tier)) {
								isExists=true;
							}
						}
					}
					if(!isExists) {
						tempItem=new Listitem(tier);
						tempItem.setValue(tier);
						tempItem.setParent(selTierLbId);
					}
				}
			}else {
				Listitem li = null;
				li=new Listitem("Tier 1");
				li.setValue("Tier 1");
				li.setParent(selTierLbId);
				selTierLbId.setSelectedItem(li);
				selTierLbId.setDisabled(true);
//				ruleTypeLbId.setDisabled(true);
//				crossAtValueTbId.setValue("");
//				crossAtValueTbId.setDisabled(true);
			}
		}
		isEdit=true;
		session.setAttribute("tierId", loyaltyProgramTier.getTierId());
		if(loyaltyProgramTier !=null && type!=null ){

			List<Listitem> tierList=selTierLbId.getItems();

			for(Listitem li : tierList) {
				if(loyaltyProgramTier.getTierType().equalsIgnoreCase(li.getValue().toString())) {
					selTierLbId.setSelectedItem(li);
					break;
				}
			}

		/*	if (!selTierLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Select Tier")) {
				int tierVal = Integer.parseInt(selTierLbId.getSelectedItem()
						.getValue().toString().replace("Tier ", ""));
				if (tierVal == prgmObj.getNoOfTiers()) {
					ruleTypeLbId.setDisabled(true);
					crossAtValueTbId.setValue("");
					crossAtValueTbId.setDisabled(true);
					//cumValueLbId.setDisabled(true);
					//cumTypeLbId.setDisabled(true);
				} else {
					ruleTypeLbId.setDisabled(false);
					crossAtValueTbId.setDisabled(false);
					//cumValueLbId.setDisabled(false);
					//cumTypeLbId.setDisabled(false);
				}
			}*/
			privilegeNameTbId.setValue(loyaltyProgramTier.getTierName());
			List<Listitem> earnTypeList=earnTypeLbId.getItems();
			for(Listitem li : earnTypeList) {
				if(loyaltyProgramTier.getEarnType().equalsIgnoreCase(li.getValue().toString())) {
					earnTypeLbId.setSelectedItem(li);
					if(tiersList.size() > 1) {
						earnTypeLbId.setDisabled(true);
					}else {
						earnTypeLbId.setDisabled(false);
					}
					break;
				}
			}

			if(earnTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)){
				coversionfromTbId.setDisabled(false);
				conversionToTbId.setDisabled(false);
				autoConvertRadioId.setDisabled(false);
				onDemandRadioId.setDisabled(false);
				if(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype())){
					autoConvertRadioId.setSelected(true);
					autoConvertRadioId.setDisabled(false);
					onDemandRadioId.setDisabled(false);
					pointsConversionDivId.setVisible(true);
				}
			}else {
				coversionfromTbId.setDisabled(true);
				conversionToTbId.setDisabled(true);
				autoConvertRadioId.setDisabled(true);
				onDemandRadioId.setDisabled(true);
			}
			
			if(loyaltyProgramTier.getIssuanceChkEnable()!=null && loyaltyProgramTier.getIssuanceChkEnable()) {
				enableChkIdForIssuance.setChecked(true);
				issuanceAmountDivId.setVisible(true);
				valueDivId.setVisible(true);
				issuanceValueTbId.setValue(loyaltyProgramTier.getMaxcap()==null?"":f.format(loyaltyProgramTier.getMaxcap().intValue()));
			} else {
				enableChkIdForIssuance.setChecked(false);
				issuanceAmountDivId.setVisible(false);
				valueDivId.setVisible(false);
				issuanceValueTbId.setValue("");
			}

			List<Listitem> earnValueTypeList=earnValueTypeLbId.getItems();
			for(Listitem li : earnValueTypeList) {
				if(loyaltyProgramTier.getEarnValueType().equalsIgnoreCase(li.getValue().toString())) {
					earnValueTypeLbId.setSelectedItem(li);
					break;
				}
			}
			if(earnValueTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_VALUE)) {
				amountDivId.setVisible(true);
				percentDivId.setVisible(false);
				amountSpentTbId.setValue(loyaltyProgramTier.getEarnOnSpentAmount()==null?"":f.format(loyaltyProgramTier.getEarnOnSpentAmount()));
			}
			else {
				amountDivId.setVisible(false);
				percentDivId.setVisible(true);
			}

			/*if(earnTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS) || 
					(earnTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT) &&
					earnValueTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_PERCENTAGE))) {*/
			if(earnTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)) {
				valueTbId.setValue(loyaltyProgramTier.getEarnValue()==null?"":loyaltyProgramTier.getEarnValue().intValue()+"");
			}else {
				valueTbId.setValue(loyaltyProgramTier.getEarnValue()==null?"":f.format(loyaltyProgramTier.getEarnValue()));
			}

			if(earnTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)){
				coversionfromTbId.setValue(loyaltyProgramTier.getConvertFromPoints()==null?"":loyaltyProgramTier.getConvertFromPoints().intValue()+"");
			}else {
				coversionfromTbId.setValue(loyaltyProgramTier.getConvertFromPoints()==null?"":f.format(loyaltyProgramTier.getConvertFromPoints()));
			}
			conversionToTbId.setValue(loyaltyProgramTier.getConvertToAmount()==null?"":f.format(loyaltyProgramTier.getConvertToAmount()));

			if(coversionfromTbId.getValue()!=null && !coversionfromTbId.getValue().isEmpty()) {

				if(loyaltyProgramTier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO)) {
					autoConvertRadioId.setChecked(true);
				}else {
					onDemandRadioId.setChecked(true);
				}
			}else {
				autoConvertRadioId.setChecked(true);
			}

			enableDateChkId.setChecked(loyaltyProgramTier.getActivationFlag() == OCConstants.FLAG_YES ? true : false);
			if(enableDateChkId.isChecked()) {
				activateAfterDivId.setVisible(true);
				List<Listitem> dateValueList=ptsActiveValueLbId.getItems();
				for(Listitem li : dateValueList) {
					if(loyaltyProgramTier.getPtsActiveDateValue().toString().equalsIgnoreCase(li.getValue().toString())) {
						li.setSelected(true);
						break;
					}
				}
			}else {
				activateAfterDivId.setVisible(false);
				ptsActiveValueLbId.setSelectedIndex(0);
			}
			logger.debug(loyaltyProgramTier.getActivateAfterDisableAllStore());
			if(loyaltyProgramTier.getActivateAfterDisableAllStore()!=null && loyaltyProgramTier.getActivateAfterDisableAllStore())  {
				logger.debug("enter");
				if(loyaltyProgramTier.getDisallowActivateAfterStores().isEmpty()) {
				onPurchaseRadioLblId.setStyle("visibility:visible;");
				onAllStore.setStyle("visibility:visible;");
				selectedStores.setStyle("visibility:visible;");
				onAllStore.setChecked(true);
				selectedStores.setChecked(false);
				storeNumbCmboBxId.setVisible(false);
				addStoreImgId.setVisible(false);
				selectedStoreLbId.setVisible(false);
			}
			else {
				logger.debug("enter");
				onPurchaseRadioLblId.setStyle("visibility:visible;");
				onAllStore.setStyle("visibility:visible;");
				selectedStores.setStyle("visibility:visible;");
				onAllStore.setChecked(false);
				selectedStores.setChecked(true);
				storeNumbCmboBxId.setVisible(true);
				addStoreImgId.setVisible(true);
				selectedStoreLbId.setVisible(true);
				String storeStr=loyaltyProgramTier.getDisallowActivateAfterStores();
				if(storeStr != null && storeStr.length() != 0) {
					Components.removeAllChildren(selectedStoreLbId);	
					String[] storeStrArr = storeStr.split(Constants.ADDR_COL_DELIMETER);
					for (String store : storeStrArr) {
						Listitem li=new Listitem();
						Listcell lc=new Listcell(store);
						lc.setParent(li);
						lc=new Listcell();
						Image delImg = new Image("/img/action_delete.gif");
						delImg.setStyle("cursor:pointer;margin-right:10px;");
						delImg.setTooltiptext("Delete Store");
						delImg.setAttribute("EXCLUSION_TYPE", "DELETE_STORE");
						delImg.addEventListener("onClick", this);
						delImg.setParent(lc);
						lc.setParent(li);
						li.setParent(selectedStoreLbId);
					}
				}
			}
			}
			else {
				if(loyaltyProgramTier.getPtsActiveDateValue()==null)
				{
					onPurchaseRadioLblId.setStyle("visibility:hidden;");
					onAllStore.setStyle("visibility:hidden;");
					selectedStores.setStyle("visibility:hidden;");
					storeNumbCmboBxId.setVisible(false);
					addStoreImgId.setVisible(false);
					selectedStoreLbId.setVisible(false);	
				}
				else {
				onPurchaseRadioLblId.setStyle("visibility:visible;");
				onAllStore.setStyle("visibility:visible;");
				selectedStores.setStyle("visibility:visible;");
				if(loyaltyProgramTier.getDisallowActivateAfterStores()!=null && loyaltyProgramTier.getDisallowActivateAfterStores().isEmpty()) {

				onAllStore.setChecked(true);
				selectedStores.setChecked(false);
				storeNumbCmboBxId.setVisible(false);
				addStoreImgId.setVisible(false);
				selectedStoreLbId.setVisible(false);
				}
				
				else {
					
					onAllStore.setChecked(false);
					selectedStores.setChecked(true);
					storeNumbCmboBxId.setVisible(true);
					addStoreImgId.setVisible(true);
					selectedStoreLbId.setVisible(true);
					
					String storeStr=loyaltyProgramTier.getDisallowActivateAfterStores();
	
			
		
					if(storeStr != null && storeStr.length() != 0) {
					Components.removeAllChildren(selectedStoreLbId);	
						String[] storeStrArr = storeStr.split(Constants.ADDR_COL_DELIMETER);
						for (String store : storeStrArr) {
							Listitem li=new Listitem();
							Listcell lc=new Listcell(store);
							lc.setParent(li);
							lc=new Listcell();
							Image delImg = new Image("/img/action_delete.gif");
							delImg.setStyle("cursor:pointer;margin-right:10px;");
							delImg.setTooltiptext("Delete Store");
							delImg.setAttribute("EXCLUSION_TYPE", "DELETE_STORE");
							delImg.addEventListener("onClick", this);
							delImg.setParent(lc);
							lc.setParent(li);
							li.setParent(selectedStoreLbId);
					
						}
					}
				}
			} 
			}	
	
			
			

			/*if(loyaltyProgramTier.getTierUpgdConstraintValue() != null) {
				List<Listitem> upgradeList = ruleTypeLbId.getItems();
				for(Listitem li:upgradeList) {
					if(loyaltyProgramTier.getTierUpgdConstraint().equalsIgnoreCase(li.getValue().toString())) {
						ruleTypeLbId.setSelectedItem(li);
						if(tiersList.size() > 1){
							ruleTypeLbId.setDisabled(true);
						}else {
							ruleTypeLbId.setDisabled(false);
						}
					}
				}
			}
			else{

				boolean isExists = false;
				LoyaltyProgramTier tierObj = null;
				for(LoyaltyProgramTier loyaltyTier :tiersList) {
					if(loyaltyTier.getTierUpgdConstraintValue() != null){
						isExists = true;
						tierObj = loyaltyTier;
						break;
					}
				}*/
				/*if(isExists) {
					List<Listitem> upgradeList = ruleTypeLbId.getItems();
					for(Listitem li:upgradeList) {
						if(tierObj.getTierUpgdConstraint().equalsIgnoreCase(li.getValue().toString())) {
							ruleTypeLbId.setSelectedItem(li);
							if(tiersList.size() > 1){
								ruleTypeLbId.setDisabled(true);
							}else {
								ruleTypeLbId.setDisabled(false);
							}
						}
					}
				}
				else{
					ruleTypeLbId.setSelectedIndex(0);
					ruleTypeLbId.setDisabled(false);
				}*/
			}

			/*if(ruleTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE)) {
				cummulativeDivId.setVisible(true);
				upgradeCumDivId.setVisible(true);
				upgradeCumDivId.setStyle("margin-top:10px;margin-right:10px;margin-left:5px;");
				upgradeDivId.setVisible(false);
				//cumTypeLbId.setSelectedIndex(0);
				List<Listitem> values = cumValueLbId.getItems();
				for(Listitem li :values ) {
					if(loyaltyProgramTier.getTierUpgradeCumulativeValue() != null && li.getValue().toString().equalsIgnoreCase(loyaltyProgramTier.getTierUpgradeCumulativeValue().toString())) {
						cumValueLbId.setSelectedItem(li);
					}
				}

			}
			else {
				cummulativeDivId.setVisible(false);
				upgradeCumDivId.setVisible(false);
				upgradeDivId.setVisible(true);
				upgradeDivId.setStyle("margin-top:10px;margin-right:10px;margin-left:5px;");
				//cumTypeLbId.setSelectedIndex(0);
			}*/
			editTierRules(loyaltyProgramTier, prgmObj);	// Edit Tier Update Rules		
			
			enablePartialReturnsAuthChkId.setChecked(loyaltyProgramTier.getPartialReversalFlag() == OCConstants.FLAG_YES ? true : false);
			enableOTPAuthChkId.setChecked(loyaltyProgramTier.getRedemptionOTPFlag() == OCConstants.FLAG_YES ? true : false);
			if(enableOTPAuthChkId.isChecked()){
				otpEnabledDivId.setVisible(true);
				otpAmtLimitTbId.setValue(loyaltyProgramTier.getOtpLimitAmt() == null ? "" : f.format(loyaltyProgramTier.getOtpLimitAmt()));
			}
			else{
				otpEnabledDivId.setVisible(false);
				otpAmtLimitTbId.setValue("");
			}
			enableRedeemedAmountChkId.setChecked(loyaltyProgramTier.getConsiderRedeemedAmountFlag() == OCConstants.FLAG_YES ? true : false);
			redemptionPercentageTbId.setValue(loyaltyProgramTier.getRedemptionPercentageLimit() != null && loyaltyProgramTier.getRedemptionPercentageLimit() == 0.0 ? "" : f.format(loyaltyProgramTier.getRedemptionPercentageLimit())+"");
			redemptionValueTbId.setValue(loyaltyProgramTier.getRedemptionValueLimit() != null && loyaltyProgramTier.getRedemptionValueLimit() == 0.0 ? "" : f.format(loyaltyProgramTier.getRedemptionValueLimit())+"");
			
			minReceiptAmtTbId.setValue(loyaltyProgramTier.getMinReceiptValue()!=null?loyaltyProgramTier.getMinReceiptValue().toString():"");
			minBalanceTbId.setValue(loyaltyProgramTier.getMinBalanceValue()!=null?loyaltyProgramTier.getMinBalanceValue().toString():"");
			
			expAftrTbId.setValue(loyaltyProgramTier.getRewardExpiryDateValue()!=null?loyaltyProgramTier.getRewardExpiryDateValue().toString():"");
			crossOverBounsTbId.setValue(loyaltyProgramTier.getCrossOverBonus()!=null?loyaltyProgramTier.getCrossOverBonus().toString():"");

			/*if(ruleTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_LIFETIME_POINTS)) {
				crossAtValueTbId.setValue(loyaltyProgramTier.getTierUpgdConstraintValue()==null?"":loyaltyProgramTier.getTierUpgdConstraintValue().intValue()+"");
			}else {
				crossAtValueTbId.setValue(loyaltyProgramTier.getTierUpgdConstraintValue()==null?"":f.format(loyaltyProgramTier.getTierUpgdConstraintValue()));
			}*/
					if(loyaltyProgramTier.getRoundingType()!=null && loyaltyProgramTier.getRoundingType().toString().equalsIgnoreCase("Down")){
							roundLbId.setSelectedIndex(0);
						}else if(loyaltyProgramTier.getRoundingType()!=null && loyaltyProgramTier.getRoundingType().toString().equalsIgnoreCase("Up")){
							roundLbId.setSelectedIndex(1);
						}else if(loyaltyProgramTier.getRoundingType()!=null && loyaltyProgramTier.getRoundingType().toString().equalsIgnoreCase("Near")){
							roundLbId.setSelectedIndex(2);
						}
		}

	//}//tierSettings()

	    
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
	
	public void onClick$addStoreImgId(){

		if(storeNumbCmboBxId.getValue() == null || ((String) storeNumbCmboBxId.getValue()).trim().length() == 0) {
			MessageUtil.setMessage("Please provide store number.", "red", "TOP");
			return;
		}
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		Long orgId=ltyPrgmSevice.getOrgId(userId) ;

		OrganizationStores orgStores = ltyPrgmSevice.getStore(orgId,storeNumbCmboBxId.getValue());



		Listitem tempItem = null;
		Listcell tempCell = null;

		List<Listitem> listItem= selectedStoreLbId.getItems();
		boolean existedStore = false;
		if(selectedStoreLbId.getItemCount() > 0) {
			for (Listitem eachItem : listItem) {
				if(eachItem.getLabel().equals(((String) storeNumbCmboBxId.getValue()).trim())) {
					existedStore = true;
					break;
				}

			}
		}

		if(!existedStore){
			if(orgStores == null) {
				if(Messagebox.show("This store does not exist. Do you want to continue? ", "Confirm" ,Messagebox.OK | Messagebox.CANCEL, 
						Messagebox.QUESTION) != Messagebox.OK) {
					return;
				}
			}
			tempItem = new Listitem();
			tempCell = new Listcell(((String) storeNumbCmboBxId.getValue()).trim());
			tempCell.setParent(tempItem);


			tempCell = new Listcell();
			Image delImg = new Image("/img/action_delete.gif");
			delImg.setStyle("cursor:pointer;margin-right:10px;");
			delImg.setTooltiptext("Delete Store");
			delImg.setAttribute("EXCLUSION_TYPE", "DELETE_STORE");
			delImg.addEventListener("onClick", this);

			delImg.setParent(tempCell);
			tempCell.setParent(tempItem);
			tempItem.setParent(selectedStoreLbId);
		}
		storeNumbCmboBxId.setValue(""); 


	}//onClick$addStoreImgId()
	private void setDefaultStores() {
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		Long orgId=ltyPrgmSevice.getOrgId(userId) ;
		logger.info("orgId"+orgId);
		List<OrganizationStores> storeIdList = ltyPrgmSevice.getAllStores(orgId);
		logger.info("storeIdList"+storeIdList);
		if(storeIdList == null || storeIdList.size() == 0) return;
		Comboitem comboItem = null;
		for (OrganizationStores eachStore : storeIdList){
			comboItem = new Comboitem(eachStore.getHomeStoreId());
			comboItem.setParent(storeNumbCmboBxId);
			//logger.info("comboItem"+storeNumbCmboBxId.getChildren());
		}

	}//setDefaultStores()


	private void addTierRule() {
		Row tempRow = new Row();
		String tierSelection = (String) mulTierRuleRowsId.getAttribute("ListBoxSelection");
		if (replaceRow == true) {
			Row temp = (Row) mulTierRuleRowsId.getAttribute("ROW");
			mulTierRuleRowsId.insertBefore(tempRow, temp);
			mulTierRuleRowsId.removeChild(temp);
		}
		String savedTierValue = "";
		String savedTierMonth = "";
		String savedTierValue2 = "";
		String savedTierMonth2 = "";
		Boolean tierEdit = (Boolean) mulTierRuleRowsId.getAttribute("TierEdit");
		if (tierEdit != null && tierEdit == true) {
			tierSelection = (String) mulTierRuleRowsId.getAttribute("TierSelected");
			savedTierValue = (String) mulTierRuleRowsId.getAttribute("TierValue");
			savedTierMonth = (String) mulTierRuleRowsId.getAttribute("TierMonth");
			
			if (tierSelection.equalsIgnoreCase("CumulativePoints")) {
				savedTierValue2 = (String) mulTierRuleRowsId.getAttribute("TierValue2");
				savedTierMonth2 = (String) mulTierRuleRowsId.getAttribute("TierMonth2");
			}
		}

		Listbox tempListbox = typesOfTierUpdateRules();
		tempListbox.addEventListener("onSelect", this);
		tempListbox.setWidth("200px");
		tempListbox.setParent(tempRow);
		if(tierSelection == null || tierSelection.equals("")) tierSelection = OCConstants.LOYALTY_LIFETIME_POINTS;
		Integer tierSelIndex = ((tierSelection.equals(OCConstants.LOYALTY_LIFETIME_POINTS)) ? 0
				: (tierSelection.equals(OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE)) ? 1
						: (tierSelection.equals(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE)) ? 2
								: (tierSelection.equals(OCConstants.LOYALTY_SINGLE_PURCHASE_VALUE)) ? 3
										: (tierSelection.equals(OCConstants.LOYALTY_CUMULATIVE_POINTS)) ? 4
												: (tierSelection.equals(OCConstants.LOYALTY_CUMULATIVE_VISITS)) ? 5 : 0);
		tempListbox.setSelectedIndex(tierSelIndex);
		if (selectedTierIsValid == false)
			tempListbox.setDisabled(true);
		else
			tempListbox.setDisabled(false);

		Div div1 = new Div();
		div1.setParent(tempRow);

		Div div2 = new Div();
		div2.setParent(tempRow);

		final Label label1 = new Label();

		Textbox tempTextBox1 = new Textbox();
		tempTextBox1.setWidth("60px");

		if (tierSelection.equalsIgnoreCase(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE)
				|| tierSelection.equalsIgnoreCase(OCConstants.LOYALTY_CUMULATIVE_VISITS)) {
			label1.setValue(tierSelection.equalsIgnoreCase(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE)
					? "Cross-over to next tier on cumulative purchase of "
					: "Cross-over to next tier on cumulative visits of ");
			label1.setStyle("color:black;");
			label1.setParent(div1);

			tempTextBox1.setParent(div1);
			if (tierEdit != null && tierEdit == true) {
				if (savedTierValue == null || savedTierValue.equalsIgnoreCase("null")) savedTierValue = "";
				tempTextBox1.setValue(savedTierValue);
			}
			if (selectedTierIsValid == false) {
				tempTextBox1.setValue("");
				tempTextBox1.setDisabled(true);
			} else {
				tempTextBox1.setDisabled(false);
			}

			final Label label2 = new Label();
			label2.setValue(tierSelection.equalsIgnoreCase(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE) ? "currency within ": "within ");
			label2.setStyle("color:black;");
			label2.setParent(div2);

			Textbox tempTextBox2 = new Textbox();
			tempTextBox2.setWidth("60px");
			tempTextBox2.setParent(div2);
			if (tierEdit != null && tierEdit == true)
				tempTextBox2.setValue(savedTierMonth);
			if (selectedTierIsValid == false) {
				tempTextBox2.setValue("");
				tempTextBox2.setDisabled(true);
			} else {
				tempTextBox2.setDisabled(false);
			}

			final Label label3 = new Label();
			label3.setValue(" month(s)");
			label3.setStyle("color:black;");
			label3.setParent(div2);

		}  else if (tierSelection.equalsIgnoreCase(OCConstants.LOYALTY_CUMULATIVE_POINTS)) {
			// CUMULATIVE POINTS
			label1.setValue("Cross-over to next tier on cumulative points of ");
			label1.setStyle("color:black;");
			label1.setParent(div1);

			tempTextBox1.setParent(div1);
			if (tierEdit != null && tierEdit == true) {
				if (savedTierValue == null || savedTierValue.equalsIgnoreCase("null")) savedTierValue = "";
				tempTextBox1.setValue(savedTierValue);
			}
			if (selectedTierIsValid == false) {
				tempTextBox1.setValue("");
				tempTextBox1.setDisabled(true);
			} else {
				tempTextBox1.setDisabled(false);
			}

			final Label label2 = new Label();
			label2.setValue(" within ");
			label2.setStyle("color:black;");
			label2.setParent(div1);

			Textbox tempTextBox2 = new Textbox();
			tempTextBox2.setWidth("60px");
			tempTextBox2.setParent(div1);
			if (tierEdit != null && tierEdit == true)
				tempTextBox2.setValue(savedTierMonth);
			if (selectedTierIsValid == false) {
				tempTextBox2.setValue("");
				tempTextBox2.setDisabled(true);
			} else {
				tempTextBox2.setDisabled(false);
			}

			final Label label3 = new Label();
			label3.setValue(" month(s)");
			label3.setStyle("color:black;");
			label3.setParent(div1);
			
			// CUMULATIVE VISITS
			final Label label4 = new Label();
			label4.setValue("Cross-over to next tier on cumulative visits of ");
			label4.setStyle("color:black;");
			label4.setParent(div2);

			Textbox tempTextBox3 = new Textbox();
			tempTextBox3.setWidth("60px");
			tempTextBox3.setParent(div2);
			if (tierEdit != null && tierEdit == true) {
				if (savedTierValue2 == null || savedTierValue2.equalsIgnoreCase("null")) savedTierValue2 = "";
				tempTextBox3.setValue(savedTierValue2);
			}
			if (selectedTierIsValid == false) {
				tempTextBox3.setValue("");
				tempTextBox3.setDisabled(true);
			} else {
				tempTextBox3.setDisabled(false);
			}

			final Label label5 = new Label();
			label5.setValue(" within ");
			label5.setStyle("color:black;");
			label5.setParent(div2);

			Textbox tempTextBox4 = new Textbox();
			tempTextBox4.setWidth("60px");
			tempTextBox4.setParent(div2);
			if (tierEdit != null && tierEdit == true)
				tempTextBox4.setValue(savedTierMonth2);
			if (selectedTierIsValid == false) {
				tempTextBox4.setValue("");
				tempTextBox4.setDisabled(true);
			} else {
				tempTextBox4.setDisabled(false);
			}

			final Label label6 = new Label();
			label6.setValue(" month(s)");
			label6.setStyle("color:black;");
			label6.setParent(div2);

		} else {
			label1.setValue("Cross-over to next tier at ");
			label1.setStyle("color:black;");
			label1.setParent(div1);

			tempTextBox1.setParent(div2);
			if (tierEdit != null && tierEdit == true) {
				if (savedTierValue == null || savedTierValue.equalsIgnoreCase("null")) savedTierValue = "";
				if(tierSelection.equalsIgnoreCase(OCConstants.LOYALTY_LIFETIME_POINTS) && savedTierValue.contains(".")) {
					savedTierValue = savedTierValue.split("\\.")[0];
				} 
				tempTextBox1.setValue(savedTierValue);
			}
			if (selectedTierIsValid == false) {
				tempTextBox1.setValue("");
				tempTextBox1.setDisabled(true);
			} else {
				tempTextBox1.setDisabled(false);
			}
		}

		Div div3 = new Div();
		div3.setParent(tempRow);

		Image imgBtn = new Image();

		if (tierUpdateFirstRow == true) {
			imgBtn.setAttribute("TYPE", "ADD_TIER_RULE");
			imgBtn.setTooltiptext("Add Tier Rule");
			imgBtn.setSrc("/images/action_add.gif");
			imgBtn.setStyle("cursor:pointer;text-align:center;");
			if (selectedTierIsValid == true)
				imgBtn.addEventListener("onClick", this);
			imgBtn.setParent(div3);
		} else {
			imgBtn.setAttribute("TYPE", "DELETE_TIER_RULE");
			imgBtn.setTooltiptext("Delete Tier Rule");
			imgBtn.setSrc("/images/action_delete.gif");
			imgBtn.setStyle("cursor:pointer;text-align:center;");
			imgBtn.addEventListener("onClick", this);
			imgBtn.setParent(div3);
		}

		tempRow.setParent(mulTierRuleRowsId);
		tierUpdateFirstRow = false;
		replaceRow = false;
	} // addTierRule()

	private void editTierRules(LoyaltyProgramTier loyaltyProgramTier, LoyaltyProgram prgmObj) {
		tierUpdateFirstRow = true;
		List rowList = mulTierRuleRowsId.getChildren();
		for (int i = rowList.size() - 1; i >= 0; i--) {
			Row row = (Row) rowList.get(i);
			mulTierRuleRowsId.removeChild(row);
		}

		int tierVal = Integer.parseInt(selTierLbId.getSelectedItem().getValue().toString().replace("Tier ", ""));
		if (tierVal == prgmObj.getNoOfTiers()) {
			tierUpdateFirstRow = true;
			mulTierRuleRowsId.setAttribute("ListBoxSelection", OCConstants.LOYALTY_LIFETIME_POINTS);
			selectedTierIsValid = false;
			addTierRule();
		} else {

			String tierName = "";
			String value = "";
			String month = "";
			String value2 = "";
			String month2 = "";

			String multipleTierRules = loyaltyProgramTier.getMultipleTierUpgrdRules();
			if (multipleTierRules != null && !multipleTierRules.isEmpty()) {
				String[] arrMulTierRules = multipleTierRules.split("\\|\\|");
				tierUpgradeDropDownCount = arrMulTierRules.length + 1; //taking edited tier-rules count
				for (String tierRule : arrMulTierRules) {
					int firstColon = tierRule.indexOf(':');
					tierName = tierRule.substring(0, firstColon);
					if (tierName.equals(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE)
							|| tierName.equals(OCConstants.LOYALTY_CUMULATIVE_VISITS)) {

						int lastColon = tierRule.lastIndexOf(':');
						value = tierRule.substring(firstColon + 1, lastColon);
						month = tierRule.substring(lastColon + 1);
					} else if (tierName.equals(OCConstants.LOYALTY_CUMULATIVE_POINTS)) {
						if (tierRule.contains(";=;CumulativeVisits")) {
							String[] tierRulesArray = tierRule.split(";=;");
							int lastColon1 = tierRulesArray[0].lastIndexOf(':');
							value = tierRulesArray[0].substring(firstColon + 1, lastColon1);
							month = tierRulesArray[0].substring(lastColon1 + 1);
							
							int lastColon2 = tierRulesArray[1].lastIndexOf(':');
							value2 = tierRulesArray[1].substring(firstColon + 1, lastColon2);
							month2 = tierRulesArray[1].substring(lastColon2 + 1);

						} else {
							int lastColon = tierRule.lastIndexOf(':');
							value = tierRule.substring(firstColon + 1, lastColon);
							month = tierRule.substring(lastColon + 1);
						}
					} else {
						value = tierRule.substring(firstColon + 1);
					}
					String tierSelection = ((tierName.equals(OCConstants.LOYALTY_LIFETIME_POINTS)) ? OCConstants.LOYALTY_LIFETIME_POINTS
							: (tierName.equals(OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE)) ? OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE
									: (tierName.equals(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE)) ? OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE
											: (tierName.equals(OCConstants.LOYALTY_SINGLE_PURCHASE_VALUE)) ? OCConstants.LOYALTY_SINGLE_PURCHASE_VALUE
													: (tierName.equals(OCConstants.LOYALTY_CUMULATIVE_POINTS)) ? OCConstants.LOYALTY_CUMULATIVE_POINTS
															: (tierName.equals(OCConstants.LOYALTY_CUMULATIVE_VISITS)) ? OCConstants.LOYALTY_CUMULATIVE_VISITS : null);
					mulTierRuleRowsId.setAttribute("TierSelected", tierSelection);
					mulTierRuleRowsId.setAttribute("TierValue", value);
					mulTierRuleRowsId.setAttribute("TierMonth", month);
					mulTierRuleRowsId.setAttribute("TierEdit", true);
					selectedTierIsValid = true;

					if(tierSelection.equalsIgnoreCase("CumulativePoints")) {
						mulTierRuleRowsId.setAttribute("TierValue2", value2);
						mulTierRuleRowsId.setAttribute("TierMonth2", month2);
					}
					addTierRule();
				}
			} else {
				tierName = loyaltyProgramTier.getTierUpgdConstraint();
				value = String.valueOf(loyaltyProgramTier.getTierUpgdConstraintValue());
				month = String.valueOf(loyaltyProgramTier.getTierUpgradeCumulativeValue());

				if (tierName != null) {
					String tierSelection = ((tierName.equals(OCConstants.LOYALTY_LIFETIME_POINTS)) ? OCConstants.LOYALTY_LIFETIME_POINTS
							: (tierName.equals(OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE)) ? OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE
									: (tierName.equals(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE)) ? OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE
											: (tierName.equals(OCConstants.LOYALTY_SINGLE_PURCHASE_VALUE)) ? OCConstants.LOYALTY_SINGLE_PURCHASE_VALUE : null);
					mulTierRuleRowsId.setAttribute("TierSelected", tierSelection);
					mulTierRuleRowsId.setAttribute("TierValue", value);
					mulTierRuleRowsId.setAttribute("TierMonth", month);
					mulTierRuleRowsId.setAttribute("TierEdit", true);
					selectedTierIsValid = true;
				} else {
					mulTierRuleRowsId.setAttribute("TierSelected", 0);
					mulTierRuleRowsId.setAttribute("TierValue", "");
					mulTierRuleRowsId.setAttribute("TierMonth", "");
				}

				addTierRule();
			}
		}
	}// editTierRules()

	private String getMultipleTierRules(LoyaltyProgram prgmObj) {
		String query = "";
		if (!selTierLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Select Tier")) {

			int tierVal = Integer.parseInt(selTierLbId.getSelectedItem().getValue().toString().replace("Tier ", ""));
			if (tierVal == prgmObj.getNoOfTiers()) {
				return query;
			}else {
				List rowList = mulTierRuleRowsId.getChildren();
				boolean mulTierRules = false;

				for (Object object : rowList) {
					Row row = (Row) object;
					List rowFields = row.getChildren();
					Listbox listbox = (Listbox) rowFields.get(0);
					Listitem listitem = listbox.getSelectedItem();
					String tierType = (String) listitem.getValue();
					if (mulTierRules == true) {
						query = query.concat("||" + tierType + ":");
					} else {
						query = query.concat(tierType + ":");
						mulTierRules = true;
					}

					Div div1 = (Div) rowFields.get(1);
					Div div2 = (Div) rowFields.get(2);

					if (tierType.equals(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE)
							|| tierType.equals(OCConstants.LOYALTY_CUMULATIVE_VISITS)) {
						List list1 = div1.getChildren();
						Textbox textbox1 = (Textbox) list1.get(1);
						String tbInput = textbox1.getValue().toString();

						List list2 = div2.getChildren();
						Textbox textbox2 = (Textbox) list2.get(1);
						String tbInputMonth = textbox2.getValue().toString();
						query = query.concat(tbInput + ":" + tbInputMonth);
					} else if (tierType.equals(OCConstants.LOYALTY_CUMULATIVE_POINTS)) {
						List list1 = div1.getChildren();
						Textbox textbox1 = (Textbox) list1.get(1);
						String tbInput1 = textbox1.getValue().toString();

						Textbox monthTextbox1 = (Textbox) list1.get(3);
						String tbInputMonth1 = monthTextbox1.getValue().toString();
						query = query.concat(tbInput1 + ":" + tbInputMonth1);
						
						List list2 = div2.getChildren();
						Textbox textbox2 = (Textbox) list2.get(1);
						String tbInput2 = textbox2.getValue().toString();

						Textbox monthTextbox2 = (Textbox) list2.get(3);
						String tbInputMonth2 = monthTextbox2.getValue().toString();
						
						if (!tbInput2.trim().isEmpty() && !tbInputMonth2.trim().isEmpty()) {
							query = query.concat(";=;CumulativeVisits:"+tbInput2 + ":" + tbInputMonth2);

						}
					} else {
						List list = div2.getChildren();
						Textbox textbox = (Textbox) list.get(0);
						String tbInputValue = textbox.getValue().toString();
						query = query.concat(tbInputValue);
					}
				}
				return query;
			}
		}
		return query;
	}// getMultipleTierRules()

	private Listbox typesOfTierUpdateRules() {
		Listbox tierUpdateListBx = new Listbox();
		Listitem tempItem1 = new Listitem("Lifetime Points");
		tempItem1.setValue(OCConstants.LOYALTY_LIFETIME_POINTS);
		Listitem tempItem2 = new Listitem("Lifetime Purchase Value");
		tempItem2.setValue(OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE);
		Listitem tempItem3 = new Listitem("Cumulative Purchase Value");
		tempItem3.setValue(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE);
		Listitem tempItem4 = new Listitem("Single Purchase Value");
		tempItem4.setValue(OCConstants.LOYALTY_SINGLE_PURCHASE_VALUE);
		Listitem tempItem5 = new Listitem("Cumulative Points");
		tempItem5.setValue(OCConstants.LOYALTY_CUMULATIVE_POINTS);
		Listitem tempItem6 = new Listitem("Cumulative Visits");
		tempItem6.setValue(OCConstants.LOYALTY_CUMULATIVE_VISITS);

		tempItem1.setParent(tierUpdateListBx);
		tempItem2.setParent(tierUpdateListBx);
		tempItem3.setParent(tierUpdateListBx);
		tempItem4.setParent(tierUpdateListBx);
		tempItem5.setParent(tierUpdateListBx);
		tempItem6.setParent(tierUpdateListBx);

		tierUpdateListBx.setMold("select");

		return tierUpdateListBx;
	}

}  

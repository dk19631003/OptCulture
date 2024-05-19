package org.mq.optculture.controller.loyalty;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.ValueCodes;
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
import org.mq.optculture.data.dao.ValueCodesDao;
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
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class LoyaltyPerkRulesController extends GenericForwardComposer {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Long userId;
	private Listbox perkIssuanceTypeLbId, selTierLbId,perkExpValueTypeLbId,perkLimitDateTypeLbId,ValueCodeLbId;
	//cumTypeLbId;
	private Div tierTypeDivId,valueCodeDivId,otpEnabledDivId;
	private Textbox privilegeNameTbId, valueTbId,coversionfromTbId,conversionToTbId,perkLimitTbId,perkLimitDateTbId,redemptionPercentageTbId,redemptionValueTbId,minReceiptAmtTbId,minBalanceTbId,perkExpValueTbId,otpAmtLimitTbId;
	private Button savBtnId,cancelBtnId;
	private Rows tierRowsId;
	private Checkbox expiryChkId,enableOTPAuthChkId;
	private Columns tierColsId;
	private boolean isEdit=false;
	private  String  userCurrencySymbol = "$ "; 
	private Label currencySymbolId;
	private Users currUser = GetUser.getUserObj();
	private ValueCodesDao valueCodesDao;
	

	public LoyaltyPerkRulesController() {
		userId = GetUser.getUserObj().getUserId();
		session = Sessions.getCurrent();
		
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
		valueCodesDao = (ValueCodesDao) SpringUtil.getBean(OCConstants.VALUE_CODES_DAO);

		LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
		if(prgmObj.getTierEnableFlag() == OCConstants.FLAG_YES ){
			tierTypeDivId.setVisible(true);
		}else {
			tierTypeDivId.setVisible(false);
		}
		//setPerkExpDateValues();
		//perkIssuanceTypeLbId.setSelectedIndex(0);
		//program edit case when atleast one tier is defined 
		/*if(tierList != null) {
			for(LoyaltyProgramTier loyaltyProgramTier :tierList ) {
				List<Listitem> list = perkIssuanceTypeLbId.getItems();
				for(Listitem li:list) {
					if(loyaltyProgramTier.getPerkIssuanceType().equalsIgnoreCase(li.getValue().toString())) {
						perkIssuanceTypeLbId.setSelectedItem(li);
						//perkIssuanceTypeLbId.setDisabled(true);
					}
				}
			}
			

			boolean isExists = false;
			LoyaltyProgramTier tierObj = null;
			for(LoyaltyProgramTier loyaltyProgramTier :tierList) {
				if(loyaltyProgramTier.getTierUpgdConstraintValue() != null){
					isExists = true;
					tierObj = loyaltyProgramTier;
					break;
				}
			}


		}*/
		
		List<ValueCodes> valueCodes = valueCodesDao.findNonFBPValueCodes(currUser.getUserOrganization().getUserOrgId());
		if (valueCodes != null && valueCodes.size() > 0) {
			valueCodes.forEach(code -> {
				Listitem item = new Listitem(code.getValuCode());
				item.setValue(code.getValuCode());
				item.setParent(perkIssuanceTypeLbId);
			});
			List<Listitem> listOfValueCodes = perkIssuanceTypeLbId.getItems();
			for(Listitem listItem : listOfValueCodes) {
				
				if(listItem.getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_PERKS)) {
					perkIssuanceTypeLbId.setSelectedItem(listItem);
				}else {
					perkIssuanceTypeLbId.setSelectedIndex(0);
				}
			}
			//perkIssuanceTypeLbId.setSelectedIndex(0);
		}

		//tier edit from step-6
		prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
		logger.info("prgmObj"+prgmObj);
		LoyaltyProgramTier loyaltyProgramTier=(LoyaltyProgramTier)session.getAttribute("loyaltyProgramTier");
		if(loyaltyProgramTier!=null) {
			savBtnId.setLabel("Save");
			cancelBtnId.setVisible(true);
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
				}
				else {
					logger.info("in else");
					li=new Listitem("Tier 1");
					li.setValue("Tier 1");
					li.setParent(selTierLbId);
					selTierLbId.setSelectedItem(li);
					selTierLbId.setDisabled(true);
				}
			}
		}

		redrawTierList(tierList);

		/*if(perkIssuanceTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_PERKS)){//getSelectedIndex()==0) { //getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_PERKS)){
			coversionfromTbId.setDisabled(false);
			conversionToTbId.setDisabled(false);
			//minBalanceTbId.setDisabled(false);
		} else {
			coversionfromTbId.setDisabled(true);
			conversionToTbId.setDisabled(true);
			//minBalanceTbId.setDisabled(false);
		}*/
		
		//adding currency symbol
		 String currSymbol = Utility.countryCurrencyMap.get(GetUser.getUserObj().getCountryType());
		   if(currSymbol != null && !currSymbol.isEmpty()) userCurrencySymbol = currSymbol + " ";
		   
		   
		   currencySymbolId.setValue("perks earned is equivalent to  " + userCurrencySymbol + " ");
		 //  issuanceForCurrId.setValue("Per Invoice :  ");

	}//doAfterCompose()
	
	/*private void setPerkExpDateValues() {
		Listitem li=null;
		if(perkExpValueTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_YEAR)) {
			Components.removeAllChildren(perkExpValueLbId);
			logger.info("in Years");
			for(int i=1;i<=10;i++) {
				li=new Listitem(i+"");
				li.setValue(i);
				li.setParent(perkExpValueLbId);
				perkExpValueLbId.setSelectedIndex(0);
			}
		}else if(perkExpValueTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_MONTH)) {
			Components.removeAllChildren(perkExpValueLbId);
			logger.info("in Months");
			for(int i=1;i<=12;i++) {
				li=new Listitem(i+"");
				li.setValue(i);
				li.setParent(perkExpValueLbId);
				perkExpValueLbId.setSelectedIndex(0);
			}
		}
	}//setPerkExpDateValues()*/
	
	/*public void onSelect$perkExpValueTypeLbId() {
		setPerkExpDateValues();
	}//onSelect$perkExpValueLbId()*/

	public void onSelect$selTierLbId() {
		Long prgmId=(Long) session.getAttribute("programId");
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
		if(!selTierLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Select Tier")) {
			int tierVal = Integer.parseInt(selTierLbId.getSelectedItem().getValue().toString().replace("Tier ", ""));
			if(tierVal==prgmObj.getNoOfTiers()) {
				
			}else {
				
			}
		}
	}//onSelect$selTierLbId()
	
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
	
	public boolean isOptedForSMS() {
		Users currentUser = GetUser.getUserObj();

		if(!currentUser.isEnableSMS()) {
			return false;
		}
		return true;
	}//isOptedForSMS()

	/*public void onSelect$perkIssuanceTypeLbId() {

		if(perkIssuanceTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_PERKS)){//getSelectedIndex()==0) { //getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_PERKS)){
			coversionfromTbId.setDisabled(false);
			conversionToTbId.setDisabled(false);
			//minBalanceTbId.setDisabled(false);
		} else {
			coversionfromTbId.setDisabled(true);
			conversionToTbId.setDisabled(true);
			//minBalanceTbId.setDisabled(false);
		}
		/*if(perkIssuanceTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Value Code")) {
			valueCodeDivId.setVisible(true);
		} else 
			valueCodeDivId.setVisible(false);
	}//onSelect$earnTypeLbId()*/

	public void onClick$savBtnId() {

		Long tierId=null;
		Double perkIssuanceValue=null;
		Long perkUsageLimit=null;
		//String valueCode = null;
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
		/*if(valueCodeDivId.isVisible() && ValueCodeLbId.getSelectedIndex()!=0 && ValueCodeLbId.getSelectedItem().getValue()!=null) {
			   valueCode = ValueCodeLbId.getSelectedItem().getValue();
		}*/
		//String perkIssuanceType= null;
		/*if(perkIssuanceTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Perk")) {
			perkIssuanceType = "Points";
		} else {
			perkIssuanceType=perkIssuanceTypeLbId.getSelectedItem().getValue().toString();
		}*/
		String perkIssuanceType=perkIssuanceTypeLbId.getSelectedItem().getValue().toString();
		String earnValueType=OCConstants.LOYALTY_TYPE_VALUE;
		Double earnOnSpentAmount = 1.00;
		//boolean expiryCheck = expiryChkId.isChecked();
		
		String perkExpType = perkExpValueTypeLbId.getSelectedItem().getValue();
		Long perkExpDateValue = null;
		//if(perkExpValueTbId.getValue()!=null && !perkExpValueTbId.getValue().isEmpty())
		  //  perkExpDateValue = Long.parseLong(perkExpValueTbId.getValue());
		
		if(perkLimitTbId.getValue()!=null && !perkLimitTbId.getValue().trim().isEmpty()) {
			String amtRegExp = "^[0-9]*$";
			Pattern pattern = Pattern.compile(amtRegExp);  
			Matcher m = pattern.matcher(perkLimitTbId.getValue().trim());
			String match = "";
			while (m.find()) {
				match += m.group();
			}
			try {
				String value  = ""+Long.parseLong(match);
			} catch (NumberFormatException e) {
				MessageUtil.setMessage("Limit Value should not be decimal points.", "Color:red", "Top");
				perkLimitTbId.setFocus(true);
				return;
			}
			perkUsageLimit=Long.parseLong(perkLimitTbId.getValue());
		}
		String perkUsageLimitType = perkLimitDateTypeLbId.getSelectedItem().getValue().toString();
		if(valueTbId.getValue()!=null && !valueTbId.getValue().trim().isEmpty()) {
			String amtRegExp = "^[0-9]*$";
			Pattern pattern = Pattern.compile(amtRegExp);  
			Matcher m = pattern.matcher(valueTbId.getValue().trim());
			String match = "";
			while (m.find()) {
				match += m.group();
			}
			try {
				String value  = ""+Long.parseLong(match);
			} catch (NumberFormatException e) {
				MessageUtil.setMessage("Value should not be decimal points.", "Color:red", "Top");
				valueTbId.setFocus(true);
				return;
			}				
			//earnValue=Double.parseDouble(valueTbId.getValue());
			perkIssuanceValue=Double.parseDouble(Utility.truncateUptoTwoDecimal(Double.parseDouble(valueTbId.getValue())));
		} 
		
		Double convertFromPoints=null,convertToAmount=null;
		
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
		/*if(minBalanceTbId.getValue()!=null && !minBalanceTbId.getValue().isEmpty()) {
			try {
				minBalanceValue = Double.parseDouble(minBalanceTbId.getValue().toString());
			}catch (Exception e) {
				MessageUtil.setMessage("Please provide valid input for Min Balance required to redeem.", "color:red", "TOP");
				minBalanceTbId.setFocus(true);
				return;
			}
			
		}*/
		
		double redemptionPercentageLimit = redemptionPercentageTbId.getValue() != null && !redemptionPercentageTbId.getValue().trim().isEmpty() 
				? Double.parseDouble(Utility.truncateUptoTwoDecimal(redemptionPercentageTbId.getValue().trim())) : 0.0;	
		double redemptionValueLimit = redemptionValueTbId.getValue() != null && !redemptionValueTbId.getValue().trim().isEmpty() 
				? Double.parseDouble(Utility.truncateUptoTwoDecimal(redemptionValueTbId.getValue().trim())) : 0.0;
		
		if(isEdit) {
			String editedTier=(String) session.getAttribute("editedTierType");
			if(editedTier.equalsIgnoreCase(tierType)){
				tierId=(Long) session.getAttribute("tierId");
			}
		}
	
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

		}

		ltyPrgmSevice.savePerkTier(programId, tierType, tierName, perkIssuanceType, perkIssuanceValue,earnValueType,earnOnSpentAmount, perkExpType, perkExpDateValue, perkUsageLimit, perkUsageLimitType, 
				convertFromPoints, convertToAmount, redemptionPercentageLimit, redemptionValueLimit,minReceiptValue,minBalanceValue, tierId, userId,otpLimit,otpEnable);
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
						
							incomplete = true;
							//break;
						
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
		ltyPrgmSevice.savePrgmObj(prgmObj);

		session.removeAttribute("tierId");
		isEdit=false;
		resetFields();
		tierList = ltyPrgmSevice.getTierList(programId);
		redrawTierList(tierList);
		savBtnId.setLabel("Add");
		cancelBtnId.setVisible(false);
	}//onClick$savBtnId()
	
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
		
		/*if (perkIssuanceTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Value Code")) {
			if(ValueCodeLbId.isVisible() && ValueCodeLbId.getSelectedIndex() == 0
				&& ValueCodeLbId.getSelectedItem().getValue() == null) {
				
				MessageUtil.setMessage("Please select value code.", "color:red", "TOP");
				return false;
			}
			
		}*/
		
		/*if(perkIssuanceTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_PERKS)) {
			if(!checkIfLong(valueTbId.getValue().trim())) {
				MessageUtil.setMessage("Please provide number value for earn rule.", "red");
				valueTbId.setFocus(true);
				return false;
			}
		}else if(perkIssuanceTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT)) {
			
			if(!checkIfDouble(valueTbId.getValue().trim())) {
				MessageUtil.setMessage("Please provide number value for earn rule.", "red");
				valueTbId.setFocus(true);
				return false;
			}
		}*/
		if(!checkIfDouble(valueTbId.getValue().trim())) {
			MessageUtil.setMessage("Please provide number value for earn rule.", "red");
			valueTbId.setFocus(true);
			return false;
		}
		
		/*if(perkExpValueTbId.getValue()==null || perkExpValueTbId.getValue().trim().isEmpty()) {
			MessageUtil.setMessage("please provide Perk Expiry.", "Color:red", "Top");
			perkExpValueTbId.setFocus(true);
			return false;
		}
		
		if(perkExpValueTbId.getValue()!=null && !perkExpValueTbId.getValue().trim().isEmpty()) {
			try {
				Long value = Long.parseLong(perkExpValueTbId.getValue().toString());
			}catch (Exception e) {
				MessageUtil.setMessage("Please provide valid input for Perk Expiry.", "color:red", "TOP");
				perkExpValueTbId.setFocus(true);
				return false;
			}
			
		}*/
		
		/*else if(perkIssuanceTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Value Code")) {
			
			if(!checkIfDouble(valueTbId.getValue().trim())) {
				MessageUtil.setMessage("Please provide number value for earn rule.", "red");
				valueTbId.setFocus(true);
				return false;
			}
		}*/
		
		if((conversionToTbId.getValue().trim().isEmpty() && coversionfromTbId.getValue().trim().length() > 0) 
				||(conversionToTbId.getValue().trim().length() > 0 && coversionfromTbId.getValue().trim().isEmpty()) ){
			MessageUtil.setMessage("Conversion rule cannot be empty.", "red");
			conversionToTbId.setFocus(true);
			return false;
		}
		
		if((conversionToTbId.getValue().trim().isEmpty() && coversionfromTbId.getValue().trim().isEmpty()) ){
			MessageUtil.setMessage("Conversion rule cannot be empty.", "red");
			conversionToTbId.setFocus(true);
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

		return true;

	}//validateFields()

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

				}
			}
		}
		privilegeNameTbId.setValue("");
		/*if(tierList == null) {
			perkIssuanceTypeLbId.setDisabled(false);
		}else {
			perkIssuanceTypeLbId.setDisabled(true);
		}*/
		otpEnabledDivId.setVisible(false);
		otpAmtLimitTbId.setValue("");
		enableOTPAuthChkId.setChecked(false);
		valueTbId.setValue("");
		perkLimitDateTypeLbId.setSelectedIndex(0);
		//expiryChkId.setChecked(true);
		redemptionPercentageTbId.setValue("");
		redemptionValueTbId.setValue("");
		perkExpValueTypeLbId.setSelectedIndex(0);
		//perkExpValueLbId.setSelectedIndex(0);
		//perkExpValueTbId.setValue("");
		perkIssuanceTypeLbId.setSelectedIndex(0);
		coversionfromTbId.setDisabled(false);
		conversionToTbId.setDisabled(false);
		//minBalanceTbId.setDisabled(false);
		coversionfromTbId.setValue("");
		conversionToTbId.setValue("");
		perkLimitTbId.setValue("");
		//ValueCodeLbId.setSelectedIndex(0);
		//valueCodeDivId.setVisible(false);
		minReceiptAmtTbId.setValue("");
		//minBalanceTbId.setValue("");
		List<Listitem> listOfValueCodes = perkIssuanceTypeLbId.getItems();
		for(Listitem listItem : listOfValueCodes) {
			
			if(listItem.getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_PERKS)) {
				perkIssuanceTypeLbId.setSelectedItem(listItem);
			}else {
				perkIssuanceTypeLbId.setSelectedIndex(0);
			}
		}
		
		try {
		if(prgmObj != null) {
			if(prgmObj.getTierEnableFlag() == OCConstants.FLAG_YES ) {
				if(tierList == null) {
					//ruleTypeLbId.setDisabled(false);
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

				}
			}
		}
		} catch(Exception e) {
			
		}
	}//resetFields()

	private void redrawTierList(List<LoyaltyProgramTier> tierList) {
		Long prgmId=(Long) session.getAttribute("programId");
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
		resetTiersGridCols(prgmObj);
		String currSymbol = Utility.countryCurrencyMap.get(currUser.getCountryType());
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
						
						String quota = "Earn "+loyaltyProgramTier.getEarnValue().intValue()+" "+loyaltyProgramTier.getEarnType()+" on purchase.";
          			
						row.appendChild(new Label(quota));
						
						//String perkUsageLimit = "Max "+loyaltyProgramTier.getPerkLimitValue()+" "+loyaltyProgramTier.getEarnType() +" per "+loyaltyProgramTier.getPerkLimitExpType();
						String perkUsageLimit = "";
						if(loyaltyProgramTier.getPerkLimitValue()!=null && !loyaltyProgramTier.getPerkLimitValue().toString().isEmpty()) {
                        	perkUsageLimit = "Max "+loyaltyProgramTier.getPerkLimitValue()+" "+loyaltyProgramTier.getEarnType() +" per "+loyaltyProgramTier.getPerkLimitExpType();
                        }
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
	}//redrawTierList()

	private void resetTiersGridCols(LoyaltyProgram prgmObj) {
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

	public void onEvent(Event event) throws Exception {

		super.onEvent(event);
		if(event.getTarget() instanceof Image){
			Image img = (Image)event.getTarget();
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
		}
	}//onEvent()

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
			
			valueTbId.setValue(loyaltyProgramTier.getEarnValue()==null?"":loyaltyProgramTier.getEarnValue().intValue()+"");
			
			privilegeNameTbId.setValue(loyaltyProgramTier.getTierName());
			
			List<Listitem> perkTypeList=perkIssuanceTypeLbId.getItems();
			for(Listitem li : perkTypeList) {
				/*if(li.getValue().toString().equalsIgnoreCase("Perks")) {
					li.setValue("Points");
				}*/
				if(loyaltyProgramTier.getEarnType().equalsIgnoreCase(li.getValue().toString())) {
					perkIssuanceTypeLbId.setSelectedItem(li);
					/*if(tiersList.size() > 1) {
						perkIssuanceTypeLbId.setDisabled(true);
					}else {
						perkIssuanceTypeLbId.setDisabled(false);
					}*/
					break;
				}
			}
			
			/*if(perkIssuanceTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Value Code")) {
				valueCodeDivId.setVisible(true);
				List<Listitem> items = ValueCodeLbId.getItems();
				items.forEach(item -> {
					
					if (item.getValue() != null &&  loyaltyProgramTier.getValueCode() != null && item.getValue().equals(loyaltyProgramTier.getValueCode())) {
						
						item.setSelected(true);
					}
				});
			} else 
				valueCodeDivId.setVisible(false);*/
			
			
			/*if(perkIssuanceTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT)){
				coversionfromTbId.setDisabled(true);
				conversionToTbId.setDisabled(true);
			} else if(perkIssuanceTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_PERKS)) {
				coversionfromTbId.setDisabled(false);
				conversionToTbId.setDisabled(false);
			} else if(perkIssuanceTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_VALUE)) {
				coversionfromTbId.setDisabled(false);
				conversionToTbId.setDisabled(false);
			}*/
			/*if(perkIssuanceTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_PERKS)){//getSelectedIndex()==0) { //getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_PERKS)){
				coversionfromTbId.setDisabled(false);
				conversionToTbId.setDisabled(false);
				//minBalanceTbId.setDisabled(false);
			} else {
				coversionfromTbId.setDisabled(true);
				conversionToTbId.setDisabled(true);
				//minBalanceTbId.setDisabled(false);
			}*/
			
			List<Listitem> perkExpTypeList=perkExpValueTypeLbId.getItems();
			for(Listitem li : perkExpTypeList) {
				if(loyaltyProgramTier.getRewardExpiryDateType().equalsIgnoreCase(li.getValue().toString())) {
					perkExpValueTypeLbId.setSelectedItem(li);
					break;
				}
			}
			
			/*List<Listitem> perkExpDateList=perkExpValueLbId.getItems();
			for(Listitem li : perkExpDateList) {
				if(loyaltyProgramTier.getRewardExpiryDateValue().toString().equalsIgnoreCase(li.getValue().toString())) {
					perkExpValueLbId.setSelectedItem(li);
					break;
				}
			}*/
			//perkExpValueTypeLbId.setSelectedIndex(0);
			//perkExpValueTbId.setValue(loyaltyProgramTier.getRewardExpiryDateValue()==null?"":loyaltyProgramTier.getRewardExpiryDateValue().toString());
			
			perkLimitTbId.setValue(loyaltyProgramTier.getPerkLimitValue()==null?"":loyaltyProgramTier.getPerkLimitValue().intValue()+"");
			
			List<Listitem> perkLimitDateTypeList=perkLimitDateTypeLbId.getItems();
			for(Listitem li : perkLimitDateTypeList) {
				if(loyaltyProgramTier.getPerkLimitExpType().equalsIgnoreCase(li.getValue().toString())) {
					perkLimitDateTypeLbId.setSelectedItem(li);
				}
			}
			
			/*if(loyaltyProgramTier.isPerkIssuanceExpiryCheck())
				expiryChkId.setChecked(true);
			else 
				expiryChkId.setChecked(false);*/
			enableOTPAuthChkId.setChecked(loyaltyProgramTier.getRedemptionOTPFlag() == OCConstants.FLAG_YES ? true : false);
			if(enableOTPAuthChkId.isChecked()){
				otpEnabledDivId.setVisible(true);
				otpAmtLimitTbId.setValue(loyaltyProgramTier.getOtpLimitAmt() == null ? "" : f.format(loyaltyProgramTier.getOtpLimitAmt()));
			}
			else{
				otpEnabledDivId.setVisible(false);
				otpAmtLimitTbId.setValue("");
			}
			
			redemptionPercentageTbId.setValue(loyaltyProgramTier.getRedemptionPercentageLimit() != null && loyaltyProgramTier.getRedemptionPercentageLimit() == 0.0 ? "" : f.format(loyaltyProgramTier.getRedemptionPercentageLimit())+"");
			redemptionValueTbId.setValue(loyaltyProgramTier.getRedemptionValueLimit() != null && loyaltyProgramTier.getRedemptionValueLimit() == 0.0 ? "" : f.format(loyaltyProgramTier.getRedemptionValueLimit())+"");

			coversionfromTbId.setValue(loyaltyProgramTier.getConvertFromPoints()==null?"":loyaltyProgramTier.getConvertFromPoints().intValue()+"");
			conversionToTbId.setValue(loyaltyProgramTier.getConvertToAmount()==null?"":f.format(loyaltyProgramTier.getConvertToAmount()));
			
			minReceiptAmtTbId.setValue(loyaltyProgramTier.getMinReceiptValue()!=null?f.format(loyaltyProgramTier.getMinReceiptValue()):"");
			//minBalanceTbId.setValue(loyaltyProgramTier.getMinBalanceValue()!=null?loyaltyProgramTier.getMinBalanceValue().toString():"");

			}
	}//tierSettings()
 
}  


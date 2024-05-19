package org.mq.optculture.controller.loyalty;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.LoyaltyCardSet;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltyThresholdAlerts;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.ValueCodes;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.EncryptDecryptLtyMembshpPwd;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.loyalty.LoyaltyProgramService;
import org.mq.optculture.data.dao.ValueCodesDao;
import org.mq.optculture.data.dao.ValueCodesDaoForDML;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;


public class CreateLoyaltyProgramController extends GenericForwardComposer {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Long userId, prgmId ;
	private Listbox addPrgmLbId, noOfTiersLbId, regReqLbId,dynamicMemCardType;
	private Div noOfTiersDivId,ltyPrgmTypeDivId, otpEnabledDivId,dynamicMemTypeDivId,defaultPgmDivId,redemptionDivId, redeemPercentageDivId,enableIssuanceDisDivId,redemAmtForIssuanceLablDivId,redemAmtForIssuanceChbDivId;
	private Radio  cardNoRadioId,mobileNoRadioId,dynamicMemNoRadioId,loyaltyRadioId,perkRadioId;
	private Radiogroup ltyPrgmTypeRadioGrId,prgmTypeRadioGrId;
	private Checkbox enableTierChkId, enableDefaultChkId, enableUniqueMblNoChkId,enableUniqueEmailChkId,enableOTPAuthChkId, enablePartialReturnsAuthChkId,enableIssuanceDisChkId,enableRedeemedAmountChkId;
	private Button importBtnId, nextBtnId;
	private Textbox prgmNameTbId, prgmDescTbId, otpAmtLimitTbId, redemptionPercentageTbId, redemptionValueTbId;
	private Intbox lengthTbId;
	private Label selectedFieldsLblId,requisitesLbId;
    private Users currentUser; 
	public CreateLoyaltyProgramController() {
		userId = GetUser.getUserObj().getUserId();
		currentUser= GetUser.getUserObj();
		session = Sessions.getCurrent();
		Utility.ltyBreadCrumbFrom(1, OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype()));
		prgmId = (Long) session.getAttribute("programId");
	}//CreateLoyaltyProgramController()

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Create Loyalty Program (Step 1 of 6)","",style,true);
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();

		
		//Set add from Program Listbox
		 mobileNoRadioId.setVisible(true);
       /*if( currentUser.getloyaltyServicetype().equals(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC))
    	   mobileNoRadioId.setVisible(false);*/
		List<LoyaltyProgram> progList = ltyPrgmSevice.getProgList(userId);
		if(progList == null || progList.size() == 0) {
			logger.info("no program list exist in the DB for the user...");
		}
		else {
			Listitem tempItem = null;
			for (LoyaltyProgram loyaltyProgram : progList) {
				if(prgmId != null){
					if(loyaltyProgram.getProgramId().longValue() == prgmId.longValue()) continue;
				}
				tempItem = new Listitem(loyaltyProgram.getProgramName(),loyaltyProgram.getProgramId());
				tempItem.setParent(addPrgmLbId);
			} // for
		}

		if(prgmId != null) { //shows it is in edit mode
			addPrgmLbId.setDisabled(true);
			nextBtnId.setVisible(true);
			LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
			addPrgmLbId.setSelectedIndex(0);
			prgmNameTbId.setValue(prgmObj.getProgramName());
			prgmNameTbId.setDisabled(true);
			prgmDescTbId.setValue(prgmObj.getDescription());

			//loyaltyRadioId.setDisabled(true);
			//perkRadioId.setDisabled(true);
			mobileNoRadioId.setChecked(false);
			cardNoRadioId.setChecked(false);
			dynamicMemNoRadioId.setChecked(false);
			if(prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
				mobileNoRadioId.setChecked(true);
				ltyPrgmTypeDivId.setVisible(false);
				if(prgmObj.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_DRAFT)) {
					mobileNoRadioId.setDisabled(false);
					cardNoRadioId.setDisabled(false);
					dynamicMemNoRadioId.setDisabled(false);
				}
				else {
					loyaltyRadioId.setDisabled(true);
					perkRadioId.setDisabled(true);
					mobileNoRadioId.setDisabled(true);
					cardNoRadioId.setDisabled(true);
					dynamicMemNoRadioId.setDisabled(true);
				}
			}else if(prgmObj.getProgramType().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_TYPE_DYNAMIC)) {
				enableDefaultChkId.setDisabled(true);
				dynamicMemNoRadioId.setChecked(true);
				dynamicMemTypeDivId.setVisible(true);
				ltyPrgmTypeDivId.setVisible(true);
				defaultPgmDivId.setVisible(false);
				enableDefaultChkId.setVisible(false);
				int index = prgmObj.getValidationRule().split(Constants.ADDR_COL_DELIMETER)[0].equalsIgnoreCase("N")?0:1;
				dynamicMemCardType.setSelectedIndex(index);
				lengthTbId.setText(prgmObj.getValidationRule().split(Constants.ADDR_COL_DELIMETER)[1]);
				enableUniqueMblNoChkId.setChecked(prgmObj.getUniqueMobileFlag() == OCConstants.FLAG_YES ? true : false);
				enableUniqueEmailChkId.setChecked(prgmObj.getUniqueEmailFlag() == OCConstants.FLAG_YES ? true : false);
				if(prgmObj.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_DRAFT)) {
					mobileNoRadioId.setDisabled(false);
					cardNoRadioId.setDisabled(false);
					dynamicMemNoRadioId.setDisabled(false);
				}
				else {
					loyaltyRadioId.setDisabled(true);
					perkRadioId.setDisabled(true);
					mobileNoRadioId.setDisabled(true);
					cardNoRadioId.setDisabled(true);
					dynamicMemNoRadioId.setDisabled(true);
				}
			
			}
			else if(prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD)) {
				cardNoRadioId.setChecked(true);
				ltyPrgmTypeDivId.setVisible(true);
				//enableUserGeneratedCardChkId.setChecked((prgmObj.getDefaultFlag() == OCConstants.FLAG_YES && prgmObj.getAllowUserGeneratedCardsFlag() == OCConstants.FLAG_YES) ? true : false);
				enableDefaultChkId.setChecked(prgmObj.getDefaultFlag() == OCConstants.FLAG_YES ? true : false);
				enableUniqueMblNoChkId.setChecked(prgmObj.getUniqueMobileFlag() == OCConstants.FLAG_YES ? true : false);
				enableUniqueEmailChkId.setChecked(prgmObj.getUniqueEmailFlag() == OCConstants.FLAG_YES ? true : false);
				if(prgmObj.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_DRAFT)) {
					List<LoyaltyCardSet> list = ltyPrgmSevice.getCardsetList(prgmId);
					if(list != null && list.size() > 0) {
						mobileNoRadioId.setDisabled(true);
						cardNoRadioId.setDisabled(true);
						dynamicMemNoRadioId.setDisabled(true);
					}
					else {
						mobileNoRadioId.setDisabled(false);
						cardNoRadioId.setDisabled(false);
						dynamicMemNoRadioId.setDisabled(false);
					}
				}
				else {
					loyaltyRadioId.setDisabled(true);
					perkRadioId.setDisabled(true);
					mobileNoRadioId.setDisabled(true);
					cardNoRadioId.setDisabled(true);
					dynamicMemNoRadioId.setDisabled(true);
				}

			}

			enableTierChkId.setChecked(prgmObj.getTierEnableFlag() == OCConstants.FLAG_YES ? true : false);
			if(enableTierChkId.isChecked()) {
				noOfTiersDivId.setVisible(true);
				List<Listitem> itemsList = noOfTiersLbId.getItems();
				for (Listitem listitem : itemsList) {
					if(listitem.getValue().toString().equalsIgnoreCase(prgmObj.getNoOfTiers()+"")) {
						noOfTiersLbId.setSelectedItem(listitem);
						break;
					}
				}
			}
			else {
				noOfTiersDivId.setVisible(false);
				noOfTiersLbId.setSelectedIndex(0);
			}
			//DecimalFormat f = new DecimalFormat("#0.00");
			//enableOTPAuthChkId.setChecked(prgmObj.getRedemptionOTPFlag() == OCConstants.FLAG_YES ? true : false);
			/*if(enableOTPAuthChkId.isChecked()){
				otpEnabledDivId.setVisible(true);
				otpAmtLimitTbId.setValue(prgmObj.getOtpLimitAmt() == null ? "" : f.format(prgmObj.getOtpLimitAmt()));
			}
			else{
				otpEnabledDivId.setVisible(false);
				otpAmtLimitTbId.setValue("");
			}*/
			/*List<Listitem> minBalanceTypeList=minBalanceTypeLbId.getItems();
			for(Listitem li : minBalanceTypeList) {
				if(prgmObj.getMinBalanceType()!=null && prgmObj.getMinBalanceType().equalsIgnoreCase(li.getValue().toString())) {
					minBalanceTypeLbId.setSelectedItem(li);
					break;
				}
			}*/
			
			if(prgmObj.getRewardType()!=null && prgmObj.getRewardType().equalsIgnoreCase("Perk")) {
				perkRadioId.setChecked(true);
				//redemAmtForIssuanceLablDivId.setVisible(false);
				//redemAmtForIssuanceChbDivId.setVisible(false);
			} else {
				loyaltyRadioId.setChecked(true);
				//redemAmtForIssuanceLablDivId.setVisible(true);
				//redemAmtForIssuanceChbDivId.setVisible(true);
			}
			
			List<LoyaltyProgramTier> tierList = ltyPrgmSevice.getTierList(prgmId);
			int tiersCount = prgmObj.getNoOfTiers();
			int count=0;
			if(tierList != null){
				for (LoyaltyProgramTier tierObj : tierList) {
				 int tierNo = Integer.parseInt(tierObj.getTierType().replace("Tier ", ""));
					count = tierNo;
				}
				if(count < tiersCount || count == tiersCount) {
					loyaltyRadioId.setDisabled(true);
					perkRadioId.setDisabled(true);
				}	
			}
				
			//minBalanceValueTbId.setValue(prgmObj.getMinBalanceRedeemValue() == null ? "" : prgmObj.getMinBalanceRedeemValue().toString());
			//minReceiptAmtValueTbId.setValue(prgmObj.getMinReceiptAmtValue() == null ? "" : prgmObj.getMinReceiptAmtValue().toString());
			// added for partial reversal in returns
			//enablePartialReturnsAuthChkId.setChecked(prgmObj.getPartialReversalFlag() == OCConstants.FLAG_YES ? true : false);
			//adding for inclulde redeemed amount during issuance
			//enableRedeemedAmountChkId.setChecked(prgmObj.getConsiderRedeemedAmountFlag() == OCConstants.FLAG_YES ? true : false);
			//enableIssuanceDisChkId.setChecked(prgmObj.isIssuanceDisable());
			// added for percentage limit in redemption
			//redemptionPercentageTbId.setValue(prgmObj.getRedemptionPercentageLimit() != null && prgmObj.getRedemptionPercentageLimit() == 0.0 ? "" : f.format(prgmObj.getRedemptionPercentageLimit())+"");
			//redemptionValueTbId.setValue(prgmObj.getRedemptionValueLimit() != null && prgmObj.getRedemptionValueLimit() == 0.0 ? "" : f.format(prgmObj.getRedemptionValueLimit())+"");

			String regReqStr = prgmObj.getRegRequisites();
			if(regReqStr != null && regReqStr.length() != 0) {
				regReqLbId.setSelectedIndex(-1);
				Set regReqSet = new HashSet();
				List<Listitem> items = regReqLbId.getItems();
				for (Listitem li : items) {
					if(regReqStr.contains(li.getValue().toString())) {
						if(prgmObj.getUniqueMobileFlag() == OCConstants.FLAG_YES && li.getValue().toString().equalsIgnoreCase("mobilePhone")) {
							li.setDisabled(true);
						}
						regReqSet.add(li);
					}
				}
				regReqLbId.setSelectedItems(regReqSet);
				onSelect$regReqLbId();
			}
		}
		onCheck$enableUniqueMblNoChkId();
		onCheck$enableUniqueEmailChkId();
		//if(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype()))redemptionDivId.setVisible(false);
		/*if(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype())&&!GetUser.getUserObj().isNewPlugin()){
			redemptionDivId.setVisible(false);
			//redeemPercentageDivId.setVisible(false);//APP-1969
		}*/
		if(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype())&&!GetUser.getUserObj().isEnableLoyaltyExtraction()){
		enableIssuanceDisDivId.setVisible(false);
		}
	}//doAfterCompose()

	public void onCheck$enableTierChkId() {
		if(enableTierChkId.isChecked()) {
			noOfTiersDivId.setVisible(true);
			noOfTiersLbId.setSelectedIndex(1);
		}
		else {
			noOfTiersDivId.setVisible(false);
			noOfTiersLbId.setSelectedIndex(0);
		}
	}//onCheck$enableTierChkId()

	public void onSelect$addPrgmLbId() {
		if(addPrgmLbId.getSelectedIndex() == 0) {
			importBtnId.setVisible(false);
		}
		else {
			importBtnId.setVisible(true);
		}
	}//onSelect$addPrgmLbId()

	public void onClick$importBtnId() {
		prgmId = (Long) session.getAttribute("programId");
		if(addPrgmLbId.getSelectedIndex() == 0) {
			return;
		}
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		if(prgmId == null){ // importing other program rules into new program
			
			if(Messagebox.show("Please note that the transactional settings, privilege tiers, bonus rules, validity settings and exclusions " +
					" will be imported from the selected program.\n" +
					" Click OK to continue or Cancel to cancel import of settings.",
					"Confirm" ,Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION) == Messagebox.OK) {
				if(!validateFields()){
					return;
				}
				//first create a new program
				LoyaltyProgram loyaltyProgram=new LoyaltyProgram();
				String prgmName= prgmNameTbId.getValue();
				String description=prgmDescTbId.getValue();
				loyaltyProgram.setProgramName(prgmName);
				loyaltyProgram.setDescription(description);
				loyaltyProgram.setStatus(OCConstants.LOYALTY_PROGRAM_STATUS_DRAFT);
				loyaltyProgram.setCreatedBy(userId.toString());
				loyaltyProgram.setCreatedDate(Calendar.getInstance());
				loyaltyProgram.setUserId(userId);
				loyaltyProgram.setOrgId(ltyPrgmSevice.getOrgId(userId));
				loyaltyProgram.setMembershipType(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD);
				loyaltyProgram.setProgramType(OCConstants.LOYALTY_PROGRAM_TYPE_CARD);
				ltyPrgmSevice.savePrgmObj(loyaltyProgram);
				logger.info("loyaltyProgram====>"+loyaltyProgram.getProgramId());
				session.setAttribute("programId", loyaltyProgram.getProgramId());
				prgmId=(Long) session.getAttribute("programId");

				String status = "";
				String draftStatus = "Complete;=;Incomplete;=;Incomplete;=;Incomplete;=;Incomplete;=;Incomplete";

				Long addFrmPrgmId = addPrgmLbId.getSelectedItem().getValue();
				LoyaltyProgram addFromPrgmObj = ltyPrgmSevice.getProgmObj(addFrmPrgmId);
				String addFromDraftStatus = addFromPrgmObj.getDraftStatus();
				char uniqueMblFlag = addFromPrgmObj.getUniqueMobileFlag();

				String[] draftList = draftStatus.split(Constants.ADDR_COL_DELIMETER);
				String[] addFromDraftList = addFromDraftStatus.split(Constants.ADDR_COL_DELIMETER);
				
				if (addFromPrgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE) && 
						addFromPrgmObj.getTierEnableFlag() == OCConstants.FLAG_NO){
					draftList[1] = OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE;
				}
				else {
					draftList[1] = addFromDraftList[1];
				}

				draftList[0] = addFromDraftList[0];
//				draftList[1] = addFromDraftList[1];
				draftList[3] = addFromDraftList[3];

				draftStatus = "";
				for (String eachStr : draftList) {

					if (draftStatus.isEmpty()) {
						draftStatus = eachStr;
					} else {
						draftStatus += Constants.ADDR_COL_DELIMETER + eachStr;
					}
				}
				status=OCConstants.LOYALTY_PROGRAM_STATUS_DRAFT;
				ltyPrgmTypeRadioGrId.setSelectedItem(cardNoRadioId);
				//import settings
				importSettings(draftStatus,status,uniqueMblFlag);
			}
			else {
				return;
			}

		}	
		else { // importing other program rules into an existing draft program
			if(Messagebox.show("Please note that the transactional settings, privilege tiers, bonus rules, \n validity settings and exclusions of" +
					" the current program will be overridden with the imported program's settings.\n" +
					" Click OK to continue or Cancel to cancel import of settings.",
					"Confirm" ,Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION) == Messagebox.OK) {

				//delete all existing data...
				ltyPrgmSevice.deletePrgmSettings(prgmId);

				LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
				String status = prgmObj.getStatus();
				String draftStatus = prgmObj.getDraftStatus();
				Long addFrmPrgmId = addPrgmLbId.getSelectedItem().getValue();
				LoyaltyProgram addFromPrgmObj = ltyPrgmSevice.getProgmObj(addFrmPrgmId);
				String addFromDraftStatus = addFromPrgmObj.getDraftStatus();
				char uniqueMblFlag = addFromPrgmObj.getUniqueMobileFlag();

				String[] draftList = draftStatus.split(Constants.ADDR_COL_DELIMETER);
				String[] addFromDraftList = addFromDraftStatus.split(Constants.ADDR_COL_DELIMETER);

				if(prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
					uniqueMblFlag = prgmObj.getUniqueMobileFlag();
				}
				
				List<LoyaltyProgramTier> list = ltyPrgmSevice.getTierList(addFrmPrgmId);
				int count = list == null ? 0 : list.size();
				if (addFromPrgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE) && 
						addFromPrgmObj.getTierEnableFlag() == OCConstants.FLAG_NO){
					draftList[1] = OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE;
				}
				else if(prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE) && 
						addFromPrgmObj.getTierEnableFlag() == OCConstants.FLAG_NO && count <1) {
						draftList[1] = OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE;
				}
				else {
					draftList[1] = addFromDraftList[1];
				}

				draftList[0] = addFromDraftList[0];
//				draftList[1] = addFromDraftList[1];
				draftList[3] = addFromDraftList[3];

				draftStatus = "";
				for (String eachStr : draftList) {

					if (draftStatus.isEmpty()) {
						draftStatus = eachStr;
					} else {
						draftStatus += Constants.ADDR_COL_DELIMETER + eachStr;
					}
				}

				//save all the data of the imported prgm...
				importSettings(draftStatus,status,uniqueMblFlag);
			}
			else {
				return;
			}
		}

		Redirect.goTo(PageListEnum.EMPTY);
		Redirect.goTo(PageListEnum.LOYALTY_CREATE_PROGRAM);
		addPrgmLbId.setSelectedIndex(0);
		importBtnId.setVisible(false);
	}//onClick$importBtnId()

	public void importSettings (String draftStatus, String status,char uniqueMblFlag) {
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();

		Long addFrmPrgmId = addPrgmLbId.getSelectedItem().getValue();
		LoyaltyProgram addFromPrgmObj = ltyPrgmSevice.getProgmObj(addFrmPrgmId);
		ltyPrgmSevice.copyPrgmSettings(prgmId,addFromPrgmObj,userId,draftStatus,status,uniqueMblFlag);
		
		MessageUtil.setMessage("Program imported successfully.", "color:blue", "TOP");
		cardNoRadioId.setSelected(true);
	}//importSettings()

	public void onCheck$ltyPrgmTypeRadioGrId() {
		
		if(ltyPrgmTypeRadioGrId.getSelectedItem().getValue().toString().equalsIgnoreCase("mobileBased")) {
			ltyPrgmTypeDivId.setVisible(false);
			enableDefaultChkId.setChecked(false);
			enableUniqueMblNoChkId.setChecked(true);
			dynamicMemTypeDivId.setVisible(false);
			//onCheck$enableUniqueMblNoChkId();
			List<Listitem> items = regReqLbId.getItems();
			Set selItems = new HashSet();
			for (Listitem li : items) {
				if(li.isSelected()) {
					selItems.add(li);
				}
				if(li.getValue().toString().equalsIgnoreCase("mobilePhone")) {
					li.setDisabled(true);
					li.setSelected(true);
					selItems.add(li);
				}
			}
			regReqLbId.setSelectedItems(selItems);
			onSelect$regReqLbId();
		}
		else if(ltyPrgmTypeRadioGrId.getSelectedItem().getValue().toString().equalsIgnoreCase("cardBased")) {
			ltyPrgmTypeDivId.setVisible(true);
			dynamicMemTypeDivId.setVisible(false);
			enableDefaultChkId.setDisabled(false);
			defaultPgmDivId.setVisible(true);
			enableDefaultChkId.setVisible(true);
			
			List<Listitem> items = regReqLbId.getItems();
			for (Listitem li : items) {
				if(li.getValue().toString().equalsIgnoreCase("mobilePhone")) {
					li.setSelected(false);
					if(li.isDisabled()){
						li.setDisabled(false);
					}
				}
			}
			enableUniqueMblNoChkId.setChecked(false);
			onSelect$regReqLbId();
		}else if(ltyPrgmTypeRadioGrId.getSelectedItem().getValue().toString().equalsIgnoreCase("customBased")){
			ltyPrgmTypeDivId.setVisible(true);
			dynamicMemTypeDivId.setVisible(true);
			enableDefaultChkId.setChecked(false);
			enableDefaultChkId.setDisabled(true);
			//enableUniqueMblNoChkId.setChecked(true);
			defaultPgmDivId.setVisible(false);
			enableDefaultChkId.setVisible(false);
			//onCheck$enableUniqueMblNoChkId();
			//onCheck$enableUniqueEmailChkId();
			List<Listitem> items = regReqLbId.getItems();
			for (Listitem li : items) {
				if(li.getValue().toString().equalsIgnoreCase("mobilePhone")) {
					li.setSelected(false);
					if(li.isDisabled()){
						li.setDisabled(false);
					}
				}
			}
			enableUniqueMblNoChkId.setChecked(false);
			onSelect$regReqLbId();
			
		}
	}//onCheck$ltyPrgmTypeRadioGrId()
	
	/*public void onCheck$prgmTypeRadioGrId() {
		
		if(prgmTypeRadioGrId.getSelectedItem().getValue().toString().equalsIgnoreCase("Perk")) {
			
			redemAmtForIssuanceLablDivId.setVisible(false);
			redemAmtForIssuanceChbDivId.setVisible(false);
		} else if(prgmTypeRadioGrId.getSelectedItem().getValue().toString().equalsIgnoreCase("Loyalty")) {
			
			redemAmtForIssuanceLablDivId.setVisible(true);
			redemAmtForIssuanceChbDivId.setVisible(true);
		}
		
	}*/
	
	/*public void onCheck$prgmTypeRadioGrId() {
		
        if(prgmTypeRadioGrId.getSelectedItem().getValue().toString().equalsIgnoreCase("Perk")) {
        	try {
        		addValueCode();
        	} catch (Exception e) {
        		// TODO Auto-generated catch block
        		logger.info("onCheck$prgmTypeRadioGrId error===>"+e);
        	}
		}
	}*/
	
	private void addValueCode() throws Exception {
		
		UserOrganization OrgID=GetUser.getUserObj().getUserOrganization();
		ValueCodesDao valueCodesDao = (ValueCodesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.VALUE_CODES_DAO);
		ValueCodesDaoForDML valueCodesDaoForDML = (ValueCodesDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.VALUE_CODES_DAO_FOR_DML);
		ValueCodes vcode=null;
		
		List<ValueCodes> ValueCodeList =null;
		ValueCodeList = valueCodesDao.findValueCode(OrgID.getUserOrgId(),OCConstants.LOYALTY_TYPE_PERKS);
		if(ValueCodeList.isEmpty()) {
			vcode=new ValueCodes();
			vcode.setCreatedBy(GetUser.getUserObj().getUserId().toString());
			vcode.setCreatedDate(MyCalendar.getInstance());
			vcode.setOrgId(GetUser.getUserObj().getUserOrganization().getUserOrgId());
			vcode.setValuCode("Perks");
			vcode.setDescription("Perks");
			vcode.setModifiedBy(GetUser.getUserObj().getUserId().toString());
			vcode.setModifiedDate(MyCalendar.getInstance());
			valueCodesDaoForDML.saveOrUpdate(vcode);
		}
		
	}

	public void onCheck$enableUniqueMblNoChkId() {/*
		if(enableUniqueMblNoChkId.isChecked()) {
			List<Listitem> items = regReqLbId.getItems();
			Set selItems = new HashSet();
			for (Listitem li : items) {
				if(li.isSelected()) {
					selItems.add(li);
				}
				if(li.getValue().toString().equalsIgnoreCase("mobilePhone")) {
					li.setDisabled(true);
					li.setSelected(true);
					selItems.add(li);
				}
			}
			regReqLbId.setSelectedItems(selItems);
			onSelect$regReqLbId();
		}
		else {
			List<Listitem> items = regReqLbId.getItems();
			for (Listitem li : items) {
				if(li.getValue().toString().equalsIgnoreCase("mobilePhone")) {
					if(li.isDisabled()){
						li.setDisabled(false);
					}
				}
			}
			onSelect$regReqLbId();
		}
	*/}//onCheck$enableUniqueMblNoChkId()

	public void onCheck$enableUniqueEmailChkId() {/*
		if(enableUniqueEmailChkId.isChecked()) {
			List<Listitem> items = regReqLbId.getItems();
			Set selItems = new HashSet();
			for (Listitem li : items) {
				if(li.isSelected()) {
					selItems.add(li);
				}
				if(li.getValue().toString().equalsIgnoreCase("emailId")) {
					li.setDisabled(true);
					li.setSelected(true);
					selItems.add(li);
				}
			}
			regReqLbId.setSelectedItems(selItems);
			onSelect$regReqLbId();
		}
		else {
			List<Listitem> items = regReqLbId.getItems();
			for (Listitem li : items) {
				if(li.getValue().toString().equalsIgnoreCase("emailId")) {
					if(li.isDisabled()){
						li.setDisabled(false);
					}
				}
			}
			onSelect$regReqLbId();
		}
	*/}//onCheck$enableUniqueMblNoChkId()	
	
	public void onSelect$regReqLbId() {
		String selectedFieldstr = "";
		List<Listitem> list = regReqLbId.getItems();
		for(Listitem li : list) {
			if(li.isSelected()) {
				if(selectedFieldstr.trim().length() == 0) {
					selectedFieldstr = li.getLabel();
				}else{
					selectedFieldstr = selectedFieldstr + ", " + li.getLabel(); 
				}
			}
			else if(li.isDisabled() && !li.isSelected()){
				if(selectedFieldstr.trim().length() == 0) {
					selectedFieldstr = li.getLabel();
				}else{
					selectedFieldstr = selectedFieldstr + ", " + li.getLabel(); 
				}
			}
		}
		
		if(selectedFieldstr.trim().length() != 0) {
			requisitesLbId.setVisible(true);
			selectedFieldsLblId.setVisible(true);
			selectedFieldsLblId.setValue(selectedFieldstr);
		}else {
			requisitesLbId.setVisible(false);
			selectedFieldsLblId.setVisible(false);
		}
		
	}//onSelect$regReqLbId()
	
	/*public void onCheck$enableOTPAuthChkId() {
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		if(enableOTPAuthChkId.isChecked()) {
			otpEnabledDivId.setVisible(true);
			if(prgmId != null){
				DecimalFormat f = new DecimalFormat("#0.00");
				LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
				otpAmtLimitTbId.setValue(prgmObj.getOtpLimitAmt() == null ? "" : f.format(prgmObj.getOtpLimitAmt()));
				
			}
			else {
				otpAmtLimitTbId.setValue("");
			}
		}
		else {
			otpEnabledDivId.setVisible(false);
			otpAmtLimitTbId.setValue("");
		}
	}//onCheck$enableTierChkId()*/
	
	/*public boolean validatePercentageRedemption() {//Percentage Redemption

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
	}*/

	public void onClick$saveBtnId() {
		
		boolean isChecked=enableDefaultChkId.isChecked();
		session.setAttribute("isChecked", isChecked);
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		if(!validateFields()){
			return;
		}
		
		String prgmName = prgmNameTbId.getValue().trim();
		if(prgmName == null || prgmName.length() == 0) {
			MessageUtil.setMessage("Program name cannot be empty.", "color:red", "TOP");
			return;
		}
		
		//if(validatePercentageRedemption())//PercentageRedemptionValidation
		//return;
		
		String desc = prgmDescTbId.getValue().trim();
		boolean mobileNoBasedFlag = mobileNoRadioId.isChecked();
		boolean uniqMblEnable = enableUniqueMblNoChkId.isChecked();
		boolean uniqEmailEnable = enableUniqueEmailChkId.isChecked();
		boolean dynamicMemNoBasedFlag = dynamicMemNoRadioId.isChecked();
		//boolean prgmTypeLoyaltyFlag = loyaltyRadioId.isChecked();
		//boolean prgmTypePerkFlag = perkRadioId.isChecked();
		//shall we consider dynamicMemNoBasedFlag while setting status for program ??
		List<LoyaltyProgram> prgmList =  ltyPrgmSevice.getProgList(userId);
		
		if(mobileNoBasedFlag) {
			boolean isEnabled = false;
			// check if the program can be made a mobile based program or not
			if (prgmList != null) {
				if(prgmId == null) {
					for (LoyaltyProgram loyaltyProgram : prgmList) {

						if (loyaltyProgram.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE) ) {
							isEnabled = true;
							break;
						}
					}
					if (isEnabled) {
						MessageUtil.setMessage("This program cannot be made a mobile number based program \n as a mobile number based program already exists.", "color:red", "TOP");
						return;
					} 
				}
				else {
					for (LoyaltyProgram loyaltyProgram : prgmList) {

						if (prgmId.longValue() != loyaltyProgram.getProgramId().longValue() && loyaltyProgram.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE) ) {
							isEnabled = true;
							break;
						}
					}
					if (isEnabled) {
						MessageUtil.setMessage("This program cannot be made a mobile number based program \n as a mobile number based program already exists.", "color:red", "TOP");
						return;
					} 
				}
			}
		}else if(dynamicMemNoBasedFlag){
			boolean isEnabled = false;
			// check if the program can be made a mobile based program or not
			if (prgmList != null) {
				if(prgmId == null) {
					for (LoyaltyProgram loyaltyProgram : prgmList) {

						if (loyaltyProgram.getProgramType().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_TYPE_DYNAMIC) ) {
							isEnabled = true;
							break;
						}
					}
					if (isEnabled) {
						MessageUtil.setMessage("This program cannot be made a custom membership based program \n as a custom membership based program already exists.", "color:red", "TOP");
						return;
					} 
				}
				else {
					for (LoyaltyProgram loyaltyProgram : prgmList) {

						if (prgmId.longValue() != loyaltyProgram.getProgramId().longValue() && loyaltyProgram.getProgramType().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_TYPE_DYNAMIC) ) {
							isEnabled = true;
							break;
						}
					}
					if (isEnabled) {
						MessageUtil.setMessage("This program cannot be made a custom membership based program \n as a custom membership based program already exists.", "color:red", "TOP");
						return;
					} 
				}
			}
			if(lengthTbId.getValue()==null){
				MessageUtil.setMessage("Please provide length for card type.", "color:red", "TOP");
				return;
			}
			
			if(lengthTbId.getValue().intValue() < 4 || lengthTbId.getValue().intValue() > 20){
				MessageUtil.setMessage("Card number length should be between 4 to 20 digits.", "color:red", "TOP");
				return;
			}
			
			
			
		}



		boolean tierEnable =  enableTierChkId.isChecked();
		String noOfTiers = noOfTiersLbId.getSelectedItem().getValue().toString();

		if(prgmId != null) { // Already existing program
			// while decreasing no. of tiers check the highest tier linked with the card-set 
			LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
			int highestLinkedTierLevel = ltyPrgmSevice.findHighestTierLinkedToCardset(prgmId);
			if(Integer.parseInt(noOfTiers) < highestLinkedTierLevel){
				MessageUtil.setMessage("The highest tier linked to one or more card-sets is Tier "+ highestLinkedTierLevel +".\n "
						+ "Your program cannot have less than "+highestLinkedTierLevel+" tiers.", "color:red", "TOP");
				return;
			}
			
			// while decreasing no. of tiers check the highest tier defined
			List<LoyaltyProgramTier> tierList = ltyPrgmSevice.getTierList(prgmId);
			if(prgmObj.getTierEnableFlag() == OCConstants.FLAG_YES && tierList != null) {
				boolean isgreater = false;
				int tierLevel = 1;
				for (LoyaltyProgramTier tierObj : tierList) {
					int tierNo = Integer.parseInt(tierObj.getTierType().replace("Tier ", ""));
					if(tierNo > Integer.parseInt(noOfTiers)){
						isgreater = true;
						tierLevel = tierNo > tierLevel ? tierNo : tierLevel;
					}
				}
				if(isgreater) {
					//MessageUtil.setMessage("The highest tier defined in your program is Tier "+ tierLevel +".\n Please reduce no. of tiers defined on Step 3 before applying this setting.", "color:red", "TOP");
					MessageUtil.setMessage("The highest tier defined in your program is Tier "+ tierLevel +".\n Please reduce no. of tiers defined on Step 2 before applying this setting.", "color:red", "TOP");
					return;
				}

			}
		}

		//Set draft status for second and third steps based on tier enable and mobile based flag.
		String status = "";
		String draftStatus = "";
		if(prgmId == null) {
			status = OCConstants.LOYALTY_PROGRAM_STATUS_DRAFT;
			//if(tierEnable){
				if(mobileNoBasedFlag || dynamicMemNoBasedFlag){
					draftStatus = "Complete;=;Incomplete;=;Complete;=;";
				}
				else {
					draftStatus = "Complete;=;Incomplete;=;Incomplete;=;";
				}
				/*if(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype())){
					draftStatus += "Complete;=;Incomplete;=;Incomplete";
				}else{
					draftStatus += "Incomplete;=;Incomplete;=;Incomplete";
				}*/
				draftStatus += "Incomplete;=;Incomplete;=;Incomplete;=;Incomplete";
			/*}
			else {
				if(mobileNoBasedFlag || dynamicMemNoBasedFlag){
					draftStatus = "Complete;=;Incomplete;=;Complete;=;Incomplete;=;Incomplete;=;Incomplete";
				}
				else {
					draftStatus = "Complete;=;InComplete;=;Incomplete;=;Incomplete;=;Incomplete;=;Incomplete";
				}
			}*/
		}
		else {	// Already existing program
			LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
			draftStatus = prgmObj.getDraftStatus();
			List<LoyaltyProgramTier> list = ltyPrgmSevice.getTierList(prgmId);
			int count = list == null ? 0 : list.size();
			String[] draftList = draftStatus.split(Constants.ADDR_COL_DELIMETER);
			if (tierEnable) {
				if (count < Integer.parseInt(noOfTiers)) {
					draftList[1] = OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE;
				}
				else if(count == Integer.parseInt(noOfTiers) ) {
					draftList[1] = OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE;
				}
			} 
			else {
				if((mobileNoBasedFlag || dynamicMemNoBasedFlag )&& count < 1) {
					draftList[1] = OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE;
				}
				else {
					draftList[1] = OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE;
				}
			}
			if(prgmObj.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_DRAFT) ) {
				if(mobileNoBasedFlag || dynamicMemNoBasedFlag){
					draftList[2] = OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE;
				}
				else {
					boolean existMblFlag = prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE) ? true : false;
					if(existMblFlag) {
						draftList[2] = OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE;
					}
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

			status = prgmObj.getStatus();
		}

		Set<Listitem> regReqSet = new HashSet<Listitem>(regReqLbId.getSelectedItems());
		if(uniqMblEnable || mobileNoBasedFlag) {
			for (Listitem li : regReqLbId.getItems()) {
				if(li.isDisabled() && !li.isSelected()){
					regReqSet.add(li);
				}
			}
		}
		if(uniqEmailEnable) {
			for (Listitem li : regReqLbId.getItems()) {
				if(li.isDisabled() && !li.isSelected()){
					regReqSet.add(li);
				}
			}
		}

		boolean otpEnable = false;
		//boolean otpEnable = enableOTPAuthChkId.isChecked();
		Double otpLimit = null;
		if(otpEnable) {
			otpEnable = isOptedForSMS();
			if(!otpEnable){
				enableOTPAuthChkId.setChecked(false);
				MessageUtil.setMessage("You have not opted for SMS, sending OTP for authentication cannot be enabled.", "color:red", "TOP");
				return;
			}
			if(!otpAmtLimitTbId.getValue().trim().isEmpty()){
				otpLimit = Double.valueOf(otpAmtLimitTbId.getValue());
			}
		}
		/*Double minReceiptAmtRedemValue = null;
		Double minBalanceToRedemValue = null;
		String minBalanceType = minBalanceTypeLbId.getSelectedItem().getValue().toString();
		
		if(!minReceiptAmtValueTbId.getValue().trim().isEmpty() && minReceiptAmtValueTbId.getValue()!=null) {
			try {
				minReceiptAmtRedemValue = Double.valueOf(minReceiptAmtValueTbId.getValue());
			}catch (Exception e) {
				MessageUtil.setMessage("Please provide valid input for minimum receipt amount to redeem .", "color:red", "TOP");
				minReceiptAmtValueTbId.setFocus(true);
				return;
			}
		}
		if(!minBalanceValueTbId.getValue().trim().isEmpty() && minBalanceValueTbId.getValue()!=null) {
			try {
				minBalanceToRedemValue = Double.valueOf(minBalanceValueTbId.getValue());
			}catch (Exception e) {
				MessageUtil.setMessage("Please provide valid input for minimum balance to redeem .", "color:red", "TOP");
				minBalanceValueTbId.setFocus(true);
				return;
			}
		}*/
		
		boolean partialReversalEnable = enablePartialReturnsAuthChkId.isChecked();
		//boolean enableIssuanceEnabled =enableIssuanceDisChkId.isChecked();
		//boolean includeRedeemedAmount = enableRedeemedAmountChkId.isChecked();
		boolean includeRedeemedAmount = false;
		boolean defaultEnable = enableDefaultChkId.isChecked();
		
		
		/*double redemptionPercentageLimit = redemptionPercentageTbId.getValue() != null && !redemptionPercentageTbId.getValue().trim().isEmpty() 
				? Double.parseDouble(Utility.truncateUptoTwoDecimal(redemptionPercentageTbId.getValue().trim())) : 0.0;	
		double redemptionValueLimit = redemptionValueTbId.getValue() != null && !redemptionValueTbId.getValue().trim().isEmpty() 
				? Double.parseDouble(Utility.truncateUptoTwoDecimal(redemptionValueTbId.getValue().trim())) : 0.0;*/
		
		if(mobileNoRadioId.isChecked()){
					defaultEnable=true;
		}
		
		if(prgmTypeRadioGrId.getSelectedItem().getValue().toString().equalsIgnoreCase("Perk")) {
        	try {
        		addValueCode();
        	} catch (Exception e) {
        		// TODO Auto-generated catch block
        		logger.info("onCheck$prgmTypeRadioGrId error===>"+e);
        	}
		}
				
		if(defaultEnable) {
			//check if the program can be made default or not
			//List<LoyaltyProgram> prgmList = ltyPrgmSevice.getProgList(userId);
			LoyaltyProgram ltyPrgmObj = null;
			boolean isEnabled = false;
			if (prgmList != null) {
				if(prgmId != null) {
					for (LoyaltyProgram loyaltyProgram : prgmList) {
						if(prgmId.longValue()!=loyaltyProgram.getProgramId().longValue()) {
							if (loyaltyProgram.getDefaultFlag() == OCConstants.FLAG_YES) {
								isEnabled = true;
								ltyPrgmObj = loyaltyProgram;
								break;
							}
						}
					}
				}
				else {
					for (LoyaltyProgram loyaltyProgram : prgmList) {

						if (loyaltyProgram.getDefaultFlag() == OCConstants.FLAG_YES) {
							isEnabled = true;
							ltyPrgmObj = loyaltyProgram;
							break;
						}
					}
				}
				if (isEnabled) {
					
					if(Messagebox.show("The program  "+ltyPrgmObj.getProgramName()+" is currently default program.Do you want to replace it with this as default program?",
							"Confirm" ,Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION) == Messagebox.OK) {
						defaultEnable = true;
						ltyPrgmObj.setDefaultFlag(OCConstants.FLAG_NO);
						ltyPrgmSevice.savePrgmObj(ltyPrgmObj);
					}
					else {
						return;
					}
				} else {
					defaultEnable = true;
				}
			}
		}

		String loyaltyType = perkRadioId.isChecked() ? "Perk" : "Loyalty";
		String programType = (mobileNoBasedFlag == true ? OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE : (dynamicMemNoBasedFlag ? OCConstants.LOYALTY_PROGRAM_TYPE_DYNAMIC : OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD));
		String vallidationString = 	dynamicMemNoBasedFlag?dynamicMemCardType.getSelectedItem().getValue()+Constants.ADDR_COL_DELIMETER+lengthTbId.getValue():null;
		//Long newPrgmId = ltyPrgmSevice.onSaveProgram(prgmName,desc,mobileNoBasedFlag,tierEnable,noOfTiers,status,defaultEnable,uniqMblEnable,regReqSet,otpEnable,otpLimit,userId,prgmId,draftStatus,vallidationString,programType);
		Long newPrgmId = ltyPrgmSevice.onSaveProgram(prgmName,desc,mobileNoBasedFlag,tierEnable,noOfTiers,status,defaultEnable,uniqMblEnable,uniqEmailEnable,regReqSet,otpEnable,partialReversalEnable,otpLimit,userId,prgmId,draftStatus,vallidationString,loyaltyType,programType,includeRedeemedAmount);
				//,minReceiptAmtRedemValue,minBalanceToRedemValue,minBalanceType);
		
		generateLtyPrgmPwd(userId);
		
		session.setAttribute("programId", newPrgmId);
		prgmId = newPrgmId;
		
		

		
		// set the upgrade constraint of the highest tier to null as tier cannot be upgraded beyond the highest tier.
		LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(newPrgmId);
		List<LoyaltyProgramTier> tierList = ltyPrgmSevice.getTierList(newPrgmId);
		int tiersCount = prgmObj.getNoOfTiers();
		int count=0;
		if(tierList != null){
			for (LoyaltyProgramTier tierObj : tierList) {
			 int tierNo = Integer.parseInt(tierObj.getTierType().replace("Tier ", ""));
				if(tierNo == tiersCount){
					tierObj.setTierUpgdConstraintValue(null);
					ltyPrgmSevice.saveTierObj(tierObj);
				}
				count = tierNo;
			}
					if(count < tiersCount) {
						MessageUtil.setMessage("You've added tier(s) to the current program. Please review tier settings on Step 2 \n to ensure loyalty program completeness.", "color:blue","TOP");
						if(prgmObj.getRewardType()!=null && prgmObj.getRewardType().equalsIgnoreCase(OCConstants.REWARD_TYPE_PERK)) {
							Redirect.goTo(PageListEnum.LOYALTY_PERK_RULES);
							return;
						} else {
							Redirect.goTo(PageListEnum.LOYALTY_RULES);
							return;
						}
					}
				
		}
		
		MessageUtil.setMessage("Program saved successfully.", "color:blue", "TOP");
		
		if(perkRadioId.isChecked())
			Redirect.goTo(PageListEnum.LOYALTY_PERK_RULES);
		else
		   Redirect.goTo(PageListEnum.LOYALTY_RULES);
		//Redirect.goTo(PageListEnum.LOYALTY_RULES);
		/*if(prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE) ) {
			Redirect.goTo(PageListEnum.LOYALTY_RULES);
		}
		else {
			Redirect.goTo(PageListEnum.LOYALTY_ADD_CARDS);
		}*/
	}//onClick$saveBtnId()
	
	
	private void generateLtyPrgmPwd(Long userId) {

		try {
			LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
			LoyaltyThresholdAlerts loyaltyThresholdAlerts = null;
			List<LoyaltyProgram> prgmList = ltyPrgmSevice.getProgList(userId);
			if(prgmList != null && prgmList.size() == 1) {
				loyaltyThresholdAlerts = ltyPrgmSevice.findPwdByUserID(userId);
				if(loyaltyThresholdAlerts != null && (loyaltyThresholdAlerts.getLtySecurityPwd() == null ||
						loyaltyThresholdAlerts.getLtySecurityPwd().isEmpty())) {
					String prgmPwd = RandomStringUtils.randomAlphanumeric(8);  
					String encPrgmPwd = EncryptDecryptLtyMembshpPwd.encryptSessionID(prgmPwd);
					loyaltyThresholdAlerts.setLtySecurityPwd(encPrgmPwd);
					ltyPrgmSevice.saveOrUpdateThresholdAlerts(loyaltyThresholdAlerts);
					sendAlerts(userId,loyaltyThresholdAlerts);
				}
				else if(loyaltyThresholdAlerts == null) {
					String prgmPwd = RandomStringUtils.randomAlphanumeric(8);  
					String encPrgmPwd = EncryptDecryptLtyMembshpPwd.encryptSessionID(prgmPwd);

					loyaltyThresholdAlerts = new LoyaltyThresholdAlerts();
					Long orgId = ltyPrgmSevice.getOrgId(userId);
					loyaltyThresholdAlerts.setOrgId(orgId);
					loyaltyThresholdAlerts.setUserId(userId);
					loyaltyThresholdAlerts.setLtySecurityPwd(encPrgmPwd);
					ltyPrgmSevice.saveOrUpdateThresholdAlerts(loyaltyThresholdAlerts);
					sendAlerts(userId,loyaltyThresholdAlerts);
				}
			}
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
	}

	private void sendAlerts(Long userId, LoyaltyThresholdAlerts loyaltyThresholdAlerts) {
		try{
			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Users user = usersDao.find(userId);

			String message = PropertyUtil.getPropertyValueFromDB("loyaltyProgramEditPwd");
			message = message.replace("[fname]", user.getFirstName() == null ? "" : user.getFirstName());
			message = message.replace("[password]", EncryptDecryptLtyMembshpPwd.decryptProgramPwd(loyaltyThresholdAlerts.getLtySecurityPwd()));
			// Before APP-2005 Changes
//			message = message.replace("[username]", user.getUserName());
			// APP-2005 related changes
			message = message.replace("[username]", Utility.getOnlyUserName(user.getUserName()));
			message = message.replace("[orgid]", user.getUserOrganization().getOrgExternalId());

			String subject = "OptCulture - Your loyalty program's password";

			EmailQueueDao emailQueueDao = (EmailQueueDao) ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
			EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
			EmailQueue emailQueue = new EmailQueue(subject, message, Constants.EQ_TYPE_LOYALTY_EMAIL_ALERTS, Constants.EQ_STATUS_ACTIVE, user.getEmailId(), MyCalendar.getNewCalendar(), user);

			//emailQueueDao.saveOrUpdate(emailQueue);
			emailQueueDaoForDML.saveOrUpdate(emailQueue);
		}catch(Exception e){
			logger.error("Exception in placing loyalty template email in email queue...", e);
		}
	}

	public void onClick$nextBtnId() {
		boolean isChecked=enableDefaultChkId.isChecked();
		session.setAttribute("isChecked", isChecked);
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
		if(prgmObj.getRewardType()!=null && prgmObj.getRewardType().equalsIgnoreCase(OCConstants.REWARD_TYPE_PERK))
			Redirect.goTo(PageListEnum.LOYALTY_PERK_RULES);
		else
		    Redirect.goTo(PageListEnum.LOYALTY_RULES);
		//Redirect.goTo(PageListEnum.LOYALTY_RULES);
		/*LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj((Long) session.getAttribute("programId"));
		if(prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE) ) {
			Redirect.goTo(PageListEnum.LOYALTY_RULES);
		}
		else {
			Redirect.goTo(PageListEnum.LOYALTY_ADD_CARDS);
		}*/
	}//onClick$nextBtnId()

	public boolean isOptedForSMS() {
		Users currentUser = GetUser.getUserObj();

		if(!currentUser.isEnableSMS()) {
			return false;
		}
		return true;
	}//isOptedForSMS()

	private boolean validateFields() {

		if(prgmNameTbId.getValue() == null || prgmNameTbId.getValue().trim().length() == 0) {
			logger.info("name is empty");
			MessageUtil.setMessage("Program name cannot be empty.", "color:red", "TOP");
			prgmNameTbId.setFocus(true);
			return false;
		}
		else if(!Utility.validateBy(Constants.LTY_NAME_PATTERN,prgmNameTbId.getValue())) {
			MessageUtil.setMessage("Enter valid program name.","color:red");
			return false;
		}

		if(prgmNameTbId.getValue().trim().length() > 60) {
			logger.info("value exceeds limit");
			MessageUtil.setMessage("Program name exceeds the maximum characters limit.", "color:red", "TOP");
			return false;
		}

		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		List<LoyaltyProgram> progmList=ltyPrgmSevice.getProgList(userId);

		if (progmList != null) {
			if (prgmId == null) {
				for (LoyaltyProgram loyaltyProgram : progmList) {
					if (loyaltyProgram.getProgramName().equalsIgnoreCase(
							prgmNameTbId.getValue().trim())) {
						MessageUtil.setMessage("Program name already exists.",
								"color:red", "TOP");
						return false;
					}
				}
			} else {
				LoyaltyProgram loyaltyProgram = ltyPrgmSevice
						.getProgmObj(prgmId);
				for (LoyaltyProgram progObj : progmList) {
					if (!progObj.getProgramId().toString().equalsIgnoreCase(loyaltyProgram.getProgramId().toString())) {
						if (progObj.getProgramName().equalsIgnoreCase(prgmNameTbId.getValue().trim())) {
							MessageUtil.setMessage(
									"Program name already exists.",
									"color:red", "TOP");
							return false;
						}
					}
				}
			}
		}
		
		/*if(enableOTPAuthChkId.isChecked() && !otpAmtLimitTbId.getValue().trim().isEmpty()){
			if(!checkIfDouble(otpAmtLimitTbId.getValue().trim())){
				MessageUtil.setMessage("Please provide valid OTP limit.", "color:red", "TOP");
				return false;
			}
			if(Double.parseDouble(otpAmtLimitTbId.getValue().trim()) < 0){
				MessageUtil.setMessage("Please provide valid OTP limit.", "color:red", "TOP");
				return false;
			}
		}*/
		return true;
	}//validateFields()

	public boolean checkTwoDecimalPlaces(String value) {
		logger.info("Value is"+value);
		if(value!=null && !value.isEmpty()&& value.contains(".")) {
		logger.info("Entered check");
		String[] splitter = value.toString().split("\\.");
		int decimalLength = splitter[1].length();  
		if (decimalLength != 2) {	
			return true;
			}
		}
		return false;
	}// checkTwoDecimalPlaces()

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

	
	public boolean checkIfDouble(String in) {
		try {
			Double.parseDouble(in);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}// checkIfNumber()

}

package org.mq.optculture.controller.loyalty;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.AutoSMS;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.DigitalReceiptUserSettings;
import org.mq.marketer.campaign.beans.LoyaltyCardSet;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.ReferralProgram;
import org.mq.marketer.campaign.beans.RewardReferraltype;
import org.mq.marketer.campaign.beans.SpecialReward;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.ValueCodes;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.contacts.SegmentEnum;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.ReferralProgramDaoForDML;
import org.mq.marketer.campaign.dao.RewardReferraltypeDaoForDML;
//import org.mq.marketer.campaign.dao.ReferralProgramDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.loyalty.LoyaltyProgramService;
import org.mq.optculture.data.dao.LoyaltyCardSetDao;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.ReferralProgramDao;
import org.mq.optculture.data.dao.RewardReferraltypeDao;
import org.mq.optculture.data.dao.SpecialRewardsDao;
import org.mq.optculture.data.dao.SpecialRewardsDaoForDML;
import org.mq.optculture.data.dao.ValueCodesDao;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Image;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.jcraft.jsch.Random;


 
public class CreateReferralProgramController extends GenericForwardComposer {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Listbox loyaltyProgramLbId,discountLbId, currencyLbIds,insertCouponLbId,AttrLbId, rewardValueCodeLbId, rewardTypeLbId,refEachGetLbId;
	private Users user;
	private Textbox rewardNameTbId,referralNameTbId, minimumamountTbId,discountvalueTbId,referralvalueTbId;
	private Session session;
	private Div  milestonesDivId,flatDivId,gridDivId,expirydatedivID;
	private Button updateBtnId, draftBtnId, nextdraftBtnId,proceed8BtnId;
	public Label nameStatusLblIdes, deducItemPriceLBId,nameStatusLblId;
	private Listbox autoEmailLbId, autoSMSLbId, excludeQuantityLbId;
	private Checkbox deducItemPriceChkId, enableReturnOnCurrentRuleChkId,NolimitChkBxId;
	private Rows contactRowsId;
	private Div multiplierDivId;

	private Window previewWin;
	private Iframe previewWin$html;
	private Image previewEmailTemplateImgId, editEmailTemplateImgId, previewSmsImgId, editSmsImgId;
	private A previewEmailTemplateBtnId, editEmailTemplateBtnId, previewSmsBtnId, editSmsBtnId;

	private LoyaltyProgramTierDao loyaltyProgramTierDao;
	private LoyaltyCardSetDao loyaltyCardSetDao;
	private CouponsDao couponsDao;


	private ReferralProgramDaoForDML referralProgramDaoForDML;
	
	private RewardReferraltypeDaoForDML rewardreferralTypeDaoForDML;
	
	private LoyaltyProgramDao loyaltyProgramDao;
	private MailingListDao mailingListDao;
	private ValueCodesDao valueCodesDao;
	private OrganizationStoresDao orgStoreDao;
	private MyTemplatesDao myTemplatesDao;
	private SpecialRewardsDaoForDML specialRewardsDaoForDML;
	private SpecialRewardsDao specialRewardsDao;
	
	private ReferralProgramDao referralprogramDao;
	
	private RewardReferraltypeDao rewardreferraltypeDao;
	
	
	private CouponsDao couponDao;
	Radiogroup referralRadioGrId;
	Radio skuRadioId, tpaRadioId;
	private MailingList mappingsList;
	private MyDatebox createDateBxId, expiryDateBxId;
	private ReferralProgram eventSession;
	private RewardReferraltype eventseesionnew;
	
	private Radio flatId, milestoneId;



	ReferralProgram referalprogram = null;
	
	RewardReferraltype rewardreferraltype=null;
	
	public CreateReferralProgramController() {
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		//Utility.refBreadCrumbFrom(1);
	//	PageUtil.setHeader("Create Rew", Constants.STRING_NILL, style, true);
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
	
		user = GetUser.getUserObj();
		loyaltyProgramDao = (LoyaltyProgramDao) SpringUtil.getBean(OCConstants.LOYALTY_PROGRAM_DAO);
		loyaltyProgramTierDao = (LoyaltyProgramTierDao) SpringUtil.getBean(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		loyaltyCardSetDao = (LoyaltyCardSetDao) SpringUtil.getBean(OCConstants.LOYALTY_CARD_SET_DAO);
		
		
		specialRewardsDao = (SpecialRewardsDao) SpringUtil.getBean(OCConstants.SPECIAL_REWARDS_DAO);
	
		referralprogramDao=(ReferralProgramDao) SpringUtil.getBean(OCConstants.REFERRAL_PROGRAM_DAO);
		
		rewardreferraltypeDao=(RewardReferraltypeDao) SpringUtil.getBean(OCConstants.REWARD_REFERRAL_TYPE_DAO);
		
		specialRewardsDaoForDML = (SpecialRewardsDaoForDML) SpringUtil.getBean(OCConstants.SPECIAL_REWARDS_DAO_FOR_DML);
		referralProgramDaoForDML=(ReferralProgramDaoForDML) SpringUtil.getBean(OCConstants.REFERRAL_PROGRAM_DAO_FOR_DML);
		
		rewardreferralTypeDaoForDML=(RewardReferraltypeDaoForDML) SpringUtil.getBean(OCConstants.REWARD_REFERRAL_TYPE_DAO_FOR_DML);
		
		mailingListDao = (MailingListDao) SpringUtil.getBean(OCConstants.MAILINGLIST_DAO);
		valueCodesDao = (ValueCodesDao) SpringUtil.getBean(OCConstants.VALUE_CODES_DAO);
		orgStoreDao = (OrganizationStoresDao) SpringUtil.getBean(OCConstants.ORGANIZATION_STORES_DAO);
		myTemplatesDao = (MyTemplatesDao) SpringUtil.getBean(OCConstants.MYTEMPLATES_DAO);
		couponDao = (CouponsDao) SpringUtil.getBean(OCConstants.COUPONS_DAO);
		
		session = Sessions.getCurrent();
		
		eventSession = (ReferralProgram) session.getAttribute(OCConstants.SESSION_EDIT_REFERRAL);
	//	eventseesionnew=(RewardReferraltype)
		
		logger.info("entering session edit"+eventSession);

		getCoupons();
		//defaultsettingforEreceipt();
		
		if (eventSession != null) {
			try {
				logger.info("entering edit");
				setEventFields(eventSession);
				//defaultsettingforEreceipt1();
		
			
			} catch (WrongValueException e) {
				/**
				 * This exception comes because we are validating using ZK. Note: null or empty
				 * values not allowed in DB. As we have constrained the value from UI. There is
				 * no chance of Null or empty value coming from UI. But if someone tries to
				 * update a null or empty value or change Data-type from backed then this
				 * exception will happen.
				 **/

				logger.info("Wrong Value exception in setEventFields" + e);
			} catch (Exception e) {
				logger.info("setEventFields-->e===>" + e);
			}
		}
		session.removeAttribute(OCConstants.SESSION_EDIT_REFERRAL);
		contactRowsId.setAttribute("editmilestone", false);
		//contactRowsId.setAttribute("rewardvc", 0)	;	

	}
	boolean edit=false;
	
	boolean firstlistbox=false;
	
	boolean addbutton=false;
	private void setEventFields(ReferralProgram refprgrm) {
		
		logger.info("entering setevent fields");
	
		//Rows RowChild = (Rows) session.getAttribute("rowsid");
		// List RowChildList= RowChild.getChildren();	
		 
		 logger.info("RowChildList value");
		referralNameTbId.setValue(refprgrm.getProgramName());
		createDateBxId.setValue(refprgrm.getStartDate());
		//expiryDateBxId.setValue(refprgrm.getEndDate());
	
		boolean nolimit=refprgrm.isNoLimit();
	
		
		//boolean nolimit=refprgrm.getnoLimit();

		if(!nolimit) {
		
			expiryDateBxId.setValue(refprgrm.getEndDate());

		}

		NolimitChkBxId.setChecked(nolimit);
		
		onCheck$NolimitChkBxId();
	
		List<Listitem> otpmessageAutoSmsList = insertCouponLbId.getItems();
		for(Listitem li : otpmessageAutoSmsList) {
			Long otpmessageSmsTmpltId=li.getValue();
		
			if(otpmessageSmsTmpltId != null) {
				if(otpmessageSmsTmpltId.equals(refprgrm.getCouponId())) {
					insertCouponLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}
		}
		
		
	
		
		referralRadioGrId.setSelectedItem(refprgrm.getRewardonReferraltype().equalsIgnoreCase("Milestones") ? milestoneId :  flatId);
		
		if(refprgrm.getRewardonReferraltype().equalsIgnoreCase("Flat")) {
		
			List<Listitem> earnTypeList1=currencyLbIds.getItems();
			for(Listitem li : earnTypeList1) {
				if(refprgrm.getRewardonReferralVC().equalsIgnoreCase(li.getValue().toString())) {
					currencyLbIds.setSelectedItem(li);
					
				}
			}
			
			referralvalueTbId.setValue(refprgrm.getRewardonReferralValue());

			
		}
		
		List RowChildList=contactRowsId.getChildren();

		if(refprgrm.getRewardonReferraltype().equalsIgnoreCase("Milestones")) {


			milestonesDivId.setVisible(true);
			flatDivId.setVisible(false);
			
			firstlistbox=true;
			addbutton=true;
			edit=true;
			 
			List<RewardReferraltype> childList1 = rewardreferraltypeDao.getMilestonesListByRefId(refprgrm.getReferralId());
			Boolean isFirstRow = true;
			for (RewardReferraltype rows : childList1) {
				
				

				logger.info("string value"+rows.getRewardonReferralRepeats() +rows.getRewardonReferralVC() +rows.getRewardonReferralValue() );

	
			
				String currencyOrPoint = rows.getRewardonReferralVC();
				Integer tierSelection = ((currencyOrPoint.equals("Currency")) ? 0 : 1);
							
				logger.info("tierselection is "+tierSelection);
					
				/*for(RewardReferraltype referraltype: childList1) {				
				onClick$addRowBtnId
				onClick$addRowBtnId();
				Row temRow =new Row();
			*/
				/*Row temRow =new Row();
				@SuppressWarnings("rawtypes")
				List chaildLis = temRow.getChildren();
				logger.info("chaildLis value"+chaildLis);
				Textbox referralRewardRepeatsTextbox =new Textbox() ;
				referralRewardRepeatsTextbox.setParent(temRow);
				referralRewardRepeatsTextbox.setValue(rows.getRewardonReferralRepeats());
				Textbox referrslRewardTextbox =new Textbox();
				referrslRewardTextbox.setParent(temRow);
				referrslRewardTextbox.setValue(rows.getRewardonReferralValue());*/

				contactRowsId.setAttribute("repeats",rows.getRewardonReferralRepeats() );
				contactRowsId.setAttribute("rewards", rows.getRewardonReferralValue());
				contactRowsId.setAttribute("rewardvc", tierSelection)	;	
				contactRowsId.setAttribute("editmilestone" ,true);
				contactRowsId.setAttribute("MILESTONE-ROW-ID", rows.getRefId());
				
				if (isFirstRow == true) {
					currencyListItemVisible = true;
					pointListItemVisible = true;
					if(currencyOrPoint == "Points") {
						pointListItemSelected = true;
						onClick$addRowBtnId();
						pointListItemSelected = false;
						currencyListItemVisible = false;
						pointListItemVisible = true;
					} else {
						onClick$addRowBtnId();
					}
				
				} else if(currencyOrPoint.equals("Currency")) {
					currencyListItemVisible = true;
					pointListItemVisible = false;
					onClick$addRowBtnId();
				} else if(currencyOrPoint.equals("Points")) {
					currencyListItemVisible = false;
					pointListItemVisible = true;
					onClick$addRowBtnId();
				} 
				isFirstRow = false;
			
				
	}
		}
	
	}
	public void onChange$createDateBxId() {
		if (createDateBxId.getValue() == null || expiryDateBxId.getValue() == null)
			return;
		Calendar start = createDateBxId.getClientValue();
		Calendar end = expiryDateBxId.getClientValue();

		if (end.before(start)) {
			MessageUtil.setMessage("'From' date cannot be after 'To' date.", "red");
			createDateBxId.setText(Constants.STRING_NILL);
			return;
		}
	}
	
	
	
	
	
	public void onChange$expiryDateBxId() {
		if (createDateBxId.getValue() == null || expiryDateBxId.getValue() == null)
			return;
		Calendar start = createDateBxId.getClientValue();
		Calendar end = expiryDateBxId.getClientValue();

		if (end.before(start)) {
			MessageUtil.setMessage("'From' date cannot be after 'To' date.", "red");
			expiryDateBxId.setText(Constants.STRING_NILL);
			return;
		} 
	}

	
	
	public Calendar getServerDate(MyDatebox toDateboxId) {
		Calendar serverToDateCal = toDateboxId.getServerValue();
		return serverToDateCal;
	}

	
	public void onClick$draftBtnId() {
		//String segRule = "";
	
		if (referalprogram == null && validateName() && nameStatusLblId.getValue().startsWith("Available")) {
			ReferralProgram referralprogram=new ReferralProgram();
		
			referralprogram.setProgramName(referralNameTbId.getValue().toString());
			
			
			if (draftBtnId.getLabel().equalsIgnoreCase("Save As Draft")) {
				referralprogram.setProgramName(referralNameTbId.getValue().toString());
				
				referralprogram.setStatus("Draft");
				referralprogram.setStartDate(getServerDate(createDateBxId));
				referralprogram.setEndDate(getServerDate(expiryDateBxId));
				referralprogram.setUserId(user.getUserId());
			
				
				referralprogram.setOrgId(user.getUserOrganization().getUserOrgId());
				referralprogram.setCreatedDate(Calendar.getInstance());
				referralprogram.setModifiedBy(user.getUserId().toString());
			//	referralprogram.setMinimumAmount(minimumamountTbId.getValue().toString());
			//	referralprogram.setNoLimit(NolimitChkBxId.isChecked());
				
				referralprogram.setRefereeMPV(minimumamountTbId.getValue().toString());
				referralprogram.setDiscountforReferrertype(discountLbId.getSelectedItem().getValue());
				referralprogram.setDiscountforReferrervalue(Double.parseDouble(discountvalueTbId.getValue().toString()));
			
				
			/*	referralprogram.setRewardonReferraltype(referralRadioGrId.getSelectedItem().getValue().toString());
				
				referralprogram.setRewardonReferralVC(currencyLbIds.getSelectedItem().getValue());
				referralprogram.setRewardonReferralValue(referralvalueTbId.getValue().toString()); */
			
				referralProgramDaoForDML.saveOrUpdate(referralprogram);
				MessageUtil.setMessage("Program saved successfully as draft.", "color:green;");
			
				//updateBtnId.setAttribute("SpecialRuleObj", spRewards);
			//	referralNameTbId.setValue("");
			//	descriptionTbId.setValue("");
				//nameStatusLblId.setValue("");
			}
			}
				Redirect.goTo(PageListEnum.LOYALTY_MYREFERAL_PROGRAMS);
	
	}
	
	
	
	
	public void onClick$proceedBtnId() {
		
		String referralName = referralNameTbId.getValue().trim();
	
		
		
		if (referralName == null || referralName.trim().length() == 0 ) {
			MessageUtil.setMessage("Please provide a Referral Name.", "color:red", "TOP");
			referralNameTbId.setFocus(true);
			return;
		}else if(nameStatusLblId.getValue().equalsIgnoreCase("Not Available ")) {
		
			logger.info("entering name condition");
			MessageUtil.setMessage("Referral Name already exist.", "color:red", "TOP");
			return;
		}
		
		List RowChildList =  contactRowsId.getChildren();

		for (Object object : RowChildList) {

			Row temRow = (Row) object;

			@SuppressWarnings("rawtypes")
			List chaildLis = temRow.getChildren();

			Textbox referralRewardRepeatsTextbox = (Textbox) chaildLis.get(1);
			Textbox referrslRewardTextbox = (Textbox) chaildLis.get(5);
			Listbox referralRewardListbox = (Listbox) chaildLis.get(3);
			
			logger.info("referralRewardRepeatsTextbox values is"+referralRewardRepeatsTextbox.getValue());

			logger.info("referrslRewardTextbox values is"+referrslRewardTextbox.getValue());

		
			if(referralRewardRepeatsTextbox.getValue().isEmpty() || referrslRewardTextbox.getValue().isEmpty()) {
				
				MessageUtil.setMessage("Please enter value to continue", "color:red", "TOP");
				return;
			}
			
			try {
				if(Integer.parseInt(referralRewardRepeatsTextbox.getText().toString()) == 0 || Integer.parseInt(referrslRewardTextbox.getText().toString()) == 0 ) {
					MessageUtil.setMessage("Please provide non-zero number.", "color:red", "TOP");
					return;
				}
				if(Integer.parseInt(referralRewardRepeatsTextbox.getText().toString()) < 0 || Integer.parseInt(referrslRewardTextbox.getText().toString()) < 0) {
					MessageUtil.setMessage("Please provide non-negative number.", "color:red", "TOP");
					return;
				}
			}
			catch (NumberFormatException e) {
					MessageUtil.setMessage("Please provide a numeric value.", "color:red", "TOP");
					return;
			}
			
		}
		
		if (eventSession != null) {
			updateEvent(eventSession);
		}
		
		else {
			
			

				//List RowChildList =  contactRowsId.getChildren();
				logger.info("RowChildList "+RowChildList);
				logger.info("RowChildList size "+RowChildList.size());
		
				//	if(RowChildList.size() >= 1) {
				//	for (Object object : RowChildList) {	
				
				boolean nolimit=NolimitChkBxId.isChecked();
				ReferralProgram referralprogram=new ReferralProgram();

		
			if (validateName() && nameStatusLblId.getValue().startsWith("Available")) {
			
					//ReferralProgram referralprogram=new ReferralProgram();
					logger.info("ref program"+referralName);
					referralprogram.setProgramName(referralNameTbId.getValue().toString());
					referralprogram.setStatus("Active");
					referralprogram.setStartDate(getServerDate(createDateBxId));
					if (!NolimitChkBxId.isChecked()) {
						referralprogram.setEndDate(getServerDate(expiryDateBxId));
					}
					referralprogram.setUserId(user.getUserId());
					referralprogram.setOrgId(user.getUserOrganization().getUserOrgId());
					referralprogram.setCreatedDate(Calendar.getInstance());
					referralprogram.setModifiedBy(user.getUserId().toString());
		
				
					referralprogram.setNoLimit(NolimitChkBxId.isChecked());
				
					if(insertCouponLbId.getSelectedItem().getValue()!=null) {
					Long couponId=insertCouponLbId.getSelectedItem().getValue();
					logger.info("coupon  value"+couponId);
					referralprogram.setCouponId(couponId);
					} else {
						MessageUtil.setMessage("Please select Discount code to continue", "color:red", "TOP");
						return;
					}
					
					
					//referralprogram.setRefereeMPV(minimumamountTbId.getValue().toString());
					//referralprogram.setDiscountforReferrertype(discountLbId.getSelectedItem().getValue());
				
					try {	
					//referralprogram.setDiscountforReferrervalue(Double.parseDouble(discountvalueTbId.getValue()));
					}catch(Exception e) {
						logger.info("Exception caught");
					}

					referralprogram.setRewardonReferraltype(referralRadioGrId.getSelectedItem().getValue().toString());

					if(referralRadioGrId.getSelectedItem().getValue().toString().equalsIgnoreCase("Flat")) {
						
					logger.info("entering flat");

					referralprogram.setRewardonReferralVC(currencyLbIds.getSelectedItem().getValue());
					
				
				
					if(referralvalueTbId.getValue().toString()!=null ) {
						try {
							if(Integer.parseInt(referralvalueTbId.getText().toString()) == 0) {
								MessageUtil.setMessage("Please provide non-zero number.", "color:red", "TOP");
								return;
							}
							if(Integer.parseInt(referralvalueTbId.getText().toString()) < 0) {
								MessageUtil.setMessage("Please provide non-negative number.", "color:red", "TOP");
								return;
							}
						}
						catch (NumberFormatException e) {
							MessageUtil.setMessage("Please provide a numeric value.", "color:red", "TOP");
							return;
						}
					
					referralprogram.setRewardonReferralValue(referralvalueTbId.getValue().toString());
					}else {
						
						MessageUtil.setMessage("Please provide Flat value.", "color:red", "TOP");
						return;
					}
					}
					//rewardreferralTypeDaoForDML.deleteProgramIdsByreferralId(referralprogram.getReferralId());
					
					
					
					referralProgramDaoForDML.saveOrUpdate(referralprogram);

					logger.info("referraltype value is"+referralRadioGrId.getSelectedItem().getValue().toString());
					
					if(referralRadioGrId.getSelectedItem().getValue().toString().equalsIgnoreCase("Milestones")) {
						
						logger.info("entering milestones 678");

				
						saveMilestoneMapping(referralprogram);				
						}
						
						MessageUtil.setMessage("Program saved successfully", "color:green;");
						Redirect.goTo(PageListEnum.LOYALTY_MYREFERAL_PROGRAMS);


	
			} else {
		
					MessageUtil.setMessage("Referral Name already exists", "color:red", "TOP");
					return;
					}
		}

		
	}
	
	
	
	private void saveMilestoneMapping(ReferralProgram referralprogram) {
		
		@SuppressWarnings("rawtypes")
		List RowChildList =  contactRowsId.getChildren();
		
		logger.info("RowChildList value"+RowChildList);
		
		
		if(RowChildList != null && RowChildList.size() > 0) {
			
			saveMilestones(RowChildList,referralprogram);
			//saveMilestonesType(RowChildList,referralprogram);
		}
	
	}
	
private void saveMilestoneMappingedit(ReferralProgram referralprogram) {
		
		@SuppressWarnings("rawtypes")
		List RowChildList =  contactRowsId.getChildren();
		
		logger.info("RowChildList value"+RowChildList);
		
		if(RowChildList != null && RowChildList.size() > 0) {
			
			saveMilestonesTypeupdating(RowChildList,referralprogram);
		}
	
	}
	
	
	
	
	
	
	
	private void saveMilestonesType(@SuppressWarnings("rawtypes") List rowList,ReferralProgram referralprogram ) {
		
		logger.info("entering milestone method");

		long count=0;
	
		contactRowsId.setAttribute("childsize", rowList);
		if(rowList.size() >=1) {
			
			for (Object object : rowList) {
				
				RewardReferraltype referraltype=new RewardReferraltype();
				
				boolean isObjModified = false;
				
				Row temRow = (Row)object;
			
				@SuppressWarnings("rawtypes")
				List chaildLis = temRow.getChildren();
				
				logger.info("chaildLis value"+chaildLis);

				Textbox referralRewardRepeatsTextbox = (Textbox) chaildLis.get(1);
			
				Textbox referrslRewardTextbox = (Textbox) chaildLis.get(5);

				Listbox referralRewardListbox= (Listbox) chaildLis.get(3);
				
				logger.info(" value"+referrslRewardTextbox.getValue());

				count=count+1;
				
			
				referraltype.setMilestoneLevel(count);
				
				referraltype.setReferralid(referralprogram.getReferralId());

				referraltype.setRewardonReferraltype(referralRadioGrId.getSelectedItem().getValue().toString());

				referraltype.setRewardonReferralRepeats((referralRewardRepeatsTextbox.getValue()));
				referraltype.setRewardonReferralVC(referralRewardListbox.getSelectedItem().getLabel());
				referraltype.setRewardonReferralValue(referrslRewardTextbox.getValue());
			
			
				logger.info(" value refid"+referralprogram.getReferralId());
			
		
				rewardreferralTypeDaoForDML.saveOrUpdate(referraltype);

				}
			}

	}
	
	
	
	
private void saveMilestones(@SuppressWarnings("rawtypes") List rowList,ReferralProgram referralprogram ) {
		
		logger.info("entering milestone method");

		long count=0;
	
		
		contactRowsId.setAttribute("childsize", rowList);
		
		
		if(rowList.size() >=1) {
			
			for (Object object : rowList) {
				
				RewardReferraltype referraltype=new RewardReferraltype();
				
				boolean isObjModified = false;
				
				Row temRow = (Row)object;
			
				@SuppressWarnings("rawtypes")
				List chaildLis = temRow.getChildren();
				
				logger.info("chaildLis value"+chaildLis);

				Textbox referralRewardRepeatsTextbox = (Textbox) chaildLis.get(1);
			
				Textbox referrslRewardTextbox = (Textbox) chaildLis.get(5);

				Listbox referralRewardListbox= (Listbox) chaildLis.get(3);
				
				logger.info(" value"+referrslRewardTextbox.getValue());

				count=count+1;
				
				referraltype.setMilestoneLevel(count);
				
				referraltype.setReferralid(referralprogram.getReferralId());

				referraltype.setRewardonReferraltype(referralRadioGrId.getSelectedItem().getValue().toString());

				referraltype.setRewardonReferralRepeats(referralRewardRepeatsTextbox.getValue());
				
				
				referraltype.setRewardonReferralVC(referralRewardListbox.getSelectedItem().getLabel());
				referraltype.setRewardonReferralValue(referrslRewardTextbox.getValue());
			
				logger.info(" valueofreferrslRewardTextbox"+referrslRewardTextbox.getValue());
			
				logger.info(" RewardonReferralRepeats"+referralRewardRepeatsTextbox.getValue());

				
				
				logger.info(" value refid"+referralprogram.getReferralId());
			
		
				rewardreferralTypeDaoForDML.saveOrUpdate(referraltype);

				//session.setAttribute("rowsid", contactRowsId);
			}
		}}
	
	
	
	private void saveMilestonesTypeupdating(@SuppressWarnings("rawtypes") List rowList, ReferralProgram referralprogram) {

		logger.info("entering milestone method");
		
		
		referralprogram.setRewardonReferralVC(null);
		referralprogram.setRewardonReferralValue(null);
		referralProgramDaoForDML.saveOrUpdate(referralprogram);

		List<RewardReferraltype> referraltypeList = rewardreferraltypeDao.getMilestonesListByRefId(referralprogram.getReferralId());

		
		if(referraltypeList==null) {
		
			saveMilestonesType(rowList,referralprogram);
			
		}
		if(referraltypeList!=null) {
		for (RewardReferraltype rewardRefObject : referraltypeList) {

			Boolean deleteRow = true;

			for (Object object : rowList) {

				Row temRow = (Row) object;

				@SuppressWarnings("rawtypes")
				List chaildLis = temRow.getChildren();

				Textbox referralRewardRepeatsTextbox = (Textbox) chaildLis.get(1);
				Long milestoneRowId = (Long) referralRewardRepeatsTextbox.getAttribute("MILESTONE-ROW-ID");

				Textbox referrslRewardTextbox = (Textbox) chaildLis.get(5);
				Listbox referralRewardListbox = (Listbox) chaildLis.get(3);

				if (milestoneRowId != null && milestoneRowId >= 1 && milestoneRowId.equals(rewardRefObject.getRefId())) {
					rewardRefObject.setRewardonReferralRepeats(referralRewardRepeatsTextbox.getValue());
					rewardRefObject.setRewardonReferralValue(referrslRewardTextbox.getValue());
					rewardRefObject.setRewardonReferralVC(referralRewardListbox.getSelectedItem().getLabel());
					deleteRow = false;
					rewardreferralTypeDaoForDML.saveOrUpdate(rewardRefObject);
					referralRewardRepeatsTextbox.setAttribute("MILESTONE-ROW-ID", (long) 0);
				} else if (milestoneRowId == null || milestoneRowId == -1) {
					RewardReferraltype referraltype = new RewardReferraltype();
					referraltype.setReferralid(referralprogram.getReferralId());
					referraltype.setRewardonReferraltype(referralRadioGrId.getSelectedItem().getValue().toString());
					referraltype.setRewardonReferralRepeats(referralRewardRepeatsTextbox.getValue());
					referraltype.setRewardonReferralValue(referrslRewardTextbox.getValue());
					referraltype.setRewardonReferralVC(referralRewardListbox.getSelectedItem().getLabel());

					rewardreferralTypeDaoForDML.saveOrUpdate(referraltype);
					referralRewardRepeatsTextbox.setAttribute("MILESTONE-ROW-ID", (long) 0);
				}
			}
			if (deleteRow)
				rewardreferralTypeDaoForDML.deleteRewardReferralByRefId(rewardRefObject.getRefId());
		}

		List<RewardReferraltype> refTypeList = rewardreferraltypeDao.getMilestonesListByRefId(referralprogram.getReferralId());
		long count = 0;
		for (RewardReferraltype refObj : refTypeList) {
			refObj.setMilestoneLevel(++count);
			rewardreferralTypeDaoForDML.saveOrUpdate(refObj);
		}
		
	}
		
		
	}


	
	private void updateEvent(ReferralProgram refprgrm) {
		
			List RowChildList =  contactRowsId.getChildren();

			refprgrm.setProgramName(referralNameTbId.getValue().toString());
			//refprgrm.setStatus("Active");
			refprgrm.setStartDate(getServerDate(createDateBxId));
			refprgrm.setEndDate(getServerDate(expiryDateBxId));
			refprgrm.setUserId(user.getUserId());
			refprgrm.setModifiedBy(user.getUserId().toString());
			refprgrm.setNoLimit(NolimitChkBxId.isChecked());
			refprgrm.setModifiedDate(Calendar.getInstance());
		
				if(insertCouponLbId.getSelectedItem().getValue()!=null) {
				Long couponId=insertCouponLbId.getSelectedItem().getValue();
				logger.info("coupon  value"+couponId);
				refprgrm.setCouponId(couponId);
				} else {
					MessageUtil.setMessage("Please select Discount code to continue", "color:red", "TOP");
					return;
				}
				
			
			//refprgrm.setRefereeMPV(minimumamountTbId.getValue().toString());
			//refprgrm.setDiscountforReferrertype(discountLbId.getSelectedItem().getValue());
		
			try {
			//refprgrm.setDiscountforReferrervalue(Double.parseDouble(discountvalueTbId.getValue()));
	
			}catch(Exception e) {}
			
			
			refprgrm.setRewardonReferraltype(referralRadioGrId.getSelectedItem().getValue().toString());


			if(referralRadioGrId.getSelectedItem().getValue().toString().equalsIgnoreCase(("Flat"))) {
				
			logger.info("entering flat");

			refprgrm.setRewardonReferralVC(currencyLbIds.getSelectedItem().getValue());
			if(referralvalueTbId.getValue().toString()!=null ) {
				try {
					if(Integer.parseInt(referralvalueTbId.getText().toString()) == 0) {
						MessageUtil.setMessage("Please provide non-zero number.", "color:red", "TOP");
						return;
					}
					if(Double.parseDouble(referralvalueTbId.getText().toString()) < 0) {
						MessageUtil.setMessage("Please provide non-negative number.", "color:red", "TOP");
						return;
					}
				}
				catch (NumberFormatException e) {
					MessageUtil.setMessage("Please provide a numeric value.", "color:red", "TOP");
					return;
				}
			
				refprgrm.setRewardonReferralValue(referralvalueTbId.getValue().toString());
			}else {
				
				MessageUtil.setMessage("Please provide Flat value.", "color:red", "TOP");
				return;
			}
			//referralProgramDaoForDML.saveOrUpdate(refprgrm);

			rewardreferralTypeDaoForDML.deleteProgramIdsByreferralId(refprgrm.getReferralId());

			
			}

			
			if(refprgrm.getRewardonReferraltype().equalsIgnoreCase("Milestones")) {
				
				logger.info("entering milestones ");

				saveMilestoneMappingedit(refprgrm);
				//saveMilestoneMapping(refprgrm);				
			}
			referralProgramDaoForDML.saveOrUpdate(refprgrm);
			
			MessageUtil.setMessage("Program saved successfully", "color:green;");
			Redirect.goTo(PageListEnum.LOYALTY_MYREFERAL_PROGRAMS);

			}
	
	public void onCheck$NolimitChkBxId() {

		if(NolimitChkBxId.isChecked()){
		
			expirydatedivID.setVisible(false);
			NolimitChkBxId.isChecked();
		}else {
		
			expirydatedivID.setVisible(true);
			NolimitChkBxId.isDisabled();
		}
	
		}
	
	int count=0;
	
	public void onCheck$referralRadioGrId() {
	
		
		if(referralRadioGrId.getSelectedItem().getValue().toString().equalsIgnoreCase("flat")) {
			
			flatDivId.setVisible(true);
			milestonesDivId.setVisible(false);
		
			
			
		}
		else if(referralRadioGrId.getSelectedItem().getValue().toString().equalsIgnoreCase("milestones")  ) {
			
			milestonesDivId.setVisible(true);
			flatDivId.setVisible(false);
			count=count+1;
			if(count==1 && edit!=true) {
			onClick$addRowBtnIdfirst();
			}
			//((boolean)contactRowsId.getAttribute("editmilestone"))!=true
		}
		
		
	}//onCheck$ltyPrgmTypeRadioGrId()

	
	public void onClick$addRowBtnId() {
		
		
		Boolean miledit=(boolean) contactRowsId.getAttribute("editmilestone");
	
		String repeats="";
		String rewards="";
		//String rewardvc="";
		int rewardvc=0;
		
		if(miledit != null && miledit==true) {
		
			
			repeats=(String) contactRowsId.getAttribute("repeats");
			rewards=(String) contactRowsId.getAttribute("rewards");
			rewardvc=(int) contactRowsId.getAttribute("rewardvc");
		}
		
		logger.info("repeats value "+repeats);
		logger.info("rewards value "+rewards);
		logger.info("rewardvc value "+rewardvc);

		
		gridDivId.setVisible(true);
		Row tempRow = new Row();
		if(firstlistbox) {
		
			Listbox tempListbox1 = null;
			tempListbox1 = refMappingListboxone();
			tempListbox1.addEventListener("onSelect", this);
			tempListbox1.setWidth("90px");
			tempListbox1.setParent(tempRow);
			
			
			
		}else {
	
			Listbox tempListbox = null;
			tempListbox = refMappingListbox();
			tempListbox.addEventListener("onSelect", this);
			tempListbox.setWidth("90px");
			tempListbox.setParent(tempRow);
		}
		firstlistbox=false;
		//textbox1
		Textbox tempTextBox;
		tempTextBox = new Textbox();
		tempTextBox.setWidth("60px");
		if(miledit != null && miledit==true ) {
			tempTextBox.setValue(repeats);
			tempTextBox.setAttribute("MILESTONE-ROW-ID", ((long) contactRowsId.getAttribute("MILESTONE-ROW-ID")));
		} else {
			tempTextBox.setAttribute("MILESTONE-ROW-ID", ((long) -1));
		}
		tempTextBox.setParent(tempRow);
		
		final Label label = new Label();
		label.setValue("References each get");
		label.setParent(tempRow);      
		
		
		
		Listbox tempListbox2 = refprgrmMappingListbox();
		if(miledit != null && miledit==true ) {
			if (pointListItemVisible && !currencyListItemVisible )
				tempListbox2.setSelectedIndex(0);
			else
				tempListbox2.setSelectedIndex(rewardvc);
		}	
		tempListbox2.addEventListener("onSelect", this);
		tempListbox2.setWidth("90px");
		tempListbox2.setParent(tempRow);
		
	
	
		final Label label1 = new Label();
		label1.setValue("Value of");
		label1.setParent(tempRow);
	
		//textbox
		Textbox tempTextBox1;

		tempTextBox1 = new Textbox();
		tempTextBox1.setWidth("60px");
		tempTextBox1.setParent(tempRow);
		
		if(miledit != null && miledit==true ) {
			tempTextBox1.setValue(rewards);
		}	
		
		
		
		Div div=new Div();
		div.setParent(tempRow);

		//Delete Action
		if(addbutton) {
		Image addImg = new Image();
		addImg.setAttribute("TYPE", "ADD_MAPPING");
		addImg.setTooltiptext("Add");
		addImg.setSrc("/images/action_add.gif");
		addImg.setStyle("cursor:pointer; padding-left:40px");
		addImg.addEventListener("onClick", this);
	
		addImg.setParent(div);
		
		}else {
		
		Image delImg = new Image();
		delImg.setAttribute("TYPE", "ROW_MAPPING");

		delImg.setTooltiptext("Delete");
		delImg.setSrc("/images/action_delete.gif");
		delImg.setStyle("cursor:pointer;padding-left:40px");
		delImg.addEventListener("onClick", this);
		delImg.setParent(div);
		}
		addbutton=false;
		/*	Image addImg = new Image();
		addImg.setAttribute("TYPE", "ADD_MAPPING");
		addImg.setTooltiptext("Add");
		addImg.setSrc("/images/action_add.gif");
		addImg.setStyle("cursor:pointer;");
		addImg.addEventListener("onClick", this);
	
		addImg.setParent(div);*/
		tempRow.setParent(contactRowsId);
	}                                       
	
	int value=0;
	public void onClick$addRowBtnIdfirst() {
		
		
		gridDivId.setVisible(true);
		Row tempRow = new Row();
		
		Listbox tempListbox = null;
		tempListbox = refMappingListboxone();
		tempListbox.addEventListener("onSelect", this);
		tempListbox.setWidth("90px");
		tempListbox.setParent(tempRow);
		
	
	
		//textbox1
		Textbox tempTextBox;
		tempTextBox = new Textbox();
		tempTextBox.setWidth("60px");
		tempTextBox.setParent(tempRow);
				
		final Label label = new Label();
		label.setValue("References each get");
		label.setParent(tempRow);      
	
	
	
		tempListbox = refprgrmMappingListbox();
		tempListbox.addEventListener("onSelect", this);
		tempListbox.setWidth("90px");
		tempListbox.setParent(tempRow);
		tempListbox.setId("refEachGetLbId");
		
		
		
		final Label label1 = new Label();
		label1.setValue("Value of");
		label1.setParent(tempRow);
	
		
		
		
		//textbox
		tempTextBox = new Textbox();
		tempTextBox.setWidth("60px");
		tempTextBox.setParent(tempRow);
		
		
		Div div=new Div();
		div.setParent(tempRow);

		Image addImg = new Image();
		addImg.setAttribute("TYPE", "ADD_MAPPING");
		addImg.setTooltiptext("Add");
		addImg.setSrc("/images/action_add.gif");
		addImg.setStyle("cursor:pointer; padding-left:40px");
		addImg.addEventListener("onClick", this);
	
		addImg.setParent(div);
		
		
		
	
		
		tempRow.setParent(contactRowsId);
		
		
		
	}   
	
		public void onSelect$refEachGetLbId(){
				logger.info("onSelect$refEachGetLbId called..");
		}
	
			Boolean pointListItemSelected = false;

			public void onEvent(Event event) throws Exception {

				// TODO Auto-generated method stub
				super.onEvent(event);

				if (event.getTarget() instanceof Listbox) {
					Listbox listBox = (Listbox) event.getTarget();
					Listitem listItem = listBox.getSelectedItem();
					String tierType = (String) listItem.getValue();

					if (tierType != null && tierType.equals("Currency")) {
						List rowList = contactRowsId.getChildren();
						for (int i = rowList.size() - 1; i >= 0; i--) {
							Row row = (Row) rowList.get(i);
							contactRowsId.removeChild(row);
						}
						currencyListItemVisible = true;
						pointListItemVisible = true;
						onClick$addRowBtnIdfirst();
						currencyListItemVisible = true;
						pointListItemVisible = false;
					} else if (tierType != null && tierType.equals("Points")) {
						pointListItemSelected = true;
						List rowList = contactRowsId.getChildren();
						for (int i = rowList.size() - 1; i >= 0; i--) {
							Row row = (Row) rowList.get(i);
							contactRowsId.removeChild(row);
						}
						currencyListItemVisible = true;
						pointListItemVisible = true;
						onClick$addRowBtnIdfirst();
						currencyListItemVisible = false;
						pointListItemVisible = true;
					}
					
					return;
				}

				if (event.getTarget() instanceof Image) {

					Image img = (Image) event.getTarget();
					Row temRow = null;
					String imgAction = (String) img.getAttribute("TYPE");

					logger.info("LOGGER ALERT");

					if ("ALERT_DEL".equals(imgAction)) {
						Div tempDiv = (Div) img.getParent();
						logger.info("ALERT@@");

						logger.info("LOGGER ALERT 1");
						// alertMailDivId.removeChild(tempDiv);
					}
					/*
					 * else if(!imgAction.equals("FTP_SETTINGS")) {
					 * 
					 * temRow = (Row)img.getParent().getParent();
					 * 
					 * logger.info("LOGGER ALERT 2");
					 * 
					 * }
					 */
					if (img.getAttribute("TYPE").equals("ROW_MAPPING")) { // POS CONTACT
						// contactRowsId,,
						temRow = (Row) img.getParent().getParent();
						contactRowsId.removeChild(temRow);
						logger.info("LOGGER ALERT 3");
					}

					else if (img.getAttribute("TYPE").equals("ADD_MAPPING")) {
						if(currencyListItemVisible) {
							currencyListItemVisible = true;
							pointListItemVisible = false;
						}
						if(pointListItemVisible) {
							currencyListItemVisible = false;
							pointListItemVisible = true;
						}
						onClick$addRowBtnId();

					}
				}
			}


	public void  getCoupons() {

		try {
			couponsDao = (CouponsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONS_DAO);
			List<Coupons> couponsList = couponsDao.findrefCouponsByOrgId(user.getUserOrganization().getUserOrgId());

			if(couponsList == null || couponsList.size() == 0) {
				logger.debug("got no coupons for this org");
				return;
			}

			Listitem item  = null;

			for (Coupons coupons : couponsList) {

				item = new Listitem(coupons.getCouponName(), coupons.getCouponId());
				item.setParent(insertCouponLbId);

			}
		}
		catch(Exception e) {
			logger.error("Exception ::" , e);
		}

	}//getCoupons
	

	
	
	private void defaultsettingforEreceipt(){

		logger.debug("---Entered---");
		try {
			couponsDao = (CouponsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONS_DAO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Coupons> couponsList = couponsDao.findrefCouponsByOrgId(user.getUserOrganization().getUserOrgId());
		List<Listitem> listboxcouponsList = insertCouponLbId.getItems();
		logger.info("listboxcouponsList items"+listboxcouponsList);

		for(Listitem li : listboxcouponsList) {
		
			Long discouponId=li.getValue();
			
			logger.info("discouponId value"+discouponId);
			
		//	logger.info(" couponsList value"+couponsList.getCouponId());


			if(discouponId != null) {
				if(discouponId.equals(((Coupons) couponsList).getCouponId())) {
					insertCouponLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}
		}
		logger.debug("---Exit---");
	}
	
	
	
	private void defaultsettingforEreceipt1(){

		logger.debug("---Entered setOtpMessageAutoSmsValues---");
		//Long tempid =	digitalReceiptUserSettings.getSmsTmpltId();
		
		try {
			couponsDao = (CouponsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONS_DAO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Coupons couponsList = couponsDao.findrefCouponsByOrgId2(user.getUserOrganization().getUserOrgId());
		
		List<Listitem> otpmessageAutoSmsList = insertCouponLbId.getItems();
		for(Listitem li : otpmessageAutoSmsList) {
			Long otpmessageSmsTmpltId=li.getValue();
		
			if(otpmessageSmsTmpltId != null) {
				if(otpmessageSmsTmpltId.equals(couponsList.getCouponId())) {
					insertCouponLbId.setSelectedItem(li);
					li.setSelected(true);
					break;
				}
			}
		}
		logger.debug("---Exit setOtpMessageAutoSmsValues---");
	}
	
	
	Boolean currencyListItemVisible = true;
	Boolean pointListItemVisible = true;
	private Listbox refprgrmMappingListbox() {

		Listbox dateFormatListBx = new Listbox();
//			contactGenFieldList = null;
		Listitem tempItem = null;
		
		if (currencyListItemVisible) {
			tempItem = new Listitem("Currency");
			tempItem.setValue("Currency");
			tempItem.setParent(dateFormatListBx);
		}

		if (pointListItemVisible) {
			tempItem = new Listitem("Points");
			tempItem.setValue("Points");
			tempItem.setParent(dateFormatListBx);
		}
		
		if(currencyListItemVisible && pointListItemVisible && pointListItemSelected)
			dateFormatListBx.setSelectedIndex(1);
		else
			dateFormatListBx.setSelectedIndex(0);
		//genralFieldList
		
		dateFormatListBx.setMold("select");
		return dateFormatListBx;
		
	}// createCo
	
	private Listbox refMappingListbox() {

		Listbox dateFormatListBx = new Listbox();
//			contactGenFieldList = null;
		Listitem tempItem = null;
		
		tempItem = new Listitem("Next");
		tempItem.setParent(dateFormatListBx);
		
	
		
		dateFormatListBx.setSelectedIndex(0);
		//genralFieldList
		
		dateFormatListBx.setMold("select");
		return dateFormatListBx;
		
	}// createCo
	
	private Listbox refMappingListboxone() {

		Listbox dateFormatListBx = new Listbox();
//			contactGenFieldList = null;
		Listitem tempItem = null;
		
		tempItem = new Listitem("First");
		tempItem.setParent(dateFormatListBx);
		
	
		
		dateFormatListBx.setSelectedIndex(0);
		//genralFieldList
		
		dateFormatListBx.setMold("select");
		return dateFormatListBx;
		
	}// createCo
	
	private boolean validateName() {
		String referralName = referralNameTbId.getValue().trim();
		if (referralName == null || referralName.trim().length() == 0) {
			MessageUtil.setMessage("Please provide a Referral Name.", "color:red", "TOP");
			referralNameTbId.setFocus(true);
			return false;
		}

		return true;

	}

	public void onBlur$referralNameTbId() {
		String ruleName = referralNameTbId.getValue().trim();
		nameStatusLblId.setValue("");
		if (ruleName.length() > 0 && !Utility.validateName(ruleName)) {
			nameStatusLblId.setStyle("color:red");
			nameStatusLblId.setValue("Special characters not allowed.");
			return;
		}
		
		if (eventSession == null) {
		
		if (ruleName.length() > 0) {
			
			//need to make changes
			List<ReferralProgram> list = referralprogramDao.findRewarRuleByName(ruleName, user.getUserId());
		
			if (list == null || list.size() == 0) {
				nameStatusLblId.setStyle("color:#023849");
				nameStatusLblId.setValue("Available");
				return;
			} else {
				nameStatusLblId.setStyle("color:red");
				nameStatusLblId.setValue("Not Available");
				return;
			}
		}
	
		}
		
	
	}

	

	public void onClick$nextdraftBtnId() {
		SpecialReward spRewards = (SpecialReward) updateBtnId.getAttribute("SpecialRuleObj");
		if (spRewards != null) {
		//	if (nextdraftBtnId.getLabel().contains("Suspend"))
			//	saveRule(spRewards, "Suspended");
		//	else if (nextdraftBtnId.getLabel().contains("Save As Draft"))
			//	saveRule(spRewards, "Draft");
		}
	}


	
	
}

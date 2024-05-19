package org.mq.optculture.controller.loyalty;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.AutoSMS;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.LoyaltyCardSet;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.POSMapping;
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
import org.mq.optculture.data.dao.SpecialRewardsDao;
import org.mq.optculture.data.dao.SpecialRewardsDaoForDML;
import org.mq.optculture.data.dao.ValueCodesDao;
import org.mq.optculture.utils.OCConstants;
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
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

public class CreateSpecialRewardsController extends GenericForwardComposer {
 
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Listbox loyaltyProgramLbId, AttrLbId, rewardValueCodeLbId, rewardTypeLbId, RewardExpiryTypeLbId,
			rewardExpiryTypeValueLbId, tierLbId;
	private Users user;
	private Textbox rewardNameTbId, descriptionTbId, valueTbId,IgnoreItemQuantityTbId,issuanceWindowTextBox;
	private Session session;
	private Div AttributeDivId, specialRuleFirstDivId, specialRulesecondDivID;
	private Button updateBtnId,draftBtnId,nextdraftBtnId;
	private Label nameStatusLblId,deducItemPriceLBId;
	private Listbox autoEmailLbId, autoSMSLbId,excludeQuantityLbId;
	private Checkbox deducItemPriceChkId,enableReturnOnCurrentRuleChkId;
	private Div multiplierDivId;
	private Window previewWin;
	private Iframe previewWin$html;
	private Image previewEmailTemplateImgId, editEmailTemplateImgId, previewSmsImgId, editSmsImgId;
	private A previewEmailTemplateBtnId, editEmailTemplateBtnId, previewSmsBtnId, editSmsBtnId;
	

	private LoyaltyProgramTierDao loyaltyProgramTierDao;
	private LoyaltyCardSetDao loyaltyCardSetDao;
	private SpecialRewardsDaoForDML specialRewardsDaoForDML;
	private SpecialRewardsDao specialRewardsDao;
	private LoyaltyProgramDao loyaltyProgramDao;
	private MailingListDao mailingListDao;
	private ValueCodesDao valueCodesDao;
	private OrganizationStoresDao orgStoreDao;
	private MyTemplatesDao myTemplatesDao;
	private CouponsDao couponDao;
	
	private MailingList mappingsList;
	private Map<Integer, String> dayMap = new HashMap<>();
	SpecialReward specialReward = null;
	
	public CreateSpecialRewardsController() {
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Create Reward", Constants.STRING_NILL, style, true);
	}
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		user = GetUser.getUserObj();
		loyaltyProgramDao = (LoyaltyProgramDao) SpringUtil.getBean(OCConstants.LOYALTY_PROGRAM_DAO);
		loyaltyProgramTierDao = (LoyaltyProgramTierDao) SpringUtil.getBean(OCConstants.LOYALTY_PROGRAM_TIER_DAO); 
		loyaltyCardSetDao = (LoyaltyCardSetDao) SpringUtil.getBean(OCConstants.LOYALTY_CARD_SET_DAO);
		specialRewardsDao = (SpecialRewardsDao) SpringUtil.getBean(OCConstants.SPECIAL_REWARDS_DAO);
		specialRewardsDaoForDML = (SpecialRewardsDaoForDML) SpringUtil.getBean(OCConstants.SPECIAL_REWARDS_DAO_FOR_DML);
		mailingListDao = (MailingListDao) SpringUtil.getBean(OCConstants.MAILINGLIST_DAO);
		valueCodesDao = (ValueCodesDao) SpringUtil.getBean(OCConstants.VALUE_CODES_DAO);
		orgStoreDao = (OrganizationStoresDao) SpringUtil.getBean(OCConstants.ORGANIZATION_STORES_DAO);
		myTemplatesDao = (MyTemplatesDao) SpringUtil.getBean(OCConstants.MYTEMPLATES_DAO);
		couponDao = (CouponsDao) SpringUtil.getBean(OCConstants.COUPONS_DAO);
		session = Sessions.getCurrent();
		dayMap.put(1, "Sunday");
		dayMap.put(2, "Monday");
		dayMap.put(3, "Tuesday");
		dayMap.put(4, "Wednesday");
		dayMap.put(5, "Thursday");
		dayMap.put(6, "Friday");
		dayMap.put(7, "Saturday");
		//APP-1990

		mappingsList = mailingListDao.findPOSMailingList(user);
		if (mappingsList == null) {
			mappingsList = mailingListDao.findUserBCRMList(user);
			if (mappingsList == null)
				MessageUtil.setMessage("Please frist create POS List.", "color:red");
			return;
		}
		List<LoyaltyProgram> programsList = loyaltyProgramDao.getAllProgramsListByUserId(GetUser.getUserId());
		if (programsList == null) {
			MessageUtil.setMessage("Please first create a Loyalty Program to create a Reward.", "color:red");
			return;
		}

		SetItemsToBeDragged(1, AttrLbId);
		setNextpageData();
		rewardNameTbId.setValue("");
		rewardNameTbId.setDisabled(false);
		descriptionTbId.setValue("");
		specialReward = (SpecialReward) session.getAttribute("editRewardRule");
		draftBtnId.setVisible(true);
		if (specialReward != null) {
			rewardNameTbId.setValue(specialReward.getRewardName());
			issuanceWindowTextBox.setValue(specialReward.getIssuanceWindow());
			rewardNameTbId.setDisabled(true);
			descriptionTbId.setValue(specialReward.getDescription());
			deleteExistedRulesDiv(AttributeDivId);
			createDivForEdit(specialReward.getRewardRule());
			session.removeAttribute("editRewardRule");
			nextdraftBtnId.setLabel("Suspend Reward");
			updateBtnId.setLabel("Save & Exit");
			draftBtnId.setLabel("Save");
		}
	}

	public void SetItemsToBeDragged(int category, Listbox targetLb) {
		Listitem item = null;
		Components.removeAllChildren(targetLb);
		List<SpecialRewardEnum> arrAttr = SpecialRewardEnum.getEnumsByCategoryCode(category);
		for (SpecialRewardEnum attrListItem : arrAttr) {
			item = new Listitem(attrListItem.getDispLabel(), attrListItem);
			item.setDraggable("outerDivDropable");
			item.setStyle("padding:10px;");
			item.setAttribute("DBcolumnName", attrListItem.getColumnName());
			item.setAttribute("item", attrListItem.getItem());
			item.setId(attrListItem.getItem());
			item.setParent(targetLb);
		}
	}

	private void setNextpageData() {
		Listitem li = null;
		List<LoyaltyProgram> programsList = loyaltyProgramDao.getAllProgramsListByUserId(GetUser.getUserId());
		for (LoyaltyProgram loyaltyProgram : programsList) {
			li = new Listitem(loyaltyProgram.getProgramName());
			li.setParent(loyaltyProgramLbId);
			li.setValue(loyaltyProgram);
		}
		List<ValueCodes> valueCodes = valueCodesDao.findNonFBPValueCodes(user.getUserOrganization().getUserOrgId());
		if (valueCodes != null && valueCodes.size() > 0) {
			valueCodes.forEach(code -> {
				Listitem item = new Listitem(code.getValuCode());
				item.setValue(code.getValuCode());
				item.setParent(rewardValueCodeLbId);
			});
			rewardValueCodeLbId.setSelectedIndex(0);
		}
		onSelect$RewardExpiryTypeLbId();
		setAutoEmailTemplate(autoEmailLbId, OCConstants.SPECIALREWARDS);
		setAutoSmsTemplateList(autoSMSLbId, OCConstants.SPECIALREWARDS);
	}

	public void onSelect$autoEmailLbId() {
		if (autoEmailLbId.getSelectedItem().getLabel().toString().equalsIgnoreCase("Select Auto Email")) {
			previewEmailTemplateImgId.setVisible(false);
			editEmailTemplateImgId.setVisible(false);
			previewEmailTemplateBtnId.setVisible(false);
			editEmailTemplateBtnId.setVisible(false);
		} else {
			previewEmailTemplateImgId.setVisible(true);
			editEmailTemplateImgId.setVisible(true);
			previewEmailTemplateBtnId.setVisible(true);
			editEmailTemplateBtnId.setVisible(true);
		}
	}

	public void onSelect$autoSMSLbId() {
		if (autoSMSLbId.getSelectedItem().getLabel().toString().equalsIgnoreCase("Select Auto SMS")) {
			previewSmsImgId.setVisible(false);
			editSmsImgId.setVisible(false);
			previewSmsBtnId.setVisible(false);
			editSmsBtnId.setVisible(false);
		} else {
			previewSmsImgId.setVisible(true);
			editSmsImgId.setVisible(true);
			previewSmsBtnId.setVisible(true);
			editSmsBtnId.setVisible(true);
		}
	}

	private void setAllDataForEdit(SpecialReward spreward) {
		
		setFlatOrMultiplier(spreward.getRewardRule());
		
		List<Listitem> items = rewardTypeLbId.getItems();
		items.forEach(item -> {
			if (item.getValue().equals(spreward.getRewardType()))
				item.setSelected(true);
		});
		
		items = rewardValueCodeLbId.getItems();
		items.forEach(item -> {
			
			
			logger.info("Item" +item.getValue());
			logger.info("String" +spreward.getRewardValueCode());
			if (item.getValue() != null &&  spreward.getRewardValueCode() != null && 
					(item.getValue().equals(spreward.getRewardValueCode()) || (item.getValue().equals(OCConstants.LOYALTY_EXPIRY_TYPE_AMOUNT) &&  spreward.getRewardValueCode().equals(OCConstants.LOYALTY_TYPE_CURRENCY)) )) {
				
				item.setSelected(true);
			}
		});
		onSelect$rewardValueCodeLbId();
		if(rewardTypeLbId.getSelectedItem().getValue().equals("M")) {
			rewardValueCodeLbId.setVisible(false);
			tierLbId.setVisible(false);
			onSelect$rewardTypeLbId();
		}
		valueTbId.setValue(spreward.getRewardValue());
		items = excludeQuantityLbId.getItems();
		
		items.forEach(item -> {			
			//logger.info("item==>"+item);
			//logger.info("item.getValue()==>"+item.getValue());
			//logger.info("3===>"+((Coupons)item.getValue()).getCouponCode());
			if(item!=null && item.getValue() != null && ((Coupons)item.getValue()).getCouponCode()!=null 
					&& spreward!=null && ((Coupons)item.getValue()).getCouponCode().toString().equals(spreward.getPromoCode())) {
				logger.info("entered");
				logger.info("item==>"+item.getValue().toString());
				item.setSelected(true);
			}
		});
		
		items = RewardExpiryTypeLbId.getItems();
		items.forEach(item -> {
			if (item.getValue() != null && item.getValue().equals(spreward.getRewardExpiryType()))
				item.setSelected(true);
		});
		items = rewardExpiryTypeValueLbId.getItems();
		items.forEach(item -> {
			if (item.getValue() != null && item.getValue().toString().equals(spreward.getRewardExpiryValue()))
				item.setSelected(true);
		});
		items = loyaltyProgramLbId.getItems();
		List<LoyaltyProgram> pgrms = specialRewardsDao.findByRewardId(specialReward.getRewardId());
		if(pgrms!=null && pgrms.size()>0){
		for (LoyaltyProgram program : pgrms) {
			items.forEach(item -> {
				if (item.getValue() != null
						&& ((LoyaltyProgram) item.getValue()).getProgramId().equals(program.getProgramId()))
					item.setSelected(true);
			});
		 }
		}

		if (rewardTypeLbId.getSelectedItem().getValue().equals("T")) {
			setTierData();
			setTierDataOnEdit(spreward);
			rewardValueCodeLbId.setVisible(false);
			valueTbId.setVisible(false);
			tierLbId.setVisible(true);
		}

		items = autoEmailLbId.getItems();
		items.forEach(item -> {
			if (item.getValue() != null && item.getValue().toString().equals(spreward.getAutoCommEmail()))
				item.setSelected(true);
		});
		items = autoSMSLbId.getItems();
		items.forEach(item -> {
			if (item.getValue() != null && item.getValue().toString().equals(spreward.getAutoCommSMS()))
				item.setSelected(true);
		});
		if(deducItemPriceChkId!=null)
		deducItemPriceChkId.setChecked(spreward.isDeductItemPrice()!=null?spreward.isDeductItemPrice():false);
		enableReturnOnCurrentRuleChkId.setChecked(spreward.getEnableReturnOnCurrentRule()!=null?spreward.getEnableReturnOnCurrentRule():false);
		if (rewardTypeLbId.getSelectedItem().getValue().equals("M")) {
			deducItemPriceChkId.setVisible(false);
			deducItemPriceLBId.setVisible(false);
		}else {
			deducItemPriceChkId.setVisible(true);
			deducItemPriceLBId.setVisible(true);
		}
		if(spreward.getExcludeQty()!=null) {
			IgnoreItemQuantityTbId.setValue(spreward.getExcludeQty()+"");
		}
		
		 onSelect$autoEmailLbId();
		 onSelect$autoSMSLbId();
	}
	public void onSelect$rewardValueCodeLbId(){
	    valueTbId.setValue("");
			    
	    for(int count = excludeQuantityLbId.getItemCount(); count>0; count--){
	    	excludeQuantityLbId.removeItemAt(count-1);
		}
	    
	    List<Coupons> couponList = couponDao.findCouponForValueCode(GetUser.getUserId(),
				rewardValueCodeLbId.getSelectedItem().getValue());	
		logger.info("coupon==>"+couponList);
		Listitem li = null;
		li = new Listitem("Select Item ");
		li.setParent(excludeQuantityLbId);
		excludeQuantityLbId.setDisabled(false);
		IgnoreItemQuantityTbId.setDisabled(false);
		if(couponList != null && !couponList.isEmpty())
		for (Coupons coupon : couponList) {
			//Only to show coupons when coupon.getPurchaseQty()==null
			if(coupon.getPurchaseQty()==null) {
			li = new Listitem(coupon.getCouponCode());
			li.setParent(excludeQuantityLbId);
			li.setValue(coupon);
			}
		}
		
	}
	
	public void onClick$draftBtnId(){
		String segRule="";
		if (specialReward==null && validateName() && nameStatusLblId.getValue().startsWith("Available")) {
			SpecialReward spRewards = new SpecialReward();
			spRewards.setRewardName(rewardNameTbId.getValue().toString());
			spRewards.setDescription(descriptionTbId.getValue().toString());
			segRule = saveRule();
			logger.info("Reward Rule::" + segRule);
			if (segRule != null)
				spRewards.setRewardRule(segRule);
			else return;
			spRewards.setOrgId(user.getUserOrganization().getUserOrgId() + "");
			spRewards.setCreatedBy(user.getUserId() + "");
			spRewards.setCreatedDate(Calendar.getInstance());
			if(draftBtnId.getLabel().equalsIgnoreCase("Save As Draft")){
			spRewards.setStatusSpecialReward("Draft");
		updateBtnId.setAttribute("SpecialRuleObj", spRewards);
		specialReward=spRewards;
		specialRewardsDaoForDML.saveOrUpdate(spRewards);
		MessageUtil.setMessage("Reward saved successfully as draft.", "color:green;");
		rewardNameTbId.setValue("");
		descriptionTbId.setValue("");
		nameStatusLblId.setValue(""); 
		deleteExistedRulesDiv(AttributeDivId);
			}
			
		}
		else if (specialReward != null) {
			SpecialReward spRewards = specialReward;
			spRewards.setDescription(descriptionTbId.getValue().toString());
			segRule = saveRule();
			if (segRule != null)
				spRewards.setRewardRule(segRule);
			else
				return;
			updateBtnId.setAttribute("SpecialRuleObj", spRewards);
			specialRewardsDaoForDML.saveOrUpdate(spRewards);	
			MessageUtil.setMessage("Reward  saved successfully.", "color:green;");
		}
		
	}

	public void onClick$proceedBtnId() {
		String segRule = null;
		if (specialReward != null) {
			SpecialReward spRewards = specialReward;
			spRewards.setDescription(descriptionTbId.getValue().toString());
			segRule = saveRule();
			if (segRule != null)
				spRewards.setRewardRule(segRule);
			else
				return;
			updateBtnId.setAttribute("SpecialRuleObj", spRewards);
			specialRuleFirstDivId.setVisible(false);
			specialRulesecondDivID.setVisible(true);
			setAllDataForEdit(specialReward);
		} else {
			if (validateName() && nameStatusLblId.getValue().startsWith("Available")) {
				SpecialReward spRewards = new SpecialReward();
				spRewards.setRewardName(rewardNameTbId.getValue().toString());
				spRewards.setDescription(descriptionTbId.getValue().toString());
				segRule = saveRule();
				logger.info("Reward Rule::" + segRule);
				if (segRule != null)
					spRewards.setRewardRule(segRule);
				else
					return;
				spRewards.setOrgId(user.getUserOrganization().getUserOrgId() + "");
				spRewards.setCreatedBy(user.getUserId() + "");
				spRewards.setCreatedDate(Calendar.getInstance());
				spRewards.setStatusSpecialReward("Draft");
				specialRewardsDaoForDML.saveOrUpdate(spRewards);
				updateBtnId.setAttribute("SpecialRuleObj", spRewards);
				specialReward=spRewards;
				specialRuleFirstDivId.setVisible(false);
				specialRulesecondDivID.setVisible(true);
				setFlatOrMultiplier(segRule);
				setAllDataForEdit(specialReward);
			}
			
		}
		List<Listitem> list = loyaltyProgramLbId.getItems();
		if (segRule != null && (segRule.contains("[#PurchaseTier#]") || segRule.contains("[#PurchaseCardSet#]"))) {
			String[] rowsArr = segRule.split("\\|\\|");
			String ruleStr = "";
			loyaltyProgramLbId.setSelectedIndex(-1);
			for (int i = 0; i < rowsArr.length; i++) {
				ruleStr = rowsArr[i];
				if (ruleStr.contains("<OR>")) {
					String[] orRuleTokensArr = ruleStr.split("<OR>");
					for (int j = 0; j < orRuleTokensArr.length; j++) {
						String[] tokenArr = orRuleTokensArr[j].split(";=;");
						if (tokenArr[0].equals("[#PurchaseTier#]") || tokenArr[0].equals("[#PurchaseCardSet#]"))
							list.forEach(item -> {
								if (item.getValue() != null && ((LoyaltyProgram) item.getValue()).getProgramId()
										.toString().equals(tokenArr[2]))
									item.setSelected(true);
									item.setDisabled(true);
									loyaltyProgramLbId.setStyle("pointer-events: none;");
							});
					}
				}
			}
		} else {
			list.forEach(item -> {
				item.setDisabled(false);
			});
		}

	}

	private void setFlatOrMultiplier(String segRule) {
		Listitem item = null;
		
		String[] subRuleTokenArr = segRule.split("<OR>");
		boolean isMultipleAcross =false;
		
		multiplierDivId.setVisible(true);
		rewardValueCodeLbId.setVisible(false);
		Components.removeAllChildren(rewardTypeLbId);
		
		for(String subRule : subRuleTokenArr) {
			String subRuleAttributes[] = subRule.split(";=;");
			if(subRuleAttributes[0].equals(OCConstants.SP_RULE_ATTR_ITEM_FACTOR) 
					&& subRuleAttributes[5].equals(OCConstants.SP_RULE_ATTR_ACCROSS_MULTIPLE_PURCHASE)) {
				isMultipleAcross =true;
			}
		}
		
		
		if (segRule.contains("[#PurchasedItem#]") || segRule.contains("[#Visit#]")|| segRule.contains("#PuchaseDate#") || (segRule.contains("#Minimum Receipt Total#"))) {
			item = new Listitem("Flat-Value", "F");
			item.setParent(rewardTypeLbId);
			multiplierDivId.setVisible(false);
			rewardValueCodeLbId.setVisible(true);
		}
		
		if (segRule.contains("[#PurchasedItem#]") || segRule.contains("[#Visit#]")|| segRule.contains("#PuchaseDate#") || (segRule.contains("#Minimum Receipt Total#"))) {
			item = new Listitem("Percentage", "P");
			item.setParent(rewardTypeLbId);
			multiplierDivId.setVisible(false);
			rewardValueCodeLbId.setVisible(true);
		}
		
		if (segRule.contains("[#ItemFactor#]") || segRule.contains("[#PurchasedItem#]") || segRule.contains("[#Visit#]")|| segRule.contains("#PuchaseDate#") || (segRule.contains("#Minimum Receipt Total#"))) {
			item = new Listitem("Tier", "T");
			item.setParent(rewardTypeLbId);
		}
		tierLbId.setVisible(false);
		
		if(!isMultipleAcross)
		item = new Listitem("Multiplier", "M");
		
		if (!segRule.contains("[#Visit#]"))
			item.setParent(rewardTypeLbId);
		rewardTypeLbId.setSelectedIndex(0);
	}

	public void onSelect$rewardTypeLbId() {
		if (rewardTypeLbId.getSelectedItem().getValue().equals("M")) {
			rewardValueCodeLbId.setVisible(false);
			multiplierDivId.setVisible(true);
			deducItemPriceChkId.setDisabled(true);
			deducItemPriceChkId.setVisible(false);
			deducItemPriceLBId.setVisible(false);
			deducItemPriceChkId.setChecked(false);
			excludeQuantityLbId.setDisabled(true);
			IgnoreItemQuantityTbId.setDisabled(true);
			tierLbId.setVisible(false);
			valueTbId.setVisible(true);
		} else if (rewardTypeLbId.getSelectedItem().getValue().equals("T")) {
			setTierData();
			rewardValueCodeLbId.setVisible(false);
			multiplierDivId.setVisible(false);
			deducItemPriceChkId.setDisabled(false);
			deducItemPriceChkId.setVisible(true);
			deducItemPriceLBId.setVisible(true);
			excludeQuantityLbId.setDisabled(false);
			IgnoreItemQuantityTbId.setDisabled(false);
			tierLbId.setVisible(true);
			valueTbId.setVisible(false);
		} else {
			rewardValueCodeLbId.setVisible(true);
			multiplierDivId.setVisible(false);
			deducItemPriceChkId.setDisabled(false);
			deducItemPriceChkId.setVisible(true);
			deducItemPriceLBId.setVisible(true);
			excludeQuantityLbId.setDisabled(false);
			IgnoreItemQuantityTbId.setDisabled(false);
			tierLbId.setVisible(false);
			valueTbId.setVisible(true);
		}
		valueTbId.setValue("");
	}

	private boolean validateName() {
		String ruleName = rewardNameTbId.getValue().trim();
		if (ruleName == null || ruleName.trim().length() == 0) {
			MessageUtil.setMessage("Please provide a Reward Name.", "color:red", "TOP");
			rewardNameTbId.setFocus(true);
			return false;
		}

		return true;

	}

	public void onBlur$rewardNameTbId() {
		String ruleName = rewardNameTbId.getValue().trim();
		nameStatusLblId.setValue("");
		if (ruleName.length() > 0 && !Utility.validateName(ruleName)) {
			nameStatusLblId.setStyle("color:red");
			nameStatusLblId.setValue("Special characters not allowed.");
			return;
		}
		if(ruleName.length()>0){
		List<SpecialReward> list = specialRewardsDao.findRewarRuleByName(ruleName, user.getUserId());
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

	public void onClick$updateBtnId() {
		SpecialReward spRewards = (SpecialReward) updateBtnId.getAttribute("SpecialRuleObj");
		if (spRewards != null)
			saveRule(spRewards,"Active");
		
		
	}
	public void onClick$nextdraftBtnId(){
		SpecialReward spRewards = (SpecialReward) updateBtnId.getAttribute("SpecialRuleObj");
		if (spRewards != null){
			if(nextdraftBtnId.getLabel().contains("Suspend"))
			saveRule(spRewards,"Suspended");
			else if(nextdraftBtnId.getLabel().contains("Save As Draft"))
				saveRule(spRewards,"Draft");
		}
	}

	public void saveRule(SpecialReward spRewards,String status) {
		String value = "";
		if (rewardValueCodeLbId.isVisible() && rewardValueCodeLbId.getSelectedIndex() == 0
				&& rewardValueCodeLbId.getSelectedItem().getValue() == null) {
			MessageUtil.setMessage("Please select Reward Type.", "color:red", "TOP");
			return;
		}
		if (valueTbId.isVisible() && (valueTbId.getText() == null || valueTbId.getText().trim().length() == 0)) {
			MessageUtil.setMessage("Please provide value for Reward Type.", "color:red", "TOP");
			return;
		}
		Set<Listitem> selectedLytPrgms = loyaltyProgramLbId.getSelectedItems();
		if (rewardTypeLbId.getSelectedItem().getValue().equals("T") && selectedLytPrgms.isEmpty()) {
			MessageUtil.setMessage("Please select atleast one loyalty program.", "color:red", "TOP");
			return;
		}
		if (tierLbId.isVisible() && (tierLbId.getSelectedItem() == null || tierLbId.getSelectedItem().getLabel().equals("Select Tier"))) {
			MessageUtil.setMessage("Please select Tier for Reward Type.", "color:red", "TOP");
			return;
		}

		//APP-2293
		try {
			logger.info("rewardTypeLbId.getSelectedItem().getValue().equals()==>"+rewardTypeLbId.getSelectedItem().getValue().equals("M"));
			if((rewardValueCodeLbId!=null && rewardValueCodeLbId.isVisible() && rewardValueCodeLbId.getSelectedItem()!=null
					&& rewardValueCodeLbId.getSelectedItem().getValue() !=null 
					&& rewardValueCodeLbId.getSelectedItem().getValue().toString().equals(OCConstants.LOYALTY_TYPE_CURRENCY))
					|| rewardTypeLbId.getSelectedItem().getValue().equals("M")) {
				if(Double.parseDouble(valueTbId.getText().toString()) == 0) {
					MessageUtil.setMessage("Please provide non-zero number.", "color:red", "TOP");
					return;
				}
				if(Double.parseDouble(valueTbId.getText().toString()) < 0) {
					MessageUtil.setMessage("Please provide non-negative number.", "color:red", "TOP");
					return;
				}
				value = Double.parseDouble(valueTbId.getText().toString())+Constants.STRING_NILL;	
			}
			else if (tierLbId.isVisible() && rewardTypeLbId.getSelectedItem().getValue().equals("T")) {
				// Empty
			}
			else {
				if(Integer.parseInt(valueTbId.getText().toString()) == 0) {
					MessageUtil.setMessage("Please provide non-zero number.", "color:red", "TOP");
					return;
				}
				if(Integer.parseInt(valueTbId.getText().toString()) < 0) {
					MessageUtil.setMessage("Please provide non-negative number.", "color:red", "TOP");
					return;
				}
					value = Integer.parseInt(valueTbId.getText().toString())+Constants.STRING_NILL;
			}

		}
		catch (NumberFormatException e) {
			MessageUtil.setMessage("Please provide a numeric value.", "color:red", "TOP");
			return;
		}
		
		try {
			
			if(excludeQuantityLbId.getSelectedIndex()>0) {
				if(IgnoreItemQuantityTbId.getValue().toString().equals(Constants.STRING_NILL)) {
					MessageUtil.setMessage("Excluded Item qunatity cannot be empty", "color:red", "TOP");
					return;
				}
				Double.parseDouble(IgnoreItemQuantityTbId.getValue().toString().trim());
			}
			
		}
		catch(NumberFormatException e) {
			MessageUtil.setMessage("Please provide decimal value for Excluded Item qunatity", "color:red", "TOP");
			return;
		}
		
		
		Set<LoyaltyProgram> programIds = getAllSelecteProgram();
		if (programIds == null)
			return;
		spRewards.setRewardType(rewardTypeLbId.getSelectedItem().getValue());
		String rewardType = rewardValueCodeLbId.isVisible() ? rewardValueCodeLbId.getSelectedItem().getValue().toString() : "";
		if (tierLbId.isVisible() && rewardTypeLbId.getSelectedItem().getValue().equals("T")) {
			rewardType = tierLbId.getSelectedItem().getLabel();
			value = tierLbId.getSelectedItem().getValue().toString();
		}
		spRewards.setRewardValueCode(rewardType.equals(OCConstants.LOYALTY_TYPE_CURRENCY)?OCConstants.LOYALTY_TYPE_AMOUNT:rewardType);
		spRewards.setRewardValue(Utility.truncateUptoTwoDecimal(value));
		spRewards.setDeductItemPrice(deducItemPriceChkId.isChecked());
		spRewards.setRewardExpiryType(RewardExpiryTypeLbId.getSelectedItem().getValue().toString());
		spRewards.setRewardExpiryValue(rewardExpiryTypeValueLbId.getSelectedItem().getValue().toString());
		spRewards.setAutoCommEmail(autoEmailLbId.getSelectedItem().getValue()!=null? autoEmailLbId.getSelectedItem().getValue().toString():null);
		spRewards.setAutoCommSMS(autoSMSLbId.getSelectedItem().getValue()!=null?autoSMSLbId.getSelectedItem().getValue().toString():null);
		spRewards.setLoyaltyPrograms(programIds);
		spRewards.setStatusSpecialReward(status);
		spRewards.setExcludeQty(IgnoreItemQuantityTbId.isDisabled() ||IgnoreItemQuantityTbId.getValue().toString().trim().isEmpty()?null:Double.parseDouble(IgnoreItemQuantityTbId.getValue()
				.toString().trim()));
		spRewards.setPromoCode(excludeQuantityLbId.getSelectedIndex()>0?
				((Coupons)excludeQuantityLbId.getSelectedItem().getValue()).getCouponCode():null);
		spRewards.setPromoCodeName(excludeQuantityLbId.getSelectedIndex()>0?
				((Coupons)excludeQuantityLbId.getSelectedItem().getValue()).getCouponName():null);
		spRewards.setEnableReturnOnCurrentRule(enableReturnOnCurrentRuleChkId.isChecked());
		spRewards.setIssuanceWindow(issuanceWindowTextBox.getValue()!=null ? issuanceWindowTextBox.getValue() :"");
		String msg="";
		if(status.contains("Draft"))
			msg="Are you sure you want to save as draft?";
		else if(status.contains("Suspended"))
			msg="Are you sure you want to suspend the reward?";
		else 
			msg="Are you sure you want to save the reward?";
		try {
			int confirm = Messagebox.show(msg, "Special Reward",
					Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if (confirm ==1) {
				specialRewardsDaoForDML.saveOrUpdate(spRewards);
				updateBtnId.removeAttribute("SpecialRuleObj");
				MessageUtil.setMessage("Reward  saved  successfully.", "color:green;");
				Redirect.goTo(PageListEnum.LOYALTY_MY_SPECIAL_REWARDS);
			}

		} catch (Exception e) {
			logger.error("Exception ::", e);
			return;
		}
		
		
		
		
		
	}
	

	private void setAutoEmailTemplate(Listbox emailLbid, String type) {
		List<CustomTemplates> templatelist = null;
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		templatelist = ltyPrgmSevice.getTemplateList(user.getUserId(), type);
		Components.removeAllChildren(emailLbid);
		Listitem item = null;
		item = new Listitem("Select Auto Email", null);
		item.setParent(emailLbid);
		if (templatelist != null && templatelist.size() > 0) {
			for (CustomTemplates customTemplates : templatelist) {
				item = new Listitem(customTemplates.getTemplateName(), customTemplates.getTemplateId());
				item.setParent(emailLbid);
			}
		}
		if (emailLbid.getItemCount() > 0)
			emailLbid.setSelectedIndex(0);
	}

	private void setAutoSmsTemplateList(Listbox smsLbId, String type) {
		logger.debug("---Entered getLoyaltySmsTemplateList---");
		List<AutoSMS> smsTemplateList = null;
		try {
			LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
			smsTemplateList = ltyPrgmSevice.getSmsTemplateList(user.getUserId(), type);
			Components.removeAllChildren(smsLbId);
			Listitem item = null;
			item = new Listitem("Select Auto SMS", null);
			item.setParent(smsLbId);
			item = new Listitem("Default Message", new Long(-1));
			item.setParent(smsLbId);
			if (smsTemplateList != null && smsTemplateList.size() > 0) {
				for (AutoSMS autoSMS : smsTemplateList) {
					item = new Listitem(autoSMS.getTemplateName(), autoSMS.getAutoSmsId());
					item.setParent(smsLbId);
				}
			}
			if (smsLbId.getItemCount() > 0)
				smsLbId.setSelectedIndex(0);

		} catch (Exception e) {
			logger.error(" - ** Exception to get the Loyalty template List - " + e + " **");
		}
		logger.debug("---Exit getLoyaltySmsTemplateList----");
	}// getLoyaltySmsTemplateList()

	public void onSelect$RewardExpiryTypeLbId() {
		String value = RewardExpiryTypeLbId.getSelectedItem().getValue();
		int count = 1;
		if (value.equals("Month"))
			count = 12;
		else
			count = 10;
		Components.removeAllChildren(rewardExpiryTypeValueLbId);
		for (int i = 1; i <= count; i++) {
			Listitem item = new Listitem(i + "");
			item.setValue(i);
			item.setParent(rewardExpiryTypeValueLbId);
		}
		rewardExpiryTypeValueLbId.setSelectedIndex(0);
	}

	public void onClick$backBtnId() {
		specialRuleFirstDivId.setVisible(true);
		specialRulesecondDivID.setVisible(false);
	}

	public StringBuilder SegRuleFormation() {
		List<Component> allChildren = AttributeDivId.getChildren();
		logger.info("allChildren===>" + allChildren);
		for (Component div : allChildren) {

			if (div instanceof Div) {
				logger.info("div===>" + Components.getVisibleChildren(div));
				Collection<Component> allInternalComp = Components.getVisibleChildren(div);
				for (Component intrnComp : allInternalComp) {
					if (intrnComp instanceof Div) {
						Div intrnCompDiv = (Div) intrnComp;
						Collection<Component> allInternalDiv = Components.getVisibleChildren(intrnCompDiv);
						logger.info("allInternalDiv===>" + allInternalDiv);
					}

				}
			}
		}

		return null;

	}

	boolean flag = true;
	public void onDrop$AttributeDivId(Event event) {

		logger.debug("---ControllerSide -- Drop Event =" + event);

		DropEvent dropEvent = ((DropEvent) ((ForwardEvent) event).getOrigin());
		Component dragged = dropEvent.getDragged();

		logger.debug("----------Dragged target =" + dragged);

		if (dragged instanceof Listitem) {
			// new object
			Listitem item = (Listitem) dragged;
			// final Listitem itemForChild = item;
			SpecialRewardEnum segmentEnum = (SpecialRewardEnum) item.getValue();
			List<Component> list = AttributeDivId.getChildren();
			if (list.size() > 1) {
				for (Component comp : list) {
					if (comp.getLastChild() instanceof Label)
						continue;
					Div div = (Div) comp.getLastChild().getFirstChild();
					String draggedEnum = (String) div.getAttribute("Type");
					List<Component> innerlist= comp.getLastChild().getChildren();
					for(Component innercomp:innerlist){
						if(innercomp instanceof Div){
							Div innerDiv=(Div) innercomp;
							String innerEnum=(String) innerDiv.getAttribute("Type");
						 if (segmentEnum.name().equals(SpecialRewardEnum.PURCHASE_IN_STORE.name())
								 || segmentEnum.name().equals(SpecialRewardEnum.PURCHASE_IN_TIER.name())
								 || segmentEnum.name().equals(SpecialRewardEnum.PURCHASE_IN_CARD_SET.name())){
						   if(innerEnum!=null && (innerEnum.equals(segmentEnum.name())|| draggedEnum.equals(segmentEnum.name()))){
							   MessageUtil.setMessage("This attribute can't be clubbed with AND conjuntion.", "color:red");
								return;
						   }
						 }
						 else if(segmentEnum.name().equals(SpecialRewardEnum.VISITS.name())){
							 if(innerEnum!=null && (innerEnum.equals(segmentEnum.name())|| draggedEnum.equals(segmentEnum.name()))){
							 MessageUtil.setMessage("This attribute can't be clubbed with AND conjuntion.", "color:red");
								return; 
							 }
						 }
						/* if (segmentEnum.name().equals(SpecialRewardEnum.PURCHASE_IN_TIER.name())
									|| segmentEnum.name().equals(SpecialRewardEnum.PURCHASE_IN_CARD_SET.name())) 
							if (innerEnum!=null &&(innerEnum.equals(SpecialRewardEnum.PURCHASE_IN_TIER.name())||
									innerEnum.equals(SpecialRewardEnum.PURCHASE_IN_CARD_SET.name())
									||draggedEnum.equals(SpecialRewardEnum.PURCHASE_IN_TIER.name())
									|| draggedEnum.equals(SpecialRewardEnum.PURCHASE_IN_CARD_SET.name()))) {
									MessageUtil.setMessage("This attribute can't be clubbed with AND conjuntion.", "color:red");
									return;
							}
						}*/
					  }
					}
					
					/*if (draggedEnum.equals(SpecialRewardEnum.PURCHASE_IN_STORE.name()))
						if (segmentEnum.name().equals(SpecialRewardEnum.PURCHASE_IN_STORE.name())) {
							MessageUtil.setMessage("This attribute can't clubbed with AND conjuntion.", "color:red");
							return;
						}*/
					/*if (draggedEnum.equals(SpecialRewardEnum.PURCHASE_IN_TIER.name())
							|| draggedEnum.equals(SpecialRewardEnum.PURCHASE_IN_CARD_SET.name())) 
						if (segmentEnum.name().equals(SpecialRewardEnum.PURCHASE_IN_TIER.name())||
								segmentEnum.name().equals(SpecialRewardEnum.PURCHASE_IN_CARD_SET.name())) {
							MessageUtil.setMessage("This attribute can't clubbed with AND conjuntion.", "color:red");
							return;
					}*/
				}
			}
			if (flag && segmentEnum.name().equals(SpecialRewardEnum.ITEM_PURCHASE_ATTRIBUTE.name())) {
				createSubDivRule();
				flag = false;
			}

			Div tempDiv = new Div();
			tempDiv.setSclass("");
			tempDiv.setParent(AttributeDivId);

			Label AndLabel = new Label("AND");
			AndLabel.setStyle("display:inline-block; width:20px; padding:0 5px;margin:0 10px;font-weight:bold;");
			AndLabel.setParent(tempDiv);

			Div vdiv = new Div();// this is the outer component
			vdiv.setDroppable("outerDivDropable");
			vdiv.setParent(tempDiv);
			vdiv.setSclass("segment_parent_div");
			updateAndLabel(AttributeDivId);
			Div obj = createNewRuleDiv(item);
			obj.setParent(vdiv);
			updateORLabel(vdiv);

			// Generate Drop Div
			if (segmentEnum.name().equals(SpecialRewardEnum.ITEM_PURCHASE_ATTRIBUTE.name())
					|| segmentEnum.name().equals(SpecialRewardEnum.VISITS.name()))
				vdiv.setDroppable("notDraggable");
			else
				vdiv.appendChild(createDropORDiv("Drag Attribute(s) here to create OR combination rule."));

			vdiv.addEventListener("onDrop", myDropListener);
		}

	} // onDrop$programdesignerWinId

	public void updateAndLabel(Component parentDiv) {

		logger.info("in update AND label ::" + parentDiv.getId());
		List<Component> divComp = parentDiv.getChildren();

		for (Component eachDiv : divComp) {

			logger.info("each CLASS=" + eachDiv.getClass());
			if (eachDiv instanceof Div) {

				if (((Div) eachDiv).getSclass() == null) {

					Component label = eachDiv.getFirstChild();
					logger.info("each CLASS=" + label.getClass());

					if (label != null && label instanceof Label) {

						((Label) label).setValue("");
					}

					break;

				}

				else if (((Div) eachDiv).getSclass().contains("drop_"))
					continue;

			}
		} // for

	}

	private MyDropEventListener myDropListener = new MyDropEventListener();

	private class MyDropEventListener implements EventListener {

		public MyDropEventListener() {
		}

		@Override
		public void onEvent(Event event) throws Exception {
			DropEvent dropEvent = (DropEvent) event;
			Component dragged = dropEvent.getDragged();
			if (dragged instanceof Listitem) {
				Listitem item = (Listitem) dragged;
				Div obj = (Div) event.getTarget(); 
				Div testDiv = (Div) obj.getFirstChild();
				SpecialRewardEnum dragEnum = (SpecialRewardEnum) item.getValue();
				 if (dragEnum.name().equals(SpecialRewardEnum.VISITS.name())|| dragEnum.name().equals(SpecialRewardEnum.ITEM_PURCHASE_ATTRIBUTE.name())){
						   MessageUtil.setMessage("This attribute can't be dragged with OR conjuntion.", "color:red");
							return;
			       }
				List<Component> list = AttributeDivId.getChildren();
				 boolean isStoreDragged=false;
				 boolean isTierDragged=false;
				 boolean iscardSetDragged =false;
				 boolean isPurchaseDateDragged=false;
				if (list.size() > 1) {
					for (Component comp : list) {
						if (comp.getLastChild() instanceof Label)
							continue;
						Div div = (Div) comp.getLastChild().getFirstChild();
						String outerDraggedEnum = (String) div.getAttribute("Type");
					
							if(outerDraggedEnum!=null && outerDraggedEnum.equals(SpecialRewardEnum.PURCHASE_IN_STORE.name())){
							isStoreDragged=true;
						}
						else if(outerDraggedEnum!=null && (outerDraggedEnum.equals(SpecialRewardEnum.PURCHASE_IN_TIER.name()))){ 
							isTierDragged=true;
						}
						else if(outerDraggedEnum!=null && (outerDraggedEnum.equals(SpecialRewardEnum.PURCHASE_IN_CARD_SET.name()))){ 
							iscardSetDragged=true;
						}
						else if(outerDraggedEnum!=null && (outerDraggedEnum.equals(SpecialRewardEnum.PURCHASE_DATE.name()))){ 
							isPurchaseDateDragged=true;
						}
						
					}
				}
					
				if (isStoreDragged && dragEnum.name().equals(SpecialRewardEnum.PURCHASE_IN_STORE.name())) {
					if (testDiv.getAttribute("Type") != null
							&& testDiv.getAttribute("Type").toString().equals(dragEnum.name())) {
						Div div = createNewRuleDiv(item);// elements div
						div.setParent(obj);
					} else {
						MessageUtil.setMessage("This attribute can't be clubbed with OR conjuntion.", "color:red");
						return;
					}
				} else if ( isTierDragged && (dragEnum.name().equals(SpecialRewardEnum.PURCHASE_IN_TIER.name()))) {
					if (testDiv.getAttribute("Type") != null && 
							testDiv.getAttribute("Type").toString().equals(SpecialRewardEnum.PURCHASE_IN_TIER.name())) {
						Div div = createNewRuleDiv(item);// elements div
						div.setParent(obj);
					} else {
						MessageUtil.setMessage("This attribute can't be clubbed with OR conjuntion.", "color:red");
						return;
					}
				}
				else if ( iscardSetDragged && (dragEnum.name().equals(SpecialRewardEnum.PURCHASE_IN_CARD_SET.name()))) {
					if (testDiv.getAttribute("Type") != null
							&& testDiv.getAttribute("Type").toString().equals(SpecialRewardEnum.PURCHASE_IN_CARD_SET.name())) {
						Div div = createNewRuleDiv(item);// elements div
						div.setParent(obj);
					} else {
						MessageUtil.setMessage("This attribute can't be clubbed with OR conjuntion.", "color:red");
						return;
					}
				}
				else if (isPurchaseDateDragged && (dragEnum.name().equals(SpecialRewardEnum.PURCHASE_DATE.name()))) {
					if (testDiv.getAttribute("Type") != null
							&& testDiv.getAttribute("Type").toString().equals(SpecialRewardEnum.PURCHASE_DATE.name())) {
						Div div = createNewRuleDiv(item);// elements div
						div.setParent(obj);
					} else {
						MessageUtil.setMessage("This attribute can't be clubbed with OR conjuntion.", "color:red");
						return;
					}
				}
				else {
					MessageUtil.setMessage("This attribute can't be clubbed with OR conjuntion.", "color:red");
					return;
				}
				}

		}// onEvent()

	}// class

	public void updateORLabel(Component vdiv) {

		logger.info("SCLASS=" + ((Div) vdiv).getSclass());
		List<Component> divComp = vdiv.getChildren();

		for (Component eachDiv : divComp) {

			logger.info("each CLASS=" + eachDiv.getClass());

			if (((Div) eachDiv).getSclass().contains("drop_"))
				continue;

			Component label = eachDiv.getFirstChild();

			if (label != null && label instanceof Label) {
				((Label) label).setValue("");
			}

			break;
		} // for

	} // updateORLabel

	public Div createNewRuleDiv(Listitem item) {
		SpecialRewardEnum segmentEnum = (SpecialRewardEnum) item.getValue();
		return getCreatedCompDiv(segmentEnum, item);
	}

	private Div createDropORDiv(String dragMsg) {
		Div dropDiv = new Div();
		dropDiv.setSclass("drop_or_div");
		Label orLbl = new Label(" OR ");
		orLbl.setStyle("left: -80px; position: absolute;");
		dropDiv.appendChild(orLbl);
		dropDiv.appendChild(new Label(dragMsg));

		return dropDiv;
	}

	int count = 0;
	@SuppressWarnings("unchecked")
	public Div getCreatedCompDiv(SpecialRewardEnum segmentEnum, Listitem item) {
		List<Listitem> itemList = AttrLbId.getItems();
		if (segmentEnum.name().equals(SpecialRewardEnum.VISITS.name())) {
			itemList.forEach(itm -> {
				SpecialRewardEnum spenum = itm.getValue();
				if (spenum.name().equals(SpecialRewardEnum.ITEM_PURCHASE_ATTRIBUTE.name()))
					itm.setDraggable("notdrag");
			});
			count++;
		} else if (segmentEnum.name().equals(SpecialRewardEnum.ITEM_PURCHASE_ATTRIBUTE.name())) {
			itemList.forEach(itm -> {
				SpecialRewardEnum spenum = itm.getValue();
				if (spenum.name().equals(SpecialRewardEnum.VISITS.name()))
					itm.setDraggable("notdrag");
			});
			count++;
		}
		Listitem li = null;// for rulelb
		Div testDiv = new Div();
		testDiv.setSclass("segment_child_div");
		testDiv.setAttribute("Type", segmentEnum.name());
		Label orlbl = new Label(" OR ");// or label-----0
		orlbl.setStyle("display:inline-block; width:20px; padding:0 5px;margin:0 10px;font-weight:bold;");
		orlbl.setParent(testDiv);

		Label lbl = new Label();// display label-----1
		lbl.setStyle("padding:0 5px; display:inline-block; width:200px;");

		lbl.setParent(testDiv);
		lbl.setValue(item.getLabel());
		lbl.setAttribute("columnName", (String) item.getAttribute("DBcolumnName"));
		lbl.setAttribute("item", (String) item.getAttribute("item"));

		Listbox rulelb = new Listbox();// rule lb----2
		rulelb.setMold("select");
		rulelb.setStyle("margin-right:5px; width:200px;");
		rulelb.setParent(testDiv);

		Label lbl1 = new Label();
		lbl1.setWidth("20px");
		lbl1.setParent(testDiv);

		final Textbox tb = new Textbox();// string type value--------6
		tb.setStyle("margin-right:5px;");
		tb.setWidth("100px");
		tb.setVisible(false);
		tb.setParent(testDiv);

		if (segmentEnum.name().equals(SpecialRewardEnum.ITEM_PURCHASE_QUANTIY.name())) {
			tb.setVisible(true);
			lbl1.setValue(" is ");
		}

		rulelb.setAttribute("isTier", false);
		if (segmentEnum.name().equals(SpecialRewardEnum.PURCHASE_IN_TIER.name()))
			rulelb.setAttribute("isTier", true);

		final Listbox operatorlb = new Listbox();// rule options lb----------5
		operatorlb.setMold("select");
		operatorlb.setStyle("margin-right:5px;");
		operatorlb.setParent(testDiv);
		operatorlb.setVisible(false);

		final Label label = new Label();
		label.setVisible(false);
		label.setParent(testDiv);

		final Listbox numberLb = new Listbox();// days/weeks/mnths------4
		numberLb.setMold("select");

		numberLb.setStyle("margin-right:5px;");

		for (int i = 1; i <= 30; i++) {
			li = new Listitem("" + i);
			li.setParent(numberLb);
		}
		numberLb.setVisible(false);

		numberLb.setParent(testDiv);
		final Textbox textBox = new Textbox();// string type value--------6
		textBox.setStyle("margin-right:5px;");
		textBox.setWidth("100px");
		textBox.setParent(testDiv);
		textBox.setVisible(false);

		final Decimalbox dblBox1 = new Decimalbox();// number type
													// value1------------7
		dblBox1.setStyle("margin-right:5px;");
		dblBox1.setParent(testDiv);
		dblBox1.setVisible(false);

		final Decimalbox dblBox2 = new Decimalbox();// number type
													// value2------------8
		dblBox2.setStyle("margin-right:5px;");
		dblBox2.setParent(testDiv);

		dblBox2.setVisible(false);

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		final Datebox db1 = new MyDatebox();// date type value1------------9
		db1.setStyle("margin-right:10px;");
		db1.setFormat(format.toPattern());
		db1.setReadonly(true);
		db1.setVisible(false);
		db1.setParent(testDiv);

		final Datebox db2 = new MyDatebox();// date type value2------------10
		db2.setStyle("margin-right:10px;");
		db2.setParent(testDiv);
		db2.setReadonly(true);
		db2.setFormat(format.toPattern());
		db2.setVisible(false);

		Toolbarbutton deleteIcon = new Toolbarbutton();// -------13 (14)
		deleteIcon.setImage("/images/action_delete.gif");
		deleteIcon.setStyle("float:right; margin-right:25px;");
		deleteIcon.setAttribute("Type", segmentEnum.name());
		if (!segmentEnum.name().equals(SpecialRewardEnum.ITEM_PURCHASE_QUANTIY.name()))
			deleteIcon.setParent(testDiv);
		List<String> posAttributeList =new ArrayList<String>();
		List<SpecialRewardEnum> ruleEnums = segmentEnum.getChidrenByParentEnum(segmentEnum);// to
																							// prepare
																							// rule
																							// lb
		if (segmentEnum.name().equals(SpecialRewardEnum.ITEM_PURCHASE_ATTRIBUTE.name())) {//Changes posAttributes to be shown.
			ruleEnums = segmentEnum.getChidrenByParentEnumForPurchaseItem(segmentEnum);
			posAttributeList = segmentEnum.getChidrenByParentEnumForPurchaseItemForString(segmentEnum);
		}
		int counter = 0;
		if(!posAttributeList.isEmpty() && segmentEnum.name().equals(SpecialRewardEnum.ITEM_PURCHASE_ATTRIBUTE.name())) {
			SpecialRewardEnum enumUDF = null;
			for (SpecialRewardEnum enums : ruleEnums) {
					li = new Listitem(posAttributeList.get(counter), enums);
					li.setParent(rulelb);
					logger.info("counter===>"+counter+"==="+posAttributeList.get(counter));
					counter++;
			}
//
//			POSMappingDao posMappingDao = (POSMappingDao)SpringUtil.getBean("posMappingDao");
//			List<POSMapping> posMappingsList = posMappingDao.findByType(OCConstants.POS_MAPPING_POS_ATTRIBUTE_SKU, user.getUserId());
//			for (POSMapping posMapping : posMappingsList) {
//				
//				if(posMapping.getCustomFieldName().trim().toLowerCase().startsWith(OCConstants.POS_MAPPING_POS_ATTRIBUTE_UDF)) {
//									
//					if(posMapping.getDataType().equalsIgnoreCase("String")) {
//					
//						try {
//				        Class<SpecialRewardEnum> cls = (Class<SpecialRewardEnum>) Class.forName("org.mq.optculture.controller.loyalty.SpecialRewardEnum");
//				        Field field = cls.getDeclaredField("ITEM_PURCHASE_"+posMapping.getCustomFieldName());
//				        SpecialRewardEnum tmp = (SpecialRewardEnum) field.get(cls);
//						enumUDF = tmp;
//						if(enumUDF != null) {
//							li = new Listitem(posMapping.getPosAttribute(), enumUDF);
//							li.setLabel(posMapping.getPosAttribute());
//							li.setParent(rulelb);
//						}
//						}
//						catch(Exception e) {
//							logger.info("e===>"+e);
//						}
//				}//if
//			}
//			} // for
		} 
		else 
			for (SpecialRewardEnum enums : ruleEnums) {
				li = new Listitem(enums.getDispLabel(), enums);
				li.setParent(rulelb);
				counter++;
				logger.info("counter===>"+counter+"==="+enums.getDispLabel());
			
		}

		if (segmentEnum.name().equals(SpecialRewardEnum.PURCHASE_IN_STORE.name())) {
			List<OrganizationStores> storeList = orgStoreDao
					.findByOrgByOrgId(user.getUserOrganization().getUserOrgId());
			if (storeList != null && storeList.size() > 0) {
				storeList.forEach(store -> {
					Listitem itm = new Listitem(store.getStoreName(), store.getHomeStoreId());
					itm.setParent(rulelb);
				});
			}
		}
		SpecialRewardEnum oprEnum = null;
		if (segmentEnum.name().equals(SpecialRewardEnum.PURCHASE_IN_TIER.name())
				|| segmentEnum.name().equals(SpecialRewardEnum.PURCHASE_IN_CARD_SET.name())) {
			oprEnum = segmentEnum.getChidrenByParentEnum(segmentEnum).get(0);
			List<LoyaltyProgram> programsList = loyaltyProgramDao.getAllProgramsListByUserId(GetUser.getUserId());
			programsList.forEach(program -> {
				Listitem itm = new Listitem(program.getProgramName(), program.getProgramId());
				itm.setParent(rulelb);
			});
			List<SpecialRewardEnum> childEnumList = segmentEnum.getChidrenByParentEnum(oprEnum);
			for (SpecialRewardEnum en : childEnumList) {
				Listitem listitem = new Listitem(en.getDispLabel(), en);
				listitem.setParent(operatorlb);
			}
			operatorlb.setVisible(true);
			rulelb.setAttribute("Type", "Program");
		} else {
			oprEnum = segmentEnum.getChidrenByParentEnum(segmentEnum).get(0);
			List<SpecialRewardEnum> childEnumList = segmentEnum.getChidrenByParentEnum(oprEnum);
			if (childEnumList != null && childEnumList.size() > 0) {
				for (SpecialRewardEnum en : childEnumList) {
					Listitem listitem = new Listitem(en.getDispLabel(), en);
					listitem.setParent(operatorlb);
				}
				operatorlb.setVisible(true);
			}
		}
		if (segmentEnum.getChidrenByParentEnum(segmentEnum).get(0).equals(SpecialRewardEnum.TIME_OF_THE_DAY)) {
			createNumberOptionsForRule(segmentEnum, operatorlb);
			createNumberOptionsForRule(segmentEnum, numberLb);
			label.setVisible(true);
			label.setStyle("padding:0 5px; display:inline-block; width:20px;");
			label.setValue(" To ");
			if (rulelb.getItemCount() > 0)
				rulelb.setSelectedIndex(0);
		} else {
			if (rulelb.getItemCount() > 0)
				rulelb.setSelectedIndex(0);
			SpecialRewardEnum tempEnum = null;
			if (operatorlb.isVisible() && operatorlb.getItemCount() > 0) {
				operatorlb.setSelectedIndex(0);
				tempEnum = (SpecialRewardEnum) operatorlb.getSelectedItem().getValue();
			} else {
				tempEnum = (SpecialRewardEnum) rulelb.getSelectedItem().getValue();
			}
			if (tempEnum != null) {
				String type = tempEnum.getType();
				if (type.equalsIgnoreCase("date")) {

					dblBox1.setVisible(false);
					dblBox2.setVisible(false);
					textBox.setVisible(false);

					createValuesForOperatorForTypeDate(db1, db2, numberLb, tempEnum);
				} else if (type.equalsIgnoreCase("string") || type.equalsIgnoreCase("json")) {

					numberLb.setVisible(false);
					db1.setVisible(false);
					db1.setVisible(false);
					dblBox1.setVisible(false);
					dblBox2.setVisible(false);
					createValuesForOperatorTypeSting(textBox, tempEnum);

				} else if (type.equalsIgnoreCase("number")) {
	
					if(segmentEnum.name().equals(SpecialRewardEnum.RECEIPT_TOTAL_ATTRIBUTE.name())) {
                    rulelb.setVisible(false);
					}
					numberLb.setVisible(false);
					db1.setVisible(false);
					db1.setVisible(false);
					textBox.setVisible(false);
					createValuesForOperatorTypeNumber(dblBox1, dblBox2, textBox, tempEnum);

				}
			}
		}
		deleteIcon.addEventListener("onClick", new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {

				Toolbarbutton deleteIcon = (Toolbarbutton) event.getTarget();
				Component componentsDiv = deleteIcon.getParent();
				Component rulesDiv = componentsDiv.getParent();
				Component ruleParent = rulesDiv.getParent();
				Component parent = ruleParent.getParent();
				if (deleteIcon.getAttribute("Type").equals(SpecialRewardEnum.ITEM_PURCHASE_ATTRIBUTE.name())
						|| deleteIcon.getAttribute("Type").equals(SpecialRewardEnum.VISITS.name())) {
					count--;
					if (count == 0) {
						itemList.forEach(itm -> {
							SpecialRewardEnum spenum = itm.getValue();
							if (spenum.name().equals(SpecialRewardEnum.VISITS.name())
									|| spenum.name().equals(SpecialRewardEnum.ITEM_PURCHASE_ATTRIBUTE.name()))
								itm.setDraggable("outerDivDropable");
						});
						List<Component> list = AttributeDivId.getChildren();
						for (Component comp : list) {
							Div tempDiv = (Div) comp;
							if (tempDiv.getAttribute("Type") != null && (tempDiv.getAttribute("Type").toString()
									.equals(SpecialRewardEnum.ITEM_PURCHASE_QUANTIY.name()))) {
								AttributeDivId.removeChild(tempDiv);
								flag = true;
								break;
							}
						}

					}

				}
				rulesDiv.removeChild(componentsDiv);
				if (rulesDiv.getChildren().size() == 0) {

					rulesDiv.getParent().removeChild(rulesDiv);
					ruleParent.getParent().removeChild(ruleParent);
				} else if (rulesDiv.getChildren().size() == 1) {

					logger.info("((Div)rulesDiv.getFirstChild()).getSclass() ::"
							+ ((Div) rulesDiv.getFirstChild()).getSclass());
					if (((Div) rulesDiv.getFirstChild()).getSclass().equalsIgnoreCase("drop_or_div")) {

						rulesDiv.getParent().removeChild(rulesDiv);
						ruleParent.getParent().removeChild(ruleParent);
					}
				} else {

					updateORLabel(rulesDiv);

				}

				updateAndLabel(parent);
			}

		});

		rulelb.addEventListener("onSelect", new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {

				Listbox lb = (Listbox) event.getTarget();
				SpecialRewardEnum ruleEnum = null;
				if (lb.getSelectedItem().getValue() instanceof SpecialRewardEnum) {
					ruleEnum = (SpecialRewardEnum) lb.getSelectedItem().getValue();
					if (segmentEnum.name().equals(SpecialRewardEnum.PURCHASE_IN_TIER.name())
							|| segmentEnum.name().equals(SpecialRewardEnum.PURCHASE_IN_CARD_SET.name())) {
						creteOperatorOptionsForRule((boolean) rulelb.getAttribute("isTier"), operatorlb, 0l);
						return;
					}
				} else {
					if (rulelb.getAttribute("Type") != null)
						creteOperatorOptionsForRule((boolean) rulelb.getAttribute("isTier"), operatorlb,
								lb.getSelectedItem().getValue());
					return;
				}
				SpecialRewardEnum tempEnum = null;
				if (segmentEnum.name().equals(SpecialRewardEnum.PURCHASE_DATE.name())) {
					tempEnum = (SpecialRewardEnum) lb.getSelectedItem().getValue();
					if (tempEnum.name().equals(SpecialRewardEnum.TIME_OF_THE_DAY.name())) {
						createNumberOptionsForRule(segmentEnum, operatorlb);
						createNumberOptionsForRule(segmentEnum, numberLb);
						label.setVisible(true);
						db1.setVisible(false);
						db2.setVisible(false);
						dblBox1.setVisible(false);
						dblBox2.setVisible(false);
						textBox.setVisible(false);
					} else {
						label.setVisible(false);
						operatorlb.setVisible(false);
					}
				} else {
					if (operatorlb.isVisible() && operatorlb.getItemCount() > 0) {
						operatorlb.setSelectedIndex(0);
						tempEnum = (SpecialRewardEnum) operatorlb.getSelectedItem().getValue();
					} else {
						tempEnum = ruleEnum;
					}
				}

				if (ruleEnum.getType().equalsIgnoreCase("date")) {
					dblBox1.setVisible(false);
					dblBox2.setVisible(false);
					textBox.setVisible(false);

					createValuesForOperatorForTypeDate(db1, db2, numberLb, tempEnum);
				} else if (ruleEnum.getType().equalsIgnoreCase("string")
						|| ruleEnum.getType().equalsIgnoreCase("json")) {
					db1.setVisible(false);
					db2.setVisible(false);
					dblBox1.setVisible(false);
					dblBox2.setVisible(false);
					textBox.setVisible(false);
					numberLb.setVisible(false);
					createValuesForOperatorTypeSting(textBox, tempEnum);

				}

			}

		});

		operatorlb.addEventListener("onSelect", new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {

				Listbox operatorlb = (Listbox) event.getTarget();

				Object obj = operatorlb.getSelectedItem().getValue();

				if (obj instanceof SpecialRewardEnum) {

					SpecialRewardEnum segmentEnum = operatorlb.getSelectedItem().getValue();
					String type = segmentEnum.getType();

					if (type.equalsIgnoreCase("date")) {
						createValuesForOperatorForTypeDate(db1, db2, numberLb, segmentEnum);

					} else if (type.equalsIgnoreCase("string")) {

						createValuesForOperatorTypeSting(textBox, segmentEnum);

					} else if (type.equalsIgnoreCase("number")) {

						createValuesForOperatorTypeNumber(dblBox1, dblBox2, textBox, segmentEnum);

					}

				}

			}

		});

		return testDiv;

	}

	private void createSubDivRule() {
		Div tempDiv = new Div();
		tempDiv.setSclass("");
		tempDiv.setParent(AttributeDivId);

		Label AndLabel = new Label("AND");
		AndLabel.setStyle("display:inline-block; width:20px; padding:0 5px;margin:0 10px;font-weight:bold;");
		AndLabel.setParent(tempDiv);

		Div vdiv = new Div();// this is the outer component
		// vdiv.setDroppable("purIntSegment");
		vdiv.setParent(tempDiv);
		vdiv.setSclass("segment_parent_div");
		updateAndLabel(AttributeDivId);

		List<SpecialRewardEnum> purchaseAttrList = SpecialRewardEnum.getEnumsByCategoryCode(4);

		Listitem subitem = null;
		for (SpecialRewardEnum purchaseSegEnum : purchaseAttrList) {
			subitem = new Listitem(purchaseSegEnum.getDispLabel(), purchaseSegEnum);
			subitem.setStyle("padding:10px;");
			subitem.setAttribute("DBcolumnName", purchaseSegEnum.getColumnName());
			subitem.setAttribute("item", purchaseSegEnum.getItem());
			tempDiv.setAttribute("Type", purchaseSegEnum.name());
		}
		Div obj = createNewRuleDiv(subitem);
		obj.setParent(vdiv);
		updateORLabel(vdiv);
		vdiv.addEventListener("onDrop", myDropListener);
	}

	public void createValuesForOperatorTypeSting(Textbox textbox, SpecialRewardEnum segmentEnum) {

		if (segmentEnum == null) {
			logger.debug("segment enum is null....");
			return;
		}
		String val1 = segmentEnum.getType1();
		String val2 = segmentEnum.getType2();
		logger.debug("val1 ::" + val1);
		logger.debug("val2 ::" + val2);
		if (val1 == null && val2 == null) {
			textbox.setVisible(false);
		} else if (val1 != null && val2 == null) {
			if (segmentEnum.getDispLabel().equals("One of")) {
				textbox.setTooltiptext("You can enter multiple values as comma (',') separated values.");
			} else {
				textbox.setTooltiptext("");
			}
			textbox.setVisible(true);

		} // else if

	}

	public void createValuesForOperatorTypeNumber(Decimalbox dblBox1, Decimalbox dblBox2, Textbox textbox,
			SpecialRewardEnum segmentEnum) {

		if (segmentEnum == null) {
			logger.debug("special reward enum is null....");
			return;
		}
		String val1 = segmentEnum.getType1();
		String val2 = segmentEnum.getType2();
		String token = segmentEnum.getToken();

		logger.debug("val2 ::" + val2);
		if (val1 == null && val2 == null) {

			dblBox1.setVisible(false);
			dblBox2.setVisible(false);
			textbox.setVisible(false);

		} else if (val1 != null && val2 == null) {

			if (val1.equalsIgnoreCase("text")) {
				textbox.setVisible(false);
				dblBox1.setVisible(false);
				dblBox2.setVisible(false);
			} else {

				if (segmentEnum.getDispLabel().equals("One of")) {
					textbox.setTooltiptext(
							"You can enter multiple numeric type values as comma (',') separated values.");
				} else {
					textbox.setTooltiptext("Enter only numeric type values.");
				}
				textbox.setVisible(true);
				dblBox1.setVisible(false);
				dblBox2.setVisible(false);
			}

		} // if
		else {
			textbox.setVisible(false);
			dblBox1.setVisible(false);
			dblBox2.setVisible(false);

		}

		if (val1 != null && val2 != null) {
			textbox.setVisible(false);
			dblBox1.setVisible(false);
			dblBox2.setVisible(false);

		} 
		else if(val1=="text")
			{
			
			textbox.setVisible(true);
			}
			else {
			dblBox1.setVisible(true);
			dblBox2.setVisible(true);
			dblBox2.setTooltiptext("End date should be greater than the start date.");
			textbox.setVisible(false);
		}

	}

	public void createValuesForOperatorForTypeDate(Datebox db1, Datebox db2, Listbox numberLb,
			SpecialRewardEnum segmentEnum) {

		if (segmentEnum == null) {
			logger.debug("segment enum is null....");
			return;
		}
		String val1 = segmentEnum.getType1();
		String val2 = segmentEnum.getType2();
		logger.debug("val2 ::" + val2);

		if (val1 == null && val2 == null) {

			db1.setVisible(false);
			db2.setVisible(false);
			numberLb.setVisible(false);

			// txtBox.setVisible(false);

		} else if (val1 != null && val2 != null) {

			if (!val1.equalsIgnoreCase("date")) {// data type is date but type
													// is:within last-range of
													// days

				if (val1.equalsIgnoreCase("number")) {

					numberLb.setVisible(false);

				} else {
					Components.removeAllChildren(numberLb);
					int num = Integer.parseInt(val1);
					for (int i = 1; i <= num; i++) {
						Listitem li = new Listitem("" + i, i);
						if (segmentEnum.name().equals(SpecialRewardEnum.DAY_OF_THE_WEEK.name()))
							li = new Listitem(dayMap.get(i), i);
						li.setParent(numberLb);

					}

					numberLb.setSelectedIndex(0);
					numberLb.setVisible(true);
				}
				db1.setVisible(false);
				db2.setVisible(false);

			} else {
				db1.setVisible(true);
				db2.setVisible(true);
				db2.setTooltiptext("Start date should be less than end date.");
				numberLb.setVisible(false);
			}

		} else if (val1 != null && val2 == null) {

			if (val1.equalsIgnoreCase("date")) {

				db2.setVisible(false);
				numberLb.setVisible(false);
				db1.setVisible(true);
				// txtBox.setVisible(false);

			}
		}

	}

	public void creteOperatorOptionsForRule(boolean istire, Listbox operatorLb, Long programId) {

		Listitem li = null;
		Components.removeAllChildren(operatorLb);
		if (istire) {
			li = new Listitem("Select Tier", "");
			li.setSelected(true);
			li.setParent(operatorLb);
			List<LoyaltyProgramTier> tierList = loyaltyProgramTierDao.getTierListByPrgmId(programId);
			if (tierList != null && tierList.size() > 0) {
				for (LoyaltyProgramTier tierObj : tierList) {
					li = new Listitem(tierObj.getTierName(), tierObj.getTierId());
					li.setParent(operatorLb);
				}
			}

		} else {
			li = new Listitem("Select Card-set", "");
			li.setSelected(true);
			li.setParent(operatorLb);
			List<LoyaltyCardSet> list = loyaltyCardSetDao.findByProgramId(programId);
			if (list != null && list.size() > 0) {
				for (LoyaltyCardSet cardset : list) {
					li = new Listitem(cardset.getCardSetName(), cardset.getCardSetId());
					li.setParent(operatorLb);
				}
			}
		}
		operatorLb.setVisible(true);
		operatorLb.setSelectedIndex(0);

	}// creteOperatorOptionsForRule()

	private void createNumberOptionsForRule(SpecialRewardEnum segmentEnum, Listbox listbox) {
		Components.removeAllChildren(listbox);
		Listitem li = null;
		for (int i = 1; i <= 24; i++) {
			if (i <= 11)
				li = new Listitem(i + "AM", i);
			else if (i == 12)
				li = new Listitem(i + "PM", i);
			else if (i == 24)
				li = new Listitem(12 + "AM", 0);
			else
				li = new Listitem((i - 12) + "PM", i);
			li.setParent(listbox);
		}
		listbox.setVisible(true);
		listbox.setSelectedIndex(0);
	}

	public Set<LoyaltyProgram> getAllSelecteProgram() {
		Set<Listitem> selectItem = loyaltyProgramLbId.getSelectedItems();
		if (selectItem == null || selectItem.size() == 0) {
			MessageUtil.setMessage("Please select atleast one program.", "color:red", "TOP");
			return null;
		}
		Set<LoyaltyProgram> set = new HashSet<>();
		for (Listitem item : selectItem) {
			LoyaltyProgram ltyprg = item.getValue();
			set.add(ltyprg);
		}
		return set;
	}

	public String saveRule() {
		List<Component> childVboxList = new ArrayList<Component>();
		if (targetDivHasChildren(AttributeDivId))
			childVboxList.addAll(AttributeDivId.getChildren());
		if (childVboxList.size() == 0) {// only the dashed areas r left
			MessageUtil.setMessage("No Attribute(s) added to create reward rule. Please add at least one Attribute(s).", "color:red;");
			return null;
		}

		Div chilVDiv = null;
		StringBuffer segmentRuleSB = new StringBuffer();// "all:"+((MailingList)dispMlLBoxId.getSelectedItem().getValue()).getListId());
		String ruleStr = null;
		boolean allValid = true;

		for (Object obj : childVboxList) {

			if (obj instanceof Div) {

				boolean isValid = true;
				for (Object object : ((Div) obj).getChildren()) {

					if (isValid == false)
						allValid = false;

					// Reset
					isValid = true;

					if (object instanceof Div) {
						chilVDiv = (Div) object;
						if (chilVDiv.getSclass().contains("drop_")) {
							continue;
						}
						ruleStr = createSpecialRule(chilVDiv);
						if (ruleStr == null) {
							isValid = false;
						}
						if (ruleStr != null && ruleStr.trim().length() > 0) {
							segmentRuleSB.append(ruleStr + "||");
						}
					} // ruleDiv

				} // tempDiv added for And

				if (isValid == false)
					allValid = false;

			} // if obj
		}
		if (!allValid) {
			logger.warn(" >>>>>>>>>>>>> Segmentation validation failed ");

			MessageUtil.setMessage("Please provide proper value(s) for highlighted rule(s).", "color:red", "TOP");
			return null;
		}
		if (segmentRuleSB.toString().trim().length() == 0) {

			MessageUtil.setMessage(
					"There are no valid rules to prepare reward rule (as of now, derivative filter rules are ignored).",
					"color:red", "TOP");
			return null;
		}
		
		if(validateDuplicacy(segmentRuleSB.toString())) {
			MessageUtil.setMessage(
					"Duplicate rules found. Please ensure each rule is distinct.",
					"color:red", "TOP");
			return null;
		}
		
		return segmentRuleSB.toString();

	}

	public boolean targetDivHasChildren(Div targetDiv) {
		List<Component> childComponentList = targetDiv.getChildren();
		if (childComponentList.size() == 0
				|| (childComponentList.size() == 1 && childComponentList.get(0) instanceof Div)) {

			Div orANDDiv = (Div) childComponentList.get(0);
			if (orANDDiv.getSclass() != null && orANDDiv.getSclass().equals("drop_and_div"))
				return false;
			else
				return true;

		} else
			return true;

	}

	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private String createSpecialRule(Div ruleVDiv) {
		List childDivList = ruleVDiv.getChildren();
		Div ruleDiv = null;
		boolean retValue = true;
		boolean isValid = true;
		String hashTagValue = null;
		StringBuilder sb = new StringBuilder();
		for (Object object : childDivList) {
			if (isValid == false)
				retValue = false;
			// Reset the isValid
			isValid = true;
			if (object instanceof Div) {
				Label fielslbl = null;
				List columns = null;
				String col = null;
				String itemCode = null;
				Listbox ruleLb = null;
				Listbox operatorLb = null;
				Listbox numberLb = null;
				String val1 = "";
				String val2 = "";
				String val3 = "";
				String jsonatt = "";
				String programId = "";
				ruleDiv = (Div) object;
				if (ruleDiv.getSclass().contains("drop_")) {
					continue;
				}
				columns = ruleDiv.getChildren();
				fielslbl = (Label) columns.get(1); //
				col = (String) fielslbl.getAttribute("columnName");
				itemCode = (String) fielslbl.getAttribute("item");
				ruleLb = (Listbox) columns.get(2);
				operatorLb = (Listbox) columns.get(5);
				numberLb = (Listbox) columns.get(7);
				Textbox txtbox = (Textbox) columns.get(8);
				Datebox dateBox1 = (Datebox) columns.get(11);
				Datebox dateBox2 = (Datebox) columns.get(12);
				SpecialRewardEnum specialRewardEnum;
				specialRewardEnum = (SpecialRewardEnum) ruleLb.getItemAtIndex(0).getValue();
				hashTagValue = Utility.specialRuleHashTag.get(itemCode);
				jsonatt = specialRewardEnum.getJsonAttribute();
				// get all vales
				if (specialRewardEnum.getParentEnum().name().equals(SpecialRewardEnum.ITEM_PURCHASE_ATTRIBUTE.name())) {
					jsonatt = ((SpecialRewardEnum) ruleLb.getSelectedItem().getValue()).getJsonAttribute();
				} else if (specialRewardEnum.getParentEnum().name()
						.equals(SpecialRewardEnum.ITEM_PURCHASE_QUANTIY.name())) {
					val1 = ((SpecialRewardEnum) ruleLb.getSelectedItem().getValue()).getColumnValue();
					Textbox tb = (Textbox) columns.get(4);
					val2 = tb.getText().trim();
					val3 = ((SpecialRewardEnum) operatorLb.getSelectedItem().getValue()).getColumnValue();
					if (val2 == null || val2.length() == 0) {
						isValid = false;
						setErrorToDiv(ruleDiv, isValid);
						continue;
					}
					try{
					Double.parseDouble(val2);//Number Value to be expected.
					}
					catch(NumberFormatException e) {
						isValid = false;
						setErrorToDiv(ruleDiv, isValid);
						continue;
					}
				} else if (specialRewardEnum.getParentEnum().name().equals(SpecialRewardEnum.PURCHASE_DATE.name())
						|| specialRewardEnum.getParentEnum().name().equals(SpecialRewardEnum.VISITS.name())) {
					val1 = ((SpecialRewardEnum) ruleLb.getSelectedItem().getValue()).getColumnValue();
					if (operatorLb.isVisible())
						val2 = operatorLb.getSelectedItem().getValue().toString();
				} 
				
		
				else if (specialRewardEnum.getParentEnum().name().equals(SpecialRewardEnum.RECEIPT_TOTAL_ATTRIBUTE.name())
						) {
				
					Textbox tb1 = (Textbox) columns.get(8);
					val1 = tb1.getText().trim();
					logger.debug(val1);
							
				} 
						else {
					if (specialRewardEnum.getParentEnum().name().equals(SpecialRewardEnum.PURCHASE_IN_TIER.name())
							|| specialRewardEnum.getParentEnum().name()
									.equals(SpecialRewardEnum.PURCHASE_IN_CARD_SET.name())) {
						programId = ruleLb.getSelectedItem().getValue().toString();
						if (ruleLb.getSelectedIndex() == 0 || programId == null || programId.length() == 0) {
							isValid = false;
							setErrorToDiv(ruleDiv, isValid);
							continue;
						}
					} else {
						val1 = ruleLb.getSelectedItem().getValue().toString();
						if (ruleLb.getSelectedIndex() == 0 || val1 == null || val1.length() == 0) {
							isValid = false;
							setErrorToDiv(ruleDiv, isValid);
							continue;
						}
					}
					if (operatorLb.isVisible()) {
						if (val1.isEmpty()) {
							val1 = operatorLb.getSelectedItem().getValue().toString();
							if (operatorLb.getSelectedIndex() == 0 || val1 == null || val1.length() == 0) {
								isValid = false;
								setErrorToDiv(ruleDiv, isValid);
								continue;
							}
						} else {
							val2 = operatorLb.getSelectedItem().getValue().toString();
							if (operatorLb.getSelectedIndex() == 0 || val2 == null || val2.length() == 0) {
								isValid = false;
								setErrorToDiv(ruleDiv, isValid);
								continue;
							}
						}
					}
				}

				if (!itemCode.equalsIgnoreCase("[#Minimum Receipt Total#]") && txtbox.isVisible()) {
					if (val1.isEmpty()) {
						val1 = txtbox.getText().trim();
						if (val1 == null || val1.length() == 0) {
							isValid = false;
							setErrorToDiv(ruleDiv, isValid);
							continue;
						}
					} else {
						val2 = txtbox.getText().trim();
						if (val2 == null || val2.length() == 0) {
							isValid = false;
							setErrorToDiv(ruleDiv, isValid);
							continue;
						}
						try{
							Integer.parseInt(val2);
							}
							catch(NumberFormatException e) {
								isValid = false;
								setErrorToDiv(ruleDiv, isValid);
								continue;
							}
					}
				}
				if (dateBox1.isVisible()) {
					if(dateBox1.getValue()==null){
						isValid = false;
						setErrorToDiv(ruleDiv, isValid);
						continue;
					}
					val2 = dateFormat.format(dateBox1.getValue());
				}
				if (dateBox2.isVisible()) {
					if(dateBox2.getValue()==null){
						isValid = false;
						setErrorToDiv(ruleDiv, isValid);
						continue;
					}
					val3 = dateFormat.format(dateBox2.getValue());
					if (dateBox1.getValue().after(dateBox2.getValue())) {
						isValid = false;
						setErrorToDiv(ruleDiv, isValid);
						continue;
					}

				}
				if (numberLb.isVisible()) {
					if (val2.isEmpty()) {
						val2 = numberLb.getSelectedItem().getValue().toString();
						if (val2 == null || val2.length() == 0) {
							isValid = false;
							setErrorToDiv(ruleDiv, isValid);
							continue;
						}
					} else {
						val3 = numberLb.getSelectedItem().getValue().toString();
						if(val3.equals("0")) val3 = "24";//APP-3484, for 24-hour cycle.

						if (val3 == null || val3.length() == 0) {
							isValid = false;
							setErrorToDiv(ruleDiv, isValid);
							continue;
						}
						if (Integer.parseInt(val2) > Integer.parseInt(val3)) {
							isValid = false;
							setErrorToDiv(ruleDiv, isValid);
							continue;
						}
					}
				}
				if (programId != null && programId.length() > 0)
					hashTagValue = hashTagValue.replace("<programId>", programId);
				if (jsonatt != null && jsonatt.length() > 0)
					hashTagValue = hashTagValue.replace("<jsonAttribute>", jsonatt);
				if (val1 != null && val1.length() > 0)
					hashTagValue = hashTagValue.replace("<val1>", val1);
				if (val2 != null && val2.length() > 0)
					hashTagValue = hashTagValue.replace("<val2>", val2);
				if (val3 != null && val3.length() > 0)
					hashTagValue = hashTagValue.replace("<val3>", val3);
				// add hash tag
				hashTagValue = itemCode + ";=;" + hashTagValue + "<OR>";
				sb.append(hashTagValue);
			}
			isValid = true;
			setErrorToDiv(ruleDiv, isValid);
		}
		if (isValid == false)
			retValue = false;
		if (!retValue)
			return null;
		return sb.toString();

	}

	public void setErrorToDiv(Div errorDiv, boolean isValid) {

		if (isValid) {
			errorDiv.setSclass("segment_child_div");
		} else {
			errorDiv.setSclass("segment_child_error_div");
		}

	}

	public void deleteExistedRulesDiv(Component parentComponent) {
		List<Component> list = parentComponent.getChildren();
		List<Component> deleteList = new ArrayList<Component>();
		for (Component component : list) {

			if (component instanceof Div) {

				Div deleteDiv = (Div) component;
				if (deleteDiv.getSclass() != null && deleteDiv.getSclass().startsWith("drop_"))
					continue;
				else
					deleteList.add(deleteDiv);// deleteDiv.setParent(null);//profileAttributeDivId.removeChild(deleteDiv);

			} else if (component instanceof Label)
				deleteList.add(component);// component.setParent(null);//profileAttributeDivId.removeChild(component);

		}

		if (!deleteList.isEmpty()) {

			for (Component component : deleteList) {

				parentComponent.removeChild(component);

			}

		}
	}// deleteExistedRulesDiv

	private void createDivForEdit(String segRule) {
		logger.debug("Reward  Rule :" + segRule);
		if (segRule == null) {
			return;
		}
		String[] rowsArr = segRule.split("\\|\\|");
		String ruleStr = "";
		for (int i = 0; i < rowsArr.length; i++) {
			String[] orRuleTokensArr = null;
			String[] tokenArr = null;
			String itemCode = "";
			String value1=null;
			ruleStr = rowsArr[i];
			if (ruleStr.contains("<OR>")) {
				orRuleTokensArr = ruleStr.split("<OR>");
				Div tempDiv = new Div();
				tempDiv.setSclass("");
				tempDiv.setParent(AttributeDivId);

				Label AndLabel = new Label("AND");
				AndLabel.setStyle("display:inline-block; width:20px; padding:0 5px;margin:0 10px;font-weight:bold;");
				AndLabel.setParent(tempDiv);

				Div vdiv = new Div();// this is the outer component
				vdiv.setDroppable("outerDivDropable");
				vdiv.setParent(tempDiv);
				vdiv.setSclass("segment_parent_div");
				vdiv.addEventListener("onDrop", myDropListener);
				updateAndLabel(AttributeDivId);
				Div obj = null;
				boolean isORDivCreated=true;
				for (int j = 0; j < orRuleTokensArr.length; j++) {
					tokenArr = orRuleTokensArr[j].split(";=;");
					itemCode = tokenArr[0];
					value1=tokenArr[3];
					SpecialRewardEnum specialRewardEnum = SpecialRewardEnum.getEnumByItem(itemCode);
					for (Listitem item : AttrLbId.getItems()) {
						if (itemCode.equals((String) item.getAttribute("item"))) {
							obj = createNewRuleDiv(item);
							obj.setParent(vdiv);
							updateORLabel(vdiv);
							break;
						}
					}
					if (flag && itemCode.equals("[#ItemFactor#]")) {
						Listitem item = new Listitem(specialRewardEnum.getDispLabel(), specialRewardEnum);
						item.setAttribute("item", specialRewardEnum.getItem());
						tempDiv.setAttribute("Type", specialRewardEnum.name());
						obj = createNewRuleDiv(item);
						obj.setParent(vdiv);
						updateORLabel(vdiv);
						flag = false;
					}
					// set all values
					List ruleDivChildren = obj.getChildren();
					Label fielslbl = (Label) ruleDivChildren.get(1); //
					itemCode = (String) fielslbl.getAttribute("item");
					Listbox ruleLb = (Listbox) ruleDivChildren.get(2);
					Listbox operatorLb = (Listbox) ruleDivChildren.get(5);
					Listbox numberLb = (Listbox) ruleDivChildren.get(7);
					Textbox txtbox = (Textbox) ruleDivChildren.get(8);
					Datebox dateBox1 = (Datebox) ruleDivChildren.get(11);
					Datebox dateBox2 = (Datebox) ruleDivChildren.get(12);
					if(itemCode.equalsIgnoreCase("[#Minimum Receipt Total#]")) {	
						txtbox.setText(value1);
					}
					Label templbl = (Label) ruleDivChildren.get(6);
					templbl.setValue("");
					String val1 = "", val2 = "", val3 = "";
					String programId = "";
					String jsonAtt = "";
					boolean dflag = true;
					if(!tokenArr[0].contains("[#Minimum Receipt Total#]")) {
					if (!tokenArr[2].contains("<programId>"))
						programId = tokenArr[2];
					jsonAtt = tokenArr[1].split(":")[1];
					val1 = tokenArr[3];
					val2 = tokenArr[4];
					val3 = tokenArr[5];
					List<Listitem> list = ruleLb.getItems();
					if (specialRewardEnum.name().equals(SpecialRewardEnum.ITEM_PURCHASE_ATTRIBUTE.name())
							|| specialRewardEnum.name().equals(SpecialRewardEnum.ITEM_PURCHASE_QUANTIY.name())
							|| specialRewardEnum.name().equals(SpecialRewardEnum.VISITS.name()))
						vdiv.setDroppable("notDraggable");
					else{ 
						if(isORDivCreated)
						vdiv.appendChild(createDropORDiv("Drag Attribute(s) here to create OR combination rule."));
						isORDivCreated=false;
					}

					if (specialRewardEnum.name().equals(SpecialRewardEnum.ITEM_PURCHASE_ATTRIBUTE.name())) {
						for (Listitem itm : list) {
							if (itm.getValue() != null
									&& ((SpecialRewardEnum) itm.getValue()).getJsonAttribute().equals(jsonAtt))
								ruleLb.setSelectedItem(itm);
						}
						txtbox.setValue(val1);
					} else if (specialRewardEnum.name().equals(SpecialRewardEnum.ITEM_PURCHASE_QUANTIY.name())) {
						for (Listitem itm : list) {
							if (itm.getValue() != null
									&& ((SpecialRewardEnum) itm.getValue()).getColumnValue().equals(val1))
								ruleLb.setSelectedItem(itm);
						}
						Textbox tb = (Textbox) ruleDivChildren.get(4);
						if (tb.isVisible())
							tb.setValue(val2);
						list = operatorLb.getItems();
						for (Listitem itm : list) {
							if (itm.getValue() != null
									&& ((SpecialRewardEnum) itm.getValue()).getColumnValue().equals(val3))
								operatorLb.setSelectedItem(itm);
						}
						dflag = false;
					} else if (specialRewardEnum.name().equals(SpecialRewardEnum.VISITS.name())
							|| specialRewardEnum.name().equals(SpecialRewardEnum.PURCHASE_DATE.name())) {
						for (Listitem itm : list) {
							if (itm.getValue() != null
									&& ((SpecialRewardEnum) itm.getValue()).getColumnValue().equals(val1))
								ruleLb.setSelectedItem(itm);
						}
						if (txtbox.isVisible())
							txtbox.setValue(val2);
						//Events.sendEvent("onSelect", ruleLb, null);
						SpecialRewardEnum tempEnum = (SpecialRewardEnum) ruleLb.getSelectedItem().getValue();
						  if (tempEnum.name().equals(SpecialRewardEnum.TIME_OF_THE_DAY.name())) {
							createNumberOptionsForRule(specialRewardEnum, operatorLb);
							createNumberOptionsForRule(specialRewardEnum, numberLb);
						} else {
							createValuesForOperatorForTypeDate(dateBox1, dateBox2, operatorLb, tempEnum);
							numberLb.setVisible(false);
						}
					} else {
						if (specialRewardEnum.name().equals(SpecialRewardEnum.PURCHASE_IN_STORE.name())) {
							for (Listitem itm : list) {
								if (itm.getValue() != null && itm.getValue().toString().equals(val1))
									ruleLb.setSelectedItem(itm);
							}
						} else {
							for (Listitem itm : list) {
								if (itm.getValue() != null && itm.getValue().toString().equals(programId))
									ruleLb.setSelectedItem(itm);
							}
							//Events.sendEvent("onSelect", ruleLb, null);
							creteOperatorOptionsForRule(itemCode.equals("[#PurchaseTier#]") ? true : false, operatorLb,
									Long.parseLong(programId));
							list = operatorLb.getItems();
							for (Listitem itm : list) {
								if (itm.getValue() != null && itm.getValue().toString().equals(val1))
									operatorLb.setSelectedItem(itm);
							}
						}
					}

					if (dateBox1.isVisible() && val2.contains("-")) {
						try {
							dateBox1.setValue(dateFormat.parse(val2));
						} catch (Exception e) {
							logger.error("Exception", e);
						}
					}
					if (dflag && operatorLb.isVisible()) {
						list = operatorLb.getItems();
						for (Listitem itm : list) {
							if (itm.getValue() != null && itm.getValue().toString().equals(val2))
								operatorLb.setSelectedItem(itm);
						}
					}

					if (numberLb.isVisible()) {
						list = numberLb.getItems();
						for (Listitem itm : list) {
							if(val3.equals("24")) val3 = "0";//3484
							if (itm.getValue() != null && itm.getValue().toString().equals(val3))
								numberLb.setSelectedItem(itm);
						}
						numberLb.setVisible(true);
						templbl.setValue(" To ");
					}
					if (dateBox2.isVisible() && val3.contains("-")) {
						try {
							dateBox2.setValue(dateFormat.parse(val3));
						} catch (Exception e) {
							logger.error("Exception", e);
						}
					}
					}
				}
			}
		}
	}

	public void onClick$previewEmailTemplateBtnId() {
		previewEmailTemplate(autoEmailLbId,  OCConstants.SPECIALREWARDS);
	}

	public void onClick$editEmailTemplateBtnId() {
		editEmailTemplate(autoEmailLbId,  OCConstants.SPECIALREWARDS);
	}

	public void onClick$addEmailTemplateBtnId() {
		addEmailTemplate(autoEmailLbId, OCConstants.SPECIALREWARDS);
	}

	public void onClick$previewSmsBtnId() {
		previewSMSTemplate(autoSMSLbId, OCConstants.SPECIALREWARDS);
	}

	public void onClick$editSmsBtnId() {
		editSMSTemplate(autoSMSLbId, OCConstants.AUTO_SMS_TEMPLATE_TYPE_SPECIAL_REWARDS);
	}

	public void onClick$addSmsBtnId() {
		addSMSTemplate(autoSMSLbId, OCConstants.SPECIALREWARDS);
	}

	private void previewEmailTemplate(Listbox lbId, String defaultMsgType) {
		if (lbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto Email")) {
			MessageUtil.setMessage("Please select atleast one template", "color:red", "TOP");
		} else {
			String templateContent = "";
			LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
			logger.info("lbId.getSelectedItem().getValue()===>"+lbId.getSelectedItem().getValue());
			CustomTemplates customTemplates = ltyPrgmSevice.getCustomTemplate(Long.parseLong(lbId.getSelectedItem().getValue().toString().trim()));
			if (customTemplates == null) {
				templateContent = PropertyUtil.getPropertyValueFromDB(defaultMsgType);
			} else {
				if (customTemplates.getHtmlText() != null && !customTemplates.getHtmlText().isEmpty()) {
					templateContent = customTemplates.getHtmlText();
				} else if (customTemplates.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_BEE)
						&& customTemplates.getMyTemplateId() != null) {
					MyTemplates myTemplates = myTemplatesDao
							.getTemplateByMytemplateId(customTemplates.getMyTemplateId());
					if (myTemplates != null) {
						templateContent = myTemplates.getContent();
					} else {
						MessageUtil.setMessage(
								"No template was found configured in this auto-email message. Please edit the message to add a template to it.",
								"color:red", "TOP");
						return;
					}
				}
			}
			Utility.showPreview(previewWin$html, user.getUserName(), templateContent);
			previewWin.setVisible(true);
		}
		logger.debug("---Exit previewEmailTemplate---");
	}// previewEmailTemplate()

	private void editEmailTemplate(Listbox lbId, String tempType) {
		if (lbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto Email")) {
			MessageUtil.setMessage("Please select atleast one template", "color:red", "TOP");
		} else {
			LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
			CustomTemplates customTemplates = ltyPrgmSevice.getCustomTemplate(Long.parseLong(lbId.getSelectedItem().getValue().toString().trim()));
			session.setAttribute("editCustomTemplate", customTemplates);
			session.setAttribute("Mode", "edit");
			session.setAttribute("typeOfEmail", tempType);
			session.setAttribute("fromAddNewBtn", "loyalty/loyaltyAutoCommunication");
			Redirect.goTo(PageListEnum.CONTACT_MANAGE_AUTO_EMAILS_BEE);
		}
		logger.debug("---Exit editEmailTemplate---");
	}// editEmailTemplate()

	private void addEmailTemplate(Listbox lbId, String tempType) {
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		CustomTemplates customTemplates = lbId.getSelectedItem().getValue()!=null? ltyPrgmSevice.getCustomTemplate(Long.parseLong(lbId.getSelectedItem().getValue().toString().trim())):null;
		session.setAttribute("editCustomTemplate", customTemplates);
		session.setAttribute("Mode", "add");
		session.setAttribute("typeOfEmail", tempType);
		session.setAttribute("fromAddNewBtn", "loyalty/loyaltyAutoCommunication");
		Redirect.goTo(PageListEnum.CONTACT_MANAGE_AUTO_EMAILS_BEE);
		logger.debug("---Exit addEmailTemplate---");
	}// addEmailTemplate()

	public void previewSMSTemplate(Listbox lbId, String defaultMsgType) {
		logger.info("---Entered previewSMSTemplate-----");
		if (lbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto SMS")) {
			MessageUtil.setMessage("Please select atleast one template.", "color:red", "TOP");
		} else {
			String templateContent = "";
			LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
			AutoSMS autoSMS = ltyPrgmSevice.getAutoSmsTemplateById((Long) lbId.getSelectedItem().getValue());
			if (autoSMS == null) {
				templateContent = PropertyUtil.getPropertyValueFromDB(defaultMsgType);
			} else {
				templateContent = autoSMS.getMessageContent();
			}
			logger.info("templateContent 1----" + templateContent);
			Utility.showPreview(previewWin$html, user.getUserName(), templateContent);
			previewWin.setVisible(true);
		}
		logger.debug("--Exit previewSMSTemplate---");
	}

	public void editSMSTemplate(Listbox lbId, String tempType) {
		logger.info("---Entered editSMSTemplate-----");
		if (lbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Auto SMS")) {
			MessageUtil.setMessage("Please select atleast one template.", "color:red", "TOP");
		} else {
			LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
			AutoSMS autoSMS = ltyPrgmSevice.getAutoSmsTemplateById((Long) lbId.getSelectedItem().getValue());
			session.setAttribute("editSmsTemplate", autoSMS);
			session.setAttribute("SmsMode", "edit");
			session.setAttribute("typeOfSms", tempType);
			session.setAttribute("fromAddNewBtn", "loyalty/loyaltyAutoCommunication");
			Redirect.goTo(PageListEnum.CONTACT_MANAGE_AUTO_SMS);
		}
		logger.debug("--Exit editSMSTemplate---");
	}

	public void addSMSTemplate(Listbox lbId, String tempType) {
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		AutoSMS autoSMS = ltyPrgmSevice.getAutoSmsTemplateById((Long) lbId.getSelectedItem().getValue());
		session.setAttribute("editSmsTemplate", autoSMS);
		session.setAttribute("SmsMode", "add");
		session.setAttribute("typeOfSms", tempType);
		session.setAttribute("fromAddNewBtn", "loyalty/loyaltyAutoCommunication");
		Redirect.goTo(PageListEnum.CONTACT_MANAGE_AUTO_SMS);
		logger.debug("--Exit addSMSTemplate---");
	}// addSMSTemplate()

	//APP-2038
	public boolean validateDuplicacy(String segmentRuleSB) {
		String arrRule[]=segmentRuleSB.split("\\|\\|");
		HashSet<String> hashSet = new HashSet<String>();
		
		for (String eleName : arrRule) {
			if (hashSet.add(eleName) == false && !eleName.isEmpty()) { 
			return true;
			}
		}
		for(String eleName :arrRule) {
			String arrRuleOR[]=eleName.split("<OR>");
			HashSet<String> hashSetForOR = new HashSet<String>();
			for (String eleNameOR : arrRuleOR) {
				if (hashSetForOR.add(eleNameOR) == false && !eleNameOR.isEmpty()) { 
				return true;
				}
			}
		}
		
	
		return false;
	
	
	
	}

	public void setTierData() {

		List tierListItems = tierLbId.getChildren();
		for (int i = tierListItems.size() - 1; i > 0; i--) {
			tierLbId.removeItemAt(i);
		}

		Set<Listitem> selectedLytPrgms = loyaltyProgramLbId.getSelectedItems();

		for (Listitem lytPrgListItem : selectedLytPrgms) {

			LoyaltyProgram ltyPrgm = lytPrgListItem.getValue();

			List<LoyaltyProgramTier> lytPrgmTierList = loyaltyProgramTierDao.getAllTierByProgramId(ltyPrgm.getProgramId());

			Listitem li = null;

			for (LoyaltyProgramTier lytPrgmTier : lytPrgmTierList) {
				if (!lytPrgmTier.getTierType().equals("Tier 1")) {
					li = new Listitem(lytPrgmTier.getTierName());
					li.setValue(lytPrgmTier.getTierId());
					li.setParent(tierLbId);
				}
			}
		}
	}

	public void setTierDataOnEdit(SpecialReward spreward) {

		List<Listitem> items = tierLbId.getItems();

		items.forEach(item -> {
			if (spreward.getRewardType().equals("T") && item.getLabel().equals(spreward.getRewardValueCode())
					&& item.getValue().toString().equals(spreward.getRewardValue())) {
				item.setSelected(true);
			}
		});
	}

	public void onSelect$loyaltyProgramLbId() {

		setTierData();

		SpecialReward spreward = specialReward;

		if (spreward.getRewardType() != null)
			setTierDataOnEdit(spreward);
	}


}	
	


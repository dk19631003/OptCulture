package org.mq.optculture.controller.loyalty;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramExclusion;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.SkuFile;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.loyalty.LoyaltyProgramService;
import org.mq.optculture.utils.OCConstants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;


public class LoyaltyAdditionalSettingsController extends GenericForwardComposer{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	TimeZone clientTimeZone ;
	private Long userId;
	private Checkbox earnedRwrdExpiryCbId,memValidityCbId,resetValidCbId,disallowCbId,disallowRedeemCbId,giftAmtExpCbId,giftmemExpCbId, excludeRedeemCbId;
	private Listbox tierLbId, memTierLbId, selIssuePromotionsLbId,selRedeemPromotionsLbId, selectedStoreLbId,itemCategoryLbId,productCodeLbId,
	dateLbId,redemptionDateLbId,excDateLbId,excMonthLbId,excYearLbId,excRedemptionYearLbId,excRedemptionMonthLbId,excRedemptionDateLbId,giftAmtExpTypeLbId,giftAmtExpValueLbId,giftmemExpTypeLbId,giftMemExpValueLbId, redemselectedStoreLbId;
	private Div rewardTierDivId,memTierDivId,selRedeemPromoDivId,selIssuePromoDivId,resetDivId,giftAmtExpDivId,giftMemExpDivId,giftAmtDivId,
	giftValidityDivId,disallowPromoDivId,secndDivId,disallowIssuRempDivId,excludeProductsDivId,additionSettingtoSBtoOc, redemptionDivId, selectedStoreDivId, redemEnableChkbDivId;
	private Textbox addCodeValueTbId;
	private Combobox storeNumbCmboBxId, redemstoreNumbCmboBxId;
	private Radio selectedStoresId,allStoresChkBxId;
	public LoyaltyAdditionalSettingsController() {
		userId = GetUser.getUserObj().getUserId();
		session = Sessions.getCurrent();
		Utility.ltyBreadCrumbFrom(4, OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype()));
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {

		super.doAfterCompose(comp);
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Create Loyalty Program (Step 4 of 6)","",style,true);
		clientTimeZone =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
		logger.info("in do after compose");
		Long prgmId=(Long) session.getAttribute("programId");
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
		setAvailablePromos(selIssuePromotionsLbId);
		setAvailablePromos(selRedeemPromotionsLbId);
		setDefaultStores();
		setDefaultExcDates();
		setDefaultRedemExcDates();
		setGftAmtExpDateValues();
		setGftMemExpDateValues();

		//edit mode
		if(prgmObj != null) {
			/*if(prgmObj.getRewardExpiryFlag()==OCConstants.FLAG_YES) {
				setRewardExpiryTier();
				//earnedRwrdExpiryCbId.setChecked(true);
				rewardTierDivId.setVisible(true);
			}
			else {
				//earnedRwrdExpiryCbId.setChecked(false);
				rewardTierDivId.setVisible(false);
			}*/
			if(prgmObj.getMembershipExpiryFlag()==OCConstants.FLAG_YES) {
				setMemExpiryTier();
				memValidityCbId.setChecked(true);
				memTierDivId.setVisible(true);
				if(prgmObj.getTierEnableFlag() == OCConstants.FLAG_YES) {
					resetDivId.setVisible(true);
				}
				else {
					resetDivId.setVisible(false);
				}
			}else {
				memValidityCbId.setChecked(false);
				memTierDivId.setVisible(false);
				resetDivId.setVisible(false);
				resetValidCbId.setChecked(false);
			}
			if(prgmObj.getMbrshipExpiryOnLevelUpgdFlag()==OCConstants.FLAG_YES) {
				resetValidCbId.setChecked(true);
			}else {
				resetValidCbId.setChecked(false);
			}
			if(prgmObj.getGiftAmountExpiryFlag() == OCConstants.FLAG_YES) {
				giftAmtExpCbId.setChecked(true);
				giftAmtExpDivId.setVisible(true);
				List<Listitem> type = giftAmtExpTypeLbId.getItems();
				for(Listitem li : type) {
					if(prgmObj.getGiftAmountExpiryDateType().equalsIgnoreCase(li.getValue().toString())) {
						giftAmtExpTypeLbId.setSelectedItem(li);
						break;
					}
				}
				List<Listitem> value = giftAmtExpValueLbId.getItems();
				for(Listitem li : value) {
					if(prgmObj.getGiftAmountExpiryDateValue().toString().equalsIgnoreCase(li.getValue().toString())) {
						giftAmtExpValueLbId.setSelectedItem(li);
						break;
					}
				}

			}else {
				giftAmtExpCbId.setChecked(false);
				giftAmtExpDivId.setVisible(false);
				giftAmtExpTypeLbId.setSelectedIndex(0);
				giftAmtExpValueLbId.setSelectedIndex(0);
			}

			if(prgmObj.getGiftMembrshpExpiryFlag() == OCConstants.FLAG_YES) {
				giftmemExpCbId.setChecked(true);
				giftMemExpDivId.setVisible(true);
				List<Listitem> type = giftmemExpTypeLbId.getItems();
				for(Listitem li : type) {
					if(prgmObj.getGiftMembrshpExpiryDateType().equalsIgnoreCase(li.getValue().toString())) {
						giftmemExpTypeLbId.setSelectedItem(li);
						break;
					}
				}
				List<Listitem> value = giftMemExpValueLbId.getItems();
				for(Listitem li : value) {
					if(prgmObj.getGiftMembrshpExpiryDateValue().toString().equalsIgnoreCase(li.getValue().toString())) {
						giftMemExpValueLbId.setSelectedItem(li);
						break;
					}
				}

			}else {
				giftmemExpCbId.setChecked(false);
				giftMemExpDivId.setVisible(false);
				giftmemExpTypeLbId.setSelectedIndex(0);
				giftMemExpValueLbId.setSelectedIndex(0);
			}
			
			
			/*if(!OCConstants.LOYALTY_SERVICE_TYPE_OC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype())){
				giftAmtDivId.setVisible(false);
				giftValidityDivId.setVisible(false);
			}*/
			if(OCConstants.LOYALTY_SERVICE_TYPE_OC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype())){
				giftAmtDivId.setVisible(true);
				giftValidityDivId.setVisible(true);
                secndDivId.setVisible(true);
				disallowIssuRempDivId.setVisible(true);
				additionSettingtoSBtoOc.setVisible(true);
				if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE.equalsIgnoreCase(prgmObj.getMembershipType())){
					giftAmtDivId.setVisible(false);
					giftValidityDivId.setVisible(false);
				}
				else if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD.equalsIgnoreCase(prgmObj.getMembershipType())){
					giftAmtDivId.setVisible(true);
					giftValidityDivId.setVisible(true);
				}
			}
			else if(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype())&&GetUser.getUserObj().isEnableLoyaltyExtraction()){{
				secndDivId.setVisible(true);
				disallowIssuRempDivId.setVisible(false);
				additionSettingtoSBtoOc.setVisible(true);
			  }
			}
			
			setExistingExclusions(prgmId);
		}

	}//doAfterCompose()

	private void setGftMemExpDateValues() {
		Listitem li=null;
		if(giftmemExpTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_YEAR)) {
			Components.removeAllChildren(giftMemExpValueLbId);
			logger.info("in Years");
			for(int i=1;i<=10;i++) {
				li=new Listitem(i+"");
				li.setValue(i);
				li.setParent(giftMemExpValueLbId);
				giftMemExpValueLbId.setSelectedIndex(0);
			}
		}else if(giftmemExpTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_MONTH)) {
			Components.removeAllChildren(giftMemExpValueLbId);
			logger.info("in hours");
			for(int i=1;i<=12;i++) {
				li=new Listitem(i+"");
				li.setValue(i);
				li.setParent(giftMemExpValueLbId);
				giftMemExpValueLbId.setSelectedIndex(0);
			}
		}
	}//setGftMemExpDateValues()

	private void setGftAmtExpDateValues() {
		Listitem li=null;
		if(giftAmtExpTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_YEAR)) {
			Components.removeAllChildren(giftAmtExpValueLbId);
			logger.info("in Years");
			for(int i=1;i<=10;i++) {
				li=new Listitem(i+"");
				li.setValue(i);
				li.setParent(giftAmtExpValueLbId);
				giftAmtExpValueLbId.setSelectedIndex(0);
			}
		}else if(giftAmtExpTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_MONTH)) {
			Components.removeAllChildren(giftAmtExpValueLbId);
			logger.info("in Months");
			for(int i=1;i<=12;i++) {
				li=new Listitem(i+"");
				li.setValue(i);
				li.setParent(giftAmtExpValueLbId);
				giftAmtExpValueLbId.setSelectedIndex(0);
			}
		}
	}//setGftAmtExpDateValues()

	public void onSelect$giftAmtExpTypeLbId() {
		setGftAmtExpDateValues();
	}//onSelect$giftAmtExpTypeLbId()

	public void onSelect$giftmemExpTypeLbId() {
		setGftMemExpDateValues();
	}//onSelect$giftmemExpTypeLbId()

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

	private void setExistingExclusions(Long prgmId) {
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		LoyaltyProgramExclusion loyaltyProgramExclusion=ltyPrgmSevice.getExclusionObj(prgmId);
		if(loyaltyProgramExclusion != null) {
			if(loyaltyProgramExclusion.getIssuanceWithPromoFlag()==OCConstants.FLAG_YES) {
				disallowCbId.setChecked(true);
				selIssuePromoDivId.setVisible(true);
				String promoStr= loyaltyProgramExclusion.getIssuancePromoIdStr();
				if(promoStr != null && promoStr.length() != 0) {
					if(promoStr.equalsIgnoreCase(OCConstants.LOYALTY_PROMO_EXCLUSION_ALL)){
						selIssuePromotionsLbId.selectAll();
					}
					else {
						String[] promoStrArr = promoStr.split(Constants.ADDR_COL_DELIMETER);
						Set promoSet = new HashSet();
						selIssuePromotionsLbId.setSelectedIndex(-1);
						List<Listitem> items = selIssuePromotionsLbId.getItems();
						for (Listitem li : items) {
							for(String promo:promoStrArr) {
								if(promo.equalsIgnoreCase(li.getValue().toString())) {
									promoSet.add(li);
								}
							}
						}
						selIssuePromotionsLbId.setSelectedItems(promoSet);
					}
				}
			}
			if(loyaltyProgramExclusion.getRedemptionWithPromoFlag()==OCConstants.FLAG_YES) {
				disallowRedeemCbId.setChecked(true);
				selRedeemPromoDivId.setVisible(true);
				String promoredeemStr= loyaltyProgramExclusion.getRedemptionPromoIdStr();
				if(promoredeemStr != null && promoredeemStr.length() != 0) {
					if(promoredeemStr.equalsIgnoreCase(OCConstants.LOYALTY_PROMO_EXCLUSION_ALL)){
						selRedeemPromotionsLbId.selectAll();
					}
					else {
						String[] promoStrArr = promoredeemStr.split(Constants.ADDR_COL_DELIMETER);
						Set promoSet = new HashSet();
						selRedeemPromotionsLbId.setSelectedIndex(-1);
						List<Listitem> items = selRedeemPromotionsLbId.getItems();
						for (Listitem li : items) {
							for(String promo:promoStrArr) {
								if(promo.equalsIgnoreCase(li.getValue().toString())) {
									promoSet.add(li);
								}
							}
						}
						selRedeemPromotionsLbId.setSelectedItems(promoSet);
					}
				}
			}
			String redemStoreStr=loyaltyProgramExclusion.getSelectedStoreStr();
			if(redemStoreStr != null && redemStoreStr.length() != 0) {
				
				String[] storeStrArr = redemStoreStr.split(Constants.ADDR_COL_DELIMETER);
				for (String store : storeStrArr) {
					Listitem li=new Listitem();
					Listcell lc=new Listcell(store);
					lc.setParent(li);
					lc=new Listcell();
					Image delImg = new Image("/img/action_delete.gif");
					delImg.setStyle("cursor:pointer;margin-right:10px;");
					delImg.setTooltiptext("Delete Store");
					delImg.setAttribute("STORE_TYPE", "DELETE_STORE");
					delImg.addEventListener("onClick", this);
					delImg.setParent(lc);
					lc.setParent(li);
					li.setParent(redemselectedStoreLbId);
				}
				excludeRedeemCbId.setChecked(true);
				redemEnableChkbDivId.setVisible(true);
				selectedStoreDivId.setVisible(true);
				selectedStoresId.setChecked(true);
				redemstoreNumbCmboBxId.setVisible(true);
				redemselectedStoreLbId.setVisible(true);
				redemaddStoreImgId.setVisible(true);
				if(allStoresChkBxId.isChecked() || !excludeRedeemCbId.isChecked()) {
					selectedStoreDivId.setVisible(false);
					redemstoreNumbCmboBxId.setVisible(false);
					redemselectedStoreLbId.setVisible(false);
					redemaddStoreImgId.setVisible(false);
				}
			}
			if(loyaltyProgramExclusion.getAllStrChk()!=null && loyaltyProgramExclusion.getAllStrChk()==true) {
				excludeRedeemCbId.setChecked(true);
				redemEnableChkbDivId.setVisible(true);
				allStoresChkBxId.setChecked(true);
			}
			String storeStr=loyaltyProgramExclusion.getStoreNumberStr();
			if(storeStr != null && storeStr.length() != 0) {
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
			String itemStr=loyaltyProgramExclusion.getItemCatStr();
			String type= "";
			String codeType = "";
			if(itemStr != null && itemStr.length() != 0) {
				type="Item Category";
				String[] itemStrArr = itemStr.split(Constants.ADDR_COL_DELIMETER);
				codeType = OCConstants.LOYALTY_PRODUCT_ITEMCATEGORY;
				setCodeList(type,itemStrArr,codeType);
			}
			String classStr=loyaltyProgramExclusion.getClassStr();
			if(classStr != null && classStr.length() != 0) {
				type="Class";
				String[] classStrArr = classStr.split(Constants.ADDR_COL_DELIMETER);
				codeType = OCConstants.LOYALTY_PRODUCT_CLASS;
				setCodeList(type,classStrArr,codeType);
			}

			String skuStr=loyaltyProgramExclusion.getSkuNumStr();
			if(skuStr != null && skuStr.length() != 0) {
				type="SKU Number";
				String[] skuStrArr = skuStr.split(Constants.ADDR_COL_DELIMETER);
				codeType = OCConstants.LOYALTY_PRODUCT_SKUNUMBER;
				setCodeList(type,skuStrArr,codeType);
			}

			String deptCodeStr=loyaltyProgramExclusion.getDeptCodeStr();
			if(deptCodeStr != null && deptCodeStr.length() != 0) {
				type="Department Code";
				String[] deptCodeStrArr = deptCodeStr.split(Constants.ADDR_COL_DELIMETER);
				codeType = OCConstants.LOYALTY_PRODUCT_DEPARTMENTCODE;
				setCodeList(type,deptCodeStrArr,codeType);
			}

			String dcsStr=loyaltyProgramExclusion.getDcsStr();
			if(dcsStr != null && dcsStr.length() != 0) {
				type="DCS";
				String[] dcsStrArr = dcsStr.split(Constants.ADDR_COL_DELIMETER);
				codeType = OCConstants.LOYALTY_PRODUCT_DCS;
				setCodeList(type,dcsStrArr,codeType);
			}

			String vendorStr=loyaltyProgramExclusion.getVendorStr();
			if(vendorStr != null && vendorStr.length() != 0) {
				type="Vendor Code";
				String[] vendorStrArr = vendorStr.split(Constants.ADDR_COL_DELIMETER);
				codeType = OCConstants.LOYALTY_PRODUCT_VENDORCODE;
				setCodeList(type,vendorStrArr,codeType);
			}

			String subClassStr=loyaltyProgramExclusion.getSubClassStr();
			if(subClassStr != null && subClassStr.length() != 0) {
				type="Subclass";
				String[] subClassStrArr = subClassStr.split(Constants.ADDR_COL_DELIMETER);
				codeType = OCConstants.LOYALTY_PRODUCT_SUBCLASS;
				setCodeList(type,subClassStrArr,codeType);
			}

			String dateStr=loyaltyProgramExclusion.getDateStr();
			if(dateStr != null && dateStr.length() != 0) {
				String[] dateStrrArr = dateStr.split(Constants.ADDR_COL_DELIMETER);
				for (String date : dateStrrArr) {
					Listitem li=new Listitem();
					Listcell lc=new Listcell(date);
					lc.setParent(li);
					lc=new Listcell();
					Image delImg = new Image("/img/action_delete.gif");
					delImg.setStyle("cursor:pointer;margin-right:10px;");
					delImg.setTooltiptext("Delete Date");
					delImg.setAttribute("DATE_TYPE", "DELETE_DATE");
					delImg.addEventListener("onClick", this);
					delImg.setParent(lc);
					lc.setParent(li);
					li.setParent(dateLbId);
				}
			}
			
			String exclRedemdateStr=loyaltyProgramExclusion.getExclRedemDateStr();
			if(exclRedemdateStr != null && exclRedemdateStr.length() != 0) {
				String[] dateStrrArr = exclRedemdateStr.split(Constants.ADDR_COL_DELIMETER);
				for (String date : dateStrrArr) {
					Listitem li=new Listitem();
					Listcell lc=new Listcell(date);
					lc.setParent(li);
					lc=new Listcell();
					Image delImg = new Image("/img/action_delete.gif");
					delImg.setStyle("cursor:pointer;margin-right:10px;");
					delImg.setTooltiptext("Delete Date");
					delImg.setAttribute("DATE_REDEM_TYPE", "DELETE_REDEM_DATE");
					delImg.addEventListener("onClick", this);
					delImg.setParent(lc);
					lc.setParent(li);
					li.setParent(redemptionDateLbId);
				}
			}

		}

	}//setExistingExclusions()

	private void setCodeList(String type, String[] codeStrArr, String codeType) {
		for (String code : codeStrArr) {
			Listitem li=new Listitem();
			li.setValue(codeType);
			Listcell lc=new Listcell(type);
			lc.setParent(li);
			lc=new Listcell(code);
			lc.setParent(li);
			lc=new Listcell();
			Image delImg = new Image("/img/action_delete.gif");
			delImg.setStyle("cursor:pointer;margin-right:10px;");
			delImg.setTooltiptext("Delete Product");
			delImg.setAttribute("PRODUCT_TYPE", "DELETE_PRODUCT");
			delImg.addEventListener("onClick", this);
			delImg.setParent(lc);
			lc.setParent(li);
			li.setParent(productCodeLbId);
		}
	}//setCodeList()

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
			comboItem = new Comboitem(eachStore.getHomeStoreId());
			comboItem.setParent(redemstoreNumbCmboBxId);
			//logger.info("comboItem"+storeNumbCmboBxId.getChildren());
		}

	}//setDefaultStores()

	private void setDefaultExcDates() {
		Components.removeAllChildren(excYearLbId);
		Listitem li = new Listitem("All Years");
		li.setValue("All");
		li.setSelected(true);
		li.setParent(excYearLbId);
		int year = Calendar.getInstance().get(Calendar.YEAR);
		for (int i = 1; i <=10; i++) {
			li = new Listitem(year+"");
			li.setValue(year+"");
			li.setParent(excYearLbId);
			year = year + 1;
		}
		onSelect$excMonthLbId();
		onSelect$excYearLbId();
	}//setDefaultExcDates()
	
	private void setDefaultRedemExcDates() {
		Components.removeAllChildren(excRedemptionYearLbId);
		Listitem li = new Listitem("All Years");
		li.setValue("All");
		li.setSelected(true);
		li.setParent(excRedemptionYearLbId);
		int year = Calendar.getInstance().get(Calendar.YEAR);
		for (int i = 1; i <=10; i++) {
			li = new Listitem(year+"");
			li.setValue(year+"");
			li.setParent(excRedemptionYearLbId);
			year = year + 1;
		}
		onSelect$excRedemptionMonthLbId();
		onSelect$excRedemptionYearLbId();
	}//setDefaultRedemExcDates


	public void onClick$addCodeImgId() {
		String selectdCategory=itemCategoryLbId.getSelectedItem().getValue().toString();
		String productCode=addCodeValueTbId.getValue().trim();

		if(selectdCategory==null || selectdCategory.isEmpty() ||productCode.isEmpty() ||productCode ==null ) {
			MessageUtil.setMessage("Please enter  code number.", "red", "TOP");
			return;
		}
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		SkuFile skuFile = ltyPrgmSevice.getSkuFile(selectdCategory,productCode,userId);

		Listitem li=null;
		Listcell lc=null;

		List<Listitem> listItem= productCodeLbId.getItems();
		boolean existedCode = false;
		if(productCodeLbId.getItemCount() > 0) {
			for (Listitem eachItem : listItem) {
				Listcell listcell=(Listcell)eachItem.getFirstChild();
				String type = listcell.getLabel();
				listcell=(Listcell)eachItem.getChildren().get(1);
				String code = listcell.getLabel();
				if(type.equalsIgnoreCase(itemCategoryLbId.getSelectedItem().getLabel()) && 
						code.equalsIgnoreCase(addCodeValueTbId.getValue().trim())) {
					existedCode = true;
					break;
				}

			}
		}


		if(!existedCode){

			if(skuFile == null) {
				if(Messagebox.show("This product code was not found in your inventory. Would you like to add it anyway? ", "Confirm" ,Messagebox.OK | Messagebox.CANCEL, 
						Messagebox.QUESTION) != Messagebox.OK) {
					return;
				}
			}

			li = new Listitem();
			li.setValue(selectdCategory);

			lc = new Listcell(itemCategoryLbId.getSelectedItem().getLabel().toString());
			lc.setParent(li);

			lc = new Listcell(productCode);
			lc.setParent(li);


			lc = new Listcell();
			Image delImg = new Image("/img/action_delete.gif");
			delImg.setStyle("cursor:pointer;margin-right:10px;");
			delImg.setTooltiptext("Delete Product");
			delImg.setAttribute("PRODUCT_TYPE", "DELETE_PRODUCT");
			delImg.addEventListener("onClick", this);

			delImg.setParent(lc);
			lc.setParent(li);
			li.setParent(productCodeLbId);

		}
		addCodeValueTbId.setValue("");

	}//onClick$addCodeImgId()

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

	@Override
	public void onEvent(Event event) throws Exception {


		super.onEvent(event);

		if(event.getTarget() instanceof Image) {

			Image tempImg = (Image)event.getTarget();

			if("DELETE_STORE".equalsIgnoreCase((String) tempImg.getAttribute("EXCLUSION_TYPE"))) {
				logger.info("delete store here ");
				Listitem storeItem = (Listitem)tempImg.getParent().getParent();
				selectedStoreLbId.removeChild(storeItem);

			}
			if("DELETE_PRODUCT".equalsIgnoreCase((String) tempImg.getAttribute("PRODUCT_TYPE")) ) {
				Listitem productItem = (Listitem)tempImg.getParent().getParent();
				productCodeLbId.removeChild(productItem);

			}

			if("DELETE_DATE".equalsIgnoreCase((String) tempImg.getAttribute("DATE_TYPE")) ) {
				Listitem dateItem = (Listitem)tempImg.getParent().getParent();
				dateLbId.removeChild(dateItem);

			}
			
			if("DELETE_REDEM_DATE".equalsIgnoreCase((String) tempImg.getAttribute("DATE_REDEM_TYPE")) ) {
				Listitem dateItem = (Listitem)tempImg.getParent().getParent();
				redemptionDateLbId.removeChild(dateItem);

			}
			
			if("DELETE_STORE".equalsIgnoreCase((String) tempImg.getAttribute("STORE_TYPE")) ) {
				logger.debug("delete store here ");
				//TODO  show message here
				Listitem storeItem = (Listitem)tempImg.getParent().getParent();
				redemselectedStoreLbId.removeChild(storeItem);
				
			}

		}
		else if(event.getTarget() instanceof Listbox) {

			Listbox lb=(Listbox) event.getTarget();
			if(lb.getAttribute("type").equals("dateType")) {
				logger.info("lb.getParent().getParent()"+lb.getParent().getParent());
				Hbox hbox=(Hbox) lb.getParent();
				Listbox valueLb=(Listbox) hbox.getLastChild();
				Components.removeAllChildren(valueLb);
				setDateValues(valueLb, lb.getSelectedItem().getValue().toString());

			}


		}
		else if(event.getTarget() instanceof Listitem) {
			Listitem li =( Listitem)event.getTarget();
			Listbox lb=(Listbox)li.getParent();
			if(li.isSelected()) {
				lb.selectAll();
			}else {
				lb.clearSelection();
			}
		}
	}//onEvent()

	public void onSelect$excMonthLbId() {
		if(excMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Jan") ||
				excMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Mar") ||
				excMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("May") ||
				excMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Jul") ||
				excMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Aug") ||
				excMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Oct") ||
				excMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Dec")) {
			Components.removeAllChildren(excDateLbId);
			for(int i = 1; i <= 31; i++) {
				Listitem li =new Listitem(i+"");
				li.setValue(i+"");
				if(i == 1) {
					li.setSelected(true);
				}
				li.setParent(excDateLbId);
			}
		}
		else if(excMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Apr") ||
				excMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Jun") ||
				excMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Sep") ||
				excMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Nov")) {
			Components.removeAllChildren(excDateLbId);
			for(int i = 1; i <= 30; i++) {
				Listitem li =new Listitem(i+"");
				li.setValue(i+"");
				if(i == 1) {
					li.setSelected(true);
				}
				li.setParent(excDateLbId);
			}
		}

		else if(excMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Feb")) {
			Components.removeAllChildren(excDateLbId);
			if(!excYearLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
				int year = Integer.parseInt(excYearLbId.getSelectedItem().getValue().toString());
				if(year % 400 == 0 || (year % 100 != 0 && year % 4 == 0)) {
					for(int i = 1; i <= 29; i++) {
						Listitem li =new Listitem(i+"");
						li.setValue(i+"");
						if(i == 1) {
							li.setSelected(true);
						}
						li.setParent(excDateLbId);
					}
				}
				else {
					for(int i = 1; i <= 28; i++) {
						Listitem li =new Listitem(i+"");
						li.setValue(i+"");
						if(i == 1) {
							li.setSelected(true);
						}
						li.setParent(excDateLbId);
					}
				}
			}
			else {
				for(int i = 1; i <= 28; i++) {
					Listitem li =new Listitem(i+"");
					li.setValue(i+"");
					if(i == 1) {
						li.setSelected(true);
					}
					li.setParent(excDateLbId);
				}
			}
		}
	}//onSelect$excMonthLbId()
	
	public void onSelect$excRedemptionMonthLbId() {
		if(excRedemptionMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Jan") ||
				excRedemptionMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Mar") ||
				excRedemptionMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("May") ||
				excRedemptionMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Jul") ||
				excRedemptionMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Aug") ||
				excRedemptionMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Oct") ||
				excRedemptionMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Dec")) {
			Components.removeAllChildren(excRedemptionDateLbId);
			Listitem li = new Listitem("Day");
			li.setValue("Day");
			li.setSelected(true);
			li.setParent(excRedemptionDateLbId);
			for(int i = 1; i <= 31; i++) {
				li =new Listitem(i+"");
				li.setValue(i+"");
				if(i == 1) {
					li.setSelected(true);
				}
				li.setParent(excRedemptionDateLbId);
			}
		}
		else if(excRedemptionMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Apr") ||
				excRedemptionMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Jun") ||
				excRedemptionMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Sep") ||
				excRedemptionMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Nov")) {
			Components.removeAllChildren(excRedemptionDateLbId);
			Listitem li = new Listitem("Day");
			li.setValue("Day");
			li.setSelected(true);
			li.setParent(excRedemptionDateLbId);
			for(int i = 1; i <= 30; i++) {
				li =new Listitem(i+"");
				li.setValue(i+"");
				if(i == 1) {
					li.setSelected(true);
				}
				li.setParent(excRedemptionDateLbId);
			}
			
		}
		else if(excRedemptionMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Feb")) {
			Components.removeAllChildren(excRedemptionDateLbId);
			Listitem li = new Listitem("Day");
			li.setValue("Day");
			li.setSelected(true);
			li.setParent(excRedemptionDateLbId);
			if(!excRedemptionYearLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
				int year = Integer.parseInt(excRedemptionYearLbId.getSelectedItem().getValue().toString());
				if(year % 400 == 0 || (year % 100 != 0 && year % 4 == 0)) {
					for(int i = 1; i <= 29; i++) {
						li =new Listitem(i+"");
						li.setValue(i+"");
						if(i == 1) {
							li.setSelected(true);
						}
						li.setParent(excRedemptionDateLbId);
					}
					
				}
				else {
					for(int i = 1; i <= 28; i++) {
						li =new Listitem(i+"");
						li.setValue(i+"");
						if(i == 1) {
							li.setSelected(true);
						}
						li.setParent(excRedemptionDateLbId);
					}
				}
			}
			else {
				for(int i = 1; i <= 28; i++) {
					li =new Listitem(i+"");
					li.setValue(i+"");
					if(i == 1) {
						li.setSelected(true);
					}
					li.setParent(excRedemptionDateLbId);
				}
			}
		}
	}//onSelect$excRedemptionMonthLbId()

	public void onSelect$excYearLbId() {
		if(excMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Feb")) {
			Components.removeAllChildren(excDateLbId);
			if(!excYearLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
				int year = Integer.parseInt(excYearLbId.getSelectedItem().getValue().toString());
				if(year % 400 == 0 || (year % 100 != 0 && year % 4 == 0)) {
					for(int i = 1; i <= 29; i++) {
						Listitem li =new Listitem(i+"");
						li.setValue(i+"");
						if(i == 1) {
							li.setSelected(true);
						}
						li.setParent(excDateLbId);
					}
				}
				else {
					for(int i = 1; i <= 28; i++) {
						Listitem li =new Listitem(i+"");
						li.setValue(i+"");
						if(i == 1) {
							li.setSelected(true);
						}
						li.setParent(excDateLbId);
					}
				}
			}
			else {
				for(int i = 1; i <= 28; i++) {
					Listitem li =new Listitem(i+"");
					li.setValue(i+"");
					if(i == 1) {
						li.setSelected(true);
					}
					li.setParent(excDateLbId);
				}
			}
		}
	}//onSelect$excYearLbId()
	
	public void onSelect$excRedemptionYearLbId() {
		if(excRedemptionMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Feb")) {
			Components.removeAllChildren(excRedemptionDateLbId);
			Listitem li = new Listitem("Day");
			li.setValue("Day");
			li.setSelected(true);
			li.setParent(excRedemptionDateLbId);
			if(!excRedemptionYearLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
				int year = Integer.parseInt(excRedemptionYearLbId.getSelectedItem().getValue().toString());
				if(year % 400 == 0 || (year % 100 != 0 && year % 4 == 0)) {
					for(int i = 1; i <= 29; i++) {
						li =new Listitem(i+"");
						li.setValue(i+"");
						if(i == 1) {
							li.setSelected(true);
						}
						li.setParent(excRedemptionDateLbId);
					}
				}
				else {
					for(int i = 1; i <= 28; i++) {
						li =new Listitem(i+"");
						li.setValue(i+"");
						if(i == 1) {
							li.setSelected(true);
						}
						li.setParent(excRedemptionDateLbId);
					}
				}
			}
			else {
				for(int i = 1; i <= 28; i++) {
					li =new Listitem(i+"");
					li.setValue(i+"");
					if(i == 1) {
						li.setSelected(true);
					}
					li.setParent(excRedemptionDateLbId);
				}
			}
		}
	}//onSelect$excRedemptionYearLbId


	public void onClick$addDateImgId()  {
		String dateStr = "";
		if(!excYearLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
			dateStr = excMonthLbId.getSelectedItem().getValue().toString()+" "+excDateLbId.getSelectedItem().getValue().toString()+", "+
					excYearLbId.getSelectedItem().getValue().toString();
		}else {
			dateStr = excMonthLbId.getSelectedItem().getValue().toString()+" "+excDateLbId.getSelectedItem().getValue().toString();
		}

		List<Listitem> listItem= dateLbId.getItems();
		boolean existedDate = false;
		if(dateLbId.getItemCount() > 0) {
			for (Listitem eachItem : listItem) {
				if(eachItem.getLabel().equalsIgnoreCase(dateStr)) {
					existedDate = true;
					break;
				}

			}
		}
		if(!existedDate){
			Listitem li=new Listitem();
			Listcell lc=null;

			lc = new Listcell(dateStr);

			li.appendChild(lc);
			lc = new Listcell();
			Image delImg = new Image("/img/action_delete.gif");
			delImg.setStyle("cursor:pointer;margin-right:10px;");
			delImg.setTooltiptext("Delete Date");
			delImg.setAttribute("DATE_TYPE", "DELETE_DATE");
			delImg.addEventListener("onClick", this);

			delImg.setParent(lc);
			lc.setParent(li);

			li.setParent(dateLbId);
		}
		excMonthLbId.setSelectedIndex(0);
		excDateLbId.setSelectedIndex(0);
		excYearLbId.setSelectedIndex(0);
	}//onClick$addDateImgId()
	
	public void onClick$addRedemptionDateImgId()  {
		String dateStr = "";
		
		if(!excRedemptionYearLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All") && excRedemptionMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Month") && excRedemptionDateLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Day")) {
			
			dateStr = excRedemptionYearLbId.getSelectedItem().getValue().toString();
			
		} else if (!excRedemptionYearLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All") && !excRedemptionMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Month") && excRedemptionDateLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Day")) {
			
			dateStr = excRedemptionMonthLbId.getSelectedItem().getValue().toString()+"-"+excRedemptionYearLbId.getSelectedItem().getValue().toString();
			
		} else if(excRedemptionYearLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All") && !excRedemptionMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Month") && excRedemptionDateLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Day")) {
			
			dateStr = excRedemptionMonthLbId.getSelectedItem().getValue().toString();
			
		} else if(excRedemptionYearLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All") && !excRedemptionMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Month") && !excRedemptionDateLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Day"))  {
			
			dateStr = excRedemptionMonthLbId.getSelectedItem().getValue().toString()+" "+excRedemptionDateLbId.getSelectedItem().getValue().toString();
			
		} else if(!excRedemptionYearLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All") && !excRedemptionMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Month") && !excRedemptionDateLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Day")) {
			
			dateStr = excRedemptionMonthLbId.getSelectedItem().getValue().toString()+" "+excRedemptionDateLbId.getSelectedItem().getValue().toString()+", "+
					excRedemptionYearLbId.getSelectedItem().getValue().toString();
			
		} else if((excRedemptionYearLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All") && excRedemptionMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Month") && excRedemptionDateLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Day")) || 
				     ((excRedemptionYearLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All") && excRedemptionMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Month") && !excRedemptionDateLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Day"))) || 
				         ((!excRedemptionYearLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All") && excRedemptionMonthLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Month") && !excRedemptionDateLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Day")))) {
			
			MessageUtil.setMessage("Please provide valid input.", "red", "TOP");
			return;
		}
		
		/*if(!excRedemptionYearLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
			dateStr = excRedemptionMonthLbId.getSelectedItem().getValue().toString()+" "+excRedemptionDateLbId.getSelectedItem().getValue().toString()+", "+
					excRedemptionYearLbId.getSelectedItem().getValue().toString();
		}else {
			dateStr = excRedemptionMonthLbId.getSelectedItem().getValue().toString()+" "+excRedemptionDateLbId.getSelectedItem().getValue().toString();
		}*/

		List<Listitem> listItem= redemptionDateLbId.getItems();
		boolean existedDate = false;
		if(redemptionDateLbId.getItemCount() > 0) {
			for (Listitem eachItem : listItem) {
				if(eachItem.getLabel().equalsIgnoreCase(dateStr)) {
					existedDate = true;
					break;
				}

			}
		}
		if(!existedDate){
			Listitem li=new Listitem();
			Listcell lc=null;

			lc = new Listcell(dateStr);

			li.appendChild(lc);
			lc = new Listcell();
			Image delImg = new Image("/img/action_delete.gif");
			delImg.setStyle("cursor:pointer;margin-right:10px;");
			delImg.setTooltiptext("Delete Date");
			delImg.setAttribute("DATE_REDEM_TYPE", "DELETE_REDEM_DATE");
			delImg.addEventListener("onClick", this);

			delImg.setParent(lc);
			lc.setParent(li);

			li.setParent(redemptionDateLbId);
		}
		excRedemptionMonthLbId.setSelectedIndex(0);
		excRedemptionDateLbId.setSelectedIndex(0);
		excRedemptionYearLbId.setSelectedIndex(0);
	}//onClick$addRedemptionDateImgId()


	private void setAvailablePromos(Listbox id) {
		Components.removeAllChildren(id);
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		Long orgId=ltyPrgmSevice.getOrgId(userId) ;
		List<Coupons> couponList=ltyPrgmSevice.getCouponList(orgId);
		Listitem li=null;
		li=new Listitem("All Promos");
		li.setValue("All Promos");
		li.addEventListener("onClick",this);
		li.setParent(id);
		if(couponList!=null) {
			for(Coupons coupons:couponList) {
				li = new Listitem(coupons.getCouponName(),coupons.getCouponId());
				li.setParent(id);
			}
		}

	}//setAvailablePromos()

	private void setMemExpiryTier() {
		Components.removeAllChildren(memTierLbId);
		Long prgmId=(Long) session.getAttribute("programId");
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

						Listcell lc=new Listcell();
						Hbox hbox = new Hbox();
						Label lb=new Label("after");
						lb.setStyle("display:inline-block;margin-top:5px;");
						lb.setParent(hbox);
						Listbox typelb = new Listbox();
						typelb.setMold("select");
						Listitem yearLi=new Listitem();
						yearLi.setLabel("Year(s)");
						yearLi.setValue(OCConstants.LOYALTY_TYPE_YEAR);
						yearLi.setParent(typelb);
						Listitem monthLi=new Listitem();
						monthLi.setLabel("Month(s)");
						monthLi.setValue(OCConstants.LOYALTY_TYPE_MONTH);
						monthLi.setParent(typelb);
						typelb.setSelectedItem(yearLi);
						typelb.addEventListener("onSelect", this);
						typelb.setAttribute("type", "dateType");
						typelb.setParent(hbox);
						List<Listitem> list=null;
						if(loyaltyProgramTier.getMembershipExpiryDateType()==null || loyaltyProgramTier.getMembershipExpiryDateType().isEmpty()) {
							typelb.setSelectedIndex(0);
						}
						else {
							list=typelb.getItems();
							for(Listitem listItem :list) {
								if(loyaltyProgramTier.getMembershipExpiryDateType().equalsIgnoreCase(listItem.getValue().toString())) {
									typelb.setSelectedItem(listItem);
									break;
								}
							}
						}

						Listbox valuelb = new Listbox();
						valuelb.setMold("select");
						valuelb.setWidth("20px");
						setDateValues(valuelb,typelb.getSelectedItem().getValue().toString());
						valuelb.addEventListener("onSelect", this);
						valuelb.setAttribute("type", "dateValue");
						valuelb.setParent(hbox);
						list=valuelb.getItems();

						if(loyaltyProgramTier.getMembershipExpiryDateValue()==null || loyaltyProgramTier.getMembershipExpiryDateValue().toString().isEmpty()) {
							valuelb.setSelectedIndex(0);
						}
						else {
							list=valuelb.getItems();
							for(Listitem listItem :list) {
								if(loyaltyProgramTier.getMembershipExpiryDateValue().toString().equalsIgnoreCase(listItem.getValue().toString())) {
									valuelb.setSelectedItem(listItem);
									break;
								}
							}
						}

						hbox.setParent(lc);
						li.appendChild(lc);

						li.setParent(memTierLbId);
						li.setValue(loyaltyProgramTier.getTierId());

						isExists=true;
					}
				}
			}
			if(!isExists) {
				li = new Listitem();
				li.appendChild(new Listcell(tier));
				li.appendChild(new Listcell("--"));
				li.appendChild(new Listcell("--"));
				li.setParent(memTierLbId);
			}
		}

	}//setMemExpiryTier()

	public void onCheck$disallowCbId() {

		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		Long orgId=ltyPrgmSevice.getOrgId(userId) ;
		List<Coupons> couponList=ltyPrgmSevice.getCouponList(orgId);
		logger.info("couponList"+couponList);
		if(disallowCbId.isChecked()) {
			logger.info("couponList2"+couponList);
			if(couponList!= null)  {
				setAvailablePromos(selIssuePromotionsLbId);
				selIssuePromoDivId.setVisible(true); 
			}
			else {
				logger.info("couponList3"+couponList);
				MessageUtil.setMessage("No Promos exists", "color:red", "TOP");
				disallowCbId.setChecked(false);
			}
		}else {
			selIssuePromoDivId.setVisible(false);
		}
	}//onCheck$disallowCbId()

	public void onCheck$disallowRedeemCbId() {

		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		Long orgId=ltyPrgmSevice.getOrgId(userId) ;
		List<Coupons> couponList=ltyPrgmSevice.getCouponList(orgId);
		if(disallowRedeemCbId.isChecked()) {
			if(couponList!= null)  {
				setAvailablePromos(selRedeemPromotionsLbId);
				selRedeemPromoDivId.setVisible(true); 
			}
			else {
				MessageUtil.setMessage("No Promos exists", "color:red", "TOP");
				disallowRedeemCbId.setChecked(false);
			}
		}else {
			selRedeemPromoDivId.setVisible(false);
		}
	}//onCheck$disallowRedeemCbId()
	
    private void setVisibleItems(Listbox lstBox, boolean flag) {
		
		List<Listitem> chaildItemLst = lstBox.getItems();
		for (Listitem listitem : chaildItemLst) {
			Image delImg = ((Image)((Listcell)listitem.getLastChild()).getFirstChild());
			delImg.setVisible(flag);
		}
	}

    private Image redemaddStoreImgId;
	public void onCheck$selectedStoresId() {
		if(selectedStoresId.isChecked()) {
			selectedStoreDivId.setVisible(true);
			redemstoreNumbCmboBxId.setVisible(true);
			redemselectedStoreLbId.setVisible(true);
			setVisibleItems(redemselectedStoreLbId,true);
			redemaddStoreImgId.setVisible(true);
			
		}else {
			
			selectedStoreDivId.setVisible(false);
			redemstoreNumbCmboBxId.setVisible(false);
			redemselectedStoreLbId.setVisible(false);
			setVisibleItems(redemselectedStoreLbId,false);
			redemaddStoreImgId.setVisible(false);
		}
	}
	
	public void onCheck$excludeRedeemCbId( ) {
		if(excludeRedeemCbId.isChecked()) {
			redemEnableChkbDivId.setVisible(true);
			if(selectedStoresId.isChecked()) {
				selectedStoreDivId.setVisible(true);
				redemstoreNumbCmboBxId.setVisible(true);
				redemselectedStoreLbId.setVisible(true);
				setVisibleItems(redemselectedStoreLbId,true);
				redemaddStoreImgId.setVisible(true);
				
			}
		} else {
			redemEnableChkbDivId.setVisible(false);
			selectedStoreDivId.setVisible(false);
			redemstoreNumbCmboBxId.setVisible(false);
			redemselectedStoreLbId.setVisible(false);
			setVisibleItems(redemselectedStoreLbId,false);
			redemaddStoreImgId.setVisible(false);
		}
	}
	
	public void onCheck$allStoresChkBxId(){
		selectedStoreDivId.setVisible(false);
		redemstoreNumbCmboBxId.setVisible(false);
		redemselectedStoreLbId.setVisible(false);
		setVisibleItems(redemselectedStoreLbId,false);
		redemaddStoreImgId.setVisible(false);
		
	}
	
	public void onClick$redemaddStoreImgId() {
		
		 if(redemstoreNumbCmboBxId.getValue() == null || ((String) redemstoreNumbCmboBxId.getValue()).trim().length() == 0) {
			 MessageUtil.setMessage("Please provide store number.", "red", "TOP");
				return;
		 }
		 LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
			Long orgId=ltyPrgmSevice.getOrgId(userId) ;

			OrganizationStores orgStores = ltyPrgmSevice.getStore(orgId,redemstoreNumbCmboBxId.getValue());
		 
		 Listitem tempItem = null;
		 Listcell tempCell = null;
		 if(redemselectedStoreLbId.getItemCount() == 0) {
			 tempItem = new Listitem();
			 tempCell = new Listcell(redemstoreNumbCmboBxId.getValue().trim());
			 tempCell.setParent(tempItem);
			 
			 
			 tempCell = new Listcell();
			 Image delImg = new Image("/img/action_delete.gif");
			 delImg.setStyle("cursor:pointer;margin-right:10px;");
			 delImg.setTooltiptext("Delete Store");
			 delImg.setAttribute("STORE_TYPE", "DELETE_STORE");
			 delImg.addEventListener("onClick", this);
			 
			 delImg.setParent(tempCell);
			 tempCell.setParent(tempItem);
			 tempItem.setParent(redemselectedStoreLbId);
		 }
		 
		 List<Listitem> listItem= redemselectedStoreLbId.getItems();
		 boolean existedStore = false;
		 for (Listitem eachItem : listItem) {
			 if(eachItem.getLabel().equals(redemstoreNumbCmboBxId.getValue().trim())) {
				 existedStore = true;
				 break;
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
			 tempCell = new Listcell(redemstoreNumbCmboBxId.getValue().trim());
			 tempCell.setParent(tempItem);
			 
			 
			 tempCell = new Listcell();
			 Image delImg = new Image("/img/action_delete.gif");
			 delImg.setStyle("cursor:pointer;margin-right:10px;");
			 delImg.setTooltiptext("Delete Store");
			 delImg.setAttribute("STORE_TYPE", "DELETE_STORE");
			 delImg.addEventListener("onClick", this);
			 
			 delImg.setParent(tempCell);
			 tempCell.setParent(tempItem);
			 tempItem.setParent(redemselectedStoreLbId);
		 }
		 redemstoreNumbCmboBxId.setValue("");
		 
	 }

	/*public void onCheck$earnedRwrdExpiryCbId() {

		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		if(earnedRwrdExpiryCbId.isChecked()) {
			rewardTierDivId.setVisible(true);

			Long prgmId=(Long) session.getAttribute("programId");
			List<LoyaltyProgramTier> tierList=ltyPrgmSevice.getTierList(prgmId);
			if(tierList!=null) {
				setRewardExpiryTier();

			}else {
				Components.removeAllChildren(tierLbId);
				LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
				int noOfTiers=prgmObj.getNoOfTiers();
				Listitem li=null;
				for(int i=1;i<=noOfTiers;i++){
					String tier="Tier "+i;
					li = new Listitem();
					li.appendChild(new Listcell(tier));
					li.appendChild(new Listcell("--"));
					li.appendChild(new Listcell("--"));
					li.setParent(tierLbId);
				}
			}
		}
		else {
			rewardTierDivId.setVisible(false);
		}
	}//onCheck$earnedRwrdExpiryCbId()*/


	public void onCheck$memValidityCbId() {

		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		if (memValidityCbId.isChecked()) {
			memTierDivId.setVisible(true);


			Long prgmId = (Long) session.getAttribute("programId");
			LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
			if(prgmObj.getTierEnableFlag() == OCConstants.FLAG_YES){
				resetDivId.setVisible(true);
				resetValidCbId.setChecked(true);
			}
			else {
				resetDivId.setVisible(false);
				resetValidCbId.setChecked(false);
			}

			List<LoyaltyProgramTier> tierList = ltyPrgmSevice
					.getTierList(prgmId);
			if (tierList != null) {
				setMemExpiryTier();
			} else {
				Components.removeAllChildren(memTierLbId);

				int noOfTiers = prgmObj.getNoOfTiers();
				Listitem li = null;
				for (int i = 1; i <= noOfTiers; i++) {
					String tier = "Tier " + i;
					li = new Listitem();
					li.appendChild(new Listcell(tier));
					li.appendChild(new Listcell("--"));
					li.appendChild(new Listcell("--"));
					li.setParent(memTierLbId);
				}
			}
		}
		else {
			memTierDivId.setVisible(false);
			resetDivId.setVisible(false);
			resetValidCbId.setChecked(false);
		}
	}//onCheck$memValidityCbId()


	public void onCheck$giftAmtExpCbId() {
		if(giftAmtExpCbId.isChecked()) {
			giftAmtExpDivId.setVisible(true);
		}
		else {
			giftAmtExpDivId.setVisible(false);
		}

	}//onCheck$giftAmtExpCbId()

	public void onCheck$giftmemExpCbId() {
		if(giftmemExpCbId.isChecked()) {
			giftMemExpDivId.setVisible(true);
		}
		else {
			giftMemExpDivId.setVisible(false);
		}
	}//onCheck$giftmemExpCbId()

	/*private void setRewardExpiryTier() {
		Components.removeAllChildren(tierLbId);
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		Long prgmId=(Long) session.getAttribute("programId");
		LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
		int noOfTiers=prgmObj.getNoOfTiers();
		List<LoyaltyProgramTier> tierList=ltyPrgmSevice.getTierList(prgmId);
		Listitem li=null;

		for(int i=1;i<=noOfTiers;i++){
			String tier="Tier "+i;
			boolean isExists=false;
			if(tierList != null) {
				for(LoyaltyProgramTier loyaltyProgramTier:tierList) {
					if(loyaltyProgramTier.getTierType().equalsIgnoreCase(tier)) {


						li = new Listitem();
						li.appendChild(new Listcell(loyaltyProgramTier.getTierType()));
						li.appendChild(new Listcell(loyaltyProgramTier.getTierName()));

						Listcell lc=new Listcell();
						Hbox hbox = new Hbox();
						Label lb=new Label("after");
						lb.setStyle("display:inline-block;margin-top:5px;");
						lb.setParent(hbox);
						Listbox typelb = new Listbox();
						typelb.setMold("select");
						Listitem monthLi=new Listitem();
						monthLi.setLabel("Month(s)");
						monthLi.setValue(OCConstants.LOYALTY_TYPE_MONTH);
						monthLi.setParent(typelb);
						typelb.addEventListener("onSelect", this);
						typelb.setAttribute("type", "dateType");
						typelb.setParent(hbox);
						typelb.setSelectedIndex(0);
						Textbox valuetb = new Textbox();
						valuetb.setStyle("display:inline-block;width:50px;");
						valuetb.setVisible(true);
						valuetb.addEventListener("onSelect", this);
						valuetb.setAttribute("type", "dateValue");
						valuetb.setParent(hbox);
						if(loyaltyProgramTier.getRewardExpiryDateValue()==null || loyaltyProgramTier.getRewardExpiryDateValue().toString().isEmpty()) {
							valuetb.setValue("");
						}
						else {
							valuetb.setValue(loyaltyProgramTier.getRewardExpiryDateValue().toString());
						}
						hbox.setParent(lc);
						li.appendChild(lc);

						li.setParent(tierLbId);
						li.setValue(loyaltyProgramTier.getTierId());

						isExists=true;
					}
				}
			}
			if(!isExists) {
				li = new Listitem();
				li.appendChild(new Listcell(tier));
				li.appendChild(new Listcell("--"));
				li.appendChild(new Listcell("--"));
				li.setParent(tierLbId);
			}
		}
	}//setRewardExpiryTier()*/

	public void onClick$previousBtnId() {
//		Redirect.goTo(PageListEnum.LOYALTY_RULES);
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj((Long) session.getAttribute("programId"));
		if(prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE) || prgmObj.getProgramType().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_TYPE_DYNAMIC) ) {
			Redirect.goTo(PageListEnum.LOYALTY_RULES);
		}
		else {
			Redirect.goTo(PageListEnum.LOYALTY_ADD_CARDS);
		}
	}//onClick$previousBtnId()

	public void onClick$nextBtnId() {
		Redirect.goTo(PageListEnum.LOYALTY_AUTO_COMMUNICATION);
	}//onClick$nextBtnId()

	public void onClick$saveBtnId() {

		Long prgmId=(Long) session.getAttribute("programId");
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		LoyaltyProgramExclusion loyaltyProgramExclusion=ltyPrgmSevice.getExclusionObj(prgmId);
		if(!validateFields()) {
			return;
		}
		//char rewardExpiryFlag=OCConstants.FLAG_NO; 
		//if(earnedRwrdExpiryCbId.isChecked())rewardExpiryFlag=OCConstants.FLAG_YES;
		char memExpiryFlag=OCConstants.FLAG_NO;
		if(memValidityCbId.isChecked())memExpiryFlag=OCConstants.FLAG_YES;
		char issuanceWithPromoFlag=OCConstants.FLAG_NO;
		if(disallowCbId.isChecked())issuanceWithPromoFlag = OCConstants.FLAG_YES;
		char redemptionWithPromoFlag=OCConstants.FLAG_NO;
		if(disallowRedeemCbId.isChecked())redemptionWithPromoFlag = OCConstants.FLAG_YES;
		char mbrshipExpiryOnLevelUpgd=OCConstants.FLAG_NO;
		if(resetValidCbId.isChecked())mbrshipExpiryOnLevelUpgd=OCConstants.FLAG_YES;
		char giftAmountExpiryFlag=OCConstants.FLAG_NO;
		if(giftAmtExpCbId.isChecked())giftAmountExpiryFlag=OCConstants.FLAG_YES;
		char giftMembrshpExpiryFlag=OCConstants.FLAG_NO;
		if(giftmemExpCbId.isChecked())giftMembrshpExpiryFlag=OCConstants.FLAG_YES;
		
		LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
		StringBuffer logDetails = null;
		//boolean isChanged = checkIfChangedRewardValidity(rewardExpiryFlag, prgmObj);
		/*if(isChanged){
			logDetails = ltyPrgmSevice.logLtyRewardValidityChanges(prgmId, userId, null, false, (prgmObj.getRewardExpiryFlag() == OCConstants.FLAG_YES ? OCConstants.FLAG_YES : OCConstants.FLAG_NO));
		}*/
		/*if(earnedRwrdExpiryCbId.isChecked()) {
			List<Listitem> list = tierLbId.getItems();
			for(Listitem li:list) {

				if(li.getValue()!=null) {
					Long tierId=li.getValue();
					logger.info("tierId1"+tierId);
					LoyaltyProgramTier loyaltyProgramTier=ltyPrgmSevice.getTierObj(tierId);
					Listcell lc=(Listcell)li.getLastChild();
					Hbox hbox=(Hbox)lc.getFirstChild();
					Listbox typeLb=(Listbox)hbox.getChildren().get(1);
					Textbox valueLb=(Textbox)hbox.getLastChild();
					String type=typeLb.getSelectedItem().getValue().toString();
					//Long value=Long.parseLong(valueLb.getValue().toString());
					Long value = null;
					if(valueLb.getValue()!=null && !valueLb.getValue().toString().isEmpty()) {
						try {
							value = Long.parseLong(valueLb.getValue().toString());
						}catch (Exception e) {
							MessageUtil.setMessage("Please provide valid input for Reward Expiry Month(s).", "color:red", "TOP");
							valueLb.setFocus(true);
							return;
						}
						
					} else {
						MessageUtil.setMessage("please provide Reward Expiry Month(s).", "Color:red", "Top");
						valueLb.setFocus(true);
						return;
					}
					loyaltyProgramTier.setRewardExpiryDateType(type);
					loyaltyProgramTier.setRewardExpiryDateValue(value);
					ltyPrgmSevice.updateTier(loyaltyProgramTier);
				}
			}
		}else {
			List<Listitem> list = tierLbId.getItems();
			for(Listitem li:list) {

				if(li.getValue()!=null) {
					Long tierId=li.getValue();
					logger.info("tierId1"+tierId);
					LoyaltyProgramTier loyaltyProgramTier=ltyPrgmSevice.getTierObj(tierId);
					loyaltyProgramTier.setRewardExpiryDateType(null);
					loyaltyProgramTier.setRewardExpiryDateValue(null);
					ltyPrgmSevice.updateTier(loyaltyProgramTier);
				}
			}
		}
		if(isChanged){
			ltyPrgmSevice.logLtyRewardValidityChanges(prgmId, userId, logDetails, true, rewardExpiryFlag);
		}*/
		

		boolean isChanged = checkIfChangedMembrshpValidity(memExpiryFlag, prgmObj, mbrshipExpiryOnLevelUpgd);
		if(isChanged){
			logDetails = ltyPrgmSevice.logLtyMembrshpValidityChanges(prgmId, userId, null, false,
					(prgmObj.getMembershipExpiryFlag() == OCConstants.FLAG_YES ? OCConstants.FLAG_YES : OCConstants.FLAG_NO), 
					(prgmObj.getMbrshipExpiryOnLevelUpgdFlag() == OCConstants.FLAG_YES ? OCConstants.FLAG_YES : OCConstants.FLAG_NO));
		}
		if(memValidityCbId.isChecked()) {
			List<Listitem> list = memTierLbId.getItems();

			for(Listitem li:list) {
				if(li.getValue()!=null) {
					Long tierId=li.getValue();
					logger.info("tierId"+tierId);
					LoyaltyProgramTier loyaltyProgramTier=ltyPrgmSevice.getTierObj(tierId);
					Listcell lc=(Listcell) li.getLastChild();
					Hbox hbox=(Hbox)lc.getFirstChild();
					Listbox typeLb=(Listbox) hbox.getChildren().get(1);
					Listbox valueLb=(Listbox) hbox.getLastChild();
					String memType=typeLb.getSelectedItem().getValue().toString();
					Long memValue=Long.parseLong(valueLb.getSelectedItem().getValue().toString());
					loyaltyProgramTier.setMembershipExpiryDateType(memType);
					loyaltyProgramTier.setMembershipExpiryDateValue(memValue);
					ltyPrgmSevice.updateTier(loyaltyProgramTier);
				}
			}
		}else {
			List<Listitem> list = memTierLbId.getItems();

			for(Listitem li:list) {
				if(li.getValue()!=null) {
					Long tierId=li.getValue();
					logger.info("tierId"+tierId);
					LoyaltyProgramTier loyaltyProgramTier=ltyPrgmSevice.getTierObj(tierId);
					loyaltyProgramTier.setMembershipExpiryDateType(null);
					loyaltyProgramTier.setMembershipExpiryDateValue(null);
					ltyPrgmSevice.updateTier(loyaltyProgramTier);
				}
			}
		}
		if(isChanged){
			ltyPrgmSevice.logLtyMembrshpValidityChanges(prgmId, userId, logDetails, true, memExpiryFlag, mbrshipExpiryOnLevelUpgd);
		}
		
		String giftAmountExpiryDateType = null;
		Long giftAmountExpiryDateValue = null;
		if(giftAmtExpCbId.isChecked()) {
			giftAmountExpiryDateType = giftAmtExpTypeLbId.getSelectedItem().getValue();
			giftAmountExpiryDateValue = Long.parseLong(giftAmtExpValueLbId.getSelectedItem().getValue().toString());
		}

		String giftMembrshpExpiryDateType = null;
		Long giftMembrshpExpiryDateValue = null;
		if(giftmemExpCbId.isChecked()) {
			giftMembrshpExpiryDateType = giftmemExpTypeLbId.getSelectedItem().getValue();
			giftMembrshpExpiryDateValue = Long.parseLong( giftMemExpValueLbId.getSelectedItem().getValue().toString());
		}

		Set<Listitem> issueCouponSet = selIssuePromotionsLbId.getSelectedItems();
		Set<Listitem> redeemCouponSet = selRedeemPromotionsLbId.getSelectedItems();
		List<Listitem> storeItemLst = selectedStoreLbId.getItems();
		List<Listitem> productItemLst = productCodeLbId.getItems();
		List<Listitem> dateItemLst = dateLbId.getItems();
		List<Listitem> exclRedemdateItemLst = redemptionDateLbId.getItems();
		boolean isStrRedmChkb = false;
		boolean isAllStrChk = false;
		List<Listitem> selectedStoreLst = null;
		if(excludeRedeemCbId.isChecked()) {
			isStrRedmChkb = true;
		}
		if(excludeRedeemCbId.isChecked() && allStoresChkBxId.isChecked()) {
			isAllStrChk = true;
		}
		else if(excludeRedeemCbId.isChecked() && redemstoreNumbCmboBxId!=null && selectedStoresId.isChecked()) {
			selectedStoreLst = redemselectedStoreLbId.getItems();
		}
		if(excludeRedeemCbId.isChecked() && (redemselectedStoreLbId.getItems() == null || redemselectedStoreLbId.getItems().size()==0) && !allStoresChkBxId.isChecked()) {
			 MessageUtil.setMessage("Please provide store number.", "red", "TOP");
				return;
		 }
		ltyPrgmSevice.saveExclusions(prgmId,memExpiryFlag,mbrshipExpiryOnLevelUpgd,issuanceWithPromoFlag,redemptionWithPromoFlag,issueCouponSet,redeemCouponSet,storeItemLst,productItemLst,
				dateItemLst,exclRedemdateItemLst,loyaltyProgramExclusion,userId,giftAmountExpiryFlag,giftAmountExpiryDateType,giftAmountExpiryDateValue,giftMembrshpExpiryFlag,
				giftMembrshpExpiryDateType,giftMembrshpExpiryDateValue,isStrRedmChkb,isAllStrChk,selectedStoreLst);
		MessageUtil.setMessage("Validity and Exclusions saved successfully.", "color:blue", "TOP");
		resetFields();
		Redirect.goTo(PageListEnum.LOYALTY_AUTO_COMMUNICATION);
	}//onClick$saveBtnId()

	//to check for changes if done to maintain activity logs
	private boolean checkIfChangedMembrshpValidity(char memExpiryFlag, LoyaltyProgram prgmObj, char mbrshipExpiryOnLevelUpgd) {
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		if(memExpiryFlag == OCConstants.FLAG_YES) {
			if(prgmObj.getMembershipExpiryFlag() != OCConstants.FLAG_YES){
				return true;
			}
			if(mbrshipExpiryOnLevelUpgd != prgmObj.getMbrshipExpiryOnLevelUpgdFlag()){
				return true;
			}

			List<Listitem> list = memTierLbId.getItems();
			for(Listitem li:list) {
				if(li.getValue()!=null) {
					Long tierId=li.getValue();
					logger.info("tierId"+tierId);
					LoyaltyProgramTier loyaltyProgramTier=ltyPrgmSevice.getTierObj(tierId);
					Listcell lc=(Listcell) li.getLastChild();
					Hbox hbox=(Hbox)lc.getFirstChild();
					Listbox typeLb=(Listbox) hbox.getChildren().get(1);
					Listbox valueLb=(Listbox) hbox.getLastChild();
					String memType=typeLb.getSelectedItem().getValue().toString();
					Long memValue=Long.parseLong(valueLb.getSelectedItem().getValue().toString());

					if(loyaltyProgramTier.getMembershipExpiryDateType() == null ||
							!memType.equalsIgnoreCase(loyaltyProgramTier.getMembershipExpiryDateType()) ||
							loyaltyProgramTier.getMembershipExpiryDateValue() == null ||
							memValue.longValue() != loyaltyProgramTier.getMembershipExpiryDateValue().longValue()){
						return true;
					}
				}
			}

		}
		else{
			if(prgmObj.getMembershipExpiryFlag() == OCConstants.FLAG_YES){
				return true;
			}
		}
		return false;
	}

	//to check for changes if done to maintain activity logs
	/*private boolean checkIfChangedRewardValidity(char rewardExpiryFlag, LoyaltyProgram prgmObj) {
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		if(rewardExpiryFlag == OCConstants.FLAG_YES) {
			if(prgmObj.getRewardExpiryFlag() != OCConstants.FLAG_YES){
				return true;
			}

			List<Listitem> list = tierLbId.getItems();
			for(Listitem li:list) {

				if(li.getValue()!=null) {
					Long tierId=li.getValue();
					logger.info("tierId1"+tierId);
					LoyaltyProgramTier loyaltyProgramTier=ltyPrgmSevice.getTierObj(tierId);
					Listcell lc=(Listcell)li.getLastChild();
					Hbox hbox=(Hbox)lc.getFirstChild();
					Listbox typeLb=(Listbox)hbox.getChildren().get(1);
					Textbox valueLb=(Textbox)hbox.getLastChild();
					String type=typeLb.getSelectedItem().getValue().toString();
					Long value = null;
					try {
						value=valueLb.getValue() == null || valueLb.getValue().isEmpty() ? null : Long.parseLong(valueLb.getValue().toString());
					}catch (Exception e) {
						MessageUtil.setMessage("Please provide valid input for Reward Expiry Month(s).", "color:red", "TOP");
						valueLb.setFocus(true);
						return false;
					}
					//Long value=valueLb.getValue() == null || valueLb.getValue().isEmpty() ? null : Long.parseLong(valueLb.getValue().toString());

					if(loyaltyProgramTier.getRewardExpiryDateType() == null ||
							!type.equalsIgnoreCase(loyaltyProgramTier.getRewardExpiryDateType()) ||
							loyaltyProgramTier.getRewardExpiryDateValue() == null || valueLb.getValue()==null || valueLb.getValue().isEmpty() ||
							value.longValue() != loyaltyProgramTier.getRewardExpiryDateValue().longValue()){
						return true;
					}
				}
			}
		}
		else{
			if(prgmObj.getRewardExpiryFlag() == OCConstants.FLAG_YES){
				return true;
			}
		}
		return false;
	}*/

	private boolean validateFields() {
		if(disallowCbId.isChecked() && selIssuePromotionsLbId.getSelectedCount()==0){
			MessageUtil.setMessage("Please select a promotion name to disallow in issuance.", "red");
			return false;
		}

		if(disallowRedeemCbId.isChecked() && selRedeemPromotionsLbId.getSelectedCount()==0){
			MessageUtil.setMessage("Please select a promotion name to disallow in redemption.", "red");
			return false;
		}
		return true;
	}//validateFields()

	private void resetFields() {
		//earnedRwrdExpiryCbId.setChecked(false);
		//rewardTierDivId.setVisible(false);
		memValidityCbId.setChecked(false);
		memTierDivId.setVisible(false);
		resetDivId.setVisible(false);
		resetValidCbId.setChecked(false);
		disallowCbId.setChecked(false);
		disallowRedeemCbId.setChecked(false);
		selIssuePromoDivId.setVisible(false);
		selRedeemPromoDivId.setVisible(false);
		addCodeValueTbId.setValue("");
		//dateboxId.setText("");
		excDateLbId.setSelectedIndex(0);
		excMonthLbId.setSelectedIndex(0);
		excYearLbId.setSelectedIndex(0);
		excRedemptionDateLbId.setSelectedIndex(0);
		excRedemptionMonthLbId.setSelectedIndex(0);
		excRedemptionYearLbId.setSelectedIndex(0);
		Components.removeAllChildren(selectedStoreLbId);
		Components.removeAllChildren(redemselectedStoreLbId);
		Components.removeAllChildren(productCodeLbId);
		Components.removeAllChildren(dateLbId);
	}//resetFields()

}

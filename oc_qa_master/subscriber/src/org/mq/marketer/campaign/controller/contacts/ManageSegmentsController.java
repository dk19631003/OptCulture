package org.mq.marketer.campaign.controller.contacts;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.FormMapping;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.SegmentRules;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.ValueCodes;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.FormMappingDao;
import org.mq.marketer.campaign.dao.MLCustomFieldsDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.SegmentRulesDao;
import org.mq.marketer.campaign.dao.SegmentRulesDaoForDML;
import org.mq.marketer.campaign.general.ClickHouseDB;
import org.mq.marketer.campaign.general.ClickHouseSalesQueryGenerator;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.SalesQueryGenerator;
import org.mq.marketer.campaign.general.SegmentQueryGenerator;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;

public class ManageSegmentsController extends GenericForwardComposer {
	
	private MailingListDao mailingListDao;
	private Users user;
	//private Listbox listTypeRgId;
	private Listbox dispMlLBoxId,segRuleOptionLbId;
	private Session session;
	private SegmentRules segmentRule;
	private SegmentRulesDao segmentRulesDao;
	private SegmentRulesDaoForDML segmentRulesDaoForDML;
	private POSMappingDao posMappingDao;
	private MLCustomFieldsDao  mlCFDao;
	private CampaignsDao campaignsDao;
	private ContactsDao contactsDao;
	
	private FormMappingDao formMappingDao;
	private LoyaltyProgramDao loyaltyProgramDao;
	private ValueCodesDao valueCodesDao;
	private LoyaltyProgramTierDao loyaltyProgramTierDao;
	//private Grid segmentRulesGridId;
	private Vbox segmentRulesVbId;
	private Toolbarbutton addRuleToolbarId;
	private Users currentUser;
	
	/*private int segmentCount = 0;
	private int maxSegmentCount ;*/
	private Set<Long> segmentIdsSet; 
	private String selectedValueCode="";
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
private Map<String, String> tokenMap ;
private Map<String ,String> tokenMapForIyD;
	public ManageSegmentsController() {
		logger.debug("---just---");
		mlCFDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
		contactsDao = (ContactsDao)SpringUtil.getBean("contactsDao");
		mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
		user = GetUser.getUserObj();
		session = Sessions.getCurrent();
		segmentRulesDao = (SegmentRulesDao)SpringUtil.getBean("segmentRulesDao");
		campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
		formMappingDao = (FormMappingDao)SpringUtil.getBean("formMappingDao");
		loyaltyProgramDao = (LoyaltyProgramDao)SpringUtil.getBean("loyaltyProgramDao");
		valueCodesDao = (ValueCodesDao)SpringUtil.getBean("valueCodesDao");
		loyaltyProgramTierDao = (LoyaltyProgramTierDao) SpringUtil.getBean("loyaltyProgramTierDao");
		segmentRulesDaoForDML = (SegmentRulesDaoForDML)SpringUtil.getBean("segmentRulesDaoForDML");
		posMappingDao = (POSMappingDao)SpringUtil.getBean("posMappingDao");
		currentUser = GetUser.getUserObj();

		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Create Segment","",style,true);
		segmentIdsSet = (Set<Long>)session.getAttribute(Constants.SEGMENTIDS_SET);
		tokenMap = new HashMap<String, String>();
		tokenMap.put("isetoday", "isiytoday");
		tokenMap.put("isequal", "isiyequal");
		tokenMap.put("isebetween", "isiybetween");
		tokenMap.put("iseonOrAfter", "isiyonorafter");
		tokenMap.put("iseonOrBefore", "isiyonorbefore");
		tokenMap.put("iseafter", "isiyafter");
		tokenMap.put("isebefore", "isiybefore");
		tokenMap.put("iswithinnext_days", "iseqiywithinnext_day");
		tokenMap.put("iswithinnext_weeks", "iseqiywithinnext_week");
		tokenMap.put("iswithinnext_months", "iseqiywithinnext_month");
		tokenMapForIyD=new HashMap<>();
		tokenMapForIyD.put("iswithinnext_days", "iseqwithinnext_days");
		tokenMapForIyD.put("iswithinnext_weeks", "iseqwithinnext_weeks");
		tokenMapForIyD.put("iswithinnext_months", "iseqwithinnext_months");

		
	}
	
	private Tab purchaseAttrTabId;
	//private Listbox campListLb ;
	//private List<Campaigns> campList;
	private List<FormMapping> mappingsList;
	private List<LoyaltyProgram> programsList;
	private List<ValueCodes> valueCodeList;
	
	private Listbox dispcampaignsLbId;
	private Checkbox latestCRChkBoxId;
	
	@Override
	public void doAfterCompose(Component comp){
		try {
			super.doAfterCompose(comp);
			
			
			MessageUtil.clearMessage();
			
			//campaigns for opens and clicks
			List<Object[]> campList = campaignsDao.findAllSentCampaignsBySql(GetUser.getUserId());
			
			if(campList != null) {
				Listitem item = null;
				for (Object[] campaignArr : campList) {
					
					item = new Listitem(campaignArr[0].toString(), (Long)campaignArr[1]);
					item.setAttribute("cr_id", (Long)campaignArr[2]);
					item.setParent(dispcampaignsLbId);
					
					
				}//for
				
				
			}
			mappingsList = formMappingDao.findAllByUserId(GetUser.getUserId());
			programsList = loyaltyProgramDao.getAllProgramsListByUserId(GetUser.getUserId());
			valueCodeList = valueCodesDao.findValueCodeBy(GetUser.getUserObj().getUserOrganization().getUserOrgId());
			//prepare the list items to be dragged as category wise
			SetItemsToBeDragged(1,profileAttrLbId);
			SetItemsToBeDragged(2, purchaseAttrLbId);
			
			SetItemsToBeDragged(3, interactionAttrLbId);
			
			
			//show the existing segment Rules
			segmentRule = (SegmentRules)session.getAttribute("editSegment");
			if(segmentRule != null) {
				
				showSegmentRulePanel(false, segmentRule);
				onClick$proceedBtnId();
				
			}
		
			
			
		} catch (Exception e) {
			logger.info("Exception ", e);
		}
	
	}

	public void SetItemsToBeDragged(int category, Listbox targetLb) {
		String scope = null;
		String draggble = "";
		
		
		//boolean isMoreFormsFlag = (mappingsList != null && mappingsList.size() > 0);
		
		if(category == 3) {
			
			
			List<SegmentEnum> purchaseAttrList = SegmentEnum.getEnumsByCategoryCode(category);
			
			Listitem item = null;
			
			Components.removeAllChildren(targetLb);
			
			for (SegmentEnum purchaseSegEnum : purchaseAttrList) {
				
				item = new Listitem(purchaseSegEnum.getDispLabel(), purchaseSegEnum);
				item.setDraggable("IntSegment");
				item.setStyle("padding:10px;");
				item.setParent(targetLb);
				item.setAttribute("DBcolumnName", purchaseSegEnum.getColumnName());
				item.setAttribute("item", purchaseSegEnum.getItem());
				logger.debug("purchaseSegEnum.getItem() ::"+purchaseSegEnum.getItem());
			}
			
			return;
		}
		
		
		
		if(category == 1) {
			
			scope = "'"+Constants.POS_MAPPING_TYPE_CONTACTS+"'";
			draggble = "profileSegment";
			
		}else if(category == 2 ) {
			
			/*if(!((String)listTypeRgId.getSelectedItem().getValue()).equalsIgnoreCase("POS")) {
				
				return;
			}*/
			
			
			draggble = "purIntSegment";
			scope = "'"+Constants.POS_MAPPING_TYPE_SALES+"','"+Constants.POS_MAPPING_TYPE_SKU+"'";
			
		}
		
		POSMappingDao posMappingDao = (POSMappingDao)SpringUtil.getBean("posMappingDao");
		
		//for sales(purchase)
		List<SegmentEnum> purchaseAttrList = SegmentEnum.getEnumsByCategoryCode(category);
		
		List<POSMapping> posMappingsList = posMappingDao.findByType(scope, user.getUserId());
		logger.debug("posMappingsList size::"+posMappingsList.size());
		
		SegmentEnum UDFSegmentEnum = null;
		SegmentEnum valueCodeSegmentEnum = null;
		Listitem item = null;
		
		Components.removeAllChildren(targetLb);
		
		for (SegmentEnum purchaseSegEnum : purchaseAttrList) {
			
			if(user.getloyaltyServicetype().equalsIgnoreCase(OCConstants.LOYALTY_SERVICE_TYPE_SB)
					&& SegmentEnum.gethiddenSegments().contains(purchaseSegEnum.name())) continue;
			item = new Listitem(purchaseSegEnum.getDispLabel(), purchaseSegEnum);
			
			if(purchaseSegEnum.getDispLabel() != null &&
					(purchaseSegEnum.getDispLabel().trim().equals("Total Loyalty Earned"))||
					(purchaseSegEnum.getDispLabel().trim().equals("Total Gift-card Balance"))){
				item.setVisible(false);
			}
			
			item.setDraggable(draggble);
			item.setStyle("padding:10px;");
			item.setParent(targetLb);
			item.setAttribute("DBcolumnName", purchaseSegEnum.getColumnName());
			item.setAttribute("item",purchaseSegEnum.getItem());
			logger.debug("purchaseSegEnum.getItem() 1::"+purchaseSegEnum.getItem());
			for (POSMapping posMapping : posMappingsList) {
				
				if(purchaseSegEnum.isMappingField() == true) {
					
					if( purchaseSegEnum.getPosCustomFieldLbl() != null && 
							purchaseSegEnum.getPosCustomFieldLbl().equals(posMapping.getCustomFieldName()) &&
							posMapping.getOptionalValues() != null) {
						
						item.setAttribute("optionalValues", posMapping.getOptionalValues());
						break;
						
					}//if
					
				}//if
				
			}//for
			//APP-3558
			try {
				for (POSMapping posMapping : posMappingsList) {
					
					if(posMapping.getMappingType()!=null && posMapping.getMappingType().equalsIgnoreCase("SKU")) {
						
						if( purchaseSegEnum.getPosCustomFieldLbl() != null && 
								purchaseSegEnum.getPosCustomFieldLbl().equals(posMapping.getCustomFieldName())) {
							
							if(!purchaseSegEnum.getDispLabel().equals(posMapping.getDisplayLabel())) {
								logger.info("display label is modified in POS settings ::"+posMapping.getDisplayLabel());
								item.setLabel(posMapping.getDisplayLabel());
							}
							
						}//if
					}//if	
					
				}//for
			} catch (Exception e) {
				logger.error("Exception ::", e);
			}
			
		}//for
		
		
		for (POSMapping posMapping : posMappingsList) {
			
			if(posMapping.getCustomFieldName().trim().toLowerCase().startsWith("udf")) {
				 UDFSegmentEnum = null; 
				if(posMapping.getDataType().toLowerCase().startsWith("date")) {
					UDFSegmentEnum = SegmentEnum.UDF_DATE;
				}
				else if(posMapping.getDataType().equalsIgnoreCase("String")) {
					UDFSegmentEnum = SegmentEnum.UDF_STRING;
				}else if(posMapping.getDataType().equalsIgnoreCase("number") || posMapping.getDataType().equalsIgnoreCase("Double")) {
					UDFSegmentEnum = SegmentEnum.UDF_NUMBER;
				}
				
				String cat = "";
				if(posMapping.getMappingType().equals(Constants.POS_MAPPING_TYPE_SALES)) {
					cat = "sal."+posMapping.getCustomFieldName().trim().toLowerCase();
				}
				
				else if(posMapping.getMappingType().equals(Constants.POS_MAPPING_TYPE_SKU)) {
					cat = "sku."+posMapping.getCustomFieldName().trim().toLowerCase();
				}
				
				else if(posMapping.getMappingType().equals(Constants.POS_MAPPING_TYPE_CONTACTS)) {
					cat = "c."+posMapping.getCustomFieldName().trim().toLowerCase();
				}
				
				
				if(posMapping.getDataType().toLowerCase().startsWith("date")) {
					cat = "DATE("+cat+")";
				}//if
				
				if(UDFSegmentEnum != null) {
					item = new Listitem(posMapping.getDisplayLabel(), UDFSegmentEnum);
					item.setDraggable(draggble);
					item.setAttribute("DBcolumnName", cat);
					item.setAttribute("item", posMapping.getDisplayLabel());
					item.setStyle("padding:10px;");
					item.setParent(targetLb);
					if(posMapping.getOptionalValues() != null) {
						item.setAttribute("optionalValues", posMapping.getOptionalValues());
					}
				}
			}//if
			
		} // for
		
		
	}
	
	
	private Label numOfContactsLblId, numOfEmailContactsLblId, numOfMobileContactsLblId, numOfCHContactsLblId, numOfCHEmailContactsLblId, numOfCHMobileContactsLblId;
	public void onClick$getNumOfContactsAnchId() {
		
		
		String operator = "";
		boolean isCampaignsExists = false;
		String campaignIds = Constants.STRING_NILL;
		String crIds = Constants.STRING_NILL;
		List<Component> childVboxList = new ArrayList<Component>();
		if(targetDivHasChildren(profileAttributeDivId))childVboxList.addAll(profileAttributeDivId.getChildren());
		if(targetDivHasChildren(purIntAttributeDivId))childVboxList.addAll(purIntAttributeDivId.getChildren());
		if(targetDivHasChildren(intAttributeDivId)){
			
			if(dispcampaignsLbId.getSelectedCount() == 0) {
				
				MessageUtil.setMessage("Please select the campaign(s) for interaction rules.", "color:red;");
				return;
			}
			isCampaignsExists = true;
			Set<Listitem> items = dispcampaignsLbId.getSelectedItems();
			
			for (Listitem listitem : items) {
				
				Long selCampId = listitem.getValue();
				Long selCrId = (Long)listitem.getAttribute("cr_id");
				
				if(!campaignIds.isEmpty()) campaignIds += Constants.DELIMETER_COMMA;
				
				campaignIds += selCampId.longValue();
				
				if(latestCRChkBoxId.isChecked()) {
					if(!crIds.isEmpty()) crIds += Constants.DELIMETER_COMMA;
					
					crIds += selCrId.longValue();
				}
				
			}//for
			
			
			childVboxList.addAll(intAttributeDivId.getChildren());
		}
		/*List<Component> childVboxList = profileAttributeDivId.getChildren();
		
		childVboxList.addAll(purIntAttributeDivId.getChildren());*/
		Div chilVDiv = null;
		
		StringBuffer segmentRuleSB = new StringBuffer();//"all:"+((MailingList)dispMlLBoxId.getSelectedItem().getValue()).getListId());
		String ruleStr = null;
	
		if(childVboxList.size() == 0) {//only the dashed areas r left
			
			MessageUtil.setMessage("No segmentation rule provided. Please add at least one rule.", "color:red;");
			return;
			
		}
		
		
		boolean allValid=true;
		for (Object obj : childVboxList) {
			
			if(obj instanceof Div) {
				
				boolean isValid = true;
				for(Object object : ((Div)obj).getChildren()) {
					
					if(isValid==false) allValid=false;
					
					//Reset
					isValid = true;
					
					if(object instanceof Div) {
						
						
						chilVDiv = (Div)object;
						if(chilVDiv.getSclass().contains("drop_")) {
							continue;
						}
						
						ruleStr = prepareSegmentRule(chilVDiv);
						if(ruleStr == null) {
							//chilVDiv.setStyle("padding:10px;border:1px solid;color:red");
							isValid = false;
						}
						/*else {
							chilVDiv.setStyle("padding:10px;border:none");
							
						}*/
						
						//if(childVboxList.size() > 1)  operator = "<AND>";
						
						if(ruleStr != null && ruleStr.trim().length()>0) {					
							segmentRuleSB.append("||"+ruleStr+operator);
						}
						Coupons coupon =(Coupons)purchaseAttrLbId.getAttribute("CouponObj");
						if(ruleStr != null && ruleStr.contains("cp.coupon_expiry_date|date:ise") && coupon!=null && coupon.getExpiryType().equals("S")  ) {
							 MessageUtil.setMessage(" Given Discount code doesn't work for expiry on is Today/equal to ... .", "color:red", "TOP");
							 return;
						}
						if(ruleStr != null && chilVDiv.getAttribute("columnName")!=null && chilVDiv.getAttribute("replaceColumnName")!=null ) {
							ruleStr = ruleStr.replace((String)chilVDiv.getAttribute("replaceColumnName"), (String)chilVDiv.getAttribute("columnName"));
							segmentRuleSB.append("||"+ruleStr);
						}
						
					}//ruleDiv
					
				}//tempDiv added for And
				
				
				if(isValid==false) allValid=false;
				
			}//if obj
			
		}//for each vb
		/*if(couponNameflag) {
			 MessageUtil.setMessage("Please provide valid OptCulture Promotion Name.", "color:red", "TOP");
			 couponNameflag=false;
			 return;
			}*/
		
		if(!allValid) {
			logger.warn(" >>>>>>>>>>>>> Segmentation validation failed ");
			
			MessageUtil.setMessage("Please provide proper value(s) for highlighted rule(s).", "color:red", "TOP");
			return ;
		}
		
		if(segmentRuleSB.toString().trim().length()==0) {
			
			MessageUtil.setMessage("There are no valid rules to prepare segment (as of now, derivative filter rules are ignored).", "color:red","TOP");
			return;
		}
		
		//segmentRuleSB.insert(0, "all:"+((MailingList)dispMlLBoxId.getSelectedItem().getValue()).getListId());
		/*segmentRuleSB.insert(0, "all:"+currentUser.getUserId().longValue()+(isCampaignsExists ? (Constants.DELIMETER_COLON + campaignIds 
				+ (latestCRChkBoxId.isChecked() ? (Constants.DELIMETER_COLON + crIds) : "")) : ""));
		*/
		segmentRuleSB.insert(0, "all:"+currentUser.getUserId().longValue()+(isCampaignsExists ? (Constants.DELIMETER_COLON + campaignIds) 
				+ (latestCRChkBoxId.isChecked() ? (Constants.DELIMETER_COLON + Constants.INTERACTION_CAMPAIGN_CRID_PH) : "") : ""));
		
		
		logger.info("segmentRuleSB ::"+segmentRuleSB.toString());
		long mlsbit = Utility.getMlsBit(mlSet);
		//ClickHouse changes
		String segmentQueryStr = "";
		if(!currentUser.isEnableClickHouseDBFlag())
			segmentQueryStr = SalesQueryGenerator.generateListSegmentCountQuery( segmentRuleSB.toString(), true, Constants.SEGMENT_ON_EXTERNALID, mlsbit);
		else
			segmentQueryStr = ClickHouseSalesQueryGenerator.generateListSegmentCountQuery( segmentRuleSB.toString(), true, Constants.SEGMENT_ON_EXTERNALID, mlsbit);
			
				if(segmentQueryStr == null) {
					MessageUtil.setMessage("Selected invalid segmentation rule.", "color:red", "TOP");
					return ;
				}
		
				segmentQueryStr = segmentQueryStr.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH,(latestCRChkBoxId.isChecked() ? ("AND cr_id in("+crIds+")") :"") );
				
				if(segmentQueryStr.contains("<Reward>")) {
					segmentQueryStr = segmentQueryStr.replace("<Reward>",selectedValueCode);
				}
				logger.info("segmentQueryStr :: "+segmentQueryStr);
				setClickhouseSegmentedCountsAndQuery(segmentRuleSB.toString());

				int allSize = 0;
				if(!currentUser.isEnableClickHouseDBFlag()) 
					allSize = contactsDao.getSegmentedContactsCount(segmentQueryStr);
				else
					allSize = contactsDao.getSegmentedContactsCountFromCH(segmentQueryStr);
			
		numOfContactsLblId.setValue(" "+allSize);
		
		
		
		// Email Contacts
		//segmentQueryStr = SalesQueryGenerator.generateListSegmentCountQuery( segmentRuleSB.toString(), true, Constants.SEGMENT_ON_EMAIL, mlsbit);
		//ClickHouse changes
		if(!currentUser.isEnableClickHouseDBFlag())
			segmentQueryStr = SalesQueryGenerator.generateListSegmentCountQuery( segmentRuleSB.toString(), false, Constants.SEGMENT_ON_EMAIL, mlsbit);
		else
			segmentQueryStr = ClickHouseSalesQueryGenerator.generateListSegmentCountQuery( segmentRuleSB.toString(), false, Constants.SEGMENT_ON_EMAIL, mlsbit);

		logger.info("segmentQueryStr :: "+segmentQueryStr);
		
		if(segmentQueryStr == null) {
			MessageUtil.setMessage("Selected invalid segmentation rule.", "color:red", "TOP");
			return ;
		}
		segmentQueryStr = segmentQueryStr.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH,(latestCRChkBoxId.isChecked() ? ("AND cr_id in("+crIds+")") :"") );
		if(segmentQueryStr.contains("<Reward>")) {
			segmentQueryStr = segmentQueryStr.replace("<Reward>",selectedValueCode);
		}
		int emailSize = 0;
		if(!currentUser.isEnableClickHouseDBFlag())
			emailSize = contactsDao.getSegmentedContactsCount(segmentQueryStr);
		else
			emailSize = contactsDao.getSegmentedContactsCountFromCH(segmentQueryStr);
		
		numOfEmailContactsLblId.setValue(" "+emailSize);
	
	// Mobile Contacts
		//segmentQueryStr = SalesQueryGenerator.generateListSegmentCountQuery( segmentRuleSB.toString(), true, Constants.SEGMENT_ON_MOBILE, mlsbit);
		//ClickHouse changes
		if(!currentUser.isEnableClickHouseDBFlag())
			segmentQueryStr = SalesQueryGenerator.generateListSegmentCountQuery( segmentRuleSB.toString(), false, Constants.SEGMENT_ON_MOBILE, mlsbit);
		else
			segmentQueryStr = ClickHouseSalesQueryGenerator.generateListSegmentCountQuery( segmentRuleSB.toString(), false, Constants.SEGMENT_ON_MOBILE, mlsbit);
		
		if(segmentQueryStr == null) {
			MessageUtil.setMessage("Selected invalid segmentation rule.", "color:red", "TOP");
			return ;
		}
		segmentQueryStr = segmentQueryStr.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH,(latestCRChkBoxId.isChecked() ? ("AND cr_id in("+crIds+")") :"") );
		segmentQueryStr = segmentQueryStr.replace("<MOBILEOPTIN>",""); // added on date 15th may 2015
		if(segmentQueryStr.contains("<Reward>")) {
			segmentQueryStr = segmentQueryStr.replace("<Reward>",selectedValueCode);
		}
		int mobileSize = 0;
		if(!currentUser.isEnableClickHouseDBFlag())
			mobileSize = contactsDao.getSegmentedContactsCount(segmentQueryStr);
		else
			mobileSize = contactsDao.getSegmentedContactsCountFromCH(segmentQueryStr);
		
		numOfMobileContactsLblId.setValue(" "+mobileSize);
		
		logger.info("segmentQueryStr :: "+segmentQueryStr);
		
		/*MessageUtil.setMessage("This segmentation rule returned : \n\t " +
				"All contacts : "+allSize +"\n\t " +
				"Contacts with Email ID : "+emailSize +"\n\t " +
				"Contacts with Mobile number : "+mobileSize ,"color:green;");*/
		/*MessageUtil.setMessage("This segmentation rule returned : \n\t " +
				"Contacts with Active Email Addresses : "+emailSize +"\n\t " +
				"Contacts with Active Phone Numbers : "+mobileSize ,"color:green;");*/
		
		MessageUtil.setMessage("This segmentation rule returned : \n\t " +
				"All contacts : "+allSize +"\n\t " +
				"Contacts with Active Email Addresses : "+emailSize +"\n\t " +
				"Contacts with Active Phone Numbers : "+mobileSize ,"color:green;");
		
	}
	
	private String clickhouseQuery = "";

	public void onClick$getChContactsQuery() {
		MessageUtil.setMessage("ClickHouse Query : \n\t \n\t " + clickhouseQuery, "color:green;");
	}

	private void setClickhouseSegmentedCountsAndQuery(String segRule) {
		logger.info("Segment-Rule :: "+ segRule);
		long totCHContactsCount = 0;
		long totCHEmailCount = 0;
		long totCHMobileCount = 0;

		try {
			ClickHouseDB ch = new ClickHouseDB();
			logger.info("-- CLICKHOUSE-SEGMENTS-QUERY -- ");
			String totChQuery = new SegmentQueryGenerator().getSegmentQuery(segRule, "ALL_CONTACTS");
			String emailChQuery = new SegmentQueryGenerator().getSegmentQuery(segRule, "ACTIVE_EMAIL_CONTACTS");
			String mobileChQuery = new SegmentQueryGenerator().getSegmentQuery(segRule, "ACTIVE_MOBILE_CONTACTS");
			clickhouseQuery = totChQuery;

			logger.info("CH-TOTAL-QUERY  >>>> " + totChQuery);
			logger.info("CH-EMAIL-QUERY  >>>> " + emailChQuery);
			logger.info("CH-MOBILE-QUERY >>>> " + mobileChQuery);
			
			String chEmailQuery = "SELECT COUNT(DISTINCT cid) FROM contacts WHERE cid IN (" + emailChQuery + ")";
			String chMobileQuery = "SELECT COUNT(DISTINCT cid) FROM contacts WHERE cid IN (" + mobileChQuery + ")";

			totCHContactsCount = ch.getCHConnectionAndRunCountQuery(totChQuery.replaceFirst("DISTINCT c.cid", "COUNT(DISTINCT c.cid)"));
			totCHEmailCount = ch.getCHConnectionAndRunCountQuery(chEmailQuery);
			totCHMobileCount = ch.getCHConnectionAndRunCountQuery(chMobileQuery);

			logger.info("CH-TOTAL-COUNT  >>>> " + totCHContactsCount);
			logger.info("CH-EMAIL-COUNT  >>>> " + totCHEmailCount);
			logger.info("CH-MOBILE-COUNT >>>> " + totCHMobileCount);

		} catch (Exception e) {
			logger.info("CLICKHOUSE-SEGMENTS-QUERY EXCEPTION :: " + e);
		}

		numOfCHContactsLblId.setValue(" " + totCHContactsCount);
		numOfCHEmailContactsLblId.setValue(" " + totCHEmailCount);
		numOfCHMobileContactsLblId.setValue(" " + totCHMobileCount);
	}
	
	private boolean nameExist;
	
	private Label nameStatusLblId;
	public void onBlur$segmentRuleNameTbId() {
		try {
			checkSegmentName(segmentRuleNameTbId,nameStatusLblId);
		} catch (Exception e) {
			logger.error("Exception :: error getting from the checkEmailName method ",e);
		}
	}
	
	public void checkSegmentName(Textbox cNameTbId, Label nameStatusLblId) throws Exception{
		String segRuleName = Utility.condense(cNameTbId.getValue().trim());
		
		if(segRuleName.trim().equals("")){
			nameStatusLblId.setValue("");
			return;
		}else if(!Utility.validateName(segRuleName)){
			nameStatusLblId.setStyle("color:red");
			nameStatusLblId.setValue("Special characters not allowed");
			return;
		}
		MessageUtil.clearMessage();
		//CampaignsDao campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
		nameExist =  segmentRulesDao.checkName(segRuleName,user.getUserId());
		if(nameExist){
			nameStatusLblId.setStyle("color:red");
			nameStatusLblId.setValue("Already exist");
		}else{
			nameStatusLblId.setStyle("color:#023849");
			nameStatusLblId.setValue("Available");
//			cNameTbId.setValue(emailName);
		}
	}
	
	
	
	private Textbox segmentRuleNameTbId,segRuleDescpTxtBoxId;
	@SuppressWarnings("unchecked")
	public boolean validate() {
		
		String segmentRuleStr=null;
		String listIdsStr = "";

		String segRuleName = segmentRuleNameTbId.getValue();
		
		//validate if the segRule is empty
		if(segRuleName == null || segRuleName.trim().length() == 0) {
			
			MessageUtil.setMessage(
					"Please provide name for your segment. Name cannot be left empty.","color:red", "TOP");
			return false;
			
		}
		
		//validate for the availability of the segRule
		if(nameExist){
			MessageUtil.setMessage("Entered name is already in use. " +
					"Please choose a different name.", "color:red", "TOP");
			segmentRuleNameTbId.setFocus(true);
			return false;
		}
		if(nameStatusLblId.getStyle() != null && nameStatusLblId.getStyle().equals("color:red")) {
			
			MessageUtil.setMessage("Please provide valid segment name." +
					" Special characters are not allowed.", "color:red", "TOP");
			segmentRuleNameTbId.setFocus(true);
			return false;
			
		}
		
		int num = dispMlLBoxId.getItemCount();
		if (num == 0) {
			MessageUtil.setMessage(
					"Please create a contact list first so that you can send" +
					" your campaigns to it.","color:red", "TOP");
			return false;
		}
		
		int mlcount = dispMlLBoxId.getSelectedCount();
		if (mlcount <= 0) {
			MessageUtil.setMessage("Please select at least one mailing list.", "color:red","TOP");
			return false;
		}
		
		return true;
		
	}
	
	/*public void onclick$segmentBtnId() {
		
		Set<Listitem> selItems = dispMlLBoxId.getSelectedItems();
		
		
		for (Listitem listitem : selItems) {
			
			
			
			
		}
		
		String listIdsStr = "";
		Set<Listitem> lists = dispMlLBoxId.getSelectedItems();
		MailingList ml;
		for (Listitem li : lists) {

			ml = (MailingList) li.getValue();
			if(listIdsStr.length() != 0) listIdsStr+=",";
			listIdsStr += ml.getListId();
		}
		
		prepareSegmentRule(listIdsStr);
		
		
	}*/
	
	
	
	/*public String segment(int index) {
		
		String segmentQueryStr;//this i need to save as segmentrule
				
		String segmentRuleStr;
		String listIdsStr = "";
		Set lists = dispMlLBoxId.getSelectedItems();
		Listitem li;
		MailingList ml;
		for (Object obj : lists) {
			li = (Listitem) obj;
			ml = (MailingList) li.getValue();
			if(listIdsStr.length() != 0) listIdsStr+=",";
			listIdsStr += ml.getListId();
		}




		segmentRuleStr = prepareRule(listIdsStr);
		logger.info(" SegmentRule returned by prepareRule() :"+segmentRuleStr+" index ::"+index);
		
		if(segmentRuleStr == null) {
			MessageUtil.setMessage("Please select valid segmentation rules.", "color:red", "TOP");
			return null;
		}
		if(index == 0) {
			segmentQueryStr = QueryGenerator.generateListSegmentQuery( segmentRuleStr, true);
			logger.info(" Generated Query :"+segmentQueryStr);
			
			if(segmentQueryStr == null) {
				MessageUtil.setMessage("Selected invalid segmentation rules.", "color:red", "TOP");
			}
			
			int emailCount = contactsDao.getSegmentedContactsCount(segmentQueryStr);
			
			if(emailCount == 0) {
				//popupWinId.setVisible(false);
				MessageUtil.setMessage("Your segment returned 0 contacts.", "color:red", "TOP");
				return null;
			}
				
			if(Messagebox.show("Your segment returned "+emailCount+" contacts. Do you want to continue?", 
					"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION) == Messagebox.OK) {
				
				popupWinId.setVisible(true);//need to enable for the normal mailing list
				popupWinId.doHighlighted();
			}
		
		}
		
		return null;
		
	}*/
	
	private String prepareSegmentRule(String listIdsStr) {
		
		StringBuffer segmentRuleSB = new StringBuffer(listIdsStr+":");
		List rulesList = profileAttributeDivId.getChildren();
		
		boolean isValid = true;
		if(rulesList.size() <= 0) {
			MessageUtil.setMessage("At least one segment rule must be selected.", 
					"color:red", "TOP");
			
			isValid = false;
		}

		Div ruleVDiv = null;
		String segRuleStr = null;
		
		for (Object object : rulesList) {
			
			ruleVDiv = (Div)object;
			
			segRuleStr = prepareSegmentRule(ruleVDiv);
			
			if(segRuleStr == null) {
				ruleVDiv.setStyle("padding:10px;border:1px solid;color:red");
				isValid = false;
			}else {
				ruleVDiv.setStyle("padding:10px;border:none");
			}
			segmentRuleSB.append("||"+segRuleStr);
			
			
			
		}
		
		logger.debug("****************** segmentRuleSb :"+segmentRuleSB.toString());
		if(!isValid) {
			logger.warn(" >>>>>>>>>>>>> Segmentation validation failed ");
			return  null;
		}
		return segmentRuleSB.toString();
	}
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	private String prepareSegmentRule(Div ruleVDiv) {
		
		//prepare segment rule for each div
		
		
		
		
		StringBuffer ruleSB = new StringBuffer();
		
		List childDivList = ruleVDiv.getChildren();
		Div ruleDiv = null;
		String operator ="";
		
		boolean retValue=true;
		
		boolean isValid = true;
		Coupons coupon=null;
		boolean expiryflag=false;
		
		for (Object object : childDivList) {
			
			if(isValid==false) retValue=false;
			
			//Reset the isValid
			isValid = true;
			if(object instanceof Div || 
					(object instanceof Label && ((Label) object).getValue().equals(SegmentEnum.PROMO_OPTCULTURE_PROMOTION_NAME.getItem()))
					|| (object instanceof Label && ((Label) object).getValue().equals(SegmentEnum.PROMO_OPTCULTURE_PROMOTION_NAME.getDispLabel()))) {
				Label fielslbl=null;
				List columns=null;
				String col=null;
				String dispLabel=null;
				if(object instanceof Label) {
					fielslbl=(Label) object;
					columns=childDivList;
					 dispLabel = (String)fielslbl.getAttribute("item");
				List<SegmentEnum> purchaseAttrList = SegmentEnum.getEnumsByCategoryCode(4);
						for(SegmentEnum en:purchaseAttrList) {
							//dispLabel=	en.getDispLabel();
							dispLabel=	en.getItem();
							col=en.getColumnName();
						}
						String multipleCol  = col;
						 if(multipleCol.contains("::")) {
							 col = multipleCol.split("::")[1];
							 ruleVDiv.setAttribute("columnName", multipleCol.split("::")[0]);
							 ruleVDiv.setAttribute("replaceColumnName", col);
						 }
				}
				else {
				ruleDiv = (Div)object;
				
				if(ruleDiv.getSclass().contains("drop_")) {
					continue;
				}
				columns = ruleDiv.getChildren();//gives the rule division
				fielslbl = (Label)columns.get(1); //
				String multipleCol  = (String)fielslbl.getAttribute("columnName");
				 if(multipleCol != null ) {
					 if(multipleCol.contains("::")) {
					 col = multipleCol.split("::")[1];
					 ruleVDiv.setAttribute("columnName", multipleCol.split("::")[0]);
					 ruleVDiv.setAttribute("replaceColumnName", col);
				 }
				 else{
					 col = (String)fielslbl.getAttribute("columnName");
				 }
				 }
				 dispLabel = (String)fielslbl.getAttribute("item");
				
				}
				logger.debug("dispLabel ::"+dispLabel+ " colName ::"+col);
				
				if(dispLabel.equals("OptCulture Promotion Name"))operator="<OR>";
				ruleSB.append(dispLabel+"|");
				
				if( col!= null && col.startsWith("total.")) {
					
					continue;
				}
				if(col != null && col.trim().length() > 0) {
					ruleSB.append(col+"|");
				}
				
				//append type
				String type ;
				String constraint;
				SegmentEnum segmentEnum;
				Listbox ruleLb = (Listbox)columns.get(2);
				//Listbox formLb = (Listbox)columns.get(10);
				Listbox formLb = (Listbox)columns.get(11);
				Listbox ltylb = (Listbox)columns.get(3);
				
 				if(dispLabel.equalsIgnoreCase("Loyalty transaction")|| dispLabel.equalsIgnoreCase("Loyalty Balance")){
					if(ltylb.getSelectedIndex() == -1) {
						
						segmentEnum = (SegmentEnum)ltylb.getItemAtIndex(0).getValue();
						
						} else {
							
							segmentEnum = (SegmentEnum)ltylb.getSelectedItem().getValue();
						}
				}
				else {
					if(ruleLb.getSelectedIndex() == -1) {
					
					segmentEnum = (SegmentEnum)ruleLb.getItemAtIndex(0).getValue();
					
					} else {
						
						segmentEnum = (SegmentEnum)ruleLb.getSelectedItem().getValue();
					}
				}
				if(col == null || col.trim().length() == 0) {
					
					col = segmentEnum.getColumnName();
					if(col != null && col.trim().length() > 0) {
						
						ruleSB.append(col+"|");
					}
					
					
				}
				
				String valStr = "";
				boolean isIgnoreYear = false;
				boolean isIssuedOn=false;
				
				//append operation/constraint
				
					Listbox operatorLb = (Listbox)columns.get(5);
					if(operatorLb.isVisible()) {
						if(operatorLb.getSelectedIndex() == -1) {
							
							segmentEnum = (SegmentEnum)operatorLb.getItemAtIndex(0).getValue();
							
							valStr  = operatorLb.getItemAtIndex(0).getLabel();
							
							logger.debug("valStr====>"+valStr);
							
							segmentEnum = (SegmentEnum)operatorLb.getItemAtIndex(0).getValue();
							
							
							if(segmentEnum.getColumnValue() != null && !segmentEnum.getColumnValue().equals("") ) {
								valStr = segmentEnum.getColumnValue();
							}
							
						} else {
							Object obj = operatorLb.getSelectedItem().getValue();
							//for another lty list box
							if(obj instanceof SegmentEnum){
								segmentEnum = (SegmentEnum)operatorLb.getSelectedItem().getValue();
								valStr  = operatorLb.getSelectedItem().getLabel();
								/*if(ltylb.isVisible()){
									obj = ltylb.getSelectedItem().getValue();
									segmentEnum = (SegmentEnum)ltylb.getSelectedItem().getValue();
									valStr  = ltylb.getSelectedItem().getLabel();
								}*/
								if(segmentEnum.getColumnValue() != null && !segmentEnum.getColumnValue().equals("") ) {
									valStr = segmentEnum.getColumnValue();
									if(formLb.isVisible()) {
										valStr = valStr+Constants.DELIMETER_COLON+formLb.getSelectedItem().getLabel();
										
										
									}//if
								}
							}
							else if (obj instanceof Long){
								valStr = operatorLb.getSelectedItem().getValue().toString();
							}
							else if(obj == null){
								isValid = false;
								setErrorToDiv(ruleDiv, isValid);
								continue;
							}
						}
						
					}
					
					if(col == null || col.trim().length() == 0) {
						
						col = segmentEnum.getColumnName();
						
						if(col != null && col.trim().length() > 0) {
							
							ruleSB.append(col+"|");
						}
						
						
					}
					
					 coupon =(Coupons)purchaseAttrLbId.getAttribute("CouponObj");
					if(segmentEnum.getParentEnum().getParentEnum()!=null && (
							segmentEnum.getParentEnum().getParentEnum().name().equalsIgnoreCase(SegmentEnum.PROMO_CODE_ISSUED.name())||
							segmentEnum.getParentEnum().getParentEnum().name().equalsIgnoreCase(SegmentEnum.PROMO_CODE_REDEEMED.name())||
							segmentEnum.getParentEnum().getParentEnum().name().equalsIgnoreCase(SegmentEnum.PROMO_CODE_EXPIRED.name())))  {
						 if(ruleLb.getSelectedIndex() == -1) 
								segmentEnum = (SegmentEnum)ruleLb.getItemAtIndex(0).getValue();
								 else 
									segmentEnum = (SegmentEnum)ruleLb.getSelectedItem().getValue();
						     ruleSB= new StringBuffer();
						     //dispLabel=segmentEnum.getParentEnum().getDispLabel();
						     dispLabel=segmentEnum.getParentEnum().getItem();
						    ruleSB.append(dispLabel+"|");
		                        col = segmentEnum.getColumnName();
		                        ruleSB.append(col+"|");
		                        type = segmentEnum.getType();
								ruleSB.append(type+":");
								constraint = segmentEnum.getToken();
								ruleSB.append(constraint+"|");
								valStr=segmentEnum.getColumnValue();
								ruleSB.append(valStr);						
						ruleSB.append("<OR>||"+dispLabel+"|");
						valStr=segmentEnum.getInnerQryColumnName();
						 if(segmentEnum.name().equalsIgnoreCase(SegmentEnum.PROMO_CODE_EXPIRED.name()) && coupon!=null && coupon.getExpiryType().equalsIgnoreCase("D")) {
							 String arr[]=coupon.getExpiryDetails().split(";=;");
							     valStr="cd.issued_on<DVal>"+arr[1];
							     if(arr[0].equalsIgnoreCase("I")) {
							    	 isIssuedOn=true;
							    	 constraint = segmentEnum.getToken();
							     }
							     else if(arr[0].equalsIgnoreCase("B")) {
									valStr="c.birth_day<DVal>"+arr[1];
									isIgnoreYear = true;
								}
								else if(arr[0].equalsIgnoreCase("A")){
									valStr="c.anniversary_day<DVal>"+arr[1];
									isIgnoreYear = true;
								} 
								expiryflag=true; 
						 }
						 else if(segmentEnum.name().equalsIgnoreCase(SegmentEnum.PROMO_CODE_EXPIRED.name()) && coupon!=null && coupon.getExpiryType().equalsIgnoreCase("S")) {
							 valStr="cp.coupon_expiry_date";
						 }
						ruleSB.append(valStr+"|");
						if(operatorLb.isVisible()) {
							if(operatorLb.getSelectedIndex() == -1) 
								segmentEnum = (SegmentEnum)operatorLb.getItemAtIndex(0).getValue();
								else
									segmentEnum = (SegmentEnum)operatorLb.getSelectedItem().getValue();
						type=segmentEnum.getType();
					ruleSB.append(type+":");
						if(isIssuedOn && constraint.contains("iswithinnext")) {
							constraint = tokenMapForIyD.get(segmentEnum.getToken());
						}
						else if(isIgnoreYear) {
							constraint = tokenMap.get(segmentEnum.getToken());
						}else {
							constraint = segmentEnum.getToken();
						}
						ruleSB.append(constraint+"|");
						valStr="";
							}
					}
					else {
					type = segmentEnum.getType();
					if(segmentEnum.name().equalsIgnoreCase(SegmentEnum.VALUE_CODES.name())) {
						//selectedValueCode="'"+ruleLb.getSelectedItem().getLabel()+"'";
						if(!selectedValueCode.isEmpty()) selectedValueCode += Constants.DELIMETER_COMMA;
						
						selectedValueCode += "'"+ruleLb.getSelectedItem().getLabel()+"'";
						
					}
					if(ltylb.getSelectedItem()!=null) {
					SegmentEnum valEnum = ltylb.getSelectedItem().getValue();
					if(valEnum.name().equalsIgnoreCase(SegmentEnum.VALUE_CODES_TOTAL.name())||
							valEnum.name().equalsIgnoreCase(SegmentEnum.VALUE_CODES_TOTAL_BALANCE.name())) {
						if(!selectedValueCode.isEmpty()) selectedValueCode += Constants.DELIMETER_COMMA;
						selectedValueCode +="'"+ltylb.getSelectedItem().getLabel()+"'";
					}
					}
					ruleSB.append(type+":");
				    constraint = segmentEnum.getToken();
				    ruleSB.append(constraint+"|");
					}
				
				//append value
				if(type.equalsIgnoreCase("Date")) {
					
					/*Datebox dateBox1 = (Datebox)columns.get(8);
					Datebox dateBox2 = (Datebox)columns.get(9);
					Listbox numberLb = (Listbox)columns.get(3);
					Decimalbox numberbox1 = (Decimalbox)columns.get(6);
					Decimalbox numberbox2 = (Decimalbox)columns.get(7);*/
					Datebox dateBox1 = (Datebox)columns.get(9);
					Datebox dateBox2 = (Datebox)columns.get(10);
					Listbox numberLb = (Listbox)columns.get(4);
					Decimalbox numberbox1 = (Decimalbox)columns.get(7);
					Decimalbox numberbox2 = (Decimalbox)columns.get(8);
					
					if(!dateBox1.isVisible() && !dateBox2.isVisible() && !numberLb.isVisible() && 
							!numberbox1.isVisible() && !numberbox2.isVisible()) {
						
						if(valStr.isEmpty()) {
							
							valStr = segmentEnum.getColumnValue();
						}
						ruleSB.append(valStr);
						
						
					}//if
					
					if(dateBox1.isVisible()) {
					
						if(dateBox1.getValue() == null) {
							
						//	MessageUtil.setMessage("Please provide the value for the date.", "color:red", "TOP");
							isValid = false;
							setErrorToDiv(ruleDiv, isValid);
							continue;
							
						}
						
						ruleSB.append(dateFormat.format(dateBox1.getValue()));
						if(segmentEnum.getDispLabel().equals("between") || dateBox2.isVisible()){
							
							if(dateBox2.getValue() == null){
								
						//		MessageUtil.setMessage("Please provide the value for the date.", "color:red", "TOP");
								isValid = false;
								setErrorToDiv(ruleDiv, isValid);
								continue;
							}
							
							if(dateBox1.getValue().after(dateBox2.getValue())) {
						//		MessageUtil.setMessage("First date must be later than second date. ", "color:red", "TOP");
								isValid = false;
								setErrorToDiv(ruleDiv, isValid);
								continue;
							}
							ruleSB.append("|"+dateFormat.format(dateBox2.getValue()));
							
						}
						
					}
					else if(numberLb.isVisible()) { //value is the range and le=abel is the selected num of days/weeks/months
						if(segmentEnum.getParentEnum().name().equalsIgnoreCase(SegmentEnum.PROMO_DATE_IS_ISSUED.name())|| 
								segmentEnum.getParentEnum().name().equalsIgnoreCase(SegmentEnum.PROMO_DATE_IS_REDEEM.name())||
								segmentEnum.getParentEnum().name().equalsIgnoreCase(SegmentEnum.PROMO_DATE_IS_EXPIRED.name())) {
							
						}
						else {
						long value = ( (Integer.parseInt(numberLb.getSelectedItem().getLabel()) ) *
								( Integer.parseInt((String)numberLb.getSelectedItem().getValue())  ));
						
						ruleSB.append(value);
						}
						
					}else if(numberbox1.isVisible()) {
						

						
						if(numberbox1.getValue() == null) {
						//	MessageUtil.setMessage("Value should not be empty. ", "color:red", "TOP");
							isValid = false;
							setErrorToDiv(ruleDiv, isValid);
							continue;
						}
						ruleSB.append(numberbox1.getValue());
						if(numberbox2.isVisible()) {
							
							if(numberbox2.getValue() == null) {
								//MessageUtil.setMessage("Value should not be empty. ", "color:red", "TOP");
								isValid = false;
								setErrorToDiv(ruleDiv, isValid);
								continue;
							}
							if(numberbox2.getValue().compareTo( numberbox1.getValue()) == -1 ) {
								isValid = false;
								setErrorToDiv(ruleDiv, isValid);
								continue;
								
							}
							ruleSB.append("|"+numberbox2.getValue());
							
						}//if
						
						
					}
					else if(ruleSB.toString().endsWith("|")) { //need to be generic
						
						ruleSB.append("now()");
					}
				}//if date
				else if(type.equalsIgnoreCase("string"))  {
					
					logger.info(" type == string");
					
					/*Textbox txtbox = (Textbox)columns.get(5);
					Combobox cb1 = (Combobox)columns.get(11);
					Combobox cb2 = (Combobox)columns.get(12);*/
					Textbox txtbox = (Textbox)columns.get(6);
					Combobox cb1 = (Combobox)columns.get(12);
					Combobox cb2 = (Combobox)columns.get(13);
					/*Combobox cb1 = (Combobox)columns.get(12);
					Combobox cb2 = (Combobox)columns.get(13);*/
					//Listbox formLb = (Listbox)columns.get(11);
					
					if(!txtbox.isVisible() && !cb1.isVisible() && !cb2.isVisible()) {
						if(valStr.isEmpty()) {
							valStr = segmentEnum.getColumnValue();
						}
						ruleSB.append(valStr);
						
					}
					else {
						
						if(txtbox.isVisible()) 	valStr = txtbox.getText().trim();
						 
						else if(cb1.isVisible()) {
							
							
							valStr = cb1.getValue().trim();
							logger.info(" got combo value as ::"+valStr);
						}
						
						if( valStr.length() <= 0) {
						//	MessageUtil.setMessage("Value should not be empty. ", "color:red", "TOP");
							isValid = false;
							setErrorToDiv(ruleDiv, isValid);
							continue;
						}
						if(valStr.contains("|")) {
						//	MessageUtil.setMessage("Value should not contains pipe(|).", "color:red", "TOP");
							isValid = false;
							setErrorToDiv(ruleDiv, isValid);
							continue;
						}
						 
						if(segmentEnum.name().equalsIgnoreCase(SegmentEnum.PROMO_OPTCULTURE_NAME.name())) {
							 
							 try {
								CouponsDao couponsDao = (CouponsDao)ServiceLocator.getInstance().getDAOByName("couponsDao");
								 coupon=couponsDao.getConpounByName(currentUser.getUserId().toString(), valStr.trim());
								
								if(coupon!=null) {
									purchaseAttrLbId.setAttribute("CouponObj", coupon);
								}
								
								//ruleSB.append(valStr);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								
							}
					}
						ruleSB.append(valStr);
					
					}
				}// else if
				else if(type.equalsIgnoreCase("number"))  {
					
					/*Textbox txtbox = (Textbox)columns.get(5);
					Combobox cb1 = (Combobox)columns.get(11);
					Combobox cb2 = (Combobox)columns.get(12);*/
					
					Textbox txtbox = (Textbox)columns.get(6);
					Combobox cb1 = (Combobox)columns.get(12);
					Combobox cb2 = (Combobox)columns.get(13);
					
					/*Combobox cb1 = (Combobox)columns.get(12);
					Combobox cb2 = (Combobox)columns.get(13);
					*/
					
					
					/*Decimalbox numberbox1 = (Decimalbox)columns.get(6);
					Decimalbox numberbox2 = (Decimalbox)columns.get(7);*/
					Decimalbox numberbox1 = (Decimalbox)columns.get(7);
					Decimalbox numberbox2 = (Decimalbox)columns.get(8);
					//Listbox campLb = (Listbox)columns.get(10); 
					
					if(!numberbox1.isVisible() && !numberbox2.isVisible() && !txtbox.isVisible() && !cb1.isVisible() && !cb2.isVisible() ){//&& !campLb.isVisible()){
						
						if(valStr.isEmpty()) {
							
							valStr = segmentEnum.getColumnValue();
						}
						ruleSB.append(valStr);
						
					}
					else if(numberbox1.isVisible()) {
					
						if(numberbox1.getValue() == null || numberbox1.getValue().compareTo(BigDecimal.ZERO) <0 ) {
						//	MessageUtil.setMessage("Value should not be empty. ", "color:red", "TOP");
							isValid = false;
							setErrorToDiv(ruleDiv, isValid);
							continue;
						}
						ruleSB.append(numberbox1.getValue());
						if( (segmentEnum.getToken().equalsIgnoreCase("between") 
								|| segmentEnum.getToken().equalsIgnoreCase("range") ) 
								&& numberbox2.isVisible()) {
							
							if(numberbox2.getValue() == null||numberbox2.getValue().compareTo(BigDecimal.ZERO) <0) {
								//MessageUtil.setMessage("Value should not be empty. ", "color:red", "TOP");
								isValid = false;
								setErrorToDiv(ruleDiv, isValid);
								continue;
							}
							if(numberbox2.getValue().compareTo( numberbox1.getValue()) == -1 || numberbox2.getValue().compareTo( numberbox1.getValue()) == 0) {
								isValid = false;
								setErrorToDiv(ruleDiv, isValid);
								continue;
								
							}
							ruleSB.append("|"+numberbox2.getValue());
							
						}//if
						
						
						/*if( campLb.isVisible() ) {
							
							if(campLb.getItemCount() == 0) {
								
								isValid = false;
								setErrorToDiv(ruleDiv, isValid);
								continue;
							}
							
							ruleSB.append("|"+((Campaigns)campLb.getSelectedItem().getValue()).getCampaignId().longValue());
							
							
						}//if
*/					
					}//if
					else if(cb1.isVisible()) {
						String cb1ValStr = cb1.getValue();
						String cb2ValStr = cb2.getValue();

						
						if(cb1ValStr == null || cb1ValStr.trim().length() == 0 ) {
						//	MessageUtil.setMessage("Value should not be empty. ", "color:red", "TOP");
							isValid = false;
							setErrorToDiv(ruleDiv, isValid);
							continue;
						}
						
						double cb1ValDbl=0;
						
						try{
							
							if(cb1ValStr.contains(",") ) {
								
								if(segmentEnum.getToken().toLowerCase().contains("is in")) {
									String[] valArr = cb1ValStr.split(",");
									for (int i = 0; i < valArr.length; i++) {
										
										cb1ValDbl = Double.parseDouble(valArr[i]);
										
									}//for
								}else {
									
									isValid = false;
									setErrorToDiv(ruleDiv, isValid);
									continue;
								}
								
							}
							else{
								
								cb1ValDbl = Double.parseDouble(cb1ValStr);
								
							}
							
						}catch (NumberFormatException e) {
							isValid = false;
							setErrorToDiv(ruleDiv, isValid);
							continue;
						
						}
						
						ruleSB.append(cb1ValStr);
						if(segmentEnum.getToken().equalsIgnoreCase("between") && cb2.isVisible()) {
							
							if(cb2ValStr == null || cb2ValStr.trim().length() == 0) {
								//MessageUtil.setMessage("Value should not be empty. ", "color:red", "TOP");
								isValid = false;
								setErrorToDiv(ruleDiv, isValid);
								continue;
							}
							double cb2ValDbl = 0;
							try{
								
								cb2ValDbl = Double.parseDouble(cb2ValStr);
								
							}catch (NumberFormatException e) {
								isValid = false;
								setErrorToDiv(ruleDiv, isValid);
								continue;
							
							}
							if(cb1ValDbl >= cb2ValDbl) {
								isValid = false;
								setErrorToDiv(ruleDiv, isValid);
								continue;
								
							}
							ruleSB.append("|"+cb2ValStr);
							
						}//if
						
						
						/*if( campLb.isVisible() ) {
							
							if(campLb.getItemCount() == 0) {
								
								isValid = false;
								setErrorToDiv(ruleDiv, isValid);
								continue;
							}
							
							ruleSB.append("|"+((Campaigns)campLb.getSelectedItem().getValue()).getCampaignId().longValue());
							
							
						}//if
					
					*/
						
						
						
						
					}// else if
					else if(!numberbox1.isVisible() && !numberbox2.isVisible() && !txtbox.isVisible() && !cb1.isVisible() && !cb2.isVisible() ){//&& campLb.isVisible()){
						
						/*if(campLb.getItemCount() == 0) {
							
							isValid = false;
							setErrorToDiv(ruleDiv, isValid);
							continue;
						}
						
						ruleSB.append("0|"+((Campaigns)campLb.getSelectedItem().getValue()).getCampaignId().longValue());
						*/
					}
					else if(txtbox.isVisible() && !cb1.isVisible()) {
						
						try {
							String valueStr = txtbox.getText();
							if(valueStr.contains(",")) {
								String[] valArr = valueStr.split(",");
								for (int i = 0; i < valArr.length; i++) {
									if(col != null && col.equals("c.zip") ){
										String value = valArr[i].trim();
									}else{
										
										int value = Integer.parseInt(valArr[i].trim());
									}
									
								}//for
								
							}else {
								if(col != null && col.equals("c.zip") ){
									String value = txtbox.getText().trim();
								}else{
									
									int value = Integer.parseInt(txtbox.getText().trim());
								}
							
							}
							
						}  catch (NumberFormatException e) {
							
							isValid = false;
							setErrorToDiv(ruleDiv, isValid);
							continue;
						}
						ruleSB.append(txtbox.getText());
						
					}
					
					
				
			}//else if
			else if (type.equalsIgnoreCase("boolean")) {
				
				if(valStr.isEmpty()) {
					
					valStr = segmentEnum.getColumnValue();
					
				}
				ruleSB.append(valStr);
				
			}
			 if(segmentEnum.name().equalsIgnoreCase(SegmentEnum.PROMO_OPTCULTURE_NAME.name()	) && coupon!=null) {
				 ruleSB.append(";=;"+coupon.getCouponId());
			 }
			
			 
			isValid = true;
			if(ruleDiv!=null)
			setErrorToDiv(ruleDiv, isValid);
				
			if(ruleSB.toString().contains("aggrlty_totreward")) {
				if(!selectedValueCode.isEmpty()) selectedValueCode += Constants.DELIMETER_COMMA;
				selectedValueCode += "'"+ruleLb.getSelectedItem().getLabel()+"'";
				ruleSB.append("|"+ruleLb.getSelectedItem().getLabel());
			}
			if(ruleSB.toString().contains("aggrlty_reward")) {
				if(!selectedValueCode.isEmpty()) selectedValueCode += Constants.DELIMETER_COMMA;
				selectedValueCode += "'"+ruleLb.getSelectedItem().getLabel()+"'";
				ruleSB.append("|"+ltylb.getSelectedItem().getLabel());
			}
			if(childDivList.size() > 1 ) operator ="<OR>";
			ruleSB.append(operator);
			
		}//if
	}//for
		
		if(isValid==false) retValue=false;
		
		if(!retValue) return null;
		
		logger.info("ruleSb :: "+ruleSB.toString());
		
		
		return ruleSB.toString();
		
}// prepareSegmentRule(Vbox ruleVb)
	
	
	public void setErrorToDiv(Div errorDiv, boolean isValid ) {
		
		if(isValid) {
			errorDiv.setSclass("segment_child_div");
		}
		else {
			errorDiv.setSclass("segment_child_error_div");
		}
		
	}
	
	
	/*@SuppressWarnings("unchecked")
	private String prepareRule(Hbox ruleHb) {
		
		StringBuffer ruleSB = new StringBuffer();
		DateFormat dateFormat = 
			new SimpleDateFormat(PropertyUtil.getPropertyValue("customFiledDateFormat"));
		
		try {
			List columns = ruleHb.getChildren();
			Listbox lb = (Listbox)columns.get(0);
			ListSegmentRule lstSegRule;
			
			if(lb.getSelectedIndex() == -1) {
				lstSegRule = (ListSegmentRule)lb.getItemAtIndex(0).getValue();
			} else {
				lstSegRule = (ListSegmentRule)lb.getSelectedItem().getValue();
			}
			
			if(lstSegRule.getColumnName().startsWith("cust_")) {
				ruleSB.append("CF:"+lstSegRule.getMailingListId()+":"+
						lstSegRule.getColumnName()+":"+lstSegRule.getFieldName()+"|");
			} else {
				ruleSB.append(lstSegRule.getColumnName()+"|");
			}

			lb = (Listbox)columns.get(1);
			String dataType = lstSegRule.getDataType();
			String operation ;
			if(lb.getSelectedIndex() == -1) {
				operation = (String)lb.getItemAtIndex(0).getLabel();
			} else {
				operation = (String)lb.getSelectedItem().getLabel();
			}
			logger.debug("selected index :"+lb.getSelectedIndex()+", operation :"+operation);
			ruleSB.append(dataType+":"+operation+"|");
			
			if(dataType.equalsIgnoreCase(ListSegmentRule.STRING)) {
				Textbox tb1 = (Textbox)columns.get(2);
				try {
					String valueStr = tb1.getValue();
					if(valueStr.length() <= 0) {
						MessageUtil.setMessage("Value should not be empty. ", "color:red", "TOP");
						return null;
					}
					if(valueStr.contains("|")) {
						MessageUtil.setMessage("Value should not contain pipe(|).", "color:red", "TOP");
						return null;
					}
					ruleSB.append(valueStr);
				} catch (Exception e) {
					logger.error("Exception : ", (Throwable)e);
					MessageUtil.setMessage("Wrong value "+tb1.getValue(), "color:red", "TOP");
					return null;
				}
				
			} 
			else if(dataType.equalsIgnoreCase(ListSegmentRule.NUMBER)) {
				Textbox tb1 = (Textbox)columns.get(2);
				try {
					long value1 = Long.parseLong(tb1.getValue());
					ruleSB.append(value1);
				} catch (NumberFormatException e) {
					logger.error("NumberFormatException : ", (Throwable)e);
					MessageUtil.setMessage("Wrong value selected "+tb1.getValue(), "color:red", "TOP");
					return null;
				}
				Textbox tb2 = (Textbox)columns.get(3);
				if(lb.getSelectedItem().getLabel().equalsIgnoreCase("between") || tb2.isVisible()) {
					try {
						long value2 = Long.parseLong(tb2.getValue());
						ruleSB.append("|"+value2);
					} catch (NumberFormatException e) {
						logger.error("NumberFormatException : ", (Throwable)e);
						MessageUtil.setMessage("Wrong value selected "+tb2.getValue(), "color:red", "TOP");
						return null;
					}
				}
			}
			else if(dataType.equalsIgnoreCase(ListSegmentRule.DATE)) {
				
				Datebox dateBox1 = (Datebox)columns.get(4);
				Datebox dateBox2 = (Datebox)columns.get(5);

				ruleSB.append(dateFormat.format(dateBox1.getValue()));
				String selLabelStr;
				if(lb.getSelectedIndex() == -1){
					selLabelStr = lb.getItemAtIndex(0).getLabel();
				}else{
					selLabelStr = lb.getSelectedItem().getLabel();
				}
				if(selLabelStr.equalsIgnoreCase("between") || dateBox2.isVisible()) {
					
					if(dateBox1.getValue().after(dateBox2.getValue())) {
						MessageUtil.setMessage("First date must be later than second date. ", "color:red", "TOP");
						return null;
					}
					ruleSB.append("|"+dateFormat.format(dateBox2.getValue()));
				}
			}else if(dataType.equalsIgnoreCase(ListSegmentRule.BOOLEAN)) {
				Listbox booleanLb = (Listbox)columns.get(6);
				if(booleanLb.getSelectedIndex() == -1) {
					ruleSB.append(booleanLb.getItemAtIndex(0).getLabel());
				}else{
					ruleSB.append(booleanLb.getSelectedItem().getLabel());
				}
				
			}
			MessageUtil.clearMessage();
			
		} catch (Exception e) {
			logger.error("** Exception : While preparing the rule ", (Throwable)e);
		}

		return ruleSB.toString();
	}
	*/
	
	
	
	
	/*@SuppressWarnings("unchecked")
	private String prepareRule(String listIdsStr) {
		
		StringBuffer segmentRuleSB = new StringBuffer(segRuleOptionLbId.getSelectedItem().getLabel()+":");
		List rulesList = segmentRulesVbId.getChildren();

		segmentRuleSB.append(listIdsStr);
		logger.debug(" Rule Before adding fields : "+segmentRuleSB.toString());
		
		String tempStr;
		boolean isValid = true;
		
		if(rulesList.size() <= 1) {
			MessageUtil.setMessage("At least one segment rule must be selected.", 
					"color:red", "TOP");
			isValid = false;
		}
		
		for(int i=1; i<rulesList.size();i++) {
			
			try{
				Hbox ruleHb = (Hbox)rulesList.get(i);
				tempStr = prepareRule(ruleHb);
				if(tempStr == null) {
					ruleHb.setStyle("padding:10px;border:1px solid;color:red");
					isValid = false;
				}else {
					ruleHb.setStyle("padding:10px;border:none");
				}
				segmentRuleSB.append("||"+tempStr);
			}catch(Exception e){
				logger.error("** Exception : Problem while preparing the rules in for loop", 
						(Throwable)e);
			}
		}
		
		logger.debug("****************** segmentRuleSb :"+segmentRuleSB.toString());
		if(!isValid) {
			logger.warn(" >>>>>>>>>>>>> Segmentation validation failed ");
			return  null;
		}
		return segmentRuleSB.toString();
	}
	*/
	
	/*@Override
	public void onEvent(Event event) throws Exception {
		super.onEvent(event);
		if(event instanceof DropEvent) 
		DropEvent dropEvent = ((DropEvent) ((ForwardEvent) event).getOrigin());
		Component dragged = dropEvent.getDragged();
		if(dragged instanceof Listitem) {
			Listitem item = (Listitem) dragged;
			Div obj = (Div)event.getTarget();
			Div div = createNewRuleDiv(item);//elements div
			div.setParent(obj);
			updateORLabel(div);
			
		}
		
		
	}*/
	
	private Div createDropORDiv(String dragMsg) {
		Div dropDiv = new Div();
		dropDiv.setSclass("drop_or_div");
		Label orLbl = new Label(" OR ");
		orLbl.setStyle("left: -80px; position: absolute;");
		dropDiv.appendChild(orLbl);
		dropDiv.appendChild(new Label(dragMsg));
		
		return dropDiv;
	}
	
	
	private Div profileAttributeDivId, purIntAttributeDivId, intAttributeDivId;
	public void onDrop$profileAttributeDivId(Event event) {
		
		logger.debug("---ControllerSide -- Drop Event ="+event);

		DropEvent dropEvent = ((DropEvent) ((ForwardEvent) event).getOrigin());
		Component dragged = dropEvent.getDragged();
		
		logger.debug("----------Dragged target ="+dragged);
		
		if (dragged instanceof Listitem) {
			// new object
			Listitem item = (Listitem) dragged;
			//final Listitem itemForChild = item;
			
			
			Div tempDiv = new Div();
			tempDiv.setSclass("");
			tempDiv.setParent(profileAttributeDivId);
			
			Label AndLabel = new Label("AND");
			AndLabel.setStyle("display:inline-block; width:20px; padding:0 5px;margin:0 10px;font-weight:bold;");
			AndLabel.setParent(tempDiv);
			
			
			
			Div vdiv = new Div();//this is the outer component
			vdiv.setDroppable("profileSegment");
			vdiv.setParent(tempDiv);
			vdiv.setSclass("segment_parent_div");
			updateAndLabel(profileAttributeDivId);
			//vdiv.setStyle("overflow:auto;background:#EEFFDD;border:1px solid #AAAAAA;margin:15px 10px;");
			//vdiv.setWidth("95%");
			//vdiv.setHeight("150px");
			
			/*Label orlbl = new Label("(OR)");
			orlbl.setParent(obj);
			orlbl.setStyle("")*/
			
			Div obj = createNewRuleDiv(item);
			obj.setParent(vdiv);
			updateORLabel(vdiv);

			// Generate Drop Div
			vdiv.appendChild(createDropORDiv("Drag Profile Attribute here to create OR combination rule."));
			
			vdiv.addEventListener("onDrop", myDropListener);
		}
		
	} // onDrop$programdesignerWinId
	public void onDrop$intAttributeDivId(Event event) {
		

		
		logger.debug("---ControllerSide -- Drop Event ="+event);

		DropEvent dropEvent = ((DropEvent) ((ForwardEvent) event).getOrigin());
		Component dragged = dropEvent.getDragged();
		
		logger.debug("----------Dragged target ="+dragged);
		
		if (dragged instanceof Listitem) {
			// new object
			Listitem item = (Listitem) dragged;
			final Listitem itemForChild = item;
			
			Div tempDiv = new Div();
			tempDiv.setSclass("");
			tempDiv.setParent(intAttributeDivId);
			
			Label AndLabel = new Label("AND");
			AndLabel.setStyle("display:inline-block; width:20px; padding:0 5px;margin:0 10px;font-weight:bold;");
			AndLabel.setParent(tempDiv);
			
			
			Div vdiv = new Div();//this is the outer component
			vdiv.setDroppable("IntSegment");
			vdiv.setParent(tempDiv);
			vdiv.setSclass("segment_parent_div");
			updateAndLabel(intAttributeDivId);
			//vdiv.setStyle("overflow:auto;background:#EEFFDD;border:1px solid #AAAAAA;margin:15px 10px;");
			//vdiv.setWidth("95%");
			//vdiv.setHeight("150px");
			
			/*Label orlbl = new Label("(OR)");
			orlbl.setParent(obj);
			orlbl.setStyle("")*/
			
			Div obj = createNewRuleDiv(item);
			obj.setParent(vdiv);
			updateORLabel(vdiv);
			//obj.setStyle("padding:10px 0px;");
			
			// Generate Drop Div
			vdiv.appendChild(createDropORDiv("Drag Interaction Attribute here to create OR combination rule."));
			
			vdiv.addEventListener("onDrop", myDropListener);
		}
		
	
		
		
		
		
		
	}
	boolean flag=true;
	int count=0;
	public void onDrop$purIntAttributeDivId(Event event) {
		
		logger.debug("---ControllerSide -- Drop Event ="+event);

		DropEvent dropEvent = ((DropEvent) ((ForwardEvent) event).getOrigin());
		Component dragged = dropEvent.getDragged();
		
		logger.debug("----------Dragged target ="+dragged);
		
		if (dragged instanceof Listitem) {
			// new object
			Listitem item = (Listitem) dragged;
			  if(item.getValue().toString().equals(SegmentEnum.PROMO_CODE_DATE_STATUS.name())||item.getValue().toString().equals(SegmentEnum.PROMO_CODE_PROMO_STATUS.name())) {
				 count++;
				 logger.info("drag count::"+count);
				 if(flag) {
					 flag=false;
				    Div tempDiv = new Div();
					tempDiv.setSclass("");
					tempDiv.setAttribute("Type", item.getValue().toString());
					tempDiv.setParent(purIntAttributeDivId);
					
					
					Label AndLabel = new Label("AND");
					AndLabel.setStyle("display:inline-block; width:20px; padding:0 5px;margin:0 10px;font-weight:bold;");
					AndLabel.setParent(tempDiv);
					
					
					Div vdiv = new Div();//this is the outer component
					//vdiv.setDroppable("purIntSegment");
					vdiv.setParent(tempDiv);
					vdiv.setSclass("segment_parent_div");
					updateAndLabel(purIntAttributeDivId);
					
					List<SegmentEnum> purchaseAttrList = SegmentEnum.getEnumsByCategoryCode(4);
					
					Listitem subitem = null;
					for (SegmentEnum purchaseSegEnum : purchaseAttrList) {
						
						subitem = new Listitem(purchaseSegEnum.getDispLabel(), purchaseSegEnum);
						subitem.setStyle("padding:10px;");
						subitem.setAttribute("DBcolumnName", purchaseSegEnum.getColumnName());
						subitem.setAttribute("item", purchaseSegEnum.getItem());
						logger.debug("purchaseSegEnum.getItem() ::"+purchaseSegEnum.getItem());
					}
					Div obj = createNewRuleDiv(subitem);
					obj.setParent(vdiv);
					updateORLabel(vdiv);
					vdiv.addEventListener("onDrop", myDropListener);
			      }
			  }
			final Listitem itemForChild = item;
			
			Div tempDiv = new Div();
			tempDiv.setSclass("");
			tempDiv.setParent(purIntAttributeDivId);
			
			Label AndLabel = new Label("AND");
			AndLabel.setStyle("display:inline-block; width:20px; padding:0 5px;margin:0 10px;font-weight:bold;");
			AndLabel.setParent(tempDiv);
			
			
			Div vdiv = new Div();//this is the outer component
			vdiv.setDroppable("purIntSegment");
			vdiv.setParent(tempDiv);
			vdiv.setSclass("segment_parent_div");
			updateAndLabel(purIntAttributeDivId);
			//vdiv.setStyle("overflow:auto;background:#EEFFDD;border:1px solid #AAAAAA;margin:15px 10px;");
			//vdiv.setWidth("95%");
			//vdiv.setHeight("150px");
			
			/*Label orlbl = new Label("(OR)");
			orlbl.setParent(obj);
			orlbl.setStyle("")*/
			
			Div obj = createNewRuleDiv(item);
			obj.setParent(vdiv);
			updateORLabel(vdiv);
			//obj.setStyle("padding:10px 0px;");
			
			// Generate Drop Div
			vdiv.appendChild(createDropORDiv("Drag Purchase Attribute here to create OR combination rule."));
			
			vdiv.addEventListener("onDrop", myDropListener);
		}
		
	} // onDrop$programdesignerWinId
	
	
	
	
	private MyDropEventListener myDropListener = new MyDropEventListener();
	
	private class MyDropEventListener implements EventListener {
		
		
		public MyDropEventListener() {}
		@Override
		public void onEvent(Event event) throws Exception {
			DropEvent dropEvent = (DropEvent) event;
			Component dragged = dropEvent.getDragged();
			if(dragged instanceof Listitem) {
				Listitem item = (Listitem) dragged;
				if(item.getValue().toString().equals(SegmentEnum.PROMO_CODE_DATE_STATUS.name())||item.getValue().toString().equals(SegmentEnum.PROMO_CODE_PROMO_STATUS.name()))
					 count++;
				Div obj = (Div)event.getTarget();
				Div div = createNewRuleDiv(item);//elements div
				div.setParent(obj);
				/*if(item.getAttribute("isDragged") == null) {
					
					item.setDraggable(null);
					item.setAttribute("isDragged", "Done");
					//make received rule can be draggable only to this v div.
					
				}*/
				
				
				
				
				//updateORLabel(div);
				
			}//if
			
		}//onEvent()
		
	}//class
	
	public  void updateORLabel(Component vdiv) {
		
		logger.info("SCLASS="+ ((Div)vdiv).getSclass());
		List<Component> divComp = vdiv.getChildren();
		
		for (Component eachDiv : divComp) {

			logger.info("each CLASS="+ eachDiv.getClass());
			
			if( ((Div)eachDiv).getSclass().contains("drop_")) continue;
			
			Component label = eachDiv.getFirstChild();
			
			if(label!=null && label instanceof Label) {
				((Label)label).setValue("");
			}
			
			break;
		} // for 
		
	} // updateORLabel
	
	
	public void updateAndLabel(Component parentDiv) {
		
		logger.info("in update AND label ::"+parentDiv.getId() );
		List<Component> divComp = parentDiv.getChildren();
		
		
		for (Component eachDiv : divComp) {
			
			
			logger.info("each CLASS="+ eachDiv.getClass());
			if(eachDiv instanceof Div) {
				
				if(((Div) eachDiv).getSclass() == null ) {
					
					Component label = eachDiv.getFirstChild();
					logger.info("each CLASS="+ label.getClass());
					
					
					if(label!=null && label instanceof Label) {
						
						((Label)label).setValue("");
					}
					
					break;
					
					
				}
				
				else if( ((Div)eachDiv).getSclass().contains("drop_")) continue;
				
				
			}
		} // for 
		
		
		
	}
	
	private Map<String, String[]> purchaseDateFldOpMap = new HashMap<String, String[]>();
	
	
	private SegmentEnum getSegmentEnumByCode(int itemIndex, int categoryCode) {
		
		SegmentEnum tempEnum[] = SegmentEnum.values();
		
		for (SegmentEnum segmentEnum : tempEnum) {
			
			
				if(segmentEnum.getCode() == itemIndex && segmentEnum.getCategoryCode() == categoryCode) {
					return segmentEnum;
				}
			
			
		}
		return null;
		
	}
	
	public Div getCreatedCompDiv(SegmentEnum segmentEnum, Listitem item) {
		
		Listitem li = null;//for rulelb 
		Div testDiv = new Div();
		
		
		testDiv.setSclass("segment_child_div");
		
		
		
		Label orlbl  = new Label(" OR ");//or label-----0
		orlbl.setStyle("display:inline-block; width:20px; padding:0 5px;margin:0 10px;font-weight:bold;");
		orlbl.setParent(testDiv);
		
		Label lbl  = new Label();//display label-----1
		lbl.setStyle("padding:0 5px; display:inline-block; width:150px;");
		
		lbl.setParent(testDiv);
		lbl.setValue(item.getLabel());
		lbl.setAttribute("columnName", (String)item.getAttribute("DBcolumnName"));
		lbl.setAttribute("item", (String)item.getAttribute("item"));
		
		
		Listbox rulelb = new Listbox();//rule lb----2
		rulelb.setMold("select");
		if(segmentEnum.name().equalsIgnoreCase(SegmentEnum.PROMO_CODE_DATE_STATUS.name()))
		rulelb.setStyle("margin-right:5px; width:110px;");
		else
		rulelb.setStyle("margin-right:5px; width:180px;");
		rulelb.setParent(testDiv);
		
		//for lty harshi ---------3
		 final Listbox ltylb = new Listbox();
		//if(segmentEnum.name().equalsIgnoreCase("LOYALTY_TRANSACTIONS")){
		ltylb.setMold("select");
		ltylb.setStyle("margin-right:5px;");
	     	ltylb.setParent(testDiv);
	     	ltylb.setVisible(false);
		 //}
		
		final Listbox numberLb = new Listbox();//days/weeks/mnths------4
		numberLb.setMold("select");
		
		numberLb.setStyle("margin-right:5px;");
		
		for(int i=1; i<=30; i++){
			li = new Listitem(""+i);
			li.setParent(numberLb);
		}
		numberLb.setVisible(false);
		
		numberLb.setParent(testDiv);
		
		final Listbox operatorlb = new Listbox();//rule options lb----------5
		operatorlb.setMold("select");
		operatorlb.setStyle("margin-right:5px;");
		operatorlb.setParent(testDiv);
		
		
		final Textbox textBox = new Textbox();//string type value--------6
		textBox.setStyle("margin-right:5px;");
		textBox.setParent(testDiv);
		textBox.setVisible(false);
		
		final Decimalbox dblBox1 = new Decimalbox();//number type value1------------7
		dblBox1.setStyle("margin-right:5px;");
		dblBox1.setParent(testDiv);
		dblBox1.setVisible(false);
		
		final Decimalbox dblBox2 = new Decimalbox();//number type value2------------8
		dblBox2.setStyle("margin-right:5px;");
		dblBox2.setParent(testDiv);
		
		dblBox2.setVisible(false);
		
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		final Datebox db1 = new MyDatebox();//date type value1------------9
		db1.setStyle("margin-right:10px;");
		db1.setFormat(format.toPattern());
		db1.setReadonly(true);
		db1.setVisible(false);
		db1.setParent(testDiv);
		db1.setLenient(false);
		
		
		final Datebox db2 = new MyDatebox();//date type value2------------10
		db2.setStyle("margin-right:10px;");
		db2.setParent(testDiv);
		db2.setReadonly(true);
		db2.setFormat(format.toPattern());
		db1.setLenient(false);
		db2.setVisible(false);
		
		
		/*final Listbox campLb = new Listbox();//-------10
		
		campLb.setMold("select");
		//campLb.setStyle("margin-right:5px;");
		campLb.setParent(testDiv);
		campLb.setVisible(false);*/
		/*if(item.getLabel().trim().equalsIgnoreCase("Opens") || item.getLabel().trim().equalsIgnoreCase("Clicks")) {
			
			for (Campaigns campaign : campList) {
				
				li = new Listitem(campaign.getCampaignName(), campaign);
				li.setParent(campLb);
				
				
			}
			if(campLb.getItemCount() > 0) campLb.setSelectedIndex(0);
			campLb.setVisible(true);
			
		}*/
		
		final Listbox formLb = new Listbox();//-------11
		
		formLb.setMold("select");
			//campLb.setStyle("margin-right:5px;");
		formLb.setParent(testDiv);
		formLb.setVisible(false);
		logger.info("item.getLabel().trim() "+item.getLabel().trim());
		if(item.getLabel().trim().equalsIgnoreCase(SegmentEnum.PROFILE_OPT_IN_MEDIUM.getDispLabel()) && mappingsList != null) {
				
			for (FormMapping mapping : mappingsList) {
				
				li = new Listitem(mapping.getFormMappingName(), mapping);
				li.setParent(formLb);
			}
			
			if(formLb.getItemCount() > 0) formLb.setSelectedIndex(0);
			formLb.setVisible(false);
			
		}
		
		final Combobox cb1 = new Combobox();//----12
		cb1.setSclass("cb_100w");
		cb1.setStyle("margin-right:5px;");
		cb1.setParent(testDiv);
		cb1.setVisible(false);
		
		final Combobox cb2 = new Combobox();//--------13
		cb2.setSclass("cb_100w");
		cb2.setStyle("margin-right:5px;");
		cb2.setParent(testDiv);
		cb2.setVisible(false);
		
		String optionalValues = (String)item.getAttribute("optionalValues");
		
		if(optionalValues != null) {
			String optValues[] = optionalValues.split(Constants.ADDR_COL_DELIMETER);
			for (String optVal : optValues) {
				cb1.appendItem(optVal);
				cb2.appendItem(optVal);
			}
		}
		
		/*if(cb1.getItemCount()>0) cb1.setSelectedIndex(0);
		if(cb2.getItemCount()>0) cb2.setSelectedIndex(0);
		*/
		
		
		Toolbarbutton deleteIcon  = new Toolbarbutton();//-------13 (14)
		deleteIcon.setImage("/images/action_delete.gif");
		deleteIcon.setStyle("float:right; margin-right:25px;");
		deleteIcon.setAttribute("Type", segmentEnum.name());
		if(!segmentEnum.name().equalsIgnoreCase(SegmentEnum.PROMO_OPTCULTURE_PROMOTION_NAME.name())) {
		deleteIcon.setParent(testDiv);
		}
		
		
		if(segmentEnum.name().equalsIgnoreCase(SegmentEnum.LOYALTY_TRANSACTIONS.name())||
				segmentEnum.name().equalsIgnoreCase(SegmentEnum.PROMO_CODE_DATE_STATUS.name())||
				segmentEnum.name().equalsIgnoreCase(SegmentEnum.PURCHASE_LOYALTY_BALANCE.name())){
			ltylb.setVisible(true);
		}
		List<SegmentEnum> ruleEnums = segmentEnum.getChidrenByParentEnum(segmentEnum);//to prepare rule lb
		
		String rule = "";
		int strtIndex;
		int endIndex;
		
		for (SegmentEnum enums : ruleEnums) {
			
			if(!enums.getDispLabel().isEmpty()) {
			li = new Listitem(enums.getDispLabel(),enums);
			li.setParent(rulelb);
			}
			//if(enums.getType().equalsIgnoreCase("string")) textBox.setVisible(true);
			
		}//for lty
		for (SegmentEnum enums : ruleEnums) {
			//List<SegmentEnum> valueEnum = segmentEnum.getChidrenByParentEnum(enums);
			//for(SegmentEnum sunEnum : valueEnum) {
			if(enums.name().equalsIgnoreCase(SegmentEnum.VALUE_CODES.name()) && valueCodeList !=null){
				for (ValueCodes vc : valueCodeList) {
					li = new Listitem(vc.getValuCode(), enums);
					li.setParent(rulelb);
					li.addEventListener("onSelect", this);
				}
			}
		}
		//}
		if(ruleEnums.get(0).name().equalsIgnoreCase(SegmentEnum.NUMBER_OF_TRANSACTIONS.name())||ruleEnums.get(0).name().equalsIgnoreCase(SegmentEnum.PROMO_CODE_ISSUED.name())
				||ruleEnums.get(0).name().equalsIgnoreCase(SegmentEnum.PROMO_CODE_REDEEMED.name())||
				ruleEnums.get(0).name().equalsIgnoreCase(SegmentEnum.PROMO_CODE_EXPIRED.name())||
				ruleEnums.get(0).name().equalsIgnoreCase(SegmentEnum.PURCHASE_LOYALTY_TOTAL_ISSUED_IS.name())||
				ruleEnums.get(0).name().equalsIgnoreCase(SegmentEnum.PURCHASE_LOYALTY_BALANCE_IS.name())) {
		creteOperatorOptionsForRule(ruleEnums.get(0), ltylb,null);
		List<SegmentEnum> ltyRuleEnum = segmentEnum.getChidrenByParentEnum(ruleEnums.get(0));
		List<SegmentEnum> ltySubRuleEnum = new ArrayList<SegmentEnum>();
			SegmentEnum ltyEnum = ltyRuleEnum.get(0);
			ltySubRuleEnum = segmentEnum.getChidrenByParentEnum(ltyEnum);
				for (SegmentEnum ltySubEnum : ltySubRuleEnum) {
				li = new Listitem(ltySubEnum.getDispLabel(),ltySubEnum);
				li.setParent(operatorlb);
				li.addEventListener("onSelect", this);
				operatorlb.setSelectedIndex(0);
			}
		}
		else
			creteOperatorOptionsForRule(ruleEnums.get(0), operatorlb,null);
			
			//}
			/*for (SegmentEnum ltySubEnum : ltySubRuleEnum) {
				li = new Listitem(ltySubEnum.getDispLabel(),ltySubEnum);
				li.setParent(ltylb);
				ltylb.setSelectedIndex(0);
			}*/
		/*if(ltyRuleEnum!=null && ltyRuleEnum.size()>0){
			creteOperatorOptionsForRule(ltyRuleEnum.get(0), operatorlb);
		}*/
		
		if(rulelb.getItemCount() > 0) rulelb.setSelectedIndex(0);
		//if(operatorlb.getItemCount() > 0) operatorlb.setSelectedIndex(0);
		
		
		SegmentEnum tempEnum = null;
		if(operatorlb.isVisible() && operatorlb.getItemCount() > 0){
			
			operatorlb.setSelectedIndex(0);
			tempEnum = (SegmentEnum)operatorlb.getSelectedItem().getValue();
			
		}else {
			
			tempEnum = (SegmentEnum)rulelb.getSelectedItem().getValue();
		}
		
		//test for lty num field
		/*if(ltylb.isVisible() && ltylb.getItemCount() > 0){
			
			tempEnum = (SegmentEnum)ltylb.getSelectedItem().getValue();
		}*/
		
		if (tempEnum != null) {
			String type = tempEnum.getType();
			if (type.equalsIgnoreCase("date")) {

				dblBox1.setVisible(false);
				dblBox2.setVisible(false);
				textBox.setVisible(false);

				createValuesForOperatorForTypeDate(db1, db2, numberLb,
						tempEnum, dblBox1, dblBox2);
			} else if (type.equalsIgnoreCase("string")) {

				numberLb.setVisible(false);
				db1.setVisible(false);
				db1.setVisible(false);
				dblBox1.setVisible(false);
				dblBox2.setVisible(false);
				createValuesForOperatorTypeSting(textBox, tempEnum, cb1, cb2,
						formLb);

			} else if (type.equalsIgnoreCase("number")) {

				numberLb.setVisible(false);
				db1.setVisible(false);
				db1.setVisible(false);
				textBox.setVisible(false);
				createValuesForOperatorTypeNumber(dblBox1, dblBox2, textBox,
						tempEnum, cb1, cb2);

			}
		}
		
		
		
		
		//SimpleDateFormat fomat = new SimpleDateFormat("dd/MM/yyyy");
		
		
		deleteIcon.addEventListener("onClick", new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				try {
				Toolbarbutton deleteIcon = (Toolbarbutton)event.getTarget();
				//logger.info("deleteIcon.getParent().getParent().getChildren().size()"+deleteIcon.getParent().getParent().getChildren().size());
		
				Component componentsDiv = deleteIcon.getParent();
				//Label  intLabel = (Label)componentsDiv.getChildren().get(0);
				Component rulesDiv = componentsDiv.getParent();
				Component ruleParent = rulesDiv.getParent();
				Component parent = ruleParent.getParent();
				
				 logger.info("Count ::"+count);
				if(deleteIcon.getAttribute("Type").equals(SegmentEnum.PROMO_CODE_DATE_STATUS.name())|| deleteIcon.getAttribute("Type").equals(SegmentEnum.PROMO_CODE_PROMO_STATUS.name())) {
					count--;
					if(count==0) {
					List<Component> list=purIntAttributeDivId.getChildren();
					for(Component comp:list) {
						Div tempDiv=(Div) comp;
						if(tempDiv.getAttribute("Type")!=null && (tempDiv.getAttribute("Type").toString().equals(SegmentEnum.PROMO_CODE_PROMO_STATUS.name())||tempDiv.getAttribute("Type").toString().equals(SegmentEnum.PROMO_CODE_DATE_STATUS.name()))) {
							purIntAttributeDivId.removeChild(tempDiv);
							flag=true;count=0;
							break;
						}
					  }
					}
				}
				rulesDiv.removeChild(componentsDiv);
				logger.info("rulesDiv parent is ::"+(Div)rulesDiv.getParent().getParent()+
						" rulesDiv.getChildren().size() "+rulesDiv.getChildren().size());
				
					if(rulesDiv.getChildren().size()==0) {
						
						rulesDiv.getParent().removeChild(rulesDiv);
						ruleParent.getParent().removeChild(ruleParent);
						
						
						
						
					}
					else if(rulesDiv.getChildren().size()==1) {
						
						logger.info("((Div)rulesDiv.getFirstChild()).getSclass() ::"+((Div)rulesDiv.getFirstChild()).getSclass());
						if(((Div)rulesDiv.getFirstChild()).getSclass().equalsIgnoreCase("drop_or_div")) {
							
							rulesDiv.getParent().removeChild(rulesDiv);
							ruleParent.getParent().removeChild(ruleParent);
							
							
						}
					}
					else {
						
						updateORLabel(rulesDiv);
						
					}
				
				updateAndLabel(parent);
				
				//this is required only for interaction attributes
				//after all deleted enable the draggable option for int rules
					
					/*for (Listitem receviedRuleItem : interactionAttrLbId.getItems()) {
						
						if(receviedRuleItem.getAttribute("isDragged") == null) continue;
						
						if(intLabel.getValue().equals(receviedRuleItem.getLabel())) {
								
							if(receviedRuleItem.getLabel().equals(SegmentEnum.INTERACTION_RECEIVED.getItem()) ) {
								
								receviedRuleItem.setDraggable("IntSegment");
								
							}else{
								
								logger.debug("set the items dragged=====");
								receviedRuleItem.removeAttribute("isDragged");
								receviedRuleItem.setDraggable("IntSegment");
								
							}
							
							
						}//
						else{
								
								if(receviedRuleItem.getLabel().equals(SegmentEnum.INTERACTION_RECEIVED.getItem())) {
									
									receviedRuleItem.setDraggable("IntSegment");
									
								}
								else{
									
									receviedRuleItem.setDraggable(null);
								}
								
						}
							
						}*/
						/*intRuleItem.setDraggable("IntSegment");
						intRuleItem.removeAttribute("isDragged");
						*/
					
				
			}
				catch(Exception e)
				{
					logger.info("Exception ", e);
				}
			}
		});
		
		rulelb.addEventListener("onSelect", new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				
				
				Listbox lb = (Listbox)event.getTarget();
				
				
				SegmentEnum ruleEnum = (SegmentEnum)lb.getSelectedItem().getValue();
				
				Components.removeAllChildren(operatorlb);
				creteOperatorOptionsForRule(ruleEnum, operatorlb,rulelb);
				SegmentEnum tempEnum = null;
				if(operatorlb.isVisible() && operatorlb.getItemCount() > 0){
					
					
					operatorlb.setSelectedIndex(0);
					 tempEnum = (SegmentEnum)operatorlb.getSelectedItem().getValue();
				}else {
					
					tempEnum = ruleEnum;
				}
				
				if(ruleEnum.getType().equalsIgnoreCase("date")) {
					dblBox1.setVisible(false);
					dblBox2.setVisible(false);
					textBox.setVisible(false);
					
					createValuesForOperatorForTypeDate(db1, db2, numberLb,tempEnum,dblBox1, dblBox2 );
				}
				else if(ruleEnum.getType().equalsIgnoreCase("string")) {
					if(ruleEnum.getParentEnum().name().equalsIgnoreCase(SegmentEnum.PROMO_CODE_DATE_STATUS.name())) {
							List<SegmentEnum> ltySubRuleEnum = segmentEnum.getChidrenByParentEnum(ruleEnum);
							Components.removeAllChildren(ltylb);
							for (SegmentEnum ltySubEnum : ltySubRuleEnum) {
								Listitem li = new Listitem(ltySubEnum.getDispLabel(),ltySubEnum);
								li.setParent(ltylb);
								li.addEventListener("onSelect", this);
								ltylb.setSelectedIndex(0);
						     }
							ruleEnum=ltySubRuleEnum.get(0);
						  ltySubRuleEnum = segmentEnum.getChidrenByParentEnum(ruleEnum);
							Components.removeAllChildren(operatorlb);
							for (SegmentEnum ltySubEnum : ltySubRuleEnum) {
								Listitem li = new Listitem(ltySubEnum.getDispLabel(),ltySubEnum);
								li.setParent(operatorlb);
								li.addEventListener("onSelect", this);
								operatorlb.setSelectedIndex(0);
						     }
							operatorlb.setVisible(true);
							ltylb.setVisible(true);
					}
					numberLb.setVisible(false);
					db1.setVisible(false);
					db2.setVisible(false);
					dblBox1.setVisible(false);
					dblBox2.setVisible(false);
					createValuesForOperatorTypeSting(textBox,tempEnum, cb1, cb2, formLb);
					
					
				}else if(ruleEnum.getType().equalsIgnoreCase("number")) {
					if(ruleEnum.getParentEnum().name().equalsIgnoreCase(SegmentEnum.LOYALTY_TRANSACTIONS.name())) {
						//ruleEnum=segmentEnum.getChidrenByParentEnum(segmentEnum).get(0);
							List<SegmentEnum> ltySubRuleEnum = segmentEnum.getChidrenByParentEnum(ruleEnum);
							Components.removeAllChildren(ltylb);
							for (SegmentEnum ltySubEnum : ltySubRuleEnum) {
								Listitem li = new Listitem(ltySubEnum.getDispLabel(),ltySubEnum);
								li.setParent(ltylb);
								li.addEventListener("onSelect", this);
								ltylb.setSelectedIndex(0);
						     }
							ruleEnum=ltySubRuleEnum.get(0);
						  ltySubRuleEnum = segmentEnum.getChidrenByParentEnum(ruleEnum);
							Components.removeAllChildren(operatorlb);
							for (SegmentEnum ltySubEnum : ltySubRuleEnum) {
								Listitem li = new Listitem(ltySubEnum.getDispLabel(),ltySubEnum);
								li.setParent(operatorlb);
								li.addEventListener("onSelect", this);
								operatorlb.setSelectedIndex(0);
						     }
							operatorlb.setVisible(true);
							ltylb.setVisible(true);
							textBox.setVisible(true);
					}else if(ruleEnum.getParentEnum().name().equalsIgnoreCase(SegmentEnum.PURCHASE_LOYALTY_BALANCE.name())) {

						//ruleEnum=segmentEnum.getChidrenByParentEnum(segmentEnum).get(0);
							List<SegmentEnum> ltySubRuleEnum = segmentEnum.getChidrenByParentEnum(ruleEnum);
							Components.removeAllChildren(ltylb);
							for (SegmentEnum ltySubEnum : ltySubRuleEnum) {
								if(ltySubEnum.name().equalsIgnoreCase(SegmentEnum.VALUE_CODES_TOTAL.name()) ||
										ltySubEnum.name().equalsIgnoreCase(SegmentEnum.VALUE_CODES_TOTAL_BALANCE.name())) continue;
								Listitem li = new Listitem(ltySubEnum.getDispLabel(),ltySubEnum);
								li.setParent(ltylb);
								li.addEventListener("onSelect", this);
								//ltylb.setSelectedIndex(0);
						     }
							Listitem vli = null;
							for (SegmentEnum ltySubEnum : ltySubRuleEnum) {
								if(ltySubEnum.name().equalsIgnoreCase(SegmentEnum.VALUE_CODES_TOTAL.name()) ||
										ltySubEnum.name().equalsIgnoreCase(SegmentEnum.VALUE_CODES_TOTAL_BALANCE.name())
										&& valueCodeList!=null) {
							for (ValueCodes vc : valueCodeList) {
								vli = new Listitem(vc.getValuCode(), ltySubEnum);
								vli.setParent(ltylb);
								//vli.addEventListener("onSelect", this);
								}
								}
							}
							ltylb.setSelectedIndex(0);
							ruleEnum=ltySubRuleEnum.get(0);
						  ltySubRuleEnum = segmentEnum.getChidrenByParentEnum(ruleEnum);
							Components.removeAllChildren(operatorlb);
							for (SegmentEnum ltySubEnum : ltySubRuleEnum) {
								Listitem li = new Listitem(ltySubEnum.getDispLabel(),ltySubEnum);
								li.setParent(operatorlb);
								li.addEventListener("onSelect", this);
								operatorlb.setSelectedIndex(0);
						     }
							operatorlb.setVisible(true);
							ltylb.setVisible(true);
							textBox.setVisible(true);
					
					}
					else {
						textBox.setVisible(false);
					}
					numberLb.setVisible(false);
					db1.setVisible(false);
					db2.setVisible(false);
					//textBox.setVisible(false);
					createValuesForOperatorTypeNumber(dblBox1, dblBox2, textBox, tempEnum, cb1, cb2);
					
				}
				
			}
			
		});
		
		
		operatorlb.addEventListener("onSelect", new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				
				Listbox operatorlb = (Listbox)event.getTarget();
				
				Object obj = operatorlb.getSelectedItem().getValue();
				
				if(obj instanceof SegmentEnum) {
					
					SegmentEnum segmentEnum = operatorlb.getSelectedItem().getValue();
					String type = segmentEnum.getType();
					
					if(type.equalsIgnoreCase("date")) {
					createValuesForOperatorForTypeDate(db1, db2, numberLb, segmentEnum, dblBox1, dblBox2);
						
					}else if(type.equalsIgnoreCase("string")) {
						
						
						createValuesForOperatorTypeSting(textBox, segmentEnum, cb1, cb2,formLb);
						
					}else if(type.equalsIgnoreCase("number") ) {
						
						createValuesForOperatorTypeNumber(dblBox1, dblBox2, textBox, segmentEnum, cb1,cb2);
						
					}
					
				}
				
				
				
			}
			
		});
		
		
		ltylb.addEventListener("onSelect", new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				
				Listbox ltylb = (Listbox)event.getTarget();
				
				Object obj = ltylb.getSelectedItem().getValue();
				
				if(obj instanceof SegmentEnum) {
					
					SegmentEnum segmentEnum = ltylb.getSelectedItem().getValue();
					String type = segmentEnum.getType();
					
					if(type.equalsIgnoreCase("date")) { 
						SegmentEnum ltyEnum =null;
						if(segmentEnum.getParentEnum().name().equalsIgnoreCase(SegmentEnum.PROMO_CODE_ISSUED.name())|| 
								segmentEnum.getParentEnum().name().equalsIgnoreCase(SegmentEnum.PROMO_CODE_EXPIRED.name())
								|| segmentEnum.getParentEnum().name().equalsIgnoreCase(SegmentEnum.PROMO_CODE_REDEEMED.name())) {
							ltyEnum=segmentEnum.getChidrenByParentEnum(segmentEnum).get(0);
							segmentEnum=ltyEnum;
								List<SegmentEnum> ltySubRuleEnum = segmentEnum.getChidrenByParentEnum(segmentEnum.getParentEnum());
								Components.removeAllChildren(operatorlb);
								for (SegmentEnum ltySubEnum : ltySubRuleEnum) {
									Listitem li = new Listitem(ltySubEnum.getDispLabel(),ltySubEnum);
									li.setParent(operatorlb);
									li.addEventListener("onSelect", this);
									operatorlb.setSelectedIndex(0);
							     }
						}
						textBox.setVisible(false);
						createValuesForOperatorForTypeDate(db1, db2, numberLb, segmentEnum, dblBox1, dblBox2);
						
					}else if(type.equalsIgnoreCase("string")) {
						
						
						createValuesForOperatorTypeSting(textBox, segmentEnum, cb1, cb2,formLb);
						
					}else if(type.equalsIgnoreCase("number") ) {
						SegmentEnum ltyEnum =null;
						if(segmentEnum.getParentEnum().name().equalsIgnoreCase(SegmentEnum.NUMBER_OF_TRANSACTIONS.name())){
							ltyEnum=segmentEnum.getChidrenByParentEnum(segmentEnum).get(0);
							segmentEnum=ltyEnum;
						}else if(segmentEnum.getParentEnum().name().equalsIgnoreCase(SegmentEnum.PURCHASE_LOYALTY_BALANCE.name()) ||
								segmentEnum.getParentEnum().name().equalsIgnoreCase(SegmentEnum.PURCHASE_LOYALTY_TOTAL_ISSUED_IS.name()) ||
								segmentEnum.getParentEnum().name().equalsIgnoreCase(SegmentEnum.PURCHASE_LOYALTY_BALANCE_IS.name())) {
							ltyEnum=segmentEnum.getChidrenByParentEnum(segmentEnum).get(0);
							/*ltyEnum=(operatorlb.getSelectedItem().getValue()!=null ? operatorlb.getSelectedItem().getValue() :
								(segmentEnum.getChidrenByParentEnum(segmentEnum)).get(0));*/
							ltyEnum=segmentEnum.getChidrenByParentEnum(segmentEnum).get(0);
							List<SegmentEnum> ltySubRuleEnum = segmentEnum.getChidrenByParentEnum(ltyEnum.getParentEnum());
							Components.removeAllChildren(operatorlb);
							for (SegmentEnum ltySubEnum : ltySubRuleEnum) {
								Listitem li = new Listitem(ltySubEnum.getDispLabel(),ltySubEnum);
								li.setParent(operatorlb);
								li.addEventListener("onSelect", this);
								operatorlb.setSelectedIndex(0);
						     }
							segmentEnum=ltyEnum;
						}
						createValuesForOperatorTypeNumber(dblBox1, dblBox2, textBox, segmentEnum, cb1,cb2);
						
					}
					
				}
				
				
				
			}
			
		});
		
		
		
		
		return testDiv;
		
	}
	
	
	
	public Div createNewRuleDiv(Listitem item) {
		
		int index = item.getIndex();
	
		//String srcCategory = ((Listbox)item.getParent()).getId();
		
		SegmentEnum segmentEnum = (SegmentEnum)item.getValue();
		
		/*if(srcCategory.equals("purchaseAttrLbId")) {
			
			segmentEnum = getSegmentEnumByCode(index, 2);
			
			
			
			
		} else if(srcCategory.equals("profileAttrLbId")) {
			
			
			//****************************************************
			//getEnum by dragged item's index
			segmentEnum = getSegmentEnumByCode(index, 1); 
			
			
			
			
		}else if(srcCategory.equals("interactionAttrLbId")) {
			
			segmentEnum = getSegmentEnumByCode(index, 3);
		}*/
		
			
			//item.setDraggable(item.getLabel().equals(SegmentEnum.INTERACTION_RECEIVED.getItem()) ? "IntVdivSegment" : null);
			
			//make received rule can be draggable only to this v div.
		/*boolean isSameItem = false;
			for (Listitem receviedRuleItem : interactionAttrLbId.getItems()) {
				
				isSameItem = item.getLabel().equals(receviedRuleItem.getLabel());
				if(receviedRuleItem.getAttribute("isDragged") == null) {
					
					if(isSameItem) {
						
						item.setAttribute("isDragged", "Done");
						receviedRuleItem.setDraggable(null);
					}
					
				if(!isSameItem && receviedRuleItem.getLabel().equals(SegmentEnum.INTERACTION_RECEIVED.getItem()) ) {
					
					receviedRuleItem.setDraggable("IntSegment");
					
					
				}else{
					
					if(!isSameItem)receviedRuleItem.setDraggable("IntSegment");
					
				}
				
				
			}//
			else{
					
					if(receviedRuleItem.getLabel().equals(SegmentEnum.INTERACTION_RECEIVED.getItem())) {
						
						receviedRuleItem.setDraggable("IntSegment");
						
					}
					else{
						
						receviedRuleItem.setDraggable(null);
					}
					
			}
			//intAttributeDivId.setDroppable(null);
			
		}//if
*/		
		return getCreatedCompDiv(segmentEnum, item);
		
		
	}
	
	public void populateValuesForOperatorForTypeDate(Datebox db1, Datebox db2, Listbox numberLb,String value1,String value2,
			Decimalbox dblBox1, Decimalbox dblBox2, SegmentEnum operatorEnum) {
		
		
		if(numberLb.isVisible() && operatorEnum != null) {
			
			int value = Integer.parseInt(value1)/Integer.parseInt(operatorEnum.getType2());
			for (Listitem numberItem : numberLb.getItems()) {
				
				if(numberItem.getLabel().equals(value+Constants.STRING_NILL)) {
					
					numberItem.setSelected(true);
					break;
				}
				
			}//for
				
			return;	
		}//if numberLb visible
		
		if(db1.isVisible()) {
			
			try {
				Date dateValue1 = dateFormat.parse(value1);
				db1.setValue(dateValue1);
				
				if(db2.isVisible()) {
					
					Date dateValue2 = dateFormat.parse(value2);
					db2.setValue(dateValue2);
					
				}//if
				                     
				
				return;
			} catch (ParseException e) {
				logger.error("Exception while parsing the date value",e);
			}
			
			
		}
		
		if(dblBox1.isVisible()) {
			
			try {
				BigDecimal daysDecimalValue1 = new BigDecimal(value1);
				dblBox1.setValue(daysDecimalValue1);
				
				if(dblBox2.isVisible()) {
					
					BigDecimal daysDecimalValue2 = new BigDecimal(value2);
					dblBox2.setValue(daysDecimalValue2);
					
					
				}//if
				return;
			} catch (WrongValueException e) {
				logger.error("Exception while parsing the decimal value",e);
			}
			
		}
		
		
	}
	
	public void populateValuesForOperatorForTypeString(Textbox textbox, Combobox cb1, Combobox cb2, Listbox formLb, String value1, String value2) {
		
		
		if(textbox.isVisible()) {
			
			textbox.setText(value1);
			return;
			
		}
		
		
		if(cb1.isVisible()) {
			
			cb1.setValue(value1);
			if(cb2.isVisible()) {
				cb2.setValue(value2);
				
			}
			
			return;
			
		}
		
		if(formLb.isVisible()){
			
			value1 = value1.substring(value1.lastIndexOf(Constants.DELIMETER_COLON)+1);
			for (Listitem formItem : formLb.getItems()) {
				
				if(value1.equalsIgnoreCase(formItem.getLabel())) {
					
					formItem.setSelected(true);
					break;
				}
				
			}//for
			
			return;
		}
		
	}//populateValuesForOperatorForTypeString()
	
	public void populateValuesForOperatorForTypeNumber(Decimalbox  dblBox1, Decimalbox  dblBox2, 
			Textbox textbox,  Combobox cb1, Combobox cb2,  String value1, String value2)  {
		
		if(textbox.isVisible()) {
			
			textbox.setText(value1);
			return;
			
		}//if
		
		if(cb1.isVisible()) {
			
			cb1.setValue(value1);
			if(cb2.isVisible()) {
				cb2.setValue(value2);
				
			}
			
			return;
			
		}
		
		if(dblBox1.isVisible()) {
			
			try {
				BigDecimal daysDecimalValue1 = new BigDecimal(value1);
				dblBox1.setValue(daysDecimalValue1);
				
				if(dblBox2.isVisible()) {
					
					BigDecimal daysDecimalValue2 = new BigDecimal(value2);
					dblBox2.setValue(daysDecimalValue2);
					
					
				}//if
				
				
			} catch (WrongValueException e) {
				logger.error("Exception while parsing the decimal value",e);
			}
			
		}
		
		/*if(campLb.isVisible()) {
			
			for (Listitem campItem : campLb.getItems()) {
				
				if(((Campaigns)campItem.getValue()).getCampaignId().toString().equals(campIdVal)) {
					
					campItem.setSelected(true);
					break;
				}
				
			}
			
		}*/
	}//populateValuesForOperatorForTypeNumber()
	
	
	public void createValuesForOperatorTypeSting(Textbox textbox, SegmentEnum segmentEnum, Combobox cb1, Combobox cb2, Listbox formLb) {
		
		if(segmentEnum == null){
			logger.debug("segment enum is null...."); 
			return;
		}
		String val1 = segmentEnum.getType1();
		String val2= segmentEnum.getType2();
		logger.debug("val1 ::" +val1);
		logger.debug("val2 ::" +val2);
		if(val1 == null && val2 == null) {
			
			textbox.setVisible(false);
			cb1.setVisible(false);
			cb2.setVisible(false);
			
			String colVal = segmentEnum.getColumnValue(); 
			if(colVal != null && colVal.equals(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM)) {
				
				formLb.setVisible(formLb.getItemCount() > 0);
				
			}//if
			else{
				formLb.setVisible(false);
			}
			
			
			
		}else if(val1 != null && val2 == null) {
			
			if(cb1.getItemCount() > 0) {
				
				cb1.setVisible(true);
				textbox.setVisible(false);
				if(segmentEnum.getDispLabel().equals("One of") || segmentEnum.getDispLabel().equals("Only like")){
					cb1.setTooltiptext("You can enter multiple values as comma (',') separated values.");
				}else{
					cb1.setTooltiptext("");
				}
				
			}
			else {
				
				cb1.setVisible(false);
				cb1.setVisible(false);
				if(segmentEnum.getDispLabel().equals("One of") || segmentEnum.getDispLabel().equals("Only like")){
					textbox.setTooltiptext("You can enter multiple values as comma (',') separated values.");
				}else{
					textbox.setTooltiptext("");
				}
				textbox.setVisible(true);
				
			}//else
			
		}//else if
		
	}
	
	public void createValuesForOperatorTypeNumber(Decimalbox  dblBox1, Decimalbox  dblBox2,Textbox textbox, SegmentEnum segmentEnum, Combobox cb1, Combobox cb2) {
		
		if(segmentEnum == null){
			logger.debug("segment enum is null...."); 
			return;
		}
		String val1 = segmentEnum.getType1();
		String val2= segmentEnum.getType2();
		String token = segmentEnum.getToken();
		
		logger.debug("val2 ::" +val2);
		if(val1 == null && val2 == null) {
			
			dblBox1.setVisible(false);
			dblBox2.setVisible(false);
			textbox.setVisible(false);
			cb1.setVisible(false);
			cb2.setVisible(false);
			
		}else if(val1 != null && val2 == null) {
			
			if(val1.equalsIgnoreCase("text") ) {
				
				if(cb1.getItemCount() > 0) {
					
					cb1.setVisible(true);
					textbox.setVisible(false);
					cb2.setVisible(false);
					dblBox1.setVisible(false);
					dblBox2.setVisible(false);
					if(segmentEnum.getDispLabel().equals("One of")){
						cb1.setTooltiptext("You can enter multiple Number type values as comma (',') separated values.");
					}else{
						cb1.setTooltiptext("Enter Only Number type values.");
					}
					
					
				}else {
					
					if(segmentEnum.getDispLabel().equals("One of")){
						textbox.setTooltiptext("You can enter multiple Number type values as comma (',') separated values.");
					}else{
						textbox.setTooltiptext("Enter Only Number type values.");
					}
					textbox.setVisible(true);
					dblBox1.setVisible(false);
					dblBox2.setVisible(false);
					cb1.setVisible(false);
					cb2.setVisible(false);
				}
				
			}//if
			else {
				if(cb1.getItemCount() > 0) {
					
					cb1.setVisible(true);
					cb2.setVisible(false);
					textbox.setVisible(false);
					dblBox1.setVisible(false);
					dblBox2.setVisible(false);
					
					
				}
				else{
				
					dblBox1.setVisible(true);
					dblBox2.setVisible(false);
					textbox.setVisible(false);
					cb1.setVisible(false);
					cb2.setVisible(false);
				}
			}
			
		}else if(val1 != null && val2 != null) {
			if(cb1.getItemCount() > 0) {
				
				cb1.setVisible(true);
				cb2.setVisible(true);
				cb2.setTooltiptext("Value should be greater than the first value.");
				textbox.setVisible(false);
				dblBox1.setVisible(false);
				dblBox2.setVisible(false);
				
				
			}else {
				
				dblBox1.setVisible(true);
				dblBox2.setVisible(true);
				dblBox2.setTooltiptext("Value should be greater than the first value.");
				textbox.setVisible(false);
				cb1.setVisible(false);
				cb2.setVisible(false);
				
			}
			
		}
		
		
	}
	
	public void createValuesForOperatorForTypeDate(Datebox db1, Datebox db2, Listbox numberLb, SegmentEnum segmentEnum,
			Decimalbox dblBox1, Decimalbox dblBox2) {
		
		
		if(segmentEnum == null){
			logger.debug("segment enum is null...."); 
			return;
		}
		String val1 = segmentEnum.getType1();
		String val2= segmentEnum.getType2();
		logger.debug("val2 ::" +val2);
			
			if(val1 == null && val2 == null) {
				
				db1.setVisible(false);
				db2.setVisible(false);
				numberLb.setVisible(false);
				dblBox1.setVisible(false);
				dblBox2.setVisible(false);
				//txtBox.setVisible(false);
				
			}else if(val1 != null && val2 != null) {
				
				if(!val1.equalsIgnoreCase("date")) {// data type is date but type is:within last-range of days
					
					if(val1.equalsIgnoreCase("number")) {
						
						dblBox1.setVisible(true);
						dblBox2.setVisible(true);
						dblBox2.setTooltiptext("Value should be greater than the first value.");
						numberLb.setVisible(false);
						
						
					}else{
						Components.removeAllChildren(numberLb);
						int num = Integer.parseInt(val1);
						for(int i=1; i<=num ; i++) {
							
							Listitem li = new Listitem(""+i, val2);
							li.setParent(numberLb);
							
						}
						
						numberLb.setSelectedIndex(0);
						numberLb.setVisible(true);
						dblBox1.setVisible(false);
						dblBox2.setVisible(false);
					}
					db1.setVisible(false);
					db2.setVisible(false);
					
					
					
				}else{
					db1.setVisible(true);
					db2.setVisible(true);
					db2.setTooltiptext("Start date should be less than end date");
					dblBox1.setVisible(false);
					dblBox2.setVisible(false);
					numberLb.setVisible(false);
				}
				
			}else if(val1 != null && val2 == null) {
				
				if(val1.equalsIgnoreCase("date")) {
				
				db1.setVisible(true);
				db2.setVisible(false);
				numberLb.setVisible(false);
				dblBox1.setVisible(false);
				dblBox2.setVisible(false);
				//txtBox.setVisible(false);
				
				}
			}
			
		
		
		
		
	}
	
	/**
	 * To cretae and return the operator listbox based on the selected rule in rulelb
	 * @param ruleEnumArr
	 * @return
	 */
	public void creteOperatorOptionsForRule(SegmentEnum ruleEnum, Listbox operatorLb,Listbox rulelb) {
		
		Listitem li =null;
		
		List<SegmentEnum> childEnumList = ruleEnum.getChidrenByParentEnum(ruleEnum); 
		
		if(childEnumList.size() == 0){
			
			if(ruleEnum.getParentEnum().getItem().trim().equalsIgnoreCase(SegmentEnum.PURCHASE_LOYALTY_TIER.getDispLabel()) && programsList != null) {
					li  = new Listitem("Select Tier");
					li.setSelected(true);
					li.setParent(operatorLb);
				for (LoyaltyProgram prgmObj : programsList) {
					List<LoyaltyProgramTier> tierList = loyaltyProgramTierDao.getTierListByPrgmId(prgmObj.getProgramId());
					
					if(tierList != null && tierList.size() > 0) {
						for (LoyaltyProgramTier tierObj : tierList) {
							li = new Listitem(prgmObj.getProgramName()+" : "+tierObj.getTierType(),tierObj.getTierId());
							li.setParent(operatorLb);
						}
					}
					
				}
				
			}//if
			else {
				operatorLb.setVisible(false);
				
				return;
			}
		}
		
		for (SegmentEnum childEnum : childEnumList) {
			
			logger.info(rulelb!=null ? rulelb.getSelectedItem() : "");
			if(childEnum.name().equalsIgnoreCase(SegmentEnum.VALUE_CODES_TOTAL.name())||
					childEnum.name().equalsIgnoreCase(SegmentEnum.VALUE_CODES_TOTAL_BALANCE.name())) continue;
				li = new Listitem(childEnum.getDispLabel(), childEnum);
				if(childEnum.getType().equalsIgnoreCase("date") && childEnum.getToken().contains("_between")){
					
					li.setTooltiptext("Year will be ignored.");
				}
				li.setParent(operatorLb);
				li.addEventListener("onSelect", this);
				
			
		}//for
		for (SegmentEnum childEnum : childEnumList) {
			
			if((childEnum.name().equalsIgnoreCase(SegmentEnum.VALUE_CODES_TOTAL.name())||
					childEnum.name().equalsIgnoreCase(SegmentEnum.VALUE_CODES_TOTAL_BALANCE.name())) && valueCodeList !=null){
				for (ValueCodes vc : valueCodeList) {
					li = new Listitem(vc.getValuCode(), childEnum);
					li.setParent(operatorLb);
					li.addEventListener("onSelect", this);
				}
			}
			
		}
		/*if(ruleEnum.getParentEnum().getDispLabel().trim().equalsIgnoreCase(SegmentEnum.PURCHASE_LOYALTY_BALANCE.getDispLabel()) && valueCodeList != null) {
			for (ValueCodes vc : valueCodeList) {
				li = new Listitem(vc.getValuCode(), ruleEnum);
				li.setParent(operatorLb);
				li.addEventListener("onSelect", this);
			}
		}*/
		
		operatorLb.setVisible(true);
		
		if(operatorLb.getItemCount() > 0)
		operatorLb.setSelectedIndex(0);
	}//creteOperatorOptionsForRule()
	
	
	
	
	
	
	
	
	
	
	
	
	private Button segmentBtnId,updateBtnId;
	//private Groupbox addNewSegRuleGbId;
	/*public void onClick$addRuleBtnId() {
		
		
		
		//addNewSegRuleGbId.setVisible(true);
		showSegmentRulePanel(true, segmentRules);
		
		
		
		
	}*/
	
	public void onClick$updateBtnId() {
		
		saveRule(true);
	}
	
	
	public boolean saveSegmentSettings() {
		
		
		
		
		
		//this is only for POS LIST
		 if(!validate()){
			
			 return false;
		}
		if(segmentRule == null){
			
			logger.error("segment rule is null.....returning...");
			return false;
			
		}
		
		
		
	/*	logger.debug("--- creating the new segment rule---");
		
		if(index ==1) {
			String segmentQueryStr = QueryGenerator.generateListSegmentQuery( segmentRuleStr, true);
			
				if(segmentQueryStr == null) {
					MessageUtil.setMessage("Selected invalid segmentation rules.", "color:red", "TOP");
					return false;
				}
				
			if(segmentRules == null) {
				
			 segmentRules = new SegmentRules(segmentRuleNameTbId.getValue(), segRuleDescpTxtBoxId.getValue(), 
					(MailingList)dispMlLBoxId.getSelectedItem().getValue(), segmentRuleStr,segmentQueryStr, "", user.getUserId());
			}
			else{
				
				segmentRules.setDescription(segRuleDescpTxtBoxId.getValue());
				segmentRules.setMailingList((MailingList)dispMlLBoxId.getSelectedItem().getValue());
				segmentRules.setSegRule(segmentRuleStr);
				segmentRules.setSegQuery(segmentQueryStr);
			}
			
			segmentRulesDao.saveOrUpdate(segmentRules);
			Include xcontents = (Include)Sessions.getCurrent().getAttribute("xcontents");
			xcontents.setSrc("/zul/" + PageListEnum.EMPTY + ".zul");
			xcontents.setSrc("/zul/" + PageListEnum.CONTACT_MANAGE_SEGMENTS + ".zul");
			
			
		}*/
		
		return true;
	}
	
	public boolean targetDivHasChildren(Div targetDiv) {
		List<Component> childComponentList = targetDiv.getChildren();
		if(childComponentList.size() == 0 || (childComponentList.size() == 1 && 
				childComponentList.get(0) instanceof Div )) {
			
			Div orANDDiv = (Div)childComponentList.get(0);
			if(orANDDiv.getSclass() != null && orANDDiv.getSclass().equals("drop_and_div") ) return false;
			else return true;
			
		}
		else return true;
		
		
	}
	
	
	//boolean couponNameflag=false;
	public void saveRule(boolean isEdit) {
		

		
		
		String operator = "";
		boolean isCampaignsExists = false;
		String campaignIds = Constants.STRING_NILL;
		String crIds = Constants.STRING_NILL;
		List<Component> childVboxList = new ArrayList<Component>();
		
		if(targetDivHasChildren(profileAttributeDivId))childVboxList.addAll(profileAttributeDivId.getChildren());
		if(targetDivHasChildren(purIntAttributeDivId))childVboxList.addAll(purIntAttributeDivId.getChildren());
		if(targetDivHasChildren(intAttributeDivId)){
			
			if(dispcampaignsLbId.getSelectedCount() == 0) {
				
				MessageUtil.setMessage("Please select the campaign(s) for interaction rules.", "color:red;");
				return;
			}
			isCampaignsExists = true;
			Set<Listitem> items = dispcampaignsLbId.getSelectedItems();
			
			for (Listitem listitem : items) {
				
				Long selCampId = listitem.getValue();
				Long selCrId = (Long)listitem.getAttribute("cr_id");
				
				if(!campaignIds.isEmpty()) campaignIds += Constants.DELIMETER_COMMA;
				
				campaignIds += selCampId.longValue();
				
				if(latestCRChkBoxId.isChecked()) {
					if(!crIds.isEmpty()) crIds += Constants.DELIMETER_COMMA;
					
					crIds += selCrId.longValue();
					
				}
				
			}//for
			
			
			childVboxList.addAll(intAttributeDivId.getChildren());
		}
		
		if(childVboxList.size() == 0) {//only the dashed areas r left
			
			MessageUtil.setMessage("No segmentation rule provided. Please add at least one rule.", "color:red;");
			return;
			
		}
		
		/*List<Component> childVboxList = profileAttributeDivId.getChildren();
		
		childVboxList.addAll(purIntAttributeDivId.getChildren());*/
		Div chilVDiv = null;
		
		StringBuffer segmentRuleSB = new StringBuffer();//"all:"+((MailingList)dispMlLBoxId.getSelectedItem().getValue()).getListId());
		String ruleStr = null;
		boolean allValid=true;
		
		for (Object obj : childVboxList) {
			
			if(obj instanceof Div) {
				
				boolean isValid = true;
				for(Object object : ((Div)obj).getChildren()) {
					
					if(isValid==false) allValid=false;
					
					//Reset
					isValid = true;
					
					if(object instanceof Div) {
						
						
						chilVDiv = (Div)object;
						if(chilVDiv.getSclass().contains("drop_")) {
							continue;
						}
						
						ruleStr = prepareSegmentRule(chilVDiv);
						if(ruleStr == null) {
							isValid = false;
						}
						/*else {
							chilVDiv.setStyle("padding:10px;border:none");
							
						}*/
						
						//if(childVboxList.size() > 1)  operator = "<AND>";
						
						if(ruleStr != null && ruleStr.trim().length()>0) {					
							segmentRuleSB.append("||"+ruleStr+operator);
						}
						if(ruleStr != null && chilVDiv.getAttribute("columnName")!=null && chilVDiv.getAttribute("replaceColumnName")!=null ) {
							ruleStr = ruleStr.replace((String)chilVDiv.getAttribute("replaceColumnName"), (String)chilVDiv.getAttribute("columnName"));
							segmentRuleSB.append("||"+ruleStr);
						}
						
						
					}//ruleDiv
					
				}//tempDiv added for And
				
				
				if(isValid==false) allValid=false;
				
			}//if obj
			
		}//for each vb
		if(!allValid) {
			logger.warn(" >>>>>>>>>>>>> Segmentation validation failed ");
			
			MessageUtil.setMessage("Please provide proper value(s) for highlighted rule(s).", "color:red", "TOP");
			return ;
		}
		
		
		if(segmentRuleSB.toString().trim().length()==0) {
			
			MessageUtil.setMessage("There are no valid rules to prepare segment (as of now, derivative filter rules are ignored).", "color:red","TOP");
			return;
		}
		
		//segmentRuleSB.insert(0, "all:"+((MailingList)dispMlLBoxId.getSelectedItem().getValue()).getListId());
		//segmentRuleSB.insert(0, "all:"+currentUser.getUserId());
		
		/*StringBuffer segmentRuleSBToSend = new StringBuffer();
		
		
		segmentRuleSBToSend.insert(0, "all:"+currentUser.getUserId().longValue()+
				(isCampaignsExists ? (Constants.DELIMETER_COLON + campaignIds 
						+ (latestCRChkBoxId.isChecked() ? (Constants.DELIMETER_COLON + crIds) : "")) : ""));
		*/
		segmentRuleSB.insert(0, "all:"+currentUser.getUserId().longValue()+(isCampaignsExists ? (Constants.DELIMETER_COLON + campaignIds) 
				+ (latestCRChkBoxId.isChecked() ? (Constants.DELIMETER_COLON + Constants.INTERACTION_CAMPAIGN_CRID_PH) : "") : ""));
	
		
		
		logger.debug("segmentRuleSB ::"+segmentRuleSB.toString());
		
			long mlsbit = Utility.getMlsBit(mlSet);
			String totSegmentQueryStr = "";
			if(!currentUser.isEnableClickHouseDBFlag())
				totSegmentQueryStr = SalesQueryGenerator.generateListSegmentQuery( segmentRuleSB.toString(), true, Constants.SEGMENT_ON_EXTERNALID, mlsbit);
			else
				totSegmentQueryStr = ClickHouseSalesQueryGenerator.generateListSegmentQuery( segmentRuleSB.toString(), true, Constants.SEGMENT_ON_EXTERNALID, mlsbit);

			if(totSegmentQueryStr == null) {
				MessageUtil.setMessage("Selected invalid segmentation rule.", "color:red", "TOP");
				return ;
			}
			logger.debug("segmentQueryStr :: "+totSegmentQueryStr);
			
			totSegmentQueryStr = totSegmentQueryStr.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH,(latestCRChkBoxId.isChecked() ? ("AND cr_id in("+crIds+")") :"") );
			//ClickHouse changes
			String totSegmentCountQry = "";
			if(!currentUser.isEnableClickHouseDBFlag())
				totSegmentCountQry = SalesQueryGenerator.generateListSegmentCountQuery( segmentRuleSB.toString(), true, Constants.SEGMENT_ON_EXTERNALID, mlsbit);
			else
				totSegmentCountQry = ClickHouseSalesQueryGenerator.generateListSegmentCountQuery( segmentRuleSB.toString(), true, Constants.SEGMENT_ON_EXTERNALID, mlsbit);

			if(totSegmentCountQry == null) {
				MessageUtil.setMessage("Selected invalid segmentation rule.", "color:red", "TOP");
				return ;
			}
			totSegmentCountQry = totSegmentCountQry.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH,(latestCRChkBoxId.isChecked() ? ("AND cr_id in("+crIds+")") :"") );
			
			if(totSegmentCountQry.contains("<Reward>")) {
				totSegmentCountQry = totSegmentCountQry.replace("<Reward>",selectedValueCode);
			}
			logger.debug("segmentQueryStr :: "+totSegmentCountQry);
				
			
			//String emailSegmentQueryStr = SalesQueryGenerator.generateListSegmentQuery( segmentRuleSB.toString(), true, Constants.SEGMENT_ON_EMAIL, mlsbit);
			String emailSegmentQueryStr = "";
			if(!currentUser.isEnableClickHouseDBFlag())
				emailSegmentQueryStr = SalesQueryGenerator.generateListSegmentQuery( segmentRuleSB.toString(), false, Constants.SEGMENT_ON_EMAIL, mlsbit);
			else
				emailSegmentQueryStr = ClickHouseSalesQueryGenerator.generateListSegmentQuery( segmentRuleSB.toString(), false, Constants.SEGMENT_ON_EMAIL, mlsbit);
			
			if(emailSegmentQueryStr == null) {
				MessageUtil.setMessage("Selected invalid segmentation rule.", "color:red", "TOP");
				return ;
			}
			emailSegmentQueryStr = emailSegmentQueryStr.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH,(latestCRChkBoxId.isChecked() ? ("AND cr_id in("+crIds+")") :"") );
			logger.debug("segmentQueryStr :: "+emailSegmentQueryStr);
			
			//String emailSegmentCountQueryStr = SalesQueryGenerator.generateListSegmentCountQuery( segmentRuleSB.toString(), true, Constants.SEGMENT_ON_EMAIL, mlsbit);
			//ClickHouse changes
			String emailSegmentCountQueryStr = "";
			if(!currentUser.isEnableClickHouseDBFlag())
				emailSegmentCountQueryStr = SalesQueryGenerator.generateListSegmentCountQuery( segmentRuleSB.toString(), false, Constants.SEGMENT_ON_EMAIL, mlsbit);
			else
				emailSegmentCountQueryStr = ClickHouseSalesQueryGenerator.generateListSegmentCountQuery( segmentRuleSB.toString(), false, Constants.SEGMENT_ON_EMAIL, mlsbit);
			
			if(emailSegmentCountQueryStr == null) {
				
				MessageUtil.setMessage("Selected invalid segmentation rule.", "color:red", "TOP");
				return ;
			}
			emailSegmentCountQueryStr = emailSegmentCountQueryStr.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH,(latestCRChkBoxId.isChecked() ? ("AND cr_id in("+crIds+")") :"") );
			
			if(emailSegmentCountQueryStr.contains("<Reward>")) {
				emailSegmentCountQueryStr = emailSegmentCountQueryStr.replace("<Reward>",selectedValueCode);
			}
			logger.debug("segmentQueryStr :: "+emailSegmentCountQueryStr);
			
			
			
			
			//String mobileSegmentQueryStr = SalesQueryGenerator.generateListSegmentQuery( segmentRuleSB.toString(), true, Constants.SEGMENT_ON_MOBILE, mlsbit);
			//ClickHouse changes
			String mobileSegmentQueryStr = "";
			if(!currentUser.isEnableClickHouseDBFlag())
				mobileSegmentQueryStr = SalesQueryGenerator.generateListSegmentQuery( segmentRuleSB.toString(), false, Constants.SEGMENT_ON_MOBILE, mlsbit);
			else
				mobileSegmentQueryStr = ClickHouseSalesQueryGenerator.generateListSegmentQuery( segmentRuleSB.toString(), false, Constants.SEGMENT_ON_MOBILE, mlsbit);
			
			if(mobileSegmentQueryStr == null) {
				MessageUtil.setMessage("Selected invalid segmentation rule.", "color:red", "TOP");
				return ;
			}
			mobileSegmentQueryStr = mobileSegmentQueryStr.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH,(latestCRChkBoxId.isChecked() ? ("AND cr_id in("+crIds+")") :"") );
			mobileSegmentQueryStr = mobileSegmentQueryStr.replace("<MOBILEOPTIN>","");
			logger.debug("segmentQueryStr :: "+mobileSegmentQueryStr);
				
			
			//String mobileSegmentCountQueryStr = SalesQueryGenerator.generateListSegmentCountQuery( segmentRuleSB.toString(), true, Constants.SEGMENT_ON_MOBILE, mlsbit);
			//ClickHouse changes
			String mobileSegmentCountQueryStr = "";
			if(!currentUser.isEnableClickHouseDBFlag())
				mobileSegmentCountQueryStr = SalesQueryGenerator.generateListSegmentCountQuery( segmentRuleSB.toString(), false, Constants.SEGMENT_ON_MOBILE, mlsbit);
			else
				mobileSegmentCountQueryStr = ClickHouseSalesQueryGenerator.generateListSegmentCountQuery( segmentRuleSB.toString(), false, Constants.SEGMENT_ON_MOBILE, mlsbit);
			
			if(mobileSegmentCountQueryStr == null) {
				MessageUtil.setMessage("Selected invalid segmentation rule.", "color:red", "TOP");
				return ;
			}
			
			mobileSegmentCountQueryStr = mobileSegmentCountQueryStr.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH,(latestCRChkBoxId.isChecked() ? ("AND cr_id in("+crIds+")") :"") );
			mobileSegmentCountQueryStr = mobileSegmentCountQueryStr.replace("<MOBILEOPTIN>","");
			
			if(mobileSegmentCountQueryStr.contains("<Reward>")) {
				mobileSegmentCountQueryStr = mobileSegmentCountQueryStr.replace("<Reward>",selectedValueCode);
			}
			
			logger.debug("segmentQueryStr :: "+mobileSegmentCountQueryStr);
			
			
			try {
				int confirm = Messagebox.show("Are you sure you want to " +(isEdit ? "update" : "create") 
								+ " segment?", (isEdit ? "Update" : "Create") +" Segment",
						 		Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				if(confirm != 1){
					return ;
				}
				
			} catch (Exception e) {
				logger.error("Exception ::", e);
				return;
			}	
			int size = 0;
			int totSize = 0;
			int totMobileCount = 0;
			if(!currentUser.isEnableClickHouseDBFlag()) {				
					size = contactsDao.getSegmentedContactsCount(emailSegmentCountQueryStr);
					totSize = contactsDao.getSegmentedContactsCount(totSegmentCountQry);
					totMobileCount = contactsDao.getSegmentedContactsCount(mobileSegmentCountQueryStr);
			}else {
				size = contactsDao.getSegmentedContactsCountFromCH(emailSegmentCountQueryStr);
				totSize = contactsDao.getSegmentedContactsCountFromCH(totSegmentCountQry);
				totMobileCount = contactsDao.getSegmentedContactsCountFromCH(mobileSegmentCountQueryStr);
			}
			
			String segRuleToView = dispRule(segmentRuleSB.toString());
			boolean isNew = false;
				
			if(segmentRule == null) {
				
			 segmentRule = new SegmentRules(segmentRuleNameTbId.getValue(), segRuleDescpTxtBoxId.getValue(), 
					 	((MailingList)dispMlLBoxId.getSelectedItem().getValue()).getListId(), segmentRuleSB.toString(),
					 	emailSegmentQueryStr, totSegmentQueryStr , mobileSegmentQueryStr ,  segRuleToView,
					 	user.getUserId(), Calendar.getInstance(), Calendar.getInstance(), 0,0, 0, listIdsStr);
			
			 isNew = true;
			
			}
			else{
				
				segmentRule.setDescription(segRuleDescpTxtBoxId.getValue());
				segmentRule.setListId(((MailingList)dispMlLBoxId.getSelectedItem().getValue()).getListId());
				segmentRule.setSegRule(segmentRuleSB.toString());
				segmentRule.setSegRuleToView(segRuleToView);
				segmentRule.setEmailSegQuery(emailSegmentQueryStr);
				segmentRule.setTotSegQuery(totSegmentQueryStr);
				segmentRule.setMobileSegQuery(mobileSegmentQueryStr);
				segmentRule.setSegmentMlistIdsStr(listIdsStr);
			}
			
			segmentRule.setLastRefreshedOn(Calendar.getInstance());
			segmentRule.setSegmentType(Constants.SEGMENT_TYPE_CONTACT);
			segmentRule.setSize(size);
			segmentRule.setTotSize(totSize);
			segmentRule.setTotMobileSize(totMobileCount);
			/*if(latestCRChkBoxId.isChecked())  {
				
				segmentRule.setLatestCampaigns((byte)1);
				
			}else {
				
				segmentRule.setLatestCampaigns((byte)0);

			}*/
			logger.info("Segment Rule "+segmentRule);
		    segmentRulesDaoForDML.saveOrUpdate(segmentRule);
			
			if(isNew) {
				
				
				segmentIdsSet.add(segmentRule.getSegRuleId());
				session.setAttribute(Constants.SEGMENTIDS_SET, segmentIdsSet);
				
				
			}
			
			
			MessageUtil.setMessage("Segment rule "+(isEdit ? "updated" : "saved") +" successfully.", "color:green;");
			
			Redirect.goTo(PageListEnum.CONTACT_VIEW_SEGMENTS);
			
		
	}
	
	
	
	public void onClick$segmentBtnId() {
		
		saveRule(false);
	}
	
	
	
	public String dispRule(String rule) {
		String dispRule = "";
		String option=null;
		String campaignId = null;
		String campName = "";
		if(rule != null) {
			
			
			String[] rowsArr = rule.split("\\|\\|");
			String[] columnsArr; 
			
			
			columnsArr = rowsArr[0].split(":");
			if(columnsArr.length > 0) {
				
				if(columnsArr[0].trim().equalsIgnoreCase("Any") ) { 
					option = "OR";
				} 
				else {
					option = "AND";
				}
				
				if(columnsArr.length > 2) {
					
					campaignId = columnsArr[2];
					if(campaignId != null && !campaignId.isEmpty()) {
						
						List<Campaigns> campLst = campaignsDao.getCampaignById((campaignId));
						if(campLst != null) { 
						for (Campaigns campaigns : campLst) {
							
							if(!campName.isEmpty()) campName += ", ";
							campName +=  (campaigns != null ? campaigns.getCampaignName() : "");
						}
						}
					}
					
					//StringTokenizer tokenizerr = new StringTokenizer(columnsArr, Constants.DELIMETER_COMMA);
					//numOfCampaigns = tokenizerr.countTokens();
				}//if
				
			}//if
			
			
			String[] tempStrArr = null;
			String fieldNameStr = null;
			String itemStr = null;
			String dataTypeStr = null;
			String constraintStr = null;
			String data1 = null;
			String data2 = null;
			String data = "";
			String[] tokenArr = null;
			
			for(int i=1;i<rowsArr.length;i++) {
				
				tokenArr = rowsArr[i].split("<OR>");
				String innerRule = "";
				for (String token : tokenArr) {
					
					columnsArr = token.split("\\|");
					if(innerRule.length()>0) innerRule += " "+"OR"+" ";
					
					itemStr = columnsArr[0].trim();
					fieldNameStr = columnsArr[1].trim();
					tempStrArr = columnsArr[2].trim().split(":");
					dataTypeStr = tempStrArr[0].toUpperCase().trim();
					constraintStr = tempStrArr[1];
					
					data = data1 = (columnsArr.length>3)?columnsArr[3]:"";
					
					
					logger.debug("fieldNameStr :"+fieldNameStr);
					logger.debug("dataTypeStr :"+dataTypeStr);
					logger.debug("constraintStr :"+constraintStr);
					logger.debug("data1 :"+data1);
					logger.debug("data2 :"+data2);
					
					/*if(fieldNameStr.trim().startsWith("cs.")) {
						
						if((columnsArr.length>5)) {
							campaignId = columnsArr[5].trim().length()>0 ? columnsArr[5] : null;
							data2 = columnsArr[4].trim().length()>0 ? columnsArr[4] : "";;
						}
						
						else if(columnsArr.length==5 ){
							
							campaignId = columnsArr[4].trim().length()>0 ? columnsArr[4] : null;
							
						}
						
						
						if(campaignId != null) {
							
							List<Campaigns> campLst = campaignsDao.getCampaignById((campaignId));
							if(campLst == null) continue;
							
							for (Campaigns campaigns : campLst) {
								
								if(!campName.isEmpty()) campName += ", ";
								campName +=  (campaigns != null ? campaigns.getCampaignName() : "");
							}
							
						}
						
						
						if(data2 != null && campName.trim().length()>0){
							data = data1+" , "+data2+ " IN Campaign: "+campName;
						}else if(data2 == null && campName.trim().length()>0) {
							data = data1+ " IN Campaign: "+campName;
						}
						
					}*/
					if(itemStr.equalsIgnoreCase(SegmentEnum.INTERACTION_CLICKS.getItem()) 
							|| itemStr.equalsIgnoreCase(SegmentEnum.INTERACTION_OPENS.getItem()) ) {
						
						/*if((columnsArr.length>5)) {
							campaignId = columnsArr[5].trim().length()>0 ? columnsArr[5] : null;
							data2 = columnsArr[4].trim().length()>0 ? columnsArr[4] : "";;
						}
						
						else if(columnsArr.length==5 ){
							
							campaignId = columnsArr[4].trim().length()>0 ? columnsArr[4] : null;
							
						}*/
						
						SegmentEnum retEnum = SegmentEnum.getEnumByColumn(fieldNameStr);
						
						if(retEnum != null) {
							
							fieldNameStr = retEnum.getParentEnum().getDispLabel();
							
							constraintStr = retEnum.getDispLabel() +  " IN Campaign(s): "+campName;
							data = "";
						}//if
						
						
						
						/*if(data2 != null && campName.trim().length()>0){
							data = data1+" , "+data2+ " IN Campaign: "+campName;
						}else if(data2 == null && campName.trim().length()>0) {
							data = data1+ " IN Campaign: "+campName;
						}*/
						
					}
					else{
						
						data2 = (columnsArr.length>4)?columnsArr[4]:"";
						if(data2 != null ){
							data = data1+" , "+data2;
						}
					}
					
					
					innerRule += "("+fieldNameStr+" "+constraintStr+" "+data+")";
					
					
				}//for 
				
				
				if(dispRule.length()>0) dispRule += " "+option+" ";
				dispRule += "("+innerRule+")";
				
				
				
			} // outer for
			
			
		
		}
		
		
		
		return dispRule;
		
	}
	
	
	
	
	
	//SegmentRules segmentRule;
	private Label selListLblId;
	private Div addNewSegRuleDivId,segmentRulesDivId;
	private Listbox purchaseAttrLbId,profileAttrLbId,interactionAttrLbId;
	private String listIdsStr ;
	//private long mlsbit;
	private Set<MailingList> mlSet = null;
	public void onClick$proceedBtnId() {
		
		if(validate()){
			/*if(segmentRule == null) {
				segmentRule = new SegmentRules();
			}
			segmentRule.setSegRuleName(segmentRuleNameTbId.getValue());
			segmentRule.setDescription(segRuleDescpTxtBoxId.getValue());
			segmentRule.setMailingList((MailingList)dispMlLBoxId.getSelectedItem().getValue());
			segmentRule.setUserId(user.getUserId());
			
			segmentRulesDao.saveOrUpdate(segmentRule);*/
			//*******************get and display all the items from Segment Enum********************************************
			
			String listNames = "";
			Set<Listitem> lists = dispMlLBoxId.getSelectedItems();
			MailingList ml;
			mlSet = new HashSet<MailingList>();
			listIdsStr = "";
			for (Listitem li : lists) {

				ml = (MailingList) li.getValue();
				mlSet.add(ml);
				if(listNames.trim().length() > 0) listNames+=", ";
				listNames += ml.getListName();
				
				if(listIdsStr == null){
					
					listIdsStr = ml.getListId().longValue()+"";
					continue;
				}
				
				if(listIdsStr != null && listIdsStr.length() > 0) listIdsStr += ",";
				
				listIdsStr += ml.getListId().longValue()+"";
				
				
			}
			
			
			/*if(mlSet.size() > 0) {
				
				mlsbit = Utility.getMlsBit(mlSet);//need to get it from different method once we offer multiple user's list selection in segmenting
				
				
			}*/
			
			
			
			
			selListLblId.setValue(listNames);
			if(segmentRule != null) {
				//need not to get it from executing qry jsut get the avaible  last refreshed counts
				//totSize = contactsDao.getSegmentedContactsCount(segmentRule.getEmailSegQuery());
				numOfContactsLblId.setValue(segmentRule.getTotSize()+Constants.STRING_NILL);
				numOfEmailContactsLblId.setValue(segmentRule.getSize()+Constants.STRING_NILL);
				numOfMobileContactsLblId.setValue(segmentRule.getTotMobileSize()+Constants.STRING_NILL);
				
				deleteExistedRulesDiv(profileAttributeDivId);
				deleteExistedRulesDiv(purIntAttributeDivId);
				deleteExistedRulesDiv(intAttributeDivId);
				parseRules(segmentRule.getSegRule());
				
				updateBtnId.setVisible(true);
				segmentBtnId.setVisible(false);
				
			}
			
			
			
			addNewSegRuleDivId.setVisible(false);
			segmentRulesDivId.setVisible(true);
			
		}      
		
		
		
	}
	
	
	public void deleteExistedRulesDiv(Component parentComponent) {
		List<Component> list = parentComponent.getChildren();
		List<Component> deleteList = new ArrayList<Component>();
		for (Component component : list) {
			
			if(component instanceof Div) {
				
				Div deleteDiv = (Div)component;
				if( deleteDiv.getSclass() != null && deleteDiv.getSclass().startsWith("drop_") ) continue;
				else deleteList.add(deleteDiv);//deleteDiv.setParent(null);//profileAttributeDivId.removeChild(deleteDiv);
				
			}
			else if(component instanceof Label) deleteList.add(component);// component.setParent(null);//profileAttributeDivId.removeChild(component);
			
		}
		
		if(!deleteList.isEmpty()) {
			
			for (Component component : deleteList) {
				
				parentComponent.removeChild(component);
				
			}
			
			
		}
	}//deleteExistedRulesDiv
	
	/*private void createItemsForOperatorsLb(Listbox operatorsLb,String type, String constraint) {
		
		String[] operatorsArr = null;
		Components.removeAllChildren(operatorsLb);
		Listitem li;
		
		if(type == null) {
			type = ListSegmentRule.STRING;
		}
		
		if(type.equals(ListSegmentRule.NUMBER)) {
			for (int i = 0; i < numeric_operators.length; i++) {
				li = new Listitem(numeric_operators[i]);
				li.setParent(operatorsLb);
				
				if(i==0 && constraint==null) {
					logger.debug(type+":"+constraint+" is matched ");
					operatorsLb.setSelectedItem(li);
				}
				
				if(constraint!=null && constraint.equals(numeric_operators[i])) {
					logger.debug(type+":"+constraint+" is matched ");
					operatorsLb.setSelectedItem(li);
				}
			}
			return;
		}
		
		if(type.equals(ListSegmentRule.STRING)) {
			operatorsArr = str_operators;
		}
		else if(type.equals(ListSegmentRule.DATE)) {
			operatorsArr = date_operators;
		}else {
			operatorsArr = boolean_operators;
		}
		
		for (int i = 0; i < operatorsArr.length; i++) {
			li = new Listitem(operatorsArr[i]);
			li.setParent(operatorsLb);
			
			if(i==0 && constraint==null) {
				logger.debug(type+":"+constraint+" is matched ");
				operatorsLb.setSelectedItem(li);
			}
			
			if(constraint!=null && constraint.equals(operatorsArr[i])) {
				logger.debug(type+":"+constraint+" is matched ");
				operatorsLb.setSelectedItem(li);
			}
		}
	}
	*/
	
	
	
	
	
	
	/*int i=0;
	public Hbox addRules(String ruleStr) {
		
		logger.debug(" Rule :"+ruleStr+"   "+(i++));
		if( segmentCount == maxSegmentCount ) {
			return null;
		}
		
		String[] columnsArr = null;
		String[] tempStrArr = null;
		String fieldNameStr = null;
		String dataTypeStr = null;
		String constraintStr = null;
		String data1 = null;
		String data2 = null;
		if(ruleStr != null) {
			
			columnsArr = ruleStr.split("\\|");
			fieldNameStr = columnsArr[0].trim();
			tempStrArr = columnsArr[1].trim().split(":");
			dataTypeStr = tempStrArr[0].toUpperCase().trim();
			constraintStr = tempStrArr[1];
			data1 = columnsArr[2];
			data2 = (columnsArr.length>3)?columnsArr[5]:null;
			logger.debug("fieldNameStr :"+fieldNameStr);
			logger.debug("dataTypeStr :"+dataTypeStr);
			logger.debug("constraintStr :"+constraintStr);
			logger.debug("data1 :"+data1);
			logger.debug("data2 :"+data2);
		}
		
		Hbox ruleHb = new Hbox();
		ruleHb.setStyle("padding:6px 0");
		ruleHb.setPack("center");
		ruleHb.setAlign("center");
		final Listbox fieldsLb = new Listbox();
		fieldsLb.setWidth("120px");
		Listitem li;
		ListSegmentRule lstSegRule;
		
		
		for(int j=0;j<totalFieldsList.size();j++) {
			lstSegRule = (ListSegmentRule)totalFieldsList.get(j);
			li = new Listitem(lstSegRule.toString());
			li.setValue(lstSegRule);
			li.setParent(fieldsLb);
			if(fieldNameStr != null) {
				
				if(fieldNameStr.startsWith("CF:") &&
						fieldNameStr.contains(":"+lstSegRule.getMailingListId()+":") &&
						fieldNameStr.endsWith(":"+lstSegRule.getFieldName())) {
					logger.debug(fieldNameStr+" is Matched ");
					fieldsLb.setSelectedItem(li);
				}
				else if(fieldNameStr.equalsIgnoreCase(lstSegRule.getColumnName()) ){
					fieldsLb.setSelectedItem(li);
					logger.debug(fieldNameStr+" is Matched ");
				}
			}
		}
		fieldsLb.setParent(ruleHb);
		fieldsLb.setMold("select");
		
		final Listbox operatorLb = new Listbox();
		operatorLb.setWidth("120px");
		operatorLb.setMold("select");
		operatorLb.setStyle("margin:0 8px;");
		
		createItemsForOperatorsLb(operatorLb, dataTypeStr,constraintStr);
		operatorLb.setParent(ruleHb);
		
		final Textbox value1Tb = new Textbox();
		value1Tb.setWidth("120px");
		//value1Tb.setHeight("20px");
		value1Tb.setParent(ruleHb);		
		value1Tb.setVisible(false);
		if(ruleStr==null) value1Tb.setVisible(true);
		
		if(data1 != null && 
			(dataTypeStr.equals(ListSegmentRule.STRING) || 
					dataTypeStr.equals(ListSegmentRule.NUMBER))) {
			value1Tb.setValue(data1);
			value1Tb.setVisible(true);
		}
		
		final Textbox value2Tb = new Textbox();
		value2Tb.setWidth("120px");
		//value2Tb.setHeight("20px");
		value2Tb.setParent(ruleHb);
		value2Tb.setVisible(false);
		
		if(data2 != null && 
				(dataTypeStr.equals(ListSegmentRule.STRING) || 
						dataTypeStr.equals(ListSegmentRule.NUMBER))) {
			
			value2Tb.setValue(data2);
			value2Tb.setVisible(true);
		}
		
		final Datebox datebox1 = new Datebox();
		datebox1.setParent(ruleHb);
		datebox1.setVisible(false);
		if(data1 != null &&  dataTypeStr.equals(ListSegmentRule.DATE)) {
			datebox1.setFormat("dd/MM/yyyy");
			datebox1.setText(data1);
			datebox1.setReadonly(true);
			//datebox1.setValue(new Date(data1));
			datebox1.setVisible(true);
		}
		
		final Datebox datebox2 = new Datebox();
		datebox2.setParent(ruleHb);
		datebox2.setVisible(false);
		if(data2 != null && dataTypeStr.equals(ListSegmentRule.DATE) ) {
			datebox2.setFormat("dd/MM/yyyy");
			datebox2.setText(data2);
			datebox2.setReadonly(true);
			//datebox2.setValue(new Date(data2));
			datebox2.setVisible(true);
		}
		
		final Listbox booleanLb = new Listbox();
		booleanLb.setMold("select");
		Listitem bLi1 = new Listitem("True");
		bLi1.setParent(booleanLb);
		Listitem bLi2 = new Listitem("False");
		bLi2.setParent(booleanLb);
		booleanLb.setVisible(false);
		
		if(dataTypeStr != null && dataTypeStr.equals(ListSegmentRule.BOOLEAN)) {
			booleanLb.setVisible(true);
			if(data1.equalsIgnoreCase("true")) 
				booleanLb.setSelectedItem(bLi1);
			else 
				booleanLb.setSelectedItem(bLi2);
		}
		booleanLb.setParent(ruleHb);
		
		
		Toolbarbutton deleteIcon  = new Toolbarbutton();
		deleteIcon.setImage("/images/action_delete.gif");
		deleteIcon.setStyle("margin-left:10px;");
		deleteIcon.setParent(ruleHb);
		ruleHb.setParent(segmentRulesVbId);
		
	
		
		fieldsLb.addEventListener("onSelect", new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				
				value2Tb.setVisible(false);
				Listbox lb = (Listbox)event.getTarget();
				try {
					ListSegmentRule lstSegRule = 
						(ListSegmentRule)lb.getSelectedItem().getValue();
					String dataType = lstSegRule.getDataType();
					logger.debug(" dataType :"+dataType);
					value1Tb.setValue("");
					value2Tb.setValue("");
					if(dataType.equalsIgnoreCase(ListSegmentRule.STRING)) {
						createItemsForOperatorsLb(operatorLb, dataType,null);
						value1Tb.setVisible(true);
						value2Tb.setVisible(false);
						datebox1.setVisible(false);
						datebox2.setVisible(false);
						booleanLb.setVisible(false);
						
						
					} else if(dataType.equalsIgnoreCase(ListSegmentRule.NUMBER)) {
						createItemsForOperatorsLb(operatorLb, dataType,null);
						value1Tb.setVisible(true);
						value2Tb.setVisible(false);
						datebox1.setVisible(false);
						datebox2.setVisible(false);
						booleanLb.setVisible(false);
						
						
					} else if(dataType.equalsIgnoreCase(ListSegmentRule.DATE)) {
						createItemsForOperatorsLb(operatorLb, dataType,null);
						value1Tb.setVisible(false);
						value2Tb.setVisible(false);
						datebox1.setVisible(true);
						datebox2.setVisible(false);
						booleanLb.setVisible(false);
						
						
					}else if(dataType.equalsIgnoreCase(ListSegmentRule.BOOLEAN)) {
						createItemsForOperatorsLb(operatorLb, dataType,null);
						value1Tb.setVisible(false);
						value2Tb.setVisible(false);
						datebox1.setVisible(false);
						datebox2.setVisible(false);
						booleanLb.setVisible(true);
						
					}
				} catch (Exception e) {
					logger.error("Exception : ", (Throwable)e);
				}
				
			}
			
		});
		
		operatorLb.addEventListener("onSelect", new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				
				Listbox lb = (Listbox)event.getTarget();
				try {
					ListSegmentRule lstSegRule;
					if(fieldsLb.getSelectedIndex() == -1 ){
						lstSegRule = (ListSegmentRule)fieldsLb.getItemAtIndex(0).getValue();
					} else {
						lstSegRule = (ListSegmentRule)fieldsLb.getSelectedItem().getValue();
					}
					
					if(lb.getSelectedItem().getLabel().equalsIgnoreCase("between")) {
						if(lstSegRule.getDataType().equalsIgnoreCase(ListSegmentRule.DATE)) {
							datebox1.setVisible(true);
							datebox2.setVisible(true);
							value1Tb.setVisible(false);
							value2Tb.setVisible(false);
							booleanLb.setVisible(false);
						}else {
							value1Tb.setVisible(true);
							value2Tb.setVisible(true);
							datebox1.setVisible(false);
							datebox2.setVisible(false);
							booleanLb.setVisible(false);
						}
						
					} else {
						if(lstSegRule.getDataType().equalsIgnoreCase(ListSegmentRule.DATE)) {
							datebox1.setVisible(true);
							datebox2.setVisible(false);
							value1Tb.setVisible(false);
							value2Tb.setVisible(false);
							booleanLb.setVisible(false);
						}else if(lstSegRule.getDataType().equalsIgnoreCase(ListSegmentRule.BOOLEAN)) {
							value1Tb.setVisible(false);
							value2Tb.setVisible(false);
							datebox1.setVisible(false);
							datebox2.setVisible(false);
							booleanLb.setVisible(true);
						} else {
							value1Tb.setVisible(true);
							value2Tb.setVisible(false);
							datebox1.setVisible(false);
							datebox2.setVisible(false);
							booleanLb.setVisible(false);
						}
						
					}
				} catch (Exception e) {
					logger.error("Exception : ", (Throwable)e);
				}
				
			}
			
		});
		
		deleteIcon.addEventListener("onClick", new EventListener() {

			@Override
			public void onEvent(Event event) throws Exception {
				Hbox hb = (Hbox)(event.getTarget().getParent());
				Vbox segmentRulesVbId =(Vbox) hb.getParent();
				if(segmentRulesVbId.getChildren().size() == 2) {
					
					MessageUtil.setMessage("At least one rule is must.", "color:red;");
					return;
					
				}
				
				
				segmentRulesVbId.removeChild(hb);
				if(segmentRulesVbId.getChildren().size() <= 1) {
					segmentRulesVbId.setVisible(false);
					addRuleToolbarId.setVisible(false);
					//listsTypeRgId.setSelectedIndex(0);
				}
				
				segmentCount--;
				if(segmentCount < maxSegmentCount) {
					addRuleToolbarId.setDisabled(false);
				} else {
					addRuleToolbarId.setDisabled(true);
				}
			}
			
		});
		segmentCount++;
		if(segmentCount >= maxSegmentCount) {
			addRuleToolbarId.setDisabled(true);
		} else {
			addRuleToolbarId.setDisabled(false);
		}
		return ruleHb;
	}*/
	
	/*public void onSelect$dispMlLBoxId() {
		try {
			logger.debug("---just entered to display fields---");
			if(dispMlLBoxId.getSelectedCount() > 0) {
				//getMListCFData();
			}
		} catch (Exception e) {
			logger.error("Exception :: error getting from the getMListCFData()",e);
		}
	} // onClick$dispMlLBoxId
	*/
	
	//decide further
	/*@SuppressWarnings("unchecked")
	public void getMListCFData() {
		
		logger.debug("-- just entered --");
		Set<Listitem> selectedItems = dispMlLBoxId.getSelectedItems();
		
		totalFieldsList.clear();
		totalFieldsList.addAll(defaultFieldsList);
		String listNamesStr="";
		
		for (Listitem li : selectedItems) {
			
			MailingList mailingList = (MailingList) li.getValue();			
			if(listNamesStr.length() != 0) listNamesStr+=", ";
			listNamesStr += mailingList.getListName() ;
			
			if(segRuleOptionLbId.getSelectedIndex() != 0 || selectedItems.size() > 1) {
				continue;
			}// if
			
			logger.debug(" IS custom fields configured : "+mailingList.isCustField());
			
			if( !mailingList.isCustField()) continue;

			List<MLCustomFields> list = mlCFDao.findAllByList(mailingList);
			
			if(logger.isDebugEnabled())
				logger.debug(" Total Custom Fileds for the already existing list :"	+list.size());
			
			ListSegmentRule lstSegRule;
			
			for (MLCustomFields customField : list) {
				
				lstSegRule = new ListSegmentRule(customField.getCustFieldName(),
						customField.getDataType().toUpperCase(), "cust_"+customField.getFieldIndex());
				lstSegRule.setMailingListId(mailingList.getListId());
				totalFieldsList.add(lstSegRule);
			}// for
		} // for
		
		
		//selMlLblId.setValue(listNamesStr);
		//selectedListsDivId.setVisible(true);
		List rows = segmentRulesVbId.getChildren();
		Listitem li;
		ListSegmentRule lstSegRule;
		Listbox fieldsList;
		
		for(int i=1;i<rows.size();i++) {
			fieldsList = (Listbox)(((Hbox)rows.get(i)).getChildren().get(0));
			
			Components.removeAllChildren(fieldsList);
			for(int j=0;j<totalFieldsList.size();j++) {
				lstSegRule = (ListSegmentRule)totalFieldsList.get(j);
				li = new Listitem(lstSegRule.toString());
				li.setValue(lstSegRule);
				li.setParent(fieldsList);
			}
			fieldsList.invalidate();
			fieldsList = (Listbox)(((Hbox)rows.get(i)).getChildren().get(1));
			createItemsForOperatorsLb(fieldsList, ListSegmentRule.STRING, null);
		}
		
		//resetSegmentFields();
		//filterSegmentFields();
	}*/
	
	
	
	
	
	
	/*public void onClick$addRuleToolbarId() {
		try {
			addRules(null);
		} catch (Exception e) {
			logger.error("Exception :: error getting from addRules method",e);
		}
	}
	*/
	
	/*public ListModel getUsersModel() {
		return new ListModelList(getSegRuleList());
	}
	*/
	
	
	/*public List getSegRuleList() {
		logger.debug("---just entered---6");
		List retList = segmentRulesDao.findByUser(user.getUserId());
		logger.debug("got segmentRules::"+retList.size());
		return retList;
	}*/
	
	
	/*public RowRenderer getRowRenderer() {
		return rowRender;
	}
	
	private final RowRenderer rowRender  = new MyRenderer();
	
	private class MyRenderer implements RowRenderer,EventListener {
		MyRenderer() {
			super();
			logger.debug("new MyRenderer object is created");
		}
		
		public void render(Row row, java.lang.Object data,int arg2) {
			
			try {
				int size = 0;
				String generatedquery=null;
			if(data instanceof SegmentRules) {
				SegmentRules segmentRules = (SegmentRules) data;
			
				new Label(segmentRules.getSegRuleName()).setParent(row);
				
				
				
				new Label(dispRule(segmentRules.getSegRule())).setParent(row);
				new Label(segmentRules.getMailingList().getListName()).setParent(row);
				
				generatedquery = segmentRules.getSegQuery();
				if(generatedquery != null) {
					
					size = contactsDao.getSegmentedContactsCount(generatedquery);
					
				}
				
				new Label(size+"").setParent(row);
				
				
				Hbox hbox = new Hbox();
				hbox.setPack("center");
				
				Image img = new Image("/img/icons/delete_icon.png");
				img.setTooltiptext("Delete");
				img.setAttribute("segmentRuleObj", segmentRules);
				img.setAttribute("imageEventName", "deletesegRule");
				img.setStyle("cursor:pointer;");
				img.addEventListener("onClick", this);
				img.setStyle("padding-right:5px");
				img.setParent(hbox);
				
				img = new Image("/img/icons/edit_lists_icon.png");
				img.setTooltiptext("Edit");
				img.setAttribute("segmentRuleObj", segmentRules);
				img.setAttribute("imageEventName", "editSegRule");
				img.setStyle("cursor:pointer;");
				img.addEventListener("onClick", this);
				img.setParent(hbox);
				
				hbox.setParent(row);
				
			} // if
			} catch (Exception e) {
				logger.error("Exception ::", e);
			}

		} // render
		
		public String dispRule(String rule) {
			String dispRule = "";
			String option=null;
			if(rule != null) {
				
				
				String[] rowsArr = rule.split("\\|\\|");
				String[] columnsArr; 
				
				
				columnsArr = rowsArr[0].split(":");
				if(columnsArr.length > 0) {
					
					if(columnsArr[0].trim().equalsIgnoreCase("Any") ) { 
						option = "OR";
					} 
					else {
						option = "AND";
					}
				}//if
				
				
				String[] tempStrArr = null;
				String fieldNameStr = null;
				String dataTypeStr = null;
				String constraintStr = null;
				String data1 = null;
				String data2 = null;
				String data = "";
				
				for(int i=1;i<rowsArr.length;i++) {
					
					columnsArr = rowsArr[i].split("\\|");
					fieldNameStr = columnsArr[0].trim();
					tempStrArr = columnsArr[1].trim().split(":");
					dataTypeStr = tempStrArr[0].toUpperCase().trim();
					constraintStr = tempStrArr[1];
					data = data1 = columnsArr[2];
					data2 = (columnsArr.length>3)?columnsArr[5]:null;
					logger.debug("fieldNameStr :"+fieldNameStr);
					logger.debug("dataTypeStr :"+dataTypeStr);
					logger.debug("constraintStr :"+constraintStr);
					logger.debug("data1 :"+data1);
					logger.debug("data2 :"+data2);
					
					if(data2 != null){
						data = data1+" , "+data2;
					}
					if(dispRule.length()>0) dispRule += " "+option+" ";
					dispRule += "("+fieldNameStr+" "+constraintStr+" "+data+")";
					
					
				} // outer for
			
			}
			
			return dispRule;
			
		}
		
		
		@Override
		public void onEvent(Event evt) throws Exception {
			
			Object obj = evt.getTarget();
			if(obj instanceof Image) {
				
				Image img = (Image)obj;
				SegmentRules segmentRules = (SegmentRules)img.getAttribute("segmentRuleObj");
				
				if(img.getAttribute("imageEventName").equals("editSegRule")) {
					
					showSegmentRulePanel(false, segmentRules);
					
					
				}
				else if(img.getAttribute("imageEventName").equals("deletesegRule")) {
					
					 if (Messagebox.show("Are you sure you want to delete the segment rule?", "Prompt", 
			        		  Messagebox.YES|Messagebox.NO, Messagebox.QUESTION) == Messagebox.NO) {
			            return;
			          }
					
					 
					 segmentRulesDao.delete(segmentRules);
						//img.getParent().getParent().setVisible(false);
						
						
						Include xcontents = (Include)Sessions.getCurrent().getAttribute("xcontents");
						xcontents.setSrc("/zul/" + PageListEnum.EMPTY + ".zul");
						xcontents.setSrc("/zul/" + PageListEnum.CONTACT_MANAGE_SEGMENTS + ".zul");
						
						logger.debug("--After Delete--");
						
						Messagebox.show("Segment rule deleted successfully.", 
								"Information", Messagebox.OK, Messagebox.INFORMATION);
					
				}
				
			}
			
		}
		
	}*/
	
	public void onClick$cancelBtnId() {//it is nothing but back
		
		addNewSegRuleDivId.setVisible(true);
		segmentRulesDivId.setVisible(false);
		
		
		
	}//onClick$cancelBtnId()
	
	
	public void showSegmentRulePanel(boolean isFromAdd, SegmentRules segRules) {
		
		
		if(!isFromAdd) {
			/*for(int i=0; i<listTypeRgId.getItemCount(); i++) {
				
				if(((String)listTypeRgId.getItemAtIndex(i).getValue()).equals(segmentRule.getMailingList().getListType())) {
					
					listTypeRgId.setSelectedIndex(i);
				}
				
			}*/
			
			
			//onSelect$listTypeRgId();
			if(dispMlLBoxId.getSelectedCount() > 0) dispMlLBoxId.clearSelection();
			
			String segmentMLlistsStr = segRules.getSegmentMlistIdsStr(); 
			if(segmentMLlistsStr == null || segmentMLlistsStr.isEmpty()) {
				
				MessageUtil.setMessage("No mailing lists has been configured to this segment.", "color:red;");
				
				
			}
			else if(segmentMLlistsStr != null && !segmentMLlistsStr.isEmpty()) {
			
				String[] segmentMLlistsArr = segmentMLlistsStr.split(Constants.DELIMETER_COMMA);
				
				for (String listID : segmentMLlistsArr) {
					
					
					for(int i=0; i< dispMlLBoxId.getItemCount(); i++) {
						
						Listitem item = dispMlLBoxId.getItemAtIndex(i);
						MailingList mList = (MailingList)item.getValue();
						
						if(mList.getListId().toString().equals(listID.trim())) {
							
							dispMlLBoxId.addItemToSelection(item);
							
						}
						
					}//INNER-FOR
					
					
				}//OUTER
			
			}
			//getMListCFData();
			
			segmentRuleNameTbId.setValue(segmentRule.getSegRuleName());
			segmentRuleNameTbId.setDisabled(true);
			
			segRuleDescpTxtBoxId.setValue(segmentRule.getDescription());
			
			
		}else if(isFromAdd) {
			
			//listTypeRgId.setSelectedIndex(0);
			
			
			segmentRuleNameTbId.setValue("");
			segRuleDescpTxtBoxId.setValue("");
			
			/*segRuleOptionLbId.setSelectedIndex(0);
			
			
			if(segmentRulesVbId.getChildren().size() == 1) {
				
				addRules(null);
			}
			segmentRulesVbId.setVisible(true);
			addRuleToolbarId.setVisible(segmentRulesVbId.isVisible());
			getMListCFData();
			
			segmentBtnId.setVisible(true);
			updateBtnId.setVisible(false);
			addRuleBtnId.setDisabled(true);*/
			
			
		}
		addNewSegRuleDivId.setVisible(true);
		
	}
	
	
	private void parseRules(String segRule){
		logger.debug("-- just entered--");
		try{
			
			logger.debug("Segment Rule :"+ segRule);
			if(segRule == null ){
				return;
			}
			//listsTypeRgId.setSelectedIndex(1);	
			//segmentRulesVbId.setVisible(true);
			String[] rowsArr = segRule.split("\\|\\|");
			//String[] columnsArr; 
			
			//*************changes made for new approch of interaction attribute******************
			String[]  columnsArr = rowsArr[0].split(":");
			
			if(columnsArr.length > 2 && dispcampaignsLbId.getSelectedCount() == 0) {
				String csCampIDs = columnsArr[2];
				if(columnsArr.length == 4) {
					latestCRChkBoxId.setChecked(true);
				}else{
					latestCRChkBoxId.setChecked(false);
				}
				
				logger.debug("csCampIDs in if ::"+csCampIDs);
				if(csCampIDs.contains(Constants.DELIMETER_COMMA)) {
					for (String campId : csCampIDs.split(Constants.DELIMETER_COMMA)) {
						
						for (Listitem campItem : dispcampaignsLbId.getItems()) {
							
							if(campId.trim().equals(((Long)campItem.getValue()).longValue()+Constants.STRING_NILL)) {
								dispcampaignsLbId.addItemToSelection(campItem);
								logger.debug("csCampIDs got selected ");
								break;
								
							}
							
							
							
						}
						
						
						
					}
				}
				else{
					for (Listitem campItem : dispcampaignsLbId.getItems()) {
						logger.debug("csCampIDs in else ::"+csCampIDs);
						if(csCampIDs.trim().equals(((Long)campItem.getValue()).longValue()+Constants.STRING_NILL)) {
							
							dispcampaignsLbId.addItemToSelection(campItem);
							logger.debug("csCampIDs got selected ");
							break;
							
						}//if
					
					
					}//for
				}//else
			}//if
			//******************changes made for new approch of interaction attribute*****************
			
			
			/*if(columnsArr.length > 0) {
				
				if(columnsArr[0].trim().equalsIgnoreCase("Any") ) { 
					segRuleOptionLbId.setSelectedItem(segRuleOptionLbId.getItemAtIndex(0));
				} 
				else {
					segRuleOptionLbId.setSelectedItem(segRuleOptionLbId.getItemAtIndex(1));
				}
			}*/
			
			for(int i=1;i<rowsArr.length;i++) {
				// top most rule div along with it i need to create a temp div consisting
				Div tempDiv = new Div();
				tempDiv.setSclass("");
					
				Label AndLabel = new Label("AND");
				AndLabel.setStyle("display:inline-block; width:20px; padding:0 5px;margin:0 10px;font-weight:bold;");
				AndLabel.setParent(tempDiv);
				if(!editPromoFlag)
					promotempDiv=tempDiv;
			    if(promotempDiv!=null && editPromoFlag) {
			    	tempDiv=promotempDiv;
			    	promotempDiv=null;
			    }
				addSegmentRules(rowsArr[i], tempDiv); 
				//addRules(rowsArr[i]);
			} // outer for
			//addRuleToolbarId.setVisible(true);
		}catch(Exception e ){
			logger.error("", (Throwable)e);
		}
	}
	boolean editPromoFlag=false;
	Div promotempDiv=null;
	Div promoObj=null;
	Div promovdiv=null;
	public void addSegmentRules(String ruleStr, Div anotheRuleDivSetDiv) {
		
		try {
			logger.debug("got the segment Rule...."+ruleStr);
			
			String[] orRuleTokensArr = null;
			//String orRuleToken = null;
			String[] tokenArr = null;
			String columnName = null;
			String dispLabel = null;
			String type = null;
			String constraint = null;
			if(ruleStr.contains("<OR>")) {
				orRuleTokensArr = ruleStr.split("<OR>");
				Div vdiv=null;
				if(!editPromoFlag) {
				vdiv = new Div();//this is the outer component
				vdiv.setParent(anotheRuleDivSetDiv);
				vdiv.setSclass("segment_parent_div");
				vdiv.addEventListener("onDrop", myDropListener);
				promovdiv=vdiv;
				}
				if(promovdiv!=null && editPromoFlag) {
					vdiv=promovdiv;
					promovdiv=null;
			    }
				
				for (String orRuleToken : orRuleTokensArr) {
					String value1 = null;
					String value2 = null;
					//String campIdVal = null;   
					
					String itemLabel = null;
					List<SegmentEnum> children = null;
					logger.debug("'OR' Rule token is :: "+orRuleToken);
					tokenArr = orRuleToken.split("\\|");
					dispLabel = tokenArr[0];
					columnName = tokenArr[1];
					type = tokenArr[2].split(":")[0];
					constraint = tokenArr[2].split(":")[1];
					value1 = tokenArr[3];
					
					if(dispLabel.equalsIgnoreCase("OptCulture Promotion Name") && columnName.equalsIgnoreCase("cd.coupon_id") )
						return;
					if(dispLabel.equalsIgnoreCase("OptCulture Promotion Name") && value1.contains(";=;"))
						value1=value1.split(";=;")[0];
					if(dispLabel.equalsIgnoreCase("Promo-code Date"))
						editPromoFlag=true;
					if(columnName.contains("c.birth_day") ||columnName.contains("c.anniversary_day") ) {
						for(Entry<String, String> entry : tokenMap.entrySet()) {
							if(constraint.equals(tokenMap.get(entry.getKey()))) {
								constraint=entry.getKey(); break;
							}
									
						}
					}
					if(columnName.contains("cd.issued_on") && constraint.contains("iseqwithinnext")) {
						for(Entry<String, String> entry : tokenMapForIyD.entrySet()) {
							if(constraint.equals(tokenMapForIyD.get(entry.getKey()))) {
								constraint=entry.getKey(); break;
							}
						}
					}
					if(tokenArr.length > 4) {
						
						value2 = tokenArr[4];
						
					}//values 
					
					SegmentEnum retEnum = SegmentEnum.getEnumByItem(dispLabel);
					if(retEnum != null) {
						 //itemLabel = retEnum.getDispLabel();
						 try {
							POSMapping posMapping = posMappingDao.findRecordBycustomFieldName(retEnum.getPosCustomFieldLbl(), Constants.POS_MAPPING_TYPE_SKU, currentUser.getUserId());
							 if(!posMapping.getDisplayLabel().equals(retEnum.getDispLabel()))
								 itemLabel=posMapping.getDisplayLabel();	//APP-3558
						} catch (Exception e) {
							logger.error("Exception",e);
							itemLabel = retEnum.getDispLabel();
						}
						children = retEnum.getChidrenByParentEnum(retEnum);
						logger.debug("constraint ::"+constraint+" children ::"+children.size());
						
					}
					
					SegmentEnum seg = SegmentEnum.getEnumByItem(dispLabel);
					//itemLabel = getMostParentDisplayLabel(retEnum);
					
					
					
					
					if( columnName.toLowerCase().startsWith("c.") || columnName.toLowerCase().startsWith("date(c.") 
							|| ( columnName.toLowerCase().startsWith("loyalty.") && seg != null && seg.getCategoryCode() == 1)|| 
							(columnName.toLowerCase().startsWith("date(loyalty.")&& seg != null && seg.getCategoryCode() == 1)){//if Profile attribute;include 'date(c.' further
						if(anotheRuleDivSetDiv.getParent() == null) {
							vdiv.setDroppable("profileSegment");
							anotheRuleDivSetDiv.setParent(profileAttributeDivId);
							updateAndLabel(profileAttributeDivId);
							vdiv.appendChild(createDropORDiv("Drag Profile Attribute here to create OR combination rule."));
						}
						
						for (Listitem item : profileAttrLbId.getItems()) {
							if(itemLabel == null){
								logger.debug("columnName ::"+columnName+" item-column :: "+(String)item.getAttribute("DBcolumnName"));
								if(!columnName.equals((String)item.getAttribute("DBcolumnName"))){
									
									continue;
								}
								
								itemLabel = item.getLabel();
								
							}
							if(item.getLabel().equalsIgnoreCase(itemLabel)) {
								Div obj = createNewRuleDiv(item);
								obj.setParent(vdiv);
								updateORLabel(vdiv);
								
								List ruleDivChildren =  obj.getChildren();
								Listbox ruleLb = (Listbox)ruleDivChildren.get(2);
								Listbox numberLb = (Listbox)ruleDivChildren.get(4);
								Listbox operatorLb = (Listbox)ruleDivChildren.get(5);
								/*Datebox dateBox1 = (Datebox)ruleDivChildren.get(8);
								Datebox dateBox2 = (Datebox)ruleDivChildren.get(9);
								Decimalbox numberbox1 = (Decimalbox)ruleDivChildren.get(6);
								Decimalbox numberbox2 = (Decimalbox)ruleDivChildren.get(7);
								Textbox txtbox = (Textbox)ruleDivChildren.get(5);*/
								Datebox dateBox1 = (Datebox)ruleDivChildren.get(9);
								Datebox dateBox2 = (Datebox)ruleDivChildren.get(10);
								Decimalbox numberbox1 = (Decimalbox)ruleDivChildren.get(7);
								Decimalbox numberbox2 = (Decimalbox)ruleDivChildren.get(8);
								Textbox txtbox = (Textbox)ruleDivChildren.get(6);
								/*Combobox cb1 = (Combobox)ruleDivChildren.get(12);
								Combobox cb2 = (Combobox)ruleDivChildren.get(13);
								Listbox formLb = (Listbox)ruleDivChildren.get(11);*/
								/*Combobox cb1 = (Combobox)ruleDivChildren.get(11);
								Combobox cb2 = (Combobox)ruleDivChildren.get(12);
								Listbox formLb = (Listbox)ruleDivChildren.get(10);*/
								Combobox cb1 = (Combobox)ruleDivChildren.get(12);
								Combobox cb2 = (Combobox)ruleDivChildren.get(13);
								Listbox formLb = (Listbox)ruleDivChildren.get(11);
								//Listbox campLb = (Listbox)ruleDivChildren.get(10);
								
								// Generate Drop Div
								
								
								
								//make selection of the options lb
								//Date(c.created_date)|date:isToday|Today
								boolean isSelected = false;
								
								if(constraint != null) {
									boolean isParentSelected = false;
									for (Listitem ruleItem : ruleLb.getItems()) {
										SegmentEnum segmentEnum = ruleItem.getValue();
										logger.debug("ruleItem ::"+ruleItem.getLabel()+" segmentEnum.getColumnValue() "+segmentEnum.getColumnValue());
										
											if(segmentEnum.getColumnName() != null && !segmentEnum.getColumnName().isEmpty() ){
												if(segmentEnum.getColumnName().equals(columnName)) {
													ruleItem.setSelected(true);
													Events.sendEvent("onSelect", ruleLb, null);
													isParentSelected = true;
												}
												else continue;
												
											}
											List<SegmentEnum> retList = segmentEnum.getChidrenByParentEnum(segmentEnum);
											if( ( retList == null || retList.isEmpty() ) && segmentEnum.getToken() != null 
													&& !segmentEnum.getToken().isEmpty() && constraint.equals(segmentEnum.getToken())) {
												if(!isParentSelected){
													ruleItem.setSelected(true);
													Events.sendEvent("onSelect", ruleLb, null);
												}
												if(type.equalsIgnoreCase("Date")) {
													
													populateValuesForOperatorForTypeDate(dateBox1, dateBox2, 
															numberLb, value1, value2, numberbox1, numberbox2, null);
													
												}else if(type.equalsIgnoreCase("String")) {
													
													populateValuesForOperatorForTypeString(txtbox, cb1, cb2, formLb, value1, value2);
													
												}else if(type.equalsIgnoreCase("number")) {
													if(segmentEnum.getType1() == null && segmentEnum.getType2() == null &&
															value1 != null && value2 == null){
														for (Listitem operatorItem : operatorLb.getItems()) {
															if(operatorItem.getValue() != null && operatorItem.getValue().toString().equalsIgnoreCase(value1)) {
																operatorItem.setSelected(true);
															}
														}
													}
													
													
													populateValuesForOperatorForTypeNumber(numberbox1, numberbox2, 
															txtbox, cb1, cb2, value1, value2);
													
												}
												break;
											}
											else if(retList != null && !retList.isEmpty()) {
												logger.debug("for ::"+segmentEnum.name()+" ITEM ::"+ruleItem.getLabel());
												for (SegmentEnum child : retList) {
													logger.debug("child is ::"+child.name());
													if(child.getToken() != null && constraint.equals(child.getToken())) {
														logger.debug("child.getToken() ::"+child.getToken());
														if(!isParentSelected){
															ruleItem.setSelected(true);
															Events.sendEvent("onSelect", ruleLb, null);
														}
														for (Listitem operatorItem : operatorLb.getItems()) {
															SegmentEnum operatorEnum = operatorItem.getValue();
															if(constraint.equals(operatorEnum.getToken())) {
																operatorItem.setSelected(true);
																Events.sendEvent("onSelect", operatorLb, null);
																
																if(type.equalsIgnoreCase("Date")) {
																	
																	populateValuesForOperatorForTypeDate(dateBox1, dateBox2, 
																			numberLb, value1, value2, numberbox1, numberbox2, operatorEnum);
																	
																}else if(type.equalsIgnoreCase("String")) {
																	
																	populateValuesForOperatorForTypeString(txtbox, cb1, cb2, formLb, value1, value2);
																	
																}else if(type.equalsIgnoreCase("number")) {
																	populateValuesForOperatorForTypeNumber(numberbox1, numberbox2,
																			txtbox, cb1, cb2,  value1, value2);
																}
																
																/*if(numberLb.isVisible()) {
																	
																	int value = Integer.parseInt(value1)/Integer.parseInt(operatorEnum.getType2());
																	for (Listitem numberItem : numberLb.getItems()) {
																		
																		if(numberItem.getLabel().equals(value+Constants.STRING_NILL)) {
																			
																			numberItem.setSelected(true);
																			break;
																		}
																		
																	}//for
																		
																		
																}//if numberLb visible
*/															break;
															}
															
														}
														isSelected = true;
														break;
														
														
													}
													
													
												}//for
												
												
											}//if
											if(isSelected) break;
											
											
									
										if(isSelected) break;
									}
									
									
									
								}//if
								
								
								
								break;
								
							}
							
							
						}
						//DropProfileComponent(true);
						//onDrop$profileAttributeDivId(event);
						
					}//if
					else if(columnName.startsWith("sal.") || columnName.toLowerCase().startsWith("date(sal.") 
							|| columnName.startsWith("sku.") ||  columnName.toLowerCase().startsWith("date(sku.") 
							||  columnName.startsWith("new_tot") ||
							columnName.startsWith("new_avg") || 
							columnName.startsWith("new_max") ||
							columnName.startsWith("new_count") ||  columnName.startsWith("aggr_") || columnName.startsWith("aggrlty_count")
							|| columnName.toLowerCase().startsWith("loyalty.")
							|| columnName.toLowerCase().startsWith("date(ltytrxch.")
							|| columnName.toLowerCase().startsWith("aggrlty_") 
							|| columnName.startsWith("cd.") ||columnName.startsWith("date(cd.")|| columnName.startsWith("cp.") 
							|| columnName.startsWith("active") ||  columnName.startsWith("redeem")
							||columnName.toLowerCase().startsWith("c.")
							&& seg != null && (seg.getCategoryCode() == 2 || seg.getCategoryCode() == 4) ||  
							columnName.startsWith("aggr.") ||  columnName.startsWith("if((year(now())")//these 2 added for RSBros
							|| columnName.startsWith("ROUND((sal.quantity*sal.sales_price)+sal.tax-(IF(sal.discount"))//app-3827
					{//if Profile attribute;include 'date(c.' further
						if(anotheRuleDivSetDiv.getParent() == null){
							vdiv.setDroppable("purIntSegment");
							anotheRuleDivSetDiv.setParent(purIntAttributeDivId);
							updateAndLabel(purIntAttributeDivId);
							if(!columnName.startsWith("cp."))
							vdiv.appendChild(createDropORDiv("Drag Purchase Attribute here to create OR combination rule."));
							
						}
						if(columnName.startsWith("cd.")||columnName.startsWith("date(cd."))count++;
						if(columnName.contains("cp.coupon_id")) {
							SegmentEnum senum=null;
						 flag=false;
						List<SegmentEnum> purchaseAttrList = SegmentEnum.getEnumsByCategoryCode(4);
						{
							for(SegmentEnum en:purchaseAttrList) {
								itemLabel=	en.getDispLabel();
								senum=en;
							}
						  }
						Listitem item=new Listitem(itemLabel);
						Div obj = getCreatedCompDiv(senum, item);
						 vdiv.setAttribute("Type",SegmentEnum.PROMO_CODE_PROMO_STATUS.name());
						 obj.setParent(vdiv);
						 vdiv.setParent(purIntAttributeDivId);
						 updateORLabel(vdiv);
						// updateAndLabel(purIntAttributeDivId);

						List ruleDivChildren =  obj.getChildren();
						Listbox ruleLb = (Listbox)ruleDivChildren.get(2);
						Listbox numberLb = (Listbox)ruleDivChildren.get(4);
						Listbox operatorLb = (Listbox)ruleDivChildren.get(5);
						
						Datebox dateBox1 = (Datebox)ruleDivChildren.get(9);
						Datebox dateBox2 = (Datebox)ruleDivChildren.get(10);
						Decimalbox numberbox1 = (Decimalbox)ruleDivChildren.get(7);
						Decimalbox numberbox2 = (Decimalbox)ruleDivChildren.get(8);
						Textbox txtbox = (Textbox)ruleDivChildren.get(6);
					
						Combobox cb1 = (Combobox)ruleDivChildren.get(12);
						Combobox cb2 = (Combobox)ruleDivChildren.get(13);
						Listbox formLb = (Listbox)ruleDivChildren.get(11);
						
						boolean isSelected = false;
						logger.debug("constraint ::"+constraint);
						if(constraint != null ) {
							boolean isParentSelected = false;
							for (Listitem ruleItem : ruleLb.getItems()) {
								SegmentEnum segmentEnum = ruleItem.getValue();
								logger.debug("ruleItem ::"+ruleItem.getLabel()+" segmentEnum.getColumnValue() "+segmentEnum.getColumnValue());
								
									if(segmentEnum.getColumnName() != null && !segmentEnum.getColumnName().isEmpty() ){
										if(segmentEnum.getColumnName().equals(columnName)) {
											ruleItem.setSelected(true);
											Events.sendEvent("onSelect", ruleLb, null);
											isParentSelected = true;
										}
										else continue;
										
									}
									List<SegmentEnum> retList = segmentEnum.getChidrenByParentEnum(segmentEnum);
									if( ( retList == null || retList.isEmpty() ) && segmentEnum.getToken() != null 
											&& !segmentEnum.getToken().isEmpty() && constraint.equals(segmentEnum.getToken())) {
										
										if(!isParentSelected){
											ruleItem.setSelected(true);
											Events.sendEvent("onSelect", ruleLb, null);
										}
										if(type.equalsIgnoreCase("Date")) {
											
											populateValuesForOperatorForTypeDate(dateBox1, dateBox2, 
													numberLb, value1, value2, numberbox1, numberbox2, null);
											
										}else if(type.equalsIgnoreCase("String")) {
											
											populateValuesForOperatorForTypeString(txtbox, cb1, cb2, formLb, value1, value2);
											
										}else if(type.equalsIgnoreCase("number")) {
											populateValuesForOperatorForTypeNumber(numberbox1, numberbox2, 
													txtbox, cb1, cb2, value1, value2);
											
										}
										break;
									}
									else if(retList != null && !retList.isEmpty()) {
										logger.debug("for ::"+segmentEnum.name()+" ITEM ::"+ruleItem.getLabel());
										for (SegmentEnum child : retList) {
											logger.debug("child is ::"+child.name());
											if(child.getToken() != null && constraint.equals(child.getToken())) {
												logger.debug("child.getToken() ::"+child.getToken());
												if(!isParentSelected){
													ruleItem.setSelected(true);
													Events.sendEvent("onSelect", ruleLb, null);
												}
												for (Listitem operatorItem : operatorLb.getItems()) {
													SegmentEnum operatorEnum = operatorItem.getValue();
													if(constraint.equals(operatorEnum.getToken())) {
														operatorItem.setSelected(true);
														Events.sendEvent("onSelect", operatorLb, null);
														
														if(type.equalsIgnoreCase("Date")) {
															
															populateValuesForOperatorForTypeDate(dateBox1, dateBox2, 
																	numberLb, value1, value2, numberbox1, numberbox2, operatorEnum);
															
														}else if(type.equalsIgnoreCase("String")) {
															
															populateValuesForOperatorForTypeString(txtbox, cb1, cb2, formLb, value1, value2);
															
														}else if(type.equalsIgnoreCase("number")) {
															populateValuesForOperatorForTypeNumber(numberbox1, numberbox2,
																	txtbox, cb1, cb2, value1, value2);
														}
														
													break;
													}
													
												}
												isSelected = true;
												break;
												
												
											}
											
										}//for
										
									}//if
									if(isSelected) break;
								if(isSelected) break;
							}
						  }		
							
						}
						
						for (Listitem item : purchaseAttrLbId.getItems()) {
							if(itemLabel == null){
								logger.debug("columnName ::"+columnName+" item-column :: "+(String)item.getAttribute("DBcolumnName"));
								if(!columnName.equals((String)item.getAttribute("DBcolumnName"))){
									
									continue;
								}
								
								itemLabel = item.getLabel();
								
							}
							if(item.getLabel().equalsIgnoreCase(itemLabel)) {
								Div obj=null;
								if(promoObj!=null && editPromoFlag) {
									obj=promoObj;
									editPromoFlag=false;
									promoObj=null;
									count--;
								}
								else{
								obj  = createNewRuleDiv(item);
								obj.setParent(vdiv);
								updateORLabel(vdiv);
								if(editPromoFlag)
									promoObj=obj;
								}

								List ruleDivChildren =  obj.getChildren();
								Listbox ruleLb = (Listbox)ruleDivChildren.get(2);
								Listbox ltyLb = (Listbox)ruleDivChildren.get(3);
								Listbox numberLb = (Listbox)ruleDivChildren.get(4);
								Listbox operatorLb = (Listbox)ruleDivChildren.get(5);
								/*Datebox dateBox1 = (Datebox)ruleDivChildren.get(8);
								Datebox dateBox2 = (Datebox)ruleDivChildren.get(9);
								Decimalbox numberbox1 = (Decimalbox)ruleDivChildren.get(6);
								Decimalbox numberbox2 = (Decimalbox)ruleDivChildren.get(7);
								Textbox txtbox = (Textbox)ruleDivChildren.get(5);*/
								Datebox dateBox1 = (Datebox)ruleDivChildren.get(9);
								Datebox dateBox2 = (Datebox)ruleDivChildren.get(10);
								Decimalbox numberbox1 = (Decimalbox)ruleDivChildren.get(7);
								Decimalbox numberbox2 = (Decimalbox)ruleDivChildren.get(8);
								Textbox txtbox = (Textbox)ruleDivChildren.get(6);
							/*	Combobox cb1 = (Combobox)ruleDivChildren.get(12);
								Combobox cb2 = (Combobox)ruleDivChildren.get(13);
								Listbox formLb = (Listbox)ruleDivChildren.get(11);*/
								/*Combobox cb1 = (Combobox)ruleDivChildren.get(11);
								Combobox cb2 = (Combobox)ruleDivChildren.get(12);
								Listbox formLb = (Listbox)ruleDivChildren.get(10);*/
								Combobox cb1 = (Combobox)ruleDivChildren.get(12);
								Combobox cb2 = (Combobox)ruleDivChildren.get(13);
								Listbox formLb = (Listbox)ruleDivChildren.get(11);
								
								//Listbox campLb = (Listbox)ruleDivChildren.get(10);
								// Generate Drop Div
								
								//make selection of the options lb
								//Date(c.created_date)|date:isToday|Today
								//make selection of the options lb
								//Date(c.created_date)|date:isToday|Today
								boolean isSelected = false;
								boolean isLtySelected = false;
								logger.debug("constraint ::"+constraint);
								if(constraint != null ) {
									boolean isParentSelected = false;
									if(dispLabel.equalsIgnoreCase("Loyalty transaction")){
									for (Listitem ruleItem : ruleLb.getItems()) {
										SegmentEnum segmentEnum = ruleItem.getValue();
										logger.debug("ruleItem ::"+ruleItem.getLabel()+" segmentEnum.getColumnValue() "+segmentEnum.getColumnValue());
										
										List<SegmentEnum> childList = segmentEnum.getChidrenByParentEnum(segmentEnum);
										for(SegmentEnum ltyChild:childList) {
											if(ltyChild.getColumnName() != null && !ltyChild.getColumnName().isEmpty() ){
												if(ltyChild.getColumnName().equals(columnName)) {
													if(ltyChild.name().contains(SegmentEnum.VALUE_CODES.name())) {
														logger.debug("ruleItem ::"+ruleLb.getSelectedItem().getLabel());
														if(ruleItem.getLabel().equalsIgnoreCase(value2)) {
														ruleItem.setSelected(true);
														Events.sendEvent("onSelect", ruleLb, null);
														isParentSelected = true;
														
														for(Listitem ltyRuleItem : ltyLb.getItems()) {
															SegmentEnum ltySegmentEnum = ltyRuleItem.getValue();
															if(ltySegmentEnum.getColumnName().equals(columnName)){
																ltyRuleItem.setSelected(true);
																Events.sendEvent("onSelect", ltyLb, null);
																segmentEnum=ltySegmentEnum;
															}
														}
													}else continue;
												}else {
														ruleItem.setSelected(true);
														Events.sendEvent("onSelect", ruleLb, null);
														isParentSelected = true;
													
														for(Listitem ltyRuleItem : ltyLb.getItems()) {
														SegmentEnum ltySegmentEnum = ltyRuleItem.getValue();
														if(ltySegmentEnum.getColumnName().equals(columnName)){
															ltyRuleItem.setSelected(true);
															Events.sendEvent("onSelect", ltyLb, null);
															segmentEnum=ltySegmentEnum;
														}
													}
													}
												}
												else continue;
												
											}
										}
										
											List<SegmentEnum> retList = segmentEnum.getChidrenByParentEnum(segmentEnum);
											if( ( retList == null || retList.isEmpty() ) && segmentEnum.getToken() != null 
													&& !segmentEnum.getToken().isEmpty() && constraint.equals(segmentEnum.getToken())) {
												
												if(!isParentSelected){
													ruleItem.setSelected(true);
													Events.sendEvent("onSelect", ruleLb, null);
												}
												if(type.equalsIgnoreCase("Date")) {
													
													populateValuesForOperatorForTypeDate(dateBox1, dateBox2, 
															numberLb, value1, value2, numberbox1, numberbox2, null);
													
												}else if(type.equalsIgnoreCase("String")) {
													
													populateValuesForOperatorForTypeString(txtbox, cb1, cb2, formLb, value1, value2);
													
												}else if(type.equalsIgnoreCase("number")) {
													populateValuesForOperatorForTypeNumber(numberbox1, numberbox2, 
															txtbox, cb1, cb2, value1, value2);
													
												}
												break;
											}
											else if(retList != null && !retList.isEmpty()) {
												logger.debug("for ::"+segmentEnum.name()+" ITEM ::"+ruleItem.getLabel());
												for (SegmentEnum child : retList) {
													logger.debug("child is ::"+child.name());
													if(child.getToken() != null && constraint.equals(child.getToken())) {
														logger.debug("child.getToken() ::"+child.getToken());
														if(!isParentSelected){
															ruleItem.setSelected(true);
															Events.sendEvent("onSelect", ltyLb, null);
														}
														for (Listitem operatorItem : operatorLb.getItems()) {
															SegmentEnum operatorEnum = operatorItem.getValue();
															if(constraint.equals(operatorEnum.getToken())) {
																operatorItem.setSelected(true);
																Events.sendEvent("onSelect", operatorLb, null);
																
																if(type.equalsIgnoreCase("Date")) {
																	
																	populateValuesForOperatorForTypeDate(dateBox1, dateBox2, 
																			numberLb, value1, value2, numberbox1, numberbox2, operatorEnum);
																	
																}else if(type.equalsIgnoreCase("String")) {
																	
																	populateValuesForOperatorForTypeString(txtbox, cb1, cb2, formLb, value1, value2);
																	
																}else if(type.equalsIgnoreCase("number")) {
																	populateValuesForOperatorForTypeNumber(numberbox1, numberbox2,
																			txtbox, cb1, cb2, value1, value2);
																}
																
																/*if(numberLb.isVisible()) {
																	
																	int value = Integer.parseInt(value1)/Integer.parseInt(operatorEnum.getType2());
																	for (Listitem numberItem : numberLb.getItems()) {
																		
																		if(numberItem.getLabel().equals(value+Constants.STRING_NILL)) {
																			
																			numberItem.setSelected(true);
																			break;
																		}
																		
																	}//for
																		
																		
																}//if numberLb visible
*/															break;
															}
															
														}
														isSelected = true;
														break;
														
														
													}
													
													
												}//for
												
												
											}//if
											if(isSelected) break;
											
											
									
										if(isSelected) break;
									}
									}
									else if(dispLabel.equalsIgnoreCase("Promo-code Date")) {
										List<SegmentEnum> retList=null;
										for (Listitem ruleItem : ruleLb.getItems()) {
											SegmentEnum segmentEnum = ruleItem.getValue();
											logger.debug("ruleItem ::"+ruleItem.getLabel()+" segmentEnum.getColumnValue() "+segmentEnum.getColumnValue());
													if(segmentEnum.getToken().equals(constraint)) {
														ruleItem.setSelected(true);
														Events.sendEvent("onSelect", ruleLb, null);
														isParentSelected = true;
													}
													else continue;
													
										    }for(Listitem ruleItem : ltyLb.getItems()) {
										    	SegmentEnum segmentEnum = ruleItem.getValue();
										    	if(segmentEnum.getColumnName() != null && !segmentEnum.getColumnName().isEmpty() ){
													if(segmentEnum.getColumnName().equals(columnName)) {
														ruleItem.setSelected(true);
														Events.sendEvent("onSelect", ltyLb, null);
														isParentSelected = true;
													}
													else if((segmentEnum.getInnerQryColumnName()!=null && !segmentEnum.getInnerQryColumnName().isEmpty() && segmentEnum.getInnerQryColumnName().equals(columnName)) ){
														ruleItem.setSelected(true);
														Events.sendEvent("onSelect", ruleLb, null);
														isParentSelected = true;
													}
													else continue;
													
												  }
												retList = segmentEnum.getChidrenByParentEnum(segmentEnum);
												if( ( retList == null || retList.isEmpty() ) && segmentEnum.getToken() != null 
														&& !segmentEnum.getToken().isEmpty() && constraint.equals(segmentEnum.getToken())) {
													
													if(!isParentSelected){
														ruleItem.setSelected(true);
														Events.sendEvent("onSelect", ltyLb, null);
													}
													if(type.equalsIgnoreCase("Date")) {
														
														populateValuesForOperatorForTypeDate(dateBox1, dateBox2, 
																numberLb, value1, value2, numberbox1, numberbox2, null);
														
													}else if(type.equalsIgnoreCase("String")) {
														
														populateValuesForOperatorForTypeString(txtbox, cb1, cb2, formLb, value1, value2);
														
													}else if(type.equalsIgnoreCase("number")) {
														populateValuesForOperatorForTypeNumber(numberbox1, numberbox2, 
																txtbox, cb1, cb2, value1, value2);
														
													}
													break;
												}
												else if(retList != null && !retList.isEmpty()) {
													logger.debug("for ::"+segmentEnum.name()+" ITEM ::"+ruleItem.getLabel());
													for (SegmentEnum child : retList) {
														logger.debug("child is ::"+child.name());
														if(child.getToken() != null && constraint.equals(child.getToken())) {
															logger.debug("child.getToken() ::"+child.getToken());
															if(!isParentSelected){
																ruleItem.setSelected(true);
																Events.sendEvent("onSelect", ltyLb, null);
															}
															for (Listitem operatorItem : operatorLb.getItems()) {
																SegmentEnum operatorEnum = operatorItem.getValue();
																if(constraint.equals(operatorEnum.getToken())) {
																	operatorItem.setSelected(true);
																	Events.sendEvent("onSelect", operatorLb, null);
																	
																	if(type.equalsIgnoreCase("Date")) {
																		
																		populateValuesForOperatorForTypeDate(dateBox1, dateBox2, 
																				numberLb, value1, value2, numberbox1, numberbox2, operatorEnum);
																		
																	}else if(type.equalsIgnoreCase("String")) {
																		
																		populateValuesForOperatorForTypeString(txtbox, cb1, cb2, formLb, value1, value2);
																		
																	}else if(type.equalsIgnoreCase("number")) {
																		populateValuesForOperatorForTypeNumber(numberbox1, numberbox2,
																				txtbox, cb1, cb2, value1, value2);
																	}
																
	   													break;
																}
																
															}
															isSelected = true;
															break;
															
															
														}
														
														
													}//for
													
													
												}//if
												if(isSelected) break;
												
												
										
											if(isSelected) break;
										}
										}else if(dispLabel.equalsIgnoreCase("Loyalty Balance")) {

											for (Listitem ruleItem : ruleLb.getItems()) {
												SegmentEnum segmentEnum = ruleItem.getValue();
												logger.debug("ruleItem ::"+ruleItem.getLabel()+" segmentEnum.getColumnValue() "+segmentEnum.getColumnValue());
												
												List<SegmentEnum> childList = segmentEnum.getChidrenByParentEnum(segmentEnum);
												for(SegmentEnum ltyChild:childList) {
													if(ltyChild.getColumnName() != null && !ltyChild.getColumnName().isEmpty() ){
														if(ltyChild.getColumnName().equals(columnName)) {
															ruleItem.setSelected(true);
															Events.sendEvent("onSelect", ruleLb, null);
															isParentSelected = true;
															
															for(Listitem ltyRuleItem : ltyLb.getItems()) {
																SegmentEnum ltySegmentEnum = ltyRuleItem.getValue();
																if(ltySegmentEnum.getColumnName().equals(columnName)){
																	
																	if(ltySegmentEnum.name().contains("VALUE_CODES_TOTAL")) {
																		//logger.debug("ruleItem ::"+ltyLb.getSelectedItem().getLabel());
																		if(ltyRuleItem.getLabel().equalsIgnoreCase(value2)) {
																			if(!isLtySelected)
																			{
																				ltyRuleItem.setSelected(true);
																				segmentEnum=ltySegmentEnum;
																				Events.sendEvent("onSelect", ltyLb, null);
																				isLtySelected = true;
																			}
																	}else continue;
																}else {
																	if(!isLtySelected)
																	{
																		ltyRuleItem.setSelected(true);
																		segmentEnum=ltySegmentEnum;
																		Events.sendEvent("onSelect", ltyLb, null);
																		isLtySelected = true;
																	}
																}
															}
														/*}
														else continue;*/
														
													//}
												//}
												
													List<SegmentEnum> retList = segmentEnum.getChidrenByParentEnum(segmentEnum);
													
													if( ( retList == null || retList.isEmpty() ) && segmentEnum.getToken() != null 
															&& !segmentEnum.getToken().isEmpty() && constraint.equals(segmentEnum.getToken())) {
														
														if(!isParentSelected){
															ruleItem.setSelected(true);
															Events.sendEvent("onSelect", ruleLb, null);
														}
														if(type.equalsIgnoreCase("Date")) {
															
															populateValuesForOperatorForTypeDate(dateBox1, dateBox2, 
																	numberLb, value1, value2, numberbox1, numberbox2, null);
															
														}else if(type.equalsIgnoreCase("String")) {
															
															populateValuesForOperatorForTypeString(txtbox, cb1, cb2, formLb, value1, value2);
															
														}else if(type.equalsIgnoreCase("number")) {
															populateValuesForOperatorForTypeNumber(numberbox1, numberbox2, 
																	txtbox, cb1, cb2, value1, value2);
															
														}
														break;
													}
													else if(retList != null && !retList.isEmpty()) {
														logger.debug("for ::"+segmentEnum.name()+" ITEM ::"+ruleItem.getLabel());
														List<SegmentEnum> childEnumList = segmentEnum.getChidrenByParentEnum(segmentEnum);
														for (SegmentEnum child : childEnumList) {
															//List<SegmentEnum> finalEnumList = segmentEnum.getChidrenByParentEnum(child);
															//for(SegmentEnum finalEnum : finalEnumList) {
															//logger.debug("child is ::"+finalEnum.name());
															//if(finalEnum.getToken() != null && constraint.equals(finalEnum.getToken())) {
																if(child.getToken() != null && constraint.equals(child.getToken())) {
																logger.debug("child.getToken() ::"+child.getToken());
																if(!isParentSelected){
																	ruleItem.setSelected(true);
																	Events.sendEvent("onSelect", ruleLb, null);
																}
																for (Listitem operatorItem : operatorLb.getItems()) {
																	SegmentEnum operatorEnum = operatorItem.getValue();
																	if(constraint.equals(operatorEnum.getToken())) {
																		operatorItem.setSelected(true);
																		Events.sendEvent("onSelect", operatorLb, null);
																		
																		if(type.equalsIgnoreCase("Date")) {
																			
																			populateValuesForOperatorForTypeDate(dateBox1, dateBox2, 
																					numberLb, value1, value2, numberbox1, numberbox2, operatorEnum);
																			
																		}else if(type.equalsIgnoreCase("String")) {
																			
																			populateValuesForOperatorForTypeString(txtbox, cb1, cb2, formLb, value1, value2);
																			
																		}else if(type.equalsIgnoreCase("number")) {
																			populateValuesForOperatorForTypeNumber(numberbox1, numberbox2,
																					txtbox, cb1, cb2, value1, value2);
																		}
																		
																		/*if(numberLb.isVisible()) {
																			
																			int value = Integer.parseInt(value1)/Integer.parseInt(operatorEnum.getType2());
																			for (Listitem numberItem : numberLb.getItems()) {
																				
																				if(numberItem.getLabel().equals(value+Constants.STRING_NILL)) {
																					
																					numberItem.setSelected(true);
																					break;
																				}
																				
																			}//for
																				
																				
																		}//if numberLb visible
		*/															break;
																	}
																	
																}
																isSelected = true;
																break;
																
																
															}
															
															
														}//for
													}
														
													}
													}
													}
													else continue;
													if(isSelected) break;
													
													
											
												if(isSelected) break;
											}
										}
									}else{
									//for loyalty rule display
									for (Listitem ruleItem : ruleLb.getItems()) {
									SegmentEnum segmentEnum = ruleItem.getValue();
									logger.debug("ruleItem ::"+ruleItem.getLabel()+" segmentEnum.getColumnValue() "+segmentEnum.getColumnValue());
									
										if(segmentEnum.getColumnName() != null && !segmentEnum.getColumnName().isEmpty() ){
											if(segmentEnum.getColumnName().equals(columnName) ) {
												ruleItem.setSelected(true);
												Events.sendEvent("onSelect", ruleLb, null);
												isParentSelected = true;
											}
											else continue;
											
										}
										List<SegmentEnum> retList = segmentEnum.getChidrenByParentEnum(segmentEnum);
										if( ( retList == null || retList.isEmpty() ) && segmentEnum.getToken() != null 
												&& !segmentEnum.getToken().isEmpty() && constraint.equals(segmentEnum.getToken())) {
											
											if(!isParentSelected){
												ruleItem.setSelected(true);
												Events.sendEvent("onSelect", ruleLb, null);
											}
											if(type.equalsIgnoreCase("Date")) {
												
												populateValuesForOperatorForTypeDate(dateBox1, dateBox2, 
														numberLb, value1, value2, numberbox1, numberbox2, null);
												
											}else if(type.equalsIgnoreCase("String")) {
												
												populateValuesForOperatorForTypeString(txtbox, cb1, cb2, formLb, value1, value2);
												
											}else if(type.equalsIgnoreCase("number")) {
												populateValuesForOperatorForTypeNumber(numberbox1, numberbox2, 
														txtbox, cb1, cb2, value1, value2);
												
											}
											break;
										}
										else if(retList != null && !retList.isEmpty()) {
											logger.debug("for ::"+segmentEnum.name()+" ITEM ::"+ruleItem.getLabel());
											for (SegmentEnum child : retList) {
												logger.debug("child is ::"+child.name());
												if(child.getToken() != null && constraint.equals(child.getToken())) {
													logger.debug("child.getToken() ::"+child.getToken());
													if(!isParentSelected){
														ruleItem.setSelected(true);
														Events.sendEvent("onSelect", ruleLb, null);
													}
													for (Listitem operatorItem : operatorLb.getItems()) {
														SegmentEnum operatorEnum = operatorItem.getValue();
														if(constraint.equals(operatorEnum.getToken())) {
															operatorItem.setSelected(true);
															Events.sendEvent("onSelect", operatorLb, null);
															
															if(type.equalsIgnoreCase("Date")) {
																
																populateValuesForOperatorForTypeDate(dateBox1, dateBox2, 
																		numberLb, value1, value2, numberbox1, numberbox2, operatorEnum);
																
															}else if(type.equalsIgnoreCase("String")) {
																
																populateValuesForOperatorForTypeString(txtbox, cb1, cb2, formLb, value1, value2);
																
															}else if(type.equalsIgnoreCase("number")) {
																populateValuesForOperatorForTypeNumber(numberbox1, numberbox2,
																		txtbox, cb1, cb2, value1, value2);
															}
															
															/*if(numberLb.isVisible()) {
																
																int value = Integer.parseInt(value1)/Integer.parseInt(operatorEnum.getType2());
																for (Listitem numberItem : numberLb.getItems()) {
																	
																	if(numberItem.getLabel().equals(value+Constants.STRING_NILL)) {
																		
																		numberItem.setSelected(true);
																		break;
																	}
																	
																}//for
																	
																	
															}//if numberLb visible
*/															break;
														}
														
													}
													isSelected = true;
													break;
													
													
												}
												
												
											}//for
											
											
										}//if
										if(isSelected) break;
										
										
								
									if(isSelected) break;
								
									
									}
								}
									
								}//if
								
								
								
								
								
								
								break;
								
							}
							
							
						}
						//DropProfileComponent(true);
						//onDrop$profileAttributeDivId(event);
						
					}
					
					else if( retEnum.getItem().equalsIgnoreCase(SegmentEnum.INTERACTION_OPENS.getItem()) 
							|| retEnum.getItem().equalsIgnoreCase(SegmentEnum.INTERACTION_CLICKS.getItem())
							|| retEnum.getItem().equalsIgnoreCase(SegmentEnum.INTERACTION_RECEIVED.getItem())) {
						
						
						if(anotheRuleDivSetDiv.getParent() == null){
							
							vdiv.setDroppable("IntSegment");
							anotheRuleDivSetDiv.setParent(intAttributeDivId);
							updateAndLabel(intAttributeDivId);
							vdiv.appendChild(createDropORDiv("Drag Interaction Attribute here to create OR combination rule."));
							
						}
						
						
						for (Listitem item : interactionAttrLbId.getItems()) {
							
							/*if(tokenArr.length > 4) {
								
								value2 = tokenArr[4];
								
							}*/
							
							if(item.getLabel().equalsIgnoreCase(itemLabel)) {
								
								Div obj = createNewRuleDiv(item);
								obj.setParent(vdiv);
								updateORLabel(vdiv);
								
								List ruleDivChildren =  obj.getChildren();
								Listbox ruleLb = (Listbox)ruleDivChildren.get(2);
								//Listbox numberLb = (Listbox)ruleDivChildren.get(3);
								Listbox operatorLb = (Listbox)ruleDivChildren.get(5);
								
								// Generate Drop Div
								//vdiv.appendChild(createDropORDiv("Drag Profile Attribute here to create OR combination rule."));
							
								SegmentEnum actualConstraintEnum = SegmentEnum.getEnumByColumn(columnName);
								SegmentEnum parentRuleEnum = actualConstraintEnum.getParentEnum();
								
								for (Listitem ruleItem : ruleLb.getItems()) {
									
									SegmentEnum segmentEnum = ruleItem.getValue();
									logger.debug("==========="+segmentEnum.getDispLabel()+"==========="+parentRuleEnum.getDispLabel());
									if(parentRuleEnum.getDispLabel().equalsIgnoreCase(segmentEnum.getDispLabel())) {
										ruleItem.setSelected(true);
										Events.sendEvent("onSelect", ruleLb, null);
										for (Listitem operatorItem : operatorLb.getItems()) {
											SegmentEnum operatorEnum = operatorItem.getValue();
											if(constraint.equals(operatorEnum.getToken())) {
												operatorItem.setSelected(true);
												Events.sendEvent("onSelect", operatorLb, null);
											}
										}
										
										
										break;
									}else if(actualConstraintEnum.getDispLabel().equalsIgnoreCase(segmentEnum.getDispLabel())) {
										

										ruleItem.setSelected(true);
										/*Events.sendEvent("onSelect", ruleLb, null);
										for (Listitem operatorItem : operatorLb.getItems()) {
											SegmentEnum operatorEnum = operatorItem.getValue();
											if(constraint.equals(operatorEnum.getToken())) {
												operatorItem.setSelected(true);
												Events.sendEvent("onSelect", operatorLb, null);
											}
										}*/
										
										
										break;
									
										
									}
								
								}
								
								
								
								/*Datebox dateBox1 = (Datebox)ruleDivChildren.get(8);
								Datebox dateBox2 = (Datebox)ruleDivChildren.get(9);
								Decimalbox numberbox1 = (Decimalbox)ruleDivChildren.get(6);
								Decimalbox numberbox2 = (Decimalbox)ruleDivChildren.get(7);
								Textbox txtbox = (Textbox)ruleDivChildren.get(5);*/
								/*Combobox cb1 = (Combobox)ruleDivChildren.get(12);
								Combobox cb2 = (Combobox)ruleDivChildren.get(13);
								Listbox formLb = (Listbox)ruleDivChildren.get(11);*/
								/*Combobox cb1 = (Combobox)ruleDivChildren.get(11);
								Combobox cb2 = (Combobox)ruleDivChildren.get(12);
								Listbox formLb = (Listbox)ruleDivChildren.get(10);
								*/
								//Listbox campLb = (Listbox)ruleDivChildren.get(10);
									
								//make selection of the options lb
								//Date(c.created_date)|date:isToday|Today
								//make selection of the options lb
								//Date(c.created_date)|date:isToday|Today
								/*boolean isSelected = false;
								logger.debug("constraint ::"+constraint);
								if(constraint != null && children != null && !children.isEmpty()) {
									for (Listitem ruleItem : ruleLb.getItems()) {
										SegmentEnum segmentEnum = ruleItem.getValue();
										
										List<SegmentEnum> retList = segmentEnum.getChidrenByParentEnum(segmentEnum);
										if( ( retList == null || retList.isEmpty() ) && segmentEnum.getToken() != null 
												&& !segmentEnum.getToken().isEmpty() && constraint.equals(segmentEnum.getToken())) {
											
											ruleItem.setSelected(true);
											Events.sendEvent("onSelect", ruleLb, null);
											if(type.equalsIgnoreCase("Date")) {
												
												populateValuesForOperatorForTypeDate(dateBox1, dateBox2, 
														numberLb, value1, value2, numberbox1, numberbox2, null);
												
											}else if(type.equalsIgnoreCase("String")) {
												
												populateValuesForOperatorForTypeString(txtbox, cb1, cb2, formLb, value1, value2);
												
											}else if(type.equalsIgnoreCase("number")) {
												
												populateValuesForOperatorForTypeNumber(numberbox1, numberbox2, 
														txtbox, cb1, cb2, value1, value2);
												
											}
											break;
										}
										else if(retList != null && !retList.isEmpty()) {
											logger.debug("for ::"+segmentEnum.name());
											for (SegmentEnum child : retList) {
												logger.debug("child is ::"+child.name());
												if(child.getToken() != null && constraint.equals(child.getToken())) {
													logger.debug("child.getToken() ::"+child.getToken());
													ruleItem.setSelected(true);
													Events.sendEvent("onSelect", ruleLb, null);
													for (Listitem operatorItem : operatorLb.getItems()) {
														SegmentEnum operatorEnum = operatorItem.getValue();
														if(constraint.equals(operatorEnum.getToken())) {
															operatorItem.setSelected(true);
															Events.sendEvent("onSelect", operatorLb, null);
															
															if(type.equalsIgnoreCase("Date")) {
																
																populateValuesForOperatorForTypeDate(dateBox1, dateBox2, 
																		numberLb, value1, value2, numberbox1, numberbox2, operatorEnum);
																
															}else if(type.equalsIgnoreCase("String")) {
																
																populateValuesForOperatorForTypeString(txtbox, cb1, cb2, formLb, value1, value2);
																
															}else if(type.equalsIgnoreCase("number")) {
																
																populateValuesForOperatorForTypeNumber(numberbox1, numberbox2, 
																		txtbox, cb1, cb2, value1, value2);
															}*/
															
															/*if(numberLb.isVisible()) {
																	
																	int value = Integer.parseInt(value1)/Integer.parseInt(operatorEnum.getType2());
																	for (Listitem numberItem : numberLb.getItems()) {
																		
																		if(numberItem.getLabel().equals(value+Constants.STRING_NILL)) {
																			
																			numberItem.setSelected(true);
																			break;
																		}
																		
																	}//for
																		
																		
																}//if numberLb visible
															 */															break;
														/*}
														
													}
													isSelected = true;
													break;
													
													
												}
												
												
											}//for
											
											
										}//if
										if(isSelected) break;
										
										
										
										if(isSelected) break;
									}
									
									
									
								}//if
								
								
								
								
								
								
								break;*/
								
							}
							
							
						}
						
						
						
						
						
						
					}
					
					
				}//for
				
				
			}//if
		} catch (Exception e) {
			logger.error("Exception ",e);
		}
		
		
		
		
		
		
		
	}//addSegmentRules
	
	public String getMostParentDisplayLabel(SegmentEnum childEnum) {
		
		SegmentEnum parentEnum = childEnum.getParentEnum();
		
		if(parentEnum == null) return childEnum.getDispLabel();
		
		return getMostParentDisplayLabel(parentEnum);
		
		
		
	}
	
	
	public Div DropComponent(boolean isEdit, Listitem item, boolean isInnerDiv, Component parent) {
		
		
		
		
		
		Div vdiv = new Div();//this is the outer component
		vdiv.setDroppable("profileSegment");
		vdiv.setParent(parent);
		vdiv.setSclass("segment_parent_div");
		updateAndLabel(profileAttributeDivId);
		
		
		Div obj = createNewRuleDiv(item);
		obj.setParent(vdiv);
		updateORLabel(vdiv);

		// Generate Drop Div
		vdiv.appendChild(createDropORDiv("Drag Profile Attribute here to create OR combination rule."));
		
		vdiv.addEventListener("onDrop", myDropListener);
		
		return vdiv;
		
	}
	
	
	
	
	
	//private Set<Long> userIdsSet = GetUser.getUsersSet();
	
	public List<MailingList> getMailingLists() {
		
		List<MailingList> mlLists = null;
		try {
			//mlLists = mailingListDao.findListsByType(userIdsSet, "POS"); 
			
			mlLists = mailingListDao.findAllByCurrentUser(currentUser.getUserId());
			
		}catch (Exception e) {
			logger.error("** Exception : ",e);
		}
		return mlLists;
		
	}
	
	/*public void onSelect$listTypeRgId() {
		logger.debug("---just entered---");
		int count = dispMlLBoxId.getItemCount();
		for(int i=count-1; i>=0; i--){
			
			dispMlLBoxId.removeItemAt(i);
		}
		String type = (String)listTypeRgId.getSelectedItem().getValue();
		
		List list = mailingListDao.findListsByType(userIdsSet, type);
		
		if(list != null && list.size() > 0) {
			MailingList ml = null;
			Listitem li = null;
			
			
			
			for (Object object : list) {
				
				ml = (MailingList)object;
				li = new Listitem(ml.getListName());
				li.setValue(ml);
				li.setParent(dispMlLBoxId);
				
				
				
				
				
			}
			
		}
		
	}*/
}

package org.mq.marketer.campaign.controller.campaign;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampaignSchedule;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.MLCustomFields;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.SegmentRules;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.SubscriptionDetails;
import org.mq.marketer.campaign.controller.contacts.SegmentEnum;
import org.mq.marketer.campaign.dao.CampaignScheduleDao;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.CampaignsDaoForDML;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.MLCustomFieldsDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.SegmentRulesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.CampaignStepsEnum;
import org.mq.marketer.campaign.general.ClickHouseSalesQueryGenerator;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.LBFilterEventListener;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.RightsEnum;
import org.mq.marketer.campaign.general.SalesQueryGenerator;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataIntegrityViolationException;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;

@SuppressWarnings( { "serial", "unchecked" })
public class CampMlistController extends GenericForwardComposer  {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Label selMlLblId;
	private Listbox dispMlLBoxId,dispsegmentsLbId;
	private Radiogroup listsTypeRgId;
	private Vbox segmentRulesVbId;
	private Listbox segRuleOptionLbId;
	private Session session = null;
	private Toolbarbutton addRuleToolbarId;
	private Button saveAsDraftBtnId,saveBtnId;
	private Div selectedListDivId;
	
	private MailingListDao mailingListDao;
	private MLCustomFieldsDao mlCFDao;
	private ContactsDao contactsDao;
	private UserActivitiesDao userActivitiesDao;
	private UserActivitiesDaoForDML userActivitiesDaoForDML;
	private SegmentRulesDao segmentRulesDao;
	private CampaignsDao campaignsDao;
	private CampaignsDaoForDML campaignsDaoForDML;
	private CampaignScheduleDao campaignScheduleDao;
	
	private int segmentCount = 0;
	private int maxSegmentCount = 2;
	
	private Long currentUserId;
	private Users currUser;
	//private Set<Long> userIdsSet = GetUser.getUsersSet();
	
	private Set<Long> listIdsSet; 
	private Set<Long> segmentIdsSet; 
	
	private Campaigns campaign = null;
	private String isEdit = null;
	
	private String[] str_operators  = {"is","starts with","ends with","contains","does not contain"};
	private String[] numeric_operators  = {"=",">","<",">=","<=","!=", "between"};
	private String[] date_operators  = {"is","before","after","between"};
	private String[] boolean_operators  = {"is"};
	/*private String[][] fieldArray = 
		new String[][]{{"Email","First Name","Last Name","Address One","Address Two","City","State","Country","Pin","Phone"},
			{"email_id","first_name","last_name","address_one","address_two","city","state","country","pin","phone"}};*/
	/*private String[][] fieldArray = 
			new String[][]{{"Email","First Name","Last Name","Address One","Address Two","City","State","Country","ZIP","Phone"},
				{"email_id","first_name","last_name","address_one","address_two","city","state","country","zip","phone"}};
*/
	private List<ListSegmentRule> totalFieldsList = new ArrayList();
	//private List<ListSegmentRule> defaultFieldsList = new ArrayList();
	
	
	public CampMlistController() {
		try {
			this.session = Sessions.getCurrent();
			campaign = (Campaigns) session.getAttribute("campaign");
			isEdit = (String)session.getAttribute("editCampaign");
			this.mlCFDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
			this.contactsDao = (ContactsDao)SpringUtil.getBean("contactsDao");
			this.mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
			this.campaignScheduleDao = (CampaignScheduleDao)
					SpringUtil.getBean("campaignScheduleDao");
			segmentRulesDao = (SegmentRulesDao)SpringUtil.getBean("segmentRulesDao");
			campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
			campaignsDaoForDML = (CampaignsDaoForDML)SpringUtil.getBean("campaignsDaoForDML");
			currentUserId = GetUser.getUserId();
			currUser = GetUser.getUserObj();
		
			if(isEdit!=null){
				if(campaign==null){
					//Redirect.goTo(PageListEnum.CAMPAIGN_VIEW);
					Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
				}
			}
		
			Utility.breadCrumbFrom(2);
			
			userActivitiesDao = (UserActivitiesDao) SpringUtil.getBean("userActivitiesDao");
			/*if(userActivitiesDao != null) {
				userActivitiesDao.addToActivityList(ActivityEnum.VISIT_CAMPAIGN_MLIST,GetUser.getUserObj());
			}*/
			
			userActivitiesDaoForDML = (UserActivitiesDaoForDML) SpringUtil.getBean("userActivitiesDaoForDML");
			if(userActivitiesDaoForDML != null) {
				userActivitiesDaoForDML.addToActivityList(ActivityEnum.VISIT_CAMPAIGN_MLIST,GetUser.getLoginUserObj());
			}
			
			
			
		 
			maxSegmentCount = Integer.parseInt(PropertyUtil.getPropertyValue("maxSegmentRules"));
			
			//added for sharing
			listIdsSet = (Set<Long>)session.getAttribute(Constants.LISTIDS_SET);
			segmentIdsSet = (Set<Long>)session.getAttribute(Constants.SEGMENTIDS_SET);
			
		} catch (Exception e) {
			logger.error("Exception **",e);
		}
	}

	/*public void init(Label selMlLblId, Listbox dispMlLBoxId , Button saveAsDraftBtnId,
			Button saveBtnId, Radiogroup listsTypeRgId,Vbox segmentRulesVbId,
			Listbox segRuleOptionLbId, Toolbarbutton addRuleToolbarId) {
	*/
	public void doAfterCompose(org.zkoss.zk.ui.Component comp) throws Exception {
		
	  super.doAfterCompose(comp);	
	  
	  String style = "font-weight:bold;font-size:15px;color:#313031;" +
					 "font-family:Arial,Helvetica,sans-serif;align:left";
	  PageUtil.setHeader("Create Email (Step 2 of 6)","",style,true);
	  
	  //PageUtil.setHeader("Create Email (Step 2 of 6)","","",true);
		
		/*this.selMlLblId = selMlLblId;
		this.dispMlLBoxId = dispMlLBoxId;
		this.listsTypeRgId = listsTypeRgId;
		this.segmentRulesVbId = segmentRulesVbId;
		this.segRuleOptionLbId = segRuleOptionLbId;
		this.addRuleToolbarId = addRuleToolbarId;*/
		
 		MessageUtil.clearMessage();
 		
 		List<MailingList> mlList = getMailingLists();
 		if (mlList != null && mlList.size() < 1){
 			MessageUtil.setMessage("Please create a contact list first so that you can send to that list.",
 					"color:red", "TOP");
 			return;
 		}
 		
 		Listitem listItem;
 		
 		if(mlList != null) {
	 		for (MailingList mailingList : mlList) {
	 			listItem = new Listitem();
	 			listItem.setValue(mailingList);
	 			listItem.setLabel(mailingList.getListName());
	 			listItem.setParent(dispMlLBoxId); 
			}
 		}
 		List<SegmentRules> segList = getSegmentRules(); 
 		if(segList != null) {
 		
	 		for (SegmentRules segRule : segList) {
	 			listItem = new Listitem();
	 			listItem.setValue(segRule);
	 			listItem.setLabel(segRule.getSegRuleName());
	 			listItem.setParent(dispsegmentsLbId); 
			}
 		
 		}
 		
	/*	ListSegmentRule lstSegRule;
	
		for(int i=0;i<fieldArray[0].length;i++) {
			
			lstSegRule = new ListSegmentRule(fieldArray[0][i], ListSegmentRule.STRING, fieldArray[1][i]);
			
			if( fieldArray[0][i].trim().equalsIgnoreCase("Pin") || 
				fieldArray[0][i].trim().equalsIgnoreCase("Phone") ) {
				lstSegRule.setDataType(ListSegmentRule.NUMBER);
			}
			defaultFieldsList.add(lstSegRule);
		}*/
		
		//logger.debug("defaultFieldsList :: "+defaultFieldsList.size()+" totalFieldsList :: "+totalFieldsList.size());
		if(campaign!=null) {
			Set mlSet = campaign.getMailingLists();
			if(mlSet!=null){
				String listType = campaign.getListsType();
				List<Listitem> lbList = dispMlLBoxId.getItems();
				String listNamesStr="";
				for(Object obj:mlSet){
					
					MailingList ml = (MailingList)obj;
					for(Listitem li:lbList){
						
						MailingList lbml = (MailingList)li.getValue();
						if(lbml.getListName().equals(ml.getListName())){
							if(listType.equalsIgnoreCase("Total")) dispMlLBoxId.addItemToSelection(li);//if listType=List
							if(listNamesStr.length() != 0) listNamesStr+=",";
							listNamesStr += lbml.getListName() ;
						}
					}
				} //for
				//String listType = campaign.getListsType();
				logger.info("listType--"+listType);
				if( listType == null || listType.equalsIgnoreCase("Total") ) {
					selMlLblId.setValue(listNamesStr);
					onSelect$dispMlLBoxId();
				}else if(listType.startsWith("Segment")) { //Added for APP-3432
					configurelistTabId.setSelectedIndex(1);
					onCheck$configurelistTabId();
					enableSegment();
					onSelect$dispsegmentsLbId();
				}
				
				//getMListCFData();
				onSelect$dispMlLBoxId();
			}//if
			//parseRules();//disabeled after modifications done for POS
			//enableSegment(); 
			
			
		}//if
		
		logger.debug(" isEdit : "+ isEdit);
		
		if(isEdit!=null){
			
			if(isEdit.equalsIgnoreCase("edit")){
				saveAsDraftBtnId.setVisible(false);
				saveBtnId.setLabel("Save");
			}
			parseRules();
			enableSegment(); 
			
		}
		
		try{
 			
 			Set<String> userRoleSet = (Set<String>)session.getAttribute("userRoleSet");
 		//	logger.debug("userRoleSet ::"+userRoleSet);
 			
 			if(userRoleSet != null) {
 				createNewSegDivId.setVisible(userRoleSet.contains(RightsEnum.MenuItem_CreateSegment_VIEW));
 				
 				/*if(!userRoleSet.contains(Constants.ROLE_USER_BASIC)) {
 					createNewSegDivId.setVisible(true);
 					
 				}//if
 				*/
 				
 			}//if
 			
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception ::", e);
		}
		
		
		
		LBFilterEventListener.lbFilterSetup(dispMlLBoxId);
		LBFilterEventListener.lbFilterSetup(dispsegmentsLbId);
		
		
	}
		
	public void onChanging$searchBoxId(InputEvent event) {
		int count=dispsegmentsLbId.getItemCount();
			for (; count > 0; count--) {
				dispsegmentsLbId.removeItemAt(count - 1);
			}
		String key = event.getValue();
		logger.info("got the key ::" + key);
		List<SegmentRules> segList = segmentRulesDao.searchBy(segmentIdsSet,key);
 		if(segList != null) {
 			
 			Listitem listItem=null;
	 		for (SegmentRules segRule : segList) {
	 			listItem = new Listitem();
	 			listItem.setValue(segRule);
	 			listItem.setLabel(segRule.getSegRuleName()); 
	 			listItem.setParent(dispsegmentsLbId); 
			}	
		}
 		enableSegment(); 
	}
	public void onChanging$searchBoxIdforlist(InputEvent event){
 			int count=dispMlLBoxId.getItemCount();
 			for (; count > 0; count--) {
 				dispMlLBoxId.removeItemAt(count - 1);
 			}
		String key = event.getValue();
          Listitem listItem;
          List<MailingList> mlList=  mailingListDao.searchMailBy(listIdsSet,key);
          if(mlList != null) {
	 		for (MailingList mailingList : mlList) {
	 			listItem = new Listitem();
	 			listItem.setValue(mailingList);
	 			listItem.setLabel(mailingList.getListName());
	 			listItem.setParent(dispMlLBoxId); 
			}
 		}
 		if(campaign!=null) {
			Set mlSet = campaign.getMailingLists();
			if(mlSet!=null){
				
				List<Listitem> lbList = dispMlLBoxId.getItems();
				String listNamesStr="";
				for(Object obj:mlSet){
					
					MailingList ml = (MailingList)obj;
					for(Listitem li:lbList){
						
						MailingList lbml = (MailingList)li.getValue();
						if(lbml.getListName().equals(ml.getListName())){
							dispMlLBoxId.addItemToSelection(li);
							if(listNamesStr.length() != 0) listNamesStr+=",";
							listNamesStr += lbml.getListName() ;
						}
					}
				} //for
				selMlLblId.setValue(listNamesStr);
				
				//getMListCFData();
				try {
					onSelect$dispMlLBoxId();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}//if
			//parseRules();//disabeled after modifications done for POS
			//enableSegment(); 
			
			
		}
	}
	private Div createNewSegDivId;
	//public void onCheck(int selectedIndex){
	public void onCheck$listsTypeRgId() throws Exception {
		int selectedIndex = listsTypeRgId.getSelectedIndex();
		if(selectedIndex == 0) {
			segmentRulesVbId.setVisible(false);
			addRuleToolbarId.setVisible(segmentRulesVbId.isVisible());
		}else {
			if(segmentRulesVbId.getChildren().size() == 1) {
				addRules(null);
			}
			segmentRulesVbId.setVisible(true);
			addRuleToolbarId.setVisible(segmentRulesVbId.isVisible());
			
		}
	}
	
	private void parseRules(){
		logger.debug("-- just entered--");
		try{
			String segRule = campaign.getListsType();
			logger.debug("Segment Rule :"+ segRule);
			
			configurelistTabId.setSelectedIndex(0);
			
			if(segRule == null || segRule.equalsIgnoreCase("Total")) {
				listsTypeRgId.setSelectedIndex(0);
				return;
			}
			
			if(!segRule.startsWith("Segment")) {//added after POS
				listsTypeRgId.setSelectedIndex(1);	
				segmentRulesVbId.setVisible(true);
				String[] rowsArr = segRule.split("\\|\\|");
				String[] columnsArr; 
				
				
				columnsArr = rowsArr[0].split(":");
				if(columnsArr.length > 0) {
					
					if(columnsArr[0].trim().equalsIgnoreCase("Any") ) { 
						segRuleOptionLbId.setSelectedItem(segRuleOptionLbId.getItemAtIndex(0));
					} 
					else {
						segRuleOptionLbId.setSelectedItem(segRuleOptionLbId.getItemAtIndex(1));
					}
				}
				
				for(int i=1;i<rowsArr.length;i++) {
					addRules(rowsArr[i]);
				} // outer for
				addRuleToolbarId.setVisible(true);
			}
		}catch(Exception e ){
			logger.error("", (Throwable)e);
		}
	}
	
	private void createItemsForOperatorsLb(Listbox operatorsLb,String type,String constraint) {
		
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
	
	public List getMailingLists() {
		List mlLists = null;
		try {
			//mlLists = mailingListDao.findAllByUser(userIdsSet); 
			
			mlLists = mailingListDao.findByIds(listIdsSet);
		}catch (Exception e) {
			logger.error("** Exception : " + e);
		}
		return mlLists;
	}
	
	
	/*public void filterSegmentFields() {
		// 0 -->Any
		// 1 -->All
		if(segRuleOptionLbId.getSelectedIndex() == 1 && dispMlLBoxId.getSelectedCount() > 1) {
			totalFieldsList.clear();
			totalFieldsList.addAll(defaultFieldsList);
			resetSegmentFields();
		}
	}*/
	//******************added for POS***********************************
	private Div dispRuleDivId;
	private Label selRuleLblId,selRuleListLblId;
	
	
	public List getSegmentRules() {
		List segLists = null;
		try {
			//segLists = segmentRulesDao.findByUser(userIdsSet); 
			
			
			segLists = segmentRulesDao.findByIds(segmentIdsSet);
		}catch (Exception e) {
			logger.error("** Exception : " + e);
		}
		return segLists;
	}
	
	
	public void onSelect$dispsegmentsLbId() {
		
		Set<Listitem> selRules = dispsegmentsLbId.getSelectedItems();
		String segRuleIds= "";
		String listIdsStr = "";
		String dispSegRule = "";
		listnamesStr = "";
		
		if(selRules.size() == 0 ) {
			
			selRuleLblId.setValue(dispSegRule);
			selRuleListLblId.setValue(listnamesStr);
			return;
		}
		
		for (Listitem listitem : selRules) {
			
			SegmentRules segmentRule = (SegmentRules)listitem.getValue();

			
			if(listIdsStr.length() > 0) listIdsStr += ",";
			listIdsStr += segmentRule.getSegmentMlistIdsStr();
			
			String segRule = segmentRule.getSegRule();
			if(segRule != null) {
				
				if(dispSegRule.length() > 0) dispSegRule += "& \n";
				dispSegRule += dispRule(segRule);
				
			}
			
			
		}//for
		
		selRuleLblId.setValue(dispSegRule);
		List<MailingList> mlList = mailingListDao.findByIds(listIdsStr);
		
		if(mlList == null) {
			
			MessageUtil.setMessage("Configured segment's target list no longer exists. You might have deleted it.", "color:red;");
			configurelistTabId.setSelectedIndex(1);
			onCheck$configurelistTabId();
			return ;
			
			
		}
		
		for (MailingList mailingList : mlList) {
			
			if(listnamesStr == null) listnamesStr = mailingList.getListName();
			
			if(listnamesStr != null && listnamesStr.length() > 0) listnamesStr += ", ";
			listnamesStr += mailingList.getListName();
			
		}//for
		
		
		
		selRuleListLblId.setValue(listnamesStr);
		
		if(selRuleLblId.getValue().length() != 0) {
			dispRuleDivId.setVisible(true);
		}else{
			dispRuleDivId.setVisible(false);
		}
		
		
	}//onSelect$dispsegmentsLbId()
	
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
	
	public void onClick$createNewSegAnchId() {
		
		Redirect.goTo(PageListEnum.CONTACT_MANAGE_SEGMENTS);
		
	}
	
	String listnamesStr;
	public boolean saveSegRule() {
		
		int num = dispsegmentsLbId.getItemCount();
		if(num == 0) {
			MessageUtil.setMessage(
					"Please create a Segment first \n so that you can configure" +
					"it to your campaigns.","color:red", "TOP");
			return false;
			
		}
		
		int mlcount = dispsegmentsLbId.getSelectedCount();
		
		if (mlcount == 0) {
			MessageUtil.setMessage("Select at least one segment.", "color:red","TOP");
			return false;
		}
		
		Set<Listitem> selRules = dispsegmentsLbId.getSelectedItems();
		String segRuleIds= "";
		String listIdsStr = "";
		for (Listitem listitem : selRules) {
			
			SegmentRules segmentRule = (SegmentRules)listitem.getValue();

			
			if(segRuleIds.length() > 0) {
				
				segRuleIds += ",";
				
			}//if
			segRuleIds += segmentRule.getSegRuleId().longValue();
			
			if(listIdsStr.length() > 0) listIdsStr+= ",";
			listIdsStr += segmentRule.getSegmentMlistIdsStr();
			
		}//for
		
		
		
		
		
		//SegmentRules segmentRule = (SegmentRules)dispsegmentsLbId.getSelectedItem().getValue();
		//campaign.setListsType("Segment"+Constants.DELIMETER_COLON+segmentRule.getSegRuleId());
		campaign.setListsType("Segment"+Constants.DELIMETER_COLON+segRuleIds);
		
		//can avoid setting the set of mailing lists to the campaign
		Set<MailingList> mlSet = new HashSet<MailingList>();
		
		listnamesStr = "";
		
		
		List<MailingList> mlList = mailingListDao.findByIds(listIdsStr);
		
		if(mlList == null) {
			
			MessageUtil.setMessage("Configured segment's target list no longer exists. You might have deleted it.", "color:red;");
			configurelistTabId.setSelectedIndex(1);
			onCheck$configurelistTabId();
			return false;
			
			
		}
		
		for (MailingList mailingList : mlList) {
			
			mlSet.add(mailingList);
			if(listnamesStr == null) listnamesStr = mailingList.getListName();
			
			if(listnamesStr != null && listnamesStr.length() > 0) listnamesStr += ", ";
			listnamesStr += mailingList.getListName();
			
		}//for
		
		
		//mlSet.add(segmentRule.getMailingList());
		
		campaign.setMailingLists(mlSet);
		
		if(!otherCampSettings()) {
			return false;
		}
		
		return true;
		
	}
	
	public void enableSegment() {
		
		
		String segRule = campaign.getListsType();
		logger.debug("Segment Rule :"+ segRule);
		if(segRule != null && !segRule.equalsIgnoreCase("Total") && segRule.startsWith("Segment")) {
			
			String segRuleId = segRule.split(""+Constants.DELIMETER_COLON)[1];
			List<SegmentRules> segmenRules = segmentRulesDao.findById(segRuleId);
			if(segmenRules == null) {
				
				MessageUtil.setMessage("Configured segment no longer exists. You might have deleted it.", "color:red;");
				configurelistTabId.setSelectedIndex(1);
				onCheck$configurelistTabId();
				return ;
				
				
			}
			
			String segRuleIds= "";
			String listIdsStr = "";
			String dispSegRule = "";
			listnamesStr = "";
			
			for (SegmentRules segmentRules : segmenRules) {
				
				if(segmentRules == null) {
					
					MessageUtil.setMessage("One of the segments configured to this campaign no longer exists. You might have deleted it.", "color:red;");
					configurelistTabId.setSelectedIndex(1);
					onCheck$configurelistTabId();
					continue ;
					
				}
				
				for(int i=0; i<dispsegmentsLbId.getItemCount(); i++){
					
					if(dispsegmentsLbId.getItemAtIndex(i).getLabel().equals(segmentRules.getSegRuleName()) ){
						dispsegmentsLbId.addItemToSelection(dispsegmentsLbId.getItemAtIndex(i));
						
						
					}//if
					
				}//for
				
				if(dispSegRule.length() > 0) dispSegRule += "& \n";
				dispSegRule += dispRule(segmentRules.getSegRule());
				
				if(listIdsStr.length() > 0) listIdsStr += ",";
				listIdsStr += segmentRules.getSegmentMlistIdsStr();
				
				
			}//for
			
			selRuleLblId.setValue(dispSegRule);
			
			
			//SegmentRules segmentRule = segmentRulesDao.findById(segRuleId);
			
			/*if(segRuleId == null) {
				
				MessageUtil.setMessage("Configured segment no longer exists. You might have deleted it.", "color:red;");
				configurelistTabId.setSelectedIndex(1);
				onCheck$configurelistTabId();
				return ;
				
			}*/
			
			//if(segmentRule != null) {
				
				/*for(int i=0; i<dispsegmentsLbId.getItemCount(); i++){
					
					if(dispsegmentsLbId.getItemAtIndex(i).getLabel().equals(segmentRule.getSegRuleName()) ){
						dispsegmentsLbId.setSelectedIndex(i);
						break;
						
					}//if
					
				}//for
*/			
			
			
			
			List<MailingList> mlList = mailingListDao.findByIds(listIdsStr);
			
			if(mlList == null) {
				
				logger.debug("Configured Segment's target list is no longer exist.You might have deleted it.");
				configurelistTabId.setSelectedIndex(1);
				onCheck$configurelistTabId();
				return ;
				
				
			}
			
			for (MailingList mailingList : mlList) {
				
				if(listnamesStr == null) listnamesStr = mailingList.getListName();
				
				if(listnamesStr != null && listnamesStr.length() > 0) listnamesStr += ", ";
				listnamesStr += mailingList.getListName();
				
			}//for
			
			selRuleListLblId.setValue(listnamesStr);
			
			
			if(selRuleLblId.getValue().length() != 0) {
				dispRuleDivId.setVisible(true);
			}else{
				dispRuleDivId.setVisible(false);
			}
			
			
			configurelistTabId.setSelectedIndex(1);
			onCheck$configurelistTabId();
		
			//}
			
			
		}
		
	}
	
	
	
	
	
	//**********************************************************
	
	
	
	
	
	
	//public void getMListCFData() {
	public void onSelect$dispMlLBoxId() throws Exception {
		
		logger.debug("-- just entered --");
		
		
		
		
		Set<Listitem> selectedItems = dispMlLBoxId.getSelectedItems();
		
		/*totalFieldsList.clear();
		totalFieldsList.addAll(defaultFieldsList);*/
		String listNamesStr="";
		
		if(segRuleOptionLbId.getSelectedIndex() == 0 || dispMlLBoxId.getSelectedCount() == 1) {
			for (Listitem li : selectedItems) {
	
				MailingList mailingList = (MailingList) li.getValue();			
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
			}// outer for
		}// if
		
		for (Listitem li : selectedItems) {
			
			MailingList mailingList = (MailingList) li.getValue();			
			if(listNamesStr.length() != 0) listNamesStr+=",";
			listNamesStr += mailingList.getListName() ;
		}
		
		selMlLblId.setValue(listNamesStr);
		
		//visibility div of Selected list
		if(selMlLblId.getValue().length() != 0) {
			selectedListDivId.setVisible(true);
		}else{
			selectedListDivId.setVisible(false);
		}
		
		
		
	/*	List rows = segmentRulesVbId.getChildren();
		Listitem li;
		ListSegmentRule lstSegRule;
		Listbox fieldsList;
		
		for(int i=1;i<rows.size();i++) {
			fieldsList = (Listbox)(((Hbox)rows.get(i)).getChildren().get(0));
			//fieldsList.setSelectedIndex(0);
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
		}*/
		
		/*resetSegmentFields();
		filterSegmentFields();*/
	}
	
	/*private void resetSegmentFields() {
		
		List rows = segmentRulesVbId.getChildren();
		Listitem li;
		ListSegmentRule lstSegRule;
		Listbox fieldsList;
		Listbox operatorsList;
		
		for(int i=1;i<rows.size();i++) {
			fieldsList = (Listbox)(((Hbox)rows.get(i)).getChildren().get(0));
			//fieldsList.setSelectedIndex(0);
			Components.removeAllChildren(fieldsList);
			for(int j=0;j<totalFieldsList.size();j++) {
				lstSegRule = (ListSegmentRule)totalFieldsList.get(j);
				li = new Listitem(lstSegRule.toString());
				li.setValue(lstSegRule);
				li.setParent(fieldsList);
			}
			fieldsList.setSelectedIndex(0);
			fieldsList.invalidate();
			operatorsList = (Listbox)(((Hbox)rows.get(i)).getChildren().get(1));
			createItemsForOperatorsLb(operatorsList, ListSegmentRule.STRING, null);
		}

	}*/
	
	public void onSelect$segRuleOptionLbId() throws Exception {
		//logger.debug("called for :: "+i);
		onSelect$dispMlLBoxId();
	}
	
	@SuppressWarnings("deprecation")
	public void addRules(String ruleStr) {
		
		logger.debug(" Rule :"+ruleStr);
		if( segmentCount == maxSegmentCount ) {
			return;
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
			data2 = (columnsArr.length>3)?columnsArr[3]:null;
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
		
		
		
		//ruleHb.setStyle("padding:5px");
//		ruleHb.setValign("center");
		final Listbox fieldsLb = new Listbox();
		fieldsLb.setWidth("120px");
		Listitem li = null;
		ListSegmentRule lstSegRule = null;
		
		if(ruleStr == null) { //Genral fields
			for(int j=0;j<totalFieldsList.size();j++) {
				lstSegRule = (ListSegmentRule)totalFieldsList.get(j);
				li = new Listitem(lstSegRule.toString());
				li.setValue(lstSegRule);
				li.setParent(fieldsLb);
			}
			fieldsLb.setSelectedIndex(0);
		}
		else { //general fields and  customFields
		for(int j=0;j<totalFieldsList.size();j++) {
			lstSegRule = (ListSegmentRule)totalFieldsList.get(j);
			li = new Listitem(lstSegRule.toString());
			li.setValue(lstSegRule);
			li.setParent(fieldsLb);
			
			if(fieldNameStr != null) {
				//logger.debug("===========lstSegRule::"+lstSegRule.getColumnName()+"=============fieldNameStr::"+fieldNameStr);
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
		}//if
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
		deleteIcon.setImage("/img/icons/icon_x.png");
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
				segmentRulesVbId.removeChild(hb);
				if(segmentRulesVbId.getChildren().size() <= 1) {
					segmentRulesVbId.setVisible(false);
					addRuleToolbarId.setVisible(false);
					listsTypeRgId.setSelectedIndex(0);
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
		//return ruleHb;
	}
	
	public void onClick$addRuleToolbarId() throws Exception {
		addRules(null);
	}
	
	
	
	
	
	public boolean saveMlist() {
		logger.debug("-- just entered --");
		try {
			CampaignsDao campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
			int num = dispMlLBoxId.getItemCount();
			if (num == 0) {
				MessageUtil.setMessage(
						"Please create a contact list first so that you can send" +
						" your campaigns to it.","color:red", "TOP");
				Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
				return false;
			}
			int mlcount = dispMlLBoxId.getSelectedIndex();
			if (mlcount == -1) {
				MessageUtil.setMessage("Select at least one list.", "color:red","TOP");
				return false;
			}
			
			Set lists = dispMlLBoxId.getSelectedItems();
			logger.debug(" No of Mailing lists selected :"+lists.size());
			Set mlSet = new HashSet();
			Listitem li;
			MailingList ml = null;
			StringBuffer segmentRuleSB = null;
			
			
			if(!segmentRulesVbId.isVisible()) {
				//TODO check whether All or Total
				segmentRuleSB = new StringBuffer("Total");
			}else {
				segmentRuleSB = 
					new StringBuffer(segRuleOptionLbId.getSelectedItem().getLabel()+":");
			}
			
			logger.debug(" Selected Option : "+ segmentRuleSB.toString());
			
			String listIdsStr = "";
			/*long emailCount = 0;
			long totalCount = 0;
			long unpurgedCount = 0;*/
			
			for (Object obj : lists) {
				li = (Listitem) obj;
				ml = (MailingList) li.getValue();
				
				if(listIdsStr.length() != 0) { 
					listIdsStr+=",";
				}	
				listIdsStr += ml.getListId();
				mlSet.add(ml);
			}
			
			logger.debug("No of mailing lists :" + mlSet.size());
			
			if(segmentRulesVbId.isVisible()) {
				List rulesList = segmentRulesVbId.getChildren();
				segmentRuleSB.append(listIdsStr);
				logger.debug(" Rule Before adding fields : "+segmentRuleSB.toString());
				
				String tempStr;
				boolean isValid = true;
				if(rulesList.size() <= 1) {
					MessageUtil.setMessage("At least one segment rule must be selected.", "color:red", "TOP");
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
				if(!isValid) {
					return false;
				}
			}
			logger.info(" Rule after preparation :" + segmentRuleSB.toString());
			campaign.setListsType(segmentRuleSB.toString());
			
			
			
			campaign.setMailingLists(mlSet);
			
			if(!otherCampSettings()) {
				
				return false;
				
				
				
			}
			
		} catch (Exception e) {
			logger.error("** Exception : " ,(Throwable) e);
			return false;
		}
		return true;
	}
	
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
					String valueStr = tb1.getValue().trim();
					if(valueStr.length() <= 0) {
						MessageUtil.setMessage("Value should not be left empty.", "color:red", "TOP");
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
						MessageUtil.setMessage("First date must be earlier than second date. ", "color:red", "TOP");
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
	
	public boolean otherCampSettings() {
		
		
		long emailCount = 0;
		long totalCount = 0;
		long unpurgedCount = 0;

		boolean isSomethingWrong = false;
		
		campaign.setCouponFlag(false);
		MessageUtil.clearMessage();
		String segmentStr = campaign.getListsType();
		//logger.debug("segmentStr ::"+segmentStr);
		
		Set<MailingList> mlSet = campaign.getMailingLists();
		totalCount = contactsDao.getAllEmailCount(mlSet);
		unpurgedCount = contactsDao.getAllUnpurgedCount(mlSet);
		String message = "";
		
		if(segmentStr.startsWith("Segment")) {
			
			String segRuleIds = segmentStr.split(""+Constants.DELIMETER_COLON)[1];
			List<SegmentRules> segmenRules = segmentRulesDao.findById(segRuleIds);
			
			if(segmenRules == null) {
				
				MessageUtil.setMessage("Configured segment no longer exists. You might have deleted it.", "color:red;");
				configurelistTabId.setSelectedIndex(1);
				onCheck$configurelistTabId();
				return false;
				
				
			}//if
			String tempQry = "";
			logger.debug("size ::"+segmenRules.size());
			for (SegmentRules segmentRules : segmenRules) {
				
				Set<MailingList> mlistSet = new HashSet<MailingList>();
				List<MailingList> mlList = mailingListDao.findByIds(segmentRules.getSegmentMlistIdsStr());
				if(mlList == null) {
					logger.debug("continue");
					continue;
				}
				
				mlistSet.addAll(mlList);
				long mlsbit = Utility.getMlsBit(mlistSet);
				//ClickHouse changes
				if(!currUser.isEnableClickHouseDBFlag())
					segmentStr = SalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(),false, Constants.SEGMENT_ON_EMAIL,mlsbit);
				else
					segmentStr = ClickHouseSalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(),false, Constants.SEGMENT_ON_EMAIL,mlsbit);

				if(segmentStr == null) {
					MessageUtil.setMessage("Selected invalid segmentation rules.", "color:red", "TOP");
					continue;
				}
				if(SalesQueryGenerator.CheckForIsLatestCamapignIdsFlag(segmentRules.getSegRule())) {
					String csCampIds = SalesQueryGenerator.getCamapignIdsFroFirstToken(segmentRules.getSegRule());
					
					if(csCampIds != null ) {
						String crIDs = Constants.STRING_NILL;
						//CampaignsDao campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
						List<Object[]> campList = campaignsDao.findAllLatestSentCampaignsBySql(segmentRules.getUserId(), csCampIds);
						if(campList != null) {
							for (Object[] crArr : campList) {
								
								if(!crIDs.isEmpty()) crIDs += Constants.DELIMETER_COMMA;
								crIDs += ((Long)crArr[0]).longValue();
								
							}
						}
						
						segmentStr = segmentStr.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH, ("AND cr_id in("+crIDs+")"));
					}
				}
				
				
				logger.info(" Generated Query :"+segmentStr);
				if(!currUser.isEnableClickHouseDBFlag()) {
					if(tempQry.length() > 0) tempQry += " UNION ";
				}else {
					if(tempQry.length() > 0) tempQry += " UNION ALL ";
				}
				
				tempQry += segmentStr;
//				contactsDao.insertSegmentedContacts(tempQry);
				
				
				
				
			}//for
			
			if(!currUser.isEnableClickHouseDBFlag())
				emailCount = contactsDao.getSegmentedContactsCount(tempQry);
			else
				emailCount = contactsDao.getSegmentedContactsCountFromCH(tempQry);
			/*
			if(unpurgedCount > 0) {
				Messagebox.show("Selected mailing list(s) have "+ unpurgedCount +" unpurged contacts.","Info", Messagebox.OK, Messagebox.INFORMATION);
			}*/		 
			
			if(emailCount == 0) {
				
				message = "Your segment returned 0 active unique contacts of "+ totalCount + " available contacts.";
				//MessageUtil.setMessage("Your segment returned 0 active unique contacts of "+ totalCount + " available contacts." , "color:red", "TOP");
				//return false;
			}
			
		
		}//if segment
		else {
			emailCount = contactsDao.getUniqueCount(mlSet,"Active");
			
			if(unpurgedCount > 0) {
				Messagebox.show("Selected mailing list(s) have "+ unpurgedCount +" unpurged contacts.","Info", Messagebox.OK, Messagebox.INFORMATION);
			}
			
			if(emailCount == 0) {
				MessageUtil.setMessage("Your selection returned 0 active unique contacts of "+ totalCount + " available contacts. ", "color:red", "TOP");
				return false;
			}
		}
		
		
		//boolean isSegment = (segmentStr == null ?false:(segmentStr.equals("Total")?false:true));
		
		//String ml_ids_str ="";
		/*for(MailingList mailingList : mlSet){
			
			if(ml_ids_str.length()!=0) 	ml_ids_str +=  ",";
			ml_ids_str +=  mailingList.getListId();
		}*/
		//long mlsbit = Utility.getMlsBit(mlSet);
		
		
		/*if(isSegment) {

			logger.debug("segment rule::"+segmentStr);
			
			segmentStr = SalesQueryGenerator.generateListSegmentQuery(segmentStr,mlsbit);
			logger.info(" Generated Query :"+segmentStr);
			if(segmentStr == null) {
				MessageUtil.setMessage("Selected invalid segmentation rules.", "color:red", "TOP");
				return false;
			}
			
		}*/
		/*else {
			emailCount = contactsDao.getUniqueCount(mlSet,"Active");
			
			if(unpurgedCount > 0) {
				Messagebox.show("Selected mailing list(s) have "+ unpurgedCount +" unpurged contacts.","Info", Messagebox.OK, Messagebox.INFORMATION);
			}
			
			if(emailCount == 0) {
				MessageUtil.setMessage("Your selection returned 0 unique contacts of "+ totalCount + " available contacts. ", "color:red", "TOP");
				return false;
			}
		}*/
		
		// Check if user email count is sufficient to send campaign
		UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDao");

		if(usersDao != null) {
			
			SubscriptionDetails subDetails = new SubscriptionDetails();
			int userAvailableEmlCount = subDetails.getEmailStatus(Calendar.getInstance());
			
			//logger.debug("User available limit is :"+userAvailableEmlCount + " UserId:"+userId);
			/*if(userAvailableEmlCount == -1) {
				 MessageUtil.setMessage("Your account validity period has expired. Please renew your subscription to continue.", "color:red", "TOP");
			 	 return false;
			}	*/ 
			/*else if(emailCount > userAvailableEmlCount) {
				 MessageUtil.setMessage("Configured " + emailCount + " email addresses. Your available email credit limit is " + userAvailableEmlCount +".\n " +
				 		"Please contact support@optculture.com to request additional credits.", "color:red", "TOP");
				 return false;
			}*/
		} else {
			logger.error("** Exception : UsersDao is null. Could not perform user email Count validation. **");
		}
		
		if(isEdit!=null) {
			if(isEdit.equalsIgnoreCase("view")) {//new camp but not finished all steps
				//campaign.setStatus("Draft");
				//campaign.setStatusChangedOn(Calendar.getInstance());
				//campaign.setDraftStatus("CampLayout");
				//if(Utility.isSomethingWrong(campaign, CampaignStepsEnum.CampMlist.getPos())){
				if (!campaign.getStatus().equalsIgnoreCase("Draft") && !campaign.getDraftStatus().equalsIgnoreCase("complete")) {
				if((isEdit == null || isEdit.equalsIgnoreCase("view")) && Utility.isSomethingWrong(campaign, CampaignStepsEnum.CampMlist.getPos())){
					isSomethingWrong = true; 
					/*MessageUtil.setMessage(OCConstants.CAMPAIGN_MSG, "color:blue;");
					Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
					*/
				}
				}
				
				if((campaign.getDraftStatus()==null) || 
						CampaignStepsEnum.CampMlist.getPos() >= CampaignStepsEnum.valueOf(campaign.getDraftStatus()).getPos()) {
					campaign.setDraftStatus("CampLayout");
				} //
				
			}
			else if(isEdit.equalsIgnoreCase("edit")){
				campaign.setModifiedDate(Calendar.getInstance());
			}
		}
		else {//new camp creating step-by-step
			if (!campaign.getStatus().equalsIgnoreCase("Draft") && !campaign.getDraftStatus().equalsIgnoreCase("Complete")) {
			if(Utility.isSomethingWrong(campaign, CampaignStepsEnum.CampMlist.getPos())){
				isSomethingWrong = true;
				/*MessageUtil.setMessage(OCConstants.CAMPAIGN_MSG, "color:blue;");
				Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
				*/
			}}
			// campaign.setDraftStatus("CampLayout");
			
			if((campaign.getDraftStatus()==null) || 
					CampaignStepsEnum.CampMlist.getPos() >= CampaignStepsEnum.valueOf(campaign.getDraftStatus()).getPos()) {
				campaign.setDraftStatus("CampLayout");
			}

		}
		if(!isSomethingWrong){
			try {
				int confirm;
				if(message.length() > 0){
					confirm = Messagebox.show(message, "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				}
				else{
					confirm = Messagebox.show("Total "+emailCount+" active unique contacts have been configured. Do you want to continue?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				}
				if(confirm != 1){
					return false;
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				logger.error("Exception ::", e1);
			}
			try{
				if (session != null) {
					session.setAttribute("campaign", campaign);
					//campaignsDao.saveOrUpdate(campaign);
					campaignsDaoForDML.saveOrUpdate(campaign);
					UserActivitiesDao userActivitiesDao = (UserActivitiesDao) SpringUtil.getBean("userActivitiesDao");
					UserActivitiesDaoForDML userActivitiesDaoForDML = (UserActivitiesDaoForDML) SpringUtil.getBean("userActivitiesDaoForDML");
					/*if(userActivitiesDao != null) {
            		userActivitiesDao.addToActivityList(ActivityEnum.CONTS_ML_CONFIGURED_p1mlName_p2campaignName,GetUser.getUserObj(), campaign.getCampaignName(),mlSet.toString());
				}*/
					if(userActivitiesDaoForDML != null) {
						userActivitiesDaoForDML.addToActivityList(ActivityEnum.CONTS_ML_CONFIGURED_p1mlName_p2campaignName,GetUser.getLoginUserObj(), campaign.getCampaignName(),mlSet.toString());
					}
					
				} else {
					MessageUtil.setMessage("Problem experienced while saving. Please re-login and try again.","color:red", "TOP");
					logger.error("** Exception : session is null ** ");
					return false;
				}
			}catch (DataIntegrityViolationException dive) {
				logger.error("** Exception: Data Integrity Violation while saving the Email mailinglists: "
						+ dive + " **");
				return false;
			}catch (Exception e) {
				logger.error("** Exception: Problem while saving the Email mailinglists: " + e + " **");
				return false;
			} 
			 
		 
			return true;
			
			
		}else{
			MessageUtil.setMessage(OCConstants.CAMPAIGN_MSG, "color:blue;");
			Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
			
			return false;
		}
	}
	
	
	private Radiogroup configurelistTabId;
	private Div mlDivId,segDivId;
	
	public void onCheck$configurelistTabId() {
		
		
		if(configurelistTabId.getSelectedIndex()==0){
			mlDivId.setVisible(true);
			segDivId.setVisible(false);
			
		}else if(configurelistTabId.getSelectedIndex() == 1) {
			
			mlDivId.setVisible(false);
			segDivId.setVisible(true);
			
			
		}
		
		
		
	}
	
	
	
	
	//public void saveNext() {
	public void onClick$saveBtnId() throws Exception {
		if (campaign!=null) {
		Campaigns dbcampaign = campaignsDao.findByCampaignId(campaign.getCampaignId());
			if (!campaign.getStatus().equalsIgnoreCase("Draft") && !(campaign.getDraftStatus().equalsIgnoreCase("complete") || campaign.getDraftStatus().equalsIgnoreCase("CampFinal"))) {
				if((isEdit == null || isEdit.equalsIgnoreCase("view")) && Utility.isSomethingWrong(campaign, CampaignStepsEnum.CampTextMsg.getPos())){
					MessageUtil.setMessage(OCConstants.CAMPAIGN_MSG, "color:blue;");
					Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST); 
					return;
				}  
				}else if(!dbcampaign.getStatus().equalsIgnoreCase("Draft") && dbcampaign.getDraftStatus().equalsIgnoreCase("complete")) {
					if((isEdit == null || isEdit.equalsIgnoreCase("view")) && Utility.isSomethingWrong(campaign, CampaignStepsEnum.CampSett.getPos())){
						MessageUtil.setMessage(OCConstants.CAMPAIGN_MSG, "color:blue;");
						Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST); 
						return ;
					}} 
			
		}
		/*int campScheduleList = campaignScheduleDao.getActiveCountByCampaignId(campaign.getUsers().getUserId(),campaign.getCampaignId());
		if(!(campScheduleList == 0) ) {
			MessageUtil.setMessage(OCConstants.CAMPAIGN_MSG, "color:blue;");
			Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST); 
			return;
		}*/
		if(configurelistTabId.getSelectedIndex() == 0) {
			
			if(saveMlist()==false) return;
		}
		else if(configurelistTabId.getSelectedIndex() == 1) {
			
			if(saveSegRule() == false) return;
			
		}
		PageUtil.setFromPage("campaign/CampMlist");
		
		if(isEdit!=null){
			if(isEdit.equalsIgnoreCase("edit")){
				Redirect.goTo(PageListEnum.CAMPAIGN_FINAL);
			}if(isEdit.equalsIgnoreCase("view")){
				session.removeAttribute("editCampaign");
				Redirect.goTo(PageListEnum.CAMPAIGN_LAYOUT);
			}
		}else{
			session.removeAttribute("editCampaign");
			Redirect.goTo(PageListEnum.CAMPAIGN_LAYOUT);
		}
	}
	
	//public void saveAsDraft(){
	public void onClick$saveAsDraftBtnId() throws Exception {
		List<CampaignSchedule> campScheduleList = campaignScheduleDao.getByCampaignId(campaign.getCampaignId());
		long activeCount = campScheduleList.stream().filter(campaignSchedule -> campaignSchedule.getStatus() == 0).count(); 
		if(campScheduleList.size() == 0 || activeCount == 0) {
			if(configurelistTabId.getSelectedIndex() == 0) {
				
				if(saveMlist()==false) return;
			}
			else if(configurelistTabId.getSelectedIndex() == 1) {
				
				if(saveSegRule() == false) return;
				
			}
			
			//saveMlist();//modified aftyer POS
			PageUtil.setFromPage("campaign/CampMlist");
			//Redirect.goTo(PageListEnum.CAMPAIGN_VIEW);
			Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
		}else {
			//MessageUtil.setMessage(" A campaign with active schedules cannot be saved as draft. Please delete all active schedules first.", "color:red");
			MessageUtil.setMessage(" A campaign with upcoming schedule/s \n cannot be saved as a draft.\n Please delete all active schedules first.", "color:red");
			return;
		}
	}
	
	public void closeCampMlists() {
		PageUtil.setFromPage("campaign/CampMlist");
		Redirect.goTo(PageListEnum.RM_HOME);
	}
	
	//public void back() {
	public void onClick$backBtnId() throws Exception {	
		if(isEdit!=null){
			if(isEdit.equalsIgnoreCase("edit"))
				Redirect.goTo(PageListEnum.CAMPAIGN_FINAL);
			else if(isEdit.equalsIgnoreCase("view")){
				session.removeAttribute("editCampaign");
				//Redirect.goTo(PageListEnum.CAMPAIGN_VIEW);
				Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
			}
		}else{
			Redirect.goTo(PageListEnum.CAMPAIGN_SETTINGS);
		}
	}

}

class ListSegmentRule {
	
	public static String STRING = "STRING";
	public static String NUMBER = "NUMBER";
	public static String DATE = "DATE";
	public static String BOOLEAN = "BOOLEAN";
	
	private String fieldName;
	private String dataType;
	private long mailingListId = -1;
	private String columnName;

	public ListSegmentRule(String fieldName, String dataType, String columnName) {
		
		this.fieldName = fieldName;
		this.columnName = columnName;
		this.dataType = (dataType.equals("DOUBLE")) ? NUMBER : dataType;
	}
	
	public long getMailingListId() {
		return mailingListId;
	}

	public void setMailingListId(long mailingListId) {
		this.mailingListId = mailingListId;
	}
	
	public boolean isCFData() {
		return mailingListId != -1;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	public String toString() {
		if(isCFData()) {
			
			return "CF" + columnName.substring(columnName.lastIndexOf('_')+1) + ":" + this.fieldName;
		}
		else {
			return this.fieldName;
		}
	}
}

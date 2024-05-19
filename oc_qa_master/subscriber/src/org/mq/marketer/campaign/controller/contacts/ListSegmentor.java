package org.mq.marketer.campaign.controller.contacts;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.CustomFieldData;
import org.mq.marketer.campaign.beans.MLCustomFields;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.campaign.CampMlistController;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.CustomFieldDataDao;
import org.mq.marketer.campaign.dao.CustomFieldDataDaoForDML;
import org.mq.marketer.campaign.dao.MLCustomFieldsDao;
import org.mq.marketer.campaign.dao.MLCustomFieldsDaoForDML;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MailingListDaoForDML;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.QueryGenerator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
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
import org.zkoss.zul.Window;


@SuppressWarnings("serial")
public class ListSegmentor extends GenericForwardComposer {
	
	

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Label selMlLblId;
	private Listbox dispMlLBoxId;
	private Radiogroup listsTypeRgId;
	private Vbox segmentRulesVbId;
	private Listbox segRuleOptionLbId;
	private MailingListDao mailingListDao;
	private MailingListDaoForDML mailingListDaoForDML;
	private MLCustomFieldsDao mlCFDao;
	private MLCustomFieldsDaoForDML mlCFDaoForDML;
	private Window popupWinId;
	private Toolbarbutton addRuleToolbarId;
	
	private int segmentCount = 0;
	private int maxSegmentCount = 2;
	private ContactsDao contactsDao;
	private CustomFieldDataDao cfDataDao;
	private CustomFieldDataDaoForDML cfDataDaoForDML;

	//private Listbox popupWinId$mlistLbId;

	private Long currentUserId;
	
	//Set<Long> userIdsSet = GetUser.getUsersSet();//added for multiuseracc
	
	
	private String[] str_operators  = {"is","starts with","ends with","contains","does not contain"};
	private String[] numeric_operators  = {"=",">","<",">=","<=","!=", "between"};
	private String[] date_operators  = {"is","before","after","between"};
	private String[] boolean_operators  = {"is"};
	private String[][] fieldArray = 
		new String[][]{{"Email","First Name","Last Name","Address One","Address Two","City","State","Country","Pin","Phone"},
			{"email_id","first_name","last_name","address_one","address_two","city","state","country","pin","phone"}};

	private List<ListSegmentRule> totalFieldsList = new ArrayList<ListSegmentRule>();
	private List<ListSegmentRule> defaultFieldsList = new ArrayList<ListSegmentRule>();
	
	
	public ListSegmentor() {
		
		this.mlCFDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
		this.mlCFDaoForDML = (MLCustomFieldsDaoForDML)SpringUtil.getBean("mlCustomFieldsDaoForDML");
		this.contactsDao = (ContactsDao)SpringUtil.getBean("contactsDao");
		this.mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
		mailingListDaoForDML = (MailingListDaoForDML)SpringUtil.getBean("mailingListDaoForDML");
		cfDataDao = (CustomFieldDataDao)SpringUtil.getBean("cfDataDao");
		cfDataDaoForDML = (CustomFieldDataDaoForDML)SpringUtil.getBean("cfDataDaoForDML");
		currentUserId = GetUser.getUserId();
		
		UserActivitiesDao userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
		UserActivitiesDaoForDML userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
	/*if(userActivitiesDao != null) {
			userActivitiesDao.addToActivityList(ActivityEnum.VISIT_CONTACT_MANAGECONTACTS,GetUser.getUserObj());
		}*/
		if(userActivitiesDaoForDML != null) {
			userActivitiesDaoForDML.addToActivityList(ActivityEnum.VISIT_CONTACT_MANAGECONTACTS,GetUser.getLoginUserObj());
		}
		try {
			maxSegmentCount = Integer.parseInt(PropertyUtil.getPropertyValue("maxSegmentRules"));
		} catch (Exception e) {
		}
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		
		MessageUtil.clearMessage();
 		
		if (this.dispMlLBoxId.getItemCount()<1){
			MessageUtil.setMessage("No mailing list found in your account.", "color:red", "TOP");
			return;
		}
		
		ListSegmentRule lstSegRule;
	
		for(int i=0;i<fieldArray[0].length;i++) {
			
			lstSegRule = new ListSegmentRule(fieldArray[0][i], ListSegmentRule.STRING, fieldArray[1][i]);
			
			if( fieldArray[0][i].trim().equalsIgnoreCase("Pin") || 
				fieldArray[0][i].trim().equalsIgnoreCase("Phone") ) {
				lstSegRule.setDataType(ListSegmentRule.NUMBER);
			}
			defaultFieldsList.add(lstSegRule);
		}
		
		if(popupWinId$mlistLbId.getItemCount() > 0) {
			popupWinId$mlistLbId.setSelectedIndex(0);
		}
		
	} // doAfterCompose
/*
	public void init(Label selMlLblId, Listbox dispMlLBoxId , Radiogroup listsTypeRgId,  
			Vbox segmentRulesVbId,Listbox segRuleOptionLbId, Window popupWinId, Toolbarbutton addRuleToolbarId) {
		
		this.selMlLblId = selMlLblId;
		this.dispMlLBoxId = dispMlLBoxId;
		this.listsTypeRgId = listsTypeRgId;
		this.segmentRulesVbId = segmentRulesVbId;
		this.segRuleOptionLbId = segRuleOptionLbId;
		this.popupWinId = popupWinId;
		this.addRuleToolbarId = addRuleToolbarId;
		
 		MessageUtil.clearMessage();
 		
		if (this.dispMlLBoxId.getItemCount()<1){
			MessageUtil.setMessage("No mailing lists in your account.", "color:red", "TOP");
			return;
		}
		
		ListSegmentRule lstSegRule;
	
		for(int i=0;i<fieldArray[0].length;i++) {
			
			lstSegRule = new ListSegmentRule(fieldArray[0][i], ListSegmentRule.STRING, fieldArray[1][i]);
			
			if( fieldArray[0][i].trim().equalsIgnoreCase("Pin") || 
				fieldArray[0][i].trim().equalsIgnoreCase("Phone") ) {
				lstSegRule.setDataType(ListSegmentRule.NUMBER);
			}
			defaultFieldsList.add(lstSegRule);
		}
		
	}
	*/	
	
	public void onCheck(int selectedIndex){
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
	
	private void createItemsForOperatorsLb(Listbox operatorsLb,String type, String constraint) {
		
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
	
	public List<MailingList> getMailingLists() {
		List<MailingList> mlLists = null;
		try {
			//mlLists = mailingListDao.findAllByUser(userIdsSet); 
			Set<Long> listIdsSet = (Set<Long>)session.getAttribute(Constants.LISTIDS_SET);
			mlLists = mailingListDao.findByIds(listIdsSet);
		}catch (Exception e) {
			logger.error("** Exception : " + e);
		}
		return mlLists;
	}
	
	public void filterSegmentFields() {
		// 0 -->Any
		// 1 -->All
		if(segRuleOptionLbId.getSelectedIndex() == 1 && dispMlLBoxId.getSelectedCount() > 1) {
			totalFieldsList.clear();
			totalFieldsList.addAll(defaultFieldsList);
			//resetSegmentFields();
		}
	}
	
	private Div selectedListsDivId;
	
	@SuppressWarnings("unchecked")
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
			//logger.debug("<<<segRuleOptionLbId.getSelectedIndex()>>>>>::"+segRuleOptionLbId.getSelectedIndex());
			/*if(segRuleOptionLbId.getSelectedIndex() != 0 || selectedItems.size() > 1) {
				continue;
			}// if
			*/
			if(segRuleOptionLbId.getSelectedIndex() == -1 || selectedItems.size() > 1) {
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
		
		
		selMlLblId.setValue(listNamesStr);
		selectedListsDivId.setVisible(true);
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
	}
	
	@SuppressWarnings("deprecation")
	public Hbox addRules(String ruleStr) {
		
		logger.debug(" Rule :"+ruleStr);
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
		return ruleHb;
	}
	
	public void segment(Listbox mlistLbId, String newListName,String newListDesc, Label msgLbId) {
		
		try {
			
			logger.debug("-- just entered --");
			//**** list name and description set the eampty value
			popupWinId$listNameTbId.setValue("");
			popupWinId$descTbId.setValue("");
			
			
			if(mlistLbId.getSelectedIndex() < 0) {
				
				msgLbId.setValue(" Please select a mailing list to upload segemented contatcs");
				msgLbId.setVisible(true);
				return;
			}
			
			
			
			MailingList mList = null;
			if(mlistLbId.getSelectedIndex() == 0 ) {
				
				if(newListName == null || newListName.length() == 0) {
					
					msgLbId.setValue(" Please provide new list name to upload segemented contatcs");
					msgLbId.setVisible(true);
					return;
				}
				
				mList = new MailingList(newListName, true, GetUser.getUserObj());
				
				if(newListDesc != null) {
					mList.setDescription(newListDesc);
				}
				
				try {
					mailingListDaoForDML.saveOrUpdate(mList);
					
				} catch (Exception e) {
					logger.error("Exception ::", e);
					msgLbId.setValue(" Please provide valid/no-existed list name to" +
							" upload segemented contatcs");
					msgLbId.setVisible(true);
					return;
				}
			} 
			else {
				mList = (MailingList)mlistLbId.getSelectedItem().getValue();
			}
			
			String segmentQueryStr;
			if(listsTypeRgId.getSelectedIndex() == 0) {
				
				
				segmentQueryStr = 
					" SELECT list_id, email_id, first_name, last_name, created_date, " +
					" purged, email_status, last_status_change, last_mail_date, address_one, " +
					" address_two, city, state, country, pin, phone, optin, subscription_type, " +
					" optin_status FROM contacts WHERE list_id IN ("+listIdsStr+")";
			}
			else  {
				segmentQueryStr = QueryGenerator.generateListSegmentQuery( segmentRuleStr, true);
				logger.info(" Generated Query :"+segmentQueryStr);
			}
			
		
			
			//contactsDao.doSegment(segmentQueryStr, mList.getListId());
			
			msgLbId.setValue("");
			
			if(dispMlLBoxId.getSelectedCount() > 1 ) return;
			MailingList mailingList  = (MailingList)dispMlLBoxId.getSelectedItem().getValue() ;
			if(!mailingList.isCustField()) return;
			
			//*****************mlCustField ************
			try {
				List<MLCustomFields> mlCustFieldList  =	mlCFDao.findAllByList(mailingList);
				logger.debug("mlCustFieldList size is >>>::"+mlCustFieldList.size());
				
				if(mlCustFieldList == null || mlCustFieldList.size() == 0) return;
				
				List<MLCustomFields> tempList = new ArrayList<MLCustomFields>();
				for (Object obj : mlCustFieldList) {
					MLCustomFields tempMlCustField = (MLCustomFields)obj;

					//logger.debug("default value is ::"+tempMlCustField.getDefaultValue());
					tempMlCustField.setMailingList(mList);
					tempMlCustField.setcId(null);
					
					tempList.add(tempMlCustField);
					logger.debug("mlcfList saveorUpdate method called here");
				//	tempList.add(tempMlCustField);
//						mlCFDao.OrUpdate(tempMlCustField);
				}
				
				mList.setCustField(true);
				mailingListDaoForDML.saveOrUpdate(mList); 
				
				//mlCFDao.saveByCollection(tempList);
				mlCFDaoForDML.saveByCollection(tempList);

			} catch (Exception e) {
				logger.error("Exception ::", e);
			}
			
			//*************customField_data***********
			List<Contacts> newContactList = contactsDao.findContactByListId(mList); 
			
			if(newContactList == null && newContactList.size() == 0) return;
			
			List<CustomFieldData> tempList = new ArrayList<CustomFieldData>();
			for (Contacts contact : newContactList) {
				
				 Contacts tempContact = contactsDao.findContctsFromListId(contact.getEmailId(), mailingList);
				
				if(tempContact == null) continue;
				
				logger.debug("old contct  is ::"+tempContact);
				
				logger.debug("old contct getId is ::"+contact.getContactId());
				CustomFieldData oldCustfieldData = cfDataDao.getByContact(tempContact.getContactId());
				
				oldCustfieldData.setContact(contact);
				oldCustfieldData.setcId(null);
				tempList.add(oldCustfieldData);
			} // for
			
			//cfDataDao.saveByCollection(tempList);
			cfDataDaoForDML.saveByCollection(tempList);

			
		
			
			
		} catch (Exception e) {
			logger.error("** Exception : " ,(Throwable) e);
		}
	} //segment
	
	@SuppressWarnings("unchecked")
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
						MessageUtil.setMessage("First date must be earlier than second date.", "color:red", "TOP");
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
	
	@Override
	public void onEvent(Event event) throws Exception {
		super.onEvent(event);
		
	}
	
	@SuppressWarnings("unchecked")
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
	
	String segmentRuleStr;
	String listIdsStr = "";
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void validate() {
		
		int num = dispMlLBoxId.getItemCount();
		if (num == 0) {
			MessageUtil.setMessage(
					"Please create a contact list first so that you can send" +
					" your campaigns to it.","color:red", "TOP");
			return;
		}
		
		int mlcount = dispMlLBoxId.getSelectedIndex();
		if (mlcount == -1) {
			MessageUtil.setMessage("Select at least one list.", "color:red","TOP");
			return;
		}
		
		Set lists = dispMlLBoxId.getSelectedItems();
		Listitem li;
		MailingList ml;
		for (Object obj : lists) {
			li = (Listitem) obj;
			ml = (MailingList) li.getValue();
			if(listIdsStr.length() != 0) listIdsStr+=",";
			listIdsStr += ml.getListId();
		}
		
		String segmentQueryStr;
		
		if(listsTypeRgId.getSelectedIndex() == 0) {
			segmentQueryStr = "SELECT * FROM contacts WHERE list_id IN ("+listIdsStr+")";
		}
		else{
			
			segmentRuleStr = prepareRule(listIdsStr);
			logger.info(" SegmentRule returned by prepareRule() :"+segmentRuleStr);
			
			if(segmentRuleStr == null) {
				MessageUtil.setMessage("Please select valid segmentation rules.", "color:red", "TOP");
				return;
			}
			
			segmentQueryStr = QueryGenerator.generateListSegmentQuery( segmentRuleStr, true);
			logger.info(" Generated Query :"+segmentQueryStr);
			
			if(segmentQueryStr == null) {
				MessageUtil.setMessage("Invalid segmentation rules selected.", "color:red", "TOP");
			}
		}
		int emailCount = contactsDao.getSegmentedContactsCount(segmentQueryStr);
		
		if(emailCount == 0) {
			popupWinId.setVisible(false);
			MessageUtil.setMessage("Your segment returned 0 contacts.", "color:red", "TOP");
			return;
		}
		try {
			
			if(Messagebox.show("Your segment returned "+emailCount+" contacts. Do you want to continue?", 
					"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION) == Messagebox.OK) {
				
				popupWinId.setVisible(true);
				popupWinId.doHighlighted();
			}
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}
	
	//private Listbox dispMlLBoxId;
	public void onSelect$dispMlLBoxId() {
		try {
			if(dispMlLBoxId.getSelectedCount() > 0) {
				getMListCFData();
			}else {
				selectedListsDivId.setVisible(false);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception :: error getting from the getMListCFData()",e);
		}
	} // onClick$dispMlLBoxId
	
	public void onCheck$listsTypeRgId() {
		try {
			onCheck(listsTypeRgId.getSelectedIndex());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception :: error getting from onCheck method",e);
		}
	} //onCheck$listsTypeRgId
	
	public void onSelect$segRuleOptionLbId() {
		try {
			getMListCFData();
		} catch (Exception e) {
			logger.error("Exception :: error getting from getMListCFData method",e);
		}
	} //onSelect$segRuleOptionLbId
	
	public void onClick$addRuleToolbarId() {
		try {
			addRules(null);
		} catch (Exception e) {
			logger.error("Exception :: error getting from addRules method",e);
		}
	}
	
	public void onClick$segmentBtnId() {
		try {
			validate();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception :: error getting from validate method",e);
		}
	}
	
	private Listbox popupWinId$mlistLbId;
	private Textbox popupWinId$listNameTbId,popupWinId$descTbId;
	private Label popupWinId$msgLbId;
	
	public void onClick$submitBtnId$popupWinId() {
		String listName = popupWinId$listNameTbId.getValue();
		if(listName.trim().equals("")) {
			MessageUtil.setMessage("Please provide valid list name.","color:red","TOP");
			return;
		}
		boolean isExists = isMLExist(popupWinId$listNameTbId.getValue());
		
		if(isExists){
			MessageUtil.setMessage("Name already exists. ","color:red","TOP");
			return;
		}
		
		segment(popupWinId$mlistLbId,popupWinId$listNameTbId.getValue(),popupWinId$descTbId.getValue(),popupWinId$msgLbId);
		popupWinId.setVisible(false);
	}
	
	public boolean isMLExist(String listName){
		try{
			MailingListDao mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
			MailingList mailingList = mailingListDao.findByListName(listName, GetUser.getUserId());
			if(mailingList == null)
				return false;
			else
				return true;
		}catch(Exception e){
			logger.error(" ** Exception :"+ e +" **");
			return false;
		}
	}
	
	
	
} // main

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

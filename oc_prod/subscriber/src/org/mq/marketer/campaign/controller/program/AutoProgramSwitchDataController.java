package org.mq.marketer.campaign.controller.program;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.MLCustomFields;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.MLCustomFieldsDao;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.ProgramUtility;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.zkforge.canvas.Canvas;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;
import org.mq.marketer.campaign.general.Constants;

/**
 * 
 * @author Proumya
 *
 */
public class AutoProgramSwitchDataController extends GenericForwardComposer{

	private Session session;
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
//	private EventListener listener = new MyEventListener();
	
	private Vbox switchCondRulesVbId,switchActivityCondRulesVbId;
	private Listbox switchActivityCondRuleOptionLbId;
	private int segmentCount = 0;
	private int maxSegmentCount;
	private int maxActivityRules;
	private Groupbox activityFilterGbId;
	private Checkbox activityFilterCbId;
	
	private MLCustomFieldsDao mlCustomFieldsDao;
	
	
	
	private List<ListSegmentRule> defaultFieldsList = new ArrayList<ListSegmentRule>();	
	private Toolbarbutton addRuleToolbarId;
	//private String[] activityCriteria = {"Opens","Clicks"};
	private String[] str_operators  = {"is","starts with","ends with","contains","does not contain"};
	private String[] numeric_operators  = {"=",">","<",">=","<=","!=", "between"};
	private String[] date_operators  = {"is","before","after","between"};
	private String[] boolean_operators  = {"is"};
	private String[][] fieldArray = 
		new String[][]{{"Email","First Name","Last Name","Address One","Address Two","City","State","Country","Pin","Phone"},
			{"email_id","first_name","last_name","address_one","address_two","city","state","country","pin","phone"}};
	
	
	
	
	public AutoProgramSwitchDataController() {
		session = Sessions.getCurrent();
		mlCustomFieldsDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
		
		
	}
	
	private Map<String,String>  progCampMap;
	private List<MailingList> programMlLists;
	private Window switchCompWin;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		session.setAttribute("MY_COMPOSER", this);
		
		programMlLists = (List<MailingList>)Executions.getCurrent().getAttribute("programMailingLists");
		
		
		logger.info("got program Mailing lists=====>"+programMlLists);
		
		
		
		ListSegmentRule lstSegRule;
		for(int i=0;i<fieldArray[0].length;i++) {
			
			lstSegRule = new ListSegmentRule(fieldArray[0][i], ListSegmentRule.STRING, fieldArray[1][i]);
			
			if( fieldArray[0][i].trim().equalsIgnoreCase("Pin") || 
				fieldArray[0][i].trim().equalsIgnoreCase("Phone") ) {
				lstSegRule.setDataType(ListSegmentRule.NUMBER);
			}
			defaultFieldsList.add(lstSegRule);
			
		}
		if(programMlLists != null && programMlLists.size() == 1) {
			//TODO need to consider the customfields
			
			MailingList ml = programMlLists.get(0);
			if(ml.isCustField()) {
				
				
				List<MLCustomFields> list = mlCustomFieldsDao.findAllByList(ml);
				
				if(logger.isDebugEnabled())
					logger.debug(" Total Custom Fileds for the already existing list :"	+list.size());
				
				for (MLCustomFields customField : list) {
					
					lstSegRule = new ListSegmentRule(customField.getCustFieldName(),
							customField.getDataType().toUpperCase(), "cust_"+customField.getFieldIndex());
					lstSegRule.setMailingListId(ml.getListId());
					defaultFieldsList.add(lstSegRule);
				}// for
				
			}//if
			
			
		}//if
		maxSegmentCount = Integer.parseInt(PropertyUtil.getPropertyValue("maxSegmentRules"));
		maxActivityRules = Integer.parseInt(PropertyUtil.getPropertyValue("maxActivityRules"));
		//logger.info("switchCondRulesVbId size before addRule()=====>"+switchCondRulesVbId.getChildren().size());
		//addRule();
		//logger.info("switchCondRulesVbId size after addRule()=====>"+switchCondRulesVbId.getChildren().size());
		//switchCondRulesVbId.setVisible(false);
		
		//TODO need to get the customfields if the selected list has
		
		
		
		progCampMap = (Map<String,String>)Executions.getCurrent().getAttribute("progCampMap");
	}
	
	private Checkbox dataFilterCbId;
	private Groupbox dataFilterGbId;
	/*public void onCheck$dataFilterCbId() {
		
		boolean open = dataFilterCbId.isChecked();
		
		dataFilterGbId.setOpen(open);
		if(open) {
			
			addRule();
		}
		
		
		
	}*/
	
	
	/*public void onCheck$activityFilterCbId() {
		//logger.info("---just entered----"+activityFilterCbId.isChecked());
		boolean open = activityFilterCbId.isChecked();
		
		if(progCampMap.keySet().size()==0 && open) {
			
			MessageUtil.setMessage("cant be apply Activity Filter: no Email Activity " +
					"Component is present in input path of this switch .", "color:red;");
			activityFilterCbId.setChecked(false);
			activityFilterGbId.setOpen(false);
			return;
			
		}
		activityFilterGbId.setOpen(open);
		
		if(open) {
			
			Collection<String> progCampList = (Collection<String>)progCampMap.keySet();
			
			addActivityRule(progCampList);
			
			
			
		}
		
		
		
	}*/
	
	public void onClick$addRuleToolbarId() {
		
		//addRule();
		addRule(null);
		
		
	}
	
	
	private Toolbarbutton addActivityRuleToolbarId;
	public void onClick$addActivityRuleToolbarId() {
		
		addActivityRule((Set<String>)progCampMap.keySet());
		
		
		
	}
	
	
	private Listbox title1LbId,title2LbId;
	public void getNextComponents(List<Label> winTitleList,boolean visibility) {
		try {
			
			if(visibility) {
				Components.removeAllChildren(title1LbId);
				Components.removeAllChildren(title2LbId);
				
				nextLine2HbId.setVisible(true);
				
				Listitem li = null;
				for (Label title : winTitleList) {
					
					
					li = new Listitem();
					/*if(title1LbId.getItemCount() > 0) {
						for(int i=0; i<title1LbId.getItemCount(); i++) {
							
							if(title.getValue().equalsIgnoreCase(title1LbId.getItemAtIndex(i).getLabel())) {
								
								li.setLabel(title.getValue()+i);
								
							}//if
							else {
								li.setLabel(title.getValue());
							}
						}//for
					}else{
						li.setLabel(title.getValue());
					}*/
					li.setLabel(title.getValue());
					li.setValue(title);
					li.setParent(title1LbId);
					
					li = new Listitem();
					/*
					if(title2LbId.getItemCount()>0) {
						for(int i=0; i<title2LbId.getItemCount(); i++) {
							
							if(title.getValue().equalsIgnoreCase(title2LbId.getItemAtIndex(i).getLabel())) {
								
								li.setLabel(title.getValue()+i);
								
							}//if
							else {
								li.setLabel(title.getValue());
							}
						}//for
					}else {
						li.setLabel(title.getValue());
						
					}*/
					li.setLabel(title.getValue());
					li.setValue(title);
					li.setParent(title2LbId);
					
					
				}//for
				
				title1LbId.setSelectedIndex(0);
				title2LbId.setSelectedIndex(0);
				/*if(!((Label)win.getFellowIfAny("nextCompLblId")).getValue().contains(",")) {
					if( Messagebox.show("Component should have two output lines.","Captiway",
							 Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION) == Messagebox.OK ){
						return;
					}
				}
				else {
				}*/
			}
			else {
				
				Components.removeAllChildren(title1LbId);
				
				Listitem li = null;
				for (Label title : winTitleList) {
					
					
					li = new Listitem();
					li.setLabel(title.getValue());
					li.setValue(title);
					li.setParent(title1LbId);
				}
				title1LbId.setSelectedIndex(0);
				nextLine2HbId.setVisible(false);
				
			}//else
		} catch (Exception e) {
			logger.error("Exception ::" , e);;
		}
		
	}//getNextComponents
	
	
	public void setCriteriaSelection(Listbox criteriaLb, String activityRuleStr ) {
		
		String[] tempArr = activityRuleStr.split("\\|");
		String criteria = null;
		
		for(int i=0; i<criteriaLb.getItemCount(); i++) {
			
			criteria = criteriaLb.getItemAtIndex(i).getLabel();
			if(tempArr[1].equalsIgnoreCase(criteria)) {
				
				criteriaLb.setSelectedIndex(i);
				
			}//if
			
		}//for
		
		
		
	}//setCriteriaSelection
	
	
	
	public void setActivityFldSelection(Listbox progCampLb, String activityRuleStr) {
		//logger.info("----------------5-----------------");
		//ex:activityRuleStr = ACTIVITY_SEND_EMAIL-12w,ACTIVITY_SEND_EMAIL-13w|Opens
		logger.debug("activityRuleStr is====>"+activityRuleStr);
		String[] tempArr = activityRuleStr.split("\\|");
		String progCampCompWinId = null;
		
		for(int i=0; i<progCampLb.getItemCount(); i++){
			progCampCompWinId = (String)progCampLb.getItemAtIndex(i).getValue();
			//logger.info("is equals===>"+progCampCompWinId+"====="+tempArr[0]);
			if(tempArr[0].trim().equalsIgnoreCase(progCampCompWinId)) {
				
				progCampLb.setSelectedIndex(i);
				
			}//if
			
			
		}//for
		
	}//setActivityFldSelection
	
	
	
	public void setFldSelection(Listbox lb, String tempStr) {
		String[] tempArr = tempStr.split("\\|");
		ListSegmentRule tempSegRule = null;
		for(int i=0; i<lb.getItemCount(); i++) {
			tempSegRule = (ListSegmentRule)lb.getItemAtIndex(i).getValue();
			if(tempArr[0].trim().equalsIgnoreCase(tempSegRule.getColumnName()))
				lb.setSelectedIndex(i);
			
			if(tempSegRule.getDataType().equalsIgnoreCase(ListSegmentRule.NUMBER)) {
				
				//TODO need to cretae the operator list box as per the number type
				
				
				logger.debug("need to cretae the operator list box as per the number type....");
			}
			
		}//for
		
		
	}//setFldSelection
	
	
	
	
	public void setOptrSelection(Listbox lb, String tempStr) {
		
		String[] tempArr = tempStr.split("\\|");
		//logger.info("tempStr=====>"+tempStr);
		for(int i=0; i<lb.getItemCount(); i++) {
			//logger.info("tempArr[1]=====>"+tempArr[1]+" tempArr[1].split()[1]====>"+tempArr[1].split(":")[0]);
			if(tempArr[1].split(":")[1].trim().equalsIgnoreCase(lb.getItemAtIndex(i).getLabel()))
			lb.setSelectedIndex(i);
			
		}//for
		
	}
	
	/**
	 * this method prepares the view as this switch consists the data like rules,messageline text etc....
	 * @param conditionRules--->it holds the rule like how to apply activity and data filters.
	 * @param msgLbl1---------->it holds the message line text if user sets it.
	 * @param msgLbl2---------->it holds the message line text if user sets.
	 * @param centerDiv-------->it is require to have the actual window from the passed window id.
	 * @param visibility------->it desides how many go to lines should appear(if its true the msgLbl2 will be set). 
	 */
	public void setProperties(String conditionRules, Label msgLbl1, Label msgLbl2, Canvas centerDiv, boolean visibility) {
		//decide that data/activity groupbox set to be opened or not,
		//set the condition rules based on the conditionstr(conditionRules) of switchcondion obj,
		//set the properties for navigate groupbox.
		//logger.info("----------------1---------------");
		logger.debug("the condition rule got is======>"+conditionRules);
		
		//if this is not there,when u close n click again u will be added with mr rules
		List tempLst = switchCondRulesVbId.getChildren();
		if(tempLst.size()>1){
			
			for(int i=tempLst.size()-1; i>0; i--){
				
				tempLst.remove(i);
				segmentCount--;
			}
			
		}
		
		
		tempLst = switchActivityCondRulesVbId.getChildren();
		if(tempLst.size()>1){
			
			for(int i=tempLst.size()-1; i>0; i--){
				
				tempLst.remove(i);
				activityCount--;
			}
			
		}
		
		
		if(conditionRules == null) {
			addRule(null);
			addActivityRule(progCampMap.keySet());
			
			
			
		}//if
		
		
		
		String moveTo = "";
		String[] tempArr = null;
		String title = "";
		String[] ruleStrArr = null;
		ListSegmentRule tempSegRule = null;
		Listbox ruleoperatorLb = null;
		String dataConditionRules = null;
		String activityConditionRules = null;
		
		if(conditionRules == null) {
			logger.debug("*******got no condition query************"+switchCondRulesVbId.getChildren().size());
			
			//addRule();
			
		}
		//Any:<mlIdsToBeReplaced>||first_name|STRING:starts with|s;=;Any:||ACTIVITY_SEND_EMAIL-12w,ACTIVITY_SEND_EMAIL-13w|Opens::3
		
		
		
		
		else if(conditionRules != null) {
			conditionRules = conditionRules.replace(Constants.DELIMETER_DOUBLECOLON+conditionRules.charAt(conditionRules.length()-1), "");
			
			if(conditionRules.trim().endsWith(Constants.ADDR_COL_DELIMETER)) {
				
				dataConditionRules = conditionRules.replace(Constants.ADDR_COL_DELIMETER,"");
				//logger.info("the data/activity condition's rule string are=====>"+condRules);
				
			}
			else if(conditionRules.trim().startsWith(Constants.ADDR_COL_DELIMETER)) {
				
				activityConditionRules = conditionRules.replace(Constants.ADDR_COL_DELIMETER, "");
			}else {
				
				String[] condRules = conditionRules.split(Constants.ADDR_COL_DELIMETER);
				dataConditionRules = condRules[0];
				activityConditionRules = condRules[1];
				
				
			}
			
			
			
			//construct the rules
			
			
			if(dataConditionRules != null && dataConditionRules.length()>0) {
			
				dataFilterCbId.setChecked(true);
				dataFilterGbId.setOpen(true);
				
				String match = dataConditionRules.substring(0, dataConditionRules.indexOf(":", 0));
				
				for(int i=0; i<switchCondRuleOptionLbId.getItemCount(); i++) {
					
					if(switchCondRuleOptionLbId.getItemAtIndex(i).getLabel().trim().equalsIgnoreCase(match))
						switchCondRuleOptionLbId.setSelectedIndex(i);
					
				}
				
				//logger.info("conditionRules.substring(0, conditionRules.indexOf(|, 0)+2)====>"+dataConditionRules.substring(0, dataConditionRules.indexOf("||", 0)+2));
				//conditionRules = conditionRules.replace(conditionRules.substring(0, conditionRules.indexOf("|", 0)+2), "");
				//Any:<mlIdsToBeReplaced>
				ruleStrArr = dataConditionRules.split("\\|\\|"); //ex:Any:<mlIdsToBeReplaced>||first_name|STRING:starts with|s
				for (int i=1; i<ruleStrArr.length ; i++) {
					
					Hbox tempHbox = addRule(ruleStrArr[i]);
					//logger.info("the created hbox for switch is=====>"+tempHbox);
					/*Listbox ruleFldLb = (Listbox)tempHbox.getChildren().get(0);
					setFldSelection(ruleFldLb,ruleStrArr[i]);
					
					tempSegRule = (ListSegmentRule)ruleFldLb.getSelectedItem().getValue();
					
					
					ruleoperatorLb = (Listbox)tempHbox.getChildren().get(1);
					if(tempSegRule.getDataType().equalsIgnoreCase(ListSegmentRule.NUMBER)) {
						
						//TODO need to cretae the operator list box as per the number type
						
						
						
						//logger.info("need to cretae the operator list box as per the number type....");
						createItemsForOperatorsLb(ruleoperatorLb, tempSegRule.getDataType(), null);
						
					}
					setOptrSelection(ruleoperatorLb,ruleStrArr[i]);
					
					String value = ruleStrArr[i].split("\\|")[2];
					
					Textbox value1 = (Textbox)tempHbox.getChildren().get(2);
					value1.setValue(value);
					
					if(ruleoperatorLb.getSelectedItem().getLabel().equalsIgnoreCase("between")) {
						
						//TODO need to set the value for this second textbox;need to observe the format in condition string to split it properly
						//Any:<mlIdsToBeReplaced>||email_id|STRING:starts with|s||pin|NUMBER:between|1445454|15454545
						Textbox value2 = (Textbox)tempHbox.getChildren().get(3);
						value2.setValue(ruleStrArr[i].split("\\|")[3]);
						value2.setVisible(true);
						
					}//if
*/					
				}//for
				
			}//if
			
			
			
			if(activityConditionRules != null && activityConditionRules.length()>0) {
				//logger.info("---------------2---------------");
				
				
				activityFilterCbId.setChecked(true);
				activityFilterGbId.setOpen(true);
				
				//logger.info("activityConditionRules======>"+activityConditionRules+" progMap===>"+progCampMap);
				String match = activityConditionRules.substring(0, activityConditionRules.indexOf(":", 0));
				
				for(int i=0; i<switchActivityCondRuleOptionLbId.getItemCount(); i++) {
					
					if(switchActivityCondRuleOptionLbId.getItemAtIndex(i).getLabel().trim().equalsIgnoreCase(match))
						switchActivityCondRuleOptionLbId.setSelectedIndex(i);
					
				}//for 
				
				
				String fromDate = activityConditionRules.substring(activityConditionRules.indexOf(":", 0)+1, activityConditionRules.indexOf(">", 0)+1);
				
				if(fromDate.contains(Constants.AUTO_PROGRAM_EMAILREP_FROM_CREATED)) {
					reportDateConsRGId.setSelectedIndex(0);
					
					
				}else if(fromDate.contains(Constants.AUTO_PROGRAM_EMAILREP_FROM_MODIFIED)) {
					reportDateConsRGId.setSelectedIndex(1);
					
					
				}
				
				String[] tempruleStrArr = activityConditionRules.split("\\|\\|");//ex:All:||ACTIVITY_SEND_EMAIL-12w|Opens||ACTIVITY_SEND_EMAIL-19w|Opens
				//logger.info("tempruleStrArr.length=====>"+tempruleStrArr.length);
				for(int i=1; i<tempruleStrArr.length; i++) {
					Hbox tempHbox = addActivityRule((Set<String>)progCampMap.keySet());
					
					Listbox progCampList = (Listbox)tempHbox.getChildren().get(0);
					setActivityFldSelection(progCampList,tempruleStrArr[i]);
					
					
					Listbox criteriaLb = (Listbox)tempHbox.getChildren().get(2);
					setCriteriaSelection(criteriaLb, tempruleStrArr[i]);
					
					
				}//for
				
				
			}//if
			
		}//if conditionrules
		
		
		moveTo = (String)msgLbl1.getAttribute("moveTo");
		
		if(moveTo != null) {
			//has the value : "true||ACTIVITY_SEND_EMAIL-11w0" 
			tempArr = moveTo.split("\\|\\|");
			
			
			String mode =  tempArr[0];
			
			for(int i=0; i<mode1LbId.getItemCount(); i++) {
				
				if(mode1LbId.getItemAtIndex(i).getLabel().trim().equalsIgnoreCase(mode))
					mode1LbId.setSelectedIndex(i);
			}
			
			String nextWinId = tempArr[1];
			Window nextWin = ProgramUtility.getWindowFromCenterDiv(nextWinId, centerDiv);
			
			//logger.info("the window object got from center div for 1st time======>"+nextWin);
			if(nextWin != null) {
			
				/*if(nextWinId.startsWith("ACTIVITY_")) {	
					title = ((Label)nextWin.getFellowIfAny("titleLblId")).getValue();
				}
				else if(nextWinId.startsWith("EVENT_")) {
					
					title = ((Label)nextWin.getFellowIfAny("titleLblId")).getValue();
				}*/
				title = ((Label)nextWin.getFellowIfAny("titleLblId")).getValue();
				for(int i=0; i<title1LbId.getItemCount(); i++) {
					
					if(title1LbId.getItemAtIndex(i).getLabel().trim().equalsIgnoreCase(title))
						title1LbId.setSelectedIndex(i);
					
				}//for
				
				
			}//if
			msgLine1TbId.setValue(msgLbl1.getValue());
			
		}//if
		
		
		if(visibility == true ) {
			moveTo = (String)msgLbl2.getAttribute("moveTo");
		
		
		
			if(moveTo != null) {
				
	
				//has the value : "true||ACTIVITY_SEND_EMAIL-11w0" 
				tempArr = moveTo.split("\\|\\|");
				
				
				String mode =  tempArr[0];
				
				for(int i=0; i<mode2LbId.getItemCount(); i++) {
					
					if(mode2LbId.getItemAtIndex(i).getLabel().trim().equalsIgnoreCase(mode))
						mode2LbId.setSelectedIndex(i);
				}
				
				String nextWinId = tempArr[1];
				Window nextWin = ProgramUtility.getWindowFromCenterDiv(nextWinId, centerDiv);
				
				//logger.info("the window got from center div is====>"+nextWin);
				if(nextWin != null) {
					/*if(nextWinId.startsWith("ACTIVITY_")) {	
						title = ((Label)nextWin.getFellowIfAny("titleLblId")).getValue();
					}
					else if(nextWinId.startsWith("EVENT_")) {
						
						title = ((Label)nextWin.getFellowIfAny("titleLblId")).getValue();
					}*/
					title = ((Label)nextWin.getFellowIfAny("titleLblId")).getValue();
					for(int i=0; i<title2LbId.getItemCount(); i++) {
						
						if(title2LbId.getItemAtIndex(i).getLabel().trim().equalsIgnoreCase(title))
							title2LbId.setSelectedIndex(i);
						
					}
					
					msgLine2TbId.setValue(msgLbl2.getValue());
				
				}//if
				
			}//if
		
		}
	}//setProperties
	//private ListitemRenderer renderer = new MyRenderer();
	public void onSelect$switchCondRuleOptionLbId() {
		
		logger.info("---just entered---");
		String label = switchCondRuleOptionLbId.getSelectedItem().getLabel();
		defaultFieldsList.clear();
		//programMlLists = (List<MailingList>)Executions.getCurrent().getAttribute("programMailingLists");
		
		
		logger.info("got program Mailing lists=====>"+programMlLists);
		
		
		
		ListSegmentRule lstSegRule;
		for(int i=0;i<fieldArray[0].length;i++) {
			
			lstSegRule = new ListSegmentRule(fieldArray[0][i], ListSegmentRule.STRING, fieldArray[1][i]);
			
			if( fieldArray[0][i].trim().equalsIgnoreCase("Pin") || 
				fieldArray[0][i].trim().equalsIgnoreCase("Phone") ) {
				lstSegRule.setDataType(ListSegmentRule.NUMBER);
			}
			defaultFieldsList.add(lstSegRule);
			
		}
		if((programMlLists != null && programMlLists.size() == 1) && !label.equalsIgnoreCase("All")) {
			//TODO need to consider the customfields
			logger.info("the selected index is=====>"+switchActivityCondRuleOptionLbId.getSelectedIndex());
			MailingList ml = programMlLists.get(0);
			if(ml.isCustField()) {
				
				
				List<MLCustomFields> list = mlCustomFieldsDao.findAllByList(ml);
				
				if(logger.isDebugEnabled())
					logger.debug(" Total Custom Fileds for the already existing list :"	+list.size());
				
				for (MLCustomFields customField : list) {
					
					lstSegRule = new ListSegmentRule(customField.getCustFieldName(),
							customField.getDataType().toUpperCase(), "cust_"+customField.getFieldIndex());
					lstSegRule.setMailingListId(ml.getListId());
					defaultFieldsList.add(lstSegRule);
				}// for
				
				
			}//if
			
			
		}//if
		
		
		List rows = switchCondRulesVbId.getChildren();
		Listitem li;
		//ListSegmentRule lstSegRule;
		Listbox fieldsList;
		
		for(int i=1;i<rows.size();i++) {
			fieldsList = (Listbox)(((Hbox)rows.get(i)).getChildren().get(0));
			
			Components.removeAllChildren(fieldsList);
			for(int j=0;j<defaultFieldsList.size();j++) {
				lstSegRule = (ListSegmentRule)defaultFieldsList.get(j);
				li = new Listitem(lstSegRule.toString());
				li.setValue(lstSegRule);
				li.setParent(fieldsList);
			}
			fieldsList.invalidate();
			fieldsList = (Listbox)(((Hbox)rows.get(i)).getChildren().get(1));
			createItemsForOperatorsLb(fieldsList, ListSegmentRule.STRING, null);
		}
		
		
		
		
	}//onSelect$switchCondRuleOptionLbId() 
	
	
	
	
	private int activityCount;
	public Hbox addActivityRule(Set<String> progCampList) {
		
		//logger.info("------------3-------------");
		if(activityCount == maxActivityRules) {
			return null;
		}
		Hbox activityRule = new Hbox();
		activityRule.setStyle("padding:6px;");
		activityRule.setWidth("100%");
		activityRule.setPack("center");
		activityRule.setAlign("center");
		activityRule.setStyle("padding:6px;");
		
		Listbox campListbox = new Listbox();
		campListbox.setWidth("200px");
		
		
		/*campListbox.setModel(new ListModelList(progCampList));
		campListbox.setItemRenderer(renderer);*/
		
		
		Listitem item = null;
		if(progCampList.size() <= 0 || progCampList.size() > 0 ) {
			
			item = new Listitem("--Select Campaign--");
			item.setParent(campListbox);
			
		}
		
		for (String camp : progCampList) {
			
			item = new Listitem(camp);
			item.setValue(progCampMap.get(camp));
			item.setParent(campListbox);
			
		}
		
		campListbox.setMold("select");
		campListbox.setSelectedIndex(0);
		campListbox.setParent(activityRule);
		activityRule.setParent(switchActivityCondRulesVbId);
		
		Label lbl = new Label("Has");
		lbl.setParent(activityRule);
		
		
		Listbox criteriaListbox = new Listbox();
		criteriaListbox.setMold("select");
		criteriaListbox.setParent(activityRule);
		
		Listitem li = new Listitem("Opens");
		li.setParent(criteriaListbox);
		
		li = new Listitem("Clicks");
		li.setParent(criteriaListbox);
		criteriaListbox.setSelectedIndex(0);
		
		Toolbarbutton deleteIcon  = new Toolbarbutton();
		deleteIcon.setImage("/img/icons/delete_icon.png");
//		deleteIcon.addEventListener("onClick", new );
		deleteIcon.setParent(activityRule);
		
		//activityCount++;
		/*if(activityCount == maxActivityRules){
			
		
		}*/
		
		deleteIcon.addEventListener("onClick", new EventListener() {

			@Override
			public void onEvent(Event event) throws Exception {
				Hbox hb = (Hbox)(event.getTarget().getParent());
				Vbox segmentRulesVbId =(Vbox) hb.getParent();
				segmentRulesVbId.removeChild(hb);
				
				
				activityCount--;
				if(activityCount < maxActivityRules) {
					addActivityRuleToolbarId.setDisabled(false);
				} else {
					addActivityRuleToolbarId.setDisabled(true);
				}
			}
			
		});
		
		activityCount++;
		if(activityCount >= maxActivityRules) {
			addActivityRuleToolbarId.setDisabled(true);
		} else {
			addActivityRuleToolbarId.setDisabled(false);
		}
		
		return activityRule;
		
	}
	//actually not required directly we can pass this list from the parent zul.
	public List<Campaigns> getProgramCampList() {
		
		List<Campaigns> progcampList = null;
		
		
		return null;
		
	}
	
	public Hbox addRule(String ruleStr) {
		
		logger.debug(" Rule :"+ruleStr);
		if( segmentCount == maxSegmentCount ) {
			return null ;
		}
		
		DateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
		
		
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
		ruleHb.setStyle("padding:5px");
//		ruleHb.setValign("center");
		final Listbox fieldsLb = new Listbox();
		fieldsLb.setWidth("120px");
		Listitem li;
		ListSegmentRule lstSegRule;
		
		
		for(int j=0;j<defaultFieldsList.size();j++) {
			lstSegRule = (ListSegmentRule)defaultFieldsList.get(j);
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
			}//if
		}
		fieldsLb.setParent(ruleHb);
		fieldsLb.setMold("select");
		
		final Listbox operatorLb = new Listbox();
		operatorLb.setWidth("120px");
		operatorLb.setMold("select");
		
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
		final MyDatebox datebox1 = new MyDatebox();
		//final Datebox datebox1 = new Datebox();
		datebox1.setParent(ruleHb);
		datebox1.setVisible(false);
		if(data1 != null &&  dataTypeStr.equals(ListSegmentRule.DATE)) {
			datebox1.setFormat("dd/MM/yyyy");
			//datebox1.setValue(date1);
			datebox1.setText(data1);
			datebox1.setReadonly(true);
			datebox1.setVisible(true);
		}
		
		final MyDatebox datebox2 = new MyDatebox();
		datebox2.setParent(ruleHb);
		datebox2.setVisible(false);
		if(data2 != null && dataTypeStr.equals(ListSegmentRule.DATE) ) {
			//datebox2.setValue(new Date(data2));
			datebox2.setFormat("dd/MM/yyyy");
			datebox2.setText(data2);
			datebox2.setReadonly(true);
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
		deleteIcon.setImage("/img/icons/delete_icon.png");
		deleteIcon.setParent(ruleHb);
		ruleHb.setParent(switchCondRulesVbId);
		
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
	
		
		
		
		
		
		
	}//addRule
	
	
	
	/**
	 * this method allows to add the condition for the switch type of component
	 * @param ruleStr 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public Hbox addRule() {
		
		if(segmentCount == maxSegmentCount) {
			return null;
		}// if
		
		String dataTypeStr = null;
		String constraintStr = null;
		
		Hbox ruleHb = new Hbox();
		ruleHb.setStyle("padding:6px");
		ruleHb.setPack("center");
		final Listbox fieldsLb = new Listbox();
		fieldsLb.setWidth("120px");
		fieldsLb.setAttribute("ontheLb", "fieldsLb");
//		fieldsLb.addEventListener("onSelect", listener);
		Listitem li;
		ListSegmentRule lstSegRule;
		
		for(int i=0; i<defaultFieldsList.size(); i++) {
			lstSegRule = (ListSegmentRule)defaultFieldsList.get(i);
			
			li = new Listitem(lstSegRule.toString());
			li.setValue(lstSegRule);
			li.setParent(fieldsLb);
			
		}//for
		fieldsLb.setParent(ruleHb);
		fieldsLb.setMold("select");
		
		final Listbox operatorLb = new Listbox();
		operatorLb.setWidth("120px");
		operatorLb.setMold("select");
		operatorLb.setAttribute("onTheLb", "operatorLb");
//		operatorLb.addEventListener("onSelect", listener);
		
		createItemsForOperatorsLb(operatorLb, dataTypeStr,constraintStr);
		operatorLb.setParent(ruleHb);
		
		final Textbox value1Tb = new Textbox();
		
		value1Tb.setWidth("120px");
		//value1Tb.setHeight("20px");
		value1Tb.setParent(ruleHb);		
		value1Tb.setVisible(true);
		
		
		final Textbox value2Tb = new Textbox();
		
		value2Tb.setWidth("120px");
		//value1Tb.setHeight("20px");
		value2Tb.setParent(ruleHb);		
		value2Tb.setVisible(false);
		
		//TODO need to add one more textbox inorder to handle the between operator
		
		Toolbarbutton deleteIcon  = new Toolbarbutton();
		deleteIcon.setImage("/img/icons/delete_icon.png");
//		deleteIcon.addEventListener("onClick", new );
		deleteIcon.setParent(ruleHb);
		
		final Datebox datebox1 = new Datebox();
		datebox1.setParent(ruleHb);
		datebox1.setVisible(false);
		
		final Datebox datebox2 = new Datebox();
		datebox2.setParent(ruleHb);
		datebox2.setVisible(false);
		
		final Listbox booleanLb = new Listbox();
		booleanLb.setMold("select");
		Listitem bLi1 = new Listitem("True");
		bLi1.setParent(booleanLb);
		Listitem bLi2 = new Listitem("False");
		bLi2.setParent(booleanLb);
		booleanLb.setVisible(false);
		
		
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
				
				
				segmentCount--;
				if(segmentCount < maxSegmentCount) {
					addRuleToolbarId.setDisabled(false);
				} else {
					addRuleToolbarId.setDisabled(true);
				}
			}
			
		});
		ruleHb.setParent(switchCondRulesVbId);
		
		segmentCount++;
		if(segmentCount >= maxSegmentCount) {
			addRuleToolbarId.setDisabled(true);
		} else {
			addRuleToolbarId.setDisabled(false);
		}
		
		return ruleHb;
		
		
	}//addRule
	
	
public void createItemsForOperatorsLb(Listbox operatorsLb,String type, String constraint) {
		
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
		}// for
	}//createItemsForOperatorsLb

private Textbox msgLine2TbId,msgLine1TbId;
private Listbox mode1LbId,mode2LbId;
private Hbox nextLine2HbId;

@SuppressWarnings("unchecked")
public String validateAndGetStrToFormQry(Label msgLine1, Label msgLine2) {
	
	try {
		
		String strToFormQuery = "";
		
		int open = 0;
		if(!dataFilterCbId.isChecked() && !activityFilterCbId.isChecked()) {
			
			MessageUtil.setMessage("At least one filter should be applied.", "color:red;","top");
			return null;
			
		}
		
		strToFormQuery = prepareRule();
		logger.debug(" SegmentRule returned by prepareRule() :"+strToFormQuery);
		
		if(strToFormQuery == null) 	return null;
		
		
		if(nextLine2HbId.isVisible()){
			String mode1 = mode1LbId.getSelectedItem().getLabel();
			String mode2 = mode2LbId.getSelectedItem().getLabel();
			
			if(mode1.equalsIgnoreCase(mode2) || mode2.equalsIgnoreCase(mode1) ) return null;
			
			String title1 = title1LbId.getSelectedItem().getLabel();
			String title2 = title2LbId.getSelectedItem().getLabel();
			
			if( title1.equalsIgnoreCase(title2) || title2.equalsIgnoreCase(title1) ) return null;
			
			
			msgLine1.setValue(msgLine1TbId.getValue());
			msgLine1.setAttribute("moveTo", mode1+"||"+((Label)title1LbId.getSelectedItem().getValue()).getAttribute("mode"));
			//logger.info("dfsdfdsfadfsdfdsfsdaf"+mode1LbId.getSelectedItem().getLabel()+"||"+((Label)title1LbId.getSelectedItem().getValue()).getAttribute("mode"));
			
			msgLine2.setValue(msgLine2TbId.getValue());
			msgLine2.setAttribute("moveTo", mode2+"||"+((Label)title2LbId.getSelectedItem().getValue()).getAttribute("mode"));
			//logger.info("dfsdfdsfadfsdfdsfsdaf"+mode2LbId.getSelectedItem().getLabel()+"||"+((Label)title2LbId.getSelectedItem().getValue()).getAttribute("mode"));
		}
		else {
			
			String mode1 = mode1LbId.getSelectedItem().getLabel();
			
			String title1 = title1LbId.getSelectedItem().getLabel();
			msgLine1.setValue(msgLine1TbId.getValue());
			msgLine1.setAttribute("moveTo", mode1+"||"+((Label)title1LbId.getSelectedItem().getValue()).getAttribute("mode"));
		}
		
		if(strToFormQuery.length() >0 && strToFormQuery.endsWith(Constants.ADDR_COL_DELIMETER)) {
			
			//activityFilterCbId.setChecked(false);
			if(errMessage.trim().length() > 0) {
				MessageUtil.setMessage(errMessage, "color:red;");
				return null;
			
			}//if
		}// else if
		
		if( dataFilterCbId.isChecked() && !activityFilterCbId.isChecked()){
			open = 1;
		}else if(activityFilterCbId.isChecked() && !dataFilterCbId.isChecked()) {
			open = 2;
		}else if(dataFilterCbId.isChecked() && activityFilterCbId.isChecked()) {
			open = 3;
		}
		
		
		return strToFormQuery+Constants.DELIMETER_DOUBLECOLON+open;
		
	} catch (WrongValueException e) {
		logger.error("Exception ::" , e);;
		return null;
	} 
	
}//validateAndGetStrToFormQry


private Listbox switchCondRuleOptionLbId;
private Radiogroup reportDateConsRGId;

public String prepareRule() {
	errMessage = "";
	//check for the condition rules likes data/activity are set open or not and proceed further
	
	StringBuffer switchTotConditionSB = new StringBuffer();
	
	/*********this is when the data condition is set open******************/
	
	StringBuffer switchCondSB = new StringBuffer();
	
	if(dataFilterCbId.isChecked()) {
	List rulesList = switchCondRulesVbId.getChildren();
	
	switchCondSB.append(switchCondRuleOptionLbId.getSelectedItem().getLabel()+":"+"<mlIdsToBeReplaced>");
	
	if(rulesList.size() <= 1) {
		//TODO need to show the popup box containing error
		MessageUtil.setMessage("At least one segment must be selected.", "color:red", "top");
		return null;
	}
	
	String tempStr;
	boolean isValid = true;
	
	for(int i=1; i<rulesList.size(); i++) {
		
		try{
			Hbox ruleHb = (Hbox)rulesList.get(i);
			tempStr = prepareRule(ruleHb);
			if(tempStr == null) {
				ruleHb.setStyle("padding:10px;border:1px solid;color:red");
				isValid = false;
			}else {
				ruleHb.setStyle("padding:10px;border:none");
			}
			switchCondSB.append("||"+tempStr);
		}catch(Exception e){
			logger.error("** Exception : Problem while preparing the rules in for loop", 
					(Throwable)e);
		}
	}
	
	if(!isValid) {
		logger.warn(" >>>>>>>>>>>>> Segmentation validation failed ");
		return  null;
	}
	
	}
	/**********this is when activity condition set open****************************/
	
	//by this time switchCondSB is having data related ruleStr with so many delimeters.
	//prepare Activity related ruleStr and append it to switchCondSB with a new Delimeter.
	
	
	StringBuffer switchActivityCondSB = new StringBuffer();//switchActivityCondRuleOptionLbId.getSelectedItem().getLabel()+":"
	
	if(activityFilterCbId.isChecked()) {
		
		String fromDate = null;
		
		if(reportDateConsRGId.getSelectedItem().getId().equalsIgnoreCase("prgCreatedDateRBtnId")) {
			
			fromDate = Constants.AUTO_PROGRAM_EMAILREP_FROM_CREATED;
			
		}else if(reportDateConsRGId.getSelectedItem().getId().equalsIgnoreCase("prgModifiedDateRBtnId")) {
			
			fromDate = Constants.AUTO_PROGRAM_EMAILREP_FROM_MODIFIED;
			
		}
		
		
		String option = switchActivityCondRuleOptionLbId.getSelectedItem().getLabel();
		switchActivityCondSB.append(option+":"+"<"+fromDate+">");
		
		List activityRules = switchActivityCondRulesVbId.getChildren();
		
		if(activityRules.size() <= 1) {
			//TODO need to show the popup box containing error
			MessageUtil.setMessage("At least one Activity rule must be selected.", "color:red", "top");
			return null;
		}
		Hbox ActivityRuleHb = null;
		for(int i=1; i<activityRules.size(); i++ ) {
			
			ActivityRuleHb = (Hbox)activityRules.get(i);
			String tempActivityStr = prepareActivityRule(ActivityRuleHb);
			
			if(tempActivityStr != null) {

				switchActivityCondSB.append("||"+tempActivityStr);
			}
			
		}//for
		
		if(switchActivityCondSB.toString().equalsIgnoreCase(option+":"+"<"+fromDate+">")) {
			switchActivityCondSB = new StringBuffer();
		}
	}
	
	switchTotConditionSB.append(switchCondSB).append(Constants.ADDR_COL_DELIMETER+switchActivityCondSB);
	
	
	
	return switchTotConditionSB.toString();
	
	
}//prepareRule

String errMessage = "";


public String prepareActivityRule(Hbox ActivityRuleHb) {
	
	StringBuffer activityRuleSB = new StringBuffer();
	
	List columns = ActivityRuleHb.getChildren();
	Listbox CampsLb = (Listbox)columns.get(0);
	
	String compWinId = null;
	
	//logger.info("getSelectedIndex-------->"+CampsLb.getSelectedIndex());
	if(CampsLb.getSelectedIndex() <= 0) {
		
		if(CampsLb.getItemCount() > 1) {
			//logger.info("fjkdsjfkdjfkdjfdkjfkj");
			errMessage = "please Select the Campaign that has been opened/clicked.";
			return null;
			
		}else {
			activityFilterCbId.setChecked(false);
			errMessage = "cant be apply Activity Filter: no Email Activity \n" +"Component is present in input path of this switch .";
			return null;
		}
		
	}
	else if(CampsLb.getSelectedIndex() > 0) {
		
		compWinId = (String)CampsLb.getSelectedItem().getValue();
	}
	
	activityRuleSB.append(compWinId+"|");
	
	
	Listbox criteriaLb = (Listbox)columns.get(2);
	
	String criteria;
	
	if(criteriaLb.getSelectedIndex() == -1) {
		criteria = criteriaLb.getItemAtIndex(0).getLabel();
		//logger.info("-------1------"+criteria);
	}else {
		
		criteria = criteriaLb.getSelectedItem().getLabel();
		//logger.info("-------2------"+criteria);
	}
	
	
	activityRuleSB.append(criteria);
	
	return activityRuleSB.toString();
	
}


public String prepareRule(Hbox ruleHb) {
	
	StringBuffer ruleSB = new StringBuffer();
	DateFormat dateFormat = 
		new SimpleDateFormat(PropertyUtil.getPropertyValue("customFiledDateFormat"));
	
		
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
					MessageUtil.setMessage("Value cannot be left empty. ", "color:red", "TOP");
					return null;
				}
				if(valueStr.contains("|")) {
					MessageUtil.setMessage("Value cannot contain pipe(|).", "color:red", "TOP");
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
					logger.error("NumberFormatException : ", e);
					MessageUtil.setMessage("Wrong value selected "+tb2.getValue(), "color:red", "TOP");
					return null;
				}
			}
		}
		else if(dataType.equalsIgnoreCase(ListSegmentRule.DATE)) {
			
			MyDatebox dateBox1 = (MyDatebox)columns.get(4);
			//dateBox1.setFormat("dd/mm/yyyy");
			MyDatebox dateBox2 = (MyDatebox)columns.get(5);
			//dateBox2.setFormat("dd/mm/yyyy");
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

	return ruleSB.toString();
	

}

/*
		
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
						MessageUtil.setMessage("Value should not be empty.", "color:red", "TOP");
						return null;
					}
					if(valueStr.contains("|")) {
						MessageUtil.setMessage("Value should not contains pipe(|).", "color:red", "TOP");
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
						MessageUtil.setMessage("First date must be before second date. ", "color:red", "TOP");
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
	
 * 
 * 
 * 
 */



/*
 * @SuppressWarnings("unchecked")
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
 */



	/*
	 *
		
String segmentRuleStr;
	String listIdsStr = "";
	
	@SuppressWarnings("unchecked")
	public void validate() {
		
		int num = dispMlLBoxId.getItemCount();
		if (num == 0) {
			MessageUtil.setMessage(
					"Please create a contact List first so that you can send" +
					" your campaigns to that list of contacts","color:red", "TOP");
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
				MessageUtil.setMessage("Selected invalid segmentation rules.", "color:red", "TOP");
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
					"Captiway", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION) == Messagebox.OK) {
				
				popupWinId.setVisible(true);
				popupWinId.doHighlighted();
			}
		} catch (InterruptedException e) {
			logger.error("Exception ::", e);
		}
	}	 
	 */
/*private class MyEventListener implements EventListener {
	
	public MyEventListener() {
		super();
	
	}
	
	@Override
	public void onEvent(Event evt) throws Exception {
		// TODO Auto-generated method stub
		Object obj = evt.getTarget();
		if(obj instanceof Listbox) {
			
			Listbox lb = (Listbox)obj;
			if(((String)lb.getAttribute("onTheLb")).equalsIgnoreCase("fieldsLb")) {
				
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
			else if(((String)lb.getAttribute("onTheLb")).equalsIgnoreCase("operatorLb")) {
				
				
				
			}
			
			
		}// if
		else if(obj instanceof Toolbarbutton) {
			
			Toolbarbutton tlbtn = (Toolbarbutton)obj; 
			
			Hbox hb = (Hbox)(tlbtn.getParent());
			Vbox switchCondRulesVbId =(Vbox) hb.getParent();
			switchCondRulesVbId.removeChild(hb);
			
			
			segmentCount--;
			if(segmentCount < maxSegmentCount) {
				addRuleToolbarId.setDisabled(false);
			} else {
				addRuleToolbarId.setDisabled(true);
			}
		}// else if
		
			
			
			
	}// onEvent
		
	
}*///MyEventListener

//this will not be work out as the listboxes are dynamically created n deleted
private class MyRenderer implements ListitemRenderer,EventListener{
	
	MyRenderer() {
		
		super();
	}
	
	@Override
	public void render(Listitem item, Object obj, int arg2) throws Exception {
		//logger.info("--------------------4----------------");
		String progCamp = null;
		Listcell lc = null;
		
		if(obj instanceof String) {
			progCamp = (String)obj;
			//logger.info("the value to be set for this list item is=====>"+progCampMap.get(progCamp)); 
			item.setValue(progCampMap.get(progCamp));
			lc = new Listcell(progCamp);
			lc.setParent(item);
			
		}//if
		
	}//render
	
	@Override
	public void onEvent(Event event) throws Exception {

		
		
	}//onEvent
	
	
	
	
}//MyRenderer



public static void main(String args[]) {
	
	Date date = new Date("12/03/2011");
	logger.info("my date is======>"+date.toString());
	Datebox datebox = new Datebox(date);
	logger.info("my date box value is=====>"+datebox.getValue());
	
	
}


}//class

 


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
	
	
	
	
	

}//class

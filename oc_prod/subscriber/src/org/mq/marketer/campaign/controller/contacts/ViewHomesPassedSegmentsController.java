package org.mq.marketer.campaign.controller.contacts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.SegmentRules;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.SegmentRulesDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Detail;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Window;

public class ViewHomesPassedSegmentsController extends GenericForwardComposer implements EventListener{
	
	
	private Session session;
	private SegmentRulesDao segmentRulesDao;
	private UsersDao usersDao;
	private Users currentUser;
	private Listbox segmentsLbId;
	private Label totCountLblId, totContactCountLblId, descLblId;
	
	private ContactsDao contactsDao;
	private Rows gridRowsId;
	private Column filterOnLblColId;
	private CampaignsDao campaignsDao;
	private POSMappingDao posMappingDao;
	
	private A countryAId, stateAId,	districtAId, cityAId, zipAId, areaAId, streetAId, addrOneAId, addrTwoAId;
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	public static Map<String, String> genFieldContMap = new HashMap<String, String>();
	public static Map<String, String> genFieldCustomersMap = new HashMap<String, String>();
	
	static{
		
		genFieldContMap.put("Country", "country");
		genFieldContMap.put("State", "state");
		genFieldContMap.put("District", "district");
		genFieldContMap.put("City", "city");
		genFieldContMap.put("ZIP", "zip");
		genFieldContMap.put("Area", "area");
		genFieldContMap.put("Street", "street");
		genFieldContMap.put("Address One", "address_one");
		genFieldContMap.put("Address Two", "address_two");
		genFieldContMap.put("Addressunit Id", "address_unit_id");
		/*genFieldContMap.put("Mobile", "getPhone");
		genFieldContMap.put("RetailPro ID", "getExternalId" );
		genFieldContMap.put("Gender", "getGender");
		genFieldContMap.put("BirthDay", "getBirthDay");
		genFieldContMap.put("Anniversary", "getAnniversary");
		genFieldContMap.put("Home Store", "getHomeStore");*/
		genFieldContMap.put("UDF1", "udf1");
		genFieldContMap.put("UDF2", "udf2");
		genFieldContMap.put("UDF3", "udf3");
		genFieldContMap.put("UDF4", "udf4");
		genFieldContMap.put("UDF5", "udf5");
		genFieldContMap.put("UDF6", "udf6");
		genFieldContMap.put("UDF7", "udf7");
		genFieldContMap.put("UDF8", "udf8");
		genFieldContMap.put("UDF9", "udf9");
		genFieldContMap.put("UDF10", "udf10");
		genFieldContMap.put("UDF11", "udf11");
		genFieldContMap.put("UDF12", "udf12");
		genFieldContMap.put("UDF13", "udf13");
		genFieldContMap.put("UDF14", "udf14");
		genFieldContMap.put("UDF15", "udf15");
		
		
		genFieldCustomersMap.put("Email" , "email_id");
		genFieldCustomersMap.put("First Name" , "first_name");
		genFieldCustomersMap.put("Last Name" , "last_name");
		genFieldCustomersMap.put("Street" , "address_one");
		genFieldCustomersMap.put("City" , "city");
		genFieldCustomersMap.put("State" , "state");
		genFieldCustomersMap.put("Country" , "country");
		genFieldCustomersMap.put("ZIP" , "pin");
		genFieldCustomersMap.put("Mobile" , "phone");
		genFieldCustomersMap.put("Customer ID" , "external_id" );
		genFieldCustomersMap.put("Addressunit ID" , "hp_id" );
		genFieldCustomersMap.put("Gender" , "gender");
		genFieldCustomersMap.put("HomeStore" , "home_store");
		genFieldCustomersMap.put("UDF1", "udf1");
		genFieldCustomersMap.put("UDF2", "udf2");
		genFieldCustomersMap.put("UDF3", "udf3");
		genFieldCustomersMap.put("UDF4", "udf4");
		genFieldCustomersMap.put("UDF5", "udf5");
		genFieldCustomersMap.put("UDF6", "udf6");
		genFieldCustomersMap.put("UDF7", "udf7");
		genFieldCustomersMap.put("UDF8", "udf8");
		genFieldCustomersMap.put("UDF9", "udf9");
		genFieldCustomersMap.put("UDF10", "udf10");
		genFieldCustomersMap.put("UDF11", "udf11");
		genFieldCustomersMap.put("UDF12", "udf12");
		genFieldCustomersMap.put("UDF13", "udf13");
		genFieldCustomersMap.put("UDF14", "udf14");
		genFieldCustomersMap.put("UDF15", "udf15");
		
		
	}
	
	
	A activityAarr[] = null;
	
	public ViewHomesPassedSegmentsController() {
		
		session = Sessions.getCurrent();
		
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("BCRM Segment(s)","",style,true);
		
		segmentRulesDao = (SegmentRulesDao)SpringUtil.getBean("segmentRulesDao");
		usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		contactsDao = (ContactsDao)SpringUtil.getBean("contactsDao");
		campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
		posMappingDao = (POSMappingDao)SpringUtil.getBean("posMappingDao");
		
		currentUser = GetUser.getUserObj();
		
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		List<SegmentRules> homePassedSegList = segmentRulesDao.findAllBySegType(Constants.SEGMENT_TYPE_HOMESPASSED, currentUser.getUserId());
		if(homePassedSegList == null || homePassedSegList.size() == 0) {
			
			logger.info("No BCRM Segment rules Found");
			return;
		}
		
		Listitem item = new Listitem("--Select--", null);
		item.setParent(segmentsLbId);
		
		for (SegmentRules segmentRules : homePassedSegList) {
			
			item = new Listitem(segmentRules.getSegRuleName(), segmentRules);
			item.setParent(segmentsLbId);
			
			
			
		}//for
		if(segmentsLbId.getItemCount() > 0) {
			segmentsLbId.setSelectedIndex(0);
		}
		
		
	}
	
	public void onCreate$viewSegmentWinId() {
		
		activityAarr =new A[] { countryAId, stateAId, districtAId, cityAId,  areaAId, zipAId, streetAId, addrOneAId, addrTwoAId};
		dispLinksBasedOnMapping();
		
		
	}
	
	public void onSelect$segmentsLbId() {
		
		/*A activityAarr[] ={ countryAId, stateAId, districtAId, cityAId, zipAId, areaAId, streetAId, addrOneAId, addrTwoAId};
		for (A a : activityAarr) {
			
			
			
			
		}
		*/
		
		
		if(countryAId.isVisible()) onClick$countryAId();
		else if(stateAId.isVisible()) onClick$stateAId();
		else if(districtAId.isVisible()) onClick$districtAId();
		else if(cityAId.isVisible()) onClick$cityAId();
		else if(areaAId.isVisible()) onClick$areaAId();
		else if(zipAId.isVisible()) onClick$zipAId();
		else if(streetAId.isVisible()) onClick$streetAId();
		else if(addrOneAId.isVisible()) onClick$addrOneAId();
		else if(addrTwoAId.isVisible()) onClick$addrTwoAId();
		
		
	}
	
	
	public void dispLinksBasedOnMapping() {
		
		if(segmentsLbId.getItemCount() == 0) {
			
			MessageUtil.setMessage("No segmentation rule found.", "color:red;");
			return;
			
		}
		
		HomesPassedEnum enumForAnch = null; 
		for (int i = 0; i < activityAarr.length; i++) {
			activityAarr[i].setVisible(false);
			enumForAnch = HomesPassedEnum.getEnumByCode(i+1);
			activityAarr[i].setAttribute("targetEnum", enumForAnch);
			activityAarr[i].setAttribute("dispLabel", enumForAnch.getDispLabel());
			
		}
		
		//TODO need to get the segment Created user's mappings
		List<POSMapping> posMappingList = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_HOMES_PASSED+"'", currentUser.getUserId());

		for (A a : activityAarr) {
			
			for (POSMapping eachPos : posMappingList) {
		
				if(eachPos.getCustomFieldName().startsWith("UDF") || eachPos.getCustomFieldName().contains("Addressunit Id")) continue;
				
				if(((String)a.getAttribute("dispLabel")).equalsIgnoreCase(eachPos.getCustomFieldName())) {
					
					a.setVisible(true);
					a.setLabel(eachPos.getDisplayLabel());
					break;
				}//if
			}//for
		}//for
		
		
	}
	
	
	
	
	public void onClick$countryAId() {
		
		logger.info("country clicked");
		/*String selQry = " SELECT country, COUNT(h.hpid) AS allcount, CONCAT(country) " +" as address ";
		String groupByStr = " GROUP BY country";
		*/
		
		HomesPassedEnum countryEnum = HomesPassedEnum.COUNTRY;
		
		prepareData("",countryEnum, countryAId, gridRowsId);
		
		
	}
	public void onClick$stateAId() {
		
		
		prepareData("",HomesPassedEnum.STATE, stateAId, gridRowsId);
		
			
			
		}
	public void onClick$districtAId() {
		
		
		prepareData("",HomesPassedEnum.DISTRICT, districtAId, gridRowsId);
		
		
	}
	public void onClick$cityAId() {
		
		prepareData("",HomesPassedEnum.CITY, cityAId, gridRowsId);
		
	}
	
	public void onClick$zipAId() {
		
		prepareData("",HomesPassedEnum.ZIP, zipAId, gridRowsId);
		
	}
	public void onClick$areaAId() {
		
		prepareData("",HomesPassedEnum.AREA, areaAId, gridRowsId);		
		
	}
	public void onClick$streetAId() {
		
		prepareData("",HomesPassedEnum.STREET, streetAId, gridRowsId);
		
	}
	public void onClick$addrOneAId() {
		
		prepareData("",HomesPassedEnum.ADDRESSONE, addrOneAId, gridRowsId);
	}
	public void onClick$addrTwoAId() {
		
		prepareData("",HomesPassedEnum.ADDRESSTWO, addrTwoAId, gridRowsId);
		
	}

	public void prepareData(String parentAndCond, HomesPassedEnum targetEnum, A targetAnchId, Rows parentRows) {
		
		
		if(segmentsLbId.getItemCount() == 0) {
			
			MessageUtil.setMessage("No segmentation rule found.", "color:red;");
			return;
			
		}
		
		if(parentRows == gridRowsId) {
			
			Components.removeAllChildren(gridRowsId);
		}
		
		if(segmentsLbId.getSelectedIndex() == 0) {
			MessageUtil.setMessage("Please select segmentation rule.", "color:red;");
			return;
		}
		
		if(targetAnchId != null){
			
			setALinksStyle(targetAnchId);
			filterOnLblColId.setLabel(targetAnchId.getLabel());
			
		}
		
		
		
		SegmentRules selSegRule = segmentsLbId.getSelectedItem().getValue();
		
		String stndardQry = selSegRule.getTotSegQuery();
		
		if(stndardQry == null || stndardQry.trim().length() == 0) {
			//TODO
			
			return;
		}
		
		String subQry = stndardQry.substring(stndardQry.indexOf(" FROM "));
		
		//to get contact count and homes count
		String contactsCntStr = "";
		if(subQry.contains(" contacts c ")) {
			
			if(!subQry.contains("homespassed h")) {
				
				subQry = subQry.replace("contacts c", " contacts c, homespassed h ");
				logger.info("subQry :: "+subQry);
				
			}
			contactsCntStr =" COUNT(DISTINCT c.external_id) AS contactCount , ";
		}
		
		
		String countQry = "SELECT COUNT(DISTINCT h.address_unit_id) AS allcount "+subQry+" ";
		if(contactsCntStr.length()>0) {
			 countQry = "SELECT COUNT(DISTINCT h.address_unit_id) AS allcount, COUNT(DISTINCT c.external_id) AS contactCount "+subQry+" ";
		}
		logger.info("countQry ::"+countQry);
		
		List<Map<String, Object>> countList = segmentRulesDao.executeJdbcQueryForList(countQry);
		
		if(countList == null ) {
			
			
			logger.info("problem while executing QRY");
			return;
			
		}
		
		
		logger.info("countList ::" +countList);
		
		long count = 0;
		long contactCount = 0;
		for (Map<String, Object> echMap : countList) {
			
			count = (Long)echMap.get("allcount");
			
			if(echMap.containsKey("contactCount")) 	contactCount = (Long)echMap.get("contactCount");
			
			
		}
		descLblId.setValue(selSegRule.getDescription() != null ? selSegRule.getDescription() : "");
		descLblId.setTooltiptext(selSegRule.getDescription() != null ? selSegRule.getDescription() : "");
		totCountLblId.setValue(""+count);
		totContactCountLblId.setValue(""+contactCount);
		logger.info("count ::" +count);
		
		//TODO need to populate grid for city
		

	
		
		
		String selQryRepStr = "";
		String grpByQryRepStr = "";
		
		for (int i = 0; i < activityAarr.length; i++) {
			if(activityAarr[i].isVisible()==false) continue;
			
			 HomesPassedEnum currEnum =  (HomesPassedEnum) activityAarr[i].getAttribute("targetEnum");
			 
			 
			 if(selQryRepStr.length()>0) selQryRepStr = " , ', ', " + selQryRepStr;
			 
			 selQryRepStr = " IFNULL (h."+currEnum.getColLabel()+",'--') " +selQryRepStr;
			 
			 if(grpByQryRepStr.length()>0) grpByQryRepStr = grpByQryRepStr + " , ";
			 grpByQryRepStr = grpByQryRepStr + " h."+currEnum.getColLabel(); 
			 
			 if(currEnum.getSerealNum()==targetEnum.getSerealNum()) break;

		} // for i
	
		String selQry = targetEnum.getSelectQry() ;
		String grpByQry = targetEnum.getGroupByStr();

		selQry = selQry.replace("<CONCATSTR>", selQryRepStr).replace("<CONTACTCOUNT>", contactsCntStr);
		
		grpByQry = grpByQry.replace("<GROUPBYSTR>", grpByQryRepStr);
		
		
		
		String filterFromQry = selQry + subQry +" AND h."+targetEnum.getColLabel()+" IS NOT NULL "+ parentAndCond + grpByQry;
		
		
		
		logger.info("filterFromQry :: "+filterFromQry);
		
		/*
		 *  Rows rows=(Rows)schedGrdId.getFellow("schedGrdRowsId");
			List list = rows.getChildren();
			
			for(Object obj:list){
				Row row=(Row)obj;
				CampaignSchedule campShcedule=(CampaignSchedule)row.getValue();
				
				List<Object[]> childList=campaignScheduleDao.getAllChidren(campShcedule.getCsId(),campShcedule.getCampaignId());
				if(childList!=null) {
					logger.debug("/list size is"+childList.size()+"*****"+((Detail)row.getChildren().get(0)).isOpen());
					Detail detail=(Detail)row.getChildren().get(0);
					detail.setStyle("display:block;");
					detail.addEventListener("onOpen", listener);
					
				} 

			}
			
		 */
		
		
		countList = segmentRulesDao.executeJdbcQueryForList(filterFromQry);
		Row row = null;
		Label lbl = null;
		
		if(countList == null ) {
			
			
			logger.info("problem while executing QRY");
			return;
			
		}
		
		for (Map<String, Object> map : countList) {
			
			String colVal = (String)map.get(targetEnum.getColLabel());
			String whrSubQry = parentAndCond+" AND h."+targetEnum.getColLabel()+"='"+colVal+"' ";
			
			row = new Row();
			row.setParent(parentRows);
			
			boolean lastFlag=false;
			boolean visibleFlag=false;
			//to make last detail as invisible
			for (int i = activityAarr.length-1; i >=0; i--) {
				HomesPassedEnum tempEnum = (HomesPassedEnum) activityAarr[i].getAttribute("targetEnum");
				
				if(visibleFlag==false && tempEnum.getSerealNum()==targetEnum.getSerealNum()) {
					lastFlag=true;
					break;
				}
				
				if(visibleFlag==true) break;

				if(activityAarr[i].isVisible()==true) {
					visibleFlag=true;
				}
				

			} // for i
			
			
			Detail detail = new Detail();
			detail.setOpen(false);
			detail.setParent(row);
			detail.setStyle("display:block;");
			detail.setAttribute("parentAndCond", whrSubQry);
			
			detail.setAttribute("targetEnum", targetEnum);
			detail.addEventListener("onOpen", this);
			
			
			if(targetEnum.getChildCode() == -1 || lastFlag) {
				detail.setStyle("display:none;");
				//row.appendChild(new Label(""));	
			}
			else {
				detail.setStyle("display:block;");
				
			}
			
			
			
			
			lbl = new Label(colVal);
			
			
			
			lbl.setParent(row);
			
			long totCount = (Long)map.get("allcount");
			lbl = new Label(""+totCount);
			lbl.setParent(row);
			
			lbl = new Label(""+(Utility.getPercentage(totCount, count, 2)));
			lbl.setParent(row);
			
			long totContactCount = map.get("contactCount")==null ? 0 : (Long)map.get("contactCount");
			
			lbl = new Label(""+totContactCount);
			lbl.setParent(row);
			
			lbl = new Label((String)map.get("address"));
			lbl.setParent(row);
			
			Hbox hbox = new Hbox();
			hbox.setParent(row);
			
			Image img = new Image("/img/icons/export.jpg");
			img.setTooltiptext("Export Homes");
			img.setAttribute("EventName", "Export");
			img.setStyle("cursor:pointer;");
			img.addEventListener("onClick", this);
			img.setStyle("margin-right:8px");
			img.setParent(hbox);
			
			if(totContactCount != 0 ) {
				img = new Image("/img/icons/recipient.gif");
				img.setTooltiptext("Export Customers");
				img.setAttribute("EventName", "Customers");
				img.setStyle("cursor:pointer;");
				img.addEventListener("onClick", this);
				img.setStyle("margin-right:8px");
				img.setParent(hbox);
			}
			
			
		}//for
		
	
		
		
	}
	
	private void setALinksStyle(A tempId) {
		
		
			for (A a : activityAarr) {
				a.setSclass("dashboardMyLinks");
			}
			tempId.setSclass("dashboardMyLinksSelected");
		
		
	} // 
	private Label viewSegRuleWinId$segRuleLblId;
	private Window viewSegRuleWinId;
	
	public void onClick$viewSegRuleAnchId() {
		
		
		if(segmentsLbId.getItemCount() == 0) {
			
			MessageUtil.setMessage("No segmentation rule found.", "color:red;");
			return;
			
		}
		
		
		SegmentRules selRule = segmentsLbId.getSelectedItem().getValue();
		
		if(selRule == null) {
			
			MessageUtil.setMessage("Please select segmentation rule.", "color:red;");
			return;
			
			
		}
		
		
		String segRuleToView = selRule.getSegRuleToView(); 
		if(segRuleToView == null || segRuleToView.trim().length() == 0) {
			
			viewSegRuleWinId$segRuleLblId.setValue(dispRule(selRule.getSegRule()));
		}
		else if(segRuleToView != null &&  segRuleToView.trim().length() > 0){
			viewSegRuleWinId$segRuleLblId.setValue(segRuleToView);
			
		}
		viewSegRuleWinId.doModal();
		
		
		
		
	}//onClick$viewSegRuleAnchId()
	
	
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
	
	
	
	
	@Override
	public void onEvent(Event evt) throws Exception {
		// TODO Auto-generated method stub
		super.onEvent(evt);
		
		Object targetObj = evt.getTarget();
		
		Grid innerGrid = null;
		Row row = null;
		
		Label lbl = null;
		if(targetObj instanceof Detail) {
			
			Detail detail = (Detail)targetObj;
			
			if(detail.getChildren() != null && detail.getChildren().size() > 0) {
				return;
			}
			
			
			HomesPassedEnum targetEnum = (HomesPassedEnum)detail.getAttribute("targetEnum");
			
			
			boolean currFound=false;
			HomesPassedEnum childEnum=null; 
			String dispLabel = "";
			
			for (int i = 0; i < activityAarr.length; i++) {
				
				if(activityAarr[i].isVisible()==false) continue;
				
				childEnum = (HomesPassedEnum) activityAarr[i].getAttribute("targetEnum");
				dispLabel = activityAarr[i].getLabel();
				if(childEnum.getSerealNum()==targetEnum.getSerealNum() ) {
					currFound=true;
					childEnum=null;
					continue;
				}
				
				if(currFound==false) {
					continue;
				}
				else break;

			} // for
			
			
			if(childEnum==null) {
				return;
			}
			
			String parentAndCond = (String)detail.getAttribute("parentAndCond");
			//int count = getAndOccurenceCount(parentAndCond);
			int andCount = parentAndCond.split(" AND ").length-1;
			
			
			Grid grid = new Grid();
			Columns cols = new Columns();
			cols.setParent(grid);
			grid.setParent(detail);
			
			
			Column col = new Column("");
			col.setWidth("40px");
			col.setParent(cols);

			col = new Column(dispLabel);
			col.setSort("auto");
			//col.setWidth((400-((childEnum.getSerealNum()-1)*40))+ "px");
			
			col.setWidth((400-(andCount*41))+ "px");
			
			col.setParent(cols);
			
			col = new Column("Homes #");
			col.setSort("auto");
			col.setAlign("center");
			col.setWidth("120px");
			col.setParent(cols);
			
			col = new Column("% on Total");
			col.setAlign("center");
			col.setSort("auto");
			col.setWidth("120px");
			col.setParent(cols);
			
			col = new Column("Customers #");
			col.setSort("auto");
			col.setAlign("center");
			col.setWidth("120px");
			col.setParent(cols);


			col = new Column("Address");
			col.setSort("auto");
			col.setParent(cols);


			col = new Column("");
			col.setWidth("80px");
			col.setAlign("center");
			col.setParent(cols);
			
			
			Rows rows = new Rows();
			rows.setParent(grid);
			
			
			/*Detail childDetail = new Detail();
			childDetail.setOpen(false);
			childDetail.setParent(row);
			childDetail.setStyle("display:block;");
			//childDetail.setAttribute("parentAndCond", whrSubQry);
			
			//childDetail.setAttribute("targetEnum", targetEnum);
			childDetail.addEventListener("onOpen", this);*/
			
			prepareData(parentAndCond, childEnum, null, rows);
			
			
		}//if
		else if(targetObj instanceof Image) {
			
		//csv	
			Image img = (Image)targetObj;
			Row parentRow = (Row)((Hbox)img.getParent()).getParent();
			String evtName = (String)img.getAttribute("EventName");
			
			if(evtName.equals("Export")) {
				
				List childList = parentRow.getChildren();
				Detail detail = null;
				/*String segmentQry = null;
				HomesPassedEnum targetEnum = null;
				List<Map<String, Object>>  countList = null;*/
				if(childList != null && childList.size() > 0) {
					detail  = (Detail)childList.get(0);
					if(detail != null) {
						
						exportCSV("csv", detail);
						
					}//if
					
				}//if
			
			}//if
			else if(evtName.equals("Customers")) {
				
				List childList = parentRow.getChildren();
				Detail detail = null;
				/*String segmentQry = null;
				HomesPassedEnum targetEnum = null;
				List<Map<String, Object>>  countList = null;*/
				if(childList != null && childList.size() > 0) {
					detail  = (Detail)childList.get(0);
					if(detail != null) {
						
						exportCustomerCSV("csv", detail);
						
					}//if
					
				}//if
				
				
				
			}//else if
			
		}//els eif
		
		
		
		
		
		
	}
	
	
	
	public void exportCSV(String ext, Detail detail) {
		//List<Contacts> list = null;
		long totalContacts = 0;
		String segmentQry = null;
		HomesPassedEnum targetEnum = null;
		List<Map<String, Object>>  countList = null;
		try {
			
			
			segmentQry = (String)detail.getAttribute("parentAndCond");
			targetEnum = (HomesPassedEnum)detail.getAttribute("targetEnum"); 
			
			SegmentRules selSegRule = segmentsLbId.getSelectedItem().getValue();
			
			String stndardQry = selSegRule.getTotSegQuery();
			
			if(stndardQry == null || stndardQry.trim().length() == 0) {
				//TODO
				
				return;
			}
			
			//to form group by qry
			
			String grpByQry = " GROUP BY h.address_unit_id ";
			
			String subQry = stndardQry.substring(stndardQry.indexOf(" FROM "));
			
			
			if(!subQry.contains("homespassed h")) {
				
				subQry = subQry.replace("contacts c", " contacts c, homespassed h ");
				logger.info("subQry :: "+subQry);
				
			}
			
			String selQry = "SELECT h.* "+ subQry + segmentQry+" "+grpByQry;
			
			logger.info("selQry :: "+selQry);
			
			
			totalContacts = contactsDao.getSegmentedContactsCount(selQry);
			
			if(totalContacts == 0) {
				
				MessageUtil.setMessage("No data found for export.", "color:red;");
				return;
				
				
			}
			
			ext = ext.trim();
			String userName = GetUser.getUserName();
			String usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
			File downloadDir = new File(usersParentDirectory + "/" + userName + "/List/download/" );
			if(downloadDir.exists()){
				try {
					FileUtils.deleteDirectory(downloadDir);
					logger.debug(downloadDir.getName() + " is deleted");
				} catch (Exception e) {
					logger.error("Exception ::", e);
					logger.warn(downloadDir.getName() + " is not deleted");
				}
			}
			if(!downloadDir.exists()){
				downloadDir.mkdirs();
			}
			String filePath = "";
			boolean isCustField = false;
			filePath = usersParentDirectory + "/" + userName + "/List/download/HomesPassedSegments" + System.currentTimeMillis() + "." + ext;
			
			
			int size = 1000;
			StringBuffer sb = null;
			File file = new File(filePath);
			BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
			logger.debug("Writing to the file : " + filePath);
			String udfFldsLabel= "";
			String columnStr = "";
			String custFldName = null;
			
			Users user = null;
			/*MailingList mlList = selSegRule.getMailingList();
			user = usersDao.findMlUser(mlList.getUsers().getUserId());
			*/
			
			user = usersDao.findMlUser(selSegRule.getUserId());
			
			
			if(user == null) {
				
				logger.debug("do not Export as user is null....");
				return;
			}
			logger.debug("-------1user---------------"+user.getUserId().longValue());

			List<POSMapping> posMappingsList = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_HOMES_PASSED+"'", user.getUserId());
			
			logger.debug("POS Mapping List :"+posMappingsList);
			
			for (POSMapping posMapping : posMappingsList) {
				
				
				if(udfFldsLabel.length() > 0) udfFldsLabel += ",";
				
				udfFldsLabel += "\""+posMapping.getDisplayLabel().trim()+"\"";
				
			}
			
			
				
					sb = new StringBuffer();
					
						
						countList = segmentRulesDao.executeJdbcQueryForList(selQry);
						//list = contactsDao.getSegmentedContacts(segmentQuery, i, size);
						
						if(countList == null) {
							
							MessageUtil.setMessage("Problem encountered while getting data.", "color:red;");
							return;
							
							
						}//if
						
						logger.debug("sel qry:: "+selQry+"  countList ::"+countList);
					
					logger.debug("Got contacts of size : " + countList.size() );
					
					Object obj = null;
					if(countList.size()>0){
						
						sb.append(udfFldsLabel);
						sb.append("\r\n");

						Method tempMethod = null;
						Set<String> keySet = null;
						for (Map<String, Object> colMap : countList) {
							
							StringBuffer innerSB = new StringBuffer();
							for (POSMapping posMapping : posMappingsList) {
								
								obj = colMap.get(genFieldContMap.get(posMapping.getCustomFieldName()));
									
								
								if(innerSB.length() > 0) innerSB.append(",");
								innerSB.append("\""); innerSB.append(obj == null ? "" : obj); innerSB.append("\"");
								
							}//for
							
							sb.append(innerSB);
							sb.append("\r\n");
						}//for
						
						
						/*for (Contacts contact : list) {
							
							innerSB.setLength(0);
								for (POSMapping posMapping : posMappingsList) {
									if(genFieldContMap.containsKey(posMapping.getCustomFieldName())) {
										tempMethod = Contacts.class.getMethod(genFieldContMap.get(posMapping.getCustomFieldName()));
										
									}
									
									if(tempMethod != null) {
										Object obj = tempMethod.invoke(contact);
										String value = null;
										if(obj != null && obj instanceof Calendar) {
											
											obj = MyCalendar.calendarToString((Calendar)obj, MyCalendar.FORMAT_DATETIME_STYEAR);
											
											
										}//if
										
										
										if(innerSB.length() > 0) innerSB.append(",");
										innerSB.append("\""); innerSB.append(obj == null ? "" : obj); innerSB.append("\"");
										
									}
									
									
								}
							sb.append(innerSB);
							sb.append("\r\n");
							
							
						}
					}*/
					bw.write(sb.toString());
					countList = null;
					//System.gc();
				}
			
			bw.flush();
			bw.close();
			logger.debug("----end---");
			
			Filedownload.save(file, "text/plain");
			logger.debug("exited");
			
			
				
		} catch (Exception e) {
			logger.error("** Exception : " , e);
		}
	}
	
	public void exportCustomerCSV(String ext, Detail detail) {
		//List<Contacts> list = null;
		long totalContacts = 0;
		String segmentQry = null;
		HomesPassedEnum targetEnum = null;
		List<Map<String, Object>>  countList = null;
		try {
			
			
			segmentQry = (String)detail.getAttribute("parentAndCond");
			targetEnum = (HomesPassedEnum)detail.getAttribute("targetEnum"); 
			
			SegmentRules selSegRule = segmentsLbId.getSelectedItem().getValue();
			
			String stndardQry = selSegRule.getTotSegQuery();
			
			if(stndardQry == null || stndardQry.trim().length() == 0) {
				//TODO
				
				return;
			}
			
			//to form group by qry
			
			String grpByQry = " GROUP BY c.external_id ";
			
			String subQry = stndardQry.substring(stndardQry.indexOf(" FROM "));
			
			if(!subQry.contains("homespassed h")) {
				
				subQry = subQry.replace("contacts c", " contacts c, homespassed h ");
				logger.info("subQry :: "+subQry);
				
			}
			
			
			String selQry = "SELECT c.* "+ subQry + segmentQry+" "+grpByQry;
			
			logger.info("selQry :: "+selQry);
			
			
			totalContacts = contactsDao.getSegmentedContactsCount(selQry);
			
			if(totalContacts == 0) {
				
				MessageUtil.setMessage("No data found for export.", "color:red;");
				return;
				
				
			}
			
			ext = ext.trim();
			String userName = GetUser.getUserName();
			String usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
			File downloadDir = new File(usersParentDirectory + "/" + userName + "/List/download/" );
			if(downloadDir.exists()){
				try {
					FileUtils.deleteDirectory(downloadDir);
					logger.debug(downloadDir.getName() + " is deleted");
				} catch (Exception e) {
					logger.error("Exception ::", e);
					logger.warn(downloadDir.getName() + " is not deleted");
				}
			}
			if(!downloadDir.exists()){
				downloadDir.mkdirs();
			}
			String filePath = "";
			boolean isCustField = false;
			filePath = usersParentDirectory + "/" + userName + "/List/download/HomesPassedSegments_Customers" + System.currentTimeMillis() + "." + ext;
			
			
			int size = 1000;
			StringBuffer sb = null;
			File file = new File(filePath);
			BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
			logger.debug("Writing to the file : " + filePath);
			String udfFldsLabel= "";
			String columnStr = "";
			String custFldName = null;
			
			Users user = null;
		/*	MailingList mlList = selSegRule.getMailingList();
			user = usersDao.findMlUser(mlList.getUsers().getUserId());
			*/
			
			user = usersDao.findMlUser(selSegRule.getUserId());
			
			if(user == null) {
				
				logger.debug("do not Export as user is null....");
				return;
			}
			logger.debug("-------1user---------------"+user.getUserId().longValue());

			List<POSMapping> posMappingsList = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_CONTACTS+"'", user.getUserId());
			
			logger.debug("POS Mapping List :"+posMappingsList);
			
			for (POSMapping posMapping : posMappingsList) {
				
				
				if(udfFldsLabel.length() > 0) udfFldsLabel += ",";
				
				udfFldsLabel += "\""+posMapping.getDisplayLabel().trim()+"\"";
				
			}
			
			
				
					sb = new StringBuffer();
					
						
						countList = segmentRulesDao.executeJdbcQueryForList(selQry);
						//list = contactsDao.getSegmentedContacts(segmentQuery, i, size);
						
						if(countList == null) {
							
							MessageUtil.setMessage("Problem encountered while getting data.", "color:red;");
							return;
							
							
						}//if
						
						logger.debug("sel qry:: "+selQry+"  countList ::"+countList);
					
					logger.debug("Got contacts of size : " + countList.size() );
					
					Object obj = null;
					if(countList.size()>0){
						
						sb.append(udfFldsLabel);
						sb.append("\r\n");

						Method tempMethod = null;
						Set<String> keySet = null;
						for (Map<String, Object> colMap : countList) {
							
							StringBuffer innerSB = new StringBuffer();
							for (POSMapping posMapping : posMappingsList) {
								
								obj = colMap.get(genFieldCustomersMap.get(posMapping.getCustomFieldName()));
								
								
								if(innerSB.length() > 0) innerSB.append(",");
								innerSB.append("\""); innerSB.append(obj == null ? "" : obj); innerSB.append("\"");
								
							}//for
							
							sb.append(innerSB);
							sb.append("\r\n");
						}//for
						
						
						/*for (Contacts contact : list) {
							
							innerSB.setLength(0);
								for (POSMapping posMapping : posMappingsList) {
									if(genFieldContMap.containsKey(posMapping.getCustomFieldName())) {
										tempMethod = Contacts.class.getMethod(genFieldContMap.get(posMapping.getCustomFieldName()));
										
									}
									
									if(tempMethod != null) {
										Object obj = tempMethod.invoke(contact);
										String value = null;
										if(obj != null && obj instanceof Calendar) {
											
											obj = MyCalendar.calendarToString((Calendar)obj, MyCalendar.FORMAT_DATETIME_STYEAR);
											
											
										}//if
										
										
										if(innerSB.length() > 0) innerSB.append(",");
										innerSB.append("\""); innerSB.append(obj == null ? "" : obj); innerSB.append("\"");
										
									}
									
									
								}
							sb.append(innerSB);
							sb.append("\r\n");
							
							
						}
					}*/
					bw.write(sb.toString());
					countList = null;
					//System.gc();
				}
			
			bw.flush();
			bw.close();
			logger.debug("----end---");
			
			Filedownload.save(file, "text/plain");
			logger.debug("exited");
			
			
				
		} catch (Exception e) {
			logger.error("** Exception : " , e);
		}
	}

	
	
	
	
	
}

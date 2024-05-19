package org.mq.marketer.campaign.controller.contacts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.SegmentRules;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.SegmentRulesDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.SalesQueryGenerator;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.data.dao.ContactsJdbcResultsetHandler;
import org.mq.optculture.data.dao.JdbcResultsetHandler;
import org.mq.optculture.utils.OCCSVWriter;
import org.mq.optculture.utils.OCConstants;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.event.PagingEvent;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Window;



public class ViewSegmentedContactsController extends GenericForwardComposer implements EventListener{
	
	private Paging segmentContactsPagingId;
	
	private Session session;
	private ContactsDao contactsDao;
	private SegmentRulesDao segmentRulesDao;
	private SegmentRules segmentRule;
	private String segmentQuery;
	
	private Listbox segcontactListLBId;
	private TimeZone clientTimeZone;
	
	private Combobox exportCbId;
	
	private MailingListDao mailingListDao;
	private UsersDao usersDao;
	private POSMappingDao posMappingDao;
	
	private String generatedCountQuery;
	private Long userId;
	private Window custExport;
	private Div custExport$chkDivId;
	
	private String label;

	private ContactsLoyaltyDao contactsLoyaltyDao;
	private ContactsLoyalty contactsLoyalty;
	
	
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private static Map<String, String> genFieldContMap = new LinkedHashMap<String, String>();
	
	static{
		
		genFieldContMap.put("Email", "c.email_id");
		genFieldContMap.put("Mobile", "c.mobile_phone");
		genFieldContMap.put("First Name", "c.first_name");
		genFieldContMap.put("Last Name", "c.last_name");
		genFieldContMap.put("Street", "c.address_one");
		genFieldContMap.put("Address Two", "c.address_two");
		genFieldContMap.put("City", "c.city");
		genFieldContMap.put("State", "c.state");
		genFieldContMap.put("Country", "c.country");
		genFieldContMap.put("ZIP", "c.zip");
		genFieldContMap.put("Customer ID", "c.external_id" );
		genFieldContMap.put("Gender", "c.gender");
		genFieldContMap.put("BirthDay", "c.birth_day");
		genFieldContMap.put("Anniversary", "c.anniversary_day");
		genFieldContMap.put("Home Store", "c.home_store");
		genFieldContMap.put("UDF1", "c.udf1");
		genFieldContMap.put("UDF2", "c.udf2");
		genFieldContMap.put("UDF3", "c.udf3");
		genFieldContMap.put("UDF4", "c.udf4");
		genFieldContMap.put("UDF5", "c.udf5");
		genFieldContMap.put("UDF6", "c.udf6");
		genFieldContMap.put("UDF7", "c.udf7");
		genFieldContMap.put("UDF8", "c.udf8");
		genFieldContMap.put("UDF9", "c.udf9");
		genFieldContMap.put("UDF10", "c.udf10");
		genFieldContMap.put("UDF11", "c.udf11");
		genFieldContMap.put("UDF12", "c.udf12");
		genFieldContMap.put("UDF13", "c.udf13");
		genFieldContMap.put("UDF14", "c.udf14");
		genFieldContMap.put("UDF15", "c.udf15");
	}
	
	
	public ViewSegmentedContactsController() {
		
		contactsDao = (ContactsDao)SpringUtil.getBean("contactsDao");
		segmentRulesDao = (SegmentRulesDao)SpringUtil.getBean("segmentRulesDao");
		mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
		posMappingDao = (POSMappingDao)SpringUtil.getBean("posMappingDao");
		usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		session = Sessions.getCurrent();
		userId = GetUser.getUserObj().getUserId();
		clientTimeZone = (TimeZone)session.getAttribute("clientTimeZone");
		segmentRule = (SegmentRules)session.getAttribute("SegmentRule");
		//segmentQuery = (String)session.getAttribute("SegmentQuery");
		label=(String)session.getAttribute("labelType");
		
		this.contactsLoyaltyDao = (ContactsLoyaltyDao)SpringUtil.getBean("contactsLoyaltyDao");
		
		 String style = "font-weight:bold;font-size:15px;color:#313031;" +
			"font-family:Arial,Helvetica,sans-serif;align:left";
		 PageUtil.setHeader("Segmented Contacts","",style,true);

	}
	
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		
		
		//long starttimer = System.currentTimeMillis();
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		if(segmentRule == null ) {
			
			logger.debug("got segment rule as null....,returning");
			Redirect.goToPreviousPage();
			return;
		
		}
		/*if(segmentRule != null && (segmentQuery == null || segmentQuery.trim().length() == 0)) {
			
			segmentQuery = segmentRule.getEmailSegQuery();
			
		}*/
		List<MailingList> mlList = mailingListDao.findByIds(segmentRule.getSegmentMlistIdsStr());
		
		Set<MailingList> mlSet = new HashSet<MailingList>();
		long mlBit = 0;
		if(mlList != null)  {
			mlSet.addAll(mlList);
			 mlBit = Utility.getMlsBit(mlSet);
			
			}
		
		//label=(String)session.getAttribute("labelType");
		logger.debug("****label type got **********  " +label  );
		
		/*generatedCountQuery = SalesQueryGenerator.generateListSegmentCountQuery(segmentRule.getSegRule(), true, label,mlBit);
		segmentQuery = SalesQueryGenerator.generateListSegmentQuery(segmentRule.getSegRule(), true, label,mlBit);*/
		
		if(Constants.SEGMENT_ON_EXTERNALID.equals(label)){
			generatedCountQuery = SalesQueryGenerator.generateListSegmentCountQuery(segmentRule.getSegRule(), true, label,mlBit);
			segmentQuery = SalesQueryGenerator.generateListSegmentQuery(segmentRule.getSegRule(), true, label,mlBit);
		}
		else{
			generatedCountQuery = SalesQueryGenerator.generateListSegmentCountQuery(segmentRule.getSegRule(), false, label,mlBit);
			segmentQuery = SalesQueryGenerator.generateListSegmentQuery(segmentRule.getSegRule(), false, label,mlBit);
			
			generatedCountQuery = generatedCountQuery.replace("<MOBILEOPTIN>","");
			segmentQuery = segmentQuery.replace("<MOBILEOPTIN>","");
		}
		
		
		if(SalesQueryGenerator.CheckForIsLatestCamapignIdsFlag(segmentRule.getSegRule())) {
			String csCampIds = SalesQueryGenerator.getCamapignIdsFroFirstToken(segmentRule.getSegRule());
			
			if(csCampIds != null ) {
				String crIDs = Constants.STRING_NILL;
				CampaignsDao campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
				List<Object[]> campList = campaignsDao.findAllLatestSentCampaignsBySql(segmentRule.getUserId(), csCampIds);
				if(campList != null) {
					for (Object[] crArr : campList) {
						
						if(!crIDs.isEmpty()) crIDs += Constants.DELIMETER_COMMA;
						crIDs += ((Long)crArr[0]).longValue();
						
					}
				}
				
				generatedCountQuery = generatedCountQuery.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH, ("AND cr_id in("+crIDs+")"));
				segmentQuery = segmentQuery.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH, ("AND cr_id in("+crIDs+")"));
			}
		}
		logger.debug("****regen query **********  " +generatedCountQuery  );
		
		
		
		//int totalsize = contactsDao.getSegmentedContactsCount(segmentQuery);
		
		int totalsize = contactsDao.getSegmentedContactsCount(generatedCountQuery);
		
		segmentContactsPagingId.setTotalSize(totalsize);
		segmentContactsPagingId.addEventListener("onPaging", this);
		
		
		redraw(segmentQuery, 0, segmentContactsPagingId.getPageSize());
		
		/*logger.fatal("Total Time Taken :: ViewSegmentedContactsController ::  doAfterCompose "
				+ (System.currentTimeMillis() - starttimer));*/
		
	}
	
	public void redraw(String segmentQuery, int startIndex, int count) {
		List<Contacts> segmentContactsList = new ArrayList<Contacts>();
		
		boolean fetchIndividual = false;
		if(segmentQuery.startsWith(OCConstants.replacedSegmentQryPrefiX)){
			segmentContactsList = contactsDao.getSegmentedLtyContacts(segmentQuery, startIndex, count);
		}
		else{
			 fetchIndividual = true;
			segmentContactsList = contactsDao.getSegmentedContacts(segmentQuery, startIndex, count);
		}
		
		int itemCount = segcontactListLBId.getItemCount();
		for (int i = itemCount; i > 0; i--) {
			
			segcontactListLBId.removeItemAt(i-1);
		}
		
		Listitem item = null;
		Listcell lc = null;
		logger.debug("got the list of size ::"+segmentContactsList.size());
		for (Contacts contact : segmentContactsList) {
			
			item = new Listitem();
			item.setValue(contact);
			
			
			//Email
			lc = new Listcell();
			
			String email = contact.getEmailId();
			lc.setLabel(email == null || email.trim().equals("") ? "--" : email );
			item.appendChild(lc);
			
			//Mobile
			/*if(contact.getPhone() ==null || contact.getPhone() == 0 ) {
				item.appendChild(new Listcell("--"));
			}
			else {
				item.appendChild(new Listcell(""+contact.getPhone()));
			}*/
			if(contact.getMobilePhone() == null || contact.getMobilePhone().length() == 0 ) {
				item.appendChild(new Listcell("--"));
			}
			else {
				item.appendChild(new Listcell(contact.getMobilePhone()));
			}
			
			//loyalty columns
			if(fetchIndividual) {
				String cardNumber=null;
				Double balancePoints=null;
				Double rewardBalance=null;
				
				List<ContactsLoyalty> loyaltyList = contactsLoyaltyDao.findLoyaltyListByContactId(userId, contact.getContactId());
				if(loyaltyList != null && loyaltyList.size() > 0){
					Iterator<ContactsLoyalty> iterList = loyaltyList.iterator();
					ContactsLoyalty latestLoyalty = null;
					ContactsLoyalty iterLoyalty = null;
					while(iterList.hasNext()){
						iterLoyalty = iterList.next();
						if(latestLoyalty != null && latestLoyalty.getCreatedDate().after(iterLoyalty.getCreatedDate())){
							continue;
						}
						latestLoyalty = iterLoyalty;
					}


					
					//get latest card Number			
					cardNumber=latestLoyalty.getCardNumber().toString();
					
					//get latest balance points
					balancePoints=latestLoyalty.getLoyaltyBalance();
					
					//get latest reward balance
					rewardBalance=latestLoyalty.getGiftcardBalance() ;
					
					
					
					contact.setCardNumber(cardNumber);
					contact.setRewardBalance(rewardBalance);
					contact.setPoints(balancePoints);
					
				}
				
				//to add Card Number
/*				if(cardNumber == null || cardNumber.equals("")){
					item.appendChild(new Listcell("--"));
				}
				else{
					item.appendChild(new Listcell(cardNumber));
				}
*/
			}
						
			//card number
			if(contact.getCardNumber()==null || contact.getCardNumber().trim().length() == 0 ) {
				item.appendChild(new Listcell("--"));
			}
			else {
				item.appendChild(new Listcell(contact.getCardNumber()));
			}
			
			//First Name
			if(contact.getFirstName()==null || contact.getFirstName().trim().length() == 0 ) {
				item.appendChild(new Listcell("--"));
			}
			else {
				item.appendChild(new Listcell(contact.getFirstName()));
			}
			
			//Last Name
			if(contact.getLastName()==null || contact.getLastName().trim().length() == 0 ) {
				item.appendChild(new Listcell("--"));
			}
			else {
				item.appendChild(new Listcell(contact.getLastName()));
			}
			//item.appendChild(new Listcell((mlMap.get(contact.getMailingList().getListId())).getListName()));
			
			//Created Date
			String date =MyCalendar.calendarToString(contact.getCreatedDate(), MyCalendar.FORMAT_DATEONLY);
			//to add Created Date
			if(date == null || date.equals("")){
				item.appendChild(new Listcell("--"));
			}
			else{
				item.appendChild(new Listcell(date));
			}		
			//Email Status
			if(contact.getEmailStatus() == null || contact.getEmailStatus().equals("")) {
				item.appendChild(new Listcell("--"));
			} 
			else {
				
				item.appendChild(new Listcell(contact.getEmailStatus()));
			}
			
			//MObile Status
			if(contact.getMobileStatus() == null || contact.getMobileStatus().equals("")) {
				item.appendChild(new Listcell(Constants.CON_MOBILE_STATUS_PENDING));
			} 
			else {
				
				item.appendChild(new Listcell(contact.getMobileStatus()));
			}
			//Last Mail Date
			if(contact.getLastMailDate() == null) {
				item.appendChild(new Listcell("--"));
			}
			else {
			item.appendChild(new Listcell(MyCalendar.calendarToString(
					contact.getLastMailDate(), MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone)));
			}
			
			/*//to add Balance Points
			if(balancePoints == null || balancePoints.equals("")){
				item.appendChild(new Listcell("--"));
			}
			else{
				item.appendChild(new Listcell(balancePoints.toString()));
			}
			
			//to add Reward Balance
			if(rewardBalance == null || rewardBalance.equals("")){
				item.appendChild(new Listcell("--"));
			}
			else{
				item.appendChild(new Listcell(Utility.getAmountInUSFormat(rewardBalance)));
			}*/
			//to add Balance Points
			if(contact.getPoints() == null || contact.getPoints().equals("")){
				item.appendChild(new Listcell("--"));
			}
			else{
				item.appendChild(new Listcell(contact.getPoints().toString()));
			}
			
			//to add Reward Balance
			if(contact.getRewardBalance() == null || contact.getRewardBalance().equals("")){
				item.appendChild(new Listcell("--"));
			}
			else{
				item.appendChild(new Listcell(Utility.getAmountInUSFormat(contact.getRewardBalance())));
			}

			
			item.setParent(segcontactListLBId);
		}
		
		
	}//redraw()
	
	
	public void onClick$backBtnId() {
		try {
			Redirect.goToPreviousPage();
		} catch (Exception e) {
			logger.error("Error occured from the gotoMylists method ***",e);
		}
	}
	
	// starts from here
	
	/*public void onClick$exportBtnId() {
		try {
			exportCSV((String)exportCbId.getSelectedItem().getValue());
		} catch (Exception e) {
			logger.error("Error occured from the exportCSV method ***",e);
		}
	}
	*/
	// main export method
	
	/*public void exportCSV(String ext) {
		List<Contacts> list = null;
		long totalContacts = 0;
		try {
			if(segmentQuery  == null) {
				logger.warn("Got segment query as null....returning");
				return;
			}
				
				//totalContacts = contactsDao.getSegmentedContactsCount(segmentQuery);
				totalContacts = contactsDao.getSegmentedContactsCount(generatedCountQuery);
			
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
			filePath = usersParentDirectory + "/" + userName + "/List/download/Segmented_Contacts_" + System.currentTimeMillis() + "." + ext;
			
			
			int size = 1000;
			StringBuffer sb = null;
			File file = new File(filePath);
			BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
			logger.debug("Writing to the file : " + filePath);
			String udfFldsLabel= "";
			String columnStr = "";
			String custFldName = null;
			
			Users user = null;
			MailingList mlList = segmentRule.getMailingList();
			user = usersDao.findMlUser(mlList.getUsers().getUserId());
			
			
			//MailingList mlList = segmentRule.getMailingList();
			user = usersDao.findMlUser(segmentRule.getUserId());
			
			
			if(user == null) {
				
				logger.debug("do not Export as user is null....");
				return;
			}
			logger.debug("-------1user---------------"+user.getUserId().longValue());

			List<POSMapping> posMappingsList = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_CONTACTS+"'", user.getUserId());
			
			//logger.debug("POS Mapping List :"+posMappingsList);
			
			
			Map<String, POSMapping> orderedMappingMap = getOrderedMappingSet(posMappingsList);
			
			//logger.debug("map ::"+orderedMappingMap);
			
			for (String custFldKey : orderedMappingMap.keySet()) {
				
				if(udfFldsLabel.length() > 0) udfFldsLabel += ",";
				
				udfFldsLabel += "\""+orderedMappingMap.get(custFldKey).getDisplayLabel().trim()+"\"";

				
				
				
			}//for
			
			
			
			
			
			
			
			for (POSMapping posMapping : posMappingsList) {
				
				
				if(udfFldsLabel.length() > 0) udfFldsLabel += ",";
				
				udfFldsLabel += "\""+posMapping.getDisplayLabel().trim()+"\"";
				
			}
				
			
			
			sb = new StringBuffer();
			sb.append(udfFldsLabel);
			sb.append("\r\n");
			bw.write(sb.toString());
			
				for(int i=0;i < totalContacts; i+=size){
					sb = new StringBuffer();
					
						
						list = contactsDao.getSegmentedContacts(segmentQuery, i, size);
						
					
					logger.debug("Got contacts of Start index: " + i + " size : " + list.size() );
					if(list.size()>0){
						

						Method tempMethod = null;
						
						for (Contacts contact : list) {
							StringBuffer innerSB = new StringBuffer();
							
							innerSB.setLength(0);
							for (String custFldKey : orderedMappingMap.keySet()) {
								
								POSMapping posMapping = orderedMappingMap.get(custFldKey);
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
					}
					bw.write(sb.toString());
					list = null;
					System.gc();
				}
			
			bw.flush();
			bw.close();
			//logger.debug("----end---");
			
			Filedownload.save(file, "text/plain");
			logger.debug("exited");
		} catch (Exception e) {
			logger.error("** Exception : " , e);
		}
	}*/
	
	private Listbox pageSizeLbId;
	public void onSelect$pageSizeLbId() {
		
		int n = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		segmentContactsPagingId.setPageSize(n);
		segmentContactsPagingId.setActivePage(0);
		redraw(segmentQuery, 0, n);
		
		
	}//onSelect$pageSizeLbId()
	
	
	
	@Override
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		super.onEvent(event);
		
		Object obj = event.getTarget();
		if(obj instanceof Paging) {
			
			
			Paging paging = (Paging) obj;
			int desiredPage = paging.getActivePage();
			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
			redraw(segmentQuery, ofs, paging.getPageSize());
			
			
		}//if
		
		
		
	}
	
	public Map<String, POSMapping> getOrderedMappingSet(List<POSMapping> mappingList) {
		
		
		Map<String,	POSMapping> orderedMap = new LinkedHashMap<String, POSMapping>();
		for (String custFldkey : genFieldContMap.keySet()) {
			
			//logger.debug("keySet is::"+custFldkey);
			
			
			for (POSMapping posMapping : mappingList) {
				
				if(posMapping.getCustomFieldName().equals(custFldkey)) {
					
					orderedMap.put(custFldkey, posMapping);
					break;
				}
				
			}
			
		}
		
		return orderedMap;
		
		
	}
	
	/*************************************************/
	
	public void onClick$exportBtnId() {
		try {
			if(userId!=null){
				
				createWindow();
				anchorEvent(false);
				
				
				
				custExport.setVisible(true);
				custExport.doHighlighted();
			}
			else{
				
				MessageUtil.setMessage("Please select a user", "info");
			}
		} catch (Exception e) {
			logger.error("Error occured from the exportCSV method ***",e);
		}
	}
	
public void onClick$selectAllAnchr$custExport() {
	
	anchorEvent(true);

}

public void onClick$clearAllAnchr$custExport() {
	
	anchorEvent(false);
}

public void anchorEvent(boolean flag) {
	List<Component> chkList = custExport$chkDivId.getChildren();
	Checkbox tempChk = null;
	for (int i = 0; i < chkList.size(); i++) {
		if(!(chkList.get(i) instanceof Checkbox)) continue;
		
		tempChk = (Checkbox)chkList.get(i);
		tempChk.setChecked(flag);
		
	} // for

}


/*
public void onClick$selectFieldBtnId$custExport() {

	custExport.setVisible(false);
	List<Component> chkList = custExport$chkDivId.getChildren();

	int indexes[]=new int[chkList.size()];
	
	boolean checked=false;
	
	for(int i=0;i<chkList.size();i++) {
		indexes[i]=-1;
	} // for
*/	

	//Labels
	private String[] contactLoyaltyLabelsOne=new String[]{"Membership Number","Card Pin"};
	private String[] contactLoyaltyLabelsTwo= new String[]{"Points","Reward Balance","Gift Balance","Hold Points","Hold Currency"};



	public void onClick$selectFieldBtnId$custExport() {


		custExport.setVisible(false);
		List<Component> chkList = custExport$chkDivId.getChildren();


		int indexes[]=new int[chkList.size()];
		int[] loyaltyIndexsOne=new int[contactLoyaltyLabelsOne.length];
		int[] loyaltyIndexsTwo = new int[contactLoyaltyLabelsTwo.length];
		String query = "select ";


		boolean checked=false;

		for(int i=0;i<contactLoyaltyLabelsOne.length;i++) {
			loyaltyIndexsOne[i]=-1;
		} // for

		for(int i=0;i<contactLoyaltyLabelsTwo.length;i++) {
			loyaltyIndexsTwo[i]=-1;
		} // for

		for(int i=0;i<chkList.size();i++) {
			indexes[i]=-1;
		} 

		Checkbox tempChk = (Checkbox)custExport$chkDivId.getFirstChild();
		//Created Date
		if( tempChk.isChecked()) {

			indexes[0]=0;
			checked=true;
			query += tempChk.getValue();
		}

		//Email
		tempChk = (Checkbox)chkList.get(1);
		if(tempChk.getLabel().equalsIgnoreCase("Email") ) {

			if(tempChk.isChecked()){
				indexes[1]=0;
				checked=true;
				query += tempChk.getValue();
			}
			else{
				indexes[1]=-1;
			}
		}

		//card number
		tempChk = (Checkbox)chkList.get(2);
		if(tempChk.getLabel().equalsIgnoreCase("Membership Number") ) {

			if(tempChk.isChecked()){
				loyaltyIndexsOne[0]=0;
				checked=true;
				query += tempChk.getValue();
			}
			else{
				loyaltyIndexsOne[0]=-1;
			}
		}
		//card Pin
		tempChk = (Checkbox)chkList.get(3);
		if(tempChk.getLabel().equalsIgnoreCase("Card Pin") ) {

			if(tempChk.isChecked()){
				loyaltyIndexsOne[1]=0;
				checked=true;
				query += tempChk.getValue();
			}
			else{
				loyaltyIndexsOne[1]=-1;
			}
		}//if

		//Mobile Number

		tempChk = (Checkbox)chkList.get(4);
		if(tempChk.getLabel().equalsIgnoreCase("Mobile Number") ) {

			if(tempChk.isChecked()){
				indexes[4]=0;
				checked=true;
				query += tempChk.getValue();
			}
			else{
				indexes[4]=-1;
			}
		}
		//Loyalty Balances
				for(int i=0; i < 5; i++)
				{
				tempChk = (Checkbox)chkList.get(i+5);
				if(tempChk.getLabel().equalsIgnoreCase(contactLoyaltyLabelsTwo[i]) ) {

					if(tempChk.isChecked()){
						loyaltyIndexsTwo[i]=0;
						checked=true;
						query += tempChk.getValue();
					}
					else{
						loyaltyIndexsTwo[i]=-1;
					}
				}
				}
		/*//rewards balance
		tempChk = (Checkbox)chkList.get(6);
		if(tempChk.getLabel().equalsIgnoreCase("Reward Balance") ) {

			if(tempChk.isChecked()){
				loyaltyIndexsTwo[1]=0;
				checked=true;
				query += tempChk.getValue();
			}
			else{
				loyaltyIndexsTwo[1]=-1;
			}
		}//if
*/

		for (int i = 0; i < chkList.size(); i++) {
			if(!(chkList.get(i) instanceof Checkbox)) continue;

			tempChk = (Checkbox)chkList.get(i);
			if(tempChk.getLabel().equalsIgnoreCase("Created Date") ||tempChk.getLabel().equalsIgnoreCase("Email") || tempChk.getLabel().equalsIgnoreCase("Membership Number") ||
					tempChk.getLabel().equalsIgnoreCase("Card Pin") || tempChk.getLabel().equalsIgnoreCase("Mobile Number") ||
					tempChk.getLabel().equalsIgnoreCase("Points") ||tempChk.getLabel().equalsIgnoreCase("Reward Balance") || tempChk.getLabel().equalsIgnoreCase("Gift Balance")
					|| tempChk.getLabel().equalsIgnoreCase("Hold Points")|| tempChk.getLabel().equalsIgnoreCase("Hold Currency") )  {
				continue;
			}

			if(tempChk.isChecked()) {
				indexes[i]=0;
				checked=true;
				if(query.indexOf((String)tempChk.getValue()) == -1 ) {
					query += tempChk.getValue();
				}
			}
			
		} // for

		query = query.substring(0, query.length()-2);
		if(checked) {

			int confirm=Messagebox.show("Do you want to Export with selected fields ?", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm==1){
				try{

					exportCSV(query, (String)exportCbId.getSelectedItem().getValue(), indexes,loyaltyIndexsOne, loyaltyIndexsTwo);

				}catch(Exception e){
					logger.error("Exception caught :: ",e);
				}
			}
			else{
				custExport.setVisible(true);
			}

		}
		else {

			MessageUtil.setMessage("Please select atleast one field", "red");
			custExport.setVisible(false);
		}

	}



	public void exportCSV(String query, String ext,int[] indexes,int[] loyaltyIndexsOne, int[] loyaltyIndexsTwo) {

		Long startTime = System.currentTimeMillis();
		JdbcResultsetHandler jdbcResultsetHandler = null;
		String userName = Constants.STRING_NILL;
		String usersParentDirectory = Constants.STRING_NILL;
		String filePath = Constants.STRING_NILL;
		File downloadDir = null;
		StringBuffer sb = null;
		File file = null;
		BufferedWriter bw = null;
		String udfFldsLabel= Constants.STRING_NILL;
		Users user = null;
		List<POSMapping> posMappingsList = null;
		Map<String, POSMapping> orderedMappingMap = null;
//		long totalContacts = 0;
		try {
			if(segmentQuery  == null) {
				logger.warn("Got segment query as null....returning");
				return;
			}

			//totalContacts = contactsDao.getSegmentedContactsCount(segmentQuery);
//			totalContacts = contactsDao.getSegmentedContactsCount(generatedCountQuery);


			ext = ext.trim();
			userName = GetUser.getUserName();
			usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
			if(usersParentDirectory == null) {
				logger.error("User Parent directry path is not existed..returning");
				return;
			}
			/*String fileNameStr = "Segmented_Contacts_" + System.currentTimeMillis() + "." + ext;
			MessageUtil.setMessage("Please download the file from Export and The file name is "+fileNameStr, "color : green");
			
			*/
			filePath = usersParentDirectory + "/" + userName + "/List/download/"+segmentRule.getSegRuleName()+"_"+ System.currentTimeMillis() + "." + ext;
			
			
			
			downloadDir = new File(usersParentDirectory + "/" + userName + "/List/download/" );
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
			/*String filePath = "";
			filePath = usersParentDirectory + "/" + userName + "/List/download/Segmented_Contacts_" + System.currentTimeMillis() + "." + ext;
*/

			
			file = new File(filePath);
			bw = new BufferedWriter(new FileWriter(filePath));
			logger.debug("Writing to the file : " + filePath);
			



			//Created Date
			if(indexes[0]==0) {

				udfFldsLabel = "\""+"Created Date"+"\"";
			}

			//Email
			if(indexes[1]==0) {
				if(udfFldsLabel.length() > 0) udfFldsLabel += ",";
				udfFldsLabel += "\""+"Email"+"\"";
			}
			
			int k=0;
			//Card Number , Card Pin
			for (String contactLoyaltyLabel : contactLoyaltyLabelsOne) {

				if(loyaltyIndexsOne[k]==0 && k<loyaltyIndexsOne.length) {
					if(udfFldsLabel.length() > 0) udfFldsLabel += ",";

					udfFldsLabel += "\""+contactLoyaltyLabel+"\"";

					logger.debug("added label now "+contactLoyaltyLabel);
				}
				k++;
			}//for
			
			//Mobile Number
			if(indexes[4]==0) {
				if(udfFldsLabel.length() > 0) udfFldsLabel += ",";
				udfFldsLabel += "\""+"Mobile Number"+"\"";
			}	
			
			
			k=0;
			//Points & Loyalty Balance
			for (String contactLoyaltyLabel : contactLoyaltyLabelsTwo) {

				if(loyaltyIndexsTwo[k]==0 && k<loyaltyIndexsTwo.length) {
					if(udfFldsLabel.length() > 0) udfFldsLabel += ",";

					udfFldsLabel += "\""+contactLoyaltyLabel+"\"";

					logger.debug("added label now "+contactLoyaltyLabel);
				}
				k++;
			}//for
			int indexLength=(indexes.length);

			user = usersDao.findMlUser(segmentRule.getUserId());


			if(user == null) {

				logger.debug("do not Export as user is null....");
				return;
			}
			logger.debug("-------1user---------------"+user.getUserId().longValue());

			posMappingsList = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_CONTACTS+"'", user.getUserId());

			//logger.debug("POS Mapping List :"+posMappingsList);


			orderedMappingMap = getOrderedMappingSet(posMappingsList);

			
			k=10;
			for (String custFldKey : orderedMappingMap.keySet()) {

				if(custFldKey.equalsIgnoreCase("Mobile") || custFldKey.equalsIgnoreCase("Email"))
					continue;

				if(k==indexLength)
					break;
				if(k!=0 || k!=1 || k!=4) {
					if(indexes[k]==0 && k < indexLength) {
						if(udfFldsLabel.length() > 0) udfFldsLabel += ",";

						udfFldsLabel += "\""+orderedMappingMap.get(custFldKey).getDisplayLabel().trim()+"\"";
						//logger.debug("********** headers o wat *****  "+orderedMappingMap.get(custFldKey).getDisplayLabel().trim() );
					}
				}
				k++;
			}//for


			sb = new StringBuffer();
			sb.append(udfFldsLabel);
			sb.append("\r\n");
			bw.write(sb.toString());
			
			if(segmentQuery.startsWith(OCConstants.replacedSegmentQryPrefiX)){
				query += " from ("+segmentQuery+") as c";
			    query = query.replace("cl.","c.");
	        }
	        else{
	            query += " from ("+segmentQuery+") as c left join contacts_loyalty cl on c.cid = cl.contact_id ";
	        }
			jdbcResultsetHandler = new JdbcResultsetHandler();
			jdbcResultsetHandler.executeStmt(query);
			long totalCount = jdbcResultsetHandler.totalRecordsSize();
			logger.info(" >>>>>>>>>>>>>>> total contacts is  ::"+totalCount);
			if(totalCount == 0) {
				logger.info("No records exist from DB  ");
				
			}
			
			OCCSVWriter csvWriter = null;
			try{
				csvWriter = new OCCSVWriter(bw);
				csvWriter.writeAll(jdbcResultsetHandler.getResultSet(), false);
			}
			catch(Exception e){
				logger.info("Exception while writing into file ", e);
			}finally{
				bw.flush();
				csvWriter.flush();
				bw.close();
				csvWriter.close();
			}
			
			/*do{
				
			}
			
			for(int i=0;i < totalContacts; i+=size){
				sb = new StringBuffer();
				
				list = contactjdbcResultsetHandler.getContacts(size);
				logger.info("Total count is  :: "+totalCount+"  and  curent fetch count is::"+contactjdbcResultsetHandler.getCurrentFetchingCount());
				
				if(list != null && list.size()>0){
					

					Method tempMethod = null;

					for (Contacts contact : list) {
						StringBuffer innerSB = new StringBuffer();
						//get the loyalty details
						contactsLoyalty = contactsLoyaltyDao.findByContactId(contact.getContactId());
						innerSB.setLength(0);

						// created date here

						if(indexes[0]==0) {

							if(innerSB.length() > 0) innerSB.append(",");
							innerSB.append("\""); innerSB.append(contact.getCreatedDate() != null ? MyCalendar.calendarToString(contact.getCreatedDate(), MyCalendar.FORMAT_DATEONLY) : ""); innerSB.append("\"");
							//logger.debug("**********  created date value fist for array ************ "+ MyCalendar.calendarToString(contact.getCreatedDate(), MyCalendar.FORMAT_DATEONLY));
						}

						//Email 
						if(indexes[1]==0) {

							if(innerSB.length() > 0) innerSB.append(",");
							innerSB.append("\""); innerSB.append(contact.getEmailId()==  null ? "" : contact.getEmailId()+""); innerSB.append("\"");
							//logger.debug("**********  Email value fist for array ************ "+contact.getEmailId());
						}


						//Card Number & Card Pin


						Object contactLoyaltyValues1[] = {contactsLoyalty ==null ? "" :contactsLoyalty.getCardNumber(),contactsLoyalty ==null ? "" :contactsLoyalty.getCardPin()};

						k=0;
						//ContactLoyaltyLabelValues
						for (Object contactLoyaltyValue : contactLoyaltyValues1) {

							if(loyaltyIndexsOne[k]==0 && k<loyaltyIndexsOne.length) {

								if(innerSB.length() > 0) innerSB.append(",");
								innerSB.append("\""); innerSB.append(contactLoyaltyValue == null ? "" : contactLoyaltyValue+""); innerSB.append("\"");
							}
							k++;
						}


						//Mobile Number
						if(indexes[4]==0) {

							if(innerSB.length() > 0) innerSB.append(",");
							innerSB.append("\""); innerSB.append(contact.getMobilePhone()==  null ? "" : contact.getMobilePhone()+""); innerSB.append("\"");
							//logger.debug("**********  Mobile value fist for array ************ "+contact.getMobilePhone());
						}


						//Points & Balance
						k=0;

						Object contactLoyaltyValues[] = {contactsLoyalty ==null ? "" :contactsLoyalty.getLoyaltyBalance(),contactsLoyalty ==null ? "" :contactsLoyalty.getGiftcardBalance()};


						//ContactLoyaltyLabelValues
						for (Object contactLoyaltyValue : contactLoyaltyValues) {

							if(loyaltyIndexsTwo[k]==0 && k<loyaltyIndexsTwo.length) {

								if(innerSB.length() > 0) innerSB.append(",");
								innerSB.append("\""); innerSB.append(contactLoyaltyValue == null ? "" : contactLoyaltyValue+""); innerSB.append("\"");
							}
							k++;
						}

						int j=6;
						for (String custFldKey : orderedMappingMap.keySet()) {

							if(custFldKey.equalsIgnoreCase("Mobile"))
								continue;

							if(j==indexLength)
								break;

							POSMapping posMapping = orderedMappingMap.get(custFldKey);
							if(genFieldContMap.containsKey(posMapping.getCustomFieldName())) {

								tempMethod = Contacts.class.getMethod(genFieldContMap.get(posMapping.getCustomFieldName()));

							}

							if(tempMethod != null) {
								Object obj = tempMethod.invoke(contact);
								if(obj != null && obj instanceof Calendar) {
									obj = MyCalendar.calendarToString((Calendar)obj, MyCalendar.FORMAT_DATETIME_STYEAR);

								}//if


								// values are here
								if(j!=0 || j!=1 || j!=4) {
									if(indexes[j]==0 && j<indexes.length) {
										if(innerSB.length() > 0) innerSB.append(",");
										innerSB.append("\""); innerSB.append(obj == null ? "" : obj); innerSB.append("\"");
										//logger.debug(" values to be exported"+obj);
									}
								}
							}
							j++;

						}


						sb.append(innerSB);
						sb.append("\r\n");


					}
				}
				bw.write(sb.toString());

				list = null;
				System.gc();
				
			}while(contactjdbcResultsetHandler.getCurrentFetchingCount() < totalCount-1);*/

			

			Filedownload.save(file, "text/csv");
			logger.debug("exited");
		} catch (Exception e) {
			logger.error("** Exception : " , e);
		}finally{
			if(jdbcResultsetHandler!=null ) jdbcResultsetHandler.destroy();
			jdbcResultsetHandler = null; userName = null; usersParentDirectory = null; filePath = null; downloadDir = null;sb = null;
			file = null; bw = null; udfFldsLabel= null; user = null; posMappingsList = null; orderedMappingMap = null;indexes =null;
			loyaltyIndexsOne = null; loyaltyIndexsTwo = null; query = null; ext = null;
			//System.gc();
		}
		logger.info("time taken to export segmented contacts ::: ::"+(System.currentTimeMillis()-startTime));
	}//end of exportCSV





	//String[] contactLoyaltyLabelsTwo= new String[]{"Created Date","Membership Number", "Card Pin", "Points","Reward Balance"};
	public void createWindow()	{

		try {

			Components.removeAllChildren(custExport$chkDivId);
			List<String[]> headerFields = getHeaders();

			//Created Date
			Checkbox tempChk1 = new Checkbox("Created Date");
			tempChk1.setSclass("custCheck");
			tempChk1.setValue("c.created_date, ");
			tempChk1.setParent(custExport$chkDivId);

			//Email
			tempChk1 = new Checkbox("Email");
			tempChk1.setSclass("custCheck");
			tempChk1.setValue("c.email_id, ");
			tempChk1.setParent(custExport$chkDivId);

			//Card Number
			tempChk1 = new Checkbox("Membership Number");
			tempChk1.setSclass("custCheck");
			tempChk1.setValue("cl.card_number, ");
			tempChk1.setParent(custExport$chkDivId);

			//Card Pin
			tempChk1 = new Checkbox("Card Pin");
			tempChk1.setSclass("custCheck");
			tempChk1.setValue("cl.card_pin, ");
			tempChk1.setParent(custExport$chkDivId);

			tempChk1 = new Checkbox("Mobile Number");
			tempChk1.setSclass("custCheck");
			tempChk1.setValue("c.mobile_phone, ");
			tempChk1.setParent(custExport$chkDivId);



			//Checkbox tempChk=null;
			if(headerFields!=null) {

				////Point Balance & Reward Balance
				for (int i = 0; i < contactLoyaltyLabelsTwo.length; i++) {
										
					tempChk1=new Checkbox(contactLoyaltyLabelsTwo[i]);
					tempChk1.setSclass("custCheck");
					tempChk1.setParent(custExport$chkDivId);
					
					if(i==0) {
						tempChk1.setValue("cl.loyalty_balance, ");
					}
					else if(i == 1) {
						tempChk1.setValue("cl.giftcard_balance, ");
					}
					else if(i == 2) {
						tempChk1.setValue("cl.gift_balance, ");
					}
					else if(i == 3) {
						tempChk1.setValue("cl.holdpoints_balance, ");
					}
					else {
						tempChk1.setValue("cl.holdAmount_balance, ");
					}
				}


				for (int i = 0; i < headerFields.size(); i++) {
					if(((String)headerFields.get(i)[0]).equalsIgnoreCase("Email") || ((String)headerFields.get(i)[0]).equalsIgnoreCase("Email ID") || ((String)headerFields.get(i)[0]).equalsIgnoreCase("Email_ID") ||((String)headerFields.get(i)[0]).equalsIgnoreCase("MobilePhone") ||((String)headerFields.get(i)[0]).equalsIgnoreCase("Mobile_Phone") || ((String)headerFields.get(i)[0]).equalsIgnoreCase("Mobile Phone") ||((String)headerFields.get(i)[0]).equalsIgnoreCase("Contact Number")){
						continue;
					}
					tempChk1 = new Checkbox((String)headerFields.get(i)[0]);
					tempChk1.setSclass("custCheck");
					tempChk1.setValue(((String)headerFields.get(i)[1])+", ");
					tempChk1.setParent(custExport$chkDivId);
				} // for

			}

			else {

				MessageUtil.setMessage("There is no field to export ", "info");
			}


		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}//create window


	public List<String[]> getHeaders() {

		Users user = null;

		user = usersDao.findMlUser(segmentRule.getUserId());

		if(user == null) {

			logger.debug("do not Export as user is null....");
			return null;
		}

		List<POSMapping> posMappingsList = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_CONTACTS+"'", user.getUserId());
		
		
		Map<String, POSMapping> orderedMappingMap = getOrderedMappingSet(posMappingsList);
		
		//String headers[] = new String[orderedMappingMap.size()];
		
		List<String[]> headers = new ArrayList<String[]>();
		String[] headerWithDbValue = null;
		for (String custFldKey : orderedMappingMap.keySet()) {
				
			headerWithDbValue = new String[2];
				headerWithDbValue[0] = orderedMappingMap.get(custFldKey).getDisplayLabel().trim();
				headerWithDbValue[1] = genFieldContMap.get(orderedMappingMap.get(custFldKey).getCustomFieldName());
				
				if(headerWithDbValue[0]!=null) {
					headers.add(headerWithDbValue);
					
				}
				
		
		}//for
		
		return headers;
		
		
	}

}

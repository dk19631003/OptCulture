/**
 * 
 */
package org.mq.marketer.campaign.controller.contacts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.collections.functors.WhileClosure;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
// import org.hsqldb.ClientConnectionHTTP;
import org.apache.commons.lang.StringEscapeUtils;
import org.mq.marketer.campaign.beans.ContactParentalConsent;
import org.mq.marketer.campaign.beans.ContactScores;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.CustomFieldData;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.MLCustomFields;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.ContactParentalConsentDao;
import org.mq.marketer.campaign.dao.ContactParentalConsentDaoForDML;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.CustomFieldDataDao;
import org.mq.marketer.campaign.dao.MLCustomFieldsDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MailingListDaoForDML;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.LBFilterEventListener;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.loyalty.LoyaltyProgramService;
import org.mq.optculture.data.dao.ContactsJdbcResultsetHandler;
import org.mq.optculture.utils.OCCSVWriter;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
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
import org.zkoss.zul.A;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Footer;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Span;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

@SuppressWarnings({ "unchecked", "serial" })
public class ViewContactsController extends GenericForwardComposer {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private Session sessionScope = null;
	private Include xcontents = null;
	
	private MailingList mailingList ;
	private Listbox contactListLBId;
	private Listbox contactStatusLbId,toMlistlbId;
	private Listbox pageSizeLbId;
	private Paging contactsPagingId,contactsPaging1Id;
	private Window custExport;
	private Div custExport$chkDivId;
	
	private Window viewContactListWinId;
	private Textbox searchBoxId, first_nameTbId, last_nameTbId;
	private Span fullNameSpanId;

	private UserActivitiesDao userActivitiesDao;
	private UserActivitiesDaoForDML userActivitiesDaoForDML;
	
	private String mlIds;
	private String viewType;
	
	private Long currentUserId;
	//private Set<Long> userIdsSet = null;
	private Set<MailingList> mlSet;
	
//	private int totSize = 0;
	private int pageSize;
	
//	private Map<Long,MailingList> mlMap;
	private  ListitemRenderer renderer = new  MyRenderer();
	private  List<Object[]> scoreContctList;
	private static HashMap<String, Object> scoreMap=new HashMap<String, Object>();
	
	private Combobox  exportCbId;
	private Listbox statusLBId;
	private Listbox mstatusLBId;
	
	private String contactsIds;
	private Div exportDivId;
	
	private ContactsDao contactsDao;
	private MailingListDao mailingListDao;
	private UsersDao usersDao;
	private POSMappingDao posMappingDao;
	private ContactParentalConsentDao contactParentalConsentDao;
	
	private ContactsLoyaltyDao contactsLoyaltyDao;
	private ContactsLoyalty contactsLoyalty;
	
	
	
	private Set<Long> listIdsSet; 
	private Long userId;
	
	
	private MyDatebox searchByDateBxId;

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
	
		genFieldContMap.put("Home Store", "org.store_name");
	
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
	
	
	
	
	
	
	public ViewContactsController() {
		
		this.sessionScope = Sessions.getCurrent();
		this.xcontents = Utility.getXcontents();
		this.contactsDao = (ContactsDao)SpringUtil.getBean("contactsDao");
		this.mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
		userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
		userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
		usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		posMappingDao = (POSMappingDao)SpringUtil.getBean("posMappingDao");
		contactParentalConsentDao = (ContactParentalConsentDao)SpringUtil.getBean("contactParentalConsentDao");
		this.contactsLoyaltyDao = (ContactsLoyaltyDao)SpringUtil.getBean("contactsLoyaltyDao");
		userId = GetUser.getUserObj().getUserId();
		this.mlIds = "";
		this.contactsIds = "";
		this.mlSet = new HashSet<MailingList>();
		//this.userIdsSet = GetUser.getUsersSet();
		this.currentUserId = GetUser.getUserId();
		
		/* if(userActivitiesDao != null) {
		      	userActivitiesDao.addToActivityList(ActivityEnum.VISIT_CONTACT_CONTACT_VIEW,GetUser.getUserObj());
		  }*/
		 if(userActivitiesDaoForDML != null) {
			 userActivitiesDaoForDML.addToActivityList(ActivityEnum.VISIT_CONTACT_CONTACT_VIEW,GetUser.getLoginUserObj());
		  }
		 
		 
		 listIdsSet = (Set<Long>)sessionScope.getAttribute(Constants.LISTIDS_SET);
		 
	}
	
	Paging myPagingId;
	Paging myPaging1Id;
	String filterQuery=null;
	String filterCountQuery=null;
	String qryPrefix=null;
	LBFilterEventListener contactLbELObj =null;	
	private boolean isDifferOwner = true;
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		viewType = (String)sessionScope.getAttribute("viewType");
		
		this.pageSize =Integer.parseInt((pageSizeLbId.getSelectedItem().getLabel()));
 		logger.debug("viewType======="+viewType);
		
 		setDefaultMethod();
 		LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService();
			List<MailingList> userMlList = mailingListDao.findAllByCurrentUser(userId);
			if(userMlList == null || userMlList.size() == 0) {
				logger.info("no mailing list exist in the DB for the user...");
			}
			else{
				if(viewType==null){
			MailingList mailingList =(MailingList)sessionScope.getAttribute("mailingList");
			Long mlId=mailingList.getListId();
				Listitem tempItem = null;
				for (MailingList mlist : userMlList) {
					if(mlist != null){
						if(mlist.getListId().longValue() == mlId.longValue()) continue;
					}
					tempItem = new Listitem(mlist.getListName(),mlist);
					tempItem.setParent(toMlistlbId);
				} // for
			}
				else{
					Listitem tempItem = null;
					for (MailingList mlist : userMlList) {
						tempItem = new Listitem(mlist.getListName(),mlist);
						tempItem.setParent(toMlistlbId);
					} // for
				}
			}

 		
	} //doAfterCompose
	
	public void setDefaultMethod() {

		try {
			searchFieldStr=searchTBId.getText();
			emailStatusItem =contactStatusLbId.getSelectedItem();
			mobileStatusItem=	mobileStatusLbId.getSelectedItem();
			searchCriteriaIndex=searchLBId.getSelectedIndex();
			searchTBId.setVisible(true);
			searchByDateBxId.setVisible(false); 
			fullNameSpanId.setVisible(false); 
			 
			if(viewType == null) {
				
				logger.debug("viewType is "+viewType+", getting the mailingList obj from session");
				this.mailingList = (MailingList)sessionScope.getAttribute("mailingList");
				if(mailingList == null) {
					logger.warn("** MailingList object is null. Redirecting to Mylist page.");
			        Redirect.goTo(PageListEnum.RM_HOME);
			        return;
				 }
				
				 mlSet.add(mailingList);
				// totSize = (int)contactsDao.getEmailCount(mailingList.getListId().toString(),false);
//				 totSize = (int)contactsDao.getAllContactsCount(mlSet);
				 String style = "font-weight:bold;font-size:15px;color:#313031;" +
								"font-family:Arial,Helvetica,sans-serif;align:left";
				 PageUtil.setHeader("My Contacts","",style,true);
				 exportDivId.setVisible(true);
				 
					Users mlUser = mailingList.getUsers();
					Long current_mlBit=mailingList.getMlBit();
					logger.info("MlBit of currently opened mailing list----------"+current_mlBit);	
										
					//Long userId = mlUser.getParentUser() != null ? mlUser.getParentUser().getUserId() : mlUser.getUserId() ;*/
					
				 
				 filterQuery = " FROM Contacts  WHERE users= "+mlUser.getUserId().longValue()+
						 " AND bitwise_and(mlBits, "+mailingList.getMlBit()+" )>0 ";
				 
				 filterCountQuery = " SELECT COUNT(contactId) "+filterQuery;
				 
				 qryPrefix="";
				 if(mlUser.getUserId().longValue() == currentUserId.longValue()) {
						
						isDifferOwner = false;
					}
			} 
			else if(viewType.equalsIgnoreCase("contact")) {
				
				logger.debug("viewType is "+viewType+", fetching all the contacts from Db");
				//List<MailingList> mlList = mailingListDao.findAllByUser(userIdsSet);
				
				
				List<MailingList> mlList = mailingListDao.getMailingListByIds(listIdsSet);
				logger.info(">>>>> :: mlList is ::: "+mlList);	
				String userIdsStr = Constants.STRING_NILL;
				if(mlList != null && mlList.size() > 0) {
					for(MailingList ml:mlList) {
						
						long userID = ml.getUsers().getUserId().longValue();
						
						if(ml.getUsers().getUserId().longValue() == currentUserId.longValue()) {
							
							isDifferOwner = false;
						}
						
						if(mlIds.equalsIgnoreCase("")) {
							mlIds =mlIds+ ml.getListId();
						} else {
							mlIds=mlIds+","+ml.getListId();
						}
						mlSet.add(ml);
						
						if(!userIdsStr.startsWith(userID+Constants.STRING_NILL) && !userIdsStr.endsWith(userID+Constants.STRING_NILL)
								&& !userIdsStr.contains(Constants.DELIMETER_COMMA+userID+Constants.STRING_NILL+Constants.DELIMETER_COMMA)) {
							if(!userIdsStr.isEmpty()) userIdsStr += Constants.DELIMETER_COMMA;
							
							userIdsStr += ml.getUsers().getUserId().longValue();
						}
					}
					
					
//					totSize =(int) contactsDao.getAllContactsCount(mlSet);
					 exportDivId.setVisible(true);
					//showContactDetails();
						/*Commented For Performance
						 * filterQuery = "SELECT DISTINCT c FROM Contacts c, MailingList ml WHERE c.users =  ml.users " +
								" AND ml.listId IN ("+mlIds+") "+
								" AND bitwise_and(c.mlBits, ml.mlBit)>0 ";

						filterCountQuery = "SELECT COUNT(DISTINCT c.contactId) FROM Contacts c, MailingList ml WHERE c.users = ml.users " +
								" AND ml.listId IN ("+mlIds+") "+
								" AND bitwise_and(c.mlBits, ml.mlBit)>0 ";
						qryPrefix="c";*/
						//Added For performance
						/*filterQuery = "SELECT DISTINCT c FROM Contacts c, MailingList ml WHERE c.users ="+currentUserId.longValue() +" AND ml.users = "+mlList.get(0).getUsers().getUserId()
								+" AND ml.listId IN ("+mlIds+") "+
								" AND bitwise_and(c.mlBits, ml.mlBit)>0 ";
*/
					 filterQuery = "SELECT DISTINCT c FROM Contacts c, MailingList ml"
					 		+ " WHERE c.users IN("+userIdsStr +")" //  AND ml.users IN "+mlList.get(0).getUsers().getUserId()
											+ " AND ml.listId IN ("+mlIds+") "+
								" AND bitwise_and(c.mlBits, ml.mlBit)>0 ";
					 
						filterCountQuery = "SELECT COUNT(DISTINCT c.contactId) FROM Contacts c, MailingList ml"
											+ " WHERE c.users IN("+userIdsStr +")" //  AND ml.users IN "+mlList.get(0).getUsers().getUserId()
											+ " AND ml.listId IN ("+mlIds+") "+
											" AND bitwise_and(c.mlBits, ml.mlBit)>0 ";
						
						qryPrefix="c";
					 
					 
					 
				}
				
				contActionsBandBoxId.setDisabled(isDifferOwner);
				/*this.userIdsSet = GetUser.getUsersSet();
				logger.info("=userIdsSet=="+userIdsSet);*/
				
				/*if(userIdsSet.size()==1) {
					long userVal = userIdsSet.iterator().next().longValue();
					
					Users users = usersDao.findByUserId(userVal);
					userVal = users.getParentUser() != null ? users.getParentUser().getUserId().longValue() : userVal;
					filterQuery = "  FROM Contacts c WHERE c.users="+userVal+" AND c.mlBits >0 ";
					filterCountQuery = "SELECT COUNT(DISTINCT c.contactId) "+filterQuery;
					qryPrefix="c";
				}
				else {*/
					
				//}
				
				
				
			}//else if 
			
			/*this.contactListLBId = contactListLBId;
			this.contactsPagingId = contactsPagingId;
			this.searchBoxId = searchBoxId;
			this.contactStatusLbId = contactStatusLbId;
			this.viewContactListWinId = viewContactListWinId;
			*/
			//contactsPagingId.setDetailed(true);
			//contactsPagingId.setTotalSize(totSize);
			contactsPagingId.setAttribute("onPaging", "topPaging");
			contactsPagingId.addEventListener("onPaging", new MyRenderer());
			
			//contactsPaging1Id.setTotalSize(totSize);
			contactsPaging1Id.setAttribute("onPaging", "bottomPaging");
			contactsPaging1Id.addEventListener("onPaging", new MyRenderer());
			//showContactDetails();
			contactListLBId.setItemRenderer( renderer);
			
			/*toMlistlbId.setModel(new ListModelList(getMailingLists()));
			toMlistlbId.setItemRenderer(renderer);*/
			if(toMlistlbId.getItemCount() > 0)toMlistlbId.setSelectedIndex(0);
			
			Map<Integer, Field> objMap = new HashMap<Integer, Field>();
			
			objMap.put(1, Contacts.class.getDeclaredField("emailId"));
			//2 lists
			objMap.put(3, Contacts.class.getDeclaredField("mobilePhone"));
			//membership no
			//objMap.put(4, Contacts.class.getDeclaredField("cardNumber"));
			objMap.put(5, Contacts.class.getDeclaredField("firstName"));
			objMap.put(6, Contacts.class.getDeclaredField("lastName"));
			objMap.put(7, Contacts.class.getDeclaredField("createdDate"));
			objMap.put(8, Contacts.class.getDeclaredField("modifiedDate"));
			objMap.put(9, Contacts.class.getDeclaredField("emailStatus"));
			objMap.put(10, Contacts.class.getDeclaredField("mobileStatus"));
			objMap.put(11, Contacts.class.getDeclaredField("lastMailDate"));
			
			myPagingId.setPageSize(Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
			myPaging1Id.setPageSize(Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));

			if(filterCountQuery != null && filterQuery != null) {
				contactLbELObj = LBFilterEventListener.lbFilterSetup1(contactListLBId, myPagingId, myPaging1Id,filterQuery, filterCountQuery, qryPrefix, objMap);
				Utility.refreshModel1(contactLbELObj, 0, true);
				
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
	}

	public void showContactDetails() throws Exception {
		
		List<Contacts> contactList = getAllContacts();
		
		if(contactList == null || contactList.size() <= 0) {
			
			logger.debug("No contacts found.........");
			return;
		}
		
		for (Contacts contact : contactList) {
			
			if(contactsIds.equalsIgnoreCase("")) {
				contactsIds =contactsIds+"'"+ contact.getEmailId()+"'";
			} else {
				contactsIds=contactsIds+",'"+contact.getEmailId()+"'";
			}
		}
		
		//logger.info("contactsIds ====>"+contactsIds);
		try {
			scoreContctList = contactsDao.findByScoreList(listIdsSet,contactsIds);
			if(scoreContctList != null) {
				
				for (Object[] obj : scoreContctList) {
					scoreMap.put((String)obj[0],(ContactScores)obj[1] );
				}
				
			}
			
		} catch (Exception e) {
			logger.error("** Exception :error occured while getting from contactScore Obj",e);
		}
	
		//set Multiple(true) to the listModelList for checkmark (zk6ce )		
		ListModelList listModelList = new ListModelList(contactList);
		listModelList.setMultiple(true);
		contactListLBId.setModel(listModelList);
		contactListLBId.setItemRenderer( renderer);
		
		
	} // showContactDetails
	/**this method returns all the contacts of all the mailing lists
	 * 
	 * @return List<Contacts>
	 */	
	public List<Contacts> getAllContacts() {
		List<Contacts> contactList=null;
		int si = contactListLBId.getItemCount();
		
		for(;si>0;si--){
			contactListLBId.removeItemAt(si-1);
		}
		//contactList = getAllContactsByStatus( contactStatusLbId.getSelectedItem().getLabel(), searchBoxId.getValue() );
		String status = contactStatusLbId.getSelectedItem().getLabel();
		String mobileStatus=contactMobileStatusLbId.getSelectedItem().getLabel();
		if(status.equalsIgnoreCase("--All--") || mobileStatus.equalsIgnoreCase("--All--")) {
			if(viewType==null) {
				contactList = contactsDao.findByList(mailingList, contactsPagingId.getActivePage()*pageSize, 
							contactsPagingId.getPageSize() );
			} else {
				contactList = contactsDao.find(mlSet,contactsPagingId.getActivePage()*pageSize, contactsPagingId.getPageSize());	
			}
		}else {
			
			if(viewType==null) {
				contactList = contactsDao.findByStatus(mailingList,status, contactsPagingId.getActivePage()*pageSize,
							contactsPagingId.getPageSize(),mobileStatus);
				} else {
					contactList = contactsDao.find(mlSet, status, contactsPagingId.getActivePage()*pageSize, 
								contactsPagingId.getPageSize(),mobileStatus);//find(mlIds,firstResult, size);	
				}
			
		}
		//methodCallFlag is the attribute used in onEvent to differentiate 
		sessionScope.removeAttribute("methodCallFlag");
		sessionScope.setAttribute("methodCallFlag", "0");
		return contactList;
	}
	
	
	/*public void onPaging$contactListLBId() {
		
	}*/
	
	public List<MailingList> getMailingLists() {
		List<MailingList> mlLists=null;
//		mlMap = new HashMap<Long, MailingList>();
		
		/*if(mailingList == null) 
			mailingList = (MailingList)sessionScope.getAttribute("mailingList");
		
		if(mailingList == null) 
			return null;*/
		
		//mlLists = mailingListDao.findAllByUser(userIdsSet);
		
		//mlLists = mailingListDao.findByIds(listIdsSet);
		mlLists = mailingListDao.getMailingListByIds(listIdsSet);
		/*for(MailingList ml:mlLists) {
			mlMap.put(ml.getListId(), ml);
		}*/
		return mlLists;
	}
	
	
	
	public List<POSMapping> getPosMappingLists(){
		List<POSMapping> contactsUDFList=null;
		
		contactsUDFList = posMappingDao.findOnlyByType(Constants.POS_MAPPING_TYPE_CONTACTS, GetUser.getUserId() );
		
		return contactsUDFList;
		
	}
	
	/**this method retrieves selected number of records from DB
	 * @param sizeStr
	 * 
	 */
	public void changeRows(String sizeStr)throws Exception {
		
		try {
			MessageUtil.clearMessage();
			//searchBoxId.setValue("");
			pageSize = Integer.parseInt(sizeStr);
		} catch (NumberFormatException e) {
			logger.error("Exception ::", e);
			pageSize = 10;
		}
		contactsPagingId.setPageSize(pageSize);
		contactsPaging1Id.setPageSize(pageSize);
		/*contactListLBId.setModel(new ListModelList(getAllContacts()));
		contactListLBId.setItemRenderer(renderer);*/
		//redraw(contactsPagingId.getActivePage()*pageSize,pageSize);
	}

	public void gotoMylists() throws Exception {
		try {
			//sessionScope.setAttribute("fromPage", "contacts");
			sessionScope.removeAttribute("viewType");
			logger.debug("going to page contacts====>"+(String)Executions.getCurrent().getAttribute("viewType"));
			xcontents.setSrc(null);	
			Redirect.goTo(PageListEnum.CONTACT_LIST_VIEW);
			/*showContactDetails();
			contactStatusLbId.setSelectedIndex(0);
			searchBoxId.setValue("Search Email...");
			*/
		
		} catch (Exception e) {
			logger.error("Exception :: ******",e);
		}
	}
	
	/**this method deletes the selected contacts from DB
	 * 
	 * @throws Exception
	 */
	//ISH
		public void deleteSelectedContacts() throws Exception {
			try{
				MessageUtil.clearMessage();
					MailingListDaoForDML mailingListDaoForDML = (MailingListDaoForDML)SpringUtil.getBean("mailingListDaoForDML");
					ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)SpringUtil.getBean("contactsLoyaltyDao");
					Contacts contact = (Contacts)contactListLBId.getSelectedItem().getValue();
					List<MailingList> conMlList =null;
					List<String> activeConfiguredList=null;
					List<String> activeSmsConfiguredList=null;
					List<String> activeEventTriggerConfiguredList=null;
					String contactStatus=contact.getEmailStatus();
					Contacts emailId = null;
			    	String contactIds = ""; 
			    	String contactNames = "";
					Set selSet = contactListLBId.getSelectedItems();
					if(selSet.size()==0){
						MessageUtil.setMessage("Select contacts to delete.", "color:red;", "TOP");
						return;
					}
					int confirm = Messagebox.show("Are you sure you want to delete the selected contacts?", 
							"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm != 1) {	return;	}
					
					List<Contacts> contacts = new ArrayList<Contacts>();
					
					if(viewType == null) {
						
						MailingList mailingList = (MailingList) sessionScope.getAttribute("mailingList");
						conMlList = mailingListDao.findByContactBit(currentUserId, contact.getMlBits());
						
					} else if(viewType.equalsIgnoreCase("contact")){
						conMlList = mailingListDao.findByContactBit(currentUserId, contact.getMlBits());
					}
			
				Iterator<MailingList> mlSetItr = conMlList.iterator();
				MailingList mlObj = null;
				String listIdStr = "";
				Map<String,String> listNameMap = new HashMap<String, String>();
				
			while(mlSetItr.hasNext()){
				MailingList mailingList = mlSetItr.next();
				listIdStr +=  listIdStr.length()== 0? mailingList.getListId():","+mailingList.getListId();
				listNameMap.put(""+mailingList.getListId(), mailingList.getListName());
				logger.info("mailingList.getListId(), mailingList.getListName()"+mailingList.getListId()+","+mailingList.getListName());
			}
			
			if(!listIdStr.isEmpty()) {
				int count=0;
				String notDelList = Constants.STRING_NILL;
				List<String> configuredList = mailingListDao.getCampaignCountByMlists(listIdStr);
				List<String> smsConfiguredList = mailingListDao.getSMSCountByMlists(listIdStr);
				List<String> eventTriggerConfiguredList = mailingListDao.getEventTriggerCountByMlist(listIdStr);
				
				if((configuredList.size()!=0) || (smsConfiguredList.size()!=0) || (eventTriggerConfiguredList.size()!=0))
				{
				activeConfiguredList = mailingListDao.findStatusByMlists(listIdStr,currentUserId,Constants.MAILINGLIST_CONFIGURED_TYPE_CAMPAIGN);
				if(activeConfiguredList.size()==0)
				{
					activeSmsConfiguredList = mailingListDao.findStatusByMlists(listIdStr,currentUserId,Constants.MAILINGLIST_CONFIGURED_TYPE_SMS);
					if(activeSmsConfiguredList.size()==0)
					{
							activeEventTriggerConfiguredList = mailingListDao.findStatusByMlists(listIdStr,currentUserId,Constants.MAILINGLIST_CONFIGURED_TYPE_EVENT_TRIGGER);
							if(activeEventTriggerConfiguredList.size()==0)
							{
								for(Object obj:selSet) {
									emailId = (Contacts)((Listitem)obj).getValue();
									if(emailId.getUsers().getUserId().longValue() != currentUserId.longValue()) {
									
										MessageUtil.setMessage("Some of the selected contacts are shared by others.You cannot delete them.", "color:red;");
										return;
									
									
									}			
									contacts.add(emailId);
									contactIds += contactIds.equals("")? emailId.getContactId():"," + emailId.getContactId(); 
									contactNames += contactNames.equals("")? emailId.getEmailId():"," + emailId.getEmailId();
								}
							}
							else{
								for (String etListId : eventTriggerConfiguredList) {
									
									if(notDelList.equals("")) notDelList += listNameMap.get(etListId);
									else if(!notDelList.equals("") && !notDelList.contains(listNameMap.get(etListId))) {
										notDelList += ","+listNameMap.get(etListId);
										
									}
									
								}
							}
					}
					else{
						for(String smsListId : activeSmsConfiguredList) {
							
							if(notDelList.equals("")) notDelList += listNameMap.get(smsListId);
							else if(!notDelList.equals("") && !notDelList.contains(listNameMap.get(smsListId))) {
								notDelList += ","+listNameMap.get(smsListId);
								
							}
							
						}
					}
				}
				else{
					/*for(String campListId : activeConfiguredList){
						if(notDelList.equals(""))
							notDelList += listNameMap.get(campListId);
						else if(!notDelList.equals("") && !notDelList.contains(listNameMap.get(campListId))) {
							notDelList += ","+listNameMap.get(campListId);
						}
					}*/
					for (String key : activeConfiguredList) {
						notDelList +=  notDelList.equals("")?listNameMap.get(key):","+listNameMap.get(key);
					}
				}
			}
			else
			{
				for(Object obj:selSet) {
					emailId = (Contacts)((Listitem)obj).getValue();
					if(emailId.getUsers().getUserId().longValue() != currentUserId.longValue()) {
					
						MessageUtil.setMessage("Some of the selected contacts are shared by others.You cannot delete them.", "color:red;");
						return;
					
					
					}			
					contacts.add(emailId);
					contactIds += contactIds.equals("")? emailId.getContactId():"," + emailId.getContactId(); 
					contactNames += contactNames.equals("")? emailId.getEmailId():"," + emailId.getEmailId();
					}
			}
						
			if(notDelList.length()>0) {
				
				confirm = Messagebox.show("The selected contact(s) exists in " + notDelList + " and are configured to Emails / SMS / Event Trigger \n " +
						"and they cannot be " +
						"deleted. Do you want to continue deleting other contact(s)?", 
						"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				
				if(confirm != 1){
					return;
				}
			}
		        if (contacts.size() == 0) {
		        	return;
		        }// if
		    try {
	            	ContactsDao contactsDao = (ContactsDao)SpringUtil.getBean("contactsDao");
	            	ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML)SpringUtil.getBean(OCConstants.CONTACTS_DAO_FOR_DML);
	            	if(viewType == null) {
	     
	            		String[] contactIdsArray = contactIds.split(",");
	            		for(String contactId : contactIdsArray){
	            			logger.info("============ 1 ============"+contactId);
	            			List<ContactsLoyalty> contactsLoyaltyList = contactsLoyaltyDao.findMembershipByContactId(userId, contactId);
	            			
	            			if(contactsLoyaltyList != null && contactsLoyaltyList.size() > 0 ){
	            				boolean hasActiveMembership = false;
	            				for (ContactsLoyalty contactsLoyalty : contactsLoyaltyList) {
									
	            					if(contactsLoyalty.getMembershipStatus() != null && 
	            							contactsLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE)) {
	            						
	            						hasActiveMembership = true;
	            						break;
	            					}
								}
	            				
	            				if(hasActiveMembership){
	            					MessageUtil.setMessage("Some of the selected contact(s) having Active membership.You cannot delete them.", "color:red;");
	            					return;
	            					
	            				}
	            			}
	            			contact = contactsDao.findById(Long.parseLong(contactId));
	             			contact.setMlBits(contact.getMlBits().longValue()& (~mailingList.getMlBit().longValue()));
	            			if(contact.getMlBits().longValue() == 0l){
	            				Utility.setContactFieldsOnDeletion(contact);
	            			}
	            			count++;
	            			contactsDaoForDML.saveOrUpdate(contact);
	            		}
	            		MailingList mlList = mailingListDao.find(mailingList.getListId());
	            		mlList.setLastModifiedDate(Calendar.getInstance());
				//mlList.setListSize(mlList.getListSize() - count);
	            		if(count > mlList.getListSize()) mlList.setListSize(0L); 
	            		else mlList.setListSize(mlList.getListSize() - count);
	            		mailingListDaoForDML.saveOrUpdate(mlList);
	            		if(userActivitiesDaoForDML != null) {
	                		userActivitiesDaoForDML.addToActivityList(ActivityEnum.CONTS_DEL_CONT_p1contacts_p2mlName, GetUser.getLoginUserObj(), 
	                				contactNames, mailingList.getListName());
	    				}
	            	} else if(viewType.equals("contact")) {
	            		
	            		String mlIdStr="";
	            		Set<MailingList> modifiedMlSets = new HashSet<MailingList>();
	            		List<MailingList> mlList = null; 
	            		String[] contactIdsArray = contactIds.split(",");
	            		for(String contactId : contactIdsArray){
	            			logger.info("============ 2 contact============"+contactId);
	            			List<ContactsLoyalty> contactsLoyaltyList = contactsLoyaltyDao.findMembershipByContactId(userId, contactId);
	            			
	            			if(contactsLoyaltyList != null && contactsLoyaltyList.size() > 0 ){
	            				boolean hasActiveMembership = false;
	            				for (ContactsLoyalty contactsLoyalty : contactsLoyaltyList) {
									
	            					if(contactsLoyalty.getMembershipStatus() != null && 
	            							contactsLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE)) {
	            						
	            						hasActiveMembership = true;
	            						break;
	            					}
								}
	            				
	            				if(hasActiveMembership){
	            					MessageUtil.setMessage("Some of the selected contact(s) having Active membership.You cannot delete them.", "color:red;");
	            					return;
	            					
	            				}
	            			}
	            			
	            		}
	            		
	            		for(Contacts con : contacts) {
	            			String mlStr = "";
	            			MailingList Mlist = null;
	            			conMlList = mailingListDao.findByContactBit(currentUserId, con.getMlBits());
	            			modifiedMlSets.addAll(conMlList);
	        				con.setMlBits(0l);
	        				if(con.getMlBits().longValue() == 0l){
	        					Utility.setContactFieldsOnDeletion(con);     					
	        				}
	        				count++;
	        				contactsDaoForDML.saveOrUpdate(con);
	            		} // for
	            		
	            		List<MailingList> updatemlLists = new LinkedList<MailingList>();
	            		MailingList mlist = null;
	            		if(modifiedMlSets.size()>0 && modifiedMlSets != null){
	            			Iterator<MailingList> mlIterator = modifiedMlSets.iterator();
	            			while(mlIterator.hasNext()){
	            				
	            				mlist = mlIterator.next();
	            				mlist.setLastModifiedDate(Calendar.getInstance());          				
	            				//mlist.setListSize(mlist.getListSize() - count);
	            				if(count > mlist.getListSize()) mlist.setListSize(0L);
	                    		else mlist.setListSize(mlist.getListSize() - count);
	            				updatemlLists.add(mlist);
	            			}
	            		}
	            		
	            		if(updatemlLists != null){
	            		for(MailingList mlList1 : updatemlLists){
	            			mailingListDaoForDML.saveOrUpdate(mlList1);
	            		}
	            		}	            		
	            	}
	            	ContactParentalConsentDao contactParentalConsentDao = (ContactParentalConsentDao)SpringUtil.getBean("contactParentalConsentDao");
	            	ContactParentalConsentDaoForDML contactParentalConsentDaoForDML = (ContactParentalConsentDaoForDML)SpringUtil.getBean("contactParentalConsentDaoForDML");
	          	    //contactParentalConsentDao.deleteByContactId(contactIds);  
	              	contactParentalConsentDaoForDML.deleteByContactId(contactIds);
	                logger.info("Selected contact(s) deleted successfully");
	                MessageUtil.setMessage("Selected contact(s) deleted successfully.", "color:blue;", "TOP");
	                count=contactListLBId.getSelectedCount();
	                for(;count>0;count--){
//						contactListLBId.removeItemAt(count-1);
						logger.debug("SelectedItem is >>>>>::"+contactListLBId.getSelectedItem().getValue());
	                	contactListLBId.removeItemAt(contactListLBId.getSelectedIndex());
					}//for	      
		        }catch (Exception e) {
	                logger.error("** Problem in deleting the emails: Exception:",e);
	                MessageUtil.setMessage("Problem experienced while deleting selected contacts.", "color:red;", "TOP");
	            }
			}
			}catch (RuntimeException e) {
				logger.error("** Exception : " , e);
			}
		}
		//ISH
	/**
	 * this method changes the status of selected contact from acitve to inactive
	 * @param status
	 */
	
	public void changeStatus(String status) throws Exception {
		try {
			MessageUtil.clearMessage();
			Set set = contactListLBId.getSelectedItems();
			Set emailIdSet = new HashSet();
			for(Object obj:set){
				Listitem li = (Listitem) obj;
				Contacts emailId = (Contacts)li.getValue();
				if(emailId.getUsers().getUserId().longValue() != currentUserId.longValue()) {
					
					MessageUtil.setMessage("Some of the selected contacts are shared by others;" +
							" You cannot change the status.", "color:red;");
					
					return;
					
					
				}
				
				
				
				String emailStatus = emailId.getEmailStatus();
				if(emailStatus!=null){
					if((emailStatus.equalsIgnoreCase("active") || emailStatus.equalsIgnoreCase("inactive"))&& !(emailStatus.equals(status)))
						emailIdSet.add(emailId);
				}else{emailIdSet.add(emailId);}
			}
			ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML)SpringUtil.getBean("contactsDaoForDML");
			boolean  result = contactsDaoForDML.changeStatus(emailIdSet,status);
			if(result == true ){
				if(emailIdSet.size()>0){
					xcontents.invalidate();	
					MessageUtil.setMessage("Status updated successfully.", "color:blue;", "TOP");
				}else
					MessageUtil.setMessage("Select 'Active / Inactive' status contacts only.", "color:red", "TOP");
				}
			else{
				MessageUtil.setMessage("Updating status failed.", "color:red", "TOP");
			}
		} catch (RuntimeException e) {
			logger.error("** Exception : " + e);
		}
		
		
		

 }
	
	/*this method is for changing the mobile status to active/inactive*/
	
	public void mchangeStatus(String status) throws Exception {
		try {
			MessageUtil.clearMessage();
			Set set = contactListLBId.getSelectedItems();
			Set mobileSet = new HashSet();
			for(Object obj:set){
				Listitem li = (Listitem) obj;
				Contacts mobile = (Contacts)li.getValue();
				if(mobile.getUsers().getUserId().longValue() != currentUserId.longValue()) {
					
					MessageUtil.setMessage("Some of the selected contacts are shared by others;" +
							" You cannot change the status.", "color:red;");
					
					return;
					
					
				}
			
				
				
				String mobileStatus = mobile.getMobileStatus();
				if(mobileStatus!=null){
					if((mobileStatus.equalsIgnoreCase("active") || mobileStatus.equalsIgnoreCase("inactive"))&& !(mobileStatus.equals(status)))
						mobileSet.add(mobile);
				}else{mobileSet.add(mobile);}
			}
			ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML)SpringUtil.getBean("contactsDaoForDML");
			boolean  result = contactsDaoForDML.changeMobileStatus(mobileSet,status);
			if(result == true ){
				if(mobileSet.size()>0){
					xcontents.invalidate();	
					MessageUtil.setMessage("Status updated successfully.", "color:blue;", "TOP");
				}else
					MessageUtil.setMessage("Select 'Active / Inactive' status contacts only.", "color:red", "TOP");
				}
			else{
				MessageUtil.setMessage("Updating status failed.", "color:red", "TOP");
			}
		} catch (RuntimeException e) {
			logger.error("** Exception : " + e);
		}

 }
	
	/**
	 * this method is used to move the selected contacts from existing list to selected list
	 * @param toMList
	 */
	
	public void moveToList(MailingList toMList) throws Exception {
		try{
			MessageUtil.clearMessage();
			ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML)SpringUtil.getBean(OCConstants.CONTACTS_DAO_FOR_DML);
			MailingListDao mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
			MailingListDaoForDML mailingListDaoForDML = (MailingListDaoForDML)SpringUtil.getBean("mailingListDaoForDML");
			Set<Listitem> selSet = contactListLBId.getSelectedItems();
			if(selSet.size()==0){
				MessageUtil.setMessage("Select contacts to move.", "color:red", "TOP");
				return;
			}
		
			
			int confirm = Messagebox.show("Are you sure you want to move the " +
							"selected contact(s) to the list: "+toMList.getListName()+"?", 
							"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm != 1) {
				return;
			}
			boolean exists = false;
			Contacts contact;
			for(Listitem li:selSet){
				try{
					contact = (Contacts)li.getValue();
					Set<MailingList> mlIdSet = null;
					
    				if(viewType == null){
    					long newConBit = (contact.getMlBits().longValue() & ~mailingList.getMlBit().longValue()) | toMList.getMlBit().longValue();
    					logger.info("&&&&&"+(contact.getMlBits() & toMList.getMlBit()));
    					long sameContact=contact.getMlBits() & toMList.getMlBit();
    					contact.setMlBits(newConBit);
    					mailingList.setListSize(mailingList.getListSize() - 1);
    					mailingListDaoForDML.saveOrUpdate(mailingList);
    					if(sameContact!=toMList.getMlBit()){
    					toMList.setListSize(toMList.getListSize() + 1);
    					}
    					mailingListDaoForDML.saveOrUpdate(toMList);
    					}
    					
    			
    				else if(viewType.equalsIgnoreCase("contact")) {
    					
    					List<String> mlList = mailingListDao.findListNamesByContactBit(listIdsSet, contact.getMlBits());
    					List<MailingList> mailList = mailingListDao.getMailingListByIds(listIdsSet);
    					
    					if(contact.getUsers().getUserId().longValue() != currentUserId.longValue() ) {
    						
    						MessageUtil.setMessage("Some of the contacts are shared by others ," +
    								" \n You cannot move them.", "color:blue", "TOP");
    						
    						return;
    						
    					}
    					
    					if(mailList != null && mailList.size() > 0) {
    						for(MailingList ml:mailList) mlSet.add(ml);
    						}
    					
    					for(MailingList ml : mlSet){
    						
    						for(String ml1 : mlList){
    							
    							if(ml.getListName().equalsIgnoreCase(ml1)){
    								ml.setListSize(ml.getListSize() - 1);
    								mailingListDaoForDML.saveOrUpdate(ml);
    							}
    						}
    					}			
    					
    					contact.setMlBits(toMList.getMlBit());
    					toMList.setListSize(toMList.getListSize() + 1);
    					mailingListDaoForDML.saveOrUpdate(toMList);
    				}
    				
					Users user = null;
					user = usersDao.findMlUser(toMList.getUsers().getUserId());//in mailing lists users is of lazy=false
					
					contact.setUsers(user);

					contactsDaoForDML.saveOrUpdate(contact); // can be saved in bulk
				}catch (DataIntegrityViolationException uniqueEx) {
					exists = true;
				}
			}
			String message = "Selected contacts are moved to the list '" + toMList.getListName() + "' \n";
			if(exists){
				message = message + " some contacts already exists, they are not moved to the list";
			}
			xcontents.invalidate();
			MessageUtil.setMessage(message, "color:blue", "TOP");
			
		}catch (Exception e) {
			logger.error("** Exception : " + e + " **");
			MessageUtil.setMessage("Problem encountered while moving the contacts.", "color:red", "TOP");
		}
		
	}
	/**
	 * this method used to get all the contacts as csv file
	 * @param ext
	 */
	
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
	
	
	public void exportCSV_old(String ext) {
		List<Contacts> list = null;
		long totalContacts = 0;
		try {
			logger.debug("entered ");
			/*if(mlIds.trim().isEmpty()) {
				logger.debug("no contacts are exists for exporting...");
				return;
			}*/
			if(viewType==null) {
				if(mailingList == null) { return;	}
				
				if( mailingList.getSize() != null && mailingList.getSize() == 0) {
					MessageUtil.setMessage("No contacts exist in the selected mailing list.", "color:red", "TOP");
					return;
					
					
				}
				
				String listName = mailingList.getListName();
				logger.info("The listName is "+listName);
				totalContacts = contactsDao.findSize(mailingList);
			} else if(viewType.equalsIgnoreCase("contact")) {
				if(mlIds.trim().isEmpty()) {
					logger.debug("no contacts are exists for exporting...");
					return;
				}
				
				totalContacts = contactsDao.getAllContactsCount(mlSet);
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
			if(viewType==null) {
				filePath = usersParentDirectory + "/" + userName + "/List/download/" + mailingList.getListName() + "_" + System.currentTimeMillis() + "." + ext;
				isCustField = mailingList.isCustField();
				
			} else if(viewType.equalsIgnoreCase("contact")) {
				filePath = usersParentDirectory + "/" + userName + "/List/download/MasterList_" + System.currentTimeMillis() + "." + ext;
			}
			
			int size = 1000;
			StringBuffer sb = null;
			File file = new File(filePath);
			BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
			logger.debug("Writing to the file : " + filePath);
			String custFldsLabel= "";
			String custFldName = null;
			if(isCustField) {
				
				MLCustomFieldsDao mlCustomFieldsDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
				CustomFieldDataDao customFieldDataDao = (CustomFieldDataDao)SpringUtil.getBean("cfDataDao");
				
				List<MLCustomFields> mlCustFields = mlCustomFieldsDao.findAllByList(mailingList);
				for (MLCustomFields mlCustomFields : mlCustFields) {
					
					custFldName = mlCustomFields.getCustFieldName();
					if(custFldName != null) {

						if(custFldsLabel.length() > 0) custFldsLabel += ",";
						
						custFldsLabel += "\"CF_"+custFldName+"\"";
						
						
					}//if
					
					
					
					
				}//for
				
				if(custFldsLabel.trim().length() > 0) {
					
					custFldsLabel = ","+custFldsLabel;
				}
				String cfdData = null;
				sb = new StringBuffer();
				sb.append("\"EmailId\",\"FirstName\",\"LastName\",\"CreatedDate\",\"AddressOne\",\"AddressTwo\",\"City\",\"State\",\"Country\",\"ZIP\",\"MobilePhone\""+custFldsLabel);
				sb.append("\r\n");
				
				for(int i=0;i < totalContacts; i+=size){
					if(viewType==null) {
						
						list = contactsDao.findByList(mailingList,i,size);
					
					} else if(viewType.equalsIgnoreCase("contact")) {
						
						list = contactsDao.find(mlSet, i, size);
					}
					logger.debug("Got contacts of Start index: " + i + " size : " + list.size() );
					if(list.size()>0){
						
						StringBuffer innerSB = new StringBuffer();
						
						for (Contacts contact : list) {
							
							sb.append("\"");sb.append(contact.getEmailId() != null ? contact.getEmailId() : "" ); sb.append("\",");
							sb.append("\""); sb.append(contact.getFirstName()!=null?contact.getFirstName():""); sb.append("\",");
							sb.append("\""); sb.append(contact.getLastName()!=null?contact.getLastName():""); sb.append("\",");
							sb.append("\""); sb.append(contact.getCreatedDate()!=null?MyCalendar.calendarToString(contact.getCreatedDate(), MyCalendar.FORMAT_DATEONLY):""); sb.append("\",");
							sb.append("\""); sb.append(contact.getAddressOne()!=null?contact.getAddressOne():""); sb.append("\",");
							sb.append("\""); sb.append(contact.getAddressTwo()!=null?contact.getAddressTwo():""); sb.append("\",");
							sb.append("\""); sb.append(contact.getCity()!=null?contact.getCity():""); sb.append("\",");
							sb.append("\""); sb.append(contact.getState()!=null?contact.getState():""); sb.append("\",");
							sb.append("\""); sb.append(contact.getCountry()!=null?contact.getCountry():""); sb.append("\",");
							/*sb.append("\""); sb.append(contact.getPin()); sb.append("\",");
							sb.append("\""); sb.append(contact.getPhone()!=null?contact.getPhone():""); sb.append("\""); */
							sb.append("\""); sb.append(contact.getZip() !=null?contact.getZip():""); sb.append("\","); 
							sb.append("\""); sb.append(contact.getMobilePhone() !=null?contact.getMobilePhone():""); sb.append("\""); 
							
							CustomFieldData cfd = null;
							cfd = customFieldDataDao.getByContact(contact.getContactId());
							
								for (MLCustomFields mlCustomFields : mlCustFields) {
									
										cfdData = getCustomFldData(mlCustomFields.getFieldIndex(), cfd);
										sb.append(",\""); sb.append(cfdData != null?cfdData:""); sb.append("\"");
									
									
								}//for
								
							
							sb.append("\r\n");
							
						}
					}
					bw.write(sb.toString());
					sb.setLength(0);
					list = null;
					//System.gc();
				}
			}else {
				
				boolean isTypePOS = mailingList.getListType().equals(Constants.MAILINGLIST_TYPE_POS);
				String udfFldsLabel = "\""+"Created Date"+"\"";
				if(isTypePOS) {
					
					Users user = null;
					
			//		user = usersDao.findMlUser(mailingList.getUsers().getUserId());
					 user=usersDao.findByUserId(currentUserId);
					
					if(user == null) {
						
						logger.debug("do not Export as user is null....");
						return;
					}
					
					
					List<POSMapping> posMappingsList = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_CONTACTS+"'", user.getUserId());
					
					logger.debug("POS Mapping List :"+posMappingsList);
					
					for (POSMapping posMapping : posMappingsList) {
						
						
						if(udfFldsLabel.length() > 0) udfFldsLabel += ",";
						
						udfFldsLabel += "\""+posMapping.getDisplayLabel().trim()+"\"";
						
					}
					sb = new StringBuffer();
					sb.append(udfFldsLabel);
					sb.append("\r\n");
					
					for(int i=0;i < totalContacts; i+=size){
						
							
						list = contactsDao.findByList(mailingList,i,size);
							
						
						logger.debug("Got contacts of Start index: " + i + " size : " + list.size() );
						if(list.size()>0){

							Method tempMethod = null;
							StringBuffer innerSB = new StringBuffer();
							for (Contacts contact : list) {
								
								innerSB.setLength(0);
								
									
									if(innerSB.length() > 0) innerSB.append(",");
									innerSB.append("\""); innerSB.append(contact.getCreatedDate() != null ? MyCalendar.calendarToString(contact.getCreatedDate(), MyCalendar.FORMAT_DATEONLY) : ""); innerSB.append("\"");
									
									
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
						sb.setLength(0);
						list = null;
						//System.gc();
					}
					
					
					
					
				
				}//if pos list
				
				else{
					sb = new StringBuffer();
					sb.append("\"EmailId\",\"FirstName\",\"LastName\",\"CreatedDate\",\"AddressOne\",\"AddressTwo\",\"City\",\"State\",\"Country\",\"ZIP\",\"MobilePhone\"");
					sb.append("\r\n");
					
					for(int i=0;i < totalContacts; i+=size) {
						if(viewType==null) {
							
							list = contactsDao.findByList(mailingList,i,size);
							
						} else if(viewType.equalsIgnoreCase("contact")) {
							
							list = contactsDao.find(mlSet, i, size);
						}
						logger.debug("Got contacts of Start index: " + i + " size : " + list.size() );
						if(list.size()>0){
							
							for (Contacts contact : list) {
								sb.append("\"");sb.append(contact.getEmailId() != null ? contact.getEmailId() : "" ); sb.append("\",");
								sb.append("\""); sb.append(contact.getFirstName()!=null?contact.getFirstName():""); sb.append("\",");
								sb.append("\""); sb.append(contact.getLastName()!=null?contact.getLastName():""); sb.append("\",");
								sb.append("\""); sb.append(contact.getCreatedDate()!=null?MyCalendar.calendarToString(contact.getCreatedDate(), MyCalendar.FORMAT_DATEONLY):""); sb.append("\",");
								sb.append("\""); sb.append(contact.getAddressOne()!=null?contact.getAddressOne():""); sb.append("\",");
								sb.append("\""); sb.append(contact.getAddressTwo()!=null?contact.getAddressTwo():""); sb.append("\",");
								sb.append("\""); sb.append(contact.getCity()!=null?contact.getCity():""); sb.append("\",");
								sb.append("\""); sb.append(contact.getState()!=null?contact.getState():""); sb.append("\",");
								sb.append("\""); sb.append(contact.getCountry()!=null?contact.getCountry():""); sb.append("\",");
								/*sb.append("\""); sb.append(contact.getPin()); sb.append("\",");
								sb.append("\""); sb.append(contact.getPhone()!=null?contact.getPhone():""); sb.append("\"\r\n");*/
								sb.append("\""); sb.append(contact.getZip() !=null?contact.getZip():""); sb.append("\","); 
								sb.append("\""); sb.append(contact.getMobilePhone() !=null?contact.getMobilePhone():""); sb.append("\""); 
								sb.append("\r\n");
							}
						}
						bw.write(sb.toString());
						sb.setLength(0);
						list = null;
						//System.gc();
					}
					
				}
				
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
	private String getCustomFldData(int index, CustomFieldData cfd) {
		if(cfd == null ) return null;
			switch (index) {
			case 1 : return cfd.getCust1();
			case 2 : return cfd.getCust2();
			case 3 : return cfd.getCust3();
			case 4 : return cfd.getCust4();
			case 5 : return cfd.getCust5();
			case 6 : return cfd.getCust6();
			case 7 : return cfd.getCust7();
			case 8 : return cfd.getCust8();
			case 9 : return cfd.getCust9();
			case 10 : return cfd.getCust10();
			case 11 : return cfd.getCust11();
			case 12 : return cfd.getCust12();
			case 13 : return cfd.getCust13();
			case 14 : return cfd.getCust14();
			case 15 : return cfd.getCust15();
			case 16 : return cfd.getCust16();
			case 17 : return cfd.getCust17();
			case 18 : return cfd.getCust18();
			case 19 : return cfd.getCust19();
			case 20 : return cfd.getCust20();
			

		default:
			return null;
		}
		
	}
	
	/**
	 * this method used to retrieve the contacts based on the given search string
	 * @param searchStr
	 * @param status
	 * @param mobileStr
	 * @param mobStatus
	 */

	public void searchByEmail(String searchStr,String status,String mobileStr,String mobStatus)throws Exception {
		MessageUtil.clearMessage();
		List<Contacts> contactList = null;

		//set Multiple(true) to the listModelList for checkmark (zk6ce )		
		ListModelList listModelList = new ListModelList(getAllContactsBySearchStr(searchStr, status,mobileStr,mobStatus));
		listModelList.setMultiple(true);
		contactListLBId.setModel(listModelList);
		contactListLBId.setItemRenderer(new MyRenderer());
		sessionScope.setAttribute("methodCallFlag","1");
		
	}// searchByEmail
	
	/**
	 * this method is allowed to retrieve the contacts based on the given search string
	 * @param searchStr
	 * @param status
	 * @param mobileStr
	 * @param mobStatus
	 * @return List<Contacts> 
	 */
	
	public List<Contacts> getAllContactsBySearchStr(String searchStr, String status,String mobileStr,String mobStatus) {
		
		List<Contacts> contactList = null;
		int size=0;
		logger.debug("---just entered---");
		logger.debug("the search input value is >>>>>"+searchStr+"and status is.."+status);
		
		 if( searchStr.equals("") || searchStr.equals("Search Email...") ) {
			 
			 searchStr ="";
		 }
			if( mobileStr.equals("") || mobileStr.equals("Search Mobile...") ) {
			
			mobileStr ="";
			
		}
		
		if(viewType == null) {
			size =(int) contactsDao.findAllByemailIdAndMobile(mailingList,searchStr,status,mobileStr,mobStatus);
			contactsPagingId.setTotalSize(size);
			contactsPaging1Id.setTotalSize(size);
			contactList = contactsDao.findByemailIdAndMobile(mailingList,searchStr,status,
							contactsPagingId.getActivePage()*pageSize,contactsPagingId.getPageSize(),mobileStr,mobStatus);
		} else {
			size = (int)contactsDao.findAll(mlSet, searchStr, status,mobileStr,mobStatus);
			contactsPagingId.setTotalSize(size);
			contactsPaging1Id.setTotalSize(size);
			contactList = contactsDao.find(mlSet, searchStr, status, contactsPagingId.getActivePage()*pageSize,
							contactsPagingId.getPageSize(),mobileStr,mobStatus);
			}//else		
		return contactList;
	}
	
	/**
	 * this method allows to retrieve the contacts based on the given status
	 * @param status
	 * @param searchStr
	 * @param mobStatus
	 *  * @param mobStr
	 */
	public void getContactsByStatus(String status,String searchStr,String mobStatus,String mobStr) throws Exception {
		MessageUtil.clearMessage();
		List<Contacts> contactList = null;
	
		//set Multiple(true) to the listModelList for checkmark (zk6ce )		
		ListModelList listModelList = new ListModelList(getAllContactsByStatus(status, searchStr,mobStatus,mobStr));
		listModelList.setMultiple(true);
		contactListLBId.setModel(listModelList);
		contactListLBId.setItemRenderer(new MyRenderer());
		sessionScope.removeAttribute("methodCallFlag");
		sessionScope.setAttribute("methodCallFlag", "2");
		
	}// getContactsByStatus

	/**
	 * this method retrieves the contacts based up on the given status
	 * @param status
	 * @param searchStr
	 *  * @param mobStatus
	 * @param mobStr
	 * @return
	 */
	public List<Contacts> getAllContactsByStatus(String status,String searchStr,String mobStatus,String mobStr ) {
		List<Contacts> contactList=null;
		int size = 0;
		
			if(( searchStr.equals("") || searchStr.equals("Search Email...") )){
				searchStr="";
			}
			if(( mobStr.equals("") || mobStr.equals("Search Mobile...") )){
				mobStr="";
			}
			if(viewType==null) {
				size = (int) contactsDao.findAllByStatus(mailingList,status,mobStatus);
				contactsPagingId.setTotalSize(size);
				contactsPaging1Id.setTotalSize(size);
				contactList = contactsDao.findByStatus(mailingList,status,contactsPagingId.getActivePage()*pageSize,
							contactsPagingId.getPageSize(),mobStatus);
			}else {
				size = (int)contactsDao.findByAll(mlSet, status,mobStatus);
				contactsPagingId.setTotalSize(size);
				contactsPaging1Id.setTotalSize(size);
				contactList = contactsDao.find(mlSet,status, contactsPagingId.getActivePage()*pageSize,
							contactsPagingId.getPageSize(),mobStatus);
			}//else
			return contactList;
		
	}
	
	
	private class MyRenderer implements ListitemRenderer, EventListener {
	
	MyRenderer() {
		super();
	}//MyRenderer()
	
	
	
	public void render(Listitem item, java.lang.Object data, int arg2) {
		setValues();
		//logger.debug("render is called");
		TimeZone clientTimeZone =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
		
		if(data instanceof Contacts) {
			Contacts contact = (Contacts)data;
			
			Listcell lc = new Listcell();
			lc.setParent(item);
			
			item.setValue(contact);
			
			lc = new Listcell();
			//Email Address
			if(contact.getEmailId() == null) {
				lc.setLabel("--");
			}else {
				
				A emailAnch = new A(contact.getEmailId());
				emailAnch.setAttribute("CONTACT_VIEW", contact);
				emailAnch.setStyle("cursor:pointer;color:blue;");
				//CONTACT_VIEW
				emailAnch.addEventListener("onClick", this);
				emailAnch.setParent(lc);
			}
			item.appendChild(lc);
			
			List<String> mlList = mailingListDao.findListNamesByContactBit(listIdsSet, contact.getMlBits());
			
			String mlNameStr = "";
			if(mlList  != null ) { 
				Iterator<String> mlSetItr = mlList.iterator();
				while(mlSetItr.hasNext()){
					mlNameStr +=  mlNameStr.length()== 0? mlSetItr.next().toString():", "+mlSetItr.next().toString();
				}
			}
			
			lc = new Listcell();
			lc.setLabel(mlNameStr);
			lc.setTooltiptext(mlNameStr);
			item.appendChild(lc);
			
			if(contact.getMobilePhone() == null || contact.getMobilePhone().equals("")){
				item.appendChild(new Listcell("--"));
			}
			else{

				item.appendChild(new Listcell(contact.getMobilePhone()));
				
			}
			
			//loyalty Data 
			
			String cardNumber=null;
			Double balancePoints=null;
			Double rewardBalance=null;

			List<ContactsLoyalty> loyaltyList = contactsLoyaltyDao.findLoyaltyListByContactId(currentUserId, contact.getContactId());
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
				cardNumber=latestLoyalty.getCardNumber(); //.toString(); app-3734

				//get latest balance points
				balancePoints=latestLoyalty.getLoyaltyBalance();

				//get latest reward balance
				rewardBalance=latestLoyalty.getGiftcardBalance();
			}
			
			//to add Card Number in Listcell
			if(cardNumber == null || cardNumber.equals("")){
				item.appendChild(new Listcell("--"));
			}
			else{
				item.appendChild(new Listcell(cardNumber));
			}
			
			if(contact.getFirstName()==null || contact.getFirstName().trim().length() == 0 ) {
				item.appendChild(new Listcell("--"));
			}
			else {
				item.appendChild(new Listcell(contact.getFirstName()));
			}
			
			if(contact.getLastName()==null || contact.getLastName().trim().length() == 0 ) {
				item.appendChild(new Listcell("--"));
			}
			else {
				item.appendChild(new Listcell(contact.getLastName()));
			}
			
			String date= MyCalendar.calendarToString(contact.getCreatedDate(), MyCalendar.FORMAT_DATEONLY,clientTimeZone);
			
			//to add Created Date in Listcell
			if(date == null || date.equals("")){
				item.appendChild(new Listcell("--"));
			}
			else{
				item.appendChild(new Listcell(date));
			}	
			String date1= MyCalendar.calendarToString(contact.getModifiedDate(), MyCalendar.FORMAT_DATETIME_STDATE,clientTimeZone);
			if(date1 == null || date1.equals("")){
				item.appendChild(new Listcell("--"));
			}
			else{
				item.appendChild(new Listcell(date1));
			}	
			if(contact.getEmailStatus() == null || contact.getEmailStatus().equals("")  ) {
				item.appendChild(new Listcell("--"));
			}
			else {
				
				item.appendChild(new Listcell(contact.getEmailStatus()));
			}
			
			if(contact.getMobileStatus() == null || contact.getMobileStatus().equals("")  ) {
				item.appendChild(new Listcell(Constants.CON_MOBILE_STATUS_PENDING));
			}
			else {
				
				item.appendChild(new Listcell(contact.getMobileStatus()));
			}
		
			if(contact.getLastMailDate() == null) {
				
				item.appendChild(new Listcell("--"));
			}
			else {
			item.appendChild(new Listcell(MyCalendar.calendarToString(
					contact.getLastMailDate(), MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone)));
			}

			

				//to add Balance Points
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
					item.appendChild(new Listcell(rewardBalance.toString()));
				}

				Hbox hbox = new Hbox();

				Image img = new Image("/img/theme/preview_icon.png");
				img.setStyle("margin-right:5px;cursor:pointer;");
				img.setTooltiptext("View Details");
				img.setAttribute("imageEventName", "view");
				img.addEventListener("onClick",this);
				img.setParent(hbox);

				img = new Image("/img/icons/small_edit_icon.png");
				img.setStyle("margin-right:5px;cursor:pointer;");
				img.setTooltiptext("Edit Contact");
				img.setAttribute("imageEventName", "edit");
				img.addEventListener("onClick",this);
				img.setParent(hbox);
			
			lc = new Listcell();
			hbox.setParent(lc);
			item.appendChild(lc);
		}
	}//render
	
	
	@Override
	public void onEvent(Event evt) throws Exception {
		Object obj = evt.getTarget();
		if(obj instanceof Image) {
			
			Image img = (Image)obj;
			Listitem li = (Listitem)img.getParent().getParent().getParent();
			String eventName = (String)img.getAttribute("imageEventName");
			Contacts contact = (Contacts)li.getValue();
			if( eventName.equalsIgnoreCase("view") && contact  != null) {
				
				//sessionScope.setAttribute("editcontact","view")
				logger.info(">>> contact Id ::"+contact.getContactId());
//				Contacts contact = (Contacts)img.getAttribute("CONTACT_VIEW");
				sessionScope.setAttribute("VIEW_CONTACT_DETAILS", contact);
				Redirect.goTo(PageListEnum.CONTACT_CONTACT_DETAILS);
				
				
			} else if(eventName.equalsIgnoreCase("edit")) {
				
				if(contact.getUsers().getUserId().longValue() != currentUserId.longValue()) {
					
					MessageUtil.setMessage("Selected contact is shared by others.You cannot edit it.", "color:red;");
					return;
					
					
				}//if
				
				
				if(contact.getEmailStatus().equals(Constants.CONT_STATUS_PARENTAL_PENDING)) {
					
					ContactParentalConsent contactParentalConsent = contactParentalConsentDao.findByContactId(contact.getContactId());
					if(contactParentalConsent != null && contactParentalConsent.getStatus()
							.equalsIgnoreCase(Constants.CONT_PARENTAL_STATUS_PENDING_APPROVAL)) {
						
						MessageUtil.setMessage("Contact has been identified to be below 13 years of age." +
								" \n Please approve the contact to have received parent's \n" +
								"consent on Parental Consent Approvals screen.", "color:red;");
						return;
						
					}//if
					
				}
				
				
				sessionScope.setAttribute("editcontact","edit");
				
				sessionScope.setAttribute("editcontact", (String)img.getAttribute("imageEventName") );
				Contacts contObj =(Contacts)li.getValue();
				logger.info(">>>>>>>>>"+contObj);
				if(contObj != null) {
					
					sessionScope.setAttribute("emailId",contObj);
					Redirect.goTo(PageListEnum.CONTACT_EDIT_CONTACT);
				}
				
			}
			
			
		} 
		else if(obj instanceof Paging) {
			
			Paging paging = (Paging) obj;
			int desiredPage = paging.getActivePage();
			
			if(paging.getAttribute("onPaging").equals("topPaging")) {
				logger.info("desired page of topPaging=====>"+desiredPage);
				contactsPaging1Id.setActivePage(desiredPage);
				
				
			}else if(paging.getAttribute("onPaging").equals("bottomPaging")) {
				logger.info("desired page of bottomPaging=====>"+desiredPage);
				contactsPagingId.setActivePage(desiredPage);
				
				
				
			}//else if
			
			PagingEvent pagingEvent = (PagingEvent) evt;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
			logger.debug(""+ofs+""+pSize);
			String flag=(String)(sessionScope.getAttribute("methodCallFlag"));
			if(flag.equals("1")) {
				
				//set Multiple(true) to the listModelList for checkmark (zk6ce )
				ListModelList tempListModelList = new ListModelList(getAllContactsBySearchStr(searchBoxId.getValue(),
																	contactStatusLbId.getSelectedItem().getLabel(),mobileSearchBoxId.getValue(),contactMobileStatusLbId.getSelectedItem().getLabel()));
				tempListModelList.setMultiple(true);
				/*contactListLBId.setModel(new ListModelList(getAllContactsBySearchStr(searchBoxId.getValue(),
						contactStatusLbId.getSelectedItem().getLabel())));*/
				contactListLBId.setModel(tempListModelList);
				contactListLBId.setItemRenderer(renderer);
				
			}else if(flag.equals("0")) {
				
				//set Multiple(true) to the listModelList for checkmark (zk6ce )
				ListModelList tempListModelList = new ListModelList(getAllContacts());
				tempListModelList.setMultiple(true);
				//contactListLBId.setModel(new ListModelList(getAllContacts()));//((List<Contacts>)(sessionScope.getAttribute("contactList"))));
				contactListLBId.setModel(tempListModelList);
				contactListLBId.setItemRenderer(renderer);
			
			} else if(flag.equals("2")) {
				
				//set Multiple(true) to the listModelList for checkmark (zk6ce )
				ListModelList tempListModelList = new ListModelList(getAllContactsByStatus(contactStatusLbId.getSelectedItem().getLabel(),
														searchBoxId.getValue(),contactMobileStatusLbId.getSelectedItem().getLabel(),mobileSearchBoxId.getValue()));
				tempListModelList.setMultiple(true);
//				contactListLBId.setModel(new ListModelList(getAllContactsByStatus(contactStatusLbId.getSelectedItem().getLabel(),
//						searchBoxId.getValue())));//(searchBoxId.getValue(), contactStatusLbId.getSelectedItem().getLabel())));
				contactListLBId.setModel(tempListModelList);
				contactListLBId.setItemRenderer(renderer);
			}
			
		}
		else if(obj instanceof A){
	
			A tb = (A)obj;
			logger.info(" >>> tb.getAttribute of CONTACT_VIEW is  ::"+tb.getAttribute("CONTACT_VIEW"));
			if(tb.getAttribute("CONTACT_VIEW") != null) {
				
				Contacts contact = (Contacts)tb.getAttribute("CONTACT_VIEW");
				if(contact != null) {
					logger.info(">>> contact Id is"+contact.getContactId());
					sessionScope.setAttribute("VIEW_CONTACT_DETAILS", contact);
					Redirect.goTo(PageListEnum.CONTACT_CONTACT_DETAILS);
				}
				
			}else {
				
				Window viewScoreWinId = ((Window)viewContactListWinId.getFellowIfAny("viewScoreWinId"));
				
				ContactScores contactScores = (ContactScores)tb.getAttribute("contactScoresObj");
				sessionScope.setAttribute("contactScores", contactScores);
				showScoreBreakup(contactScores,viewScoreWinId);
				viewScoreWinId.doModal();
			}
			
			
		}
	}//onEvent
	
	
 }
	
	/** Displaying for individual contact score(Score Breakup) when Click on a AnchorTag in My Contacts
	 * @param contactScores
	 * @param viewScoreWinId
	 */
	public void showScoreBreakup(ContactScores contactScores, Window viewScoreWinId) {
		try{
			 TimeZone tz =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
			Rows scoreBreakUpRowsId = (Rows)viewScoreWinId.getFellowIfAny("scoreBreakUpRowsId");
			
			String lblIds[] = {
				"pageVisitedScoreLblId",
				"downloadedLblId",
				"srcOfVisitedLblId",
				"emailOpenedLblId",
				"emailClickedLblId",
				"emailNotOpenedLblId",
				"emailUnsubscribedLblId",
				"formSubmitLblId",
				"formAbondonedLblId",
				"formFillRatioLblId"
			};

			Integer totalScore=0;
			Label tempLabel= null;
			String tempVal=null;
			Integer tempScore=null;
			
			for (int i = 0; i < lblIds.length; i++) {
				
				((Label)((Row)(scoreBreakUpRowsId.getChildren().get(i))).getFirstChild()).
				setValue(MyCalendar.calendarToString(contactScores.getLastModifiedDate(), MyCalendar.FORMAT_DATETIME_STDATE, tz));
				switch (i) {
				
				case 0: tempScore = contactScores.getPageVisitedCount(); 		break;
				case 1: tempScore = contactScores.getDownLoadedCount(); 		break;
				case 2: tempScore = contactScores.getSourceOfVisitCount(); 		break;
				case 3: tempScore = contactScores.getEmailOpenedCount();		break;
				case 4: tempScore = contactScores.getEmailClickedCount(); 		break;
				case 5: tempScore = contactScores.getEmailNotOpenedCount(); 	break;
				case 6: tempScore = contactScores.getEmailUnsubscribedCount();	break;
				case 7: tempScore = contactScores.getFormSubmittedCount(); 		break;
				case 8: tempScore = contactScores.getFormAbondonedCount();	 	break;
				case 9: tempScore = contactScores.getFormFillRatioCount();	 	break;
				
				default:break;
				}
				
				tempLabel = (Label)viewScoreWinId.getFellowIfAny(lblIds[i]);
				
				if(tempLabel==null) continue;
				if(tempScore != null){
				 totalScore=totalScore+tempScore;
				}
//				totalScore=totalScore+tempScore;
				tempVal = (tempScore != null) ? ""+tempScore : "Never";
				tempLabel.setValue(tempVal);
				
			} // for i
			
			Footer tempfooter=(Footer)viewScoreWinId.getFellowIfAny("totalLblId");
			tempfooter.setLabel(""+totalScore);
			
		}catch (Exception e) {
		logger.error("Exception ::", e);
		}
		
		
		
		
	}
	
	/**
	 * Displaying Anchor Tag Value (Total Score)
	 * @param contactScores
	 * @return
	 */
	public String getTotal(ContactScores contactScores) {
		
		Integer tempScore=null;
		Integer totalScore=0;
		
		
		String lblIds[] = {
				"pageVisitedScoreLblId",
				"downloadedLblId",
				"srcOfVisitedLblId",
				"emailOpenedLblId",
				"emailClickedLblId",
				"emailNotOpenedLblId",
				"emailUnsubscribedLblId",
				"formSubmitLblId",
				"formAbondonedLblId",
				"formFillRatioLblId"
			};
		for (int i = 0; i < lblIds.length; i++) {
			
			
			switch (i) {
			
			case 0: tempScore = contactScores.getPageVisitedCount(); 		break;
			case 1: tempScore = contactScores.getDownLoadedCount(); 		break;
			case 2: tempScore = contactScores.getSourceOfVisitCount(); 		break;
			case 3: tempScore = contactScores.getEmailOpenedCount();		break;
			case 4: tempScore = contactScores.getEmailClickedCount(); 		break;
			case 5: tempScore = contactScores.getEmailNotOpenedCount(); 	break;
			case 6: tempScore = contactScores.getEmailUnsubscribedCount();	break;
			case 7: tempScore = contactScores.getFormSubmittedCount(); 		break;
			case 8: tempScore = contactScores.getFormAbondonedCount();	 	break;
			case 9: tempScore = contactScores.getFormFillRatioCount();	 	break;
			
			default:break;
			}
		
			if(tempScore != null){
				 totalScore=totalScore+tempScore;
				}
		
		
	}
	return ""+totalScore;
	
	}	

// exporting here
	
	/*public void onClick$exportBtnId() {
		try {
			exportCSV((String)exportCbId.getSelectedItem().getValue());
		} catch (Exception e) {
			logger.error("Error occured from the exportCSV method ***",e);
		}
	}*/

	public void onClick$backBtnId() {
		try {
			gotoMylists();
		} catch (Exception e) {
			logger.error("Error occured from the gotoMylists method ***",e);
		}
	}
	
	private  static String ERROR_STYLE = " border:1px solid #DD7870;";
	private  static String NORMAL_STYLE = "position:relative border:1px solid #7F9DB9;";
	private Textbox mobileSearchBoxId,searchTBId;
	private Listbox contactMobileStatusLbId,searchLBId,mobileStatusLbId,membershipStatusLbId;
	/*public void onClick$searchEmailBtnId() {
		try {
			if(contactListLBId.getItemCount() == 0) {
				
				MessageUtil.setMessage("No contacts found with given search criteria.", "color:red");
				
				logger.debug("No contacts found......");
				return;
				
			}
			changeRows(pageSizeLbId.getSelectedItem().getLabel());
		
			searchByEmail(searchBoxId.getValue(),contactStatusLbId.getSelectedItem().getLabel(),mobileSearchBoxId.getValue(),contactMobileStatusLbId.getSelectedItem().getLabel());
		
		
		} catch (Exception e) {
			logger.error("Error occured from the searchByEmail method ***",e);
		}
	}*/
	public void setValues()
	{
		//logger.info("set values method");
		searchLBId.setSelectedIndex(searchCriteriaIndex);
		if(searchFieldStr.contains("'")){
			searchTBId.setValue(searchFieldStrWithoutQuote);
		}else{
			searchTBId.setValue(searchFieldStr);
		}
		
		contactStatusLbId.setSelectedItem(emailStatusItem);
		mobileStatusLbId.setSelectedItem(mobileStatusItem);
	}
	private String searchFieldStr;
	private String searchFieldStrWithoutQuote;
	private Listitem emailStatusItem,mobileStatusItem,membershipStatusItem;
	private int searchCriteriaIndex;
	private String searchFirstStr,searchLastStr;
		public void onClick$searchEmailBtnId() { 
		try {

			searchFieldStr = searchTBId.getValue().trim();
			searchCriteriaIndex = searchLBId.getSelectedIndex();
			searchFirstStr = first_nameTbId.getValue().trim();
			searchLastStr = last_nameTbId.getValue().trim();
			logger.info("searchFieldStr>>" + searchFieldStr + "searchCriteriaIndex>>" + searchCriteriaIndex);
			//searchTBId.setStyle(NORMAL_STYLE);
			//searchTBId.setValue(Constants.STRING_NILL);
			if (searchByDateBxId.isVisible()) {
				tempCal1 = (MyCalendar) searchByDateBxId.getClientValue();
				tempCal = MyCalendar.calendarToString(tempCal1, MyCalendar.FORMAT_YEARTODATE);
				// logger.info("date as stringggggggggggggggggggggggggggggg :
				// onClick$searchEmailBtnId "+tempCal);
			}

			if (searchLBId.getSelectedIndex() == 0 && searchFieldStr.length() > 0
					&& !searchFieldStr.startsWith("Search")) {
				MessageUtil.setMessage("Please select the Criteria.", "color:red", "TOP");
				return;
			} else if (searchLBId.getSelectedIndex() > 0
					&& (searchFieldStr.length() == 0 || searchFieldStr.startsWith("Search"))
					&& searchTBId.isVisible()) {
				MessageUtil.setMessage("Please provide text in Textbox.", "color:red", "TOP");
				searchTBId.setStyle(ERROR_STYLE);
				return;
			} else if (searchLBId.getSelectedIndex() == 3) {
				if (searchFieldStr.startsWith("Search") || searchFieldStr.trim().length() == 0) {
					MessageUtil.setMessage("Please provide membership number.", "color:red", "TOP");
					searchTBId.setStyle(ERROR_STYLE);
					return;
				}
				try {
					Long.parseLong(searchFieldStr);
				} catch (Exception e) {
					MessageUtil.setMessage("Please provide  valid membership number.", "color:red", "TOP");
					searchTBId.setStyle(ERROR_STYLE);
					logger.error("Exception ::", e);

					return;
				}
			} else if (searchFieldStr.contains("'")) {
				searchFieldStrWithoutQuote = searchFieldStr;
				searchFieldStr = StringEscapeUtils.escapeSql(searchFieldStr);
				/*
				 * MessageUtil.
				 * setMessage("Please enter valid search text without using single quote.",
				 * "color:red", "TOP"); return;
				 */
			}

			try {
				emailStatusItem = contactStatusLbId.getSelectedItem();
				mobileStatusItem = mobileStatusLbId.getSelectedItem();
				membershipStatusItem = membershipStatusLbId.getSelectedItem();
				String emailStatusStr = contactStatusLbId.getSelectedIndex() == 0 ? ""
						: contactStatusLbId.getSelectedItem().getLabel();
				String mobileStatusStr = mobileStatusLbId.getSelectedIndex() == 0 ? ""
						: mobileStatusLbId.getSelectedItem().getLabel();
				String membershipStatusStr = membershipStatusLbId.getSelectedIndex() == 0 ? ""
						: membershipStatusLbId.getSelectedItem().getLabel();

				myPagingId.setPageSize(Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
				myPaging1Id.setPageSize(Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));

				int size = 0;

				if (viewType == null) {

					if (searchLBId.getSelectedIndex() > 5) {

						POSMapping posmappingList = searchLBId.getSelectedItem().getValue();

						String customFieldName = posmappingList.getCustomFieldName();
						// logger.info("indexssssssssssssssssssssss:"+customFieldName);

						if (searchby_Date_String) {
							filterCountQuery = "SELECT COUNT(c.contactId) " + findFilterContactsListCountByUdfField(
									mailingList, customFieldName, tempCal, emailStatusStr, mobileStatusStr,membershipStatusStr);

							filterQuery = findFilterContactsListCountByUdfField(mailingList, customFieldName, tempCal,
									emailStatusStr, mobileStatusStr, membershipStatusStr);
						} else {
							filterCountQuery = "SELECT COUNT(c.contactId) "
									+ findFilterContactsListCountByUdfField(mailingList, customFieldName,
											searchFieldStr.trim(), emailStatusStr, mobileStatusStr, membershipStatusStr);

							filterQuery = findFilterContactsListCountByUdfField(mailingList, customFieldName,
									searchFieldStr.trim(), emailStatusStr, mobileStatusStr, membershipStatusStr);
						}
						/*
						 * contactsPagingId.setTotalSize(size); contactsPaging1Id.setTotalSize(size);
						 */

					} else {
						//changes App- current
						if (searchLBId.getSelectedIndex() == 4 && validateFullName()) {
						filterCountQuery = "SELECT COUNT(c.contactId) " + findFilterContactsListCount(mailingList,
								searchLBId.getSelectedIndex(), searchFirstStr.trim(),searchLastStr.trim(), emailStatusStr, mobileStatusStr,membershipStatusStr);
						/*
						 * contactsPagingId.setTotalSize(size); contactsPaging1Id.setTotalSize(size);
						 */
						filterQuery = findFilterContactsListCount(mailingList, searchLBId.getSelectedIndex(),
								searchFirstStr.trim(),searchLastStr.trim(), emailStatusStr, mobileStatusStr,membershipStatusStr);
						}
						else {
							filterCountQuery = "SELECT COUNT(c.contactId) " + findFilterContactsListCount(mailingList,
									searchLBId.getSelectedIndex(), searchFieldStr.trim(),null, emailStatusStr, mobileStatusStr,membershipStatusStr);
							/*
							 * contactsPagingId.setTotalSize(size); contactsPaging1Id.setTotalSize(size);
							 */
							filterQuery = findFilterContactsListCount(mailingList, searchLBId.getSelectedIndex(),
									searchFieldStr.trim(),null, emailStatusStr, mobileStatusStr,membershipStatusStr);	
						}

					}
				} else {

					if (searchLBId.getSelectedIndex() > 5) {

						POSMapping posmappingList = searchLBId.getSelectedItem().getValue();

						String customFieldName = posmappingList.getCustomFieldName();
						// logger.info("indexssssssssssssssssssssss:"+customFieldName);

						if (searchby_Date_String) {

							logger.info("dataType_customFieldName   :" + tempCal);
							filterCountQuery = findCountQueryfromMlSet(mlSet, searchLBId.getSelectedIndex(), tempCal,
									null, emailStatusStr, mobileStatusStr, customFieldName, membershipStatusStr);
							filterQuery = findContListFromMlSet(mlSet, searchLBId.getSelectedIndex(), tempCal, null, // changes
																														// 2.5.3.0
									emailStatusStr, mobileStatusStr, customFieldName, membershipStatusStr);
						} else {
							filterCountQuery = findCountQueryfromMlSet(mlSet, searchLBId.getSelectedIndex(),
									searchFieldStr.trim(), null, emailStatusStr, mobileStatusStr, customFieldName, membershipStatusStr);
							filterQuery = findContListFromMlSet(mlSet, searchLBId.getSelectedIndex(),
									searchFieldStr.trim(), null, emailStatusStr, mobileStatusStr, customFieldName, membershipStatusStr);
						}
					} else if (searchLBId.getSelectedIndex() == 4 && validateFullName()) {
						filterCountQuery = findCountQueryfromMlSet(mlSet, searchLBId.getSelectedIndex(), searchFirstStr,
								searchLastStr, emailStatusStr, mobileStatusStr, null, membershipStatusStr);
						filterQuery = findContListFromMlSet(mlSet, searchLBId.getSelectedIndex(), searchFirstStr,
								searchLastStr, emailStatusStr, mobileStatusStr, null, membershipStatusStr);
					} else {
						filterCountQuery = findCountQueryfromMlSet(mlSet, searchLBId.getSelectedIndex(),
								searchFieldStr.trim(), null, emailStatusStr, mobileStatusStr, null,  membershipStatusStr);
						filterQuery = findContListFromMlSet(mlSet, searchLBId.getSelectedIndex(), searchFieldStr.trim(),
								null, emailStatusStr, mobileStatusStr, null, membershipStatusStr);
					}
				} // else

				/*
				 * contactsPagingId.setTotalSize(size); contactsPaging1Id.setTotalSize(size);
				 */
				myPagingId.setTotalSize(size);
				myPaging1Id.setTotalSize(size);

				logger.info("filterCountQuery is ::" + filterCountQuery);
				logger.info("filterQuery is ::" + filterQuery);

				Map<Integer, Field> objMap = new HashMap<Integer, Field>();

				objMap.put(1, Contacts.class.getDeclaredField("emailId"));
				objMap.put(3, Contacts.class.getDeclaredField("mobilePhone"));
				objMap.put(5, Contacts.class.getDeclaredField("firstName"));
				objMap.put(6, Contacts.class.getDeclaredField("lastName"));
				objMap.put(7, Contacts.class.getDeclaredField("createdDate"));
				objMap.put(8, Contacts.class.getDeclaredField("modifiedDate"));
				objMap.put(9, Contacts.class.getDeclaredField("emailStatus"));
				objMap.put(10, Contacts.class.getDeclaredField("mobileStatus"));
				objMap.put(11, Contacts.class.getDeclaredField("lastMailDate"));

				myPagingId.setPageSize(Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
				
				myPaging1Id.setPageSize(Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
				if (filterCountQuery != null && filterQuery != null) {
					contactLbELObj = LBFilterEventListener.lbFilterSetup1(contactListLBId, myPagingId,myPaging1Id, filterQuery,
							filterCountQuery, qryPrefix, objMap);
					Utility.refreshModel1(contactLbELObj, 0, true);

				}

				return;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::", e);
			}

		} catch (Exception e) {
			logger.error("Error occured from the searchByEmail method ***", e);
		}
	}

	private boolean searchby_Date_String;
	MyCalendar tempCal1;
	String tempCal;
	Date udfDateBox = new Date();

	public void onSelect$searchLBId() {
		searchTBId.setText("");
		first_nameTbId.setText("");
		last_nameTbId.setText("");
		
		if (searchLBId.getSelectedIndex() > 5) {
			POSMapping posmappingList = searchLBId.getSelectedItem().getValue();
			String dataType_customFieldName = posmappingList.getDataType();

			if (dataType_customFieldName.startsWith("Date")) {

				fullNameSpanId.setVisible(false);
				searchTBId.setVisible(false);
				searchByDateBxId.setVisible(true);
				searchByDateBxId.setValue(udfDateBox);
				/*
				 * tempCal1=(MyCalendar)searchByDateBxId.getClientValue(); tempCal =
				 * MyCalendar.calendarToString(tempCal1, MyCalendar.FORMAT_YEARTODATE);
				 * logger.info("date as stringggggggggggggggggggggggggggggg:   onSelect  "
				 * +tempCal);
				 */
				searchby_Date_String = true;
			} else {
				fullNameSpanId.setVisible(false);
				searchTBId.setVisible(true);
				searchByDateBxId.setVisible(false);
				searchby_Date_String = false;
			}
		} else if (searchLBId.getSelectedIndex() == 4) {
			fullNameSpanId.setVisible(true);
			searchTBId.setVisible(false);
			searchByDateBxId.setVisible(false);
			searchby_Date_String = false;
		} else {
			fullNameSpanId.setVisible(false);
			searchTBId.setVisible(true);
			searchByDateBxId.setVisible(false);
			searchby_Date_String = false;
		}

	}

	/*
	 * public void onClick$actionsBndBoxCloseBtnId() { actionsBandBoxId.close();
	 * open = true; }
	 */
	private Bandbox contActionsBandBoxId;

	public void onSelect$contactListLBId() {
		//logger.info("in onselect of the contacts list box.........");
		
		if(contactListLBId.getSelectedCount() == 0 || 
				(viewType == null && mailingList.getUsers().getUserId().longValue() != currentUserId.longValue() )){
			
			contActionsBandBoxId.setDisabled(true);
			contActionsBandBoxId.setButtonVisible(false);
		}else if(contactListLBId.getSelectedCount() > 0) {
			
			contActionsBandBoxId.setDisabled(isDifferOwner);
			contActionsBandBoxId.setButtonVisible(!isDifferOwner);
		}
		
		
		
		
		
		
	}//onSelect$contactListLBId()
	
	//boolean open = true;
	/*public void onDrop$actionsBandpopupId() {
		
		actionsBandBoxId.setOpen(open);
		
		
		
	}//onDrop$actionsBandpopupId()
*/	
	/*public void onOpen$actionsBandBoxId() {
		
		
		open = false; 
		
		
		
	}//onOpen$actionsBandBoxId()
	*/
	/*public void onClose$actionsBandBoxId() {
		
		
		open = true;
		
	}//onClose$actionsBandBoxId()
*/	
	/*public void onClick$actionsBandBoxId() {
		
		actionsBandBoxId.setOpen(open);
		open = !open;
		
		
	}//onClick$actionsBandBoxId()
*/	
	/*public void onClick$resetSearchCriteriaAnchId() {
		
		try {
			
			
			searchBoxId.setValue("Search Email...");
			contactStatusLbId.setSelectedIndex(0);
			mobileSearchBoxId.setValue("Search Mobile...");
			contactMobileStatusLbId.setSelectedIndex(0);
			pageSizeLbId.setSelectedIndex(0);
			if(contactListLBId.getItemCount() == 0) {
				
				logger.debug("No Contacts found....");
				return;
				
			}
			
			changeRows(pageSizeLbId.getSelectedItem().getLabel());
		
			searchByEmail(searchBoxId.getValue(),contactStatusLbId.getSelectedItem().getLabel(),mobileSearchBoxId.getValue(),contactMobileStatusLbId.getSelectedItem().getLabel());
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
	}//onClick$resetSearchCriteriaAnchId()
*/	
	
	public void onClick$resetSearchCriteriaAnchId() {
		
		try {
			
			searchLBId.setSelectedIndex(0);
			searchTBId.setValue("Search...");
			contactStatusLbId.setSelectedIndex(0);
			mobileStatusLbId.setSelectedIndex(0);
			membershipStatusLbId.setSelectedIndex(0);
			pageSizeLbId.setSelectedIndex(0);
			setDefaultMethod();
			/*if(contactListLBId.getItemCount() == 0) {
				
				logger.debug("No Contacts found....");
				return;
				
			}*/
			
			//changeRows(pageSizeLbId.getSelectedItem().getLabel());
		
			//searchByEmail(searchBoxId.getValue(),contactStatusLbId.getSelectedItem().getLabel(),mobileSearchBoxId.getValue(),contactMobileStatusLbId.getSelectedItem().getLabel());
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
	}//onClick$resetSearchCriteriaAnchId()
	
	/*public void onClick$contactStatusBtnId() {
		try {
			getContactsByStatus(contactStatusLbId.getSelectedItem().getLabel(),searchBoxId.getValue());
		
		} catch (Exception e) {
			logger.error("Error occured from the getContactsByStatus method ***",e);
		}
	}*/
	
	public void onClick$changeStatusBtnId() {
		try {
			changeStatus(statusLBId.getSelectedItem().getLabel());
		} catch (Exception e) {
			logger.error("Error occured from the changeStatus method ***",e);
		}
	}
	
	
	
	public void onClick$mchangeStatusBtnId() {
		try {
			mchangeStatus(mstatusLBId.getSelectedItem().getLabel());
		} catch (Exception e) {
			logger.error("Error occured from the mchangeStatus method ***",e);
		}
	}
	
	
	
	public void onSelect$pageSizeLbId() {
		try {
			setValues();
			//onClick$searchEmailBtnId();
			myPagingId.setPageSize(Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
			myPaging1Id.setPageSize(Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
			Utility.refreshModel1(contactLbELObj, 0, true);
			logger.info("Contact Navigation");
			/*if(true) return;
			
			changeRows(pageSizeLbId.getSelectedItem().getLabel());
			searchByEmail(searchBoxId.getValue(),contactStatusLbId.getSelectedItem().getLabel(),mobileSearchBoxId.getValue(),contactMobileStatusLbId.getSelectedItem().getLabel());*/
		} catch (Exception e) {
			logger.error("Error occured from the changeRows method ***",e);
		}
	}

	public void onClick$moveToListBtnId() {
		try {
			if (contactListLBId.getSelectedCount() == 0 || toMlistlbId.getItemCount() == 0) {

				logger.debug("No mailing lists/contacts found....");

				return;

			}
			// logger.info(" "+(MailingList)(toMlistlbId.getSelectedItem().getValue()));
			// logger.info(" "+(String)(toMlistlbId.getSelectedItem().getValue()));
			logger.info(" " + toMlistlbId.getSelectedItem().getValue());
			if (toMlistlbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Select a list")) {
				MessageUtil.setMessage("Please select a list", "red");
			} else {
				moveToList((MailingList) (toMlistlbId.getSelectedItem().getValue()));
			}
		} catch (Exception e) {
			logger.error("Error occured from the moveToList method ***", e);
		}
	} // onClick$moveToListBtnId

	public void onClick$delContactsBtnId() {
		try {
			deleteSelectedContacts();
		} catch (Exception e) {
			logger.error("Error occured from the deleteSelectedContacts method ***", e);
		}
	}

/*	private String findFilterContactsListCount(MailingList mList, int selectFieldIDx, String searchValStr,String searchLastStr,
			String emailStatus, String mobStatus) {

		String subQry = "";

		if (selectFieldIDx == 1) {
			subQry += " AND c.emailId like '%" + searchValStr + "%'";
		} else if (selectFieldIDx == 2) {
			subQry += " AND c.mobilePhone like '%" + searchValStr + "%'";
		} else if (selectFieldIDx == 3) {
			subQry += " AND c.contactId in ( SELECT cl.contact FROM ContactsLoyalty cl WHERE cl.userId in("
					+ mList.getUsers().getUserId() + ") AND cl.cardNumber=" + searchValStr + ")";
			// subQry += " AND c.contactId ="+ searchValStr +""
		} else if (selectFieldIDx == 4) {
			if (!searchValStr.isEmpty() && !searchLastStr.isEmpty()) {
				subQry += " AND  ( c.firstName like '%" + searchValStr + "%' OR c.lastName like '%" + searchLastStr
						+ "%' )  ";
			} else if (!searchValStr.isEmpty() && searchLastStr.isEmpty()) {
				subQry += " AND  c.firstName like '%" + searchValStr + "%'";
			} else if (searchValStr.isEmpty() && !searchLastStr.isEmpty()) {
				subQry += " AND c.lastName like '%" + searchLastStr + "%' ) ";
			}
		}
		
		else if (selectFieldIDx == 5) {
			subQry += " AND c.lastName like '%" + searchValStr + "%'";
		}

		if (!emailStatus.isEmpty()) {

			subQry += " AND c.emailStatus = '" + emailStatus + "'";

		}

		if (!mobStatus.isEmpty()) {

			subQry += " AND c.mobileStatus = '" + mobStatus + "'";

		}

		String query = " FROM Contacts c WHERE c.users = " + mList.getUsers().getUserId()
				+ " AND bitwise_and(c.mlBits, " + mList.getMlBit() + ") > 0 " + subQry;
		return query;
	}// findAllByemailIdAndMobile
*/
	
	private String findFilterContactsListCount(MailingList mList, int selectFieldIDx, String searchValStr,String searchLastStr,
			String emailStatus, String mobStatus,String membershipStatus) {

		String subQry = "";

		if (selectFieldIDx == 1) {
			subQry += " AND c.emailId like '%" + searchValStr + "%'";
		} else if (selectFieldIDx == 2) {
			subQry += " AND c.mobilePhone like '%" + searchValStr + "%'";
		} else if (selectFieldIDx == 3) {
			subQry += " AND c.contactId in ( SELECT cl.contact FROM ContactsLoyalty cl WHERE cl.userId in("
					+ mList.getUsers().getUserId() + ") AND cl.cardNumber=" + searchValStr + ")";
			// subQry += " AND c.contactId ="+ searchValStr +""
		} else if (selectFieldIDx == 4) {
			if (!searchValStr.isEmpty() && !searchLastStr.isEmpty()) {
				subQry += " AND  ( c.firstName like '%" + searchValStr + "%' AND c.lastName like '%" + searchLastStr
						+ "%' )  ";
			} else if (!searchValStr.isEmpty() && searchLastStr.isEmpty()) {
				subQry += " AND  c.firstName like '%" + searchValStr + "%'";
			} else if (searchValStr.isEmpty() && !searchLastStr.isEmpty()) {
				subQry += " AND c.lastName like '%" + searchLastStr + "%'  ";
			}
		}
		
		/*else if (selectFieldIDx == 5) {
			subQry += " AND c.lastName like '%" + searchValStr + "%'";
		}*/

		if (!emailStatus.isEmpty()) {

			subQry += " AND c.emailStatus = '" + emailStatus + "'";

		}

		if (!mobStatus.isEmpty()) {

			subQry += " AND c.mobileStatus = '" + mobStatus + "'";

		}
		if(!membershipStatus.isEmpty())
		{
		Iterator<MailingList> mlIter = mlSet.iterator();
		String userIdStr = "";
		Set<String> userIdSet = new HashSet<String>();
		while (mlIter.hasNext()) {
			userIdSet.add("" + mlIter.next().getUsers().getUserId().longValue());
		}
		for (String str : userIdSet) {

			if (!userIdStr.isEmpty())
				userIdStr += ",";
			userIdStr += str;
		}


			subQry += " AND c.contactId in (SELECT cl.contact FROM ContactsLoyalty cl WHERE cl.userId in("
				+ userIdStr + ") and cl.membershipStatus='"+membershipStatus+"')";
		}
		
		
	//	String query;
		
		/*if(!membershipStatus.isEmpty()){
			
			 query="from contacts c join contacts_loyalty cl on c.cid = cl.contact_id "
					+ " where c.users = "+mList.getUsers().getUserId()+" and c.user_id=cl.user_id "
					+" and bitwise_and(c.mlBits, " + mList.getMlBit() + ") > 0"
					+" and cl.membership_status="+membershipStatus+" "
					+" and c.mobile_status = "+mobStatus+" "
					+" and c.email_status = "+emailStatus+"";									
		}else{*/

		String query = " FROM Contacts c WHERE c.users = " + mList.getUsers().getUserId()
				+ " AND bitwise_and(c.mlBits, " + mList.getMlBit() + ") > 0 " + subQry;
		return query;
	}// findAllByemailIdAndMobile
	private String findFilterContactsListCountByUdfField(MailingList mList, String customFieldName, String searchValStr,
			String emailStatus, String mobStatus , String membershipStatus) {

		String subQry = "";

		subQry += " AND c." + customFieldName.toLowerCase() + " like '%" + searchValStr + "%'";
		// subQry += " AND c.contactId ="+ searchValStr +""
		logger.info("subQry is :::::::::::" + subQry);

		String query = " FROM Contacts c WHERE c.users = " + mList.getUsers().getUserId()
				+ " AND bitwise_and(c.mlBits, " + mList.getMlBit() + ") > 0 " + subQry;
		logger.info("subQry is ::" + subQry);
		return query;
	}// findFilterContactsListCountByUdfField

	private String findContListFromMlSet(Set<MailingList> mlSet, int selectFieldIDx, String searchValStr,
			String searchLastStr, String emailStatus, String mobStatus, String customFieldName , String membershipStatus) {
		long count = 0;

		List<Contacts> contactsList = null;
		logger.debug("--just entered--");
		try {
			Iterator<MailingList> mlIt = mlSet.iterator();

			long mlsbit = Utility.getMlsBit(mlSet);
			String queryStr = Utility.getMultiUserQry(mlSet, mlsbit);
			logger.info("queryStr under find List ::" + queryStr);
			String subQry = "";

			if (selectFieldIDx > 5) {
				subQry += " AND c." + customFieldName.toLowerCase() + " like '%" + searchValStr + "%'";
			} else if (selectFieldIDx == 1) {
				subQry += " AND c.emailId like '%" + searchValStr + "%'";
			} else if (selectFieldIDx == 2) {
				subQry += " AND c.mobilePhone like '%" + searchValStr + "%'";
			} else if (selectFieldIDx == 3) {
				String userIdStr = "";
				Iterator<MailingList> mlIter = mlSet.iterator();
				Set<String> userIdSet = new HashSet<String>();
				while (mlIter.hasNext()) {
					userIdSet.add("" + mlIter.next().getUsers().getUserId().longValue());
				}
				for (String str : userIdSet) {

					if (!userIdStr.isEmpty())
						userIdStr += ",";
					userIdStr += str;
				}

				subQry += " AND c.contactId in (SELECT cl.contact FROM ContactsLoyalty cl WHERE cl.userId in("
						+ userIdStr + ") AND cl.cardNumber=" + searchValStr + ")";

			} else if (selectFieldIDx == 4 && searchValStr != null && searchLastStr != null) {
				if (!searchValStr.isEmpty() && !searchLastStr.isEmpty()) {
					subQry += " AND  ( c.firstName like '%" + searchValStr + "%' AND c.lastName like '%" + searchLastStr
							+ "%' )  ";
				} else if (!searchValStr.isEmpty() && searchLastStr.isEmpty()) {
					subQry += " AND  c.firstName like '%" + searchValStr + "%'";
				} else if (searchValStr.isEmpty() && !searchLastStr.isEmpty()) {
					subQry += " AND c.lastName like '%" + searchLastStr + "%'  ";
				}
			}
			// else if(selectFieldIDx == 5) {
			// subQry += " AND c.lastName like '%"+ searchValStr +"%'";
			// }

			if (!emailStatus.isEmpty()) {
				subQry += " AND c.emailStatus = '" + emailStatus + "'";
			}

			if (!mobStatus.isEmpty()) {
				subQry += " AND c.mobileStatus = '" + mobStatus + "'";
			}
			if(!membershipStatus.isEmpty())
			{
			Iterator<MailingList> mlIter = mlSet.iterator();
			String userIdStr = "";
			Set<String> userIdSet = new HashSet<String>();
			while (mlIter.hasNext()) {
				userIdSet.add("" + mlIter.next().getUsers().getUserId().longValue());
			}
			for (String str : userIdSet) {

				if (!userIdStr.isEmpty())
					userIdStr += ",";
				userIdStr += str;
			}

	
				subQry += " AND c.contactId in (SELECT cl.contact FROM ContactsLoyalty cl WHERE cl.userId in("
					+ userIdStr + ") and cl.membershipStatus='"+membershipStatus+"')";
			}
			
			
			

			if (queryStr != null) {
				queryStr = "SELECT DISTINCT  c " + queryStr + " " + subQry;
				// queryStr = queryStr+ " "+subQry;

			} else {
				Long userId = mlIt.next().getUsers().getUserId();
				queryStr = " FROM Contacts c WHERE c.users = " + userId + " AND bitwise_and(c.mlBits," + mlsbit
						+ " )>0  " + subQry;
			}

			return queryStr;

		} catch (Exception e) {
			logger.error("** Exception ", e);
			return null;
		}

	}

	private String findCountQueryfromMlSet(Set<MailingList> mlSet, int selectFieldIDx, String searchValStr,
			String searchLastStr, String emailStatus, String mobStatus, String customFieldName , String membershipStatus) {

		List<Contacts> contactsList = null;
		logger.debug("--just entered--");
		try {
			Iterator<MailingList> mlIt = mlSet.iterator();

			long mlsbit = Utility.getMlsBit(mlSet);
			String queryStr = Utility.getMultiUserQry(mlSet, mlsbit);
			// logger.info("queryStr under find List ::"+queryStr);

			String subQry = "";

			if (selectFieldIDx > 5) {
				subQry += " AND c." + customFieldName.toLowerCase() + " like '%" + searchValStr + "%'";
			} else if (selectFieldIDx == 1) {
				subQry += " AND c.emailId like '%" + searchValStr + "%'";
			} else if (selectFieldIDx == 2) {
				subQry += " AND c.mobilePhone like '%" + searchValStr + "%'";
			} else if (selectFieldIDx == 3) {
				Iterator<MailingList> mlIter = mlSet.iterator();
				String userIdStr = "";
				Set<String> userIdSet = new HashSet<String>();
				while (mlIter.hasNext()) {
					userIdSet.add("" + mlIter.next().getUsers().getUserId().longValue());
				}
				for (String str : userIdSet) {

					if (!userIdStr.isEmpty())
						userIdStr += ",";
					userIdStr += str;
				}

				subQry += " AND c.contactId in (SELECT cl.contact FROM ContactsLoyalty cl WHERE cl.userId in("
						+ userIdStr + ") AND cl.cardNumber=" + searchValStr + ")";

				// subQry += " AND c.firstName ='"+ searchValStr +"'";
			} else if (selectFieldIDx == 4 && searchValStr != null && searchLastStr !=null) {
				if (!searchValStr.isEmpty() && !searchLastStr.isEmpty()) {
					/*subQry += " AND  ( c.firstName like '%" + searchValStr + "%' OR c.lastName like '%" + searchLastStr
							+ "%' )  ";*/
					subQry += " AND  ( c.firstName like '%" + searchValStr + "%' AND c.lastName like '%" + searchLastStr
							+ "%' )  ";
				} else if (!searchValStr.isEmpty() && searchLastStr.isEmpty()) {
					subQry += " AND  c.firstName like '%" + searchValStr + "%'";
				} else if (searchValStr.isEmpty() && !searchLastStr.isEmpty()) {
					//subQry += " AND c.lastName like '%" + searchLastStr + "%' ) ";
					subQry += " AND c.lastName like '%" + searchLastStr + "%'" ;
				}
			}
			// else if(selectFieldIDx == 5) {
			// subQry += " AND c.lastName like '%"+ searchValStr +"%'";
			// }

			if (!emailStatus.isEmpty()) {
				subQry += " AND c.emailStatus = '" + emailStatus + "'";
			}

			if (!mobStatus.isEmpty()) {
				subQry += " AND c.mobileStatus = '" + mobStatus + "'";
			}
			if(!membershipStatus.isEmpty())
			{
			Iterator<MailingList> mlIter = mlSet.iterator();
			String userIdStr = "";
			Set<String> userIdSet = new HashSet<String>();
			while (mlIter.hasNext()) {
				userIdSet.add("" + mlIter.next().getUsers().getUserId().longValue());
			}
			for (String str : userIdSet) {

				if (!userIdStr.isEmpty())
					userIdStr += ",";
				userIdStr += str;
			}

	
				subQry += " AND c.contactId in (SELECT cl.contact FROM ContactsLoyalty cl WHERE cl.userId in("
					+ userIdStr + ") and cl.membershipStatus='"+membershipStatus+"')";
			}
			
			
			
	// logger.info("subQry ::"+subQry);

			if (queryStr != null) {
				queryStr = "SELECT COUNT(c.contactId)  " + queryStr + " " + subQry;
				// queryStr = queryStr+ " "+subQry;

			} else {
				Long userId = mlIt.next().getUsers().getUserId();
				queryStr = "SELECT COUNT(c.contactId) FROM Contacts c WHERE c.users = " + userId
						+ " AND bitwise_and(c.mlBits," + mlsbit + " )>0  " + subQry;
			}
			logger.info("final query Str queryStr ::" + queryStr);
			return queryStr;

		} catch (Exception e) {
			logger.error("** Exception ", e);
			return null;
		}
	}	    
	    
	    /**************************************************************/
	    public void onClick$exportBtnId() {
			try {
				setValues();
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

		//Email Status
				tempChk = (Checkbox)chkList.get(2);
				if(tempChk.getLabel().equalsIgnoreCase("Email Status") ) {

					if(tempChk.isChecked()){
						indexes[2]=0;
						checked=true;
						query += tempChk.getValue();
					}
					else{
						indexes[2]=-1;
					}
				}
				
		
		//Mobile Number

		tempChk = (Checkbox)chkList.get(3);
		if(tempChk.getLabel().equalsIgnoreCase("Mobile Number") ) {
				
			if(tempChk.isChecked()){
				indexes[3]=0;
				checked=true;
				query += tempChk.getValue();
			}
			else{
				indexes[3]=-1;
			}
		}
		
		//Mobile Status

				tempChk = (Checkbox)chkList.get(4);
				if(tempChk.getLabel().equalsIgnoreCase("Mobile Status") ) {
				
					if(tempChk.isChecked()){
						indexes[4]=0;
						checked=true;
						query += tempChk.getValue();
					}
					else{
						indexes[4]=-1;
					}
				}
				
				//card number
				tempChk = (Checkbox)chkList.get(5);
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
				tempChk = (Checkbox)chkList.get(6);
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

				//Loyalty Balances
				for(int i=0; i < 5; i++)
				{
				tempChk = (Checkbox)chkList.get(i+7);
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
		tempChk = (Checkbox)chkList.get(8);
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
			if(tempChk.getLabel().equalsIgnoreCase("Created Date") ||tempChk.getLabel().equalsIgnoreCase("Email")||tempChk.getLabel().equalsIgnoreCase("Email Status") || tempChk.getLabel().equalsIgnoreCase("Membership Number") ||
					tempChk.getLabel().equalsIgnoreCase("Card Pin") || tempChk.getLabel().equalsIgnoreCase("Mobile Number") ||tempChk.getLabel().equalsIgnoreCase("Mobile Status")||
					tempChk.getLabel().equalsIgnoreCase("Points") ||tempChk.getLabel().equalsIgnoreCase("Reward Balance") || tempChk.getLabel().equalsIgnoreCase("Gift Balance")
					|| tempChk.getLabel().equalsIgnoreCase("Hold Points")|| tempChk.getLabel().equalsIgnoreCase("Hold Currency"))  {
				continue;
			}

			if(tempChk.isChecked()) {
				indexes[i]=0;
				checked=true;
				//logger.info("appending :::: "+tempChk.getValue());
				if(query.indexOf((String)tempChk.getValue()) == -1 ) {
					query += tempChk.getValue();
				}
			}

		} // for
		
		query = query.substring(0, query.length()-2);

		if(checked) {

			int confirm=Messagebox.show("Do you want to export with selected fields ?", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
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


	}//on select




	public void exportCSV(String query, String ext, int[] indexes,int[] loyaltyIndexsOne, int[] loyaltyIndexsTwo) {

		long startTime = System.currentTimeMillis();
		searchFirstStr = first_nameTbId.getValue().trim();
		searchLastStr = last_nameTbId.getValue().trim();
		//Long orgid=mailingList.getUsers().getUserOrganization().getUserOrgId();
		long totalContacts = 0;
		ContactsJdbcResultsetHandler contactsJdbcResultsetHandler = null;
		try {
			if(viewType==null) {
			logger.debug("entered ");

			
			logger.info("entering into view type");

			 /*query += " from contacts c left join contacts_loyalty cl on c.cid = cl.contact_id where c.user_id = "+mailingList.getUsers().getUserId()+
					"  and c.mlbits & "+ mailingList.getMlBit() + " > 0";*/									
			/* query += " from (select * from contacts where user_id="+userId.longValue()+
						"  and mlbits & "+ mailingList.getMlBit() + 
						" > 0) as c LEFT JOIN (select cl.* from contacts_loyalty cl where user_id="+userId.longValue()+") as cl on c.cid = cl.contact_id ";*/
			 Long orgid=mailingList.getUsers().getUserOrganization().getUserOrgId();
			 query += " from (select * from contacts where user_id="+userId.longValue()+
						"  and mlbits & "+ mailingList.getMlBit() + 
						" > 0) as c LEFT JOIN (select cl.* from contacts_loyalty cl where user_id="+userId.longValue()+") as cl on c.cid = cl.contact_id "
						+ "LEFT JOIN (select org.home_store_id, org.subsidary_id, org.store_name from org_stores  org where org_id="+orgid.longValue()+") as org on"
						+ " (if(c.subsidiary_number IS NOT NULL and c.subsidiary_number != '', c.subsidiary_number= org.subsidary_id AND c.home_store=org.home_store_id , c.home_store=org.home_store_id))";
			
			logger.info("query for export is"+query);
			}
			else if(viewType.equalsIgnoreCase("contact")) {
	            
			logger.info("entering into contact view type");
			Long orgid=usersDao.findByUserId(userId).getUserOrganization().getUserOrgId();
			
				query +=" from (select * from contacts where user_id="+userId.longValue()+" > 0) as c LEFT JOIN (select cl.* from contacts_loyalty cl where user_id="+userId.longValue()+") as cl on c.cid = cl.contact_id "
						+ "LEFT JOIN (select org.home_store_id, org.subsidary_id, org.store_name from org_stores  org where org_id="+orgid.longValue()+") as org on"
						+ " (if(c.subsidiary_number IS NOT NULL and c.subsidiary_number != '', c.subsidiary_number= org.subsidary_id AND c.home_store=org.home_store_id , c.home_store=org.home_store_id))";

			logger.info("query for export is"+query);
			
			
			
			}
                        String tempQuery="";
			 tempQuery=query;
	             String si=searchLBId.getSelectedItem().getLabel();
			if(si.equalsIgnoreCase(searchLBId.getItemAtIndex(1).getLabel()))
			{
				query +=" and c.email_id like '%"+searchTBId.getText()+"%' ";
				
			}
			else if((si.equalsIgnoreCase(searchLBId.getItemAtIndex(2).getLabel())))
			{
				query +=" and c.mobile_phone like '%"+searchTBId.getText()+"%' ";
				
			}
			else if((si.equalsIgnoreCase(searchLBId.getItemAtIndex(3).getLabel())))
			{
				query +=" and cl.card_number = "+searchTBId.getText()+" ";
				
			}
			/*else if((si.equalsIgnoreCase(searchLBId.getItemAtIndex(4).getLabel())))
			{
				query +=" and c.first_name like '"+first_nameTbId.getText()+"' and c.last_name like '"+last_nameTbId.getText()+"' " ;
				
			}*/
			else if (si.equalsIgnoreCase(searchLBId.getItemAtIndex(4).getLabel())) {
				if (!searchFirstStr.isEmpty() && !searchLastStr.isEmpty()) {
					query += " AND  ( c.first_name like '%" + searchFirstStr + "%' AND c.last_name like '%" + searchLastStr
							+ "%' )  ";
				} else if (!searchFirstStr.isEmpty() && searchLastStr.isEmpty()) {
					query += " AND  c.first_name like '%" + searchFirstStr + "%'";
				} else if (searchFirstStr.isEmpty() && !searchLastStr.isEmpty()) {
					query += " AND c.last_name like '%" + searchLastStr + "%'  ";
				}
			}
			/*else if((si.equalsIgnoreCase(searchLBId.getItemAtIndex(5).getLabel()))) // APP-1201
			{
				query +=" and c.last_name like '"+searchTBId.getText()+"' ";
				
			}*/
					
					if(contactStatusLbId.getSelectedIndex()!=0)
					{
						query +=" and c.email_status like '"+ contactStatusLbId.getSelectedItem().getLabel()+"' ";
						
					}
					if(mobileStatusLbId.getSelectedIndex()!=0)
					{
						query +=" and c.mobile_status like '"+mobileStatusLbId.getSelectedItem().getLabel()+"' ";
					}
					if(membershipStatusLbId.getSelectedIndex()!=0)
					{
						query +=" and cl.membership_status like '"+membershipStatusLbId.getSelectedItem().getLabel()+"' ";
					}
					
					
					if(tempQuery.contains("email_id")) //Email is selected
						query += " order by email_id";
					//query += " order by email_id";
					logger.info("query is"+query);
			
			contactsJdbcResultsetHandler = new ContactsJdbcResultsetHandler();
			contactsJdbcResultsetHandler.executeStmt(query);
			logger.fatal("Time taken to execute query is ::::::::::::::::::::::: " + (System.currentTimeMillis()-startTime));
			totalContacts = contactsJdbcResultsetHandler.totalRecordsSize();
			
			if( totalContacts == 0) {
				MessageUtil.setMessage("No contacts exist in the selected search", "color:red", "TOP");
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
			
			StringBuffer sb = null;
			

			String udfFldsLabel= "";


			//Created Date
			if(indexes[0]==0) {

				udfFldsLabel = "\""+"Created Date"+"\"";
				 
			}

			//Email
			if(indexes[1]==0) {
				if(udfFldsLabel.length() > 0) udfFldsLabel += ",";
				udfFldsLabel += "\""+"Email"+"\"";
			}
			
			if(indexes[2]==0){
				if(udfFldsLabel.length() > 0) udfFldsLabel += ",";
				udfFldsLabel += "\""+"Email Status"+"\"";
			}
	

			//Mobile Number
			if(indexes[3]==0) {
				
				if(udfFldsLabel.length() > 0) udfFldsLabel += ",";
				udfFldsLabel += "\""+"Mobile Number"+"\"";
			}	
			
			//Mobile Status
			if(indexes[4]==0) {
				
				if(udfFldsLabel.length() > 0) udfFldsLabel += ",";
				udfFldsLabel += "\""+"Mobile Status"+"\"";
			}
			


			Users user = null;

			//user = usersDao.findMlUser(mailingList.getUsers().getUserId());

			 user=usersDao.findByUserId(currentUserId);
			if(user == null) {

				logger.debug("do not Export as user is null....");
				return;
			}

			List<POSMapping> posMappingsList = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_CONTACTS+"'", user.getUserId());

			Map<String, POSMapping> orderedMappingMap = getOrderedMappingSet(posMappingsList);

			// udfs logic here i think header here only

			int k=0;
			//Card Number , Card Pin
			for (String contactLoyaltyLabel : contactLoyaltyLabelsOne) {

				if(loyaltyIndexsOne[k]==0 && k<loyaltyIndexsOne.length) {
					if(udfFldsLabel.length() > 0) 
					{
						udfFldsLabel += ",";
					}
					udfFldsLabel += "\""+contactLoyaltyLabel+"\"";
					
					
				}
				k++;
			}//for

			
			k=0;
			//Points & Loyalty Balance
			for (String contactLoyaltyLabel : contactLoyaltyLabelsTwo) {

				if(loyaltyIndexsTwo[k]==0 && k<loyaltyIndexsTwo.length) {
					if(udfFldsLabel.length() > 0) udfFldsLabel += ",";
					udfFldsLabel += "\""+contactLoyaltyLabel+"\"";
					
				}
				k++;
			}//for
			int indexLength=(indexes.length);

			k=12;
			for (String custFldKey : orderedMappingMap.keySet()) {
			//	logger.info(custFldKey+"   custFldKey  ");

				if(custFldKey.equalsIgnoreCase("Mobile") || custFldKey.equalsIgnoreCase("Email"))
					continue;

				if(k==indexLength)
					break;
				if(k!=0 || k!=1 || k!=2 || k!=3 || k!=4) {
					if(indexes[k]==0 && k < indexLength) {
						
						if(udfFldsLabel.length() > 0) 
							udfFldsLabel += ",";
						udfFldsLabel += "\""+orderedMappingMap.get(custFldKey).getDisplayLabel().trim()+"\"";
						
													
					}
				}
				k++;
			}//for


			sb = new StringBuffer();
			sb.append(udfFldsLabel);
			sb.append("\r\n");
			
			OCCSVWriter csvWriter = null;
			BufferedWriter bw = null;
			File file = null;
			try {
				String filePath = "";
				if(viewType==null) {
					filePath = usersParentDirectory + "/" + userName + "/List/download/" + mailingList.getListName() + "_" + System.currentTimeMillis() + "." + ext;

				} else if(viewType.equalsIgnoreCase("contact")) {
					filePath = usersParentDirectory + "/" + userName + "/List/download/MasterList_" + System.currentTimeMillis() + "." + ext;
				}
				try {
					file = new File(filePath);
					bw = new BufferedWriter(new FileWriter(file));
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					logger.error("file not found ",e1);		
				}
				bw.write(sb.toString());

				/*CSVWriter csvWriter = new CSVWriter(bw);
				csvWriter.setTimestampFormat(MyCalendar.FORMAT_DATEONLY);
				csvWriter.setDateFormat(MyCalendar.FORMAT_DATEONLY);
				csvWriter.writeAll(contactsJdbcResultsetHandler.getResultSet(), false);
				*/
				
				try {
					csvWriter = new OCCSVWriter(bw);
					csvWriter.writeAll(contactsJdbcResultsetHandler.getResultSet(), false);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("exception while initiating writer ",e);
					
				}finally{
					bw.flush();
					csvWriter.flush();
					bw.close();
					csvWriter.close();
				}
				
				
				
				Filedownload.save(file, "text/csv");
			} catch (Exception e) {
				logger.error("exception while initiating writer ",e);
				
			}finally {
				
				sb = null;
				posMappingsList = null; orderedMappingMap = null;user = null; bw= null;csvWriter = null;file= null;   indexes = null;
			    loyaltyIndexsOne = null; loyaltyIndexsTwo = null; file= null;downloadDir = null; udfFldsLabel = null; query = null;ext = null; 
				
			}
			
			logger.debug("exited");
		} 
			catch (Exception e) {
			logger.error("** Exception : " , e);
		}finally{
			if(contactsJdbcResultsetHandler!=null ) contactsJdbcResultsetHandler.destroy();
			contactsJdbcResultsetHandler = null;
			//System.gc();
		}
		long endTime = System.currentTimeMillis();
		logger.fatal("Time taken to export listcontacts is :::   :: " + (endTime-startTime));
	}//end of exportCSV


	//Getting Header's
	public List<String[]> getHeaders() 
	{
try {
		Users user = null;

	// user = usersDao.findMlUser(mailingList.getUsers().getUserId());
        user=usersDao.findByUserId(currentUserId);

		if(user == null) 
		{

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
catch(Exception e)
{
	logger.debug("wrong");
	
}
return null;
		
	}
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
			
			//Email Status
			tempChk1 = new Checkbox("Email Status");
			tempChk1.setSclass("custCheck");
			tempChk1.setValue("c.email_status, ");
			tempChk1.setParent(custExport$chkDivId);
			
			tempChk1 = new Checkbox("Mobile Number");
			tempChk1.setSclass("custCheck");
			tempChk1.setValue("c.mobile_phone, ");
			tempChk1.setParent(custExport$chkDivId);
			
			//Mobile Status
			tempChk1 = new Checkbox("Mobile Status");
			tempChk1.setSclass("custCheck");
			tempChk1.setValue("c.mobile_status, ");
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
	}

	private boolean validateFullName() {
		if(("First Name".equalsIgnoreCase(first_nameTbId.getValue().trim())) && ("Last Name".equalsIgnoreCase(last_nameTbId.getValue().trim()))||
				(Constants.STRING_NILL.equals(first_nameTbId.getValue().trim()) && Constants.STRING_NILL.equals(last_nameTbId.getValue().trim())) ){
			MessageUtil.setMessage("Please enter name.", "red");
			fullNameSpanId.setVisible(true);
			return false;
		}

		return true;
		
	}
	
	

}

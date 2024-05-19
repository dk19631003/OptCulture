package org.mq.marketer.campaign.controller.report;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.CouponDiscountGeneration;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.ReferralcodesIssued;
import org.mq.marketer.campaign.beans.UserActivities;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.CouponCodesDao;
import org.mq.marketer.campaign.dao.CouponDiscountGenerateDao;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.ReferralcodesIssuedDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.POSFieldsEnum;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.data.dao.JdbcResultsetHandler;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.ReferralcodesRedeemedDao;
import org.mq.optculture.utils.OCCSVWriter;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

import com.google.zxing.Writer;


public class ReferralReportController extends GenericForwardComposer implements EventListener{

	private static Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private MyDatebox toDateboxId, fromDateboxId, storeFromDateboxId, storeToDateboxId;
	private Listbox srchLbId,statusLbId, pageSizeLbId,memberPerPageStoreLBId,exportCouponReportLbId,exportStoreRedmLbId;
	private CouponsDao couponsDao;
	private TimeZone clientTimeZone;
	private Rows referalRowsId,storeRedmRowsId;
	private Users users;
	private CouponDiscountGeneration couponDiscountGeneration;
	private CouponDiscountGenerateDao couponDiscountGenerateDao;
	//private RetailProSalesDao retailProsalesDao;
	private CouponCodesDao couponCodesDao;
	private String fromDate, endDate;
	private OrganizationStoresDao organizationStoresDao;
	private UserActivitiesDaoForDML userActivitiesDaoForDML = null;
	private List<Map<String, Object>> storeNumberNameMapList;
	private String storeFromDateStr,storeToDateStr;
	private Calendar fromDateCal, toDateCal ;
	
	 private Window custExport,custExportAsStore;
	 private Div custExport$chkDivId,custExportAsStore$chkStoreDivId;
	 private Combobox exportCbId;
	
	private final String SEARCH_BY_STATUS = "Status";
	private final String SEARCH_BY_DATE = "Date";
	private final String SEARCH_BY_PROMOTIONNAME = "PromotionName";
	
	//private Listbox statusLbId;
	private Div searchByPromoCodeNameDivId;
	private Div searchByLastModifiedDivId;
	private Div searchByStatusDivId;
	
	private Textbox searchByPromoCodeNameTbId;
	private  String  userCurrencySymbol = "$ "; 

	
	public ReferralReportController() {
		session = Sessions.getCurrent();
		String style = "font-weight:bold;font-size:15px;color:#313031;"
				+ "font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Referral Reports", "", style, true);
		users = GetUser.getUserObj();
		this.couponsDao = (CouponsDao) SpringUtil.getBean("couponsDao");
		this.couponDiscountGenerateDao = (CouponDiscountGenerateDao) SpringUtil
				.getBean("couponDiscountGenerateDao");
		couponCodesDao = (CouponCodesDao) SpringUtil.getBean("couponCodesDao");
		//retailProsalesDao = (RetailProSalesDao)SpringUtil.getBean("retailProSalesDao");
		clientTimeZone = (TimeZone) session.getAttribute("clientTimeZone");
		organizationStoresDao =	(OrganizationStoresDao)SpringUtil.getBean("organizationStoresDao");
		userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");

	}
	private int pageSize = 0;

	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		Long userId = GetUser.getUserObj().getUserId();
		if(userActivitiesDaoForDML!=null) {
		UserActivities userActivity = new UserActivities("Visited referral report page", "Visited pages", Calendar.getInstance(),userId );
		userActivitiesDaoForDML.saveOrUpdate(userActivity);
		}
		
		int tempCount = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		
		
		ReferralcodesIssuedDao refcodesissuedDao=null;
		try {
			refcodesissuedDao = (ReferralcodesIssuedDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRALCODES_DAO);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		long count=refcodesissuedDao.findReferralCodesByOrgId(users.getUserOrganization().getUserOrgId(), null, fromDate,endDate,orderby_colName,desc_Asc,null);
		
		ReferralRepListBottomPagingId.setTotalSize((int) count);
		ReferralRepListBottomPagingId.setPageSize(tempCount);
		ReferralRepListBottomPagingId.setActivePage(0);
		ReferralRepListBottomPagingId.addEventListener("onPaging", this);
		ReferralRepListBottomPagingId.setAttribute("onPaging", "coupon");
			
		getCouponReports(0, tempCount);
		
	} // doAfterCompose



	
	


	private Paging ReferralRepListBottomPagingId,storePagingId;
	

	
	public String orderby_colName="userCreatedDate",desc_Asc="desc"; 
	    
	    public void desc2ascasc2desc()
	    {
	    	if(desc_Asc=="desc")
				desc_Asc="asc";
			else
				desc_Asc="desc";
		
	    }
		 
		

	public void onSelect$pageSizeLbId() {
		
		try {
			/*String fromDate = MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
			String endDate = MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
			String status = statusLbId.getSelectedItem().getLabel();*/
			
			int tempCount = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
			ReferralRepListBottomPagingId.setPageSize(tempCount);
			ReferralRepListBottomPagingId.setActivePage(0);
		
			getCouponReports(ReferralRepListBottomPagingId.getActivePage()*ReferralRepListBottomPagingId.getPageSize(),tempCount);
			
			
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
		
	
		
	}//onSelect$pageSizeLbId() 

	
	
	int totalSize = 0;
	

	
	
	//Changes 2.5.2.19 
	public void getCouponReports(int startIndex, int count) {
		
		ReferralcodesIssuedDao refcodesissuedDao=null;
		try {
			refcodesissuedDao = (ReferralcodesIssuedDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRALCODES_DAO);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		List<ReferralcodesIssued> ReferralList = refcodesissuedDao.findReferralCodesByOrgId(users.getUserOrganization().getUserOrgId(),startIndex,count, null, fromDate,endDate,orderby_colName,desc_Asc,null);
		// write method in referralcodesisueddao 
		//call in Referralreportscontroller
		
		//int count = couponsDao.findTotCountCouponsofOrg(users.getUserOrganization().getUserOrgId(),fromDate,endDate);	
		if(ReferralList!= null ) {
		 totalSize  = ReferralList.size();
		}
//				couponsDao.findCoupBasedOnDates(currentUser.getUserId(),statusStr,fromDate, endDate);
				
		logger.debug(">>>>>>>>totalSize ::"+totalSize);
		
	renderingPromoList(ReferralList);

	}//getCouponReports()
	
	
	private void renderingPromoList(List<ReferralcodesIssued> referralList){
		Components.removeAllChildren(referalRowsId);
		if(referralList == null) {
			//TODO
			logger.debug(" No Referral codes  exists ");
			return;
		}
		Label tempLabel = null;
		
	
		
		
		
		//List<CouponDiscountGeneration> dis
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		for (ReferralcodesIssued referralIssuedObj : referralList) {
			String lastModifiedDate=null;

			//findContactByValues
			
			Row row = new Row();
			row.setParent(referalRowsId);
			Label empty=new Label("--");
			//contact detailse
			Contacts contact=getContact(referralIssuedObj);
			
			if(contact!=null && contact.getEmailId()!=null) {
				tempLabel=new Label(contact.getEmailId());
			}
			else 
				{
				if(referralIssuedObj.getCampaignType().toString().trim().equalsIgnoreCase("email")) {
					tempLabel=new Label(referralIssuedObj.getSentTo().toString());
				}
				 else {tempLabel=empty;}
				}
				
				//Contacts contact=contactsDao.findContactByValues(email, null, null,referralIssuedObj.getUserId());
//				tempLabel= new Label(contact.getEmailId());
				tempLabel.setStyle("cursor:pointer;color:blue;text-decoration: underline;");
				tempLabel.addEventListener("onClick", this);
				//email
				row.appendChild(tempLabel);

				//mobile
//				logger.info("mobilr phone"+contact.getMobilePhone());
				if(contact!=null && contact.getMobilePhone()!=null) {
					
					row.appendChild(new Label(contact.getMobilePhone()));	
				}
				else {
					if(referralIssuedObj.getCampaignType().toString().trim().equalsIgnoreCase("sms")||referralIssuedObj.getCampaignType().toString().trim().equalsIgnoreCase("Webapp") ) {
						row.appendChild(new Label(referralIssuedObj.getSentTo().toString()));
					}
					else {row.appendChild(new Label("--")); }
					}
//			logger.
			//Refcode
			logger.debug("coupon code is"+referralIssuedObj.getRefcode());
			row.appendChild(new Label(referralIssuedObj.getRefcode()));
			//Used by
			Long usedBy=getRedeemedCountUsed(referralIssuedObj.getRefcode());
			logger.info(usedBy);
		  row.appendChild(new Label(usedBy.toString()));
		  
			Double referralReward = 0.0;
		  	if( contact!= null && contact.getContactId()!=null) {
		  	
		  	referralReward=getReferralRewardEarned(contact.getUsers().getUserId(),contact.getContactId());
			logger.info("referralReward value is"+referralReward);
			}
		  
			row.appendChild(new Label(referralReward.toString()));

			row.setValue(referralIssuedObj);
			
			row.setParent(referalRowsId);

		} // for
	} //renderingPromoList
	
	
	private  Double getReferralRewardEarned(Long userId,Long contactId){

		ContactsLoyaltyDao contactsLoyaltyDao = null;
		LoyaltyTransactionChildDao loyaltyTransactionDao = null;
		try {
		contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName("contactsLoyaltyDao");
		loyaltyTransactionDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
		}catch(Exception e) {
			logger.error("Exception in loading Daos",e);
		}
		ContactsLoyalty contactsLoyalty= contactsLoyaltyDao.findByContactId(userId,contactId);
		logger.info("contact loyaltyObj in getReferralRewardEarned:"+contactsLoyalty);
		if(contactsLoyalty==null) return 0.0;

		List<Object[]> totalRewards=loyaltyTransactionDao.findTotalRewardsEarnByMembershipNumber(contactsLoyalty.getCardNumber(),userId,"Reward");
		logger.info("totalRewards :"+totalRewards);
		if(totalRewards==null || totalRewards.size()==0) {
			return 0.0;
		}
		else {
			Double amount = 0.0;
			amount = (Double) totalRewards.get(0)[0];
			Double points = 0.0;
			points = (Double) totalRewards.get(0)[1];
			if(points!=null && points>0.0) {
				return points;
			}
			else if(amount!=null && amount>0){
					return amount;
				}
			return 0.0;
		}
	}

	private Contacts getContact(ReferralcodesIssued referralIssuedObj) {
		ContactsDao contactsDao=null;
		 //ReferralcodesRedeemedDao referralcodesRedeemedDao=null;
		try {
			contactsDao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
			//referralcodesRedeemedDao= ( ReferralcodesRedeemedDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRAL_CODES_REDEEMED_DAO);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// TODO Auto-generated method stub
		Contacts contact=null;
		if(referralIssuedObj.getCampaignType()!=null) {
		
		if(referralIssuedObj.getCampaignType().trim().equalsIgnoreCase("email")) {
			logger.info("campaign type is Email ");
			String email=referralIssuedObj.getSentTo();
			
			 contact=contactsDao.findContactByValues(email, null, null,referralIssuedObj.getUserId());
		}
		else {
			
			String mobilePhn=referralIssuedObj.getSentTo();
			
			Long userId=users.getUserId();
			String countryCarrier =users.getCountryCarrier()!=null?users.getCountryCarrier()+"":"" ;//APP-2208
			
			int i=countryCarrier.length();
			
			logger.info("phone "+mobilePhn);

			if(mobilePhn.startsWith(countryCarrier)) {

				logger.info("entering phone length" + i);

				logger.info("entering phone Z+");
				mobilePhn = mobilePhn.substring(i);
			}
			contact=contactsDao.findContactByValues(null,mobilePhn, null,referralIssuedObj.getUserId());
			
		}
		} 
		return contact;
	}//get Contact details




	public void setDateValues() {

		Calendar cal = MyCalendar.getNewCalendar();
		toDateboxId.setValue(cal);
		logger.debug("ToDate (server) :" + cal);
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
		logger.debug("FromDate (server) :" + cal);
		fromDateboxId.setValue(cal);

	}//setDateValues()

	
	
	public void onChange$fromDateboxId (){
		if(fromDateboxId.getValue() ==null || toDateboxId.getValue()== null) return;
		Calendar  start = fromDateboxId.getClientValue();
		Calendar end = toDateboxId.getClientValue();
		
		if(end.before(start)) {
			MessageUtil.setMessage("'From' date cannot be later than 'To' date.", "red");
			return;
		}
	}
	
	public void onChange$toDateboxId(){
		Calendar  start = null,end = null;
		try {
		  start = fromDateboxId.getClientValue();
		  end = toDateboxId.getClientValue();
		}catch (Exception e) {
		  end = toDateboxId.getClientValue();
		}
		if(end.before(start)) {
			MessageUtil.setMessage("'From' date cannot be later than 'To' date.", "red");
			return;
		}
	}
	
	
	//call to redemed reports
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		super.onEvent(event);
		
		 if(event.getTarget() instanceof Label ) {
		
		Label tempLable = (Label)event.getTarget();
		Row tempRow = (Row)tempLable.getParent();
		
		if(tempRow.getValue()!=null){
		
			ReferralcodesIssued refCodeObj = (ReferralcodesIssued)tempRow.getValue();
		
		logger.debug("couponObj is  :: "+refCodeObj);
		if(refCodeObj != null){
			
			session.setAttribute("refCodeObject", refCodeObj);
			Contacts contact=getContact(refCodeObj);
			session.setAttribute("contact", contact);
		}
		}else{
			String store = ""+tempLable.getAttribute("original value");
			session.removeAttribute("refCodeObject");
			//session.setAttribute("STORE_REDEEMED_DETAILS", store);
		}
		Long userId = GetUser.getUserObj().getUserId();
		if(userActivitiesDaoForDML!=null) {
		UserActivities userActivity = new UserActivities("Visited detailed referral report page", "Visited pages", Calendar.getInstance(),userId );
		userActivitiesDaoForDML.saveOrUpdate(userActivity);
		}
		Redirect.goTo(PageListEnum.REPORT_REDEEMED);
		
	} else if(event.getTarget() instanceof Paging) {
		/*String fromDate = MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
		String endDate = MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
		String statusStr = statusLbId.getSelectedItem().getLabel();*/
		Paging paging = (Paging) event.getTarget();
		int desiredPage = paging.getActivePage();
		PagingEvent pagingEvent = (PagingEvent) event;
		int pSize = pagingEvent.getPageable().getPageSize();
		int ofs = desiredPage * pSize;
		
		if(paging.getAttribute("onPaging").equals("coupon")) {
			this.ReferralRepListBottomPagingId.setActivePage(desiredPage);
			getCouponReports(ofs, (byte) pagingEvent.getPageable().getPageSize());
			
		}
	}
	
	}

		 public void onClick$exportBtnReferralReportId() {
				createWindow();
				custExport.setVisible(true);
				custExport.doHighlighted();
			}

		 
		 
			 public void onClick$selectAllAnchr$custExport() {
				 anchorEvent(true);
			 }

			 public void onClick$clearAllAnchr$custExport() {
				anchorEvent(false);
			 }
			 public void onClick$selectAllAnchr$custExportAsStore() {
				 anchorEvent(true);
			 }

			 public void onClick$clearAllAnchr$custExportAsStore() {
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
		
		 			 public void createWindow()	{

					try {
						Components.removeAllChildren(custExport$chkDivId);

						Checkbox tempChk2 = new Checkbox("Contact Email");
						tempChk2.setSclass("custCheck");
						tempChk2.setParent(custExport$chkDivId);
						tempChk2.setWidth("200px");
						tempChk2.setChecked(true);
	              
					    tempChk2 = new Checkbox("Contact Mobile");
						tempChk2.setSclass("custCheck");
						tempChk2.setParent(custExport$chkDivId);
						tempChk2.setWidth("200px");
						tempChk2.setChecked(true);
						
					    tempChk2 = new Checkbox("Referrel Code");
						tempChk2.setSclass("custCheck");
						tempChk2.setParent(custExport$chkDivId);
						tempChk2.setWidth("200px");
						tempChk2.setChecked(true);


						tempChk2 = new Checkbox("Used By");
						tempChk2.setSclass("custCheck");
						tempChk2.setParent(custExport$chkDivId);
						tempChk2.setWidth("200px");
						tempChk2.setChecked(true);


					} catch (Exception e) {
						logger.error("Exception ::", e);
					}
				}
			 //exporting function code
			 public void onClick$selectFieldBtnId$custExport() {

				 custExport.setVisible(false);
				 List<Component> chkList = custExport$chkDivId.getChildren();

				 int indexes[]=new int[chkList.size()];
				 
				 boolean checked=false;

				 for(int i=0;i<chkList.size();i++) {
					 indexes[i]=-1;
				 } // for

				 Checkbox tempChk = null;

				 for (int i = 0; i < chkList.size(); i++) {
					 if(!(chkList.get(i) instanceof Checkbox)) continue;

					 tempChk = (Checkbox)chkList.get(i);

					 if(tempChk.isChecked()) {
						 indexes[i]=0;
						 checked=true;
					 }else{
							indexes[i]=-1;
						}

				 } // for


				 if( ((Checkbox)custExport$chkDivId.getLastChild()).isChecked()) {

					 checked=true;
				 }

				 if(checked) {

					 int confirm=Messagebox.show("Do you want to export with selected fields ?", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					 if(confirm==1){
						 try{
							 String fileFormate = exportCouponReportLbId.getSelectedItem().getValue().toString();
								/*if (fileFormate.contains("xls")) {
									exportExcel(fileFormate, indexes);
								} else if (fileFormate.contains("csv")) {
									exportCSV(fileFormate, indexes);
								}*/
							 
							 switch(fileFormate)
							  {
								  case "xls": exportExcel(fileFormate, indexes);break;
								  case "csv": exportCSV(fileFormate, indexes);break;
//								  case "csvzip": exportCsvZIP(indexes);break;
//								  case "xlszip": exportExcelZip(indexes);break;
							  }

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
			 
			  
			    

			 
			 
			 private void exportCSV(String type, int[] indexes) {
				 	logger.debug("-- just entered into exportCSV --");
					StringBuffer sb = null;
					String userName = GetUser.getUserName();
					String usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
					String exportDir = usersParentDirectory + "/" + userName + "/Export/" ;
					File downloadDir = new File(exportDir);
					if(downloadDir.exists()){
						try {
							FileUtils.deleteDirectory(downloadDir);
							logger.debug(downloadDir.getName() + " is deleted");
						} catch (Exception e) {
							logger.error("Exception ::" , e);
							
							logger.debug(downloadDir.getName() + " is not deleted");
						}
					}
					if(!downloadDir.exists()){
						downloadDir.mkdirs();
					}
					
						
						String filePath = exportDir +  "Referrel Reports_" + System.currentTimeMillis() + "." +type;
							MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_YEARTOSEC, clientTimeZone);
							try {
				
									logger.debug("Download File path : " + filePath);
									File file = new File(filePath);
									BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
//									String status = statusLbId.getSelectedItem().getLabel();
//									String promotionName = searchByPromoCodeNameTbId.getValue().trim();
									ReferralcodesIssuedDao refcodesissuedDao=null;
									try {
										refcodesissuedDao = (ReferralcodesIssuedDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRALCODES_DAO);
									} catch (Exception e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									long count=refcodesissuedDao.findReferralCodesByOrgId(users.getUserOrganization().getUserOrgId(), null, fromDate,endDate,orderby_colName,desc_Asc,null);
									if(count == 0) {
										Messagebox.show("No referral code report found.","Info", Messagebox.OK,Messagebox.INFORMATION);
										return;
									}
									 String udfFldsLabel= "";
							
									 if(indexes[0]==0) {
										 udfFldsLabel = "\""+"Contact Email"+"\""+",";
									 }
									 if(indexes[1]==0) {
										 udfFldsLabel += "\""+"Contact Mobile"+"\""+",";
									 }
									 if(indexes[2]==0) {
										 udfFldsLabel += "\""+"Referral Code"+"\""+",";
									 }
									 if(indexes[3]==0) {
										 udfFldsLabel += "\""+"Used By"+"\""+",";
									 }
//									 if(indexes[4]==0) {
//										 udfFldsLabel += "\""+"Discount"+"\""+",";
//									 }	
//									
									 sb = new StringBuffer();
									 sb.append(udfFldsLabel);
									 sb.append("\r\n");

									 bw.write(sb.toString());
									 //System.gc();
									
									int size = (int)count;
									List<ReferralcodesIssued> ReferralList=null;
									for (int i = 0; i < count; i+=size) {
										sb = new StringBuffer();
										//pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
							ReferralList = refcodesissuedDao.findReferralCodesByOrgId(users.getUserOrganization().getUserOrgId(),0,(int)count, null, fromDate,endDate,orderby_colName,desc_Asc,null);
                           if(ReferralList!=null && ReferralList.size()>0)
											for (ReferralcodesIssued referralIssuedObj: ReferralList) {
												
												Contacts contact=getContact(referralIssuedObj);
									DecimalFormat decimalFormat = new DecimalFormat("#0.00");
												if(indexes[0]==0) {
													sb.append("\"");
//													sb.append((contact!=null && contact.getEmailId()!=null)?contact.getEmailId().trim():"--"); 
													if(contact!=null && contact.getEmailId()!=null) {
														sb.append(contact.getEmailId());
													}
													else 
														{
														if(referralIssuedObj.getCampaignType().toString().trim().equalsIgnoreCase("email")) {
															sb.append(referralIssuedObj.getSentTo().toString());
														}
														 else {sb.append("--");}
														}
													sb.append("\",");
												}
												if(indexes[1]==0) {
													sb.append("\"");
//													sb.append((contact!=null && contact.getMobilePhone()!=null)?contact.getMobilePhone():"--"); 
													if(contact!=null && contact.getMobilePhone()!=null) {
														
														sb.append(contact.getMobilePhone());	
													}
													else {
														if(referralIssuedObj.getCampaignType().toString().trim().equalsIgnoreCase("sms")) {
															sb.append(referralIssuedObj.getSentTo().toString());
														}
														else {sb.append("--"); }
														}
													sb.append("\",");
												}
												if(indexes[2]==0) {
													sb.append("\"");sb.append(referralIssuedObj.getRefcode()); sb.append("\",");
												}
												if(indexes[3]==0) {
													long usedBy=getRedeemedCountUsed(referralIssuedObj.getRefcode());
													sb.append("\"");sb.append(usedBy); sb.append("\",");
												}
//												if(indexes[4]==0) {
//													sb.append("\"");sb.append(discount); sb.append("\",");
//												}
//												

												sb.append("\r\n");
											}
									
							
										bw.write(sb.toString());
										ReferralList = null;
										sb = null;
										//System.gc();
									}
									bw.flush();
									bw.close();
									Filedownload.save(file, "text/plain");
								} catch (IOException e) {
									logger.error("Exception ::",e);
									
								}
								logger.debug("-- exit --");
				
				}
			 
			 
			 
			 
			 
			 private long getRedeemedCountUsed(String refcode) {
				// TODO Auto-generated method stub
				 ReferralcodesRedeemedDao referralcodesRedeemedDao=null;
					try {
					
						referralcodesRedeemedDao= ( ReferralcodesRedeemedDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRAL_CODES_REDEEMED_DAO);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					long count=0;
					count=referralcodesRedeemedDao.findRedeemdCountByCode(refcode);
				return count;
			}


			public void exportExcel(String type, int[] indexes) {
					try {
						String userName = GetUser.getUserName();
						String usersParentDirectory = (String) PropertyUtil.getPropertyValue("usersParentDirectory");

						File downloadDir = new File(usersParentDirectory + "/" + userName + "/List/download/");

						if (!downloadDir.exists()) {
							downloadDir.mkdirs();
						}

						String filePath = usersParentDirectory + "/" + userName + "/List/download/Referral_Reports_"+ System.currentTimeMillis() + "." + type;
						File file = new File(filePath);
						logger.debug("Writing to the file : " + filePath);
						FileOutputStream fileOut = new FileOutputStream(filePath);
						HSSFWorkbook hwb = new HSSFWorkbook();
						HSSFSheet sheet = hwb.createSheet("Promotion Report");
						HSSFRow row = sheet.createRow((short) 0);
						HSSFCell cell = null;
//						String status = statusLbId.getSelectedItem().getLabel();
//						String promotionName = searchByPromoCodeNameTbId.getValue().trim();
						ReferralcodesIssuedDao refcodesissuedDao=null;
						try {
							refcodesissuedDao = (ReferralcodesIssuedDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRALCODES_DAO);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						long count=refcodesissuedDao.findReferralCodesByOrgId(users.getUserOrganization().getUserOrgId(), null, fromDate,endDate,orderby_colName,desc_Asc,null);
						
						if (count == 0) {
							Messagebox.show("No referral report found.", "Info", Messagebox.OK, Messagebox.INFORMATION);
							return;
					      }
						
						row = sheet.createRow(0);
						int cellNo = 0;
						if (indexes[0] == 0) {
							
							cell = row.createCell((cellNo++));
							cell.setCellValue("Contact Email");
						}
						if (indexes[1] == 0) {
							
							cell = row.createCell(cellNo++);
							cell.setCellValue("Contact Mobile");
						}
						if (indexes[2] == 0) {
							
							cell = row.createCell(cellNo++);
							cell.setCellValue("Referral Code");
						}
						if (indexes[3] == 0) {
							
							cell = row.createCell(cellNo++);
							cell.setCellValue("Used By");
						}
//						if (indexes[4] == 0) {
//							
//							cell = row.createCell(cellNo++);
//							cell.setCellValue("Discount");
//							
//						}

						List<ReferralcodesIssued> ReferralList = null;
						int size = (int)count;
						for (int i = 0; i < count; i += size) {
							ReferralList = refcodesissuedDao.findReferralCodesByOrgId(users.getUserOrganization().getUserOrgId(),0,(int)count, null, fromDate,endDate,orderby_colName,desc_Asc,null);
	                           if(ReferralList!=null && ReferralList.size()>0) {
	                        		int rowId = 1;
	                           
								for (ReferralcodesIssued referralIssuedObj: ReferralList) {
                        				Contacts contact=getContact(referralIssuedObj);
									DecimalFormat decimalFormat = new DecimalFormat("#0.00");
									row = sheet.createRow(rowId++);
									int columnId = 0;
									cell = null;
									if (indexes[0] == 0) {
										cell = row.createCell(columnId++);
										if(contact!=null && contact.getEmailId()!=null) {
											cell.setCellValue(contact.getEmailId());
										}
										else 
											{
											if(referralIssuedObj.getCampaignType().toString().trim().equalsIgnoreCase("email")) {
												cell.setCellValue(referralIssuedObj.getSentTo().toString());
											}
											 else {cell.setCellValue("--");}
											}
										//cell.setCellValue((contact!=null && contact.getEmailId()!=null)?contact.getEmailId().trim():"--");
									}
									if (indexes[1] == 0) {
										cell = row.createCell(columnId++);
										if(contact!=null && contact.getMobilePhone()!=null) {
											
											cell.setCellValue(contact.getMobilePhone());	
										}
										else {
											if(referralIssuedObj.getCampaignType().toString().trim().equalsIgnoreCase("sms")) {
												cell.setCellValue(referralIssuedObj.getSentTo().toString());
											}
											else {cell.setCellValue("--"); }
											}
										//cell.setCellValue((contact!=null && contact.getMobilePhone()!=null)?contact.getMobilePhone():"--");
									}
									if (indexes[2] == 0) {
										cell = row.createCell(columnId++);
										cell.setCellValue(referralIssuedObj.getRefcode());
									}
									if (indexes[3] == 0) {
										cell = row.createCell(columnId++);
										long usedBy=getRedeemedCountUsed(referralIssuedObj.getRefcode());
										cell.setCellValue(usedBy);
									}
//									if (indexes[4] == 0) {
//										cell = row.createCell(columnId++);
//										cell.setCellValue(discount);
//									}
									
									
								  }
						     	}	
						     }
							
						     hwb.write(fileOut);
						    fileOut.flush();
							fileOut.close();
					
						Filedownload.save(file, "application/vnd.ms-excel");
						logger.debug("exited");

					} catch (Exception e) {
						logger.error("** Exception : ", e);
					}

				}
			 
			 
			 

}


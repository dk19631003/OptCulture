
package org.mq.marketer.campaign.controller.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.CouponCodes;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.ReferralcodesIssued;
import org.mq.marketer.campaign.beans.ReferralcodesRedeemed;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.RetailProSalesCSV;
import org.mq.marketer.campaign.beans.SpecialReward;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.CampaignSentDao;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.CouponCodesDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.SMSCampaignSentDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.RetailProSalesDao;
import org.mq.marketer.campaign.dao.SMSCampaignSentDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.POSFieldsEnum;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.optculture.data.dao.JdbcResultsetHandler;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.ReferralcodesRedeemedDao;
import org.mq.optculture.utils.OCCSVWriter;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.event.PagingEvent;

public class ReferralRedeemedController  extends GenericForwardComposer {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
//	private Users currentUser;
//	private CouponDiscountGenerateDao couponDiscountGenerateDao;
//	private CouponsDao couponsDao;
//	private TimeZone clientTimeZone;
//	private Grid couponsGridId;
	private Rows RedeemedRowsId,promotionRedmDetailRowsId,storeRedmRowsId;
	private Div couponsReportDivId,storeReportDivId;
//	private Coupons coupons;
//	private CouponDiscountGeneration couponDiscountGeneration;
	private Paging RedeemedRepListBottomPagingId,promoDetailPagingId,storePagingId;
	private MyDatebox storeFromDateboxId, storeToDateboxId;
	private Listbox pageSizeLbId,memberPerPagePromoDetailLBId,exportFilterPromoDetailLbId,memberPerPageStoreLBId;
	private Label couponLblId, validityLblId, discountLblId,storeID,promoID;
	Coupons coupObj;
	ReferralcodesIssued refObj;
	Contacts contact;
	CouponCodesDao couponCodesDao;
	CampaignSentDao campaignSentDao;
	SMSCampaignSentDao SMSCampaignSentDao;
	private RetailProSalesDao retailProsalesDao;
	private List<Map<String, Object>> storeNumberNameMapList;
	private TimeZone clientTimeZone;
	private OrganizationStoresDao organizationStoresDao;
	private String store;
	private String storeFromDateStr,storeToDateStr;
	private Users users;
	private Listbox srchLbId;
	private Div RedeemedDateId,IssuedDateId,StoreNameId,statusId;
	private Listbox searchByStoreNameId,filterByStatusId;
//	private  String emailOrPhone ;
	private MyDatebox fromRedeemedDateboxId,toRedeemedDateboxId;
	private MyDatebox fromIssuedDateboxId,toIssuedDateboxId;
	String userIds;
	public ReferralRedeemedController() {
		session = Sessions.getCurrent();
//		currentUser = GetUser.getUserObj();
		
		String style = "font-weight:bold;font-size:15px;color:#313031;"
				+ "font-family:Arial,Helvetica,sans-serif;align:left";
		users = GetUser.getUserObj();
		PageUtil.setHeader("Redeemed Reports", "", style, true);
		SMSCampaignSentDao = (SMSCampaignSentDao)SpringUtil.getBean("SMSCampaignSentDao");
		campaignSentDao = (CampaignSentDao)SpringUtil.getBean("campaignSentDao");
		couponCodesDao = (CouponCodesDao) SpringUtil.getBean("couponCodesDao");
		retailProsalesDao= (RetailProSalesDao)SpringUtil.getBean("retailProSalesDao");
		organizationStoresDao =	(OrganizationStoresDao)SpringUtil.getBean("organizationStoresDao");


	}

	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
//		emailOrPhone = currentUser.getEmailId() != null ? currentUser.getEmailId() : currentUser.getPhone() != null ?currentUser.getPhone() :"";
		int tempCount = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		logger.info("count of items per page"+tempCount);
		
		if(session == null ){
			logger.error("Session Object is Null so redirecting.....");
			Redirect.goToPreviousPage();
		}
	
		
		refObj = (ReferralcodesIssued) session.getAttribute("refCodeObject");
		contact=(Contacts) session.getAttribute("contact");
	
		
		if (refObj != null) {
			logger.info(" getting coupon Obj from Session ::"+refObj);
			 ReferralcodesRedeemedDao referralcodesRedeemedDao=null;
//			 ContactsLoyaltyDao contactsLoyaltyDao=null;
//			 ContactsDao contactsDao=null;
				try {
				
					referralcodesRedeemedDao= ( ReferralcodesRedeemedDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRAL_CODES_REDEEMED_DAO);
//					contactsLoyaltyDao= (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
//					contactsDao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
				} 
				catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				long count=referralcodesRedeemedDao.findRedeemdCountByCode(refObj.getRefcode());
						RedeemedRepListBottomPagingId.setTotalSize( (int) count);
						RedeemedRepListBottomPagingId.setPageSize(tempCount);
						RedeemedRepListBottomPagingId.setActivePage(0);
						RedeemedRepListBottomPagingId.addEventListener("onPaging", this);
						RedeemedRepListBottomPagingId.setAttribute("onPaging", "coupon");
						 
			getRedeemedReport(0, tempCount ,refObj,contact);
			}
	} // doAfterCompose
	
	
	public void getRedeemedReport(int startIdx, int endIdx ,ReferralcodesIssued refObj,Contacts contact) {
		Components.removeAllChildren(RedeemedRowsId);
	try {
		 //logger.info("contact"+contact.toString());		
		 ReferralcodesRedeemedDao referralcodesRedeemedDao=null;
		 ContactsLoyaltyDao contactsLoyaltyDao=null;
		 ContactsDao contactsDao=null;
			try {
			
				referralcodesRedeemedDao= ( ReferralcodesRedeemedDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRAL_CODES_REDEEMED_DAO);
				contactsLoyaltyDao= (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
				contactsDao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
			} 
			catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	
		
		 List<ReferralcodesRedeemed> redeemedMembersList=referralcodesRedeemedDao.findRedeemedMemberListByCode(refObj.getRefcode(),startIdx,endIdx);
		 
	
		
		// TODO Auto-generated method stub
		    //   Long tableSize=redeemedMembersList.size();
		 
		              for(ReferralcodesRedeemed referralcodesRedeemed:redeemedMembersList) {
					Row row = new Row();
					row.setParent(RedeemedRowsId);
					Label tempLabel= null;
					// //email
					if(contact!=null && contact.getEmailId()!=null) {
						tempLabel=new Label(contact.getEmailId());
					}
					else 
						{
						if(refObj.getCampaignType().toString().trim().equalsIgnoreCase("email")) {
							tempLabel=new Label(refObj.getSentTo().toString());
						}
						 else {tempLabel=new Label("--");}
						}
					row.appendChild(tempLabel);
					
					//mobile
					if(contact!=null && contact.getMobilePhone()!=null) {
						
						row.appendChild(new Label(contact.getMobilePhone()));	
					}
					else {
						if(refObj.getCampaignType().toString().trim().equalsIgnoreCase("sms")) {
							row.appendChild(new Label(refObj.getSentTo().toString()));
						}
						else {row.appendChild(new Label("--")); }
						}
					
					//code
					row.appendChild(new Label(refObj.getRefcode()));
					
					Contacts redeemedcontact=contactsDao.findById(referralcodesRedeemed.getRefereecid());
					//used by mobile
					logger.info("redeemed contact "+redeemedcontact);
					if(redeemedcontact!=null) {
					row.appendChild(new Label(redeemedcontact.getMobilePhone()!=null?redeemedcontact.getMobilePhone():"--"));
					
					//used by email
					row.appendChild(new Label(redeemedcontact.getEmailId()!=null?redeemedcontact.getEmailId():"--"));
					}
					else {
						row.appendChild(new Label("--"));
						row.appendChild(new Label("--"));
					}
					
					ContactsLoyalty contactsLoyalty= contactsLoyaltyDao.findByContactId(redeemedcontact.getUsers().getUserId(),redeemedcontact.getContactId());
					
					//membership id
					logger.info("contact loyalty obj value"+contactsLoyalty+"userId"+refObj.getUserId()+" contact Id"+refObj.getReferredCId());
					if(contactsLoyalty!=null && contactsLoyalty.getCardNumber()!=null) {
					row.appendChild(new Label(contactsLoyalty.getCardNumber()));
					}
					else {
						row.appendChild(new Label("--"));
					}
					logger.info("passed the card number ");
				    //total reevenue
					
					if(referralcodesRedeemed.getTotRevenue()!=null) {
							row.appendChild(new Label(referralcodesRedeemed.getTotRevenue().toString()));
					}
					else {
						row.appendChild(new Label("--"));
					}
					row.setValue(refObj);
//                 logger.info("value code"+refObj.getRefcode());
				
		              }
		              
     }
	catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}

	} // getCoupons
	
		
	public void onSelect$pageSizeLbId() {
		int tempCount = Integer.parseInt(pageSizeLbId.getSelectedItem()
				.getLabel());
		RedeemedRepListBottomPagingId.setPageSize(tempCount);
		RedeemedRepListBottomPagingId.setActivePage(0);
		refObj = (ReferralcodesIssued) session.getAttribute("refCodeObject");
		contact=(Contacts) session.getAttribute("contact");
	getRedeemedReport(RedeemedRepListBottomPagingId.getActivePage()*RedeemedRepListBottomPagingId.getPageSize(), tempCount,refObj,contact);

	} // onSelect$pageSizeLbId
	

	public void onClick$backBtnId(){
		
//		session.removeAttribute("COUP_REDEEMED_DETAILS");
//		Redirect.goToPreviousPage();
		Redirect.goTo(PageListEnum.REPORT_REFERRAL_REPORTS);
		
		
	}
	
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		super.onEvent(event);

		//onSelect$pageSizeLbId();

		if (event.getTarget() instanceof Paging) {
			Paging paging = (Paging) event.getTarget();
			
			int desiredPage = paging.getActivePage();
			
			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
			if(paging.getAttribute("onPaging").equals("coupon")){
//		
			refObj = (ReferralcodesIssued) session.getAttribute("refCodeObject");
			contact=(Contacts) session.getAttribute("contact");
             getRedeemedReport(ofs, (byte) pagingEvent.getPageable().getPageSize(),refObj,contact);
	}}
	} // onEvent
	private Combobox exportCbId;
	public void onClick$exportBtnId() {
		if (exportCbId.getSelectedItem().getValue().equals("csv")) {
			
			exportCsv(exportCbId);
		} else if (exportCbId.getSelectedItem().getValue().equals("xls")) {
			exportExcel(exportCbId);
		}
	}
	
	
	public void exportCsv(Combobox exportCbId){
		JdbcResultsetHandler jdbcResultsetHandler = null;
		StringBuffer sb = null;
		BufferedWriter bw = null;
		ResultSet resultSet = null;
		logger.info("enter into export Csv");
		try {
			if(RedeemedRowsId.getChildren().size() == 0) {
				MessageUtil.setMessage("No records exist in the selected search.", "color:red", "TOP");
				return;
			}

			String ext = "csv";
			Long userId = refObj.getUserId();

			String usersParentDirectory = (String) PropertyUtil.getPropertyValue("usersParentDirectory");

			String exportDir = usersParentDirectory + "/" + GetUser.getOnlyUserName()+ "/Export/";
			File downloadDir = new File(exportDir);
			if (!downloadDir.exists()) {
				downloadDir.mkdirs();
			}

			String filePath = exportDir +  "Redeemed Reports_" + System.currentTimeMillis() + "." +ext;
			

			sb = new StringBuffer();
			File file = new File(filePath);
			bw = new BufferedWriter(new FileWriter(file));
			sb.append(
					"\"Contact Email\",\"Contact Mobile\",\"Referral Code\",\"Used By Mobile No.\",\"Used By Email Id\",\"User By Membership Id\",\"Total Revenue\"\n");
			bw.write(sb.toString());

			long totalSize=getRedeemedCountUsed(refObj.getRefcode());
			
			 ReferralcodesRedeemedDao referralcodesRedeemedDao=null;
			 ContactsLoyaltyDao contactsLoyaltyDao=null;
			 ContactsDao contactsDao=null;
				try {
				
					referralcodesRedeemedDao= ( ReferralcodesRedeemedDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRAL_CODES_REDEEMED_DAO);
					contactsLoyaltyDao= (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
					contactsDao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
				} 
				catch (Exception e1) {
					// TODO Auto-generated catch block
					logger.error("Exception :: "+e1);
				}
				
				
				List<ReferralcodesRedeemed> redeemedMembersList=referralcodesRedeemedDao.findRedeemedMemberListByCode(refObj.getRefcode(),0,(int)totalSize);
			 
			
			/*jdbcResultsetHandler = new JdbcResultsetHandler();
			jdbcResultsetHandler.executeStmt(query);
			resultSet = jdbcResultsetHandler.getResultSet();*/
			//while (resultSet.next()) {
				
			for(ReferralcodesRedeemed referralcodesRedeemed : redeemedMembersList) {
				sb.setLength(0);
				
				//mail
				sb.append("\"");
//				sb.append((contact!=null && contact.getEmailId()!=null)?contact.getEmailId().trim():"--"); 
				if(contact!=null && contact.getEmailId()!=null) {
					sb.append(contact.getEmailId());
				}
				else 
					{
					if(refObj.getCampaignType().toString().trim().equalsIgnoreCase("email")) {
						sb.append(refObj.getSentTo().toString());
					}
					 else {sb.append("--");}
					}
				sb.append("\",");
				
				//mobile
				sb.append("\"");
//				sb.append((contact!=null && contact.getMobilePhone()!=null)?contact.getMobilePhone():"--"); 
				if(contact!=null && contact.getMobilePhone()!=null) {
					
					sb.append(contact.getMobilePhone());	
				}
				else {
					if(refObj.getCampaignType().toString().trim().equalsIgnoreCase("sms")) {
						sb.append(refObj.getSentTo().toString());
					}
					else {sb.append("--"); }
					}
				sb.append("\",");
			
				sb.append("\"" +refObj.getRefcode()+ "\",");
				Contacts redeemedcontact=contactsDao.findById(referralcodesRedeemed.getRefereecid());
				
				if(redeemedcontact!=null && redeemedcontact.getMobilePhone()!=null) {sb.append("\"" +redeemedcontact.getMobilePhone()+ "\","); }
				else {
					sb.append("\"" +"--"+ "\",");
				}
				if(redeemedcontact!=null && redeemedcontact.getEmailId()!=null) {sb.append("\"" +redeemedcontact.getEmailId()+ "\","); }
				else {
					sb.append("\"" +"--"+ "\",");
				}
				logger.info("referree id "+referralcodesRedeemed.getRefereecid());
				ContactsLoyalty contactLoyalty= contactsLoyaltyDao.findByContactId(redeemedcontact.getUsers().getUserId(),redeemedcontact.getContactId());
				
				
				if(contactLoyalty!=null && contactLoyalty.getCardNumber() !=null) {
				sb.append("\""+contactLoyalty.getCardNumber()+ "\",");
				}
				else {
					sb.append("\"" +"--"+ "\",");
				}
				if(referralcodesRedeemed!=null && referralcodesRedeemed.getTotRevenue()!=null) {
				sb.append("\"" +referralcodesRedeemed.getTotRevenue().toString()+ "\",");
				}
				else {
					sb.append("\"" +"--"+ "\",");
				}
			//	logger.info("tot revenue"+referralcodesRedeemed.getTotRevenue());
                sb.append("\n");
                

				bw.write(sb.toString());
			}
			bw.flush();
			bw.close();

			Filedownload.save(file, "text/csv");

		} catch (Exception e) {
			logger.error("Exception :: ", e);
		} finally {
			if (jdbcResultsetHandler != null)
				jdbcResultsetHandler.destroy();
			jdbcResultsetHandler = null;
			sb = null;
			bw = null;
		}
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
	 
	public void exportExcel(Combobox exportCbId) {
		try {
			String ext = "xls";
			Long userId = refObj.getUserId();

			String usersParentDirectory = (String) PropertyUtil.getPropertyValue("usersParentDirectory");

			String exportDir = usersParentDirectory + "/" + GetUser.getOnlyUserName()+ "/Export/";
			File downloadDir = new File(exportDir);
			if (!downloadDir.exists()) {
				downloadDir.mkdirs();
			}

			String filePath = exportDir +  "Redeemed Reports_" + System.currentTimeMillis() + "." +ext;
			

//			sb = new StringBuffer();
			File file = new File(filePath);
			logger.debug("Writing to the file : " + filePath);
			FileOutputStream fileOut = new FileOutputStream(filePath);
			HSSFWorkbook hwb = new HSSFWorkbook();
			HSSFSheet sheet = hwb.createSheet("Redeemed Report");
			HSSFRow row = sheet.createRow((short) 0);
			HSSFCell cell = null;
			List<LoyaltyProgramTier> tiersList =null;
			String subQuery = Constants.STRING_NILL;
			
			long totalSize=getRedeemedCountUsed(refObj.getRefcode());
			 ReferralcodesRedeemedDao referralcodesRedeemedDao=null;
			 ContactsLoyaltyDao contactsLoyaltyDao=null;
			 ContactsDao contactsDao=null;
				try {
				
					referralcodesRedeemedDao= ( ReferralcodesRedeemedDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRAL_CODES_REDEEMED_DAO);
					contactsLoyaltyDao= (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
					contactsDao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
				} 
				catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
		
				List<ReferralcodesRedeemed> redeemedMembersList=referralcodesRedeemedDao.findRedeemedMemberListByCode(refObj.getRefcode(),0,(int)totalSize);
			 
		
			if (totalSize== 0) {
				Messagebox.show("No report found.", "Info", Messagebox.OK, Messagebox.INFORMATION);
				return;
		      }
			row = sheet.createRow(0);
			int cellNo = 0;
				cell = row.createCell((cellNo++));
				cell.setCellValue("Contact Email");
			
				cell = row.createCell((cellNo++));
				cell.setCellValue("Contact Mobile");
				
				cell = row.createCell(cellNo++);
				cell.setCellValue("Referral Code");
				
				cell = row.createCell(cellNo++);
				cell.setCellValue("Used By Mobie No.");
				
				cell = row.createCell(cellNo++);
				cell.setCellValue("Used By Email Id");

				cell = row.createCell((cellNo++));
				cell.setCellValue("User by Membership Id");
				
				cell = row.createCell((cellNo++));
				cell.setCellValue("Total Revenue");
				
//				List<Object[]> list=null;
				
//				int size = count;
//				for (int i = 0; i < count; i += size) {
					
					
				int rowId = 1;
				for (ReferralcodesRedeemed referralcodesRedeemed : redeemedMembersList) {
					row = sheet.createRow(rowId++);
					int columnId = 0;
					cell = null;
					
					cell = row.createCell(columnId++);
				//	cell.setCellValue((contact!=null && contact.getEmailId()!=null)?contact.getEmailId():"--");
					if(contact!=null && contact.getEmailId()!=null) {
						cell.setCellValue(contact.getEmailId());
					}
					else 
						{
						if(refObj.getCampaignType().toString().trim().equalsIgnoreCase("email")) {
							cell.setCellValue(refObj.getSentTo().toString());
						}
						 else {cell.setCellValue("--");}
						}
					
					cell = row.createCell(columnId++);
					//cell.setCellValue((contact!=null && contact.getMobilePhone()!=null)?contact.getMobilePhone():"--");
					if(contact!=null && contact.getMobilePhone()!=null) {
						
						cell.setCellValue(contact.getMobilePhone());	
					}
					else {
						if(refObj.getCampaignType().toString().trim().equalsIgnoreCase("sms")) {
							cell.setCellValue(refObj.getSentTo().toString());
						}
						else {cell.setCellValue("--"); }
						}
					
					cell = row.createCell(columnId++);
					cell.setCellValue(refObj.getRefcode());
					
					Contacts redeemedcontact=contactsDao.findById(referralcodesRedeemed.getRefereecid());
					ContactsLoyalty contactLoyalty= contactsLoyaltyDao.findByContactId(redeemedcontact.getUsers().getUserId(),redeemedcontact.getContactId());
					
					cell = row.createCell(columnId++);
					cell.setCellValue(redeemedcontact!=null && redeemedcontact.getMobilePhone()!=null?redeemedcontact.getMobilePhone():"--");
					
					cell = row.createCell(columnId++);
					cell.setCellValue( redeemedcontact!=null && redeemedcontact.getEmailId()!=null?redeemedcontact.getEmailId():"--");
					
					cell = row.createCell(columnId++);
					cell.setCellValue(contactLoyalty!=null && contactLoyalty.getCardNumber()!=null?contactLoyalty.getCardNumber():"--");
					
					cell = row.createCell(columnId++);
					cell.setCellValue(referralcodesRedeemed.getTotRevenue()!=null?referralcodesRedeemed.getTotRevenue().toString():"--");
				}
//			}
				
				hwb.write(fileOut);
				fileOut.flush();
				fileOut.close();
			Filedownload.save(file, "application/vnd.ms-excel");
			logger.debug("exited");

		} catch (Exception e) {
			logger.error("** Exception : ", e);
		}
	}


	private void setPageCount() {
//		int totalSize = couponCodesDao.findTotCountCouponCodesWithFilter(coupObj.getCouponId(),getFilterData());
//		RedeemedRepListBottomPagingId.setTotalSize(totalSize);
		RedeemedRepListBottomPagingId.setActivePage(0);
		RedeemedRepListBottomPagingId.addEventListener("onPaging", this);
		RedeemedRepListBottomPagingId.setAttribute("onPaging", "couponDetail");
		//int tempCount = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		//couponListBottomPagingId.setPageSize(tempCount);
	} 

} // class

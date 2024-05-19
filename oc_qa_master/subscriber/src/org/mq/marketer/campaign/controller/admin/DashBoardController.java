	package org.mq.marketer.campaign.controller.admin;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.clapper.util.io.FileUtil;
import org.mq.marketer.campaign.beans.AsyncLoyaltyTrx;
import org.mq.marketer.campaign.beans.AutoSMS;
import org.mq.marketer.campaign.beans.CampaignReport;
import org.mq.marketer.campaign.beans.CampaignSchedule;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.ContactsLoyaltyStage;
import org.mq.marketer.campaign.beans.CouponDiscountGeneration;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.DomainStatus;
import org.mq.marketer.campaign.beans.FBPItems;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.LoyaltyTransactionExpiry;
import org.mq.marketer.campaign.beans.LoyaltyTransactionExpiryUtil;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.RetailProSalesCSV;
import org.mq.marketer.campaign.beans.SpecialReward;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.ValueCodes;
import org.mq.marketer.campaign.controller.ActiveUsers;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.MessageHandler;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.AsyncLoyaltyTrxDao;
import org.mq.marketer.campaign.dao.AsyncLoyaltyTrxDaoForDML;
import org.mq.marketer.campaign.dao.AutoSMSDao;
import org.mq.marketer.campaign.dao.CampaignReportDao;
import org.mq.marketer.campaign.dao.CampaignScheduleDao;
import org.mq.marketer.campaign.dao.CampaignSentDao;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.dao.ContactsLoyaltyStageDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyStageDaoForDML;
import org.mq.marketer.campaign.dao.CouponDiscountGenerateDaoForDML;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.CouponsDaoForDML;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDaoForDML;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MessagesDao;
import org.mq.marketer.campaign.dao.MessagesDaoForDML;
import org.mq.marketer.campaign.dao.OpensDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.PurgeDao;
import org.mq.marketer.campaign.dao.PurgeDaoForDML;
import org.mq.marketer.campaign.dao.SkuFileDao;
import org.mq.marketer.campaign.dao.SkuFileDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.CouponDescriptionAlgorithm;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.loyalty.SBtoOCTrxConvertor;
//import org.mq.optculture.utils.findLoyaltyMissedTrxs;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDaoForDML;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDao;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDaoForDML;
import org.mq.optculture.data.dao.SpecialRewardsDao;
import org.mq.optculture.data.dao.SpecialRewardsDaoForDML;
import org.mq.optculture.data.dao.ValueCodesDao;
import org.mq.optculture.data.dao.ValueCodesDaoForDML;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.loyalty.HeaderInfo;
import org.mq.optculture.model.loyalty.AmountDetails;
import org.mq.optculture.model.loyalty.CustomerInfo;
import org.mq.optculture.model.loyalty.EnrollmentInfo;
import org.mq.optculture.model.loyalty.IssuanceInfo;
import org.mq.optculture.model.loyalty.LoyaltyEnrollJsonRequest;
import org.mq.optculture.model.loyalty.LoyaltyEnrollJsonResponse;
import org.mq.optculture.model.loyalty.LoyaltyEnrollRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyEnrollResponseObject;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceJsonRequest;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceJsonResponse;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceResponseObject;
import org.mq.optculture.model.loyalty.LoyaltyRedemptionJsonRequest;
import org.mq.optculture.model.loyalty.LoyaltyRedemptionRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyRedemptionJsonResponse;
import org.mq.optculture.model.loyalty.RedemptionInfo;
import org.mq.optculture.model.loyalty.UserDetails;
import org.mq.optculture.utils.LoyaltyRewardExpiryUtility;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.mq.optculture.utils.UpdateDRStore;
import org.zkoss.util.media.Media;
import org.zkoss.zhtml.Fileupload;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;

import au.com.bytecode.opencsv.CSVReader;

import com.google.gson.Gson;

 public class DashBoardController extends GenericForwardComposer {
	
	 private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private  ListitemRenderer renderer  = new MyRenderer();
	private Listbox userListLbId;
//	private Listbox campUserListLbId;
	private Listbox mlistUserListLbId;
	private Listbox campRepSelectLbId;
	private Listbox mlSelectedLbId;
	private Listbox recentCampListLbId;
	private Listbox viewCampListLbId;
	private Listbox customersLbId;
	private Listbox campSelectLbId;
	private Listbox viewDtldRepLbId;
	private Listbox DomainsLbId;
	private Listbox numOfFetchLatestRepLbId;
	private Listbox fetchLatestRepLbId,campSchedulesSelectLbId,campScheduleUserListLbId,campSchedulesStatusLbId,viewCampSchedulesListLbId;
	
	private Listbox userOrgLbId,userNameLbId;
	private Listbox campUserOrgLbId,campUserNameLbId;
	private Listbox mlUserOrgLbId,mlUserNameLbId;
	private Listbox campScheduleUserOrgLbId,campScheduleUserNameLbId;
	
	private Tab listsUsersTabId;
	private Tab latestCampaignsTabId;
	private Textbox userTxtId,firstTxtId,lastTxtId, userDetailsTxtId, bulkUploaduserIDTbId, uploadCSVTbId,usersTxtID,SaleDateTxtID,SaleEndDateTxtID,
	DateToConsiderTxtId,userIDTxtID,progTxtId,activationDateTxtId,
	 excludeIncludItemTbId, acrossSingleMultipleTbId, requiredQtyTbId, couponNameTxtId, userIdTxtId, expiryuserTbId, freeQtyTbId;
	
	private Label sntCntLabelId;
	private Label currentTimeLblId;
	private Groupbox dtldRepsGbId;
	private Groupbox loginUserGBId;
	
	private CampaignsDao campaignsDao=null;
	private CampaignReportDao campaignReportDao=null;
	private MailingListDao mailingListDao =null;
	private CampaignSentDao campaignSentDao=null;
	private OpensDao opensDao=null;
	private UsersDao usersDao=null;
	private Session sessionScope;
	private TimeZone clientTimeZone;
	private CampaignScheduleDao campaignScheduleDao=null;
	
	private Image repImgId;
	private Image campRepImgId;
	
	private MyDatebox fromDateboxId;
	private MyDatebox toDateboxId;
	private Button refreshBtnId,updateStoreNumberBtnId;
	private Textbox updateStoreNumberTxtId;
	
	private Textbox userTbId, fileTbId, ltyUserTbId, ltyFileTbId;
	private Button enrollBtnId;
	private Checkbox appliedPromo;
	
	
	private Div furtherDetailsDivID;
	private static String allStr = "--All--";
	
	//StringBuffer invalidSB = new StringBuffer();

	int failure = 0;
	
	
	public DashBoardController(){
		
		logger.debug("----just entered----");
		
		sessionScope = Sessions.getCurrent();
		campaignReportDao = (CampaignReportDao)SpringUtil.getBean("campaignReportDao");
		clientTimeZone = (TimeZone)sessionScope.getAttribute("clientTimeZone");
		usersDao=(UsersDao)SpringUtil.getBean("usersDao");
		
		campaignScheduleDao=(CampaignScheduleDao)SpringUtil.getBean("campaignScheduleDao");
		
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
     	PageUtil.setHeader("Administrator / Dashboard","",style,true);
	}

	private static HashMap<Long, String> usersMap= new HashMap<Long, String>();
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		logger.debug("----just entered----");
		String CuuentTime= MyCalendar.calendarToString(Calendar.getInstance(),MyCalendar.FORMAT_DATETIME_STDATE);
		currentTimeLblId.setValue(CuuentTime);
		setUserOrg(userOrgLbId);
		recentCampListLbId.setItemRenderer(renderer);
		recentCampListLbId.setModel(new ListModelList(getCampReports()));
		
	}//doAfterCompose
	
	public void onClick$getCoupoDescBtnId() throws Exception{
		try {
			CouponDescriptionAlgorithm algo = new CouponDescriptionAlgorithm();
			
			String couponName = couponNameTxtId.getValue();
			String userID = userIdTxtId.getValue();
			
			CouponsDao coupDao = (CouponsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONS_DAO);
			UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Coupons coupon = coupDao.getConpounByName(userID, couponName);
			Users user = userDao.findByUserId(Long.parseLong(userID));
			String desc =algo.preparecouponDisc(coupon, user);
			MessageUtil.setMessage(desc, "color:blue;");
			coupon.setCouponDescription(desc);
			CouponsDaoForDML couponDaoForDML = (CouponsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.COUPONS_DAOForDML);
			couponDaoForDML.saveOrUpdate(coupon);
		}catch(Exception e){
			
			logger.error("Exception ", e);
		}
	}

	public boolean copyDataFromMediaToFile(String path,Media m) {
		MessagesDao messagesDao = (MessagesDao) SpringUtil.getBean("messagesDao");
		MessagesDaoForDML messagesDaoForDML = (MessagesDaoForDML) SpringUtil.getBean("messagesDaoForDML");
		String ext = FileUtil.getFileNameExtension(path);
		File file = new File(path);
		BufferedReader br = null;
		BufferedWriter bw = null ;
		if(!ext.equalsIgnoreCase("csv")){
			MessageUtil.setMessage("Upload .csv file only.","color:red","BOTTOM");
			return false;
		}
		try{
//			if(logger.isDebugEnabled()) logger.debug("reading data from media using getReaderData()");
			br = new BufferedReader((InputStreamReader)m.getReaderData());
			bw = new BufferedWriter(new FileWriter(path));
			String line = "";
			while((line=br.readLine())!=null){
				bw.write(line);
				bw.newLine();
			}
			bw.flush();
			bw.close();
			br.close();
			return true;
		}catch(Exception e1){
//			logger.error("** Exception is " + e1.getMessage()+" :trying to read with Media.getStringData() **");
			try{
//				if(logger.isDebugEnabled()) logger.debug("Reading file with Media.getStringData()");
				String data = m.getStringData(); 
				FileUtils.writeStringToFile(file, data);
				return true;
			}catch(Exception e2){
//				logger.error("** Exception is " + e2 +" :trying to read as Streams **");
				try {
					FileOutputStream out = new FileOutputStream (file);
					BufferedInputStream in = new BufferedInputStream((FileInputStream)m.getStreamData());
					byte[] buf = new byte[1024];
					int count = 0;
					while ((count = in.read(buf)) >= 0) {
						out.write(buf, 0, count);
					}
					out.flush();
					in.close();
					out.close();
					return true;
				} catch (FileNotFoundException e) {
//					logger.error("** Exception is : File not found **");
				} catch (Exception e3) {
//					logger.error("** Exception is " + e3 +"  so trying to read as bytes **");
					try {
						byte[] data = m.getByteData();
						FileOutputStream fos = new FileOutputStream(file);
						fos.write(data);
						fos.flush();
						fos.close(); 
						return true;
					} catch (Exception e) {
//						logger.error("** Exception is " + e +" **");
					}
				}
				String message = "CSV file upload failed,"+m.getName()+"\n could not copied reason may be due to network problem or may be very large file";
				Users user = GetUser.getUserObj();
				(new MessageHandler(messagesDao,user.getUserName())).sendMessage("Contact","uploaded failed",message,"Inbox",false,"INFO", user);
				return false;
			}
			
		}
	} // copyDataFromMediaToFile
	
	@SuppressWarnings("unchecked")
	public void onClick$startFBPReport() throws Exception{
		
		
		
		SkuFileDao skuFileDao = (SkuFileDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SKU_FILE_DAO);
		SkuFileDaoForDML skuFileDaoForDML = (SkuFileDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SKU_FILE_DAO_FOR_DML);
		
		List<FBPItems> retList = skuFileDao.executeQuery("FROM FBPItems ");
		Map<String, Set<String>>  frequentDocSidsMap = new HashMap<String, Set<String>>();
		
		try {
			for (FBPItems fbpItems : retList) {

				try {
					/*List<RetailProSalesCSV> retFBList = skuFileDao.executeQuery("FROM RetailProSalesCSV WHERE userId="+usersTxtID.getText() +
							" AND inventoryId="+fbpItems.getSkuId()+" AND cid IS NOT NULL AND Date(salesDate)>'"+SaleDateTxtID.getText()+"' ORDER BY salesDate DESC");
					*/
					List<RetailProSalesCSV> retFBList = skuFileDao.executeQuery("FROM RetailProSalesCSV WHERE userId="+usersTxtID.getText()+
							" AND cid IS NOT NULL AND Date(salesDate)>'"+SaleDateTxtID.getText()+"' AND date(salesDate)<='"+SaleEndDateTxtID.getText()+"' "
									+ "AND inventoryId in(SELECT skuId from SkuFile WHERE userId="+usersTxtID.getText()+
							" AND udf4='"+fbpItems.getUdf4()+"' AND dcs='"+fbpItems.getDCS()+"' and udf2='"+fbpItems.getUdf2()+"')  ORDER BY salesDate DESC");
									
					
					Set<String> contactsSet = new HashSet<String>();
					for (RetailProSalesCSV retailProSalesCSV : retFBList) {
						contactsSet.add(retailProSalesCSV.getCid()+"");
					}
					if(contactsSet.isEmpty()) continue;
					for (String cid : contactsSet) {
						try {
							int itemQty = 0;
							for (RetailProSalesCSV retailProSalesCSV : retFBList) {
								try {
									if(!retailProSalesCSV.getCid().toString().equals(cid)) continue;
									
									Set<String> frequentCombo = null;
									 if(retailProSalesCSV.getUdf3() != null && retailProSalesCSV.getUdf3().equalsIgnoreCase("FREQNT")){
										 if(frequentDocSidsMap.containsKey(retailProSalesCSV.getDocSid())){
											 frequentCombo = frequentDocSidsMap.get(retailProSalesCSV.getDocSid());
											 
										 }else{
											 frequentCombo = new HashSet<String>();
										 }
										 frequentCombo.add(fbpItems.getSkuId().longValue()+"");
										 frequentDocSidsMap.put(retailProSalesCSV.getDocSid(), frequentCombo);
										 
										break;
									}
								} catch (Exception e) {
									// TODO Auto-generated catch block
									logger.error("Exception ", e);
								}
							}
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error("Exception ", e);
						}
						
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ", e);
				}
				
			
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e1);
		}
		
		for (FBPItems fbpItems : retList) {
			try {
				List<RetailProSalesCSV> retFBList = skuFileDao.executeQuery("FROM RetailProSalesCSV WHERE userId="+usersTxtID.getText()+
						" AND cid IS NOT NULL AND Date(salesDate)>'"+SaleDateTxtID.getText()+"' AND date(salesDate)<='"+SaleEndDateTxtID.getText()+"' "
						+ "AND inventoryId in(SELECT skuId from SkuFile WHERE userId="+usersTxtID.getText()+ 
				" AND udf4='"+fbpItems.getUdf4()+"' AND dcs='"+fbpItems.getDCS()+"' and udf2='"+fbpItems.getUdf2()+"')  ORDER BY salesDate DESC");
						
				Set<String> contactsSet = new HashSet<String>();
				for (RetailProSalesCSV retailProSalesCSV : retFBList) {
					contactsSet.add(retailProSalesCSV.getCid()+"");
				}
				if(contactsSet.isEmpty()) continue;
				for (String cid : contactsSet) {
					try {
						int itemQty = 0;
						for (RetailProSalesCSV retailProSalesCSV : retFBList) {
							try {
								if(!retailProSalesCSV.getCid().toString().equals(cid)) continue;
								
								 if(retailProSalesCSV.getUdf3() != null && retailProSalesCSV.getUdf3().equalsIgnoreCase("FREQNT")){
									 /*skuFileDaoForDML.executeJdbcUpdateQuery("insert into FBP_customers (inventory_id, cid, qty) "
												+ "values("+retailProSalesCSV.getInventoryId()+","+retailProSalesCSV.getCid()+","+
												retailProSalesCSV.getQuantity()+" )");*/
									 itemQty = 0;
									break;
								}else{
									//itemQty += retailProSalesCSV.getQuantity();
									try {
										if(frequentDocSidsMap.containsKey(retailProSalesCSV.getDocSid()) && 
												frequentDocSidsMap.get(retailProSalesCSV.getDocSid()) != null && 
												!frequentDocSidsMap.get(retailProSalesCSV.getDocSid()).isEmpty() &&
												frequentDocSidsMap.get(retailProSalesCSV.getDocSid()).contains(fbpItems.getSkuId().longValue()+"")) {
											continue;
										}
									} catch (Exception e) {
										// TODO Auto-generated catch block
										logger.error("Exception ", e);
									}
									try {
										skuFileDaoForDML.executeJdbcUpdateQuery("insert into FBP_customers_detailed (inventory_id, cid, qty, sales_date, doc_sid,item_sid,customer_id, sales_id) "
												+ " values("+retailProSalesCSV.getInventoryId()+","+retailProSalesCSV.getCid()+","+
												retailProSalesCSV.getQuantity()+",'"+MyCalendar.calendarToString(retailProSalesCSV.getSalesDate(), MyCalendar.FORMAT_DATETIME_STYEAR)+"','"+
												retailProSalesCSV.getDocSid()+"','"+retailProSalesCSV.getItemSid()+"','"+retailProSalesCSV.getCustomerId()+"',"+retailProSalesCSV.getSalesId()+")" );
									} catch (Exception e) {
										// TODO Auto-generated catch block
										logger.error("Exception ", e);
									}
									itemQty += retailProSalesCSV.getQuantity();
									
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								logger.error("Exception ", e);
							}
						}
						if(itemQty > 0){
							skuFileDaoForDML.executeJdbcUpdateQuery("insert into FBP_customers (cid, qty, udf4,udf2,dcs) "
									+ "values("+cid+","+itemQty+",'"+fbpItems.getUdf4()+"','"+fbpItems.getUdf2()+"','"+fbpItems.getDCS()+"')");
							itemQty = 0;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception ", e);
					}
					
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ", e);
			}
			
		}
		
	}
	
	
	public void onClick$uploadBtnId(){
		Media media = Fileupload.get();	
		MessageUtil.clearMessage();
		if(media == null) {
			MessageUtil.setMessage("Please select a file.", "color:red", "TOP");
			return;
		}
		String userID = bulkUploaduserIDTbId.getText();
		String path = PropertyUtil.getPropertyValue("usersParentDirectory").trim() + "/" + userID + "/List/" +((Media)media).getName();
		logger.info("path---->"+path);
		String ext = FileUtil.getFileNameExtension(path);
		logger.info("ext---->"+ext);
		if(ext == null){
			MessageUtil.setMessage("Upload .csv file only.","color:red","TOP");
			return;
		}
		if(!ext.equalsIgnoreCase("csv")){
			MessageUtil.setMessage("Upload .csv file only.","color:red","TOP");
			return;
		}
		
		
		String pathString = PropertyUtil.getPropertyValue("usersParentDirectory").trim() + "/" + userID + "/List/" + media.getName();
		boolean isSuccess = copyDataFromMediaToFile(pathString,media);
		uploadCSVTbId.setValue(media.getName());
		uploadCSVTbId.setDisabled(true);
		media = null;
		if(!isSuccess){
			MessageUtil.setMessage("File Upload failed. Please contact Dev.", "color:red;");
			return;
		}
		furtherDetailsDivID.setVisible(true);
		MessageUtil.setMessage("successfully Uploaded the file.\n Please fill more details and proceed.", "color:blue;");
		
		
	}
	
	
public void onClick$rewardExpiryBtnId() throws Exception{
		
		try {
			String userID = expiryuserTbId.getText();
			if(userID == null || userID.isEmpty()) {
				MessageUtil.setMessage("missing User ID.", "color:red");
				return;
			}
			Users user = usersDao.findByUsername((userID));
			if(user == null || !user.isEnabled() || Calendar.getInstance().after(user.getPackageExpiryDate())){
				MessageUtil.setMessage("No user Found. Or it is diabled or expired.", "color:red");
				return;

			}
			LoyaltyRewardExpiryUtility loyaltyRewardExpiryUtility =  new LoyaltyRewardExpiryUtility();
			loyaltyRewardExpiryUtility.setUser(user);
			
			boolean isDone = loyaltyRewardExpiryUtility.runExpiry();
			
			MessageUtil.setMessage(isDone ? "Done Boss! " : "Error Boss!", isDone ? "color:blue;" : "color:red;");
			
		}catch (Exception e) {

			logger.error("Exception ===", e);
		}
		
	}
	public void onClick$addToExpiry(){
		
		try {
			String DateToConsider = DateToConsiderTxtId.getValue();
			String userID = userIDTxtID.getText();
			String programID = progTxtId.getText();
			String considerActivationDate = activationDateTxtId.getText();

			LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
			
			LoyaltyProgramTier tier = loyaltyProgramTierDao.getTierById(Long.parseLong(programID));
			
			
			String query = "FROM LoyaltyTransactionChild WHERE userId="+userID
					+ " AND transactionType ='"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"'"+(!considerActivationDate.isEmpty() ? 
							( " AND valueActivationDate IS NOT NULL AND DATE(valueActivationDate)< '"+DateToConsider+"' ORDER BY DATE(valueActivationDate)") : "");
			LoyaltyTransactionChildDao loyaltyTransactionDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			List<LoyaltyTransactionChild> retList = loyaltyTransactionDao.executeQuery(query);
			if(retList == null || retList.isEmpty()){
				
				logger.debug("nothing to do");
				return;
			}
			
			List<LoyaltyTransactionExpiryUtil> addList = new ArrayList<LoyaltyTransactionExpiryUtil>();
			for (LoyaltyTransactionChild loyaltyTransactionChild : retList) {
				
				LoyaltyTransactionExpiryUtil expiry = createExpiryTransaction(loyaltyTransactionChild, 
						loyaltyTransactionChild.getEarnedPoints(), loyaltyTransactionChild.getEarnedAmount(),
						loyaltyTransactionChild.getOrgId(), loyaltyTransactionChild.getTransChildId(), null);
				addList.add(expiry);
				
			}
			if(!addList.isEmpty()){
				
			LoyaltyTransactionExpiryDaoForDML loyaltyTransactionExpiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
			//loyaltyTransactionExpiryDao.saveOrUpdate(transaction);
			loyaltyTransactionExpiryDaoForDML.saveByCollection(addList);
			}
			
			for (LoyaltyTransactionChild loyaltyTransactionChild : retList) {
			if(loyaltyTransactionChild.getConversionAmt() == null || loyaltyTransactionChild.getConversionAmt() <=0 ) continue;
				if(loyaltyTransactionChild.getConversionAmt() != null && loyaltyTransactionChild.getConversionAmt()>0){
					
					long subPoints  = getAutoConvertionReversalVal(loyaltyTransactionChild.getConversionAmt(), tier);
					deductPointsFromExpiryTable(loyaltyTransactionChild.getLoyaltyId(), loyaltyTransactionChild.getUserId(), subPoints, loyaltyTransactionChild.getConversionAmt());
				}
				
				
			}
			
			query = "FROM LoyaltyTransactionChild WHERE userId="+userID
					+ " AND transactionType ='"+OCConstants.LOYALTY_TRANSACTION_REDEMPTION+"'"
							;
			retList = loyaltyTransactionDao.executeQuery(query);
			if(retList == null || retList.isEmpty()){
				
				logger.debug("nothing to do with reemption");
				
			}
			if(retList != null && !retList.isEmpty()){
				
				for (LoyaltyTransactionChild loyaltyTransactionChild : retList) {
					if(loyaltyTransactionChild.getPointsDifference() != null &&
							!loyaltyTransactionChild.getPointsDifference().isEmpty() && 
							Math.abs(Long.parseLong(loyaltyTransactionChild.getPointsDifference())) >0){
						
						deductPointsFromExpiryTable(loyaltyTransactionChild.getLoyaltyId(), loyaltyTransactionChild.getUserId(), Math.abs(Long.parseLong(loyaltyTransactionChild.getPointsDifference())));
					}else if(loyaltyTransactionChild.getAmountDifference() != null &&
							!loyaltyTransactionChild.getAmountDifference().isEmpty() && 
							Math.abs(Long.parseLong(loyaltyTransactionChild.getAmountDifference())) >0){
						
						deductLoyaltyAmtFromExpiryTable(loyaltyTransactionChild.getLoyaltyId(), loyaltyTransactionChild.getUserId(), Math.abs(Long.parseLong(loyaltyTransactionChild.getAmountDifference())));
					}
				}
			}
			
			query = "FROM LoyaltyTransactionChild WHERE userId="+userID
					+ " AND transactionType ='"+OCConstants.LOYALTY_TRANSACTION_RETURN+"'"
							;
			retList = loyaltyTransactionDao.executeQuery(query);
			if(retList == null || retList.isEmpty()){
				
				logger.debug("nothing to do with return");
				
			}
			if(retList != null && !retList.isEmpty()){
				addList = new ArrayList<LoyaltyTransactionExpiryUtil>();
				for (LoyaltyTransactionChild loyaltyTransactionChild : retList) {
					
					if(loyaltyTransactionChild.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL)) {
						
						if(loyaltyTransactionChild.getPointsDifference() != null &&
								!loyaltyTransactionChild.getPointsDifference().isEmpty() && 
								Math.abs(Long.parseLong(loyaltyTransactionChild.getPointsDifference())) >0){
							
							deductPointsFromExpiryTable(loyaltyTransactionChild.getLoyaltyId(), loyaltyTransactionChild.getUserId(), Math.abs(Long.parseLong(loyaltyTransactionChild.getPointsDifference())));
						}else if(loyaltyTransactionChild.getAmountDifference() != null &&
								!loyaltyTransactionChild.getAmountDifference().isEmpty() && 
								Math.abs(Long.parseLong(loyaltyTransactionChild.getAmountDifference())) >0){
							
							deductLoyaltyAmtFromExpiryTable(loyaltyTransactionChild.getLoyaltyId(), loyaltyTransactionChild.getUserId(), Math.abs(Long.parseLong(loyaltyTransactionChild.getAmountDifference())));
						}
					}else if(loyaltyTransactionChild.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL)){
						LoyaltyTransactionExpiryUtil expiry = createExpiryTransaction(loyaltyTransactionChild, 
								loyaltyTransactionChild.getEarnedPoints(), loyaltyTransactionChild.getEarnedAmount(),
								loyaltyTransactionChild.getOrgId(), loyaltyTransactionChild.getTransChildId(), null);
						addList.add(expiry);
						
						
						
					}
					
				}
				
				if(!addList.isEmpty()){
					
					LoyaltyTransactionExpiryDaoForDML loyaltyTransactionExpiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
					//loyaltyTransactionExpiryDao.saveOrUpdate(transaction);
					loyaltyTransactionExpiryDaoForDML.saveByCollection(addList);
					}
			}
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.debug("Exception ", e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.debug("Exception ", e);
		}
		
		
		
		
	}	
	
	
	private void deductPointsFromExpiryTable(Long loyaltyId, Long userId, double subPoints, double earnedAmt) throws Exception{
		logger.info(" Entered into deductPointsFromExpiryTable method >>>");
		
		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		LoyaltyTransactionExpiryDaoForDML expiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
		List<LoyaltyTransactionExpiryUtil> expiryList = null; //expiryDao.fetchExpPointsTrans(""+membershipNumber, 100, userId);
		Iterator<LoyaltyTransactionExpiryUtil> iterList = null; //expiryList.iterator();
		LoyaltyTransactionExpiryUtil expiry = null;
		long remainingPoints = (long)subPoints;
		
		do{
			expiryList = expiryDao.fetchExpLoyaltyPtsTransFromUtil(loyaltyId, 100, userId);
			if(expiryList == null) break;
			iterList = expiryList.iterator();
			
			while(iterList.hasNext()){
				
				logger.info("remainingPoints = "+remainingPoints +" earnedAmt = "+earnedAmt);
				expiry = iterList.next();
				
				if((expiry.getExpiryPoints() == null || expiry.getExpiryPoints() <= 0) && 
						(expiry.getExpiryAmount() == null || expiry.getExpiryAmount() <= 0)){
					logger.info("Wrong entry condition...");
				}
				else if(expiry.getExpiryPoints() < remainingPoints){
					logger.info("subtracted points = "+expiry.getExpiryPoints());
					remainingPoints = remainingPoints - expiry.getExpiryPoints().longValue();
					expiry.setExpiryPoints(0l);
					//expiryDao.saveOrUpdate(expiry);
					expiryDaoForDML.saveOrUpdate(expiry);
					continue;
				}
				else if(expiry.getExpiryPoints() >= remainingPoints){
					logger.info("subtracted points = "+expiry.getExpiryPoints());
					expiry.setExpiryPoints(expiry.getExpiryPoints() - remainingPoints);
					remainingPoints = 0;
					if(expiry.getExpiryAmount() == null){
						expiry.setExpiryAmount(earnedAmt);
					}
					else{
						expiry.setExpiryAmount(expiry.getExpiryAmount() + earnedAmt);
					}
					//logger.info("expiry.getExpiryAmount() = "+expiry.getExpiryAmount()+ " earnedAmt = "+earnedAmt);
					//expiryDao.saveOrUpdate(expiry);
					expiryDaoForDML.saveOrUpdate(expiry);
					//logger.info("expiry.getExpiryAmount() = "+expiry.getExpiryAmount()+ " earnedAmt = "+earnedAmt);
					break;
				}
			}
		
		}while(remainingPoints > 0 && expiryList != null);
		logger.info("Completed deductPointsFromExpiryTable method <<<");
	}
private void deductLoyaltyAmtFromExpiryTable(Long loyalty, Long userId, double subAmt) throws Exception{
		
		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		LoyaltyTransactionExpiryDaoForDML expiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
		List<LoyaltyTransactionExpiryUtil> expiryList = null; 
		Iterator<LoyaltyTransactionExpiryUtil> iterList = null;
		LoyaltyTransactionExpiryUtil expiry = null;
		double remAmount = subAmt;
		
		do{
			expiryList = expiryDao.fetchExpLoyaltyAmtTransUtil(loyalty, 100, userId);
			if(expiryList == null || remAmount <= 0) break;
			iterList = expiryList.iterator();
			
			while(iterList.hasNext()){
				expiry = iterList.next();
				
				if(expiry.getExpiryAmount() == null || expiry.getExpiryAmount() <= 0){ 
					logger.info("WRONG EXPIRY TRANSACTION FETCHED...");
					continue;
				}
				else if(expiry.getExpiryAmount() < remAmount){
					logger.info("subtracted loyalty amount = "+expiry.getExpiryAmount());
					remAmount = remAmount - expiry.getExpiryAmount().doubleValue();
					expiry.setExpiryAmount(0.0);
					//expiryDao.saveOrUpdate(expiry);
					expiryDaoForDML.saveOrUpdate(expiry);
					logger.info("Expiry Amount deducted..."+expiry.getExpiryAmount().doubleValue());
					continue;
					
				}
				else if(expiry.getExpiryAmount() >= remAmount){
					logger.info("subtracted loyalty amount = "+expiry.getExpiryAmount());
					expiry.setExpiryAmount(expiry.getExpiryAmount() - remAmount);
					remAmount = 0; 
					//expiryDao.saveOrUpdate(expiry);
					expiryDaoForDML.saveOrUpdate(expiry);
					logger.info("Expiry Amount deducted..."+remAmount);
					break;
				}
				
			}
			expiryList = null;
		
		}while(remAmount > 0);
		
	}
private void deductPointsFromExpiryTable(Long loyalty, Long userId, long subPoints) throws Exception{
		
		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		LoyaltyTransactionExpiryDaoForDML expiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
		List<LoyaltyTransactionExpiryUtil> expiryList = null; 
		Iterator<LoyaltyTransactionExpiryUtil> iterList = null;
		LoyaltyTransactionExpiryUtil expiry = null;
		long remPoints = subPoints;
		
		do{
			expiryList = expiryDao.fetchExpLoyaltyPtsTransFromUtil(loyalty, 100, userId);
			if(expiryList == null || remPoints <= 0) break;
			iterList = expiryList.iterator();
			
			while(iterList.hasNext()){
				expiry = iterList.next();
				
				if(expiry.getExpiryPoints() == null || expiry.getExpiryPoints() <= 0){ 
					logger.info("WRONG EXPIRY TRANSACTION FETCHED...");
					continue;
				}
				else if(expiry.getExpiryPoints() < remPoints){
					logger.info("subtracted loyalty points = "+expiry.getExpiryPoints());
					remPoints = remPoints - expiry.getExpiryPoints().longValue();
					expiry.setExpiryPoints(0l);
					expiryDaoForDML.saveOrUpdate(expiry);
					continue;
					
				}
				else if(expiry.getExpiryPoints() >= remPoints){
					logger.info("subtracted loyalty points = "+expiry.getExpiryPoints());
					expiry.setExpiryPoints(expiry.getExpiryPoints() - remPoints);
					remPoints = 0; 
					expiryDaoForDML.saveOrUpdate(expiry);
					break;
				}
				
			}
			expiryList = null;
		
		}while(remPoints > 0);
		
	}
	
	
	private long getAutoConvertionReversalVal(Double convertedAmount, LoyaltyProgramTier tier) {
		/* Amith Lulla: e.g. rule 100 points to $1
		[12:53:27] Amith Lulla: sorry, you understood na?
		[12:53:41] Amith Lulla: anyways...
		[12:53:58] Amith Lulla: 550 points earned. converts to $5 and 50 points.
		[12:54:11] Amith Lulla: if product worth returned and 350 points return
		[12:54:25] proumya Acharya: autoconvertion pura rollback karna hein na?
		[12:54:34] proumya Acharya: plz continue
		[12:54:35] Amith Lulla: The calculator will convert $5 x 100 points + 50- points = 550 points
		[12:54:45] Amith Lulla: -350 points= 250 points
		[12:54:59] Amith Lulla: now bal =250 points = $2 and 50p */

		double unitAmtFactor = (double)tier.getConvertFromPoints()/tier.getConvertToAmount();
		//int multiple = (int)unitAmtFactor;
		double multiple = (double)unitAmtFactor;
		Double totConvertedPts = convertedAmount * multiple;

		return totConvertedPts.longValue();//changes long conversion was removed APP-1072
		//double subPoints = multiple * tier.getConvertFromPoints();
		
		
	}
	private LoyaltyTransactionExpiryUtil createExpiryTransaction(LoyaltyTransactionChild loyalty,
			Double expiryPoints, Double expiryAmount, Long orgId, Long transChildId,Long bonusId){
		logger.info(" Entered into createExpiryTransaction method >>>");
		LoyaltyTransactionExpiryUtil transaction = null;
		try{
			
			transaction = new LoyaltyTransactionExpiryUtil();
			transaction.setTransChildId(transChildId);
			transaction.setMembershipNumber(""+loyalty.getMembershipNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			Calendar createdDate = Calendar.getInstance();
			if(loyalty.getValueActivationDate() != null )createdDate.setTime(loyalty.getValueActivationDate());
			transaction.setCreatedDate(createdDate);
			transaction.setOrgId(orgId);
			transaction.setUserId(loyalty.getUserId());
			transaction.setExpiryPoints(expiryPoints != null ? expiryPoints.longValue() : null);
			transaction.setExpiryAmount(expiryAmount);
			transaction.setRewardFlag(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L);
			transaction.setLoyaltyId(loyalty.getLoyaltyId());
			transaction.setBonusId(bonusId);
			transaction.setProgramId(loyalty.getProgramId());
			transaction.setTierId(loyalty.getTierId());
			

			LoyaltyTransactionExpiryDao loyaltyTransactionExpiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
			/*LoyaltyTransactionExpiryDaoForDML loyaltyTransactionExpiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
			//loyaltyTransactionExpiryDao.saveOrUpdate(transaction);
			loyaltyTransactionExpiryDaoForDML.saveOrUpdate(transaction);*/
			
			
			
			
		}catch(Exception e){
			logger.error("Exception while logging enroll transaction...",e);
		}
		logger.info("Completed createExpiryTransaction method <<<");
		return transaction;
	}

	public void onClick$bulkuploadBtnID() throws Exception{
		try {
			long newlyAdded = 0l;
			long updated=0l;
			String userID = bulkUploaduserIDTbId.getText();
			String file = uploadCSVTbId.getText();
			String exclueInclueDiscountedItem  = excludeIncludItemTbId.getText();
			String singleMultiplepurchases = acrossSingleMultipleTbId.getText();
			String requiredQty  = requiredQtyTbId.getText();
			String freeQty = freeQtyTbId.getText();
			boolean IsAppliedPromo = appliedPromo.isChecked();
			if(IsAppliedPromo && (freeQty.isEmpty() || Double.parseDouble(freeQty) <= 0)) {
				
				MessageUtil.setMessage("free item's quanty is missed.", "color:red");
				return;
			}
			if(exclueInclueDiscountedItem == null || exclueInclueDiscountedItem.isEmpty() || 
					(!exclueInclueDiscountedItem.equals("E") && !exclueInclueDiscountedItem.equals("I") )){
				
				MessageUtil.setMessage("Value in Exclude-Include box is missed(E-Exclude, I-Include).", "color:red");
				return;
			}
			if(singleMultiplepurchases == null || singleMultiplepurchases.isEmpty() || (
					!singleMultiplepurchases.equals("S") && !singleMultiplepurchases.equals("M") )){
				
				MessageUtil.setMessage("Value in Across Single-Multiple box is missed(M-Multiple, S-Single).", "color:red");
				return;
			}
			if(requiredQty == null || requiredQty.isEmpty()  ){
				
				MessageUtil.setMessage("Value in required Qty box is missed(must be an integral).", "color:red");
				return;
			}
			if(userID == null || userID.isEmpty()) {
				MessageUtil.setMessage("missing User ID.", "color:red");
				return;
			}
			Users user = usersDao.findByUsername((userID));
			if(user == null){
				MessageUtil.setMessage("No user Found.", "color:red");
				return;

			}
			String usersParentDirectory = PropertyUtil.getPropertyValue("usersParentDirectory") + "/" + user.getUserName() + "/List/"+file;
			CSVReader userFileReader;			
			char delimiter=',';
			try {
				logger.info("fileName "+ usersParentDirectory);
				userFileReader = new CSVReader(new FileReader(usersParentDirectory),delimiter);
			} catch (FileNotFoundException e1) {
				logger.error("Exception ", e1);
				MessageUtil.setMessage("Didnot find the file in the path"+usersParentDirectory, "color:red;");
				return;
			}
			
			List<org.mq.marketer.campaign.beans.POSMapping> SKUPOSMappingList = null;
			POSMappingDao posMappingDao = (POSMappingDao)ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
			SKUPOSMappingList = posMappingDao.findByType("'SKU'", user.getUserId());

			if(SKUPOSMappingList == null || SKUPOSMappingList.size() == 0) {
				MessageUtil.setMessage("Didnot find the SKU mappings.", "color:red;");
				return;
			}
			
			LoyaltyProgramDao programDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO); 
			
			List<LoyaltyProgram> program = programDao.getProgListByUserId(user.getUserId());
			
			if(program == null || program.isEmpty()){
				
				MessageUtil.setMessage("No LoyaltyProgram Found.", "color:red");
				return;
			}
			Set<LoyaltyProgram> programSet = new HashSet<LoyaltyProgram>();
			programSet.addAll(program);
			
			String lineStr=null;
			String tempStr;
			List<String> skuFieldList = new ArrayList<String>();
			skuFieldList.add("DESC");
			skuFieldList.add("SPRNAME");
			skuFieldList.add("VALUECODE");
			skuFieldList.add("REWARDVALUE");
			skuFieldList.add("REWARDTYPE");
			skuFieldList.add("EXPTYPE");
			skuFieldList.add("EXPVALUE");
			skuFieldList.add("LTYPOINTS");
			skuFieldList.add("DISCOUNT");
			skuFieldList.add("QTY");
			skuFieldList.add("MAXDISCOUNT");
			
			
			
			
			
			String Description = Constants.STRING_NILL;
			String rewardName = Constants.STRING_NILL;
			String rewardRule = Constants.STRING_NILL;
			String rewardValueCode = Constants.STRING_NILL;
			String rewardValue = Constants.STRING_NILL;
			String rewardType = Constants.STRING_NILL;
			String rewardExpiryType = Constants.STRING_NILL;
			String rewardExpiryValue = Constants.STRING_NILL;
			String requiredLoyltyPoits = Constants.STRING_NILL;
			String promoDiscount = Constants.STRING_NILL;
			String quantity = Constants.STRING_NILL, maxDiscount = Constants.STRING_NILL;
			
			
			//identify the headers from file
			String[] lineStrTokens;
			String csvColumnStr = "";
			int idxSBSNum = -1;
			int idxSKU = -1;
			int idxDescription = -1;
			int idxItemCategory = -1,idxItemSid= -1,idxDCS=-1,idxVcode= -1,idxDCode =-1,idxCCode =-1,idxSCode =-1;
			
			//					int idxTax = 0;
			//					Map<String, Object> skuHashMap = new HashMap<String, Object>();
			Hashtable udfHashtable = new Hashtable();
			//					List<SkuFile> skuFileList = new ArrayList<SkuFile>();

			FileReader fileReader = new FileReader(usersParentDirectory);
			BufferedReader br = new BufferedReader(fileReader);

			String[] nextLineStr ;

			int descIdx = -1, sprNameIdx = -1 , sprValucode = -1, 
					rewardValidx =-1,rewardExpTypeidx=-1, rewardExpValidx=-1,rewardTypeidx=-1, requiredLoyltyPoitsidx =-1, 
					promoDiscountidx=-1, itemQtyidx = -1, maxDiscountidx = -1;
			//identify the headers from file
			while((lineStr = br.readLine())!= null) {
				if(lineStr.trim().length()==0) continue;
				nextLineStr = parse(lineStr);//  lineStr.split(csvDelemiterStr);


				if( nextLineStr.length == 0) {	continue;	}

				for(int j=0; j<nextLineStr.length ;j++) {
					
					csvColumnStr = nextLineStr[j].trim();
					
					if ((skuFieldList.contains(csvColumnStr.toUpperCase()))
							&& csvColumnStr.equalsIgnoreCase("SPRNAME")) {
						sprNameIdx = j;
						continue;
					}else if ((skuFieldList.contains(csvColumnStr.toUpperCase()))
							&& csvColumnStr.equalsIgnoreCase("DESC")) {
						descIdx = j;
						continue;
					}else if ((skuFieldList.contains(csvColumnStr.toUpperCase()))
							&& csvColumnStr.equalsIgnoreCase("VALUECODE")) {
						sprValucode = j;
						continue;
					}else if ((skuFieldList.contains(csvColumnStr.toUpperCase()))
							&& csvColumnStr.equals("REWARDVALUE")) {
						rewardValidx = j;
						continue;
					}else if ((skuFieldList.contains(csvColumnStr.toUpperCase()))
							&& csvColumnStr.equalsIgnoreCase("REWARDTYPE")) {
						rewardTypeidx = j;
						continue;
					}else if ((skuFieldList.contains(csvColumnStr.toUpperCase()))
							&& csvColumnStr.equalsIgnoreCase("EXPTYPE")) {
						rewardExpTypeidx = j;
						continue;
					}else if ((skuFieldList.contains(csvColumnStr.toUpperCase()))
							&& csvColumnStr.equalsIgnoreCase("EXPVALUE")) {
						rewardExpValidx = j;
						continue;
					}else if ((skuFieldList.contains(csvColumnStr.toUpperCase()))
							&& csvColumnStr.equalsIgnoreCase("LTYPOINTS")) {
						requiredLoyltyPoitsidx = j;
						continue;
					}else if ((skuFieldList.contains(csvColumnStr.toUpperCase()))
							&& csvColumnStr.equalsIgnoreCase("DISCOUNT")) {
						promoDiscountidx = j;
						continue;
					}else if ((skuFieldList.contains(csvColumnStr.toUpperCase()))
							&& csvColumnStr.equalsIgnoreCase("QTY")) {
						itemQtyidx = j;
						continue;
					}else if ((skuFieldList.contains(csvColumnStr.toUpperCase()))
							&& csvColumnStr.equalsIgnoreCase("MAXDISCOUNT")) {
						maxDiscountidx = j;
						continue;
					}
					
				}
				for(POSMapping posMapping : SKUPOSMappingList) {

					for(int j=0; j<nextLineStr.length ;j++) {

						tempStr = nextLineStr[j].trim();
						if(skuFieldList.contains(posMapping.getPosAttribute())) {
							//logger.debug("SKU Field already mapped with the general fields..");
							continue;
						}

						
						else if(!(skuFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Subsidiary Number") 
								&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
							//										//logger.debug("tempStr is ::"+tempStr +">> idxStoreNum IDx is ::"+j );
							idxSBSNum =  j;
							skuFieldList.add(tempStr);
							continue;
						} 

						else if(!(skuFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("SKU") 
								&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
							//										//logger.debug("tempStr is ::"+tempStr +">> idxSKU IDx is ::"+j );
							idxSKU =  j;
							skuFieldList.add(tempStr);
							continue;
						} 
						else if(!(skuFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Description") 
								&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
							//										//logger.debug("tempStr is ::"+tempStr +">> idxDescription is ::"+j );
							idxDescription = j;
							skuFieldList.add(tempStr);
							continue;
						}
						
						else if(!(skuFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Item Category") 
								&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
							//										//logger.debug("tempStr is ::"+tempStr +">> idxItemCategory is ::"+j );
							idxItemCategory = j;
							skuFieldList.add(tempStr);
							continue;
						}
						else if(!(skuFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Item Sid") 
								&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
							idxItemSid = j;
							skuFieldList.add(tempStr);
							continue;


						}else if(!(skuFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Vendor") 
								&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
							idxVcode = j;
							skuFieldList.add(tempStr);
							continue;
						}else if(!(skuFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Department") 
								&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
							idxDCode = j;
							skuFieldList.add(tempStr);
							continue;
						}else if(!(skuFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Class") 
								&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
							idxCCode = j;
							skuFieldList.add(tempStr);
							continue;
						}else if(!(skuFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Subclass") 
								&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
							idxSCode = j;
							skuFieldList.add(tempStr);
							continue;
						}else if(!(skuFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("DCS") 
								&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
							idxDCS = j;
							skuFieldList.add(tempStr);
							continue;
						}//idxDCS=-1,idxVcode= -1,idxDCode =-1,idxCCode =-1,idxSCode =-1;
						else if(!(skuFieldList.contains(tempStr)) && posMapping.getCustomFieldName().startsWith("UDF")
								&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr) ) {	
							//										logger.debug("tempStr is ::"+tempStr +">> file IDx is ::"+j +" >> CustFieldName is :: " +posMapping.getCustomFieldName() +" >>posMapping.getPosAttribute() is ::"+posMapping.getPosAttribute());
							udfHashtable.put(posMapping.getCustomFieldName(), j);
							skuFieldList.add(tempStr);
							continue;
						}

					}	
				} // for

				break;

			}
			
			
			CouponsDaoForDML couponsDaoForDML = (CouponsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.COUPONS_DAOForDML);
			SpecialRewardsDaoForDML specialRewardsDaoForDML = (SpecialRewardsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SPECIAL_REWARDS_DAO_FOR_DML);
			CouponDiscountGenerateDaoForDML couponDiscountGenerateDaoForDML = (CouponDiscountGenerateDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.COUPON_DICOUNT_GENERATE_DAO_FOR_DML);
			ValueCodesDaoForDML valueCodesDaoForDML = (ValueCodesDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.VALUE_CODES_DAO_FOR_DML);
			ValueCodesDao valueCodesDao = (ValueCodesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.VALUE_CODES_DAO);
			CouponsDao couponsDao = (CouponsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONS_DAO);
			SpecialRewardsDao specialRewardsDao  = (SpecialRewardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SPECIAL_REWARDS_DAO);
			CouponDescriptionAlgorithm couponDescriptionAlgorithm =  new CouponDescriptionAlgorithm();
			CustomTemplatesDao customTemplatesDao = (CustomTemplatesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CUSTOMTEMPLATES_DAO);
			AutoSMSDao autoSmsDao = (AutoSMSDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
			
			List<CustomTemplates> retEmailList = customTemplatesDao.findByTemplateName(user.getUserId(), "Discount Against Purchases");
			String rewardTempId = null;
			String rewardSMSTempID = null;
			if(retEmailList != null && !retEmailList.isEmpty() ) {
				rewardTempId = retEmailList.get(0).getTemplateId()+"";
				
			}
			
			List<AutoSMS> retSMSList = autoSmsDao.findByTemplateName(user.getUserId(), "Discount Against Purchases");
			if(retSMSList != null && !retSMSList.isEmpty() ) {
				rewardSMSTempID = retSMSList.get(0).getAutoSmsId()+"";
				
			}
			
			while((lineStr = br.readLine()) != null) {		
				if(lineStr.trim().length()==0) continue;
				nextLineStr = parse(lineStr);//  lineStr.split(csvDelemiterStr);

				if( nextLineStr.length == 0) {
					continue;
				}
				rewardRule = "[#ItemFactor#];=;<json>:<jsonAttribute>;=;<programId>;=;"+exclueInclueDiscountedItem+";=;"+requiredQty+";=;"+singleMultiplepurchases+"<OR>||";
				List<CouponDiscountGeneration> couponDiscounts = new ArrayList<CouponDiscountGeneration>();
				if(descIdx > -1 && descIdx < nextLineStr.length && (tempStr = nextLineStr[descIdx].trim()).length() !=0 ){
					Description = tempStr;
				}
				if(promoDiscountidx > -1 && promoDiscountidx < nextLineStr.length && (tempStr = nextLineStr[promoDiscountidx].trim()).length() !=0 ){
					promoDiscount = tempStr;
				}
				if(sprNameIdx > -1 && sprNameIdx < nextLineStr.length && (tempStr = nextLineStr[sprNameIdx].trim()).length() !=0 ){
					rewardName = tempStr;
				}
				if(rewardTypeidx > -1 && rewardTypeidx < nextLineStr.length && (tempStr = nextLineStr[rewardTypeidx].trim()).length() !=0 ){
					rewardType = tempStr;
				}
				if(rewardValidx > -1 && rewardValidx < nextLineStr.length && (tempStr = nextLineStr[rewardValidx].trim()).length() !=0 ){
					rewardValue = tempStr;
				}
				if(rewardExpValidx > -1 && rewardExpValidx < nextLineStr.length && (tempStr = nextLineStr[rewardExpValidx].trim()).length() !=0 ){
					rewardExpiryValue = tempStr;
				}
				if(rewardExpTypeidx > -1 && rewardExpTypeidx < nextLineStr.length && (tempStr = nextLineStr[rewardExpTypeidx].trim()).length() !=0 ){
					rewardExpiryType = tempStr;
				}
				if( sprValucode> -1 && sprValucode < nextLineStr.length && (tempStr = nextLineStr[sprValucode].trim()).length() !=0 ){
					rewardValueCode = tempStr;
				}
				if( requiredLoyltyPoitsidx> -1 && requiredLoyltyPoitsidx < nextLineStr.length && (tempStr = nextLineStr[requiredLoyltyPoitsidx].trim()).length() !=0 ){
					requiredLoyltyPoits = tempStr;
				}
				if( maxDiscountidx> -1 && maxDiscountidx < nextLineStr.length && (tempStr = nextLineStr[maxDiscountidx].trim()).length() !=0 ){
					maxDiscount = tempStr;
				}
				if( itemQtyidx> -1 && itemQtyidx < nextLineStr.length && (tempStr = nextLineStr[itemQtyidx].trim()).length() !=0 ){
					quantity = tempStr;
				}
//[#ItemFactor#];=;<json>:<jsonAttribute>;=;<programId>;=;E;=;12;=;S<OR>||[#PurchasedItem#];=;items:udf1;=;<programId>;=;ALMO NATURE;=;<val2>;=;<val3><OR>||[#PurchasedItem#];=;items:udf3;=;<programId>;=;3.8OZC;=;<val2>;=;<val3><OR>||[#PurchasedItem#];=;items:departmentCode;=;<programId>;=;CF;=;<val2>;=;<val3><OR>||[#PurchasedItem#];=;items:itemSubClass;=;<programId>;=;ZZZ;=;<val2>;=;<val3><OR>||[#PurchasedItem#];=;items:udf2;=;<programId>;=;CHICKEN;=;<val2>;=;<val3><OR>||[#PurchasedItem#];=;items:itemClass;=;<programId>;=;CN;=;<val2>;=;<val3><OR>||				
				//set the SKU 
				//[#PurchasedItem#];=;items:udf1;=;<programId>;=;ALMO NATURE;=;<val2>;=;<val3><OR>||
				/*private String itemCategory; 
				private String departmentCode;
				private String itemClass;
				private String itemSubClass; 
				private String DCS;
				private String vendorCode;
				private String skuNumber;*/
				if(idxSKU > -1 &&  idxSKU < nextLineStr.length && (tempStr = nextLineStr[idxSKU].trim()).length() !=0 ){
					CouponDiscountGeneration couponDiscount = new CouponDiscountGeneration(Double.parseDouble(promoDiscount), null, null, 
							null, tempStr, "SKU", user.getUserId(), null, quantity, !maxDiscount.isEmpty() ? Double.parseDouble(maxDiscount):null);
					couponDiscounts.add(couponDiscount);
					rewardRule += "[#PurchasedItem#];=;items:skuNumber;=;<programId>;=;"+tempStr+";=;<val2>;=;<val3><OR>||";
				}

				//set the Description
				//		logger.debug("Description value is  ::"+tempStr);
				if(idxDescription > -1 && idxDescription < nextLineStr.length && (tempStr = nextLineStr[idxDescription].trim()).length() !=0){
					CouponDiscountGeneration couponDiscount = new CouponDiscountGeneration(Double.parseDouble(promoDiscount), null, null, 
							null, tempStr, "Description", user.getUserId(), null, quantity, !maxDiscount.isEmpty() ? Double.parseDouble(maxDiscount):null);
					couponDiscounts.add(couponDiscount);
					rewardRule += "[#PurchasedItem#];=;items:description;=;<programId>;=;"+tempStr+";=;<val2>;=;<val3><OR>||";
				}
				
				if(idxItemCategory > -1 && idxItemCategory < nextLineStr.length && (tempStr = nextLineStr[idxItemCategory].trim()).length() !=0){
				
					CouponDiscountGeneration couponDiscount = new CouponDiscountGeneration(Double.parseDouble(promoDiscount), null, null, 
							null, tempStr, "Item Category", user.getUserId(), null, quantity, !maxDiscount.isEmpty() ? Double.parseDouble(maxDiscount):null);
					couponDiscounts.add(couponDiscount);
					rewardRule += "[#PurchasedItem#];=;items:itemCategory;=;<programId>;=;"+tempStr+";=;<val2>;=;<val3><OR>||";
				}

				//Set Item Sid 
				/*if(idxItemSid > -1 && idxItemSid < nextLineStr.length && (tempStr = nextLineStr[idxItemSid].trim()).length() !=0){
				
					CouponDiscountGeneration couponDiscount = new CouponDiscountGeneration(Double.parseDouble(promoDiscount), null, null, 
							null, tempStr, "Vendor", user.getUserId(), null, quantity, !maxDiscount.isEmpty() ? Double.parseDouble(maxDiscount):null);
					couponDiscounts.add(couponDiscount);
				}*/

				// set DCS
				if(idxDCS > -1 && idxDCS < nextLineStr.length && (tempStr = nextLineStr[idxDCS].trim()).length() !=0){
					CouponDiscountGeneration couponDiscount = new CouponDiscountGeneration(Double.parseDouble(promoDiscount), null, null, 
							null, tempStr, "DCS", user.getUserId(), null, quantity, !maxDiscount.isEmpty() ? Double.parseDouble(maxDiscount):null);
					couponDiscounts.add(couponDiscount);
					rewardRule += "[#PurchasedItem#];=;items:DCS;=;<programId>;=;"+tempStr+";=;<val2>;=;<val3><OR>||";
				}

				//Set V Code
				if(idxVcode > -1 && idxVcode < nextLineStr.length && (tempStr = nextLineStr[idxVcode].trim()).length() !=0){
					CouponDiscountGeneration couponDiscount = new CouponDiscountGeneration(Double.parseDouble(promoDiscount), null, null, 
							null, tempStr, "Vendor", user.getUserId(), null, quantity, !maxDiscount.isEmpty() ? Double.parseDouble(maxDiscount):null);
					couponDiscounts.add(couponDiscount);
					rewardRule += "[#PurchasedItem#];=;items:vendorCode;=;<programId>;=;"+tempStr+";=;<val2>;=;<val3><OR>||";
				}

				//set D Code
				if(idxDCode > -1 && idxDCode < nextLineStr.length && (tempStr = nextLineStr[idxDCode].trim()).length() !=0){
				
					CouponDiscountGeneration couponDiscount = new CouponDiscountGeneration(Double.parseDouble(promoDiscount), null, null, 
							null, tempStr, "Department", user.getUserId(), null, quantity, !maxDiscount.isEmpty() ? Double.parseDouble(maxDiscount):null);
					couponDiscounts.add(couponDiscount);
					rewardRule += "[#PurchasedItem#];=;items:departmentCode;=;<programId>;=;"+tempStr+";=;<val2>;=;<val3><OR>||";
				}


				//Set C Code
				if(idxCCode > -1 && idxCCode < nextLineStr.length && (tempStr = nextLineStr[idxCCode].trim()).length() !=0){
					
					
					CouponDiscountGeneration couponDiscount = new CouponDiscountGeneration(Double.parseDouble(promoDiscount), null, null, 
							null, tempStr, "Class", user.getUserId(), null, quantity, !maxDiscount.isEmpty() ? Double.parseDouble(maxDiscount):null);
					couponDiscounts.add(couponDiscount);
					rewardRule += "[#PurchasedItem#];=;items:itemClass;=;<programId>;=;"+tempStr+";=;<val2>;=;<val3><OR>||";
					
				}
				
				//Set S_Code
				if(idxSCode > -1 && idxSCode < nextLineStr.length && (tempStr = nextLineStr[idxSCode].trim()).length() !=0){
					CouponDiscountGeneration couponDiscount = new CouponDiscountGeneration(Double.parseDouble(promoDiscount), null, null, 
							null, tempStr, "Subclass", user.getUserId(), null, quantity, !maxDiscount.isEmpty() ? Double.parseDouble(maxDiscount):null);
					couponDiscounts.add(couponDiscount);
					rewardRule += "[#PurchasedItem#];=;items:itemSubClass;=;<programId>;=;"+tempStr+";=;<val2>;=;<val3><OR>||";
				}

				if(idxSBSNum > -1 && idxSBSNum < nextLineStr.length && (tempStr = nextLineStr[idxSBSNum].trim()).length() !=0){
					CouponDiscountGeneration couponDiscount = new CouponDiscountGeneration(Double.parseDouble(promoDiscount), null, null, 
							null, tempStr, "Subsidiary Number", user.getUserId(), null, quantity, !maxDiscount.isEmpty() ? Double.parseDouble(maxDiscount):null);
					couponDiscounts.add(couponDiscount);
					rewardRule += "[#PurchasedItem#];=;items:subsidiaryNumber;=;<programId>;=;"+tempStr+";=;<val2>;=;<val3><OR>||";
				}

				if(udfHashtable.size() >0 ) {
					String udfDataStr = null;
					String dateFormat ="";
					String dataTypeStr = "";
					for (POSMapping posMapping : SKUPOSMappingList) {
						String custFieldName  = posMapping.getCustomFieldName();

						if(udfHashtable.containsKey(custFieldName)) {
							int udfIdx =(Integer)udfHashtable.get(custFieldName);

							if(udfIdx < nextLineStr.length && (udfDataStr = nextLineStr[udfIdx].trim()).length() !=0) {
																int UDFIdx = Integer.parseInt(custFieldName.substring("UDF".length()));
								CouponDiscountGeneration couponDiscount = new CouponDiscountGeneration(Double.parseDouble(promoDiscount), null, null, 
										null, udfDataStr, custFieldName.toLowerCase(), user.getUserId(), null, quantity, !maxDiscount.isEmpty() ? Double.parseDouble(maxDiscount):null);
								couponDiscounts.add(couponDiscount);
								rewardRule += "[#PurchasedItem#];=;items:"+custFieldName.toLowerCase()+";=;<programId>;=;"+udfDataStr+";=;<val2>;=;<val3><OR>||";
							}
						}
					} //for
				} // if
				
				ValueCodes valueCode = null;
				List<ValueCodes> valueCodes =  valueCodesDao.findValueCode(user.getUserOrganization().getUserOrgId(), rewardValueCode);
				if(valueCodes != null && !valueCodes.isEmpty()) {
					
					valueCode = valueCodes.get(0);
				}
				if(valueCode == null ){
					valueCode = new ValueCodes();
					valueCode.setValuCode(rewardValueCode);
					valueCode.setCreatedBy(userID);
					valueCode.setCreatedDate(Calendar.getInstance());
					valueCode.setOrgId(user.getUserOrganization().getUserOrgId());
				}
				valueCode.setAssociatedWithFBP(true);
				valueCode.setModifiedBy(userID);
				valueCode.setModifiedDate(Calendar.getInstance());
				valueCode.setDescription(Description);
				
				
				valueCodesDaoForDML.saveOrUpdate(valueCode);
				
				Calendar now = Calendar.getInstance();
				
				Calendar expired = Calendar.getInstance();
				expired.add(Calendar.YEAR, 1);
				
				Coupons coupon = null;
				
				coupon = couponsDao.getConpounByName(user.getUserId()+"", rewardName);
				if(coupon != null) {
					
					couponDiscountGenerateDaoForDML.deleteDiscountGenByCouponId(coupon.getCouponId());
				}
				if(coupon == null) {
					
					coupon = new Coupons(rewardName, Description, "Running", "single", rewardValueCode, 
							"Percentage", "SKU", 0l, user.getUserId(), Calendar.getInstance(), 
							Calendar.getInstance(), Calendar.getInstance(), expired, true, 
							0l, true);
				}
				coupon.setEnableBarcode(false);
				coupon.setOrgId(user.getUserOrganization().getUserOrgId());
				coupon.setLastModifiedUser(user.getUserName());
				coupon.setExcludeItems(false);
				coupon.setAccumulateOtherPromotion(false);
				coupon.setLoyaltyPoints((byte)1);
				coupon.setRequiredLoyltyPoits(Integer.parseInt(requiredLoyltyPoits));
				coupon.setAllStoreChk(true);
				coupon.setValueCode(rewardValueCode);
				coupon.setExpiryType("S");
				coupon.setNoOfEligibleItems("HPIWD");
				coupon.setCombineItemAttributes(true);
				coupon.setSingPromoContUnlimitedRedmptChk(true);
				List<CouponDiscountGeneration> discountsTobeAdded = new ArrayList<CouponDiscountGeneration>();
				couponsDaoForDML.saveOrUpdate(coupon);
				for (CouponDiscountGeneration  dicount : couponDiscounts) {
					dicount.setCoupons(coupon);
					
					discountsTobeAdded.add(dicount);
					
				}
				
				couponDiscountGenerateDaoForDML.saveByCollection(discountsTobeAdded);
				
				
SpecialReward specialReward =  null;
				
				List<SpecialReward> retList = specialRewardsDao.findRewarRuleByName(rewardName, user.getUserId());
				
				if(retList != null && !retList.isEmpty()) {
					
					specialReward = retList.get(0);
				}
					
				if(specialReward == null){
					newlyAdded++;
					specialReward = new SpecialReward(rewardName, rewardRule, Description, rewardType, 
							rewardValueCode, rewardValue, rewardExpiryType, rewardExpiryValue, 
							rewardTempId, rewardSMSTempID, programSet, user.getUserOrganization().getUserOrgId()+"", user.getUserId()+"", 
							Calendar.getInstance(), "Active", true,  true);
				}else{
					updated ++;
					specialReward.setRewardRule(rewardRule);
					specialReward.setDescription(Description);
				}
				specialReward.setAssociatedWithFBP(true);
				specialReward.setAutoCommEmail(rewardTempId);
				specialReward.setAutoCommSMS(rewardSMSTempID);
				if(IsAppliedPromo) {
					specialReward.setPromoCode(coupon.getCouponCode());
					specialReward.setPromoCodeName(coupon.getCouponName());
					specialReward.setExcludeQty(Double.parseDouble(freeQty));
				}else{
					
					specialReward.setPromoCode(null);
					specialReward.setExcludeQty(null);
				}
				
				if(rewardExpiryType != null && !rewardExpiryValue.isEmpty() && OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(rewardExpiryValue) ){
					now.add(Calendar.MONTH, Integer.parseInt(rewardExpiryValue));
				}else if(rewardExpiryType != null && !rewardExpiryValue.isEmpty() && OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(rewardExpiryValue)){
					now.add(Calendar.YEAR, Integer.parseInt(rewardExpiryValue));
				}
				specialRewardsDaoForDML.saveOrUpdate(specialReward);
				
				String couponDesc = couponDescriptionAlgorithm.preparecouponDisc(coupon, user);
				if(!user.getCountryType().equals(Constants.SMS_COUNTRY_US) && 
						couponDesc.contains(Utility.countryCurrencyMap.get(user.getCountryType()))){
					couponDesc = couponDesc.replace(Utility.countryCurrencyMap.get(user.getCountryType()), "[PHCurr]");
					
				}
				coupon.setCouponDescription(couponDesc);
				coupon.setPurchaseQty(Double.parseDouble(requiredQty));
				coupon.setSpecialRewadId(specialReward.getRewardId());
				couponsDaoForDML.saveOrUpdate(coupon);
				
				specialReward.setDescription(couponDesc);
				specialRewardsDaoForDML.saveOrUpdate(specialReward);
				
			}

			MessageUtil.setMessage("Done! \n newlyadded : "+newlyAdded+ " \n updated : "+updated, "color:blue;");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			MessageUtil.setMessage(e.getMessage(), "color:red;");
		}
		
		
	}
		public String[] parse(String csvLine) {
			
			Pattern csvPattern = Pattern.compile("\"([^\"]*)\"|(?<=,|^)([^,]*)(?:,|$)");  
			 ArrayList<String> allMatches = new ArrayList<String>();        
			Matcher matcher = null;
			matcher = csvPattern.matcher(csvLine);
			allMatches.clear();
			String match;
			while (matcher.find()) {
				match = matcher.group(1);
				if (match!=null) {
					allMatches.add(match);
				}
				else {
					allMatches.add(matcher.group(2));
				}
			}

			if (allMatches.size() > 0) {
				return allMatches.toArray(new String[allMatches.size()]);
			}
			else {
				return new String[0];
			}                       
		}   
	public void onClick$insertSbtoOcDataBtnId() throws Exception{
		

		
		String user = userDetailsTxtId.getText();
		String st = firstTxtId.getText();
		String userID=userTxtId.getText();
		
		if(userID.length()==0){
			MessageUtil.setMessage("Enter userid","color:red");
			return;
		}
		logger.debug("Please enter last trx id");
		String lt = lastTxtId.getText();
		
		String limit = "";
		if(lt.length()>0){
			limit = " Where user_detail='"+user+"' AND id between "+st+" and "+lt + " and "
					+ " json_response like '%Success%' AND id NOT IN(select transaction_id FROM "
					+ " loyalty_transaction_child where user_id="+userID+" AND transaction_id between "+st+" and "+lt +")";
		}
		
		LoyaltyTransactionDao loyaltyTransactionDao = (LoyaltyTransactionDao) ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
		String countQry = "SELECT COUNT(1) from loyalty_transaction "+limit;
		logger.debug(countQry);
		Long count = loyaltyTransactionDao.getCount(countQry);
		
		int startCount = 0;
		while(count>0){
			try {
				String actualQry = "FROM LoyaltyTransaction  Where userDetail='"+user+"' AND id between "+st+" and "+lt + " and "
						+ " jsonResponse like '%Success%' AND id NOT IN(select transactionId FROM "
						+ " LoyaltyTransactionChild where userId="+userID+" AND transaction_id between "+st+" and "+lt +")";
				logger.debug("actualQry ===="+actualQry);
List<LoyaltyTransaction> trxs =  loyaltyTransactionDao.find(actualQry, startCount);

for(LoyaltyTransaction lty : trxs){
				SBtoOCTrxConvertor thread = new SBtoOCTrxConvertor(lty, Long.valueOf(userID));

				thread.run();
}
startCount +=100;
count -=100;
			} catch (Exception e) {
				// TODO Auto-generated catch block
			logger.error("Exception====", e);
			}
		
		}
		//SBtoOCTrxConvertor.save();
	logger.debug("Done Boss !! ");
	MessageUtil.setMessage("Done boss!!",
			"color:red");
			return;
		
	/*
	findLoyaltyMissedTrxs.main(new String[1]);*/
	}
	
	/**
	 * enables the latest campaigns tab
	 */
//Added to update store_number in dr_sent, will remove in 2.4.3
	public void onClick$updateStoreNumberBtnId(){
	String user_id=	updateStoreNumberTxtId.getValue();
	Date fromCal = fromDateboxId.getValue();
	Date endCal = toDateboxId.getValue();
	if (endCal.before(fromCal)) {
		MessageUtil.setMessage("'To' date must be later than 'From' date.",
				"color:red");
				return;
	}
	if(user_id.isEmpty()){
		Messagebox.show("Please enter user_id");
		return ;
	}
	try{Long.valueOf(user_id);}catch (Exception e) {
		Messagebox.show("Please enter vallid user_id");
		return ;
	}
	UpdateDRStore updateDRStore = new UpdateDRStore();
	updateDRStore.setUser_id(Long.valueOf(user_id));
	Calendar frCal = Calendar.getInstance();
	frCal.setTime(fromCal);
	Calendar eCal = Calendar.getInstance();
	eCal.setTime(endCal);
	updateDRStore.main(MyCalendar.calendarToString(frCal,MyCalendar.FORMAT_YEARTODATE), MyCalendar.calendarToString(eCal,MyCalendar.FORMAT_YEARTODATE));
	
	}
	private static int doLookup(String hostName) throws NamingException {
		Hashtable<Object, Object> env = new Hashtable<Object, Object>();
		env.put("java.naming.factory.initial",  "com.sun.jndi.dns.DnsContextFactory");
		DirContext ictx = new InitialDirContext(env);
	    Attributes attrs = ictx.getAttributes(hostName, new String[]{"MX"});
	    Attribute attr = attrs.get( "MX" );
	    if( attr == null ) return( 0 );
	    return( attr.size());
	}
	//Added for domain pool refresh
	public void onClick$updateDomainStatusBtnId(){
		try{
			PurgeDao purgeDao = (PurgeDao) ServiceLocator.getInstance().getDAOByName("purgeDao");
			PurgeDaoForDML purgeDaoForDML = (PurgeDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName("purgeDaoForDML");
			List<DomainStatus> domainStatus = purgeDao.getAllDomainswithStatus();
			Hashtable<Object, Object> env = new Hashtable<Object, Object>();
			env.put("java.naming.factory.initial",  "com.sun.jndi.dns.DnsContextFactory");
			DirContext ictx = new InitialDirContext(env);
			for(DomainStatus domain:domainStatus){
				int mxCount=-1;
				String cacheMsgStr = Constants.STRING_NILL;
				try {
				    Attributes attrs = ictx.getAttributes(domain.getDomain(), new String[]{"MX"});
				    Attribute attr = attrs.get( "MX" );
				    if( attr == null ) mxCount= 0;
				    mxCount= attr.size();
					cacheMsgStr = (mxCount > 0) ? "Active" : "Not a Mail Server";
				} 
				catch (NamingException ex) {
					cacheMsgStr = ex.getMessage().trim().toUpperCase();
					
					if(cacheMsgStr.contains("DNS ERROR")) {
						cacheMsgStr = "DNS ERROR";
						continue;
					} // if
					else if(cacheMsgStr.contains("DNS NAME NOT FOUND")) {
						cacheMsgStr="Invalid Domain";
					}
				}
				domain.setStatus(cacheMsgStr);
			}
			purgeDaoForDML.saveOrUpdateAll(domainStatus);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		
	}
	public void onClick$latestCampaignsTabId() {
		try{
			setUserOrg(campUserOrgLbId);
			viewCampListLbId.setItemRenderer(renderer);
			viewCampListLbId.setModel(new ListModelList(getCampaigns()));
		} catch (Exception e) {
			logger.error("exception in rendering",e);
		}
	}// onClick$latestCampaignsTabId()
	
	/**
	 * enables the lists and users tab
	 */
	public void onClick$listsUsersTabId() {
		setUserOrg(mlUserOrgLbId);
		loginUserGBId.setVisible(true);
		customersLbId.setItemRenderer(renderer);
		customersLbId.setModel(new ListModelList(getMailingLists()));
		
	}//onClick$listsUsersTabId()
	
	public void onClick$campaignSchedulesTabId() {
		try{
			setUserOrg(campScheduleUserOrgLbId);
			viewCampSchedulesListLbId.setItemRenderer(renderer);
			viewCampSchedulesListLbId.setModel(new ListModelList(getCampSchedules()));
		} catch (Exception e) {
			logger.error("exception in rendering",e);
		}
	}// onClick$latestCampaignsTabId()
	
	public void onClick$enrollBtnId() {
		if(Messagebox.show("Are you sure you want to Enroll? ", "Confirm" ,Messagebox.OK | Messagebox.CANCEL, 
				Messagebox.QUESTION) == Messagebox.OK) {
			processBulkEnrollments();
			
		}
	}
	
	
	private void processBulkEnrollments(){	

		try {
			String userName = userTbId.getValue().trim();
			String fileName = fileTbId.getValue().trim();
			Users user = usersDao.findByUsername(userName);
			logger.info("Input FileName" +fileName);

			char delimiter=',';
			String[] nextLine;
			String path = PropertyUtil.getPropertyValue("usersParentDirectory") + "/" + userName + "/List/";
			CSVReader userFileReader;			

			try {
				logger.info("path+fileName "+( path+fileName));
				userFileReader = new CSVReader(new FileReader(path+fileName),delimiter);
			} catch (FileNotFoundException e1) {
				logger.error("Exception ", e1);
				MessageUtil.setMessage("given inputs are incorrect", "color:red;");
				return;
			}
			// Mapping csv headers with Json Attributes 
			int homeStoreIdx = -1, cardNumberIdx = -1, customerIdIdx = -1, firstNameIdx = -1, lastNameIdx = -1 ;
			int mobileIdx = -1, emailIdIdx = -1, streetIdx = -1, address2Idx = -1, cityIdx = -1, stateIdx = -1, zipIdx = -1;
			int countryIdx = -1, birthDayIdx = -1, genderIdx = -1,  createdDateIdx = -1  ;

			String csvColumnStr = "";
			List<String> fileHeaderList = new ArrayList<String>();
			fileHeaderList.addAll(Constants.BULK_ENROLL_LIST);
			
			String[] lineStrTokens;

			while ((lineStrTokens = userFileReader.readNext())!= null) {


				if (lineStrTokens.length == 0) {
					continue;
				}

				for (int j = 0; j < lineStrTokens.length; j++) {

					csvColumnStr = lineStrTokens[j].trim();
					
					if ((fileHeaderList.contains(csvColumnStr))
							&& csvColumnStr.equals("Home Store")) {
						homeStoreIdx = j;
						logger.info(homeStoreIdx+"-->"+csvColumnStr);
						continue;
					}
					else if ((fileHeaderList.contains(csvColumnStr))
							&& csvColumnStr.equals("Card Number")) {
						cardNumberIdx = j;
						logger.info(cardNumberIdx+"-->"+csvColumnStr);
						continue;
					}
					
					else if ((fileHeaderList.contains(csvColumnStr))
							&& csvColumnStr.equals("Customer Id")) {
						customerIdIdx = j;
						logger.info(customerIdIdx+"-->"+csvColumnStr);
						continue;
					}
					else if ((fileHeaderList.contains(csvColumnStr))
							&& csvColumnStr.equals("First Name")) {
						firstNameIdx = j;
						logger.info(firstNameIdx+"-->"+csvColumnStr);
						continue;
					}
					else if ((fileHeaderList.contains(csvColumnStr))
							&& csvColumnStr.equals("Last Name")) {
						lastNameIdx = j;
						logger.info(lastNameIdx+"-->"+csvColumnStr);
						continue;
					}
					else if ((fileHeaderList.contains(csvColumnStr))
							&& csvColumnStr.equals("Mobile")) {
						mobileIdx = j;
						logger.info(mobileIdx+"-->"+csvColumnStr);
						continue;
					}
					else if ((fileHeaderList.contains(csvColumnStr))
							&& csvColumnStr.equals("Email")) {
						emailIdIdx = j;
						logger.info(emailIdIdx+"-->"+csvColumnStr);
						continue;
					}
					else if ((fileHeaderList.contains(csvColumnStr))
							&& csvColumnStr.equals("Street")) {
						streetIdx = j;
						logger.info(streetIdx+"-->"+csvColumnStr);
						continue;
					}
					else if ((fileHeaderList.contains(csvColumnStr))
							&& csvColumnStr.equals("Address2")) {
						address2Idx = j;
						logger.info(address2Idx+"-->"+csvColumnStr);
						continue;
					}
					else if ((fileHeaderList.contains(csvColumnStr))
							&& csvColumnStr.equals("City")) {
						cityIdx = j;
						logger.info(cityIdx+"-->"+csvColumnStr);
						continue;
					}
					else if ((fileHeaderList.contains(csvColumnStr))
							&& csvColumnStr.equals("State")) {
						stateIdx = j;
						logger.info(stateIdx+"-->"+csvColumnStr);
						continue;
					}
					else if ((fileHeaderList.contains(csvColumnStr))
							&& csvColumnStr.equals("Zip")) {
						zipIdx = j;
						logger.info(zipIdx+"-->"+csvColumnStr);
						continue;
					}
					else if ((fileHeaderList.contains(csvColumnStr))
							&& csvColumnStr.equals("Country")) {
						countryIdx = j;
						logger.info(countryIdx+"-->"+csvColumnStr);
						continue;
					}
					else if ((fileHeaderList.contains(csvColumnStr))
							&& csvColumnStr.equals("Birthday")) {
						birthDayIdx = j;
						logger.info(birthDayIdx+"-->"+csvColumnStr);
						continue;
					}
					else if ((fileHeaderList.contains(csvColumnStr))
							&& csvColumnStr.equals("Gender")) {
						genderIdx = j;
						logger.info(genderIdx+"-->"+csvColumnStr);
						continue;
					}
					else if ((fileHeaderList.contains(csvColumnStr))
							&& csvColumnStr.equals("Created Date")) {
						createdDateIdx = j;
						logger.info(createdDateIdx+"-->"+csvColumnStr);
						continue;
					}

				
				} // for j
				break;
			}
				
					
			int totLines =0;
			int invalidCount = 0;

			FileWriter writer;
			try{
				String outputPath = PropertyUtil.getPropertyValue("usersParentDirectory") + "/" + userName + "/List/";
				String outputFileName = "output"+fileName;
				logger.info("Output FileName" +outputFileName);
				File file = new File(outputPath+"/"+outputFileName);
				writer = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(writer);
				bw.write("\"RecordCount\",\"CardNumber\",\"Status\",\"ErrorCode\",\"ErrorMessage\",\"ContactId\",\"ContactsLoyaltyId\",\"TransChildId\" \r\n");

				while ((nextLine = userFileReader.readNext()) != null) {
					totLines++;

					HeaderInfo headerInfo = new HeaderInfo();
					EnrollmentInfo enrollmentInfo = new EnrollmentInfo();
					CustomerInfo customerInfo = new CustomerInfo();
					AmountDetails amountDetails = new AmountDetails();
					UserDetails userDetails = new UserDetails();

					/* "HEADERINFO":{
				"REQUESTID":"123",
				"STORENUMBER" : "165",
				\93SOURCETYPE\94: \93Store\94
				},*/
					String requestId = "BEU_"+user.getToken()+"_"+System.currentTimeMillis()+"_"+totLines;

					headerInfo.setREQUESTID(""+requestId);
					headerInfo.setSOURCETYPE(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MANUAL);
					headerInfo.setSTORENUMBER(nextLine[homeStoreIdx]); // storeNmuber
					/*"ENROLLMENTINFO":{
				"STORELOCATIONID":"111222",
				"CARDNUMBER":"",
				"CARDPIN":"",
				"CARDTYPE":"loyalty",
				"EMPID":"786"
				},*/
					enrollmentInfo.setSTORELOCATIONID("");
					enrollmentInfo.setCARDNUMBER(nextLine[cardNumberIdx]); // cardNumber
					enrollmentInfo.setCARDPIN("");
					enrollmentInfo.setCARDTYPE(OCConstants.CARD_TYPE_LOYALTY);
					enrollmentInfo.setEMPID("");

					/*"CUSTOMERINFO":{
				"CUSTOMERID":"7777777776666666666",
				"CUSTOMERTYPE":"",
				"FIRSTNAME":"",
				"MIDDLENAME":"",
				"LASTNAME":"",
				"PHONE":"1234567890",
				"PHONEPREF":"",
				"EMAIL":"myid@optculture.com",
				"EMAILPREF":"",
				"ADDRESS1":"",
				"ADDRESS2":"",
				"CITY":"",
				"STATE":"",
				"POSTAL":"",
				"COUNTRY":"",
				"MAILPREF":"",
				"BIRTHDAY":"",
				"ANNIVERSARY":"",
				"GENDER":""
				},*/
					customerInfo.setCUSTOMERID(nextLine[customerIdIdx]); // customerId
					customerInfo.setCUSTOMERTYPE("");
					customerInfo.setFIRSTNAME(nextLine[firstNameIdx]); // FirstName
					customerInfo.setMIDDLENAME("");
					customerInfo.setLASTNAME(nextLine[lastNameIdx]); //LastName
					customerInfo.setPHONE(nextLine[mobileIdx]); // mobilePhone
					customerInfo.setPHONEPREF("");
					customerInfo.setEMAIL(nextLine[emailIdIdx]); // email
					customerInfo.setEMAILPREF("");
					customerInfo.setADDRESS1(nextLine[streetIdx]);      //street
					customerInfo.setADDRESS2(nextLine[address2Idx]);	//address2
					customerInfo.setCITY(nextLine[cityIdx]); //city
					customerInfo.setSTATE(nextLine[stateIdx]); //state
					customerInfo.setPOSTAL(nextLine[zipIdx]); // zip
					customerInfo.setCOUNTRY(nextLine[countryIdx]); // country
					customerInfo.setMAILPREF("");
					customerInfo.setBIRTHDAY(nextLine[birthDayIdx]); // birthday
					customerInfo.setANNIVERSARY("");
					customerInfo.setGENDER(nextLine[genderIdx]); // gender
					customerInfo.setCREATEDDATE(nextLine[createdDateIdx]);
					/*"AMOUNTDETAILS":{
				"ENTEREDAMOUNT":"",
				"VALUECODE":""
				},*/
					amountDetails.setENTEREDAMOUNT("");
					amountDetails.setVALUECODE("");

					/*"USERDETAILS":{
				"USERNAME":"username",
				"ORGANISATION":"oc",
				"TOKEN":"token_value"
				}*/

					/*StringTokenizer st = new StringTokenizer(userName);
				String name = st.nextToken("__");
				String org=st.nextToken("__");
				String orgId=st.nextToken("__");*/

					userDetails.setORGANISATION(Utility.getOnlyOrgId(userName));
					userDetails.setTOKEN(""+user.getToken());
					userDetails.setUSERNAME(Utility.getOnlyUserName(userName));

					LoyaltyEnrollRequestObject loyaltyEnrollRequestObject = null;
					loyaltyEnrollRequestObject = prepareEnrollmentRequest(headerInfo, userDetails, enrollmentInfo, customerInfo, amountDetails );

					LoyaltyEnrollJsonRequest jsonRequestObject = null;
					jsonRequestObject = new LoyaltyEnrollJsonRequest();
					jsonRequestObject.setENROLLMENTREQ(loyaltyEnrollRequestObject);


					String requestDate = nextLine[createdDateIdx]; 
					Calendar cal;
					if(requestDate != null && !requestDate.trim().isEmpty()){
						DateFormat formatter;
						Date date;
						formatter = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
						date = (Date) formatter.parse(requestDate);
						cal = new MyCalendar(Calendar.getInstance(), null,
								MyCalendar.dateFormatMap.get(MyCalendar.FORMAT_DATETIME_STYEAR));
						cal.setTime(date);
					}else{
						cal = Calendar.getInstance();
					}

					String serverTimeZoneVal = PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE);
					int serverTimeZoneValInt = Integer.parseInt(serverTimeZoneVal);
					logger.info("ServerTime.... "+serverTimeZoneValInt);
					String timezoneDiffrenceMinutes = user.getClientTimeZone();
					int timezoneDiffrenceMinutesInt = 0;
					if(timezoneDiffrenceMinutes != null) 
						timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
					logger.info("ClientTime.... "+timezoneDiffrenceMinutesInt);
					timezoneDiffrenceMinutesInt = serverTimeZoneValInt - timezoneDiffrenceMinutesInt;
					logger.info("Client time to Server Time.."+timezoneDiffrenceMinutesInt);
					cal.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt );
					logger.info("Client time to Server Time in Calendar.."+cal);

					Gson gson = new Gson();
					//Convert Object to JSON string
					String requestJson = gson.toJson(jsonRequestObject);
					logger.info("RequestJson::::"+requestJson);
					StringBuffer retSB  = asyncLoyaltyEnrollmentRestService(jsonRequestObject, cal, user, totLines);

					try{

						bw.write(retSB.toString());
						retSB = null;

					}catch (Exception e) {
						logger.error("Exception while writing failed message.", e);
					}

				}

				StringBuffer str = new StringBuffer();
				str.append("TotalInvalidCount: "+invalidCount);
				str.append("\r\n");
				str.append("TotalFailures: "+failure);
				str.append("\r\n");
				bw.write(str.toString());
				str = null;
				failure = 0;
				bw.flush();
				bw.close();
			}catch (Exception e) {
				logger.error("Exception while creating output file.", e);
			}
			userFileReader.close();
		}catch(Exception e){
			logger.info("Exception while bulk enrollments..........."+e);
		}

	}
	private StringBuffer prepareResponse(int totLines, String cardNumber, String status, String errorCode, String errorMsg, Long contactId, Long contactsLoyaltyId, Long transChildId){
		StringBuffer sb = new StringBuffer();
		sb.append("\"");sb.append(totLines); sb.append("\""); sb.append(",");
		sb.append("\"");sb.append(cardNumber); sb.append("\""); sb.append(",");
		sb.append("\"");sb.append(status); sb.append("\""); sb.append(",");
		sb.append("\"");sb.append(errorCode); sb.append("\""); sb.append(",");
		sb.append("\"");sb.append(errorMsg); sb.append("\""); sb.append(",");
		sb.append("\"");sb.append(contactId != null ? contactId.longValue()+"" : ""); sb.append("\""); sb.append(",");
		sb.append("\"");sb.append(contactsLoyaltyId != null ? contactsLoyaltyId.longValue()+"" : ""); sb.append("\""); sb.append(",");
		sb.append("\"");sb.append(transChildId != null? transChildId.longValue()+"" : ""); sb.append("\""); sb.append("\r\n");
		
		return sb;
	}
	
	private StringBuffer asyncLoyaltyEnrollmentRestService(LoyaltyEnrollJsonRequest jsonRequestObject, Calendar cal, Users user, int totLines){

		logger.info("Started Async Loyalty Enroll Rest Service for bulk enrollments..."); 
		ContactsLoyaltyStage loyaltyStage = null;
		try{

			Gson gson = new Gson();
			String requestJson = gson.toJson(jsonRequestObject);
			StringBuffer sb = new StringBuffer();
			logger.info("JSON Request: = "+requestJson);

			LoyaltyEnrollJsonRequest jsonRequest = null;
			try{
				jsonRequest = gson.fromJson(requestJson, LoyaltyEnrollJsonRequest.class);
			}catch(Exception e){
				String responseJson = "{\"ENROLLMENTRESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"101001\",\"MESSAGE\":\"Error 101001: Invalid request.\",\"STATUS\":\"Failure\"}}}";
				sb = prepareResponse(totLines,"","Failure", "101001", "Invalid Request", null, null, null);
				failure++;
				logger.info("Response = "+responseJson);
				//return null;
				return sb;
			}


			if(jsonRequest == null){
				String responseJson = "{\"ENROLLMENTRESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"101001\",\"MESSAGE\":\"Error 101001: Invalid request.\",\"STATUS\":\"Failure\"}}}";
				sb = prepareResponse(totLines, "", "Failure", "101001", "Invalid request", null, null, null);
				failure++;
				logger.info("Response = "+responseJson);
				//return null;
				return sb;
			}

			if(jsonRequest.getENROLLMENTREQ() == null){
				String responseJson = "{\"ENROLLMENTRESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"101003\",\"MESSAGE\":\"Error 101003: Invalid request.\",\"STATUS\":\"Failure\"}}}";
				sb = prepareResponse(totLines, "", "Failure", "101003", "Invalid request", null, null, null);
				failure++;
				logger.info("Response = "+responseJson);
				return sb;
				//return null;
			}
			if(jsonRequest.getENROLLMENTREQ().getHEADERINFO() == null){
				String responseJson = "{\"ENROLLMENTRESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"1004\",\"MESSAGE\":\"Error 1004: Invalid request.\",\"STATUS\":\"Failure\"}}}";
				sb = prepareResponse(totLines, jsonRequest.getENROLLMENTREQ().getENROLLMENTINFO().getCARDNUMBER(), "Failure", "1004", "Invalid request", null, null, null);
				failure++;
				logger.info("Response = "+responseJson);
				return sb;
				//return null;
			}

			String pcFlag = jsonRequest.getENROLLMENTREQ().getHEADERINFO().getPCFLAG();
			String requestId = jsonRequest.getENROLLMENTREQ().getHEADERINFO().getREQUESTID();
			LoyaltyTransaction transaction = null;
			//transaction = findTransactionByRequestId(requestId);
			//if(pcFlag != null && pcFlag.equalsIgnoreCase("true")){
			transaction = findTransactionByRequestId(requestId);
			if(transaction != null && transaction.getStatus().equals(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED)){
				String responseJson = transaction.getJsonResponse();
				sb = prepareResponse(totLines, jsonRequest.getENROLLMENTREQ().getENROLLMENTINFO().getCARDNUMBER(), "Failure", "", "Duplicate request", null, null, null);
				failure++;
				logger.info("Response = "+responseJson);
				return sb;
				//return null;
			}else if(transaction != null && transaction.getStatus().equals(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW)){
			    String responseJson = "{\"ENROLLMENTRESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"101020\",\"MESSAGE\":\"Pending \"101020\": Request is being processed..\",\"STATUS\":\"Failure\"}}}";
				sb = prepareResponse(totLines, jsonRequest.getENROLLMENTREQ().getENROLLMENTINFO().getCARDNUMBER(), "Failure", "101020", "Request is being processed", null, null, null);
			   failure++;
				logger.info("Response = "+responseJson);
				return sb;
				//return null;
			}
			//}	

			//code to handle multiple enrolments for single customer due to timeout at pos or connection delay by sparkbase.
			loyaltyStage = findDuplicateRequest(jsonRequest.getENROLLMENTREQ());

			if(loyaltyStage != null){
				logger.info("Duplicate request....timed out request...");
				String responseJson = "{\"ENROLLMENTRESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"101505\",\"MESSAGE\":\"Error 101505: Request is being processed.\",\"STATUS\":\"Failure\"}}}";
				sb = prepareResponse(totLines, jsonRequest.getENROLLMENTREQ().getENROLLMENTINFO().getCARDNUMBER(), "Failure", "101505", "Request is being processed..", null, null, null);
				failure++;
				logger.info("Response = "+responseJson);
				return sb;
				//return null;
			}
			else{
				loyaltyStage = saveRequestInStageTable(jsonRequest.getENROLLMENTREQ());

			}

			//log transaction
			if(transaction == null){
				transaction = logTransactionRequest(jsonRequest.getENROLLMENTREQ(), requestJson, OCConstants.LOYALTY_MODE_MANUAL_BULK_ENROLLMENT);
			}

			Date date = transaction.getRequestDate().getTime();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
			String transDate = df.format(date);


			BaseRequestObject requestObject = new BaseRequestObject();
			requestObject.setJsonValue(requestJson);
			requestObject.setAction(OCConstants.LOYALTY_SERVICE_ACTION_ENROLMENT);
			requestObject.setTransactionId(""+transaction.getId());
			requestObject.setTransactionDate(transDate);

			BaseService baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.ASYNC_LOYALTY_ENROLMENT_BUSINESS_SERVICE);

			BaseResponseObject responseObject = baseService.processRequest(requestObject);

			logger.info("JSON Response: = "+responseObject.getJsonValue());
			LoyaltyEnrollJsonResponse responseobject = gson.fromJson(responseObject.getJsonValue(), 
					LoyaltyEnrollJsonResponse.class);

			//changes w.r.t migration
			/*if(responseObject.getResponseObject() != null && 
				!OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equals(responseObject.getResponseObject().toString())) {
			 */	
			updateTransactionStatus(loyaltyStage, transaction, responseObject.getJsonValue(), responseobject.getENROLLMENTRESPONSE());
			/**find the CL, con, ltrxchild
			
			from ltrxchild, find the cid, cl
			udate optindate,mode in cl
			update createdte,mode in ltrxchild
			have a logic to compare n update the createddate in con
			
			change date in ltrx
		change the way of preperating response sep method, columns(trxid,cid, lttrxchildid, clid),
			*///}
			// updating created dates and mode in contacts, contactsLoyalty, loyaltyTransactionChild
			
			Long loyaltyId = null;
			Long contactId = null;
			Long transChildId = null;
			try{
				if(responseobject.getENROLLMENTRESPONSE().getSTATUS().getERRORCODE()!= null  && responseobject.getENROLLMENTRESPONSE().getSTATUS().getERRORCODE().equalsIgnoreCase("0")){

					LoyaltyTransactionChildDao loyaltyTransactionChildDao = null;
					loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionChildDao");

					ContactsLoyaltyDao contactsLoyaltyDao = null;
					contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName("contactsLoyaltyDao");
					LoyaltyTransactionChild loyaltyTransactionChild = loyaltyTransactionChildDao.getTransactionChild(user.getUserId(), transaction.getId());
					if(loyaltyTransactionChild != null){
						loyaltyId = loyaltyTransactionChild.getLoyaltyId();
						ContactsLoyalty contactsLoyalty = contactsLoyaltyDao.findAllByLoyaltyId(loyaltyId);
						contactId = contactsLoyalty.getContact().getContactId();
						transChildId = loyaltyTransactionChild.getTransChildId();
						if(transChildId != null){
							updateCreatedDatesAndMode(user.getUserId(), contactId, loyaltyId, transChildId, cal);
						}
					}
				}
			}catch(Exception e){
				logger.info("Exception while updating created dates in child and contacts::", e);
			}

			//we can make use of this table?if yes we have to  stop deletion here 
			/*if(responseObject.getResponseObject() != null && 
				OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equals(responseObject.getResponseObject().toString())) {
			LoyaltyTransactionDao loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			    transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
			    logger.debug("responseJson ======"+responseObject.getJsonValue());
			    transaction.setJsonResponse(responseObject.getJsonValue());
			    transaction.setCardNumber(responseobject.getENROLLMENTRESPONSE().getENROLLMENTINFO().getCARDNUMBER());
			    loyaltyTransactionDao.saveOrUpdate(transaction);
			if(loyaltyStage != null)
				deleteRequestFromStageTable(loyaltyStage);
		}*/

			if(responseObject.getAction().equals(OCConstants.LOYALTY_SERVICE_ACTION_ENROLMENT)){
				String responseJson = responseObject.getJsonValue();
				sb = prepareResponse(totLines, jsonRequest.getENROLLMENTREQ().getENROLLMENTINFO().getCARDNUMBER(), responseobject.getENROLLMENTRESPONSE().getSTATUS().getSTATUS(), responseobject.getENROLLMENTRESPONSE().getSTATUS().getERRORCODE(), responseobject.getENROLLMENTRESPONSE().getSTATUS().getMESSAGE(), contactId, loyaltyId, transChildId);
				if(responseJson.contains("Failure")){
					failure++;
				}
				logger.info("Response = "+responseJson);
				logger.info("Completed Loyalty Enroll Rest Service.");
				//return null;
				return sb;
			}
			sb = null;
		}catch(Exception e){
			logger.error("Error in Enroll rest service", e);
			String responseJson = "{\"ENROLLMENTRESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"101000\",\"MESSAGE\":\"Server error  101000.\",\"STATUS\":\"Failure\"}}}";
			StringBuffer sb = new StringBuffer();
			sb = prepareResponse(totLines, "", "Failure", "101000", "Server error", null, null, null);
			failure++;
			logger.info("Response = "+responseJson);
			if(loyaltyStage != null)
				deleteRequestFromStageTable(loyaltyStage);
			//return null;
			return sb;
		}/*finally{

		deleteRequestFromStageTable(loyaltyStage);

		logger.info("Completed Loyalty Enroll Rest Service.");
	}*/
		logger.info("Completed Loyalty Enroll Rest Service.");
		//return null;
		return null;
	}
	
	
	public void updateCreatedDatesAndMode(Long userId, Long contactId, Long loyaltyId, Long transChildId, Calendar cal){
		try {
		/*LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = null;
		loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionChildDaoForDML");*/
		
		ContactsLoyaltyDaoForDML contactsLoyaltyDaoForDML = null;
		contactsLoyaltyDaoForDML = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("contactsLoyaltyDaoForDML");
		
		ContactsDaoForDML contactsDaoForDML = null;
		contactsDaoForDML = (ContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("contactsDaoForDML");
		
		ContactsDao contactsDao = null;
		contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName("contactsDao");
		Contacts contacts = contactsDao.findById(contactId);
		
		if(contacts != null){
			if(cal.before(contacts.getCreatedDate())){
				contactsDaoForDML.updateCreatedDateForBulkEnrollment(userId, contactId, cal);
			}
		}
		
		contactsLoyaltyDaoForDML.updateOptInDateAndModeForBulkEnrollment(userId, loyaltyId, cal);
		//loyaltyTransactionChildDaoForDML.updateCreatedDateForBulkTransactions(userId, transChildId, cal);
		
		}catch(Exception e){
			logger.error("Exception in find transaction by requestid", e);
		}
	}

	public static LoyaltyTransaction findTransactionByRequestId(String requestId){
		LoyaltyTransaction transaction = null;
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			transaction = loyaltyTransactionDao.findByRequestId(requestId, OCConstants.LOYALTY_SERVICE_TYPE_SB);
		}catch(Exception e){
			logger.error("Exception in find transaction by requestid", e);
		}
		return transaction;
	}

	public LoyaltyTransaction logTransactionRequest(LoyaltyEnrollRequestObject requestObject, String jsonRequest, String mode){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		LoyaltyTransaction transaction = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");


			transaction = new LoyaltyTransaction();
			transaction.setJsonRequest(jsonRequest);
			transaction.setStoreNumber(requestObject.getHEADERINFO().getSTORENUMBER());
			transaction.setRequestId(requestObject.getHEADERINFO().getREQUESTID());
			transaction.setPcFlag(Boolean.valueOf(requestObject.getHEADERINFO().getPCFLAG()));
			transaction.setMode(mode);//online or offline
			transaction.setRequestDate(Calendar.getInstance());
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
			transaction.setType(OCConstants.LOYALTY_TRANSACTION_ENROLMENT);
			transaction.setUserDetail(requestObject.getUSERDETAILS().getUSERNAME()+"__"+requestObject.getUSERDETAILS().getORGANISATION());
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);

		} catch (Exception e) {
			logger.error("Exception in logging transaction", e);
		}
		return transaction;
	}

	public static void updateTransactionStatus(ContactsLoyaltyStage ltyStage, LoyaltyTransaction transaction, String responseJson, LoyaltyEnrollResponseObject response){
		try {
			AsyncLoyaltyTrxDao asyncLoyaltyTrxDao = null;
			AsyncLoyaltyTrxDaoForDML asyncLoyaltyTrxDaoForDML = null;
			AsyncLoyaltyTrx asyncLoyaltyTrx = null;
			LoyaltyTransactionDao loyaltyTransactionDao = null;
			LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");

			transaction = findTransaction(transaction.getId());
			if(transaction.getLoyaltyServiceType() != null && OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equals(transaction.getLoyaltyServiceType())) {

				transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
				logger.debug("responseJson ======"+responseJson);
				transaction.setJsonResponse(responseJson);
				transaction.setCardNumber(response.getENROLLMENTINFO().getCARDNUMBER());
				//    loyaltyTransactionDao.saveOrUpdate(transaction);
				loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
				if(ltyStage != null)
					deleteRequestFromStageTable(ltyStage);

			}else{
				if(response.getSTATUS().getERRORCODE()!= null  && response.getSTATUS().getERRORCODE().equalsIgnoreCase("0")){
					asyncLoyaltyTrxDao    = (AsyncLoyaltyTrxDao) ServiceLocator.getInstance().getDAOByName(OCConstants.ASYNC_LOYALTY_TRX_DAO);
					asyncLoyaltyTrxDaoForDML    = (AsyncLoyaltyTrxDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.ASYNC_LOYALTY_TRX_DAO_FOR_DML);
					asyncLoyaltyTrx = new AsyncLoyaltyTrx();
					asyncLoyaltyTrx.setCreatedTime(MyCalendar.getInstance());
					asyncLoyaltyTrx.setLoyaltyTransaction(transaction);
					asyncLoyaltyTrx.setStatus(OCConstants.ASQ_STATUS_NEW);
					asyncLoyaltyTrx.setTrxType(OCConstants.LOYALTY_ENROLLMENT);
					//asyncLoyaltyTrxDao.saveOrUpdate(asyncLoyaltyTrx);
					asyncLoyaltyTrxDaoForDML.saveOrUpdate(asyncLoyaltyTrx);
					ltyStage.setTrxId(transaction.getId());
					ltyStage.setCardNumber(response.getENROLLMENTINFO().getCARDNUMBER());
					ContactsLoyaltyStageDao contactsLoyaltyStageDao = (ContactsLoyaltyStageDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO);
					ContactsLoyaltyStageDaoForDML contactsLoyaltyStageDaoForDML = (ContactsLoyaltyStageDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO_FOR_DML);
					contactsLoyaltyStageDaoForDML.saveOrUpdate(ltyStage);
				}else{

					transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
					logger.debug("responseJson ======"+responseJson);
					transaction.setJsonResponse(responseJson);
					transaction.setCardNumber(response.getENROLLMENTINFO().getCARDNUMBER());
					//loyaltyTransactionDao.saveOrUpdate(transaction);
					loyaltyTransactionDaoForDML.saveOrUpdate(transaction);

					//we can make use of this table?if yes we have to  stop deletion here 
					if(ltyStage != null)
						deleteRequestFromStageTable(ltyStage);


				}
			}


		}catch(Exception e){
			logger.error("Exception in updating transaction", e);
		}
	}
	public static LoyaltyTransaction findTransaction(Long trxID){
		LoyaltyTransaction transaction = null;
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			transaction = loyaltyTransactionDao.findById(trxID);
		}catch(Exception e){
			logger.error("Exception in find transaction by requestid", e);
		}
		return transaction;
	}
	private static ContactsLoyaltyStage findDuplicateRequest(LoyaltyEnrollRequestObject requestObject) {
		//find the request in stage
		ContactsLoyaltyStage loyaltyStage = null;

		try{

			ContactsLoyaltyStageDao contactsLoyaltyStageDao = (ContactsLoyaltyStageDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO);
			String custId = requestObject.getCUSTOMERINFO().getCUSTOMERID() == null ? "" : requestObject.getCUSTOMERINFO().getCUSTOMERID().trim(); 
			String email = requestObject.getCUSTOMERINFO().getEMAIL() == null ? "" : requestObject.getCUSTOMERINFO().getEMAIL().trim();
			String phone = requestObject.getCUSTOMERINFO().getPHONE() == null ? "" : requestObject.getCUSTOMERINFO().getPHONE().trim(); 		
			String card = requestObject.getENROLLMENTINFO().getCARDNUMBER() == null ? "" : requestObject.getENROLLMENTINFO().getCARDNUMBER().trim(); 
			String userName = requestObject.getUSERDETAILS().getUSERNAME()+Constants.USER_AND_ORG_SEPARATOR +requestObject.getUSERDETAILS().getORGANISATION();  

			ContactsLoyaltyStage requestStage = contactsLoyaltyStageDao.findRequest(custId, email, phone, card, userName,
					OCConstants.LOYALTY_SERVICE_TYPE_SB, OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT);
			if(requestStage != null){
				return loyaltyStage;
			}

		}catch(Exception e){
			logger.error("Exception in finding loyalty duplicate request...");
		}
		return loyaltyStage;
	}

	private static ContactsLoyaltyStage saveRequestInStageTable(LoyaltyEnrollRequestObject requestObject){

		ContactsLoyaltyStage loyaltyStage = null;
		try{

			ContactsLoyaltyStageDao contactsLoyaltyStageDao = (ContactsLoyaltyStageDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO);
			ContactsLoyaltyStageDaoForDML contactsLoyaltyStageDaoForDML = (ContactsLoyaltyStageDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO_FOR_DML);
			String custId = requestObject.getCUSTOMERINFO().getCUSTOMERID() == null ? "" : requestObject.getCUSTOMERINFO().getCUSTOMERID().trim(); 
			String email = requestObject.getCUSTOMERINFO().getEMAIL() == null ? "" : requestObject.getCUSTOMERINFO().getEMAIL().trim();
			String phone = requestObject.getCUSTOMERINFO().getPHONE() == null ? "" : requestObject.getCUSTOMERINFO().getPHONE().trim(); 		
			String card = requestObject.getENROLLMENTINFO().getCARDNUMBER() == null ? "" : requestObject.getENROLLMENTINFO().getCARDNUMBER().trim(); 
			String userName = requestObject.getUSERDETAILS().getUSERNAME()+Constants.USER_AND_ORG_SEPARATOR +requestObject.getUSERDETAILS().getORGANISATION();  

			logger.info("saving request in stage table...");
			loyaltyStage = new ContactsLoyaltyStage();
			loyaltyStage.setCustomerId(custId);
			loyaltyStage.setEmailId(email);
			loyaltyStage.setPhoneNumber(phone);
			loyaltyStage.setUserName(userName);
			loyaltyStage.setReqType(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT);
			loyaltyStage.setServiceType(OCConstants.LOYALTY_SERVICE_TYPE_SB);
			loyaltyStage.setStatus(Constants.LOYALTY_STAGE_PENDING);

			contactsLoyaltyStageDaoForDML.saveOrUpdate(loyaltyStage);
		}catch(Exception e){
			logger.error("Exception while saving loyalty request in stage table...", e);
		}
		return loyaltyStage;

	}

	private static void deleteRequestFromStageTable(ContactsLoyaltyStage loyaltyStage) {

		try{

			ContactsLoyaltyStageDao contactsLoyaltyStageDao = (ContactsLoyaltyStageDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO);
			ContactsLoyaltyStageDaoForDML contactsLoyaltyStageDaoForDML = (ContactsLoyaltyStageDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO_FOR_DML);
			logger.info("deleting loyalty stage record...");
			//contactsLoyaltyStageDao.delete(loyaltyStage);
			contactsLoyaltyStageDaoForDML.delete(loyaltyStage);

		}catch(Exception e){
			logger.error("Exception in while deleting request record from staging table...", e);
		}

	}


	private LoyaltyEnrollRequestObject prepareEnrollmentRequest (HeaderInfo headerInfo, UserDetails userDetails1, EnrollmentInfo enrollInfo,
			CustomerInfo customerInfo, AmountDetails amountDetails) {
		LoyaltyEnrollRequestObject enrollRequest = new LoyaltyEnrollRequestObject();
		enrollRequest.setHEADERINFO(headerInfo);
		enrollRequest.setENROLLMENTINFO(enrollInfo);
		enrollRequest.setUSERDETAILS(userDetails1);
		enrollRequest.setCUSTOMERINFO(customerInfo);
		enrollRequest.setAMOUNTDETAILS(amountDetails);
		return enrollRequest;

	}
	public void onClick$ltyOkBtnId() {
		if(Messagebox.show("Are you sure you want to do bulk transactions? ", "Confirm" ,Messagebox.OK | Messagebox.CANCEL, 
				Messagebox.QUESTION) == Messagebox.OK) {
			processBulkTransactions();
			
		}
	}

	private void processBulkTransactions(){
		try{
			String userName = ltyUserTbId.getValue().trim();
			String fileName = ltyFileTbId.getValue().trim();
			Users user = usersDao.findByUsername(userName);
			logger.info("Input FileName" +fileName);

			char delimiter=',';
			String[] nextLine;
			String path = PropertyUtil.getPropertyValue("usersParentDirectory") + "/" + userName + "/List/";
			CSVReader userFileReader;			

			try {
				logger.info("path+fileName "+( path+fileName));
				userFileReader = new CSVReader(new FileReader(path+fileName),delimiter);
			} catch (FileNotFoundException e1) {
				logger.error("Exception ", e1);
				MessageUtil.setMessage("given inputs are incorrect", "color:red;");
				return;
			}
			// Mapping csv headers with Json Attributes 
			int transactionTypeIdx = -1, storeNumberIdx = -1, customerIdIdx = -1, docsidIdx = -1;
			int cardNumberIdx = -1, enteredAmountIdx = -1, createdDateIdx = -1  ;

			String csvColumnStr = "";
			List<String> fileHeaderList = new ArrayList<String>();
			fileHeaderList.addAll(Constants.BULK_TRANS_LIST);

			String[] lineStrTokens;

			while ((lineStrTokens = userFileReader.readNext())!= null) {

				if (lineStrTokens.length == 0) {
					continue;
				}

				for (int j = 0; j < lineStrTokens.length; j++) {

					csvColumnStr = lineStrTokens[j].trim();

					if ((fileHeaderList.contains(csvColumnStr))
							&& csvColumnStr.equals("Transaction Type")) {
						transactionTypeIdx = j;
						logger.info(transactionTypeIdx+"-->"+csvColumnStr);
						continue;
					}
					else if ((fileHeaderList.contains(csvColumnStr))
							&& csvColumnStr.equals("Store Number")) {
						storeNumberIdx = j;
						logger.info(storeNumberIdx+"-->"+csvColumnStr);
						continue;
					}
					else if ((fileHeaderList.contains(csvColumnStr))
							&& csvColumnStr.equals("Customer Id")) {
						customerIdIdx = j;
						logger.info(customerIdIdx+"-->"+csvColumnStr);
						continue;
					}
					else if ((fileHeaderList.contains(csvColumnStr))
							&& csvColumnStr.equals("Docsid")) {
						docsidIdx = j;
						logger.info(docsidIdx+"-->"+csvColumnStr);
						continue;
					}
					else if ((fileHeaderList.contains(csvColumnStr))
							&& csvColumnStr.equals("Card Number")) {
						cardNumberIdx = j;
						logger.info(cardNumberIdx+"-->"+csvColumnStr);
						continue;
					}			
					else if ((fileHeaderList.contains(csvColumnStr))
							&& csvColumnStr.equals("Entered Amount")) {
						enteredAmountIdx = j;
						logger.info(enteredAmountIdx+"-->"+csvColumnStr);
						continue;
					}
					else if ((fileHeaderList.contains(csvColumnStr))
							&& csvColumnStr.equals("Created Date")) {
						createdDateIdx = j;
						logger.info(createdDateIdx+"-->"+csvColumnStr);
						continue;
					}
				} // for j
				break;
			}// while
			int totLines =0;
			int invalidCount = 0;

			FileWriter writer;
			try{
				String outputPath = PropertyUtil.getPropertyValue("usersParentDirectory") + "/" + userName + "/List/";
				String outputFileName = "output"+fileName;
				logger.info("Output FileName" +outputFileName);
				File file = new File(outputPath+"/"+outputFileName);
				writer = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(writer);
				bw.write("\"RecordCount\",\"CardNumber\",\"Status\",\"ErrorCode\",\"ErrorMessage\" \r\n");

				StringBuffer retSB= null;
				while ((nextLine = userFileReader.readNext()) != null) {
					totLines++;
					int lineTokenCount = nextLine.length;
					/*if(lineTokenCount==1 && nextLine[0]!=null && nextLine[0].trim().equals("")) {
						if(logger.isDebugEnabled()) logger.debug("Empty line");
						StringBuffer str = new StringBuffer();
						str = prepareIRResponse(totLines, nextLine[cardNumberIdx], "Failure", "", "EmptyLine");
						bw.write(str.toString());
						str = null;
						invalidCount++;
						continue;
					}*/
					
					if(nextLine[transactionTypeIdx].equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_ISSUANCE)){

						HeaderInfo headerInfo = new HeaderInfo();
						UserDetails userDetails = new UserDetails();
						IssuanceInfo issuanceInfo = new IssuanceInfo();
						/*"HEADERINFO":{
							"REQUESTID":"890",
							"STORENUMBER" : "165",
							\93SOURCETYPE\94: \93Store\94
							},*/
						
						String requestId = "BIU_"+nextLine[docsidIdx]+"_"+System.currentTimeMillis()+"_"+totLines;

						headerInfo.setREQUESTID(""+requestId);
						headerInfo.setSTORENUMBER(nextLine[storeNumberIdx]);
						headerInfo.setSOURCETYPE(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MANUAL);
						
						/*"USERDETAILS":{
							"TOKEN":"token_value",
							"USERNAME":"user_name",
							"ORGANISATION":"oc"
							},*/
						userDetails.setORGANISATION(Utility.getOnlyOrgId(userName));
						userDetails.setTOKEN(""+user.getToken());
						userDetails.setUSERNAME(Utility.getOnlyUserName(userName));
						
						/*"ISSUANCEINFO":{
							"EMPID":"786",
							"CUSTOMERID":"222222233333333",
							"STORELOCATIONID":"111222",
							"DOCSID": "343545435322233123",
							"CARDNUMBER":"999999998888888",
							"CARDPIN":"123456",
							"VALUECODE":"Points",
							"ENTEREDAMOUNT":"1"
							}*/
						issuanceInfo.setEMPID("");
						issuanceInfo.setCUSTOMERID(nextLine[customerIdIdx]);
						issuanceInfo.setSTORELOCATIONID("");
						issuanceInfo.setDOCSID(nextLine[docsidIdx]);
						issuanceInfo.setCARDNUMBER(nextLine[cardNumberIdx]);
						issuanceInfo.setCARDPIN("");
						issuanceInfo.setVALUECODE(OCConstants.LOYALTY_USD);
						issuanceInfo.setENTEREDAMOUNT(nextLine[enteredAmountIdx]);
						issuanceInfo.setCREATEDDATE(nextLine[createdDateIdx]);
						
						LoyaltyIssuanceRequestObject loyaltyIssuanceRequestObject = null;
						loyaltyIssuanceRequestObject = prepareIssuanceRequest(headerInfo, userDetails, issuanceInfo);

						LoyaltyIssuanceJsonRequest jsonRequestObject = null;
						jsonRequestObject = new LoyaltyIssuanceJsonRequest();
						jsonRequestObject.setLOYALTYISSUANCEREQ(loyaltyIssuanceRequestObject);


						/*String requestDate = nextLine[createdDateIdx];  
						DateFormat formatter;
						Date date;
						formatter = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
						date = (Date) formatter.parse(requestDate);
						Calendar cal = new MyCalendar(Calendar.getInstance(), null,
								MyCalendar.dateFormatMap.get(MyCalendar.FORMAT_DATETIME_STYEAR));
						cal.setTime(date);
						
						String serverTimeZoneVal = PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE);
						int serverTimeZoneValInt = Integer.parseInt(serverTimeZoneVal);
						String timezoneDiffrenceMinutes = user.getClientTimeZone();
						logger.info(timezoneDiffrenceMinutes);
						int timezoneDiffrenceMinutesInt = 0;
						if(timezoneDiffrenceMinutes != null) 
							timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
						timezoneDiffrenceMinutesInt = serverTimeZoneValInt - timezoneDiffrenceMinutesInt;
						logger.info("Client time to Server Time.."+timezoneDiffrenceMinutesInt);
						cal.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt > 0 ? -timezoneDiffrenceMinutesInt : timezoneDiffrenceMinutesInt);*/

						Gson gson = new Gson();
						//Convert Object to JSON string
						String requestJson = gson.toJson(jsonRequestObject);
						logger.info("RequestJson::::"+requestJson);
						retSB  = asyncLoyaltyIssuanceRestService(jsonRequestObject, user, totLines);

					}
					else if(nextLine[transactionTypeIdx].equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_REDEMPTION)){

						
						HeaderInfo headerInfo = new HeaderInfo();
						UserDetails userDetails = new UserDetails();
						RedemptionInfo redemptionInfo = new RedemptionInfo();
						/*"HEADERINFO":{
							"REQUESTID":"890",
							"STORENUMBER" : "165",
							\93SOURCETYPE\94: \93Store\94
							},*/
						String requestId = "BRU_"+nextLine[docsidIdx]+"_"+System.currentTimeMillis()+"_"+totLines;
						headerInfo.setREQUESTID(""+requestId);
						headerInfo.setSTORENUMBER(nextLine[storeNumberIdx]);
						headerInfo.setSOURCETYPE(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MANUAL);
						
						/*"USERDETAILS":{
							"TOKEN":"token_value",
							"USERNAME":"user_name",
							"ORGANISATION":"oc"
							},*/
						userDetails.setORGANISATION(Utility.getOnlyOrgId(userName));
						userDetails.setTOKEN(""+user.getToken());
						userDetails.setUSERNAME(Utility.getOnlyUserName(userName));
						
						/*"REDEMPTIONINFO":{
							"EMPID":"786",
							"STORELOCATIONID":"111222",
							"DOCSID": "343545435322233123",
							"CARDNUMBER":"99999999888888",
							"CARDPIN":"123456",
							"VALUECODE":"Points",
							"ENTEREDAMOUNT":"2"
							}*/
						redemptionInfo.setEMPID("");
						redemptionInfo.setSTORELOCATIONID("");
						redemptionInfo.setDOCSID(nextLine[docsidIdx]);
						redemptionInfo.setCARDNUMBER(nextLine[cardNumberIdx]);
						redemptionInfo.setCARDPIN("");
						redemptionInfo.setVALUECODE(OCConstants.LOYALTY_USD);
						redemptionInfo.setENTEREDAMOUNT(nextLine[enteredAmountIdx]);
						redemptionInfo.setCREATEDDATE(nextLine[createdDateIdx]);
						LoyaltyRedemptionRequestObject loyaltyRedemptioneRequestObject = null;
						loyaltyRedemptioneRequestObject = prepareRedemptionRequest(headerInfo, userDetails, redemptionInfo);

						LoyaltyRedemptionJsonRequest jsonRequestObject = null;
						jsonRequestObject = new LoyaltyRedemptionJsonRequest();
						jsonRequestObject.setLOYALTYREDEMPTIONREQ(loyaltyRedemptioneRequestObject);


						/*String requestDate = nextLine[createdDateIdx];  
						DateFormat formatter;
						Date date;
						formatter = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
						date = (Date) formatter.parse(requestDate);
						Calendar cal = new MyCalendar(Calendar.getInstance(), null,
								MyCalendar.dateFormatMap.get(MyCalendar.FORMAT_DATETIME_STYEAR));
						cal.setTime(date);
						
						String serverTimeZoneVal = PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE);
						int serverTimeZoneValInt = Integer.parseInt(serverTimeZoneVal);
						String timezoneDiffrenceMinutes = user.getClientTimeZone();
						logger.info(timezoneDiffrenceMinutes);
						int timezoneDiffrenceMinutesInt = 0;
						if(timezoneDiffrenceMinutes != null) 
							timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
						timezoneDiffrenceMinutesInt = serverTimeZoneValInt - timezoneDiffrenceMinutesInt;
						logger.info("Client time to Server Time.."+timezoneDiffrenceMinutesInt);
						cal.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt > 0 ? -timezoneDiffrenceMinutesInt : timezoneDiffrenceMinutesInt);*/

						Gson gson = new Gson();
						//Convert Object to JSON string
						String requestJson = gson.toJson(jsonRequestObject);
						logger.info("RequestJson::::"+requestJson);
						retSB  = asyncLoyaltyRedemptionRestService(jsonRequestObject, user, totLines);

					
					}
					try{

						bw.write(retSB.toString());
						retSB = null;

					}catch (Exception e) {
						logger.error("Exception while writing failed message.", e);
					}
					
				}
				StringBuffer str = new StringBuffer();
				str.append("TotalInvalidCount: "+invalidCount);
				str.append("\r\n");
				str.append("TotalFailures: "+failure);
				str.append("\r\n");
				bw.write(str.toString());
				str = null;
				failure = 0;
				bw.flush();
				bw.close();

				
			}catch (Exception e) {
				logger.error("Exception while creating output file.", e);
			}
			userFileReader.close();
		}
		catch(Exception e){
			logger.info("Exception while bulk enrollments..........."+e);
		}


	}
	private StringBuffer prepareIRResponse(int totLines, String cardNumber, String status, String errorCode, String errorMsg){
		StringBuffer sb = new StringBuffer();
		sb.append("\"");sb.append(totLines); sb.append("\""); sb.append(",");
		sb.append("\"");sb.append(cardNumber); sb.append("\""); sb.append(",");
		sb.append("\"");sb.append(status); sb.append("\""); sb.append(",");
		sb.append("\"");sb.append(errorCode); sb.append("\""); sb.append(",");
		sb.append("\"");sb.append(errorMsg); sb.append("\""); sb.append("\r\n");
		return sb;
	}
	
	private StringBuffer  asyncLoyaltyIssuanceRestService(LoyaltyIssuanceJsonRequest jsonRequestObject, Users user, int totLines){

		logger.info("Started Bulk Transaction Loyalty Issuance Rest Service...");
		try{
			Gson gson = new Gson();
			String requestJson = gson.toJson(jsonRequestObject);
			StringBuffer sb = new StringBuffer();
			logger.info("JSON Request: = "+requestJson);

			LoyaltyIssuanceJsonRequest jsonRequest = null;
			
			try{
				jsonRequest = gson.fromJson(requestJson, LoyaltyIssuanceJsonRequest.class);
			}catch(Exception e){
				String responseJson = "{\"LOYALTYISSUANCERESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"101002\",\"MESSAGE\":\"Error \"101002\": Invalid request.\",\"STATUS\":\"Failure\"}}}";
				sb = prepareIRResponse(totLines,"","Failure", "101002", "Invalid Request");
				failure++;
				logger.info("Response = "+responseJson);
				//return null;
				return sb;
			}
			
			//TODO: CHANGE RESPONSE
			if(jsonRequest == null){
				String responseJson = "{\"LOYALTYISSUANCERESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"101002\",\"MESSAGE\":\"Error \"101002\": Invalid request.\",\"STATUS\":\"Failure\"}}}";
				sb = prepareIRResponse(totLines,"","Failure", "101002", "Invalid Request");
				failure++;
				logger.info("Response = "+responseJson);
				return sb;
			}
			
			if(jsonRequest.getLOYALTYISSUANCEREQ() == null){
				String responseJson = "{\"LOYALTYISSUANCERESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"200001\",\"MESSAGE\":\"Error \"200001\": Invalid request.\",\"STATUS\":\"Failure\"}}}";
				sb = prepareIRResponse(totLines,"","Failure", "200001", "Invalid Request");
				failure++;
				logger.info("Response = "+responseJson);
				return sb;
			}
			
			if(jsonRequest.getLOYALTYISSUANCEREQ().getHEADERINFO() == null){
				String responseJson = "{\"LOYALTYISSUANCERESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"1004\",\"MESSAGE\":\"Error \"1004\": Invalid request.\",\"STATUS\":\"Failure\"}}}";
				sb = prepareIRResponse(totLines,"","Failure", "1004", "Invalid Request");
				failure++;
				logger.info("Response = "+responseJson);
				return sb;
			}

			if(jsonRequest.getLOYALTYISSUANCEREQ().getHEADERINFO().getREQUESTID() == null){
				String responseJson = "{\"LOYALTYISSUANCERESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"1004\",\"MESSAGE\":\"Error \"1004\": Invalid request.\",\"STATUS\":\"Failure\"}}}";
				sb = prepareIRResponse(totLines,"","Failure", "1004", "Invalid Request");
				failure++;
				logger.info("Response = "+responseJson);
				return sb;
			}
			String userName = jsonRequest.getLOYALTYISSUANCEREQ().getUSERDETAILS().getUSERNAME()+Constants.USER_AND_ORG_SEPARATOR+jsonRequest.getLOYALTYISSUANCEREQ().getUSERDETAILS().getORGANISATION();
			String pcFlag = jsonRequest.getLOYALTYISSUANCEREQ().getHEADERINFO().getPCFLAG();
			String requestId = jsonRequest.getLOYALTYISSUANCEREQ().getHEADERINFO().getREQUESTID();
			
			LoyaltyTransaction transaction = null;
			String docSId = jsonRequest.getLOYALTYISSUANCEREQ().getISSUANCEINFO().getDOCSID();
			
			//reverting the changes
			//if DOCSID not there then split the request id(the second token is meant for DOCSID).This is given for v8 plugin
			boolean isInFormat = (requestId != null && !requestId.isEmpty() && requestId.split(OCConstants.TOKEN_UNDERSCORE).length==3);
			
			String CustSID =  jsonRequest.getLOYALTYISSUANCEREQ().getISSUANCEINFO().getCUSTOMERID();
			
			boolean isCustSID = (isInFormat && CustSID != null && !CustSID.trim().isEmpty() && 
					CustSID.equalsIgnoreCase(requestId.split(OCConstants.TOKEN_UNDERSCORE)[1]));
			
			if(docSId == null ||( docSId != null && docSId.trim().isEmpty()) && isInFormat && !isCustSID) {
				String POSVersion = PropertyUtil.getPOSVersion(userName);
				if(POSVersion != null && !POSVersion.equalsIgnoreCase(OCConstants.POSVERSION_V8)){
					docSId = requestId.split(OCConstants.TOKEN_UNDERSCORE)[1];
				}
				
			}
			
			String userDetail = jsonRequest.getLOYALTYISSUANCEREQ().getUSERDETAILS().getUSERNAME()+"__"+jsonRequest.getLOYALTYISSUANCEREQ().getUSERDETAILS().getORGANISATION();
			//transaction = findTransactionByRequestId(requestId);
			if(pcFlag != null && pcFlag.equalsIgnoreCase("true")){
				transaction = findIssuanceTransactionBy(userDetail,requestId, null);
				
				if(transaction != null && transaction.getStatus().equals(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED)){
					String responseJson = transaction.getJsonResponse();
					sb = prepareIRResponse(totLines, jsonRequest.getLOYALTYISSUANCEREQ().getISSUANCEINFO().getCARDNUMBER(), "Failure", "", "Duplicate request");
					failure++;
					logger.info("Response = "+responseJson);
					return sb;
					
				}else if(transaction != null && transaction.getStatus().equals(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW)){
					String responseJson = "{\"LOYALTYISSUANCERESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"101020\",\"MESSAGE\":\"Pending \"101020\": Request is being processed..\",\"STATUS\":\"Failure\"}}}";
					sb = prepareIRResponse(totLines,"","Failure", "101020", "Request is being processed..");
					failure++;
					logger.info("Response = "+responseJson);
					return sb;
				}
			}
			if(docSId != null && !docSId.trim().isEmpty()){
				transaction = findIssuanceTransactionBy(userDetail,null, docSId);
				if(transaction != null && transaction.getStatus().equals(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED)){
					String responseJson = transaction.getJsonResponse();
					sb = prepareIRResponse(totLines, jsonRequest.getLOYALTYISSUANCEREQ().getISSUANCEINFO().getCARDNUMBER(), "Failure", "", "Duplicate request");
					failure++;
					logger.info("Response = "+responseJson);
					return sb;
				}else if(transaction != null && transaction.getStatus().equals(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW)){
					String responseJson = "{\"LOYALTYISSUANCERESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"101020\",\"MESSAGE\":\"Pending \"101020\": Request is being processed..\",\"STATUS\":\"Failure\"}}}";
					sb = prepareIRResponse(totLines,"","Failure", "101020", "Request is being processed..");
					failure++;
					logger.info("Response = "+responseJson);
					return sb;
				}
			}
			
			
			//log transaction
			if(transaction == null){
				transaction = logIssuanceTransactionRequest(jsonRequest.getLOYALTYISSUANCEREQ(), requestJson, "online", docSId);
			}
			
			BaseRequestObject requestObject = new BaseRequestObject();
			requestObject.setJsonValue(requestJson);
			requestObject.setAction(OCConstants.LOYALTY_SERVICE_ACTION_ISSUANCE);
			
			Date date = transaction.getRequestDate().getTime();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
			String transDate = df.format(date);
			requestObject.setTransactionId(""+transaction.getId());
			requestObject.setTransactionDate(transDate);
			
			
			BaseService baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.ASYNC_LOYALTY_ISSUANCE_BUSINESS_SERVICE);
			BaseResponseObject responseObject = baseService.processRequest(requestObject);
			logger.info("JSON Response: = "+responseObject.getJsonValue());

			LoyaltyIssuanceJsonResponse jsonResponseObject = gson.fromJson(responseObject.getJsonValue(), 
					LoyaltyIssuanceJsonResponse.class);
			//changes w.r.t migration
			/*if(responseObject.getResponseObject() != null && 
					!OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equals(responseObject.getResponseObject().toString())) {*/

			updateIssuanceTransactionStatus(transaction, responseObject.getJsonValue(), jsonResponseObject.getLOYALTYISSUANCERESPONSE());
			//}

			/*Long transChildId = null;
			try{
				if(jsonResponseObject.getLOYALTYISSUANCERESPONSE().getSTATUS()!=null  && jsonResponseObject.getLOYALTYISSUANCERESPONSE().getSTATUS().getERRORCODE().equalsIgnoreCase("0")){

					LoyaltyTransactionChildDao loyaltyTransactionChildDao = null;
					loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionChildDao");
					LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = null;
					loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionChildDaoForDML");
					LoyaltyTransactionChild loyaltyTransactionChild = loyaltyTransactionChildDao.getTransactionChild(user.getUserId(), transaction.getId());
					if(loyaltyTransactionChild != null){
						transChildId = loyaltyTransactionChild.getTransChildId();
						if(transChildId != null){
							loyaltyTransactionChildDaoForDML.updateCreatedDateForBulkTransactions(user.getUserId(), transChildId, cal);
						}
					}
				}
			}catch(Exception e){
				logger.info("Exception while updating created dates in child::", e);
			} */

			/*if(responseObject.getResponseObject() != null && 
					OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equals(responseObject.getResponseObject().toString())) {
				LoyaltyTransactionDao loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
				    transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
				    logger.debug("responseJson ======"+responseObject.getJsonValue());
				    transaction.setJsonResponse(responseObject.getJsonValue());
				    loyaltyTransactionDao.saveOrUpdate(transaction);

			}*/

			if(responseObject.getAction().equals(OCConstants.LOYALTY_SERVICE_ACTION_ISSUANCE)){
				String responseJson = responseObject.getJsonValue();
				sb = prepareIRResponse(totLines, jsonRequest.getLOYALTYISSUANCEREQ().getISSUANCEINFO().getCARDNUMBER(), jsonResponseObject.getLOYALTYISSUANCERESPONSE().getSTATUS().getSTATUS(), jsonResponseObject.getLOYALTYISSUANCERESPONSE().getSTATUS().getERRORCODE(), jsonResponseObject.getLOYALTYISSUANCERESPONSE().getSTATUS().getMESSAGE());
				if(responseJson.contains("Failure")){
					failure++;
				}
				logger.info("Response = "+responseJson);
				logger.info("Completed Loyalty Issuance Rest Service.");
				//return null;
				return sb;
			}
		}catch(Exception e){
			logger.error("Error in issuancerestservice", e);
			String responseJson = "{\"LOYALTYISSUANCERESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"101000\",\"MESSAGE\":\"Server error  101000.\",\"STATUS\":\"Failure\"}}}";
			StringBuffer sb = new StringBuffer();
			sb = prepareIRResponse(totLines,"","Failure", "101000", "Server error");
			failure++;
			logger.info("Response = "+responseJson);
			return sb;
		}finally{
			logger.info("Completed Loyalty Issuance Rest Service.");
		}
		return null;
	
	}	
	public LoyaltyTransaction findIssuanceTransactionBy(String userDetail, String requestId, String docSId){
		LoyaltyTransaction transaction = null;
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			//transaction = loyaltyTransactionDao.findByRequestIdAndType(userDetail, requestId, docSId, OCConstants.LOYALTY_TRANSACTION_ISSUANCE);
			transaction = loyaltyTransactionDao.findByDocSIdRequestIdAndType(userDetail, requestId, docSId, OCConstants.LOYALTY_TRANSACTION_ISSUANCE);
		}catch(Exception e){
			logger.error("Exception in find transaction by requestid", e);
		}
		return transaction;
	}
	
	public LoyaltyTransaction logIssuanceTransactionRequest(LoyaltyIssuanceRequestObject requestObject, String jsonRequest, String mode, String docSid){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		LoyaltyTransaction transaction = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction = new LoyaltyTransaction();
			transaction.setJsonRequest(jsonRequest);
			transaction.setStoreNumber(requestObject.getHEADERINFO().getSTORENUMBER());
			transaction.setRequestId(requestObject.getHEADERINFO().getREQUESTID());
			transaction.setPcFlag(Boolean.valueOf(requestObject.getHEADERINFO().getPCFLAG()));
			transaction.setMode(mode);//online or offline
			transaction.setRequestDate(Calendar.getInstance());
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
			transaction.setType(OCConstants.LOYALTY_TRANSACTION_ISSUANCE);
			transaction.setDocSID(docSid != null && 
					!docSid.trim().isEmpty() ? docSid.trim() : null);
			transaction.setUserDetail(requestObject.getUSERDETAILS().getUSERNAME()+"__"+requestObject.getUSERDETAILS().getORGANISATION());
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
			
		} catch (Exception e) {
			logger.error("Exception in logging transaction", e);
		}
		return transaction;
	}
	
	public void updateIssuanceTransactionStatus(LoyaltyTransaction transaction, String responseJson, LoyaltyIssuanceResponseObject response){
		AsyncLoyaltyTrxDao asyncLoyaltyTrxDao = null;
		AsyncLoyaltyTrxDaoForDML asyncLoyaltyTrxDaoForDML = null;
		AsyncLoyaltyTrx asyncLoyaltyTrx = null;
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			 
			  transaction = findTransaction(transaction.getId());
			  if(transaction.getLoyaltyServiceType() != null && OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equals(transaction.getLoyaltyServiceType())) {
				  
				  transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
				  logger.debug("responseJson ======"+responseJson);
				  transaction.setJsonResponse(responseJson);
				  transaction.setCardNumber(response.getISSUANCEINFO().getCARDNUMBER());
				  //loyaltyTransactionDao.saveOrUpdate(transaction);
				  loyaltyTransactionDaoForDML.saveOrUpdate(transaction);

				  
			  }else{
				  if(response.getSTATUS().getERRORCODE().equalsIgnoreCase("0")){
						asyncLoyaltyTrxDao    = (AsyncLoyaltyTrxDao) ServiceLocator.getInstance().getDAOByName(OCConstants.ASYNC_LOYALTY_TRX_DAO);
						asyncLoyaltyTrxDaoForDML    = (AsyncLoyaltyTrxDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.ASYNC_LOYALTY_TRX_DAO_FOR_DML);
						asyncLoyaltyTrx = new AsyncLoyaltyTrx();
						asyncLoyaltyTrx.setCreatedTime(MyCalendar.getInstance());
						asyncLoyaltyTrx.setLoyaltyTransaction(transaction);
						asyncLoyaltyTrx.setStatus(OCConstants.ASQ_STATUS_NEW);
						asyncLoyaltyTrx.setTrxType(OCConstants.LOYALTY_ISSUANCE);
						//asyncLoyaltyTrxDao.saveOrUpdate(asyncLoyaltyTrx);
						asyncLoyaltyTrxDaoForDML.saveOrUpdate(asyncLoyaltyTrx);
					}else{
						
						//loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
						transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
						logger.debug("responseJson ======"+responseJson);
						transaction.setJsonResponse(responseJson);
						transaction.setCardNumber(response.getISSUANCEINFO().getCARDNUMBER());
						//loyaltyTransactionDao.saveOrUpdate(transaction);
						loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
						
					}
			  }
			
		}catch(Exception e){
			logger.error("Exception in updating transaction", e);
		}
	}
	
	private LoyaltyIssuanceRequestObject prepareIssuanceRequest (HeaderInfo headerInfo, UserDetails userDetails1, IssuanceInfo issuanceInfo) {
		LoyaltyIssuanceRequestObject issuanceRequest = new LoyaltyIssuanceRequestObject();
		issuanceRequest.setHEADERINFO(headerInfo);
		issuanceRequest.setUSERDETAILS(userDetails1);
		issuanceRequest.setISSUANCEINFO(issuanceInfo);
		return issuanceRequest;

	}
	private StringBuffer  asyncLoyaltyRedemptionRestService(LoyaltyRedemptionJsonRequest jsonRequestObject, Users user, int totLines){

		logger.info("Entered Loyalty Redemption Rest Service.");
		try{
			
			Gson gson = new Gson();
			String requestJson = gson.toJson(jsonRequestObject);
			StringBuffer sb = new StringBuffer();
			logger.info("JSON Request: = "+requestJson);

			LoyaltyRedemptionJsonRequest jsonRequest = null;
			
			try{
				jsonRequest = gson.fromJson(requestJson, LoyaltyRedemptionJsonRequest.class);
			}catch(Exception e){
				String responseJson = "{\"LOYALTYISSUANCERESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"101002\",\"MESSAGE\":\"Error \"101002\": Invalid request.\",\"STATUS\":\"Failure\"}}}";
				sb = prepareIRResponse(totLines,"","Failure", "101002", "Invalid Request");
				failure++;
				logger.info("Response = "+responseJson);
				//return null;
				return sb;
			}
			BaseRequestObject requestObject = new BaseRequestObject();
			requestObject.setJsonValue(requestJson);
			requestObject.setAction(OCConstants.LOYALTY_SERVICE_ACTION_REDEMPTION);
			
			BaseService baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_REDEMPTION_BUSINESS_SERVICE);
			BaseResponseObject responseObject = baseService.processRequest(requestObject);
			
			String requestId = jsonRequest.getLOYALTYREDEMPTIONREQ().getHEADERINFO().getREQUESTID();
			LoyaltyTransaction transaction = null;
			transaction = findTransactionByRequestId(requestId);
			LoyaltyRedemptionJsonResponse jsonResponseObject = gson.fromJson(responseObject.getJsonValue(), LoyaltyRedemptionJsonResponse.class);
			logger.info("JSON Response: = "+responseObject.getJsonValue());
			
			/*Long transChildId = null;
			try{
				if(jsonResponseObject.getLOYALTYREDEMPTIONRESPONSE().getSTATUS()!=null  && jsonResponseObject.getLOYALTYREDEMPTIONRESPONSE().getSTATUS().getERRORCODE().equalsIgnoreCase("0")){

					LoyaltyTransactionChildDao loyaltyTransactionChildDao = null;
					loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionChildDao");
					LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = null;
					loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionChildDaoForDML");
					LoyaltyTransactionChild loyaltyTransactionChild = loyaltyTransactionChildDao.getTransactionChild(user.getUserId(), transaction.getId());
					if(loyaltyTransactionChild != null){
						transChildId = loyaltyTransactionChild.getTransChildId();
						if(transChildId != null){
							loyaltyTransactionChildDaoForDML.updateCreatedDateForBulkTransactions(user.getUserId(), transChildId, cal);
						}
					}
				}
			}catch(Exception e){
				logger.info("Exception while updating created dates in child::", e);
			} */
			if(responseObject.getAction().equals(OCConstants.LOYALTY_SERVICE_ACTION_REDEMPTION)){
				String responseJson = responseObject.getJsonValue();
				sb = prepareIRResponse(totLines, jsonRequest.getLOYALTYREDEMPTIONREQ().getREDEMPTIONINFO().getCARDNUMBER(), jsonResponseObject.getLOYALTYREDEMPTIONRESPONSE().getSTATUS().getSTATUS(), jsonResponseObject.getLOYALTYREDEMPTIONRESPONSE().getSTATUS().getERRORCODE(), jsonResponseObject.getLOYALTYREDEMPTIONRESPONSE().getSTATUS().getMESSAGE());
				if(responseJson.contains("Failure")){
					failure++;
				}
				logger.info("Response = "+responseJson);
				logger.info("Completed Loyalty Redemption Rest Service.");
				//return null;
				return sb;
			}
		}catch(Exception e){
			logger.error("Error in redemption rest service", e);
			StringBuffer sb = new StringBuffer();
			String responseJson = "{\"LOYALTYREDEMPTIONRESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"101000\",\"MESSAGE\":\"Server error  101000.\",\"STATUS\":\"Failure\"}}}";
			sb = prepareIRResponse(totLines,"","Failure", "101000", "Server Error");
			failure++;
			logger.info("Response = "+responseJson);
			return sb;
		}finally{
			logger.info("Completed Loyalty redemption Rest Service.");
		}
		return null;
	
		}
	
	private LoyaltyRedemptionRequestObject prepareRedemptionRequest (HeaderInfo headerInfo, UserDetails userDetails1, RedemptionInfo redemptionInfo) {
		LoyaltyRedemptionRequestObject redemptionRequest = new LoyaltyRedemptionRequestObject();
		redemptionRequest.setHEADERINFO(headerInfo);
		redemptionRequest.setUSERDETAILS(userDetails1);
		redemptionRequest.setREDEMPTIONINFO(redemptionInfo);
		return redemptionRequest;

	}

	
	private void setUserOrg(Listbox orgLbId) {

		
		List<UserOrganization> orgList	= usersDao.findAllOrganizations();
		
		if(orgList == null) {
			logger.debug("no organization list exist from the DB...");
			return ;
		}
		
		Listitem tempList = new Listitem(allStr);
		tempList.setParent(orgLbId);
		
		Listitem tempItem = null;
		
		for (UserOrganization userOrganization : orgList) {
			
			//set Organization Name
			if(userOrganization.getOrganizationName() == null || userOrganization.getOrganizationName().trim().equals("")) continue;
			
			tempItem = new Listitem(userOrganization.getOrgExternalId().trim(),userOrganization.getUserOrgId());
			tempItem.setParent(orgLbId);
		} // for
		orgLbId.setSelectedIndex(0);
	}//setUserOrg
	
private List<Users> usersList=null;
	
	
	/**
	 * this method fetches the 'Users' objects from DB 
	 */
	public List<Users> getUsers() {
		logger.debug("---- Just Entered ----"+ userListLbId);
		
		if(usersList != null) {
			return usersList;
		}
		
		if(usersDao == null) {
			usersDao=(UsersDao)SpringUtil.getBean("usersDao");
		}
		try {
			logger.debug("---- Getting the Users ----");
			usersList=usersDao.findAll();
			usersMap.clear();
			
			for (Users tempUser : usersList) {
				usersMap.put(tempUser.getUserId(), tempUser.getUserName());
			} // for
				
			
		} catch (Exception e) {
			logger.error("** Exception occured while fetching the users :", e);
		}//catch
		
		return usersList;
	}//getUsers()
	
	/**
	 * this method allows to get the campaign reports based on the selected user
	 */
	
	/*public void onSelect$userListLbId() {
		
		logger.debug("----just entered----");
		dtldRepsGbId.setVisible(false);
		recentCampListLbId.setItemRenderer(renderer);
		recentCampListLbId.setModel(new ListModelList(getCampReports()));
		
	}//onSelect$userListLbId()*/
	
	public void onSelect$userOrgLbId() {
		Components.removeAllChildren(userNameLbId);
		dtldRepsGbId.setVisible(false);

		if(userOrgLbId.getSelectedItem().getLabel().equals(allStr)) {
			Listitem tempList = new Listitem(allStr);
			tempList.setParent(userNameLbId);
			userNameLbId.setSelectedIndex(0);
			recentCampListLbId.setModel(new ListModelList(getCampReports()));
			return;
		}
		List<Users> usersList = usersDao.getUsersByOrgIdAndUserId((Long)userOrgLbId.getSelectedItem().getValue(), null); 
		
		if(usersList == null || usersList.size() == 0) {
			logger.debug("No users exists for the Selected Organization..");
			Listitem tempList = new Listitem(allStr);
			tempList.setParent(userNameLbId);
			recentCampListLbId.setModel(new ListModelList(usersList));
			return;
		}
		
		Listitem tempList = new Listitem(allStr);
		tempList.setParent(userNameLbId);
		
		Listitem tempItem = null;
		for (Users users : usersList) {
			String userNameStr = Utility.getOnlyUserName(users.getUserName());
			
			tempItem = new Listitem(userNameStr,users);
			tempItem.setParent(userNameLbId);
		} // for
		
		if(userNameLbId.getItemCount() > 0) {
			logger.debug("usersListBxId count is .."+userNameLbId.getItemCount());
			userNameLbId.setSelectedIndex(0);
		}
		recentCampListLbId.setModel(new ListModelList(getCampReports()));
	}//onSelect$userOrgLbId
	
	
	public void onSelect$userNameLbId() {
		dtldRepsGbId.setVisible(false);
		recentCampListLbId.setModel(new ListModelList(getCampReports()));
	}//onSelect$userNameLbId
	
	/**
	 * this method allows to get required number of campaign reports from DB
	 */
	
	public void onSelect$campRepSelectLbId() {
		dtldRepsGbId.setVisible(false);
		String selUserOrg = userOrgLbId.getSelectedItem().getLabel();
		if(!selUserOrg.equals(allStr) && !(userNameLbId.getItemCount()>1)){
			return;
		}
		recentCampListLbId.setModel(new ListModelList(getCampReports()));
		
	}//onSelect$campRepSelectLbId() 
	
	/**
	 * this method fetches the 'CampaignReport' objects from DB
	 */
	
	public List<CampaignReport> getCampReports() {
		logger.debug("in getCampReports()");
		List<CampaignReport> campReportsList=null;
		if(campaignReportDao == null) {
			campaignReportDao = (CampaignReportDao)SpringUtil.getBean("campaignReportDao");
		}// if
		try {
			String numOfReports=campRepSelectLbId.getSelectedItem().getLabel();
			int numberOfReports=Integer.parseInt(numOfReports);
			
			
			
			String selUserOrg = userOrgLbId.getSelectedItem().getLabel();
			Long selUserOrgId = null;
			if(!selUserOrg.equals(allStr) ) {
				selUserOrgId = userOrgLbId.getSelectedItem().getValue();
				
			}
			
			String selUserName = userNameLbId.getSelectedItem().getLabel();
		
			Long selUserId = null;
			if( !selUserName.equals(allStr) ) {
				selUserId = userNameLbId.getSelectedItem().getValue() == null ? null : ((Users) userNameLbId.getSelectedItem().getValue()).getUserId();
			}

			if(selUserOrgId == null) {

				campReportsList=campaignReportDao.findBynumberOfReports( 0, numberOfReports);
			} // if
			else {
				
//				Users user=(Users)userListLbId.getSelectedItem().getValue();
				campReportsList=campaignReportDao.findByOrgIdAndUserId(selUserOrgId, selUserId, 0, numberOfReports);
				
			}//else
		
		} catch (Exception e) {
			logger.error("** Exception occured while fetching the campaign reports :", e);
		}//catch
		
		return campReportsList;
	}//getCampReports
	
	
	
	
	/**
	 * this method allows to get the campaigns based on the selected user
	 */
	/*public void onSelect$campUserListLbId() {
		logger.debug("---just entered---");
		viewCampListLbId.setItemRenderer(renderer);
		viewCampListLbId.setModel(new ListModelList(getCampaigns()));
	}*/
	
	public void onSelect$campUserOrgLbId() {
		logger.debug("---just entered---");
		Components.removeAllChildren(campUserNameLbId);

		if(campUserOrgLbId.getSelectedItem().getLabel().equals(allStr)) {
			Listitem tempList = new Listitem(allStr);
			tempList.setParent(campUserNameLbId);
			campUserNameLbId.setSelectedIndex(0);
			viewCampListLbId.setModel(new ListModelList(getCampaigns()));
			return;
		}
		List<Users> usersList = usersDao.getUsersByOrgIdAndUserId((Long)campUserOrgLbId.getSelectedItem().getValue(), null); 
		
		if(usersList == null || usersList.size() == 0) {
			logger.debug("No users exists for the Selected Organization..");
			Listitem tempList = new Listitem(allStr);
			tempList.setParent(campUserNameLbId);
			viewCampListLbId.setModel(new ListModelList(usersList));
			return;
		}
		
		Listitem tempList = new Listitem(allStr);
		tempList.setParent(campUserNameLbId);
		
		Listitem tempItem = null;
		for (Users users : usersList) {
			String userNameStr = Utility.getOnlyUserName(users.getUserName());
			
			tempItem = new Listitem(userNameStr,users);
			tempItem.setParent(campUserNameLbId);
		} // for
		
		if(campUserNameLbId.getItemCount() > 0) {
			logger.debug("usersListBxId count is .."+campUserNameLbId.getItemCount());
			campUserNameLbId.setSelectedIndex(0);
		}
		viewCampListLbId.setModel(new ListModelList(getCampaigns()));
	}
	
	
	public void onSelect$campUserNameLbId() {
		viewCampListLbId.setModel(new ListModelList(getCampaigns()));
	}
	
	
	/**
	 * this method allows to get required number of campaigns  from DB
	 */
	
	public void onSelect$campSelectLbId() {
		
		logger.debug("************* onselect on**********"+campSelectLbId);
		String selUserOrg = campUserOrgLbId.getSelectedItem().getLabel();
		if(!selUserOrg.equals(allStr) && !(campUserNameLbId.getItemCount()>1)){
			return;
		}
		viewCampListLbId.setModel(new ListModelList(getCampaigns()));
		
	}
	
	/**
	 * this method fetches the latest 'Campaigns' From DB
	 */
	
	public List getCampaigns() { 
		logger.debug("-- Just Entered in latest campaigns -- ");
		
		List<Campaigns> campaignsList =null;
		
		if(campaignsDao==null) {
			campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
		} 
		try {
			String numOfCamps=campSelectLbId.getSelectedItem().getLabel();
			logger.debug("the no of campaigns====>"+numOfCamps);
			int numberOfCampaigns=Integer.parseInt(numOfCamps);
			
			String selUserOrg = campUserOrgLbId.getSelectedItem().getLabel();
			Long selUserOrgId = null;
			if(!selUserOrg.equals(allStr) ) {
				selUserOrgId = campUserOrgLbId.getSelectedItem().getValue();
			}
			
			String selUserName = campUserNameLbId.getSelectedItem().getLabel();
			
			Long selUserId = null;
			if(!selUserName.equals(allStr)  ) {
				selUserId = campUserNameLbId.getSelectedItem().getValue() == null ? null : ((Users) campUserNameLbId.getSelectedItem().getValue()).getUserId();
			}
			
			if(selUserOrgId == null) {
				campaignsList=campaignsDao.getLatestCampaings(0, numberOfCampaigns);

			} else {
//				Users user=(Users)campUserListLbId.getSelectedItem().getValue();
				campaignsList = campaignsDao.findByOrgIdAndUserId(selUserOrgId, selUserId, 0, numberOfCampaigns);
			}// else
		
			
		} catch(Exception e) {
			logger.error("** Exception occured while fetching the campaigns :", e);
		}//catch
		
		 return campaignsList;
	}
	
	/**
	 * this method allows to get the mailinglists based on the selected user
	 */
	/*public void onSelect$mlistUserListLbId() {
		logger.debug("---just entered---");
		customersLbId.setItemRenderer(renderer);
		customersLbId.setModel(new ListModelList(getMailingLists()));
		
	}*/
	
	public void onSelect$mlUserOrgLbId() {
		logger.debug("---just entered---");
		Components.removeAllChildren(mlUserNameLbId);

		if(mlUserOrgLbId.getSelectedItem().getLabel().equals(allStr)) {
			Listitem tempList = new Listitem(allStr);
			tempList.setParent(mlUserNameLbId);
			mlUserNameLbId.setSelectedIndex(0);
			customersLbId.setModel(new ListModelList(getMailingLists()));
			return;
		}
		List<Users> usersList = usersDao.getUsersByOrgIdAndUserId((Long)mlUserOrgLbId.getSelectedItem().getValue(), null); 
		
		if(usersList == null || usersList.size() == 0) {
			logger.debug("No users exists for the Selected Organization..");
			Listitem tempList = new Listitem(allStr);
			tempList.setParent(mlUserNameLbId);
			customersLbId.setModel(new ListModelList(usersList));
			return;
		}
		
		String allUserIdForOrgId = Constants.STRING_NILL ;
//		List<Users> allUsersForOrgId = usersDao.getUsersListByOrg((Long)mlUserOrgLbId.getSelectedItem().getValue());
		for (Users users : usersList) {
			
			if(allUserIdForOrgId.trim().length() > 0) allUserIdForOrgId += Constants.DELIMETER_COMMA;
			allUserIdForOrgId += users.getUserId().toString();
			
		}
		
		
		Listitem tempItem =  new Listitem(allStr, allUserIdForOrgId);
		tempItem.setParent(mlUserNameLbId);
		
		for (Users users : usersList) {
			String userNameStr = Utility.getOnlyUserName(users.getUserName());
			
			tempItem = new Listitem(userNameStr,users);
			tempItem.setParent(mlUserNameLbId);
		} // for
		
		if(mlUserNameLbId.getItemCount() > 0) {
			logger.debug("usersListBxId count is .."+mlUserNameLbId.getItemCount());
			mlUserNameLbId.setSelectedIndex(0);
		}
		customersLbId.setModel(new ListModelList(getMailingLists()));
	}
	
	
	public void onSelect$mlUserNameLbId() {
		customersLbId.setModel(new ListModelList(getMailingLists()));
	}//onSelect$mlUserNameLbId
	
	
	
	/**
	 * this method allows to get required number of campaign mailing lists from DB
	 */
	public void onSelect$mlSelectedLbId() {
		String selUserOrg = mlUserOrgLbId.getSelectedItem().getLabel();
		if(!selUserOrg.equals(allStr) && !(mlUserNameLbId.getItemCount()>1)){
			return;
		}
		customersLbId.setModel(new ListModelList(getMailingLists()));
		
	}//onSelect$mlSelectedLbId
	
	/**
	 * this methods fetches the 'MailingLists' from DB
	 */
	
	public List<MailingList> getMailingLists() { 
		
		logger.debug("-----just entered-----");
		
		List<MailingList> mailingLists=null;
		
		if(mailingListDao==null) {
			mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
		}
		try {
			
			String numOfmlists=mlSelectedLbId.getSelectedItem().getLabel();
			int numberOfMlLists=Integer.parseInt(numOfmlists);
			
			String selUserOrg = mlUserOrgLbId.getSelectedItem().getLabel();
			Long selUserOrgId = null;
			String selUserId = null;
			if(!selUserOrg.equals(allStr) ) {
				
				selUserOrgId = mlUserOrgLbId.getSelectedItem().getValue();
				String selUserName = mlUserNameLbId.getSelectedItem().getLabel();
				
				if(selUserName.equals(allStr) ) {
					selUserId =  mlUserNameLbId.getSelectedItem().getValue(); 
				}
				else {
					selUserId =  ((Users) mlUserNameLbId.getSelectedItem().getValue()).getUserId().toString();
				}
			}
			
			
			if(selUserOrgId == null) {
				mailingLists=mailingListDao.getLatestMailingLists(0, numberOfMlLists);
				
				logger.debug("the number of retrieved mailinglists===> "+mailingLists.size());
					
			} else {
//				Users user = (Users)mlUserNameLbId.getSelectedItem().getValue();
				mailingLists=mailingListDao.getLatestMailingLists(selUserId,0, numberOfMlLists);
				logger.debug("the number of retrieved mailinglists for selected user===> "+mailingLists.size());
			}
			
		} catch (Exception e) {
			logger.error("Exception  ::", e);
		}
		
		return mailingLists;
	}//getMailingLists()
	
	
	/**
	 * this method allows to get the campaigns based on the selected user
	 */
	/*public void onSelect$campScheduleUserListLbId() {
		logger.debug("---just entered---");
		viewCampSchedulesListLbId.setItemRenderer(renderer);
		viewCampSchedulesListLbId.setModel(new ListModelList(getCampSchedules()));
	}//onSelect$campScheduleUserListLbId()
*/	
	
	public void onSelect$campScheduleUserOrgLbId() {
		logger.debug("---just entered---");
		Components.removeAllChildren(campScheduleUserNameLbId);

		if(campScheduleUserOrgLbId.getSelectedItem().getLabel().equals(allStr)) {
			Listitem tempList = new Listitem(allStr);
			tempList.setParent(campScheduleUserNameLbId);
			campScheduleUserNameLbId.setSelectedIndex(0);
			viewCampSchedulesListLbId.setModel(new ListModelList(getCampSchedules()));
			return;
		}
		List<Users> usersList = usersDao.getUsersByOrgIdAndUserId((Long)campScheduleUserOrgLbId.getSelectedItem().getValue(), null); 
		
		if(usersList == null || usersList.size() == 0) {
			logger.debug("No users exists for the Selected Organization..");
			Listitem tempList = new Listitem(allStr);
			tempList.setParent(campScheduleUserNameLbId);
			viewCampSchedulesListLbId.setModel(new ListModelList(usersList));
			return;
		}
		
		Listitem tempList = new Listitem(allStr);
		tempList.setParent(campScheduleUserNameLbId);
		
		Listitem tempItem = null;
		for (Users users : usersList) {
			String userNameStr = Utility.getOnlyUserName(users.getUserName());
			
			tempItem = new Listitem(userNameStr,users);
			tempItem.setParent(campScheduleUserNameLbId);
		} // for
		
		if(campScheduleUserNameLbId.getItemCount() > 0) {
			logger.debug("usersListBxId count is .."+campScheduleUserNameLbId.getItemCount());
			campScheduleUserNameLbId.setSelectedIndex(0);
		}
		viewCampSchedulesListLbId.setModel(new ListModelList(getCampSchedules()));
	}//onSelect$campScheduleUserOrgLbId
	
	
	public void onSelect$campScheduleUserNameLbId() {
		viewCampSchedulesListLbId.setModel(new ListModelList(getCampSchedules()));
	}//onSelect$campScheduleUserNameLbId
	
	
	/**
	 * this method allows to get required number of campaigns Schedules   from DB
	 */

		public void onSelect$campSchedulesSelectLbId() {
		
		logger.debug("************* onselect on**********"+campSchedulesSelectLbId);
//		viewCampSchedulesListLbId.setItemRenderer(renderer);
		String selUserOrg = campScheduleUserOrgLbId.getSelectedItem().getLabel();
		if(!selUserOrg.equals(allStr) && !(campScheduleUserNameLbId.getItemCount()>1)){
			return;
		}
		viewCampSchedulesListLbId.setModel(new ListModelList(getCampSchedules()));
		
		}
		
		public void onSelect$campSchedulesStatusLbId() {
			logger.debug("---just entered 1---");
			
			List<CampaignSchedule> tempList = getCampSchedules();
			
			if(tempList  != null) {
				logger.info("tempList size is :"+tempList.size());
				viewCampSchedulesListLbId.setModel(new ListModelList(tempList));
//				viewCampSchedulesListLbId.setItemRenderer(renderer);
			}
			
		}//onSelect$campScheduleUserListLbId()
	
		public List<CampaignSchedule> getCampSchedules() {
		
		logger.debug("in getCampSchedules()");
		List<CampaignSchedule> campScheduleList=null;
		if(campaignScheduleDao == null) {
			campaignScheduleDao = (CampaignScheduleDao)SpringUtil.getBean("campaignScheduleDao");
		}// if
		try {
			String numOfReports=campSchedulesSelectLbId.getSelectedItem().getLabel();
			logger.info("no of schedules is "+numOfReports);
			int numberOfReports=Integer.parseInt(numOfReports);

			String selUserOrg = campScheduleUserOrgLbId.getSelectedItem().getLabel();
			Long selUserOrgId = null;
			if(!selUserOrg.equals(allStr) ) {
				selUserOrgId = campScheduleUserOrgLbId.getSelectedItem().getValue();
			}
			
			String selUserName = campScheduleUserNameLbId.getSelectedItem().getLabel();
			
			Long selUserId = null;
			if(!selUserName.equals(allStr)  ) {
				selUserId = campScheduleUserNameLbId.getSelectedItem().getValue() == null ? null : ((Users) campScheduleUserNameLbId.getSelectedItem().getValue()).getUserId();
			}
			
			String  status =campSchedulesStatusLbId.getSelectedItem().getValue();
			
			if(selUserOrgId == null) {
				campScheduleList=campaignScheduleDao.findBynumberOfSchedules( 0, numberOfReports,status);

			} else {
				campScheduleList = campaignScheduleDao.findByOrgIdAndUserId(selUserOrgId, selUserId, 0, numberOfReports,status);
			}// else
			
			/*if((campScheduleUserListLbId.getSelectedItem().getLabel().equalsIgnoreCase("--All--") && (campSchedulesStatusLbId.getSelectedItem().getLabel()).equalsIgnoreCase("--All--"))) {

				campScheduleList=campaignScheduleDao.findBynumberOfSchedules( 0, numberOfReports);
				logger.info(" size of list of all is "+campScheduleList.size());
			} // if
			else {
				Long userId= null;
				if(campScheduleUserListLbId.getSelectedIndex() > 0){
					
					userId = ((Users)campScheduleUserListLbId.getSelectedItem().getValue()).getUserId();
				}
				if(campSchedulesStatusLbId.getSelectedIndex() > 0 ) {
					status =campSchedulesStatusLbId.getSelectedItem().getValue();
				}
				campScheduleList=campaignScheduleDao.findByUserAndStatus(userId, 0, numberOfReports,status);
				logger.info(" size of list of selected  is "+campScheduleList.size());
			}//else
*/		
		} catch (Exception e) {
			logger.error("** Exception occured while fetching the campaign Schedule :", e);
		}//catch
		
		logger.info("campScheduleList size is "+campScheduleList);
		return campScheduleList;
	}//getCampSchedules()
	


	
	
	
	
	
	/**
	 * this method gets the user details
	 * 
	 */
	
	public List<Users> getUserDetails(){
		try {
			logger.info("the active users in dash board are===>"+ActiveUsers.activeUsersMap.size());
			List<Users> usersList = new ArrayList<Users>();
			
			
			Map<String, Users> tempMap = ActiveUsers.activeUsersMap;
			Set<String> tempIds = tempMap.keySet();
			
			for (String sesId : tempIds) {
				logger.info("the session id iss====>"+sesId);
				logger.info("the name of the user is===>"+tempMap.get(sesId));
				usersList.add(tempMap.get(sesId));
				
			}
			/*try{
			
				logger.debug("-- just entered --");


			if(usersDao == null) {
				usersDao=(UsersDao)SpringUtil.getBean("usersDao");
			}
			
			if(usersList == null) {
				usersList=usersDao.findAll();
			}
			
			Users ulist=null;
			
			 Calendar tempCal;
			 for(Object object : usersList ){
				 TimeZone tz =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
				 ulist=(Users)object;
				 tempCal=ulist.getPackageExpiryDate();
				 
				 ulist.setPackageExpiryDate(new MyCalendar(tempCal, tz ,MyCalendar.FORMAT_STDATE ));
			  	}//for

			}catch(Exception e){
				logger.error("Exception  ::", e);
				logger.info("exception  here");
			}//catch
			*/
			Users ulist=null;
			Calendar tempCal;
			 for(Object object : usersList ){
				 TimeZone tz =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
				 ulist=(Users)object;
				 tempCal=ulist.getPackageExpiryDate();
				 
				 ulist.setPackageExpiryDate(new MyCalendar(tempCal, tz ,MyCalendar.FORMAT_STDATE ));
//				 logger.info("the name of the user is=====>"+ulist.getUserName());
			  	}//for
		
			
			return usersList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception  ::", e);
			return null;
		}
	} // getUserDetails()
	
	
	/*****************************************************OPENS and CLICKS************************************************************/
	/**
	 * this method retrieves the opens/clicks based on the selected domain
	 */
	public void onSelect$DomainsLbId() {
		logger.debug("----just entered----");
		long crId=((CampaignReport)recentCampListLbId.getSelectedItem().getAttribute("campaignReport")).getCrId();
		sntCntLabelId.setValue(getSentCount(crId)+" / "+((CampaignReport)recentCampListLbId.getSelectedItem().getAttribute("campaignReport")).getSent());
		viewDtldRepLbId.setItemRenderer(renderer);
		viewDtldRepLbId.setModel(new ListModelList(getDetailedReports(crId)));
		
	}//onSelect$DomainsLbId()
	
	/**
	 * this method allows to get required number of opens/clicks reports from DB
	 */
	
	public void onSelect$numOfFetchLatestRepLbId() {
		
		logger.debug("----just entered----");
		viewDtldRepLbId.setItemRenderer(renderer);
		viewDtldRepLbId.setModel(new ListModelList(getDetailedReports(((CampaignReport)recentCampListLbId.getSelectedItem().getAttribute("campaignReport")).getCrId())));
		
	}//onSelect$numOfFetchLatestRepLbId()
	
	/**
	 * this method fetches the opens/clicks for selected campaignreport
	 */
	public void onSelect$recentCampListLbId() {
		MessageUtil.clearMessage();
		List<Object[]> opensList=null;
		long crId=((CampaignReport)recentCampListLbId.getSelectedItem().getAttribute("campaignReport")).getCrId();
		sntCntLabelId.setValue(getSentCount(crId)+" / "+((CampaignReport)recentCampListLbId.getSelectedItem().getAttribute("campaignReport")).getSent());
		logger.debug(recentCampListLbId.getSelectedItem().getAttribute("campaignReport"));
		opensList = getDetailedReports(crId);
		if(opensList.size() == 0) {
			dtldRepsGbId.setVisible(false);
			//MessageUtil.setMessage("No opens/clicks for this campaign.", "color:red", "top");
			return;
		} else {
			dtldRepsGbId.setVisible(true);
			viewDtldRepLbId.setItemRenderer(renderer);
			viewDtldRepLbId.setModel(new ListModelList(opensList));
		}
		
	}//onSelect$recentCampListLbId()
	
	public String getSentCount(long crId) {
		
		String count=null;
		try{
			if(campaignSentDao==null) {
				
				campaignSentDao=(CampaignSentDao)SpringUtil.getBean("campaignSentDao");
				
			}
			count=campaignSentDao.getSentCountByDomain((String)DomainsLbId.getSelectedItem().getValue(),crId);
		}catch (Exception e) {
			logger.debug("Exception while fetching sentCount"+e);
		}
		return count;
	}//getSentCount(long crId)
	/**
	 * this method allows to set the list box 
	 */
	public void onSelect$fetchLatestRepLbId() {
		
		logger.debug("----just entered----");
		List<Listheader> lstheader=new ArrayList<Listheader>();
		try{
		if(fetchLatestRepLbId.getSelectedIndex()==1) {
			List lheader=viewDtldRepLbId.getFirstChild().getChildren();
			for(Object obj:lheader) {
				Listheader listheader=(Listheader)obj;
				lstheader.add(listheader);
			}//for
			
			lstheader.get(0).setLabel("Email Id");
			lstheader.get(1).setLabel("Click Date");
			lstheader.get(2).setLabel("ClickUrl");
			lstheader.get(3).setLabel("Status");
			
			viewDtldRepLbId.setItemRenderer(renderer);
			viewDtldRepLbId.setModel(new ListModelList(getDetailedReports(((CampaignReport)recentCampListLbId.getSelectedItem().getAttribute("campaignReport")).getCrId())));
			
			
		}//if
		else if(fetchLatestRepLbId.getSelectedIndex()==0){
			
			List lheader=viewDtldRepLbId.getFirstChild().getChildren();
			for(Object obj:lheader) {
				Listheader listheader=(Listheader)obj;
				lstheader.add(listheader);
			}
			
			logger.debug(lstheader.size());
			lstheader.get(0).setLabel("Email Id");
			lstheader.get(1).setLabel("Open Date");
			lstheader.get(2).setLabel("");
			lstheader.get(3).setLabel("Status");
			viewDtldRepLbId.setItemRenderer(renderer);
			viewDtldRepLbId.setModel(new ListModelList(getDetailedReports(((CampaignReport)recentCampListLbId.getSelectedItem().getAttribute("campaignReport")).getCrId())));
			
		   }//else if
		
		}catch (Exception e) {
			logger.error("Exception"+e);
		}
	}//onSelect$fetchLatestRepLbId()
	
	/**
	 * this mwthod fetches the opens/clicks from db
	 * @param crId
	 * @return
	 */
	
	public List<Object[]> getDetailedReports(long crId) {
		logger.debug("-----just entered----");
		List<Object[]> opensList=null;
	try {
		if(opensDao==null) {
			opensDao=(OpensDao)SpringUtil.getBean("opensDao");
		}
		String numOfReps=numOfFetchLatestRepLbId.getSelectedItem().getLabel();
		int maxResults=Integer.parseInt(numOfReps);
		logger.debug("getting the objects");
		int index=fetchLatestRepLbId.getSelectedIndex();
		logger.debug((String)DomainsLbId.getSelectedItem().getValue());
		opensList=opensDao.getOpensByCrId(crId,(String)DomainsLbId.getSelectedItem().getValue(),0,maxResults,index);
		
	 }catch (Exception e) {
		logger.error("Exception while fetching Opens/clicks object"+e);
	}
	 return opensList;
	}//getDetailedReports(long crId)
	
	/*****************************************************OPENS and CLICKS************************************************************/
	

	
	private static class MyRenderer implements ListitemRenderer, EventListener {

		MyRenderer() {
			super();
			logger.debug("new MyRenderer object is created");
		}//MyRenderer()
		
		public void render(Listitem item, java.lang.Object data,int arg2) {
			
			//logger.debug("----just entered----");
			
			TimeZone tz =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
			
			if(data instanceof CampaignReport) {
				CampaignReport campaignReport=(CampaignReport)data;
				
				item.setAttribute("campaignReport", campaignReport);
				Listcell lc=new Listcell();
				lc.setLabel(Utility.getOnlyUserName(campaignReport.getUser().getUserName()));
				lc.setParent(item);
				
				UsersDao usersDao=(UsersDao)SpringUtil.getBean("usersDao");
				UserOrganization userOrg = usersDao.findByOrgName(Utility.getOnlyOrgId(campaignReport.getUser().getUserName()));
				lc=new Listcell();
				lc.setLabel(userOrg.getOrganizationName());
				lc.setParent(item);
				
				lc=new Listcell();
				lc.setLabel(campaignReport.getCampaignName());
				lc.setParent(item);
				
				lc = new Listcell(MyCalendar.calendarToString(campaignReport.getSentDate(),
						MyCalendar.FORMAT_DATETIME_STDATE, tz));
				lc.setParent(item);
				
				lc=new Listcell();
				lc.setLabel(""+campaignReport.getSent());
				lc.setParent(item);
				
				lc=new Listcell();
				lc.setLabel(""+campaignReport.getOpens());
				lc.setParent(item);
				
				lc=new Listcell();
				lc.setLabel(""+campaignReport.getClicks());
				lc.setParent(item);
				
				lc=new Listcell();
				lc.setLabel(""+campaignReport.getBounces());
				lc.setParent(item);
				
				lc=new Listcell();
				Image img = new Image("/img/theme/home/reports_icon.png" );
				img.setTooltiptext("Get Reports");
				img.setStyle("margin-right:5px;cursor:pointer;");
				img.setAttribute("addEvent", "onCampReport");
				img.addEventListener("onClick", this);
				img.setParent(lc);
				lc.setParent(item);
				
			} else if(data instanceof MailingList) {
				
				MailingList mlist=(MailingList)data;
				item.setAttribute("mailingList", mlist);
				
				Listcell lc=new Listcell();
				lc.setLabel(Utility.getOnlyUserName(mlist.getUsers().getUserName()));
				lc.setParent(item);
				
				UsersDao usersDao=(UsersDao)SpringUtil.getBean("usersDao");
				UserOrganization userOrg = usersDao.findByOrgName(Utility.getOnlyOrgId(mlist.getUsers().getUserName()));
				lc=new Listcell();
				lc.setLabel(userOrg.getOrganizationName());
				lc.setParent(item);
				
				lc=new Listcell();
				lc.setLabel(mlist.getListName());
				lc.setParent(item);
				
				lc=new Listcell();
				lc.setLabel(""+mlist.getSize());
				lc.setParent(item);
				
				lc = new Listcell(MyCalendar.calendarToString(mlist.getLastModifiedDate(),
						MyCalendar.FORMAT_DATETIME_STDATE, tz));
				
				lc.setParent(item);
				
			} else if(data instanceof Campaigns) {
				
				Campaigns campaign=(Campaigns)data;
				item.setAttribute("campaign", campaign);
				
				Listcell lc=new Listcell();
				lc.setLabel(Utility.getOnlyUserName(campaign.getUsers().getUserName()));
				lc.setParent(item);
				
				UsersDao usersDao=(UsersDao)SpringUtil.getBean("usersDao");
				UserOrganization userOrg = usersDao.findByOrgName(Utility.getOnlyOrgId(campaign.getUsers().getUserName()));
				lc=new Listcell();
				lc.setLabel(userOrg.getOrganizationName());
				lc.setParent(item);
				
				lc=new Listcell();
				lc.setLabel(campaign.getCampaignName());
				lc.setParent(item);
				
				lc=new Listcell(MyCalendar.calendarToString(campaign.getCreatedDate(),
						MyCalendar.FORMAT_DATETIME_STDATE, tz));
				
				lc.setParent(item);
				
				lc=new Listcell();
				lc.setLabel(campaign.getStatus());
				lc.setParent(item);
				
				lc=new Listcell();
				Image img = new Image("/img/theme/home/reports_icon.png" );
				img.setTooltiptext("Get Reports");
				img.setStyle("margin-right:5px;cursor:pointer;");
				img.setAttribute("addEvent","onCampaign" );
				img.addEventListener("onClick", this);
				img.setParent(lc);
				lc.setParent(item);
			
			}else if(data instanceof CampaignSchedule) {
				CampaignSchedule campaignSchedule=(CampaignSchedule)data;
				item.setAttribute("campaignSchedule", campaignSchedule);
				
				Listcell lc=new Listcell();
				lc.setLabel(Utility.getOnlyUserName(campaignSchedule.getUser().getUserName()));
				lc.setParent(item);
				
				UsersDao usersDao=(UsersDao)SpringUtil.getBean("usersDao");
				UserOrganization userOrg = usersDao.findByOrgName(Utility.getOnlyOrgId(campaignSchedule.getUser().getUserName()));
				lc=new Listcell();
				lc.setLabel(userOrg.getOrganizationName());
				lc.setParent(item);
				
				
				lc=new Listcell(MyCalendar.calendarToString(campaignSchedule.getScheduledDate(), MyCalendar.FORMAT_DATETIME_STDATE));
				
				lc.setParent(item);
				
				lc=new Listcell();
				lc.setLabel(""+campaignSchedule.getCampaignId());
				lc.setParent(item);
				
				lc=new Listcell();
				lc.setLabel(""+campaignSchedule.getCsId());
				lc.setParent(item);
				
			
				Date date1 = Calendar.getInstance().getTime();
				Date date2 =campaignSchedule.getScheduledDate().getTime();
				
				long diff = date1.getTime() - date2.getTime();
				
				long daysInBetween = diff / (24*60*60*1000);
				long hoursInBetween= diff / (60*60*1000);
				long minutsInBetween= diff / (60*1000);
				
				
				
				long hours=0;
				long min=0;
				
				if(daysInBetween == 0 && hoursInBetween ==0 && minutsInBetween ==0 ) {
					hours=0;
					 min=0;
				}else if(daysInBetween == 0 && hoursInBetween ==0 && minutsInBetween !=0 ) {
					hours=0;
					 min=minutsInBetween;
				}
				else
				if(daysInBetween == 0 && hoursInBetween !=0 && minutsInBetween ==0 ) {
					hours=hoursInBetween;
					 min=0;
				}else
				if(daysInBetween == 0 && hoursInBetween !=0 && minutsInBetween !=0 ) {
					hours=hoursInBetween;
					 min=minutsInBetween % 60;
				}else{
				
				hours = hoursInBetween % daysInBetween;
				min = minutsInBetween % hoursInBetween;
				}
				
				String res=daysInBetween +" days ," +hours+ "Hours,"  +min+ " Minuts";
				lc=new Listcell();
				lc.setLabel(res);
				lc.setParent(item);

					
										
				lc=new Listcell();
				lc.setLabel(campaignSchedule.getStatusStr());
				lc.setParent(item);
				
			
			}else if(data instanceof Object[]) {
				logger.debug("----just entered---");
				Object[] obj=(Object[])data;
				//item.setAttribute("opens", open);
				
				Listcell lc=new Listcell();
				lc.setLabel((String)obj[0]);
				lc.setParent(item);
				
				lc=new Listcell(MyCalendar.calendarToString((Calendar)obj[1],
						MyCalendar.FORMAT_DATETIME_STDATE, tz));
				lc.setParent(item);
				if(obj.length==3){
				lc=new Listcell();
				lc.setParent(item);
				}
				
				lc=new Listcell();
				lc.setLabel((String)obj[2]);
				lc.setParent(item);
				
				if(obj.length==4) {
				lc=new Listcell();
				lc.setLabel((String)obj[3]);
				lc.setParent(item);
				}
			}
			
		}//render()
		
		@Override
		public void onEvent(Event evt) throws Exception {
			CampaignReportDao campaignReportDao = (CampaignReportDao)SpringUtil.getBean("campaignReportDao");
			Session sessionScope = Sessions.getCurrent();
			Object obj = evt.getTarget();
			
			try {
				
				if(obj instanceof Image) {
					logger.debug("--Entered onClick ---");
				
					Image img=(Image)obj;
					
					if(img.getAttribute("addEvent").equals("onCampReport")) {
						
						Listitem listitem=(Listitem)img.getParent().getParent();
						CampaignReport campaignReport=(CampaignReport)listitem.getAttribute("campaignReport");
						logger.debug(campaignReport);
					
						PageUtil.clearHeader();
						MessageUtil.clearMessage();
					
						sessionScope.setAttribute("fromPage","admin/dashBoard");
						sessionScope.setAttribute("campaignReport", campaignReport);
						logger.debug("****** redirecting to detailedReport page");
						Redirect.goTo(PageListEnum.REPORT_DETAILED_REPORT);
					} else if(img.getAttribute("addEvent").equals("onCampaign")) {
					
						Listitem listitem=(Listitem)img.getParent().getParent();
						Campaigns campaign=(Campaigns)listitem.getAttribute("campaign");
						logger.debug(campaign);
						
						Users user=campaign.getUsers();
						long userId=user.getUserId();
						long reportCount = campaignReportDao.getReportCountByCampaign(campaign.getCampaignName(), userId);
						logger.debug("*************----**"+reportCount);
						if(reportCount < 1) {
							MessageUtil.setMessage("No records exist for the email : "+campaign.getCampaignName(),"color:red", "TOP");
							return;
						}else {
							sessionScope.setAttribute("campaign",campaign);
							sessionScope.setAttribute("campreport","true");
							sessionScope.setAttribute("fromPage","admin/dashBoard");
							Redirect.goTo(PageListEnum.CAMPAIGN_REPORT);
						}//else
						
						
					}//else if
					
				}
			} catch (Exception e) {
				logger.error("** Exception :", e);
			}//catch
			
		}//onEvent()
		
	}//MyRenderer
	
	
	public static void main(String[] args) {
		Set<String> test = new HashSet<String>();
		
		test.add((new Long(100)).longValue()+"");
		test.add((new Long(1000)).longValue()+"");
		test.add((new Long(1002)).longValue()+"");
		test.add((new Long(1001)).longValue()+"");
		
		System.out.println(test.contains(100+""));
		System.out.println(test.contains(1003+""));
		System.out.println(test.contains(1002+""));
		System.out.println(test.contains(1004+""));
		System.out.println(test.contains(1001+""));
		System.out.println(test.contains(10011+""));
	}
	
}//dashBoardController
				
				
				
				

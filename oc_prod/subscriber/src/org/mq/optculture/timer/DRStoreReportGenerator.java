package org.mq.optculture.timer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.mq.marketer.campaign.beans.DRSent;
import org.mq.marketer.campaign.beans.GenerateReportSetting;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.UsersDomains;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.DRSentDao;
import org.mq.marketer.campaign.dao.GenerateReportSettingDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDomainsDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.data.dao.JdbcResultsetHandler;
import org.mq.optculture.model.ereceipt.EReceipt;
import org.mq.optculture.model.ereceipt.EReceiptMetrics;
import org.mq.optculture.model.ereceipt.EReceiptReport;
import org.mq.optculture.model.ereceipt.EReceiptStatus;
import org.mq.optculture.model.ereceipt.Report;
import org.mq.optculture.model.ereceipt.Store;
import org.mq.optculture.utils.OCCSVWriter;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OCFTPUpload;
import org.mq.optculture.utils.ServiceLocator;
import org.mq.optculture.utils.XMLUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Listitem;

public class DRStoreReportGenerator {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private final String TIMEZONE_EST = " EST";
	//@Scheduled(cron="0 0/5 * 1/1 * ?")
	@Scheduled(cron="0 0 * ? * *")//every 60 minutes
	public void run_task() {
		logger.info(">>> Started DRStoreReportGenerator timer >>>");
		try {
			ServiceLocator locator = ServiceLocator.getInstance();
			OrganizationStoresDao orgStoreDao = (OrganizationStoresDao)locator.getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
			DRSentDao drSentDao = (DRSentDao)locator.getDAOByName(OCConstants.DR_SENT_DAO);
			UsersDao userDao = (UsersDao)locator.getDAOByName(OCConstants.USERS_DAO);
			UsersDomainsDao domainDao = (UsersDomainsDao)locator.getDAOByName("usersDomainsDao");
			
			GenerateReportSettingDao generateReportSettingDao = (GenerateReportSettingDao)locator.getDAOByName(OCConstants.GENERATE_REPORT_SETTING_DAO);
			List<GenerateReportSetting> retList = generateReportSettingDao.findAll();
			//String serverTimeZoneVal = PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE);
			//int serverTimeZoneValInt = Integer.parseInt(serverTimeZoneVal);
			if(retList == null){
				logger.debug("====Nothing to proceed===");
				return;
			}
			logger.debug("Began the step===1"+retList.size());
			
			Long orgId = null;
			/*String header[] = null;
			String fields = Constants.STRING_NILL;
			String userName = null;// + System.currentTimeMillis()+ext;
*/
			
			for (GenerateReportSetting generateReportSetting : retList) {
				
				Long orgID = generateReportSetting.getOrgId();
				UserOrganization org = userDao.findByOrgId(orgID);
				
				boolean isMultiUser = org.isMultiUser();
				
				
				String freequency = generateReportSetting.getFreequency();
				Date generateAt = generateReportSetting.getGenerateAt();
				int hour = generateAt.getHours();//get(Calendar.HOUR);
				//Calendar current = Calendar.getInstance();
				Date current = new Date();
				if(!(freequency != null && freequency.equals(OCConstants.LTY_SETTING_REPORT_FRQ_DAY) && current.getHours() == hour)) {
					
					logger.debug("Began the step===2"+freequency+" hour "+hour+" current.HOUR ==="+current.getHours());
					continue;
					
				}
				
				Map<String, String> storeMap = null;
				String fromDate = null;//MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
				Calendar endCal = Calendar.getInstance();
				String endDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);
				long reportCount = 0l;
				
				String usersParentDirectory = (String) PropertyUtil.getPropertyValueFromDB("OCLocalDir");
		    	
		    	File downloadDir = new File(usersParentDirectory );
		    	
		    	if (!downloadDir.exists()) {
		    		downloadDir.mkdirs();
		    	}
		    	
		    	String filePath = usersParentDirectory + "/" + generateReportSetting.getType()+"_Report_"+
		    			+ System.currentTimeMillis() + "." + "json";
		    	File file = new File(filePath);
		    	
		    	
		    
		    	logger.debug("Began the step===3"+file.getName());
		    	if(generateReportSetting.getLastGeneratedOn() != null){
		    		
		    		fromDate = MyCalendar.calendarToString(generateReportSetting.getLastGeneratedOn(), MyCalendar.FORMAT_DATETIME_STYEAR);
		    	}else {
		    		endCal.set(Calendar.DAY_OF_MONTH, (endCal.get(Calendar.DAY_OF_MONTH)-1));
		    		fromDate =  MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);
		    	}

		    	List<UsersDomains> usersDomainsList = domainDao.getAllDomainsByOrganizationID(orgID);
		    	EReceiptReport eReceiptReport = new EReceiptReport();
		    	if(isMultiUser) {
					orgId = generateReportSetting.getOrgId();
					if(usersDomainsList == null) {
						logger.debug("Began the step===4==No Domains exists for "+orgId.longValue());
						continue;
						
					}
					Map<Long, String> domainMap = new HashMap<Long, String>();
					
					
					StringBuilder sdomainSb=new StringBuilder("");
					
					for (UsersDomains usersDomains : usersDomainsList) {
						sdomainSb.append(""+usersDomains.getDomainId());
						List<Map<String, Object>> tempList = userDao.findPowerUsersOfSelDomain(usersDomains.getDomainId());
						Long powuserIdoftheDomain = null;
						for (Map<String, Object> map : tempList) {
							powuserIdoftheDomain = Long.parseLong(map.get("user_id").toString());
							if(powuserIdoftheDomain != null){
								//if(sbusr.length()>0)sbusr.append(",");
								//sbusr.append(powuserIdoftheDomain);
								domainMap.put(powuserIdoftheDomain, usersDomains.getDomainName());
							}
						}
						if(powuserIdoftheDomain == null) continue;
						
						List<OrganizationStores> stores = orgStoreDao.findSubsidaryBydomainIds(usersDomains.getDomainId().longValue()+Constants.STRING_NILL);
						if(stores == null) {
							
							logger.debug("Began the step===5==no stores ===");
							continue;
						}
						storeMap = new HashMap<String, String>(); 
						for (OrganizationStores organizationStores : stores) {
							
							storeMap.put(organizationStores.getHomeStoreId(), 
									(usersDomains.getDomainName() != null ? usersDomains.getDomainName() :Constants.STRING_NILL ) +
									Constants.DELIMETER_DOUBLECOLON+(organizationStores.getHomeStoreId() != null ? organizationStores.getHomeStoreId() : Constants.STRING_NILL)+
									Constants.DELIMETER_DOUBLECOLON+(organizationStores.getSubsidiaryId() != null ? organizationStores.getSubsidiaryId() : Constants.STRING_NILL)+
									Constants.DELIMETER_DOUBLECOLON+(organizationStores.getAddress().getCountry() != null ? organizationStores.getAddress().getCountry() : Constants.STRING_NILL)+
									Constants.DELIMETER_DOUBLECOLON+(organizationStores.getSubsidiaryName() != null ? organizationStores.getSubsidiaryName() : (organizationStores.getSubsidiaryId() != null ? "Subsidiary ID "+organizationStores.getSubsidiaryId() : Constants.STRING_NILL)) +
									Constants.DELIMETER_DOUBLECOLON+(organizationStores.getERPStoreId() != null ? organizationStores.getERPStoreId() : Constants.STRING_NILL)+
									Constants.DELIMETER_DOUBLECOLON+(organizationStores.getStoreName() != null ? organizationStores.getStoreName() : (organizationStores.getHomeStoreId() != null ? "Store ID "+organizationStores.getHomeStoreId() : Constants.STRING_NILL )));
						}
						reportCount = drSentDao.getCountBy(powuserIdoftheDomain, fromDate, endDate);
						logger.debug("===total reports==="+reportCount);
						List<DRSent> retDrLst = drSentDao.find(powuserIdoftheDomain, fromDate, endDate);
						if(retDrLst == null ) continue;
						List<EReceipt> transactionsList = new ArrayList<EReceipt>();
						//EReceiptReport eReceiptReport = new EReceiptReport();
						
						for (DRSent drSent : retDrLst) {
							EReceiptMetrics eReceiptMetric = prepareMetrics(drSent);
							Store storeInfo = prepareStoreInfo(storeMap, drSent);
							EReceipt eReceipt = prepareTransactions(drSent, eReceiptMetric, storeInfo);
							transactionsList.add(eReceipt);
							 
						}//for
						
						Report report = new Report();
						report.setTotalCount(reportCount+Constants.STRING_NILL);
						
						EReceiptStatus eReceiptStatus = new EReceiptStatus();
						eReceiptStatus.setErrorCode("0");
						eReceiptStatus.setMessage("eReceipts report generation was successful.");
						eReceiptStatus.setStatus("success");
						
						eReceiptReport.setTransactions(transactionsList);
						eReceiptReport.setReport(report);
						eReceiptReport.setStatus(eReceiptStatus);
				
					}
					
				}else{
					Long ownerId = userDao.getOwnerofOrg(orgID);
					List<OrganizationStores> stores = orgStoreDao.findByOrganization(orgID);
					if(stores == null) {
						
						logger.debug("Began the step===5==no stores ===");
						continue;
					}
					
					UsersDomains usersDomains = usersDomainsList.get(0);
					storeMap = new HashMap<String, String>(); 
					for (OrganizationStores organizationStores : stores) {
						
						storeMap.put(organizationStores.getHomeStoreId(), 
								(usersDomains.getDomainName() != null ? usersDomains.getDomainName() :Constants.STRING_NILL ) +
								Constants.DELIMETER_DOUBLECOLON+(organizationStores.getHomeStoreId() != null ? organizationStores.getHomeStoreId() : Constants.STRING_NILL)+
								Constants.DELIMETER_DOUBLECOLON+(organizationStores.getSubsidiaryId() != null ? organizationStores.getSubsidiaryId() : Constants.STRING_NILL)+
								Constants.DELIMETER_DOUBLECOLON+(organizationStores.getAddress().getCountry() != null ? organizationStores.getAddress().getCountry() : Constants.STRING_NILL)+
								Constants.DELIMETER_DOUBLECOLON+(organizationStores.getSubsidiaryName() != null ? organizationStores.getSubsidiaryName() : (organizationStores.getSubsidiaryId() != null ? "Subsidiary ID "+organizationStores.getSubsidiaryId() : Constants.STRING_NILL)) +
								Constants.DELIMETER_DOUBLECOLON+(organizationStores.getERPStoreId() != null ? organizationStores.getERPStoreId() : Constants.STRING_NILL)+
								Constants.DELIMETER_DOUBLECOLON+(organizationStores.getStoreName() != null ? organizationStores.getStoreName() : (organizationStores.getHomeStoreId() != null ? "Store ID "+organizationStores.getHomeStoreId() : Constants.STRING_NILL )));
					}
					reportCount = drSentDao.getCountBy(ownerId, fromDate, endDate);
					logger.debug("===total reports==="+reportCount);
					
					String qry=" FROM DRSent WHERE userId ="+ownerId.longValue()+""+
							"  AND sentDate >= '"+fromDate+"' AND sentDate <='"+endDate+"'" ;
			
					logger.debug("====qry==="+qry);
					List<DRSent> retDrLst = drSentDao.find(ownerId, fromDate, endDate);
					if(retDrLst == null ) continue;
					
					List<EReceipt> transactionsList = new ArrayList<EReceipt>();
					
					
					for (DRSent drSent : retDrLst) {
						EReceiptMetrics eReceiptMetric = prepareMetrics(drSent);
						Store storeInfo = prepareStoreInfo(storeMap, drSent);
						EReceipt eReceipt = prepareTransactions(drSent, eReceiptMetric, storeInfo);
						transactionsList.add(eReceipt);
						 
					}//for
					
					Report report = new Report();
					report.setTotalCount(reportCount+Constants.STRING_NILL);
					
					EReceiptStatus eReceiptStatus = new EReceiptStatus();
					eReceiptStatus.setErrorCode("0");
					eReceiptStatus.setMessage("eReceipts report generation was successful.");
					eReceiptStatus.setStatus("success");
					
					eReceiptReport.setTransactions(transactionsList);
					eReceiptReport.setReport(report);
					eReceiptReport.setStatus(eReceiptStatus);
					
				}//else
				
				
				logger.debug("Began the step===6===");
				
				
				
				ObjectMapper mapper = new ObjectMapper();
				
				try {
					logger.debug("Start");
					//writing in file json data from java obj

					mapper.writerWithDefaultPrettyPrinter().writeValue(file, eReceiptReport);
					File createdXmlFile = new File(filePath);
					if(!createdXmlFile.exists()) {
						logger.debug(" XML file not creted with the Jaxb marshal of this file "+filePath);
					}else {
						
						//create the zip file 
						boolean zipFileCreationflag = compressFile(filePath, filePath+".zip");
						
					
						logger.debug("done ???"+zipFileCreationflag);
					}
				} catch (Exception e) {

					logger.error("Exception ", e);
				}
				//trigger FTP upload here
				OCFTPUpload ocFTPFileUpload = new OCFTPUpload();
				ocFTPFileUpload.uplodFileToClientFTP(generateReportSetting, filePath+".zip");
				
			}
				
				
				
			
		}catch (Exception e) {

		logger.debug("Exception ", e);
		}
	}
	
	
	public static void main(String[] args) throws Exception{
		Calendar endCal = Calendar.getInstance();
		logger.debug(Calendar.DAY_OF_MONTH);
		endCal.set(Calendar.DAY_OF_MONTH, (endCal.get(Calendar.DAY_OF_MONTH)-1));
		String endDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);
		logger.debug(endDate);
		
		String sentTimeStamp = "2018-10-24 23:05:30";
		DateFormat formatter = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
		Date date = (Date)formatter.parse(sentTimeStamp); 
		
		formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");//changes done as per their request
		String sentTimeStampStr = formatter.format(date);
		//TODO convert into client's timezone
		String[] trxTimeStrArr = sentTimeStampStr.split(" ");
		String sentDate = trxTimeStrArr[0];
		String sentTime = trxTimeStrArr[1];
		logger.debug(sentDate + " "+sentTime);
		
		
		String trxTimeStamp = "10/25/2018 23:52:52 AM";
		formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aaa");
		date = (Date)formatter.parse(trxTimeStamp); 
		
		formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");//changes done as per their request
		String trxTimeStr = formatter.format(date);
		trxTimeStrArr = trxTimeStr.split(" ");
		String trxDate = trxTimeStrArr[0];
		String trxTime = trxTimeStrArr[1];
		logger.debug(trxDate + " "+trxTime);
	}
	
	public EReceipt prepareTransactions(DRSent drSent, EReceiptMetrics eReceiptMetric, Store storeInfo) {
		
		EReceipt eReceipt = new EReceipt();
		eReceipt.setDocSID(drSent.getDocSid());
		eReceipt.setEmailAddress(drSent.getEmailId());
		eReceipt.setSentDate(MyCalendar.calendarToString(drSent.getSentDate(), MyCalendar.FORMAT_DATETIME_STYEAR) + TIMEZONE_EST);
		eReceipt.setStatus(drSent.getStatus());
		eReceipt.setMetrics(eReceiptMetric);
		eReceipt.setStore(storeInfo);
		
		return eReceipt;
		
		
		
	}
	public EReceiptMetrics prepareMetrics(DRSent drSent) {
			logger.debug("===entered prepared Metrics===");
		EReceiptMetrics metrics = new EReceiptMetrics();
		metrics.setSent(drSent.getSentCount()+Constants.STRING_NILL);
		metrics.setOpens(drSent.getOpens()+Constants.STRING_NILL);
		metrics.setClicks(drSent.getClicks()+Constants.STRING_NILL);
			
		return metrics;
			
			
			
	}
	
	public Store prepareStoreInfo(Map<String, String> storeMap, DRSent drSent){
		
		Store storeInfo = new Store();
		storeInfo.setStoreNumber(drSent.getStoreNumber() != null ? drSent.getStoreNumber() : Constants.STRING_NILL);
		
		String StoreMapStr = storeMap.get(drSent.getStoreNumber()); 
		if(StoreMapStr == null) return storeInfo; 
		
		logger.debug("===entered prepareStoreInfo===");
		String brand = StoreMapStr.split(Constants.DELIMETER_DOUBLECOLON)[0] ;
		String store1 =  StoreMapStr.split(Constants.DELIMETER_DOUBLECOLON)[1] ;
		String sbs = StoreMapStr.split(Constants.DELIMETER_DOUBLECOLON)[2] ;
		String country = StoreMapStr.split(Constants.DELIMETER_DOUBLECOLON)[3] ;
		String sbsName =  StoreMapStr.split(Constants.DELIMETER_DOUBLECOLON)[4] ;
		String ERPStoreID = StoreMapStr.split(Constants.DELIMETER_DOUBLECOLON)[5] ;
		String storeName = StoreMapStr.split(Constants.DELIMETER_DOUBLECOLON)[6] ;
					
		
		storeInfo.setSubsidiaryNumber(sbs);
		storeInfo.setSubsidiaryName(sbsName);
		storeInfo.setStoreName(storeName);
		storeInfo.setErpStoreCode(ERPStoreID);
		storeInfo.setBrand(brand);
		storeInfo.setCountry(country);
		
		return storeInfo;
		
		
		
		
	}
	
	public boolean compressFile(String sourceFileName , String destPathZipFileName) {
		
		try {
			int BUFFER = 2048;
			BufferedInputStream origin = null;
			FileOutputStream dest = new FileOutputStream(destPathZipFileName);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
			//out.setMethod(ZipOutputStream.DEFLATED);
			byte data[] = new byte[BUFFER];
			// get a list of files from current directory
			
			File f = new File(sourceFileName);
//        String files[] = f.list();
			FileInputStream fi = new FileInputStream(f);
			origin = new BufferedInputStream(fi, BUFFER);
			ZipEntry entry = new ZipEntry(f.getName());
			out.putNextEntry(entry);
			int count;
			while((count = origin.read(data, 0, BUFFER)) != -1) {
			 out.write(data, 0, count);
			}
			origin.close();

//        for (int i=0; i<files.length; i++) {
//           logger.debug("Adding: "+files[i]);
//        }
			out.close();
		} catch (FileNotFoundException e) {
			logger.error("Exception ", e);
			return false;
		} catch (IOException e) {
			logger.error("Exception ", e);
			return false;
		}catch(Exception e){
			logger.error("Exception ", e);
			return false;
		}
        
		return true;
     
	}
}

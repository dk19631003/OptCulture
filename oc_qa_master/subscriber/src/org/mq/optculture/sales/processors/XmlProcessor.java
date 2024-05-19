package org.mq.optculture.sales.processors;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.clapper.util.io.FileUtil;
import org.mq.marketer.campaign.beans.ETLFileUploadLogs;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ETLFileUploadLogDaoForDML;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.sales.json.DRBody;
import org.mq.optculture.sales.json.DRHead;
import org.mq.optculture.sales.json.DRItem;
import org.mq.optculture.sales.json.DRJsonRequest;
import org.mq.optculture.sales.json.DRReceipt;
import org.mq.optculture.sales.json.User;
import org.mq.optculture.sales.model.CrystalReport;
import org.mq.optculture.sales.model.Details;
import org.mq.optculture.sales.model.Field;
import org.mq.optculture.sales.model.Group;
import org.mq.optculture.sales.model.Group2;
import org.mq.optculture.sales.model.Group3;
import org.mq.optculture.sales.model.Group4;
import org.mq.optculture.sales.model.GroupFooter;
import org.mq.optculture.sales.model.GroupHeader;
import org.mq.optculture.sales.model.ReportHeader;
import org.mq.optculture.sales.model.Section;
import org.mq.optculture.sales.model.Subreport;
import org.mq.optculture.utils.ApplicationContextProvider;
import org.mq.optculture.utils.OCConstants;
import org.springframework.context.ApplicationContext;


/*
 *   One xml file can have multiple digital receipts information
 *   
 */

public class XmlProcessor implements Processor {
	
	
	public static boolean isvalidFBBMobile(String strPhone,String countryCarrier,UserOrganization userOrg)
	{
	try
	{
		String regEx="";
		Pattern phonePattern;
		int phoneLength=strPhone.length();
		int withCountryCodeLength= userOrg.getMinNumberOfDigits()+countryCarrier.length();
		if(phoneLength==userOrg.getMinNumberOfDigits())
		{
			regEx= "\\d{"+userOrg.getMinNumberOfDigits()+","+userOrg.getMaxNumberOfDigits()+"}";
			phonePattern = Pattern.compile(regEx);
		}
		else if(phoneLength==withCountryCodeLength)
		{
			regEx= "^"+countryCarrier+"\\d{"+userOrg.getMinNumberOfDigits()+","+userOrg.getMaxNumberOfDigits()+"}";
			phonePattern = Pattern.compile(regEx);
				
		}
		else
		{
			return false;
		}
		
		
		Matcher m = phonePattern.matcher(strPhone);
		return m.matches();
	}catch(Exception e) {logger.error(" Error "+e);return false;}
	}

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private ApplicationContext context ;
	private EmailQueueDaoForDML emailQueueDaoForDML = null;
	private ETLFileUploadLogDaoForDML etlFileUploadLogDaoForDML;
	private long  userId;
	public XmlProcessor() {
		
		context = ApplicationContextProvider.getApplicationContext();
		etlFileUploadLogDaoForDML = (ETLFileUploadLogDaoForDML)context.getBean("etlFileUploadLogDaoForDML");
		emailQueueDaoForDML = (EmailQueueDaoForDML)context.getBean("emailQueueDaoForDML");
	}
	
	@Override
	public void process(Exchange exchange) throws Exception {

	try
	{
		
		   HSSFWorkbook workBook = new HSSFWorkbook();
		   HSSFSheet sheet = workBook.createSheet("Receipt Details");
		   HSSFFont font = workBook.createFont();
		   font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		   HSSFCellStyle style = workBook.createCellStyle();
		   style.setFont(font);
			
			
			HSSFRow row = sheet.createRow(0);
			HSSFCell cell = null;
			cell = row.createCell(0);
			cell.setCellValue("S.No");
			cell.setCellStyle(style);
			
			cell = row.createCell(1);
			cell.setCellValue("Receipt Number");
			cell.setCellStyle(style);
			
			cell = row.createCell(2);
			cell.setCellValue("Receipt Date");
			cell.setCellStyle(style);
			
			cell = row.createCell(3);
			cell.setCellValue("Phone Number");
			cell.setCellStyle(style);
			
			cell = row.createCell(4);
			cell.setCellValue("Store Number");
			cell.setCellStyle(style);
			
			
			cell = row.createCell(5);
			cell.setCellValue("Comments");
			cell.setCellStyle(style);
			
			
			
			
			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4);
			
			
			
		
		
		
		Map<String, String> mapReportHeader = new HashMap<String, String>();
		Map<String, String> mapGroup4Footer = new HashMap<String, String>();
		Map<String, String> mapGroupHeader = new HashMap<String, String>();
		Map<String, String> mapGroupFooter = new HashMap<String, String>();

		Map<String, String> mapGroup2Header = new HashMap<String, String>();
		Map<String, String> mapGroup2Footer = new HashMap<String, String>();

		Map<String, String> mapGroup3Header = new HashMap<String, String>();

		CrystalReport crystalReport = (CrystalReport) exchange.getIn().getBody();
		Group[] lstGroups = crystalReport.getGroup();

		ReportHeader reportHeader = crystalReport.getReportHeader();
		Section sectionHeader = reportHeader.getSection();
		Subreport subreport = sectionHeader.getSubreport();
		Details details = subreport.getDetails();
		sectionHeader = details.getSection();

		Field[] fieldsHeader = sectionHeader.getField();

		for (Field field : fieldsHeader) {
			mapReportHeader.put(field.getName(), field.getValue());
		}

		List<DRJsonRequest> lstRequests = new ArrayList<>();
		List<DRItem> lstDrItems;
		DRItem drItem = null;
		DRJsonRequest drJsonRequest = null;
		DRReceipt drReceipt = null;
		DRBody drBody = null;

		List<String> lstInvoiceNumsForItems = new ArrayList<>();       /* receipt numbers with no items */
		List<String> lstInvoiceNumsForMobile = new ArrayList<>();       /* receipt numbers of invalid mobile numbers */
		List<String> lstInvoiceNumsForDate = new ArrayList<>();       /* receipt numbers of invalid date */
	
		int count =1;
		for (Group group : lstGroups) {
			count++;
			// for receipt details excel file
			row = sheet.createRow(count);
			cell = row.createCell(0);
			cell.setCellValue(count-1);
			cell.setCellStyle(style);
			
			GroupHeader gHeader = group.getGroupHeader();
			Section section = gHeader.getSection();
			Field[] fields = section.getField();

			for (Field field : fields) {
				mapGroupHeader.put(field.getName(), field.getValue());
			}

			GroupFooter gFooter = group.getGroupFooter();
			section = gFooter.getSection();
			fields = section.getField();

			for (Field field : fields) {
				mapGroupFooter.put(field.getName(), field.getValue());
			}

			Group2 group2 = group.getGroup2();
			gHeader = group2.getGroupHeader();
			section = gHeader.getSection();
			fields = section.getField();

			for (Field field : fields) {
				mapGroup2Header.put(field.getName(), field.getValue());
			}

			GroupFooter footer2 = group2.getGroupFooter();
			section = footer2.getSection();
			fields = section.getField();

			for (Field field : fields) {
				mapGroup2Footer.put(field.getName(), field.getValue());
			}

			Group3[] group3 = group2.getGroup3();
			Double totQty = 0.0, totalTax = 0.0;
			lstDrItems = new ArrayList<>();
		for(Group3 group34:group3)
		{
			gHeader = group34.getGroupHeader();
			section = gHeader.getSection();
			fields = section.getField();

			for (Field field : fields) {
				mapGroup3Header.put(field.getName(), field.getValue());
			}

			
			Group4[] lstGroups4 = group34.getGroup();
			
			
			for (Group4 group4 : lstGroups4) {
				gFooter = group4.getGroupFooter();
				if(gFooter==null) 
					{
					continue;  // no items found for the section
					}
				section = gFooter.getSection();
				fields = section.getField();
				for (Field field : fields) {
					mapGroup4Footer.put(field.getName(), field.getValue());
				}

				drItem = new DRItem();
				String strQty=mapGroup4Footer.get("SummevondTaQty1");
				String strDiscount = mapGroup4Footer.get("SummevonDiscountForQuantity1");
				drItem.setItemSID(mapGroup4Footer.get("szPOSItemID1"));
				drItem.setDesc1(mapGroup4Footer.get("szDesc1"));
				drItem.setTax(mapGroup4Footer.get("SummevonExactTaxValue1"));
				drItem.setQty(strQty);
				drItem.setExtTax(mapGroup4Footer.get("SummevonExactTaxValue1"));
				
				String strTotal= mapGroup4Footer.get("SummevonTurnoverWithTaxExemption1");
				
				//double discount = Double.parseDouble(strDiscount);
				double absDiscount = Math.abs(Double.parseDouble(strDiscount));
				//double itemPrice=0.0;
				double docItemOrgPrice=0.0;
				
				drItem.setDocItemDisc(absDiscount+"");
			//	drItem.setDocItemDiscAmt(absDiscount+"");   // commented on Jan 18th 2020
				
				
				/*
				 * InvcItemPrc, DocItemOrigPrc are the prices before discount
				 * (means before the discount applied)
				 * and there is one more field called DocItemPrc - 
				 * which is the price after we applied the discount so if discount is 0 all 3 are  same
				 */
				
				docItemOrgPrice = Double.parseDouble(strTotal)/Double.parseDouble(strQty);
				/*
				 *   when discount is in percentage
				 */
				//itemPrice = Double.parseDouble(strTotal)/(Double.parseDouble(strQty)*(1-discount));
				// itemPrice = (Double.parseDouble(strTotal) - discount)/Double.parseDouble(strQty);
				
				drItem.setDocItemOrigPrc(Math.abs(docItemOrgPrice)+"");
				drItem.setInvcItemPrc(Math.abs(docItemOrgPrice)+"");
				//drItem.setDocItemPrc(Math.abs(itemPrice)+"");  // commented on Jan 19th 2020
				drItem.setDocItemPrc(Math.abs(docItemOrgPrice)+"");
				totQty += Double.parseDouble(drItem.getQty());
				totalTax += Double.parseDouble(drItem.getTax());

				lstDrItems.add(drItem);
				mapGroup4Footer.clear();
			}
		}
			drJsonRequest = new DRJsonRequest();
			DRHead drHead = new DRHead();
			
			
			String username = exchange.getProperty("ETLUserName").toString();
			String token  = exchange.getProperty("token").toString();
	        userId = Long.parseLong(exchange.getProperty("userId").toString());
			User user = new User();
			user.setOrganizationId(Utility.getOnlyOrgId(username));
			user.setToken(token);
			user.setUserName(Utility.getOnlyUserName(username));
			
			exchange.setProperty("userName", user.getUserName());
			exchange.setProperty("orgId", user.getOrganizationId());
			
			UserOrganization userOrganization = (UserOrganization)exchange.getProperty("userOrg");
			String  countryCarrier = exchange.getProperty("countryCarrier").toString();
			
			drHead.setUser(user);
			drHead.setIsLoyaltyCustomer("Y");
			
			drBody = new DRBody();
			drReceipt = new DRReceipt();
			drReceipt.setStoreHeading1(mapReportHeader.get("szFirstBusinessName"));

			String strStore = mapReportHeader.get("lRetailStoreID");
			String strInvoiceNum = mapGroup3Header.get("lTaNmbr2");
			drReceipt.setStoreCode(strStore);
			drReceipt.setStore(strStore);

			drReceipt.setStoreName(mapReportHeader.get("szDescription"));
			drReceipt.setTotalTax(totalTax.toString());
			drReceipt.setInvcTotalQty(totQty.toString());
			drReceipt.setWorkstation(mapGroup3Header.get("lWorkstationNmbr2"));
			drReceipt.setInvcNum(strInvoiceNum);
			

			/*String strDocDate = mapGroup2Header.get("GroupNameTxDatedaily1");
			drReceipt.setDocDate(strDocDate);
			// move to method
			SimpleDateFormat sdf = new SimpleDateFormat("M/dd/yyyy");
			Date defaultDate = sdf.parse(strDocDate);
			//SimpleDateFormat newDateFormat = new SimpleDateFormat("MMddyyyy");
			SimpleDateFormat newDateFormat = new SimpleDateFormat(MyCalendar.FORMAT_SB_DATEONLY);  //Nov 11 2019
			String strDate = newDateFormat.format(defaultDate);*/
			
			String strDateForDocSid="";
			String strXmlDate="";
			try
			{

			strXmlDate = mapGroup2Header.get("GroupNameTxDatedaily1").trim();
			DateTimeFormatter formatterMdyyyy = DateTimeFormatter.ofPattern("M/d/yyyy");
			LocalDate localDate;
				try
				{
					localDate = LocalDate.parse(strXmlDate,formatterMdyyyy);
				}catch(DateTimeParseException pe)
				{
					DateTimeFormatter formatterddMMMyy = DateTimeFormatter.ofPattern("dd-MMM-yy");
					localDate = LocalDate.parse(strXmlDate,formatterddMMMyy);
				}
			
				DateTimeFormatter formatteryyyyMMdd = DateTimeFormatter.ofPattern(MyCalendar.FORMAT_SB_DATEONLY);
				strDateForDocSid = localDate.format(formatteryyyyMMdd);
			
			/*
			 *   convert any format to M/d/yyyy
			 */
			
			String strDocDate = localDate.format(formatterMdyyyy);
			drReceipt.setDocDate(strDocDate);
			
			cell = row.createCell(1);  // xls file receipt number and date
			cell.setCellValue(strInvoiceNum);
			cell.setCellStyle(style);
			
			cell = row.createCell(2);  
			cell.setCellValue(strDocDate);
			cell.setCellStyle(style);
			
			
			cell = row.createCell(4);  
			cell.setCellValue(strStore);
			cell.setCellStyle(style);
			
			cell = row.createCell(5);  
			cell.setCellValue("Processed successfully");
			cell.setCellStyle(style);
			
			
			}catch(Exception e)
			{
				lstInvoiceNumsForDate.add(strInvoiceNum);
				cell = row.createCell(1);  // xls file receipt number and date
				cell.setCellValue(strInvoiceNum);
				cell.setCellStyle(style);
				
				cell = row.createCell(2);  
				cell.setCellValue(strXmlDate);
				cell.setCellStyle(style);
				
				cell = row.createCell(4);  
				cell.setCellValue(strStore);
				cell.setCellStyle(style);
				
				cell = row.createCell(5);  
				cell.setCellValue("Date format not supported");
				cell.setCellStyle(style);
				
				
				
				continue; //skip the receipt for other dateFormats.
			}
			
			//Doc Sid =StoreNum-date-invoiceNum
			String strDocSid= strStore+"-"+strDateForDocSid+"-"+strInvoiceNum;
			
			

			drReceipt.setDocSID(strDocSid);
			// split name
			//split name (validation to be added)
			String strName = mapGroupHeader.get("GroupNameCustomerName1");
			String strLastName="";
			String strFirstName="";
			String strMobileNo="";
			if(strName!=null)
			{
			 try
			 {
				 String[] strNames = strName.split(",");
				 strLastName = strNames[0];
				 strLastName = strLastName.replaceAll("\\d","");
				 String[] strNameCustId= strNames[1].split("-");
				 strFirstName = strNameCustId[0].trim();
				 strMobileNo = strNameCustId[1].trim();
			 }catch(Exception e) {logger.error(" Error "+e);}
			} 
			
			
			cell = row.createCell(1);  // xls file receipt number and mobile number
			cell.setCellValue(strInvoiceNum);
			cell.setCellStyle(style);
			
			cell = row.createCell(3);  
			cell.setCellValue(strMobileNo);
			cell.setCellStyle(style);
			
			cell = row.createCell(4);  
			cell.setCellValue(strStore);
			cell.setCellStyle(style);
			
			if(!isvalidFBBMobile(strMobileNo,countryCarrier,userOrganization))  //skip the receipts for invalid mobileNos
			{
				lstInvoiceNumsForMobile.add(strInvoiceNum);
				cell = row.createCell(1);  // xls file receipt number and mobile number
				cell.setCellValue(strInvoiceNum);
				cell.setCellStyle(style);
				
				cell = row.createCell(3);  
				cell.setCellValue(strMobileNo);
				cell.setCellStyle(style);
				
				cell = row.createCell(4);  
				cell.setCellValue(strStore);
				cell.setCellStyle(style);
				
				cell = row.createCell(5);  
				cell.setCellValue("Not a valid mobile number");
				cell.setCellStyle(style);
				
				continue;
			}
			drReceipt.setBillToLName(strLastName);
			drReceipt.setBillToFName(strFirstName);
			drReceipt.setBillToPhone1(strMobileNo);
			drReceipt.setBillToUDF8(strMobileNo); //Dec 11 2019.
			String strReceiptTotal = mapGroupFooter.get("SummevonTurnoverWithTaxExemption4");
			Double subTotal =0.0;
			try
			{
		      subTotal = Double.parseDouble(strReceiptTotal)- totalTax; 
			}catch(Exception e) {subTotal=0.0;}
		      drReceipt.setTotal(strReceiptTotal);
			drReceipt.setSubtotal(subTotal.toString());
			//drReceipt.setBillToCustSID(strMobileNo); //Dec 11 2019.
			drBody.setReceipt(drReceipt);
			
			/*
			 *  If Items information not found don't generate DR/Json
			 */
			if(lstDrItems.size()==0)
			{
				lstInvoiceNumsForItems.add(drReceipt.getInvcNum());
			}
			else
			{
			  drBody.setItems(lstDrItems);
			  drJsonRequest.setHead(drHead);
			  drJsonRequest.setBody(drBody);
			  lstRequests.add(drJsonRequest); 
			}
			
			
			
		}// end for read xml file
		String receiptsFile="";
	try
	{
		String fileName = exchange.getProperty("fileName").toString();
		String fileNameNoExt = FileUtil.getFileNameNoExtension(new File(fileName).getName());
		receiptsFile = Paths.get(exchange.getProperty("processedPath").toString(),fileNameNoExt+".xls").toString();
		try(FileOutputStream fileOut = new FileOutputStream(receiptsFile);)
		{
		   workBook.write(fileOut);
		   logger.info("excel file created..");
		}
	}catch(Exception e) {e.printStackTrace();	}    
		  int receiptCount = lstRequests.size();
		  int failedItemsCount= lstInvoiceNumsForItems.size();
		  int failedMobileCount = lstInvoiceNumsForMobile.size();
		  int failedDateCount = lstInvoiceNumsForDate.size();
		  int failedCount=failedItemsCount+failedMobileCount+failedDateCount;
		  int totalCount = receiptCount + failedCount;
		  String strComments = "";
		  String strEmailComments="<br/>";
		  boolean sendEmail=false;
		   String strStatus = "Processed="+receiptCount+" ,Failed="+failedCount;
		   
		   
		   if(failedCount==0)
		   {
			   strComments=OCConstants.ETL_DEFAULT_COMMENTS;
			   
		   }
		   if(failedItemsCount>0)
		   {
			   sendEmail=true;
			   strComments= strComments+OCConstants.ETL_ITEMS_NOT_FOUND+lstInvoiceNumsForItems+"\n";
			   strEmailComments= strEmailComments+OCConstants.ETL_ITEMS_NOT_FOUND+lstInvoiceNumsForItems+"<br/>";
		   }
		   if(failedMobileCount>0)
		   {
			   sendEmail=true;
			   strComments= strComments+OCConstants.ETL_INVALID_MOBILE_NUMBER+lstInvoiceNumsForMobile+"\n";
			   strEmailComments= strEmailComments+OCConstants.ETL_INVALID_MOBILE_NUMBER+lstInvoiceNumsForMobile+"<br/>";;
		   }
		   if(failedDateCount>0)
		   {
			   sendEmail=true;
			   strComments= strComments+OCConstants.ETL_INVALID_DATE+lstInvoiceNumsForDate+"\n";
			   strEmailComments= strEmailComments+OCConstants.ETL_INVALID_DATE+lstInvoiceNumsForDate+"<br/>";
		   }
		
		ETLFileUploadLogs fileUploadLogs = new ETLFileUploadLogs();
		fileUploadLogs.setFileName(exchange.getProperty("fileName").toString());
		fileUploadLogs.setUploadTime(Calendar.getInstance());
		fileUploadLogs.setUserId(userId);
		fileUploadLogs.setRecordCount(totalCount);
		fileUploadLogs.setFileStatus(strStatus);
		fileUploadLogs.setComments(strComments);
		String strProssedFile = Paths.get(exchange.getProperty("processedPath").toString(),exchange.getProperty("fileName").toString()).toString();
		fileUploadLogs.setProcessedFilePath(strProssedFile);
		fileUploadLogs.setReceiptDetailsPath(receiptsFile);
		
		etlFileUploadLogDaoForDML.saveOrUpdate(fileUploadLogs);
		logger.info("ETL Info updated");
 		exchange.getIn().setBody(lstRequests, List.class);
 		
 		 		
 		if(sendEmail)
		  {
		   String strETLUserName = exchange.getProperty("ETLUserName").toString();
	       String strOrgId= Utility.getOnlyOrgId(strETLUserName);
		   String strUserName=Utility.getOnlyUserName(strETLUserName);
		   String strFileName = exchange.getProperty("fileNameNoExt").toString()+"_"+exchange.getProperty("fileTime").toString()+".xml";
		   String emailSubject = "OptCulture Alert: "
			   		+ "XML file uploaded with errors (Username: "+strUserName+")";
		   String emailMessage  = PropertyUtil.getPropertyValueFromDB(OCConstants.ETL_INVALID_RECEIPTS_EMAIL_TEMPLATE);
		   Calendar cal = Calendar. getInstance();
		   emailMessage = emailMessage.replace(OCConstants.ETL_MAIL_TIMESTAMP, new Date().toString()); // use IST Date
		   emailMessage = emailMessage.replace(OCConstants.ETL_MAIL_FILE_NAME, strFileName);
		   emailMessage = emailMessage.replace(OCConstants.ETL_MAIL_USER_NAME, strUserName);
		   emailMessage = emailMessage.replace(OCConstants.ETL_MAIL_ORG_ID, strOrgId);
		   emailMessage = emailMessage.replace(OCConstants.ETL_MAIL_COMMENTS, strEmailComments);
		   emailMessage = emailMessage.replace(OCConstants.ETL_TOTAL_RECEIPTS, totalCount+"");
		   emailMessage = emailMessage.replace(OCConstants.ETL_PROCESSED_RECEIPTS, receiptCount+"");
		   emailMessage = emailMessage.replace(OCConstants.ETL_FAILED_RECEIPTS, failedCount+"");
		   
		  
		   
		   String type = Constants.EQ_TYPE_SUPPORT_ALERT;
		   String status = Constants.SUPPORT_STATUS_ACTIVE;
		   String fbbSupportEmails =PropertyUtil.getPropertyValueFromDB("FBBAlertToEmailId");
		   String[] fbbSupportEmailsArr = new String[0];
		   if(fbbSupportEmails!=null)
		      {  fbbSupportEmailsArr = fbbSupportEmails.split(",");}
		   for (String fbbSupportEmail : fbbSupportEmailsArr) 
		   {
			   if(!Utility.validateEmail(fbbSupportEmail)) 
				   {
				   logger.error("invalid Email "+fbbSupportEmail);
				     continue;
				   }
			   EmailQueue alertEmailQueue = 
				   new EmailQueue(emailSubject, emailMessage, type, status, fbbSupportEmail,cal);
			   emailQueueDaoForDML.saveOrUpdate(alertEmailQueue);
			   logger.info("alert mail is saved on email queue"+fbbSupportEmail);
		   }
		  
		  }
 		
 		
		
		}catch(Exception e)
			{
			  
			{logger.error(" Error "+e);}
			}
	
			
		
	}

}

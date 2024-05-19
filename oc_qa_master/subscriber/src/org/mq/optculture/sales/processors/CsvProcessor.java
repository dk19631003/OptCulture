package org.mq.optculture.sales.processors;

import java.nio.file.Paths;
import java.time.LocalDateTime;
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
import org.mq.marketer.campaign.beans.CSVFileUploadLogs;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.dao.CSVFileUploadLogDaoForDML;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.sales.csv.SalesDetails;
import org.mq.optculture.sales.json.DRBody;
import org.mq.optculture.sales.json.DRHead;
import org.mq.optculture.sales.json.DRItem;
import org.mq.optculture.sales.json.DRJsonRequest;
import org.mq.optculture.sales.json.DRReceipt;
import org.mq.optculture.sales.json.User;
import org.mq.optculture.utils.ApplicationContextProvider;
import org.mq.optculture.utils.OCConstants;
import org.springframework.context.ApplicationContext;

/*
 *   One csv file can have multiple digital receipts information
 *   
 */

public class CsvProcessor implements Processor {

	public static boolean isValidDate(String strDate) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
			LocalDateTime.parse(strDate, formatter);
			return true;
		} catch (DateTimeParseException e) {
			logger.info(e.getLocalizedMessage() + ":" + strDate);
			return false;
		}
	}

	public static boolean isvalidMobile(String strPhone, String countryCarrier, UserOrganization userOrg) {
		try {
			String regEx = "";
			Pattern phonePattern;
			int phoneLength = strPhone.length();
			int withCountryCodeLength = userOrg.getMinNumberOfDigits() + countryCarrier.length();
			if (phoneLength == userOrg.getMinNumberOfDigits()) {
				regEx = "\\d{" + userOrg.getMinNumberOfDigits() + "," + userOrg.getMaxNumberOfDigits() + "}";
				phonePattern = Pattern.compile(regEx);
			} else if (phoneLength == withCountryCodeLength) {
				regEx = "^" + countryCarrier + "\\d{" + userOrg.getMinNumberOfDigits() + ","
						+ userOrg.getMaxNumberOfDigits() + "}";
				phonePattern = Pattern.compile(regEx);

			} else {
				return false;
			}

			Matcher m = phonePattern.matcher(strPhone);
			return m.matches();
		} catch (Exception e) {
			logger.error(" Error " + e);
			return false;
		}
	}

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private ApplicationContext context;
	private EmailQueueDaoForDML emailQueueDaoForDML = null;
	private CSVFileUploadLogDaoForDML csvFileUploadLogDaoForDML;
	private long userId;

	public CsvProcessor() {

		context = ApplicationContextProvider.getApplicationContext();
		csvFileUploadLogDaoForDML = (CSVFileUploadLogDaoForDML) context.getBean("csvFileUploadLogDaoForDML");
		emailQueueDaoForDML = (EmailQueueDaoForDML) context.getBean("emailQueueDaoForDML");

	}

	@Override
	public void process(Exchange exchange) throws Exception {

		try {
			String fileName = exchange.getProperty("fileName").toString();
			logger.info("fileName.." + fileName);
			logger.info(exchange.getIn().getBody());
			List<SalesDetails> lstSales = new ArrayList<SalesDetails>();
			try {
				lstSales = (List) exchange.getIn().getBody();
			} catch (ClassCastException e) {
				lstSales.add((SalesDetails) exchange.getIn().getBody());
			}
			List<DRJsonRequest> lstRequests = new ArrayList<>();

			DRItem drItem = null;
			DRJsonRequest drJsonRequest = null;
			DRReceipt drReceipt = null;
			DRBody drBody = null;
			DRHead drHead = null;
			SalesDetails sales = null;

			Map<String, List<DRItem>> mapReceiptItems = new HashMap();
			String prevReceipt = "";
			List<DRItem> lstDrItems = new ArrayList<DRItem>();
			List<String> lstInvoiceNumsForMobile = new ArrayList<>(); /* receipt numbers of invalid mobile numbers */
			List<String> lstInvoiceNumsForDate = new ArrayList<>(); /* receipt numbers of invalid date */
			for (int i = 0; i < lstSales.size(); i++) {
				sales = lstSales.get(i);
				String currReceipt = sales.getReceiptNumber();
				logger.info("currReceipt:" + currReceipt);
				logger.info("nextReceipt:" + prevReceipt);
				drItem = new DRItem();
				drItem.setDesc1(sales.getDescription());
				drItem.setItemSID(sales.getItemSid());
				drItem.setDCS(sales.getCategory());
				drItem.setQty(sales.getQty());
				drItem.setDocItemDisc(sales.getDiscount());
				drItem.setInvcItemPrc(sales.getSalePrice());
				drItem.setSize(sales.getUdf2());
				if (!prevReceipt.equalsIgnoreCase(currReceipt)) {
					lstDrItems = new ArrayList<DRItem>();
					lstDrItems.add(drItem);
					logger.info("lst " + lstDrItems);
					prevReceipt = currReceipt;
				} else {
					lstDrItems.add(drItem);
					prevReceipt = currReceipt;
					continue;
				}
				mapReceiptItems.put(currReceipt, lstDrItems);

			}

			logger.info("Map values:" + mapReceiptItems);
			prevReceipt = "";
			for (int i = 0; i < lstSales.size(); i++) {
				sales = lstSales.get(i);
				String currReceipt = sales.getReceiptNumber();
				if (prevReceipt.equalsIgnoreCase(currReceipt)) {
					continue;
				}
				prevReceipt = currReceipt;

				drJsonRequest = new DRJsonRequest();
				User user = new User();
				String username = exchange.getProperty("CSVUserName").toString();
				String token = exchange.getProperty("token").toString();
				userId = Long.parseLong(exchange.getProperty("userId").toString());
				user.setOrganizationId(Utility.getOnlyOrgId(username));
				user.setToken(token);
				user.setUserName(Utility.getOnlyUserName(username));

				exchange.setProperty("userName", user.getUserName());
				exchange.setProperty("orgId", user.getOrganizationId());

				UserOrganization userOrganization = (UserOrganization) exchange.getProperty("userOrg");
				String countryCarrier = exchange.getProperty("countryCarrier").toString();

				drHead = new DRHead();
				drHead.setUser(user);
				drHead.setEnrollCustomer("");
				drHead.setIsLoyaltyCustomer("Y");
				drHead.setEmailReceipt("N");
				drHead.setPrintReceipt("N");
				drHead.setRequestEndPoint("/processReceipt.mqrm");
				drHead.setRequestSource("OptDR");
				drHead.setRequestFormat("JSON");

				drReceipt = new DRReceipt();
				String strMobileNo = sales.getCustomerId();
				drReceipt.setBillToPhone1(strMobileNo);
				drReceipt.setBillToCustSID(strMobileNo);
				drReceipt.setBillToFName(sales.getFirstName());
				drReceipt.setBillToLName(sales.getLastName());
				drReceipt.setBillToEMail(sales.getEmail());
				String strDate = sales.getSaleDate();
				drReceipt.setDocDate(strDate);
				drReceipt.setDocTime("");
				String strReceiptNumber = sales.getReceiptNumber();
				drReceipt.setDocSID(strReceiptNumber);
				drReceipt.setInvcNum(strReceiptNumber);
				// drReceipt.setStore(sales.getHomeStore());
				drReceipt.setStore(sales.getStoreNumber()); // ?
				drReceipt.setSubsidiaryNumber(sales.getSubsidiary());
				drReceipt.setTender(sales.getTenderType());
				drReceipt.setBillToUDF1(sales.getUdf1());
				drReceipt.setSubtotal(sales.getUdf1());
				drBody = new DRBody();
				drBody.setReceipt(drReceipt);
				drBody.setItems(mapReceiptItems.get(sales.getReceiptNumber()));
				drJsonRequest.setBody(drBody);
				drJsonRequest.setHead(drHead);

				if (!isvalidMobile(strMobileNo, countryCarrier, userOrganization)) // skip the receipts for invalid
																					// mobileNos
				{
					lstInvoiceNumsForMobile.add(strReceiptNumber);
					continue;
				}

				if (!isValidDate(strDate)) {
					lstInvoiceNumsForDate.add(strReceiptNumber);
					continue;
				}

				lstRequests.add(drJsonRequest);
			}
			logger.info("total receipts :" + lstRequests.size());
			exchange.getIn().setBody(lstRequests, List.class);

			int receiptCount = lstRequests.size();
			int failedMobileCount = lstInvoiceNumsForMobile.size();
			int failedDateCount = lstInvoiceNumsForDate.size();
			int failedCount = failedMobileCount + failedDateCount;
			int totalCount = receiptCount + failedCount;
			String strComments = "";
			String strEmailComments = "<br/>";
			boolean sendEmail = false;
			String strStatus = "Processed=" + receiptCount + " ,Failed=" + failedCount;

			if (failedCount == 0) {
				strComments = OCConstants.Ambica_DEFAULT_COMMENTS;

			}
			if (failedMobileCount > 0) {
				sendEmail = true;
				strComments = strComments + OCConstants.Ambica_INVALID_MOBILE_NUMBER + lstInvoiceNumsForMobile + "\n";
				strEmailComments = strEmailComments + OCConstants.Ambica_INVALID_MOBILE_NUMBER + lstInvoiceNumsForMobile
						+ "<br/>";
				;
			}
			if (failedDateCount > 0) {
				sendEmail = true;
				strComments = strComments + OCConstants.Ambica_INVALID_DATE + lstInvoiceNumsForDate + "\n";
				strEmailComments = strEmailComments + OCConstants.Ambica_INVALID_DATE + lstInvoiceNumsForDate + "<br/>";
			}

			CSVFileUploadLogs fileUploadLogs = new CSVFileUploadLogs();
			fileUploadLogs.setFileName(exchange.getProperty("fileName").toString());
			fileUploadLogs.setUploadTime(Calendar.getInstance());
			fileUploadLogs.setUserId(userId);
			fileUploadLogs.setRecordCount(totalCount);
			fileUploadLogs.setFileStatus(strStatus);
			fileUploadLogs.setComments(strComments);
			String strProssedFile = Paths
					.get(exchange.getProperty("processedPath").toString(), exchange.getProperty("fileName").toString())
					.toString();
			fileUploadLogs.setProcessedFilePath(strProssedFile);
			fileUploadLogs.setReceiptDetailsPath(""); // will add based on future requirement.

			csvFileUploadLogDaoForDML.saveOrUpdate(fileUploadLogs);
			logger.info("CSV Info updated");
			exchange.getIn().setBody(lstRequests, List.class);

			if (sendEmail) {
				String strETLUserName = exchange.getProperty("CSVUserName").toString();
				String strOrgId = Utility.getOnlyOrgId(strETLUserName);
				String strUserName = Utility.getOnlyUserName(strETLUserName);
				String strFileName = exchange.getProperty("fileNameNoExt").toString() + "_"
						+ exchange.getProperty("fileTime").toString() + ".csv";
				String emailSubject = "OptCulture Alert: " + "CSV file uploaded with errors (Username: " + strUserName
						+ ")";
				String emailMessage = PropertyUtil
						.getPropertyValueFromDB(OCConstants.ETL_INVALID_RECEIPTS_EMAIL_TEMPLATE);
				Calendar cal = Calendar.getInstance();
				emailMessage = emailMessage.replace(OCConstants.Ambica_MAIL_TIMESTAMP, new Date().toString()); // use
																												// IST
																												// Date
				emailMessage = emailMessage.replace(OCConstants.Ambica_MAIL_FILE_NAME, strFileName);
				emailMessage = emailMessage.replace(OCConstants.Ambica_MAIL_USER_NAME, strUserName);
				emailMessage = emailMessage.replace(OCConstants.Ambica_MAIL_ORG_ID, strOrgId);
				emailMessage = emailMessage.replace(OCConstants.Ambica_MAIL_COMMENTS, strEmailComments);
				emailMessage = emailMessage.replace(OCConstants.Ambica_TOTAL_RECEIPTS, totalCount + "");
				emailMessage = emailMessage.replace(OCConstants.Ambica_PROCESSED_RECEIPTS, receiptCount + "");
				emailMessage = emailMessage.replace(OCConstants.Ambica_FAILED_RECEIPTS, failedCount + "");

				String type = Constants.EQ_TYPE_SUPPORT_ALERT;
				String status = Constants.SUPPORT_STATUS_ACTIVE;
				String supportEmails = PropertyUtil.getPropertyValueFromDB("AmbicaAlertToEmailId");
				String[] supportEmailsArr = new String[0];
				if (supportEmails != null) {
					supportEmailsArr = supportEmails.split(",");
				}
				for (String supportEmail : supportEmailsArr) {
					if (!Utility.validateEmail(supportEmail)) {
						logger.error("invalid Email " + supportEmail);
						continue;
					}
					EmailQueue alertEmailQueue = new EmailQueue(emailSubject, emailMessage, type, status,
							supportEmail, cal);
					emailQueueDaoForDML.saveOrUpdate(alertEmailQueue);
					logger.info("alert mail is saved on email queue:" + supportEmail);
				}

			}

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

	}

}

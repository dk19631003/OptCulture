package org.mq.optculture.sales.routes;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.model.dataformat.JsonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.spi.DataFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.clapper.util.io.FileUtil;
import org.mq.marketer.campaign.beans.CSVFileUploadLogs;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.CSVFileUploadLogDaoForDML;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.sales.csv.SalesDetails;
import org.mq.optculture.sales.json.DRJsonRequest;
import org.mq.optculture.sales.processors.CsvProcessor;
import org.mq.optculture.sales.processors.DRProcessor;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;



public class CsvRouteBuilder extends RouteBuilder{

	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	//private String CSVPath=PropertyUtil.getPropertyValue("CSVFileSource");
	
	 private String strRootPath=PropertyUtil.getPropertyValueFromDB("SftpUserPath1");
     private String strOrgId = "MANTRA";
     private String strFoderName = "CSVFiles";
	 private String CSVPath = Paths.get(strRootPath,strOrgId,strFoderName).toString();
	 private EmailQueueDaoForDML emailQueueDaoForDML = null;
	 private  CSVFileUploadLogDaoForDML csvFileUploadLogDaoForDML=null;
			
	public CsvRouteBuilder()
	{
		logger.info("CsvRouteBuilder");
		try
		{
		ServiceLocator locator = ServiceLocator.getInstance();
		emailQueueDaoForDML = (EmailQueueDaoForDML)locator.getDAOForDMLByName("emailQueueDaoForDML");
		csvFileUploadLogDaoForDML = (CSVFileUploadLogDaoForDML)locator.getDAOForDMLByName("csvFileUploadLogDaoForDML"); 
		   
		}catch(Exception e) {logger.error("exception", e);
		}
	}
	
	public void configure() throws Exception {
		try {
			DataFormat dataFormatSales = new BindyCsvDataFormat(SalesDetails.class);
			JsonDataFormat gsonDataFormat = new JsonDataFormat(JsonLibrary.Gson);
			
			String strCSVDir="file:"+Paths.get(CSVPath).toString();
		    String strSourceDir = strCSVDir+"?delay=30000&recursive=true&minDepth=4&delete=true&moveFailed=./../../error";
			from(strSourceDir).routeId("csv_route")
			.autoStartup(true).startupOrder(2)
			.process(new Processor() {
				public void process(Exchange exchange)  {
			         Message in = exchange.getIn();
			       	 String fileName = in.getHeader("CamelFileName", String.class);
			         String camelFileNoExt = FileUtil.getFileNameNoExtension(new File(fileName).getName());
			         String camelFileExt = FileUtil.getFileNameExtension(new File(fileName).getName()).toLowerCase();
			       	 Path path = Paths.get(fileName); 
				     Path rootDir = path.getParent().getParent().getParent().getFileName();
			       	 String strUserDir = rootDir.toString();
			       	 String fileNameNoExt = FileUtil.getFileNameNoExtension(new File(fileName).getName());
			       	 exchange.setProperty("fileNameNoExt", fileNameNoExt);
			       	 exchange.setProperty("CSVUserName", strUserDir);
			       	 String strFailedFilesPath = Paths.get(strUserDir,"failed", fileNameNoExt).toString();
			       	 String strProcessPath = Paths.get(strUserDir,"processed", fileNameNoExt).toString();
			       	 long longTime = new Date().getTime();
			         exchange.setProperty("fileName", fileNameNoExt+"_"+longTime+".csv");
			         in.setHeader("userDir",Paths.get(strUserDir,"source","files",camelFileNoExt));
			       	 in.setHeader("strTime", longTime+"");
			         in.setHeader("failedFilesPath",strFailedFilesPath);
			         in.setHeader("processPath",strProcessPath);
			         in.setHeader("camelFileExt",camelFileExt);
			         in.setHeader("CamelFileNameLower",fileName.toLowerCase());
			         exchange.setProperty("failedPath", Paths.get(strUserDir,"failed"));
			         exchange.setProperty("processedPath", Paths.get(CSVPath,strUserDir,"processed"));
			         exchange.setProperty("fileTime", longTime+"");
			         
			         try {
							UsersDao userDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
							Users userObj = userDao.findByUsername(strUserDir);
							exchange.setProperty("userId", userObj.getUserId());
							exchange.setProperty("token",userObj.getUserOrganization().getOptSyncKey());
							exchange.setProperty("userOrg", userObj.getUserOrganization());
							exchange.setProperty("countryCarrier", userObj.getCountryCarrier().toString());
							 
						} catch (Exception e) {
							logger.error("exception:", e);
						}
			         
				}
			}).to(strCSVDir+"?fileName=${header.processPath}_${header.strTime}"+"."+"${header.camelFileExt}")
			.onException(Exception.class)
			.handled(true).log(LoggingLevel.ERROR, "Exception")
			.process(new Processor() {
		         public void process(Exchange exchange) throws Exception {
		               Exception exception = (Exception) exchange.getProperty(Exchange.EXCEPTION_CAUGHT);
		              
		               String strCSVUserName = exchange.getProperty("CSVUserName").toString();
		               String strOrgId= Utility.getOnlyOrgId(strCSVUserName);
		   			   String strUserName=Utility.getOnlyUserName(strCSVUserName);
		   			   String strFileName = exchange.getProperty("fileNameNoExt").toString()+"_"+exchange.getProperty("fileTime").toString()+".csv";
					 
		   			   logger.error(exception.getLocalizedMessage()+",FileName="+strFileName);
		       		   String emailSubject = "OptCulture Alert: "
		       		   		+ "CSV file not processed (Username: "+strUserName+")";
					   String emailMessage  = PropertyUtil.getPropertyValueFromDB(OCConstants.Ambica_INVALID_EMAIL_TEMPLATE);
					   String ambicaSupportEmails =PropertyUtil.getPropertyValueFromDB("AmbicaAlertToEmailId");
					   
					   Calendar cal = Calendar. getInstance();
					   emailMessage = emailMessage.replace(OCConstants.Ambica_MAIL_TIMESTAMP, new Date().toString()); 
					   emailMessage = emailMessage.replace(OCConstants.Ambica_MAIL_FILE_NAME, strFileName);
					   emailMessage = emailMessage.replace(OCConstants.Ambica_MAIL_USER_NAME, strUserName);
					   emailMessage = emailMessage.replace(OCConstants.Ambica_MAIL_ORG_ID, strOrgId);
					   emailMessage = emailMessage.replace(OCConstants.Ambica_MAIL_FILE_LOCATION, exchange.getProperty("failedPath").toString());
					  
					   
					   Message in = exchange.getIn();
					   String fileName = in.getHeader("CamelFileName", String.class);
					   Path path = Paths.get(fileName); 
					   Path rootDir = path.getParent().getParent().getParent().getFileName();
				       String strUserDir = rootDir.toString();
					   exchange.setProperty("processedPath", Paths.get(CSVPath,strUserDir,"processed"));
					   String type = Constants.EQ_TYPE_SUPPORT_ALERT;
					   String status = Constants.SUPPORT_STATUS_ACTIVE;
					   String[] ambicaSupportEmailsArr=new String[0];
					    if(ambicaSupportEmails!=null)
					    	ambicaSupportEmailsArr = ambicaSupportEmails.split(",");
					   for (String ambicaSupportEmail : ambicaSupportEmailsArr) 
					   {
						   if(!Utility.validateEmail(ambicaSupportEmail)) 
							   {
							   logger.error("invalid Email "+ambicaSupportEmail);
							     continue;
							   }

					   EmailQueue alertEmailQueue = 
							   new EmailQueue(emailSubject, emailMessage, type, status, ambicaSupportEmail,cal);
					    emailQueueDaoForDML.saveOrUpdate(alertEmailQueue);
					    logger.info("alert mail is saved on email queue "+ambicaSupportEmail);
					   }		  
					    CSVFileUploadLogs fileUploadLogs = new CSVFileUploadLogs();
						fileUploadLogs.setFileName(exchange.getProperty("fileName").toString());
						fileUploadLogs.setUploadTime(Calendar.getInstance());
						fileUploadLogs.setUserId(Long.parseLong(exchange.getProperty("userId").toString()));
						fileUploadLogs.setRecordCount(0);
						fileUploadLogs.setFileStatus("Failed");
						fileUploadLogs.setComments("CSV Format not supported");
						String strProcessedFile = Paths.get(exchange.getProperty("processedPath").toString(),exchange.getProperty("fileName").toString()).toString();
						fileUploadLogs.setProcessedFilePath(strProcessedFile);
						csvFileUploadLogDaoForDML.saveOrUpdate(fileUploadLogs);
				  
			     }
		    })
			.to(strCSVDir+"?fileName=${header.failedFilesPath}_${header.strTime}.csv").end()
			.threads(5,10) //internally uses executor service
			.choice()
			.when(header("CamelFileNameLower").endsWith(".csv"))
			.log(LoggingLevel.INFO,  "csv Processing has started...")
			.unmarshal(dataFormatSales)
			.process(new CsvProcessor())
			.split(simple("${body}")).convertBodyTo(DRJsonRequest.class)
			.marshal(gsonDataFormat)
			.convertBodyTo(String.class)
			.process(new DRProcessor())
			.endChoice();
		
		} catch (Exception e) {
			logger.error("exception:", e);
		}
	}
	
	public static void main(String[] args) throws Exception	{
		/*String strDate = "2020-01-27 12:23";
		DateTimeFormatter formatterMdyyyy = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
		DateTimeFormatter formatterYyyyMMdd = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime localDateTime = LocalDateTime.parse(strDate,formatterYyyyMMdd);	
		String strDocDate = localDateTime.format(formatterMdyyyy);
		System.out.println(strDocDate);*/
		
		String strDate1 = "2020-01-27 12:23:000";
		DateTimeFormatter formatterYyyyMMdd1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime localDateTime1 = LocalDateTime.parse(strDate1,formatterYyyyMMdd1);	
		System.out.println(localDateTime1);
		
		
	} 
	

}

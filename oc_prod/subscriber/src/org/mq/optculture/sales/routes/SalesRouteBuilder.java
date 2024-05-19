package org.mq.optculture.sales.routes;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.bind.JAXBContext;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.dataformat.zipfile.ZipSplitter;
import org.apache.camel.model.dataformat.JsonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.clapper.util.io.FileUtil;
import org.mq.marketer.campaign.beans.ETLFileUploadLogs;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ETLFileUploadLogDaoForDML;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.sales.json.DRJsonRequest;
import org.mq.optculture.sales.model.CrystalReport;
import org.mq.optculture.sales.processors.JsonProcessor;
import org.mq.optculture.sales.processors.XmlProcessor;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;






public class SalesRouteBuilder extends RouteBuilder{

	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private String ETLPath=PropertyUtil.getPropertyValue("ETLFileSource");
	
	
	private EmailQueueDaoForDML emailQueueDaoForDML = null;
	private  ETLFileUploadLogDaoForDML etlFileUploadLogDaoForDML=null;
	
	public SalesRouteBuilder()
	{
		try
		{
		ServiceLocator locator = ServiceLocator.getInstance();
		emailQueueDaoForDML = (EmailQueueDaoForDML)locator.getDAOForDMLByName("emailQueueDaoForDML");
		etlFileUploadLogDaoForDML = (ETLFileUploadLogDaoForDML)locator.getDAOForDMLByName("etlFileUploadLogDaoForDML"); 
		   
		}catch(Exception e) {logger.error("exception", e);
		}
	}
	
	public void configure() throws Exception {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(CrystalReport.class);
			JaxbDataFormat jaxbDataFormat = new JaxbDataFormat(jaxbContext);
			JsonDataFormat gsonDataFormat = new JsonDataFormat(JsonLibrary.Gson);
			String strETLDir="file:"+Paths.get(ETLPath).toString();
			
			//String strSourceDir = strETLDir+"?recursive=true&minDepth=4"
			//		+ "&move=./../../backup";
			
		    String strSourceDir = strETLDir+"?recursive=true&minDepth=4&delete=true&moveFailed=./../../error";
					
			
			from(strSourceDir).routeId("sales_route")
			.autoStartup(true).startupOrder(1)
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
			       	 exchange.setProperty("ETLUserName", strUserDir);
			       	 String strFailedFilesPath = Paths.get(strUserDir,"failed", fileNameNoExt).toString();
			       	 String strProcessPath = Paths.get(strUserDir,"processed", fileNameNoExt).toString();
			       	 long longTime = new Date().getTime();
			         exchange.setProperty("fileName", fileNameNoExt+"_"+longTime+".xml");
			         in.setHeader("userDir",Paths.get(strUserDir,"source","files",camelFileNoExt));
			       	 in.setHeader("strTime", longTime+"");
			         in.setHeader("failedFilesPath",strFailedFilesPath);
			         in.setHeader("processPath",strProcessPath);
			         in.setHeader("camelFileExt",camelFileExt);
			         
			         in.setHeader("CamelFileNameLower",fileName.toLowerCase());
			         
			         exchange.setProperty("failedPath", Paths.get(strUserDir,"failed"));
			         exchange.setProperty("processedPath", Paths.get(ETLPath,strUserDir,"processed"));
			         
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
			}).to(strETLDir+"?fileName=${header.processPath}_${header.strTime}"+"."+"${header.camelFileExt}")
			.onException(Exception.class)
			.handled(true).log(LoggingLevel.ERROR, "Exception")
			.process(new Processor() {
		         public void process(Exchange exchange) throws Exception {
		               Exception exception = (Exception) exchange.getProperty(Exchange.EXCEPTION_CAUGHT);
		              
		               String strETLUserName = exchange.getProperty("ETLUserName").toString();
		               String strOrgId= Utility.getOnlyOrgId(strETLUserName);
		   			   String strUserName=Utility.getOnlyUserName(strETLUserName);
		   			   String strFileName = exchange.getProperty("fileNameNoExt").toString()+"_"+exchange.getProperty("fileTime").toString()+".xml";
					 
		   			   logger.error(exception.getLocalizedMessage()+",FileName="+strFileName);
		       		   String emailSubject = "OptCulture Alert: "
		       		   		+ "XML file not processed (Username: "+strUserName+")";
					   String emailMessage  = PropertyUtil.getPropertyValueFromDB(OCConstants.ETL_INVALID_EMAIL_TEMPLATE);
					   String fbbSupportEmails =PropertyUtil.getPropertyValueFromDB("FBBAlertToEmailId");
					   
					   Calendar cal = Calendar. getInstance();
					   emailMessage = emailMessage.replace(OCConstants.ETL_MAIL_TIMESTAMP, new Date().toString()); 
					   emailMessage = emailMessage.replace(OCConstants.ETL_MAIL_FILE_NAME, strFileName);
					   emailMessage = emailMessage.replace(OCConstants.ETL_MAIL_USER_NAME, strUserName);
					   emailMessage = emailMessage.replace(OCConstants.ETL_MAIL_ORG_ID, strOrgId);
					   emailMessage = emailMessage.replace(OCConstants.ETL_MAIL_FILE_LOCATION, exchange.getProperty("failedPath").toString());
					  
					  
					   
					   Message in = exchange.getIn();
					   String fileName = in.getHeader("CamelFileName", String.class);
					   Path path = Paths.get(fileName); 
					   Path rootDir = path.getParent().getParent().getParent().getFileName();
				       String strUserDir = rootDir.toString();
					   exchange.setProperty("processedPath", Paths.get(ETLPath,strUserDir,"processed"));
					   String type = Constants.EQ_TYPE_SUPPORT_ALERT;
					   String status = Constants.SUPPORT_STATUS_ACTIVE;
					   String[] fbbSupportEmailsArr=new String[0];
					    if(fbbSupportEmails!=null)
					     fbbSupportEmailsArr = fbbSupportEmails.split(",");
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
					    logger.info("alert mail is saved on email queue "+fbbSupportEmail);
					   }		  
					    ETLFileUploadLogs fileUploadLogs = new ETLFileUploadLogs();
						fileUploadLogs.setFileName(exchange.getProperty("fileName").toString());
						fileUploadLogs.setUploadTime(Calendar.getInstance());
						fileUploadLogs.setUserId(Long.parseLong(exchange.getProperty("userId").toString()));
						fileUploadLogs.setRecordCount(0);
						fileUploadLogs.setFileStatus("Failed");
						fileUploadLogs.setComments("Xml Format not supported");
						String strProcessedFile = Paths.get(exchange.getProperty("processedPath").toString(),exchange.getProperty("fileName").toString()).toString();
						fileUploadLogs.setProcessedFilePath(strProcessedFile);
						etlFileUploadLogDaoForDML.saveOrUpdate(fileUploadLogs);
				  
			     }
		    })
			.to(strETLDir+"?fileName=${header.failedFilesPath}_${header.strTime}.xml").end()
			
			.threads(5,10) //internally uses executor service
			 .choice()
			 .when(header("CamelFileNameLower").endsWith(".zip")) //case check
			 .split(new ZipSplitter())
			
			 .streaming().convertBodyTo(String.class) 
			     .choice().when(body().isNotNull())
			     .to(strETLDir+"?fileName=${header.userDir}_${date:now:yyyyMMddssSSS}.xml")
			     .end()
		         .endChoice()
		     .endChoice() 
			 .when(header("CamelFileNameLower").endsWith(".xml"))
			.log(LoggingLevel.INFO,  "xml Processing has started...")
			.unmarshal(jaxbDataFormat)
			.process(new XmlProcessor())
			.split(simple("${body}")).convertBodyTo(DRJsonRequest.class)
			.marshal(gsonDataFormat)
			.convertBodyTo(String.class)
			.process(new JsonProcessor());
			
		} catch (Exception e) {
			logger.error("exception:", e);
		}

	}
	
	
	
	public static void main(String[] args) throws Exception
	
	{
		
		
		String strDir ="ramakrishna__org__ocqa/source/files/fileName.xml";
		
		 Path path = Paths.get(strDir); 
	     Path root = path.getParent().getParent().getParent().getFileName();
	     System.out.println("Root Path: "+ root); 
		
	     String fileNameNoExt = FileUtil.getFileNameNoExtension(new File(strDir).getName());
	     System.out.println("no ext:"+fileNameNoExt);
	     
	     
	   String strFilePath = Paths.get(root.toString(),"failed", "sales.xml").toString();
			System.out.println(strFilePath);	
		
			
		System.out.println(new Date().getTime());
			
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Calendar cal_Two = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		System.out.println(cal_Two.getTime());
			
		String strDate = "9/7/2019";
		SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
		Date date = sdf.parse(strDate);
		String strDocDate = sdf.format(date);
		System.out.println(strDocDate);
		
		SimpleDateFormat newDateFormat1 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		 date = sdf.parse(strDate);
		strDocDate = newDateFormat1.format(date);
		
		System.out.println(strDocDate);
		
		String fieldValue = "9/7/2019 00:00:00";
		
		String dateformat = "MM/dd/yyyy HH:mm:ss";
		DateFormat formatter ; 
		Date date1 ; 
		formatter = new SimpleDateFormat(dateformat);
		date1 = (Date)formatter.parse(fieldValue.trim()); 
		Calendar dobCal =  new MyCalendar(Calendar.getInstance(), null, MyCalendar.dateFormatMap.get(dateformat));
		dobCal.setTime(date1);
		
		System.out.println(dobCal);
		
		
		//String fbbSupportEmails =PropertyUtil.getPropertyValueFromDB("FBBAlertToEmailId");
		String fbbSupportEmails ="test@test.com,test@test.com";
		String[] fbbSupportEmailsArr=new String[0];
		//fbbSupportEmails=null;
				
		if(fbbSupportEmails!=null)
		    fbbSupportEmailsArr = fbbSupportEmails.split(",");
		   for (String fbbSupportEmail : fbbSupportEmailsArr) 
		   {
			   if(!Utility.validateEmail(fbbSupportEmail)) 
				   {
				   logger.error("invalid Email "+fbbSupportEmail);
				     continue;
				   }
			  
			   logger.info("alert mail is saved on email queue"+fbbSupportEmail);
		   }
		  
		   //String strName = mapGroupHeader.get("GroupNameCustomerName1");
		
		   String strName = "93982841P.AVINASH, P.AVINASH - 95558825";
		   strName = "93982841P.AVINASH, P.AVINASH";
		   strName="";
		   try
		   {
		    String[] strNames = strName.split(",");
			String strLastName = strNames[0];
			strLastName = strLastName.replaceAll("\\d","");
			String[] strNameCustId= strNames[1].split("-");
			String strFirstName = strNameCustId[0].trim();
			String strCustSid = strNameCustId[1].trim();
			System.out.println("lastname:"+strLastName);
			System.out.println("firstName:"+strFirstName);
			System.out.println("CustSid:"+strCustSid);
		   }catch(Exception e) {logger.error(" error "+e);}
		/*try
		{
		System.out.println("Application context started");
		CamelContext camelContext = new DefaultCamelContext();
		RouteBuilder salesRouteBuilder = new SalesRouteBuilder();
		camelContext.addRoutes(salesRouteBuilder);
		camelContext.start();
		List<Route> lstRoutes=  camelContext.getRoutes();
		logger.info("routes" +lstRoutes);
		camelContext.startRoute("sales_route");
		Thread.sleep(5 * 1000);
		camelContext.stopRoute("sales_route");
		}catch(Exception e)
		{
			e.printStackTrace();
			logger.log(Level.ERROR, e.getMessage());
		} */
	} 
	

}

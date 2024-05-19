package org.mq.optculture.business.generalMailSender;
import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.service.ExternalSMTPSupportSender;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.HeaderInfo;
import org.mq.optculture.model.StatusInfo;
import org.mq.optculture.model.generalMailSender.GeneralMailSenderResponse;
import org.mq.optculture.model.generalMailSender.MailSenderRequestObject;
import org.mq.optculture.model.generalMailSender.MailSenderResponseObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;

public class GeneralMailSenderServiceImpl implements GeneralMailSenderBusinessService{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)
			throws BaseServiceException {
		try {
			
			logger.debug("-------entered processRequest---------");
			MailSenderResponseObject mailSenderResponseObject=null;
			BaseResponseObject baseResponseObject=new BaseResponseObject();
			if(baseRequestObject.getAction().equals(OCConstants.GENERAL_MAIL_SENDER_REQUEST)) {
				
				//json to object
				String json=baseRequestObject.getJsonValue();
				Gson gson=new Gson();
				MailSenderRequestObject mailSenderRequestObject;
				try {
					mailSenderRequestObject = gson.fromJson(json, MailSenderRequestObject.class);
				} catch (Exception e) {
					logger.error("Exception ::",e);
					StatusInfo statusInfo = new StatusInfo("600000",PropertyUtil.getErrorMessage(600000, OCConstants.ERROR_GENERALMAILSENDER_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					mailSenderResponseObject=prepareResponse(statusInfo);
					String jsonValue=gson.toJson(mailSenderResponseObject);
					baseResponseObject.setJsonValue(jsonValue);
					baseResponseObject.setAction(OCConstants.GENERAL_MAIL_SENDER_REQUEST);
					return baseResponseObject;
				}
				GeneralMailSenderBusinessService generalMailSenderBusinessService=(GeneralMailSenderBusinessService) ServiceLocator.getInstance().getServiceByName(OCConstants.GENERAL_MAIL_SENDER_BUSINESS_SERVICE);
				if(mailSenderRequestObject==null){
					logger.debug("root is null"+mailSenderRequestObject);
					StatusInfo statusInfo=new StatusInfo("600001",PropertyUtil.getErrorMessage(600001,OCConstants.ERROR_GENERALMAILSENDER_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					HeaderInfo header= new HeaderInfo();
					try{
						header = gson.fromJson(json, HeaderInfo.class);
					}catch (Exception e1){
						logger.info("Wrong Header from Json");
					}
					mailSenderResponseObject = prepareResponse(statusInfo);
					mailSenderResponseObject.setHeader(header);
					String jsonValue=gson.toJson(mailSenderResponseObject);
					baseResponseObject.setJsonValue(jsonValue);
					baseResponseObject.setAction(OCConstants.GENERAL_MAIL_SENDER_REQUEST);
					return baseResponseObject;
				}
				if(mailSenderRequestObject.getGENERALMAILSENDERREQUEST()==null)
				{
					mailSenderResponseObject = (MailSenderResponseObject) generalMailSenderBusinessService.processMailSending(mailSenderRequestObject);
				}else{
					OldGeneralMailSenderServiceImpl oldGeneralMailSenderServiceImpl = new OldGeneralMailSenderServiceImpl();
					mailSenderResponseObject = (MailSenderResponseObject) oldGeneralMailSenderServiceImpl.processMailSending(mailSenderRequestObject);
				}
				//object to json
				String jsonValue=gson.toJson(mailSenderResponseObject);
				baseResponseObject.setJsonValue(jsonValue);
				baseResponseObject.setAction(OCConstants.GENERAL_MAIL_SENDER_REQUEST);
			}
			logger.debug("-------exit  processRequest---------");
			return baseResponseObject;
		} catch (Exception e) {
			logger.error("Exception  ::", e);
			throw new BaseServiceException("Exception occured while processing processRequest::::: ", e);
		}
	}//processRequest
	
	@Override
	public MailSenderResponseObject processMailSending(
			MailSenderRequestObject mailSenderRequestObject)
					throws BaseServiceException {
		String serverName = PropertyUtil.getPropertyValue("subscriberIp");
		ExternalSMTPSupportSender externalSMTPSupportSender=new ExternalSMTPSupportSender();
		MailSenderResponseObject mailSenderResponseObject=null;
		StatusInfo statusInfo=null;
		
		try {
			logger.debug("-------entered processMailSending---------");
			UsersDao usersDao=(UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			String messageHeader="";
			String htmlContent="";
			String textContent="";
			statusInfo=validateRootObject(mailSenderRequestObject);
			String source = mailSenderRequestObject.getHeader().getSource();
			
				
			
			HeaderInfo header = null;
			try
			{
				header = mailSenderRequestObject.getHeader();
				header.setSource(null);
			}catch(Exception e)	{
				logger.info("Header is null");
			}
			if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getStatus())){
				mailSenderResponseObject=prepareResponse(statusInfo);
				mailSenderResponseObject.setHeader(header);
				return mailSenderResponseObject;
			}
			//validate userDetails 
			String token=mailSenderRequestObject.getUser().getToken();
			String userName=mailSenderRequestObject.getUser().getUserName();
			String userOrg=mailSenderRequestObject.getUser().getOrganizationId();
			statusInfo=validateUserDetails(token,userName,userOrg);
			if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getStatus())){
				mailSenderResponseObject=prepareResponse(statusInfo);
				mailSenderResponseObject.setHeader(header);
				return mailSenderResponseObject;
			}
			//validate user
			Users user = usersDao.findByToken(userName+ Constants.USER_AND_ORG_SEPARATOR +userOrg, token );
			statusInfo=validateUser(user);
			if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getStatus())){
				mailSenderResponseObject=prepareResponse(statusInfo);
				mailSenderResponseObject.setHeader(header);
				return mailSenderResponseObject;
			}
			//validate fromEmail
			statusInfo=validateFromEmail(mailSenderRequestObject.getMailInfo().getFromEmail());
			if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getStatus())){
				mailSenderResponseObject=prepareResponse(statusInfo);
				mailSenderResponseObject.setHeader(header);
				return mailSenderResponseObject;
			}
			String subject=mailSenderRequestObject.getMailInfo().getSubject();
			if(subject != null && subject.length()>=500)
			{
				statusInfo=new StatusInfo("600011",PropertyUtil.getErrorMessage(600011, OCConstants.ERROR_GENERALMAILSENDER_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				mailSenderResponseObject = new MailSenderResponseObject();
				mailSenderResponseObject.setStatus(statusInfo);
				mailSenderResponseObject.setHeader(header);
				return mailSenderResponseObject;
			}
			if( !(source.equalsIgnoreCase("optsync") || source.equalsIgnoreCase("optconnect")))
			{
				statusInfo=new StatusInfo("600019",PropertyUtil.getErrorMessage(600019, OCConstants.ERROR_GENERALMAILSENDER_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				mailSenderResponseObject = new MailSenderResponseObject();
				mailSenderResponseObject.setStatus(statusInfo);
				mailSenderResponseObject.setHeader(header);
				return mailSenderResponseObject;
			}
			String fromEmail = mailSenderRequestObject.getMailInfo().getFromEmail();
			String fromName = mailSenderRequestObject.getMailInfo().getFromName();
			if(fromName != null && fromName.trim().length() > 0)
				{
				fromEmail = fromName + "<" + fromEmail + ">";;
				}
			int totalMailRecivers = 0;
			//validate toEmail
			String to[]=mailSenderRequestObject.getMailInfo().getToEmail();
			if(to != null){ 
				totalMailRecivers += to.length;
			for(int i=0;i<to.length;i++) {
				statusInfo=validateToEmail(to[i]);
				if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getStatus())){
					mailSenderResponseObject=prepareResponse(statusInfo);
					mailSenderResponseObject.setHeader(header);
					return mailSenderResponseObject;
				}
			}
			}
			//validate ccEmail
			
			String cc[]=mailSenderRequestObject.getMailInfo().getCcEmail();
			if(cc != null)
			{ totalMailRecivers += cc.length;
			for(int i=0;i<cc.length;i++) {
				statusInfo=validateCcEmail(cc[i]);
				if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getStatus())){
					mailSenderResponseObject=prepareResponse(statusInfo);
					mailSenderResponseObject.setHeader(header);
					return mailSenderResponseObject;
				}
			}
			}
			//validate bccEmail
			String bcc[]=mailSenderRequestObject.getMailInfo().getBccEmail();
			if(bcc != null)
			{ totalMailRecivers += bcc.length;
			for(int i=0;i<bcc.length;i++) {
				statusInfo=validateBccEmail(bcc[i]);
				if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getStatus())){
					mailSenderResponseObject=prepareResponse(statusInfo);
					mailSenderResponseObject.setHeader(header);
					return mailSenderResponseObject;
				}
			}
			}
			if(totalMailRecivers == 0)
			{
				statusInfo=new StatusInfo("600016",PropertyUtil.getErrorMessage(600016, OCConstants.ERROR_GENERALMAILSENDER_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				mailSenderResponseObject = new MailSenderResponseObject();
				mailSenderResponseObject.setStatus(statusInfo);
				mailSenderResponseObject.setHeader(header);
				return mailSenderResponseObject;
			}
			
			
			String body=mailSenderRequestObject.getMailInfo().getBody();
			body=body.replace("\n", "<br/>");
			//System.out.println("body-------"+body);
			htmlContent=PropertyUtil.getPropertyValueFromDB("generalMailText");
			htmlContent=htmlContent.replace(OCConstants.GENERAL_MAIL_SENDER_PLACEHOLDER_BODY, body);
			//System.out.println("htmlContent---------------"+htmlContent);
			textContent=htmlContent;
			//sending email
			//if(user.getVmta().equalsIgnoreCase("SendGridAPI")){
			if(Constants.VMTA_SENDGRIDAPI.equalsIgnoreCase(user.getVmta().getVmtaName())){	
				try {
					if(user != null) {
						messageHeader =  "{\"unique_args\": {\"userId\": \""+ user.getUserId() +"\" ,\"EmailType\" : \""+OCConstants.GENERAL_MAIL_SENDER +"\",\"ServerName\": \""+ serverName +"\" }}";
					}
					//logger.debug("SENDING THROUGH sendGridAPI ...>>>>>>>>>>>>");
					//htmlContent = htmlContent.replace("[sentId]", supportTicket.getTicketId()+"");
					//textContent = textContent.replace("[sentId]", supportTicket.getTicketId()+"");
					byte[] attachment = mailSenderRequestObject.getMailInfo().getAttachment();
					if(attachment != null && attachment.length !=0)
					{
						/*String mimeType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(attachment));
						String ext = MIMETypeUtil.fileExtensionForMIMEType(mimeType);*/
					String fileName =  mailSenderRequestObject.getMailInfo().getFileName();
					String	ext = mailSenderRequestObject.getMailInfo().getExtension();
					if(fileName == null || ext == null || fileName.trim().isEmpty() || ext.trim().isEmpty())
					{
						statusInfo=new StatusInfo("600017",PropertyUtil.getErrorMessage(600017, OCConstants.ERROR_GENERALMAILSENDER_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						mailSenderResponseObject = new MailSenderResponseObject();
						mailSenderResponseObject.setStatus(statusInfo);
						mailSenderResponseObject.setHeader(header);
						return mailSenderResponseObject;
						
					}			
						
					fileName+="."+ext.toLowerCase();
					String filePath = PropertyUtil.getPropertyValue("usersSupportDirectory").trim()+File.separator+fileName;
					File file = new File(filePath);
					FileUtils.writeByteArrayToFile(file, attachment);
					
					statusInfo = validateAttachment(file);
					if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getStatus())){
						mailSenderResponseObject=prepareResponse(statusInfo);
						mailSenderResponseObject.setHeader(header);
							if(file.delete()) {
								logger.info(file.getName()+" deleted successfully ");
							}else {
								logger.info(file.getName()+" not deleted ");
							}
						return mailSenderResponseObject;
					}
					
					externalSMTPSupportSender.submitGeneralMail(messageHeader, htmlContent, textContent, fromEmail, 
							 subject, to, cc, bcc, fileName, filePath);
					}else {
					externalSMTPSupportSender.submitGeneralMail(messageHeader, htmlContent, textContent, fromEmail,
								 subject,to, cc, bcc);
					}
				} catch (Exception e) {
					logger.debug("Exception while sending through sendGridAPI .. returning ",e);
				}
			}//if
			statusInfo=new StatusInfo("0","Request sent to email gateway successfully",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			mailSenderResponseObject=prepareResponse(statusInfo);
			mailSenderResponseObject.setHeader(header);
			logger.debug("-------exit  processMailSending---------");
			return mailSenderResponseObject;
		
		}catch (Exception e) {
			logger.error("Exception  ::", e);
			throw new BaseServiceException("Exception occured while processing processMailSending::::: ", e);
		}
		/*finally {
			mailSenderResponseObject=prepareResponse(statusInfo);
			return mailSenderResponseObject;
			
		}*/
	}//processMailSending

	private StatusInfo validateRootObject(MailSenderRequestObject mailSenderRequestObject) {
		logger.debug("-------entered validateRootObject---------");
		StatusInfo statusInfo=null;
		if(mailSenderRequestObject==null){
			logger.debug("root is null"+mailSenderRequestObject);
			statusInfo=new StatusInfo("600001",PropertyUtil.getErrorMessage(600001,OCConstants.ERROR_GENERALMAILSENDER_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		
		if(mailSenderRequestObject.getHeader()==null) {
			statusInfo=new StatusInfo("600003",PropertyUtil.getErrorMessage(600003,OCConstants.ERROR_GENERALMAILSENDER_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		logger.info("Header is :: "+mailSenderRequestObject.getHeader());
		if(mailSenderRequestObject.getHeader().getRequestId()==null ||mailSenderRequestObject.getHeader().getRequestId().isEmpty() || 
				mailSenderRequestObject.getHeader().getSource()==null ||mailSenderRequestObject.getHeader().getSource().isEmpty() ||
				mailSenderRequestObject.getHeader().getRequestDate()==null ||mailSenderRequestObject.getHeader().getRequestDate().isEmpty()) {
			
			statusInfo=new StatusInfo("600004",PropertyUtil.getErrorMessage(600004,OCConstants.ERROR_GENERALMAILSENDER_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		if(mailSenderRequestObject.getUser()==null) {
			statusInfo=new StatusInfo("600005",PropertyUtil.getErrorMessage(600005, OCConstants.ERROR_GENERALMAILSENDER_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		logger.info("User details :: "+mailSenderRequestObject.getUser());
		if(mailSenderRequestObject.getMailInfo()==null){
			statusInfo=new StatusInfo("600012",PropertyUtil.getErrorMessage(600012, OCConstants.ERROR_GENERALMAILSENDER_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		}
		
		DateTimeFormatter formatter = DateTimeFormat.forPattern(MyCalendar.FORMAT_DATETIME_STYEAR);
		try{
			String requestDate = mailSenderRequestObject.getHeader().getRequestDate().trim();
			formatter.parseDateTime(requestDate);
			if(requestDate.substring(0, 5).charAt(4) != '-')
			{
				statusInfo=new StatusInfo("600018",PropertyUtil.getErrorMessage(600018,OCConstants.ERROR_GENERALMAILSENDER_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
			
		}catch (Exception e) {
			statusInfo=new StatusInfo("600018",PropertyUtil.getErrorMessage(600018,OCConstants.ERROR_GENERALMAILSENDER_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		
		/*if(MyCalendar.dateString2Calendar(mailSenderRequestObject.getHeader().getRequestDate().trim())==null)
		{
			statusInfo=new StatusInfo("600018",PropertyUtil.getErrorMessage(600018,OCConstants.ERROR_GENERALMAILSENDER_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}*/
		logger.debug("-------exit  validateRootObject---------");
		return statusInfo;
	}//validateRootObject
	
	private StatusInfo validateFromEmail(String fromEmail)throws BaseServiceException {
		logger.debug("-------entered validateFromEmail---------");
		StatusInfo  statusInfo=null;
		if(fromEmail==null || fromEmail.isEmpty() || !Utility.validateEmail(fromEmail.trim())) {
			statusInfo=new StatusInfo("600009",PropertyUtil.getErrorMessage(600009, OCConstants.ERROR_GENERALMAILSENDER_FLAG)+fromEmail,OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		logger.debug("-------exit  validateFromEmail---------");
		return statusInfo;
	}//validateFromEmail
	
	private StatusInfo validateToEmail(String toEmail)throws BaseServiceException {
		logger.debug("-------entered validateToEmail---------");
		StatusInfo  statusInfo=null;
		if(toEmail==null || toEmail.isEmpty() || !Utility.validateEmail(toEmail)) {
			statusInfo=new StatusInfo("600010",PropertyUtil.getErrorMessage(600010, OCConstants.ERROR_GENERALMAILSENDER_FLAG)+toEmail,OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		logger.debug("-------exit  validateToEmail---------");
		return statusInfo;
	}//validateToEmail
	private StatusInfo validateCcEmail(String ccEmail)throws BaseServiceException {
		logger.debug("-------entered validateCcEmail---------");
		StatusInfo  statusInfo=null;
		if(ccEmail==null || ccEmail.isEmpty() || !Utility.validateEmail(ccEmail)) {
			statusInfo=new StatusInfo("600014",PropertyUtil.getErrorMessage(600014, OCConstants.ERROR_GENERALMAILSENDER_FLAG)+ccEmail,OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		logger.debug("-------exit  validateCcEmail---------");
		return statusInfo;
	}//validateCcEmail
	private StatusInfo validateBccEmail(String bccEmail)throws BaseServiceException {
		logger.debug("-------entered validateBccEmail---------");
		StatusInfo  statusInfo=null;
		if(bccEmail==null || bccEmail.isEmpty() || !Utility.validateEmail(bccEmail)) {
			statusInfo=new StatusInfo("600015",PropertyUtil.getErrorMessage(600015, OCConstants.ERROR_GENERALMAILSENDER_FLAG)+bccEmail,OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		logger.debug("-------exit  validateBccEmail---------");
		return statusInfo;
	}//validateBccEmail
	private StatusInfo validateUser(Users user)throws BaseServiceException {
		logger.debug("-------entered validateUser---------");
		StatusInfo statusInfo=null;
		if(user == null){
			statusInfo=new StatusInfo("600008", PropertyUtil.getErrorMessage(600008,OCConstants.ERROR_GENERALMAILSENDER_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		logger.debug("-------exit  validateUser---------");
		return statusInfo;
	}//validateUser

	private StatusInfo validateUserDetails(String token, String userName,
			String userOrg)throws BaseServiceException {
		logger.debug("-------entered validateUserDetails---------");
		StatusInfo statusInfo=null;
		if(token == null){
			statusInfo=new StatusInfo("600006", PropertyUtil.getErrorMessage(600006, OCConstants.ERROR_GENERALMAILSENDER_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		if(userName == null || userOrg == null){
			statusInfo=new StatusInfo("600007", PropertyUtil.getErrorMessage(600007, OCConstants.ERROR_GENERALMAILSENDER_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		logger.debug("-------exit  validateUserDetails---------");
		return statusInfo;
	}//validateUserDetails
	private StatusInfo validateAttachment(File file)
	{
		logger.info("file size "+file.length()+" bytes");
		StatusInfo statusInfo=null;
		if(file.length() > 1048576)
		{
			statusInfo=new StatusInfo("600013", PropertyUtil.getErrorMessage(600013, OCConstants.ERROR_GENERALMAILSENDER_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		return statusInfo;
	}

	private MailSenderResponseObject prepareResponse(StatusInfo statusInfo) throws BaseServiceException {
		logger.debug("-------entered prepareResponse---------");
		MailSenderResponseObject mailSenderResponseObject=new MailSenderResponseObject();
		mailSenderResponseObject.setStatus(statusInfo);
		logger.debug("-------exit  prepareResponse---------");
		return mailSenderResponseObject;
	}//prepareResponse
	
	// Added for new json format, will be removed in future.
	private class OldGeneralMailSenderServiceImpl {

		public MailSenderResponseObject processMailSending(
				MailSenderRequestObject mailSenderRequestObject)
						throws BaseServiceException {
			String serverName = PropertyUtil.getPropertyValue("subscriberIp");
			ExternalSMTPSupportSender externalSMTPSupportSender=new ExternalSMTPSupportSender();
			MailSenderResponseObject mailSenderResponseObject=null;
			StatusInfo statusInfo=null;
			try {
				logger.debug("-------entered processMailSending---------");
				UsersDao usersDao=(UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
				String messageHeader="";
				String htmlContent="";
				String textContent="";
				statusInfo=validateRootObject(mailSenderRequestObject);
				if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
					mailSenderResponseObject=prepareResponse(statusInfo);
					return mailSenderResponseObject;
				}
				//validate userDetails 
				String token=mailSenderRequestObject.getGENERALMAILSENDERREQUEST().getUSERDETAILS().getTOKEN();
				String userName=mailSenderRequestObject.getGENERALMAILSENDERREQUEST().getUSERDETAILS().getUSERNAME();
				String userOrg=mailSenderRequestObject.getGENERALMAILSENDERREQUEST().getUSERDETAILS().getORGID();
				statusInfo=validateUserDetails(token,userName,userOrg);
				if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
					mailSenderResponseObject=prepareResponse(statusInfo);
					return mailSenderResponseObject;
				}
				//validate user
				Users user = usersDao.findByToken(userName+ Constants.USER_AND_ORG_SEPARATOR +userOrg, token );
				statusInfo=validateUser(user);
				if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
					mailSenderResponseObject=prepareResponse(statusInfo);
					return mailSenderResponseObject;
				}
				//validate fromEmail
				statusInfo=validateFromEmail(mailSenderRequestObject.getGENERALMAILSENDERREQUEST().getMAILINFO().getFROM_EMAIL());
				if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
					mailSenderResponseObject=prepareResponse(statusInfo);
					return mailSenderResponseObject;
				}
				//validate toEmail
				String to[]=mailSenderRequestObject.getGENERALMAILSENDERREQUEST().getMAILINFO().getTO_EMAIL();
				for(int i=0;i<to.length;i++) {
					statusInfo=validateToEmail(to[i]);
					if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
						mailSenderResponseObject=prepareResponse(statusInfo);
						return mailSenderResponseObject;
					}
				} 
				String subject=mailSenderRequestObject.getGENERALMAILSENDERREQUEST().getMAILINFO().getSUBJECT();
				String body=mailSenderRequestObject.getGENERALMAILSENDERREQUEST().getMAILINFO().getBODY();
				body=body.replace("\n", "<br/>");
				//System.out.println("body-------"+body);
				htmlContent=PropertyUtil.getPropertyValueFromDB("generalMailText");
				htmlContent=htmlContent.replace(OCConstants.GENERAL_MAIL_SENDER_PLACEHOLDER_BODY, body);
				//System.out.println("htmlContent---------------"+htmlContent);
				textContent=htmlContent;
				//sending email
				//if(user.getVmta().equalsIgnoreCase("SendGridAPI")){
				if(Constants.VMTA_SENDGRIDAPI.equalsIgnoreCase(user.getVmta().getVmtaName())){	
					try {
						if(user != null) {
							messageHeader =  "{\"unique_args\": {\"userId\": \""+ user.getUserId() +"\" ,\"EmailType\" : \""+OCConstants.GENERAL_MAIL_SENDER +"\",\"ServerName\": \""+ serverName +"\" }}";
						}
						//logger.debug("SENDING THROUGH sendGridAPI ...>>>>>>>>>>>>");
						//htmlContent = htmlContent.replace("[sentId]", supportTicket.getTicketId()+"");
						//textContent = textContent.replace("[sentId]", supportTicket.getTicketId()+"");
						externalSMTPSupportSender.submitGeneralMail(messageHeader, htmlContent, textContent,mailSenderRequestObject.getGENERALMAILSENDERREQUEST().getMAILINFO().getFROM_EMAIL(),
								subject,to, new String[]{}, new String[]{});
					} catch (Exception e) {
						logger.debug("Exception while sending through sendGridAPI .. returning ",e);
					}
				}//if
				statusInfo=new StatusInfo("0","Mail Sent Successfully",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				mailSenderResponseObject=prepareResponse(statusInfo);
				logger.debug("-------exit  processMailSending---------");
				return mailSenderResponseObject;
			}catch (Exception e) {
				logger.error("Exception  ::", e);
				throw new BaseServiceException("Exception occured while processing processMailSending::::: ", e);
			}
			/*finally {
				mailSenderResponseObject=prepareResponse(statusInfo);
				return mailSenderResponseObject;
				
			}*/
		}//processMailSending

		private StatusInfo validateRootObject(MailSenderRequestObject mailSenderRequestObject) {
			logger.debug("-------entered validateRootObject---------");
			StatusInfo statusInfo=null;
			if(mailSenderRequestObject==null){
				logger.debug("root is null"+mailSenderRequestObject);
				statusInfo=new StatusInfo("600001",PropertyUtil.getErrorMessage(600001,OCConstants.ERROR_GENERALMAILSENDER_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
			if(mailSenderRequestObject.getGENERALMAILSENDERREQUEST()==null){
				statusInfo=new StatusInfo("600002",PropertyUtil.getErrorMessage(600002,OCConstants.ERROR_GENERALMAILSENDER_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
			if(mailSenderRequestObject.getGENERALMAILSENDERREQUEST().getHEADERINFO()==null) {
				statusInfo=new StatusInfo("600003",PropertyUtil.getErrorMessage(600003,OCConstants.ERROR_GENERALMAILSENDER_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
			if(mailSenderRequestObject.getGENERALMAILSENDERREQUEST().getHEADERINFO().getREQUESTID()==null ||mailSenderRequestObject.getGENERALMAILSENDERREQUEST().getHEADERINFO().getREQUESTID().isEmpty()) {
				statusInfo=new StatusInfo("600004",PropertyUtil.getErrorMessage(600004,OCConstants.ERROR_GENERALMAILSENDER_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
			if(mailSenderRequestObject.getGENERALMAILSENDERREQUEST().getUSERDETAILS()==null) {
				statusInfo=new StatusInfo("600005",PropertyUtil.getErrorMessage(600005, OCConstants.ERROR_GENERALMAILSENDER_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
			if(mailSenderRequestObject.getGENERALMAILSENDERREQUEST().getMAILINFO()==null){
				statusInfo=new StatusInfo("600012",PropertyUtil.getErrorMessage(600012, OCConstants.ERROR_GENERALMAILSENDER_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			}
			logger.debug("-------exit  validateRootObject---------");
			return statusInfo;
		}//validateRootObject
		
		private StatusInfo validateFromEmail(String fromEmail)throws BaseServiceException {
			logger.debug("-------entered validateFromEmail---------");
			StatusInfo  statusInfo=null;
			if(fromEmail==null || fromEmail.isEmpty() || !Utility.validateEmail(fromEmail.trim())) {
				statusInfo=new StatusInfo("600009",PropertyUtil.getErrorMessage(600009, OCConstants.ERROR_GENERALMAILSENDER_FLAG)+fromEmail,OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
			logger.debug("-------exit  validateFromEmail---------");
			return statusInfo;
		}//validateFromEmail
		
		private StatusInfo validateToEmail(String toEmail)throws BaseServiceException {
			logger.debug("-------entered validateToEmail---------");
			StatusInfo  statusInfo=null;
			if(toEmail==null || toEmail.isEmpty() || !Utility.validateEmail(toEmail)) {
				statusInfo=new StatusInfo("600010",PropertyUtil.getErrorMessage(600010, OCConstants.ERROR_GENERALMAILSENDER_FLAG)+toEmail,OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
			logger.debug("-------exit  validateToEmail---------");
			return statusInfo;
		}//validateToEmail
		
		private StatusInfo validateUser(Users user)throws BaseServiceException {
			logger.debug("-------entered validateUser---------");
			StatusInfo statusInfo=null;
			if(user == null){
				statusInfo=new StatusInfo("600008", PropertyUtil.getErrorMessage(600008,OCConstants.ERROR_GENERALMAILSENDER_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
			logger.debug("-------exit  validateUser---------");
			return statusInfo;
		}//validateUser

		private StatusInfo validateUserDetails(String token, String userName,
				String userOrg)throws BaseServiceException {
			logger.debug("-------entered validateUserDetails---------");
			StatusInfo statusInfo=null;
			if(token == null){
				statusInfo=new StatusInfo("600006", PropertyUtil.getErrorMessage(600006, OCConstants.ERROR_GENERALMAILSENDER_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
			if(userName == null || userOrg == null){
				statusInfo=new StatusInfo("600007", PropertyUtil.getErrorMessage(600007, OCConstants.ERROR_GENERALMAILSENDER_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
			logger.debug("-------exit  validateUserDetails---------");
			return statusInfo;
		}//validateUserDetails

		private MailSenderResponseObject prepareResponse(StatusInfo statusInfo) throws BaseServiceException {
			logger.debug("-------entered prepareResponse---------");
			MailSenderResponseObject mailSenderResponseObject=new MailSenderResponseObject();
			GeneralMailSenderResponse generalMailSenderResponse=new GeneralMailSenderResponse();
			generalMailSenderResponse.setSTATUSINFO(statusInfo);
			mailSenderResponseObject.setGENERALMAILSENDERRESPONSE(generalMailSenderResponse);
			logger.debug("-------exit  prepareResponse---------");
			return mailSenderResponseObject;
		}//prepareResponse
		
	}

}

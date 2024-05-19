package org.mq.marketer.campaign.controller.contacts;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.ExportFileDetails;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.SegmentRules;
import org.mq.marketer.campaign.beans.UserSMSGateway;
import org.mq.marketer.campaign.beans.UserSMSSenderId;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.service.CaptiwayToSMSApiGateway;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.ContactsLoyaltyStageDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.ExportFileDetailsDao;
import org.mq.marketer.campaign.dao.ExportFileDetailsDaoForDML;
import org.mq.marketer.campaign.dao.OCSMSGatewayDao;
import org.mq.marketer.campaign.dao.UserSMSGatewayDao;
import org.mq.marketer.campaign.dao.UserSMSSenderIdDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.marketer.campaign.general.SalesQueryGenerator;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.SmsQueueHelper;
import org.mq.optculture.data.dao.JdbcResultsetHandler;
import org.mq.optculture.utils.OCCSVWriter;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Checkbox;

public class ExportThread implements Runnable {
	public Queue<Object[]> uploadQueue = new LinkedList();
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	Object[] pollObj;
	private ContactsDao contactsDao;
	protected ResultSet rs;	
	public void run(){		
		try {
			while(pollQueue()) {
				logger.debug("Thread starts here ");
				String threadType = (String)pollObj[0];
				logger.info("0|1|2|3|4| are |"+ pollObj[0]+ "|" +pollObj[1]+ "|" +pollObj[2]+ "|" +pollObj[3]+ "|" +pollObj[4]+"|");
				if(threadType != null && threadType.equals("segment")) {
					segmentExport();
				}				
				logger.debug("Thread ends here ");
				pollObj = null;
				//System.gc();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("run.............",e);
			e.printStackTrace();
		}
	}	
	private void segmentExport(){

		try {
			Long startTime = System.currentTimeMillis();
			JdbcResultsetHandler jdbcResultsetHandler= null;
			ManageSegmentsController manageSegmentsController = null;
			SegmentRules segmentRulesObj = null;
			String headerStr = Constants.STRING_NILL;
			String qryType = Constants.STRING_NILL;
			String filePathStr = Constants.STRING_NILL;
			Map<String,POSMapping> displayPosMappingMap = null;
			String emailIdStr = Constants.STRING_NILL;
			String mobileStr = Constants.STRING_NILL;
			Users userobj = null;
			String fileName = Constants.STRING_NILL;
			ExportFileDetails fileExportDetailsObj = null;
			BufferedWriter bw = null;
			StringBuffer sb = null;
			String query = Constants.STRING_NILL;
			ViewSegmentsController viewSegmentsController=null;
			ContactsDao contactsDao = null;			
			try {
				contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
				segmentRulesObj = (SegmentRules)pollObj[1];
				headerStr = (String)pollObj[2];
				qryType = (String)pollObj[3];
				filePathStr = (String)pollObj[4];
				displayPosMappingMap = (Map<String,POSMapping>)pollObj[5];
				emailIdStr = (String)pollObj[6];
				mobileStr = (String)pollObj[7];
				userobj = (Users)pollObj[8];
				query =(String)pollObj[9];
				
				fileName = filePathStr.substring(filePathStr.lastIndexOf("/")+1,filePathStr.length());
				
				//createdDB object
				fileExportDetailsObj = new ExportFileDetails();
				fileExportDetailsObj.setUserId(userobj.getUserId());
				fileExportDetailsObj.setOrgId(userobj.getUserOrganization().getUserOrgId());
				fileExportDetailsObj.setFileType("segment");
				fileExportDetailsObj.setFilePath(filePathStr);
				fileExportDetailsObj.setFileName(fileName);
				fileExportDetailsObj.setStatus(Constants.EXPORT_FILE_PROCESSING);
				
				
				fileExportDetailsObj.setCreatedTime(Calendar.getInstance());
				
				ExportFileDetailsDao fileExportDetailsDao = 
						(ExportFileDetailsDao)ServiceLocator.getInstance().getDAOByName("exportFileDetailsDao");
				ExportFileDetailsDaoForDML fileExportDetailsDaoForDML = 
						(ExportFileDetailsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("exportFileDetailsDaoForDML");
				//fileExportDetailsDao.saveOrUpdate(fileExportDetailsObj);			
				fileExportDetailsDaoForDML.saveOrUpdate(fileExportDetailsObj);	
				
				/*String headerStr = "";
				String query = "select ";
				//boolean loyaltyflag = false;
				int i = 0;
				//String exportHeaderStrArr[] = new String[chekedChkboxList.size()];
				for (Checkbox checkbox : chekedChkboxList) {
					//String chkBoxLbl = checkbox.getLabel();
					if(!loyaltyflag && (chkBoxLbl.equals("Card Pin") || chkBoxLbl.equals("Card Pin") || 
							chkBoxLbl.equals("Card Pin") || chkBoxLbl.equals("Card Pin"))){
						loyaltyflag = true;
					}
					if(headerStr.trim().length()  > 0) {
						headerStr +=",";
					}
					headerStr += "\""+checkbox.getLabel()+"\"";
					if(query.indexOf((String)checkbox.getValue()) == -1 ) {
					query += checkbox.getValue();
					}
					//exportHeaderStrArr[i++] = checkbox.getLabel();
				}
				query = query.substring(0, query.length()-2);
				logger.info("Headers are  :: "+headerStr);*/
				
				bw = new BufferedWriter(new FileWriter(filePathStr));
				sb = new StringBuffer();
				sb.append(headerStr);
				sb.append("\r\n");
				bw.write(sb.toString());	
				jdbcResultsetHandler = new JdbcResultsetHandler();
				OCCSVWriter csvWriter = new OCCSVWriter(bw);
				int threshold=50000;
				logger.info("Records per chunk : "+threshold);
				try{					
					int initialIndex=0;	
					long user_id=userobj.getUserId();
					logger.info("user id------>>>"+user_id);
					logger.info("Query being executed ======================> "+query);
					int recordSize=0;
					int loopCount =0;
					do	{		
						recordSize=1;
						String query1 = "("+query+") limit "+initialIndex +","+threshold;
						jdbcResultsetHandler.executeStmt(query1,"fromExportThread");
						ResultSet rs=jdbcResultsetHandler.getResultSet();				
						csvWriter.writeAll(rs, false);
						bw.flush();
						csvWriter.flush();
						recordSize=jdbcResultsetHandler.totalRecordsSize();
						loopCount++;
						logger.info("completed writing chunk: "+loopCount);
						logger.info("Time taken to write chunk "+loopCount+" :::: "+(System.currentTimeMillis()-startTime));
						initialIndex = loopCount*threshold+1;
					}
					while(recordSize >= threshold);				
				}			
				catch(Exception e){
					logger.info("Exception while writing into file ",e);
				}
				finally{
					bw.flush();
					csvWriter.flush();
					bw.close();
					csvWriter.close();
					csvWriter = null;
				}			
				//Updated Same DB object
				fileExportDetailsObj.setStatus(Constants.EXPORT_FILE_COMPLETED);
				//fileExportDetailsDao.saveOrUpdate(fileExportDetailsObj);
				fileExportDetailsDaoForDML.saveOrUpdate(fileExportDetailsObj);
				logger.info("email id is  ::: and mobile  id is  :::"+emailIdStr,mobileStr);
				//send a  mail
				if(emailIdStr != null && emailIdStr.trim().length() > 0) {
					String subjectStr = "Dear "+Utility.getOnlyUserName(userobj.getUserName())+",";
					StringBuffer messageStrBuff = new StringBuffer("<html><head></head><body> "+subjectStr+" "
							+ "<br />"
							+ "<br />"
							+ "The file \"Refer a "+fileName+" is exported and is available now for downloading. To access this file,<br />"
									+ "please log-in to your OptCulture account and go to the Downloads page under Contacts menu.<br /><br />"
									+ "Have a nice day! <br />"
									+ "Support Team </body></html>");
					EmailQueue emailQueue = 
							new EmailQueue("File Exporting Completed.",messageStrBuff.toString(),"File Export","Active",emailIdStr,MyCalendar.getNewCalendar(),userobj);
					EmailQueueDao emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName("emailQueueDao");
					EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("emailQueueDaoForDML");
					//emailQueueDao.saveOrUpdate(emailQueue);
					emailQueueDaoForDML.saveOrUpdate(emailQueue);
				}
				//send sms
				if(mobileStr != null && mobileStr.trim().length()  > 0){
					
					sendTestSMS(mobileStr.trim(), "File exporting is completed. Please check in Downloads",userobj);
				}
			}catch(Exception e){
				logger.info("exception while writing to file ",e);
			}finally{
				if(jdbcResultsetHandler != null) jdbcResultsetHandler.destroy();
				jdbcResultsetHandler = null; segmentRulesObj = null; headerStr = null; qryType = null; filePathStr = null; displayPosMappingMap = null; emailIdStr = null;
				mobileStr = null; userobj = null; fileName = null; fileExportDetailsObj = null; bw = null; sb = null; query = null;
				//System.gc();
			}
			logger.info("time taken to write to file through exportthread :::: "+(System.currentTimeMillis()-startTime));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("segmentExport",e);
			e.printStackTrace();
		}
	
	} // segmentExport
	
	
	

	public boolean sendTestSMS(String mblNumber, String sendingMsg, Users userObj) {
		
		try{
			
			CaptiwayToSMSApiGateway captiwayToSMSApiGateway = 
					(CaptiwayToSMSApiGateway)ServiceLocator.getInstance().getBeanByName("captiwayToSMSApiGateway");
			
//			CaptiwayToSMSApiGateway captiwayToSMSApiGateway = (CaptiwayToSMSApiGateway)SpringUtil.getBean("captiwayToSMSApiGateway");
			
			UserSMSGatewayDao userSMSGatewayDao = (UserSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERSMSGATEWAY_DAO);
			OCSMSGatewayDao ocSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.OCSMSGATEWAY_DAO);
			String account = SMSStatusCodes.defaultAccountMap.get(userObj.getCountryType());
			
			
			String[] accountArr = null;
			//String[] accountArr = account.split(Constants.ADDR_COL_DELIMETER);
			if(account != null){
				accountArr = account.split(Constants.ADDR_COL_DELIMETER);
			}else{
				accountArr = new String[4];
				// for account==null case from user_sms_gateway PR route will be selected and msg will be sent.
				
				
				
				UserSMSGateway userSMSGateway = userSMSGatewayDao.findByUserId(userObj.getUserId(),"PR");
				if(userSMSGateway == null) return false;
				
				OCSMSGateway ocSMSGateway = ocSMSGatewayDao.findById(userSMSGateway.getGatewayId());
				if(ocSMSGateway == null) return false;
				
				accountArr[0]=ocSMSGateway.getGatewayName();
				accountArr[1]=ocSMSGateway.getUserId();
				accountArr[2]=ocSMSGateway.getGatewayName().equals(Constants.USER_SMSTOOL_BSMS_ITS_PAKISTAN) ? ocSMSGateway.getAPIId() : ocSMSGateway.getPwd();
				accountArr[3]=ocSMSGateway.getSenderId();
				
				
				
				
			}
			
			
			String senderId = (String)accountArr[3];
			
			if(senderId == null || senderId.length() == 0) {//if SMS sender id is not selected
				logger.error("User sender id not existed , so retuning... and mobile number is  ::"+mblNumber);
				return false;
			}
			
			String userSMSDetails = Constants.STRING_NILL;
			
			logger.debug(" my sms details are ====>"+userSMSDetails);
			
			
			
			logger.debug("mobile numbers to send Test SMS are====>"+mblNumber);
			
			
				
			/***split,truncate,ignore  or send the SMS message as it is depend on the selected sending option ******/
			
			logger.info("get the user sms count=====>"+userObj.getSmsCount());
			if(userObj.getSmsCount()==0){
				logger.info("We are unable to send sms to for this mobile  because of user have less sms credits .."+userObj.getSmsCount());
				return false; 
				
			}
			
			
			captiwayToSMSApiGateway.sendMessageFromOC(accountArr[0], accountArr[1], accountArr[2], sendingMsg, ""+mblNumber, senderId, (accountArr.length==5 ? accountArr[4] : null));
			
			/**
		     * Update the Used SMS count
		     */
		    try{
		     //UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		     UsersDaoForDML usersDaoForDML = (UsersDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.USERS_DAOForDML);
		     //usersDao.updateUsedSMSCount(userObj.getUserId(), 1);
		     usersDaoForDML.updateUsedSMSCount(userObj.getUserId(), 1);
		    }catch(Exception exception){
		     logger.error("Exception while updating the Used SMS count",exception);
		    }
//			captiwayToSMSApiGateway.sendToSMSApi(ocgateway, sendingMsg, ""+mblNumber, msgType, "9848495956", ""+mblNumber, "1", senderId);
		    
		    /**
		     * Update SMS Queue 
		     */
		    SmsQueueHelper smsQueueHelper = new SmsQueueHelper();
		    smsQueueHelper.updateSMSQueue(mblNumber, sendingMsg, Constants.SMS_MSG_TYPE_EXPORT,  userObj, senderId);	
		}catch (NumberFormatException e) {
			logger.error("** Exception",e);
		}catch (Exception e) {
			logger.error("** Exception while sending a test SMS",e);
		}
		return true;	
	}
	boolean pollQueue(){
		pollObj = uploadQueue.poll();
		if(pollObj!=null) {
			return true;
		} 
		else {
			return false;
		}
	} // pollQueue
}
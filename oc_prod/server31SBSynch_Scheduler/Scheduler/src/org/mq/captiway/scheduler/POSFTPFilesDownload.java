package org.mq.captiway.scheduler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.POSFileLogs;
import org.mq.captiway.scheduler.beans.UserPosFTPSettings;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.MessagesDao;
import org.mq.captiway.scheduler.dao.POSFileLogDao;
import org.mq.captiway.scheduler.dao.UserPosFTPSettingsDao;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.FTPPOSFileAccess;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.scheduler.utility.Utility;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class POSFTPFilesDownload extends TimerTask implements ApplicationContextAware {
	
	private static final  Logger logger = LogManager.getLogger(Constants.FILE_PROCESS_LOGGER);
	
//	private String userParentDir;
	UserPosFTPSettingsDao userPosFTPSettingsDao  = null;
	//FTPPOSFileAccess ftpPOSFileAccess;
	Long userId = null;
//	private Users users;
//	private UsersDao usersDao;
	POSFileLogDao posFileLogDao;
	MessagesDao messagesDao;
	
	private ApplicationContext context;
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		this.context = context;
	}
	private static String sourceFolder = PropertyUtil.getPropertyValue("sourceFolder"); //home/ocftpuser
	private static String unProcessedFolder = PropertyUtil.getPropertyValue("unProcessedFolder");//home/ocftpuser/timestampedcsvFile
	/*private static String doneFolder = PropertyUtil.getPropertyValue("done");
	private static String backUpFoldLocationStr = PropertyUtil.getPropertyValue("backUpFoldLocationStr");*/ //home/ocftpuser/processedfolder
	public POSFTPFilesDownload() {}
	
	public void run() {
		
		logger.info("<<<<<<< Thread Started now >>>>>>");
//		userParentDir = PropertyUtil.getPropertyValue("usersParentDirectory");
//		usersDao = (UsersDao)context.getBean("usersDao");
		posFileLogDao = (POSFileLogDao)context.getBean("posFileLogDao");
		messagesDao = (MessagesDao)context.getBean("messagesDao");
		
		//moviing ftp Files From ocftpUser to unProcessed Folder
		moveFilesFromOcftToUnProces();
		
		
		//get the Details of POS settings and check the files from FTP /SFTP Host Address
		getPOSFilesFromFTP();
		
	}
	
	
	
	private void moveFilesFromOcftToUnProces() {
//		String outboxoptIntelPath = PropertyUtil.getPropertyValue("outboxoptintel");
		String donePath = PropertyUtil.getPropertyValue("doneoptintel");
		try {
			//if(logger.isInfoEnabled()) 
			logger.info(" sourceFolder :: "+sourceFolder);
			File sourceFold = new File(sourceFolder);
			
			if(!sourceFold.exists()  || !sourceFold.isDirectory()) {
				logger.error(sourceFold+" is not existed or is not directory");
				return;
			}
			
			File[] sourceFileLst =  sourceFold.listFiles();
			
			if(sourceFileLst == null || sourceFileLst.length == 0) {
				logger.error("0 files Existed from Ocftpuser "); 
				return;
			}
			
			String fileName = "";
			
			for (File file : sourceFileLst) {
				
				if(file.isDirectory()) {
					logger.error("Is Directory"); 
					continue;
				}
				
				File tempFile = null;
				String ext =  FilenameUtils.getExtension(file.getName());
				
				if(ext.equalsIgnoreCase("zip")){
					tempFile = Utility.unzip(file.getAbsolutePath(), sourceFolder);
					
					//move to Outbox for this zip file
					if(tempFile == null){
						//write failed message on outbox
						Utility.writeMessage("unzip the file ","Temporary", file.getName(), ext,"Not Processed");
						
						//Move zip in to outbox/optintel
						file.renameTo(new File(donePath+File.separator+file.getName()));
						continue;
					}
					file.renameTo(new File(donePath+File.separator+file.getName()));
				}else {
					tempFile = file;
				}
				
				
				if(!tempFile.canRead()) {
					logger.error("unable to read the file  ::"+tempFile.getName());
					continue;
				}
				
//			if(!file.canWrite()) continue;
				
				fileName = tempFile.getName().trim();
				//logger.info("File Name ::"+fileName);
				
				if(!(fileName.trim().toLowerCase().endsWith(".csv"))) {
					logger.error(fileName+" is not csv file ...");
					continue;
				}
				
				//file.renameTo(new File(unProcessedFolder+file.getName()+"_"+System.currentTimeMillis()));
				Calendar cCal = Calendar.getInstance();
				
				String currTimeStr = MyCalendar.calendarToString(cCal, MyCalendar.FORMAT_YEARTOSEC);
				String extensionStr = FilenameUtils.getExtension(tempFile.getName());
//				String fileNamewithOutExt = tempFile.getName().substring(0, tempFile.getName().length()-4);
				
				boolean flag = tempFile.renameTo(new File(unProcessedFolder+tempFile.getName()+"_OC_"+currTimeStr + cCal.get(Calendar.MILLISECOND)+ "."+extensionStr));
				
				logger.info(tempFile.getName()+" File is  move to timestamped csv folder is  :: "+flag);
								
			} //for
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception, error getting while moving the csv file from ocftp folder to unprocessed folder... "+e);
		}
		
		
		
		
	} // moveFilesFromOcftToUnProces
	
	
	
	private void getPOSFilesFromFTP() {
		
		
		//logger.info("userPosSettingList.size() is :.."+userPosSettingList.size());
		
		
		File sourceFold = new File(unProcessedFolder);
		File[] sourceFileLst =  sourceFold.listFiles();
		
		if(sourceFileLst == null || sourceFileLst.length == 0) {
			logger.error("source folder is eampty for the user id");
			return ;
		}
		
		for (File eachTimeStampedFile : sourceFileLst) {
			 try {
				 if(!(eachTimeStampedFile.getName().trim().toLowerCase().endsWith(".csv"))) {
						logger.error(eachTimeStampedFile.getName().trim()+" is not csv file ...");
						continue;
					}
				FTPPOSFileAccess.fileCopyMoveToUserAndProcessFolder(eachTimeStampedFile);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception :::",e);
			}
		}
		
		
		/*userPosFTPSettingsDao = (UserPosFTPSettingsDao)context.getBean("userPosFTPSettingsDao");
		List<UserPosFTPSettings> userPosSettingList = userPosFTPSettingsDao.findListByTimeDiff();
		
		if(userPosSettingList == null || userPosSettingList.size() == 0) {
//			logger.info("No Data exists for FTP settings:..");
			return;
		}
		
		for (UserPosFTPSettings userPosFTPSettings : userPosSettingList) {
				
				
				
				userId = userPosFTPSettings.getUserId();
				users = usersDao.find(userId);
				
				userPosDirPath = checkDirExists(users);
				
				//Processing the POS file for each user..
				//ftpConnFlag = FTPPOSFileAccess.getFtpConnection(userPosFTPSettings,userPosDirPath,users,posFileLogDao,messagesDao,userPosFTPSettingsDao);
				
				if(fileProcessFlag){
					logger.info(" is processing sucesfully in to user and process folders");
				}
					
		}
				*/

		
		
		
	}
	
	
	/*private String  checkDirExists(Users userObj) {
		String source = "";
		String tempStr = "";
		source = userParentDir + "/" + userObj.getUserName()  + "/POSList";
		File tempFile ;
		File sorceFiles = new File(source);
	
		if(!sorceFiles.exists()) {
			sorceFiles.mkdir();
		}
		
		String sourceFileStr =  source + "/sourceFiles/";
		
		sorceFiles = new File(sourceFileStr);
		if(!sorceFiles.exists()) {
			sorceFiles.mkdir();
		}
		
		//Source salesFiles
		tempStr = sourceFileStr +"/salesFiles";
		tempFile = new File(tempStr.trim());
		if(!tempFile.exists()) {
			tempFile.mkdir();
		}
		
		
		//Source skuFiles
		tempStr = sourceFileStr +"/skuFiles";
		tempFile = new File(tempStr.trim());
		if(!tempFile.exists()) {
			tempFile.mkdir();
		}
		
		//Source contactFiles
		tempStr = sourceFileStr +"/contactFiles";
		tempFile = new File(tempStr.trim());
		if(!tempFile.exists()) {
			tempFile.mkdir();
		}
		
		//Source home Processed
		tempStr = sourceFileStr +"/homesPassedFiles";
		tempFile = new File(tempStr.trim());
		if(!tempFile.exists()) {
			tempFile.mkdir();
		}
		
		String destPath   = source + "/processedFiles";
		
		//processed contactFiles
		tempStr = destPath +"/salesFiles";
		tempFile = new File(tempStr.trim());
		if(!tempFile.exists()) {
			tempFile.mkdir();
		}
				
				
		//processed skuFiles
		String procSKUStr = destPath +"/SkuFiles";
		tempFile = new File(procSKUStr);
		if(!tempFile.exists()) {
			tempFile.mkdir();
		}
				
		//processed contactFiles
		tempStr = destPath +"/contactFiles";
		tempFile = new File(tempStr.trim());
		if(!tempFile.exists()) {
			tempFile.mkdir();
		}
		
		//processed contactFiles
		tempStr = destPath +"/homesPassedFiles";
		tempFile = new File(tempStr.trim());
		if(!tempFile.exists()) {
			tempFile.mkdir();
		}
		
		
		File destDir = new File(destPath);
//		logger.info("destFile is exist..."+destDir.exists() +"::destFile name is>>"+destDir.getName());
		
		//File destFile = new File(destPath + "/" + sourceFile.getName());
		
		if(!destDir.exists()) {
			
//			logger.info("directory not exist create new ...");
			destDir.mkdir();
		}
		
		File unProcessDir = new File(source+"/unProcessFiles");
		if(!unProcessDir.exists()) {
			
			unProcessDir.mkdir();
		}
		
		return source;
	}
	*/
	
	
	
	
	
}

package org.mq.captiway.scheduler.utility;
/*import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;*/

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.Messages;
import org.mq.captiway.scheduler.beans.POSFileLogs;
import org.mq.captiway.scheduler.beans.UserPosFTPSettings;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.AutoSmsQueueDao;
import org.mq.captiway.scheduler.dao.MessagesDao;
import org.mq.captiway.scheduler.dao.MessagesDaoForDML;
import org.mq.captiway.scheduler.dao.POSFileLogDao;
import org.mq.captiway.scheduler.dao.POSFileLogDaoForDML;
import org.mq.captiway.scheduler.dao.UserPosFTPSettingsDao;
import org.mq.captiway.scheduler.dao.UserPosFTPSettingsDaoForDML;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.services.SMSCConnector.mainMethodListener;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;


public class FTPPOSFileAccess {
	
	
	private static final  Logger logger = LogManager.getLogger(Constants.FILE_PROCESS_LOGGER);
	
	public FTPPOSFileAccess() {}

	
	
	private static String unProcessedFolder =  PropertyUtil.getPropertyValue("unProcessedFolder");
	//private static String backUpFoldLocationStr = PropertyUtil.getPropertyValue("backUpFoldLocationStr");
		
		
	/*public static boolean getFtpConnection(UserPosFTPSettings userPosFTPSettings,String userPosdestLocation, Users users,
			POSFileLogDao posFileLogDao,MessagesDao messagesDao,MessagesDaoForDML messagesDaoForDML, UserPosFTPSettingsDao userPosFTPSettingsDao) {
		
		
		try {
			String userName = userPosFTPSettings.getFtpUserName();
			String pwd = userPosFTPSettings.getFtpPassword(); 
			String hostAddr = userPosFTPSettings.getHostAddress();
			String workingDir = userPosFTPSettings.getDirectoryPath();
			String fileFormat = userPosFTPSettings.getFileFormat();
			String fileType = userPosFTPSettings.getFileType();
			
//			if(logger.isInfoEnabled()) logger.info("waiting  Connection for ::"+hostAddr );
			
//			if(hostAddr.equalsIgnoreCase("localhost") && userName.equals("ocftpuser") ) {
			
			if(hostAddr.equalsIgnoreCase("localhost")  ) {
				if(copyFileToUserDir(fileFormat,fileType, workingDir, userPosdestLocation, users, 
												userPosFTPSettings,	posFileLogDao,userPosFTPSettingsDao)==true) {
					return true;
				}else return false;
			}
			
			String tempLoc = userPosdestLocation;
			FTPClient ftpClient  = new FTPClient();
			
			try {
				//HostAddress
				ftpClient.connect(hostAddr);
				
				if(logger.isInfoEnabled()) logger.info("Connection  ::"+ftpClient.isConnected());
				if(logger.isInfoEnabled()) logger.info("username  ::"+userName);
				if(logger.isInfoEnabled()) logger.info("Password   ::"+pwd);
				//UserName and Password
				ftpClient.login(userName, pwd);
//				ftpClient.setType(FTPClient.TYPE_BINARY);
			} catch (Exception e) {
				
				logger.error("Exception ::::", e);
				logger.info("unable get the FTP Connection for this  ::"+hostAddr);
				Messages messages = new Messages("Connection Failure",	" FTP/SFTP connection failure ", " Unable to get the POS files.. due to connection failure . Please verify the FTP/SFTP credentials",  Calendar.getInstance(), 
						"Inbox",false ,"Info", users);
				//messagesDao.saveOrUpdate(messages);
				messagesDaoForDML.saveOrUpdate(messages);
				return false;
				try {
					
					if(getSFTPConnection(userName, pwd, hostAddr, workingDir, fileFormat, fileType,   destLocation, users, userPosFTPSettingsDao)) {
						
						return true;
					}else
						return false;
					
				} catch(Exception f){
					if(users !=null && messagesDao!= null) {
						
						
					}
					return false;
				} 	
				
			}
			
			if(logger.isInfoEnabled()) logger.info("workingDir="+workingDir);
			
			if(!workingDir.trim().equals(".")) {
				ftpClient.changeWorkingDirectory(workingDir);
			}
			
			downloadFileFromFtp(hostAddr ,ftpClient , fileFormat,fileType,  userPosdestLocation, 
										users ,posFileLogDao,userPosFTPSettingsDao,userPosFTPSettings);
			
			return true;
		}  catch (Exception e) {
			logger.error("Exception ::::", e);
			return false;
		}
	}*/
	
	
//	public void  downloadFileFromFtp(String userName, String pwd,String hostAddr,String workingDir, String destLocation) {
	public static void  downloadFileFromFtp(String hostAddr , FTPClient ftpClient, String fileFormat,
									String fileType, String destLocation,Users users,POSFileLogDao posFileLogDao,
									UserPosFTPSettingsDao  userPosFTPSettingsDao,UserPosFTPSettings userPosFTPSettings) {
		
		try {
			String tempLoc = destLocation;
//			FTPDataTransferListener listner = new FTPDataTransferListener();
			
			FTPFile[] ftpFiles = ftpClient.listFiles();
			
			UserPosFTPSettingsDaoForDML userPosFTPSettingsDaoForDML = (UserPosFTPSettingsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.USER_POS_FTP_SETTINGS_DAO_FOR_DML);
			
			if(ftpFiles.length == 0) {
//				logger.info("no files Exist from this Ftp Address..");
				return ;
			}
			
//			logger.info(" FtpFile size is ::"+ftpFiles.length);
			
			InputStream in = null;
			BufferedInputStream bis = null;
			OutputStream os = null;
			
			String extStr = "";
//			String withoutExtStr = "";
			String ftpFileName  = "";
			File destFile = null;
			for (FTPFile ftpFile : ftpFiles) {
				
					try {
						extStr = "";
						
						if(!ftpFile.isFile()) continue;
						
						ftpFileName = ftpFile.getName();
						if(logger.isInfoEnabled()) logger.info("File Name is ::" +ftpFileName );
						
						
						if(!ftpFileName.contains(fileType)) {
							
							continue;
						}
						/*else if(fileNameList.contains(ftpFileName)) {
							logger.info("--2---");
							continue;
						}*/
						else if(!ftpFileName.startsWith(fileFormat)) { 
							//TODO for reguler Expressions
							continue;
						}
						
						if(ftpFile.getName().indexOf(".") != -1) {
							ftpFileName  = ftpFileName.substring(0, ftpFile.getName().lastIndexOf("."));
							if(logger.isInfoEnabled()) logger.info("withoutExtStr :"+ftpFileName);
							extStr = ftpFile.getName().substring(ftpFile.getName().lastIndexOf("."));
							if(logger.isInfoEnabled()) logger.info("extStr : "+extStr);
							
						}
						
						
//				ftpFileName = ftpFile.getName();
						
						
						if(ftpFileName.toUpperCase().contains("#CONTACTS#")) { 				// Contacts Files 
							destLocation = destLocation+"/sourceFiles/contactFiles/";
						}
						else if(ftpFileName.toUpperCase().contains("#SALES#")) {			 	//Sales File 
							destLocation = destLocation+"/sourceFiles/salesFiles/";
						}
						else if(ftpFileName.toUpperCase().contains("#SKU#")) { 				//SKU Files
							destLocation = destLocation+"/sourceFiles/skuFiles/";
						}else if(ftpFileName.toUpperCase().contains("#HOMESPASSED#")) { 				//BCRM Files
							destLocation = destLocation+"/sourceFiles/homesPassedFiles/";
						}
						else {																//UnProcesseFiles
							destLocation = destLocation+"/unProcessFiles/";
						}
						
						if(logger.isInfoEnabled()) logger.info("Dest location is ::"+destLocation);
//				ftpClient.completePendingCommand();
						
						//FileOutputStream fos = new FileOutputStream(destLocation+ftpFileName);
						String timeStampFilename = ftpFileName.trim()+extStr.trim()+"_"+System.currentTimeMillis();

						//set the file name as  _POSINCMPLETEPART before downloading...
						destFile = new File(destLocation + timeStampFilename + "_POSINCMPLETEPART");

						//BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destLocation+timeStampFilename));
						BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile));
						
						InputStream is  = ftpClient.retrieveFileStream(ftpFile.getName());
						if(logger.isInfoEnabled()) logger.info("input stream is ::" +is);
						
						byte[] buf = new byte[1024*5];
						
						if(is == null) {
							if(logger.isInfoEnabled()) logger.info("no such file:" + ftpFileName);
						}
						else {
							
							int count = 0;
							while ((count = is.read(buf)) >= 0) {
								bos.write(buf, 0, count);
							}
							
							bos.flush();
							bos.close();
							is.close();
							
							//rename the fileName  after downloading...
							String tempStr = destFile.getAbsolutePath();
							
							File renameFile = new File(tempStr.substring(0, tempStr.lastIndexOf("_POSINCMPLETEPART")));
							
							if(logger.isInfoEnabled()) logger.info("Rename FileName is  :: "+renameFile.getName());
							
							destFile.renameTo(renameFile);
							if(logger.isInfoEnabled()) logger.info(" After renaming the File name is >>"+destFile.getName());
							POSFileLogDaoForDML posFileLogDaoForDML = (POSFileLogDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("posFileLogDaoForDML");
							//save the file name entry in (pos_files_log) table
							//posFileLogDao.saveOrUpdate(new POSFileLogs(users.getUserId(), fileType, destFile.getName(), Calendar.getInstance()));
							posFileLogDaoForDML.saveOrUpdate(new POSFileLogs(users.getUserId(), fileType, destFile.getName(), Calendar.getInstance()));
							//set the Last Fetched Time
							userPosFTPSettings.setLastFetchedTime(Calendar.getInstance());
							//userPosFTPSettingsDao.saveOrUpdate(userPosFTPSettings);
							userPosFTPSettingsDaoForDML.saveOrUpdate(userPosFTPSettings);
							
							
						}
						ftpClient.completePendingCommand();   

						destLocation = tempLoc  ;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception ::::", e);
					}
				
			} // for
			ftpClient.disconnect();
			if(logger.isInfoEnabled()) logger.info("download complete..");
			
			
		} catch (IllegalStateException e) {
			logger.error("Exception ::::", e);
			return ;
		} catch (IOException e) {
			logger.error("Exception ::::", e);
			return ;
		} catch (Exception e) {
			logger.error("Exception ::::", e);
			return;
		}
		return ;
	} //

	
	//getSFTPConnection(userName, pwd, hostAddr, workingDir, fileFormat, fileType, fileNameList,  destLocation, users, userPosFTPSettingsDao)
	public static boolean getSFTPConnection(String userName, String pwd,String hostAddr,String workingDir, String fileFormat,
			String fileType, 	String destLocation , Users users ,UserPosFTPSettingsDao  userPosFTPSettingsDao) {
		
		try {
			Session 	session 	= null;
			Channel 	channel 	= null;
			ChannelSftp channelSftp = null; 
//			String tempLoc = destLocation;

			if(logger.isInfoEnabled()) logger.info("waiting for  SFTP Connection ..");
			JSch jsch = new JSch();
			
			try {
				session = jsch.getSession(userName, hostAddr);
				session.setPassword(pwd);
				java.util.Properties config = new java.util.Properties();
				config.put("StrictHostKeyChecking", "no");
				session.setConfig(config);
				session.connect();
				channel = session.openChannel("sftp");
				channel.connect();
				if(logger.isInfoEnabled()) logger.info("Got the SFTP Connection Now .."+ channel.isConnected());
				channelSftp = (ChannelSftp)channel;
				channelSftp.cd(workingDir);
			} catch (Exception e) {
				logger.error("Exception ::::", e);
				return false;
			}
			
			getSFTPFilesDownload(channelSftp, workingDir, fileFormat, fileType,  destLocation , users, userPosFTPSettingsDao);
			
			return true;
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::", e);
			return false;
		}
		
	}
	
	private static void getSFTPFilesDownload(ChannelSftp channelSftp, String workingDir, String fileFormat,
			
			String fileType, 	String destLocation , Users users ,UserPosFTPSettingsDao  userPosFTPSettingsDao) {

		try {
			String tempLoc = destLocation;
			if(logger.isInfoEnabled()) logger.info("workingDir ..."+workingDir);
			Vector sftpFilelist = channelSftp.ls(workingDir);
			
			if(sftpFilelist.size() == 0) {
				if(logger.isInfoEnabled()) logger.info("no SFTP files Exist from this  Address");
				return;
			}
			String sftpFileName = "";
			String extStr = "";
			
			for (int i=0; i<sftpFilelist.size(); i++) {
				
				extStr = "";
				com.jcraft.jsch.ChannelSftp.LsEntry lsEntry = (com.jcraft.jsch.ChannelSftp.LsEntry) sftpFilelist.get(i);
				sftpFileName = lsEntry.getFilename();
				if(logger.isInfoEnabled()) logger.info("sftpFileName is >>:: "+sftpFileName);
				
				if(!sftpFileName.startsWith(fileFormat)) {
					continue;
				}
				
				
				if (!sftpFileName.equals(".") && !	sftpFileName.equals("..")) {
					
					
					
					if(logger.isInfoEnabled()) logger.info("Downloading file :: "+lsEntry.getFilename());
					if(logger.isInfoEnabled()) logger.info("destLocation  is :: "+destLocation);
					
					
					if(!lsEntry.getFilename().contains(fileType)) {
						continue;
					}
					else if(!lsEntry.getFilename().startsWith(fileFormat)) { 
						//TODO for reguler Expressions
						continue;
					}
					
					if(sftpFileName.indexOf(".") != -1) {
						sftpFileName  = sftpFileName.substring(0, sftpFileName.lastIndexOf("."));
						if(logger.isInfoEnabled()) logger.info("withoutExtStr :"+sftpFileName);
						extStr = lsEntry.getFilename().substring(lsEntry.getFilename().lastIndexOf("."));
						if(logger.isInfoEnabled()) logger.info("extStr : "+extStr);
						
					}
				
					if(lsEntry.getFilename().toUpperCase().contains("#CONTACTS#")) { 				// Contacts Files 
						destLocation = destLocation+"/sourceFiles/contactFiles/";
					}else if(lsEntry.getFilename().toUpperCase().contains("#SALES#")) {			 	//Sales File 
						destLocation = destLocation+"/sourceFiles/salesFiles/";
					}else if(lsEntry.getFilename().toUpperCase().contains("#SKU#")) { 				//SKU Files
						destLocation = destLocation+"/sourceFiles/skuFiles/";
					}
					else {																//UnProcesseFiles
						destLocation = destLocation+"/unProcessFiles/";
					}
					
					
					String timeStampFilename = sftpFileName.trim()+"_"+System.currentTimeMillis()+extStr.trim();
				
					//change the file Name before DownLoading
					File destFile = new File(destLocation +timeStampFilename+"_POSINCMPLETEPART");
					
					if(logger.isInfoEnabled()) logger.info("Working Dir is :: "+workingDir+"/"+lsEntry.getFilename());
					if(logger.isInfoEnabled()) logger.info("Dest Dir is :: "+destLocation +sftpFileName.trim()+"_POSINCMPLETEPART");
					
					//Downloading file 
					channelSftp.get(workingDir+"/"+lsEntry.getFilename(), destLocation +timeStampFilename+"_POSINCMPLETEPART");
					
					//rename the fileName  after downloading...
					String tempStr = destFile.getAbsolutePath();
					
					
					//After downloading change the file name
					destFile.renameTo(new File(tempStr.substring(0, tempStr.lastIndexOf("_POSINCMPLETEPART"))));
					
					/*//remove file from SFTP location 
					channelSftp.rm(lsEntry.getFilename());
					*/
					
					
					//save the file name entry in (pos_files_log) table
//					userPosFTPSettingsDao.saveFileName(users.getUserId(), fileType , destFile.getName());
					
					
				}
				destLocation = tempLoc ;
			} // for
			
			channelSftp.disconnect();
			if(logger.isInfoEnabled()) logger.info("SFTP Files downloaded completly..");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::", e);
		}
		
	}
	

	
	
	//Copying files to UserDir if the  HostAddress is Localhost
	private static boolean copyFileToUserDir(String fileFormat,String fileType, String wrkingDir,
										String inDestLocation,Users users,UserPosFTPSettings userPosFTPSettings ,
										POSFileLogDao posFileLogDao,UserPosFTPSettingsDao userPosFTPSettingsDao) {
		
		try {
			
			
			
			
			File sourceFold = new File(unProcessedFolder);
			File[] sourceFileLst =  sourceFold.listFiles();
			
			UserPosFTPSettingsDaoForDML userPosFTPSettingsDaoForDML = (UserPosFTPSettingsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.USER_POS_FTP_SETTINGS_DAO_FOR_DML);
			
			if(sourceFileLst == null || sourceFileLst.length == 0) {
				logger.error("source folder is eampty for the user id"+users.getUserId());
				return false;
			}
			
			
			
			
			
//			logger.info(" >>>> Source File List size is::"+sourceFileLst.length);
			String fileName = "";
			
			
			for (File file : sourceFileLst) {
				String destLocation = inDestLocation;
				
				try {
					fileName = file.getName().trim();
					
					if(!file.canRead()) {
						if(logger.isInfoEnabled()) logger.info("File cannot be read properly.. fileName="+fileName);
						continue;
					}
					
					//logger.info("File Name ::"+fileName + " >>> ::and fileFormat::"+fileFormat +">>>>> file Type:: "+fileType);
					
					if(!fileFormat.endsWith("\\S*")) {
						fileFormat = fileFormat + "\\S*";
					}
						
					//check the file Name with regex 
					if(!fileName.matches(fileFormat)) {
						//logger.info("Formated  file not match with fileName.. ...");
						continue;
					}
					/*else {
						logger.info("Format Matched :: File Name ::"+fileName + " >>> ::and fileFormat::"+fileFormat +">>>>> file Type:: "+fileType);
					}*/
					
					if(logger.isInfoEnabled()) logger.info("File Name ::"+fileName + " >>> ::and fileFormat::"+fileFormat +">>>>> file Type:: "+fileType);
					
					//logger.info("File Type is >>> ::"+fileType.trim() +" >>> contact Type >> "+Constants.POS_MAPPING_TYPE_CONTACTS);
					
					if(fileType.trim().startsWith(Constants.POS_MAPPING_TYPE_CONTACTS.trim())) { 
						destLocation = destLocation+"/sourceFiles/contactFiles/";
					}
					else if(fileType.trim().startsWith(Constants.POS_MAPPING_TYPE_SALES.trim())) {	
						destLocation = destLocation+"/sourceFiles/salesFiles/";
					}
					else if(fileType.trim().startsWith(Constants.POS_MAPPING_TYPE_SKU.trim())) { 
						destLocation = destLocation+"/sourceFiles/skuFiles/";
					}
					else if(fileType.trim().startsWith(Constants.POS_MAPPING_TYPE_HOMES_PASSED.trim())) { 
						//homes passed Files
						destLocation = destLocation+"/sourceFiles/homesPassedFiles/";
					}
					else {		
						//UnProcesseFiles
						destLocation = destLocation+"/unProcessFiles/";
					}
					
					
					try {
						InputStream inStream = new FileInputStream(file);
						OutputStream outStream = new FileOutputStream(new File(destLocation+file.getName()));
 
						byte[] buffer = new byte[1024];
 
						int length;
						//copy the file content in bytes 
						while ((length = inStream.read(buffer)) > 0){
 
							outStream.write(buffer, 0, length);
 
						}
 
						inStream.close();
						outStream.close();
 
					
						/*boolean flag = file.renameTo(new File(backUpFoldLocationStr+file.getName()));
						
						if(flag) {
							POSFileLogDaoForDML posFileLogDaoForDML = (POSFileLogDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("posFileLogDaoForDML");
							//save the file name entry in (pos_files_log) table
							//posFileLogDao.saveOrUpdate(new POSFileLogs(users.getUserId(), fileType, file.getName(), Calendar.getInstance()));
							posFileLogDaoForDML.saveOrUpdate(new POSFileLogs(users.getUserId(), fileType, file.getName(), Calendar.getInstance()));
							
							//set the Last Fetched Time
							userPosFTPSettings.setLastFetchedTime(Calendar.getInstance());
							//userPosFTPSettingsDao.saveOrUpdate(userPosFTPSettings);
							userPosFTPSettingsDaoForDML.saveOrUpdate(userPosFTPSettings);
						}
					*/
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception ::::", e);
					}
				}catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::::", e);
				}
				
			} //for
			
			return true;
		} catch (Exception e) {
			
			logger.error("Exception ::::", e);
			return false;
		}
		
	} //copyFileToUserDir
	
	
	public static void fileCopyMoveToUserAndProcessFolder(File timeStapmedFile ) throws Exception{
		boolean sucessFlag = false;
//		String done= "/home/sumanm/ocftpuser/opt_sync/done";
//		String outboxoptintel="/home/sumanm/ocftpuser/opt_sync/outbox/optintel";
		
		UserPosFTPSettingsDaoForDML userPosFTPSettingsDaoForDML = (UserPosFTPSettingsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.USER_POS_FTP_SETTINGS_DAO_FOR_DML);
		
		logger.info("file started to moving in to User and processed folder"+timeStapmedFile.getName());
		String doneFolder = PropertyUtil.getPropertyValue("doneoptintel");
		doneFolder = doneFolder.endsWith(File.separator) ? doneFolder : doneFolder+File.separator;
		String fileName = timeStapmedFile.getName();
		logger.info("Uploaded fileName is "+fileName);

	  
	    
	    	String messageStr = "";
		String status= "";
		String reason = "";
		try {
			
			POSFileLogDao posFileLogDao  =(POSFileLogDao) ServiceLocator.getInstance().getDAOByName("posFileLogDao");
			UserPosFTPSettingsDao userPosFTPSettingsDao = (UserPosFTPSettingsDao)ServiceLocator.getInstance().getDAOByName("userPosFTPSettingsDao");
			UsersDao usersDao =(UsersDao)ServiceLocator.getInstance().getDAOByName("usersDao");
			
			List<UserPosFTPSettings> userPosSettingList = userPosFTPSettingsDao.findListByTimeDiff(fileName);
			logger.info("userPosSettingList size is "+userPosSettingList.size());

			if(userPosSettingList == null || userPosSettingList.size() == 0) {
				
				messageStr = "No User FTP setting from Optculture. So file is unprocessed";
				status = "Not Processed";
				return ;
			}
			logger.info("userPosSettingList size is :: "+userPosSettingList.size());
			Long userId= null;
			Users users;
			for (UserPosFTPSettings userPosFTPSettings : userPosSettingList) {
				try {
					
					logger.debug("userid is  :: "+userPosFTPSettings.getUserId()+""
							+ " file type is ::"+userPosFTPSettings.getFileType()+ " file format : "+userPosFTPSettings.getFileFormat());
					logger.debug("hostAddres :: "+userPosFTPSettings.getHostAddress());
					if(!userPosFTPSettings.getHostAddress().equals("localhost")){
						logger.debug("is matched");
						continue;
					}
					messageStr = "";
					status = "";
					reason = "";
					userId = userPosFTPSettings.getUserId();
					users = usersDao.find(userId);
					
					
					String fileType = userPosFTPSettings.getFileType();
					String fileFormat = userPosFTPSettings.getFileFormat();
					
					logger.info("File Name ::"+fileName + " >>> ::and fileFormat::"+fileFormat +">>>>> file Type:: "+fileType);
					
					if(!fileFormat.endsWith("\\S*")) {
						fileFormat = fileFormat + "\\S*";
					}
						
					//check the file Name with regex 
					if(!fileName.matches(fileFormat)) {
						logger.info("Formated  file not match with fileName.. ...");
						continue;
					}
					/*
					//check the file Name with regex 
					if(!fileName.matches(fileFormat)) {
						logger.info("Formated  file not match with fileName.. ...");
						continue;
					}
					*/
					String destLocation = checkDirExists(users);
					
					logger.info("File Type is >>> ::"+fileType.trim() );
					
					if(fileType.trim().startsWith(Constants.POS_MAPPING_TYPE_CONTACTS.trim())) { 
						destLocation = destLocation+"/sourceFiles/contactFiles/";
					}
					else if(fileType.trim().startsWith(Constants.POS_MAPPING_TYPE_SALES.trim())) {	
						destLocation = destLocation+"/sourceFiles/salesFiles/";
					}
					else if(fileType.trim().startsWith(Constants.POS_MAPPING_TYPE_SKU.trim())) { 
						destLocation = destLocation+"/sourceFiles/skuFiles/";
					}
					else if(fileType.trim().startsWith(Constants.POS_MAPPING_TYPE_HOMES_PASSED.trim())) { 
						//homes passed Files
						destLocation = destLocation+"/sourceFiles/homesPassedFiles/";
					}
					else {		
						//UnProcesseFiles
						destLocation = destLocation+"/unProcessFiles/";
					}
					
					//============================================================

					/*File destFile = new File(destLocation + timeStapmedFile.getName() + "_POSINCMPLETEPART");

					//BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destLocation+timeStampFilename));
					InputStream inStream = new FileInputStream(timeStapmedFile);
					BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile));
					
					if(logger.isInfoEnabled()) logger.info("input stream is ::" +inStream);
					
					byte[] buf = new byte[1024*5];
						
					int count = 0;
					while ((count = inStream.read(buf)) >= 0) {
						bos.write(buf, 0, count);
					}
					
					bos.flush();
					bos.close();
					inStream.close();
					
					//rename the fileName  after downloading...
					String tempStr = destFile.getAbsolutePath();
					
					File renameFile = new File(tempStr.substring(0, tempStr.lastIndexOf("_POSINCMPLETEPART")));
					
					if(logger.isInfoEnabled()) logger.info("Rename FileName is  :: "+renameFile.getName());
					
					destFile.renameTo(renameFile);
					if(logger.isInfoEnabled()) logger.info(" After renaming the File name is >>"+destFile.getName());*/
						
					//String renameCmd = "mv "+xmlFile+" "+xmlFile+".txt";
					//Runtime.getRuntime().exec(renameCmd);
					
					//String moveCmd = "mv "+xmlFile+".txt"+" "+donePath;
					//fix is to solve the partial file processing bug when there is a big file 
					boolean processFlag = false;
					/*try {
						String cpCmd = "cp "+timeStapmedFile.getPath()+" "+backUpFoldLocationStr;
						logger.info("cpCmd ::"+cpCmd);
						Runtime.getRuntime().exec(cpCmd);
						processFlag = true;
					} catch (Exception e) {
						processFlag = false;
						logger.error("Exception "+ e.getMessage());
						
					}
					*/
					try {
						String moveCmd = "mv "+timeStapmedFile.getPath()+" "+destLocation;
						logger.info("moveCmd ::"+moveCmd);
						Runtime.getRuntime().exec(moveCmd);
						processFlag = true;
					} catch (Exception e) {
						processFlag = false;
						logger.error("Exception "+ e.getMessage());
					}
					//boolean processFlag = timeStapmedFile.renameTo(new File(backUpFoldLocationStr+timeStapmedFile.getName()));
					
					logger.info("processFlag ::"+processFlag);
					if(processFlag) {
						POSFileLogDaoForDML posFileLogDaoForDML = (POSFileLogDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("posFileLogDaoForDML");
						//save the file name entry in (pos_files_log) table
						//posFileLogDao.saveOrUpdate(new POSFileLogs(users.getUserId(), fileType, timeStapmedFile.getName(), Calendar.getInstance()));
						posFileLogDaoForDML.saveOrUpdate(new POSFileLogs(users.getUserId(), fileType, timeStapmedFile.getName(), Calendar.getInstance()));
						//set the Last Fetched Time
						userPosFTPSettings.setLastFetchedTime(Calendar.getInstance());
						//userPosFTPSettingsDao.saveOrUpdate(userPosFTPSettings);
						userPosFTPSettingsDaoForDML.saveOrUpdate(userPosFTPSettings);
						messageStr = "File processed sucessfully"; 
						reason = "";
						status = "Processed";
					}else {
						logger.info("file is not taken for processing ::"+timeStapmedFile.getName());
						/*timeStapmedFile.renameTo(new File(doneFolder+timeStapmedFile.getName()));
						messageStr = "File processing failed";
						status = "Not Processed";
						reason = "Temporary";*/
					}
					sucessFlag = true;
					return;
					
				}catch(Exception e){
					logger.error("Exception occure while time stamped file procesing....",e);
					continue; 
				}
			}
			
			
		} catch (Exception e) {
			logger.error("Exception occure while getting the Dao....",e);
			sucessFlag= false; 
		}finally{
			logger.info("fileName is  >>> "+fileName);
			logger.info("sucessFlag is :: "+sucessFlag);
			/*if(!sucessFlag){
				timeStapmedFile.renameTo(new File(doneFolder+fileName));
				messageStr = "File not matched with any settings";
				status = "Not Processed";
				reason = "Permanent";
				timeStapmedFile.renameTo(new File(doneFolder+timeStapmedFile.getName()));
			}
			
			String ext = FilenameUtils.getExtension(fileName);
			if(fileName.contains("_OC_")){
				fileName = fileName.substring(0, fileName.lastIndexOf("_OC_"));
				fileName = fileName+"."+ext;
			}
			
			logger.info("messageStr is  >>> "+messageStr+ "  :: reason is :: "+reason + "  :: Andstatus is :: "+status+" :: ext is  ::"+ext);
			Utility.writeMessage(messageStr, reason, fileName, ext, status);
			*/
		}
	}
	
	 

	private static String  checkDirExists(Users userObj) {
		String SftpUserPath = PropertyUtil.getPropertyValueFromDB("SftpUserPath");
		String userParentDir = PropertyUtil.getPropertyValue("usersParentDirectory")+"/"+userObj.getUserName();
		String userSpecificParentDir = SftpUserPath + "/" + Utility.getOnlyOrgId(userObj.getUserName());
		
		String source = "";
		String tempStr = "";
		source = userParentDir + "/POSList";
		if(userObj.isSpecificDir()) {
			source = userSpecificParentDir + "/POSList";
		}
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
	
	
}

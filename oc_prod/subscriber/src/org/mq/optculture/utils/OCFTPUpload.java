package org.mq.optculture.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.GenerateReportSetting;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.dao.GenerateReportSettingDao;
import org.mq.marketer.campaign.dao.GenerateReportSettingDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
	
public class OCFTPUpload {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private GenerateReportSetting generateReportSetting;
	private String fileName;
	
	public void uplodFileToClientFTP(GenerateReportSetting generateReportSetting,String fileName ) {
		
		try {
			GenerateReportSettingDaoForDML generateReportSettingDaoForDML = (GenerateReportSettingDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.GENERATE_REPORT_SETTING_DAO_FOR_DML);
			String SFTPUSER;
			String SFTPPASS;
			String SFTPHOST;
			String SFTPWORKINGDIR;

			boolean isPushed = false;
			try {
				SFTPUSER = generateReportSetting.getUsername();//"ocftpuser";
				SFTPPASS = generateReportSetting.getPassword();//"oc2)1@"; 
				SFTPHOST = generateReportSetting.getHost();//"dev.optculture.com";
				SFTPWORKINGDIR = generateReportSetting.getTargetDir();//"/home/ocftpuser/"; 
				
				 Session session = null;
			        Channel channel = null;
			        ChannelSftp channelSftp = null;
			        logger.debug("preparing the host information for sftp.");
			        try {
			            JSch jsch = new JSch();
			            session = jsch.getSession(SFTPUSER, SFTPHOST, generateReportSetting.getPort());
			            logger.info("session........"+SFTPUSER+" "+SFTPHOST+" "+generateReportSetting.getPort()); 
			            session.setPassword(SFTPPASS);
			            java.util.Properties config = new java.util.Properties();
			            config.put("StrictHostKeyChecking", "no");
			            session.setConfig(config);
			            session.connect();
			            logger.debug("Host connected.");
			            channel = session.openChannel("sftp");
			            channel.connect();
			            logger.debug("sftp channel opened and connected.");
			            channelSftp = (ChannelSftp) channel;
			            channelSftp.cd(SFTPWORKINGDIR);
			            File f = new File(fileName);
			            channelSftp.put(new FileInputStream(f), f.getName());
			            isPushed = true;
			            //log.info("File transfered successfully to host.");
			        } catch (Exception ex) {
			             logger.debug("Exception found while tranfer the response.");
			        }
			        finally{

			            channelSftp.exit();
			            logger.debug("sftp Channel exited.");
			            channel.disconnect();
			            logger.debug("Channel disconnected.");
			            session.disconnect();
			            logger.debug("Host Session disconnected.");
			        }
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				logger.error("Exception ", e1);
			}
//		if(logger.isInfoEnabled()) logger.info("waiting  Connection for ::"+hostAddr );
			
//		if(hostAddr.equalsIgnoreCase("localhost") && userName.equals("ocftpuser") ) {
			if(isPushed) {
				generateReportSetting.setLastGeneratedOn(Calendar.getInstance());
				generateReportSetting.setLastGeneratedFile(fileName);
				generateReportSettingDaoForDML.saveOrUpdate(generateReportSetting);
			}
			
			String FTPBAckupDir = PropertyUtil.getPropertyValueFromDB("OCFTPBackupDir");
			String moveCmd = "mv "+fileName+" "+FTPBAckupDir;
			Runtime.getRuntime().exec(moveCmd);
	
			
	}catch (Exception e) {
		// TODO: handle exception
		logger.debug("Exception ", e);
	}
	}
}

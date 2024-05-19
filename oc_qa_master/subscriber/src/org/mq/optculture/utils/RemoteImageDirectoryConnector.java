package org.mq.optculture.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.ChannelSftp.LsEntry;

public class RemoteImageDirectoryConnector {

	//sftp properties
		private String host;
		private Integer port;
		private String user;
		private String password;

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public Integer getPort() {
			return port;
		}

		public void setPort(Integer port) {
			this.port = port;
		}

		public String getUser() {
			return user;
		}

		public void setUser(String user) {
			this.user = user;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		
		
		private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
		
		public RemoteImageDirectoryConnector(String host, Integer port, String user, String password) {
			this.host = host;
			this.port = port;
			this.user = user;
			this.password = password;
		}
		
		public RemoteImageDirectoryConnector() {}
		
		public ChannelSftp connect(){
			try{
				
				JSch jsch = new JSch();
				Session session = jsch.getSession(user, host,port);
				session.setConfig("StrictHostKeyChecking", "no");
				session.setPassword(password);
				session.connect();
				Channel channel = session.openChannel("sftp");
				channel.connect();
				ChannelSftp sftpChannel = (ChannelSftp) channel;
	        
	        return sftpChannel;
	       /* sftpChannel.cd("/usr/local/UserData/mahesh__org__ocqa/Gallery/rama");
	        System.out.println("connected");
	        File file = new File("D:/venkata.motupalli/Pictures/70TriumphTR6C-r.jpg");
	        FileInputStream fis = new FileInputStream(file);
	        System.out.println("uploaded");
	        sftpChannel.put(fis,file.getName());
	        System.out.println("uploaded");*/
	        
	        
			}catch (JSchException e) {
				logger.error("Exception ::", e);
			}catch(Exception e)
			{
				logger.error("Exception ::", e);
				
			}
			return null;
		}
		
		
		public void disconnect(ChannelSftp sftpChannel, Channel channel, Session session) {
			logger.info("disconnecting...");
			if(sftpChannel != null && sftpChannel.isConnected())sftpChannel.disconnect();
			if(channel != null && channel.isConnected())channel.disconnect();
			if(session != null && session.isConnected())session.disconnect();
		}
		public Boolean upload(ChannelSftp sftpChannel, String sourceDirPath, String fileName, String remoteDir ) {
			FileInputStream fis = null;
			try {
			// Change to output directory
				if(sftpChannel == null || !sftpChannel.isConnected()){
					
					sftpChannel = connect();
				}
				
			if(sftpChannel != null && sftpChannel.isConnected()) sftpChannel.cd(remoteDir);
			// Upload file
			File file = new File(sourceDirPath+fileName);
			fis = new FileInputStream(file);
			sftpChannel.put(fis, fileName);
			fis.close();
			//System.out.println("File uploaded successfully - "+ file.getAbsolutePath());
			} catch (Exception e) {
				logger.error("Exception ::", e);
				return false;
			}
			return true;
		}
		public boolean download(ChannelSftp sftpChannel, String fileName, String localDir, String remoteDirPath) {
			byte[] buffer = new byte[1024];
			BufferedInputStream bis;
			try {
				if(sftpChannel == null || !sftpChannel.isConnected()){
					
					sftpChannel = connect();
				}
			// Change to output directory
			sftpChannel.cd(remoteDirPath);
			bis = new BufferedInputStream(sftpChannel.get(fileName));
			File newLocalFile = new File(localDir +fileName);
			// Download file
			OutputStream os = new FileOutputStream(newLocalFile);
			BufferedOutputStream bos = new BufferedOutputStream(os);
			int readCount;
			while ((readCount = bis.read(buffer)) > 0) {
			bos.write(buffer, 0, readCount);
			}
			bis.close();
			bos.close();
			logger.debug("File downloaded successfully - ");
			} catch (Exception e) {
				logger.error("Exception ::", e);
				return false;
			}
			
			return true;
		}
		
		public List<String> lsDir(ChannelSftp sftpChannel, String remoteDir){
			
			List<String> dirList = new ArrayList<String>();
			try {
				
				if(sftpChannel == null || !sftpChannel.isConnected()){
					
					sftpChannel = connect();
				}
				sftpChannel.cd(remoteDir);
				Vector listOfDir = sftpChannel.ls(remoteDir);
				 for(int i=0; i<listOfDir.size();i++){
	                LsEntry entry = (LsEntry) listOfDir.get(i);
	                String fileName = entry.getFilename();
	                if (".".equals(fileName) || "..".equals(fileName)) {
	                    continue;
	                }
	                dirList.add(fileName);
	            }

			} catch (SftpException e) {
				// TODO Auto-generated catch block
				try {
					sftpChannel.mkdir(remoteDir);
				} catch (SftpException e1) {
					logger.error(remoteDir + " -cannot be created ", e);
					return dirList;
					
				}
			}
			
			return dirList;
		}
		
		public boolean deleteLocalTempDir(String path, boolean delDir){
			
			try {
				//this path 
				File dirFile = new File(path);
				if(dirFile.exists() && dirFile.isDirectory() ) {
					
					if(dirFile.list().length > 0){
						String[] galleryFileNameArr = dirFile.list();
						for(int i=0; i<galleryFileNameArr.length; i++){
							File tempFile = new File(path +"/" +galleryFileNameArr[i]);
							if(tempFile.isDirectory()){
								FileUtils.deleteDirectory(tempFile);
							}else{
								tempFile.delete();
							}
														
						 }
					}
					if(delDir) 	FileUtils.deleteDirectory(dirFile);
						
						
				}else{
					
						
						dirFile.delete();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return false;
			}
			return true;
		}
		
		
		public boolean lsFolderCopy(String sourcePath, String destPath, ChannelSftp sftpChannel ) throws SftpException {
			if(sftpChannel == null || !sftpChannel.isConnected()){
				
				sftpChannel = connect();
			}
			sftpChannel.cd(sourcePath);
			// List source (remote, sftp) directory and create a local copy of it - method for every single directory.
		    Vector<ChannelSftp.LsEntry> list = sftpChannel.ls(sourcePath); // List source directory structure.
		    for (ChannelSftp.LsEntry oListItem : list) { // Iterate objects in the list to get file/folder names.
		        if (!oListItem.getAttrs().isDir()) { // If it is a file (not a directory).
		            if (!(new File(destPath + "/" + oListItem.getFilename())).exists() || (oListItem.getAttrs().getMTime() > Long.valueOf(new File(destPath + "/" + oListItem.getFilename()).lastModified() / (long) 1000).intValue())) { // Download only if changed later.
		                new File(destPath + "/" + oListItem.getFilename());
		                sftpChannel.get(sourcePath + "/" + oListItem.getFilename(), destPath + "/" + oListItem.getFilename()); // Grab file from source ([source filename], [destination filename]).
		            }
		        } else if (!(".".equals(oListItem.getFilename()) || ("..".equals(oListItem.getFilename())))) {
		            new File(destPath + "/" + oListItem.getFilename()).mkdirs(); // Empty folder copy.
		            lsFolderCopy(sourcePath + "/" + oListItem.getFilename(), destPath + "/" + oListItem.getFilename(), sftpChannel); // Enter found folder on server to read its contents and create locally.
		        }
		    }
		    
		    return true;
		}

		public boolean isRemoteDirExists(String remoteDir, ChannelSftp sftpChannel, String newDir) throws Exception{
			if(sftpChannel == null || !sftpChannel.isConnected()){
				
				sftpChannel = connect();
			}
			sftpChannel.cd(remoteDir);
			
			String currentDirectory=sftpChannel.pwd();
			SftpATTRS attrs=null;
			try {
			    attrs = sftpChannel.stat(currentDirectory+"/"+newDir);
			} catch (Exception e) {
			    logger.debug(currentDirectory+"/"+newDir+" not found");
			    return false;
			}

			return true;
			/*if (attrs != null) {
			    System.out.println("Directory exists IsDir="+attrs.isDir());
			} else {
			    System.out.println("Creating dir "+newDir);
			    sftpChannel.mkdir(newDir);
			}*/
			
		}
		
		public boolean mkRemoteDir(String remoteDir, ChannelSftp sftpChannel, String newDir) {
			
				if(sftpChannel == null || !sftpChannel.isConnected()){
					
					sftpChannel = connect();
				}
				try {
				sftpChannel.cd(remoteDir);
				
				sftpChannel.mkdir(newDir);
				
				}catch(Exception e){
					logger.debug(remoteDir+"/"+newDir+" not created");
				    return false;
				}
				return true;
		}
	
		
		public boolean deleteRemoteDirOrImage(String remoteDir, ChannelSftp sftpChannel, String newDir, String directoryOrImage) {
			
			if(sftpChannel == null || !sftpChannel.isConnected()){
				
				sftpChannel = connect();
			}
			try {
			sftpChannel.cd(remoteDir);
			
			if(directoryOrImage.equals("directory")){
				deleteImages(sftpChannel, remoteDir+"/"+newDir+"/");
				sftpChannel.cd(remoteDir);
				sftpChannel.rmdir(newDir);
			}else	
				sftpChannel.rm(newDir);
			
			}catch(Exception e){
				logger.debug(remoteDir+"/"+newDir+" not deleted");
			    return false;
			}
			logger.debug(remoteDir+"/"+newDir+" is deleted");
			return true;
		}
		
		
		
		
public boolean deleteImages(ChannelSftp sftpChannel, String remoteDir){
			
			List<String> dirList = new ArrayList<String>();
			try {
				
				if(sftpChannel == null || !sftpChannel.isConnected()){
					
					sftpChannel = connect();
				}
				sftpChannel.cd(remoteDir);
				Vector listOfDir = sftpChannel.ls(remoteDir);
				 for(int i=0; i<listOfDir.size();i++){
	                LsEntry entry = (LsEntry) listOfDir.get(i);
	                String fileName = entry.getFilename();
	                if (".".equals(fileName) || "..".equals(fileName)) {
	                    continue;
	                }
	                sftpChannel.rm(fileName);
	            }

			} catch (SftpException e) {
				// TODO Auto-generated catch block
				return false;
			}
			
			return true;
		}
		
	
		
		public boolean clearAllImages(ChannelSftp sftpChannel, String remoteDir){
			try {


				sftpChannel.cd(remoteDir);
				Vector listOfDir = sftpChannel.ls(remoteDir);
				for(int i=0; i<listOfDir.size();i++){
					LsEntry entry = (LsEntry) listOfDir.get(i);
					String fileName = entry.getFilename();
					if (".".equals(fileName) || "..".equals(fileName)) {
						continue;
					}
					sftpChannel.rm(fileName);
					//(fileName);
					logger.info("deleted file name:"+fileName);
				}
				return true;
			} catch (SftpException e) {
				// TODO Auto-generated catch block
				logger.info("unable to delete"+e);
				return false;
			}
		}

		
		
		
}

package org.mq.marketer.campaign.general;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.media.Media;

public class FileUploader {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	/**
	 * Uploads the media content into the specified destination path <BR>
	 * If replace is true, replaces if file exists
	 * 
	 * @param media
	 * @param destinationPathStr - String, Absolute file path of the destination
	 * @param replace - boolean, true if u want to replace existing file or else false
	 * @return
	 * 		Returns 0 if file upload success with ReaderData, <BR>
	 * 				1 if file upload success with StringData, <BR>
	 * 				2 if file upload success with StreamData, <BR>
	 * 				3 if file upload success with ByteData, <BR>
	 * 				-10 if file exists and replace is false, <BR>
	 * 				-4 if fails with IllegalStateException, <BR>
	 * 				-3 if fails with FileNotFoundException, <BR>
	 * 				-2 if fails with IOException, <BR>
	 * 				-1 if fails in all the cases
	 */
	public static byte upload(Media media, String destinationPathStr, boolean replace) {

		File file = new File(destinationPathStr);
		
		if(file.exists() && !replace) {
			return -10;
		} 
		else if(file.exists()) {
			file.delete();
		}
		
		File parentDir = file.getParentFile();
		if(!parentDir.exists()) { 
			parentDir.mkdir();
		}

		try {
			
			if(logger.isDebugEnabled()) logger.debug("Trying to read with Media.getReaderData() ");
			try {
				BufferedReader br = new BufferedReader((InputStreamReader)media.getReaderData());
				BufferedWriter bw = new BufferedWriter(new FileWriter(destinationPathStr));
				String line = "";
				while((line=br.readLine())!=null){
					bw.write(line);
					bw.newLine();
				}
				bw.flush();
				bw.close();
				br.close();
				if(logger.isDebugEnabled()) logger.debug("File upload successful with Media.getReaderData()");
				return 0;
			}catch (IllegalStateException ise) {
				logger.warn("IllegalStateException is " + ise );
			}
			
			if(logger.isDebugEnabled()) logger.debug("Trying to read with Media.getStringData() ");
					
			try{
				String data = media.getStringData(); 
				FileUtils.writeStringToFile(file, data);
				if(logger.isDebugEnabled()) logger.debug("File upload successful with Media.getStringData()");
				return 1;
			} catch (IllegalStateException ise) {
				logger.warn("IllegalStateException is " + ise );
			}
			
			if(logger.isDebugEnabled()) logger.debug("Trying to read with Media.getStreamData()");
			
			try {
				FileOutputStream out = new FileOutputStream (file);
				BufferedInputStream bis = new BufferedInputStream(
						(InputStream)media.getStreamData());
				byte[] buf = new byte[1024];
				int count = 0;
				while ((count = bis.read(buf)) >= 0) {
					out.write(buf, 0, count);
				}
				out.flush();
				bis.close();
				out.close();
				if(logger.isDebugEnabled()) logger.debug("File upload successful with Media.getStreamData()");
				return 2;
			} 
			catch (IllegalStateException ise) {
				logger.warn("IllegalStateException is " + ise );
			}
			
			if(logger.isDebugEnabled()) logger.debug("Trying to read with Media.getByteData() ");
			
			try {
				byte[] data = media.getByteData();
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(data);
				fos.flush();
				fos.close(); 
				return 3;
			} catch (IllegalStateException ise) {
				logger.error("** IllegalStateException is " + ise +" **");
				return -4;
			}
		}  
		catch (FileNotFoundException fnfe) {
			logger.error("** FileNotFoundException is " + fnfe +" **");
			return -3;
		}
		catch (IOException e) {
			logger.error("** IOException is " + e +" **");
			return -2;
		}
		catch (Exception e) {
			logger.error("** Exception is " + e +" **");
		}
		
		return -1;
	}
}

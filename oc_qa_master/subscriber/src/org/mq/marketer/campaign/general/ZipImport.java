package org.mq.marketer.campaign.general;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ZipImport {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	  public static String unzipFileIntoDirectory(File file,
    		  String extractedToDir,String campName,long millis) throws Exception {
    	
    	ZipFile zipFile = new ZipFile(file);
    	
    	File campFolder=null;
    	logger.debug("Just Entered with Values : ZipFile:"+zipFile+" extractedToDir:"+extractedToDir+" campName:"+campName+" millis:"+millis);
    	
    	try {
    		campFolder = new File(extractedToDir+File.separator+campName+File.separator+millis);
    		campFolder.mkdirs();
    	/*	boolean  exists = (new File(extractedToDir+File.separator+campName)).exists();
    		if(!exists) {
				logger.debug("Campaign folder does not exist in Users . Creating ...");
				logger.debug("Path : "+extractedToDir+File.separator+campName);
				logger.debug("New campaign folder successfully created.");
				
    		} else {
  	    	logger.debug("Campaign Folder exists .. creating timestamp folder in it .");
  	    	campFolder = new File(extractedToDir+File.separator+campName+File.separator+millis);
  	    	campFolder.mkdir();
    		}
    		*/
    	} catch (Exception e) {
    		logger.error("Exception ::" , e);
    		logger.error("** Exception : while creating a new campaign folder"+e);
    	}
  	    
	    File f = null;
	    String substr=null;
	    String substrExt=null;
	    int count=0;
	    String finalStr = "";
	    StringBuffer sb = new StringBuffer("Invalid Files :");
	    
	    Enumeration files = zipFile.entries();
	    
	    while (files.hasMoreElements()) {
	    	try {
	    		ZipEntry entry = (ZipEntry) files.nextElement();
	            substr=entry.getName();
	            substr = substr.substring(substr.lastIndexOf(File.separator)+1);
	            substrExt = substr.substring(substr.lastIndexOf(".")+1);
	            
	            if(!(substrExt.equalsIgnoreCase("htm") || substrExt.equalsIgnoreCase("html")||
	            	 substrExt.equalsIgnoreCase("jpeg") || substrExt.equalsIgnoreCase("jpg") || 
	            	 substrExt.equalsIgnoreCase("gif") || substrExt.equalsIgnoreCase("png") || 
	            	 substrExt.equalsIgnoreCase("bmp")||substrExt.equalsIgnoreCase("pdf") ||
	            	 substrExt.equalsIgnoreCase("css")||substrExt.equalsIgnoreCase(""))) {
	            	sb.append(substr+", ");
	            	count++;
	            	continue;
	            }
	            
	        BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
	        byte[] buffer = new byte[1024];
	        int bytesRead = 0;
	  
	       logger.debug("Next Entry : "+entry.getName());
	 	   f = new File(extractedToDir+ File.separator +campName+File.separator+millis+
	 			   File.separator+entry.getName());
	 	   
	        if(!f.getParentFile().exists()) {
	        	f.getParentFile().mkdirs();
	        }
	        if (entry.isDirectory()) {
	        	f.mkdirs();
	            continue;
	        } else {
	            f.createNewFile();
	            logger.debug("File created...");
	        }
	        
	        
	        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
	  
	        while ((bytesRead = bis.read(buffer)) != -1) {
	        	bos.write(buffer, 0, bytesRead);
	        }
	        bos.flush();
	        bos.close();
	        
	      } catch (Exception e) {
	    	  logger.error("Exception ::" , e);
	    	  logger.error("**Exception : An error occured while reading Zip entries."+e+"**");
	      } 
	    } // while

	    zipFile.close();
	    if (!file.delete()) {
	    	logger.error("** Exception: Deleting the Source Zip file failed :**"); 
	    }
	    
	    if(count > 0) {
	    	 finalStr=sb.substring(0,sb.lastIndexOf(","));
		    	if(count>4) {
		    		int index=finalStr.indexOf(',');
		    		
		    		while(index >0){
		    			finalStr=finalStr.replace(',', '\n');
		    			index=finalStr.indexOf(',');
		    		}
		    		return finalStr;
		    	}
	   } 
	    logger.info("exit from ZipImport class and finalstring is ::"+finalStr);
	  return finalStr;
   }
	  
	  public static String unzipFileIntoMyTempDirectory(File file,
    		  String extractedToDir,String tempName,long millis) throws Exception {
    	
    	ZipFile zipFile = new ZipFile(file);
    	
    	File campFolder=null;
    	logger.debug("Just Entered with Values : ZipFile:"+zipFile+" extractedToDir:"+extractedToDir+" campName:"+tempName+" millis:"+millis);
    	
    	try {
    		campFolder = new File(extractedToDir+File.separator+tempName+File.separator+millis);
    		campFolder.mkdirs();
    	/*	boolean  exists = (new File(extractedToDir+File.separator+campName)).exists();
    		if(!exists) {
				logger.debug("Campaign folder does not exist in Users . Creating ...");
				logger.debug("Path : "+extractedToDir+File.separator+campName);
				logger.debug("New campaign folder successfully created.");
				
    		} else {
  	    	logger.debug("Campaign Folder exists .. creating timestamp folder in it .");
  	    	campFolder = new File(extractedToDir+File.separator+campName+File.separator+millis);
  	    	campFolder.mkdir();
    		}
    		*/
    	} catch (Exception e) {
    		logger.error("Exception ::" , e);
    		logger.error("** Exception : while creating a new campaign folder"+e);
    	}
  	    
	    File f = null;
	    String substr=null;
	    String substrExt=null;
	    int count=0;
	    String finalStr = "";
	    StringBuffer sb = new StringBuffer("Invalid Files :");
	    
	    Enumeration files = zipFile.entries();
	    
	    while (files.hasMoreElements()) {
	    	try {
	    		ZipEntry entry = (ZipEntry) files.nextElement();
	            substr=entry.getName();
	            substr = substr.substring(substr.lastIndexOf(File.separator)+1);
	            logger.info("substr is "+substr);
	            substrExt = substr.substring(substr.lastIndexOf(".")+1);
	            logger.info("substrExt is"+substrExt);
	            
	            if(!(substrExt.equalsIgnoreCase("htm") || substrExt.equalsIgnoreCase("html")||
	            	 substrExt.equalsIgnoreCase("jpeg") || substrExt.equalsIgnoreCase("jpg") || 
	            	 substrExt.equalsIgnoreCase("gif") || substrExt.equalsIgnoreCase("png") || 
	            	 substrExt.equalsIgnoreCase("bmp")||substrExt.equalsIgnoreCase("pdf") ||
	            	 substrExt.equalsIgnoreCase("css")||substrExt.equalsIgnoreCase(""))) {
	            	sb.append(substr+", ");
	            	count++;
	            	continue;
	            }
	            
	            logger.info(" count is"+count);
	        BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
	        byte[] buffer = new byte[1024];
	        int bytesRead = 0;
	  
	       logger.debug("Next Entry : "+entry.getName());
	 	   f = new File(extractedToDir+ File.separator +tempName+File.separator+millis+
	 			   File.separator+entry.getName());
	 	   
	        if(!f.getParentFile().exists()) {
	        	f.getParentFile().mkdirs();
	        }
	        if (entry.isDirectory()) {
	        	f.mkdirs();
	            continue;
	        } else {
	            f.createNewFile();
	            logger.info("File created...");
	            logger.debug("File created...");
	        }
	        
	        
	        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
	  
	        while ((bytesRead = bis.read(buffer)) != -1) {
	        	bos.write(buffer, 0, bytesRead);
	        }
	        bos.flush();
	        bos.close();
	        
	      } catch (Exception e) {
	    	  logger.error("Exception ::" , e);
	    	  logger.error("**Exception : An error occured while reading Zip entries."+e+"**");
	      } 
	    } // while

	    zipFile.close();
	    if (!file.delete()) {
	    	logger.error("** Exception: Deleting the Source Zip file failed :**"); 
	    }
	    
	    if(count > 0) {
	    	 finalStr=sb.substring(0,sb.lastIndexOf(","));
	    	 logger.info("final str is"+finalStr);
		    	if(count>4) {
		    		int index=finalStr.indexOf(',');
		    		
		    		while(index >0){
		    			finalStr=finalStr.replace(',', '\n');
		    			index=finalStr.indexOf(',');
		    		}
		    		return finalStr;
		    	}
	   } 
	    logger.info("exit from ZipImport class and finalstring is ::"+finalStr);
	  return finalStr;
   }
	  
	 
      
}

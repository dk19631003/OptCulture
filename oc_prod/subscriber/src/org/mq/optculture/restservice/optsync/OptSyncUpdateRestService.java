package org.mq.optculture.restservice.optsync;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class OptSyncUpdateRestService extends AbstractController implements ResourceLoaderAware{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private ResourceLoader resourceLoader;
	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		
		 this.resourceLoader = resourceLoader; 
	}
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
	
		try
		{
			logger.debug("OptSyncUpdateRestService..");
			response.setContentType("application/json");
			String requestJson = OptCultureUtils.getParameterJsonValue(request);
			logger.info("Json value = "+requestJson);
			JSONObject jsonMainObj = (JSONObject)JSONValue.parse(requestJson);
			JSONObject jsonHeaderObj = (JSONObject)jsonMainObj.get("Head");
			
			JSONObject jsonUserObj = (JSONObject)jsonHeaderObj.get("user");
			
			
			String userName = jsonUserObj.get("userName").toString();
			String orgId  = jsonUserObj.get("organizationId").toString();
			String token = jsonUserObj.get("token").toString();
				
			String userFullName =  userName + Constants.USER_AND_ORG_SEPARATOR + orgId;
			UsersDao usersDao  = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Users users = usersDao.findUserByToken(userFullName,token);
			
			if(users==null)
			{
				logger.debug("Required user details  are not valid ... returning ");
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				String responseJson = "{\"RESPONSEINFO\":{\"STATUS\":{\"STATUS\":\"Failure\",\"MESSAGE\":\"Error 100011: user details are not valid.\",\"ERRORCODE\":\"100011\"}}}";
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				return null;
			}
			 String majorVersion = jsonHeaderObj.get("majorVersion").toString();
			 String minorVersion = jsonHeaderObj.get("minorVersion").toString();
			 String patchVersion = jsonHeaderObj.get("patchVersion").toString();
			 String download = jsonHeaderObj.get("download").toString();
			 logger.info("major version: "+majorVersion);
			 logger.info("minor version: "+minorVersion);
			 
			 logger.info("download: "+download);
			 /*
			  *   First client send the request with version number and download=false .
			  *   if the new version available will notify to the client that new version is available.
			  *   Client need to send request again if he wants to download the latest jar with download=true in  json request.
			  *    
			  */
			 if(download.equalsIgnoreCase("true"))
			 {
				 int result= loadResource("OptSyncHost.jar", response);
				 response.setStatus(result);
				 return null;
			 }
			 
			 double oldMajorVersion = Double.parseDouble(majorVersion);
			 int oldMinorVersion = Integer.parseInt(minorVersion);
			 int oldPatchVersion = Integer.parseInt(patchVersion);
			  
			 String strNewMajorVersion =PropertyUtil.getPropertyValueFromDB("OptSyncMajorVersion");
			 String strNewMinorVersion =PropertyUtil.getPropertyValueFromDB("OptSyncMinorVersion");
			 String strNewPatchVersion =PropertyUtil.getPropertyValueFromDB("OptSyncPatchVersion");
			 
			 
			 logger.info("new  major Version: "+strNewMajorVersion);
			 logger.info("new  minor Version: "+strNewMinorVersion);
			 logger.info("new  patch Version: "+strNewPatchVersion);
			 
			 
			 double newMajorVersion= Double.parseDouble(strNewMajorVersion);
			 int newMinorVersion= Integer.parseInt(strNewMinorVersion);
			 int newPatchVersion= Integer.parseInt(strNewPatchVersion);
			 
			 boolean majorVersionAvailable = newMajorVersion>oldMajorVersion;
		     boolean minorVersionChange = newMajorVersion==oldMajorVersion && newMinorVersion>oldMinorVersion ;
		     boolean patchVersionChange=newMajorVersion==oldMajorVersion && newMinorVersion==oldMinorVersion
		    		    &&newPatchVersion>oldPatchVersion;
			if (majorVersionAvailable || minorVersionChange ||patchVersionChange) {
				response.setStatus(HttpServletResponse.SC_OK);
				return null;
			} else {
				response.setStatus(HttpServletResponse.SC_NO_CONTENT);
				return null;
			}

		}
		catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		  	logger.error("Exception ::" , e.getMessage());
			return null;
		 }	
	
	}
	
	 private int loadResource(String strFile,HttpServletResponse response) 
	 {
		 
		String strPath =PropertyUtil.getPropertyValueFromDB("OptSyncPath");//  path=/home/ocftpuser, qc= /home/optculture/optsync_java
		String optSyncDir="file:"+Paths.get(strPath,strFile).toString();
		logger.info("optSyncDir :"+optSyncDir);
		Resource resource = resourceLoader.getResource(optSyncDir);
		//response.setContentType("application/octet-stream");
		response.setContentType("application/gzip");
		
 		response.setHeader("Content-Disposition", "attachment; filename=" + strFile);
		try
		(
		 InputStream iStream = resource.getInputStream();)		
		 	{
			 FileCopyUtils.copy(iStream, response.getOutputStream());
		     response.flushBuffer();
		     logger.info("file copied to the response");
			} catch (IOException e) {
				logger.error("IOException :" + e.getLocalizedMessage());
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
			}
		     catch (Exception e) {
			logger.error("Exception :" + e.getLocalizedMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		    }
		  return HttpServletResponse.SC_OK;
	 }

	 public static void main(String[] args) {
	     double newMajorVersion = 2.1;
	     int newMinorVersion=2;
	     int newPatchVersion=4;
	     
	     double oldMajorVersion=2.1;
	     int oldMinorVersion= 19;
	     int oldPatchVersion=3;
	     boolean majorVersionAvailable = newMajorVersion>oldMajorVersion;
	     boolean minorVersionChange = newMajorVersion==oldMajorVersion && newMinorVersion>oldMinorVersion ;
	     boolean patchVersionChange=newMajorVersion==oldMajorVersion && newMinorVersion==oldMinorVersion
	    		    &&newPatchVersion>oldPatchVersion;
	     System.out.println(majorVersionAvailable);
	     if(majorVersionAvailable || minorVersionChange ||patchVersionChange )
	     {
	    	 System.out.println("new version available...");
	     }
	     
	     else
	     {
	    	 System.out.println("new version not available");
	     }
	     
	}
	

}

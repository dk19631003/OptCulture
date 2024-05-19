package org.mq.optculture.restservice.language;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class ProcessLanguageRestService extends AbstractController
{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	//private String ETLPath=PropertyUtil.getPropertyValue("ETLFileSource");
	//String strETLDir="file:"+Paths.get(ETLPath).toString();
	
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception 
	{
		// TODO Auto-generated method stub
		try
		{
			logger.debug("ProcessLanguageRestService..");
			response.setContentType("application/json");
			String requestJson = OptCultureUtils.getParameterJsonValue(request);
			logger.info("Json value = "+requestJson);
			JSONObject jsonMainObj = (JSONObject)JSONValue.parse(requestJson);
			//logger.info("json main "+jsonMainObj);
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
				String responseJson = "{\"RESPONSEINFO\":{\"STATUS\":{\"STATUS\":\"Failure\",\"MESSAGE\":\"Error 100011: user details are not valid.\",\"ERRORCODE\":\"100011\"}}}";
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				return null;
				
			}
			
			String languageCode = jsonHeaderObj.get("languageCode").toString();
			logger.info("language Code: "+languageCode);
			
			switch(languageCode.toLowerCase())
			{
			 case "en": 
				 
				// loadExternalResoure(strETLDir,"language_en.json",response);
				loadResource("language_en.json",response);
				break;
			 case "ar":
				loadResource("language_ar.json",response);
				//loadExternalResoure(strETLDir,"language_en.json",response);
		 		break;
			 case "es":
					loadResource("language_es.json",response);
					//loadExternalResoure(strETLDir,"language_en.json",response);
			 		break;
		     default:
		    	 String responseJson = "{\"RESPONSEINFO\":{\"STATUS\":{\"STATUS\":\"Failure\",\"MESSAGE\":\"Error 30006: language file not found.\",\"ERRORCODE\":\"30006\"}}}";
				 PrintWriter printWriter = response.getWriter();
				 printWriter.write(responseJson);
				 printWriter.flush();
				 printWriter.close();
			}
			
			
		 }catch(Exception e)
		     {
			  	logger.error("Exception ::" , e);
				String responseJson = "{\"RESPONSEINFO\":{\"STATUS\":{\"STATUS\":\"Failure\",\"MESSAGE\":\"Error 101001: Invalid request.\",\"ERRORCODE\":\"101001\"}}}";
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
			  
		     }	
		return null;
	}
		
	
	 private void loadResource(String strFile,HttpServletResponse response) throws Exception
	 {
		Resource resource = new ClassPathResource(strFile);
		StringBuilder responseJson= new StringBuilder();
		try(
		   InputStream iStream = resource.getInputStream();
		   Stream<String> stream=new BufferedReader(new InputStreamReader(iStream)).lines())
		    {
			  stream.forEach(str->responseJson.append(str)); 
			}
	    PrintWriter printWriter = response.getWriter();
	    String finalResponseJson = 
	    		"{\"RESPONSEINFO\":{\"STATUS\":"
	    		+ "{\"STATUS\":\"Success\""+","
	    		+ "\"LANGUAGEJSON\":"+responseJson.toString()+","
	    		+ "\"MESSAGE\":\"Processed Successfully\",\"ERRORCODE\":\"0\"}}}";
	    printWriter.write(finalResponseJson);
		printWriter.flush();
		printWriter.close();
	 }

	/* private void loadResourceOld(String strFile,HttpServletResponse response) throws Exception
	 {
		Resource resource = new ClassPathResource(strFile);
		StringBuilder responseJson= new StringBuilder();
		try(
		   InputStream iStream = resource.getInputStream();
		   Stream<String> stream=new BufferedReader(new InputStreamReader(iStream)).lines())
		    {
			  stream.forEach(str->responseJson.append(str)); 
			}
	    PrintWriter printWriter = response.getWriter();
		printWriter.write(responseJson.toString());
		printWriter.flush();
		printWriter.close();
	 }*/
}

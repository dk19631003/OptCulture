package org.mq.optculture.restservice.loyalty;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.business.loyalty.ReIssuePerksBusinessService;
import org.mq.optculture.business.loyalty.ReIssuePerksServiceBusinessImpl;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;


public class ReIssuePerksService extends AbstractController  {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
        if(logger.isDebugEnabled()) logger.debug("-- Just Entered --");    
        
        response.setContentType("application/json");
        String jsonValue = OptCultureUtils.getParameterJsonValue(request);
		//Gson gson = new Gson();
		logger.debug("jason value perks"+jsonValue);
		
		try{
		
        Long tierId=Long.parseLong(request.getParameter("tierId"));
        Long prgmId=Long.parseLong(request.getParameter("prgmId"));
		
        ReIssuePerksBusinessService reIssuePerksBusinessService = (ReIssuePerksServiceBusinessImpl) ServiceLocator.getInstance().getServiceByName(OCConstants.REISSUEPERKS_BUSINESS_SERVICE);
        reIssuePerksBusinessService.processReIssuePerksRequest(tierId,prgmId);
        
        
        
        String responseJson = "{\"MESSAGE\":\"Success.\"}";
			logger.info("Response = "+responseJson);
			PrintWriter printWriter = response.getWriter();
			printWriter.write(responseJson);
			printWriter.flush();
			printWriter.close();
			logger.info("completed reIssuePerksService>>>>");
        
		}catch(Exception e){
			logger.info("Exception",e);
			String responseJson = "{\"MESSAGE\":\"Invalid request.\"}";
			PrintWriter printWriter = response.getWriter();
			printWriter.write(responseJson);
			printWriter.flush();
			printWriter.close();
			logger.info("Response = "+responseJson);
			
			return null;
		}
		return null;
	}
	
    /*public static void main(String[] args) throws Exception{
		
		String json ="{\"Head\":{\"user\":{\"userName\":\"ramakrishna\",\"organizationId\":\"ocqa\",\"token\":\"BHANZ1UC1BJPZBJO\"}}}";
		
		JSONObject jsonObject = (JSONObject)JSONValue.parse(json);
		JSONObject jsonHead=(JSONObject)jsonObject.get("Head");
		JSONObject userJsonObj = (JSONObject)jsonHead.get("user");
		System.out.println("userName "+userJsonObj.get("userName"));
		System.out.println("token "+userJsonObj.get("token"));
		System.out.println("userName "+(String)userJsonObj.get("userName"));
		System.out.println("token "+(String)userJsonObj.get("token"));
		
	}*/
}

package org.mq.optculture.restservice.sparkbase;

import java.io.PrintWriter;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mq.marketer.campaign.beans.GetTokenRequestLog;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.sparkbase.TokenRequestObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.gson.Gson;


public class SparkBaseTokenRestService extends AbstractController{

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response){
		BaseService baseService=null;
		TokenRequestObject tokenRequestObject=null;
		BaseResponseObject baseResponseObject=null;
		try {
		String userName = request.getParameter("username");
		String password = request.getParameter("password");
		String orgId = request.getParameter("orgid");
		tokenRequestObject = new TokenRequestObject();
		tokenRequestObject.setUserName(userName);
		//tokenRequestObject.setPassword(password);
		tokenRequestObject.setOrgId(orgId);
		GetTokenRequestLog getTokenRequestLog = logTransaction(tokenRequestObject);
		baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.SPARKBASE_TOKEN_SERVICE);
		baseResponseObject=baseService.processRequest(tokenRequestObject);
		getTokenRequestLog.setJsonResponse(baseResponseObject.getJsonValue());
		}catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		finally {
			try {
		PrintWriter pw = response.getWriter();
		pw.write(baseResponseObject.getJsonValue());
		pw.flush();
		pw.close();
			}catch (Exception e) {
				logger.error("Exception ::" , e);
			}
		}
		return null;
		
	}
	
	public GetTokenRequestLog logTransaction(TokenRequestObject tokenRequestObject) throws Exception{
		
		GetTokenRequestLog getTokenRequestLog = new GetTokenRequestLog();
		getTokenRequestLog.setJsonRequest(new Gson().toJson(tokenRequestObject));
		getTokenRequestLog.setRequestDate(Calendar.getInstance());
		UsersDaoForDML usersDaoForDML = (UsersDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.USERS_DAOForDML);
		usersDaoForDML.saveOrUpdate(getTokenRequestLog);
		return getTokenRequestLog;
		
	}

}

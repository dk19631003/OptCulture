package org.mq.optculture.restservice.beefileapiservice;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mq.marketer.campaign.beans.UserDesignedCustomRows;
import org.mq.marketer.campaign.dao.UserDesignedCustomRowsDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;



public class UserDesignedSavedRowsRestService extends AbstractController{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private UserDesignedCustomRowsDao userDesignedCustomRowsDao;
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String jsonDefault = null;
		JSONArray jsonArray = new JSONArray();
		try {
			String saveRowType = request.getParameter("name");
			String userId = request.getParameter("userId");
			logger.info("SavedRows API ");
			jsonDefault = PropertyUtil.getPropertyValueFromDB(saveRowType);
			if(jsonDefault!=null && jsonDefault.isEmpty()) {
				jsonArray.put(jsonDefault);
			}else {
			 if(userId!=null && !userId.isEmpty()) {
				this.userDesignedCustomRowsDao = (UserDesignedCustomRowsDao) ServiceLocator.getInstance().getDAOByName("userDesignedCustomRowsDao");
				List<UserDesignedCustomRows> userDesignedCustomRows  = userDesignedCustomRowsDao.findTemplatesFromUserId(Long.parseLong(userId),saveRowType);
				if(userDesignedCustomRows!=null && !userDesignedCustomRows.isEmpty()) {
					for (UserDesignedCustomRows customRows : userDesignedCustomRows) {
						JSONObject jsonObject = null;
							jsonObject = new JSONObject(customRows.getRowJsonData().toString());
							jsonArray.put(jsonObject);
				 	}
				}
			 }
			}
			
			
		} catch(Exception e) {
			logger.error("Exception ::" , e);
		} finally {
			try {
				response.setContentType("application/json");
				PrintWriter pw = response.getWriter();
				if(jsonArray!=null && !jsonArray.toString().isEmpty()) {
					pw.write(jsonArray.toString());
				}else {
					pw.write(Constants.STRING_NILL);
				}
				pw.flush();
				pw.close();
			} catch (Exception e) {
				logger.error("Exception ::" , e);
			}
		}
		
		return null;
	}
}




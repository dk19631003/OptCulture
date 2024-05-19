package org.mq.optculture.controller;

import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.FormMapping;
import org.mq.marketer.campaign.dao.FormMappingDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.business.helper.ContactSubscriptionProcessHelper;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.ContactSubscriptionRequestObject;

import org.mq.optculture.restservice.ocsurveywebhook.OCSurveyService;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
// contact subscription service
public class ContactSubscriptionService extends AbstractController {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) {

		BaseResponseObject baseResponseObject = null;// my own object 
		BaseService baseService = null;

		FormMapping formMapping = null;

		long fmId = 0;

		logger.debug("got hid   " + request.getParameter(OCConstants.Form_HID) );
		try {


			logger.debug("---------Entered -----------");
			ServiceLocator locator = ServiceLocator.getInstance();
			logger.debug("Entered try ");


			ContactSubscriptionRequestObject contactSubscriptionRequestObject = new ContactSubscriptionRequestObject();//remove

			// own object buildup by request obj
				
				HashMap<String, String> formMapValuesHM = new HashMap<String, String>();
				Enumeration<String> paramNames = request.getParameterNames();
				
				while (paramNames.hasMoreElements()) {

					String reqParaName = paramNames.nextElement();
					
					if(reqParaName.equalsIgnoreCase(OCConstants.Form_HID)) continue;
					
					 String[] paramValuesStr = request.getParameterValues(reqParaName);
					 String paramval = "";
					 
					 if(paramValuesStr.length > 1) {
						 
					  for(int i=0; i<paramValuesStr.length; i++){
						  
						  if(paramval.length() > 0) paramval += ",";
						  paramval += paramValuesStr[i];
						  
					  }//for
					  
					 }//if
					 else if(paramValuesStr.length == 1 ) {
						 
						 paramval += paramValuesStr[0];
						 
					 }
					
					formMapValuesHM.put(reqParaName, paramval);
				}

				//formMapValuesHM.put(reqParaName, paramval);
			//}

			contactSubscriptionRequestObject.setFormId(request.getParameter(OCConstants.Form_HID).trim());
			contactSubscriptionRequestObject.setFormValuesMap(formMapValuesHM);
			contactSubscriptionRequestObject.setAction(OCConstants.CONTACT_SUBSCRIPTION_REQUEST);//uuuu

			fmId = Long.parseLong(contactSubscriptionRequestObject.getFormId());

			FormMappingDao formMappingDao = (FormMappingDao) locator.getDAOByName(OCConstants.FORM_MAPPING_DAO);
			formMapping = formMappingDao.findById(fmId);

			//String retMsg = contactSubscriptionProcessHelper.validateFormMapping(formMapping);
			/*if(request.getParameter(OCConstants.Form_HID).equals(formMapping.getId()) && formMapping.isWebHook())*/
			boolean isWebHook=formMapping.isWebHook();
			if(isWebHook){

				String requestJson = OptCultureUtils.getParameterJsonValue(request); 

				logger.info("JSON from Beside TyprForm in ContactSubscriptionService..."+requestJson);

				OCSurveyService  oCSurveyService = new OCSurveyService(requestJson,formMapping);
				Thread th =  new Thread(oCSurveyService);
				th.start();

				return null;

			}

			baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.CONTACT_SUBSCRIPTION_BUSINESS_SERVICE);
			baseResponseObject = baseService.processRequest(contactSubscriptionRequestObject);
			if(baseResponseObject != null) response.sendRedirect(baseResponseObject.getAction());

		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		logger.debug("exit  hid   " );
		return null;
	}

}

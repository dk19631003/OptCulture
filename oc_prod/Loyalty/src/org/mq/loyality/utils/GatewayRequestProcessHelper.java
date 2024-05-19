package org.mq.loyality.utils;

import java.util.Enumeration;
import java.util.Properties;

import org.mq.loyality.common.dao.CountryCodeDao;
import org.mq.loyality.common.dao.OCSMSGatewayDao;
import org.mq.loyality.common.dao.UserSMSGatewayDao;
import org.mq.loyality.common.hbmbean.OCSMSGateway;
import org.mq.loyality.common.hbmbean.UserSMSGateway;
import org.mq.loyality.common.hbmbean.Users;
import org.mq.loyality.exception.BaseServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GatewayRequestProcessHelper {
	
	@Autowired
	private  UserSMSGatewayDao userSmsGatewayDao;
	@Autowired
	private  OCSMSGatewayDao oCSMSGatewayDao;
	
	public  OCSMSGateway getOcSMSGateway(Users user, String type) throws BaseServiceException {
		fillAllCountry();
		UserSMSGateway userSmsGateway = null;
		OCSMSGateway ocgateway = null;
		userSmsGateway = userSmsGatewayDao.findByUserId(user.getUserId(), type);

		if(userSmsGateway == null) {
			return null;
			//throw new BaseServiceException("no default smstype available for user");
		}
		ocgateway = oCSMSGatewayDao.
				findById(userSmsGateway.getGatewayId());
		return ocgateway;
	}
	
	@Autowired
	public CountryCodeDao countryCodeDao;
	public void fillAllCountry() {
		
		Properties countryCodes = countryCodeDao.findAllCountryCodes();
		Enumeration enm = countryCodes.propertyNames();
		while(enm.hasMoreElements()){
			String currentCountry = (String)enm.nextElement();
			SMSStatusCodes.optInMap.put(currentCountry, false);
			SMSStatusCodes.optOutFooterMap.put(currentCountry, false);
			SMSStatusCodes.userOptinMediumMap.put(currentCountry, true);
			SMSStatusCodes.setCountryCode.put(currentCountry, true);
			SMSStatusCodes.defaultSMSTypeMap.put(currentCountry, Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL);
			SMSStatusCodes.defaultSMSOptinGatewayTypeMap.put(currentCountry, Constants.SMS_TYPE_PROMOTIONAL);
			SMSStatusCodes.genericCampTypeMap.put(Constants.SMS_TYPE_NAME_BROADCAST, Constants.SMS_TYPE_PROMOTIONAL+Constants.ADDR_COL_DELIMETER+"Y");
			SMSStatusCodes.genericCampTypeMap.put(Constants.SMS_TYPE_NAME_2_WAY, Constants.SMS_TYPE_2_WAY+Constants.ADDR_COL_DELIMETER+"Y");
			SMSStatusCodes.campTypeMap.put(currentCountry, SMSStatusCodes.genericCampTypeMap);
			
			SMSStatusCodes.genericCampValueMap.put(Constants.SMS_TYPE_PROMOTIONAL, Constants.SMS_TYPE_PROMOTIONAL);
			SMSStatusCodes.genericCampValueMap.put(Constants.SMS_TYPE_2_WAY, Constants.SMS_TYPE_PROMOTIONAL);
			SMSStatusCodes.countryCampValueMap.put(currentCountry, SMSStatusCodes.genericCampValueMap);
			
			
			
		}
	}

}

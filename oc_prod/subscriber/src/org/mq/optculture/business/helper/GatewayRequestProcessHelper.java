package org.mq.optculture.business.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.beans.OrgSMSkeywords;
import org.mq.marketer.campaign.beans.SMSSettings;
import org.mq.marketer.campaign.beans.UserSMSGateway;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.OCSMSGatewayDao;
import org.mq.marketer.campaign.dao.OrgSMSkeywordsDao;
import org.mq.marketer.campaign.dao.SMSSettingsDao;
import org.mq.marketer.campaign.dao.UserSMSGatewayDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class GatewayRequestProcessHelper {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	
	public Object getKeyWordFromMsgContent(String msgContent, String receivedNumber ) throws BaseServiceException{
		SMSSettings settingKeyWord = findSettingsKeyWordFromContent(msgContent, receivedNumber);
		if(settingKeyWord != null) return settingKeyWord;
		OrgSMSkeywords orgSMSkeyWord = findOrgSMSKeyWordFromContent(msgContent, receivedNumber);
		if(orgSMSkeyWord != null) return orgSMSkeyWord;
		return null;
	}
		
	public OrgSMSkeywords findOrgSMSKeyWordFromContent(String msgContent, String receivedNumber ) throws BaseServiceException {
		OrgSMSkeywordsDao orgSMSkeywordsDao = null;
		try {
			orgSMSkeywordsDao = (OrgSMSkeywordsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ORGSMSKEYWORDS_DAO);
			
		} catch (Exception e) {
			throw new BaseServiceException("No dao(s) found in the context");
		}
		
		List<OrgSMSkeywords> keywordsList =  orgSMSkeywordsDao.findAllByReceivedNumber(receivedNumber);
		
		if(keywordsList == null) throw new BaseServiceException("No keywords find for this number "+receivedNumber);
		
		//List<String> newKeywordsList = new ArrayList<String>();
		Map<String, OrgSMSkeywords> keywordsMap = new HashMap<String, OrgSMSkeywords>();
		
		for (OrgSMSkeywords orgSMSkeywords : keywordsList) {
			
			String keyword = orgSMSkeywords.getKeyword();
			if(msgContent.startsWith(keyword) ) {
				//newKeywordsList.add(keyword);
				keywordsMap.put(keyword, orgSMSkeywords);
			}
			
			
		}
		String finalKeyword = null;
		Set<String> keySet = keywordsMap.keySet();
        for (String kWord : keySet) {
        	 logger.debug(kWord);
        	String[] strArr = msgContent.split(kWord);
        	if(strArr.length > 0 && strArr[1].startsWith(" ")) {
        		String[] finalKeywordArr = kWord.split(" ");
                if(finalKeywordArr.length == keySet.size()){
                	finalKeyword = kWord;
                    break;
                }
        		
        	}else if(kWord.equalsIgnoreCase(msgContent)) finalKeyword = kWord; 
        	else continue;
        	
        	
		}
		
		return (OrgSMSkeywords) (finalKeyword == null ? finalKeyword : keywordsMap.get(finalKeyword));
		
	}
	
	
	public SMSSettings findSettingsKeyWordFromContent(String msgContent, String receivedNumber ) throws BaseServiceException	{
		
		SMSSettingsDao smsSettingsDao = null;
		
		
		try {
			smsSettingsDao = (SMSSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BaseServiceException("No dao(s) found in the context");
		}
		
		List<SMSSettings> settingsKeywordsList =  smsSettingsDao.findAllByReceivedNumber(receivedNumber);
		
		if(settingsKeywordsList == null) return null;
		
		Map<String, SMSSettings> keywordsMap = new HashMap<String, SMSSettings>();
		
		for (SMSSettings settingskeywords : settingsKeywordsList) {
			
			String keyword = settingskeywords.getKeyword();
			//String optOutKeyWord = settingskeywords.getOptoutKeyword();
			if(msgContent.startsWith(keyword)  ) {
				//newKeywordsList.add(keyword);
				keywordsMap.put(keyword, settingskeywords);
			}
			
			
		}
		String finalKeyword = null;
		Set<String> keySet = keywordsMap.keySet();
        for (String kWord : keySet) {
        	 logger.debug(kWord);
        	String[] strArr = msgContent.split(kWord);
        	if(strArr.length > 0 && strArr[1].startsWith(" ")) {
        		String[] finalKeywordArr = kWord.split(" ");
                if(finalKeywordArr.length == keySet.size()){
                	finalKeyword = kWord;
                    break;
                }
        		
        	}else if(kWord.equalsIgnoreCase(msgContent)) finalKeyword = kWord; 
        	else continue;
        	
        	
		}
		
        return (SMSSettings) (finalKeyword == null ? finalKeyword : keywordsMap.get(finalKeyword));
		
	}
	
	public static OCSMSGateway getOcSMSGateway(Users user, String type) throws BaseServiceException{
		
		UserSMSGatewayDao userSMSGatewayDao = null;
		OCSMSGatewayDao OCSMSGatewayDao = null;
		
		UserSMSGateway userSmsGateway = null;
		OCSMSGateway ocgateway = null;
		
		try {
			userSMSGatewayDao = (UserSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERSMSGATEWAY_DAO);
			OCSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.OCSMSGATEWAY_DAO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BaseServiceException("No DAO found with given id");
		}
		
		 userSmsGateway = userSMSGatewayDao.findByUserId(user.getUserId(), type);
			
		if(userSmsGateway == null) {
			
			throw new BaseServiceException("no default smstype available for user");
			
		}
		
		 ocgateway = OCSMSGatewayDao.findById(userSmsGateway.getGatewayId());
		
		return ocgateway;
	}
	
	
	public static void main(String[] args) {
		String strArray[] = {  "START","SAMPLE", "START TRMODE AMERT", "START TRMODE"};
        List<String> keywordList = new ArrayList<String>();
        String msg = "START TRMODE AMERTuy begin";
        for(int i = 0; i < strArray.length; i++){
            if(msg.startsWith(strArray[i])){
                keywordList.add(strArray[i]);
                logger.debug("Keyword exist...u" + strArray[i]);
                String[] splitArray = msg.split(strArray[i]);
                
                
                for(int j = 0; j < splitArray.length; j++){
                    logger.debug(splitArray[j] + " Invalid");
                }
            }
        }
        String srtKeyword = null;
        for (String string : keywordList) {
        	 logger.debug(string);
        	String[] strArr = msg.split(string);
        	if(strArr.length > 0 && strArr[1].startsWith(" ")) {
        		logger.debug("fdfdfjkfjdkfjdfjkdl");
        		String[] finalKeyword = string.split(" ");
                if(finalKeyword.length == keywordList.size()){
                     srtKeyword = string;
                    logger.debug(" sdjdsdjs "+srtKeyword);
                    break;
                }
        		
        	}else if(string.equalsIgnoreCase(msg)) srtKeyword = string; else continue;
        	
        	
		}
       logger.debug(srtKeyword);
        /*
        String srtKeyword = "";

        if(keywordList.size() > 1){
            logger.debug("Matched more than once");

            for(int k = 0; k < keywordList.size(); k++){
                String dsfdKeyword = keywordList.get(k);
                String[] finalKeyword = dsfdKeyword.split(" ");
                if(finalKeyword.length == keywordList.size()){
                    srtKeyword = keywordList.get(k);
                    logger.debug(srtKeyword);
                    break;
                }
            }
        } */
	}
	
	
}

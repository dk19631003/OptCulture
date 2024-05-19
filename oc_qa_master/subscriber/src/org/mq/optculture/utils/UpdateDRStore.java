package org.mq.optculture.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.marketer.campaign.beans.DRSent;
import org.mq.marketer.campaign.beans.DigitalReceiptsJSON;
import org.mq.marketer.campaign.dao.DRSentDao;
import org.mq.marketer.campaign.dao.DRSentDaoForDML;
import org.mq.marketer.campaign.dao.DigitalReceiptsJSONDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.POSFieldsEnum;
import org.zkoss.zkplus.spring.SpringUtil;



public class UpdateDRStore {
	private String[] docSidArr = POSFieldsEnum.docSid.getDrKeyField().split(Constants.DELIMETER_DOUBLECOLON);
	private String[] storeNumberArr = POSFieldsEnum.storeNumber.getDrKeyField().split(Constants.DELIMETER_DOUBLECOLON);
	 private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Long user_id=null;
	private DigitalReceiptsJSONDao digitalReceiptsJSONDao;
	private DRSentDao drSentDao;
	private DRSentDaoForDML drSentDaoForDML;
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
	public UpdateDRStore(){
		this.digitalReceiptsJSONDao = (DigitalReceiptsJSONDao) SpringUtil
				.getBean("digitalReceiptsJSONDao");
		this.drSentDao = (DRSentDao) SpringUtil
				.getBean("drSentDao");
		this.drSentDaoForDML = (DRSentDaoForDML) SpringUtil
				.getBean("drSentDaoForDML");
	}
	
	
	
	public  void main(String fromCal, String endCal) {
		// TODO Auto-generated method stub
		
		/*int size = digitalReceiptsJSONDao.getTotalCount(user_id, fromCal, endCal);
		
		if(size==0){
			MessageUtil.setMessage("No records exist for this user", "green");
			return;
		}*/
		List<Map<String, Object>> drSentDateList = digitalReceiptsJSONDao.getDRSentDate(user_id,fromCal, endCal);
		List<DRSent> edrSentList = new ArrayList<DRSent>();
		for(Map<String, Object> m : drSentDateList){
			if(m.get("sentDate")!=null){
				
		List<DRSent> drSentList = digitalReceiptsJSONDao.getDRSent(user_id,m.get("sentDate").toString());
		List<DigitalReceiptsJSON> drJsonList = digitalReceiptsJSONDao.getDrJson(user_id,m.get("sentDate").toString());
		List<DRSent> updatedList = new ArrayList<DRSent>();
		outer : for(DRSent drSent: drSentList){
			for(DigitalReceiptsJSON digitalReceiptsJSON:drJsonList){
				String jsonStr = digitalReceiptsJSON.getJsonStr();
				JSONObject jsonObject = (JSONObject)JSONValue.parse(jsonStr);
				if(jsonObject !=null){
				JSONObject bodyReceiptObj = (JSONObject)jsonObject.get("Body");
				if(!jsonObject.containsKey("Items")) {
					bodyReceiptObj = (JSONObject)jsonObject.get("Body");
	    		}
	    		else{
	    			bodyReceiptObj = jsonObject;
	    		}
				if(bodyReceiptObj !=null){
				JSONObject receiptObj = (JSONObject)bodyReceiptObj.get("Receipt");
				if(receiptObj !=null){
				
				String docSidStr = (String)receiptObj.get(docSidArr[1]);
				if(docSidStr.equals(String.valueOf(drSent.getDocSid()))){
					
					drSent.setDrJsonObjId(digitalReceiptsJSON.getDrjsonId());
					
					String storeNumberStr = (String)receiptObj.get(storeNumberArr[1]);
					if(storeNumberStr != null && !storeNumberStr.trim().isEmpty()){
						drSent.setStoreNumber(storeNumberStr);
					}
					edrSentList.add(drSent);
					break;
				}
				}else{
					logger.info(" receiptObj is null :: DRSent ID is = "+drSent.getId());
				}
				}else{
					logger.info(" bodyReceiptObj is null:: DRSent ID is = "+drSent.getId());
				}
				}
				
			}
		}
		
	}
}
		logger.info("done ::: ");
		drSentDaoForDML.saveByCollection(edrSentList);
		MessageUtil.setMessage("Completed for this user with given time....", "green");
		return;
}
}
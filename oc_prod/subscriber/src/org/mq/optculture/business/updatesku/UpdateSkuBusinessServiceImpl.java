package org.mq.optculture.business.updatesku;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.SkuFile;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.SkuFileDao;
import org.mq.marketer.campaign.dao.SkuFileDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.POSFieldsEnum;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.HeaderInfo;
import org.mq.optculture.model.StatusInfo;
import org.mq.optculture.model.UserDetails;
import org.mq.optculture.model.updatesku.SkuInfo;
import org.mq.optculture.model.updatesku.UpdateSkuRequestObject;
import org.mq.optculture.model.updatesku.UpdateSkuResponse;
import org.mq.optculture.model.updatesku.UpdateSkuResponseObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;


import com.google.gson.Gson;

public class UpdateSkuBusinessServiceImpl implements UpdateSkuBusinessService{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)
			throws BaseServiceException {
		BaseResponseObject baseResponseObject=new BaseResponseObject();
		UpdateSkuResponseObject updateSkuResponseObject=null;
		HeaderInfo headerInfo=null;
		UserDetails userDetails=null;
		StatusInfo statusInfo=null;
		
		try {
			
			logger.debug("-------entered processRequest---------");
			//json to object
			Gson gson=new Gson();
			UpdateSkuRequestObject updateSkuRequestObject=new UpdateSkuRequestObject();
			try {
				updateSkuRequestObject = gson.fromJson(baseRequestObject.getJsonValue(), UpdateSkuRequestObject.class);
				// logger.info("updateSkuRequestObject:"+updateSkuRequestObject);
			}
			catch (Exception e) {
				logger.error("Exception ::",e);
				statusInfo = new StatusInfo("500000",PropertyUtil.getErrorMessage(500000, OCConstants.ERROR_SKU_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				updateSkuResponseObject = prepareFinalResponse(new HeaderInfo(),statusInfo,new UserDetails());
				String json=gson.toJson(updateSkuResponseObject);
				baseResponseObject.setJsonValue(json);
				baseResponseObject.setAction(OCConstants.UPDATE_SKU_SERVICE_REQUEST);
				return baseResponseObject;
			}
			
			UpdateSkuBusinessService updateSkuBusinessService=(UpdateSkuBusinessService) ServiceLocator.getInstance().getServiceByName(OCConstants.UPDATE_SKU_BUSINESS_SERVICE);
			
			headerInfo = updateSkuRequestObject.getUPDATESKUREQUEST().getHEADERINFO();
			UsersDao usersDao=(UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			MailingListDao mailingListDao=(MailingListDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MAILINGLIST_DAO);
			userDetails=updateSkuRequestObject.getUPDATESKUREQUEST().getUSERDETAILS();
			String userName=userDetails.getUSERNAME();
			String orgId=userDetails.getORGID();
			String token=userDetails.getTOKEN();
			Users user=usersDao.findByToken(userName+Constants.USER_AND_ORG_SEPARATOR+orgId , token);
			
			statusInfo=validateInnerObjects(headerInfo,userDetails);
			//logger.info("statusInfo:"+statusInfo);
			if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getStatus())) {
				updateSkuResponseObject=prepareFinalResponse(headerInfo,statusInfo,userDetails);
				String json=gson.toJson(updateSkuResponseObject);
				baseResponseObject.setJsonValue(json);
				baseResponseObject.setAction(OCConstants.UPDATE_SKU_SERVICE_REQUEST);
				 logger.debug("-------exit  processRequest---------");
				return baseResponseObject;
				
			}
			Long userId=user.getUserId();
			MailingList mailingList = mailingListDao.findListTypeMailingList(Constants.MAILINGLIST_TYPE_POS, userId);
			List<String> lstSucessItemSids = new ArrayList<>();
			List<String> lstFailureSids = new ArrayList<>();
			List <SkuInfo> lstSkuInfos = updateSkuRequestObject.getUPDATESKUREQUEST().getPRODUCTSINFO();
			
			if(lstSkuInfos!=null)
			{	
				  for(SkuInfo skuInfo:lstSkuInfos)
				  {
				     updateSkuResponseObject=(UpdateSkuResponseObject) 
						  updateSkuBusinessService.processUpdateSkuRequest(updateSkuRequestObject,skuInfo,user,mailingList);
				 
				 // logger.info("updateSkuResponseObject:"+updateSkuResponseObject);
				   statusInfo= updateSkuResponseObject.getUPDATESKURESPONSE().getSTATUSINFO();
				   if(statusInfo==null )	  lstSucessItemSids.add(skuInfo.getITEMSID());
				   else if(!skuInfo.getITEMSID().trim().isEmpty()) lstFailureSids.add(skuInfo.getITEMSID());
				  }
				
				//object to json
				String strMessage = "Sku items "+lstSucessItemSids+" updated successfully.";
				if(lstFailureSids.size()>0)
				{
					strMessage = strMessage+" Sku items "+lstFailureSids+" not updated due to invalid data.";
				}
				statusInfo=new StatusInfo("0",strMessage,OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				updateSkuResponseObject=prepareFinalResponse(headerInfo,statusInfo,userDetails);
			}
			else
			{
				SkuInfo skuInfo = updateSkuRequestObject.getUPDATESKUREQUEST().getSKUINFO();
				updateSkuResponseObject=(UpdateSkuResponseObject) 
						  updateSkuBusinessService.processUpdateSkuRequest(updateSkuRequestObject,skuInfo,user,mailingList);
				
				statusInfo= updateSkuResponseObject.getUPDATESKURESPONSE().getSTATUSINFO();
			    if(statusInfo==null)
                   statusInfo=new StatusInfo("0","SKU Item updated sucessfully.",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				updateSkuResponseObject=prepareFinalResponse(headerInfo,statusInfo,userDetails);
				updateSkuResponseObject.getUPDATESKURESPONSE().setSKUINFO(skuInfo);
			}
			
			String json=gson.toJson(updateSkuResponseObject);
			baseResponseObject.setJsonValue(json);
			baseResponseObject.setAction(OCConstants.UPDATE_SKU_SERVICE_REQUEST);
			 logger.debug("-------exit  processRequest---------");
			return baseResponseObject;
		} catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return baseResponseObject;
	}//processRequest

	@Override
	public UpdateSkuResponseObject processUpdateSkuRequest(
			UpdateSkuRequestObject updateSkuRequestObject,SkuInfo skuInfo,Users user,MailingList mailingList)throws BaseServiceException {
		UpdateSkuResponseObject updateSkuResponseObject=null;
		UserDetails userDetails=null;
		HeaderInfo headerInfo=null;
		StatusInfo statusInfo=null;
		
		try {
			logger.debug("-------entered processUpdateSkuRequest---------");
			
			if(skuInfo==null)
			{
				statusInfo = new StatusInfo("500010",PropertyUtil.getErrorMessage(500010, OCConstants.ERROR_SKU_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				updateSkuResponseObject=prepareFinalResponse(headerInfo,statusInfo,userDetails);
				return updateSkuResponseObject;
			}
			
			userDetails=updateSkuRequestObject.getUPDATESKUREQUEST().getUSERDETAILS();
			statusInfo=validateRootObject(updateSkuRequestObject);
			if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())) {
				updateSkuResponseObject=prepareFinalResponse(headerInfo,statusInfo,userDetails);
				return updateSkuResponseObject;
			}
			statusInfo=processSkuData(user, mailingList,skuInfo);
			updateSkuResponseObject=prepareFinalResponse(headerInfo,statusInfo,userDetails);
			logger.debug("-------exit  processUpdateSkuRequest---------");
			return updateSkuResponseObject;
			
		}catch (Exception e) {
			logger.error("Exception  ::", e);
			throw new BaseServiceException("Exception occured while processing processUpdateSkuRequest::::: ", e);
		}

	}  //processUpdateSkuRequest

	private StatusInfo validateInnerObjects(HeaderInfo headerInfo, UserDetails userDetails)throws BaseServiceException {
		StatusInfo statusInfo=null;
		try {
			logger.debug("-------entered validateInnerObjects---------");
			UsersDao usersDao=(UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			if(headerInfo==null) {
				statusInfo=new StatusInfo("500003",PropertyUtil.getErrorMessage(500003,OCConstants.ERROR_SKU_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
			if(headerInfo.getREQUESTID()==null ||headerInfo.getREQUESTID().trim().isEmpty()) {
				statusInfo=new StatusInfo("500004",PropertyUtil.getErrorMessage(500004,OCConstants.ERROR_SKU_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
			if(userDetails==null) {
				statusInfo=new StatusInfo("500005",PropertyUtil.getErrorMessage(500005,OCConstants.ERROR_SKU_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
			String userNameStr=userDetails.getUSERNAME();
			if(userNameStr==null || userNameStr.trim().length()==0) {
				statusInfo=new StatusInfo("500006",PropertyUtil.getErrorMessage(500006,OCConstants.ERROR_SKU_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
			String orgId=userDetails.getORGID();
			if(orgId==null || orgId.trim().length()==0) {
				statusInfo=new StatusInfo("500007",PropertyUtil.getErrorMessage(500007,OCConstants.ERROR_SKU_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
			String tokenStr=userDetails.getTOKEN();
			if(tokenStr==null || tokenStr.trim().length()==0) {
				statusInfo=new StatusInfo("500008",PropertyUtil.getErrorMessage(500008,OCConstants.ERROR_SKU_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
			Users user = usersDao.findByToken(userNameStr+Constants.USER_AND_ORG_SEPARATOR+orgId , tokenStr);
			if(user == null) {
				statusInfo = new StatusInfo("500009",PropertyUtil.getErrorMessage(500009,OCConstants.ERROR_SKU_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
			
			
			 logger.debug("-------exit  validateInnerObjects---------");
			return statusInfo;
		}catch (Exception e) {
			logger.error("Exception  ::", e);
			throw new BaseServiceException("Exception occured while processing validateInnerObjects::::: ", e);
		}
	}//validateInnerObjects

	private StatusInfo processSkuData(Users user, MailingList mailingList,
			SkuInfo skuInfo) {
		StatusInfo statusInfo=null;
		try {
			logger.debug("-------entered processSkuData---------");
			POSMappingDao posMappingDao=(POSMappingDao) ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
			SkuFileDao skuFileDao=(SkuFileDao) ServiceLocator.getInstance().getDAOByName(OCConstants.SKU_FILE_DAO);
			SkuFileDaoForDML skuFileDaoForDML=(SkuFileDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SKU_FILE_DAO_FOR_DML);
			TreeMap<String, List<String>> prioMap =null;
			prioMap = Utility.getPriorityMap(user.getUserId(), Constants.POS_MAPPING_TYPE_SKU, posMappingDao);
			List<POSMapping> SKUPOSMappingList = null;
			SKUPOSMappingList = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_SKU+"'", user.getUserId());
			statusInfo=validateSKUPOSMappingList(SKUPOSMappingList,user);	
			if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())) {
				return statusInfo;
			}
			SkuFile skuFileObj = null;
			SkuFile inputSkuFile=new SkuFile();
			inputSkuFile = prepareSkuObjectData(SKUPOSMappingList,skuInfo);
			if(inputSkuFile.getItemSid()== null || inputSkuFile.getItemSid().trim().isEmpty()) {
				statusInfo = new StatusInfo("500012",PropertyUtil.getErrorMessage(500012, OCConstants.ERROR_SKU_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
				
			if(!prioMap.isEmpty()) {
				skuFileObj = skuFileDao.findSKUByPriority(prioMap, inputSkuFile, user.getUserId());
			}
			if(skuFileObj != null) {
				skuFileObj = Utility.mergeSkuFile(inputSkuFile, skuFileObj);
				skuFileObj.setUserId(user.getUserId());
				logger.info("if skuInfo:"+skuFileObj.getClassCode());
				skuFileObj.setModifiedDate(Calendar.getInstance());
				skuFileDaoForDML.saveOrUpdate(skuFileObj);
			}
			else {
				skuFileObj = new SkuFile(); 
				skuFileObj = Utility.mergeSkuFile(inputSkuFile, skuFileObj);
				skuFileObj.setUserId(user.getUserId());
				skuFileObj.setCreatedDate(Calendar.getInstance());;
				skuFileObj.setModifiedDate(Calendar.getInstance());;

				logger.info("else skuInfo:"+skuFileObj.getClassCode());
				skuFileDaoForDML.saveOrUpdate(skuFileObj);
			}
			logger.debug("-------exit  processSkuData---------");
			return statusInfo;
		}catch (Exception e) {
			statusInfo = new StatusInfo("500010",PropertyUtil.getErrorMessage(500010, OCConstants.ERROR_SKU_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			logger.error("Exception  ::", e);
			return statusInfo;
			
			
		}
	}//processSkuData

	private StatusInfo validateSKUPOSMappingList(
			List<POSMapping> SKUPOSMappingList,Users user) {
		StatusInfo statusInfo=null;
		logger.debug("-------entered validateSKUPOSMappingList---------");
		if(SKUPOSMappingList == null || SKUPOSMappingList.size() == 0) {
			logger.debug("Not found POS Mapping type SKU for the userName"+user.getUserName());
			statusInfo=new StatusInfo("500011",PropertyUtil.getErrorMessage(500011, OCConstants.ERROR_SKU_FLAG)+user.getUserName(),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		logger.debug("-------exit  validateSKUPOSMappingList---------");
		return statusInfo;
	}//validateSKUPOSMappingList

	private SkuFile prepareSkuObjectData(
			List<POSMapping> sKUPOSMappingList,
			SkuInfo skuInfo)throws BaseServiceException {

		logger.debug("-------entered prepareSkuObjectData---------");
		Class strArg[] = new Class[] { String.class };
		Class calArg[] = new Class[] { Calendar.class };
		Class doubleArg[] = new Class[] { Double.class };
		Class dataTypeArg[] = null;
		SkuFile inputSkuFile = null;
		inputSkuFile = new SkuFile();
		String fieldValue = null;
		//logger.info(sKUPOSMappingList);
		for(POSMapping posMapping : sKUPOSMappingList){
			
			if(posMapping.getCustomFieldName() == null){
				continue;
			}
			String custFieldAttribute = posMapping.getCustomFieldName();
			//For user defined field name, Json does not contain field value
			fieldValue = getFieldValue(skuInfo,posMapping);
			//logger.info("field value:"+fieldValue);
			if(fieldValue == null || fieldValue.trim().length() <= 0){
				logger.info("field value is null, In SKU data processing>>>>>..");
				continue;
			}
			String dataTypeStr = posMapping.getDataType();
			//logger.info("custFieldAttribute:"+custFieldAttribute);
			if(custFieldAttribute.startsWith("UDF") && dataTypeStr.startsWith("Date")){
				String dateValue = getDateFormattedData(posMapping, fieldValue);
				if(dateValue == null) continue;
				fieldValue = dateValue;
			}
			//String dateFormat = null;
			Object[] params = null;
			Method method = null;
			try {
				if (custFieldAttribute.equals(POSFieldsEnum.listPrice.getOcAttr()) && fieldValue.length() > 0 ) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.listPrice.getPojoField(), doubleArg);
					Double listPriceVal = new Double(fieldValue);
					params = new Object[] { listPriceVal };
					//logger.info("parseDouble value: "+Double.parseDouble(fieldValue.trim()));
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.description.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.description.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.sku.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.sku.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.itemCategory.getOcAttr()) && fieldValue.length() > 0) {
				//	logger.info("itemCategory:"+POSFieldsEnum.itemCategory.getPojoField());
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.itemCategory.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.itemSid.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.itemSid.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.storeNumber.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.storeNumber.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.subsidiaryNumber.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.subsidiaryNumber.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				
				
				else if (custFieldAttribute.equals(POSFieldsEnum.vendorCode.getOcAttr()) && fieldValue.length() > 0) {
				//	logger.info("in vendorCode");
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.vendorCode.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.departMentCode.getOcAttr()) && fieldValue.length() > 0) {
				//	logger.info("in department");
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.departMentCode.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.classCode.getOcAttr()) && fieldValue.length() > 0) {
				//	logger.info("in classCode");
				//	logger.info(POSFieldsEnum.classCode.getPojoField());
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.classCode.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.Sclass.getOcAttr()) && fieldValue.length() > 0) {
					//logger.info("in Sclass");
					
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.Sclass.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.dcs.getOcAttr()) && fieldValue.length() > 0) {
					//logger.info("in dcs");
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.dcs.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf1.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf1.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf2.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf2.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf3.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf3.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf4.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf4.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf5.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf5.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf6.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf6.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf7.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf7.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf8.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf8.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf9.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf9.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf10.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf10.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf11.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf11.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf12.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf12.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf13.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf13.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf14.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf14.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf15.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf15.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				if (method != null) {
					try {
						method.invoke(inputSkuFile, params);
					//	logger.info("method name:  "+method.getName()+" field value: "+fieldValue);
					} catch (Exception e) {
						e.printStackTrace();
						// TODO Auto-generated catch block
						logger.error("Exception ::" , e);
					} 
				}
			} catch (Exception e) {
				e.printStackTrace();
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
			} 
		}
		logger.debug("-------exit  prepareSkuObjectData---------");
		return inputSkuFile;
	}//prepareSkuObjectData


	private String getDateFormattedData(POSMapping posMapping, String fieldValue)throws BaseServiceException {
		
		logger.debug("-------entered getDateFormattedData---------");
		String dataTypeStr = posMapping.getDataType();
		String dateFieldValue = null;
		if(posMapping.getDataType().trim().startsWith("Date")) {
			try {
				String dateFormat = dataTypeStr.substring(dataTypeStr.indexOf("(")+1, dataTypeStr.indexOf(")"));

				if(!Utility.validateDate(fieldValue, dateFormat)) {
					return null;
				}
				DateFormat formatter ; 
				Date date ; 
				formatter = new SimpleDateFormat(dateFormat);
				date = (Date)formatter.parse(fieldValue); 
				Calendar cal =  new MyCalendar(Calendar.getInstance(), null, MyCalendar.dateFormatMap.get(dateFormat));
				cal.setTime(date);
				dateFieldValue= MyCalendar.calendarToString(cal, MyCalendar.dateFormatMap.get(dateFormat));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
			}
		}
		logger.debug("-------exit  getDateFormattedData---------");
		return dateFieldValue;
	}//getDateFormattedData

	private String getFieldValue(SkuInfo skuInfo,
			POSMapping posMapping)throws BaseServiceException {
		String fieldValue=null;
	//	logger.debug("-------entered getFieldValue---------");
		if(posMapping.getCustomFieldName().equalsIgnoreCase("item sid")) {
			fieldValue=skuInfo.getITEMSID();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("store number")) {
			fieldValue=skuInfo.getSTORENUMBER();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("sku")) {
			fieldValue=skuInfo.getSKU();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("item category")) {
			fieldValue=skuInfo.getITEMCATEGORY();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("list price")) {
			fieldValue=skuInfo.getLISTPRICE();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("description")) {
			fieldValue=skuInfo.getDESCRIPTION();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("dcs")) {
			fieldValue=skuInfo.getDCS();
			//logger.info("dcs"+fieldValue);
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("vendor")) {
			fieldValue=skuInfo.getVENDORCODE();
			//logger.info("vendor"+fieldValue);
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("class")) {
			fieldValue=skuInfo.getCLASSCODE();
			//logger.info("class"+fieldValue);
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("subclass")) {
			fieldValue=skuInfo.getSUBCLASSCODE();
			//logger.info("subclass"+fieldValue);
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("Department")) {
			fieldValue=skuInfo.getDEPARTMENTCODE();
			//logger.info("Department"+fieldValue);
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("udf1")) {
			fieldValue=skuInfo.getUDF1();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("udf2")) {
			fieldValue=skuInfo.getUDF2();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("udf3")) {
			fieldValue=skuInfo.getUDF3();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("udf4")) {
			fieldValue=skuInfo.getUDF4();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("udf5")) {
			fieldValue=skuInfo.getUDF5();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("udf6")) {
			fieldValue=skuInfo.getUDF6();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("udf7")) {
			fieldValue=skuInfo.getUDF7();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("udf8")) {
			fieldValue=skuInfo.getUDF8();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("udf9")) {
			fieldValue=skuInfo.getUDF9();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("udf10")) {
			fieldValue=skuInfo.getUDF10();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("udf11")) {
			fieldValue=skuInfo.getUDF11();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("udf12")) {
			fieldValue=skuInfo.getUDF12();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("udf13")) {
			fieldValue=skuInfo.getUDF13();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("udf14")) {
			fieldValue=skuInfo.getUDF14();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("udf15")) {
			fieldValue=skuInfo.getUDF15();
			return fieldValue;
		}
		logger.debug("-------exit  getFieldValue---------");
		return fieldValue;
	}//getFieldValue

	private UpdateSkuResponseObject prepareFinalResponse(
			HeaderInfo headerInfo, StatusInfo statusInfo,UserDetails userDetails)throws BaseServiceException {
		logger.debug("-------entered prepareFinalResponse---------");
		UpdateSkuResponseObject updateSkuResponseObject=new UpdateSkuResponseObject();
		UpdateSkuResponse updateSkuResponse=new UpdateSkuResponse();
		updateSkuResponse.setUSERDETAILS(userDetails);
		updateSkuResponse.setHEADERINFO(headerInfo);
		updateSkuResponse.setSTATUSINFO(statusInfo);
		updateSkuResponseObject.setUPDATESKURESPONSE(updateSkuResponse);
		logger.debug("-------exit  prepareFinalResponse---------");
		return updateSkuResponseObject;
	}//prepareFinalResponse

	private StatusInfo validateRootObject(
			UpdateSkuRequestObject updateSkuRequestObject)throws BaseServiceException {
		StatusInfo statusInfo=null;
		try {
			logger.debug("-------entered validateRootObject---------");
			if(updateSkuRequestObject==null) {
				statusInfo=new StatusInfo("500001",PropertyUtil.getErrorMessage(500001,OCConstants.ERROR_SKU_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
			if(updateSkuRequestObject.getUPDATESKUREQUEST()==null){
				statusInfo=new StatusInfo("500002",PropertyUtil.getErrorMessage(500002,OCConstants.ERROR_SKU_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}

		}catch(Exception e) {
			logger.error("Exception  ::", e);
			throw new BaseServiceException("Exception occured while processing validateRootObject::::: ", e);
		}
		
		logger.debug("-------exit  validateRootObject---------");
		return statusInfo;
	}

	@Override
	public BaseResponseObject processUpdateSkuRequest(UpdateSkuRequestObject updateSkuRequestObject)
			throws BaseServiceException {
		// TODO Auto-generated method stub
		return null;
	}
}//validateRootObject
